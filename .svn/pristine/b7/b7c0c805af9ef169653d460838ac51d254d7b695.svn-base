package com.mapsengineering.base.standardimport.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilGenerics;

import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.WeContextEnum;

/**
 * WeRootInterfaceContext
 *
 */
public class WeRootInterfaceContext {

    public static final String MODULE = WeRootInterfaceContext.class.getName();
    public static final String ERROR_ROOT_RECORD = " Valid root record (WeRootInterface record) required to import";
    
    private String operationType;
    private WeContextEnum weContext;
    private String organizationId;
    private String workEffortRootId;
    private Timestamp estimatedStartDate;
    private Timestamp estimatedCompletionDate;
    private String orgUnitRootId;
    private String orgUnitRoleTypeRootId;
    private String parentTypeId;
    private String statusRootId;
    private String workEffortAssocTypeId;

    /**
     * Create a new context, it MUST be destroyed by calling pop.
     * @param importManager
     */
    public static void push(ImportManager importManager) {
        Map<String, Object> context = importManager.getContext();
        List<WeRootInterfaceContext> list = UtilGenerics.toList(context.get(MODULE));
        if (list == null) {
            list = new ArrayList<WeRootInterfaceContext>();
            context.put(MODULE, list);
        }
        list.add(new WeRootInterfaceContext());
    }

    /**
     * Destroy current context
     * @param importManager
     */
    public static void pop(ImportManager importManager) {
        List<WeRootInterfaceContext> list = UtilGenerics.toList(importManager.getContext().get(MODULE));
        if (list != null && list.size() > 0) {
            list.remove(list.size() - 1);
        }
    }

    /**
     * Get current context, it MUST be created by calling push.
     * @param importManager
     * @return
     */
    public static WeRootInterfaceContext get(ImportManager importManager) {
        List<WeRootInterfaceContext> list = UtilGenerics.toList(importManager.getContext().get(MODULE));
        if (list != null && list.size() > 0) {
            return list.get(list.size() - 1);
        }
        return null;
    }

    /**
     * Get operationType
     * @return operationType
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * Set operationType
     * @param operationType
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    /**
     * Get weContext
     * @return weContext
     */
    public WeContextEnum getWeContext() {
        return weContext;
    }

    /**
     * Set weContext
     * @param weContext
     */
    public void setWeContext(WeContextEnum weContext) {
        this.weContext = weContext;
    }

    /**
     * Set organizationId
     * @param organizationId
     */
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * Set workEffortRootId
     * @param workEffortRootId
     */
    public void setWorkEffortRootId(String workEffortRootId) {
        this.workEffortRootId = workEffortRootId;
    }

    /**
     * Get workEffortRootId
     * @return workEffortRootId
     */
    public String getWorkEffortRootId() {
        return workEffortRootId;
    }

    /**
     * Get estimatedStartDate
     * @return estimatedStartDate
     */
    public Timestamp getEstimatedStartDate() {
        return estimatedStartDate;
    }

    /**
     * Set estimatedStartDate
     * @param estimatedStartDate
     */
    public void setEstimatedStartDate(Timestamp estimatedStartDate) {
        this.estimatedStartDate = estimatedStartDate;
    }

    /**
     * Get estimatedCompletionDate
     * @return estimatedCompletionDate
     */
    public Timestamp getEstimatedCompletionDate() {
        return estimatedCompletionDate;
    }

    /**
     * Set estimatedCompletionDate
     * @param estimatedCompletionDate
     */
    public void setEstimatedCompletionDate(Timestamp estimatedCompletionDate) {
        this.estimatedCompletionDate = estimatedCompletionDate;
    }

    /**
     * Get orgUnitRootId
     * @return orgUnitRootId
     */
    public String getOrgUnitRootId() {
        return orgUnitRootId;
    }

    /**
     * Set orgUnitRootId
     * @param orgUnitRootId
     */
    public void setOrgUnitRootId(String orgUnitRootId) {
        this.orgUnitRootId = orgUnitRootId;
    }

    /**
     * Get orgUnitRoleTypeRootId
     * @return orgUnitRoleTypeRootId
     */
    public String getOrgUnitRoleTypeRootId() {
        return orgUnitRoleTypeRootId;
    }

    /**
     * Set orgUnitRoleTypeRootId
     * @param orgUnitRoleTypeRootId
     */
    public void setOrgUnitRoleTypeRootId(String orgUnitRoleTypeRootId) {
        this.orgUnitRoleTypeRootId = orgUnitRoleTypeRootId;
    }

    /**
     * Set parentTypeId
     * @param parentTypeId
     */
    public void setParentTypeId(String parentTypeId) {
        this.parentTypeId = parentTypeId;
    }

    /**
     * Get statusRootId
     * @return statusRootId
     */
    public String getStatusRootId() {
        return statusRootId;
    }

    /**
     * Set statusRootId
     * @param statusRootId
     */
    public void setStatusRootId(String statusRootId) {
        this.statusRootId = statusRootId;
    }

    /**
     * Get parentTypeId
     * @return parentTypeId
     */
    public String getParentTypeId() {
        return parentTypeId;
    }

    /**
     * Get organizationId
     * @return organizationId
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * Get workEffortAssocTypeId
     * @return workEffortAssocTypeId
     */
    public String getWorkEffortAssocTypeId() {
        return workEffortAssocTypeId;
    }

    /**
     * Set workEffortAssocTypeId
     * @param workEffortAssocTypeId
     */
    public void setWorkEffortAssocTypeId(String workEffortAssocTypeId) {
        this.workEffortAssocTypeId = workEffortAssocTypeId;
    }
}
