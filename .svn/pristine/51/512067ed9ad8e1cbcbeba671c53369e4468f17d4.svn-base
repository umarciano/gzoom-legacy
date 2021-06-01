	<script type="text/javascript">
        var selectors = ["div.mblShowDescription"];
        populateTooltip(selectors);
    </script>
    <#if childList?has_content && childViewMap?has_content>
        <#if !idFieldToCheck?has_content>
            <#assign idFieldToCheck = "workEffortId">
        </#if>
        <#assign showOrgUnitObj = "N">
        <#assign tmpShowOrgUnit = showOrgUnit> <#-- perche bsh override in context -->
        <#assign workEffortTypeContentList = delegator.findByAndCache("WorkEffortTypeContent",Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortTypeId", workEffortTypeId, "weTypeContentTypeId", "OBJ_AN_LAYOUT"))>
		<#if workEffortTypeContentList?has_content>
        	<#assign workEffortTypeContentObj = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(workEffortTypeContentList) />
        	<#if workEffortTypeContentObj?if_exists?has_content>
    			<#if workEffortTypeContentObj.params?if_exists?has_content>
    				<#assign showOrgUnitObj=Static["org.ofbiz.base.util.BshUtil"].eval(workEffortTypeContentObj.params, context)>
    			</#if>
        	</#if>
        </#if>
        <#assign showOrgUnit = tmpShowOrgUnit> <#-- perche bsh override in context -->
    <table class="basic-table list-table padded-row-table resizable toggleable customizable headerFixable" cellspacing="0">
        <thead>
            <tr class="header-row-2">
                <#assign cl = ""/>
                <#assign cl2 = ""/>
                <#if childDesc1List?has_content>
                	<#assign cl = (childDesc1List[0].etch)?if_exists>
                </#if>
                <#if childDesc2List?has_content>
                	<#assign cl2 = (childDesc2List[0].etch)?if_exists>
                </#if>
               
                <#if !cl?has_content>
                	<#assign cl = (childDesc1List[0].description)?if_exists>
                </#if>
               
                <#if !cl2?has_content>
                	<#assign cl2 = (childDesc2List[0].description)?if_exists>                	
                </#if>	
		                                 	
	            <th<#if showAssocWeight?default("N") =="Y"> colspan="2"</#if>>${cl?if_exists}</th>
	            <th<#if showAssocWeight?default("N") =="Y"> colspan="2"</#if>>${cl2?if_exists}</th>
                
            </tr>
        </thead>
        <tbody>
            <#assign rowIndex=0>
            <#list childList as cl>
            	    <#assign weId = cl.get(idFieldToCheck)!""/>
	                <#assign weList = childViewMap.get(weId)![{"":""}] />
	            	<#assign firstRightItem = true/>
                	<#list weList as gv>
	                <tr <#if rowIndex%2 != 0>class="alternate-row"</#if>>    
	                	<#if firstRightItem>
	                	<#assign firstRightItem = false>
    		            <td rowspan="${weList?size}">
            		        <div>
                    		    <div class="mblDisplaySingleCell mblShowDescription twoLevelLeftCol" <#if cl.description?has_content>description="${cl.description}"</#if> >
                    		    <#if "Y" == showOrgUnitObj?if_exists>
	                            	${cl.weOrgPartyDescr?if_exists}
	                            <#else>
	        						${cl.workEffortName?if_exists}
	        					</#if>
	        					</div>
                    		</div>
                		</td>
                		<#if showAssocWeight?default("N") =="Y">
                		<td rowspan="${weList?size}">
                		    <#if cl.assocWeight?has_content && cl.assocWeight != -1> 
                		    <div>
                                <div class="mblDisplayCenteredCell">${cl.assocWeight?string("#,##0")}</div>
                            </div>
                            </#if>
                		</td>
                		</#if>
                		</#if>
                		<#if cl2?has_content>
                		<td>
							<div>
                                <div class="mblDisplaySingleCell mblShowDescription" <#if gv.description?has_content>description="${gv.description}"</#if> >
                                <#if "Y" == showOrgUnit?if_exists>
	                            	${gv.weOrgPartyDescr?if_exists}
	                            <#else>
	        						${gv.workEffortName?if_exists}
	        					</#if>
                                </div>
                            </div>
        				</td>
        				<#if showAssocWeight?default("N") =="Y">
        				<td>
        				    <#if gv.assocWeight?has_content && gv.assocWeight != -1> 
                            <div>
                                <div class="mblDisplayCenteredCell">${gv.assocWeight?string("#,##0")}</div>
                            </div>
                            </#if>
                        </td>
                        </#if>
                        </#if>
	                </tr>
    			</#list>
    			<#assign rowIndex=rowIndex+1>       
            </#list>
        </tbody>
     </table>
    </#if>