package com.mapsengineering.base.standardimport.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;

/**
 * Helper Product, Party
 *
 */
public class GlAccountPurposeInterfaceHelper {
    private TakeOverService takeOverService;
    private LocalDispatcher dispatcher;
    private Delegator delegator;

    /**
     * Constructor
     * @param takeOverService
     * @param dispatcher
     * @param delegator
     */
    public GlAccountPurposeInterfaceHelper(TakeOverService takeOverService, LocalDispatcher dispatcher, Delegator delegator) {
        this.takeOverService = takeOverService;
        this.dispatcher = dispatcher;
        this.delegator = delegator;
    }

    /**
     * Dato in ingresso un genericValue che contiene il productCode cerco il producId
     * @param acc
     * @param getEntityName
     * @return productId
     * @throws GeneralException
     */
    public String doImportProduct(GenericValue acc, String getEntityName) throws GeneralException {
        // 1.4. Search Service/Product if it is in input:
        String msg = "";
        String productCode = acc.getString("productId");
        String productId = "";
        if (UtilValidate.isNotEmpty(productCode)) {
            List<GenericValue> products = delegator.findByAnd("Product", UtilMisc.toMap("productMainCode", productCode));
            GenericValue product = EntityUtil.getFirst(products);
            if (UtilValidate.isEmpty(product)) {
                msg = "The productId " + productCode + ImportManagerConstants.STR_IS_NOT_VALID;
                throw new ImportException(getEntityName, acc.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }
            productId = product.getString("productId");
        }
        return productId;
    }

    /**
     * Dato in ingresso un genericValue che contiene il partyIdCdc cerco il partyId
     * @param acc
     * @return partyId
     * @throws GeneralException
     */
    public String doImportCdcParty(GenericValue acc) throws GeneralException {
        // 1.5 Search partyId through partyIdCdc
        // it is partyId and roleTypeId for Cdc (Centro di Costo)
        String partyIdCdc = acc.getString("partyIdCdc");
        String partyId = "";
        if (UtilValidate.isNotEmpty(partyIdCdc)) {
            List<GenericValue> parents = delegator.findList("PartyParentRole", EntityCondition.makeCondition("parentRoleCode", partyIdCdc), null, null, null, false);
            GenericValue parent = EntityUtil.getFirst(parents);
            if (UtilValidate.isNotEmpty(parent)) {
                partyId = parent.getString(E.partyId.name());
            }
        }
        return partyId;
    }

    /**
     * Dato in ingresso un genericValue che contiene il partyIdCdr cerco il respCenterId
     * @param acc
     * @return respCenterId
     * @throws GeneralException
     */
    public String doImportCdrParty(GenericValue acc) throws GeneralException {
        // 1.6 Search respCenterId through partyIdCdr
        // It is partyId for Cdr (Centro di Responsabile)
        String partyIdCdr = acc.getString("partyIdCdr");
        String respCenterId = "";
        if (UtilValidate.isNotEmpty(partyIdCdr)) {
            List<GenericValue> parents = delegator.findList("PartyParentRole", EntityCondition.makeCondition("parentRoleCode", partyIdCdr), null, null, null, false);
            GenericValue parent = EntityUtil.getFirst(parents);
            if (UtilValidate.isNotEmpty(parent)) {
                respCenterId = parent.getString(E.partyId.name());
            }
        }
        return respCenterId;
    }

    /**
     *  
     * @param acc
     * @param partyId
     * @param parentRoleTypeIdDefault
     * @return roleTypeId
     * @throws GeneralException
     */
    public String doImportCdcRole(GenericValue acc, String partyId, String parentRoleTypeIdDefault) throws GeneralException {
        // 1.4 Search roleTypeId through uorgRoleTypeId
        // it is roleTypeId for Cdc (Centro di Costo)
        String roleTypeId = acc.getString("roleTypeIdCdc");
        return importPartyRole(roleTypeId, partyId, parentRoleTypeIdDefault, "Cdc");
    }

    /**
     * 
     * @param acc
     * @param respCenterId
     * @param parentRoleTypeIdDefault
     * @return
     * @throws GeneralException
     */
    public String doImportCdrRole(GenericValue acc, String respCenterId, String parentRoleTypeIdDefault) throws GeneralException {
        // 1.6 Recuperare roleTypeId tramite uorgRoleTypeId
        // It is roleTypeId for Cdr (Centro di Responsabile)
        String respCenterRoleTypeId = acc.getString("roleTypeIdCdr");
        return importPartyRole(respCenterRoleTypeId, respCenterId, parentRoleTypeIdDefault, "Cdr");
    }

    /**
     * 
     * @param roleTypeId
     * @param partyId
     * @param parentRoleTypeIdDefault
     * @return
     * @throws GeneralException
     */
    private String importPartyRole(String roleTypeId, String partyId, String parentRoleTypeIdDefault, String type) throws GeneralException {
        String roleTypeIdRet = "";

        if (UtilValidate.isNotEmpty(partyId)) {
            GenericValue roleType;
            if (UtilValidate.isNotEmpty(roleTypeId)) {
                roleType = delegator.findOne(E.PartyRole.name(), UtilMisc.toMap(E.partyId.name(), partyId, E.roleTypeId.name(), roleTypeId), true);
            } else {
                List<GenericValue> roleTypes = delegator.findList(E.PartyRole.name(), EntityCondition.makeCondition(UtilMisc.toMap(E.partyId.name(), partyId, E.parentRoleTypeId.name(), parentRoleTypeIdDefault)), null, null, null, false);
                roleType = EntityUtil.getFirst(roleTypes);
            }
            if (UtilValidate.isEmpty(roleType)) {
                throwImportRoleError(roleTypeId, partyId, type);
            }
            roleTypeIdRet = roleType.getString(E.roleTypeId.name());
        }

        String msg = type + " : partyId = " + partyId + " and roleTypeId " + roleTypeIdRet;
        takeOverService.addLogInfo(msg);

        return roleTypeIdRet;
    }

    /**
     * messaggio di errore e lancio eccezione se il ruolo non e' valido
     * @param roleTypeId
     * @param partyId
     * @param type
     * @throws GeneralException
     */
    private void throwImportRoleError(String roleTypeId, String partyId, String type) throws GeneralException {
        String msg = type + " : ";
        if (UtilValidate.isNotEmpty(roleTypeId)) {
            msg += "roleTypeId " + roleTypeId + " for accountCode " + takeOverService.getExternalValue().getString(E.accountCode.name()) + " and partyId " + partyId + " " + ImportManagerConstants.STR_IS_NOT_VALID;
        } else {
            msg += "roleTypeId for accountCode " + takeOverService.getExternalValue().getString(E.accountCode.name()) + " and partyId " + partyId + " not found";
        }
        throw new ImportException(takeOverService.getEntityName(), takeOverService.getExternalValue().getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
    }

    /**
     * crea i GlAccountOrganization
     * @param glAccountId
     * @throws GeneralException
     */    
     public void doImportGlAccountOrganization(String glAccountId) throws GeneralException {
        List<GenericValue> organizationList = delegator.findList(E.PartyAcctgPreferenceView.name(), null, null, null, null, false);
        if (UtilValidate.isNotEmpty(organizationList)) {
        	for (GenericValue org : organizationList) {
        		GenericValue glo = delegator.findOne(E.GlAccountOrganization.name(), UtilMisc.toMap(E.glAccountId.name(), glAccountId, E.organizationPartyId.name(), org.getString(E.partyId.name())), false);
        		if (UtilValidate.isEmpty(glo)) {
        			Map<String, Object> gloParams = new HashMap<String, Object>();
        			gloParams.put(E.glAccountId.name(), glAccountId);
        			gloParams.put(E.organizationPartyId.name(), org.getString(E.partyId.name()));
        			Map<String, Object> serviceMap = takeOverService.baseCrudInterface(E.GlAccountOrganization.name(), "CREATE", gloParams);
        	        Map<String, Object> res = this.dispatcher.runSync(E.crudServiceDefaultOrchestration.name(), serviceMap);
                    String msg = ServiceUtil.getErrorMessage(res);
                    if (UtilValidate.isEmpty(msg)) {
                        msg = E.GlAccountOrganization.name() + " with party " + org.getString(E.partyId.name()) + " successfully created";
                        takeOverService.addLogInfo(msg);
                    } else {
                        msg = "Error in " + E.GlAccountOrganization.name() + " creation with party " + org.getString(E.partyId.name()) + " " + msg;
                        takeOverService.addLogError(msg);
                    }
        		}
        	}
        }
    }
}
