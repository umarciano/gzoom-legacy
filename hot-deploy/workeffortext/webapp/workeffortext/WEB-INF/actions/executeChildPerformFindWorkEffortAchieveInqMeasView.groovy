import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def formatCumulator = { cumulator ->
	def uomTypeId = cumulator.uomTypeId;
	
	if (!"RATING_SCALE".equals(uomTypeId)) {
		def decimalScale = UtilValidate.isNotEmpty(cumulator.decimalScale) ? cumulator.decimalScale : 0;
		def um = UtilValidate.isNotEmpty(cumulator.abbreviation) ? cumulator.abbreviation : "";
		
		def pattern = "#,##0";
		if (decimalScale > 0) {
			pattern += ".";
			
			while (pattern.length() < 6+decimalScale) {
				pattern += "0";
			}
		}
		
		if (UtilValidate.isNotEmpty(cumulator.valBud)) {
			cumulator.valBud = UtilFormatOut.formatDecimalNumber(cumulator.valBud, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valBud += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valBudP0)) {
			cumulator.valBudP0 = UtilFormatOut.formatDecimalNumber(cumulator.valBudP0, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valBudP0 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valBudP1)) {
			cumulator.valBudP1 = UtilFormatOut.formatDecimalNumber(cumulator.valBudP1, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valBudP1 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valBudP2)) {
			cumulator.valBudP2 = UtilFormatOut.formatDecimalNumber(cumulator.valBudP2, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valBudP2 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valBudP3)) {
			cumulator.valBudP3 = UtilFormatOut.formatDecimalNumber(cumulator.valBudP3, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valBudP3 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valBudP4)) {
			cumulator.valBudP4 = UtilFormatOut.formatDecimalNumber(cumulator.valBudP4, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valBudP4 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valBudM1)) {
			cumulator.valBudM1 = UtilFormatOut.formatDecimalNumber(cumulator.valBudM1, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valBudM1 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valBudM2)) {
			cumulator.valBudM2 = UtilFormatOut.formatDecimalNumber(cumulator.valBudM2, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valBudM2 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valBudM3)) {
			cumulator.valBudM3 = UtilFormatOut.formatDecimalNumber(cumulator.valBudM3, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valBudM3 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valBudM4)) {
			cumulator.valBudM4 = UtilFormatOut.formatDecimalNumber(cumulator.valBudM4, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valBudM4 += "%";
			}
		}
		
		if (UtilValidate.isNotEmpty(cumulator.valAct)) {
			cumulator.valAct = UtilFormatOut.formatDecimalNumber(cumulator.valAct, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valAct += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valActP0)) {
			cumulator.valActP0 = UtilFormatOut.formatDecimalNumber(cumulator.valActP0, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valActP0 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valActP1)) {
			cumulator.valActP1 = UtilFormatOut.formatDecimalNumber(cumulator.valActP1, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valActP1 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valActP2)) {
			cumulator.valActP2 = UtilFormatOut.formatDecimalNumber(cumulator.valActP2, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valActP2 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valActP3)) {
			cumulator.valActP3 = UtilFormatOut.formatDecimalNumber(cumulator.valActP3, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valActP3 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valActP4)) {
			cumulator.valActP4 = UtilFormatOut.formatDecimalNumber(cumulator.valActP4, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valActP4 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valActM1)) {
			cumulator.valActM1 = UtilFormatOut.formatDecimalNumber(cumulator.valActM1, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valActM1 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valActM2)) {
			cumulator.valActM2 = UtilFormatOut.formatDecimalNumber(cumulator.valActM2, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valActM2 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valActM3)) {
			cumulator.valActM3 = UtilFormatOut.formatDecimalNumber(cumulator.valActM3, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valActM3 += "%";
			}
		}
		if (UtilValidate.isNotEmpty(cumulator.valActM4)) {
			cumulator.valActM4 = UtilFormatOut.formatDecimalNumber(cumulator.valActM4, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valActM4 += "%";
			}
		}
		
		if (UtilValidate.isNotEmpty(cumulator.valBenP0)) {
			cumulator.valBenP0 = UtilFormatOut.formatDecimalNumber(cumulator.valBenP0, pattern, locale);
			if ("%".equals(um) || um.toLowerCase().indexOf("perc") != -1) {
				cumulator.valBenP0 += "%";
			}
		}
		
		if (UtilValidate.isNotEmpty(cumulator.scoreBud)) {
			cumulator.scoreBudFormatted = UtilFormatOut.formatDecimalNumber(cumulator.scoreBud, pattern, locale);
		}
		if (UtilValidate.isNotEmpty(cumulator.perfAmountCalc)) {
			cumulator.scoreActFormatted = UtilFormatOut.formatDecimalNumber(cumulator.perfAmountCalc, pattern, locale);
		} 
	}
	
	return cumulator;
}

def localContext = context;
localContext.workEffortId = parameters.workEffortId;
localContext.workEffortAnalysisId = parameters.workEffortAnalysisId;
context.listIt = GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWorkEffortAchieveInqMeasViewOnlyList.groovy", localContext);

if (UtilValidate.isNotEmpty(context.listIt)) {
	def valueItemMap = [:];
	def valueItemList = [];
	def glResourceTypeDescriptionCounterMap = [:];
	context.listIt.each { item ->
		def key = item.description + "_" + item.accountName + "_" + (UtilValidate.isNotEmpty(item.accountDescription) ? (item.accountDescription + "_") : "") + item.detailEnumId + "_" + item.uomDescr + "_" + item.abbreviation + "_" + item.uomTypeId + "_" + (UtilValidate.isNotEmpty(item.uomRangeScoreId) ? (item.uomRangeScoreId + "_") : "") + item.workEffortMeasureId + "_" + item.glResourceTypeId + "_" + item.accountCode;
		key = Base64.base64Encode(key);
		def cumulator = valueItemMap[key];
		if (UtilValidate.isEmpty(cumulator)) {
			cumulator = [:];
			cumulator.putAll(item);
			valueItemMap[key] = cumulator;
			valueItemList.add(cumulator);
			
			def glResourceTypeDescriptionCounter = glResourceTypeDescriptionCounterMap[cumulator.glResourceTypeId];
			if (UtilValidate.isEmpty(glResourceTypeDescriptionCounter)) {
				glResourceTypeDescriptionCounterMap[cumulator.glResourceTypeId] = 1;
			} else {
				glResourceTypeDescriptionCounterMap[cumulator.glResourceTypeId] += 1;
			}
		}
		
		if (UtilValidate.isNotEmpty(item.amount)) {
			if (!"SCOREKPI".equals(item.glAccountId)) {
				if (item.typeBalanceTarIndId.equals(item.glFiscalTypeId)) {
					if ((item.referenceDate.compareTo(item.thruDate) <= 0 && item.transactionDate.equals(item.referenceDate)) || (item.referenceDate.compareTo(item.thruDate) > 0 && item.transactionDate.equals(item.referenceDate))) {
						if (UtilValidate.isEmpty(cumulator.valBud) || cumulator.valBud.compareTo(item.amount) < 0) {
							cumulator.valBud = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codBud) || cumulator.codBud.compareTo(item.uomCode) < 0) {
							cumulator.codBud = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comBud) || cumulator.comBud.compareTo(item.indicatorDescription) < 0) {
							cumulator.comBud = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearPrev)) {
						if (UtilValidate.isEmpty(cumulator.valBudP0) || cumulator.valBudP0.compareTo(item.amount) < 0) {
							cumulator.valBudP0 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codBudP0) || cumulator.codBudP0.compareTo(item.uomCode) < 0) {
							cumulator.codBudP0 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comBudP0) || cumulator.comBudP0.compareTo(item.indicatorDescription) < 0) {
							cumulator.comBudP0 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearP1Prev)) {
						if (UtilValidate.isEmpty(cumulator.valBudP1) || cumulator.valBudP1.compareTo(item.amount) < 0) {
							cumulator.valBudP1 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codBudP1) || cumulator.codBudP1.compareTo(item.uomCode) < 0) {
							cumulator.codBudP1 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comBudP1) || cumulator.comBudP1.compareTo(item.indicatorDescription) < 0) {
							cumulator.comBudP1 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearP2Prev)) {
						if (UtilValidate.isEmpty(cumulator.valBudP2) || cumulator.valBudP2.compareTo(item.amount) < 0) {
							cumulator.valBudP2 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codBudP2) || cumulator.codBudP2.compareTo(item.uomCode) < 0) {
							cumulator.codBudP2 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comBudP2) || cumulator.comBudP2.compareTo(item.indicatorDescription) < 0) {
							cumulator.comBudP2 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearP3Prev)) {
						if (UtilValidate.isEmpty(cumulator.valBudP3) || cumulator.valBudP3.compareTo(item.amount) < 0) {
							cumulator.valBudP3 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codBudP3) || cumulator.codBudP3.compareTo(item.uomCode) < 0) {
							cumulator.codBudP3 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comBudP3) || cumulator.comBudP3.compareTo(item.indicatorDescription) < 0) {
							cumulator.comBudP3 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearP4Prev)) {
						if (UtilValidate.isEmpty(cumulator.valBudP4) || cumulator.valBudP4.compareTo(item.amount) < 0) {
							cumulator.valBudP4 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codBudP4) || cumulator.codBudP4.compareTo(item.uomCode) < 0) {
							cumulator.codBudP4 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comBudP4) || cumulator.comBudP4.compareTo(item.indicatorDescription) < 0) {
							cumulator.comBudP4 = item.indicatorDescription;
						}
					}
					
					
					if (item.transactionDate.equals(item.yearM1Prev)) {
						if (UtilValidate.isEmpty(cumulator.valBudM1) || cumulator.valBudM1.compareTo(item.amount) < 0) {
							cumulator.valBudM1 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codBudM1) || cumulator.codBudM1.compareTo(item.uomCode) < 0) {
							cumulator.codBudM1 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comBudM1) || cumulator.comBudM1.compareTo(item.indicatorDescription) < 0) {
							cumulator.comBudM1 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearM2Prev)) {
						if (UtilValidate.isEmpty(cumulator.valBudM2) || cumulator.valBudM2.compareTo(item.amount) < 0) {
							cumulator.valBudM2 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codBudM2) || cumulator.codBudM2.compareTo(item.uomCode) < 0) {
							cumulator.codBudM2 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comBudM2) || cumulator.comBudM2.compareTo(item.indicatorDescription) < 0) {
							cumulator.comBudM2 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearM3Prev)) {
						if (UtilValidate.isEmpty(cumulator.valBudM3) || cumulator.valBudM3.compareTo(item.amount) < 0) {
							cumulator.valBudM3 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codBudM3) || cumulator.codBudM3.compareTo(item.uomCode) < 0) {
							cumulator.codBudM3 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comBudM3) || cumulator.comBudM3.compareTo(item.indicatorDescription) < 0) {
							cumulator.comBudM3 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearM4Prev)) {
						if (UtilValidate.isEmpty(cumulator.valBudM4) || cumulator.valBudM4.compareTo(item.amount) < 0) {
							cumulator.valBudM4 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codBudM4) || cumulator.codBudM4.compareTo(item.uomCode) < 0) {
							cumulator.codBudM4 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comBudM4) || cumulator.comBudM4.compareTo(item.indicatorDescription) < 0) {
							cumulator.comBudM4 = item.indicatorDescription;
						}
					}
					
				} else if (item.typeBalanceConsIndId.equals(item.glFiscalTypeId)) {
	                if ((item.referenceDate.compareTo(item.thruDate) <= 0 && item.transactionDate.equals(item.referenceDate)) || (item.referenceDate.compareTo(item.thruDate) > 0 && item.transactionDate.equals(item.referenceDate))) {
						if (UtilValidate.isEmpty(cumulator.valAct) || cumulator.valAct.compareTo(item.amount) < 0) {
							cumulator.valAct = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codAct) || cumulator.codAct.compareTo(item.uomCode) < 0) {
							cumulator.codAct = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comAct) || cumulator.comAct.compareTo(item.indicatorDescription) < 0) {
							cumulator.comAct = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearReal)) {
						if (UtilValidate.isEmpty(cumulator.valActP0) || cumulator.valActP0.compareTo(item.amount) < 0) {
							cumulator.valActP0 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codActP0) || cumulator.codActP0.compareTo(item.uomCode) < 0) {
							cumulator.codActP0 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comActP0) || cumulator.comActP0.compareTo(item.indicatorDescription) < 0) {
							cumulator.comActP0 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearP1Real)) {
						if (UtilValidate.isEmpty(cumulator.valActP1) || cumulator.valActP1.compareTo(item.amount) < 0) {
							cumulator.valActP1 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codActP1) || cumulator.codActP1.compareTo(item.uomCode) < 0) {
							cumulator.codActP1 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comActP1) || cumulator.comActP1.compareTo(item.indicatorDescription) < 0) {
							cumulator.comActP1 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearP2Real)) {
						if (UtilValidate.isEmpty(cumulator.valActP2) || cumulator.valActP2.compareTo(item.amount) < 0) {
							cumulator.valActP2 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codActP2) || cumulator.codActP2.compareTo(item.uomCode) < 0) {
							cumulator.codActP2 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comActP2) || cumulator.comActP2.compareTo(item.indicatorDescription) < 0) {
							cumulator.comActP2 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearP3Real)) {
						if (UtilValidate.isEmpty(cumulator.valActP3) || cumulator.valActP3.compareTo(item.amount) < 0) {
							cumulator.valActP3 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codActP3) || cumulator.codActP3.compareTo(item.uomCode) < 0) {
							cumulator.codActP3 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comActP3) || cumulator.comActP3.compareTo(item.indicatorDescription) < 0) {
							cumulator.comActP3 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearP4Real)) {
						if (UtilValidate.isEmpty(cumulator.valActP4) || cumulator.valActP4.compareTo(item.amount) < 0) {
							cumulator.valActP4 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codActP4) || cumulator.codActP4.compareTo(item.uomCode) < 0) {
							cumulator.codActP4 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comActP4) || cumulator.comActP4.compareTo(item.indicatorDescription) < 0) {
							cumulator.comActP4 = item.indicatorDescription;
						}
					}
					
					
					
					if (item.transactionDate.equals(item.yearM1Real)) {
						if (UtilValidate.isEmpty(cumulator.valActM1) || cumulator.valActM1.compareTo(item.amount) < 0) {
							cumulator.valActM1 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codActM1) || cumulator.codActM1.compareTo(item.uomCode) < 0) {
							cumulator.codActM1 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comActM1) || cumulator.comActM1.compareTo(item.indicatorDescription) < 0) {
							cumulator.comActM1 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearM2Real)) {
						if (UtilValidate.isEmpty(cumulator.valActM2) || cumulator.valActM2.compareTo(item.amount) < 0) {
							cumulator.valActM2 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codActM2) || cumulator.codActM2.compareTo(item.uomCode) < 0) {
							cumulator.codActM2 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comActM2) || cumulator.comActM2.compareTo(item.indicatorDescription) < 0) {
							cumulator.comActM2 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearM3Real)) {
						if (UtilValidate.isEmpty(cumulator.valActM3) || cumulator.valActM3.compareTo(item.amount) < 0) {
							cumulator.valActM3 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codActM3) || cumulator.codActM3.compareTo(item.uomCode) < 0) {
							cumulator.codActM3 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comActM3) || cumulator.comActM3.compareTo(item.indicatorDescription) < 0) {
							cumulator.comActM3 = item.indicatorDescription;
						}
					}
					
					if (item.transactionDate.equals(item.yearM4Real)) {
						if (UtilValidate.isEmpty(cumulator.valActM4) || cumulator.valActM4.compareTo(item.amount) < 0) {
							cumulator.valActM4 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codActM4) || cumulator.codActM4.compareTo(item.uomCode) < 0) {
							cumulator.codActM4 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comActM4) || cumulator.comActM4.compareTo(item.indicatorDescription) < 0) {
							cumulator.comActM4 = item.indicatorDescription;
						}
					}
				} /*else if ("BENCH".equals(item.glFiscalTypeId)) {
					if (item.transactionDate.equals(item.yearPrev)) {
						if (UtilValidate.isEmpty(cumulator.valBenP0) || cumulator.valBenP0.compareTo(item.amount) < 0) {
							cumulator.valBenP0 = item.amount;
						}
						if (UtilValidate.isEmpty(cumulator.codBenP0) || cumulator.codBenP0.compareTo(item.uomCode) < 0) {
							cumulator.codBenP0 = item.uomCode;
						}
						if (UtilValidate.isEmpty(cumulator.comBenP0) || cumulator.comBenP0.compareTo(item.indicatorDescription) < 0) {
							cumulator.comBenP0 = item.indicatorDescription;
						}
					}
				}*/
			} else {
				if (item.typeBalanceScoreTarId.equals(item.glFiscalTypeId)) {
					if (UtilValidate.isEmpty(cumulator.scoreBud) || cumulator.scoreBud.compareTo(item.amount) < 0) {
						cumulator.scoreBud = item.amount;
					}
				} else if (item.typeBalanceScoreConId.equals(item.glFiscalTypeId)) {
					if (UtilValidate.isEmpty(cumulator.scoreAct) || cumulator.scoreAct.compareTo(item.amount) < 0) {
						cumulator.scoreAct = item.amount;
					}
					if (item.perfAmountCalc != 0.0 ) {
						cumulator.perfAmountCalc = item.perfAmountCalc;
					}
					if (UtilValidate.isEmpty(cumulator.scoreAlert) || cumulator.scoreAlert.compareTo(item.hasScoreAlert) < 0) {
						cumulator.scoreAlert = item.hasScoreAlert;
					}
				}
			}
		}
	}
	
	/*if (UtilValidate.isNotEmpty(valueItemList)) {
		def localList = [];
		valueItemList.each { item ->
			localList.add(formatCumulator(item));
		}
		valueItemList = localList;
	}*/
	context.listIt = valueItemList;
	context.glResourceTypeDescriptionCounterMap = glResourceTypeDescriptionCounterMap;
	
	//aggiungo la descrizione bug 4202
	def descrizioniValori = [:];
	
	/** Prendo il campo typeBalanceScoreTarId e typeBalanceScoreConId per calcolo dell'immagine */
	def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId" : parameters.workEffortAnalysisId], false);
	if(UtilValidate.isNotEmpty(workEffortAnalysis)){
		actualDesc =  delegator.findOne("GlFiscalType", org.ofbiz.base.util.UtilMisc.toMap("glFiscalTypeId", workEffortAnalysis.typeBalanceConsIndId), false);
		if(UtilValidate.isNotEmpty(actualDesc)){
			descrizioniValori.actualDesc = actualDesc.description;
		}
		budgetDesc =  delegator.findOne("GlFiscalType", org.ofbiz.base.util.UtilMisc.toMap("glFiscalTypeId", workEffortAnalysis.typeBalanceTarIndId), false);
		if(UtilValidate.isNotEmpty(budgetDesc)){
			descrizioniValori.budgetDesc = budgetDesc.description;
		}
	}	
	/*
	benchDesc =  delegator.findOne("GlFiscalType", org.ofbiz.base.util.UtilMisc.toMap("glFiscalTypeId", "BENCH"), true);
	if(UtilValidate.isNotEmpty(benchDesc)){
		descrizioniValori.benchDesc = benchDesc.description;
	}*/
	
	context.descrizioniValori = descrizioniValori;
	
}