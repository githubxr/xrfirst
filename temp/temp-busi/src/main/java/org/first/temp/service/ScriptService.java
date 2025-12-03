package org.first.temp.service;

/**
 * @description Groovy脚本工具
 * */
public interface ScriptService {
    void init(String templateId, String scriptContent);
    void reload(String templateId, String scriptContent);
    Object invoke(String templateId, String funcName, Object... args);
    boolean has(String templateId);
}
