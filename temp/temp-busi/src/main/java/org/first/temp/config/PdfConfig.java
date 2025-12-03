package org.first.temp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * Spring-friendly PDF service that uses Freemarker + OpenHTMLtoPDF.
 * - Freemarker Configuration and ThreadPoolTaskExecutor are provided as beans
 * - Resources (css/font/templates) are loaded via ResourceLoader
 * - Each render task creates its own PdfRendererBuilder (no global lock)
 */

@org.springframework.context.annotation.Configuration
public class PdfConfig {

    @Bean("freemarkerConfig")
    public Configuration freemarkerConfig(StringTemplateLoader dynamicLoader) {
        Configuration cfg = new Configuration(new Version("2.3.28"));
        // 只用动态 StringTemplateLoader（不要用 FileTemplateLoader）
        cfg.setTemplateLoader(dynamicLoader);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        return cfg;
    }

    //缓存模板工具避免每次执行都重新读取
    @Bean
    public StringTemplateLoader dynamicLoader() {
        return new StringTemplateLoader();
    }

    @Bean(name = "pdfExecutor")
    public ThreadPoolTaskExecutor pdfExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(4);
        exec.setMaxPoolSize(8);
        exec.setQueueCapacity(200);
        exec.setThreadNamePrefix("pdf-exec-");
        exec.initialize();

        // 关键：配置拒绝策略（调用者线程执行，避免任务丢失，适合低并发兜底）
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return exec;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

