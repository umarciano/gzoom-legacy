<#import "/base/webapp/common/ftl/gzoomMacro.ftl"  as gzoom/>

    <script type="text/javascript">
        var selectors = ["div.mblShowDescription"];
        populateTooltip(selectors);
        
        var selectors = ["div.mblShowValue"];
        populateTooltipValue(selectors);
        
    </script>
     	
	<#macro formatDate date=null>
		<#if date?is_date>
		    <#assign date_pattern = Static["org.ofbiz.base.util.UtilDateTime"].getDateFormat(locale)>
			${date?string(date_pattern)}
		</#if>
	</#macro>
	
	<#macro createGanttColumn startDate endDate year description endReferenceDate tipudm val cod actual="N" hasShowActualDates="N">
	   <#local timeStamp = Static["org.ofbiz.base.util.UtilDateTime"].toTimestamp(1,1,year,0,0,0)>
       
       <#if isMonitor=="Y" && endReferenceDate != "">
       		<#if endReferenceDate=="Y">
       			<#assign starYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(timeStamp)>
       			<#assign endYear = referenceDateTimeStamp>
	   	   <#else>
		   	   	<#assign starYear = referenceDateTimeStamp>
	       		<#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(timeStamp, timeZone, locale)>
        	</#if>
       <#else>
       	   <#assign starYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(timeStamp)>
       	   <#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(timeStamp, timeZone, locale)>
       </#if>
       
       <#local dayLength =  Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(starYear, endYear)+1>
       
       <#local startDiff = Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(starYear, startDate)+1>
       <#local endDiff = Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(endDate, endYear)+1>
       
       <#if startDiff &lt;= 1>
            <#-- caso in cui l'inizio dell'obiettivo e' in qualche intervallo precedente -->
            <#-- in questo caso assegno 0 a startColSpan per indicare che deve subito disegnare una barra colorata -->
           <#assign startColSpan=0>
       <#elseif startDiff &gt; 1 && startDiff &lt; dayLength>
           <#-- caso in cui l'inizio dell'obiettivo e' interno all'intervallo -->
           <#assign startColSpan=startDiff-1>
       <#else>
            <#assign startColSpan=dayLength*2>
       </#if>
       
       <#if endDiff &lt; 1>
            <#-- caso in cui la fine dell'obiettivo e' in qualche intervallo successivo -->
           <#assign endColSpan=0>
       <#elseif endDiff &gt;= 1 && endDiff &lt; dayLength>
           <#-- caso in cui la fine dell'obiettivo e' interno all'intervallo -->
           <#assign endColSpan=endDiff-1>
       <#else>
            <#assign endColSpan=dayLength*2>
       </#if>
       
       <#if actual=="N">
           <#assign showLineBar="Y">
       <#else>
           <#assign showLineBar=hasShowActualDates>
       </#if>	   
       
       <#if startColSpan &gt; dayLength>
        <td style="padding-left: 0px !important; padding-right: 0px !important;" colspan="${dayLength}"></td>
       <#elseif startColSpan==0>
            <#if endColSpan?has_content && endColSpan &gt;= 0 && endColSpan &lt; dayLength>
                <td style="padding-left: 0px !important; padding-right: 0px !important;" colspan="${dayLength-endColSpan}">
                   <div <#if context.gantValueFloatOver?if_exists == 'Y'>class="mblShowValue" <#if actual=="Y" && ((!tipudm?has_content || tipudm != "RATING_SCALE") && val?has_content)>value="${val}"</#if><#else> class="mblShowDescription" <#if description?has_content>description="${description}"</#if></#if>style="width: 100%; height: 100%; padding: 2px 0; <#if showLineBar?default("N")=="Y"><#if ((!tipudm?has_content || tipudm != "RATING_SCALE") && val?has_content) || (tipudm == 'RATING_SCALE' && cod?has_content)> background-color: blue !important;<#else>background-color: #0065FF !important;</#if></#if>">
                        <#if !(context.gantValueFloatOver?if_exists == 'Y')>
	                        <#if actual=="Y" && ((!tipudm?has_content || tipudm != "RATING_SCALE") && val?has_content)>
	                            <div class="mblDisplayCenteredCell" style="color: white;" description="${val}">${val}</div>
	                        <#else>	                        	
	                            <div>&nbsp;</div>
	                        </#if>
                        <#else>
	                        <div>&nbsp;</div> 
                        </#if>                       
                   </div>
               </td>
               <#if endColSpan &gt; 0>
                    <td style="padding-left: 0px !important; padding-right: 0px !important;" colspan="${endColSpan}"></td>
               </#if>
           <#else>
                <td style="padding-left: 0px !important; padding-right: 0px !important;" colspan="${dayLength}"></td>
           </#if>
       <#elseif startColSpan &gt; 0 && startColSpan &lt; dayLength>
            <td style="padding-left: 0px !important; padding-right: 0px !important;" colspan="${startColSpan}"></td>
            <td style="padding-left: 0px !important; padding-right: 0px !important;" colspan="${dayLength-startColSpan-endColSpan}">
               <div <#if context.gantValueFloatOver?if_exists == 'Y'>class="mblShowValue" <#if actual=="Y" && ((!tipudm?has_content || tipudm != "RATING_SCALE") && val?has_content)>value="${val}"</#if><#else> class="mblShowDescription" <#if description?has_content>description="${description}"</#if></#if> style="width: 100%; height: 100%; padding: 2px 0;<#if showLineBar?default("N")=="Y"><#if ((!tipudm?has_content || tipudm != "RATING_SCALE") && val?has_content) || (tipudm == 'RATING_SCALE' && cod?has_content)> background-color: blue !important;<#else>background-color: #0065FF !important;</#if></#if>">
                    <#if !(context.gantValueFloatOver?if_exists == 'Y')>
	                    <#if actual=="Y" && ((!tipudm?has_content || tipudm != "RATING_SCALE") && val?has_content)>
	                        <div class="mblDisplayCenteredCell" style="color: white;" description="${val}">${val}</div>
	                    <#else>
	                        <div>&nbsp;</div>
	                    </#if>
                    <#else>
                        <div>&nbsp;</div>	                    
                    </#if>                    
               </div>
           </td>
           <#if endColSpan &gt; 0>
                <td style="padding-left: 0px !important; padding-right: 0px !important;" colspan="${endColSpan}"></td>
           </#if>
       </#if>
	</#macro>
	
    <#assign modulo = "emplperf"/>
    <#assign form = "EmplPerfRootViewForms"/>
    <#assign screenName = "EmplPerfScreens.xml"/>
    <#if weContext?if_exists == "CTX_BS">
        <#assign modulo = "stratperf"/>
        <#assign form = "StratPerfRootViewForms"/>
        <#assign screenName = "StratPerfScreens.xml"/>
    <#elseif  weContext?if_exists == "CTX_OR">
        <#assign modulo = "orgperf"/>
        <#assign form = "OrgPerfRootViewForms"/>
        <#assign screenName = "OrgPerfScreens.xml"/>
    <#elseif  weContext?if_exists == "CTX_CO">
        <#assign modulo = "corperf"/>
        <#assign form = "CorPerfRootViewForms"/>
        <#assign screenName = "CorPerfScreens.xml"/>
    <#elseif  weContext?if_exists == "CTX_PR">
        <#assign modulo = "procperf"/>
        <#assign form = "ProcPerfRootViewForms"/>
        <#assign screenName = "ProcPerfScreens.xml"/>
    <#elseif  weContext?if_exists == "CTX_CG">
        <#assign modulo = "cdgperf"/>
        <#assign form = "CdgPerfRootViewForms"/>
        <#assign screenName = "CdgPerfScreens.xml"/>
    <#elseif  weContext?if_exists == "CTX_TR">
        <#assign modulo = "trasperf"/>
        <#assign form = "TrasPerfRootViewForms"/>
        <#assign screenName = "TrasPerfScreens.xml"/>
    <#elseif  weContext?if_exists == "CTX_RE">
        <#assign modulo = "rendperf"/>
        <#assign form = "RendPerfRootViewForms"/>
        <#assign screenName = "RendPerfScreens.xml"/>
    <#elseif  weContext?if_exists == "CTX_GD">
        <#assign modulo = "gdprperf"/>
        <#assign form = "GdprPerfRootViewForms"/>
        <#assign screenName = "GdprPerfScreens.xml"/>
    <#elseif  weContext?if_exists == "CTX_PA">
        <#assign modulo = "partperf"/>
        <#assign form = "PartPerfRootViewForms"/>
        <#assign screenName = "PartPerfScreens.xml"/>  
    <#elseif  weContext?if_exists == "CTX_DI">
        <#assign modulo = "dirigperf"/>
        <#assign form = "DirigPerfRootViewForms"/>
        <#assign screenName = "DirigPerfScreens.xml"/>               
    </#if>	
	
	<#assign pattern = "##"/>
	
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
    
     
     
     <#assign index=0/>
     <#if parameters.workEffortAnalysisId?has_content>
        <#assign weAnalisis = delegator.findOne("WorkEffortAnalysis", Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortAnalysisId", parameters.workEffortAnalysisId), true)>
        <#if weAnalisis?has_content>
        
        	<#assign currentYear = Static["org.ofbiz.base.util.UtilDateTime"].getYear(weAnalisis.yearReal, timeZone, locale)>
    
	     	<#assign currentYearM1 = currentYear-1>
		    <#assign timeStamp = Static["org.ofbiz.base.util.UtilDateTime"].toTimestamp(1,1,currentYearM1,0,0,0)>
		    <#assign starYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(timeStamp)>
		    <#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(timeStamp, timeZone, locale)>
		    <#assign dayLengthM1 =  Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(starYear, endYear)+1>
		    
		    <#assign currentYearM2 = currentYear-2>
		    <#assign timeStamp = Static["org.ofbiz.base.util.UtilDateTime"].toTimestamp(1,1,currentYearM2,0,0,0)>
		    <#assign starYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(timeStamp)>
		    <#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(timeStamp, timeZone, locale)>
		    <#assign dayLengthM2 =  Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(starYear, endYear)+1>
		    
		    <#assign currentYearP1 = currentYear+1>
		    <#assign timeStamp = Static["org.ofbiz.base.util.UtilDateTime"].toTimestamp(1,1,currentYearP1,0,0,0)>
		    <#assign starYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(timeStamp)>
		    <#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(timeStamp, timeZone, locale)>
		    <#assign dayLengthP1 =  Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(starYear, endYear)+1>
		     
		    <#assign currentYearP2 = currentYear+2>
		    <#assign timeStamp = Static["org.ofbiz.base.util.UtilDateTime"].toTimestamp(1,1,currentYearP2,0,0,0)>
		    <#assign starYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(timeStamp)>
		    <#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(timeStamp, timeZone, locale)>
		    <#assign dayLengthP2 =  Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(starYear, endYear)+1>
		     
		    <#assign currentYearP3 = currentYear+3>
		    <#assign timeStamp = Static["org.ofbiz.base.util.UtilDateTime"].toTimestamp(1,1,currentYearP3,0,0,0)>
		    <#assign starYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(timeStamp)>
		    <#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(timeStamp, timeZone, locale)>
		    <#assign dayLengthP3 =  Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(starYear, endYear)+1>
		     
		    <#assign timeStamp = Static["org.ofbiz.base.util.UtilDateTime"].toTimestamp(1,1,currentYear,0,0,0)>
		    <#assign starYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(timeStamp)>
		    <#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(timeStamp, timeZone, locale)>
		    <#assign dayLength =  Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(starYear, endYear)+1>
        
            <#assign isMonitor = weAnalisis.isMonitor?default("N")>
            <#if weAnalisis.referenceDate?has_content>
                <#assign referenceDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(weAnalisis.referenceDate, locale)>
                <#assign referenceDateTimeStamp = Static["org.ofbiz.base.util.UtilDateTime"].toTimestamp(weAnalisis.referenceDate)>
                
            	<#assign referenceDateString = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(referenceDate, "dd/MM", timeZone,  locale)>
            	<#assign dayLength1 =  Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(starYear, referenceDateTimeStamp)+1>
            	<#assign dayLength2 =  Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(referenceDateTimeStamp, endYear)+1>
            	<#assign dayLengthTotal =  dayLength2+dayLength1>
            	<#assign endYearString = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(endYear, locale)>
                <#assign referenceEndDateString = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(endYearString, "dd/MM", timeZone,  locale)>
            </#if>
        </#if>
     </#if>

     <table class="basic-table list-table padded-row-table resizable draggable toggleable customizable headerFixable" cellspacing="0">
        <thead>
            <tr class="header-row-2">
                <th rowspan="2"><div>${uiLabelMap.FormFieldTitle_workEffortName}</div><div>${uiLabelMap.FormFieldTitle_shortDescription}</div></th>
                <#if ("Y" == context.calculateFooterValues!!)> 
            	<th rowspan="2"><div>${uiLabelMap.WorkEffortViewIdentificationData_assocWeight}</div></th>
                </#if>
                
                <th rowspan="2">${uiLabelMap.FormFieldTitle_weTransValueProgress}</th>
                <th rowspan="2" colspan="${dayLengthM2}">${currentYearM2}</th>
                <th rowspan="2" colspan="${dayLengthM1}">${currentYearM1}</th>
                
                <#if isMonitor=="Y"><th rowspan="1" colspan="${dayLengthTotal}">${currentYear}</th> </#if>
                <#if isMonitor=="N"><th rowspan="2" colspan="${dayLength}">${currentYear}</th></#if>

                <th rowspan="2" colspan="${dayLengthP1}">${currentYearP1}</th>
                <th rowspan="2" colspan="${dayLengthP2}">${currentYearP2}</th>
                <th rowspan="2" colspan="${dayLengthP3}">${currentYearP3}</th>
                <!--<th rowspan="2">${uiLabelMap.WorkEffortAchieveViewAdvance}</th>-->
                <#assign singleHeaderTitle = false>
                <#if workEffortAchieveList?has_content>
                    <#assign firstItem = workEffortAchieveList[0]>
                    <#if !firstItem.budgetValue?has_content>
                        <#assign singleHeaderTitle = true>
                    </#if>
                </#if>
                <th rowspan="2"><#if !singleHeaderTitle><div>${uiLabelMap.WorkEffortAchieveViewRealized}</div><div>${uiLabelMap.WorkEffortAchieveViewProgrammed}</div><#else><div>${uiLabelMap.WorkEffortAchieveViewPerformance}</div></#if></th>
                <th>${uiLabelMap.BaseActions}</th>
            </tr>
            <tr class="header-row-2">
            	<#if isMonitor=="Y" && currentYear?has_content && referenceDate?has_content><th rowspan="1" colspan="${dayLength1}">${referenceDateString}</th></#if>
                <#if isMonitor=="Y" && currentYear?has_content><th rowspan="1" colspan="${dayLength2}">${referenceEndDateString}</th></#if>
                
            </tr>
        </thead>
        <tbody>
            
            <#list workEffortAchieveList as item>
                <tr  <#if index%2 != 0>class="alternate-row"</#if>>
                    <td style="width: 400px;" rowspan="2">
                    	<input type="hidden" name="entityPkFields_o_${index}" value="workEffortId|workEffortTypeId|workEffortIdFrom"/>
                        <input type="hidden" name="userLoginId_o_${index}" value="${(userLogin.userLoginId)?if_exists}"/>
                    	<input type="hidden" name="workEffortId_o_${index}" value="${item.workEffortId?if_exists}"/>
                    	<input type="hidden" name="workEffortIdHeader_o_${index}" value="${item.workEffortIdFrom?if_exists}"/>
                    	<input type="hidden" name="workEffortIdFrom_o_${index}" value="${item.workEffortId?if_exists}"/>
                    	<input type="hidden" name="workEffortTypeId_o_${index}" value="${item.workEffortTypeId?if_exists}"/>
                    	<input type="hidden" name="operationalEntityName_o_${index}" value="${entityName}"/>
                    	<input type="hidden" name="parentFormNotAllowed_o_${index}" value="Y"/>
                    	<input type="hidden" name="subFolderExtraParamFields_o_${index}" value="parentFormNotAllowed"/>
                    	<input type="hidden" name="headerEntityName_o_${index}" value="${parameters.entityName}"/>
                    	<input type="hidden" name="workEffortAnalysisId_o_${index}" value="${parameters.workEffortAnalysisId?if_exists}"/>
                    	<input type="hidden" name="transactionDate_o_${index}" value="${item.transactionDate?if_exists}"/>
                    	<input type="hidden" name="snapshot_o_${index}" value="${parameters.snapshot?if_exists}"/>
                    	<input type="hidden" name="breadcrumbsCurrentItem_o_${index}" value="${parameters.breadcrumbsCurrentItem?if_exists}_**_${item.workEffortName?if_exists}"/>
                    	<input type="hidden" name="sortField_o_${index}" value="sequenceNum|sourceReferenceId|workEffortId"/>
                    	
                    	<div>
                    		<div class="mblContainer" style="font-weight: bold">
                				<div class="mblParameter" name="entityName" value="WorkEffortAchieveChildView"></div>
                				<div class="mblParameter" name="parentEntityName" value="WorkEffortAchieveView"></div>
                				<div class="mblParameter" name="workEffortIdFrom" value="${item.workEffortId?if_exists}"></div>
                				<div class="mblParameter" name="noConditionFind" value="Y"></div>
                				<div class="mblParameter" name="saveView" value="N"></div>
                				<div class="mblParameter" name="childManagement" value="Y"></div>
                				<div class="mblParameter" name="sortField" value="sequenceNum|sourceReferenceId|workEffortId"></div>
                				<#if "Y" == showOrgUnit?if_exists>
                					${item.weOrgPartyDescr?if_exists}
                				<#else>
                					${item.workEffortName?if_exists}
                				</#if>
                				
                			</div>
                			
                			<#-- <div class="mblContainer mblShowDescription" style="font-style: italic;" <#if item.description?has_content> description="${StringUtil.wrapString(item.description)}"</#if>>${StringUtil.wrapString(itemDescription)}</div> -->
                    	</div>
                    </td>
                    <#if ("Y" == context.calculateFooterValues!!)> 
                    	<td style="width: 30px; text-align: center;" rowspan="2" >
            				<div>${item.assocWeight?if_exists}</div>
                		</td>
                	</td>
                    </#if>
                    <td style="width: 70px;">
                        <div>${(descrizioniValori.budgetDesc)?if_exists}</div>
                    </td>
                    
                    <@createGanttColumn startDate=item.estimatedStartDate endDate=item.estimatedCompletionDate year=currentYearM2 description=item.description?default("") endReferenceDate="" tipudm=item.tipudm?default("") val=item.valBudM2?default("") cod=item.codBudM2?default("")/>
                    <@createGanttColumn startDate=item.estimatedStartDate endDate=item.estimatedCompletionDate year=currentYearM1 description=item.description?default("") endReferenceDate="" tipudm=item.tipudm?default("") val=item.valBudM1?default("") cod=item.codBudM1?default("")/>
                    
                    <#if isMonitor=="Y">
                    <@createGanttColumn startDate=item.estimatedStartDate endDate=item.estimatedCompletionDate year=currentYear description=item.description?default("") endReferenceDate="Y" tipudm=item.tipudm?default("") val=item.valBud?default("") cod=item.codBud?default("")/>
                    <@createGanttColumn startDate=item.estimatedStartDate endDate=item.estimatedCompletionDate year=currentYear description=item.description?default("") endReferenceDate="N" tipudm=item.tipudm?default("") val=item.valBudP0?default("") cod=item.codBudP0?default("")/>
                    <#else>
                    <#if isMonitor=="N"><@createGanttColumn startDate=item.estimatedStartDate endDate=item.estimatedCompletionDate year=currentYear description=item.description?default("") endReferenceDate="" tipudm=item.tipudm?default("") val=item.valBudP0?default("") cod=item.codBudP0?default("")/></#if>
                    </#if>                
                    
                    <@createGanttColumn startDate=item.estimatedStartDate endDate=item.estimatedCompletionDate year=currentYearP1 description=item.description?default("") endReferenceDate="" tipudm=item.tipudm?default("") val=item.valBudP1?default("") cod=item.codBudP1?default("")/>
                    <@createGanttColumn startDate=item.estimatedStartDate endDate=item.estimatedCompletionDate year=currentYearP2 description=item.description?default("") endReferenceDate="" tipudm=item.tipudm?default("") val=item.valBudP2?default("") cod=item.codBudP2?default("")/>
                    <@createGanttColumn startDate=item.estimatedStartDate endDate=item.estimatedCompletionDate year=currentYearP3 description=item.description?default("") endReferenceDate="" tipudm=item.tipudm?default("") val=item.valBudP3?default("") cod=item.codBudP3?default("")/>
                    
                    <td  style="padding: 1px !important; text-align: center; width: 100px;" rowspan="2">
                        <div style="float: left;">
                            <#if ""!=(item.imageSrc)?if_exists> 
                                <img class="speedometer speedometer_enlarge" src="${item.imageSrc}" speed="${item.imageValue}" target="${item.budgetValue?if_exists}"></img>
                          <#--  <#else>
                                <div class="noSpeedometerPresent"><span>${uiLabelMap.NoSpeedometerPresent}</span></div>-->
                            </#if>
                        </div>
                        <div style="float: right; padding-top: 20px; padding-right: 5px;">
                            <div style="height: 40%">
                                <div>
                                    <#if item.imageValue?has_content>
                                        <#assign imageValue = Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.imageValue, pattern, locale)/>
                                        ${imageValue?if_exists}%
                                    </#if>   
                                </div>
                                <div>
                                    <#if item.budgetValue?has_content>
                                        <#assign budgetValue = Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.budgetValue, pattern, locale)/>
                                        ${budgetValue?if_exists}%
                                    </#if>                        
                                </div>
                            </div>
                            <div style="height: 30%">
                                <#if (item.alertContentId?exists)>
                                    <img class="mblDisplayIconCell" src="/content/control/stream?contentId=${item.alertContentId}"/> 
                                <#else>
                                    <div class="mblDisplayIconCell"/>
                                </#if>
                            </div>
                            <div style="height: 30%">
                                <#if (item.hasScoreAlertSrc?exists)>
                                    <img class="mblDisplayIconCell" src="${item.hasScoreAlertSrc}"/> 
                                <#else>
                                    <div/>
                                </#if>
                            </div>
                        </div>
                    </td>
                    <td align="center">
                        <#if item.canUpdateRoot?if_exists == "Y">
                            <ul>
                                <li class="class-collegato-active-center"><i class="fas fa-star" onclick="ajaxUpdateAreas('common-container,/${modulo}/control/managementContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&entityName=WorkEffortView&rootInqyTree=N&specialized=Y&rootTree=N&loadTreeView=Y&ignoreSelectedIdFromCookie=Y&ajaxCall=Y&sourceReferenceId=${item.parentSourceReferenceId?if_exists}&workEffortIdRoot=${item.workEffortParentId?if_exists}&workEffortId=${item.workEffortParentId?if_exists}&successCode=management&saveView=Y&searchFormLocation=component://${modulo}/widget/forms/${form}.xml&searchFormResultLocation=component://${modulo}/widget/forms/${form}.xml&advancedSearchFormLocation=component://${modulo}/widget/forms/${form}.xml&searchFormScreenName=WorkEffortRootViewSearchFormScreen&searchFormScreenLocation=component://${modulo}/widget/screens/${screenName}&searchResultContextFormName=WorkEffortRootViewSearchResultContextForm&searchResultContextFormLocation=component://${modulo}/widget/forms/${form}.xml'); return false;"></i></li>
                            </ul>
                        <#else>
                            <ul>
                                <li class="class-collegato-disabled-center"><i class="fas fa-star" onclick="ajaxUpdateAreas('common-container,/${modulo}/control/managementContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&entityName=WorkEffortView&rootInqyTree=Y&specialized=Y&rootTree=N&loadTreeView=Y&ignoreSelectedIdFromCookie=Y&ajaxCall=Y&sourceReferenceId=${item.parentSourceReferenceId?if_exists}&workEffortIdRoot=${item.workEffortParentId?if_exists}&workEffortId=${item.workEffortParentId?if_exists}&successCode=management&saveView=Y&searchFormLocation=component://${modulo}/widget/forms/${form}.xml&searchFormResultLocation=component://${modulo}/widget/forms/${form}.xml&advancedSearchFormLocation=component://${modulo}/widget/forms/${form}.xml&searchFormScreenName=WorkEffortRootViewSearchFormScreen&searchFormScreenLocation=component://${modulo}/widget/screens/${screenName}&searchResultContextFormName=WorkEffortRootViewSearchResultContextForm&searchResultContextFormLocation=component://${modulo}/widget/forms/${form}.xml'); return false;"></i></li>
                            </ul>                                    
                        </#if>
                    </td>                    
                </tr>
                
                <tr <#if index%2 != 0>class="alternate-row"</#if>>
                	<td style="width: 70px;">
                        <div>${(descrizioniValori.actualDesc)?if_exists}</div>
                    </td>
					
					<#if item.hasShowActualDates?if_exists == "Y">
						<#assign startDate=item.actualStartDate>
						<#assign itemEndDate=item.actualCompletionDate>
					<#else>
						<#assign startDate=item.estimatedStartDate>
						<#assign itemEndDate=item.estimatedCompletionDate>					
					</#if>                   
                    <#if item.valTransactionDate?has_content && item.valTransactionDate &gt; itemEndDate>
                    	<#assign endDate=item.valTransactionDate>
                	<#else>
                		<#assign endDate=itemEndDate>
                    </#if>
                    
                    <@createGanttColumn startDate=startDate endDate=endDate year=currentYearM2 description=item.description?default("") endReferenceDate="" tipudm=item.tipudm?default("") val=item.valActM2?default("")  cod=item.codActM2?default("") actual="Y" hasShowActualDates=item.hasShowActualDates?default("N")/>
                    <@createGanttColumn startDate=startDate endDate=endDate year=currentYearM1 description=item.description?default("") endReferenceDate="" tipudm=item.tipudm?default("") val=item.valActM1?default("")  cod=item.codActM1?default("") actual="Y" hasShowActualDates=item.hasShowActualDates?default("N")/>
                    
                    <#if isMonitor=="Y">
                    <@createGanttColumn startDate=startDate endDate=endDate year=currentYear description=item.description?default("") endReferenceDate="Y" tipudm=item.tipudm?default("") val=item.valAct?default("")  cod=item.codAct?default("") actual="Y" hasShowActualDates=item.hasShowActualDates?default("N")/>
                    <@createGanttColumn startDate=startDate endDate=endDate year=currentYear description=item.description?default("") endReferenceDate="N" tipudm=item.tipudm?default("") val=item.valActP0?default("")  cod=item.codActP0?default("") actual="Y" hasShowActualDates=item.hasShowActualDates?default("N")/>
                    <#else>
                    <#if isMonitor=="N"><@createGanttColumn startDate=startDate endDate=endDate year=currentYear description=item.description?default("") endReferenceDate="" tipudm=item.tipudm?default("") val=item.valActP0?default("")  cod=item.codActP0?default("") actual="Y" hasShowActualDates=item.hasShowActualDates?default("N")/></#if>
                    </#if>                
                    
                    <@createGanttColumn startDate=startDate endDate=endDate year=currentYearP1 description=item.description?default("") endReferenceDate="" tipudm=item.tipudm?default("") val=item.valActP1?default("")  cod=item.codActP1?default("") actual="Y" hasShowActualDates=item.hasShowActualDates?default("N")/>
                    <@createGanttColumn startDate=startDate endDate=endDate year=currentYearP2 description=item.description?default("") endReferenceDate="" tipudm=item.tipudm?default("") val=item.valActP2?default("")  cod=item.codActP2?default("") actual="Y" hasShowActualDates=item.hasShowActualDates?default("N")/>
                    <@createGanttColumn startDate=startDate endDate=endDate year=currentYearP3 description=item.description?default("") endReferenceDate="" tipudm=item.tipudm?default("") val=item.valActP3?default("")  cod=item.codActP3?default("") actual="Y" hasShowActualDates=item.hasShowActualDates?default("N")/>
                    <td></td>
                </tr>
               
                <#assign index = index+1>
            </#list>
            <#if ("Y" == context.calculateFooterValues!!)> 
                <!-- prima riga -->
                <tr class="header-row-2">
                    <!-- Descrizione prima colonna -->
                    <!--<td style="width: 400px;" rowspan="2">-->
                    <td rowspan="2">
                        <div class="mblContainer" style="font-weight: bold">
                            Stato Avanzamento Progetto
                        </div>
                    </td>
                    <td style="width: 30px;" rowspan="2">
                		<div>${weightSum?if_exists}</div>
                	</td>
                    <td style="width: 70px;">
                        <div>${(descrizioniValori.budgetDesc)?if_exists}</div>
                    </td>
       
                    <@printCol colspan=dayLengthM2 val=budgetValueMap.M2 />
                    <@printCol colspan=dayLengthM1 val=budgetValueMap.M1 />
                    <#if isMonitor=="Y">
                        <@printCol colspan=dayLength1 val=budgetValueMap.ACT />
                        <@printCol colspan=dayLength2 val=budgetValueMap.P0 />
                    </#if>
                    <#if isMonitor=="N"><@printCol colspan=dayLength val=budgetValueMap.P0 /></#if>
       
                    <@printCol colspan=dayLengthP1 val=budgetValueMap.P1 />
                    <@printCol colspan=dayLengthP2 val=budgetValueMap.P2 />
                    <@printCol colspan=dayLengthP3 val=budgetValueMap.P3 />
                    
                    <td></td>
                    <td></td>
                </tr>
                <!-- seconda riga -->           
                <tr class="header-row-2">
                    <td style="width: 70px;">
                        <div>${(descrizioniValori.actualDesc)?if_exists}</div>
                    </td>
      
                    <@printCol colspan=dayLengthM2 val=actualValueMap.M2 />
                    <@printCol colspan=dayLengthM1 val=actualValueMap.M1 />
                    <#if isMonitor=="Y">
                        <@printCol colspan=dayLength1 val=actualValueMap.ACT />
                        <@printCol colspan=dayLength2 val=actualValueMap.P0 />
                    </#if>
                    <#if isMonitor=="N"><@printCol colspan=dayLength val=actualValueMap.P0 /></#if>
       
                    <@printCol colspan=dayLengthP1 val=actualValueMap.P1 />
                    <@printCol colspan=dayLengthP2 val=actualValueMap.P2 />
                    <@printCol colspan=dayLengthP3 val=actualValueMap.P3 />
                    
                    <td></td>
                    <td></td>
                </tr>
            </#if>
        </tbody>
        
        <!-- 
        	Bug 4043, footer situazione avanzamento
    	 -->
		    	 
		<#macro printCol colspan val="0">
						
			<td style="padding-left: 0px !important; padding-right: 0px !important;" colspan="${colspan}" ${colspan}>
				<div class="mblShowDescription" style="width: 100%; height: 100%; padding: 2px 0;">
					<#if (val != "0")>
						<div>${val}&#37;</div>
					<#else>	
						<div>&nbsp;</div>
					</#if>	
				     
				</div>
			</td>
		</#macro> 
    	
     </table>
