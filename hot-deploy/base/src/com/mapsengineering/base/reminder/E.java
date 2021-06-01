package com.mapsengineering.base.reminder;

/**
 * Enumeration for query
 */
public enum E {
    Y, responseMessage, managementPrintBirtSendEmail, reportId,
    //
    JobLog,
    //
    userLogin, asyncJobn, enabledSendMail, list, userProfile, outputFormat, contentType, locale, partyId, evalPartyId, contactMechId, queryReminder, localDispatcherName, batchReminder, retrieveWorkEffortReminder, 
    //
    workEffortId, workEffortTypeId, monitoringDate, reportContentId, contactMechIdTo, partyIdTo, subject, subjectLang, content, contentLang, partyIdFrom, statusId, contentMimeTypeId, communicationEventTypeId, communicationEventId,
    contentId, sequenceNum, contactMechIdFrom, parentTypeId, infoString, userLoginId, etch, etchLang, note, noteLang, fromDate, reminderActive, fromString, dataSoll, dateYearStart, dateYearEnd, currentStatusId,
    //
    
    CONTACT_MECH_ID, PARTY_ID, EVAL_PARTY_ID, SUBJECT,  SUBJECT_LANG, CONTENT, CONTENT_LANG, WORK_EFFORT_TYPE_ID, MONITORING_DATE, COM_IN_PROGRESS, AUTO_EMAIL_COMM, CONTACT_MECH_ID_TO, PARTY_ID_TO, WORK_EFFORT_ID, 
    //
    createBirtReport, createPartyContactMech,
    //
    WorkEffortType, PartyContactMechDetail, ModelReport, PartyContactMech, WorkEffortTypeContentReminder, WorkEffort, crudServiceDefaultOrchestration_WorkEffortRootStatus,
    //
    filterInnerJoin, filterLeftJoin, filterWhere, statusDatetime, WorkEffortStatus,
    //
    BaseUiLabels,
    //
    isPrimary, primaryLanguage, secondaryLanguage
}
