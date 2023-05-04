package com.mapsengineering.workeffortext.services.sequence;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.workeffortext.services.E;

public class RenumberWorkEffortSequence {

public static final String MODULE = RenumberWorkEffortSequence.class.getName();
	
	private RenumberWorkEffortSequence() {}	
	
	/**
	 * Business logic elaborazione valori per aggiornamento workEffortSequence
	 * @param ctx DispatchContext
	 * @param context Context
	 * @return Success 
	 */
	public static Map<String, Object> renumberWorkEffortSequence(DispatchContext dispCtx, Map<String, ? extends Object> context) {
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dispCtx.getDelegator();
		String workEffortIdFrom = (String) context.get(E.workEffortIdFrom.name());
		Debug.log("renumberWorkEffortSequence workEffortIdFrom:"+ workEffortIdFrom);
		try {
			renumberSequence(delegator, workEffortIdFrom, locale);
		} catch (GeneralException e) {
			Debug.logError(e, MODULE);
            return ServiceUtil.returnError(e.getMessage());
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	/**
	 * Prendo la lista ordinato di relazioni
	 * azzero il worEffortSequence
	 * riaggiorno la lista
	 * @param delegator
	 * @param parameters
	 * @throws GeneralException 
	 */
	private static void renumberSequence(Delegator delegator, String workEffortIdFrom, Locale locale) throws GeneralException  {
		List<GenericValue> workEffortAssocViewlist = workEffortAssocViewlist(delegator,  workEffortIdFrom);
		Debug.log("renumberWorkEffortSequence workEffortAssocViewlist.size="+ workEffortAssocViewlist.size());
		
		List<String> prefissoList = FastList.newInstance();
		List<GenericValue> workEffortList = FastList.newInstance();
		List<GenericValue> workEffortAssocList = FastList.newInstance();
		for(GenericValue gv: workEffortAssocViewlist) {
			
			 Map<String, Object> parameters = FastMap.newInstance();
			 parameters.put(E.delegator.name(), delegator);
			 parameters.put(E.workEffortTypeId.name(), gv.getString(E.workEffortTypeIdTo.name()));
			 parameters.put(E.workEffortIdFrom.name(), workEffortIdFrom);
			 parameters.put(E.estimatedStartDate.name(), gv.getTimestamp(E.wrToFromDate.name()));
			 parameters.put(E.orgUnitId.name(), gv.getString(E.wrToOrgUnitId.name()));			 
			 parameters.put(E.WETATOWorkEffortIdFrom.name(), "");
			 parameters.put(E.etch.name(), "");
					 
			InputParametersHandler inputParametersHandler = new InputParametersHandler(delegator, parameters, locale);
			inputParametersHandler.run();
			WorkEffortFieldsGenerator workEffortFieldsGenerator = new WorkEffortFieldsGenerator(inputParametersHandler);
			WorkEffortSequenceHelper workEffortSequenceHelper = new WorkEffortSequenceHelper(delegator, inputParametersHandler.getWorkEffortType(), workEffortFieldsGenerator.getPrefisso());
			
			if (!prefissoList.contains(workEffortSequenceHelper.getPrefisso())) {
				workEffortSequenceHelper.resetWorkEffortSequence();
				prefissoList.add(workEffortSequenceHelper.getPrefisso());
			}
			
			GenericValue workEffort = delegator.findOne(E.WorkEffort.name(), false, E.workEffortId.name(), gv.get(E.workEffortIdTo.name()));
			Debug.log("renumberWorkEffortSequence old workEffort: sourceReferenceId="+ workEffort.get(E.sourceReferenceId.name()) + "  etch=" + workEffort.get(E.etch.name()));
			workEffortSequenceHelper.insertWorkEffortSequence();
			workEffortFieldsGenerator.setProgressivo(workEffortSequenceHelper.getPogressivo());
			//GN-3022 devo sbiancare i valori altrimenti nn me li scrive
			workEffort.set(E.sourceReferenceId.name(), null);
			workEffort.set(E.etch.name(), null);
			WorkEffortSequence.setWorkEffortParameters(workEffort, workEffortFieldsGenerator);
			Debug.log("renumberWorkEffortSequence new workEffort: sourceReferenceId="+ workEffort.get(E.sourceReferenceId.name()) + "  etch=" + workEffort.get(E.etch.name()));
			workEffortList.add(workEffort);
			
			//cambio sequence all'associativa
			GenericValue workEffortAssoc = delegator.makeValue(E.WorkEffortAssoc.name());
			workEffortAssoc.setPKFields(gv);
			workEffortAssoc.set(E.sequenceNum.name(), Long.valueOf(workEffortSequenceHelper.getPogressivo()));
			workEffortAssocList.add(workEffortAssoc);
		}
		Debug.log("renumberWorkEffortSequence store workeffort");
		delegator.storeAll(workEffortList);
		
		Debug.log("renumberWorkEffortSequence store workEffortAssoc");
		delegator.storeAll(workEffortAssocList);
	}
	
	
	/**
	 * Prendo la lista delle associative escludendo quelle storicizzate
	 * @param delegator
	 * @param workEffortIdFrom
	 * @return
	 * @throws GenericEntityException
	 */
	private static List<GenericValue> workEffortAssocViewlist(Delegator delegator, String workEffortIdFrom) throws GenericEntityException {
		
		List<EntityCondition> entityCondition = new FastList<EntityCondition>();
		entityCondition.add(EntityCondition.makeCondition(E.workEffortIdFrom.name(), workEffortIdFrom));
		entityCondition.add(EntityCondition.makeCondition(E.wrToSnapShotId.name(),  EntityOperator.EQUALS, null));

		List<GenericValue> workEffortAssocViewlist = delegator.findList(E.WorkEffortAssocExtView.name(), EntityCondition.makeCondition(entityCondition), null, UtilMisc.toList(E.sequenceNum.name(), E.wrToCode.name()), null, false);
		
		if (workEffortAssocViewlist != null && workEffortAssocViewlist.size() > 0) {
			workEffortAssocViewlist = filterWorkEffortAssocViewlist(delegator, workEffortAssocViewlist);
		}
		
		return workEffortAssocViewlist;
	}
	
	private static List<GenericValue> filterWorkEffortAssocViewlist(Delegator delegator, List<GenericValue> workEffortAssocViewlist) throws GenericEntityException {
		
		List<GenericValue> list = FastList.newInstance();
		
		String workEffortTypeId = (String) workEffortAssocViewlist.get(0).get(E.workEffortTypeIdFrom.name());
		List<EntityCondition> entityCondition = new FastList<EntityCondition>();
		entityCondition.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), workEffortTypeId));
		entityCondition.add(EntityCondition.makeCondition(E.wefromWetoEnumId.name(),  "WETAFROM"));
		entityCondition.add(EntityCondition.makeCondition(E.isUnique.name(),  "N"));
		
		//TODO srivere in modo decente (stessa condizione che c'e nell'interfaccia)
		List<Map<String, String>> filterList = FastList.newInstance();
		List<GenericValue> filterGenericValueList = delegator.findList(E.WorkEffortTypeAssocAndAssocType.name(), EntityCondition.makeCondition(entityCondition), UtilMisc.toSet(E.workEffortAssocTypeId.name(), E.workEffortTypeIdRef.name()), null, null, false);
		for(GenericValue gv: filterGenericValueList){
			Map<String, String> map = FastMap.newInstance();
			map.put(E.workEffortAssocTypeId.name(), gv.getString(E.workEffortAssocTypeId.name()));
			map.put(E.workEffortTypeIdRef.name(), gv.getString(E.workEffortTypeIdRef.name()));
			filterList.add(map);
		}
		
		for(GenericValue gv: workEffortAssocViewlist){
			Map<String, String> map = FastMap.newInstance();
			map.put(E.workEffortAssocTypeId.name(), gv.getString(E.workEffortAssocTypeId.name()));
			map.put(E.workEffortTypeIdRef.name(), gv.getString(E.workEffortTypeIdTo.name()));
			if(filterList.contains(map) || UtilValidate.isEmpty(gv.getString(E.workEffortAssocTypeId.name()))){
				list.add(gv);
			}
		}
		
		return list;
	}
	
}
