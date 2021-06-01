<table cellspacing="0" cellpadding="0" class="single-editable" style="<#if popup?default("N")=="N">margin-top: 1.3em;</#if> width: 90%;">
	<tbody>
			
		<#-- Parametri di Base (nel popup deve essere vuoto!) -->   
		<#if extraFieldBaseContainerScreenLocation?has_content && extraFieldBaseContainerScreenName?has_content && extraFieldBaseContainerScreenLocation?size == extraFieldBaseContainerScreenName?size> 
		    <#list 0..extraFieldBaseContainerScreenLocation?size-1 as i>
		    	${screens.render(extraFieldBaseContainerScreenLocation[i], extraFieldBaseContainerScreenName[i], context)}     	
		    </#list>
		</#if>
	
		<#-- Seleziona lista report da visualizzare -->
		<#if popup?default("N")=="Y">
		<tr id="select-print-row">
			<#if showSelectLabel?default("N") == "Y">
				<td  style="width: 18%;" class="label-for-print-popup">			    
				    <br/>
				    ${uiLabelMap.BaseSelectPrint}
				</td>
				<td class="widget-area-style" id="select-print-cell" >
			<#else>
				<td class="widget-area-style" id="select-print-cell" colspan="2" style="padding-top:1.5em; padding-left: 1.1em; "> 			
			</#if>
				<#if loadPrintBirtScreenLocation?has_content && loadPrintBirtScreenName?has_content>
					${screens.render(loadPrintBirtScreenLocation, loadPrintBirtScreenName, context)}
				</#if>
			</td>
		</tr>
		</#if>
	</tbody>
</table>


<#-- seleziona lista di parametri eprsonalizzati per il report -->
<div id="select-report-param-print-row">

</div>


