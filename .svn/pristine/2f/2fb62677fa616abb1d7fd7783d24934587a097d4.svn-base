<#assign dateFormat = Static["org.ofbiz.base.util.UtilDateTime"].getDateFormat(locale)>
<table id="TymeEntryTable" class="basic-table list-table padded-row-table hover-bar resizable draggable toggleable selectable customizable headerFixable multi-editable" cellpadding="0" cellspacing="0">
	<thead>
    	<tr class="header-row-2">
    		<th>${uiLabelMap.Project}</th>
    		<th>${uiLabelMap.Task}</th>
    		<#if showParty?default("N")=="Y">
    			<th>${uiLabelMap.FormFieldTitle_TimeEntryPartyId}</th>
    		</#if> 		
    		<th>${uiLabelMap.FormFieldTitle_estimatedStartDate}</th>
    		<th>${uiLabelMap.FormFieldTitle_estimatedCompletionDate}</th>
    		<th>${uiLabelMap.FormFieldTitle_hours}</th>
     	</tr>
	</thead>
	<#if timeEntryList?has_content>
		<tbody style="height: auto;">
	    	<#assign index=0/>
	    	<#assign projectStr=""/>
	    	<#assign projectTaskStr=""/>
	    	<#assign index=0/>
            <#list timeEntryList as timeEntry>
                <#assign index=0/>
                <#assign projectTaskCurrentStr="${timeEntry.projectId?if_exists}_${timeEntry.taskId?if_exists}"/>
            
            	<tr <#if index%2 != 0>class="alternate-row"</#if>>
            	    <td><#if projectStr?if_exists != timeEntry.projectId?if_exists>${timeEntry.projectName?if_exists}</#if></td>
            	    <td><#if projectTaskStr?if_exists != projectTaskCurrentStr?if_exists>${timeEntry.taskName?if_exists}</#if></td>
            	    <#if showParty?default("N")=="Y">
            	    	<td>${timeEntry.partyName?if_exists}</td>
            	    </#if>
            	    <td><#if projectTaskStr?if_exists != projectTaskCurrentStr?if_exists><#if timeEntry.estimatedStartDate?has_content>${timeEntry.estimatedStartDate?string(dateFormat)}</#if></#if></td>
            	    <td><#if projectTaskStr?if_exists != projectTaskCurrentStr?if_exists><#if timeEntry.estimatedCompletionDate?has_content>${timeEntry.estimatedCompletionDate?string(dateFormat)}</#if></#if></td>
            	    <td class="numericInList">${timeEntry.hours?if_exists}</td>
            	</tr>
	    		<#assign projectStr=timeEntry.projectId?if_exists/>
	    		<#assign projectTaskStr="${timeEntry.projectId?if_exists}_${timeEntry.taskId?if_exists}"/>            	
            </#list>
        </tbody>
	</#if>
</table>
