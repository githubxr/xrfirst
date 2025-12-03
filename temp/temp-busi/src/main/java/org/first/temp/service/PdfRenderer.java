package org.first.temp.service;

import org.first.temp.entity.FontResource;

import java.io.OutputStream;
import java.util.List;

/**
 * 提供渲染方法
 * */
public interface PdfRenderer {
    ////大文件不适用 byte[] render(String html, List<FontResource> fonts, String baseUri);
    // 推荐方法：将结果写入指定的输出流
    void render(String html, List<FontResource> fonts, String baseUri, OutputStream os);
}
