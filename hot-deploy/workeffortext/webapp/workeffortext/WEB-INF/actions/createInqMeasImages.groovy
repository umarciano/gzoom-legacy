import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

public class InqMeasComparator implements Comparator<Map> {
    @Override
    public int compare(Map o1, Map o2) {
        int res = 0;
        if (UtilValidate.isNotEmpty(o1.get("uomDescr")) && UtilValidate.isNotEmpty(o2.get("uomDescr"))) {
            res = o1.get("uomDescr").compareTo(o2.get("uomDescr"));
        }
        return res;
    }
}

def loadBirtImages(id) {
	res = "";
	if (id!=null) {
		content = delegator.findOne("Content", org.ofbiz.base.util.UtilMisc.toMap("contentId", id), true);
		if (content!=null) {
			dataResource = delegator.findOne("DataResource", org.ofbiz.base.util.UtilMisc.toMap("dataResourceId", content.dataResourceId), true);
			if (dataResource!=null) {
				res = dataResource.objectInfo;
			}
		}
	}
	return res;
}


//
// Creo le immagini dei tachimetri e le associo ai relativi workEffortId nella lista
//
workEffortAchieveInqMeasList = javolution.util.FastList.newInstance();


//
// calcolo previousDate
//
previuosDateList = delegator.find("AcctgTransMaxDate", null, null, null, null, null);
previuosDate =  EntityUtil.getFirst(previuosDateList.getCompleteList());
try {
	previuosDateList.close();
} catch (Throwable e) {
}

if (UtilValidate.isNotEmpty(context.listIt)) {
	
	for (GenericValue item: context.listIt) {
		
		//
		// Genero la mappa per i campi con tutti i campi del genericvalue
		//
		Map fieldsMap = null;
		
		if (item instanceof GenericValue)
			fieldsMap = item.getAllFields();
		else
			fieldsMap = item;
		
		//
		// calcolo contentid per emoticon
		//
		String emoticonContentId = null;
		double score = 0.0;		
		if (UtilValidate.isNotEmpty(item.get("scoreAct"))) {			
			//GN-378
			String uomRangeScoreId = UtilValidate.isEmpty(item.get("weUomRangeScoreId")) ? item.get("uomRangeScoreId") : item.get("weUomRangeScoreId");
			
			if (UtilValidate.isNotEmpty(item.get("scoreBud")) && item.get("scoreBud") != 0) {		
				score = (Double)item.get("scoreBud") - (Double)item.get("scoreAct");
				emoticonContentId = com.mapsengineering.workeffortext.widgets.ValutationWidgets.getEmoticonContentId(delegator,  uomRangeScoreId, score);
			} else {				
				score = (Double)item.get("scoreAct");
				emoticonContentId = com.mapsengineering.workeffortext.widgets.ValutationWidgets.getEmoticonContentId(delegator,  uomRangeScoreId, score);
			}
		}
		fieldsMap.put("emoticonContentId", emoticonContentId);
		
		//
		// genero contentid per trend 
		//
		String trendContentId = null;
		if (UtilValidate.isNotEmpty(previuosDate)) {
			trendContentId = com.mapsengineering.workeffortext.widgets.ValutationWidgets.getTrendContentId(delegator, item, previuosDate);
		}
		fieldsMap.put("trendContentId", trendContentId);
		
		//
		// Per birt, a differenza del browser devo passare il percoso completo
		//
		emoticonSrc = loadBirtImages(emoticonContentId);
		trendSrc = loadBirtImages(trendContentId);
		
		fieldsMap.put("emoticonSrc", emoticonSrc);
		fieldsMap.put("trendSrc", trendSrc);
		
		//
		// Calcolo dei valori da mostrare
		//
		String uomDescr = "";
		String budValue = "";
		String actValue = "";
    	if ("RATING_SCALE".equalsIgnoreCase(item.get("uomTypeId"))) {
//    		uomDescr = "";
    		budValue = (String) item.get("uomCodeBud"); 
    		actValue = (String) item.get("uomCodeAct");
    	} else {
//    		uomDescr = item.get("uomDescr");
    		budValue = (String) item.get("transValueBud");
    		actValue = (String) item.get("transValueAct");
    	}
    	fieldsMap.put("uomDescr", item.get("uomDescr"));
    	fieldsMap.put("budValue", budValue);
    	fieldsMap.put("actValue", actValue);
    	
		// GN-553 punto 1
    	def workEffortMeasure = null;
		if (UtilValidate.isNotEmpty(item.workEffortMeasureId)) {			
			workEffortMeasure = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId" : item.workEffortMeasureId], true);
			def workEffortMeasureProductList = null;
			if (UtilValidate.isNotEmpty(workEffortMeasure)) {
				workEffortMeasureProductList = delegator.findList("WorkEffortMeasureProduct", EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountId", workEffortMeasure.glAccountId), EntityCondition.makeCondition("productId", workEffortMeasure.productId))
					, null, null, null, true);	
			}			
			def workEffortMeasureProduct = EntityUtil.getFirst(workEffortMeasureProductList);			
			if (UtilValidate.isNotEmpty(workEffortMeasureProduct)) {
				fieldsMap.measureProductUomDescr = workEffortMeasureProduct.uomDescr;
			} 
		}
				
		// GN-553 punto 2
    	if ("ACCDET_SUM".equals(item.detailEnumId)) {
    		ctx = context;
    		ctx.parameters.workEffortMeasureId = item.workEffortMeasureId;
        	GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/evaluateIndicatorDetailCondition.groovy", ctx);
        	fieldsMap.detailLayout = context.detailLayout;
        	def totalMap = context.totalMap;
        	def periodTypeId = (UtilValidate.isNotEmpty(workEffortMeasure) && UtilValidate.isNotEmpty(workEffortMeasure.periodTypeId)) ? 
        			            workEffortMeasure.periodTypeId : item.periodTypeId;
        	
        	//total yearReal
        	if (UtilValidate.isNotEmpty(item.yearReal)) {
        		def customTimePeriodIdYearReal = getCustomTimePeriodId(periodTypeId, item.yearReal);
        		def totalIndexYearRealTarget = item.typeBalanceTarIndId + customTimePeriodIdYearReal;
        		def totalIndexYearRealConsuntivo = item.typeBalanceConsIndId + customTimePeriodIdYearReal;
        		
        		fieldsMap.totalYearRealTarget = totalMap[totalIndexYearRealTarget];
        		fieldsMap.totalYearRealConsuntivo = totalMap[totalIndexYearRealConsuntivo];
        	}
        	//total referenceDate
        	if (UtilValidate.isNotEmpty(item.referenceDate)) {
        		def customTimePeriodIdReferenceDate = getCustomTimePeriodId(periodTypeId, item.referenceDate);
        		def totalIndexReferenceDateTarget = item.typeBalanceTarIndId + customTimePeriodIdReferenceDate;
        		def totalIndexReferenceDateConsuntivo = item.typeBalanceConsIndId + customTimePeriodIdReferenceDate;
        		
        		fieldsMap.totalReferenceDateTarget = totalMap[totalIndexReferenceDateTarget];
        		fieldsMap.totalReferenceDateConsuntivo = totalMap[totalIndexReferenceDateConsuntivo];
        	}        	
        	//total yearM1Real
        	if (UtilValidate.isNotEmpty(item.yearM1Real)) {
        		def customTimePeriodIdYearM1Real = getCustomTimePeriodId(periodTypeId, item.yearM1Real);
        		def totalIndexYearM1RealTarget = item.typeBalanceTarIndId + customTimePeriodIdYearM1Real;
        		def totalIndexYearM1RealConsuntivo = item.typeBalanceConsIndId + customTimePeriodIdYearM1Real;
        		
        		fieldsMap.totalYearM1RealTarget = totalMap[totalIndexYearM1RealTarget];
        		fieldsMap.totalYearM1RealConsuntivo = totalMap[totalIndexYearM1RealConsuntivo];
        	}        	
        	//total yearM2Real
        	if (UtilValidate.isNotEmpty(item.yearM2Real)) {
        		def customTimePeriodIdYearM2Real = getCustomTimePeriodId(periodTypeId, item.yearM2Real);
        		def totalIndexYearM2RealTarget = item.typeBalanceTarIndId + customTimePeriodIdYearM2Real;
        		def totalIndexYearM2RealConsuntivo = item.typeBalanceConsIndId + customTimePeriodIdYearM2Real;
        		
        		fieldsMap.totalYearM2RealTarget = totalMap[totalIndexYearM2RealTarget];
        		fieldsMap.totalYearM2RealConsuntivo = totalMap[totalIndexYearM2RealConsuntivo];
        	}
        	//total yearM3Real
        	if (UtilValidate.isNotEmpty(item.yearM3Real)) {
        		def customTimePeriodIdYearM3Real = getCustomTimePeriodId(periodTypeId, item.yearM3Real);
        		def totalIndexYearM3RealTarget = item.typeBalanceTarIndId + customTimePeriodIdYearM3Real;
        		def totalIndexYearM3RealConsuntivo = item.typeBalanceConsIndId + customTimePeriodIdYearM3Real;
        		
        		fieldsMap.totalYearM3RealTarget = totalMap[totalIndexYearM3RealTarget];
        		fieldsMap.totalYearM3RealConsuntivo = totalMap[totalIndexYearM3RealConsuntivo];
        	}
        	//total yearM4Real
        	if (UtilValidate.isNotEmpty(item.yearM4Real)) {
        		def customTimePeriodIdYearM4Real = getCustomTimePeriodId(periodTypeId, item.yearM4Real);
        		def totalIndexYearM4RealTarget = item.typeBalanceTarIndId + customTimePeriodIdYearM4Real;
        		def totalIndexYearM4RealConsuntivo = item.typeBalanceConsIndId + customTimePeriodIdYearM4Real;
        		
        		fieldsMap.totalYearM4RealTarget = totalMap[totalIndexYearM4RealTarget];
        		fieldsMap.totalYearM4RealConsuntivo = totalMap[totalIndexYearM4RealConsuntivo];
        	}
        	//total yearP1Real
        	if (UtilValidate.isNotEmpty(item.yearP1Real)) {
        		def customTimePeriodIdYearP1Real = getCustomTimePeriodId(periodTypeId, item.yearP1Real);
        		def totalIndexYearP1RealTarget = item.typeBalanceTarIndId + customTimePeriodIdYearP1Real;
        		def totalIndexYearP1RealConsuntivo = item.typeBalanceConsIndId + customTimePeriodIdYearP1Real;
        		
        		fieldsMap.totalYearP1RealTarget = totalMap[totalIndexYearP1RealTarget];
        		fieldsMap.totalYearP1RealConsuntivo = totalMap[totalIndexYearP1RealConsuntivo];
        	}          	
        	//total yearP2Real
        	if (UtilValidate.isNotEmpty(item.yearP2Real)) {
        		def customTimePeriodIdYearP2Real = getCustomTimePeriodId(periodTypeId, item.yearP2Real);
        		def totalIndexYearP2RealTarget = item.typeBalanceTarIndId + customTimePeriodIdYearP2Real;
        		def totalIndexYearP2RealConsuntivo = item.typeBalanceConsIndId + customTimePeriodIdYearP2Real;
        		
        		fieldsMap.totalYearP2RealTarget = totalMap[totalIndexYearP2RealTarget];
        		fieldsMap.totalYearP2RealConsuntivo = totalMap[totalIndexYearP2RealConsuntivo];
        	} 
        	//total yearP3Real
        	if (UtilValidate.isNotEmpty(item.yearP3Real)) {
        		def customTimePeriodIdYearP3Real = getCustomTimePeriodId(periodTypeId, item.yearP3Real);
        		def totalIndexYearP3RealTarget = item.typeBalanceTarIndId + customTimePeriodIdYearP3Real;
        		def totalIndexYearP3RealConsuntivo = item.typeBalanceConsIndId + customTimePeriodIdYearP3Real;
        		
        		fieldsMap.totalYearP3RealTarget = totalMap[totalIndexYearP3RealTarget];
        		fieldsMap.totalYearP3RealConsuntivo = totalMap[totalIndexYearP3RealConsuntivo];
        	} 
    	}		
		
		workEffortAchieveInqMeasList.add(fieldsMap);
	}
}

Collections.sort(workEffortAchieveInqMeasList, new InqMeasComparator());

context.workEffortAchieveInqMeasList = workEffortAchieveInqMeasList;

//Elementi che vengono caricati adesso chiamo per fare il raggruppamento e non prendo quello dentro executeChildPerformFindWorkEffortAchieveInqMeasView.groovy
if(UtilValidate.isNotEmpty(workEffortAchieveInqMeasList)){
	def valueItemMap = [:];
	def glResourceTypeDescriptionCounterMap = [:];
	workEffortAchieveInqMeasList.each { item ->
		def key = item.description + "_" + item.accountName + "_" + (UtilValidate.isNotEmpty(item.accountDescription) ? (item.accountDescription + "_") : "") + item.detailEnumId + "_" + item.uomDescr + "_" + item.abbreviation + "_" + item.uomTypeId + "_" + (UtilValidate.isNotEmpty(item.uomRangeScoreId) ? (item.uomRangeScoreId + "_") : "") + item.workEffortMeasureId + "_" + item.glResourceTypeId + "_" + item.accountCode;
		key = Base64.base64Encode(key);
		
		def cumulator = valueItemMap[key];
		if (UtilValidate.isEmpty(cumulator)) {
		
			cumulator = [:];
			cumulator.putAll(item);
			valueItemMap[key] = cumulator;
			
			def glResourceTypeDescriptionCounter = glResourceTypeDescriptionCounterMap[cumulator.glResourceTypeId];
			if (UtilValidate.isEmpty(glResourceTypeDescriptionCounter)) {
				glResourceTypeDescriptionCounterMap[cumulator.glResourceTypeId] = 1;
			} else {
				glResourceTypeDescriptionCounterMap[cumulator.glResourceTypeId] += 1;
			}
		}
	}
	context.glResourceTypeDescriptionCounterMap = glResourceTypeDescriptionCounterMap;
}


// NOTA BENE: inserisco la lista in sessione per poterla utilizzare negli script del report
session.setAttribute("workEffortAchieveInqMeasList", workEffortAchieveInqMeasList);



def getCustomTimePeriodId(periodTypeId, date) {	
	def customTimePeriodId = "";
	
    def customTimePeriodConditions = []; 
    def customTimePeriodFieldsToSelect = UtilMisc.toSet("customTimePeriodId");
    customTimePeriodConditions.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
    customTimePeriodConditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, date));
    customTimePeriodConditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, date));
    def customTimePeriodList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(customTimePeriodConditions), customTimePeriodFieldsToSelect, null, 
    		                                      null, false);
    
    def customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
    if(UtilValidate.isNotEmpty(customTimePeriod)) {
    	customTimePeriodId = customTimePeriod.customTimePeriodId;
    }
    
    
    return customTimePeriodId;
}
