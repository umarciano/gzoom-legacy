import org.ofbiz.service.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.math.*;

Debug.log("******************** STARTED SERVICE getTransactionToExport ******************");

def exportedItems = [];
def precision = new MathContext(11, RoundingMode.HALF_UP);

def refDate = context.refDate;
def workEffortPurposeTypeId = context.workEffortPurposeTypeId;
def dataSourceId = context.dataSourceId;

//Debug.log("*** refDate = " + refDate);
//Debug.log("*** workEffortPurposeTypeId = " + workEffortPurposeTypeId);
//Debug.log("*** dataSourceId = " + dataSourceId);

def workEffortPurposeType = delegator.findOne("WorkEffortPurposeType", UtilMisc.toMap("workEffortPurposeTypeId", workEffortPurposeTypeId), false);

def conditionList = [];
conditionList.add(EntityCondition.makeCondition("workEffortPurposeTypeId", workEffortPurposeTypeId));
if (UtilValidate.isNotEmpty(dataSourceId)) {
	def dataSourceCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("wmDataSourceId", dataSourceId),
			EntityOperator.OR,
			EntityCondition.makeCondition(EntityCondition.makeCondition("wmDataSourceId", GenericEntity.NULL_FIELD), 
					EntityCondition.makeCondition("dataSourceId", dataSourceId)));
	conditionList.add(dataSourceCondition);
}
def fromDateCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, refDate),
	EntityOperator.OR,
	EntityCondition.makeCondition("fromDate", GenericEntity.NULL_FIELD));
conditionList.add(fromDateCondition);
def thruDateCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, refDate),
	EntityOperator.OR,
	EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));
conditionList.add(thruDateCondition);

def measureFromDateCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("measureFromDate", EntityOperator.LESS_THAN_EQUAL_TO, refDate),
    EntityOperator.OR,
    EntityCondition.makeCondition("measureFromDate", GenericEntity.NULL_FIELD));
conditionList.add(measureFromDateCondition);
def measureThruDateCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("measureThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, refDate),
    EntityOperator.OR,
    EntityCondition.makeCondition("measureThruDate", GenericEntity.NULL_FIELD));
conditionList.add(measureThruDateCondition);

def workEffortSnapshotIdCondition = EntityCondition.makeCondition("workEffortSnapshotId", GenericEntity.NULL_FIELD);
conditionList.add(workEffortSnapshotIdCondition);

def measureRowList = delegator.findList("ExportTransactionMeasureView", EntityCondition.makeCondition(conditionList), null, null, null, false);
Debug.log("Found " + measureRowList.size() + " from ExportTransactionMeasureView with condition " + EntityCondition.makeCondition(conditionList));

def it = measureRowList.iterator();
while(it.hasNext()) {
	def row = it.next();
	def exportedItem = delegator.makeValue("ExportValoriMisure");
	def wetList = [];
	
	exportedItem.codiceIndicatore = row.accountCode;
	exportedItem.titoloIndicatore = row.accountName;
	exportedItem.codiceObiettivo = row.sourceReferenceId;
	exportedItem.descrizioneObiettivo = row.workEffortName;
	exportedItem.dataRiferimento = refDate;
	exportedItem.idMisura = row.workEffortMeasureId;
    exportedItem.descrizioneMisura = row.uomDescr;
	exportedItem.finalita = workEffortPurposeTypeId;
	exportedItem.descrizioneFinalita = workEffortPurposeType.description;
	if (UtilValidate.isNotEmpty(row.dataSourceId)) {
		def dataSource = delegator.findOne("DataSource", UtilMisc.toMap("dataSourceId", row.dataSourceId), false);
		exportedItem.codiceFonte = dataSource.dataSourceId;
		exportedItem.descrizioneFonte = dataSource.description;
	}
	if (UtilValidate.isNotEmpty(row.orgUnitId) && UtilValidate.isNotEmpty(row.orgUnitRoleTypeId)) {
		def partyRoleView = delegator.findOne("PartyRoleView", UtilMisc.toMap("partyId", row.orgUnitId, "roleTypeId", row.orgUnitRoleTypeId), false);
		if(UtilValidate.isNotEmpty(partyRoleView)) {
			exportedItem.codiceUnitaOrg = partyRoleView.parentRoleCode;           
			exportedItem.descrizioneUnitaOrg = partyRoleView.partyName;
		}
	}
	
	//Provo nelle Risorse
	def glAccontConditionList = [];
	glAccontConditionList.add(EntityCondition.makeCondition("workEffortTypeIdRes", row.workEffortTypeId));
	glAccontConditionList.add(EntityCondition.makeCondition("glAccountId", row.glAccountId));
	glAccontConditionList.add(EntityCondition.makeCondition("organizationPartyId", context.defaultOrganizationPartyId));
	def glAccountList = delegator.findList("GlAccountWithWorkEffortPurposeType", EntityCondition.makeCondition(glAccontConditionList), null, null, null, false);
	if (UtilValidate.isNotEmpty(glAccountList)) {
		def transConditionList = [];
		transConditionList.add(EntityCondition.makeCondition("weTransMeasureId", row.workEffortMeasureId));
		transConditionList.add(EntityCondition.makeCondition("weTransAccountId", row.glAccountId));
		transConditionList.add(EntityCondition.makeCondition("weTransDate", refDate));
		wetList = delegator.findList("WorkEffortTransactionSimplifiedView", EntityCondition.makeCondition(transConditionList), null, null, null, false);
		
		//Debug.log("*** VALORI RISORSA TROVATI PER MISURA : " + row.workEffortMeasureId + " -> " + wetList.size());
	}
	else {
		//Provo negli indicatori
		glAccontConditionList = [];
		glAccontConditionList.add(EntityCondition.makeCondition("workEffortTypeIdInd", row.workEffortTypeId));
		glAccontConditionList.add(EntityCondition.makeCondition("glAccountId", row.glAccountId));
		glAccontConditionList.add(EntityCondition.makeCondition("organizationPartyId", context.defaultOrganizationPartyId));
		glAccountList = delegator.findList("GlAccountWithWorkEffortPurposeType", EntityCondition.makeCondition(glAccontConditionList), null, null, null, false);
		if (UtilValidate.isNotEmpty(glAccountList)) {
			def transConditionList = [];
			transConditionList.add(EntityCondition.makeCondition("weTransMeasureId", row.workEffortMeasureId));
			transConditionList.add(EntityCondition.makeCondition("weTransAccountId", row.glAccountId));
			transConditionList.add(EntityCondition.makeCondition("weTransDate", refDate));
			//transConditionList.add(EntityCondition.makeCondition("weAcctgTransAccountId", EntityOperator.EQUALS_FIELD, "weTransAccountId"));
			wetList = delegator.findList("WorkEffortTransactionIndicatorView", EntityCondition.makeCondition(transConditionList), null, null, null, false);
			
			//Debug.log("*** VALORI INDICATORI TROVATI PER MISURA : " + row.workEffortMeasureId + " -> " + wetList.size());
		}
	}
	
	def isDateMeasure = 0;
	if (UtilValidate.isNotEmpty(row.uomTypeId) && "DATE_MEASURE".equals(row.uomTypeId)) {
		isDateMeasure = 1;
	}
	def wetIt = wetList.iterator();
	while(wetIt.hasNext()) {
		def wet = wetIt.next();
		if (!"SCOREKPI".equals(wet.weAcctgTransAccountId) && !"SCORE".equals(wet.weAcctgTransAccountId)) {
			if ("ACTUAL".equals(wet.weTransTypeValueId)) {
                exportedItem.valoreConsuntivo = wet.weTransValue + isDateMeasure;
			}
			if ("BUDGET".equals(wet.weTransTypeValueId)) {
                exportedItem.valoreBudget = wet.weTransValue + isDateMeasure;
			}
			if ("ACTUAL_PY".equals(wet.weTransTypeValueId)) {
                exportedItem.valoreConsuntivoAp = wet.weTransValue + isDateMeasure;
			}
		}
	}
	
	//Cerco SCOREKPI
	def scoreKpiConditionList = [];
	scoreKpiConditionList.add(EntityCondition.makeCondition("transactionDate", refDate));
	scoreKpiConditionList.add(EntityCondition.makeCondition("workEffortMeasureId", row.workEffortMeasureId));
	def scoreKpiList = delegator.findList("WorkEffortMeasureScoreKpi", EntityCondition.makeCondition(scoreKpiConditionList), null, null, null, false);
	if (UtilValidate.isNotEmpty(scoreKpiList)) {
		def scoreKpi = EntityUtil.getFirst(scoreKpiList);
		exportedItem.valoreRisultato = scoreKpi.amount;
	}
	exportedItems.add(exportedItem);
} 

Debug.log("***************** EXPORTED ITEMS ******************");

Debug.log("******************** END SERVICE getTransactionToExport ******************");

def res = ServiceUtil.returnSuccess();
res.exportedItems = exportedItems;

return res;