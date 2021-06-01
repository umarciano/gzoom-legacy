package com.mapsengineering.base.test.etl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.etl.EtlException;
import com.mapsengineering.base.etl.load.GenericLoader;
import com.mapsengineering.base.etl.load.LoadInterface;
import com.mapsengineering.base.test.BaseTestCase;

/**
 * Generic Test Elt Load
 *
 */
public class TestEtlLoad extends BaseTestCase {

    /**
     * Test Elt Load for dataSourceId TEST
     *
     */
    public void testGenericLoaderException() {
        testGenericLoader("TEST", true);
    }

    /**
     * Test Elt Load for dataSourceId TEST_1
     *
     */
    public void testGenericLoader() {
        testGenericLoader("TEST_1", false);
    }
    
    private void testGenericLoader(String dataSourceId, boolean isFound) {
        boolean found = false;
        boolean error = false;
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("dataSourceId", dataSourceId);

        List<GenericValue> transformed = null;

        try {
            transformed = delegator.findByAnd("DataSource", fields);
            found = true;
        } catch (GenericEntityException e1) {
            found = false;
        }

        LoadInterface genericLoader = new GenericLoader();

        try {
            genericLoader.execute(transformed);
        } catch (EtlException e) {
            error = true;
        }
        if (found && isFound) {
            assertTrue(error);
        } else {
            assertFalse(error);
        }
    }

}