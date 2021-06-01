package com.mapsengineering.base.standardimport.common;

import org.ofbiz.base.util.UtilValidate;

import com.mapsengineering.base.standardimport.util.WeRootInterfaceContext;


public abstract class WorkEffortCommonTakeOverService extends TakeOverService {

	public static final String MODULE = WorkEffortCommonTakeOverService.class.getName();
	
	private WeRootInterfaceContext weRootInterfaceContext = null;
    private String organizationId;
    private String workEffortRootId;
    
    protected String getOrganizationId() {
		return organizationId;
	}

	protected void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	protected String getWorkEffortRootId() {
		return workEffortRootId;
	}

	protected void setWorkEffortRootId(String workEffortRootId) {
		this.workEffortRootId = workEffortRootId;
	}
    
    protected WeRootInterfaceContext getWeRootInterfaceContext() {
		return (this.weRootInterfaceContext != null) ? this.weRootInterfaceContext : WeRootInterfaceContext.get(getManager());
	}
	
	protected void getContextData() {
    	setOrganizationId((String) getManager().getContext().get(E.defaultOrganizationPartyId.name()));
    	
        WeRootInterfaceContext weRootInterfaceContext = getWeRootInterfaceContext();
        if (UtilValidate.isNotEmpty(weRootInterfaceContext)) {
            setWorkEffortRootId(weRootInterfaceContext.getWorkEffortRootId());
            setWithWeRootInterfaceContext(weRootInterfaceContext);
        }
        
        setOtherContextData();
    }

	protected void setWithWeRootInterfaceContext(WeRootInterfaceContext weRootInterfaceContext) {
		// subclasses override
	}

	protected void setOtherContextData() {
		// subclasses override	
	}

}
