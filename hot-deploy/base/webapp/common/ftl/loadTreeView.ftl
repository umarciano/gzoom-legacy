<!--

   TreeView building
   For fields descriptions and filling see:  loadTreeView.groovy

-->

<!-- TreeView container -->
<div class="treeview-container-tree">

    <#if (treeViewList?has_content)>

        <!-- sum grouplist -->
        <#assign colspan = 1/>
        <#list mappingList as mapping>
            <#assign colspan = colspan + (mapping.fieldGroupSize)?default(1)/>
        </#list>

        <!-- set default -->
        <#assign narrowClass = ""/>
        <#if narrow?exists>
            <#if narrow=="Y">
                <#assign narrowClass = "narrow"/>
            </#if>
        </#if>
        
        <!-- default: d&d abilitato -->
        <#assign dragAndDropDisableClass = ""/>
        <#if disableDragAndDrop?exists>
            <#if disableDragAndDrop=="Y">
                <#assign dragAndDropDisableClass = "drag-and-drop-disabled"/>
            </#if>
        </#if>

        <!--
            Build table for tree
         -->
        <table id="treeView_table_${entityName?if_exists}" class="treeview-outer-table ${dragAndDropDisableClass}">
            <!--
                Header
            -->
            <thead>
            	<tr>
                    <!-- search field -->
                    <th colspan="${colspan?if_exists}" class="treeview-header-search">
                        <span class="treeview-search">
                            <#--<label class="treeview-search-field-label">${uiLabelMap.BaseTreeViewSearchFilter}</label>-->
                            <#if entityName=="PartyRelationshipOrganizations" || entityName?starts_with("WorkEffort")>
	                            <#if entityName == "PartyRelationshipOrganizations">
								  <#assign clearSaveView = "Y"/>
								<#else>
								  <#assign clearSaveView = "N"/>
								</#if>
	                            <!-- date filter -->
	                            <!-- Carico la data di ricerca  -->
	                            <#if parameters.searchDate?has_content>
	                            	<#assign searchDate= parameters.searchDate/>
	                            </#if>
	                            
	                            <form action="<@ofbizUrl>management</@ofbizUrl>">
	                               <#assign id=Static["com.mapsengineering.base.util.FreemarkerWorker"].getFieldIdWithTimeStamp("searchDate_datePanel")>
								   <div class="datePanelCenter datePanel calendarSingleForm" id="${id}">
								   		<input type="hidden" class="dateParams" name="paramName" value="search_date_tree"/>
								   		<input type="hidden" class="dateParams" name="time" value="false"/>
								   		<input type="hidden" class="dateParams" name="shortDateInput" value="true"/>
								   		<input type="hidden" class="dateParams" name="dateTimeValue" value=""/>
								   		<input type="hidden" class="dateParams" name="localizedInputTitle" value="${uiLabelMap.CommonFormatDate}"/>
								   		<input type="hidden" class="dateParams" name="localizedIconTitle" value="${uiLabelMap.ShowedCommonFormatDate}"/>
								   		<input type="hidden" class="dateParams" name="yearRange" value=""/>
								   		<input type="hidden" class="dateParams" name="localizedValue" value="${searchDate?if_exists}"/>
								   		<input type="hidden" class="dateParams" name="size" value="15"/>
								   		<input type="hidden" class="dateParams" name="maxlength" value="10"/>
								   		<input type="hidden" class="dateParams" name="locale" value="${locale.getLanguage()}"/>
								   		<input type="hidden" class="dateParams" name="classNames" value=""/>	
			                       </div>
			                       
			                       <#if !(parameters.survey?exists) || !(parameters.survey?has_content) || (parameters.survey!="Y")>
			                       		<input type=button OnClick="SubmitHandler.postForm(this.form);" value="${uiLabelMap.BaseButtonSearch}">
			                       </#if>
			                       
			                       <!--devo inseerire i parametri per la form-->
			                       <input type="hidden" value="${entityName?if_exists}" name="entityName">
			                       <input type="hidden" value="${parameters.survey?if_exists}" name="survey">
			                       <input type="hidden" value="${loadTreeView?if_exists}" name="loadTreeView">
			                       <input type="hidden" value="${screenNameListIndex?if_exists}" name="screenNameListIndex">
			                       <input type="hidden" value="${parameters.workEffortIdRoot?if_exists}" name="workEffortId">			                       
			                       <input type="hidden" value="${rootInqyTree?if_exists}" name="rootInqyTree">
			                       <input type="hidden" value="${parameters.rootTree?if_exists}" name="rootTree">
			                       <input type="hidden" value="${parameters.snapshot?if_exists}" name="snapshot">
			                       <input type="hidden" value="${parameters.specialized?if_exists}" name="specialized">
			                       <input type="hidden" value="${treeViewEmptyDetail?if_exists}" name="treeViewEmptyDetail">
			                       <input type="hidden" value="${parameters.breadcrumbs?if_exists}".unescapeHTML()}" name="breadcrumbs">
			                       <input type="hidden" value="Y" name="fromTreeViewSearch"/>
			                       <#if workEffortRoot?has_content>
			                           <input type="hidden" value="${context.workEffortRoot.workEffortTypeId?if_exists}" name="workEffortTypeIdRoot">
			                       </#if>
			                       <input type="hidden" name="gpMenuEnumId" value="${parameters.gpMenuEnumId?if_exists}">
			                       <input type="hidden" name="gpMenuOrgUnitRoleTypeId" value="${parameters.gpMenuOrgUnitRoleTypeId?if_exists}">
			                       <input type="hidden" name="withProcess" value="${parameters.withProcess?if_exists}">
			                       <input type="hidden" name="childStruct" value="${parameters.childStruct?if_exists}">
			                       <input type="hidden" name="definiton" value="${parameters.definiton?if_exists}">
			                       <input type="hidden" name="noLeftBar" value="${parameters.noLeftBar?if_exists?string}">
			                       <input type="hidden" name="noInfoToolbar" value="${parameters.noInfoToolbar?if_exists?string}">
			                       
			                       <input type="hidden" value="${clearSaveView}" name="clearSaveView">
			                       <input type="hidden" value="${externalLoginKey?if_exists}" name="externalLoginKey">
			                    </form>
		                       
	                        </#if>
                        </span>
                    </th>
                    
                </tr>
                <!-- tr>
                    <th colspan="${colspan?if_exists}" class="treeview-header-toolbar">
                    </th>
                </tr -->
                <tr>
                    <!-- Group labels -->

                    <!-- root -->
                    <th>&nbsp;</th>
                    <!-- Other column to display -->
                    <#list mappingList?if_exists as fieldGroup>
                        <th colspan="${(fieldGroup.fieldGroupSize)?if_exists}" class="treeview-other-column ${narrowClass}">${fieldGroup.label?if_exists}</th>
                    </#list>
                </tr>

                <tr>
                    <!-- Fields labels -->

                    <!-- root -->
                    <#list rootMap.fieldGroupList as group>
                        <th>${group.fieldGroupLabel?if_exists}</th>
                    </#list>
                    <!-- Other column to display -->
                    <#list mappingList?if_exists as mapping>
                        <#list mapping.fieldGroupList as group>
                            <th class="treeview-other-column ${narrowClass}">${group.fieldGroupLabel?if_exists}</th>
                        </#list>
                    </#list>
                </tr>
            </thead>

            <!--
                Body
            -->
            <tbody class="treeview-body">
                <!-- loop over node list -->
                <#list treeViewList as node>

                    <!-- looking for parent (see TreeWorker.java)-->
                    <#assign parentId = node["_PARENT_NODE_ID_"]?if_exists/>
                    <#if parentId?has_content>
                        <!-- build parent id -->
                        <#assign nodeClass = "child-of-" + parentId/>
                        <#if node.weTypeIconId?has_content>
                        	<#assign spanClass = "file"/>
                        <#else>
                        	<#assign spanClass = "file small-left-padding"/>
                        </#if>
                    <#else>
                        <#assign nodeClass = "parent"/>
                        <#if node.weTypeIconId?has_content>
                        	<#assign spanClass = "folder"/>
                        <#else>
                        	<#assign spanClass = "folder small-left-padding"/>
                        </#if>
                    </#if>

                    <!-- La classe dell'icona viene decisa dall'xml di definizione dell'albero nell'attributo 'style' -->
                    <#if (rootMap.iconClass)?has_content>
                    	<#assign spanClass = spanClass + " " + Static["org.ofbiz.base.util.string.FlexibleStringExpander"].expandString(rootMap.iconClass, node) />
                    </#if>

                    <!-- Add selected -->
                    <#if "Y" == node["_SELECTED_"]?if_exists>
                        <#assign nodeClass = nodeClass + " selected"/>
                    </#if>

                    <!-- here compose first column -->
                    <#assign root = ""/>
                    <#assign rootFieldName = ""/>
                    <#list (rootMap.fieldGroupList)?if_exists as fieldGroup>
                        <#if root?has_content>
                            <#assign root = root + " - "/>
                        </#if>
                        <#assign field = ""/>
                        <#list (fieldGroup.fieldList)?if_exists as fieldMap>
                            <#assign field = field + " " + node[fieldMap.fieldName]?if_exists/>
                            <#if rootFieldName?has_content><!-- Compose field name -->
                                <#assign rootFieldName = rootFieldName + "|"/>
                            </#if>
                            <#assign rootFieldName = rootFieldName + fieldMap.fieldName/>
                        </#list>
                        <#assign root = root + field/>
                    </#list>
                    
                    <#assign rootTooltip = root/>
                    <#if root?length &gt; 108>
                    	<#assign root = root?substring(0, 105)/>
                    	<#assign root = root + "..."/>
                    </#if>                  

                    <!-- here compose unique id with keys of entity -->
                    <#assign keys = ""/>
                    <#list keyFieldsList?if_exists as keyField>
                        <#assign idx = keyField?index_of(";")>
                        <#if idx &lt; 0>
                            <#assign keyFieldNode = keyField>
                            <#assign keyFieldDetail = keyField>
                        <#else>
                            <#assign keyFieldNode = keyField?substring(0, idx)>
                            <#assign keyFieldDetail = keyField?substring(idx + 1)>
                        </#if>
                        <#if keys?has_content>
                            <#assign keys = keys + ","/>
                        </#if>
                        <#assign keys =  keys + keyFieldDetail + "|" + node[keyFieldNode]?if_exists/>
                    </#list>
                    
                    <#assign toggleClass = ""/>
                    <!-- compongo attributo otherFields per il tag TR -->
                    <#assign others = ""/>
                    <#list otherFieldsList?if_exists as otherField>
                        <#if others?has_content>
                            <#assign others = others + ","/>
                        </#if>
                        <#assign others =  others + otherField + "|" + node[otherField]?if_exists/>
                        <#if otherField == "initiallyCollapsed" >
                            <#assign valore = node[otherField]?if_exists >
                            
                            <#if valore == "Y" >
                                <#assign toggleClass = "initially-collapsed"/>
                            </#if>
                         </#if>
                    </#list>
                    
                    <!-- for _node_id_ see TreeWorker.java -->
                    <tr id="${node['_NODE_ID_']?if_exists}" class="${nodeClass} ${toggleClass}" keys="${keys?if_exists}"  others="${others?if_exists}" <#if "Y"==node["_NOT_MANAGED_"]?if_exists>not_managed="Y"</#if> defaultValueFields="${defaultValueFields?if_exists}">
                        <!--
                            Main column (inizialmente sett maxwidth)
                         -->
                        <#if node.weTypeIconId?has_content>
                            <td class="first-col maxwidth" title="<#if entityName="WorkEffortView">${node['wrToCode']?if_exists} - </#if>${rootTooltip?if_exists}"><span field-name="${rootFieldName}" <#if node.weTypeIconId?has_content>style="background-image: url(<@ofbizUrl>img?imgId=${node.weTypeIconId?if_exists}</@ofbizUrl>);" </#if> class="${spanClass}">${root}</span></td>
                        <#else>
                            <td class="first-col maxwidth" title="<#if entityName="WorkEffortView">${node['wrToCode']?if_exists} - </#if>${rootTooltip?if_exists}"><span field-name="${rootFieldName}" <#if node.weTypeIconId?has_content>style="background-image: url(<@ofbizUrl>img?imgId=${node.weTypeIconId?if_exists}</@ofbizUrl>);" </#if> class="${spanClass}"><i class="far fa-folder"></i>${root}</span></td>                        
                        </#if>
                        <!--
                             Other column to display
                          -->
                        <#list mappingList?if_exists as mapping>
                            <#list mapping.fieldGroupList as group>
                                <td class="treeview-other-column ${narrowClass}">
                                    <#assign value = ""/>
                                    <#assign columnFieldName = ""/>
                                    <#assign fieldType = "string"/>
                                    <#list (group.fieldList)?if_exists as fieldMap>
                                        <#if value?has_content>
                                            <#assign value = value + "&nbsp;"/>
                                        </#if>
                                        <#assign value = value + node[fieldMap.fieldName]?if_exists/>
                                        <#if columnFieldName?has_content><!-- Compose field name -->
                                            <#assign columnFieldName = columnFieldName + "|"/>
                                        </#if>
                                        <#assign columnFieldName = columnFieldName + fieldMap.fieldName/>
                                        <#if fieldMap.fieldType?has_content><!-- Check field type (ovviamente se ho + campi i tipi devono essere coerenti) -->
                                            <#assign fieldType = fieldMap.fieldType/>
                                        </#if>
                                    </#list>
                                    <span field-name="${columnFieldName}">
                                        <!-- render field type (per adesso solo stringa(default) e image) -->
                                        <#if "string" == fieldType>
                                            ${value}
                                        <#elseif "date" == fieldType && value?has_content>
                                            ${value?datetime?string.short}
                                        <#elseif "image" == fieldType && value?has_content>
                                            <img src="/content/control/stream?contentId=${value}">
                                        </#if>
                                    </span>
                                </td>
                            </#list>
                        </#list>

                        <!--
                            form for submit
                         -->
                        <td style="display: none">
                            <span>
                                <#assign managementUrl = Static["org.ofbiz.webapp.control.RequestHandler"].makeUrl(request, response, context.managementUrl?default("managementContainerOnly")) />
                                <#assign ddUrl = Static["org.ofbiz.webapp.control.RequestHandler"].makeUrl(request, response, "elaborateFormForUpdateAjax") />
                                <#assign reloadTreeviewUrl = Static["org.ofbiz.webapp.control.RequestHandler"].makeUrl(request, response, "treeviewContainerOnly") />

                                <form action="${managementUrl}" dragdrop_action="${ddUrl}" reloadtree_action="${reloadTreeviewUrl}" class="treeview-submit-form" areaId="treeview-detail-screen">
                                <#-- form action="${url}" class="treeview-submit-form" areaId="main-container" -->
                                	<#assign nodeEntityName = node["entityName"]?default(entityName)>
                                    <input type="hidden" name="entityName" value="${nodeEntityName?if_exists}"/>
                                    <input type="hidden" name="managementFormLocation" value="${managementFormLocation?if_exists}"/>
                                    <#if (parameters.managementFormName)?has_content>
                                        <input type="hidden" name="managementFormName" value="${parameters.managementFormName}"/>
                                    </#if>
                                    <input type="hidden" value="${externalLoginKey?if_exists}" name="externalLoginKey">
                                    <input type="hidden" name="parentRelKeyFields" value="${parentRelKeyFields?if_exists}"/>
                                    <input type="hidden" name="keyFields" value="${keyFields?if_exists}"/>
                                    <input type="hidden" name="otherFields" value="${otherFields?if_exists}"/>
                                    <input type="hidden" name="rootValues" value="${rootValues?if_exists}"/>
                                    <input type="hidden" name="treeViewLocation" value="${treeViewLocation?if_exists}"/>
                                    <input type="hidden" name="treeViewName" value="${treeViewName?if_exists}"/>
                                    <input type="hidden" name="treeViewEntityName" value="${treeViewEntityName?if_exists}"/>
                                    <input type="hidden" name="treeChangesSaveMethods" value="${treeChangesSaveMethods?if_exists}"/>
                                    <input type="hidden" name="orderByFields" value="${orderByFields?if_exists}"/>
                                    <input type="hidden" name="uniqueRootNotManaged" value="${uniqueRootNotManaged?if_exists}"/>
                                    <input type="hidden" name="forceUniqueRoot" value="${forceUniqueRoot?if_exists}"/>
                                    <input type="hidden" name="constFields" value="${constFields?if_exists}"/>
                                    <input type="hidden" name="searchFromManagement" value="${searchFromManagement?if_exists}"/>
                                    <input type="hidden" name="customFindScriptLocation" value="${customFindScriptLocation?if_exists}"/>
                                    <input type="hidden" name="saveView" value="N"/>
                                    <input type="hidden" name="managementMenuEnabled" value="N"/>
                                    <input type="hidden" name="displayScreenlet" value="N"/>
                                    <input type="hidden" name="useCache" value="N"/>
                                    <input type="hidden" name="survey" value="${parameters.survey?if_exists}">                                    
                                    <!-- input type="hidden" name="loadTreeView" value="Y"/ -->

                                    <!-- Insert all keys of entity -->
                                    <#list keyFieldsList?if_exists as keyField>
                                        <#assign idx = keyField?index_of(";")>
                                        <#if idx &lt; 0>
                                            <#assign keyFieldNode = keyField>
                                            <#assign keyFieldDetail = keyField>
                                        <#else>
                                            <#assign keyFieldNode = keyField?substring(0, idx)>
                                            <#assign keyFieldDetail = keyField?substring(idx + 1)>
                                        </#if>
                                        <input type="hidden" name="${keyFieldDetail}" value="${node[keyFieldNode]?if_exists}"/>
                                    </#list>

                                    <!-- then other fields -->
                                    <#list otherFieldsList?if_exists as otherField>
                                        <input type="hidden" name="${otherField}" value="${node[otherField]?if_exists}"/>
                                    </#list>

                                    <!-- Const fields (see loadTreeView.groovy) -->
                                    <#if constValueMap?has_content>
                                        <#list constValueMap.entrySet() as constItem>
                                            <input type="hidden" name="${constItem.getKey()}" value="${constItem.getValue()}"/>
                                        </#list>
                                    </#if>

                                    <#if extraValueMap?has_content>
                                        <#list extraValueMap.entrySet() as extraItem>
                                            <input type="hidden" name="${extraItem.getKey()}" value="${node[extraItem.getValue()]?if_exists}"/>
                                        </#list>
                                    </#if>

                                </form>
                            </span>
                        </td>
                    </tr>

                </#list>
            </tbody>
            </div>
        </table>

    </#if>

</div>









