
<h2>def title</h2>
<#if .data_model["0000"]["0410"]?size != 0>
<h1>exist data</h1>
<#list .data_model["0000"]["0410"] as item>
    <ul>
        <li>name:${item.name}</li>
        <li>age:${item.age}</li>
        <li>calcConcat: ${g.calcConcat(item)}</li>
        <#if g.caleShowNum(item)>
            <li>成功执行groovy计算结果 布尔-goodsNum:${item.goodsNum}</li>
        </#if>
    </ul>
</#list>
</#if>
