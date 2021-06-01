package com.mapsengineering.workeffortext.services.rootcopy;

import org.ofbiz.base.util.UtilValidate;

import com.mapsengineering.workeffortext.services.E;


/**
 * Manage enumeration type for GlAccount
 *
 */
public enum ServiceTypeEnum {

    /*SNAPSHOT_MASSIVE("SNAPSHOT_MASSIVE", "MAS_WRK_ROOT_COPY")
    SNAPSHOT("SNAPSHOT", "WRK_ROOT_SNAP")
    CLONE("CLONE", "WRK_ROOT_CLONE")
    COPY_ALL("COPY_ALL", "WRK_ROOT_COPY_ALL")
    COPY("COPY", "WRK_ROOT_COPY")*/    
    
    /*SNAPSHOT_MASSIVE("MAS_WRK_ROOT_COPY", true, true, true),
    SNAPSHOT("WRK_ROOT_SNAP", true, true, true),
    CLONE("WRK_ROOT_CLONE", true, true, false),
    COPY_ALL("WRK_ROOT_COPY_ALL", false, false, false),
    COPY("WRK_ROOT_COPY", false, false, false);*/
    
    SNAPSHOT_MASSIVE("MAS_WRK_ROOT_COPY", true, true),
    SNAPSHOT("WRK_ROOT_SNAP", true, true),
    CLONE("WRK_ROOT_CLONE", true, false),
    COPY_ALL("WRK_ROOT_COPY_ALL", false, false),
    COPY("WRK_ROOT_COPY", false, false); 
    
    private String serviceTypeId;
    private boolean useEnableSnapshot;
    private boolean useWorkEffortRevisionId;
    
    /**
     * 
     * @param serviceTypeId
     * @param useEnableSnapshot, wether if workEffortType use enableSnapshop to enable service
     * @param snapshot
     */
    private ServiceTypeEnum(String serviceTypeId, boolean useEnableSnapshot, boolean useWorkEffortRevisionId) {
        this.serviceTypeId = serviceTypeId;
        this.useEnableSnapshot = useEnableSnapshot;
        this.useWorkEffortRevisionId = useWorkEffortRevisionId;
    }

    /**
     * Return Enumeration type
     * @param indicator
     * @return
     */
    public static ServiceTypeEnum getEnumeration(String snapshot, String duplicateAdmit) {
        if (E.Y.name().equals(snapshot)) {
            return returnCaseSnapshot(duplicateAdmit);
        } else if (UtilValidate.isNotEmpty(duplicateAdmit)) {
            return returnCaseDuplicate(duplicateAdmit);
        }
        return COPY;
    }

    private static ServiceTypeEnum returnCaseSnapshot(String duplicateAdmit) {
        if (E.SNAPSHOT.name().equals(duplicateAdmit)) {
            return SNAPSHOT;
        }
        return SNAPSHOT_MASSIVE;
    }
    
    private static ServiceTypeEnum returnCaseDuplicate(String duplicateAdmit) {
        if (E.CLONE.name().equals(duplicateAdmit)) {
            return CLONE;
        }
        return COPY_ALL;
    }

    /**
     * @return the serviceTypeId
     */
    public String getServiceTypeId() {
        return serviceTypeId;
    }

    /**
     * @return the useEnableSnapshot
     */
    public boolean isUseEnableSnapshot() {
        return useEnableSnapshot;
    }

    /**
     * @return the useWorkEffortRevisionId
     */
    public boolean isUseWorkEffortRevisionId() {
        return useWorkEffortRevisionId;
    }
}