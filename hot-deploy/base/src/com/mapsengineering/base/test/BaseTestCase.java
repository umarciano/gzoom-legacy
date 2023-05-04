package com.mapsengineering.base.test;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastMap;
import junit.framework.TestCase;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;

/**
 * Sets up stuff for test unit
 * @author rime
 *
 */
public class BaseTestCase extends TestCase {

    protected static final String DELEGATOR_NAME = "test";
    protected static final String DISPATCHER_NAME = "test";
    protected static final String USER_LOGIN = "userLogin";
    protected static final String LOCALE = "locale";
    protected static final String LAST_LOCALE = "lastLocale";
    protected static final String TIME_ZONE = "timeZone";
    protected static final String SECURITY = "security";
    protected static final String COMPANY = "Company";

    protected Delegator delegator;
    protected LocalDispatcher dispatcher;
    protected Map<String, Object> context;

    protected void setUp() throws Exception {
        this.setUp(true);
    }
    
    protected void setUp(boolean withOrganizationParams) throws Exception {
        super.setUp();
        
        initDelegator();
        dispatcher = GenericDispatcher.getLocalDispatcher(DISPATCHER_NAME, delegator);

        context = FastMap.newInstance();
        GenericValue userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "admin"));
        context.put(USER_LOGIN, userLogin);
        context.put(LOCALE, Locale.ITALY);
        context.put(TIME_ZONE, TimeZone.getDefault());
        if (withOrganizationParams) {
        	context.put(GenericService.ORGANIZATION_ID, COMPANY);
        	context.put(GenericService.DEFAULT_ORGANIZATION_PARTY_ID, COMPANY); 
        }
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected void initDelegator() {
        String delegatorName = System.getProperty("delegator-name");
        if (UtilValidate.isEmpty(delegatorName)) {
            delegatorName = DELEGATOR_NAME;
        }
        delegator = DelegatorFactory.getDelegator(delegatorName);
    }

    /**
     * Poiche' i log vengoo scritti con un'unico jobLog, conviene utilizzare questo metodo per controllare il numero di : <br/>
     * errori bloccanti<br/>
     * record elaborati e <br/>
     * warning message
     * @param result
     * @param resultMapName
     * @param entityName
     * @param blockingErrors
     * @param recordElaborated
     */
    protected void manageResultList(Map<String, Object> result, String resultMapName, String entityName, long blockingErrors, long recordElaborated) {
        List<Map<String, Object>> resultListUploadFile = UtilGenerics.toList(result.get(resultMapName));
        boolean found = false;
        if (UtilValidate.isNotEmpty(resultListUploadFile)) {
            for (Map<String, Object> resultItemUploadFile : resultListUploadFile) {
                if (entityName.equals(resultItemUploadFile.get(ServiceLogger.ENTITY_NAME))) {
                    found = true;
                    manageResult(resultItemUploadFile, blockingErrors, recordElaborated);
                }
            }
        }
        if(!found) {
            // TODO
            Debug.log("ATTENZIONE result non ha trovato !");
        }
    }
    
    protected void manageAllResultList(Map<String, Object> res, String resultMapName, long totalBlockingErrors, long totalRecordElaborated) {
        Debug.log(" manageAllResultList with expected blockingErrors " + totalBlockingErrors + " and expected recordElaborated " + totalRecordElaborated);
        List<Map<String, Object>> result = UtilGenerics.toList(res.get(resultMapName));
        Long blockingErrors = 0L;
        Long recordElaborated = 0L;
        if (UtilValidate.isNotEmpty(result)) {
            for (Map<String, Object> resultItem : result) {
                Debug.log(" manageAllResultList resultItem " + resultItem + " with blockingErrors " + resultItem.get(ServiceLogger.BLOCKING_ERRORS) + " with recordElaborated " +resultItem.get(ServiceLogger.RECORD_ELABORATED));
                blockingErrors += (Long) resultItem.get(ServiceLogger.BLOCKING_ERRORS);
                recordElaborated += (Long) resultItem.get(ServiceLogger.RECORD_ELABORATED);
            }
        }
        assertEquals(totalBlockingErrors, blockingErrors.longValue());
        assertEquals(totalRecordElaborated, recordElaborated.longValue());
    }

    protected void manageResult(Map<String, Object> resultItem, long blockErr, long recElab) {
        Debug.log(" assertEquals resultItem " + resultItem + " for blockingErrors " + blockErr + " and recordElaborated " +recElab);
        assertEquals(blockErr, resultItem.get(ServiceLogger.BLOCKING_ERRORS));
        assertEquals(recElab, resultItem.get(ServiceLogger.RECORD_ELABORATED));
    }

    protected List<GenericValue> checkListSize(List<GenericValue> list, int minSize, int maxSize) {
        int size = list != null ? list.size() : 0;
        String msg = "list size " + size;
        boolean condition = true;
        if (minSize >= 0) {
            msg += "; >= " + minSize;
            condition &= (size >= minSize);
        }
        if (maxSize >= 0) {
            msg += "; <= " + maxSize;
            condition &= (size <= maxSize);
        }
        assertTrue(msg, condition);
        return list;
    }
}
