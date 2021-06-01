<br/><br/>
<#if listTypePrintIt?if_exists?has_content>
	<#assign index = 0>
	<#list listTypePrintIt as print>   
	    <p><input type="radio" class="print-type-radio" name="outputFormat" value="${print.mimeTypeId}" <#if index=0> checked=true </#if>/><span class="readonly">${print.description}</span> </p>       
	    <#assign index = index+1>  		
	</#list>    
</#if>
