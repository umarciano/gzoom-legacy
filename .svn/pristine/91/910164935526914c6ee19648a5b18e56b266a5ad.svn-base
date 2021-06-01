    <script type="text/javascript">
        var selectors = ["div.mblShowDescription", "div.showEmoticonValue", "div.mblShowAccountDescription", "div.mblShowComBud", "div.mblShowComBudP3", "div.mblShowComBudP2", "div.mblShowComBudP1", "div.mblShowComBudM2", "div.mblShowComBudM1", "div.mblShowComAct", "div.mblShowComActP3", "div.mblShowComActP2", "div.mblShowComActP1", "div.mblShowComActM2", "div.mblShowComActM1", "div.mblShowComBudP0", "div.mblShowComActP0",];
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

    <#function yearMValues item yearM fiscalType yearM1Real yearM2Real>
        <#assign retVal="">

		<#if item.detailEnumId?default("")=="ACCDET_SUM" && item.detailLayout?default(false)==true>
			<#if fiscalType=="TARGET">
				<#if yearM==1>
				    <#assign retVal=item.totalYearM1RealTarget?default(retVal)>
				</#if>
			    <#if yearM==2>
				    <#assign retVal=item.totalYearM2RealTarget?default(retVal)>
				</#if>
		    </#if>
			<#if fiscalType=="CONSUNTIVO">
				<#if yearM==1>
				    <#assign retVal=item.totalYearM1RealConsuntivo?default(retVal)>
				</#if>
			    <#if yearM==2>
				    <#assign retVal=item.totalYearM2RealConsuntivo?default(retVal)>
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
			        <#if fiscalType=="TARGET">
                        <#if item.comActM1?has_content || item.valActM1?has_content>
                            <#if !item.codBudM1?has_content && !item.valBudM1?has_content>
                                <#assign retVal="-">
                            </#if>
                        </#if>
                        <#if item.uomTypeId?default("")=="RATING_SCALE">
                            <#assign retVal=item.codBudM1?default(retVal)>
                        <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    	    <#if item.valBudM1?has_content>
                    		    <#assign retVal = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valBudM1, locale)?default(retVal)>
                    	    </#if>
                        <#else>                    	
                            <#if item.valBudM1?has_content>
                    		    <#assign retVal = item.valBudM1?default(retVal)?string("#,##0.########")>
                    	    </#if>
                        </#if>
					</#if>				
			        <#if fiscalType=="CONSUNTIVO">
                        <#if !item.comActM1?has_content && !item.valActM1?has_content>
                            <#if item.codBudM1?has_content || item.valBudM1?has_content>
                                <#assign retVal="-">
                            </#if>
                        </#if>
                        <#if item.uomTypeId?default("")=="RATING_SCALE">
                            <#assign retVal=item.codActM1?default(retVal)>
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
			</#if>
			<#if yearM==2>
			    <#if yearM2Real?has_content>
			        <#if fiscalType=="TARGET">
                        <#if item.comActM2?has_content || item.valActM2?has_content>
                            <#if !item.codBudM2?has_content && !item.valBudM2?has_content>
                                <#assign retVal="-">
                            </#if>
						</#if>
                        <#if item.uomTypeId?default("")=="RATING_SCALE">
                            <#assign retVal=item.codBudM2?default(retVal)>
                        <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    	    <#if item.valBudM2?has_content>
                    		    <#assign retVal = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valBudM2, locale)?default(retVal)>
                    	    </#if>
                        <#else>                    	
                            <#if item.valBudM2?has_content>
                    		    <#assign retVal = item.valBudM2?default(retVal)?string("#,##0.########")>
                    	    </#if>
                        </#if>
					</#if>					
			        <#if fiscalType=="CONSUNTIVO">
                        <#if !item.comActM2?has_content && !item.valActM2?has_content>
                            <#if item.codBudM2?has_content || item.valBudM2?has_content>
                                <#assign retVal="-">
                            </#if>
						</#if>
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
                <#if item.comAct?has_content || item.valAct?has_content>
                    <#if !item.codBud?has_content && !item.valBud?has_content>
                        <#assign retVal2="-">
                    </#if>
                </#if>						
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
                <#if !item.comAct?has_content && !item.valAct?has_content>
                    <#if item.codBud?has_content || item.valBud?has_content>
                        <#assign retVal2="-">
                    </#if>
                </#if>				
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
                <#if item.comActP0?has_content || item.valActP0?has_content>
                    <#if !item.codBudP0?has_content && !item.valBudP0?has_content>
                        <#assign retVal3="-">
                    </#if>
				</#if>		
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
                <#if !item.comActP0?has_content && !item.valActP0?has_content>
                    <#if item.codBudP0?has_content || item.valBudP0?has_content>
                        <#assign retVal3="-">
                    </#if>
				</#if>	
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
	
    <#function yearPValues item yearP fiscalType yearP1Real yearP2Real yearP3Real>
        <#assign retVal4="">

		<#if item.detailEnumId?default("")=="ACCDET_SUM" && item.detailLayout?default(false)==true>
			<#if fiscalType=="TARGET">
				<#if yearP==1>
				    <#assign retVal4=item.totalYearP1RealTarget?default(retVal4)>
				</#if>
			    <#if yearP==2>
				    <#assign retVal4=item.totalYearP2RealTarget?default(retVal4)>
				</#if>
			    <#if yearP==3>
				    <#assign retVal4=item.totalYearP3RealTarget?default(retVal4)>
				</#if>
			</#if>
			<#if fiscalType=="CONSUNTIVO">
				<#if yearP==1>
				    <#assign retVal4=item.totalYearP1RealConsuntivo?default(retVal4)>
				</#if>
			    <#if yearP==2>
				    <#assign retVal4=item.totalYearP2RealConsuntivo?default(retVal4)>
				</#if>	
			    <#if yearP==3>
				    <#assign retVal4=item.totalYearP3RealConsuntivo?default(retVal4)>
				</#if>				
			</#if>
			<#if retVal4?has_content>
                <#if item.uomTypeId?default("")=="DATE_MEASURE">
                    <#assign retVal4 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(retVal4, locale)?default(retVal4)>
                <#else>
				    <#assign retVal4 = retVal4?default(retVal4)?string("#,##0.########")>
				</#if>
			</#if> 
		<#else>
		    <#if yearP==1>
                <#if yearP1Real?has_content>
			        <#if fiscalType=="TARGET">
                        <#if item.comActP1?has_content || item.valActP1?has_content>
                            <#if !item.codBudP1?has_content && !item.valBudP1?has_content>
                                <#assign retVal4="-">
                            </#if>
                        </#if>
                        <#if item.uomTypeId?default("")=="RATING_SCALE">
                            <#assign retVal4=item.codBudP1?default(retVal4)>
                        <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    	    <#if item.valBudP1?has_content>
                    		    <#assign retVal4 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valBudP1, locale)?default(retVal4)>
                    	    </#if>
                        <#else>                    	
                            <#if item.valBudP1?has_content>
                    		    <#assign retVal4 = item.valBudP1?default(retVal4)?string("#,##0.########")>
                    	    </#if>
                        </#if>						
					</#if>
			        <#if fiscalType=="CONSUNTIVO">
                        <#if !item.comActP1?has_content && !item.valActP1?has_content>
                            <#if item.codBudP1?has_content || item.valBudP1?has_content>
                                <#assign retVal4="-">
                            </#if>
                        </#if>
                        <#if item.uomTypeId?default("")=="RATING_SCALE">
                            <#assign retVal4=item.codActP1?default(retVal4)>
                        <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    	    <#if item.valActP1?has_content>
                    		    <#assign retVal4 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valActP1, locale)?default(retVal4)>
                    	    </#if>
                        <#else>                    	
                    	    <#if item.valActP1?has_content>
                    		    <#assign retVal4 = item.valActP1?default(retVal4)?string("#,##0.########")>
                    	    </#if>
                        </#if>						
					</#if>
                </#if>
			</#if>
			<#if yearP==2>
			    <#if yearP2Real?has_content>
			        <#if fiscalType=="TARGET">
                        <#if item.comActP2?has_content || item.valActP2?has_content>
                            <#if !item.codBudP2?has_content && !item.valBudP2?has_content>
                                <#assign retVal4="-">
                            </#if>
						</#if>
                        <#if item.uomTypeId?default("")=="RATING_SCALE">
                            <#assign retVal4=item.codBudP2?default(retVal4)>
                        <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    	    <#if item.valBudP2?has_content>
                    		    <#assign retVal4 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valBudP2, locale)?default(retVal4)>
                    	    </#if>
                        <#else>                    	
                            <#if item.valBudP2?has_content>
                    		    <#assign retVal4 = item.valBudP2?default(retVal4)?string("#,##0.########")>
                    	    </#if>
                        </#if>
					</#if>					
			        <#if fiscalType=="CONSUNTIVO">
                        <#if !item.comActP2?has_content && !item.valActP2?has_content>
                            <#if item.codBudP2?has_content || item.valBudP2?has_content>
                                <#assign retVal4="-">
                            </#if>
						</#if>
                        <#if item.uomTypeId?default("")=="RATING_SCALE">
                            <#assign retVal4=item.codActP2?default(retVal4)>
                        <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    	    <#if item.valActP2?has_content>
                    		    <#assign retVal4 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valActP2, locale)?default(retVal4)>
                    	    </#if>
                        <#else>
                    	    <#if item.valActP2?has_content>
                    		    <#assign retVal4 = item.valActP2?default(retVal4)?string("#,##0.########")>
                    	    </#if>
                        </#if>						
					</#if>	
				</#if>				
			</#if>
			<#if yearP==3>
			    <#if yearP3Real?has_content>
			        <#if fiscalType=="TARGET">
                        <#if item.comActP3?has_content || item.valActP3?has_content>
                            <#if !item.codBudP3?has_content && !item.valBudP3?has_content>
                                <#assign retVal4="-">
                            </#if>
                        </#if>
                        <#if item.uomTypeId?default("")=="RATING_SCALE">
                            <#assign retVal4=item.codBudP3?default(retVal4)>
                        <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    	    <#if item.valBudP3?has_content>
                    		    <#assign retVal4 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valBudP3, locale)?default(retVal4)>
                    	    </#if>
                        <#else>                    	
                            <#if item.valBudP3?has_content>
                    		    <#assign retVal4 = item.valBudP3?default(retVal4)?string("#,##0.########")>
                    	    </#if>
                        </#if>
					</#if>					
			        <#if fiscalType=="CONSUNTIVO">
                        <#if !item.comActP3?has_content && !item.valActP3?has_content>
                            <#if item.codBudP3?has_content || item.valBudP3?has_content>
                                <#assign retVal4="-">
                            </#if>
                        </#if>
                        <#if item.uomTypeId?default("")=="RATING_SCALE">
                            <#assign retVal4=item.codActP3?default(retVal4)>
                        <#elseif item.uomTypeId?default("")=="DATE_MEASURE">
                    	    <#if item.valActP3?has_content>
                    		    <#assign retVal4 = Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(item.valActP3, locale)?default(retVal4)>
                    	    </#if>
                        <#else>                    	
                    	    <#if item.valActP3?has_content>
                    		    <#assign retVal4 = item.valActP3?default(retVal4)?string("#,##0.########")>
                    	    </#if>
                        </#if>						
					</#if>	
				</#if>				
			</#if>			
        </#if>		
		
	    <#return retVal4>
    </#function>	
	
    <#if parameters.workEffortAnalysisId?has_content>
        <#assign weAnalisis = delegator.findOne("WorkEffortAnalysis", Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortAnalysisId", parameters.workEffortAnalysisId), true)>
        <#if weAnalisis?has_content>
            <#assign isMonitor = weAnalisis.isMonitor?default("N")>
            <#if weAnalisis.yearM2Real?has_content>
                <#assign yearM2Real = Static["org.ofbiz.base.util.UtilDateTime"].getYear(weAnalisis.yearM2Real, timeZone, locale)>
                <#assign labelM2Real = weAnalisis.labelM2Real?default(yearM2Real)>
            </#if>
            <#if weAnalisis.yearM1Real?has_content>
                <#assign yearM1Real = Static["org.ofbiz.base.util.UtilDateTime"].getYear(weAnalisis.yearM1Real, timeZone, locale)>
                <#assign labelM1Real = weAnalisis.labelM1Real?default(yearM1Real)>
            </#if>
            <#if weAnalisis.yearReal?has_content>
                <#assign yearReal = Static["org.ofbiz.base.util.UtilDateTime"].getYear(weAnalisis.yearReal, timeZone, locale)>
                <#assign labelReal = weAnalisis.labelReal?default(yearReal)>
            </#if>
            <#if weAnalisis.yearP1Real?has_content>
                <#assign yearP1Real = Static["org.ofbiz.base.util.UtilDateTime"].getYear(weAnalisis.yearP1Real, timeZone, locale)>
                <#assign labelP1Real = weAnalisis.labelP1Real?default(yearP1Real)>
            </#if>
            <#if weAnalisis.yearP2Real?has_content>
                <#assign yearP2Real = Static["org.ofbiz.base.util.UtilDateTime"].getYear(weAnalisis.yearP2Real, timeZone, locale)>
                <#assign labelP2Real = weAnalisis.labelP2Real?default(yearP2Real)>
            </#if>
            <#if weAnalisis.yearP3Real?has_content>
                <#assign yearP3Real = Static["org.ofbiz.base.util.UtilDateTime"].getYear(weAnalisis.yearP3Real, timeZone, locale)>
                <#assign labelP3Real = weAnalisis.labelP3Real?default(yearP3Real)>
            </#if>
            <#if weAnalisis.referenceDate?has_content>
                <#assign referenceDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(weAnalisis.referenceDate, locale)>
                <#assign referenceDateString = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(referenceDate, "dd/MM", timeZone,  locale)>
                <#assign timeStamp = Static["org.ofbiz.base.util.UtilDateTime"].toTimestamp(1,1,yearReal,0,0,0)>
                <#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(timeStamp, timeZone, locale)>
                <#assign endYearString = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(endYear, locale)>
                <#assign referenceEndDateString = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(endYearString, "dd/MM", timeZone,  locale)>
            </#if>
            
        </#if>
    </#if>
    <table class="basic-table list-table padded-row-table resizable draggable toggleable customizable headerFixable" cellspacing="0" id="InqMeasureTable">
        <thead>
            <tr class="header-row-2">
                <th rowspan="2">${uiLabelMap.FormFieldTitle_indicatorGlAccountId}<#-- /${uiLabelMap.WorkEffortIndicatorFormula} --></th>
                <th rowspan="2">${uiLabelMap.WorkEffortPurposeMeasure_workEffortMeasureId}</th>
                <th rowspan="2">${uiLabelMap.FormFieldTitle_weTransValueProgress}</th>
                <#if labelM2Real?has_content><th rowspan="2">${labelM2Real}</th></#if>
                <#if labelM1Real?has_content><th rowspan="2">${labelM1Real}</th></#if>
                
                <#if isMonitor=="Y" && yearReal?has_content && referenceDate?has_content> <th rowspan="1" colspan="2">${labelReal}</th> </#if>
                <#if isMonitor=="N" && yearReal?has_content><th rowspan="2">${labelReal}</th></#if>
                
                <#if labelP1Real?has_content><th rowspan="2">${labelP1Real}</th></#if>
                <#if labelP2Real?has_content><th rowspan="2">${labelP2Real}</th></#if>
                <#if labelP3Real?has_content><th rowspan="2">${labelP3Real}</th></#if>
                <th rowspan="2">${uiLabelMap.WorkEffortAchieveViewPerformance}</th>
            </tr>
            <tr class="header-row-2">
                <#if isMonitor=="Y" && yearReal?has_content && referenceDate?has_content><th rowspan="1">${referenceDateString}</th></#if>
                <#if isMonitor=="Y" && yearReal?has_content><th rowspan="1">${referenceEndDateString}</th></#if>
                <#-- <th>${referenceDate}</th> -->
            </tr>
        </thead>
        <tbody>
            <#assign index=0/>
            <#list workEffortAchieveInqMeasList as item>
                <#if yearM2Real?has_content>
                    <#assign valBudM2=yearMValues(item, 2, 'TARGET', '${yearM1Real?default("")}', '${yearM2Real?default("")}')>
                    <#assign valActM2=yearMValues(item, 2, 'CONSUNTIVO', '${yearM1Real?default("")}', '${yearM2Real?default("")}')>
                </#if>
                <#if yearM1Real?has_content>
                    <#assign valBudM1=yearMValues(item, 1, 'TARGET', '${yearM1Real?default("")}', '${yearM2Real?default("")}')>
                    <#assign valActM1=yearMValues(item, 1, 'CONSUNTIVO', '${yearM1Real?default("")}', '${yearM2Real?default("")}')>
                </#if>
                <#if isMonitor=="Y" && referenceDate?has_content>
                    <#assign valBud=referenceDateBudActValues(item, 'TARGET')>
                    <#assign valAct=referenceDateBudActValues(item, 'CONSUNTIVO')>
                </#if>
                <#if yearReal?has_content>
                    <#assign valBudP0=yearRealValues(item, 'TARGET')>
                    <#assign valActP0=yearRealValues(item, 'CONSUNTIVO')>
                </#if>
                <#if yearP1Real?has_content>
                    <#assign valBudP1=yearPValues(item, 1, 'TARGET', '${yearP1Real?default("")}', '${yearP2Real?default("")}', '${yearP3Real?default("")}')>
                    <#assign valActP1=yearPValues(item, 1, 'CONSUNTIVO', '${yearP1Real?default("")}', '${yearP2Real?default("")}', '${yearP3Real?default("")}')>
                </#if>
                <#if yearP2Real?has_content>
                    <#assign valBudP2=yearPValues(item, 2, 'TARGET', '${yearP1Real?default("")}', '${yearP2Real?default("")}', '${yearP3Real?default("")}')>
                    <#assign valActP2=yearPValues(item, 2, 'CONSUNTIVO', '${yearP1Real?default("")}', '${yearP2Real?default("")}', '${yearP3Real?default("")}')>
                </#if>
                <#if yearP3Real?has_content>
                    <#assign valBudP3=yearPValues(item, 3, 'TARGET', '${yearP1Real?default("")}', '${yearP2Real?default("")}', '${yearP3Real?default("")}')>
                    <#assign valActP3=yearPValues(item, 3, 'CONSUNTIVO', '${yearP1Real?default("")}', '${yearP2Real?default("")}', '${yearP3Real?default("")}')>
                </#if>
            
                <tr <#if index%2 != 0>class="alternate-row"</#if>>
                    <td rowspan="2">
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
                        <!-- end: bug 3937 -->
                        <#assign shortDescription = StringUtil.wrapString(description?default(""))>
                        <#if shortDescription?length &gt; 150>
                            <#assign shortDescription = StringUtil.wrapString(shortDescription?substring(0,150))+"...">
                        </#if>
                        <div class="mblShowDescription" <#if description?has_content && description?length &gt; 150> description="${StringUtil.wrapString(description)}"</#if>>
                            <input type="hidden" name="userLoginId" value="${(userLogin.userLoginId)?if_exists}"/>
                            <input type="hidden" name="workEffortId" value="${item.workEffortId?if_exists}"/>
                            <input type="hidden" name="workEffortMeasureId" value="${item.workEffortMeasureId?if_exists}"/>
                            <span class="title_indicator" style="cursor: pointer; color: #307bff; font-weight: bold;">${StringUtil.wrapString(shortDescription?if_exists)}</span>
                        </div>
                        <#-- <#assign accountDescription = StringUtil.wrapString(item.accountDescription?default(""))>
                        <#if accountDescription?length &gt; 150>
                            <#assign accountDescription = StringUtil.wrapString(accountDescription?substring(0,150))+"...">
                        </#if>
                        <div style="padding-top: 7px; font-style: italic;" class="mblShowAccountDescription" <#if item.accountDescription?has_content && item.accountDescription?length &gt; 150> description="${StringUtil.wrapString(item.accountDescription)}"</#if>>${StringUtil.wrapString(accountDescription?if_exists)}</div> -->
                    </td>
                    <td rowspan="2" style="width: 55px;">
                        <div>
                            <div class="mblDisplayCenteredCell">${StringUtil.wrapString(item.abbreviation?if_exists)}</div>
                        </div>
                    </td>
                    <td style="width: 65px;">
                        <div>
                            ${(descrizioniValori.budgetDesc)?if_exists}
                        </div>
                    </td>
                    <#if yearM2Real?has_content>
                    <td style="width: 60px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comBudM2?has_content> mblShowComBudM2</#if>" <#if item.comBudM2?has_content> description="${item.comBudM2}"</#if>><#if valBudM2?has_content>${valBudM2?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if yearM1Real?has_content>
                    <td style="width: 60px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comBudM1?has_content> mblShowComBudM1</#if>" <#if item.comBudM1?has_content> description="${item.comBudM1}"</#if>><#if valBudM1?has_content>${valBudM1?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if isMonitor=="Y" && referenceDate?has_content>
                    <td style="width: 60px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comBud?has_content> mblShowComBud</#if>" <#if item.comBud?has_content> description="${item.comBud}"</#if>><#if valBud?has_content>${valBud?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if yearReal?has_content>
                    <td style="width: 60px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comBudP0?has_content> mblShowComBudP0</#if>" <#if item.comBudP0?has_content> description="${item.comBudP0}"</#if>><#if valBudP0?has_content>${valBudP0?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if yearP1Real?has_content>
                    <td style="width: 60px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comBudP1?has_content> mblShowComBudP1</#if>" <#if item.comBudP1?has_content> description="${item.comBudP1}"</#if>><#if valBudP1?has_content>${valBudP1?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if yearP2Real?has_content>
                    <td style="width: 60px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comBudP2?has_content> mblShowComBudP2</#if>" <#if item.comBudP2?has_content> description="${item.comBudP2}"</#if>><#if valBudP2?has_content>${valBudP2?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if yearP3Real?has_content>
                    <td style="width: 60px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comBudP3?has_content> mblShowComBudP3</#if>" <#if item.comBudP3?has_content> description="${item.comBudP3}"</#if>><#if valBudP3?has_content>${valBudP3?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <td rowspan="2" style="padding: 1px !important; width: 80px;">
                        <div class="mblDisplayCenteredCell<#if item.perfAmountCalc?has_content && (item.perfAmountCalc != 0.0 || item.emoticonContentId?has_content)> showEmoticonValue</#if>"<#if item.perfAmountCalc?has_content && (item.perfAmountCalc != 0.0 || item.emoticonContentId?has_content)> description="${StringUtil.wrapString(item.perfAmountCalc?string("#,##0.###"))}"</#if>>
                            <@queryEmoticon id=item.emoticonContentId?default("")/> 
                        </div>
                    </td>
                </tr>   
                <tr <#if index%2 != 0>class="alternate-row"</#if>>
                    <td style="width: 65px;">
                        <div>
                            <div>${(descrizioniValori.actualDesc)?if_exists}</div>
                        </div>
                    </td>
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
                    <#if isMonitor=="Y" && referenceDate?has_content>
                    <td style="width: 62px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comAct?has_content> mblShowComAct</#if>" <#if item.comAct?has_content> description="${item.comAct}"</#if>><#if valAct?has_content>${valAct?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if yearReal?has_content>
                    <td style="width: 62px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comActP0?has_content> mblShowComActP0</#if>" <#if item.comActP0?has_content> description="${item.comActP0}"</#if>><#if valActP0?has_content>${valActP0?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if yearP1Real?has_content>
                    <td style="width: 62px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comActP1?has_content> mblShowComActP1</#if>" <#if item.comActP1?has_content> description="${item.comActP1}"</#if>><#if valActP1?has_content>${valActP1?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if yearP2Real?has_content>
                    <td style="width: 62px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comActP2?has_content> mblShowComActP2</#if>" <#if item.comActP2?has_content> description="${item.comActP2}"</#if>><#if valActP2?has_content>${valActP2?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                    <#if yearP3Real?has_content>
                    <td style="width: 62px;">
                        <div>
                            <div class="mblDisplayCenteredCell<#if item.comActP3?has_content> mblShowComActP3</#if>" <#if item.comActP3?has_content> description="${item.comActP3}"</#if>><#if valActP3?has_content>${valActP3?string}</#if></div>
                        </div>
                    </td>
                    </#if>
                </tr>
                <#assign index = index+1> 
            </#list>
        </tbody>
     </table>
