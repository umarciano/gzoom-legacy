package com.mapsengineering.workeffortext.services.sequence.helper;

import java.util.List;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.workeffortext.services.E;

public class FrameCodeHelper {
	
	private Delegator delegator;
	private String workEffortParentId;
	private String orgUnitId;
	private String weTATOWorkEffortIdFrom;
	
	
	
	/**
	 * 
	 * @param delegator
	 * @param workEffortParentId
	 * @param orgUnitId
	 * @param weTATOWorkEffortIdFrom
	 */
	public FrameCodeHelper(Delegator delegator, String workEffortParentId, String orgUnitId, String weTATOWorkEffortIdFrom) {
		this.delegator = delegator;
		this.workEffortParentId = workEffortParentId;
		this.orgUnitId = orgUnitId;
		this.weTATOWorkEffortIdFrom = weTATOWorkEffortIdFrom;
	}

	/**
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public String getCodiceUoByPartyParentRole() throws GeneralException {
		List<GenericValue> values = delegator.findByAnd(E.PartyParentRole.name(), UtilMisc.toMap(E.roleTypeId.name(), "ORGANIZATION_UNIT", E.partyId.name(), orgUnitId)); 
		return (!values.isEmpty()) ? EntityUtil.getFirst(values).getString(E.parentRoleCode.name()) : "";
	}
	
	/**
	 * controllo se ho il parent altrimenti prendo assoc
	 * @return
	 * @throws GeneralException
	 */
	public String getCodiceParent() throws GeneralException {
		GenericValue workEffortParent = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortParentId), false);
		if (UtilValidate.isNotEmpty(workEffortParent)) {
			return (workEffortParent.getString(E.etch.name()) != null) ? workEffortParent.getString(E.etch.name()) : ""; 
		}
		GenericValue workEffortWETATO = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), weTATOWorkEffortIdFrom), false);
		if (UtilValidate.isNotEmpty(workEffortWETATO)) {
			return (workEffortWETATO.getString(E.etch.name()) != null) ? workEffortWETATO.getString(E.etch.name()) : ""; 
		}
		return "";
	}

}
