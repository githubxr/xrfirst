package org.first.temp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.first.temp.dto.FtlModel;
import org.first.temp.utils.groovy.GroovyFunctionContainer;
import org.first.temp.utils.groovy.Jsr223FuncCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;



@Component
public class PdfUtil {

    private final Configuration freemarkerCfg;
    private final Executor pdfExecutor;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    private String defaultCss = null;
    private byte[] fallbackFont = null;

    private StringTemplateLoader dynamicLoader;

    //Qualifier指定注入自己配置的（by name）
    @Autowired
    public PdfUtil(@Qualifier("freemarkerConfig") freemarker.template.Configuration freemarkerCfg,
                   @Qualifier("pdfExecutor") Executor pdfExecutor,
                   ResourceLoader resourceLoader,
                   ObjectMapper objectMapper) {
        this.freemarkerCfg = freemarkerCfg;
        this.pdfExecutor = pdfExecutor;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        // Freemarker 动态 loader
        this.dynamicLoader = (StringTemplateLoader)
                freemarkerCfg.getSharedVariable("dynamicLoader");
    }


    // lazy load common assets
    @PostConstruct
    public void init() {
        try {
            Resource css = resourceLoader.getResource("classpath:static/def.css");
            if (css.exists()) defaultCss = new String(css.getInputStream().readAllBytes());

            Resource f = resourceLoader.getResource("classpath:static/font/sim.ttf");
            if (f.exists()) fallbackFont = f.getInputStream().readAllBytes();

        } catch (Exception ignore) {}
    }

    /**
     * Batch render using an executor. Returns list of output file paths.
     */
    public void genPdfBatchToDir(FtlModel model, List<String> dataList) {
        if (model == null || StringUtils.isEmpty(model.getSavePath())) {
            throw new IllegalArgumentException("FtlModel or savePath is empty");
        }
        // 构造一次模板内容
        String fullHtml = buildHtml(model);

        String templateKey = "tpl_" + model.getTemplateName();
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

        // 等全部完成并收集异常
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
    }

    public void genPdfToDir(FtlModel model, Template temp, String json) {
        File outputDir = null;
        if(model==null||model.getSavePath()==null) {
            throw new RuntimeException("FtlModel有问题：" + model);
        }
        outputDir = new File(model.getSavePath());
        if(!outputDir.exists()) {
            outputDir.mkdirs();
        }

        StringWriter out = new StringWriter();
        // 把 JSON 解析为 Map
        Map root = null;
        try {
            root = objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 要求：在渲染之前，必须先初始化脚本（对应 model.getTemplateName() 做为 templateId）
        if(!Jsr223FuncCache.hasTemplate(model.getTemplateName())){
            // 这里示例是读取约定路径：static/script/<templateName>.groovy
            // 你也可以在 FtlModel 中保存 scriptPath
            Jsr223FuncCache.initScriptForTemplate(model.getTemplateName(), model.getScriptContent());
        }
        // 注入通用容器到 root，模板内使用 g.funcName(...) 调用脚本函数
        root.put("g", new GroovyFunctionContainer(model.getTemplateName()));
        try{
            //temp.process((objectMapper.readValue(json, Object.class)), out);
            temp.process(root, out);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(out.toString(), null);

            builder.useFont(()->new ByteArrayInputStream(fallbackFont), "Microsoft YaHei");
            builder.useFastMode();
            builder.toStream(baos);
            //貌似没问题
            //synchronized (PdfUtil.class) {
            builder.run();
            //}

            ////@testFlag:为了测试多线程而sleep
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            String fileName =  model.getTemplateName()  + Thread.currentThread().getName() + System.currentTimeMillis() + ".pdf";
            File osFile = new File(outputDir, fileName);
            FileOutputStream fos = new FileOutputStream(osFile);
            baos.writeTo(fos);
            fos.flush();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("渲染出错：" + e.getMessage());
        }
    }

    private String buildHtml(FtlModel model) {
        StringBuilder sb = new StringBuilder(8192);
        sb.append("<html><head><style>")
                .append(defaultCss)
                .append("</style></head><body>");

        if (model.getFirstHeaderRef() != null)
            sb.append("<div class='header'>").append(model.getFirstHeaderRef()).append("</div>");

        sb.append(model.getContentRef());
        sb.append("</body></html>");

        return sb.toString();
    }

}