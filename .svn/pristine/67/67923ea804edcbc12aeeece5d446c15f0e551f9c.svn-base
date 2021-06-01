import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.workeffortext.widgets.*;

import java.text.*;

workEffortAchieveList = [];

def dateFormat = new SimpleDateFormat("yyyy");
def lineImagesSrc = [:];
def contextImagesPath = "images/tmp";

if(UtilValidate.isNotEmpty(parameters.workEffortAnalysisId)) {
	def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId": parameters.workEffortAnalysisId], true);
	if(UtilValidate.isNotEmpty(workEffortAnalysis)) {
		
		/** Prendo il campo typeBalanceConsIndId per calcolo dell'immagine delle linee*/
		def consuntivo = workEffortAnalysis.typeBalanceConsIndId;
		def series = [consuntivo : UtilProperties.getMessage("WorkeffortExtUiLabels", "WorkEffortAchieveViewRealized", locale)];
		
		context.isMonitor = workEffortAnalysis.isMonitor ? workEffortAnalysis.isMonitor : "N";  
		for (GenericValue item: context.workEffortAchieveInqMeasList) {
			def titles = [:];
			def values = [:];
			def previousSet = true;
			
			if(UtilValidate.isNotEmpty(item.valActM4)) {
				titles[item.yearM4Real.toString()] = dateFormat.format(item.yearM4Real);
				previousSet = true;
			}
			else {
				previousSet = false;
			}
			if(previousSet || UtilValidate.isNotEmpty(item.valActM3)) {
				titles[item.yearM3Real.toString()] = dateFormat.format(item.yearM3Real);
				previousSet = true;
			}
			else {
				previousSet = false;
			}
			if(previousSet || UtilValidate.isNotEmpty(item.valActM2)) {
				titles[item.yearM2Real.toString()] = dateFormat.format(item.yearM2Real);
				previousSet = true;
			}
			else {
				previousSet = false;
			}
			if(previousSet || UtilValidate.isNotEmpty(item.valActM1)) {
				titles[item.yearM1Real.toString()] = dateFormat.format(item.yearM1Real);
				previousSet = true;
			}
			else {
				previousSet = false;
			}
			if ("Y".equals(workEffortAnalysis.isMonitor) && (previousSet || UtilValidate.isNotEmpty(item.valAct))) {
				titles[item.referenceDate.toString()] = dateFormat.format(item.referenceDate);
			}
			
			previousSet = true;
			def m4Val;
			def m3Val;
			def m2Val;
			def m1Val;
			def m0Val;
					
			if(UtilValidate.isNotEmpty(item.valActM4)) {
				m4Val = [consuntivo: item.valActM4];
				values[item.yearM4Real.toString()] = m4Val;
				previousSet = true;
			}
			else {
				previousSet = false;
			}
			if(previousSet || UtilValidate.isNotEmpty(item.valActM3)) {
				m3Val = [consuntivo: UtilValidate.isNotEmpty(item.valActM3) ? item.valActM3 : m4Val[consuntivo]];
				values[item.yearM3Real.toString()] = m3Val;
				previousSet = true;
			}
			else {
				previousSet = false;
			}
			if(previousSet || UtilValidate.isNotEmpty(item.valActM2)) {
				m2Val = [consuntivo: UtilValidate.isNotEmpty(item.valActM2) ? item.valActM2 : m3Val[consuntivo]];
				values[item.yearM2Real.toString()] = m2Val;
				previousSet = true;
			}
			else {
				previousSet = false;
			}
			if(previousSet || UtilValidate.isNotEmpty(item.valActM1)) {
				m1Val = [consuntivo: UtilValidate.isNotEmpty(item.valActM1) ? item.valActM1 : m2Val[consuntivo]];
				values[item.yearM1Real.toString()] = m1Val;
				previousSet = true;
			}
			else {
				previousSet = false;
			}
			
			if ("Y".equals(workEffortAnalysis.isMonitor) && (previousSet || UtilValidate.isNotEmpty(item.valAct))) {
				m0Val = [consuntivo: UtilValidate.isNotEmpty(item.valAct) ? item.valAct : m1Val[consuntivo]];
				values[item.referenceDate.toString()] = m0Val;
			}
			
			if(UtilValidate.isNotEmpty(values.keySet()) && values.keySet().size() > 1) {
				def src = ValutationWidgets.buildAndSaveLine(request, contextImagesPath, series, titles, values, 450, 450, null, null, false, false);
				lineImagesSrc[item.workEffortMeasureId] = src;
			}
			
		}
	}
}

context.lineImagesSrc = lineImagesSrc;
//context.workEffortAchieveList = workEffortAchieveList;

//NOTA BENE: inserisco la lista in sessione per poterla utilizzare negli script del report
//session.setAttribute("workEffortAchieveList", workEffortAchieveList);