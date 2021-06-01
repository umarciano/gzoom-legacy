package com.mapsengineering.base.standardimport.party;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.standardimport.common.E;

public class PartyRelationshipCleanConditionsBuilder {	
	private String partyRelationshipTypeId;
	private Timestamp fromDate;
	private String partyIdFrom;
	private String notPartyIdFrom;
	private String partyIdTo;
	private String notPartyIdTo;
	private String notRoleTypeIdFrom;
	private String notRoleTypeIdTo;
	private String comments;
	private String description;
	
	
	/**
	 * la classe costruisce le condizioni di ricerca delle relazioni da eliminare o aggiornare
	 * @param partyRelationshipTypeId
	 * @param fromDate
	 * @param partyIdFrom
	 * @param notPartyIdFrom
	 * @param partyIdTo
	 * @param notPartyIdTo
	 * @param notRoleTypeIdFrom
	 * @param notRoleTypeIdTo
	 * @param comments
	 * @param description
	 */
	public PartyRelationshipCleanConditionsBuilder(String partyRelationshipTypeId, Timestamp fromDate, String partyIdFrom, String notPartyIdFrom, 
			String partyIdTo, String notPartyIdTo, String notRoleTypeIdFrom, String notRoleTypeIdTo, String comments, String description) {
		this.partyRelationshipTypeId = partyRelationshipTypeId;
		this.fromDate = fromDate;
		this.partyIdFrom = partyIdFrom;
		this.notPartyIdFrom = notPartyIdFrom;
		this.partyIdTo = partyIdTo;
		this.notPartyIdTo = notPartyIdTo;
		this.notRoleTypeIdFrom = notRoleTypeIdFrom;
		this.notRoleTypeIdTo = notRoleTypeIdTo;
		this.comments = comments;
		this.description = description;
	}
	
	/**
	 * costruisce le condizioni a partire dal partyIdFrom
	 * @param clean
	 * @param excludeFromDate
	 * @return
	 */
	public List<EntityCondition> buildFromRelConditions(boolean clean, boolean excludeFromDate) {
		List<EntityCondition> fromRelConditionList = new ArrayList<EntityCondition>();
        fromRelConditionList.add(getDateCondition(clean));
        fromRelConditionList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), partyRelationshipTypeId));
        fromRelConditionList.add(EntityCondition.makeCondition(E.partyIdFrom.name(), partyIdFrom));

        List<EntityCondition> orCondList = new ArrayList<EntityCondition>();
        orCondList.add(EntityCondition.makeCondition(E.partyIdTo.name(), EntityOperator.NOT_EQUAL, notPartyIdTo));
        orCondList.add(EntityCondition.makeCondition(E.roleTypeIdTo.name(), EntityOperator.NOT_EQUAL, notRoleTypeIdTo));
        orCondList.add(EntityCondition.makeCondition(E.roleTypeIdFrom.name(), EntityOperator.NOT_EQUAL, notRoleTypeIdFrom));
        if (! clean) {
        	if (UtilValidate.isNotEmpty(comments)) {
        		addCommentsOrConditions(orCondList, excludeFromDate);
        	}
        	if (UtilValidate.isNotEmpty(description)) {
        		addRelNameOrConditions(orCondList);
        	}
        }
        fromRelConditionList.add(EntityCondition.makeCondition(orCondList, EntityJoinOperator.OR));
        
        return fromRelConditionList;
	}
	
	/**
	 * costruisce le condizioni per eval e approver
	 * @param roleTypeIdFrom
	 * @param thruDate
	 * @param clean
	 * @return
	 */
	public List<EntityCondition> buildEvaluatorsAndApproversFromRelConditions(String roleTypeIdFrom, Timestamp thruDate, boolean clean) {
		List<EntityCondition> profilesFromRelConditionList = new ArrayList<EntityCondition>();
        profilesFromRelConditionList.add(getDateCondition(thruDate, clean));
        profilesFromRelConditionList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), partyRelationshipTypeId));
        profilesFromRelConditionList.add(EntityCondition.makeCondition(E.partyIdFrom.name(), partyIdFrom));
        profilesFromRelConditionList.add(EntityCondition.makeCondition(E.roleTypeIdFrom.name(), roleTypeIdFrom));
        profilesFromRelConditionList.add(EntityCondition.makeCondition(E.partyIdTo.name(), EntityOperator.NOT_EQUAL, notPartyIdTo));
        
        return profilesFromRelConditionList;       
	}
	
	/**
	 * costruisce le condizioni a partire dal partyIdTo
	 * @param clean
	 * @param excludeFromDate
	 * @return
	 */
	public List<EntityCondition> buildToRelConditions(boolean clean, boolean excludeFromDate) {
		List<EntityCondition> toRelConditionList = new ArrayList<EntityCondition>();
        toRelConditionList.add(getDateCondition(clean));
        toRelConditionList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), partyRelationshipTypeId));
        toRelConditionList.add(EntityCondition.makeCondition(E.partyIdTo.name(), partyIdTo));

        List<EntityCondition> orCondList = new ArrayList<EntityCondition>();
        orCondList.add(EntityCondition.makeCondition(E.partyIdFrom.name(), EntityOperator.NOT_EQUAL, notPartyIdFrom));
        orCondList.add(EntityCondition.makeCondition(E.roleTypeIdTo.name(), EntityOperator.NOT_EQUAL, notRoleTypeIdTo));
        orCondList.add(EntityCondition.makeCondition(E.roleTypeIdFrom.name(), EntityOperator.NOT_EQUAL, notRoleTypeIdFrom));        
        if (! clean) {
        	if (UtilValidate.isNotEmpty(comments)) {
        		addCommentsOrConditions(orCondList, excludeFromDate);
        	}
        	if (UtilValidate.isNotEmpty(description)) {
        		addRelNameOrConditions(orCondList);
        	}
        }     
        toRelConditionList.add(EntityCondition.makeCondition(orCondList, EntityJoinOperator.OR));
        
        return toRelConditionList;
	}
	
	/**
	 *
	 * @param clean
	 * @return
	 */
	private EntityCondition getDateCondition(boolean clean) {
		return getDateCondition(null, clean);
	}
	
	/**
	 * aggiunge condizioni sulle date, se clean = true, allora fromDate = fromDate perche' cerco con la chiave fromDate
	 * @param thruDate
	 * @param clean
	 * @return
	 */
	private EntityCondition getDateCondition(Timestamp thruDate, boolean clean) {
		return clean 
				? EntityCondition.makeCondition(E.fromDate.name(), fromDate) 
				: EntityCondition.makeCondition(E.thruDate.name(), thruDate);
	}
	
	/**
	 * aggiunge condizioni sui commenti
	 * @param orCondList
	 * @param excludeFromDate
	 */
	private void addCommentsOrConditions(List<EntityCondition> orCondList, boolean excludeFromDate) {
        List<EntityCondition> commCondList = new ArrayList<EntityCondition>();
        commCondList.add(EntityCondition.makeCondition(E.comments.name(), EntityOperator.NOT_EQUAL, comments));
        commCondList.add(EntityCondition.makeCondition(E.comments.name(), null));
        
        if (excludeFromDate) {
        	orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition(commCondList, EntityJoinOperator.OR), 
            		EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.NOT_EQUAL, fromDate)));	
        } else {
        	orCondList.add(EntityCondition.makeCondition(commCondList, EntityJoinOperator.OR));	
        }	
	}
	
	/**
	 * aggiunge condizioni su relationshipName
	 * @param orCondList
	 */
	private void addRelNameOrConditions(List<EntityCondition> orCondList) {
        List<EntityCondition> relNameCondList = new ArrayList<EntityCondition>();
        relNameCondList.add(EntityCondition.makeCondition(E.relationshipName.name(), EntityOperator.NOT_EQUAL, description));
        relNameCondList.add(EntityCondition.makeCondition(E.relationshipName.name(), null));

        orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition(relNameCondList, EntityJoinOperator.OR), 
        		EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.NOT_EQUAL, fromDate)));		
	}
	
	public String getPartyRelationshipTypeId() {
		return partyRelationshipTypeId;
	}

	public Timestamp getFromDate() {
		return fromDate;
	}

	public String getPartyIdFrom() {
		return partyIdFrom;
	}

	public String getPartyIdTo() {
		return partyIdTo;
	}

	public String getNotPartyIdTo() {
		return notPartyIdTo;
	}

	public String getNotRoleTypeIdFrom() {
		return notRoleTypeIdFrom;
	}

	public String getNotRoleTypeIdTo() {
		return notRoleTypeIdTo;
	}

	public String getComments() {
		return comments;
	}

}
