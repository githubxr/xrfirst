package org.first.temp;


import org.first.basecore.util.IoUtil;
import org.first.temp.dto.FtlModel;
import org.first.temp.service.impl.PdfService;
import org.first.temp.utils.PdfUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TempApplication.class)
@ComponentScan("org.first.temp")
public class PdfUtilTest {

    @Autowired
    private PdfUtil pdfUtil;

    @Autowired
    private PdfService pdfService;

    @Test
    public void testPdfGeneration() {

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
        pdfUtil.genPdfBatchToDir(model, strs);

    }


    @Test
    public void v() {
        System.out.println("运行没错");
        FtlModel model = new FtlModel();
        model.setContentRef(IoUtil.readToStr("static/ftl/content1.ftl"));
        model.setSavePath("D:/PDFS");
        model.setFirstHeaderRef(IoUtil.readToStr("static/ftl/firstHeader.ftl"));
        model.setTemplateName("测试");
        model.setSecHeaderRef("");
        model.setScriptContent(IoUtil.readToStr("static/scriptSample/simpleGroovy"));

        List<String> strs = new ArrayList();
        strs.add(IoUtil.readToStr("static/json/sample1.json"));
        //strs.add(IoUtil.readToStr("static/json/sample2.json"));
        //strs.add(IoUtil.readToStr("static/json/sample3.json"));
        pdfService.genPdfBatchToDir(model, strs);
    }


}
