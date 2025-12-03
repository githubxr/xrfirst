package org.first.temp.utils.groovy;

import org.first.basecore.util.IoUtil;
import org.first.temp.entity.TestClass;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @since 2025/11/28
 * @description 临时测试 Groovy 可行性：可行 √
 * */
public class GroovySample {


    //简单测试
    public static void main(String[] args) {

        TestClass item = new TestClass();
        item.setId("111");
        item.setGoodsCode("123");
        item.setGoodsNum(25);

//        // 方式1：使用 Groovy 原生 API（更灵活）
//        // 1. 构造 Groovy 绑定（传递参数）
//        Binding binding = new Binding();
//        binding.setVariable("item", item);
//
        // 2. 前端传递的 Groovy 脚本字符串
        String script = IoUtil.readToStr("static/scriptSample/simpleGroovy");
//
//        // 3. 执行脚本
//        GroovyShell shell = new GroovyShell(binding);
//        Object result = shell.evaluate(script);
//
//        // 4. 业务逻辑使用结果
//        System.out.println("Groovy 执行结果：" + result); // 输出：李四


        // 方式2：使用 JSR 223 脚本引擎（统一接口）
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine groovyEngine = manager.getEngineByName("groovy");
        try {
            groovyEngine.put("item", item);
            Object engineResult = groovyEngine.eval(script);
            System.out.println("JSR223 执行结果：" + engineResult); // 输出：李四
        } catch (javax.script.ScriptException e) {
            e.printStackTrace();
        }
    }
}
