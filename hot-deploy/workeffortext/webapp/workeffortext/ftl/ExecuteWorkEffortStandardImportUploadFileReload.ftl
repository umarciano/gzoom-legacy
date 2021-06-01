<#assign resultListUploadFile = Static["com.mapsengineering.base.util.CommonUtil"].jsonResponseFromObject(parameters.resultListUploadFile?if_exists?default("")) />
<#assign resultList = Static["com.mapsengineering.base.util.CommonUtil"].jsonResponseFromObject(parameters.resultList?if_exists?default("")) />
<#assign resultETLList = Static["com.mapsengineering.base.util.CommonUtil"].jsonResponseFromObject(parameters.resultETLList?if_exists?default("")) />

<p id="resultListUploadFile">${resultListUploadFile?if_exists?default("")}</p>
<p id="resultList">${resultList?if_exists}</p>
<p id="resultETLList">${resultETLList?if_exists}</p>

<table style="width: 100%" id="workEffortStandardImportResult">
	
	<#if blockingErrorsUploadFile?if_exists?has_content && blockingErrorsUploadFile &gt; 0>
		<#assign jobLogIdList = "(" + Static["org.ofbiz.base.util.StringUtil"].join(jobLogList, ", ") + ")"/>
		<thead>
			<tr>
				<td class="center" colspan="3">
					${uiLabelMap.WorkEffortPreStandardImport}
				</td>
			</tr>
		</thead>
		<tbody>
			<#list mappaErrorList?if_exists as errorRow>
				<tr>
					<td colspan="3">
						${errorRow.message?if_exists}
					</td>
				</tr>
			</#list>
			<tr>
				<td class="center" colspan="3">
					${uiLabelMap.WorkEffortPreStandardImportErrorResult} ${jobLogIdList}
				</td>
			</tr>
		</tbody>
	<#elseif resultList?if_exists?has_content>
		<#assign jobLogIdList = "(" + Static["org.ofbiz.base.util.StringUtil"].join(jobLogList, ", ") + ")"/>
		
		<thead>
			<tr>
				<td class="center" colspan="3">
					${uiLabelMap.WorkEffortStandardImport}
				</td>
			</tr>
		</thead>
		<tbody>
			<#list workEffortTypeMap.entrySet() as workEffortType>
			    <#if workEffortType?has_content && workEffortType.getValue()?has_content>
				    <tr>
					    <td>
						    ${uiLabelMap.WorkEffortStandardImport_number} ${workEffortType.getValue().description?if_exists}:
					    </td>
					    <td class="center" colspan="2">
						    ${workEffortType.getValue().recordElaborated?if_exists}
					    </td>
				    </tr>
				</#if>
			</#list>
			<tr>
				<td>
					${uiLabelMap.StandardImport_blockingErrors}
				</td>
				<td class="center" colspan="2">
					${blockingErrors?if_exists}
				</td>
			</tr>
			<#if blockingErrors?if_exists?has_content && blockingErrors &gt; 0>
				<tr>
					<td class="center" colspan="3">
						${uiLabelMap.ErrorSummary}
					</td>
				</tr>
				<#list mappaErrorList?if_exists as errorRow>
					<tr>
						<td>
							${uiLabelMap.WorkeffortRoot}: ${errorRow.context?if_exists}
						</td>
						<td>
							${errorRow.message}
						</td>
						<td>
							${uiLabelMap.RifId}: ${errorRow.id}
						</td>
					</tr>
				</#list>
				<tr>
					<td class="center" colspan="3">
						${uiLabelMap.WorkEffortStandardImportErrorResult} ${jobLogIdList}
					</td>
				</tr>
			<#else>
				<tr>
					<td class="center" colspan="3">
						${uiLabelMap.WorkEffortStandardImportSuccessResult} ${jobLogIdList}
					</td>
				</tr>
			</#if>
		</tbody>
	<#elseif parameters._ERROR_MESSAGE_?has_content>
		<thead>
			<tr>
				<td class="center" colspan="3">
					${uiLabelMap.WorkEffortPreStandardImport}
				</td>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td colspan="3">
					${parameters._ERROR_MESSAGE_}
				</td>
			</tr>
			<tr>
				<td class="center" colspan="3">
					${uiLabelMap.WorkEffortPreStandardImportErrorResult}
				</td>
			</tr>
		</tbody>
	</#if>
</table>


