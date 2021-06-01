import java.util.Comparator;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.TimeZone;

// Debug.log(" - executeChildPerformFindTransRadioButtonIndicator.groovy ");

//TODO  Recuperrato
context.isObiettivo = parameters.isObiettivo;

/** context.showTooltip sovrascritto coi valori di default */
context.showTooltip = "N"
context.showKpiScore = "Y";

context.titleField = "uomRatingValue";
uom = delegator.findOne("Uom", ["uomId" : 'OTH_SCO'], false);
context.scoreDecimalScale = uom.decimalScale;

Debug.log(" - executeChildPerformFindTransRadioButtonIndicator.groovy ");

/** Recupero Dati Generali */
GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindTransIndicator.groovy", context);
if (UtilValidate.isNotEmpty(parameters.errorLoadTransDescr)) {
    return;
}

Debug.log(" - Parameters: uomId = " + context.uomId + " with titleField = " + context.titleField + " and showTooltip = " + context.showTooltip);

/** il glFiscalType.description trovato e' inserito nel contesto per mostarlo a video */

/** Recupero Periodo Rilevabile */
def prilConditionList = [];
prilConditionList.add(EntityCondition.makeCondition("workEffortTypeId", context.workEffortView.workEffortTypeRootId));
prilConditionList.add(EntityCondition.makeCondition("customTimePeriodId", context.customTimePeriodId));
prilConditionList.add(EntityCondition.makeCondition("glFiscalTypeEnumId", context.glFiscalTypeEnumId));
prilConditionList.add(EntityCondition.makeCondition("organizationId", context.defaultOrganizationPartyId));
def prilList = delegator.findList("WorkEffortTypePeriod", EntityCondition.makeCondition(prilConditionList), null, null, null, false);

context.isRil = true;
for(pril in prilList) {
    context.isRil = context.prilStatusSet.contains(pril.statusEnumId) ? true : false;
}

uom = delegator.findOne("Uom", ["uomId" : context.uomId], false);

if (UtilValidate.isEmpty(context.uomId) || UtilValidate.isEmpty(uom) || "RATING_SCALE" != uom.uomTypeId || UtilValidate.isEmpty(context.titleField)) {
	def uiLabelMap = UtilProperties.getResourceBundleMap("WorkeffortExtErrorLabels", locale);
	parameters.errorLoadTrans = uiLabelMap.ErrorLoadTrans;
	parameters.errorLoadTransDescr = uiLabelMap.ErrorLoadTransDescr_uomRatingValue;
	return;
}

uomRatingScaleList = [];    
uomRatingScaleListTmp = delegator.findList("UomRatingScale", EntityCondition.makeCondition("uomId", context.uomId), null, UtilMisc.toList("uomRatingValue"), null, false);
uomRatingScaleListTmp.each { gv ->
    uomRatingValueId = String.valueOf(gv.uomRatingValue)
    
    uomRatingValue = [:];
    uomRatingValue.put("title", gv.get(context.titleField));
    if ("description".equals(context.titleField)) {
    	uomRatingValue.put("titleLang", gv.get("descriptionLang"));
    } else {
    	uomRatingValue.put("titleLang", gv.get(context.titleField));
    }
    uomRatingValue.put("valore", gv.uomRatingValue);
    uomRatingValue.put("id", uomRatingValueId); 
    uomRatingScaleList.add(uomRatingValue);
}
context.uomRatingScaleList = uomRatingScaleList;
