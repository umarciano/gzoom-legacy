package com.mapsengineering.base.standardimport.organization;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.standardimport.importothers.ImportOthersManager;
import com.mapsengineering.base.standardimport.workeffort.OperationTypeEnum;
import com.mapsengineering.base.util.ContextIdEnum;
import com.mapsengineering.base.util.DateUtilService;
import com.mapsengineering.base.util.FindUtilService;

public class WeRootImportManager {
	private TakeOverService service;
	private GenericValue roleType;
	private String partyId;
	private String parentOrgCode;
	private int refYear;
	private String sourceReferenceRootId;
	private String sourceReferenceIdFrom;
	private String oldPartyName;
	private String oldPartyNameLang;
	private GenericValue workEffortType;
	private List<GenericValue> workEffortTypeAssocList;
	private Map<String, Object> weRootDataMap;
	
	/**
	 * scatena import della scheda
	 * @param service
	 * @param partyId
	 * @param roleType
	 * @param parentOrgCode
	 * @param oldPartyName
	 * @param oldPartyNameLang
	 */
	public WeRootImportManager(TakeOverService service, String partyId, GenericValue roleType, String parentOrgCode, String oldPartyName, String oldPartyNameLang) {
		this.service = service;
		this.partyId = partyId;
		this.roleType = roleType;
		this.parentOrgCode = parentOrgCode;
		this.oldPartyName = oldPartyName;
		this.oldPartyNameLang = oldPartyNameLang;
	}
	
	/**
	 * 
	 * @throws GeneralException
	 */
	public void doImport() throws GeneralException {
		setRefYear();
		setSourceReferenceRootId();
		setSourceReferenceIdFrom();
		setWorkEffortType();
		setWorkEffortTypeAssocList();
		
		if (UtilValidate.isNotEmpty(workEffortTypeAssocList) && isImportToDo()) {
			ImportOthersManager importOthersManager = new ImportOthersManager(service);
			importOthersManager.setEntityListToImport(getEntityListToImport());
			importOthersManager.setDataMap(buildDataMap());
			importOthersManager.execute();
		}
		
		if (UtilValidate.isNotEmpty(oldPartyName) || UtilValidate.isNotEmpty(oldPartyNameLang)) {
			GenericValue externalValue = service.getExternalValue();
			if ((UtilValidate.isNotEmpty(oldPartyName) && ! oldPartyName.equals(externalValue.getString(E.description.name()))) || (UtilValidate.isNotEmpty(oldPartyNameLang) && ! oldPartyNameLang.equals(externalValue.getString(E.descriptionLang.name())))) {
				updateWorkEffort(externalValue);
			}
		}
	}
	
	/**
	 * costruisce la data con i valori da importare
	 * @return
	 */
	private Map<String, List<Map<String, Object>>> buildDataMap() throws GeneralException {
		GenericValue externalValue = service.getExternalValue();
		Map<String, List<Map<String, Object>>> dataMap = new HashMap<String, List<Map<String, Object>>>();
		buildWeRootDataMap(externalValue);
		dataMap.put(ImportManagerConstants.WE_ROOT_INTERFACE, UtilMisc.toList(weRootDataMap));
		dataMap.put(ImportManagerConstants.WE_ASSOC_INTERFACE, buildWeAssocDataMapList());
		dataMap.put(ImportManagerConstants.WE_MEASURE_INTERFACE, buildWeMeasureDataMap());
		
		return dataMap;
	}
	
	/**
	 * costruisce la mappa dei dati per la WeRootInterface
	 * @param externalValue
	 */
	private void buildWeRootDataMap(GenericValue externalValue) {
		weRootDataMap = new HashMap<String, Object>();
		weRootDataMap.put(E.id.name(), service.getManager().getDelegator().getNextSeqId(ImportManagerConstants.WE_ROOT_INTERFACE));
		weRootDataMap.put(E.sourceReferenceRootId.name(), sourceReferenceRootId);
		weRootDataMap.put(E.operationType.name(), OperationTypeEnum.A.name());
		weRootDataMap.put(E.weContext.name(), getWeContext(workEffortType.getString(E.parentTypeId.name())));
		weRootDataMap.put(E.workEffortTypeId.name(), roleType.getString(E.workEffortTypeId.name()));
		weRootDataMap.put(E.workEffortName.name(), externalValue.getString(E.description.name()));
		weRootDataMap.put(E.workEffortNameLang.name(), externalValue.getString(E.descriptionLang.name()));
        weRootDataMap.put(E.description.name(), externalValue.getString(E.longDescription.name()));
        weRootDataMap.put(E.descriptionLang.name(), externalValue.getString(E.longDescriptionLang.name()));
        weRootDataMap.put(E.etch.name(), externalValue.getString(E.orgCode.name()));
		weRootDataMap.put(E.orgCode.name(), externalValue.getString(E.orgCode.name()));
		weRootDataMap.put(E.estimatedStartDate.name(), UtilDateTime.toTimestamp(1, 1, refYear, 0, 0, 0));
		weRootDataMap.put(E.estimatedCompletionDate.name(), getEstimatedCompletionDate());
	}
	
	/**
	 * costruisce la mappa dei dati per la WeAssocInterface	
	 * @return
	 */
	private List<Map<String, Object>> buildWeAssocDataMapList() {
		List<Map<String, Object>> weAssocDataMapList = new ArrayList<Map<String, Object>>();
		for (GenericValue workEffortTypeAssoc : workEffortTypeAssocList) {
			if (UtilValidate.isNotEmpty(workEffortTypeAssoc)) {
				Map<String, Object> weAssocDataMap = new HashMap<String, Object>();
				weAssocDataMap.put(E.sourceReferenceRootId.name(), sourceReferenceRootId);
				weAssocDataMap.put(E.sourceReferenceRootIdFrom.name(), sourceReferenceIdFrom);
				weAssocDataMap.put(E.sourceReferenceIdFrom.name(), sourceReferenceIdFrom);
				weAssocDataMap.put(E.sourceReferenceRootIdTo.name(), sourceReferenceRootId);
				weAssocDataMap.put(E.sourceReferenceIdTo.name(), sourceReferenceRootId);
				weAssocDataMap.put(E.workEffortTypeIdFrom.name(), workEffortTypeAssoc.getString(E.workEffortTypeIdRef.name()));
				weAssocDataMap.put(E.workEffortTypeIdTo.name(), roleType.getString(E.workEffortTypeId.name()));
				weAssocDataMap.put(E.workEffortAssocTypeCode.name(), roleType.getString(E.workEffortAssocTypeId.name()));
				weAssocDataMap.put(E.workEffortNameFrom.name(), E._NA_.name());
				weAssocDataMap.put(E.workEffortNameTo.name(), E._NA_.name());
				
				weAssocDataMapList.add(weAssocDataMap);
			}
		}
		
		return weAssocDataMapList;
	}
	
	/**
	 * costruisce la mappa dei dati per la WeMeasureInterface
	 * @return
	 * @throws GeneralException
	 */
	private List<Map<String, Object>> buildWeMeasureDataMap() throws GeneralException {
		return new WeMeasureDataMapBuilder(service, weRootDataMap, partyId).buildWeMeasureListDataMap();
	}
	
	private String getEntityListToImport() {
		return ImportManagerConstants.WE_ROOT_INTERFACE
			+ "|" 
			+ ImportManagerConstants.WE_ASSOC_INTERFACE
		    + "|"
		    + ImportManagerConstants.WE_MEASURE_INTERFACE;
	}
	
	/**
	 * estrae l anno del refDate
	 */
	private void setRefYear() {
		GenericValue externalValue = service.getExternalValue();
		this.refYear = UtilDateTime.getYear(externalValue.getTimestamp(E.refDate.name()), service.getManager().getTimeZone(), service.getManager().getLocale());
	}
	
	/**
	 * valorizza il campo sourceReferenceRootId
	 */
	private void setSourceReferenceRootId() {
		GenericValue externalValue = service.getExternalValue();
		this.sourceReferenceRootId = E.WEPE_START_YEAR.name().equals(roleType.getString(E.workEffortPeriodId.name())) 
				? externalValue.getString(E.orgCode.name())
				: refYear + "." + externalValue.getString(E.orgCode.name());
	}
	
	/**
	 * valorizza il campo sourceReferenceIdFrom
	 */
	private void setSourceReferenceIdFrom() {
		this.sourceReferenceIdFrom = E.WEPE_START_YEAR.name().equals(roleType.getString(E.workEffortPeriodId.name()))
				? parentOrgCode
				: refYear + "." + parentOrgCode;		
	}
	
	/**
	 * workEffortType associato al ruolo
	 * @throws GeneralException
	 */
	private void setWorkEffortType() throws GeneralException {
		this.workEffortType = service.getManager().getDelegator().findOne(E.WorkEffortType.name(), UtilMisc.toMap(E.workEffortTypeId.name(), roleType.getString(E.workEffortTypeId.name())), false);
	}
	
	/**
	 * workEffortTypeAssoc associato al ruolo
	 * @throws GeneralException
	 */
	private void setWorkEffortTypeAssocList() throws GeneralException {
		List<EntityCondition> condList = new ArrayList<EntityCondition>();
		condList.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), roleType.getString(E.workEffortTypeId.name())));
		condList.add(EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), roleType.getString(E.workEffortAssocTypeId.name())));
		condList.add(EntityCondition.makeCondition(E.wefromWetoEnumId.name(), E.WETATO.name()));		
		String workEffortTypeIdRef = getWorkEffortTypeIdRef();
		if (UtilValidate.isNotEmpty(workEffortTypeIdRef)) {
			condList.add(EntityCondition.makeCondition(E.workEffortTypeIdRef.name(), workEffortTypeIdRef));
		}
		this.workEffortTypeAssocList = service.getManager().getDelegator().findList(E.WorkEffortTypeAssoc.name(), EntityCondition.makeCondition(condList), null, null, null, false);
	}
	
	/**
	 * individua il workEffortTypeId
	 * @return
	 * @throws GeneralException
	 */
	private String getWorkEffortTypeIdRef() throws GeneralException {
		List<EntityCondition> workEffortCondList = new ArrayList<EntityCondition>(); 
		workEffortCondList.add(EntityCondition.makeCondition(E.sourceReferenceId.name(), sourceReferenceIdFrom));
		GenericValue workEffort = EntityUtil.getFirst(service.getManager().getDelegator().findList(E.WorkEffort.name(), EntityCondition.makeCondition(workEffortCondList), null, null, null, false));
	    if (UtilValidate.isNotEmpty(workEffort)) {
	    	return workEffort.getString(E.workEffortTypeId.name());
	    }
	    return null;
	}
	
	/**
	 * ricava il weContext dal parentTypeId
	 * @param parentTypeId
	 * @return
	 */
	private String getWeContext(String parentTypeId) {
		ContextIdEnum contextIdEnum = ContextIdEnum.parse(parentTypeId);
		if (contextIdEnum != null) {
			return contextIdEnum.weContext();
		}
		return null;
	}
	
	private Timestamp getEstimatedCompletionDate() {
		return E.WEPE_CAL_YEAR.name().equals(roleType.getString(E.workEffortPeriodId.name()))
				? UtilDateTime.toTimestamp(12, 31, refYear, 0, 0, 0)
				: UtilDateTime.toTimestamp(12, 31, 2099, 0, 0, 0);
	}
	
	/**
	 * se da inizio anno controlla che l obiettivo non esista gia
	 * @return
	 * @throws GeneralException
	 */
	private boolean isImportToDo() throws GeneralException {
		if (E.WEPE_CAL_YEAR.name().equals(roleType.getString(E.workEffortPeriodId.name()))) {
			return true;
		}
		String orgUnitId = getOrgUnitId();
		if (UtilValidate.isEmpty(orgUnitId)) {
			return true;
		}
		List<EntityCondition> workEffortConditions = new ArrayList<EntityCondition>();
		workEffortConditions.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), roleType.getString(E.workEffortTypeId.name())));
		workEffortConditions.add(EntityCondition.makeCondition(E.orgUnitId.name(), orgUnitId));
		List<GenericValue> workEfforts = service.getManager().getDelegator().findList(E.WorkEffort.name(), EntityCondition.makeCondition(workEffortConditions), null, null, null, false);
		return workEfforts == null || workEfforts.size() == 0;
	}
	
	/**
	 * ritorna partyId da pPartyParentRole
	 * @return
	 * @throws GeneralException
	 */
	private String getOrgUnitId() throws GeneralException {
		GenericValue externalValue = service.getExternalValue();
		List<EntityCondition> partyParentRoleConditions = new ArrayList<EntityCondition>();
		partyParentRoleConditions.add(EntityCondition.makeCondition(E.parentRoleCode.name(), externalValue.getString(E.orgCode.name())));
		partyParentRoleConditions.add(EntityCondition.makeCondition(E.roleTypeId.name(), getRoleTypeId()));
		GenericValue partyParentRole = EntityUtil.getFirst(service.getManager().getDelegator().findList(E.PartyParentRole.name(), EntityCondition.makeCondition(partyParentRoleConditions), null, null, null, false));
	    if (UtilValidate.isNotEmpty(partyParentRole)) { 
	    	return partyParentRole.getString(E.partyId.name());
	    }
	    return "";
	}
	
	/**
	 * ritorna il roleTypeId
	 * @return
	 */
	private String getRoleTypeId() {
		GenericValue externalValue = service.getExternalValue();
        return E.GOAL05.name().equals(externalValue.getString(E.orgRoleTypeId.name())) ? E.GOALDIV01.name() : E.ORGANIZATION_UNIT.name();
	}
	
	/**
	 * modifica workEffort con nuovo nome orgUnit
	 * @param externalValue
	 * @throws GeneralException
	 */
	private void updateWorkEffort(GenericValue externalValue) throws GeneralException {
		List<EntityCondition> workEffortUpdateConditions = new ArrayList<EntityCondition>();
		workEffortUpdateConditions.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), roleType.getString(E.workEffortTypeId.name())));
		workEffortUpdateConditions.add(EntityCondition.makeCondition(E.orgUnitId.name(), partyId));
		workEffortUpdateConditions.add(EntityCondition.makeCondition(E.estimatedStartDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, externalValue.getTimestamp(E.refDate.name())));
		workEffortUpdateConditions.add(EntityCondition.makeCondition(E.estimatedCompletionDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, externalValue.getTimestamp(E.refDate.name())));
		List<GenericValue> workEffortList = service.getManager().getDelegator().findList(E.WorkEffort.name(), EntityCondition.makeCondition(workEffortUpdateConditions), null, null, null, false);
	    if (UtilValidate.isNotEmpty(workEffortList)) {
	    	for (GenericValue workEffort : workEffortList) {
	    		boolean toStore = false;
	    		if (UtilValidate.isNotEmpty(externalValue.getString(E.description.name())) && ! externalValue.getString(E.description.name()).equals(workEffort.getString(E.workEffortName.name()))) {
	    			toStore = true;
	    			workEffort.set(E.workEffortName.name(), externalValue.getString(E.description.name()));
	    		}
	    		if (UtilValidate.isNotEmpty(externalValue.getString(E.descriptionLang.name())) && ! externalValue.getString(E.descriptionLang.name()).equals(workEffort.getString(E.workEffortNameLang.name()))) {
	    			toStore = true;
	    			workEffort.set(E.workEffortNameLang.name(), externalValue.getString(E.descriptionLang.name()));
	    		}
	    		if (toStore) {
	    			service.getManager().getDelegator().store(workEffort);
	    		}
	    		createWorkEffortNote(externalValue, workEffort.getString(E.workEffortId.name()));
	    	}
	    }
	}
	
	/**
	 * crea nota con vecchio nome orgUnit
	 * @param externalValue
	 * @param workEffortId
	 * @throws GeneralException
	 */
	private void createWorkEffortNote(GenericValue externalValue, String workEffortId) throws GeneralException {
		List<EntityCondition> workEffortTypeAttrAndNoteDataConditions = new ArrayList<EntityCondition>();
		workEffortTypeAttrAndNoteDataConditions.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), roleType.getString(E.workEffortTypeId.name())));
		workEffortTypeAttrAndNoteDataConditions.add(EntityCondition.makeCondition(E.sequenceId.name(), 0L));
		List<GenericValue> workEffortTypeAttrList = service.getManager().getDelegator().findList(E.WorkEffortTypeAttrAndNoteData.name(), EntityCondition.makeCondition(workEffortTypeAttrAndNoteDataConditions), null, null, null, false);
		GenericValue workEffortTypeAttr = EntityUtil.getFirst(workEffortTypeAttrList);
		if (UtilValidate.isNotEmpty(workEffortTypeAttr)) {
			Map<String, Object> serviceMapParams = new HashMap<String, Object>();
			serviceMapParams.put(E.workEffortId.name(), workEffortId);
			serviceMapParams.put(E.noteId.name(), service.getManager().getDelegator().getNextSeqId(E.NoteData.name()));
			serviceMapParams.put(E.noteName.name(), workEffortTypeAttr.getString(E.attrName.name()));
			serviceMapParams.put(E.noteNameLang.name(), workEffortTypeAttr.getString(E.attrNameLang.name()));
			serviceMapParams.put(E.noteInfo.name(), oldPartyName);
			serviceMapParams.put(E.noteInfoLang.name(), oldPartyNameLang);
			serviceMapParams.put(E.noteDateTime.name(), new Timestamp(DateUtilService.getPreviousDay(externalValue.getTimestamp(E.refDate.name())).getTime()));
			serviceMapParams.put(E.internalNote.name(), workEffortTypeAttr.getString(E.internalNote.name()));
			serviceMapParams.put(E.isMain.name(), workEffortTypeAttr.getString(E.isMain.name()));
			serviceMapParams.put(E.isHtml.name(), workEffortTypeAttr.getString(E.isHtml.name()));
			serviceMapParams.put(E.sequenceId.name(), workEffortTypeAttr.getString(E.sequenceId.name()));
			service.runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortNoteAndData.name(), E.WorkEffortNoteAndData.name(), CrudEvents.OP_CREATE, serviceMapParams, E.WorkEffortNoteAndData.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortNoteAndData.name(), true);
		}
	}
}
