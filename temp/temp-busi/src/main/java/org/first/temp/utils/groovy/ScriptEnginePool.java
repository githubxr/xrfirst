package org.first.temp.utils.groovy;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 线程安全的 Groovy ScriptEngine 对象池
 * 每次执行脚本从池里借一个，不共享上下文，不会互相污染。
 */
public class ScriptEnginePool {

    private static final int POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final BlockingQueue<Invocable> POOL = new ArrayBlockingQueue<>(POOL_SIZE);

    static {
        ScriptEngineManager manager = new ScriptEngineManager();
        for (int i = 0; i < POOL_SIZE; i++) {
            ScriptEngine engine = manager.getEngineByName("groovy");
            POOL.add((Invocable) engine);
        }
    }

    public static Invocable borrowEngine() {
        try {
            return POOL.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("无法从 Groovy 引擎池借出引擎", e);
        }
    }

    public static void returnEngine(Invocable engine) {
        POOL.offer(engine);
    }
}
