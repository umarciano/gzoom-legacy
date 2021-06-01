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
public class BaseOrganizationStandardImportUploadFileTestCase extends BaseTestStandardImportUploadFile {
    public static final String MODULE = BaseOrganizationStandardImportUploadFileTestCase.class.getName();
    
    protected Map<String, Object> setContextAndRunOrganizationInterfaceUpdate(String nameFile, long blockingErrors, long recordElaborated) {
        try {
            setContextOrganizationInterfaceFile(nameFile);
            
            Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
            Debug.log(" - result OrganizationInterfaceInsert " + result);
            assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
            manageResultList(result, "resultList", "Importazione Unit\\u00E0 Organizzative Standard", blockingErrors, recordElaborated);
            return result;
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
        }
        return null;
    }

    protected void setContextOrganizationInterfaceFile(final String nameFile) throws Exception {
        getLoadContext(ImportManagerConstants.ORGANIZATION_INTERFACE, nameFile);
        context.put(E.entityListToImport.name(), ImportManagerConstants.ORGANIZATION_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.ORG_RESP_INTERFACE);
    }
    
    /**
     * ritorna il partyId dal partyCode
     * @param partyCode
     * @return
     */
    protected String getPartyId(String partyCode) throws GenericEntityException {
    	String partyId = "";
    	List<GenericValue> partyParentRoleList = delegator.findList(E.PartyParentRole.name(), EntityCondition.makeCondition(E.parentRoleCode.name(), partyCode), null, null, null, false);
    	Debug.log(" - partyParentRoleList " + partyParentRoleList);
    	GenericValue partyParentRole = EntityUtil.getFirst(partyParentRoleList); 	
    	if (UtilValidate.isNotEmpty(partyParentRole)) {
            partyId = (String) partyParentRole.get(E.partyId.name());
    	}  	
        return partyId;
    }
    
    public void checkPartyRelationship(String partyId, int expectedFrom, String partyIdTo, Timestamp thruDateTo, int expectedTo, String partyIdFrom, Timestamp thruDateFrom, int expectedResp, String partyIdResp, Timestamp thruDateResp) throws GenericEntityException {
    	List<EntityCondition> relOrgConditionList = FastList.newInstance();
    	relOrgConditionList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.GROUP_ROLLUP.name()));
    	relOrgConditionList.add(EntityCondition.makeCondition(E.partyIdFrom.name(), partyId));

    	List<GenericValue> lista = delegator.findList("PartyRelationship", EntityCondition.makeCondition(relOrgConditionList), null, null, null, false);
        Debug.log(" - lista GROUP_ROLLUP from " + lista);
        assertEquals(expectedFrom, lista.size());
        if (UtilValidate.isNotEmpty(partyIdTo)) {
            for (GenericValue gv : lista) {
                assertEquals(partyIdTo, gv.getString(E.partyIdTo.name()));
                assertEquals(thruDateTo, gv.getTimestamp(E.thruDate.name()));
            }
        }
        
        List<EntityCondition> relAllConditionList = FastList.newInstance();
        relAllConditionList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.GROUP_ROLLUP.name()));
        relAllConditionList.add(EntityCondition.makeCondition(E.partyIdTo.name(), partyId));
    	lista = delegator.findList("PartyRelationship", EntityCondition.makeCondition(relAllConditionList), null, null, null, false);
        Debug.log(" - lista GROUP_ROLLUP to " + lista);
        assertEquals(expectedTo, lista.size());
        if (UtilValidate.isNotEmpty(partyIdFrom)) {
            for (GenericValue gv : lista) {
                assertEquals(partyIdFrom, gv.getString(E.partyIdFrom.name()));
                assertEquals(thruDateFrom, gv.getTimestamp(E.thruDate.name()));
            }
        }
        
        List<EntityCondition> relRespConditionList = FastList.newInstance();
        relRespConditionList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.ORG_RESPONSIBLE.name()));
        relRespConditionList.add(EntityCondition.makeCondition(E.partyIdFrom.name(), partyId));

    	lista = delegator.findList("PartyRelationship", EntityCondition.makeCondition(relRespConditionList), null, null, null, false);
        Debug.log(" - lista ORG_RESPONSIBLE " + lista);
        assertEquals(expectedResp, lista.size());
        if (UtilValidate.isNotEmpty(partyIdResp)) {
            for (GenericValue gv : lista) {
                assertEquals(partyIdResp, gv.getString(E.partyIdTo.name()));
                assertEquals(thruDateResp, gv.getTimestamp(E.thruDate.name()));
            }
        }
    }

}
