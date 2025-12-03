package org.first.temp.utils.groovy;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.first.basecore.util.IoUtil;
import org.first.temp.entity.TestClass;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import groovy.json.JsonSlurper; // 导入 Groovy 的 JsonSlurper
import java.util.Map;


public class Jsr223SimpleDemo {

    // JSR 223脚本引擎（仅初始化1次）
    private static ScriptEngine groovyEngine;
    // 可调用接口（用于调用脚本中的函数）
    private static Invocable invocable;

    void simpleTest(){
        // 2. 业务参数
        TestClass item = new TestClass();
        item.setId("111");
        item.setGoodsCode("123");
        item.setGoodsNum(25);

        try {
            // 3. 指定函数名执行（无需重复解析脚本）
            Object discount = invocable.invokeFunction("calcDiscount", item);
            //Object price = invocable.invokeFunction("calcPrice", item, discount);
            //Object goodsInfo = invocable.invokeFunction("getGoodsInfo", item);

            System.out.println("折扣：" + discount); // 输出：0.9
            //System.out.println("总价：" + price);   // 输出：2250.0
            //System.out.println("商品信息：" + goodsInfo); // 输出：商品编码：123，数量：25
        } catch (NoSuchMethodException | ScriptException e) {
            e.printStackTrace();
        }
    }

    static void test2(){

        String jsonStr = IoUtil.readToStr("static/json/sample1.json");
        JSONObject fastJsonObj = JSON.parseObject(jsonStr);

        // 2. 关键优化：Java 侧直接转为 Groovy 原生 Map（避免脚本中转换）
        JsonSlurper jsonSlurper = new JsonSlurper();
        Map<String, Object> groovyJson = (Map<String, Object>) jsonSlurper.parseText(jsonStr);


        try {
            Object test1 = invocable.invokeFunction("acNum", groovyJson);
            System.out.println("test1:" + test1);
        } catch (NoSuchMethodException | ScriptException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 1. 初始化：加载脚本+初始化引擎（项目启动时执行1次即可）
        initScriptEngine("static/scriptSample/simpleGroovy");
        //simpleTest

        test2();

    }

    /**
     * 初始化JSR 223脚本引擎，解析脚本（只执行1次）
     * @param scriptPath 脚本文件路径
     */
    public static void initScriptEngine(String scriptPath) {
        try {
            // 1. 获取Groovy脚本引擎
            ScriptEngineManager manager = new ScriptEngineManager();
            groovyEngine = manager.getEngineByName("groovy");
            if (groovyEngine == null) {
                throw new RuntimeException("未找到Groovy脚本引擎，请检查依赖");
            }

            // 2. 读取脚本内容并解析（核心：只解析1次）
            String scriptContent = IoUtil.readToStr(scriptPath);
            groovyEngine.eval(scriptContent);

            // 3. 转为Invocable接口（用于调用脚本中的函数）
            if (!(groovyEngine instanceof Invocable)) {
                throw new RuntimeException("当前脚本引擎不支持调用函数");
            }
            invocable = (Invocable) groovyEngine;

            System.out.println("JSR 223脚本引擎初始化完成，支持调用脚本中所有函数");
        } catch (ScriptException e) {
            throw new RuntimeException("脚本解析失败", e);
        }
    }
}