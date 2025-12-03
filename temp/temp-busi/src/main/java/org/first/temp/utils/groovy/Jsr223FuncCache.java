package org.first.temp.utils.groovy;


import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @since 25/12/03
 * @remark 使用：启动或首次渲染前调用 initScriptForTemplate(templateId, scriptPath)
 * @description 通过initScriptForTemplate
 *  加载 Groovy 脚本、 Eval 一次
 *  缓存 Invocable，
 *  并允许后续按 templateId 调用脚本函数，
 *  支持重载/热更新
 */
public class Jsr223FuncCache {

    private static final ScriptEngineManager MANAGER = new ScriptEngineManager();
    private static final ConcurrentMap<String, Invocable> INVOCABLE_MAP = new ConcurrentHashMap<>();//使用线程安全的容器装载


    // 初始化并缓存脚本（线程安全）
    public static void initScriptForTemplate(String templateId, String scriptContent) {
        try {
            //获取引擎
            ScriptEngine engine = MANAGER.getEngineByName("groovy");
            if (engine == null) {
                throw new RuntimeException("未找到 Groovy 脚本引擎，请检查依赖");
            }
            // eval 脚本（请确保脚本不包含危险操作）
            //执行脚本，把脚本里的 def 函数都定义到 engine 的上下文里
            engine.eval(scriptContent);
            if (!(engine instanceof Invocable)) {
                throw new RuntimeException("脚本引擎不支持 Invocable");
            }
            //全局缓存
            INVOCABLE_MAP.put(templateId, (Invocable) engine);
        } catch (ScriptException e) {
            throw new RuntimeException("初始化脚本失败: ", e);
        }
    }


    // 重新加载脚本（覆盖）
    public static void reloadScript(String templateId, String scriptPath) {
        initScriptForTemplate(templateId, scriptPath);
    }


    // 调用脚本函数
    public static Object invoke(String templateId, String funcName, Object... args) {
        Invocable inv = INVOCABLE_MAP.get(templateId);
        if (inv == null) {
            throw new RuntimeException("未初始化脚本，templateId=" + templateId);
        }
        try {
            return inv.invokeFunction(funcName, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("脚本函数不存在: " + funcName + " in templateId=" + templateId, e);
        } catch (ScriptException e) {
            throw new RuntimeException("执行脚本函数异常: " + funcName + " in templateId=" + templateId, e);
        }
    }


    public static boolean hasTemplate(String templateId) {
        return INVOCABLE_MAP.containsKey(templateId);
    }
}