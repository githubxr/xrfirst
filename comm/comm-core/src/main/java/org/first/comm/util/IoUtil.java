package org.first.comm.util;

import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * @since 2025/12/01
 * */
public class IoUtil {

    //读取文件所有内容为字符串
    public static String readToStr(String path) throws RuntimeException {
        String res = null;
        try(InputStream is = IoUtil.class.getClassLoader().getResourceAsStream(path)) {
            BufferedReader br = new BufferedReader((new InputStreamReader(is, StandardCharsets.UTF_8)));
            res = br.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException("读取目标文件（" + path + "）出错：" + e.getMessage());
        }
        return res;
    }

    //读取文件所有内容为字节数组
    public static byte[] getBytesByPath(String path) {
        try (InputStream is = IoUtil.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("资源不存在：" + path);
            return is.readAllBytes(); // 直接调用 JDK 原生方法，一行搞定！
        } catch (IOException e) {
            throw new RuntimeException("读取字节失败：" + path, e);
        }
    }

}
