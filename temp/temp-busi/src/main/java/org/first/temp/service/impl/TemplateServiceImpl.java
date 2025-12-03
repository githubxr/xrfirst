package org.first.temp.service.impl;


import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.first.temp.service.TemplateService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于 StringTemplateLoader 的模板管理：使用 templateName 作为 key，
 * 支持 update（覆盖）和重用（避免每次用 nanoTime）。
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    private final StringTemplateLoader loader;
    private final Configuration cfg;
    //有必要缓存模板
    private final ConcurrentMap<String, String> htmlCache = new ConcurrentHashMap<>();

    //注意
    public TemplateServiceImpl(StringTemplateLoader loader,@Qualifier("freemarkerConfig") Configuration cfg) {
        this.loader = loader;
        this.cfg = cfg;
    }

    @Override
    public Template compile(String templateName, String htmlContent) {
        loader.putTemplate(templateName, htmlContent);
        htmlCache.put(templateName, htmlContent);
        try {
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
