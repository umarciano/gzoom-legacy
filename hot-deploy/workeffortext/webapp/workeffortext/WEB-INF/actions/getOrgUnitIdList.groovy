import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

/**
 * Nel folder delle relazioni si hanno considerazioni sul orgUnitId possibile (parameters.assocLevelSameUO, parameters.assocLevelParentUO, parameters.assocLevelChildUO, eventuale we.orgUnitId)
 */

/**
 *  obiettivi con unita responsabile padre di quella dell'obiettivo corrente
 */
def getLevelParentUoPartyIdList(orgUnitId, orgUnitRoleTypeId) {
    def levelParentUoCondList = [];    
    levelParentUoCondList.add(EntityCondition.makeCondition("partyIdTo", orgUnitId));
    levelParentUoCondList.add(EntityCondition.makeCondition("roleTypeIdTo", orgUnitRoleTypeId));
    levelParentUoCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
    levelParentUoCondList.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));     
    def levelParentUoPartyRelList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(levelParentUoCondList), null, null, null, false);
    
    return getPartyIdList(levelParentUoPartyRelList, "partyIdFrom");
}

/**
 *  obiettivi con unita responsabile figlia di quella dell'obiettivo corrente
 */
def getLevelChildUoPartyIdList(orgUnitId, orgUnitRoleTypeId) {
    def levelChildUoCondList = [];    
    levelChildUoCondList.add(EntityCondition.makeCondition("partyIdFrom", orgUnitId));
    levelChildUoCondList.add(EntityCondition.makeCondition("roleTypeIdFrom", orgUnitRoleTypeId));
    levelChildUoCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
    levelChildUoCondList.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));
    def levelChildUoPartyRelList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(levelChildUoCondList), null, null, null, false);
    
    return getPartyIdList(levelChildUoPartyRelList, "partyIdTo");
}

/**
 * obiettivi con unita responsabile sorella
 */
def getLevelSisterUoPartyIdList(orgUnitId, orgUnitRoleTypeId) {
	def levelSisterUOPartyIdList = [];
	
	def parentCondList = [];
	parentCondList.add(EntityCondition.makeCondition("partyIdTo", orgUnitId));
	parentCondList.add(EntityCondition.makeCondition("roleTypeIdTo", orgUnitRoleTypeId));
	parentCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
	parentCondList.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));
	def parentList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(parentCondList), null, null, null, false);
	
	if (UtilValidate.isNotEmpty(parentList)) {
		for (GenericValue parentItem : parentList) {
			def childCondList = [];
			childCondList.add(EntityCondition.makeCondition("partyIdFrom", parentItem.partyIdFrom));
			childCondList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.NOT_EQUAL, orgUnitId));
			childCondList.add(EntityCondition.makeCondition("roleTypeIdFrom", parentItem.roleTypeIdFrom));
			childCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
			childCondList.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));
		    def childrenList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(childCondList), null, null, null, false);
		    
		    levelSisterUOPartyIdList.addAll(getPartyIdList(childrenList, "partyIdTo"));
		}
	}
	return levelSisterUOPartyIdList;	
}

/**
 * estrazione dei party dalla partyRelationshipList
 */
def getPartyIdList(partyRelationshipList, partyField) {
	def partyIdList = [];
	if (UtilValidate.isNotEmpty(partyRelationshipList)) {
		partyIdList = EntityUtil.getFieldListFromEntityList(partyRelationshipList, partyField, true);
	} else {
		partyIdList.add("![null-field]");
	}
	return partyIdList;
}

/**
* gestione dei params assocLevel
*/
def orgUnitId = UtilValidate.isNotEmpty(parameters.orgUnitId) ? parameters.orgUnitId : 
	               (UtilValidate.isNotEmpty(context.parentWe) ? context.parentWe.orgUnitId : "");
def orgUnitRoleTypeId = UtilValidate.isNotEmpty(parameters.orgUnitRoleTypeId) ? parameters.orgUnitRoleTypeId : 
  (UtilValidate.isNotEmpty(context.parentWe) ? context.parentWe.orgUnitRoleTypeId : "");	

def orgUnitIdList = [];
def assocLevel = false;

// Debug.log("#### getOrgUnitIdList.groovy parameters.assocLevelSameUO " + parameters.get("assocLevelSameUO"));
// Debug.log("#### getOrgUnitIdList.groovy parameters.assocLevelParentUO " + parameters.get("assocLevelParentUO"));
// Debug.log("#### getOrgUnitIdList.groovy parameters.assocLevelChildUO " + parameters.get("assocLevelChildUO"));
// Debug.log("#### getOrgUnitIdList.groovy parameters.assocLevelSisterUO " + parameters.get("assocLevelSisterUO"));

if ("Y".equals(parameters.get("assocLevelSameUO"))) {
	assocLevel = true;
	orgUnitIdList.add(orgUnitId);
}
if ("Y".equals(parameters.get("assocLevelParentUO"))) {
	assocLevel = true;
	def levelParentUoPartyIdList = getLevelParentUoPartyIdList(orgUnitId, orgUnitRoleTypeId);
	if(UtilValidate.isNotEmpty(levelParentUoPartyIdList)) {
		orgUnitIdList.addAll(levelParentUoPartyIdList);
	}
}
if ("Y".equals(parameters.get("assocLevelChildUO"))) {
	assocLevel = true;
	def levelChildUoPartyIdList = getLevelChildUoPartyIdList(orgUnitId, orgUnitRoleTypeId);
	if(UtilValidate.isNotEmpty(levelChildUoPartyIdList)) {
		orgUnitIdList.addAll(levelChildUoPartyIdList);
	}
}
if ("Y".equals(parameters.get("assocLevelSisterUO"))) {
	assocLevel = true;
	def levelSisterUoPartyIdList = getLevelSisterUoPartyIdList(orgUnitId, orgUnitRoleTypeId);
	if (UtilValidate.isNotEmpty(levelSisterUoPartyIdList)) {
		orgUnitIdList.addAll(levelSisterUoPartyIdList);
	}
}
if(assocLevel) {
    context.orgUnitIdListAssoc = StringUtil.join(orgUnitIdList, ",");
    context.orgUnitIdList = orgUnitIdList;
	parameters.sortField = "sourceReferenceId|weEtch";
}
// Debug.log("#### getOrgUnitIdList.groovy context.orgUnitIdListAssoc " + context.orgUnitIdListAssoc);
