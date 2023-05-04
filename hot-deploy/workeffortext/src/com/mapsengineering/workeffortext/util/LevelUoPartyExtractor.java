package com.mapsengineering.workeffortext.util;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

public class LevelUoPartyExtractor {
	private Delegator delegator;
	private String orgUnitId;
	private String orgUnitRoleTypeId;
	private String levelSameUO;
	private String levelSameUOAss;
	private String levelParentUO;
	private String levelParentUOAss;
	private String levelChildUO;
	private String levelChildUOAss;
	private String levelSisterUO;
	private String levelSisterUOAss;
	private String levelTopUO;
	private String levelTopUOAss;
	
	private List<String> orgUnitIdList;
	private List<String> wepaPartyIdList;
	private boolean levelUO;
	private boolean levelUOAss;
	private final String Y = "Y";
	
	/**
	 * constructor
	 * @param delegator
	 * @param orgUnitId
	 * @param orgUnitRoleTypeId
	 */
	public LevelUoPartyExtractor(Delegator delegator, String orgUnitId, String orgUnitRoleTypeId) {
		this.delegator = delegator;
		this.orgUnitId = orgUnitId;
		this.orgUnitRoleTypeId = orgUnitRoleTypeId;
		this.orgUnitIdList = new ArrayList<String>();
		this.wepaPartyIdList = new ArrayList<String>();
		this.levelUO = false;
		this.levelUOAss = false;
	}
	
	/**
	 * inizializza same level
	 * @param levelSameUO
	 * @param levelSameUOAss
	 */
	public void initLevelSameUO(String levelSameUO, String levelSameUOAss) {
		this.levelSameUO = levelSameUO;
		this.levelSameUOAss = levelSameUOAss;
	}
	
	/**
	 * inizializza parent level
	 * @param levelParentUO
	 * @param levelParentUOAss
	 */
	public void initLevelParentUO(String levelParentUO, String levelParentUOAss) {
		this.levelParentUO = levelParentUO;
		this.levelParentUOAss = levelParentUOAss;
	}
	
	/**
	 * inizializza child level
	 * @param levelChildUO
	 * @param levelChildUOAss
	 */
	public void initLevelChildUO(String levelChildUO, String levelChildUOAss) {
		this.levelChildUO = levelChildUO;
		this.levelChildUOAss = levelChildUOAss;
	}
	
	/**
	 * inizializza sister level
	 * @param levelSisterUO
	 * @param levelSisterUOAss
	 */
	public void initLevelSisterUO(String levelSisterUO, String levelSisterUOAss) {
		this.levelSisterUO = levelSisterUO;
		this.levelSisterUOAss = levelSisterUOAss;
	}
	
	/**
	 * inizializza top level
	 * @param levelTopUO
	 * @param levelTopUOAss
	 */
	public void initLevelTopUO(String levelTopUO, String levelTopUOAss) {
		this.levelTopUO = levelTopUO;
		this.levelTopUOAss = levelTopUOAss;
	}
	
	/**
	 * imposta le liste di party id
	 * @throws GenericEntityException
	 */
	public void run() throws GenericEntityException {
		if (Y.equals(levelSameUO) || Y.equals(levelSameUOAss)) {
			List<String> levelSameUoPartyIdList = getLevelSameUoPartyIdList();
			if (Y.equals(levelSameUO)) {
				levelUO = true;
				orgUnitIdList.addAll(levelSameUoPartyIdList);	
			}
			if (Y.equals(levelSameUOAss)) {
				levelUOAss = true;
				wepaPartyIdList.addAll(levelSameUoPartyIdList);
			}
		}
		if (Y.equals(levelParentUO) || Y.equals(levelParentUOAss)) {
			List<String> levelParentUoPartyIdList = getLevelParentUoPartyIdList();
			if (Y.equals(levelParentUO)) {
				levelUO = true;
				orgUnitIdList.addAll(levelParentUoPartyIdList);	
			}
			if (Y.equals(levelParentUOAss)) {
				levelUOAss = true;
				wepaPartyIdList.addAll(levelParentUoPartyIdList);
			}
		}
		if (Y.equals(levelChildUO) || Y.equals(levelChildUOAss)) {
			List<String> levelChildUoPartyIdList = getLevelChildUoPartyIdList();
			if (Y.equals(levelChildUO)) {
				levelUO = true;
				orgUnitIdList.addAll(levelChildUoPartyIdList);	
			}
			if (Y.equals(levelChildUOAss)) {
				levelUOAss = true;
				wepaPartyIdList.addAll(levelChildUoPartyIdList);
			}
		}
		if (Y.equals(levelSisterUO) || Y.equals(levelSisterUOAss)) {
			List<String> levelSisterUoPartyIdList = getLevelSisterUoPartyIdList();
			if (Y.equals(levelSisterUO)) {
				levelUO = true;
				orgUnitIdList.addAll(levelSisterUoPartyIdList);	
			}
			if (Y.equals(levelSisterUOAss)) {
				levelUOAss = true;
				wepaPartyIdList.addAll(levelSisterUoPartyIdList);
			}
		}
		if (Y.equals(levelTopUO) || Y.equals(levelTopUOAss)) {
			List<String> levelTopUoPartyIdList = getLevelTopUoPartyIdList();
			if (Y.equals(levelTopUO)) {
				levelUO = true;
				orgUnitIdList.addAll(levelTopUoPartyIdList);	
			}
			if (Y.equals(levelTopUOAss)) {
				levelUOAss = true;
				wepaPartyIdList.addAll(levelTopUoPartyIdList);
			}
		}
	}
	
	/**
	 * imposta lista con orgUnitId
	 * @return
	 */
	private List<String> getLevelSameUoPartyIdList() {
		List<String> levelSameUoPartyIdList = new ArrayList<String>();
		levelSameUoPartyIdList.add(orgUnitId);
		return levelSameUoPartyIdList;
	}
	
	/**
	 * imposta lista con unita padre
	 * @return
	 * @throws GenericEntityException
	 */
	private List<String> getLevelParentUoPartyIdList() throws GenericEntityException {  
	    List<GenericValue> levelParentUoPartyRelList = getParentList();
		return getPartyIdList(levelParentUoPartyRelList, "partyIdFrom");
	}
	
	/**
	 * imposta lista con unita figlie
	 * @return
	 * @throws GenericEntityException
	 */
	private List<String> getLevelChildUoPartyIdList() throws GenericEntityException {
		List<EntityCondition> levelChildUoCondList = new ArrayList<EntityCondition>();
	    levelChildUoCondList.add(EntityCondition.makeCondition("partyIdFrom", orgUnitId));
	    levelChildUoCondList.add(EntityCondition.makeCondition("roleTypeIdFrom", orgUnitRoleTypeId));
	    levelChildUoCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
	    levelChildUoCondList.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));
	    List<GenericValue> levelChildUoPartyRelList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(levelChildUoCondList), null, null, null, false);   
		return getPartyIdList(levelChildUoPartyRelList, "partyIdTo");
	}
	
	/**
	 * imposta lista con unita sorelle
	 * @return
	 * @throws GenericEntityException
	 */
	private List<String> getLevelSisterUoPartyIdList() throws GenericEntityException {
		List<String> levelSisterUOPartyIdList = new ArrayList<String>();
		List<GenericValue> parentList = getParentList();		
		if (UtilValidate.isNotEmpty(parentList)) {
			for (GenericValue parentItem : parentList) {
				List<EntityCondition> childCondList = new ArrayList<EntityCondition>();
				childCondList.add(EntityCondition.makeCondition("partyIdFrom", parentItem.getString("partyIdFrom")));
				childCondList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.NOT_EQUAL, orgUnitId));
				childCondList.add(EntityCondition.makeCondition("roleTypeIdFrom", parentItem.getString("roleTypeIdFrom")));
				childCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
				childCondList.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));
				List<GenericValue> childrenList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(childCondList), null, null, null, false);			    
			    levelSisterUOPartyIdList.addAll(getPartyIdList(childrenList, "partyIdTo"));
			}
		} else {
			levelSisterUOPartyIdList.add("![null-field]");
		}
		return levelSisterUOPartyIdList;		
	}
	
	/**
	 * imposta lista con utnia vertice
	 * @return
	 * @throws GenericEntityException
	 */
	private List<String> getLevelTopUoPartyIdList() throws GenericEntityException {
		List<String> levelTopUOPartyIdList = new ArrayList<String>();
		List<GenericValue> parentList = getParentList();		
		if (UtilValidate.isNotEmpty(parentList)) {
			for (GenericValue parentItem : parentList) {
				List<EntityCondition> topCondList = new ArrayList<EntityCondition>();
	    		topCondList.add(EntityCondition.makeCondition("partyIdTo", parentItem.getString("partyIdFrom")));
	    		topCondList.add(EntityCondition.makeCondition("roleTypeIdTo", parentItem.getString("roleTypeIdFrom")));
	    		topCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
	    		topCondList.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));
	    		List<GenericValue> topList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(topCondList), null, null, null, false);				    
	    		levelTopUOPartyIdList.addAll(getPartyIdList(topList, "partyIdFrom"));
			}
		} else {
			levelTopUOPartyIdList.add("![null-field]");
		}
		return levelTopUOPartyIdList;	
	}
	
	/**
	 * ritporna lista dei padri
	 * @return
	 * @throws GenericEntityException
	 */
	private List<GenericValue> getParentList() throws GenericEntityException {
		List<EntityCondition> parentCondList = new ArrayList<EntityCondition>();
		parentCondList.add(EntityCondition.makeCondition("partyIdTo", orgUnitId));
		parentCondList.add(EntityCondition.makeCondition("roleTypeIdTo", orgUnitRoleTypeId));
		parentCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
		parentCondList.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));
		List<GenericValue> parentList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(parentCondList), null, null, null, false);
		return parentList;
	}
	
	/**
	 * estrae dalla lista il party id
	 * @param partyRelationshipList
	 * @param partyField
	 * @return
	 */
	private List<String> getPartyIdList(List<GenericValue> partyRelationshipList, String partyField) {
		List<String> partyIdList = new ArrayList<String>();
		if (UtilValidate.isNotEmpty(partyRelationshipList)) {
			partyIdList = EntityUtil.getFieldListFromEntityList(partyRelationshipList, partyField, true);
		} else {
			partyIdList.add("![null-field]");
		}	
		return partyIdList;
	}
	
	/**
	 * ritorna orgUnitIdList
	 * @return
	 */
	public List<String> getOrgUnitIdList() {
		return orgUnitIdList;
	}
	
	/**
	 * ritorna wepaPartyIdList
	 * @return
	 */
	public List<String> getWepaPartyIdList() {
		return wepaPartyIdList;
	}
	
	/**
	 * ritorna levelUO
	 * @return
	 */
	public boolean isLevelUO() {
		return levelUO;
	}
	
	/**
	 * ritorna levelUOAss
	 * @return
	 */
	public boolean isLevelUOAss() {
		return levelUOAss;
	}
}
