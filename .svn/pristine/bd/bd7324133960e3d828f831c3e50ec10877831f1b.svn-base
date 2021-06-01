	<!--
	Lista risultati
	-->
	
	<input id="ShowLogButton" type="button" value="${uiLabelMap.JobLog_showLog}" class="smallButton" style="cursor: pointer;" onClick="$('jobLogDiv_${parameters.contentId?if_exists}').removeClassName('hidden')">
	<div id="jobLogDiv_${parameters.contentId?if_exists}" class="hidden">
		<#if jobLogId?exists && jobLogId!="">		
			<div>
				<table>
					<tr>
						<td><span class="label">${uiLabelMap.JobLog_jobLogId?if_exists}</span></td><td><span class="cella">${jobLogId}</span></td>
					</tr>
				</table>
			</div>
		</#if>
		<div>
		<table class="basic-table list-table padded-row-table hover-bar resizable draggable toggleable customizable headerFixable" cellspacing="0">
		    <thead>
		        <tr class="header-row-2">
		        	<th>${uiLabelMap.JobLog_LogType?if_exists}</th>
		        	<th>${uiLabelMap.JobLog_LogMessage?if_exists}</th>
		        	<th>${uiLabelMap.JobLog_LogCode?if_exists}</th>
		        </tr>
		    </thead>
		    <tbody>
		        <#assign index=0/>
		        <#list listIt as item>
		            <tr <#if index%2 != 0>class="alternate-row"</#if>>
		                <td>
		                    <input type="hidden" name="jobLogId" value="${item.jobLogId?if_exists}"/>
		                	<input type="hidden" name="jobLogLogId" value="${item.jobLogLogId?if_exists}"/>
		                	<input type="hidden" name="userLoginId" value="${item.userLoginId?if_exists}"/>
		                	<input type="hidden" name="logTypeEnumId" value="${item.logTypeEnumId?if_exists}"/>
		                	<div>
		           				${uiLabelMap.get("Enumeration.description." + item.logTypeEnumId?if_exists)?default("")}
		                	</div>
		                </td>
		                <td>
		                	<div>
		           				${item.logMessage?if_exists}
		                	</div>
		                </td>
		                <td>
		                	<div>
		           				${item.logCode?if_exists}
		                	</div>
		                </td>
		            </tr>
		            <#assign index = index+1>
		        </#list>
		    </tbody>
		 </table>
		 
		</div>		 
	 </div>
