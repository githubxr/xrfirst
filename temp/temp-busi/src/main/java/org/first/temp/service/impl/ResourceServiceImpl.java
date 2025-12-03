package org.first.temp.service.impl;

import org.first.temp.entity.FontResource;
import org.first.temp.service.ResourceService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 内存字体池 + css 管理。register/remove 即为“reload”操作。
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    private final ConcurrentMap<String, byte[]> fontPool = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> cssPool = new ConcurrentHashMap<>();
    private final ResourceLoader resourceLoader;

    public ResourceServiceImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        // 可以在这里预加载默认 css 字体
        try {
            Resource css = resourceLoader.getResource("classpath:static/def.css");
            if (css.exists()) {
                String c = new String(css.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                cssPool.put("default", c);
            }
            Resource f = resourceLoader.getResource("classpath:static/font/sim.ttf");
            if (f.exists()) {
                byte[] b = f.getInputStream().readAllBytes();
                fontPool.put("Microsoft YaHei", b);
            }
        } catch (Exception ignore) {}
    }

    @Override
    public void registerFont(String fontKey, byte[] fontBytes) {
        fontPool.put(fontKey, fontBytes);
    }

    @Override
    public void removeFont(String fontKey) {
        fontPool.remove(fontKey);
    }

    @Override
    public FontResource getFont(String fontKey) {
        byte[] b = fontPool.get(fontKey);
        return b == null ? null : new FontResource(fontKey, b);
    }

    @Override
    public List<FontResource> getActiveFonts() {
        List<FontResource> list = new ArrayList<>();
        for (var entry : fontPool.entrySet()) {
            list.add(new FontResource(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    @Override
    public String getCss(String cssName) {
        return cssPool.get(cssName);
    }

    @Override
    public void registerCss(String name, String cssContent) {
        cssPool.put(name, cssContent);
    }
}