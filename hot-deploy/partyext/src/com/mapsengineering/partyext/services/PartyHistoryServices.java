package com.mapsengineering.partyext.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.util.DateUtilService;
import com.mapsengineering.partyext.common.E;

import javolution.util.FastList;

/**
 * PartyHistoryServices
 *
 */
public class PartyHistoryServices extends GenericService  {
    
    public static final String MODULE = PartyHistoryServices.class.getName();
    private static final String RESOURCE_ERROR = "PartyExtErrorUiLabels";
    private static final String RESOURCE = "PartyExtUiLabels";
    private static final String SERVICE_NAME = "updatePartyEndDate";
    private static final String SERVICE_TYPE_ID = "CREATE_PARTY_HISTORY";
    private static final String DATE_INITIAL = "2001-01-01 00:00:00";
    private Locale locale;
    
    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public PartyHistoryServices(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, SERVICE_NAME, SERVICE_TYPE_ID, MODULE);
    }

    /**
     * Main service
     * @param dctx
     * @param context
     * @return
     * @throws GeneralException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> createPartyHistory(DispatchContext dctx, Map<String, ? extends Object> context) throws GeneralException {
        PartyHistoryServices obj = new PartyHistoryServices(dctx, (Map<String, Object>)context);
        obj.create();
        return obj.getResult();
    }
    
    /**
     * Create PartyHistory
     */
    public void create() {
        locale = (Locale) context.get(E.locale.name());
        String partyId = (String)context.get(E.partyId.name());
        
        jobLogger.printLogInfo(UtilProperties.getMessage(RESOURCE, "CreatePartyHistory_Begin", locale));
        try {
            
            GenericValue person = delegator.findByPrimaryKey(E.Person.name(), UtilMisc.toMap(E.partyId.name(), partyId));
            GenericValue party = delegator.findByPrimaryKey(E.Party.name(), UtilMisc.toMap(E.partyId.name(), partyId));
            /**
             * Controllo se la data inserita rispetta i criteri per la nuova storicizzazione
             */
            int controlValidate = controlValidDate((Timestamp)context.get(E.emplPositionTypeDate.name()), (Timestamp)person.get(E.emplPositionTypeDate.name()) );
            if (controlValidate == 1) {
               //Loggo solo che non e prevista storicizzazione 
               jobLogger.printLogInfo(UtilProperties.getMessage(RESOURCE, "CreatePartyHistory_NotProvided", locale));
            } else if (controlValidate == 2) {
                //vado a storicizzare
                createStore((Timestamp)context.get(E.emplPositionTypeDate.name()), (Timestamp)person.get(E.emplPositionTypeDate.name()), person, party );
            } else {
                //errore
                String msg = UtilProperties.getMessage(RESOURCE_ERROR, "CreatePartyHistory_ErrorDate", locale);
                jobLogger.printLogError(msg);
                setResult(ServiceUtil.returnError(msg));
            }
            
        } catch (GenericEntityException e) {
            String msg = UtilProperties.getMessage(RESOURCE_ERROR, "CreatePartyHistory_ErrorCreatePartyHistory", locale);;
            jobLogger.printLogError(e, msg);
            setResult(ServiceUtil.returnError(msg + e.getMessage())); 
        } /*finally {
            getResult().put(ServiceLogger.MESSAGES, jobLogger.getMessages());
        }  */     
    }
    
    /**
     * Controllo ritorna i valori :
     *  0. se si vuole storicizzare ma non sono valorizzate le condizioni (ERRORE)
     *  1. se le dateNew e dateold sono vuote non devo effettuare nessuna storicizzazione
     *  2. se la datenew e valorizzata devo fare la storicizzazione e controllo:
     *      - che la dataOld sia vuota (e la prima volta che si storicizza)
     *      - che la dateNew > dateold
     * @param dateNew
     * @param dateOld
     * @return
     */
    private static int controlValidDate(Timestamp dateNew, Timestamp dateOld) {
        int result = 0;
        if(UtilValidate.isEmpty(dateNew) || (UtilValidate.isNotEmpty(dateOld) && dateNew.compareTo(dateOld) == 0)){
            result = 1;
        } else if(UtilValidate.isNotEmpty(dateNew) && (UtilValidate.isEmpty(dateOld) || dateNew.compareTo(dateOld) > 0)) {
            result = 2;
        }
        return result;
    }
    
    private void createStore(Timestamp dateNew, Timestamp dateOld, GenericValue gv, GenericValue party) throws GenericEntityException{
        if (UtilValidate.isEmpty(dateOld)) {
            dateOld = Timestamp.valueOf(DATE_INITIAL);
        }        
        GenericValue genericValue = delegator.makeValue(E.PartyHistory.name());
        genericValue.set(E.partyId.name(), gv.get(E.partyId.name()));
        genericValue.set(E.fromDate.name(), dateOld);
        genericValue.set(E.thruDate.name(), new Timestamp(DateUtilService.getPreviousDay(dateNew).getTime()));        
        genericValue.set(E.employmentAmount.name(), gv.get(E.employmentAmount.name()));
        genericValue.set(E.emplPositionTypeId.name(), gv.get(E.emplPositionTypeId.name()));
        genericValue.set(E.description.name(), party.get(E.description.name()));
        genericValue.set(E.comments.name(), gv.get(E.comments.name()));
        
        delegator.create(genericValue);
        jobLogger.printLogInfo(UtilProperties.getMessage(RESOURCE, "CreatePartyHistory_create", locale)+" " + genericValue.toString());
    }
    
    
    /**
     * Main service
     * @param dctx
     * @param context
     * @return
     * @throws GeneralException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> validateDatePartyHistory(DispatchContext dctx, Map<String, ? extends Object> context) throws GeneralException {
        PartyHistoryServices obj = new PartyHistoryServices(dctx, (Map<String, Object>)context);
        obj.validate();
        return obj.getResult();
    }
    
    private void validate() {
        locale = (Locale) context.get(E.locale.name());
        
        jobLogger.printLogInfo(UtilProperties.getMessage(RESOURCE, "CreatePartyHistory_Validate", locale));
        try {
            String partyId = (String)context.get(E.partyId.name());
            Timestamp fromDate = (Timestamp)context.get(E.fromDate.name());
            Timestamp thruDate = (Timestamp)context.get(E.thruDate.name());
            
            /*Primo controllo che il thruDate si amaggiore del FromDate e che entrambi non siano vuoti */
            // GN-5184 - aggiunto = nell'if sotto per consentire anche fromDate = thruDate
            if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate) && fromDate.compareTo(thruDate) <= 0) {
                
                /**
                 * Controllo se esiste gia un partyHistory compreso nelle nuove date
                 */
                List<EntityCondition> condList = FastList.newInstance();
                condList.add(EntityCondition.makeCondition(E.partyId.name(), partyId));                
                List<GenericValue> partyHistoryList = delegator.findList(E.PartyHistory.name(), EntityCondition.makeCondition(condList), null, UtilMisc.toList("-"+E.thruDate.name()), null, false);
                
                if (UtilValidate.isNotEmpty(partyHistoryList)){
                    
                    GenericValue gv = partyHistoryList.get(0);
                    if (UtilValidate.isEmpty(gv) || gv.getTimestamp(E.thruDate.name()).compareTo(DateUtilService.getPreviousDay(fromDate)) != 0) {
                        Map<String, Object> mappa = UtilMisc.toMap("fromDate", (Object) DateUtilService.getPreviousDay(fromDate), E.thruDate.name(), gv.getTimestamp(E.thruDate.name()));
                        String msg = UtilProperties.getMessage(RESOURCE_ERROR, "CreatePartyHistory_ErrorDate", mappa, locale);
                        jobLogger.printLogError(msg);
                        setResult(ServiceUtil.returnError(msg));
                    }
                    
                } 
                
            } else {
                Map<String, Object> mappa = UtilMisc.toMap("fromDate", (Object) DateUtilService.getPreviousDay(fromDate), E.thruDate.name(), thruDate);
                String msg = UtilProperties.getMessage(RESOURCE_ERROR, "CreatePartyHistory_FromDateGraterThruDate", mappa, locale);
                jobLogger.printLogError(msg);
                setResult(ServiceUtil.returnError(msg));
            }
           
            
        } catch (GenericEntityException e) {
            String msg = UtilProperties.getMessage(RESOURCE_ERROR, "CreatePartyHistory_ErrorValidatePartyHistory", locale);;
            jobLogger.printLogError(e, msg);
            setResult(ServiceUtil.returnError(msg + e.getMessage())); 
        }    
        
    }
    
}
