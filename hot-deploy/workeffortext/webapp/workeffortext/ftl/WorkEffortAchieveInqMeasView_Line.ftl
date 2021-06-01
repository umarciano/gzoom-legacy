<#import "/base/webapp/common/ftl/gzoomMacro.ftl"  as gzoom/>

    <script type="text/javascript">
        var selectors = ["div.mblShowDescription", "div.showEmoticonValue", "div.mblShowAccountDescription", "div.mblShowComBud", "div.mblShowComActM4", "div.mblShowComActM3", "div.mblShowComActM2", "div.mblShowComActM1", "div.mblShowComAct", "div.mblShowComBudP0", "div.mblShowComBenP0"];
        populateTooltip(selectors);
        
        //solo il prima tab è nevigabile, quindi si torna sempre al primo
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
                	<#assign retVal = item.valBud?default(retVal)?string("#,##0.########")>
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
                	<#assign retVal2 = item.valAct?default(retVal2)?string("#,##0.########")>
                </#if>
            </#if>
		</#if>
		
	    <#return retVal2>
    </#function>
	
    <#function valActP0Values item>
        <#assign retVal3="">	

		<#if item.detailEnumId?default("")=="ACCDET_SUM" && item.detailLayout?default(false)==true>
			<#assign retVal3=item.totalYearRealConsuntivo?default(retVal3)>
			<#if retVal3?has_content>
                <#if item.uomTypeId?default("")=="DATE_MEASURE">
                    <#assign retVal3 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(retVal3, locale)?default(retVal3)>
                <#else>
				    <#assign retVal3 = retVal3?default(retVal3)?string("#,##0.########")>
				</#if>
			</#if> 
		<#else>                                         
            <#if item.uomTypeId?default("")=="RATING_SCALE">
                <#assign retVal3=item.codActP0?default(retVal3)>
            <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                <#if item.valActP0?has_content>
                	<#assign retVal3 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valActP0, locale)?default(retVal3)>
                </#if>
            <#else>
                <#if item.valActP0?has_content>
                	<#assign retVal3 = item.valActP0?default(retVal3)?string("#,##0.########")>
                </#if>
            </#if>
		</#if>
		
	    <#return retVal3>
    </#function>	
	
	<div class="header-breadcrumbs">
        <div id="header-breadcrumbs-th"><@gzoom.breadcrumb parameters.breadcrumbsCurrentItem?if_exists "common-container"/></div>
        <#assign activeTabIndex = null>
        <#if activeTabIndex?has_content>
            <#if !weChildren?has_content && (parameters.workEffortTypeId?has_content || parameters.weTypeId?has_content)>
                <#assign activeTabIndex = 1>
                <input type="hidden" id="activeTabIndex" name="activeTabIndex" value="management_${activeTabIndex}" />
            </#if>
        </#if>
    </div>

	<table class="basic-table list-table padded-row-table hover-bar resizable draggable toggleable selectable customizable headerFixable" cellspacing="0" id="InqMeasureTable">
        <thead>
            <tr class="header-row-2">
                <th rowspan="2">${uiLabelMap.FormFieldTitle_indicatorGlAccountId}</th>
                <th rowspan="2">${uiLabelMap.WorkEffortPurposeMeasure_workEffortMeasureId}</th>
                <th rowspan="2">${uiLabelMap.HeaderWorkEffortStatus}</th>
                <th rowspan="2">${uiLabelMap.WorkeffortTargetBalance}</th>
                <th rowspan="2">${uiLabelMap.WorkEffortAchieveInqMeasView_Line_Trend}</th>
                <th rowspan="2">${uiLabelMap.WorkEffortAchieveViewPerformance}</th>
            </tr>
        </thead>
        <tbody>
            <#assign index=0/>
            <#assign glResourceTypeId=null>
            <#list workEffortAchieveInqMeasList as item>
               
				<#assign valBud=valBudValues(item)>
                <#assign valAct=valActValues(item)>
                <#assign valActP0=valActP0Values(item)>  
                
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
                        <#if item.uomDescr?has_content>
                            <#assign description = description + " - " + StringUtil.wrapString(item.uomDescr?default(""))>
                        </#if>
                        
                        <#assign shortDescription = StringUtil.wrapString(description?default(""))>
                        <#if shortDescription?length &gt; 50>
                            <#assign shortDescription = StringUtil.wrapString(shortDescription?substring(0,50))+"...">
                        </#if>
                        
                        <!-- end bug 3937 -->
                        
                        <div <#if description?has_content && description?length &gt; 50>class="mblShowDescription"</#if> <#if description?has_content && description?length &gt; 50> description="${StringUtil.wrapString(description)}"</#if>>
                            <input type="hidden" name="userLoginId" value="${userLogin.userLoginId?if_exists}"/>
                            <input type="hidden" name="workEffortId" value="${item.workEffortId?if_exists}"/>
                            <input type="hidden" name="workEffortMeasureId" value="${item.workEffortMeasureId?if_exists}"/>
                            <span class="title_indicator" style="cursor: pointer; color: #307bff; font-weight: bold;" onclick="">${StringUtil.wrapString(shortDescription?if_exists)}</span>
                        </div>
                    </td>
                    <td style="width: 55px;">
                        <div>
                            <div class="mblDisplayCenteredCell">${StringUtil.wrapString(item.abbreviation?if_exists)}</div>
                        </div>
                    </td>
                    <td>
                    	<div>
                            <#if isMonitor=="Y">
                            <div class="mblDisplayCenteredCell<#if item.comAct?has_content> mblShowComAct</#if>" <#if item.comAct?has_content> description="${item.comAct}"</#if>><#if valAct?has_content>${valAct?string}</#if></div>
                            <#else>
                            <div class="mblDisplayCenteredCell<#if item.comActP0?has_content> mblShowComAct</#if>" <#if item.comActP0?has_content> description="${item.comActP0}"</#if>><#if valActP0?has_content>${valActP0?string}</#if></div>
                            </#if>
                        </div>
                    </td>
                    <td>
                    	<div>
            				<div class="mblDisplayCenteredCell<#if item.comBud?has_content> mblShowComBud</#if>" <#if item.comBud?has_content> description="${item.comBud}"</#if>><#if valBud?has_content>${valBud?string}</#if></div>
        				</div>
                    </td>
                    
                    <td style="padding: 1px !important; width: 200px;">
				        <div id="lineImageDiv">
			                <img src="${lineImagesSrc[item.workEffortMeasureId]?if_exists}" width="200" height="200"/>
				        </div>
				    </td>
                    
                    <td style="padding: 1px !important; width: 70px;">
                        <div class="mblDisplayCenteredCell<#if item.perfAmountCalc?has_content && (item.perfAmountCalc != 0.0 || item.emoticonContentId?has_content)> showEmoticonValue</#if>"<#if item.perfAmountCalc?has_content && (item.perfAmountCalc != 0.0 || item.emoticonContentId?has_content)> description="${StringUtil.wrapString(item.perfAmountCalc?string("#,##0.###"))}"</#if>>
                            <@queryEmoticon id=item.emoticonContentId?default("")/> 
                        </div>
                    </td>
                </tr>   
                <#assign index = index+1> 
            </#list>
        </tbody>
     </table>
