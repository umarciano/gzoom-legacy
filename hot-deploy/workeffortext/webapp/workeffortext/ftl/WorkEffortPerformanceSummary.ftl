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
        <tr>
        	<td>${uiLabelMap.CommonTotal}</td>
        	<#list statusItemList?if_exists as stItem>
        		<#assign keyStTotal = stItem.sequenceId?if_exists/>
        		<td class="center"><#if statusTotalsMap?has_content><#if statusTotalsMap.get(keyStTotal)?if_exists &gt; 0>${statusTotalsMap.get(keyStTotal)?if_exists}</#if></#if></td>
            </#list>
            <td class="center">${totGeneral?if_exists}</td>       
        </tr>    
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

                    <#if showUoCode?if_exists == "MAIN">
                        <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                            <#assign worEffortItem = workEffort.parentRoleCode?if_exists + " - " + workEffort.partyNameLang?if_exists/>
                        <#else>
                            <#assign worEffortItem = workEffort.parentRoleCode?if_exists + " - " + workEffort.partyName?if_exists/>
                        </#if>
                    <#elseif showUoCode?if_exists == "EXT">
                        <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                            <#assign worEffortItem = workEffort.externalId?if_exists + " - " + workEffort.partyNameLang?if_exists/>
                        <#else>
                            <#assign worEffortItem = workEffort.externalId?if_exists + " - " + workEffort.partyName?if_exists/>
                        </#if>
                    <#else>
                        <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                            <#assign worEffortItem = workEffort.partyNameLang?if_exists/>
                        <#else>
                            <#assign worEffortItem = workEffort.partyName?if_exists/>
                        </#if>                                        
                    </#if>
                    
                    <div class="orgUnitColumn" title="${worEffortItem?if_exists}">
                    	${worEffortItem?if_exists}
                    </div>       
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
    </tbody>
 </table>
