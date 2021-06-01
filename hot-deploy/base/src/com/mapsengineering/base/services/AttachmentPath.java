package com.mapsengineering.base.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entityext.data.EntityDataServices;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class AttachmentPath {

    public static final String MODULE = EntityDataServices.class.getName();

    private static final String PROP_OFBIZ_HOME = "ofbiz.home";
    private static final String RUNTIME_PREFIX = "/runtime/uploads/";

    private enum E {
        LOCAL_FILE, DataResource, dataResourceTypeId, objectInfo, messages
    };

    public static Map<String, Object> update(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        List<String> messages = FastList.newInstance();

        // step 1 - retrieve path to upload directory
        String propValue = System.getProperty(PROP_OFBIZ_HOME);
        Debug.logImportant("Retrieve path to upload directory", MODULE);
        Debug.logImportant("Ofbiz home = " + propValue, MODULE);

        EntityCondition whereEntityCondition = EntityCondition.makeCondition(E.dataResourceTypeId.name(), EntityOperator.EQUALS, E.LOCAL_FILE.name());
        EntityListIterator it = delegator.find(E.DataResource.name(), whereEntityCondition, null, null, null, null);
        List<String> listOfUploadPaths = FastList.newInstance();
        // step 2 - retrieve list of records containing references to upload directory
        Debug.logImportant("Retrieve list of records containing references to upload directory", MODULE);
        try {
            GenericValue gv;
            while ((gv = it.next()) != null) {
                String match = gv.getString(E.objectInfo.name());
                if (match.indexOf(RUNTIME_PREFIX) > -1) {
                    String newPath = propValue + match.substring(match.indexOf(RUNTIME_PREFIX));
                    Debug.logImportant("Substitution of path " + match + "with new path " + newPath, MODULE);
                    gv.put(E.objectInfo.name(), newPath);
                    gv.store();
                    listOfUploadPaths.add(newPath);
                }
            }
        } finally {
            it.close();
        }

        // step 3 - check actual presence of listed files in filesystem
        Debug.logImportant("Check actual presence of listed files in filesystem", MODULE);

        Map<String, Object> result = ServiceUtil.returnSuccess();
        for (String s : listOfUploadPaths) {
            File file = new File(s);
            if (!(file.exists() && file.isFile())) {
                messages.add("The following file is not present in the filesystem: " + s);
            }

        }
        result.put(E.messages.name(), messages);
        return result;
    }
}
