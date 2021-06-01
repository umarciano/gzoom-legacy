package com.mapsengineering.base.test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.ImportManagerUploadFile;
import com.mapsengineering.base.util.MessageUtil;

import javolution.util.FastList;

/**
 * Base PersonStandardImportUploadFile
 *
 */
public class BasePersonStandardImportUploadFileTestCase extends BaseTestStandardImportUploadFile {
    public static final String MODULE = BasePersonStandardImportUploadFileTestCase.class.getName();
    
    protected Map<String, Object> setContextAndRunPersonInterfaceUpdate(String nameFile, long blockingErrors, long recordElaborated, boolean endYearElab) {
        if (endYearElab) {
            context.put(E.checkEndYearElab.name(), E.endYearElab.name());
        } else {
            context.remove(E.checkEndYearElab.name());
        }
        try {
            setContextPersonInterfaceFile(nameFile);
            
            Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
            Debug.log(" - result PersonInterfaceInsert " + result);
            assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
            manageResultList(result, "resultList", "Importazione Risorse Umane Standard", blockingErrors, recordElaborated);
            return result;
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
        }
        return null;
    }

    
    protected Map<String, Object> setContextAndRunPersonInterfaceUpdate(String nameFile, long blockingErrors, long recordElaborated) {
        return setContextAndRunPersonInterfaceUpdate(nameFile, blockingErrors, recordElaborated, false);
    }

    protected void setContextPersonInterfaceFile(final String nameFile) throws Exception {
        getLoadContext(ImportManagerConstants.PERSON_INTERFACE, nameFile);
        context.put(E.entityListToImport.name(), ImportManagerConstants.PERSON_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.PERS_RESP_INTERFACE);
    }
    
    /**
     * ritorna il partyId dal partyCode
     * @param partyCode
     * @return
     */
    protected String getPartyId(String partyCode) throws GenericEntityException {
    	String partyId = "";
    	List<GenericValue> partyParentRoleList = delegator.findList(E.PartyParentRole.name(), EntityCondition.makeCondition(E.parentRoleCode.name(), partyCode), null, null, null, false);
    	GenericValue partyParentRole = EntityUtil.getFirst(partyParentRoleList); 	
    	if (UtilValidate.isNotEmpty(partyParentRole)) {
            partyId = (String) partyParentRole.get(E.partyId.name());
    	}  	
        return partyId;
    }
    
    public void checkPartyRelationship(String partyId, int expectedEmpl, String partyIdEmpl, Timestamp thruDateEmpl, int expectedAllo, String partyIdAllo, Timestamp thruDateAllo, int expectedEval, String partyIdEval, Timestamp thruDateEval, int expectedAppr, String partyIdAppr, Timestamp thruDateAppr) throws GenericEntityException {
        List<EntityCondition> relOrgConditionList = FastList.newInstance();
    	relOrgConditionList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.ORG_EMPLOYMENT.name()));
    	relOrgConditionList.add(EntityCondition.makeCondition(E.partyIdTo.name(), partyId));
    	List<GenericValue> lista = delegator.findList("PartyRelationship", EntityCondition.makeCondition(relOrgConditionList), null, null, null, false);
        Debug.log(" - lista ORG_EMPLOYMENT " + lista);
        assertEquals(expectedEmpl, lista.size());
        if (UtilValidate.isNotEmpty(partyIdEmpl)) {
            for (GenericValue gv : lista) {
                assertEquals(partyIdEmpl, gv.getString(E.partyIdFrom.name()));
                assertEquals(thruDateEmpl, gv.getTimestamp(E.thruDate.name()));
            }
        }
        
        List<EntityCondition> relAllConditionList = FastList.newInstance();
        relAllConditionList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.ORG_ALLOCATION.name()));
        relAllConditionList.add(EntityCondition.makeCondition(E.partyIdTo.name(), partyId));
    	lista = delegator.findList("PartyRelationship", EntityCondition.makeCondition(relAllConditionList), null, null, null, false);
        Debug.log(" - lista ORG_ALLOCATION " + lista);
        assertEquals(expectedAllo, lista.size());
        if (UtilValidate.isNotEmpty(partyIdAllo)) {
            for (GenericValue gv : lista) {
                assertEquals(partyIdAllo, gv.getString(E.partyIdFrom.name()));
                assertEquals(thruDateAllo, gv.getTimestamp(E.thruDate.name()));
            }
        }
        
        List<EntityCondition> relRespConditionList = FastList.newInstance();
        relRespConditionList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.WEF_EVALUATED_BY.name()));
        relRespConditionList.add(EntityCondition.makeCondition(E.partyIdFrom.name(), partyId));

    	lista = delegator.findList("PartyRelationship", EntityCondition.makeCondition(relRespConditionList), null, null, null, false);
        Debug.log(" - lista WEF_EVALUATED_BY " + lista);
        assertEquals(expectedEval, lista.size());
        if (UtilValidate.isNotEmpty(partyIdEval)) {
            for (GenericValue gv : lista) {
                assertEquals(partyIdEval, gv.getString(E.partyIdTo.name()));
                assertEquals(thruDateEval, gv.getTimestamp(E.thruDate.name()));
            }
        }
        
        List<EntityCondition> relApprConditionList = FastList.newInstance();
        relApprConditionList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.WEF_APPROVED_BY.name()));
        relApprConditionList.add(EntityCondition.makeCondition(E.partyIdFrom.name(), partyId));

        lista = delegator.findList("PartyRelationship", EntityCondition.makeCondition(relApprConditionList), null, null, null, false);
        Debug.log(" - lista WEF_APPROVED_BY " + lista);
        assertEquals(expectedAppr, lista.size());
        if (UtilValidate.isNotEmpty(partyIdAppr)) {
            for (GenericValue gv : lista) {
                assertEquals(partyIdAppr, gv.getString(E.partyIdTo.name()));
                assertEquals(thruDateAppr, gv.getTimestamp(E.thruDate.name()));
            }
        }
    }

}
