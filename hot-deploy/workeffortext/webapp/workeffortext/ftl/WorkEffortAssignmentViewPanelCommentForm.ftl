			        <form method="post" action="<@ofbizUrl>elaborateFormForUpdateAjax</@ofbizUrl>" id="managementForm" class="basic-form cachable" onsubmit="javascript:ajaxSubmitFormUpdateAreas('managementForm','',''); return false;" name="WorkEffortAssignmentViewManagementFormPanel">
			        	<input type="hidden" name="entityName" value="WorkEffortPartyAssignment" class="ignore_check_modification mandatory encode_output">
						<input type="hidden" name="operation" value="UPDATE" class="submit-field encode_output">
						<input type="hidden" name="entityPkFields" value="${parameters.entityPkFields?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="noInfoToolbar" value="${parameters.noInfoToolbar?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="noMasthead" value="${parameters.noMasthead?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="MainColumnStyle" value="${parameters.MainColumnStyle?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="noLeftBar" value="${parameters.noLeftBar?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="folderIndex" value="${parameters.folderIndex?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="oldFolderIndex" value="${parameters.oldFolderIndex?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="backAreaId" value="${parameters.backAreaId?if_exists}" class="ignore_check_modification encode_output"> <#-- value="WorkEffortAssignmentAssignmentView_PanelPortletContainer" -->
						<input type="hidden" name="forcedBackAreaId" value="${parameters.forcedBackAreaId?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="successCode" value="${parameters.successCode?if_exists}" class="encode_output">
						<input type="hidden" name="operationalEntityName" value="${parameters.operationalEntityName?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="saveView" value="N" value="${parameters.saveView?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="contextManagement" value="${parameters.contextManagement?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="subFolder" value="${parameters.subFolder?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="ignoreToolbar" value="Y" class="ignoreToolbar ignore_check_modification encode_output">
						<input type="hidden" name="workEffortId" value="${parameters.workEffortId?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="partyId" value="${parameters.partyId?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="roleTypeId" value="${parameters.roleTypeId?if_exists}" class="ignore_check_modification encode_output">
						<input type="hidden" name="fromDate" value="${parameters.fromDate?if_exists}" class="ignore_check_modification alert encode_output">
						<div class="fieldgroup" ><div class="fieldgroup-title-bar" >
							<table >
								<tbody>
									<tr>
										<td class="collapse"></td><td></td>
									</tr>
								</tbody>
							</table>
						</div>
						<div class="fieldgroup-body"> 
							<table class="single-editable" cellspacing="0" cellpadding="0">
  								<tbody>
  									<tr style="display: none;">
										<td class="label">&nbsp;</td>
										<td colspan="7" class="widget-area-style"><input type="submit" class="management-reset-button ignore_check_modification encode_output" name="resetButton" value="Reset Button"></td>
									</tr>
									<tr style="display: none;">
									    <td class="label">&nbsp;</td>
									    <td colspan="7" class="widget-area-style"><input type="submit" class="save-button ignore_check_modification encode_output" name="submitButton" value="Invia"></td>
									</tr>
									<tr style="display: none;">
									    <td class="label">&nbsp;</td>
									    <td colspan="7" class="widget-area-style"><input type="submit" class="management-delete-button ignore_check_modification encode_output" name="deleteButton" value="Cancella"></td>
									</tr>
									<tr>
   										<td class="">
   											<table cellspacing="0" cellpadding="0">
   												<tbody>
   													<tr>
   														<#if multiTypeLang?has_content && multiTypeLang?default('NONE') == 'NONE'>
   															<td class="label">
   																<#if commentTitle?has_content>
																	${commentTitle}
																<#else>
																	&nbsp;
																</#if>
															</td>
															<td class="widget-area-style">
				   												<textarea class="encode_output" name="comments" cols="60" rows="4" id="WorkEffortAssignmentViewManagementFormPanel_comments" <#if commentsReadOnly> readonly="readonly"</#if>>${comments?if_exists}</textarea>
				   											</td>
														<#else>
	   														<td class="">
   																<table cellspacing='0' cellpadding='0'>
	   																<tr>
	   																	<td class='base-align-left'>
																			<#if commentTitle?has_content>
																				${commentTitle}
																			<#else>
																				&nbsp;
																			</#if>
																		</td>
																		<td class="base-align-right">&nbsp;&nbsp;<img src="${primaryLangFlagPath}" title="${primaryLangTooltip}"></td>
																	</tr>
																</table>
							                    			</td>
							                    			<td class="widget-area-style">
					   											<textarea class="encode_output <#if commentsRequired>mandatory</#if>" name="comments" cols="40" rows="4" id="WorkEffortAssignmentViewManagementFormPanel_comments" <#if commentsReadOnly> readonly="readonly"</#if>>${comments?if_exists}</textarea>
					   										</td>
						                    				<td class="label">
					   											<table cellspacing="0" cellpadding="0">
					   												<tbody>
					   													<tr>
					   														<td class="base-align-left">&nbsp;</td>
					   														<td class="base-align-right">&nbsp;&nbsp;<img src="${secondaryLangFlagPath}" title="${secondaryLangTooltip}"></td>
	   																	</tr>
					   												</tbody>
					   											</table>
					   										</td>
					   										<td class="widget-area-style">
			                    								<textarea class="encode_output <#if commentsRequired>mandatory</#if>" name="commentsLang" cols="40" rows="4" id="WorkEffortAssignmentViewManagementFormPanel_commentsLang" <#if commentsReadOnly> readonly="readonly"</#if>>${commentsLang?if_exists}</textarea>
			                    							</td>
				                    					</#if>
				                    					<#if weTypeSubWeId?has_content>
				                    						<#assign weList = delegator.findByAndCache("WorkEffort",Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortId", workEffortId))>
												            <#list weList as we>
											                    <#assign estimatedStartDate = we.estimatedStartDate />
											                    <#assign estimatedCompletionDate = we.estimatedCompletionDate />
											                    <#assign orgUnitId = we.orgUnitId />
												            </#list>
				                    						<td>
			                    								<table cellspacing="0" cellpadding="0">
					   												<tbody>
					   													<tr>
					   														<#assign etch1 = "" />
																            <#assign workEffortName1 = "" />
																            <#assign endWorkEffortIdWeValue1 = "" />
																            <#if endWorkEffortIdWe1?has_content>
																		        <#assign weList1 = delegator.findByAndCache("WorkEffort",Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortId", endWorkEffortIdWe1))>
																	            <#list weList1 as we1>
																                    <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
																                        <#assign workEffortName1 = we1.workEffortNameLang />
																                    <#else>
																                        <#assign workEffortName1 = we1.workEffortName />
																                    </#if>
																                    <#assign etch1 = we1.etch?default('') />
																                    <#assign endWorkEffortIdWeValue1 = etch1 + " - " + workEffortName1 />
																	            </#list>
																	        </#if>
			   																<td rowspan="3" class="label">${uiLabelMap.WorkEfforts}</td>
			   																<td class="widget-area-style">
			   																	<div  class="droplist_field" id="WorkEffortAssignmentViewManagementFormPanel_endWorkEffortIdWe1">
																		       	    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>, sourceReferenceId, workEffortRevisionId, workEffortRevisionDescr]]"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>, sourceReferenceId]]"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortTypeId| like| ${parameters.weTypeSubWeId}%]! [estimatedStartDate| less-equals| ${estimatedCompletionDate}]! [estimatedCompletionDate| greater-equals| ${estimatedStartDate}]! [workEffortSnapshotId| equals| [null-field]]! [organizationId | equals| ${defaultOrganizationPartyId?if_exists}]! [weActivation | not-equal| ACTSTATUS_REPLACED]! [orgUnitId| equals| ${orgUnitId}]]]"/>  
																				    <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>    
																				    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortId"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>"/>
																				    <div class="droplist_container">
																					    <input type="hidden" name="endWorkEffortIdWe1" value="${endWorkEffortIdWe1?if_exists}" class="droplist_code_field encode_output">
																					    <input <#if commentsReadOnly >readonly="readonly"</#if> type="text"id="WorkEffortAssignmentViewManagementFormPanel_endWorkEffortIdWe1_edit_field" name="workEffortName_endWorkEffortIdWe1" size="45" maxlength="255" value="${endWorkEffortIdWeValue1}"  class="droplist_edit_field"/>
																					    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
																					</div>
																				</div>
			   																</td>
			   															</tr>	
		   																<tr>
		   																	<#assign etch2 = "" />
																            <#assign workEffortName2 = "" />
																            <#assign endWorkEffortIdWeValue2 = "" />
																            <#if endWorkEffortIdWe2?has_content>
																		        <#assign weList2 = delegator.findByAndCache("WorkEffort",Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortId", endWorkEffortIdWe2))>
																	            <#list weList2 as we2>
																                    <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
																                        <#assign workEffortName2 = we2.workEffortNameLang />
																                    <#else>
																                        <#assign workEffortName2 = we2.workEffortName />
																                    </#if>
																                    <#assign etch2 = we2.etch?default('') />
																                    <#assign endWorkEffortIdWeValue2 = etch2 + " - " + workEffortName2 />
																	            </#list>
																	        </#if>
			   																<td class="widget-area-style">
			   																	<div  class="droplist_field" id="WorkEffortAssignmentViewManagementFormPanel_endWorkEffortIdWe2">
																		       	    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>, sourceReferenceId, workEffortRevisionId, workEffortRevisionDescr]]"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>, sourceReferenceId]]"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortTypeId| like| ${parameters.weTypeSubWeId}%]! [estimatedStartDate| less-equals| ${estimatedCompletionDate}]! [estimatedCompletionDate| greater-equals| ${estimatedStartDate}]! [workEffortSnapshotId| equals| [null-field]]! [organizationId | equals| ${defaultOrganizationPartyId?if_exists}]! [weActivation | not-equal| ACTSTATUS_REPLACED]! [orgUnitId| equals| ${orgUnitId}]]]"/>  
																				    <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>    
																				    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortId"/>
																				    <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>"/>
																				    <div class="droplist_container">
																					    <input type="hidden" name="endWorkEffortIdWe2" value="${endWorkEffortIdWe2?if_exists}" class="droplist_code_field encode_output">
																						<input <#if commentsReadOnly >readonly="readonly"</#if> type="text"id="WorkEffortAssignmentViewManagementFormPanel_endWorkEffortIdWe2_edit_field" name="workEffortName_endWorkEffortIdWe2" size="45" maxlength="255" value="${endWorkEffortIdWeValue2}"  class="droplist_edit_field"/>
																					    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
																					</div>
																				</div>
			   																</td>
		  																</tr>
		  																<tr>
		   																	<#assign etch3 = "" />
																            <#assign workEffortName3 = "" />
																            <#assign endWorkEffortIdWeValue3 = "" />
																            <#if endWorkEffortIdWe3?has_content>
																	            <#assign weList3 = delegator.findByAndCache("WorkEffort",Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortId", endWorkEffortIdWe3))>
																	            <#list weList3 as we3>
																                    <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
																                        <#assign workEffortName3 = we3.workEffortNameLang />
																                    <#else>
																                        <#assign workEffortName3 = we3.workEffortName />
																                    </#if>
																                    <#assign etch3 = we3.etch?default('') />
																                    <#assign endWorkEffortIdWeValue3 = etch3 + " - " + workEffortName3 />
																	            </#list>
																            </#if>
		   																<td class="widget-area-style">
		   																	<div class="droplist_field" id="WorkEffortAssignmentViewManagementFormPanel_endWorkEffortIdWe3">
																	       	    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
																			    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/>
																			    <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
																			    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>, sourceReferenceId, workEffortRevisionId, workEffortRevisionDescr]]"/>
																			    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
																			    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>, sourceReferenceId]]"/>
																			    <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortTypeId| like| ${parameters.weTypeSubWeId}%]! [estimatedStartDate| less-equals| ${estimatedCompletionDate}]! [estimatedCompletionDate| greater-equals| ${estimatedStartDate}]! [workEffortSnapshotId| equals| [null-field]]! [organizationId | equals| ${defaultOrganizationPartyId?if_exists}]! [weActivation | not-equal| ACTSTATUS_REPLACED]! [orgUnitId| equals| ${orgUnitId}]]]"/>  
																				<input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>    
																			    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortId"/>
																			    <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>"/>
																			    <div class="droplist_container">
																				    <input type="hidden" name="endWorkEffortIdWe3" value="${endWorkEffortIdWe3?if_exists}" class="droplist_code_field encode_output">
																					<input <#if commentsReadOnly >readonly="readonly"</#if> type="text"id="WorkEffortAssignmentViewManagementFormPanel_endWorkEffortIdWe3_edit_field" name="workEffortName_endWorkEffortIdWe3" size="45" maxlength="255" value="${endWorkEffortIdWeValue3}"  class="droplist_edit_field"/>
																				    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
																				</div>
																			</div>
		   																</td>
		  															</tr>
			  														</tbody>
					   											</table>
		                    								</td>
		                    							</#if>
   													</tr>
   												</tbody>
   											</table>
   										</td>
 </tbody></table>
</div></div></form>

