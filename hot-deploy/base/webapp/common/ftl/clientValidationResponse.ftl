<ul>
	
	<#if (parameters._ERROR_MESSAGE_?has_content)>
	    <li class="response">ERROR</li>
	    <li class="error-message">${parameters._ERROR_MESSAGE_}</li>
	<#else>
		<#if (parameters._ERROR_MESSAGE_LIST_?has_content)>
		    <#list parameters._ERROR_MESSAGE_LIST_ as errorMsg>
		        <#if (errorMsg?has_content)>
				    <li class="response">ERROR</li>
				    <li class="error-message">${errorMsg}</li>
		        </#if>
		    </#list>
		<#else>
			<li class="response">OK</li>
		    <li class="formatted-value">${parameters._EVENT_MESSAGE_?if_exists}</li>
		</#if>
	</#if>	

</ul>
