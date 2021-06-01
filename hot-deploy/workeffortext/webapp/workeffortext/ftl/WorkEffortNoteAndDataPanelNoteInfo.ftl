<div id="child-management-container-WorkEffortNoteAndDataPanelNoteInfo_${parameters.noteContentId}">
    <div>
    	<div class="management child-management" id="child-management-screenlet-container-WorkEffortNoteAndDataPanelNoteInfo_${parameters.noteContentId}">
	        <div class="screenlet" id="child-management-screenlet-body-WorkEffortNoteAndDataPanel_${parameters.noteContentId}">
	            <div class="screenlet-title-bar">
	               <ul>
	               	  <li class="h3">${uiLabelMap.BaseDetail}</li>
	               	  <li title="${uiLabelMap.CommonSave}" class="save search-save portlet-menu-item fa" style="float: right !important"><a href="#"></a></li> 
	               </ul>	                 
	               <br class="clear"> 
	            </div>
	            <div class="screenlet-body">
		            <div class="management child-management">
					   <#if parameters.noteId?has_content>
					   ${screens.render(managementFormScreenLocation, managementFormScreenName, Static["org.ofbiz.base.util.UtilMisc"].toMap("managementFormName", managementFormName, "managementFormLocation", managementFormLocation))}
					   </#if>
					</div>
			    </div>
        	</div>
    	</div>
    </div>
</div>
