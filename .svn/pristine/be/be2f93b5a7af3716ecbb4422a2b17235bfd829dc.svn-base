<div id="child-management-container-${entityName}Panel">
    <div>
    	<div class="management child-management" id="child-management-screenlet-container-${entityName}Panel">
	        <div class="screenlet" id="child-management-screenlet-body-${entityName}Panel">
	            <div class="screenlet-title-bar">
	               <ul>
	               <li class="h3">${uiLabelMap.BaseDetail}</li>
                   <li title="${uiLabelMap.CommonSave}" class="save search-save portlet-menu-item fa" style="float: right !important"><a href="#"></a></li> 
	               </ul>	                 
	               <br class="clear"> 
	            </div>
	            <#if parameters.partyId?has_content>
	                <div class="screenlet-body">		            	            
				        ${screens.render(managementFormScreenLocation, managementFormScreenName, Static["org.ofbiz.base.util.UtilMisc"].toMap("managementFormName", managementFormName, "managementFormLocation", managementFormLocation))}				
			        </div>
			    </#if>
        	</div>
    	</div>
    </div>
</div>
