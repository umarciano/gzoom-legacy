import org.ofbiz.base.util.*;
import com.mapsengineering.base.birt.util.UtilDateTime;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

import java.text.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;

def scoreKpi = null;
context.hasScoreKPI = "N";
context.showScoreKPI = "N";

context.listIt = [];
if ((!"Y".equals(parameters.insertMode) || UtilValidate.isNotEmpty(parameters.id) || "Y".equals(parameters.fromLookup)) && !"Y".equals(parameters.fromDelete) && UtilValidate.isNotEmpty(context.weTransMeasureId)) {
	if (UtilValidate.isNotEmpty(parameters.id)) {
		parameters.putAll(StringUtil.strToMap(parameters.id));
	}
	
	def workEffortMeasure = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId" : context.weTransMeasureId], false);
	if (UtilValidate.isNotEmpty(workEffortMeasure)) {
		def workEffortTransactionView = [:];
		context.listIt.add(workEffortTransactionView);
		workEffortTransactionView.customTimePeriodId = context.customTimePeriodId;
		workEffortTransactionView.weTransAccountId = workEffortMeasure.glAccountId;
		workEffortTransactionView.weTransMeasureType = workEffortMeasure.weMeasureTypeEnumId;
		workEffortTransactionView.weTransProductId = workEffortMeasure.productId;
		workEffortTransactionView.weTransEmplPosTypeId = workEffortMeasure.emplPositionTypeId;
		workEffortTransactionView.weTransUomDesc = workEffortMeasure.uomDescr;
		workEffortTransactionView.weTransOtherWorkEffortId = workEffortMeasure.otherWorkEffortId;
		workEffortTransactionView.weTransOtherMeasGoal = workEffortMeasure.weOtherGoalEnumId
		workEffortTransactionView.weTransWeId = workEffortMeasure.workEffortId;
		
		def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortMeasure.workEffortId], true);
		
		if(UtilValidate.isEmpty(parameters.isObiettivo) || !"Y".equals(parameters.isObiettivo)) {
		    /** Recupero params */
			WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
			paramsEvaluator.evaluateParams(workEffort.workEffortTypeId, parameters.contentIdInd, false);
		}
		
		def partyRole = workEffortMeasure.getRelatedOneCache("PartyRole");
		if (UtilValidate.isNotEmpty(partyRole)) {
			def roleType = partyRole.getRelatedOneCache("RoleType");
			
			workEffortTransactionView.weTransRoleDesc = roleType.description;
			workEffortTransactionView.weTransPartyId = partyRole.partyId;
		}
		//bug 4279 periodTypeId da workEffortMeasure
		def periodType = workEffortMeasure.getRelatedOneCache("PeriodType");
		if (UtilValidate.isNotEmpty(periodType)) {
			workEffortTransactionView.weTransPeriodTypeDesc = periodType.description;
			workEffortTransactionView.periodTypeId = periodType.periodTypeId;
		}
		def glAccount = workEffortMeasure.getRelatedOneCache("GlAccount");
		if (UtilValidate.isNotEmpty(glAccount)) {
			
			workEffortTransactionView.detectOrgUnitIdFlag = glAccount.detectOrgUnitIdFlag;

			/**GN-1142 Valore modificabile in base al DATA_SOURCE*/
			def dataSourceId = glAccount.dataSourceId;
			if (glAccount.inputEnumId == "ACCINP_PRD") {
				dataSourceId = workEffortMeasure.dataSourceId;
			} 
			
			def dataSource = delegator.findOne("DataSource", ["dataSourceId" : dataSourceId], true);
			if (UtilValidate.isNotEmpty(dataSource)) {
				workEffortTransactionView.valModId = dataSource.valModId;
			}
			
			workEffortTransactionView.hasCompetence = glAccount.hasCompetence;
			def uom = glAccount.getRelatedOneCache("Uom");
			if (UtilValidate.isNotEmpty(uom)) {
				workEffortTransactionView.weTransUomType = uom.uomTypeId;
				workEffortTransactionView.weTransCurrencyUomId = uom.uomId;
				workEffortTransactionView.weTransDecimalScale = uom.decimalScale;
			}
		}
        
		if (UtilValidate.isNotEmpty(parameters.weTransEntryId) && UtilValidate.isNotEmpty(parameters.weTransId)) {
			def acctgTrans = delegator.findOne("AcctgTrans", ["acctgTransId" : parameters.weTransId], false);
			if (UtilValidate.isNotEmpty(acctgTrans)) {
				workEffortTransactionView.weTransDate = acctgTrans.transactionDate;
				//bug 4279 weTransValue nascosto nella form, viene visualizzato il thruDate del periodo
				def conditionThruDateList = [];
				
				conditionThruDateList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, workEffortTransactionView.periodTypeId));
				conditionThruDateList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, workEffortTransactionView.weTransDate));
				conditionThruDateList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, workEffortTransactionView.weTransDate));				
				
				if(UtilValidate.isNotEmpty(workEffortTransactionView.periodTypeId)) {
					def customTimePeriodList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(conditionThruDateList), null, null, null, false);
					def customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
					workEffortTransactionView.customTimePeriodId = UtilValidate.isEmpty(customTimePeriod) ? "" : customTimePeriod.customTimePeriodId;
					workEffortTransactionView.weTransPeriodIsClosed = customTimePeriod.isClosed;
				}
				workEffortTransactionView.isPosted = acctgTrans.isPosted;
				workEffortTransactionView.weTransTypeValueId = acctgTrans.glFiscalTypeId;
				workEffortTransactionView.weTransComments = acctgTrans.description;
				workEffortTransactionView.weTransCommentsLang = acctgTrans.descriptionLang;
				
				def acctgTransEntry = delegator.findOne("AcctgTransEntry", ["acctgTransId" : parameters.weTransId,
					"acctgTransEntrySeqId" : parameters.weTransEntryId], true);
				
				if (UtilValidate.isNotEmpty(acctgTransEntry)) {
					def glFiscalType = delegator.getRelatedOneCache("GlFiscalType", acctgTrans);
					if(!"W".equals(parameters.insertMode)){
						if (UtilValidate.isNotEmpty(acctgTransEntry.amount)) {
							workEffortTransactionView.weTransValue = acctgTransEntry.amount;
							//GN-239 -> controllo il tipo di output che deve uscire e se è di tipo data lo converto un una data DATE_MEASURE
							if(workEffortTransactionView.weTransUomType == "DATE_MEASURE"){
								workEffortTransactionView.weTransValue = UtilDateTime.numberConvertToDate(acctgTransEntry.amount, locale);
							}						
						}
					}
					workEffortTransactionView.weTransTypeValueDesc = glFiscalType.description;
					workEffortTransactionView.weTransEntryId = acctgTransEntry.acctgTransEntrySeqId;
					workEffortTransactionView.weTransId = acctgTransEntry.acctgTransId;
					workEffortTransactionView.weTransComment = acctgTransEntry.description;
					workEffortTransactionView.weTransCommentLang = acctgTransEntry.descriptionLang;
					workEffortTransactionView.weTransProductId = acctgTransEntry.productId;
                    workEffortTransactionView.entryRoleTypeId = acctgTransEntry.roleTypeId;
                    workEffortTransactionView.entryPartyId = acctgTransEntry.partyId;
        			workEffortTransactionView.perfAmountCalc = acctgTransEntry.perfAmountCalc;
					workEffortTransactionView.origAmount = acctgTransEntry.origAmount;
   					workEffortTransactionView.amountLocked = acctgTransEntry.amountLocked;
   					
   					workEffortTransactionView.checkAmount1 = acctgTransEntry.checkAmount1;
   					workEffortTransactionView.checkAmount2 = acctgTransEntry.checkAmount2;
   					workEffortTransactionView.checkAmount3 = acctgTransEntry.checkAmount3;
   					workEffortTransactionView.textValue1 = acctgTransEntry.textValue1;
//                    workEffortTransactionView.weAcctgTransAccountId = acctgTransEntry.glAccountId;
					
					/*def uom = delegator.findOne("Uom", ["uomId": acctgTransEntry.currencyUomId], false);
					if (UtilValidate.isNotEmpty(uom)) {
						workEffortTransactionView.weTransUomType = uom.uomTypeId;
						workEffortTransactionView.weTransCurrencyUomId = uom.uomId;
						workEffortTransactionView.weTransDecimalScale = uom.decimalScale;
					}*/
					
					if (UtilValidate.isNotEmpty(acctgTransEntry.currencyUomId) && UtilValidate.isNotEmpty(acctgTransEntry.amount)) {
						// preso da uom relativo a glaccount
						//workEffortTransactionView.weTransCurrencyUomId = acctgTransEntry.currencyUomId;
						def workEffortMeasRatSc = delegator.findOne("WorkEffortMeasRatSc", ["uomId" : acctgTransEntry.currencyUomId,
							"uomRatingValue" : acctgTransEntry.amount,
							"workEffortMeasureId" : workEffortMeasure.workEffortMeasureId] , true);
						
						if (UtilValidate.isNotEmpty(workEffortMeasRatSc)) {
							def nf = NumberFormat.getNumberInstance(locale);
							def df = (DecimalFormat)nf;
							workEffortTransactionView.weTransValue = df.format(workEffortTransactionView.weTransValue);
							workEffortTransactionView.weTransValueDesc = workEffortMeasRatSc.uomDescr;
						}
					}
					if(UtilValidate.isNotEmpty(workEffortTransactionView.hasCompetence)){
						workEffortTransactionView.fromDateCompetence = acctgTransEntry.fromDateCompetence;
						workEffortTransactionView.toDateCompetence = acctgTransEntry.toDateCompetence;
					}
				}
				
				workEffortTransactionView.roleTypeId = acctgTrans.roleTypeId;
	            workEffortTransactionView.partyId = acctgTrans.partyId;
	         
	            if ("WEMT_PERF".equals(workEffortMeasure.weMeasureTypeEnumId) && UtilValidate.isNotEmpty(workEffortTransactionView.weTransWeId) && "Y".equals(context.showScoreKPI)) {
	    			def scorekpiCondList = [];
	    			scorekpiCondList.add(EntityCondition.makeCondition("workEffortId", workEffortTransactionView.weTransWeId));       
	    			scorekpiCondList.add(EntityCondition.makeCondition("transactionDate", workEffortTransactionView.weTransDate));       
	                scorekpiCondList.add(EntityCondition.makeCondition("workEffortMeasureId", workEffortMeasure.workEffortMeasureId));       
	                scorekpiCondList.add(EntityCondition.makeCondition("glFiscalTypeId", acctgTrans.glFiscalTypeId));
	                
	                scoreKpiList = delegator.findList("WorkEffortMeasureScoreKpi", EntityCondition.makeCondition(scorekpiCondList), null, null, null, false);
	                Debug.log("Found " + scoreKpiList.size() + " WorkEffortMeasureScoreKpi with condition = " + EntityCondition.makeCondition(scorekpiCondList));
		            
	                scoreKpi = EntityUtil.getFirst(scoreKpiList);
	                
	                if (UtilValidate.isNotEmpty(scoreKpi)) {
	                	workEffortTransactionView.weTransValueKpiScore = scoreKpi.amount;
	                	workEffortTransactionView.origAmountKpiScore = scoreKpi.origAmount;
		                workEffortTransactionView.amountLockedKpiScore = scoreKpi.amountLocked;
		                workEffortTransactionView.weTransCommentKpiScore = scoreKpi.weTransComment;
		                workEffortTransactionView.weTransCommentsKpiScore = scoreKpi.weTransComments;
		                workEffortTransactionView.isPostedKpiScore = scoreKpi.isPosted;
		                workEffortTransactionView.weTransEntryIdKpiScore = scoreKpi.acctgTransEntrySeqId;
						workEffortTransactionView.weTransIdKpiScore = scoreKpi.acctgTransId;
						
						if (UtilValidate.isNotEmpty(scoreKpi.trDefaultUomId)) {
							def scoreKpiUom = delegator.findOne("Uom", ["uomId" : scoreKpi.trDefaultUomId], false);
							if (UtilValidate.isNotEmpty(scoreKpiUom)) {
								workEffortTransactionView.scoreKpiUomType = scoreKpiUom.uomTypeId;
								workEffortTransactionView.scoreKpiDecimalScale = scoreKpiUom.decimalScale;								
							}
						}						
	                }
	    		}
			}
			
		} else {
			// in realta' la gestione del WEMOMG_ORG non deve essere piu' utilizzata:
            // per sapere se il movimento va memorizzato con l'unita' organizzativa del workeffort, 
            // ci si basa sul valore di detectOrgUnitIdFlag
			if("Y" == workEffortTransactionView.detectOrgUnitIdFlag){	
				Debug.log("For new transaction glAccount.detectOrgUnitIdFlag = Y so acctgTrans.[partyId, roleTypeIs] = workEffort.[orgUnitId, orgUnitRoleTypeId] ");
				def otherWorkEffort = delegator.findOne("WorkEffort", ["workEffortId" : parameters.weTransWeId], true);
        		if(UtilValidate.isNotEmpty(otherWorkEffort)) {
        			workEffortTransactionView.roleTypeId = otherWorkEffort.orgUnitRoleTypeId;
            		workEffortTransactionView.partyId = otherWorkEffort.orgUnitId;
            	}
        	}
		}
		if (context.insertByWeTransMeasureId) {
			context.putAll(workEffortTransactionView);
		}
	}
}

if(UtilValidate.isNotEmpty(scoreKpi)) {
	context.hasScoreKPI = "Y";
}
