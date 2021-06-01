package com.mapsengineering.base.util;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;

public class PartyLanguage extends GenericService {
	
	public static final String MODULE = PartyLanguage.class.getName();
	public static final String SERVICE_NAME = "partyLanguage";
	public static final String SERVICE_TYPE = "PARTY_LANGUAGE";
	
	private String partyId;
	
	public PartyLanguage(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        
        partyId = (String)context.get(E.partyId.name());
    }

	public static Map<String, Object> isPartyLanguagePrimary(DispatchContext dctx, Map<String, Object> context) {
		PartyLanguage obj = new PartyLanguage(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }
	
	 /**
     * Determina la lingua da associare ad un party
     */
    public void mainLoop() 
    {	    	
    	try 
    	{
    		String secondaryLang = "";
    		
	        // recupera le lingue configurate sui properties
	        List<Locale> availableBaseConfigLocaleList = UtilLanguageLocale.availableBaseConfigLocales();
	        
	        if (UtilValidate.isNotEmpty(availableBaseConfigLocaleList)) 
	        {
	        	this.getResult().put("primaryLanguage", availableBaseConfigLocaleList.get(0));
	        	if(availableBaseConfigLocaleList.size() > 1){
	        		secondaryLang =  availableBaseConfigLocaleList.get(1).getLanguage();	
	        		this.getResult().put("secondaryLanguage", availableBaseConfigLocaleList.get(1));
	        	}
	        }     
    		
	    	this.getResult().put("isPrimary", true);
	    	 
	    	// recupera l'informazione sul multi lingua
	        String multiTypeLang = UtilProperties.getPropertyValue("BaseConfig", "Language.multi.type");
	        if (!"BILING".equals(multiTypeLang)) {
	        	return;
	        }
	    	
	        List<EntityExpr> conditions = UtilMisc.toList(
	        		EntityCondition.makeCondition(E.partyId.name(), partyId),
                    EntityCondition.makeCondition(E.enabled.name(), "Y"));
            EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(conditions, EntityOperator.AND);
	    	List<GenericValue> ulList = getDelegator().findList(E.UserLogin.name(), ecl, UtilMisc.toSet(E.lastLocale.name()), UtilMisc.toList(E.createdStamp.name()), null, true);
	    	    	 
	    	// prende solo il primo elemento della lista
	    	GenericValue userLogin = EntityUtil.getFirst(ulList);
	    	 
	    	if(userLogin == null){  		 
	    		return;
	    	}
	    	 
	    	String locale = userLogin.getString(E.lastLocale.name());
	    	 
	    	if(UtilValidate.isEmpty(locale)){
	   		 	return;
	    	} 
	         
	        Locale localeFormatted = UtilMisc.parseLocale(locale);
	        if(localeFormatted != null){
	            String lang = localeFormatted.getLanguage();                        
	            if (UtilValidate.isNotEmpty(secondaryLang) && secondaryLang.equals(lang)) {
	            	this.getResult().put("isPrimary", false);
	            }
	        }
    	} catch (Exception e) {
            setResult(ServiceUtil.returnError(e.getMessage()));
        } 
    }
}
