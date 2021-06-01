package com.mapsengineering.base.standardimport.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilGenerics;

import com.mapsengineering.base.standardimport.ImportManager;

/**
 * PersonInterfaceContext
 * 
 * @author mirlap
 *
 */

public class PersonInterfaceContext {
	
	public static final String MODULE = PersonInterfaceContext.class.getName();
    public static final String ERROR_ROOT_RECORD = " Valid root record (PersonInterface record) required to import";
    
    private String personCode;

    /**
     * Create a new context, it MUST be destroyed by calling pop.
     * @param importManager
     */
    public static void push(ImportManager importManager) {
        Map<String, Object> context = importManager.getContext();
        List<PersonInterfaceContext> list = UtilGenerics.toList(context.get(MODULE));
        if (list == null) {
            list = new ArrayList<PersonInterfaceContext>();
            context.put(MODULE, list);
        }
        list.add(new PersonInterfaceContext());
    }

    /**
     * Destroy current context
     * @param importManager
     */
    public static void pop(ImportManager importManager) {
        List<PersonInterfaceContext> list = UtilGenerics.toList(importManager.getContext().get(MODULE));
        if (list != null && list.size() > 0) {
            list.remove(list.size() - 1);
        }
    }

    /**
     * Get current context, it MUST be created by calling push.
     * @param importManager
     * @return
     */
    public static PersonInterfaceContext get(ImportManager importManager) {
        List<PersonInterfaceContext> list = UtilGenerics.toList(importManager.getContext().get(MODULE));
        if (list != null && list.size() > 0) {
            return list.get(list.size() - 1);
        }
        return null;
    }

	public String getPersonCode() {
		return personCode;
	}

	public void setPersonCode(String personCode) {
		this.personCode = personCode;
	}

}
