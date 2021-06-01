package com.mapsengineering.base.test;

import java.sql.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.bl.crud.AbstractCrudHandler;
import com.mapsengineering.base.bl.crud.FkValidationHandler;
import com.mapsengineering.base.bl.crud.TypeValidationHandler;
import com.mapsengineering.base.bl.crud.ValueValidationHandler;
import com.mapsengineering.base.bl.crud.AbstractCrudHandler.Operation;

public class TestValidationHandlers extends GplusTestCase {

    private String entityName = "TestingGplus";
    private Map<String, Object> parameters = FastMap.newInstance();
    private Locale locale = Locale.ITALIAN;

    protected void setUp() throws Exception {
        super.setUp();
    }


    @SuppressWarnings("deprecation")
    public void testTypeValidationHandler() {
        try {
            entityName = "TestingGplus";

            List<GenericValue> allRows = delegator.findList(entityName, null, null, null, null, true);
            if (UtilValidate.isEmpty(allRows)) {
                throw new Exception("Entity " + entityName +" vuota, test non eseguibile");
            }

            //Set PK
            parameters.putAll( allRows.get(allRows.size()-1).getPrimaryKey().getAllFields() );

            //Name é obbligatorio , quindi mi deve restituire errore
            parameters.put("name", "");
            AbstractCrudHandler ach = new TypeValidationHandler();
            assertFalse(ach.execute(delegator, entityName, Operation.UPDATE.name(), locale, null, parameters, context));

            parameters.put("name", "Test");

            //Description non é modificabile, però il servizio in un caso come questo non restituisce error
            //ma toglie il campo dai parametri. Quindi non devo + trovarmelo nella lista parametri
            parameters.put("description", "Test create ----");
            assertTrue(ach.execute(delegator, entityName, Operation.UPDATE.name(), locale, null, parameters, context));
            assertTrue(UtilValidate.isEmpty(parameters.get("description")));

            //Aggiornamento OK
            parameters.put("comments", "TestTypeValidationHandler");
            assertTrue(ach.execute(delegator, entityName, Operation.UPDATE.name(), locale, null, parameters, context));

        } catch (Exception e) {
            assertNull("Errore esecuzione test, eccezione: " + e.getMessage(), e);
        }

    }


    public void testFkValidationHandler() {

        //Utilizzo questo perché ha una Fk con + di un campo
        entityName = "Employment";
        parameters.clear();


        parameters.put("partyIdTo", "admin");

        AbstractCrudHandler ach = new FkValidationHandler();
        //Deve dare errore
        assertFalse(ach.execute(delegator, entityName, Operation.UPDATE.name(), locale, null, parameters, context));
    }


    public void testValueValidationHandler() {
        try {
            entityName = "TestingGplus";

            parameters.clear();

            //Name é obbligatorio , quindi mi deve restituire errore
            parameters.put("numeric", "1234");
            parameters.put("datefrom", Date.valueOf("2008-12-14"));
            parameters.put("datethru", Date.valueOf("2008-12-12"));

            AbstractCrudHandler ach = new ValueValidationHandler();
            assertFalse(ach.execute(delegator, entityName, Operation.UPDATE.name(), locale, null, parameters, context));

            parameters.put("name", "Test");
            parameters.put("numeric", "1234");
            //Deve dare errore sulle date
            parameters.put("datefrom", Date.valueOf("2008-12-13"));
            parameters.put("datethru", Date.valueOf("2008-12-12"));
            assertFalse(ach.execute(delegator, entityName, Operation.UPDATE.name(), locale, null, parameters, context));

            //Ok
            parameters.put("name", "Test");
            parameters.put("numeric", "1234");
            parameters.put("datefrom", Date.valueOf("2008-12-12"));
            parameters.put("datethru", Date.valueOf("2008-12-13"));
            assertTrue(ach.execute(delegator, entityName, Operation.UPDATE.name(), locale, null, parameters, context));

        } catch (Exception e) {
            assertNull("Errore esecuzione test, eccezione: " + e.getMessage(), e);
        }

    }


}
