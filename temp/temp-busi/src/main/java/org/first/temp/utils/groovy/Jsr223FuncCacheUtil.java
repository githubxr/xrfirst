package org.first.temp.utils.groovy;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 对外暴露给业务调用的缓存工具类
 */
public class Jsr223FuncCacheUtil {

    private static final ConcurrentMap<String, GroovyEngineHolder> HOLDER_MAP = new ConcurrentHashMap<>();

    public static void initScriptForTemplate(String templateId, String scriptContent) {
        HOLDER_MAP.put(templateId, new GroovyEngineHolder(scriptContent));
    }

    public static void reloadScript(String templateId, String scriptContent) {
        HOLDER_MAP.put(templateId, new GroovyEngineHolder(scriptContent));
    }

    public static Object invoke(String templateId, String funcName, Object... args) {
        GroovyEngineHolder holder = HOLDER_MAP.get(templateId);
        if (holder == null) {
            throw new RuntimeException("脚本未初始化: " + templateId);
        }
        return holder.invoke(funcName, args);
    }

    public static boolean hasTemplate(String templateId) {
        return HOLDER_MAP.containsKey(templateId);
    }
}
