package com.mapsengineering.workeffortext.birt.event;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.security.Security;

import com.mapsengineering.base.birt.util.Utils;

public class WorkEffortextBirtEvents {
	
	public final static String pattern_date="dd/MM/yyyy";
	public final static String from_date_default="01/01/1900";
	public final static String thru_date_default="31/12/2100";
	
	
	private WorkEffortextBirtEvents() {}
	
	/**
	 * La funzione vede se la scheda passata o indice o un figlio,
	 * se sono indice valorizzo il workEffortId,
	 * altrimenti valorizzo il workEffortIdChild
	 * 
	 * Eseguo la ricerca escludendo le assoc:
	 * TEMPL
	 * SNAPSHOT
	 * COPY
	 * 
	 * @param context
	 */
	public static void checkChildRootEquality(Map<String, Object> context) {
		try {
			Delegator delegator = UtilGenerics.cast(context.get("delegator"));

			String workEffortId = UtilGenerics.cast(context.get("workEffortId"));
			String workEffortIdChild = UtilGenerics.cast(context.get("workEffortIdChild"));

			List<EntityCondition> entityCondition = new FastList<EntityCondition>();
			entityCondition.add(EntityCondition.makeCondition("workEffortIdTo", workEffortId));
			entityCondition.add(EntityCondition.makeCondition("workEffortAssocTypeId", EntityOperator.NOT_EQUAL, "SNAPSHOT"));
			entityCondition.add(EntityCondition.makeCondition("workEffortAssocTypeId", EntityOperator.NOT_EQUAL, "COPY"));
			entityCondition.add(EntityCondition.makeCondition("workEffortAssocTypeId", EntityOperator.NOT_EQUAL, "TEMPL"));
			
			boolean isRoot = false;
			String localWorkEffortId = null;
			if (UtilValidate.isNotEmpty(workEffortIdChild)) {
				if (UtilValidate.isNotEmpty(workEffortId)) {
					if (!workEffortIdChild.equals(workEffortId)) {
						context.put("workEffortId", "");

						localWorkEffortId = workEffortIdChild;
					} else {						
						List<GenericValue> workEffortAssocList = delegator.findList("ReportChildRootEquality", EntityCondition.makeCondition(entityCondition), null, null, null, true);
						if (UtilValidate.isNotEmpty(workEffortAssocList) && workEffortAssocList.size() == 1) {
							
							context.put("workEffortIdChild", "");

							localWorkEffortId = workEffortId;
							isRoot = true;
							
							
						} else {
							context.put("workEffortId", "");

							localWorkEffortId = workEffortIdChild;
						}
					}
				} else {
					localWorkEffortId = workEffortIdChild;
				}
			} else if (UtilValidate.isNotEmpty(workEffortId)) {
				List<GenericValue> workEffortAssocList = delegator.findList("ReportChildRootEquality", EntityCondition.makeCondition(entityCondition), null, null, null, true);
				if (UtilValidate.isNotEmpty(workEffortAssocList)) {
					if (workEffortAssocList.size() > 1) {
						context.put("workEffortId", "");
						context.put("workEffortIdChild", workEffortId);
					} else {
						isRoot = true;
					}
				}

				localWorkEffortId = workEffortId;
			}

			if (UtilValidate.isNotEmpty(localWorkEffortId) && (!isRoot)) {
				GenericValue workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", localWorkEffortId), true);
				GenericValue workEffortType = workEffort.getRelatedOne("WorkEffortType");
				if ("Y".equals(workEffortType.get("isRoot"))) {
					context.put("orgUnitId", workEffort.get("orgUnitId"));
					context.put("orgUnitRoleTypeId", workEffort.get("orgUnitRoleTypeId"));
				}
			}

		} catch (GenericEntityException e) {
		}

	}
	/**
	 * verifica workEffortId e workEffortIdChild senza settare i campi orgUnit
	 * @param context
	 */
	public static void checkChildRootEqualityWithoutOrgUnit(Map<String, Object> context) {
		String orgUnitId = UtilGenerics.cast(context.get("orgUnitId"));
		String orgUnitRoleTypeId = UtilGenerics.cast(context.get("orgUnitRoleTypeId"));
		checkChildRootEquality(context);
		if (UtilValidate.isEmpty(orgUnitId)) {
			context.remove("orgUnitId");
		}
		if (UtilValidate.isEmpty(orgUnitRoleTypeId)) {
			context.remove("orgUnitRoleTypeId");
		}
	}

	public static void cleanRootValue(Map<String, Object> context) {
		String workEffortId = UtilGenerics.cast(context.get("workEffortId"));
		String workEffortIdChild = UtilGenerics.cast(context.get("workEffortIdChild"));

		if (UtilValidate.isNotEmpty(workEffortId) && UtilValidate.isNotEmpty(workEffortIdChild)) {
			context.put("workEffortId", "");
		}
	}
	public static void cleanChildValueEqualRoot(Map<String, Object> context) {
		try {
			Delegator delegator = UtilGenerics.cast(context.get("delegator"));
			String workEffortId = UtilGenerics.cast(context.get("workEffortId"));
			String workEffortIdChild = UtilGenerics.cast(context.get("workEffortIdChild"));

			if (UtilValidate.isNotEmpty(workEffortIdChild)) {
				if (workEffortIdChild.equals(workEffortId)) {
					context.put("workEffortIdChild", "");
				} else {
					GenericValue workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortIdChild), true);
					GenericValue workEffortType = workEffort.getRelatedOne("WorkEffortType");
					if ("Y".equals(workEffortType.get("isRoot"))) {
						context.put("workEffortIdChild", "");
						
						context.put("workEffortId", workEffortIdChild);
					}
				}
			}
		} catch (GenericEntityException e) {
		}
	}
	
	/**
	 * utilizzato per il problema delle stampe di birt 
	 * @param context
	 * @throws ParseException
	 */
	public static void setMonitoringDate(Map<String, Object> context) throws ParseException {
		
		if (UtilValidate.isEmpty(context.get("monitoringDate"))) {
			context.put("monitoringDate", Calendar.getInstance().getTime());
		}		
		
	}	
	
	/**
	 * imposta la data a inizio anno
	 * @param context
	 * @throws ParseException
	 */
	public static void setDateYearStart(Map<String, Object> context) throws ParseException {
		context.put("dateYearStart", UtilDateTime.getYearStart(UtilDateTime.nowTimestamp()));
	}
	
	/**
	 * imposta la data a fine anno
	 * @param context
	 * @throws ParseException
	 */
	public static void setDateYearEnd(Map<String, Object> context) throws ParseException {
		context.put("dateYearEnd", UtilDateTime.getYearEnd(UtilDateTime.nowTimestamp()));
	}
	
	/**
	 * utilizzato per popolare la monitoring date partendo dal customTimePeriod
	 * @param context
	 * @throws ParseException
	 */
	
	public static void setMonitoringDateFromCustomTimePeriod(Map<String, Object> context) 
			throws ParseException {
		if(!UtilValidate.isEmpty(context.get("customTimePeriod"))) {
			context.put("monitoringDate", context.get("customTimePeriod"));
		}

	}
	
	/**
	 * utilizzato per il problema delle stampe null su birt, setta la data al 01/01/1900 se gli arriva un valore null 
	 * @param context
	 * @throws ParseException
	 */
	public static void setDefaultFromDate(Map<String, Object> context) throws ParseException {
		
		if (UtilValidate.isEmpty(context.get("fromDate"))) {
			DateFormat dateFormat = new SimpleDateFormat(pattern_date);
			Date date = dateFormat.parse(from_date_default);
			long time = date.getTime();
			Timestamp fromDateTimestamp= new Timestamp(time);
			context.put("fromDate", fromDateTimestamp);
		}		
		
	}
		
 
	/**
	 * utilizzato per il problema delle stampe null su birt, setta la data al 31/12/2100 se gli arriva un valore null 
	 * @param context
	 * @throws ParseException
	 */
	public static void setDefaultThruDate(Map<String, Object> context) throws ParseException {
		
		if (UtilValidate.isEmpty(context.get("thruDate"))) {
			DateFormat dateFormat = new SimpleDateFormat(pattern_date);
			Date date = dateFormat.parse(thru_date_default);
			long time = date.getTime();
			Timestamp fromDateTimestamp= new Timestamp(time);
			context.put("thruDate", fromDateTimestamp);
		}		
		
	}
	
	/**
     * Nelle stampe analisi andiamo a settare la referenceDate: parametro obbligatorio. 
     * @param context
     * @throws ParseException
     */
	public static void setReferenceDate(Map<String, Object> context) {
        try {
            
            if (UtilValidate.isEmpty(context.get("monitoringDate"))) {
                Delegator delegator = UtilGenerics.cast(context.get("delegator"));
                String workEffortAnalysisId = UtilGenerics.cast(context.get("workEffortAnalysisId"));
                if (UtilValidate.isNotEmpty(workEffortAnalysisId)) {
                    GenericValue workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", UtilMisc.toMap("workEffortAnalysisId", workEffortAnalysisId), true);
                    context.put("monitoringDate", workEffortAnalysis.get("referenceDate"));
                }
            }   
                        
        } catch (GenericEntityException e) {
        }
    }
	
	/**
	 * Utilizzata nelle stampe coem filtro nella query in base al ruole
	 * dell utente connesso
	 * @param context
	 */
	public static void setUserProfile(Map<String, Object> context) {
	    
	    Security security = (Security)context.get("security");
	    GenericValue userLogin = (GenericValue)context.get("userLogin");
	    String localDispatcherName = (String)context.get("localDispatcherName");
	    String userProfile = Utils.getUserProfile(security, userLogin, localDispatcherName);
	    context.put("userProfile", userProfile);
	}
	
	/**
	 * setta la data del workEffort
	 * @param context
	 */
	public static void getMonitoringDateFromWorkEffort(Map<String, Object> context) {	
    	try {
			Delegator delegator = UtilGenerics.cast(context.get("delegator"));
			String workEffortId = UtilGenerics.cast(context.get("workEffortId"));
			if (UtilValidate.isEmpty(workEffortId)) {
				workEffortId = UtilGenerics.cast(context.get("workEffortIdChild"));
			}
			if (UtilValidate.isNotEmpty(workEffortId)) {
				GenericValue workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
				if (UtilValidate.isNotEmpty(workEffort)) {
					context.put("monitoringDate", workEffort.get("estimatedStartDate"));
				}
			}
		} catch(GenericEntityException e) {
			
		}
	}
	
	/**
	 * Metodo di debug per i parametri della scheda obiettivi
	 * Utilizzato per validare e processare i parametri workEffortId 
	 * nei report REPORT_SOO e REPORT_LVI
	 * @param context
	 */
	public static void debugSchedaObiettiviParams(Map<String, Object> context) {
		try {
			String workEffortId = UtilGenerics.cast(context.get("workEffortId"));
			// Log dei parametri per debug se necessario
			if (UtilValidate.isNotEmpty(workEffortId)) {
				// Processa il workEffortId se necessario
				// Per ora manteniamo il valore così com'è
				context.put("workEffortId", workEffortId);
			}
		} catch (Exception e) {
			// Gestione errori silenziosa per non interrompere il flusso di stampa
		}
	}
}
