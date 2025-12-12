

@remark 把结单项目的动态PDF渲染代码也复刻在此模块中

@description 各种测试代码的家

@since 2025/12/01

@author xr





PDF渲染调用链路：

pdfServiceImpl.**genPdfBatchToDir**(model, strs)	

​	-> buildHtml

​	     templateService.**compile**(model.getTemplateName(), fullHtml)

​	     for(str: strs) 

​		->  Template tpl = templateService.**get**(model.getTemplateName())

​		     **genPdfToDir**(model, tpl, json)

​		    	 -> root = objectMapper.readValue(json, Map.class)

​				 scriptService.**init**

​				 root.put("g", new GroovyFunctionContainer)

​				 fonts.addAll(resourceService.getActiveFonts())

​				 pdfRenderer.**render**(htmlOut.toString(), fonts, null, fos)







