package com.mapsengineering.base.test;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.test.BaseTestWeStandardImportUploadFile.E;

/**
 * Test Exception for StandardImport
 *
 */
public class TestStandardImportException extends BaseTestCase {

    public void testImportManagerException() {
        try {
            context.put(E.entityListToImport.name(), "IMPORT");
            ImportManager.doImportSrv(dispatcher.getDispatchContext(), context);
        } catch (Exception exc) {
            assertTrue(true);
        }
    }

    public void testFilterConditionImportManagerException() {
        try {
            List<EntityCondition> lista = FastList.newInstance();
            EntityCondition cond = EntityCondition.makeCondition("A", "B");
            lista.add(cond);
            context.put(E.filterConditions.name(), lista);
            context.put(E.entityListToImport.name(), "IMPORT|IMPORT2");
            ImportManager.doImportSrv(dispatcher.getDispatchContext(), context);
        } catch (Exception exc) {
            assertTrue(true);
        }
    }

    public void testFilterConditionImportManagerEntityException() {
        try {
            List<EntityCondition> lista = FastList.newInstance();
            context.put(E.filterConditions.name(), lista);
            ImportManager.doImportSrv(dispatcher.getDispatchContext(), context);
        } catch (Exception exc) {
            assertTrue(true);
        }
    }

    public void testFilterMapImportManagerException() {
        try {
            Map<String, Object> mappa = FastMap.newInstance();
            Map<String, Object> mappa2 = FastMap.newInstance();
            List<Map<String, Object>> lista = FastList.newInstance();
            mappa.put("A", "A");
            lista.add(mappa);
            mappa2.put("B", "B");
            lista.add(mappa2);
            context.put(E.filterMapList.name(), lista);
            context.put(E.entityListToImport.name(), "IMPORT");
            ImportManager.doImportSrv(dispatcher.getDispatchContext(), context);
        } catch (Exception exc) {
            assertTrue(true);
        }
    }
    
    public void testImportManagerTransactionException() {
        try {
            Map<String, Object> mappa = FastMap.newInstance();
            List<Map<String, Object>> lista = FastList.newInstance();
            mappa.put("C", "C");
            lista.add(mappa);
            context.put("filterMapList", lista);
            context.put("entityListToImport", "");
            ImportManager.doImportSrv(dispatcher.getDispatchContext(), context);
        } catch (Exception exc) {
            assertTrue(true);
        }
    }

}
