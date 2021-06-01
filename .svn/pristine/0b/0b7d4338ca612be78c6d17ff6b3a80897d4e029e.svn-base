import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def workEffortTypeId = parameters.workEffortTypeId;
def debitCreditFlag = parameters.debitCreditFlag;
def glFiscalTypeList = [];
def workEffortList = [];
def transactionPanelMap = [:];
def rowTotal = [:];
def columnTotal = [:];
def chapterBudget = [:];
def chapterBudgetTotal;
def cdcBudgetTotal;
def currentStatus = null;
def currentStatusIdOtherResp = null;
def currentStatusIdOtherRespAllEquals = true;
def denyStatusChange = false;
def denyStatusChangeMsg = null;

//-------------------------------------------------------------------
// Commons

uiLabelMap = UtilProperties.getResourceBundleMap("WorkeffortExtUiLabels", context.locale, context);

def getLabelBySuffix(propNamePrefix, suffix) {
	def propName = propNamePrefix + "_" + suffix;
	def propValue = uiLabelMap.get(propName);
	if (UtilValidate.isEmpty(propValue) || propValue.equals(propName)) {
		propValue = uiLabelMap.get(propNamePrefix);
	}
	return propValue;
}

def concatLine(str, line) {
    if (UtilValidate.isEmpty(str)) {
        str = line;
    } else {
		str += "\n" + line;
    }
    return str;
}

//-------------------------------------------------------------------
// Verifica se l'utente e'
// - RSCAP : responsabile della spesa
// - DSCAP : delegato della spesa
// - RSCDC : responsabile del centro gestore
// - DSCDC : delegato del centro gestore

def checkUserResponsible(partyId, roleTypeId) {
  def respCond = [];
  respCond.add(EntityCondition.makeCondition("partyIdFrom", partyId));
  if (roleTypeId instanceof String) {
      respCond.add(EntityCondition.makeCondition("partyRelationshipTypeId", roleTypeId));
  } else {
      respCond.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN, roleTypeId));
  }
  respCond.add(EntityCondition.makeCondition("thruDate", null));
  respCond.add(EntityCondition.makeCondition("partyIdTo", context.userLogin.partyId));
  def respRelations = delegator.findList("PartyRelationship", EntityCondition.makeCondition(respCond), null, null, null, false);
  return UtilValidate.isNotEmpty(respRelations);
}

userRespMap = [
	"RSCAP": checkUserResponsible(parameters.cdcResponsibleId, "ORG_RESPONSIBLE")
	, "DSCAP": checkUserResponsible(parameters.cdcResponsibleId, "ORG_DELEGATE")
	, "RSCDC": checkUserResponsible(parameters.partyId, "ORG_RESPONSIBLE")
	, "DSCDC": checkUserResponsible(parameters.partyId, "ORG_DELEGATE")
];

Debug.log("*** userRespMap = " + userRespMap);

//-------------------------------------------------------------------

def validGlFiscalTypes = delegator.findList("GlFiscalType", EntityCondition.makeCondition("glFiscalTypeEnumId", "GLFISCTYPE_TARGETFIN"), null, null, null, false);

if (UtilValidate.isEmpty(context.firstStatus)) {
	context.firstStatus = EntityUtil.getFirst(delegator.findList("StatusItem", EntityCondition.makeCondition("statusTypeId", "BDG_STS"), null, ["sequenceId"], null, false));
}

// Ottengo la lista dei capitoli
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/getGlAccountListForBudgetting.groovy", context)
def glAccountList = context.glAccountList;

workEffortList = context.workEffortList;

// Trovo la data di fine periodo aperto
def workEffortTypePeriod = context.workEffortTypePeriod;
def customTimePeriod = delegator.findOne("CustomTimePeriod", ["customTimePeriodId": workEffortTypePeriod.customTimePeriodId], false);

//-------------------------------------------------------------------

for (glAccount in glAccountList) {
	def glAccountTotal;
	transactionPanelMap[glAccount.glAccountId] = [];

	if (glFiscalTypeList.size() == 0) {
		//5214 punto 3
		glFiscalTypeList = delegator.findList("GlAccountTypeGlFiscalTypeView", EntityCondition.makeCondition("glAccountTypeId", glAccount.glAccountTypeId), null, ["sequenceId"], null, false);
	}

    // Posso gestire solo quelle di mia responsabilita
	def myResponsability = (glAccount.respCenterId == parameters.cdcResponsibleId);

	for(workEffort in workEffortList) {
		//Creo la misura se non esiste gia
		def workEffortMeasureId;
		def wemList = delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortId", workEffort.workEffortId),
			EntityCondition.makeCondition("glAccountId", glAccount.glAccountId)), null, null, null, false);
		if(UtilValidate.isEmpty(wemList)) {
			def localParameters = [:];
			localParameters.workEffortId = workEffort.workEffortId;
			localParameters.glAccountId = glAccount.glAccountId;
			localParameters._AUTOMATIC_PK_ = "Y";
			localParameters.periodTypeId = customTimePeriod.periodTypeId;
			localParameters.weMeasureTypeEnumId = "WEMT_FINANCIAL";
			localParameters.weScoreRangeEnumId = "WESCORE_ISVALUE";
			localParameters.weScoreConvEnumId = "WECONVER_NOCONVERSIO";
			localParameters.weAlertRuleEnumId = "WEALERT_TARGETUP";
			localParameters.weWithoutPerf = "WEWITHPERF_PERF_0";
			localParameters.weOtherGoalEnumId = "WEMOMG_NONE";

			def res = dispatcher.runSync("crudServiceDefaultOrchestration_WorkEffortMeasure", ["parameters": localParameters, "userLogin": context.userLogin, "operation": "CREATE", "entityName": "WorkEffortMeasure"]);
			workEffortMeasureId = res.id.workEffortMeasureId;
		}
		else {
			workEffortMeasureId = EntityUtil.getFirst(wemList).workEffortMeasureId;
		}
		for(glFiscalType in glFiscalTypeList) {
			def dataMap = [:];
			dataMap.weTransWeId = workEffort.workEffortId;
			dataMap.weTransAccountId = glAccount.glAccountId;
			dataMap.weTransTypeValueId = glFiscalType.glFiscalTypeId;
			dataMap.customTimePeriodId = workEffortTypePeriod.customTimePeriodId;
			dataMap.weTransMeasureId = workEffortMeasureId;
			dataMap.glFiscalTypeEnumId = glFiscalType.glFiscalTypeEnumId;

			if ("CLOSE".equals(workEffortTypePeriod.statusEnumId) || !myResponsability) {
                dataMap.isReadOnly = "Y";
            }

			def transConditionList = [];
			transConditionList.add(EntityCondition.makeCondition("weTransWeId", dataMap.weTransWeId));
			transConditionList.add(EntityCondition.makeCondition("weTransAccountId", dataMap.weTransAccountId));
			transConditionList.add(EntityCondition.makeCondition("weTransMeasureId", dataMap.weTransMeasureId));
			transConditionList.add(EntityCondition.makeCondition("customTimePeriodId", dataMap.customTimePeriodId));
			transConditionList.add(EntityCondition.makeCondition("weTransTypeValueId", dataMap.weTransTypeValueId));
			transConditionList.add(EntityCondition.makeCondition("weTransWorkEffortSnapshotId", null));
			
			Debug.log(" Find movement with following condition " + transConditionList);
	        def transValue = EntityUtil.getFirst(delegator.findList("WorkEffortTransactionSimplifiedView", EntityCondition.makeCondition(transConditionList), null, null, null, false));
			if (UtilValidate.isNotEmpty(transValue)) {

				// Debug.log("*** currentStatus = " + (currentStatus != null ? currentStatus.statusId : null) + " transValue.groupStatusId = " + transValue.groupStatusId);

				if (transValue.groupStatusId == null) {
				    transValue.groupStatusId = context.firstStatus.statusId;
				}

				dataMap.weTransId = transValue.weTransId;
				dataMap.weTransEntryId = transValue.weTransEntryId;
				dataMap.weTransValue = transValue.weTransValue;
				dataMap.groupStatusId = transValue.groupStatusId;
				
				if(UtilValidate.isNotEmpty(transValue.weTransComment) || UtilValidate.isNotEmpty(transValue.weTransComments) || UtilValidate.isNotEmpty(transValue.weTransCommentLang) || UtilValidate.isNotEmpty(transValue.weTransCommentsLang)) {
					dataMap.hasComments = "Y";
				}

				if (UtilValidate.isNotEmpty(transValue.groupStatusId)) {

					// GN-674 Considera solo i movimenti modificabili.
					if (!"Y".equals(dataMap.isReadOnly)) {
					    // GN-646 Lo stato attuale globale e' quello con valore di sequenza minimo.
						if (currentStatus == null || !transValue.groupStatusId.equals(currentStatus.statusId)) {
							def currentStatusTrans = delegator.findOne("StatusItem", ["statusId": transValue.groupStatusId], false);
							if (currentStatus == null || currentStatusTrans.sequenceId.compareTo(currentStatus.sequenceId) < 0) {
							    currentStatus = currentStatusTrans;
							    Debug.log("*** currentStatus=" + currentStatus);
							}
						}
					} else if (currentStatusIdOtherRespAllEquals) {
						// GN-674 Verifica se i movimenti non gestibili sono tutti nello stesso stato.
					    if (currentStatusIdOtherResp == null) {
							currentStatusIdOtherResp = transValue.groupStatusId;
						    Debug.log("*** currentStatusIdOtherResp = " + currentStatusIdOtherResp);
					    } else if (!currentStatusIdOtherResp.equals(transValue.groupStatusId)) {
					        currentStatusIdOtherRespAllEquals = false;
						    Debug.log("*** currentStatusIdOtherResp != " + transValue.groupStatusId);
					    }
					}
				}

				if("BUDGET".equals(glFiscalType.glFiscalTypeId)) {
					if(UtilValidate.isEmpty(glAccountTotal)) {
						glAccountTotal = transValue.weTransValue;
					}
					else {
						glAccountTotal += transValue.weTransValue;
					}
				}
				
				if(UtilValidate.isEmpty(rowTotal[glAccount.glAccountId + glFiscalType.glFiscalTypeId])) {
					rowTotal[glAccount.glAccountId + glFiscalType.glFiscalTypeId] = transValue.weTransValue;
				}
				else {
					rowTotal[glAccount.glAccountId + glFiscalType.glFiscalTypeId] += transValue.weTransValue;
				}
				
				if(UtilValidate.isEmpty(columnTotal[workEffort.workEffortId + glFiscalType.glFiscalTypeId])) {
					columnTotal[workEffort.workEffortId + glFiscalType.glFiscalTypeId] = transValue.weTransValue;
				}
				else {
					columnTotal[workEffort.workEffortId + glFiscalType.glFiscalTypeId] += transValue.weTransValue;
				}
				
				if(UtilValidate.isEmpty(columnTotal[glFiscalType.glFiscalTypeId])) {
					columnTotal[glFiscalType.glFiscalTypeId] = transValue.weTransValue;
				}
				else {
					columnTotal[glFiscalType.glFiscalTypeId] += transValue.weTransValue;
				}
				
				if(myResponsability && UtilValidate.isEmpty(rowTotal[glFiscalType.glFiscalTypeId])) {
					rowTotal[glFiscalType.glFiscalTypeId] = transValue.weTransValue;
				}
				else if(myResponsability){
					rowTotal[glFiscalType.glFiscalTypeId] += transValue.weTransValue;
				}
			}
            
			transactionPanelMap[glAccount.glAccountId].add(dataMap);
		}
	}
	
	// Bilancio definitivo, colonna Stanziato per capitolo
	// GN-673
	//   partyId e' il Centro di Responsabilita'
	//   entryPartyId e' il Centro di Costo
	if ("Y".equals(parameters.finalBudget)) {
		def chBdgCond = [];
		chBdgCond.add(EntityCondition.makeCondition("entryGlAccountId", glAccount.glAccountId));
		chBdgCond.add(EntityCondition.makeCondition("partyId", parameters.cdcResponsibleId));
		chBdgCond.add(EntityCondition.makeCondition("entryPartyId", parameters.partyId));
		chBdgCond.add(EntityCondition.makeCondition("transactionDate", customTimePeriod.thruDate));
        chBdgCond.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(validGlFiscalTypes, "glFiscalTypeId", false)));
        chBdgCond.add(EntityCondition.makeCondition("entryWorkEffortSnapshotId", null));
		chBdgCond.add(EntityCondition.makeCondition("workEffortSnapshotId", null));
		Debug.log(" Find movement with following condition " + chBdgCond);
        def chBdg = EntityUtil.getFirst(delegator.findList("AcctgTransAndEntriesView", EntityCondition.makeCondition(chBdgCond), null, null, null, false));
		if (UtilValidate.isNotEmpty(chBdg)) {
			chapterBudget[glAccount.glAccountId] = chBdg.entryAmount;
			if (UtilValidate.isEmpty(chapterBudgetTotal)) {
				chapterBudgetTotal = chBdg.entryAmount;
			} else {
				chapterBudgetTotal += chBdg.entryAmount;
			}
		}
		
		if (UtilValidate.isNotEmpty(glAccountTotal) && glAccountTotal != chapterBudgetTotal) {
			for (item in transactionPanelMap[glAccount.glAccountId]) {
				if ("BUDGET".equals(item.weTransTypeValueId)) {
					item.alertFlag = "Y";
				}
			}
		}
	}
}

Debug.log(" validGlFiscalTypes " + validGlFiscalTypes);

// Stanziato per CDC
// GN-673
//   partyId e' il Centro di Responsabilita'
//   entryPartyId e' il Centro di Costo
if (UtilValidate.isNotEmpty(validGlFiscalTypes)) {
    def entryGlAccountId = "D".equals(debitCreditFlag) ? "CDC_SPESA" : "CDC_ENTRATA";
    def cdcBudgetCondition = [];
    cdcBudgetCondition.add(EntityCondition.makeCondition("entryGlAccountId", entryGlAccountId));
    cdcBudgetCondition.add(EntityCondition.makeCondition("partyId", parameters.cdcResponsibleId));
    cdcBudgetCondition.add(EntityCondition.makeCondition("entryPartyId", parameters.partyId));
    cdcBudgetCondition.add(EntityCondition.makeCondition("transactionDate", customTimePeriod.thruDate));
    cdcBudgetCondition.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(validGlFiscalTypes, "glFiscalTypeId", false)));
    cdcBudgetCondition.add(EntityCondition.makeCondition("entryDebitCreditFlag", debitCreditFlag));
    cdcBudgetCondition.add(EntityCondition.makeCondition("entryWorkEffortSnapshotId", null));
    cdcBudgetCondition.add(EntityCondition.makeCondition("workEffortSnapshotId", null));
	Debug.log(" Find movement with following condition " + cdcBudgetCondition);
    def cdcBudgetTotalGVList = delegator.findList("AcctgTransAndEntriesView", EntityCondition.makeCondition(cdcBudgetCondition), null, null, null, false);
    def cdcBudgetTotalGV = EntityUtil.getFirst(cdcBudgetTotalGVList);
    // Debug.log(" - cdcBudgetTotalGV: " + cdcBudgetTotalGV);
    if (UtilValidate.isNotEmpty(cdcBudgetTotalGV)) {
		cdcBudgetTotal = cdcBudgetTotalGV.entryAmount;

		// GN-646
		// Nella proposta di bilancio:
		//   In caso di spesa: il valore stanziato del budget proposto, non deve essere maggiore del budget da bilancio.
		//   In caso di entrata: il valore stanziato del budget proposto, non deve essere minore del budget da bilancio.
		if (!"Y".equals(parameters.finalBudget)) {
			def cdcBudgetStanziato = rowTotal["STANZIATO"];
			if (UtilValidate.isNotEmpty(cdcBudgetStanziato)) {
				if ("D".equals(debitCreditFlag)) {
					if (cdcBudgetStanziato > cdcBudgetTotal) {
					    denyStatusChange = true;
					    denyStatusChangeMsg = uiLabelMap.BudgetStatusDebitChangeNotPermitted;
					}
				} else if ("C".equals(debitCreditFlag)) {
					if (cdcBudgetStanziato < cdcBudgetTotal) {
					    denyStatusChange = true;
					    denyStatusChangeMsg = uiLabelMap.BudgetStatusCreditChangeNotPermitted;
					}
				}
			}
		}
    }
}

//Nel bilancio definitivo il passaggio di stato non deve essere permesso se lo stanziato per capitolo e' maggiore
// del suo budget o se il buget è null, non vale per le entrate
if ("Y".equals(parameters.finalBudget) && !"C".equals(debitCreditFlag)) {
    for (glAccount in glAccountList) {
        for (glFiscalType in glFiscalTypeList) {
            if ("STANZIATO" == glFiscalType.glFiscalTypeId) {
                if ( (UtilValidate.isNotEmpty(rowTotal[glAccount.glAccountId + glFiscalType.glFiscalTypeId]) && UtilValidate.isNotEmpty(chapterBudget[glAccount.glAccountId])
                   && rowTotal[glAccount.glAccountId + glFiscalType.glFiscalTypeId] > chapterBudget[glAccount.glAccountId]) 
                   || (UtilValidate.isEmpty(chapterBudget[glAccount.glAccountId]) && UtilValidate.isNotEmpty(rowTotal[glAccount.glAccountId + glFiscalType.glFiscalTypeId])) ) {
                	denyStatusChange = true;
                	denyStatusChangeMsg = uiLabelMap.BudgetProposalTable_Chapter + " " + glAccount.accountCode;
                	denyStatusChangeMsg += " " + glFiscalType.description;
                	denyStatusChangeMsg += "\n : " + uiLabelMap.BudgetStatusDebitChangeNotPermitted;
                    break;
                }
            }
        }
    }
}

//-------------------------------------------------------------------
// Status Change Validation

if (currentStatus == null) {
    currentStatus = context.firstStatus;
}
if (currentStatusIdOtherRespAllEquals && currentStatusIdOtherResp != null) {
    currentStatusIdOtherRespAllEquals = currentStatusIdOtherResp.equals(currentStatus.statusId);
}

def checkStatusValidChangeConditionByPermission(statusItem, msg, propNamePrefix, userRespEntry) {
	if (msg != null && statusItem.conditionExpression.contains(userRespEntry.key)) {
	    if (userRespEntry.value) {
			msg = null;
	    } else {
			msg = concatLine(msg, getLabelBySuffix(propNamePrefix, userRespEntry.key));
	    }
    }
	return msg;
}

def checkStatusValidChangeConditionForCDC(statusItem, msg, currentStatusIdOtherRespAllEquals) {
	// Verifica se lo stato di tutti i movimenti non gestibili e' uguale allo stato iniziale dei gestibili.
    if (statusItem.conditionExpression.contains("RSCDC") || statusItem.conditionExpression.contains("DSCDC")) {
    	statusItem.allowUpdateOtherResp = currentStatusIdOtherRespAllEquals;
		if (!statusItem.allowUpdateOtherResp) {
            msg = concatLine(msg, uiLabelMap.BudgetStatusChangeNotPermitted_CDC_allStatusNotEqual);
        }
    }
    return msg;
}

def checkStatusValidChangeCondition(statusItem, currentStatusIdOtherRespAllEquals) {
    def propNamePrefix = "BudgetStatusChangeNotPermitted";
    def msg = "";
    // Se almeno una condizione e' verificata, il messaggio viene messo nullo.
    for (def userRespEntry in userRespMap) {
        msg = checkStatusValidChangeConditionByPermission(statusItem, msg, propNamePrefix, userRespEntry);
    }
    msg = checkStatusValidChangeConditionForCDC(statusItem, msg, currentStatusIdOtherRespAllEquals);
	return msg;
}

nextStatusList = [];
if (currentStatus != null) {
    def nextStatusFilter = [ statusId: currentStatus.statusId ];
    def nextStatusListOrig = delegator.findByAnd("StatusValidChangeToDetail", nextStatusFilter, [ "sequenceId" ]);
    for (nextStatusItemOrig in nextStatusListOrig) {
	    def nextStatusItem = [:];
	    nextStatusItem.putAll(nextStatusItemOrig);
		if (!denyStatusChange && UtilValidate.isNotEmpty(nextStatusItem.conditionExpression)) {
		    nextStatusItem.denyStatusChangeMsg = checkStatusValidChangeCondition(nextStatusItem, currentStatusIdOtherRespAllEquals);
		}
	    nextStatusList.add(nextStatusItem);
    }
}

//-------------------------------------------------------------------
// Results

context.glFiscalTypeList = glFiscalTypeList;
context.workEffortList = workEffortList;
context.transactionPanelMap = transactionPanelMap;
context.rowTotal = rowTotal;
context.columnTotal = columnTotal;
context.chapterBudget = chapterBudget;
context.chapterBudgetTotal = chapterBudgetTotal;
context.cdcBudgetTotal = cdcBudgetTotal;
context.denyStatusChange = denyStatusChange;
context.denyStatusChangeMsg = denyStatusChangeMsg;
context.currentStatus = currentStatus;
context.currentStatusIdOtherRespAllEquals = currentStatusIdOtherRespAllEquals;
context.nextStatusList = nextStatusList;
context.workEffortTypeId = workEffortTypeId;
