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
	
    <#function yearMValues item yearM fiscalType yearM1Real yearM2Real yearM3Real yearM4Real>
        <#assign retVal=""> 
        
		<#if item.detailEnumId?default("")=="ACCDET_SUM" && item.detailLayout?default(false)==true>
			<#if fiscalType=="TARGET">
				<#if yearM==1>
				    <#assign retVal=item.totalYearM1RealTarget?default(retVal)>
				</#if>
			    <#if yearM==2>
				    <#assign retVal=item.totalYearM2RealTarget?default(retVal)>
				</#if>
			    <#if yearM==3>
				    <#assign retVal=item.totalYearM3RealTarget?default(retVal)>
				</#if>
			    <#if yearM==4>
				    <#assign retVal=item.totalYearM4RealTarget?default(retVal)>
				</#if>
		    </#if>
			<#if fiscalType=="CONSUNTIVO">
				<#if yearM==1>
				    <#assign retVal=item.totalYearM1RealConsuntivo?default(retVal)>
				</#if>
			    <#if yearM==2>
				    <#assign retVal=item.totalYearM2RealConsuntivo?default(retVal)>
				</#if>
			    <#if yearM==3>
				    <#assign retVal=item.totalYearM3RealConsuntivo?default(retVal)>
				</#if>
			    <#if yearM==4>
				    <#assign retVal=item.totalYearM4RealConsuntivo?default(retVal)>
				</#if>				
			</#if>
			<#if retVal?has_content>
                <#if item.uomTypeId?default("")=="DATE_MEASURE">
                    <#assign retVal = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(retVal, locale)?default(retVal)>
                <#else>
				    <#assign retVal = retVal?default(retVal)?string("#,##0.########")>
				</#if>
			</#if> 
 
		<#else>
		    <#if yearM==1>
                <#if yearM1Real?has_content>                 
                    <#if item.uomTypeId?default("")=="RATING_SCALE">
                        <#assign retVal=item.codActM1?default(valActM1)>
                    <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    	<#if item.valActM1?has_content>
                    		<#assign retVal = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valActM1, locale)?default(retVal)>
                    	</#if>
                    <#else>                    	
                    	<#if item.valActM1?has_content>
                    		<#assign retVal = item.valActM1?default(retVal)?string("#,##0.########")>
                    	</#if>
                    </#if>
                </#if>			
			</#if>
			<#if yearM==2>
                <#if yearM2Real?has_content>                   
                    <#if item.uomTypeId?default("")=="RATING_SCALE">
                        <#assign retVal=item.codActM2?default(retVal)>
                    <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    	<#if item.valActM2?has_content>
                    		<#assign retVal = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valActM2, locale)?default(retVal)>
                    	</#if>
                    <#else>    
                    	<#if item.valActM2?has_content>
                    		<#assign retVal = item.valActM2?default(retVal)?string("#,##0.########")>
                    	</#if>
                    </#if>
                </#if>			
			</#if>
			<#if yearM==3>
                <#if yearM3Real?has_content>                    
                    <#if item.uomTypeId?default("")=="RATING_SCALE">
                        <#assign retVal=item.codActM3?default(retVal)>
                    <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    	<#if item.valActM3?has_content>
                    		<#assign retVal = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valActM3, locale)?default(retVal)>
                    	</#if>
                    <#else>                    	
                    	<#if item.valActM3?has_content>
                    		<#assign retVal = item.valActM3?default(retVal)?string("#,##0.########")>
                    	</#if>
                    </#if>
                </#if>			
			</#if>
			<#if yearM==4>
                <#if yearM4Real?has_content>                  
                    <#if item.uomTypeId?default("")=="RATING_SCALE">
                        <#assign retVal=item.codActM4?default(retVal)>
                    <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    	<#if item.valActM4?has_content>
                    		<#assign retVal = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valActM4, locale)?default(retVal)>
                    	</#if>
                    <#else>                    	
                    	<#if item.valActM4?has_content>
                    		<#assign retVal = item.valActM4?default(retVal)?string("#,##0.########")>
                    	</#if>
                    </#if>
                </#if>
		    </#if>
		
		</#if>
					
	    <#return retVal>
    </#function>

    <#function referenceDateBudActValues item fiscalType>
        <#assign retVal2=""> 

		<#if item.detailEnumId?default("")=="ACCDET_SUM" && item.detailLayout?default(false)==true>
			<#if fiscalType=="TARGET">
				<#assign retVal2=item.totalReferenceDateTarget?default(retVal2)>			
			</#if>
			<#if fiscalType=="CONSUNTIVO">
				<#assign retVal2=item.totalReferenceDateConsuntivo?default(retVal2)>			
			</#if>
			<#if retVal2?has_content>
                <#if item.uomTypeId?default("")=="DATE_MEASURE">
                    <#assign retVal2 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(retVal2, locale)?default(retVal2)>
                <#else>
				    <#assign retVal2 = retVal2?default(retVal2)?string("#,##0.########")>
				</#if>
			</#if> 
		<#else>
			<#if fiscalType=="TARGET">		                    
                <#if item.uomTypeId?default("")=="RATING_SCALE">
                    <#assign retVal2=item.codBud?default(retVal2)>
                <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    <#if item.valBud?has_content>
                    	<#assign retVal2 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valBud, locale)?default(retVal2)>
                    </#if>
                <#else>                    	
                    <#if item.valBud?has_content>
                        <#assign retVal2 = item.valBud?default(retVal2)?string("#,##0.########")>
                    </#if>
                </#if>
		    </#if>
			<#if fiscalType=="CONSUNTIVO">                   
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
		</#if>
		
	    <#return retVal2>
    </#function>
	
    <#function yearRealValues item fiscalType>
        <#assign retVal3=""> 	

		<#if item.detailEnumId?default("")=="ACCDET_SUM" && item.detailLayout?default(false)==true>
			<#if fiscalType=="TARGET">
				<#assign retVal3=item.totalYearRealTarget?default(retVal3)>			
			</#if>
			<#if fiscalType=="CONSUNTIVO">
				<#assign retVal3=item.totalYearRealConsuntivo?default(retVal3)>			
			</#if>
			<#if retVal3?has_content>
                <#if item.uomTypeId?default("")=="DATE_MEASURE">
                    <#assign retVal3 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(retVal3, locale)?default(retVal3)>
                <#else>
				    <#assign retVal3 = retVal3?default(retVal3)?string("#,##0.########")>
				</#if>
			</#if> 		
		<#else>
			<#if fiscalType=="TARGET">
                <#if item.uomTypeId?default("")=="RATING_SCALE">
                    <#assign retVal3=item.codBudP0?default(retVal3)>
                <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    <#if item.valBudP0?has_content>
                    	<#assign retVal3 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valBudP0, locale)?default(retVal3)>
                    </#if>
                <#else>                    	
                    <#if item.valBudP0?has_content>
                    	<#assign retVal3 = item.valBudP0?default(retVal3)?string("#,##0.########")>
                    </#if>
                </#if>
            </#if>			
			<#if fiscalType=="CONSUNTIVO">                   
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
		</#if>
		
	    <#return retVal3>
    </#function>		
		
    <#if parameters.workEffortAnalysisId?has_content>
        <#assign weAnalisis = delegator.findOne("WorkEffortAnalysis", Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortAnalysisId", parameters.workEffortAnalysisId), true)>
        <#if weAnalisis?has_content>
            <#assign colSpanReal = 0>
            <#if weAnalisis.yearM4Real?has_content>
                <#assign yearM4Real = Static["org.ofbiz.base.util.UtilDateTime"].getYear(weAnalisis.yearM4Real, timeZone, locale)>
                <#assign labelM4Real = weAnalisis.labelM4Real?default(yearM4Real)>
                <#assign colSpanReal = colSpanReal + 1>
            </#if>
            <#if weAnalisis.yearM3Real?has_content>
                <#assign yearM3Real = Static["org.ofbiz.base.util.UtilDateTime"].getYear(weAnalisis.yearM3Real, timeZone, locale)>
                <#assign labelM3Real = weAnalisis.labelM3Real?default(yearM3Real)>
                <#assign colSpanReal = colSpanReal + 1>
            </#if>
            <#if weAnalisis.yearM2Real?has_content>
                <#assign yearM2Real = Static["org.ofbiz.base.util.UtilDateTime"].getYear(weAnalisis.yearM2Real, timeZone, locale)>
                <#assign labelM2Real = weAnalisis.labelM2Real?default(yearM2Real)>
                <#assign colSpanReal = colSpanReal + 1>
            </#if>
            <#if weAnalisis.yearM1Real?has_content>
                <#assign yearM1Real = Static["org.ofbiz.base.util.UtilDateTime"].getYear(weAnalisis.yearM1Real, timeZone, locale)>
                <#assign labelM1Real = weAnalisis.labelM1Real?default(yearM1Real)>
                <#assign colSpanReal = colSpanReal + 1>
            </#if>
            <#if weAnalisis.yearReal?has_content>
                <#assign yearReal = Static["org.ofbiz.base.util.UtilDateTime"].getYear(weAnalisis.yearReal, timeZone, locale)>
                <#assign labelReal = weAnalisis.labelReal?default(yearReal)>
            </#if>
            <#if weAnalisis.yearPrev?has_content>
                <#assign yearPrev = Static["org.ofbiz.base.util.UtilDateTime"].getYear(weAnalisis.yearPrev, timeZone, locale)>
                <#assign labelPrev = weAnalisis.labelPrev?default(yearPrev)>
            </#if>
            <#if weAnalisis.referenceDate?has_content>
                <#assign referenceDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(weAnalisis.referenceDate, locale)>
            </#if>
            <#assign isMonitor = weAnalisis.isMonitor?default("N")>
        </#if>
    </#if>
    
    <div class="header-breadcrumbs">
        <div id="header-breadcrumbs-th"><#if hideMainFolder?has_content><@gzoom.breadcrumb parameters.breadcrumbsCurrentItem?if_exists "common-container"/></#if></div>
        <#assign activeTabIndex = null>
        <#if activeTabIndex?has_content>
            <#if !weChildren?has_content && (parameters.workEffortTypeId?has_content || parameters.weTypeId?has_content)>
                <#assign activeTabIndex = 1>
                <input type="hidden" id="activeTabIndex" name="activeTabIndex" value="management_${activeTabIndex}" />
            </#if>
        </#if>
    </div>
	<table class="basic-table list-table padded-row-table resizable draggable toggleable customizable headerFixable" cellspacing="0" id="InqMeasureTable">
        <thead>
            <tr class="header-row-2">
                <#if showGlResourceType?default("Y")=="Y"><th rowspan="2"></th></#if>
                <th rowspan="2">${uiLabelMap.FormFieldTitle_indicatorGlAccountId}</th>
                <#--<th rowspan="2">${uiLabelMap.WorkEffortIndicatorFormula}</th>-->
                <th rowspan="2">${uiLabelMap.WorkEffortPurposeMeasure_workEffortMeasureId}</th>
                <#if yearM4Real?has_content || yearM3Real?has_content || yearM2Real?has_content || yearM1Real?has_content><th <#if colSpanReal?has_content && colSpanReal &gt; 0>colspan="${colSpanReal}"</#if>>${uiLabelMap.WorkEffortAchieveInqMeasViewTrend}</th></#if>
                <#if isMonitor=="Y"><th colspan="2">${uiLabelMap.WorkeffortTargetBalance}</th></#if>
                <th rowspan="2">${uiLabelMap.HeaderWorkEffortStatus}</th>
                <#if visualBenchMark?has_content && visualBenchMark==true> 
                    <th rowspan="2">${uiLabelMap.WorkEffortAchieveViewBenchmark}</th>
                </#if>
                <th rowspan="2">${uiLabelMap.WorkEffortAchieveViewPerformance}</th>
            </tr>
            <tr class="header-row-2">
                <#if yearM4Real?has_content><th>${labelM4Real}</th></#if>
                <#if yearM3Real?has_content><th>${labelM3Real}</th></#if>
                <#if yearM2Real?has_content><th>${labelM2Real}</th></#if>
                <#if yearM1Real?has_content><th>${labelM1Real}</th></#if>
                <#if isMonitor=="Y"><th>${referenceDate?if_exists}</th></#if>
                <#if isMonitor=="Y"><th>${labelPrev?if_exists}</th></#if>
            </tr>
        </thead>
        <tbody>
            <#assign index=0/>
            <#assign glResourceTypeId=null>
            <#list workEffortAchieveInqMeasList as item>
									
                <#assign valActM4=yearMValues(item, 4, 'CONSUNTIVO', '${yearM1Real?default("")}', '${yearM2Real?default("")}', '${yearM3Real?default("")}', '${yearM4Real?default("")}')>                    
                <#assign valActM3=yearMValues(item, 3, 'CONSUNTIVO', '${yearM1Real?default("")}', '${yearM2Real?default("")}', '${yearM3Real?default("")}', '${yearM4Real?default("")}')>                    
                <#assign valActM2=yearMValues(item, 2, 'CONSUNTIVO', '${yearM1Real?default("")}', '${yearM2Real?default("")}', '${yearM3Real?default("")}', '${yearM4Real?default("")}')>                    
                <#assign valActM1=yearMValues(item, 1, 'CONSUNTIVO', '${yearM1Real?default("")}', '${yearM2Real?default("")}', '${yearM3Real?default("")}', '${yearM4Real?default("")}')>  

                <#if isMonitor=="Y" && referenceDate?has_content>
                    <#assign valBud=referenceDateBudActValues(item, 'TARGET')>
                    <#assign valAct=referenceDateBudActValues(item, 'CONSUNTIVO')>
                </#if>
                <#if yearReal?has_content>
                    <#assign valBudP0=yearRealValues(item, 'TARGET')>
                    <#assign valActP0=yearRealValues(item, 'CONSUNTIVO')>
                </#if>
                <tr <#if index%2 != 0>class="alternate-row"</#if>>
                    <#if showGlResourceType?default("Y")=="Y" && (!glResourceTypeId?has_content || glResourceTypeId != item.glResourceTypeId?if_exists)>
                        <#assign rowSpan=1>
                        <#if glResourceTypeDescriptionCounterMap?has_content && glResourceTypeDescriptionCounterMap[item.glResourceTypeId?if_exists]?has_content && glResourceTypeDescriptionCounterMap[item.glResourceTypeId?if_exists] &gt; 1>
                            <#assign rowSpan=glResourceTypeDescriptionCounterMap[item.glResourceTypeId?if_exists]>
                        </#if>
                        <td style="width: 9px; " <#if rowSpan &gt; 1>rowSpan="${rowSpan}"</#if>>
                            <div style="font-weight: bold;" <#-- style="font-size: 140%; -webkit-transform: rotate(-90deg); -moz-transform: rotate(-90deg); border: #777 1px solid; padding: 5px 10px; text-align: center; vertical-align: middle;" -->>
                                ${item.description?if_exists}
                            </div>
                        </td>
                        <#assign glResourceTypeId=item.glResourceTypeId?if_exists>
                    </#if>
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
                        <#-- <#assign description = StringUtil.wrapString(item.accountName?default(""))> -->
                        <#if item.uomDescr?has_content>
                            <#assign description = description + " - " + StringUtil.wrapString(item.uomDescr?default(""))>
                        </#if>
                        <!-- end: bug 3937 -->
                        
                        <#assign shortDescription = StringUtil.wrapString(description?default(""))>
                       <#-- <#if shortDescription?length &gt; 50>
                            <#assign shortDescription = StringUtil.wrapString(shortDescription?substring(0,50))+"...">
                        </#if> -->
                        
                        <div <#if description?has_content && description?length &gt; 50>class="mblShowDescription"</#if> <#if description?has_content && description?length &gt; 50> description="${StringUtil.wrapString(description)}"</#if>>
                            <input type="hidden" name="userLoginId" value="${(userLogin.userLoginId)?if_exists}"/>
                            <input type="hidden" name="workEffortId" value="${item.workEffortId?if_exists}"/>
                            <input type="hidden" name="workEffortMeasureId" value="${item.workEffortMeasureId?if_exists}"/>
                            <span class="title_indicator" style="cursor: pointer; color: #307bff; font-weight: bold;">${StringUtil.wrapString(shortDescription?if_exists)}</span>
                        </div>
                    </td>
                    <#-- <td>
                        <div>
                            <#assign accountDescription = item.accountDescription?default("")>
                            <#if accountDescription?length &gt; 50>
                                <#assign accountDescription = accountDescription?substring(0,50)+"...">
                            </#if>
                            <div style="font-style: italic;" <#if item.accountDescription?has_content && item.accountDescription?length &gt; 50>class="mblShowAccountDescription"</#if> <#if item.accountDescription?has_content && item.accountDescription?length &gt; 50> description="${StringUtil.wrapString(item.accountDescription)}"</#if>>${StringUtil.wrapString(accountDescription?if_exists)}</div>
                        </div>
                    </td> -->
                    <td style="width: 55px;">
                        <div>
                            <div class="mblDisplayCenteredCell">${StringUtil.wrapString(item.abbreviation?if_exists)}</div>
                        </div>
                    </td>
                    <#if yearM4Real?has_content>
                    <td style="width: 62px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comActM4?has_content> mblShowComActM4</#if>" <#if item.comActM4?has_content> description="${item.comActM4}"</#if>><#if valActM4?has_content>${valActM4?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if yearM3Real?has_content>
                    <td style="width: 62px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comActM3?has_content> mblShowComActM3</#if>" <#if item.comActM3?has_content> description="${item.comActM3}"</#if>><#if valActM3?has_content>${valActM3?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if yearM2Real?has_content>
                    <td style="width: 62px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comActM2?has_content> mblShowComActM2</#if>" <#if item.comActM2?has_content> description="${item.comActM2}"</#if>><#if valActM2?has_content>${valActM2?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if yearM1Real?has_content>
                    <td style="width: 62px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comActM1?has_content> mblShowComActM1</#if>" <#if item.comActM1?has_content> description="${item.comActM1}"</#if>><#if valActM1?has_content>${valActM1?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if isMonitor=="Y">
                    <td style="width: 62px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comBud?has_content> mblShowComBud</#if>" <#if item.comBud?has_content> description="${item.comBud}"</#if>><#if valBud?has_content>${valBud?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <td style="width: 62px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comBudP0?has_content> mblShowComBudP0</#if>" <#if item.comBudP0?has_content> description="${item.comBudP0}"</#if>><#if valBudP0?has_content>${valBudP0?string}</#if></div>
                        </div>
                    </td>
                    <#if isMonitor=="Y">
                    <td style="width: 62px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comAct?has_content> mblShowComAct</#if>" <#if item.comAct?has_content> description="${item.comAct}"</#if>><#if valAct?has_content>${valAct?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if visualBenchMark?has_content && visualBenchMark==true>
                        <td style="width: 62px;">
                            <div>
                                <div class="mblDisplayCenteredCell<#if item.comBenP0?has_content> mblShowComBenP0</#if>" <#if item.comBenP0?has_content> description="${item.comBenP0}"</#if>><#if item.valBenP0?has_content>${valBenP0?string}</#if></div>
                            </div>
                        </td>
                    </#if>
                    <td  style="padding: 1px !important; width: 70px;">
                        <div class="mblDisplayCenteredCell<#if item.perfAmountCalc?has_content && (item.perfAmountCalc != 0.0 || item.emoticonContentId?has_content)> showEmoticonValue</#if>"<#if item.perfAmountCalc?has_content && (item.perfAmountCalc != 0.0 || item.emoticonContentId?has_content)> description="${StringUtil.wrapString(item.perfAmountCalc?string("#,##0.###"))}"</#if>>
                            <@queryEmoticon id=item.emoticonContentId?default("")/> 
                        </div>
                    </td>
                </tr>   
                <#assign index = index+1> 
            </#list>
        </tbody>
     </table>
