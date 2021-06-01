package com.mapsengineering.base.services.communication;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.mapsengineering.base.delete.party.E;
import com.mapsengineering.base.find.PartyEmailFindServices;
import com.mapsengineering.base.services.GenericServiceLoop;
import com.mapsengineering.base.services.communication.enumeration.CommunicationEventFieldEnum;
import com.mapsengineering.base.util.JobLogLog;

/**
 * Create communication Event for send mail
 *
 */
public class CommunicationEventCreate extends GenericServiceLoop {

    public static final String MODULE = CommunicationEventCreate.class.getName();
    public static final String SERVICE_NAME = "CommunicationEventService";
    public static final String SERVICE_TYPE = "COMM_EVENT";
    public static final String DEFAULT_CONTENT_MIME_TYPE_ID = "text/plain";
    
    private String resourceLabel;
    private String toString;
    private String partyIdEmailAddress;

    
    /**
     * Create CommunicationEvent
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> communicationEventCreate(DispatchContext dctx, Map<String, Object> context) {
        CommunicationEventCreate obj = new CommunicationEventCreate(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }



    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public CommunicationEventCreate(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, SERVICE_NAME, SERVICE_TYPE, MODULE);
        partyIdEmailAddress = (String)context.get(CommunicationEventFieldEnum.partyIdEmailAddress.name());
        toString = (String)context.get(CommunicationEventFieldEnum.toString.name());
        if (UtilValidate.isNotEmpty(context.get(CommunicationEventFieldEnum.resourceLabel.name()))) {
            resourceLabel = (String)context.get(CommunicationEventFieldEnum.resourceLabel.name());
        } else {
            resourceLabel = "BaseUiLabels";
        }
    }

    

    /**
     * Create CommunicationEvent
     * @throws Exception
     */
    protected void execute() throws Exception {
        String contactMechIdFrom = getContactMechIdFrom();
        
        Map<String, Object> commEventParams = getDctx().makeValidContext("createCommunicationEvent", ModelService.IN_PARAM, context);
        commEventParams.put(CommunicationEventFieldEnum.subject.name(), (String)context.get(CommunicationEventFieldEnum.subject.name()));
        commEventParams.put(CommunicationEventFieldEnum.content.name(), (String)context.get(CommunicationEventFieldEnum.content.name()));
        commEventParams.put(CommunicationEventFieldEnum.toString.name(), toString);
        commEventParams.put(CommunicationEventFieldEnum.partyIdFrom.name(), partyIdEmailAddress);
        commEventParams.put(CommunicationEventFieldEnum.contactMechIdFrom.name(), contactMechIdFrom);

        commEventParams.put(CommunicationEventFieldEnum.statusId.name(), CommunicationEventFieldEnum.COM_IN_PROGRESS.name());
        commEventParams.put(CommunicationEventFieldEnum.contentMimeTypeId.name(), DEFAULT_CONTENT_MIME_TYPE_ID);
        commEventParams.put(CommunicationEventFieldEnum.communicationEventTypeId.name(), CommunicationEventFieldEnum.AUTO_EMAIL_COMM.name());

        Map<String, Object> logParamCommunication = UtilMisc.toMap(CommunicationEventFieldEnum.toString.name(), (Object) toString, 
                CommunicationEventFieldEnum.partyIdFrom.name(), partyIdEmailAddress,
 CommunicationEventFieldEnum.contactMechIdFrom.name(), contactMechIdFrom);
        JobLogLog communication = new JobLogLog().initLogCode(resourceLabel, "CREATE_COMMEV_PARAM", logParamCommunication, getLocale());
        addLogInfo(communication.getLogCode(), communication.getLogMessage(), null, null, null);
        
        Map<String, ? extends Object> commEventResult = runSync(CommunicationEventFieldEnum.createCommunicationEvent.name(), commEventParams, "success", "error", false, false, null);
        Debug.log(" createCommunicationEventAndSendMail commEventResult " + commEventResult);
    }
    
    /**
     * Dato il partyId prendo il concatMech, se il partyId o il contactMechId e vuoto prendo quello di default BaseConfig.Reminder.defaultPartyIdEmailAddress
     * @param partyId
     * @return
     * @throws GeneralException 
     */
    private String getContactMechIdFrom() throws GeneralException {
        String conctachMechId = null;

        PartyEmailFindServices partyEmailFindServices = new PartyEmailFindServices(delegator);
        List<GenericValue> emailAddressesList = partyEmailFindServices.getEmailAddress(partyIdEmailAddress);
        GenericValue emailAddresses = EntityUtil.getFirst(emailAddressesList);
        if (UtilValidate.isNotEmpty(emailAddresses)) {
            conctachMechId = emailAddresses.getString(E.contactMechId.name());
        }
        
        return conctachMechId;
    }

}
