import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import com.mapsengineering.workeffortext.util.LevelUoPartyExtractor;

/**
 * Nel folder delle relazioni si hanno considerazioni sul orgUnitId possibile (parameters.assocLevelSameUO, parameters.assocLevelParentUO, parameters.assocLevelChildUO, eventuale we.orgUnitId)
 */

/**
* gestione dei params assocLevel
*/
def orgUnitId = UtilValidate.isNotEmpty(parameters.orgUnitId) ? parameters.orgUnitId : 
	               (UtilValidate.isNotEmpty(context.parentWe) ? context.parentWe.orgUnitId : "");
def orgUnitRoleTypeId = UtilValidate.isNotEmpty(parameters.orgUnitRoleTypeId) ? parameters.orgUnitRoleTypeId : 
  (UtilValidate.isNotEmpty(context.parentWe) ? context.parentWe.orgUnitRoleTypeId : "");	

LevelUoPartyExtractor levelUoPartyExtractor = new LevelUoPartyExtractor(delegator, orgUnitId, orgUnitRoleTypeId);
levelUoPartyExtractor.initLevelSameUO(parameters.get("assocLevelSameUO"), parameters.get("assocLevelSameUOAss"));
levelUoPartyExtractor.initLevelParentUO(parameters.get("assocLevelParentUO"), parameters.get("assocLevelParentUOAss"));
levelUoPartyExtractor.initLevelChildUO(parameters.get("assocLevelChildUO"), parameters.get("assocLevelChildUOAss"));
levelUoPartyExtractor.initLevelSisterUO(parameters.get("assocLevelSisterUO"), parameters.get("assocLevelSisterUOAss"));
levelUoPartyExtractor.initLevelTopUO(parameters.get("assocLevelTopUO"), parameters.get("assocLevelTopUOAss"));
levelUoPartyExtractor.run();
if(levelUoPartyExtractor.isLevelUO()) {
	def orgUnitIdList = levelUoPartyExtractor.getOrgUnitIdList();
    context.orgUnitIdListAssoc = StringUtil.join(orgUnitIdList, ",");
    context.orgUnitIdList = orgUnitIdList;
	parameters.sortField = "sourceReferenceId|weEtch";
}
if(levelUoPartyExtractor.isLevelUOAss()) {
	def wepaPartyIdList = levelUoPartyExtractor.getWepaPartyIdList();
    context.wepaPartyIdListAssoc = StringUtil.join(wepaPartyIdList, ",");
    context.wepaPartyIdList = wepaPartyIdList;
	parameters.sortField = "sourceReferenceId|weEtch";
}
