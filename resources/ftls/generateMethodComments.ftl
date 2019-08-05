
${indent}/**
<#list paramNames as paramName>
${indent} * @param ${paramName}
</#list>
<#if return !="void">
${indent} * @return ${return}
</#if>
${indent} * @since 1.0
${indent} * @author zongf
${indent} * @created ${date?substring(0,10)}
${indent} */