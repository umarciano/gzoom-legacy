<#-- COLONNA CON GL_FISCAL_TYPE (OPT) -->
<#-- RENDER DEL TH - TESTATA -->
<#if renderThead == "Y">
    <#if context.glFiscalTypeId == "ALL">
    	<th><div>${uiLabelMap.FormFieldTitle_weTransTypeValueId}</div></th>
    	<#assign colspan = colspan + 1/>
    </#if>
<#else>
<#-- RENDER DEL TD - BODY -->    		                
    <#if renderOtherTd>
        <#if context.glFiscalTypeId == "ALL">
			<td rowspan="${rowspanPeriod}" >
			    <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
			        ${workEffortTransactionIndicatorView.weTransTypeValueDescLang?if_exists}
			    <#else>
			        ${workEffortTransactionIndicatorView.weTransTypeValueDesc?if_exists}
			    </#if>
			</td>
		</#if>
    </#if>
</#if>                                            