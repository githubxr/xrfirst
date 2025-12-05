package org.first.temp.service.impl;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.first.temp.service.TemplateService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * 基于 StringTemplateLoader 的模板管理：使用 templateName 作为 key，
 * 支持 update（覆盖）和重用（避免每次用 nanoTime）。
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    private final StringTemplateLoader loader;
    private final Configuration cfg;

    //只用freemarker自带缓存
//    // O P T I M I Z E D: 使用 Guava Cache 实现 LRU 和过期自动清理
//    private final Cache<String, String> htmlCache = CacheBuilder.newBuilder()
//            .maximumSize(1000)             // 最大缓存数量，避免内存溢出
//            .expireAfterAccess(30, TimeUnit.MINUTES) // 30分钟不访问则自动过期
//            .build();
    //注意
    public TemplateServiceImpl(StringTemplateLoader loader,@Qualifier("freemarkerConfig") Configuration cfg) {
        this.loader = loader;
        this.cfg = cfg;
    }

    @Override
    public Template compile(String templateName, String htmlContent) {
        loader.putTemplate(templateName, htmlContent);
        //htmlCache.put(templateName, htmlContent);

        try {
            // ★ 新加：强制清除 FreeMarker 缓存
            cfg.removeTemplateFromCache(templateName);
            return cfg.getTemplate(templateName);
        } catch (IOException e) {
            throw new RuntimeException("template compile failed", e);
        }
    }

    @Override
    public void update(String templateName, String htmlContent) {
        compile(templateName, htmlContent);
    }

    @Override
    public Template get(String templateName) {
        try {
            return cfg.getTemplate(templateName);
        } catch (IOException e) {
            throw new RuntimeException("get template failed", e);
        }
    }
}
