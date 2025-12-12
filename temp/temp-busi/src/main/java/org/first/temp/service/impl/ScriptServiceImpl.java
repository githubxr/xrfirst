package org.first.temp.service.impl;

import org.first.temp.service.ScriptService;
import org.springframework.stereotype.Service;

import javax.script.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基本的脚本缓存与调用器。把脚本编译并保存 ScriptEngine/Bindings。
 * 注意：生产环境请考虑限制脚本能力（安全）和内存回收。
 */
@Service
public class ScriptServiceImpl implements ScriptService {

    private final ScriptEngineManager manager = new ScriptEngineManager();
    private final ConcurrentMap<String, CompiledScriptHolder> holderMap = new ConcurrentHashMap<>();

    @Override
    public void init(String templateId, String scriptContent) {
        reload(templateId, scriptContent);
    }

    @Override
    public void reload(String templateId, String scriptContent) {
        ScriptEngine engine = manager.getEngineByName("groovy");
        if (engine == null) throw new RuntimeException("groovy script engine not available");
        if (!(engine instanceof Compilable)) {
            throw new RuntimeException("script engine not compilable");
        }
        Compilable comp = (Compilable) engine;
        try {//编译脚本内容（所有函数）
            CompiledScript cs = comp.compile(scriptContent == null ? "" : scriptContent);
            holderMap.put(templateId, new CompiledScriptHolder(cs, engine));
        } catch (ScriptException e) {
            throw new RuntimeException("script compile failed", e);
        }
    }

    @Override
    public Object invoke(String templateId, String funcName,  Object... args) {
        CompiledScriptHolder holder = holderMap.get(templateId);
        if (holder == null) throw new RuntimeException("script not initialized: " + templateId);

        // 每次 eval 出一个独立 Bindings，确保线程隔离
        try {
            Bindings bindings = holder.engine.createBindings();
            holder.compiledScript.eval(bindings);

            if (holder.engine instanceof Invocable) {
                Invocable inv = (Invocable) holder.engine;

                // 核心：将 FreeMarker 的 root 绑定为 Groovy 的全局变量（命名为 root/ctx 均可）
                //holder.engine.getContext().setAttribute("root", root, ScriptContext.ENGINE_SCOPE);
                // Groovy: define function like: def myFunc(a,b) { ... }
                return inv.invokeFunction(funcName, args);
            } else {
                throw new RuntimeException("engine not invocable");
            }
        } catch (ScriptException se) {
            throw new RuntimeException("script eval failed", se);
        } catch (NoSuchMethodException nsme) {
            throw new RuntimeException("no such function: " + funcName, nsme);
        }
    }

    @Override
    public boolean has(String templateId) {
        return holderMap.containsKey(templateId);
    }

    private static class CompiledScriptHolder {
        final CompiledScript compiledScript;
        final ScriptEngine engine;
        CompiledScriptHolder(CompiledScript compiledScript, ScriptEngine engine) {
            this.compiledScript = compiledScript;
            this.engine = engine;
        }
    }
}