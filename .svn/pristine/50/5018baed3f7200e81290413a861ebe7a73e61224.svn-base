package com.mapsengineering.accountingext.util;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.accountingext.services.E;

/**
 * GlFiscalTypeOutput Utility
 *
 */
public class GlFiscalTypeOutputUtil {
    
    private Delegator delegator;

    private Map<String, ? extends Object> context;

    /**
     * Constructor
     */
    public GlFiscalTypeOutputUtil(Delegator delegator, Map<String, ? extends Object> context) {
        this.delegator = delegator;
        this.context = context;
    }
    
	/**
	 * Come tipo valore di output il tipo valore del tipo indicatore se unico, 
	 * altrimenti il tipo valore output se valorizzato, 
	 * altrimenti il tipo valore input
	 * @throws GenericEntityException 
	 */
	public String getGlFiscalTypeIdOutput (GenericValue indicator) throws GenericEntityException {
		
		String glFiscalTypeIdOutput = (String) context.get(E.glFiscalTypeIdOutput.name());
		String glFiscalTypeIdOutputIndicator = getGlFiscalTypeWorkEffort(indicator);
		
		if (UtilValidate.isNotEmpty(glFiscalTypeIdOutputIndicator)) {
			glFiscalTypeIdOutput = glFiscalTypeIdOutputIndicator;
		} else if (UtilValidate.isEmpty(glFiscalTypeIdOutput)) {
			glFiscalTypeIdOutput = (String) context.get(E.glFiscalTypeIdInput.name());
		} 
		
		return glFiscalTypeIdOutput;
	}
	
	private String getGlFiscalTypeWorkEffort(GenericValue indicator) throws GenericEntityException {
		String glFiscalTypeIdOutput = "";
		
		List<GenericValue> glFiscalTypeList = delegator.findList(E.GlAccountTypeGlFiscalTypeView.name(), EntityCondition.makeCondition(E.glAccountTypeId.name(), indicator.get(E.glResourceTypeId.name())), null, null, null, false);
		
		if (UtilValidate.isNotEmpty(glFiscalTypeList) && glFiscalTypeList.size() == 1) {
			glFiscalTypeIdOutput = (String) glFiscalTypeList.get(0).get(E.glFiscalTypeId.name());
		}
		return glFiscalTypeIdOutput;
	}
}
