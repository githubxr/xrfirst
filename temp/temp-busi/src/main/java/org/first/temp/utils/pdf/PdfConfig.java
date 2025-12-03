package org.first.temp.utils.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.io.IOException;

/**
 * Spring-friendly PDF service that uses Freemarker + OpenHTMLtoPDF.
 * - Freemarker Configuration and ThreadPoolTaskExecutor are provided as beans
 * - Resources (css/font/templates) are loaded via ResourceLoader
 * - Each render task creates its own PdfRendererBuilder (no global lock)
 */

@org.springframework.context.annotation.Configuration
public class PdfConfig {

    @Bean
    public freemarker.template.Configuration freemarkerConfig() throws IOException, TemplateModelException {
        Configuration cfg = new  Configuration(new Version("2.3.28"));
        cfg.setDefaultEncoding("UTF-8");

        // 固定模板目录，不再每次被覆盖
        FileTemplateLoader fileLoader = new FileTemplateLoader(new File("templates/"));

        // 动态模板 loader（用于 PDFUtil 动态注入）
        StringTemplateLoader dynamicLoader = new StringTemplateLoader();

        MultiTemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[]{
                fileLoader,
                dynamicLoader
        });
        cfg.setTemplateLoader(mtl);

        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);

        // !!! 将动态 loader 放入 Spring，供 PdfUtil 使用
        cfg.setSharedVariable("dynamicLoader", dynamicLoader);

        return cfg;
    }


    //项目结构混乱，不好引用core模块的线程池配置，所以暂且在pdf模块引入
    @Bean(name = "pdfExecutor")
    public ThreadPoolTaskExecutor pdfExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(4);
        exec.setMaxPoolSize(8);
        exec.setQueueCapacity(200);
        exec.setThreadNamePrefix("pdf-exec-");
        exec.initialize();
        return exec;
    }

    //ojbectMapper实例
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

