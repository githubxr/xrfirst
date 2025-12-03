package org.first.temp.service;

import org.first.temp.entity.FontResource;

import java.util.List;

/**
 * @description 资源管理
 *
 * */
public interface ResourceService {
    void registerFont(String fontKey, byte[] fontBytes);
    void removeFont(String fontKey);
    FontResource getFont(String fontKey);
    List<FontResource> getActiveFonts();
    String getCss(String cssName);
    void registerCss(String name, String cssContent);
}
