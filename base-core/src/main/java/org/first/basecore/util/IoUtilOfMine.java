package org.first.basecore.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class IoUtilOfMine {

    public static String readToStr(String path) throws RuntimeException {
        String res = null;
        try(InputStream is = IoUtilOfMine.class.getClassLoader().getResourceAsStream(path)) {
            BufferedReader br = new BufferedReader((new InputStreamReader(is, StandardCharsets.UTF_8)));
            res = br.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException("读取目标文件（" + path + "）出错：" + e.getMessage());
        }
        return res;
    }
}
