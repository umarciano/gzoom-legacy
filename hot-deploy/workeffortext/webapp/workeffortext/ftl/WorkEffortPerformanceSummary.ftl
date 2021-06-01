<table id="table_WorkEffortPlanPerformanceSummaryManagementListForm_${parameters.weContextId_value}" class="basic-table list-table padded-row-table hover-bar resizable draggable toggleable selectable customizable headerFixable" cellspacing="0">
   <thead>
        <tr class="header-row-2">
            <th>${uiLabelMap.OrganizationUnit}</th>
            <#list statusItemList?if_exists as statusItem>
                <#if statusItem?has_content>
                <th>${statusItem.get("statusDescr")?if_exists}</th>
                </#if>
            </#list>
            <th>${uiLabelMap.CommonTotal}</th>
        </tr>
    </thead>
    <tbody>
        <#assign index=0/>
        <#list listIt?if_exists as workEffort>
        	<#assign orgUnitTotal=0/>
        	<#if orgUnitTotalsMap?has_content>
        		<#assign orgUnitTotal=orgUnitTotalsMap.get(workEffort.orgUnitId?if_exists)?if_exists/>
        	</#if>
        
        
            <tr <#if index%2 != 0>class="alternate-row"</#if>>
                <td>
                    <input type="hidden" name="orgUnitId" id="orgUnitId" value="${workEffort.orgUnitId?if_exists}"/>
                    <input type="hidden" name="entityName" id="entityName" value="WorkEffortRootInqySummaryView"/>
                    <input type="hidden" name="weContextId_value" id="weContextId_value" value="${parameters.weContextId_value}"/>
                    
                    <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                    	<div class="orgUnitColumn" title="${workEffort.parentRoleCode?if_exists} - ${workEffort.partyNameLang?if_exists}">
                    		${workEffort.parentRoleCode?if_exists} - ${workEffort.partyNameLang?if_exists}
                    	</div>
                    <#else>
                    	<div class="orgUnitColumn" title="${workEffort.parentRoleCode?if_exists} - ${workEffort.partyName?if_exists}">
                    		${workEffort.parentRoleCode?if_exists} - ${workEffort.partyName?if_exists}
                    	</div>                    
                    </#if>         
                </td>
                <#list statusItemList?if_exists as item>
                	<#assign keyTotal = workEffort.orgUnitId?if_exists + "_" + item.sequenceId?if_exists/>
                    <td class="center">
                        ${workEffort.get(keyTotal)?if_exists}
                    </td>
                </#list>
                <td class="center">${orgUnitTotal?if_exists}</td>          
            </tr>
            <#assign index = index+1>
        </#list>
        <tr>
        	<td>${uiLabelMap.CommonTotal}</td>
        	<#list statusItemList?if_exists as stItem>
        		<#assign keyStTotal = stItem.sequenceId?if_exists + "_" + stItem.statusDescr?if_exists?lower_case/>
        		<td class="center"><#if statusTotalsMap?has_content>${statusTotalsMap.get(keyStTotal)?if_exists}</#if></td>
            </#list>
            <td class="center">${totGeneral?if_exists}</td>       
        </tr>
    </tbody>
 </table>
