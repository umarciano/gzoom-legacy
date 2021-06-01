import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.entity.condition.*;

// bug 4043
// mappa di 10 elementi, uno per ciascuna colonna per il calcolo dei valori pesati
def budgetValueArray = [0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d];
def actualValueArray = [0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d];

def budgetValueMap = [:];
def actualValueMap = [:];
def weightSum = 0.0d;

def list = [];
def workEffortGanttExtensionViewList = context.workEffortAchieveList;

def pattern = "#,##0";
def wea = null;
//Bug 4677
def isMonitor = "N";
if (UtilValidate.isNotEmpty(parameters.workEffortAnalysisId)) {
	wea = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId" : parameters.workEffortAnalysisId], false);
	isMonitor = wea.isMonitor;
	
	
	if (UtilValidate.isNotEmpty(workEffortGanttExtensionViewList)) {
		workEffortGanttExtensionViewList.each { element ->
		
			def workEffortGanttExtensionView = [:];
			workEffortGanttExtensionView.putAll(element);
			
			//inizializzo tutti i valori on il ""
			workEffortGanttExtensionView.valBud = "";
			workEffortGanttExtensionView.codBud = "";
			workEffortGanttExtensionView.valBudP0 = "";
			workEffortGanttExtensionView.codBudP0 = "";
			workEffortGanttExtensionView.comBudP0 = "";
			workEffortGanttExtensionView.valBudP1 = "";
			workEffortGanttExtensionView.codBudP1 = "";
			workEffortGanttExtensionView.comBudP1 = "";
			workEffortGanttExtensionView.valBudP2 = "";
			workEffortGanttExtensionView.codBudP2 = "";
			workEffortGanttExtensionView.comBudP2 = "";
			workEffortGanttExtensionView.valBudP3 = "";
			workEffortGanttExtensionView.codBudP3 = "";
			workEffortGanttExtensionView.comBudP3 = "";
			workEffortGanttExtensionView.valBudP4 = "";
			workEffortGanttExtensionView.codBudP4 = "";
			workEffortGanttExtensionView.comBudP4 = "";
			workEffortGanttExtensionView.valBudM1 = "";
			workEffortGanttExtensionView.codBudM1 = "";
			workEffortGanttExtensionView.comBudM1 = "";
			workEffortGanttExtensionView.valBudM2 = "";
			workEffortGanttExtensionView.codBudM2 = "";
			workEffortGanttExtensionView.comBudM2 = "";
			workEffortGanttExtensionView.valBudM3 = "";
			workEffortGanttExtensionView.codBudM3 = "";
			workEffortGanttExtensionView.comBudM3 = "";
			workEffortGanttExtensionView.valBudM4 = "";
			workEffortGanttExtensionView.codBudM4 = "";
			workEffortGanttExtensionView.comBudM4 = "";
			workEffortGanttExtensionView.valAct = "";
			workEffortGanttExtensionView.codAct = "";
			workEffortGanttExtensionView.valActP0 = "";
			workEffortGanttExtensionView.codActP0 = "";
			workEffortGanttExtensionView.comActP0 = "";
			workEffortGanttExtensionView.valActP1 = "";
			workEffortGanttExtensionView.codActP1 = "";
			workEffortGanttExtensionView.comActP1 = "";
			workEffortGanttExtensionView.valActP2 = "";
			workEffortGanttExtensionView.codActP2 = "";
			workEffortGanttExtensionView.comActP2 = "";
			workEffortGanttExtensionView.valActP3 = "";
			workEffortGanttExtensionView.codActP3 = "";
			workEffortGanttExtensionView.comActP3 = "";
			workEffortGanttExtensionView.valActP4 = "";
			workEffortGanttExtensionView.codActP4 = "";
			workEffortGanttExtensionView.comActP4 = "";
			workEffortGanttExtensionView.valActM1 = "";
			workEffortGanttExtensionView.codActM1 = "";
			workEffortGanttExtensionView.comActM1 = "";
			workEffortGanttExtensionView.valActM2 = "";
			workEffortGanttExtensionView.codActM2 = "";
			workEffortGanttExtensionView.comActM2 = "";
			workEffortGanttExtensionView.valActM3 = "";
			workEffortGanttExtensionView.codActM3 = "";
			workEffortGanttExtensionView.comActM3 = "";
			workEffortGanttExtensionView.valActM4 = "";
			workEffortGanttExtensionView.codActM4 = "";
			workEffortGanttExtensionView.comActM4 = "";
			workEffortGanttExtensionView.valBenP0 = "";
			workEffortGanttExtensionView.codBenP0 = "";
			workEffortGanttExtensionView.comBenP0 = "";
			
			// bug 4043
			def currentWeight = (UtilValidate.isNotEmpty(workEffortGanttExtensionView.assocWeight)) ? workEffortGanttExtensionView.assocWeight : 0.0d; 
			weightSum += currentWeight;
			
			//aggiungo peso bug 4202
			workEffortGanttExtensionView.assocWeight = currentWeight;
		
			// bug 4043, mi servono i dati ordinati per data crescente, non decrescente
			//def workEffortGanttExtensionViewListEle  = delegator.findList("WorkEffortGanttExtensionView", EntityCondition.makeCondition([EntityCondition.makeCondition("analisi", parameters.workEffortAnalysisId), EntityCondition.makeCondition("obiettivoPadre", parameters.workEffortId),EntityCondition.makeCondition("titolo", element.workEffortName)]), null, ["-valTransactionDate"], null, false);
			def workEffortGanttExtensionViewListEle  = delegator.findList("WorkEffortGanttExtensionView", EntityCondition.makeCondition([EntityCondition.makeCondition("analisi", parameters.workEffortAnalysisId), EntityCondition.makeCondition("obiettivoPadre", parameters.workEffortId),EntityCondition.makeCondition("titolo", element.workEffortName)]), null, ["valTransactionDate"], null, false);
			//bug 4043  
			def lastValueBudget = 0.0d;
			def lastValueActual = 0.0d;			
	
			if (UtilValidate.isNotEmpty(workEffortGanttExtensionViewListEle)) {
				workEffortGanttExtensionViewListEle.each { item ->
	
					//aggiungo date
					workEffortGanttExtensionView.valTransactionDate = item.valTransactionDate;
				
					//aggiungo il tipo
					if(UtilValidate.isNotEmpty(item.tipudm) && !"".equals(item.tipudm)){
						workEffortGanttExtensionView.tipudm = item.tipudm;
					}	
								
					//bug 5242 prendo il tipo selezionato sull'analisi
					if(wea.typeBalanceTarIndId.equals(item.valGlFiscalTypeId)){
						if (item.valTransactionDate == item.waYearM4Prev) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valBudM4 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                budgetValueArray[0] += item.valAmount * currentWeight;
							}
							
							workEffortGanttExtensionView.codBudM4 = item.wrUomCode;
							workEffortGanttExtensionView.comBudM4 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearM3Prev) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valBudM3 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                budgetValueArray[1] += (item.valAmount * currentWeight) - lastValueBudget;
							}
							
							workEffortGanttExtensionView.codBudM3 = item.wrUomCode;
							workEffortGanttExtensionView.comBudM3 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearM2Prev) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valBudM2 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                budgetValueArray[2] += (item.valAmount * currentWeight) - lastValueBudget;
							}
							
							workEffortGanttExtensionView.codBudM2 = item.wrUomCode;
							workEffortGanttExtensionView.comBudM2 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearM1Prev) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valBudM1 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                budgetValueArray[3] += (item.valAmount * currentWeight) - lastValueBudget;
							}
							
							workEffortGanttExtensionView.codBudM1 = item.wrUomCode;
							workEffortGanttExtensionView.comBudM1 = item.wrUomCode;
						}
						//Bug 4677
						if("Y".equals(isMonitor)) {
							if (item.valTransactionDate == item.waReferenceDate) {
								if (UtilValidate.isNotEmpty(item.valAmount)) {
									workEffortGanttExtensionView.valBud = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                    budgetValueArray[4] += (item.valAmount * currentWeight) - lastValueBudget;
								}
								
								workEffortGanttExtensionView.codBud = item.wrUomCode;
							}
						}
						if (item.valTransactionDate == item.waYearPrev) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valBudP0 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                budgetValueArray[5] += (item.valAmount * currentWeight) - lastValueBudget;
							}
							
							workEffortGanttExtensionView.codBudP0 = item.wrUomCode;
							workEffortGanttExtensionView.comBudP0 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearP1Prev) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valBudP1 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                budgetValueArray[6] += (item.valAmount * currentWeight) - lastValueBudget;
							}
							
							workEffortGanttExtensionView.codBudP1 = item.wrUomCode;
							workEffortGanttExtensionView.comBudP1 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearP2Prev) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valBudP2 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                budgetValueArray[7] += (item.valAmount * currentWeight) - lastValueBudget;
							}
							
							workEffortGanttExtensionView.codBudP2 = item.wrUomCode;
							workEffortGanttExtensionView.comBudP2 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearP3Prev) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valBudP3 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                budgetValueArray[8] += (item.valAmount * currentWeight) - lastValueBudget;
							}
							
							workEffortGanttExtensionView.codBudP3 = item.wrUomCode;
							workEffortGanttExtensionView.comBudP3 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearP4Prev) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valBudP4 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                budgetValueArray[9] += (item.valAmount * currentWeight) - lastValueBudget;
							}
							
							workEffortGanttExtensionView.codBudP4 = item.wrUomCode;
							workEffortGanttExtensionView.comBudP4 = item.wrUomCode;
						}
	
                        if (UtilValidate.isNotEmpty(item.valAmount)) {
                            lastValueBudget += (item.valAmount * currentWeight);
                        }
					} 
					
					//bug 5242 prendo il tipo selezionato sull'analisi
					if(wea.typeBalanceConsIndId.equals(item.valGlFiscalTypeId)){
						if (item.valTransactionDate == item.waYearM4Real) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valActM4 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                actualValueArray[0] += (item.valAmount * currentWeight);
							}
							
							workEffortGanttExtensionView.codActM4 = item.wrUomCode;
							workEffortGanttExtensionView.comActM4 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearM3Real) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valActM3 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                actualValueArray[1] += (item.valAmount * currentWeight) - lastValueActual;
							}
							
							workEffortGanttExtensionView.codActM3 = item.wrUomCode;
							workEffortGanttExtensionView.comActM3 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearM2Real) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valActM2 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                actualValueArray[2] += (item.valAmount * currentWeight) - lastValueActual;
							}
							
							workEffortGanttExtensionView.codActM2 = item.wrUomCode;
							workEffortGanttExtensionView.comActM2 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearM1Real) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valActM1 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                actualValueArray[3] += (item.valAmount * currentWeight) - lastValueActual;
							}
							
							workEffortGanttExtensionView.codActM1 = item.wrUomCode;
							workEffortGanttExtensionView.comActM1 = item.wrUomCode;
						}
						//Bug 4677
						if("Y".equals(isMonitor)) {
							if (item.valTransactionDate == item.waReferenceDate) {
								if (UtilValidate.isNotEmpty(item.valAmount)) {
									workEffortGanttExtensionView.valAct = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                    actualValueArray[4] += (item.valAmount * currentWeight) - lastValueActual;
								}
								
								workEffortGanttExtensionView.codAct = item.wrUomCode;
							}
						}
						if (item.valTransactionDate == item.waYearReal) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valActP0 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                actualValueArray[5] += (item.valAmount * currentWeight) - lastValueActual;
							}
							
							workEffortGanttExtensionView.codActP0 = item.wrUomCode;
							workEffortGanttExtensionView.comActP0 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearP1Real) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valActP1 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                actualValueArray[6] += (item.valAmount * currentWeight) - lastValueActual;
							}
							
							workEffortGanttExtensionView.codActP1 = item.wrUomCode;
							workEffortGanttExtensionView.comActP1 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearP2Real) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valActP2 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                actualValueArray[7] += (item.valAmount * currentWeight) - lastValueActual;
							}
							
							workEffortGanttExtensionView.codActP2 = item.wrUomCode;
							workEffortGanttExtensionView.comActP2 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearP3Real) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valActP3 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                actualValueArray[8] += (item.valAmount * currentWeight) - lastValueActual;
							}
							
							workEffortGanttExtensionView.codActP3 = item.wrUomCode;
							workEffortGanttExtensionView.comActP3 = item.wrUomCode;
						}
						if (item.valTransactionDate == item.waYearP4Real) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valActP4 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
                                actualValueArray[9] += (item.valAmount * currentWeight) - lastValueActual;
							}
							
							workEffortGanttExtensionView.codActP4 = item.wrUomCode;
							workEffortGanttExtensionView.comActP4 = item.wrUomCode;
						}
						
                        if (UtilValidate.isNotEmpty(item.valAmount)) {
                            lastValueActual += (item.valAmount * currentWeight);
                        }
					}
					
					//bug 5242 prendo il tipo selezionato sull'analisi
					/*if ("BENCH".equals(item.valGlFiscalTypeId)) {
						if (item.valTransactionDate == item.waYearPrev) {
							if (UtilValidate.isNotEmpty(item.valAmount)) {
								workEffortGanttExtensionView.valBenP0 = UtilFormatOut.formatDecimalNumber(item.valAmount, pattern, locale) +"%";
							}
							workEffortGanttExtensionView.codBenP0 = item.wrUomCode;
							workEffortGanttExtensionView.comBenP0 = item.wrUomCode;
						}
					} */
	
				}
			}
			
			
			list.add(workEffortGanttExtensionView);		
		}
		
	}
	context.workEffortGanttExtensionViewList = list;

	//
	// bug 4043 - normalizzo le medie calcolate
	//
	if (weightSum != 0.0d) {
		
		for (int i = 0; i < 10; i++) {
			budgetValueArray[i] /= weightSum;
			actualValueArray[i] /= weightSum;
		}
	}
	
	//
	// eseguo le somme progressive fermandomi all'ultima colonna diversa da 0
	// (eseguo il controllo su tutto l'array per situazione nelle quali possono esserci dei valori non consecutivi)
	//
	// trovo indici
	def budIdx = 0;
	def actIdx = 0;
	for (int i = 0; i < 10; i++) {
		if (budgetValueArray[i] != 0.0d) {
			budIdx = i;
		}
		if (actualValueArray[i] != 0.0d) {
			actIdx = i;
		}
	}
	// ora calcolo le somme
	def last = 0.0d; 
	for (int i = 0; i <= budIdx; i++) {
		budgetValueArray[i] += last;
		last = budgetValueArray[i];
	}
	last = 0.0d;
	for (int i = 0; i <= actIdx; i++) {
		actualValueArray[i] += last;
		last = actualValueArray[i];
	}
	
	//
	// formatto i valori per output su pagina
	// 
	budgetValueMap.M4 = UtilFormatOut.formatDecimalNumber(budgetValueArray[0], pattern, locale);
	budgetValueMap.M3 = UtilFormatOut.formatDecimalNumber(budgetValueArray[1], pattern, locale);
	budgetValueMap.M2 = UtilFormatOut.formatDecimalNumber(budgetValueArray[2], pattern, locale);
	budgetValueMap.M1 = UtilFormatOut.formatDecimalNumber(budgetValueArray[3], pattern, locale);
	budgetValueMap.ACT = UtilFormatOut.formatDecimalNumber(budgetValueArray[4], pattern, locale);
	budgetValueMap.P0 = UtilFormatOut.formatDecimalNumber(budgetValueArray[5], pattern, locale);
	budgetValueMap.P1 = UtilFormatOut.formatDecimalNumber(budgetValueArray[6], pattern, locale);
	budgetValueMap.P2 = UtilFormatOut.formatDecimalNumber(budgetValueArray[7], pattern, locale);
	budgetValueMap.P3 = UtilFormatOut.formatDecimalNumber(budgetValueArray[8], pattern, locale);
	budgetValueMap.P4 = UtilFormatOut.formatDecimalNumber(budgetValueArray[9], pattern, locale);
	
	actualValueMap.M4 = UtilFormatOut.formatDecimalNumber(actualValueArray[0], pattern, locale);
	actualValueMap.M3 = UtilFormatOut.formatDecimalNumber(actualValueArray[1], pattern, locale);
	actualValueMap.M2 = UtilFormatOut.formatDecimalNumber(actualValueArray[2], pattern, locale);
	actualValueMap.M1 = UtilFormatOut.formatDecimalNumber(actualValueArray[3], pattern, locale);
	actualValueMap.ACT = UtilFormatOut.formatDecimalNumber(actualValueArray[4], pattern, locale);
	actualValueMap.P0 = UtilFormatOut.formatDecimalNumber(actualValueArray[5], pattern, locale);
	actualValueMap.P1 = UtilFormatOut.formatDecimalNumber(actualValueArray[6], pattern, locale);
	actualValueMap.P2 = UtilFormatOut.formatDecimalNumber(actualValueArray[7], pattern, locale);
	actualValueMap.P3 = UtilFormatOut.formatDecimalNumber(actualValueArray[8], pattern, locale);
	actualValueMap.P4 = UtilFormatOut.formatDecimalNumber(actualValueArray[9], pattern, locale);
	
	
	//aggiungo la descrizione bug 4202
	def descrizioniValori = [:];
	
	/*benchDesc =  delegator.findOne("GlFiscalType", org.ofbiz.base.util.UtilMisc.toMap("glFiscalTypeId", "BENCH"), true);
	if(UtilValidate.isNotEmpty(benchDesc)){
		descrizioniValori.benchDesc = benchDesc.description;
	}*/
	
	actualDesc =  delegator.findOne("GlFiscalType", org.ofbiz.base.util.UtilMisc.toMap("glFiscalTypeId", wea.typeBalanceConsIndId), false);
	if(UtilValidate.isNotEmpty(actualDesc)){
		descrizioniValori.actualDesc = actualDesc.description;
	}
	
	budgetDesc =  delegator.findOne("GlFiscalType", org.ofbiz.base.util.UtilMisc.toMap("glFiscalTypeId", wea.typeBalanceTarIndId), false);	
	if(UtilValidate.isNotEmpty(budgetDesc)){
		descrizioniValori.budgetDesc = budgetDesc.description;
	}
	
	context.descrizioniValori = descrizioniValori;
	
	context.budgetValueMap = budgetValueMap;
	context.actualValueMap = actualValueMap;
	context.weightSum = weightSum;
}




