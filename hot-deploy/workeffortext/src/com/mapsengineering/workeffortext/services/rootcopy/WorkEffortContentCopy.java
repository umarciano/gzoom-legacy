package com.mapsengineering.workeffortext.services.rootcopy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.content.data.DataResourceWorker;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.workeffortext.services.E;

/**
 * Manage WorkEffortContent, only for snapshot
 *
 */
public class WorkEffortContentCopy extends AbstractWorkEffortDataCopy {

    public static final String MODULE = WorkEffortContentCopy.class.getName();

    /**
     * Constructor
     * @param service
     * @param snapshot
     */
    public WorkEffortContentCopy(WorkEffortRootCopyService service) {
        super(service, true);
    }

    /**
     * Search WorkEffortContent and execute uploadWorkEffortContentFileExtended.<br/>
     * @throws  
     */
    @Override
    public Map<String, Object> copy(String origWorkEffortId, String newWorkEffortId, Map<String, ? extends Object> data) throws GeneralException {

        List<EntityCondition> cond = FastList.newInstance();
        cond.add(EntityCondition.makeCondition(E.workEffortId.name(), EntityOperator.EQUALS, origWorkEffortId));

        List<GenericValue> contentList = getDelegator().findList(E.WorkEffortAndContentDataResourceExtended.name(), EntityCondition.makeCondition(cond), null, null, null, getUseCache());
        for (GenericValue content : contentList) {
            String contentName = content.getString(E.contentName.name());

            String errorMsg = FindUtilService.MSG_PROBLEM_CREATE + " attactment " + contentName + " to WorkEffort " + newWorkEffortId;
            
            ByteBuffer uploadedFile = getByteBufferFromOriginalDataResource(errorMsg, content.getString(E.dataResourceId.name()));

            Map<String, Object> serviceMap = FastMap.newInstance();
            serviceMap.put("userLogin", getUserLogin());
            serviceMap.put(E.workEffortId.name(), newWorkEffortId);
            serviceMap.put(E.partyId.name(), getUserLogin().getString(E.partyId.name()));
            serviceMap.put(E.roleTypeNotRequired.name(), E.Y.name());
            serviceMap.put(E.contentTypeId.name(), content.getString(E.contentTypeId.name()));
            serviceMap.put(E.isPublic.name(), content.getString(E.isPublic.name()));
            serviceMap.put(E.fromDate.name(), content.getTimestamp(E.fromDate.name()));
            serviceMap.put(E.thruDate.name(), content.getTimestamp(E.thruDate.name()));
            serviceMap.put(E._uploadedFile_fileName.name(), content.getString(E.dataResourceName.name()));
            serviceMap.put(E._uploadedFile_contentType.name(), content.getString(E.mimeTypeId.name()));
            serviceMap.put(E.uploadedFile.name(), uploadedFile);
            serviceMap.put(E.description.name(), content.getString(E.description.name()));
            serviceMap.put(E.workEffortContentTypeId.name(), content.getString(E.workEffortContentTypeId.name()));

            String successMsg = "Copied attactment " + contentName + " to WorkEffort " + newWorkEffortId;
            runSync("uploadWorkEffortContentFileExtended", serviceMap, successMsg, errorMsg, true, origWorkEffortId);
        }
        return null;
    }

    private ByteBuffer getByteBufferFromOriginalDataResource(String errorMsg, String dataResourceId) throws GeneralException {
        try {
            return DataResourceWorker.getContentAsByteBuffer(getDelegator(), dataResourceId, null, null, getLocale(), null);
        } catch (IOException e) {
            throw new GeneralException(errorMsg, e);
        }
    }

}
