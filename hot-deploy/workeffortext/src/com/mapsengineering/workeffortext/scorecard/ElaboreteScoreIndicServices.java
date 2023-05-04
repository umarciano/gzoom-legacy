package com.mapsengineering.workeffortext.scorecard;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.accountingext.services.E;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;

import bsh.EvalError;
import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * Execute scoreCardCalc or indicatorCalcObiettivo, after save movement
 *
 */
public class ElaboreteScoreIndicServices extends GenericService {

	public static final String MODULE = ElaboreteScoreIndicServices.class.getName();
	private static final String SERVICE_NAME = "elaboreteScoreIndicCrudServiceEpilog";
    private static final String SERVICE_TYPE = null;
    
	List<String> errorMessageList;

	/**
	 * Eseguo calcolo SCORE/INDIC
	 */
	public static Map<String, Object> elaboreteScoreIndicCrudServiceEpilog(DispatchContext dctx, Map<String, Object> context) {
		ElaboreteScoreIndicServices srv = new ElaboreteScoreIndicServices(dctx, context);
		srv.execute();
		return srv.getResult();
	}

	/**
	 * Constructor
	 */
	@SuppressWarnings("unchecked")
    public ElaboreteScoreIndicServices(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);	
		this.context =  (Map<String, Object>) context.get("parameters");
		errorMessageList = FastList.newInstance();
    }

	/**
	 * Esegue calcolo indicatori, popola mappa coi risultati dell'elaborazione
	 */
	public void execute() {
		//Controllo che nella tipologia il folder calcolo sia attivato:
		try {
			String elabScoreIndic = (String)context.get(E.elabScoreIndic.name());
			Debug.log("*** Start ElaboreteScoreIndicServices elabScoreIndic="+elabScoreIndic);
			
			if ("SCORE".equals(elabScoreIndic)) {					
				runService("scoreCardCalc", "WEFLD_ELAB");
				
			} else if ("INDIC".equals(elabScoreIndic)) {
				runService("indicatorCalcObiettivo", "WEFLD_ELIN");
			}
			Debug.log("*** End ElaboreteScoreIndicServices elabScoreIndic=");
			
		} catch (Exception e) {
			errorMessageList.add(e.getMessage());
			setResult(ServiceUtil.returnError(errorMessageList));
		}
		
	}
	
	private void runService(String service, String contentId) throws GeneralException, ParseException, EvalError {
		String workEffortId = (String)context.get(E.weTransWeId.name());
		String workEffortTypeId = (String)context.get(E.workEffortTypeId.name());
		
		GenericValue workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), true);
        
		boolean isFolderActivated = isFolderActivated(workEffortTypeId, contentId);
		Debug.log("*** ElaboreteScoreIndicServices isFolderActivated="+isFolderActivated + " workEffortId=" + workEffortId + " workEffortTypeId="+ workEffortTypeId + " contentId="+contentId);
		if (!isFolderActivated) {
			//TODO controllare cosa fare se errore o proseguire ocn elaborazione
			String error = UtilProperties.getMessage("BaseUiLabels", "ElaboreteScoreIndicServices_contentIdNull", getLocale());
			throw new GeneralException(error + contentId);
		}
		Debug.log("*** ElaboreteScoreIndicServices run service="+service);
		Map<String, Object> res = dispatcher.runSync(service, setDefaultParam(workEffortTypeId, workEffortId, workEffort.getString(E.workEffortParentId.name())));
		setResult(res);
	}
	
	private boolean isFolderActivated(String workEffortTypeId, String contentId) throws GenericEntityException {
		List<EntityCondition> entityCondition = new FastList<EntityCondition>();
		entityCondition.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), workEffortTypeId));
		entityCondition.add(EntityCondition.makeCondition(E.weTypeContentTypeId.name(), "FOLDER"));
		entityCondition.add(EntityCondition.makeCondition(E.contentId.name(), contentId)); 
		
		List<GenericValue> list = delegator.findList(E.WorkEffortTypeContent.name(), EntityCondition.makeCondition(entityCondition), null, null, null, false);
			
		if(UtilValidate.isEmpty(list))
			return false;
		return true;
	}
	
	private Map<String, Object> setDefaultParam(String workEffortTypeId, String workEffortId, String workEffortParentId) throws GeneralException, ParseException, EvalError {
		String elabScoreIndic = (String)context.get(E.elabScoreIndic.name());
		if ("SCORE".equals(elabScoreIndic)) {					
			return setDefaultParamScoreCardCalc(workEffortTypeId, workEffortParentId);
			
		} else if ("INDIC".equals(elabScoreIndic)) {					
			return setDefaultParamIndicatorCalcObiettivo(workEffortTypeId, workEffortId);
		}
		return null;
	}
	
	private Map<String, Object> setDefaultParamScoreCardCalc(String workEffortTypeId, String workEffortId) throws GeneralException, ParseException, EvalError {		
		Map<String, Object> map = FastMap.newInstance();
		map.put(E.limitExcellent.name(), "BUDGET");
		map.put(E.limitMax.name(), "BUDGET");
		map.put(E.target.name(), "BUDGET");
		map.put(E.limitMed.name(), "BUDGET");
        map.put(E.limitMin.name(), "BUDGET");
		map.put(E.performance.name(), "ACTUAL");		
		map.put(E.thruDate.name(), getSearchDate((String)context.get(E.searchDate.name())));
		map.put(E.scoreValueType.name(), "ACTUAL");			
		map.put(E.cleanOnlyScoreCard.name(), "N");		
		map.put(E.weightType.name(), "ORIG");
				
		map.put(E.workEffortId.name(), workEffortId);		
		map.put("userLogin", this.userLogin);
		
		Map<String, Object> mappaParams = getParams(workEffortTypeId, "WEFLD_ELAB");
		map.putAll(mappaParams);
		// poiche' i nomi dei params presenti nel folder, per esempio glFiscalTypeIdExcellentLimit,
		// e sono diversi da quelli attesi dal servizio, per esempio limitExcellent,
		// riscrivo la mappa dei parametri da passare al servizio
		Map<String, Object> serviceContext = getDctx().makeValidContext("scoreCardCalc", ModelService.IN_PARAM, map);
        if (UtilValidate.isNotEmpty(mappaParams.get("glFiscalTypeIdExcellentLimit"))) {
            serviceContext.put(E.limitExcellent.name(), mappaParams.get("glFiscalTypeIdExcellentLimit"));
        }
        if (UtilValidate.isNotEmpty(mappaParams.get("glFiscalTypeIdUpperLimit"))) {
            serviceContext.put(E.limitMax.name(), mappaParams.get("glFiscalTypeIdUpperLimit"));
        }
        if (UtilValidate.isNotEmpty(mappaParams.get("glFiscalTypeIdMediumLimit"))) {
            serviceContext.put(E.limitMed.name(), mappaParams.get("glFiscalTypeIdMediumLimit"));
        }
        if (UtilValidate.isNotEmpty(mappaParams.get("glFiscalTypeIdLowerLimit"))) {
            serviceContext.put(E.limitMin.name(), mappaParams.get("glFiscalTypeIdLowerLimit"));
        }
		
				
		Debug.log("*** ElaboreteScoreIndicServices setDefaultParamScoreCardCalc="+serviceContext);
		return serviceContext;
	}
	
	private Map<String, Object> setDefaultParamIndicatorCalcObiettivo(String workEffortTypeId, String workEffortId) throws GeneralException, ParseException, EvalError {
		Map<String, Object> map = FastMap.newInstance();
		map.put(E.glFiscalTypeIdInput.name(), "ACTUAL");
		map.put(E.glFiscalTypeIdOutput.name(), "ACTUAL"); 
		map.put(E.thruDate.name(), getSearchDate((String)context.get(E.searchDate.name())));
		map.put(E.workEffortId.name(), workEffortId);		
		map.put("userLogin", this.userLogin);
				
		map.put("onlyElaborateIndicator", getOnlyElaborateIndicator(workEffortTypeId));
		map.putAll(getParams(workEffortTypeId, "WEFLD_ELIN"));
		
		Debug.log("*** ElaboreteScoreIndicServices setDefaultParamIndicatorCalcObiettivo="+map);
		return map;
	}	
	
	private String getOnlyElaborateIndicator(String workEffortTypeId) throws GenericEntityException {
		String onlyElaborateIndicator = "N";
		
		// se sono un folder indicatore controllo se e attivo solo il calcolo indicatore o entrambi
		List<EntityCondition> entityCondition = new FastList<EntityCondition>();
		entityCondition.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), workEffortTypeId));
		entityCondition.add(EntityCondition.makeCondition(E.weTypeContentTypeId.name(), "WEFLD_ELIN"));
		
		List<GenericValue> list = delegator.findList(E.WorkEffortTypeContent.name(), EntityCondition.makeCondition(entityCondition), null, null, null, false);
		if (UtilValidate.isNotEmpty(list)){
			GenericValue gv = list.get(0);
			Debug.log("*** ElaboreteScoreIndicServices layoutContentId="+gv.get("layoutContentId"));
			if ("ONLY_ELIN".equals(gv.get(E.contentId.name()))) {
				onlyElaborateIndicator = "Y";
			}
		}
		return onlyElaborateIndicator;		
	}
	
	private Map<String, Object> getParams(String workEffortTypeId, String contentId) throws GenericEntityException, EvalError {
		WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, null, delegator);
		return paramsEvaluator.getParams(workEffortTypeId, contentId, false);
	}
	
	
	private Timestamp getSearchDate(String searchDate) throws GeneralException, ParseException {
		if (UtilValidate.isNotEmpty(searchDate)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
			Date date =  sdf.parse(searchDate);
			return UtilDateTime.toTimestamp(date);	
			//return UtilDateTime.stringToTimeStamp(searchDate, getTimeZone(), getLocale() );			
		} else {
			throw new GeneralException(UtilProperties.getMessage("BaseUiLabels", "ElaboreteScoreIndicServices_dateNull", getLocale()));
		}
	}

}
