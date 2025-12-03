package org.first.temp.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Template;
import org.first.temp.dto.FtlModel;
import org.first.temp.entity.FontResource;
import org.first.temp.service.PdfRenderer;
import org.first.temp.service.ResourceService;
import org.first.temp.service.ScriptService;
import org.first.temp.service.TemplateService;
import org.first.temp.utils.groovy.GroovyFunctionContainer;
//import org.first.temp.utils.groovy.Jsr223FuncCacheUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 对外暴露的服务：批量 / 单个渲染。
 */
@Service
public class PdfService {

    private final TemplateService templateService;
    private final ScriptService scriptService;
    private final ResourceService resourceService;
    private final PdfRenderer pdfRenderer;
    private final ObjectMapper objectMapper;
    private final Executor pdfExecutor;

    public PdfService(TemplateService templateService,
                      ScriptService scriptService,
                      ResourceService resourceService,
                      PdfRenderer pdfRenderer,
                      ObjectMapper objectMapper,
                      Executor pdfExecutor) {
        this.templateService = templateService;
        this.scriptService = scriptService;
        this.resourceService = resourceService;
        this.pdfRenderer = pdfRenderer;
        this.objectMapper = objectMapper;
        this.pdfExecutor = pdfExecutor;
    }

    public void genPdfBatchToDir(FtlModel model, List<String> dataList) {
//        System.out.println("loaded functions: " + Jsr223FuncCacheUtil.hasTemplate(model.getTemplateName()));
//        System.out.println("scriptContent = " + model.getScriptContent());
//        System.out.println("templateName = " + model.getTemplateName());

        if (model == null || StringUtils.isEmpty(model.getSavePath()))
            throw new IllegalArgumentException("FtlModel or savePath is empty");

        String fullHtml = buildHtml(model);
        templateService.compile(model.getTemplateName(), fullHtml);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (String json : dataList) {
            futures.add(CompletableFuture.runAsync(() -> {
                Template tpl = templateService.get(model.getTemplateName());
                genPdfToDir(model, tpl, json);
            }, pdfExecutor));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public void genPdfToDir(FtlModel model, Template tpl, String json) {
        File outDir = new File(model.getSavePath());
        if (!outDir.exists() && !outDir.mkdirs()) {
            if (!outDir.exists()) throw new RuntimeException("无法创建保存目录：" + outDir.getAbsolutePath());
        }

        Map<String, Object> root;
        try {
            root = objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON 解析失败", e);
        }

        // script init
        if (!scriptService.has(model.getTemplateName()) && model.getScriptContent() != null) {
            scriptService.init(model.getTemplateName(), model.getScriptContent());
        }

//        // 绑定脚本容器（简化） - 业务自己在模板里调用 g.invoke('f', args)
//        Map<String, Object> g = new HashMap<>();
//        g.put("invoke", (ScriptInvoker) (funcName, args) -> scriptService.invoke(model.getTemplateName(), funcName, args));
//        root.put("g", g);

//        //默认key g
//        root.put("g", new GroovyFunctionContainer(model.getTemplateName()));
        Template template = templateService.get(model.getTemplateName());

        root.put("g", new GroovyFunctionContainer(model.getTemplateName(), scriptService));

        StringWriter htmlOut = new StringWriter();
        try {
            tpl.process(root, htmlOut);
        } catch (Exception e) {
            throw new RuntimeException("Freemarker 渲染失败", e);
        }

        // fonts: 优先使用 model 指定字体，否则使用 resourceService 的 active fonts
        List<FontResource> fonts = new ArrayList<>();
//        if (model.getFontKeys() != null && !model.getFontKeys().isEmpty()) {
//            for (String fk : model.getFontKeys()) {
//                FontResource fr = resourceService.getFont(fk);
//                if (fr != null) fonts.add(fr);
//            }
//        } else {
            fonts.addAll(resourceService.getActiveFonts());
        //}

//        byte[] pdfBytes = pdfRenderer.render(htmlOut.toString(), fonts, null);
//
//        String fileName = model.getTemplateName() + "_" + UUID.randomUUID() + "_" + ThreadLocalRandom.current().nextInt(1000) + ".pdf";
//
//        try (FileOutputStream fos = new FileOutputStream(new File(outDir, fileName))) {
//            fos.write(pdfBytes);
//        } catch (Exception e) {
//            throw new RuntimeException("PDF 写入失败", e);
//        }

        // 1. 生成文件名
        String fileName = model.getTemplateName() + "_" + UUID.randomUUID() + "_" + ThreadLocalRandom.current().nextInt(1000) + ".pdf";

        // 2. 使用 FileOutputStream 并直接调用新的 render 方法
        File pdfFile = new File(outDir, fileName);
        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            // 调用新的流式渲染方法
            pdfRenderer.render(htmlOut.toString(), fonts, null, fos);
            // 不需要 pdfBytes 变量了，PDF 在这里被直接写入文件
        } catch (Exception e) {
            throw new RuntimeException("PDF 写入失败", e);
        }
    }

    private String buildHtml(FtlModel model) {
        StringBuilder sb = new StringBuilder(8192);
        String css = resourceService.getCss("default");
        sb.append("<html><head><style>").append(css == null ? "" : css).append("</style></head><body>");
        if (model.getFirstHeaderRef() != null) sb.append("<div class='header'>").append(model.getFirstHeaderRef()).append("</div>");
        sb.append(model.getContentRef() == null ? "" : model.getContentRef());
        sb.append("</body></html>");
        return sb.toString();
    }

    @FunctionalInterface
    public interface ScriptInvoker {
        Object invoke(String funcName, Object... args);
    }
}