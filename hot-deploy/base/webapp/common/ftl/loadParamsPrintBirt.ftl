<br/>
<#if listAddParamsIt?if_exists?has_content>
	<#assign index = 0>
	<#list listAddParamsIt as print>   
	    <p><input type="radio" class="print-addparams-radio" name="filterContentId" value="${print.contentId}"<#if index=0> checked=true </#if>/>${print.description}</span></p>       
	    <#assign index = index+1>  		
	</#list>    
</#if>
