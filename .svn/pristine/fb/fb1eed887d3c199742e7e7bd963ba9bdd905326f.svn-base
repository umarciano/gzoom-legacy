<#if showValuesPanel?has_content && showValuesPanel?default('N') == 'Y'>
<div id="WorkEffortTransactionStandardLayoutModelPortletContainer_${parameters.contentIdInd}" class="transactionPortlet">
</#if>
<div id="child-management-container-WorkEffortMeasureIndicatorModelPortlet_${parameters.contentIdInd?if_exists}">
    <div>
        <div class="management child-management" id="child-management-screenlet-container-WorkEffortMeasureIndicatorModelPortlet_${parameters.contentIdInd?if_exists}">
            <div id="child-management-screenlet-body-WorkEffortMeasureIndicatorModelPortlet_${parameters.contentIdInd?if_exists}" class="screenlet">
                <div class="screenlet-title-bar">
                    <ul>
                        <li class="h3">
                     		<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                    			${portletTitleLang?if_exists}
                    		<#else>
                    			${portletTitle?if_exists}
                    		</#if>                        
                        </li>
                        <#if showValuesPanel?has_content && showValuesPanel?default('N') == 'Y'>
                            <li title="${uiLabelMap.CommonRemove}" class="hidden delete search-delete portlet-menu-item fa"><a href="#"></a></li>
                        <#else>
                            <li title="${uiLabelMap.CommonRemove}" class="delete search-delete portlet-menu-item fa"><a href="#"></a></li>
                        </#if>
                        <li title="${uiLabelMap.CommonSave}" class="save search-save portlet-menu-item fa"><a href="#"></a></li>
                        <li style="float: right !important" class="h3">
                      		<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                    			${glFiscalTypeDescriptionLang?if_exists}
                    		<#else>
                    			${glFiscalTypeDescription?if_exists}
                    		</#if>                        
                        </li>
                    </ul>
                    <br class="clear">
                </div>
                <div class="screenlet-body">
                    <div id="child-management-container-body-WorkEffortMeasureIndicatorModelPortlet_${parameters.contentIdInd?if_exists}">

                    ${screens.render(managementFormScreenLocation, managementFormScreenName, Static["org.ofbiz.base.util.UtilMisc"].toMap("managementFormName", managementFormName, "managementFormLocation", managementFormLocation))}
 
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<#if showValuesPanel?has_content && showValuesPanel?default('N') == 'Y'>
</div>
</#if>