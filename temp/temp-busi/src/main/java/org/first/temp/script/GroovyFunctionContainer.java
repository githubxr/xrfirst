package org.first.temp.script;


import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.first.temp.service.ScriptService;


/**
 * 注入到 Freemarker root 的容器对象（一般使用键名为 "g"），
 * 当模板写 `g.funcName` 时会触发 Freemarker 调用 get(funcName)，
 * 返回一个 TemplateMethodModelEx（包装器），真正去调用 Groovy 函数。
 */
public class GroovyFunctionContainer implements TemplateHashModel {


    private final String templateId;
    private final ScriptService scriptService;


    public GroovyFunctionContainer(String templateId, ScriptService scriptService) {
        this.templateId = templateId;
        this.scriptService = scriptService;
    }


    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        return new GroovyFunctionWrapper(templateId, key, scriptService);
    }


    @Override
    public boolean isEmpty() throws TemplateModelException {
        return false;
    }
}