package com.mapsengineering.base.appheader;

import java.util.List;
import java.util.Set;
import java.net.MalformedURLException;
import java.net.URL;

import javolution.util.FastList;

import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;


/**
 * classe contenente i metodi che espongono gli url dei loghi dell header dell applicazione
 * @author nito
 *
 */
public class AppHeaderLogo {
	
	/**
	 * cerca il logo associato alla company, se non lo trova ritorna il percorso del logo di default
	 * @param delegator
	 * @param partyContentTypeId
	 * @param defaultOrganizationPartyId
	 * @return
	 * @throws GeneralException
	 */
	public static String getAppContentUrl(Delegator delegator, String partyContentTypeId, String defaultOrganizationPartyId) throws GeneralException {
		List<EntityCondition> condList = FastList.newInstance();
        condList.add(EntityCondition.makeCondition(E.partyId.name(), defaultOrganizationPartyId));
        condList.add(EntityCondition.makeCondition(E.partyContentTypeId.name(), partyContentTypeId));
        condList.add(EntityCondition.makeCondition(E.thruDate.name(), GenericValue.NULL_FIELD));
                
        Set<String> fieldsToSelect = UtilMisc.toSet(E.contentId.name());
        List<String> orderByList = UtilMisc.toList("-contentId");
		List<GenericValue> partyContentList = delegator.findList(E.PartyContent.name(), EntityCondition.makeCondition(condList), 
				fieldsToSelect, orderByList, null, true);
		GenericValue partyContent = EntityUtil.getFirst(partyContentList);
		
		String contentId = UtilValidate.isNotEmpty(partyContent) ? partyContent.getString(E.contentId.name()) : "";
		if(UtilValidate.isNotEmpty(contentId)) {
			return getImgUrlFromContent(contentId);
		}
		String defaultContentId = "DEF_" + partyContentTypeId;
		return getImgUrlFromContent(defaultContentId);        
	}
		
	/**
	 * compone la url del logo, da richiamare poi nell ftl
	 * @param contentId
	 * @return
	 */
	private static String getImgUrlFromContent(String contentId) {
		StringBuilder sb = new StringBuilder();
		sb.append("stream?contentId=");
		sb.append(contentId);
		return sb.toString();
	}	
	
	
	/**
	 * Cerco il logo associato alla Company per utilizzarlo nei report. 
	 * @throws MalformedURLException 
	 * 
	 */
	public static String getReportContentUrl(Delegator delegator, String partyContentTypeId, String defaultOrganizationPartyId) throws GeneralException, MalformedURLException {
		List<EntityCondition> condList = FastList.newInstance();
        condList.add(EntityCondition.makeCondition(E.partyId.name(), defaultOrganizationPartyId));
        condList.add(EntityCondition.makeCondition(E.partyContentTypeId.name(), partyContentTypeId));
        condList.add(EntityCondition.makeCondition(E.thruDate.name(), GenericValue.NULL_FIELD));
                
        Set<String> fieldsToSelect = UtilMisc.toSet(E.objectInfo.name());
		List<GenericValue> partyContentList = delegator.findList(E.PartyAndContentDataResource.name(), EntityCondition.makeCondition(condList), 
				fieldsToSelect, null, null, true);
		GenericValue partyContent = EntityUtil.getFirst(partyContentList);
		
		String objectInfo = UtilValidate.isNotEmpty(partyContent) ? partyContent.getString(E.objectInfo.name()) : "";
		if(UtilValidate.isNotEmpty(objectInfo)) {
			return objectInfo;
		} else {
			//Se non e' presente il logo custom uso quello di default 
			String defaultContentId = "DEF_" + partyContentTypeId;
			List<EntityCondition> condDefaultList = FastList.newInstance();
			condDefaultList.add(EntityCondition.makeCondition(E.dataResourceId.name(), defaultContentId));
	        fieldsToSelect = UtilMisc.toSet(E.objectInfo.name());
	        List<GenericValue> dataResourceContentList = delegator.findList(E.DataResourceContentView.name(), EntityCondition.makeCondition(condDefaultList), 
					fieldsToSelect, null, null, true);
			GenericValue dataResourceContent = EntityUtil.getFirst(dataResourceContentList);
			objectInfo = UtilValidate.isNotEmpty(dataResourceContent) ? dataResourceContent.getString(E.objectInfo.name()) : "";
			
			URL url = FlexibleLocation.resolveLocation(objectInfo);
			
			objectInfo = url.toString();
			
			return objectInfo;
		}
		
	}
	
}
