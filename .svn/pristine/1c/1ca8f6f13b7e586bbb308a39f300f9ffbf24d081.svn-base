package com.mapsengineering.emplperf.update.assoc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants.MessageCode;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.emplperf.update.EmplPerfServiceEnum;
import com.mapsengineering.emplperf.update.EmplPerfValueUpdate;

public class EmplPerfValueUpdateAssoc extends EmplPerfValueUpdate {
	
	private String sourceReferenceRootId;
	
	@Override
	protected JobLogger doAction(JobLogger jobLogger, Delegator delegator, GenericValue genericValue, Map<String, Object> context) throws GeneralException {
        jobLogger.addMessage(ServiceLogger.makeLogInfo(getMsgLog(genericValue), MessageCode.INFO_GENERIC.toString(), genericValue.getString(EmplPerfServiceEnum.sourceReferenceRootId.name()), null, null));
        createInterfaceRecords(delegator, genericValue);
        return jobLogger;
	}
	
	/**
	 * creazione record di interfaccia
	 * @param delegator
	 * @param genericValue
	 * @throws GeneralException
	 */
	private void createInterfaceRecords(Delegator delegator, GenericValue genericValue) throws GeneralException {
		if (UtilValidate.isEmpty(sourceReferenceRootId) || 
				! sourceReferenceRootId.equals(genericValue.getString(EmplPerfServiceEnum.sourceReferenceRootId.name())) ) {
			createWeRootInterfaceRecords(delegator, genericValue);
		}
		createWeAssocInterfaceRecords(delegator, genericValue);
		sourceReferenceRootId = genericValue.getString(EmplPerfServiceEnum.sourceReferenceRootId.name());
	}
	
	/**
	 * creazione record WeRootInterface
	 * @param delegator
	 * @param genericValue
	 * @throws GeneralException
	 */
	private void createWeRootInterfaceRecords(Delegator delegator, GenericValue genericValue) throws GeneralException {
        GenericValue gv = delegator.makeValue(EmplPerfServiceEnum.WeRootInterface.name());

        gv.put(EmplPerfServiceEnum.id.name(), delegator.getNextSeqId(EmplPerfServiceEnum.WeRootInterface.name()));
        gv.put(EmplPerfServiceEnum.dataSource.name(), "EMPL_PERF_UPDATE");
        gv.put(EmplPerfServiceEnum.sourceReferenceRootId.name(), genericValue.getString(EmplPerfServiceEnum.sourceReferenceRootId.name()));
        gv.put(EmplPerfServiceEnum.weContext.name(), "IND");
        gv.put(EmplPerfServiceEnum.workEffortTypeId.name(), genericValue.getString(EmplPerfServiceEnum.workEffortTypeRootId.name()));
        gv.put(EmplPerfServiceEnum.operationType.name(), "A");
        gv.create();
	}
	
	/**
	 * creazione record WeAssocInterface
	 * @param delegator
	 * @param genericValue
	 * @throws GeneralException
	 */
	private void createWeAssocInterfaceRecords(Delegator delegator, GenericValue genericValue) throws GeneralException {		
        GenericValue gv = delegator.makeValue(EmplPerfServiceEnum.WeAssocInterface.name());

        gv.put(EmplPerfServiceEnum.dataSource.name(), "EMPL_PERF_UPDATE");
        gv.put(EmplPerfServiceEnum.sourceReferenceRootId.name(), genericValue.getString(EmplPerfServiceEnum.sourceReferenceRootId.name()));
        gv.put(EmplPerfServiceEnum.workEffortAssocTypeCode.name(), genericValue.getString(EmplPerfServiceEnum.oldWeAssocTypeId.name()));
        gv.put(EmplPerfServiceEnum.sourceReferenceRootIdFrom.name(), genericValue.getString(EmplPerfServiceEnum.weFromSourceRefRootId.name()));
        gv.put(EmplPerfServiceEnum.workEffortTypeIdFrom.name(), genericValue.getString(EmplPerfServiceEnum.weFromTypeId.name()));       
        gv.put(EmplPerfServiceEnum.sourceReferenceIdFrom.name(), genericValue.getString(EmplPerfServiceEnum.weFromSourceRefId.name()));              
        gv.put(EmplPerfServiceEnum.sourceReferenceRootIdTo.name(), genericValue.getString(EmplPerfServiceEnum.sourceReferenceRootId.name()));
        gv.put(EmplPerfServiceEnum.workEffortTypeIdTo.name(), genericValue.getString(EmplPerfServiceEnum.workEffortTypeId.name()));
        gv.put(EmplPerfServiceEnum.sourceReferenceIdTo.name(), genericValue.getString(EmplPerfServiceEnum.sourceReferenceId.name()));      
        gv.put(EmplPerfServiceEnum.sequenceNum.name(), genericValue.getLong(EmplPerfServiceEnum.oldWeAssocSequenceNum.name()));
        gv.put(EmplPerfServiceEnum.assocWeight.name(), genericValue.getDouble(EmplPerfServiceEnum.oldWeAssocWeight.name()));              
        gv.put(EmplPerfServiceEnum.workEffortNameFrom.name(), EmplPerfServiceEnum._NA_.name());
        gv.put(EmplPerfServiceEnum.workEffortNameTo.name(), EmplPerfServiceEnum._NA_.name());
        gv.create();		
	}
	
	/**
	 * messaggio di log
	 * @param genericValue
	 * @return
	 */
	private String getMsgLog(GenericValue genericValue) {
		return "Update assoc with workEffortIdFrom = "
				+ genericValue.getString(EmplPerfServiceEnum.weFromSourceRefId.name())
				+ ", workEffortIdTo = "
				+ genericValue.getString(EmplPerfServiceEnum.sourceReferenceId.name())
				+ ", workEffortAssocTypeId = "
				+ genericValue.getString(EmplPerfServiceEnum.oldWeAssocTypeId.name());
	}

	@Override
	protected List<String> getKey() {
		return UtilMisc.toList(EmplPerfServiceEnum.weFromSourceRefId.name(), EmplPerfServiceEnum.sourceReferenceId.name(), EmplPerfServiceEnum.oldWeAssocTypeId.name());
	}
	
	@Override
	protected Set<String> getFieldsToSelect() {
		return null;
	}

	@Override
	protected boolean executeStandardImport() {
		return true;
	}

	@Override
	protected String getEntityListToImport() {
		return ImportManagerConstants.WE_ROOT_INTERFACE + "|" + ImportManagerConstants.WE_ASSOC_INTERFACE;
	}

	@Override
	protected List<String> getOrderBy() {
		return UtilMisc.toList(EmplPerfServiceEnum.sourceReferenceRootId.name());
	}
}
