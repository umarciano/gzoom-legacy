package com.mapsengineering.base.appheader;

import java.util.List;
import java.util.Set;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;


/**
 * classe contenente i metodi che espondono i dati dell header dell applicazione
 * @author nito
 *
 */
public class AppHeaderData {
	
	public static final String APPL_TITLE_NOTE_NAME = "APPLICATION_TITLE";
	
	/**
	 * ritorna il titolo dell app, prendendolo dale note del party Company
	 * @param delegator
	 * @param defaultOrganizationPartyId
	 * @return
	 * @throws GeneralException
	 */
	public static String getAppTitle(Delegator delegator, String defaultOrganizationPartyId) throws GeneralException { 
		List<EntityCondition> condList = FastList.newInstance();
        condList.add(EntityCondition.makeCondition(E.targetPartyId.name(), defaultOrganizationPartyId));
        condList.add(EntityCondition.makeCondition(E.noteName.name(), APPL_TITLE_NOTE_NAME));
        
        Set<String> fieldsToSelect = UtilMisc.toSet(E.noteInfo.name());		
		List<GenericValue> partyNoteList = delegator.findList(E.PartyNoteView.name(), EntityCondition.makeCondition(condList), 
				fieldsToSelect, null, null, true);
		GenericValue defaultPartyNote = EntityUtil.getFirst(partyNoteList);
		
		return UtilValidate.isNotEmpty(defaultPartyNote) ? defaultPartyNote.getString(E.noteInfo.name()) : "";
	}

}
