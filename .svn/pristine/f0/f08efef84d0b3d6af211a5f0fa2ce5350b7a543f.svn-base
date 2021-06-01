<table cellspacing="0" cellpadding="0" class="single-editable" style="<#if popup?default("N")!="Y" || ((!extraFieldContainerScreenLocation?has_content && !extraFieldContainerScreenName?has_content) || extraFieldContainerScreenLocation?size != extraFieldContainerScreenName?size)>margin-top: 1.3em;</#if> width: 90%;">
<tbody>
<div name="reportContainer">  <#-- div che conterrà le varie form caricate per ogni singolo report  --> 

</div>
<#-- Entro solo quadno nn sono nel popup e carica il file .ftl con la lista dei parametri -->   
<#if popup?default("N") != 'Y'>
	<#if extraFieldContainerScreenLocation?has_content && extraFieldContainerScreenName?has_content && extraFieldContainerScreenLocation?size == extraFieldContainerScreenName?size> 
	    <#list 0..extraFieldContainerScreenLocation?size-1 as i>
	    <#--<#if isLoadGenericPrint?has_content && isLoadGenericPrint=="N"> -->
	    	${screens.render(extraFieldContainerScreenLocation[i], extraFieldContainerScreenName[i], context)}     	
	    <#--</#if> -->
	    </#list>
	</#if>
</#if>
<tr id="select-print-row">
<#if showSelectLabel?default("N") == "Y">
<td class="label<#if popup?default("N")=="Y">-for-print-popup</#if>">
    <#if excludeSez?has_content>
	    <br/>
	    ${uiLabelMap.WorkEffortAnalysis_ExcludeSez}
	    <br/><br/><br/><br/><br/><br/>
    </#if>
    <br/>
    ${uiLabelMap.BaseSelectPrint}
</td>
<td class="widget-area-style" id="select-print-cell">
<#else>
<td class="widget-area-style" id="select-print-cell" colspan="2" <#if extraFieldContainerScreenLocation?has_content && extraFieldContainerScreenName?has_content && extraFieldContainerScreenLocation?size == extraFieldContainerScreenName?size>style="padding-top:1.5em; padding-left: 1.1em;"</#if>> 
</#if>
<#if loadPrintBirtScreenLocation?has_content && loadPrintBirtScreenName?has_content>
${screens.render(loadPrintBirtScreenLocation, loadPrintBirtScreenName, context)}
</#if>
</td>
</tr>
<#-- seleziona parametri aggiuntivi -->
<tr id="select-addparams-print-row">
	<td class="label">
		<br/>
		${uiLabelMap.BaseSelectAdditionalParams}
	</td>
	<td class="widget-area-style" id="select-addparams-print-cell" colspan="2"> 
		<#if loadParamsPrintBirtScreenLocation?has_content && loadParamsPrintBirtScreenName?has_content>
			${screens.render(loadParamsPrintBirtScreenLocation, loadParamsPrintBirtScreenName, context)}
		</#if>
	</td>
</tr>
<#-- seleziona formato -->
<tr id="select-type-print-row">
	<td class="label">
		<br/>
		${uiLabelMap.BaseSelectTypePrint}
	</td>
	<td class="widget-area-style" id="select-type-print-cell" colspan="2"> 
		<#if loadTypePrintBirtScreenLocation?has_content && loadTypePrintBirtScreenName?has_content>
			${screens.render(loadTypePrintBirtScreenLocation, loadTypePrintBirtScreenName, context)}
		</#if>
	</td>
</tr>
</tbody>
</table>