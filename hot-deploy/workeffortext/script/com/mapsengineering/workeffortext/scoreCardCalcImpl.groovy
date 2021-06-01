import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.services.*;
import com.mapsengineering.workeffortext.scorecard.ScoreCard;
import com.mapsengineering.workeffortext.scorecard.PreviuosResultCleaner;
import org.ofbiz.service.ServiceUtil;
	
// parametri input
workEffortId = context.workEffortId;
thruDate = context.thruDate;
limitExcellent = context.limitExcellent;
limitMax = context.limitMax;
target = context.target;
limitMin = context.limitMin;
performance = context.performance;
scoreValueType = context.scoreValueType;
weightType = context.weightType;
cleanOnlyScoreCard = context.cleanOnlyScoreCard;

delegator = context.dctx.delegator;
dispatcher = context.dctx.dispatcher;

// Lista risultati per il joblog
List runResults = javolution.util.FastList.newInstance();
errorMessages = 0L;

ScoreCard card = null;

Map serviceOutParameters = FastMap.newInstance();

recordElaborated = 0L;
warnMessages = 0L;

//Messaggio di inizio elaborazione
wrk = delegator.findOne("WorkEffort", ["workEffortId": workEffortId], false);
if(UtilValidate.isEmpty(wrk)) {
	runResults.add(ServiceLogger.makeLogError("WorkEffort " + workEffortId + " not found in application", "023", null, null, null));
	res = ServiceUtil.returnError(String.format("Finish with %d blocking errors", 1));
	res.put("runResults", runResults);
	res.put("recordElaborated", 0L);
	res.put("warnMessages", 0L);
	res.put("errorMessages", 1L);
	res.put("elabRef1", workEffortId);
	return res;
}
wrkDesc = wrk.getString("workEffortId") + " - " + wrk.getString("workEffortName");
runResults.add(ServiceLogger.makeLogInfo("Start elaboration calc score for workEffort root \"" + wrkDesc + "\"", "INFO_GENERIC", wrk.getString("sourceReferenceId"), null, null));
// Istanza calcolo punteggio
card = new ScoreCard(dispatcher, context.userLogin);

/*
---------------------------------------------
	1. Cancellazione risultati precedenti
----------------------------------------------
*/

PreviuosResultCleaner cleaner = new PreviuosResultCleaner(delegator);
cleaner.cleanPreviousResult(workEffortId, thruDate, scoreValueType);
runResults.addAll(cleaner.getJobLogger().getMessages());

ScoreCard.ElaborationResults er0 = card.getResults();
if(er0.errorMessages > 0) {
	res = ServiceUtil.returnError(String.format("Finish with %d blocking errors", er0.errorMessages));
	runResults.addAll( card.getMessages() );
	res.put("runResults", runResults);
	res.put("recordElaborated", er0.recordElaborated);
	res.put("warnMessages", er0.warnMessages);
	res.put("errorMessages", er0.errorMessages);
	res.put("elabRef1", workEffortId);
	return res;
}

/**
 * GN-899
 * Esegui il calcolo solo se il parametro in ingresso cleanOnlyScoreCard == N, 
 * altrimenti eseguo solo la cancellzione del vecchio calcolo
 */
if (cleanOnlyScoreCard != 'Y'){
	
	/*
	 ---------------------------------------------
		 3. Calcolo punteggio e lettura risultati
	 ----------------------------------------------
	 */
	 serviceOutParameters.valueScoreCardCal = card.calculate(workEffortId, thruDate, limitExcellent, limitMax, target, limitMin, performance, scoreValueType, weightType);
	 
	 /*
	 ---------------------------------------------
		 End. Operazioni finali
	 ----------------------------------------------
	 */
	 
	 //
	 //Copio lista messaggi del calcolo
	 runResults.addAll( card.getMessages() );
	 
	 //
	 //Lettura valori di risultato
	 ScoreCard.ElaborationResults er = card.getResults();
	 errorMessages += er.errorMessages;
	 
	 recordElaborated = er.recordElaborated;
	 warnMessages = er.warnMessages;
}




//
// Costruisco la mappa di riotrno in base all interfaccia ScoreCardCalcImpl
//
Map res;
if (errorMessages > 0 ) {
	res = ServiceUtil.returnError(String.format("Finish with %d blocking errors", errorMessages));
} else {
	res = ServiceUtil.returnSuccess();
}

res.put("runResults", runResults);
res.put("recordElaborated", recordElaborated);
res.put("warnMessages", warnMessages);
res.put("errorMessages", errorMessages);
res.put("elabRef1", workEffortId);
res.put("serviceOutParameters", serviceOutParameters);

return res;
