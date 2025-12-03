package org.first.temp.entity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class FontResource {

    private final String fontName;
    private final byte[] bytes;

    public FontResource(String fontName, byte[] bytes) {
        this.fontName = fontName;
        this.bytes = bytes;
    }

    public String getFontName() {
        return fontName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    /** 提供一个新的 InputStream，避免缓存 */
    public InputStream openStream() {
        return new ByteArrayInputStream(bytes);
    }
}
