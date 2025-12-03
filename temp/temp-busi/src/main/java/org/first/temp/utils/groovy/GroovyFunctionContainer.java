package org.first.temp.utils.groovy;


import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;


/**
 * 注入到 Freemarker root 的容器对象（一般使用键名为 "g"），
 * 当模板写 `g.funcName` 时会触发 Freemarker 调用 get(funcName)，
 * 返回一个 TemplateMethodModelEx（包装器），真正去调用 Groovy 函数。
 */
public class GroovyFunctionContainer implements TemplateHashModel {


    private final String templateId;


    public GroovyFunctionContainer(String templateId) {
        this.templateId = templateId;
    }


    @Override
    public TemplateModel get(String key) throws TemplateModelException {
// 返回对特定函数名的 wrapper
        return new GroovyFunctionWrapper(templateId, key);
    }


    @Override
    public boolean isEmpty() throws TemplateModelException {
        return false;
    }
}