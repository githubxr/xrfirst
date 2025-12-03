package org.first.temp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.first.temp.dto.FtlModel;
import org.first.temp.utils.groovy.GroovyFunctionContainer;
import org.first.temp.utils.groovy.Jsr223FuncCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class PdfUtil {

    private final Configuration freemarkerCfg;
    private final Executor pdfExecutor;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final StringTemplateLoader dynamicLoader;

    private String defaultCss = null;
    private byte[] fallbackFont = null;

    //通过名字获取自己自定义的
    @Autowired
    public PdfUtil(@Qualifier("freemarkerConfig") Configuration freemarkerCfg,
                   @Qualifier("pdfExecutor") Executor pdfExecutor,
                   ResourceLoader resourceLoader,
                   ObjectMapper objectMapper,
                   StringTemplateLoader dynamicLoader) {
        this.freemarkerCfg = freemarkerCfg;
        this.pdfExecutor = pdfExecutor;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.dynamicLoader = dynamicLoader; // 注入，不用 getSharedVariable/cast
    }

    @PostConstruct
    public void init() {
        try {
            Resource css = resourceLoader.getResource("classpath:static/def.css");
            if (css.exists()) defaultCss = new String(css.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            Resource f = resourceLoader.getResource("classpath:static/font/sim.ttf");
            if (f.exists()) fallbackFont = f.getInputStream().readAllBytes();

        } catch (Exception ignore) {}
    }

    public void genPdfBatchToDir(FtlModel model, List<String> dataList) {
        if (model == null || StringUtils.isEmpty(model.getSavePath())) {
            throw new IllegalArgumentException("FtlModel or savePath is empty");
        }

        String fullHtml = buildHtml(model);
        String templateKey = "tpl_" + model.getTemplateName() + "_" + System.nanoTime();
        dynamicLoader.putTemplate(templateKey, fullHtml);

        Template tpl;
        try {
            tpl = freemarkerCfg.getTemplate(templateKey);
        } catch (IOException e) {
            throw new RuntimeException("模板加载失败", e);
        }

        List<CompletableFuture<Void>> list = new ArrayList<>();
        for (String json : dataList) {
            list.add(CompletableFuture.runAsync(
                    () -> genPdfToDir(model, tpl, json), pdfExecutor));
        }

        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
    }

    //渲染逻辑
    public void genPdfToDir(FtlModel model, Template tpl, String json) {
        File outDir = new File(model.getSavePath());
        if (!outDir.exists()) {
            boolean ok = outDir.mkdirs();
            if (!ok && !outDir.exists()) {
                throw new RuntimeException("无法创建保存目录：" + outDir.getAbsolutePath());
            }
        }

        Map<String, Object> root;
        try {
            root = objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON 解析失败", e);
        }

        if (!Jsr223FuncCacheUtil.hasTemplate(model.getTemplateName())) {
            Jsr223FuncCacheUtil.initScriptForTemplate(model.getTemplateName(), model.getScriptContent());
        }

        //默认key g
        root.put("g", new GroovyFunctionContainer(model.getTemplateName(), null));//暂时改为null

        StringWriter html = new StringWriter();
        try {
            tpl.process(root, html);
        } catch (Exception e) {
            throw new RuntimeException("Freemarker 渲染失败", e);
        }

        byte[] pdfBytes = renderPdf(html.toString());

        String fileName = model.getTemplateName() + "_" +
                System.nanoTime() + "_" +
                ThreadLocalRandom.current().nextInt(1000) +
                ".pdf";

        try (FileOutputStream fos = new FileOutputStream(new File(outDir, fileName))) {
            fos.write(pdfBytes);
        } catch (Exception e) {
            throw new RuntimeException("PDF 写入失败", e);
        }
    }

    //进行渲染
    private byte[] renderPdf(String html) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfRendererBuilder b = new PdfRendererBuilder();
            b.useFastMode();
            b.withHtmlContent(html, null);

            if (fallbackFont != null) {
                b.useFont(() -> new ByteArrayInputStream(fallbackFont), "Microsoft YaHei");
            }

            b.toStream(baos);
            b.run();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 生成失败", e);
        }
    }

    //固定样式结构组装
    private String buildHtml(FtlModel model) {
        StringBuilder sb = new StringBuilder(8192);
        sb.append("<html><head><style>")
                .append(defaultCss == null ? "" : defaultCss)
                .append("</style></head><body>");

        if (model.getFirstHeaderRef() != null)
            sb.append("<div class='header'>").append(model.getFirstHeaderRef()).append("</div>");

        sb.append(model.getContentRef() == null ? "" : model.getContentRef());
        sb.append("</body></html>");

        return sb.toString();
    }

//    //更新模板
//    public void updateTemplate(String templateKey, String html) {
//        synchronized (templateCache) {
//            templateCache.put(templateKey, html);
//            dynamicLoader.putTemplate(templateKey, html);
//        }
//    }
}
