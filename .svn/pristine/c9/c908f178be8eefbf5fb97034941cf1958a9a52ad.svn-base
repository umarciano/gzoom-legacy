
<tr >
 <#if workEffortTypeList?has_content>	
	<td  class="label" style="width: 18%;">	${uiLabelMap.HeaderRootType}</td>
	<td class="widget-area-style" >	
	    <#assign index = 0>
	    <#list workEffortTypeList as print>  	    	
	        <p><input onClick="WorkEffortTypeIdWorkEffortIdExtPrintBirtExtraParameter.changeRadioButtons(this.getValue())" type="radio" class="print-radio" name="workEffortTypeId" value="${print.workEffortTypeId}" <#if index=0> checked=true </#if>/><span class="readonly"><#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>${print.descriptionTypeLang}<#else>${print.descriptionType}</#if></span> </p>
	        <#assign index = index+1>  		
	    </#list>
	</td>
<#else>
	<td class="label" style="width: 18%;">	${uiLabelMap.HeaderRootType}</td>
	<td class="widget-area-style" > ${uiLabelMap.NoReportFound}</td>	
</#if>
</tr>

