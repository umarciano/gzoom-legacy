package com.mapsengineering.base.standardimport.util.jobloglog;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

import com.mapsengineering.base.standardimport.common.E;

/**
 * JobLogLogPartyRoleCreator with partyId, roleTypeId, sourceId
 *
 */
public class JobLogLogPartyRoleCreator extends JobLogLogCreator {
	private String partyId; 
	private String roleTypeId;
	private String sourceId;

	/**
	 * 
	 * @param resourceName
	 * @param partyId
	 * @param roleTypeId
	 * @param sourceId
	 */
	public JobLogLogPartyRoleCreator(String resourceName, String partyId, String roleTypeId, String sourceId) {
		super(resourceName);
		this.partyId = partyId;
		this.roleTypeId = roleTypeId;
		this.sourceId = sourceId;
	}

	@Override
	protected Map<String, Object> buildParameters() {
		if(UtilValidate.isNotEmpty(sourceId)) {
			return UtilMisc.toMap(E.partyId.name(), (Object) partyId, E.roleTypeId.name(), (Object) roleTypeId, E.sourceId.name(), (Object) sourceId);
		}
		return UtilMisc.toMap(E.partyId.name(), (Object) partyId, E.roleTypeId.name(), (Object) roleTypeId);
	}

	@Override
	protected String buildNotFoundLogCode() {
		if(UtilValidate.isNotEmpty(sourceId)) {
			return "NO_PR_FOUND_WE";
		}
		return "NO_PR_FOUND";
	}

	@Override
	protected String buildNotUniqueLogCode() {
		if(UtilValidate.isNotEmpty(sourceId)) {
			return "PR_NOT_UNIQUE_WE";
		}
		return "PR_NOT_UNIQUE";
	}

}
