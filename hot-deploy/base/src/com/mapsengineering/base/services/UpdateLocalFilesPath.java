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

public class UpdateLocalFilesPath {

    public static final String MODULE = EntityDataServices.class.getName();

    private static final String PROP_OFBIZ_HOME = "ofbiz.home";
    private static final String RUNTIME_PREFIX = "/runtime/uploads/";
    private static final String HOTDEPLOY_PREFIX = "hot-deploy/";
    private static final String FRAMEWORK_PREFIX = "framework/";
    private static final String BASE_PREFIX = "/base/webapp/";
    private static final String THEME_PREFIX = "/theme_gplus/webapp/";
    private static final String RPTDESIGN_SUFFIX = ".rptdesign";

    private enum E {
        LOCAL_FILE, DataResource, dataResourceTypeId, objectInfo, messages, OFBIZ_FILE_BIN
    };

    public static Map<String, Object> update(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        List<String> messages = FastList.newInstance();

        // step 1 - retrieve path to upload directory
        String propValue = System.getProperty(PROP_OFBIZ_HOME);
        Debug.logImportant("Retrieve path to upload directory", MODULE);
        Debug.logImportant("Ofbiz home = " + propValue, MODULE);

        // step 2 - UPDATE OFBITZ_FILE_BIN to LOCAL_FILE
        EntityCondition where = EntityCondition.makeCondition(E.dataResourceTypeId.name(), EntityOperator.EQUALS, E.OFBIZ_FILE_BIN.name());
        EntityListIterator list = delegator.find(E.DataResource.name(), where, null, null, null, null);

        Debug.logImportant("Retrieve list of records with dataResourceTypeId = OFBITZ_FILE_BIN for update", MODULE);
        try {
        	GenericValue gv;
        	while((gv = list.next()) != null) {
        		gv.put(E.dataResourceTypeId.name(), E.LOCAL_FILE.name());
        		gv.store();
        	}
        } finally {
			list.close();
		}
        
        
        EntityCondition whereEntityCondition = EntityCondition.makeCondition(E.dataResourceTypeId.name(), EntityOperator.EQUALS, E.LOCAL_FILE.name());
        EntityListIterator it = delegator.find(E.DataResource.name(), whereEntityCondition, null, null, null, null);
        List<String> listOfUploadPaths = FastList.newInstance();
        // step 3 - retrieve list of records containing references to upload directory
        Debug.logImportant("Retrieve list of records containing references to upload directory", MODULE);
        try {
            GenericValue gv;
            while ((gv = it.next()) != null) 
            {
                String match = gv.getString(E.objectInfo.name());
                // Se trovo una stampa salto l'aggiornamento del path
                if(match.indexOf(RPTDESIGN_SUFFIX) > -1)
                	continue;
                
                if (match.indexOf(RUNTIME_PREFIX) > -1) {
                    String newPath = propValue + match.substring(match.indexOf(RUNTIME_PREFIX));
                    Debug.logImportant("Substitution of path " + match + "with new path " + newPath, MODULE);
                    gv.put(E.objectInfo.name(), newPath);
                    gv.store();
                    listOfUploadPaths.add(newPath);
                }
                else if(match.indexOf(HOTDEPLOY_PREFIX) > -1) {
                	String newPath = propValue + "/" +match.substring(match.indexOf(HOTDEPLOY_PREFIX));
                	Debug.logImportant("Substitution of path " + match + "with new path " + newPath, MODULE);
                    gv.put(E.objectInfo.name(), newPath);
                    gv.store();
                    listOfUploadPaths.add(newPath);
                }
                else if(match.indexOf(FRAMEWORK_PREFIX) > -1) {
            	String newPath = propValue + "/" +match.substring(match.indexOf(FRAMEWORK_PREFIX));
            	Debug.logImportant("Substitution of path " + match + "with new path " + newPath, MODULE);
                gv.put(E.objectInfo.name(), newPath);
                gv.store();
                listOfUploadPaths.add(newPath);
                }
                else if(match.indexOf(BASE_PREFIX) > -1) {
                	String newPath = propValue + "/hot-deploy" +match.substring(match.indexOf(BASE_PREFIX));
                	Debug.logImportant("Substitution of path " + match + "with new path " + newPath, MODULE);
                    gv.put(E.objectInfo.name(), newPath);
                    gv.store();
                    listOfUploadPaths.add(newPath);
                }
                else if(match.indexOf(THEME_PREFIX) > -1) {
                	String newPath = propValue + "/themes" +match.substring(match.indexOf(THEME_PREFIX));
                	Debug.logImportant("Substitution of path " + match + "with new path " + newPath, MODULE);
                    gv.put(E.objectInfo.name(), newPath);
                    gv.store();
                    listOfUploadPaths.add(newPath);
                }
            }
        } finally {
            it.close();
        }

        // step 4 - check actual presence of listed files in filesystem
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
