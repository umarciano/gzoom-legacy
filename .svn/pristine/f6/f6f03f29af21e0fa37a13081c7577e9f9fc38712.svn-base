package com.mapsengineering.emplperf.test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.emplperf.update.ParamsEnum;
import com.mapsengineering.workeffortext.services.E;

/**
 * Test Massive creation of employment performance  
 *
 */
public class TestEmplPerfUpdateFromPrevious extends BaseTestInsertFromTemplate {
    public static final String MODULE = TestEmplPerfUpdateFromPrevious.class.getName();
    
    private static final String SERVICE_NAME_ALL = "executeEmplPerfUpdateFromPreviousAsync";
    private static final String SERVICE_NAME_NOTE = "executeEmplPerfUpdateWorkEffortNote";
    private static final String SERVICE_NAME_TRANS = "executeEmplPerfUpdateAcctgTrans";
    private static final String SERVICE_NAME_ASSOC = "executeEmplPerfUpdateWorkEffortAssoc";
  
    
    /**
     * Test for employment performance error workEffortPurposeTypeId
     */
    public void testEmplPerfAcctgTransException() {   	
        Debug.log(" - testEmplPerfAcctgTransException ");
        try {
            // 0 record beacuse already exists
            setContextAndRunInsertFromTemplate(BaseTestInsertFromTemplate.UORG_1, BaseTestInsertFromTemplate.ROLE_TYPE_ID_UOSET, BaseTestInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, BaseTestInsertFromTemplate.START_DATE_2014, BaseTestInsertFromTemplate.COMPLETION_DATE_2014, 0, 0);
            setContextUpdateFromPrevious("MVD-12", BaseTestInsertFromTemplate.TEMPLATE_ID_FOR_D, "PROGETTI", "VC12C", new Timestamp(UtilDateTime.toDate(12, 31, 2013, 0, 0, 0).getTime()), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));
            runServiceWithoutTypeParam(ParamsEnum.updateWorkEffortPurposeType.name());
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Test for employment performance error workEffortAssocType
     */
    public void testEmplPerfAssocException() {
        Debug.log(" - testEmplPerfAssocException ");
        try {
            // 0 record beacuse already exists
            setContextAndRunInsertFromTemplate(BaseTestInsertFromTemplate.UORG_1, BaseTestInsertFromTemplate.ROLE_TYPE_ID_UOSET, BaseTestInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, BaseTestInsertFromTemplate.START_DATE_2014, BaseTestInsertFromTemplate.COMPLETION_DATE_2014, 0, 0);
            setContextUpdateFromPrevious("MVD-12", BaseTestInsertFromTemplate.TEMPLATE_ID_FOR_D, "PROGETTI", "VC12C", new Timestamp(UtilDateTime.toDate(12, 31, 2013, 0, 0, 0).getTime()), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));
            runServiceWithoutTypeParam(ParamsEnum.updateWorkEffortAssocType.name());
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Test no param error
     * @param paramName
     */
    public void runServiceWithoutTypeParam(String paramName) {
    	Debug.log(" - runServiceWithoutTypeParam ");
        try {
            Map<String, Object> serviceMap = dispatcher.getDispatchContext().makeValidContext(SERVICE_NAME_ALL, "IN", context);
            serviceMap.remove(paramName);
            Map<String, Object> result = dispatcher.runSync(SERVICE_NAME_ALL, serviceMap);
            Debug.log(" - result " + result);
            assertFalse(ServiceUtil.isSuccess(result));
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
            assertTrue(false);
        }
    }
    
    /**
     * Test for employment performance
     */
    public void testEmplPerfUpdateWorkEffortNoteFromPrevious() {
        Debug.log(" - testEmplPerfUpdateWorkEffortNoteFromPrevious ");
        try {
         // 0 record beacuse already exists
            setContextAndRunInsertFromTemplate(BaseTestInsertFromTemplate.UORG_1, BaseTestInsertFromTemplate.ROLE_TYPE_ID_UOSET, BaseTestInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, BaseTestInsertFromTemplate.START_DATE_2014, BaseTestInsertFromTemplate.COMPLETION_DATE_2014, 0, 0);
            setContextUpdateFromPrevious("MVD-12", BaseTestInsertFromTemplate.TEMPLATE_ID_FOR_D, "PROGETTI", "VC12C", new Timestamp(UtilDateTime.toDate(12, 31, 2013, 0, 0, 0).getTime()), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));
            EntityCondition extraConditionAfter = EntityCondition.makeCondition(E.templateId.name(), BaseTestInsertFromTemplate.TEMPLATE_ID_FOR_D);
            // "RCP10022"
            List <GenericValue> listEmplPerfForEvaluated = getWorkEffortEmplPerfForEvaluated(PERSONA, new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()), extraConditionAfter);
            GenericValue emplPerfForEvaluated = EntityUtil.getFirst(listEmplPerfForEvaluated);
            Debug.log("emplPerfForEvaluated " + emplPerfForEvaluated);
            createNote(BaseTestInsertFromTemplate.TEMPLATE_ID_FOR_D, emplPerfForEvaluated.getString(E.workEffortId.name()), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()), "to override");
            
            listEmplPerfForEvaluated = getWorkEffortEmplPerfForEvaluated(PERSONA, new Timestamp(UtilDateTime.toDate(12, 31, 2013, 0, 0, 0).getTime()), extraConditionAfter);
            emplPerfForEvaluated = EntityUtil.getFirst(listEmplPerfForEvaluated);
            // crea vecchia nota da copiare
            createNote(BaseTestInsertFromTemplate.TEMPLATE_ID_FOR_D, emplPerfForEvaluated.getString(E.workEffortId.name()), new Timestamp(UtilDateTime.toDate(12, 31, 2013, 0, 0, 0).getTime()), "override");
            
            Map<String, Object> serviceMap = dispatcher.getDispatchContext().makeValidContext(SERVICE_NAME_NOTE, "IN", context);
            serviceMap.put(ParamsEnum.readDate.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2013, 0, 0, 0).getTime()));
            Debug.log(" - testEmplPerfUpdateWorkEffortNoteFromPrevious serviceMap " + serviceMap);
            Map<String, Object> result = dispatcher.runSync(SERVICE_NAME_NOTE, serviceMap);
            Debug.log(" - testEmplPerfUpdateWorkEffortNoteFromPrevious " + result);
            manageResult(result, 0l, 1l);
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
            assertTrue(false);
        }
    }
    
    /**
     * Test for employment performance
     */
    public void testEmplPerfUpdateAcctgTransFromPrevious() {
        Debug.log(" - testEmplPerfUpdateAcctgTransFromPrevious ");
        try {
            // 0 record beacuse already exists
            setContextAndRunInsertFromTemplate(BaseTestInsertFromTemplate.UORG_1, BaseTestInsertFromTemplate.ROLE_TYPE_ID_UOSET, BaseTestInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, BaseTestInsertFromTemplate.START_DATE_2014, BaseTestInsertFromTemplate.COMPLETION_DATE_2014, 0, 0);
            setContextUpdateFromPrevious("MVD-12", BaseTestInsertFromTemplate.TEMPLATE_ID_FOR_D, "PROGETTI", "VC12C", new Timestamp(UtilDateTime.toDate(12, 31, 2013, 0, 0, 0).getTime()), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));
            Map<String, Object> serviceMap = dispatcher.getDispatchContext().makeValidContext(SERVICE_NAME_TRANS, "IN", context);
            Debug.log(" - testEmplPerfUpdateAcctgTransFromPrevious serviceMap " + serviceMap);
            Map<String, Object> result = dispatcher.runSync(SERVICE_NAME_TRANS, serviceMap);
            Debug.log(" - testEmplPerfUpdateAcctgTransFromPrevious " + result);
            manageResult(result, 0l, 0l);
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
            assertTrue(false);
        }
    }
    
    /**
     * Test for employment performance
     */
    public void testEmplPerfUpdateAll() {
        Debug.log(" - testEmplPerfUpdateAll ");
        try {
         // 0 record beacuse already exists
            setContextAndRunInsertFromTemplate(BaseTestInsertFromTemplate.UORG_1, BaseTestInsertFromTemplate.ROLE_TYPE_ID_UOSET, BaseTestInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, BaseTestInsertFromTemplate.START_DATE_2014, BaseTestInsertFromTemplate.COMPLETION_DATE_2014, 0, 0);
            setContextUpdateFromPrevious("MVD-12", BaseTestInsertFromTemplate.TEMPLATE_ID_FOR_D, "PROGETTI", "VC12C", new Timestamp(UtilDateTime.toDate(12, 31, 2013, 0, 0, 0).getTime()), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));
            Map<String, Object> serviceMap = dispatcher.getDispatchContext().makeValidContext(SERVICE_NAME_TRANS, "IN", context);
            serviceMap.put(ParamsEnum.updateWorkEffortPurposeType.name(), "APPR");
            Map<String, Object> result = dispatcher.runSync(SERVICE_NAME_TRANS, serviceMap);
            Debug.log(" - testEmplPerfUpdateAll 1 " + result);
            manageResult(result, 0l, 0l);
            
            
            EntityCondition extraConditionAfter = EntityCondition.makeCondition(E.templateId.name(), BaseTestInsertFromTemplate.TEMPLATE_ID_FOR_D);
            // "RCP10022"
            List <GenericValue> listEmplPerfForEvaluated = getWorkEffortEmplPerfForEvaluated(null, new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()), extraConditionAfter);
            GenericValue emplPerfForEvaluated = EntityUtil.getFirst(listEmplPerfForEvaluated);
            createMovement(emplPerfForEvaluated.getString(E.workEffortId.name()), "F2014Y");
            
            listEmplPerfForEvaluated = getWorkEffortEmplPerfForEvaluated(null, new Timestamp(UtilDateTime.toDate(12, 31, 2013, 0, 0, 0).getTime()), extraConditionAfter);
            emplPerfForEvaluated = EntityUtil.getFirst(listEmplPerfForEvaluated);
            createMovement(emplPerfForEvaluated.getString(E.workEffortId.name()), "F2013Y");
            
            Debug.log(" - testEmplPerfUpdateAll 2 serviceMap " + serviceMap);
            result = dispatcher.runSync(SERVICE_NAME_TRANS, serviceMap);
            Debug.log(" - testEmplPerfUpdateAll 2 " + result);
            manageResult(result, 0l, 6l);
    
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
            assertTrue(false);
        }
    }
    
    /**
     * Test for copy workeffortAssoc of employment performance
     */
    public void testEmplPerfUpdateWorkEffortAssocFromPrevious() {
        Debug.log(" - testEmplPerfUpdateWorkEffortAssocFromPrevious");
        try {
            Map<String, Object> serviceMap = dispatcher.getDispatchContext().makeValidContext(SERVICE_NAME_ASSOC, "IN", context);
            
            serviceMap.put(ParamsEnum.readDate.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2016, 0, 0, 0).getTime()));
            serviceMap.put(ParamsEnum.updateWorkEffortAssocType.name(), "13INC");
            serviceMap.put(E.estimatedStartDate.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2017, 0, 0, 0).getTime()));
            serviceMap.put(E.estimatedCompletionDate.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2017, 0, 0, 0).getTime()));
            // 0 record beacuse already exists
        
            Debug.log(" - testEmplPerfUpdateWorkEffortAssocFromPrevious serviceMap " + serviceMap);
            Map<String, Object> result = dispatcher.runSync(SERVICE_NAME_ASSOC, serviceMap);
            Debug.log(" - testEmplPerfUpdateWorkEffortAssocFromPrevious " + result);
            manageResult(result, 0l, 1l);
            
            List<GenericValue> list = delegator.findList(E.WorkEffort.name(), EntityCondition.makeCondition(E.workEffortParentId.name(), "B159025"), null, null, null, false);
            Debug.log("Found " + list.size() + " WorkEffort with condition = " + EntityCondition.makeCondition(E.workEffortParentId.name(), "B159025"));

            for (GenericValue workEffort : list) {
                Debug.log("Found id = " + workEffort.getString(E.workEffortId.name()) + "[" + workEffort.getString(E.workEffortName.name()) + "] from " + workEffort.getTimestamp(E.estimatedStartDate.name()) + " - " + workEffort.getTimestamp(E.estimatedCompletionDate.name()));
            }
            assertEquals(9, list.size());
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
            assertTrue(false);
        }
    }
}
