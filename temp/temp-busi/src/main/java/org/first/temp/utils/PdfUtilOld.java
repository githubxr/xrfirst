package org.first.temp.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.template.*;
import org.first.basecore.util.IoUtil;
import org.first.temp.dto.FtlModel;
import org.first.temp.utils.groovy.GroovyFunctionContainer;
import org.first.temp.utils.groovy.Jsr223FuncCacheUtil;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * pdf工具类
 * @since 25/12/01
 * */
public class PdfUtilOld {
    //freemarker配置
    private static Configuration defConfig = null;

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static String tempCss = new String(IoUtil.readToStr("static/def.css"));//样式
    private static byte[] simpFontData = IoUtil.getBytesByPath("static/font/sim.ttf");//字体


    //初始化freemarker配置
    static {
        defConfig = new Configuration(new Version("2.3.28"));
        defConfig.setDefaultEncoding("UTF-8");
        defConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);//忽略无数据的占位符
        defConfig.setOutputFormat(freemarker.core.XHTMLOutputFormat.INSTANCE);//自动转XHTML
        defConfig.setLogTemplateExceptions(false);
        defConfig.setWrapUncheckedExceptions(true);

    }

    //批量渲染pdf
    public static void genPdfBatchToDir(FtlModel model, List<String> dataList) {

        String content = model.getContentRef();//读取模板文件内容
        String firstHeader = model.getFirstHeaderRef();//读取模板文件内容
        String secHeader = model.getSecHeaderRef();//读取模板文件内容

        StringBuilder sb = new StringBuilder(10240);
        sb.append("<html><head><style>").append(tempCss).append("</style></head>")
                .append("<body>")
                .append(content)
        ;
        if( ! StringUtils.isEmpty(firstHeader)) {
            sb.append("<div class='page-header-first'>")
                    .append(firstHeader)
                    .append("</div>")
            ;
        }
        sb.append("</body></html>");

        Template template = null;

        try {
            template = new Template("defName", new StringReader(sb.toString()), defConfig);
        } catch (IOException e){
            throw new RuntimeException("构造模板失败！" + e.getMessage());
        }
        long start = System.currentTimeMillis();
        Template finalTemp = template;
        dataList.parallelStream().forEach(item -> genPdfToDir(model, finalTemp, item));
        //for(String item : dataList) genPdfToDir(model, finalTemp, item);
        long cost = System.currentTimeMillis() - start;
        System.out.println("耗时：" + cost);
    }

    /**
     * @description 渲染一个pdf
     * @param temp 模板配置信息
     * @param json 数据
     * */
    public static void genPdfToDir(FtlModel model, Template temp, String json) {
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
        if(!Jsr223FuncCacheUtil.hasTemplate(model.getTemplateName())){
        // 这里示例是读取约定路径：static/script/<templateName>.groovy
        // 你也可以在 FtlModel 中保存 scriptPath
            Jsr223FuncCacheUtil.initScriptForTemplate(model.getTemplateName(), model.getScriptContent());
        }

        // 注入通用容器到 root，模板内使用 g.funcName(...) 调用脚本函数
        root.put("g", new GroovyFunctionContainer(model.getTemplateName(), null));////暂时改为null

        try{
            //temp.process((objectMapper.readValue(json, Object.class)), out);
            temp.process(root, out);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(out.toString(), null);

            builder.useFont(()->new ByteArrayInputStream(simpFontData), "Microsoft YaHei");
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

    //测试入口
    public static void main(String[] args) {
        FtlModel model = new FtlModel();
        model.setContentRef(IoUtil.readToStr("static/ftl/content1.ftl"));
        model.setSavePath("D:/PDFS");
        model.setFirstHeaderRef(IoUtil.readToStr("static/ftl/firstHeader.ftl"));
        model.setTemplateName("测试");
        model.setSecHeaderRef("");
        model.setScriptContent(IoUtil.readToStr("static/scriptSample/simpleGroovy"));

        List<String> strs = new ArrayList();
        strs.add(IoUtil.readToStr("static/json/sample1.json"));
        strs.add(IoUtil.readToStr("static/json/sample2.json"));
        strs.add(IoUtil.readToStr("static/json/sample3.json"));
        genPdfBatchToDir(model, strs);
    }
}
