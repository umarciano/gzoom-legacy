    <script type="text/javascript">
        var selectors = ["div.mblShowDescription", "div.mblShowAccountDescription", "div.mblShowComBud", "div.mblShowComAct"];
        populateTooltip(selectors);
        
        <#if !weChildren?has_content>
            var jar = new CookieJar({path : "/"});
            jar.put('activeLinkWorkEffortAchieveExtTabMenu', 'management_0');
            
            var tabInstance = Control.Tabs.instances.find(function(instance) {
                return instance.id == "WorkEffortAchieveExtTabMenu";
            });
            if(tabInstance) {
                tabInstance.registerEvent(tabInstance.getActiveContainer().identify(), function() {
                    var jar = new CookieJar({path : "/"});
                    var oldCookie = jar.get("activeLinkWorkEffortAchieveExtTabMenu");
                    if(oldCookie) {
                        jar.remove("activeLinkWorkEffortAchieveExtTabMenu");
                    }
                    jar.put("activeLinkWorkEffortAchieveExtTabMenu", "management_0");
                }, "click", null);
            }
        </#if>
    </script>
	
	<#macro queryEmoticon id>
		<#if id?has_content>
			<img class="mblDisplayIconCell" src="/content/control/stream?contentId=${id}"/> 
		<#else>
			<div class="mblDisplayIconCell"></div>
		</#if>
	</#macro>

    <#function valBudValues item>
        <#assign retVal="">	

		<#if item.detailEnumId?default("")=="ACCDET_SUM" && item.detailLayout?default(false)==true>
			<#assign retVal=item.totalReferenceDateTarget?default(retVal)>
			<#if retVal?has_content>
                <#if item.uomTypeId?default("")=="DATE_MEASURE">
                    <#assign retVal = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(retVal, locale)?default(retVal)>
                <#else>
				    <#assign retVal = retVal?default(retVal)?string("#,##0.########")>
				</#if>
			</#if> 
		<#else>                     
            <#if item.uomTypeId?default("")=="RATING_SCALE">
                <#assign retVal=item.codBud?default(retVal)>
            <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                <#if item.valBud?has_content>
                	<#assign retVal = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valBud, locale)?default(retVal)>
                </#if>
            <#else>                    	
                <#if item.valBud?has_content>
                	<#assign retVal = item.valBud?default(retVal)>
                </#if>
            </#if>
		</#if>
		
	    <#return retVal>
    </#function>

    <#function valActValues item>
        <#assign retVal2="">	

		<#if item.detailEnumId?default("")=="ACCDET_SUM" && item.detailLayout?default(false)==true>
			<#assign retVal2=item.totalReferenceDateConsuntivo?default(retVal2)>
			<#if retVal2?has_content>
                <#if item.uomTypeId?default("")=="DATE_MEASURE">
                    <#assign retVal2 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(retVal2, locale)?default(retVal2)>
                <#else>
				    <#assign retVal2 = retVal2?default(retVal2)?string("#,##0.########")>
				</#if>
			</#if> 
		<#else>                                         
            <#if item.uomTypeId?default("")=="RATING_SCALE">
                <#assign retVal2=item.codAct?default(retVal2)>
            <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                <#if item.valAct?has_content>
                	<#assign retVal2 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valAct, locale)?default(retVal2)>
                </#if>
            <#else>
                <#if item.valAct?has_content>
                	<#assign retVal2 = item.valAct?default(retVal2)>
                </#if>
            </#if>
		</#if>
		
	    <#return retVal2>
    </#function>
		
	<table class="basic-table list-table padded-row-table hover-bar resizable draggable toggleable selectable customizable headerFixable dblclick-open-management" cellspacing="0" id="InqMeasureTable">
        <thead>
            <tr class="header-row-2">
                <th>${uiLabelMap.WorkEffortAchieveInqMeasViewMeasDesc}</th>
                <th>${uiLabelMap.WorkEffortIndicatorObjectFormula}</th>
                <th>${uiLabelMap.WorkEffortPurposeMeasure_workEffortMeasureId}</th>
                <th>${uiLabelMap.WorkEffortAchieveInqMeasViewEspected}</th>
                <th>${uiLabelMap.WorkEffortAchieveInqMeasViewActual}</th>
                <th>${uiLabelMap.WorkEffortAchieveViewPerformance}</th>
            </tr>
        </thead>
        <tbody>
            <#assign index=0/>
            <#list workEffortAchieveInqMeasList as item><!-- la list workEffortAchieveInqMeasList é generata dal groovy createInqMeasImages.groovy -->
            	         	
            	<#assign valBud=valBudValues(item)>
                <#assign valAct=valActValues(item)>                    
                    
                <tr <#if index%2 != 0>class="alternate-row"</#if>>
                    <td>
                    	<!-- sandro: bug 3937 - modificata la generazione della descrizione -->
                    	<!--
                    	<#if !item.detailEnumId?has_content || item.detailEnumId!="ACCDET_NULL">
                            <#assign description=StringUtil.wrapString(item.accountName?default("")) + " - " + StringUtil.wrapString(item.uomDescr?default(""))>
                        <#elseif item.inputEnumId?has_content && item.inputEnumId=="ACCINP_OBJ">
                            <#assign description=StringUtil.wrapString(item.uomDescr?default(""))>
                        <#else>
                            <#assign description=StringUtil.wrapString(item.accountName?default(""))>
                        </#if>
                        -->
                        <#assign description = StringUtil.wrapString(item.measureProductUomDescr?default(item.accountName))>
                        <#--<#assign description = StringUtil.wrapString(item.accountName?default(""))>-->
                        <#if item.uomDescr?has_content>
                            <#assign description = description + " - " + StringUtil.wrapString(item.uomDescr?default(""))>
                        </#if>

                        <#assign shortDescription = StringUtil.wrapString(description?default(""))>
                        <#if shortDescription?length &gt; 150>
                            <#assign shortDescription = StringUtil.wrapString(shortDescription?substring(0,150))+"...">
                        </#if>
                        <!-- end bug 3937 -->
                        
                        <div class="mblShowDescription" <#if description?has_content && description?length &gt; 150> description="${StringUtil.wrapString(description)}"</#if>>
                            <input type="hidden" name="userLoginId" value="${(userLogin.userLoginId)?if_exists}"/>
                            <input type="hidden" name="workEffortId" value="${item.workEffortId?if_exists}"/>
                            <input type="hidden" name="workEffortMeasureId" value="${item.workEffortMeasureId?if_exists}"/>
                            <span class="title_indicator" style="cursor: pointer; color: #307bff; font-weight: bold;">${StringUtil.wrapString(shortDescription?if_exists)}</span>
                        </div>
                    </td>
                    <td style="width: 290px;">
                        <div>
                            <#assign accountDescription = StringUtil.wrapString(item.accountDescription?default(""))>
                            <#if accountDescription?length &gt; 50>
                                <#assign accountDescription = StringUtil.wrapString(accountDescription?substring(0,50))+"...">
                            </#if>
                            <div class="mblDisplayCenteredCell mblShowAccountDescription" <#if item.accountDescription?has_content && item.accountDescription?length &gt; 50> description="${StringUtil.wrapString(item.accountDescription)}"</#if>>${StringUtil.wrapString(accountDescription?if_exists)}</div>
                        </div>
                    </td>
                    <td style="width: 80px;">
                    	<div>
							<div class="mblDisplayCenteredCell">${StringUtil.wrapString(item.abbreviation?if_exists)}</div>
                    	</div>
                    </td>
                    <td style="width: 90px;">
                        <div>
                            <div class="mblDisplayCenteredCell mblShowComBud" <#if item.comAct?has_content> description="${item.comBud}"</#if>>${valBud}</div>
                        </div>
                    </td>
                    <td style="width: 90px;">
                    	<div>
							<div class="mblDisplayCenteredCell mblShowComAct" <#if item.comAct?has_content> description="${item.comAct}"</#if>>${valAct}</div>
                    	</div>
                    </td>
                    <td  style="padding: 1px !important; width: 80px;">
                    	<div class="mblDisplayCenteredCell">
							<@queryEmoticon id=item.emoticonContentId?default("")/> 
                    	</div>
                    </td>
                    <#-- <td  style="padding: 1px !important;">
                    	<div class="mblDisplayCenteredCell">
							<@queryEmoticon id=item.trendContentId?default("")/>  
                    	</div>
                    </td> -->
                </tr>
                <#assign index = index+1>
            </#list>
        </tbody>
     </table>
