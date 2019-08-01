
	public ${className}() {
        super();
    }

    <#if (fieldMap?size>0)>
	public ${className}(<#list fieldMap?keys as fieldName>${fieldMap[fieldName]} ${fieldName}<#if fieldName_index != fieldMap?size-1>, </#if></#list>) {
        super();
	<#list fieldMap?keys as fieldName>
		this.${fieldName} = ${fieldName};
	</#list>
    }

	</#if>
<#list fieldMap?keys as fieldName>
    public void set${fieldName?cap_first}(${fieldMap[fieldName]} ${fieldName}){
		this.${fieldName}=${fieldName};
	}

	<#if fieldMap[fieldName] == 'Boolean' || fieldMap[fieldName] == 'boolean' >
	public ${fieldMap[fieldName]} is${fieldName?cap_first}(){
	<#else>
	public ${fieldMap[fieldName]} get${fieldName?cap_first}(){
	</#if>
		return this.${fieldName};
	}

</#list>
    public String toString() {
		return getClass().getSimpleName() + "@" + hashCode() <#if (fieldMap?size > 0)>+ ": {<#list fieldMap?keys as fieldName>${fieldName}:" + ${fieldName} <#if fieldName_index != fieldMap?size-1>+ ", </#if></#list></#if> + "}";
	}
