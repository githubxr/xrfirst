package org.first.temp.service;

import freemarker.template.Template;

/**
 * @description freemarker模板工具
 * */
public interface TemplateService {
    Template compile(String templateName, String htmlContent);
    void update(String templateName, String htmlContent);
    Template get(String templateName);

}
