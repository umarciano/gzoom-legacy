package com.mapsengineering.base.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.ImportManager;

/**
 * Test WorkEffort StandardImport from xml
 *
 */
public class TestWorkEffortStandardImport extends BaseTestCase {

    /**
     * Enumeration of field
     *
     */
    public enum E {
        entityListToImport, responseMessage, resultList, //
        WeRootInterface, WorkEffort, //
        sourceReferenceId, specialTerms
    }

    public static final char UNDERSCORE = '_';

    /**
     * Test WeRootInterface and special Terms
     * @throws GenericEntityException
     */
    public void testWorkEffortRootInterface() throws GenericEntityException {
        context.put(E.entityListToImport.name(), E.WeRootInterface.name());
        Map<String, Object> result = ImportManager.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result " + result);
        assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
        checkSpecialTerms("BSC30");
        checkSpecialTerms("BSC31");
        checkSpecialTerms("BSC30.TMN");
        checkSpecialTerms("BSC31.EFF");
        manageResultList(result, E.resultList.name(), "Importazione Schede", 37, 71);
    }

    private void checkSpecialTerms(String sourceReferenceId) throws GenericEntityException {
        GenericValue gv = EntityUtil.getFirst(checkListSize(delegator.findByAnd(E.WorkEffort.name(), UtilMisc.toMap(E.sourceReferenceId.name(), sourceReferenceId)), 1, 1));
        assertEquals(sourceReferenceId + UNDERSCORE + E.specialTerms.name(), gv.getString(E.specialTerms.name()));
    }
}
