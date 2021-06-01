package com.mapsengineering.accountingext.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilIO;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;


/**
 * CustomMethodCalculatorUtil bsh
 *
 */
public class CustomMethodCalculatorUtil {
	
	private LocalDispatcher dispatcher;
    
    private Map<String, ? extends Object> context;
    
    /**
     * Constructor
     * @param dctx
     * @param context
     */
	public CustomMethodCalculatorUtil(DispatchContext dctx, Map<String, ? extends Object> context) {
		this.dispatcher = dctx.getDispatcher();
		this.context = context;
	}
	
	/**
	 * 
	 * @param row
	 * @param column
	 * @return
	 * @throws GenericEntityException
	 */
	public Double matrix(Double row, Double column) throws GenericEntityException {
		
		Double outputValue = 0D;
		
		List<EntityCondition> condition = FastList.newInstance();
		condition.add(EntityCondition.makeCondition("rowInputValue", row.intValue()));
		condition.add(EntityCondition.makeCondition("columnInputValue", column.intValue()));
		condition.add(EntityCondition.makeCondition("customMethodId", context.get(E.customMethodId.name())));
		
        List<GenericValue> list = dispatcher.getDelegator().findList("CustomMethodMatrix", EntityCondition.makeCondition(condition), null, null, null, false);
        
        if (UtilValidate.isNotEmpty(list)) {
        	outputValue = list.get(0).getDouble(E.outputValue.name());
        }
		
		return outputValue;
	}
	
	/**
	 * 
	 * @param location
	 * @return
	 * @throws GeneralException
	 * @throws IOException
	 */
	public static String loadFileBsh (String location) throws GeneralException, IOException {
		
		URL scriptUrl = FlexibleLocation.resolveLocation(location);
        if (scriptUrl == null) {
            throw new GeneralException("Could not find bsh script at [" + location + "]");
        }
        return  UtilIO.readString(new InputStreamReader(scriptUrl.openStream()));
        
	}

}
