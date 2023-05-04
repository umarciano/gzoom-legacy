package com.mapsengineering.workeffortext.services.sequence;


import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.sequence.common.WorkEffortSequenceConstants;



public final class WorkEffortSequence {
	
	public static final String MODULE = WorkEffortSequence.class.getName();
	
	private WorkEffortSequence() {}	
	
	/**
	 * Business logic elaborazione valori per aggiornamento workEffortSequence
	 * @param ctx DispatchContext
	 * @param context Context
	 * @return Success se il crud service può continurare con l'inserimento del work effort, error altrimenti
	 */
	@SuppressWarnings("unchecked")
    public static Map<String, Object> processWorkEffortSequence(DispatchContext dispCtx, Map<String, ? extends Object> context) {
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dispCtx.getDelegator();
		Map<String, Object> parameters = (Map<String, Object>) context.get(WorkEffortSequenceConstants.CONTEXT_PARAMS);
		
		String operation = (String) context.get(WorkEffortSequenceConstants.CONTEXT_OPERATION);		
		String fromCard = (String) parameters.get(WorkEffortSequenceConstants.PARAM_FROMCARD);
		if(! isSequenceToProcess(operation, fromCard)) {
			return ServiceUtil.returnSuccess();
		}
		
		try {
			processSequence(delegator, parameters, locale);
		} catch (Exception e) {       	
            Debug.logError(e, MODULE);
            return ServiceUtil.returnError(e.getMessage());
        }
		return ServiceUtil.returnSuccess();		
	}
	
	/**
	 * deve essere un inserimento da scheda
	 * @param operation
	 * @param fromCard
	 * @return
	 */
	private static boolean isSequenceToProcess(String operation, String fromCard) {
		return WorkEffortSequenceConstants.OPERATION_CREATE.equalsIgnoreCase(operation)
				&& WorkEffortSequenceConstants.VALUE_S.equalsIgnoreCase(fromCard);
	}
	
	/**
	 * 
	 * @param delegator
	 * @param parameters
	 * @param locale
	 * @throws GeneralException
	 */
	private static void processSequence(Delegator delegator, Map<String, Object> parameters, Locale locale) throws GeneralException {
		InputParametersHandler inputParametersHandler = new InputParametersHandler(delegator, parameters, locale);
		inputParametersHandler.run();
		
		//se work_effort_type.frame_enum_id = 'MANUAL' allora non faccio nulla
		if(inputParametersHandler.isFrameManual()) {
			return;
		}		
		WorkEffortFieldsGenerator workEffortFieldsGenerator = new WorkEffortFieldsGenerator(inputParametersHandler);
		if(inputParametersHandler.isProcessWeSequence()) {
			WorkEffortSequenceHelper workEffortSequenceHelper = new WorkEffortSequenceHelper(delegator, inputParametersHandler.getWorkEffortType(), 
					workEffortFieldsGenerator.getPrefisso());
			workEffortSequenceHelper.insertWorkEffortSequence();
			workEffortFieldsGenerator.setProgressivo(workEffortSequenceHelper.getPogressivo());
		}
		setWorkEffortParameters(parameters, workEffortFieldsGenerator);
	}
	
	/**
	 * 
	 * @param parameters
	 * @param workEffortFieldsGenerator
	 */
	public static void setWorkEffortParameters(Map<String, Object> parameters, WorkEffortFieldsGenerator workEffortFieldsGenerator) {		
		workEffortFieldsGenerator.run();
		if (UtilValidate.isEmpty(parameters.get(E.sourceReferenceId.name()))) {
			parameters.put(E.sourceReferenceId.name(), workEffortFieldsGenerator.getSourceReferenceId());
		}
		if (UtilValidate.isEmpty(parameters.get(E.etch.name()))) {
			parameters.put(E.etch.name(), workEffortFieldsGenerator.getEtch());		
		}
	}
			
}
