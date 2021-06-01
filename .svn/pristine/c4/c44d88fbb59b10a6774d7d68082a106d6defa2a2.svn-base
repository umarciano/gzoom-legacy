package com.mapsengineering.base.standardimport;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.util.FindUtilService;

import javolution.util.FastList;

/**
 * Common Party initLocalValue and doImportExpiredRelations
 *
 */
public abstract class AbstractPartyTakeOverService extends TakeOverService {

	protected static final String REF_DATE_AFTER_NOW = "Future date not allowed for ";

	protected String initLocalValuePartyParentRole(String partyCode, String roleTypeIdValue) throws GenericEntityException {
		ImportManager manager = getManager();
		GenericValue externalValue = getExternalValue();
		String partyId = "";

		// Search for in PartyParentRole where parentRoleCode = TABLE_INTERFACCIA.orgCode
		List<GenericValue> parents = manager.getDelegator().findList(E.PartyParentRole.name(), EntityCondition.makeCondition(EntityCondition.makeCondition(E.parentRoleCode.name(), partyCode), EntityCondition.makeCondition(E.roleTypeId.name(), roleTypeIdValue)), null, null, null, false);
		partyId = getFirst(parents);

		if (UtilValidate.isNotEmpty(externalValue) && UtilValidate.isEmpty(parents)) {
			// If found null there is a insert
			setLocalValue(null);
			return null;
		}

		return partyId;
	}

	private String getFirst(List<GenericValue> parents) throws GenericEntityException {
		ImportManager manager = getManager();
		GenericValue parent = EntityUtil.getFirst(parents);
		String partyId = "";

		if (UtilValidate.isNotEmpty(parent)) {
			partyId = parent.getString(E.partyId.name());
			setLocalValue(manager.getDelegator().findOne(E.Party.name(), UtilMisc.toMap(E.partyId.name(), partyId), false));
		}
		return partyId;
	}

	/**
	 * if externalValue.thruDate is not null set thruDate for all relation with
	 * condition partyIdTo = partyId or partyIdFrom = partyId and then
	 */
	protected void doImportExpiredRelations(String partyId) throws GeneralException {
		ImportManager manager = getManager();
		GenericValue gv = getExternalValue();
		String msg = "";

		// Faccio scadere TUTTE le relazioni
		if (UtilValidate.isNotEmpty(gv.getTimestamp(E.thruDate.name()))) {
			List<EntityCondition> condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition(E.thruDate.name(), null));
			condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition(E.partyIdFrom.name(), partyId),
					EntityOperator.OR, EntityCondition.makeCondition(E.partyIdTo.name(), partyId)));
			List<GenericValue> partyRelList = manager.getDelegator().findList(E.PartyRelationship.name(),
					EntityCondition.makeCondition(condList), null, null, null, false);
			msg = "Found " + partyRelList.size() + " relationships of party " + partyId + " to disabled because party is disabled";
			addLogInfo(msg);
			for (GenericValue relation : partyRelList) {
				Map<String, Object> parametersMap = UtilMisc.toMap(E.partyIdFrom.name(),
						relation.getString(E.partyIdFrom.name()), E.roleTypeIdFrom.name(),
						relation.getString(E.roleTypeIdFrom.name()), E.partyIdTo.name(),
						relation.getString(E.partyIdTo.name()), E.roleTypeIdTo.name(),
						relation.getString(E.roleTypeIdTo.name()), E.partyRelationshipTypeId.name(),
						relation.getString(E.partyRelationshipTypeId.name()), E.fromDate.name(),
						relation.getTimestamp(E.fromDate.name()), E.thruDate.name(),
						gv.getTimestamp(E.thruDate.name()));
				msg = "Found " + parametersMap + " to disabled because party is disabled";
	            addLogInfo(msg);
	            runSyncCrud(E.crudServiceDefaultOrchestration_PartyRelationship.name(), E.PartyRelationship.name(),
						CrudEvents.OP_UPDATE, parametersMap,
						E.PartyRelationship.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE,
						FindUtilService.MSG_ERROR_UPDATE + E.PartyRelationship.name(), false);
			}
		}
	}

	/**
	 * rimuovere checks if party is disabled
	 * 
	 * @return
	 *  */
    protected boolean isPartyDisabled() {
        GenericValue lv = getLocalValue();
        return UtilValidate.isNotEmpty(lv) && !E.PARTY_ENABLED.name().equals(lv.getString(E.statusId.name()));
    }
	

	/**
	 * rimuovere checks if party is to be imported
	 * 
	 * @return
	 */
    protected boolean isPartyToBeImported() {
        if (isPartyDisabled()) {
            GenericValue gv = getExternalValue();
            return UtilValidate.isNotEmpty(gv) && UtilValidate.isEmpty(gv.getTimestamp(E.thruDate.name()));
        }
        return true;
    }
	

	/**
	 * checks if roleType has associative data
	 * 
	 * @param roleType
	 * @return
	 * @throws GeneralException
	 */
	protected boolean hasAssociative(GenericValue roleType) throws GeneralException {
		return UtilValidate.isNotEmpty(roleType)
				&& UtilValidate.isNotEmpty(roleType.getString(E.workEffortTypeId.name()))
				&& UtilValidate.isNotEmpty(roleType.getString(E.workEffortAssocTypeId.name()))
				&& UtilValidate.isNotEmpty(roleType.getString(E.workEffortPeriodId.name()));
	}

	/**
	 * Return true if refDate is after nowTimestamp (Future)
	 * 
	 * @param gv
	 * @return
	 * @throws GeneralException
	 */
	protected boolean checkRefDate(GenericValue gv) throws GeneralException {
		Timestamp refDate = gv.getTimestamp(E.refDate.name());
		if (refDate.after(UtilDateTime.nowTimestamp())) {
			return true;
		}
		return false;
	}
}
