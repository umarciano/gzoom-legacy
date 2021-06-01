package com.mapsengineering.base.test;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.ImportManagerUploadFile;

/**
 * Test WorkEffort StandardImport from excel
 *
 */
public class TestWeStandardImportUploadFileForClone extends BaseTestWeStandardImportUploadFile {

    private static final String SOURCE_REFERENCE_ID_FROM = "15A.P01.1";
    private static final String SOURCE_REFERENCE_ID_TO = "15A.R01";
    private static final String WORK_EFFORT_ASSOC_TYPE_ID = "15APPRO";

    /**
     * Test Standard Import for workEffort with File for clone
     * @throws Exception
     */
    public void testAllWeInterfaceUploadFileClone() throws Exception {
        final String nameFile = "WeRootInterface_Clone.xls";
        getLoadContext(E.WeRootInterface.name(), nameFile);
        final String nameFile2 = "WeInterface_Clone.xls";
        getLoadContext(E.WeInterface.name(), nameFile2);
        final String nameFile3 = "WeAssocInterface_Clone.xls";
        getLoadContext(E.WeAssocInterface.name(), nameFile3);
        context.put(E.entityListToImport.name(), E.WeRootInterface.name() + "|" + E.WeInterface.name() + "|" + E.WeAssocInterface.name() + "|" + E.WeMeasureInterface.name() + "|" + E.WePartyInterface.name() + "|" + E.WeNoteInterface.name());
        Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result " + result);
        assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));

        manageResultListWe(result, 0, 3, 0, 3); // TODO controllare altri risultati

        List<GenericValue> listaFrom = delegator.findList("WorkEffort", EntityCondition.makeCondition(E.sourceReferenceId.name(), SOURCE_REFERENCE_ID_FROM), null, null, null, false);
        GenericValue workEffortFrom = EntityUtil.getFirst(listaFrom);
        String workEffortIdFrom = workEffortFrom.getString(E.workEffortId.name());
        
        // il workEffort orignale che viene clonato e' il 15A.R01
        List<GenericValue> listaOrig = delegator.findList("WorkEffort", EntityCondition.makeCondition(E.sourceReferenceId.name(), SOURCE_REFERENCE_ID_TO), null, null, null, false);
        GenericValue workEffortOrig = EntityUtil.getFirst(listaOrig);
        String workEffortName = workEffortOrig.getString(E.workEffortName.name());
        
        List<GenericValue> listaTo = delegator.findList("WorkEffort", EntityCondition.makeCondition(EntityCondition.makeCondition(E.workEffortName.name(), workEffortName), EntityCondition.makeCondition(E.sourceReferenceId.name(), EntityJoinOperator.NOT_EQUAL, SOURCE_REFERENCE_ID_TO)) , null, null, null, false);
        GenericValue workEffortTo = EntityUtil.getFirst(listaTo);
        String workEffortIdTo = workEffortTo.getString(E.workEffortId.name());
        
        List<EntityCondition> condList = FastList.newInstance();
        condList.add(EntityCondition.makeCondition(E.workEffortIdFrom.name(), workEffortIdFrom));
        condList.add(EntityCondition.makeCondition(E.workEffortIdTo.name(), workEffortIdTo));
        condList.add(EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), WORK_EFFORT_ASSOC_TYPE_ID));
        List<GenericValue> listaAssoc = delegator.findList("WorkEffortAssoc", EntityCondition.makeCondition(condList), null, null, null, false);
        Debug.log(" - listaAssoc " + listaAssoc);

        assertEquals(1, listaAssoc.size());
    }
}
