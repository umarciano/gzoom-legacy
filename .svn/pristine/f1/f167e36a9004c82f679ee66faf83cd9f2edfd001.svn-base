<#assign dateFormat = Static["org.ofbiz.base.util.UtilDateTime"].getDateFormat(locale)>
<form method="post"  action=""  id="WEVMP001_WorkEffortView" class="basic-form parent-form" name="WorkEffortAchieveViewManagementParentForm">
<#assign pattern = "##"/>
<table cellspacing="0" cellpadding="0" class="basic-table list-table padded-row-table" style="background-color: RGB(222,222,222)">
  <tr>
   <#--<td class="label" style="width: 15%;">${comments?if_exists}</td>-->
   <td>
   <#assign weAnalisis = delegator.findOne("WorkEffortAnalysis", Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortAnalysisId", parameters.workEffortAnalysisId), true)>
    <div style="padding-bottom: 0.3em; text-align: center;">
        <span style="font-size: 2.3em">${weAnalisis.description?if_exists}</span>
    </div>
    <div style="padding-top: 0.3em">
    	<#if !weAnalisis.workEffortId?has_content || (weAnalisis.workEffortId?has_content && weAnalisis.workEffortId != (item.workEffortId)?if_exists)>
    		<table>
    			<tbody>
    				<tr>
    					<td style="width: 30%; border: none;">
    						<span style="font-size: 2em; padding-bottom: 0.3em;"><#if (item.etch)?has_content>${(item.etch)?if_exists} - </#if>${(item.comments)?if_exists}</span>
    					</td>
    					<td style="width: 70%; border: none;">
    						<#assign workEffortName = ((item.workEffortName)?if_exists)?default("")>
	    					<#if workEffortName?length &gt; 150>
	                            <#assign workEffortName = StringUtil.wrapString(item.workEffortName?substring(0,150))+"...">
	                        </#if>
    						<div style="font-weight: bold; font-size: 2em; font-style: italic" class="mblShowDescription" <#if (item.description)?has_content> description="${(item.description)?if_exists}"</#if>>${(workEffortName)?if_exists}</div>
    					</td>
    				</tr>
    			</tbody>
    		</table>
        </#if>
      </div>
   </td>
   <td  style="padding: 0.5em 1px 1px 1px !important; text-align: center !important; width: 85px;">
        <div>
            <#if ""!=(item.imageSrc)?if_exists> 
                <img class="speedometer speedometer_enlarge" src="${(item.imageSrc)?if_exists}" speed="${(item.imageValue)?if_exists}" target="${(item.budgetValue)?if_exists}"></img>
            <#else>
                <div class="noSpeedometerPresent"><span>${uiLabelMap.NoSpeedometerPresent}</span></div>
            </#if>
        </div>
    </td>
    <td style="padding: 0.5em 1px 1px 1px !important; text-align:center !important; width: 100px;" >
        <#if (item.imageValue)?has_content>
            <#assign singleHeaderTitle = false>
            <#if !(item.budgetValue)?has_content>
                <#assign singleHeaderTitle = true>
            </#if>
            <#if !singleHeaderTitle>
            <div class="label" style="padding: 0.1em 0.6em 0.1em 0em !important; text-align: center !important; width: 100%">
                ${uiLabelMap.WorkEffortAchieveViewRealized}
            </div>
            <div class="label" style="padding: 0.1em 0.6em 0.1em 0em !important; text-align: center !important; width: 100%">
                ${uiLabelMap.WorkEffortAchieveViewProgrammed}
            </div>
            <#else>
            <div class="label" style="padding: 0.1em 0.6em 0.1em 0em !important; text-align: center !important; width: 100%">
                ${uiLabelMap.WorkEffortAchieveViewPerformance}
            </div>
            </#if>
            <div>
            	<#if item.imageValue?has_content>
                    <#assign imageValue = Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.imageValue, pattern, locale)/>
                    ${imageValue?if_exists}%
                </#if>   
            </div>
            <div>
            	<#if item.budgetValue?has_content>
                    <#assign budgetValue = Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.budgetValue, pattern, locale)/>
                    ${budgetValue?if_exists}%
                </#if>  
            </div>
        </#if>
    </td>
   
  </tr>
</table>
</form>