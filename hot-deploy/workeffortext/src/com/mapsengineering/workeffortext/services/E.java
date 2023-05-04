package com.mapsengineering.workeffortext.services;

/**
 * Field for workeffort services
 *
 */
public enum E {
    WorkEffortType, WorkEffortMeasure, WorkEffort, WorkEffortMeasRatSc, WorkEffortNote,
    WorkEffortAssoc, WorkEffortAssocAndTypeView, WorkEffortAssocExtView, WorkEffortAttribute, WorkEffortNoteAndData,
    NoteData, WorkEffortView, PartyRelationship, WorkEffortAssocToView, Party, PartyEvalInCharge, MyPerformance, 
    EmplPerfRootView, StatusItem, WorkEffortTypeAttrAndNoteData, WorkEffortStatusView,
    WorkEffortTypeType, WorkEffortMeasureAndGlAccountView, WorkEffortTypePeriodAndCustomTimePeriodView,
    WorkEffortTransactionIndicatorView, workEffortId, workEffortIdFrom, workEffortIdTo, workEffortAssocTypeId, fromDate, thruDate, storeRevisionWorkEffortAssoc, workEffortRevisionIdAssoc,
    frameEnumId, seqDigit, WETATOWorkEffortIdFrom, codePrefix, PartyParentRole, parentRoleCode, WorkEffortSequence, seqId, seqOnlyId, seqName,
    weMeasureTypeEnumId, workEffortTypeId, estimatedStartDate, estimatedCompletionDate, workEffortTypeIdTo, Enumeration, enumId, enumCode,
    isPosted, workEffortMeasureId, parentTypeId, isDefault, noteId, noteDateTime, workEffortTypeIdFrom,
    partyIdTo, partyId, id, estimatedStartDateTo, estimatedStartDateFrom, workEffortParentId, workEffortIdRoot,
    weHierarchyTypeId, fromCard, sourceReferenceId, etch, lastCorrectScoreDate, workEffortTypePeriodId,
    currentStatusId, defaultStatusPrefix, sequenceNum, roleTypeIdTo, partyRelationshipTypeId, roleTypeId, estimatedCompletionDateTo,
    hierarchyAssocTypeId, attrName, workEffortName, noteName, noteNameLang, estimatedCompletionDateFrom, wrToCode, workEffortRootId, partyIdList,
    emplPositionTypeId, orgUnitId, orgUnitRoleTypeId, emplFromDate, emplThruDate, valFromDate, valThruDate, templateId, templateTypeId,
    weContextId, evalPartyId, showCode, uoRoleTypeId, evalManagerPartyId, evalManagerRoleTypeId, description, wepaPartyId, snapShotDescription, snapShotDate, workEffortSnapshotId, workEffortRevisionId, weTransWorkEffortSnapShotId, estimatedTotalEffort,
    N, Y, S, EMPLOYEE, ORG_EMPLOYMENT, ORG_RESPONSIBLE, WEF_EVALUATED_BY, ROOT, TEMPL, COPY, SNAPSHOT,
    CREATE, UPDATE, DELETE, _AUTOMATIC_PK_, userLogin, partyName, statusEnumId,
    ASS, WEMT_SCORE, CTX_BS, CTX_OR, CTX_EP, CTX_CO,
    WEPERFST, WEORGST, WEEVALST, WECORRST,
    noteInfo, noteParty, isPublic, weTransWeId, WorkEffortTransactionIndicatorViewWithScorekpi, weTransId, weTransEntryId, origTransValue, origAmount, transProductId, weTransProductId, weTransMeasureId, voucherRef, acctgTransTypeId, SCOREKPI,
    WorkEffortTransactionView, HIE, AcctgTrans, acctgTransId, AcctgTransEntry, acctgTransEntrySeqId, jobLogger, availability,
    crudServiceDefaultOrchestration_WorkEffortPartyAssignment, workEffortRootPhysicalDelete, workEffortRootCopyService,
    WorkEffortPartyAssignment, roleTypeWeight, WE_ASSIGNMENT, WorkEffortStatus, statusId, WorkEffortRevision, workEffortTypeIdFil,
    refDate, workEffortTypeIdRoot, isClosed, hasAcctgTrans, toDate,  inputEnumId, weTransAccountId, weTransDate, isStoricized, weIsRoot, glAccountId, weAcctgTransAccountId, weTransValue, entryOrigAmount, entryAmount, weTransTypeValueId, 
    ACTUAL, Company, customTimePeriodId, weTransCurrencyUomId, crudServiceDefaultOrchestration_AcctgTransAndEntries, CustomTimePeriod, SCORE, entryGlAccountTypeId, entryGlAccountId, transactionDate, AcctgTransAndEntriesView, entryAcctgTransEntrySeqId, assocWeight, weWithoutPerf, weScoreConvEnumId, isTemplate, WEM_EVAL_IN_CHARGE,
    WorkEffortAndWorkEffortPartyAssView, roleRoleTypeId, rolePartyId, WorkEffortPartyAssignRole, partyIdFrom, Person, emplPositionTypeDate, name, value, weMeasureMeasureType, wePurposeTypeIdInd, WorkEffortMeasureByWorkEffortPurposeAccount, 
    isRoot, isAutomatic, WorkEffortRevisionAndAssoc, workEffortSnapshotIdFrom, workEffortSnapshotIdTo, weightSons, weightAssocWorkEffort, workEffortRevisionIdFrom, workEffortRevisionIdTo, workEffortRootIdList, organizationId, workEffortRevisionIdFrom2, workEffortRevisionIdTo2, 
    crudServiceDefaultOrchestration_WorkEffortRootStatus, reason, statusDatetime, 
    WorkEffortAndContentDataResourceExtended, workEffortContentTypeId, roleTypeNotRequired, contentName, objectInfo, _uploadedFile_fileName, _uploadedFile_contentType,
    uploadedFile, dataResourceId, mimeTypeId, dataResourceName, contentTypeId, entityListToImport, 
    workEffortRootList, WEM_EVAL_MANAGER, contactMechId, reportContentId, outputFormat, monitoringDate, contentType, enabledSendMail, 
    UserLogin, enabled, isNote, localDispatcherName, periodNum,
    crudServiceDefaultOrchestration_WorkEffortCommunicationEventView, WorkEffortCommunicationEventView, COM_IN_PROGRESS, contentMimeTypeId, content, 
    communicationEventTypeId, AUTO_EMAIL_COMM, subject, contactMechIdTo, communicationEventId, contentId, userProfile, WorkEffortTypeContent, locale, contentPartyId, contactMechIdFrom, exposePaginator, typeNotes, ordinamento, createBirtReport, openAllRoots,
    WEM_EVAL_APPROVER, WEF_APPROVED_BY, valPartyId, appPartyId, oldEstimatedStartDate, oldEstimatedCompletionDate, statusTypeId, sequenceId, glFiscalTypeEnumId, isHtml, isMain, internalNote, actStEnumId,
    targetPeriodEnumId, weWithoutTarget, periodicalAbsoluteEnumId, WorkEffortAssocFromView, wrFromSnapShotId, wrToSnapShotId, delegator, wrToOrgUnitId, wrToFromDate, WorkEffortTypeAssocAndAssocType, wefromWetoEnumId, isUnique, workEffortTypeIdRef, 
    WorkEffortTypeStatus, workEffortTypeRootId, duplicateAdmit, WorkEffortTypeAssoc, CLONE, workEffortSnapshotService, params,
    skipAutomaticServiceNote, skipAutomaticServiceMeasure, skipAutomaticServiceAssoc, enableSnapshot, parentHierarchyAssocTypeId, 
    defaultOrganizationPartyId,
    wrToParentId, weParentId, childTemplateId, errorMsg, fiscalCode, universalId, CommEventContentAssoc, ORGANIZATION_UNIT, WorkEffortTypePeriod, firstName, lastName, PartyRoleView, personCode, DataResourceContentView, coContentId, infoString, dataResourceTypeId, PartyGroup, statusIdTo, StatusValidChange,
    WETATOWorkEffortIdFrom_2, WETATOWorkEffortIdFrom_3, WETATOWorkEffortIdFrom_4, workEffortRootIdNewList, copyWorkEffortAssocCopy, periodFromDate, periodThruDate2, estimatedCompletionDate2, infoUrl;
}
