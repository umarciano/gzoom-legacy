package com.mapsengineering.partyext.services;

import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * Party services
 *
 */
public class PartyServices {
    public static final String MODULE = PartyServices.class.getName();
    public static final String RESOURCE = "PartyExtErrorUiLabels";
    
    private PartyServices() {}

    /**
     * Update partyName
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> updatePartyName(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = FastMap.newInstance();

        Locale locale = (Locale) context.get("locale");

        String partyId = (String)context.get("partyId");
        try {
            GenericValue party = ctx.getDelegator().findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));

            if (party != null) {
            	String partyName = PartyHelper.getPartyName(party, true);
            	party.set("partyName", partyName.replace(",", ""));
            
            	String partyTypeId = party.getString("partyTypeId");
            	if ("PARTY_GROUP".equals(partyTypeId) || "IMPERSONAL_PARTY".equals(partyTypeId)) {
            		updatePartyNameLang(ctx.getDelegator(), party);
            	}
            
            	ctx.getDelegator().store(party);
            }

            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error finding Party in getPartyName", MODULE);

            result = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "party.update_name_failure", locale));
        }
        return result;
    }
    
    /**
     * recupera il partyNameLang dal partyGroup nel caso di persona giuridica o impersonale
     * @param delegator
     * @param party
     * @throws GenericEntityException
     */
    private static void updatePartyNameLang(Delegator delegator, GenericValue party) throws GenericEntityException {
    	GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", party.getString("partyId")), false);
    	if (partyGroup != null) {
    		party.set("partyNameLang", partyGroup.getString("groupNameLang"));
    	}
    }
    
    /**
     * Create Person and update endDate
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> createPerson(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();

    	Map<String, Object> result = org.ofbiz.party.party.PartyServices.createPerson(ctx, context);
    	
    	if (result.get(ModelService.RESPONSE_MESSAGE) == ModelService.RESPOND_SUCCESS) {
    		
    		result = addEndDateParty(delegator, context, result);
    	}
        
    	return result;
    }
    
    /**
     * Create PartyGroup and update endDate
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> createPartyGroup(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();

    	Map<String, Object> result = org.ofbiz.party.party.PartyServices.createPartyGroup(ctx, context);
    	
    	if (result.get(ModelService.RESPONSE_MESSAGE) == ModelService.RESPOND_SUCCESS) {
    		
    		result = addEndDateParty(delegator, context, result);
    	}
        
    	return result;
    }
    
    private static Map<String, Object> addEndDateParty (Delegator delegator, Map<String, ? extends Object> context, Map<String, Object> result) {
    	Locale locale = (Locale) context.get("locale");
    	
		GenericValue party = null;
		String partyId = (String) result.get("partyId");
		
        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), MODULE);
        }

        if (party != null) {
        	Map<String, Object> newPartyMap = UtilMisc.toMap("partyId", partyId, "endDate", context.get("endDate"));
            party = delegator.makeValue("Party", newPartyMap);
            
            try {
                delegator.store(party);
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), MODULE);
                return ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "person.update_name_failure", locale));
            }
        }
        
        return result;
    }
}
