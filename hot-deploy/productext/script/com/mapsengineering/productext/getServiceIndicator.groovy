import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;

import java.util.Calendar;


def formatAggregator = { cachedAggregator ->
	cachedAggregator.formula = (UtilValidate.isNotEmpty(cachedAggregator.measureComments) ? cachedAggregator.measureComments : cachedAggregator.accountDescription);
	cachedAggregator.fonte = (UtilValidate.isNotEmpty(cachedAggregator.dmiDescription) ? cachedAggregator.dmiDescription : cachedAggregator.dgDescription);
	
	def decimalScale = UtilValidate.isNotEmpty(cachedAggregator.decimalScale) ? cachedAggregator.decimalScale : 0;
	def um = cachedAggregator.abbreviation;
	
	def pattern = "#,##0";
	if (decimalScale > 0) {
		pattern += ".";
		
		while (pattern.length() < 6+decimalScale) {
			pattern += "0";
		}
	}
	if ("%".equals(um)) {
		pattern += "%";
	}
	
	if (UtilValidate.isNotEmpty(cachedAggregator.valueIndicator)) {
		cachedAggregator.valueIndicator = UtilFormatOut.formatDecimalNumber(cachedAggregator.valueIndicator, pattern, locale);
	}
	if (UtilValidate.isNotEmpty(cachedAggregator.valueIndicator1)) {
		cachedAggregator.valueIndicator1 = UtilFormatOut.formatDecimalNumber(cachedAggregator.valueIndicator1, pattern, locale);
	}
	if (UtilValidate.isNotEmpty(cachedAggregator.valueIndicator2)) {
		cachedAggregator.valueIndicator2 = UtilFormatOut.formatDecimalNumber(cachedAggregator.valueIndicator2, pattern, locale);
	}
	if (UtilValidate.isNotEmpty(cachedAggregator.valueIndicator3)) {
		cachedAggregator.valueIndicator3 = UtilFormatOut.formatDecimalNumber(cachedAggregator.valueIndicator3, pattern, locale);
	}
	if (UtilValidate.isNotEmpty(cachedAggregator.targetAnnuale)) {
		cachedAggregator.targetAnnuale = UtilFormatOut.formatDecimalNumber(cachedAggregator.targetAnnuale, pattern, locale);
	}
	if (UtilValidate.isNotEmpty(cachedAggregator.scostamento)) {
		cachedAggregator.scostamento = UtilFormatOut.formatDecimalNumber(cachedAggregator.scostamento, pattern, locale);
	}
	
	return cachedAggregator;
}

def productId = parameters.productId;
def yearIndicator = ObjectType.simpleTypeConvert(parameters.yearIndicator.replace('+', ' '), "java.sql.Timestamp", UtilDateTime.getDateTimeFormat(locale), locale);

def serviceIndicatorList = delegator.findList("ServiceIndicator", EntityCondition.makeCondition("productId", productId), null, null, null, true);

def returnList = [];
def returnMap = ServiceUtil.returnSuccess();
returnMap.serviceIndicatorList = returnList;

try {
	if (UtilValidate.isNotEmpty(serviceIndicatorList)) {
		def cachedId = "";
		def cachedAggregator = null;
		
		def initialAggregatorValue = [:];
		
		def currentEndYear = UtilDateTime.getYearEnd(yearIndicator, timeZone, locale);
		currentEndYear = UtilDateTime.getDayStart(currentEndYear, timeZone, locale);
		
		def currentEndYearCalendar=UtilDateTime.toCalendar(currentEndYear);
		
		currentEndYearCalendar.add(Calendar.YEAR, -1);
		def minusOneEndYear = new Timestamp(currentEndYearCalendar.getTimeInMillis());
		minusOneEndYear = UtilDateTime.getDayStart(minusOneEndYear, timeZone, locale);
		
		currentEndYearCalendar.add(Calendar.YEAR, -1);
		def minusTwoEndYear = new Timestamp(currentEndYearCalendar.getTimeInMillis());
		minusTwoEndYear = UtilDateTime.getDayStart(minusTwoEndYear, timeZone, locale);
		
		currentEndYearCalendar.add(Calendar.YEAR, -1);
		def minusThreeEndYear = new Timestamp(currentEndYearCalendar.getTimeInMillis());
		minusThreeEndYear = UtilDateTime.getDayStart(minusThreeEndYear, timeZone, locale);
		
		serviceIndicatorList.eachWithIndex { serviceIndicator, i ->
			def currentCacheId = serviceIndicator.nomeServizio + "_" + serviceIndicator.prospettivaCittadino + "_" + serviceIndicator.indicatoreDescrizione + "_" + serviceIndicator.um + "_" + (UtilValidate.isNotEmpty(serviceIndicator.measureComments) ? serviceIndicator.measureComments : serviceIndicator.accountDescription) + "_" + (UtilValidate.isNotEmpty(serviceIndicator.dmiDescription) ? serviceIndicator.dmiDescription : serviceIndicator.dgDescription) + "_" + serviceIndicator.note;
			if (!currentCacheId.equals(cachedId)) {
				if (UtilValidate.isNotEmpty(cachedAggregator)) {
					if (cachedAggregator.valueIndicator > 0.0 || cachedAggregator.targetAnnuale > 0.0) {
						if (UtilValidate.isNotEmpty(cachedAggregator.targetAnnuale)) {
							cachedAggregator.scostamento = cachedAggregator.targetAnnuale;
						}
						if (UtilValidate.isNotEmpty(cachedAggregator.valueIndicator)) {
							if (UtilValidate.isNotEmpty(cachedAggregator.scostamento)) {
								cachedAggregator.scostamento -= cachedAggregator.valueIndicator;
							} else {
								cachedAggregator.scostamento = -cachedAggregator.valueIndicator;
							}
						}
					}
					
					cachedAggregator = formatAggregator(cachedAggregator);
					
					returnList.add(cachedAggregator);
				}
				
				cachedAggregator = serviceIndicator.getAllFields();
				cachedAggregator.putAll(initialAggregatorValue);
			}
			
			if (UtilValidate.isNotEmpty(serviceIndicator.amount) && serviceIndicator.amount != 0) {
				Debug.log("................. GET SERVICES INDICATOR");
				if ("ACTUAL".equals(serviceIndicator.glFiscalTypeId)) {
					if (serviceIndicator.transactionDate == yearIndicator) {
						if (UtilValidate.isNotEmpty(cachedAggregator.valueIndicator)) {
							cachedAggregator.valueIndicator += serviceIndicator.amount;
						} else {
							cachedAggregator.valueIndicator = serviceIndicator.amount;
						}
					} else if (serviceIndicator.transactionDate == minusOneEndYear) {
						if (UtilValidate.isNotEmpty(cachedAggregator.valueIndicator1)) {
							cachedAggregator.valueIndicator1 += serviceIndicator.amount;
						} else {
							cachedAggregator.valueIndicator1 = serviceIndicator.amount;
						}
					} else if (serviceIndicator.transactionDate == minusTwoEndYear) {
						if (UtilValidate.isNotEmpty(cachedAggregator.valueIndicator2)) {
							cachedAggregator.valueIndicator2 += serviceIndicator.amount;
						} else {
							cachedAggregator.valueIndicator2 = serviceIndicator.amount;
						}
					} else if (serviceIndicator.transactionDate == minusThreeEndYear) {
						if (UtilValidate.isNotEmpty(cachedAggregator.valueIndicator3)) {
							cachedAggregator.valueIndicator3 += serviceIndicator.amount;
						} else {
							cachedAggregator.valueIndicator3 = serviceIndicator.amount;
						}
					}
				} else if ("BUDGET".equals(serviceIndicator.glFiscalTypeId)) {
					if (serviceIndicator.transactionDate == currentEndYear) {
						if (UtilValidate.isNotEmpty(cachedAggregator.targetAnnuale)) {
							cachedAggregator.targetAnnuale += serviceIndicator.amount;
						} else {
							cachedAggregator.targetAnnuale = serviceIndicator.amount;
						}
					}
				}
			}
			
			if (i == serviceIndicatorList.size() - 1) {
				if (UtilValidate.isNotEmpty(cachedAggregator.targetAnnuale)) {
					cachedAggregator.scostamento = cachedAggregator.targetAnnuale;
				}
				if (UtilValidate.isNotEmpty(cachedAggregator.valueIndicator)) {
					if (UtilValidate.isNotEmpty(cachedAggregator.scostamento)) {
						cachedAggregator.scostamento -= cachedAggregator.valueIndicator;
					} else {
						cachedAggregator.scostamento = -cachedAggregator.valueIndicator;
					}
				}
				
				cachedAggregator = formatAggregator(cachedAggregator);
				
				returnList.add(cachedAggregator);
			}
			
			cachedId = currentCacheId;
		}
	}
} catch (Exception e) {
	Debug.logError(e, "");
	returnMap = ServiceUtil.returnError(e.getMessage());
}

return returnMap;