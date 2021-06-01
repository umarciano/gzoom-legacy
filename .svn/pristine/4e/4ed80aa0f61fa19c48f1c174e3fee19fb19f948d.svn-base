package com.mapsengineering.base.standardimport.helper;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.util.JobLogLog;

/**
 * Helper for WorkEffortStatus
 *
 */
public class WeStatusInterfaceHelper {

    private TakeOverService takeOverService;
    private GenericValue defaultStatusItem;

    /**
     * Constructor
     * @param takeOverService
     */
    public WeStatusInterfaceHelper(TakeOverService takeOverService) {
        this.takeOverService = takeOverService;
        this.defaultStatusItem = null;
    }

    /**
     * Controlla esistenza unico WorkEffortTypeStatus che corrisponde ai paramtetri passati, ritorna il generic value oppure eccezione 
     * @param workEffortTypeRootId
     * @param statusRootId
      * @throws GeneralException
     */
    public GenericValue checkValidityWorkEffortTypeStatus(String workEffortTypeRootId, String statusItemDesc) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();
        EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition(E.workEffortTypeRootId.name(), workEffortTypeRootId), EntityCondition.makeCondition(E.description.name(), statusItemDesc));
        
        Map<String, Object> parameters = UtilMisc.toMap(E.statusDesc.name(), (Object) statusItemDesc, E.sourceId.name(), gv.get(E.sourceReferenceRootId.name()), E.typeId.name(), gv.get(E.workEffortTypeId.name()));
        JobLogLog statusNotUnique = new JobLogLog().initLogCode("StandardImportUiLabels", "STATUS_NOT_UNIQUE", parameters, getTakeOverService().getManager().getLocale());
        JobLogLog noStatusFound = new JobLogLog().initLogCode("StandardImportUiLabels", "NO_STATUS_FOUND", parameters, getTakeOverService().getManager().getLocale());

        return takeOverService.findOne(E.WorkEffortTypeStatusView.name(), condition, statusNotUnique, noStatusFound, getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
    }

    /**
     * takeOverService
     * @return
     */
    public TakeOverService getTakeOverService() {
        return takeOverService;
    }

    /**
     * Return statusItemDesc or defaultStatusItem.description if exist property
     * @param gv
     * @param checkDefault
     * @return
     * @throws GenericEntityException
     */
    public String getStatusItemDesc(GenericValue gv, boolean checkDefault) throws GenericEntityException {
        String statusItemDesc = null;
        if (gv != null) {
            statusItemDesc = gv.getString(E.statusItemDesc.name());
            if (UtilValidate.isEmpty(statusItemDesc) && checkDefault) {
                getDefaultStatusItem();
                if (defaultStatusItem != null) {
                    statusItemDesc = defaultStatusItem.getString(E.description.name());
                }
            }
        }
        return statusItemDesc;
    }

    /**
     * Return defaultStatusItem
     * @return
     * @throws GenericEntityException
     */
    public GenericValue getDefaultStatusItem() throws GenericEntityException {
        if (defaultStatusItem == null) {
            final String defaultStatusTypeId = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.weRootInterface.defaultStatusTypeId", null);
            if (UtilValidate.isNotEmpty(defaultStatusTypeId)) {
                EntityCondition condition = EntityCondition.makeCondition(E.statusTypeId.name(), defaultStatusTypeId);
                List<String> orderBy = UtilMisc.toList(E.sequenceId.name());
                EntityFindOptions findOptions = new EntityFindOptions(false, -1, -1, 1, 1, false);
                defaultStatusItem = EntityUtil.getFirst(takeOverService.getManager().getDelegator().findList(E.StatusItem.name(), condition, null, orderBy, findOptions, false));
            }
        }
        return defaultStatusItem;
    }
}
