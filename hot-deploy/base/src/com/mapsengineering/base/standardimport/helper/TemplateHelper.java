package com.mapsengineering.base.standardimport.helper;

import java.sql.Timestamp;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.util.JobLogger;

/**
 * Helper for manage of PartyHistory and Template
 * @author dain
 *
 */
public class TemplateHelper {

    JobLogger jobLogger;
    Delegator delegator;

    /**
     * Constructor
     * @param jobLogger
     * @param delegator
     */
    public TemplateHelper(JobLogger jobLogger, Delegator delegator) {
        this.jobLogger = jobLogger;
        this.delegator = delegator;
    }

    /**
     * return first PartyHistory, ordered by fromDate DESC
     * @param partyId
     * @param refDate
     * @return
     * @throws GeneralException
     */
    public GenericValue getPartyHistory(String partyId, Timestamp refDate) throws GeneralException {
        List<GenericValue> partyHistoryList = getPartyHistoryList(partyId, refDate);
        return EntityUtil.getFirst(partyHistoryList);
    }

    /**
     * Return thruDate of PartyHistory if the templateId is change, null otherwise
     * @param partyId
     * @param refDate
     * @return
     * @throws GeneralException
     */
    public Timestamp getThruDate(String partyId, Timestamp refDate, String templateId) throws GeneralException {
        GenericValue partyHistory = getPartyHistory(partyId, refDate);
        if (UtilValidate.isNotEmpty(partyHistory)) {
            if (refDate.equals(partyHistory.getTimestamp(TemplateEnum.thruDate.name()))) {
                jobLogger.printLogInfo(" - templateId " + templateId + " - > " + partyHistory.getString(TemplateEnum.templateId.name())); 
                if (!templateId.equals(partyHistory.getString(TemplateEnum.templateId.name()))) {
                    return partyHistory.getTimestamp(TemplateEnum.thruDate.name());
                }
            }
        }
        return null;
    }

    /**
     * Return list of PartyHistory and Template
     * @param partyId
     * @param refDate
     * @return
     * @throws GeneralException
     */
    private List<GenericValue> getPartyHistoryList(String partyId, Timestamp refDate) throws GeneralException {
        List<EntityCondition> condition = FastList.newInstance();
        condition.add(EntityCondition.makeCondition(TemplateEnum.partyId.name(), partyId));
        
        List<String> orderBy = FastList.newInstance();
        orderBy.add(TemplateEnum.fromDate.name() + " DESC");

        return delegator.findList(TemplateEnum.PartyHistoryView.name(), EntityCondition.makeCondition(condition), null, orderBy, null, false);
    }
}
