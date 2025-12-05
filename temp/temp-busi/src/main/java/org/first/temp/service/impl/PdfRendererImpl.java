package org.first.temp.service.impl;


import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.first.temp.entity.FontResource;
import org.first.temp.service.PdfRenderer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 每次 render 都使用新的 PdfRendererBuilder，并把字体通过 InputStream 注入。
 * baseUri 可以传 null，也可以传静态资源基准地址（css/img 引用）。
 */
@Service
public class PdfRendererImpl implements PdfRenderer {

    // 【新方法：直接写入 OutputStream】
    @Override
    public void render(String html, List<FontResource> fonts, String baseUri, OutputStream os) {
        try {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, baseUri);

            if (fonts != null) {
                for (FontResource font : fonts) {
                    // 每个 useFont 都传入新的 InputStream provider
                    builder.useFont(font::openStream, font.getFontName());
                }
            }

            // 【关键变化】：直接将结果导向传入的 OutputStream
            builder.toStream(os);
            builder.run();

        } catch (Exception e) {
            throw new RuntimeException("PDF render failed", e);
        }
    }

}