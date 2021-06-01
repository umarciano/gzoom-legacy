package com.mapsengineering.base.standardimport.util.jobloglog;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;

import com.mapsengineering.base.standardimport.common.E;

/**
 * JobLogLogPartyParentRoleCreator with parentRoleCode, roleTypeId, sourceId
 *
 */
public class JobLogLogPartyParentRoleCreator extends JobLogLogCreator {
	private String parentRoleCode;
	private String roleTypeId;
	private String sourceId;
	
    /**
     * 
     * @param resourceName
     * @param parentRoleCode
     * @param roleTypeId
     * @param sourceId
     */
	public JobLogLogPartyParentRoleCreator(String resourceName, String parentRoleCode, String roleTypeId, String sourceId) {
		super(resourceName);
		this.parentRoleCode = parentRoleCode;
		this.roleTypeId = roleTypeId;
		this.sourceId = sourceId;
	}

	@Override
	protected Map<String, Object> buildParameters() {
    	if(E.ORGANIZATION_UNIT.name().equals(roleTypeId)) {
    		UtilMisc.toMap(E.parentCode.name(), (Object) parentRoleCode, E.sourceId.name(), sourceId);
    	}
    	return UtilMisc.toMap(E.parentCode.name(), (Object) parentRoleCode, E.roleTypeId.name(), (Object) roleTypeId, E.sourceId.name(), sourceId);
	}

	@Override
	protected String buildNotFoundLogCode() {
    	if(E.ORGANIZATION_UNIT.name().equals(roleTypeId)) {
    		return "NO_UO_FOUND";
    	}
    	return "NO_PRLCODE_FOUND";
	}

	@Override
	protected String buildNotUniqueLogCode() {
    	if(E.ORGANIZATION_UNIT.name().equals(roleTypeId)) {
    		return "UO_NOT_UNIQUE";
    	}
    	return "PRLCODE_NOT_UNIQUE";
	}

}
