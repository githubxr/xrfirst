package org.first.temp.utils.groovy;

import javax.script.Invocable;
import javax.script.ScriptException;

/**
 * 一个 templateId 对应一套“预编译过的脚本内容”
 * 每次执行时把代码 eval 到引擎实例里（隔离运行，不共享状态）
 */
public class GroovyEngineHolder {

    private final String scriptContent;

    public GroovyEngineHolder(String scriptContent) {
        this.scriptContent = scriptContent;
    }

    public Object invoke(String funcName, Object... args) {
        Invocable engine = ScriptEnginePool.borrowEngine();
        try {
            // 每次 borrow 出来都 eval 一次，保证无共享状态
            engine.getClass().getMethod("eval", String.class).invoke(engine, scriptContent);
            return engine.invokeFunction(funcName, args);
        } catch (Exception e) {
            throw new RuntimeException("Groovy 脚本执行失败", e);
        } finally {
            ScriptEnginePool.returnEngine(engine);
        }
    }
}
