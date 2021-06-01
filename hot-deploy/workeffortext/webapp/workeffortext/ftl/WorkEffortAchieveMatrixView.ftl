    <script type="text/javascript">
        var selectors = ["div.mblShowDescription"];
        populateTooltip(selectors);
    </script>
	<table class="basic-table list-table padded-row-table resizable toggleable customizable headerFixable" cellspacing="0">
        <thead>
            <tr class="header-row-2">
            	<#list childList as cl><!-- per la list childList vedi screen WorkEffortAchieveMatrixManagementScreen -->
	                <th>${cl.workEffortName?if_exists}</th>
                </#list>
            </tr>
        </thead>
        <tbody>
        	<#if childMaxSize &gt; 0>
        		<#assign childMaxSize  = (childMaxSize - 1)/>
        		<#if !idFieldToCheck?has_content>
        		    <#assign idFieldToCheck = "workEffortId">
        		</#if>
	        	<#list 0..childMaxSize as idx><!-- per la variabile maxChildSize vedi groovy  -->
	                <tr <#-- <#if idx%2 != 0>class="alternate-row"</#if> -->>
	                	
	                	<#assign columnIndex=0>
	                	<!-- lista workEffortId, uno per ciascuna colonna -->
			        	<#list childList as cl>
			        		<#assign weId = cl.get(idFieldToCheck)!""/>
			        		
			        		<!-- Estraggo dalla mappa creata nel groovy la lista corrispondente -->
			        		<#assign weList = childViewMap.get(weId)!"" />
			        		<#if (weList?has_content && weList.size() >idx) >
			        			<#assign gv = weList.get(idx)!"" />
			        		<#else>
			        			<#assign gv = "" />
		       				</#if>
			        		
			        		<#if gv?has_content>
				                <td <#if columnIndex%2 != 0>class="alternate-row"</#if>>
				                	<div>
				                		<div class="mblDisplaySingleCell mblShowDescription" <#if gv.description?has_content>description="${gv.description}"</#if> >${gv.workEffortName?if_exists}</div>
				                	</div>
				                </td>
				            <#else>
				                <td <#if columnIndex%2 != 0>class="alternate-row"</#if>>
				                	<div>
				                		<div class="mblDisplaySingleCell"></div>
				                	</div>
				                </td>
			                </#if>
			                <#assign columnIndex=columnIndex+1>
			            </#list>
	                </tr>
        	    </#list>
        	    
			</#if>            
        </tbody>
     </table>
