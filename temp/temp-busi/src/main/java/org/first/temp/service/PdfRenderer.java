package org.first.temp.service;

import org.first.temp.entity.FontResource;

import java.util.List;

/**
 * 提供渲染方法
 * */
public interface PdfRenderer {
    byte[] render(String html, List<FontResource> fonts, String baseUri);
}
