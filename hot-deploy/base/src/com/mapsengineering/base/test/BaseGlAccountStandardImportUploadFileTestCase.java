package com.mapsengineering.base.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.ImportManagerUploadFile;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.util.MessageUtil;

public class BaseGlAccountStandardImportUploadFileTestCase extends BaseTestStandardImportUploadFile {
    public static final String MODULE = BaseGlAccountStandardImportUploadFileTestCase.class.getName();
    
    private enum E {
        JobLogServiceType, serviceTypeId, entityListToImport, STD_GLACCOUNTINTERFA, responseMessage, description
    };

    protected Map<String, Object> setContextAndRunGlAccountInterface(String nameFile, long blockingErrors, long recordElaborated, boolean isXls) {
        try {
            setContextGlAccountInterfaceFile(nameFile, isXls);
            
            Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
            Debug.log(" - result GlAccountInterface " + result);
            assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
            GenericValue serviceName = delegator.findOne(E.JobLogServiceType.name(), UtilMisc.toMap(E.serviceTypeId.name(), ImportManagerConstants.SERVICE_TYPE), true);
            manageResultList(result, "resultList", serviceName.getString(E.description.name()), blockingErrors, recordElaborated);
            return result;
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
        }
        return null;
    }

    protected void setContextGlAccountInterfaceFile(final String nameFile, final boolean isXls) throws Exception {
        getLoadContext(ImportManagerConstants.GL_ACCOUNT_INTERFACE, nameFile, isXls);
        context.put(E.entityListToImport.name(), ImportManagerConstants.GL_ACCOUNT_INTERFACE);
    }

}
