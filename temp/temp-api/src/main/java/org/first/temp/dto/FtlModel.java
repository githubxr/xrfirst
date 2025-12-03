package org.first.temp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description PDF模板配置信息
 * @since 25/12/01
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FtlModel {
    private String id;
    private String template_code;
    private String templateName;
    private String savePath;//指定保存目录
    private String contentRef;//模板正文在服务器的文件路径
    private String scriptContent;//模板脚本内容
    private String firstHeaderRef;//模板首页页头在服务器的文件路径
    private String secHeaderRef;//模板非首页页头在服务器的文件路径

}
