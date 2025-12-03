package org.first.temp.utils.groovy;



import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;


import java.util.ArrayList;
import java.util.List;


/**
 * 将模板调用转换为对 Jsr223FuncCache.invoke 的调用。
 * 注意：会把模板传进来的 TemplateModel 参数做 DeepUnwrap 转换为 Java 对象。
 */
public class GroovyFunctionWrapper implements TemplateMethodModelEx {


    private final String templateId;
    private final String funcName;


    public GroovyFunctionWrapper(String templateId, String funcName) {
        this.templateId = templateId;
        this.funcName = funcName;
    }


    @Override
    public Object exec(List arguments) throws TemplateModelException {
        try {
            Object[] args = new Object[arguments == null ? 0 : arguments.size()];
            if (arguments != null) {
                List<Object> temp = new ArrayList<>();
                for (Object o : arguments) {
                    // Freemarker 会传入 TemplateModel 的实现，DeepUnwrap 能把它转换为 Java 对象
                    Object unwrapped = DeepUnwrap.unwrap((TemplateModel) o);
                    temp.add(unwrapped);
                }
                args = temp.toArray();
            }
            Object result = Jsr223FuncCacheUtil.invoke(templateId, funcName, args);
            return result;
        } catch (Exception e) {
            throw new TemplateModelException("调用脚本函数失败: " + funcName + " templateId=" + templateId, e);
        }
    }
}