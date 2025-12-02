package com.mapsengineering.base.standardimport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;

/**
 * TakeOver Service specifico per Import Massivo Schede di Valutazione Template-Based
 * 
 * Architettura:
 * - Estende WeRootInterfaceTakeOverService per riutilizzare logica base
 * - Aggiunge logica template-based per schede di valutazione
 * - Genera automaticamente work_effort_measure e acctg_trans da template
 * 
 * Flusso Import:
 * 1. Crea/Aggiorna work_effort base (via super.doImport())
 * 2. Lookup template da templateCode
 * 3. Crea associazione TEMPL tra scheda e template
 * 4. Recupera 6 indicatori dal template
 * 5. Crea 6 work_effort_measure con sequence_id=1, kpi_score_weight=1.0
 * 6. Crea 6 acctg_trans con voucher_ref = work_effort_measure_id
 * 7. Assegna ruoli VALUTATO/VALUTATORE (via super.doImport())
 * 
 * @author GZOOM Team
 * @version 2.0 (Template-Based)
 * @date 11 Novembre 2025
 */
public class WeSchedaTakeOverService extends WeRootInterfaceTakeOverService {

    public static final String MODULE = WeSchedaTakeOverService.class.getName();

    // Campi template
    private String templateCode;
    private String templateWorkEffortId;
    private String currentStatusIdFromExcel; // Override per status dall'Excel

    /**
     * Override initLocalValue per leggere templateCode e currentStatusId dal file Excel
     */
    @Override
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
        // Chiama logica base (lookup work_effort esistente)
        super.initLocalValue(extLogicKey);

        // Leggi templateCode e currentStatusId dal record Excel
        GenericValue externalValue = getExternalValue();
        if (UtilValidate.isNotEmpty(externalValue)) {
            templateCode = externalValue.getString("templateCode");
            currentStatusIdFromExcel = externalValue.getString("currentStatusId");
            addLogInfo("Template code from Excel: " + templateCode);
            addLogInfo("Current status ID from Excel: " + currentStatusIdFromExcel);
        }

        // Se orgUnitRoleTypeId è vuoto, imposta default ORGANIZATION_UNIT
        if (UtilValidate.isEmpty(getOrgUnitRoleTypeId())) {
            setOrgUnitRoleTypeId("ORGANIZATION_UNIT");
            addLogInfo("orgUnitRoleTypeId was empty, set default: ORGANIZATION_UNIT");
        }
    }

    /**
     * Override doImport per aggiungere logica template-based
     * 
     * Ordine operazioni:
     * 1. super.doImport() - Crea work_effort base, associazione ROOT, ruoli
     * 2. handleTemplateBasedCard() - Logica template (TEMPL, measure, acctg_trans)
     * 3. copyFieldsFromTemplate() - Eredita 11 campi dal template
     * 4. setMissingDatesFromEstimated() - Imposta date NULL con valori estimated
     */
    @Override
    public void doImport() throws GeneralException {
        addLogInfo("===== START IMPORT SCHEDA VALUTAZIONE TEMPLATE-BASED =====");
        addLogInfo("Template Code: " + templateCode);

        // 1. Esegui import base (work_effort, ROOT assoc, ruoli)
        // NOTA: questo chiama anche doImportWEMeasure() che legge da WE_MEASURE_INTERFACE
        // Ma per schede template-based, NON useremo WE_MEASURE_INTERFACE
        super.doImport();

        // 2. Aggiungi logica template-based DOPO creazione work_effort
        if (UtilValidate.isNotEmpty(templateCode)) {
            handleTemplateBasedCard();
        } else {
            addLogInfo("WARNING: templateCode vuoto, skip logica template-based");
        }

        addLogInfo("===== END IMPORT SCHEDA VALUTAZIONE TEMPLATE-BASED =====");
    }

    /**
     * Gestisce logica template-based per scheda di valutazione
     * 
     * Step:
     * 1. Lookup template work_effort_id
     * 2. Crea associazione TEMPL
     * 3. Recupera 6 indicatori template
     * 4. Crea 6 work_effort_measure copiando TUTTI i campi dal template
     * 5. Eredita 11 campi dal template work_effort
     * 6. Imposta date NULL con valori estimated
     * 7. Configura stati: current_status_id=WEEVALST_EXECPEND + storico (PLANINIT→EXECPEND)
     * 8. NO acctg_trans (vengono creati quando si inseriscono valori)
     */
    private void handleTemplateBasedCard() throws GeneralException {
        addLogInfo("--- Inizio logica template-based ---");

        // Step 1: Lookup template
        lookupTemplate();

        // Step 2: Crea associazione TEMPL
        createTemplateAssociation();

        // Step 3-4: Crea work_effort_measure copiando dal template
        createWorkEffortMeasuresFromTemplate();

        // Step 5: Eredita 11 campi dal template work_effort
        copyFieldsFromTemplate();

        // Step 6: Imposta date NULL con valori estimated
        setMissingDatesFromEstimated();

        // Step 7: Override current_status_id con valore dall'Excel
        setCurrentStatusIdFromExcel();

        addLogInfo("--- Fine logica template-based ---");
    }

    /**
     * Step 1: Lookup template work_effort_id da templateCode
     * 
     * Query: SELECT work_effort_id FROM work_effort 
     *        WHERE source_reference_id = :templateCode
     * 
     * Validazioni:
     * - templateCode obbligatorio
     * - Template deve esistere
     * - Template deve essere unico
     * 
     * @throws ImportException se template non trovato o duplicato
     */
    private void lookupTemplate() throws GeneralException {
        addLogInfo("Step 1: Lookup template with code = " + templateCode);

        // Validazione templateCode obbligatorio
        if (UtilValidate.isEmpty(templateCode)) {
            String msg = "Il campo templateCode è obbligatorio per import schede di valutazione";
            throw new ImportException(getEntityName(),
                    getExternalValue().getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        // Query template
        EntityCondition condition = EntityCondition.makeCondition(
                UtilMisc.toList(
                        EntityCondition.makeCondition("sourceReferenceId", templateCode)
                ),
                EntityOperator.AND);

        List<GenericValue> templates = getManager().getDelegator().findList(
                "WorkEffort", condition, null, null, null, false);

        // Validazione: template deve esistere
        if (UtilValidate.isEmpty(templates)) {
            String msg = "Template non trovato con sourceReferenceId = " + templateCode
                    + ". Verificare che il template esista nella tabella work_effort.";
            throw new ImportException(getEntityName(),
                    getExternalValue().getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        // Validazione: template deve essere unico
        if (templates.size() > 1) {
            String msg = "Trovati " + templates.size() + " template con sourceReferenceId = " + templateCode
                    + ". Il templateCode deve essere univoco.";
            throw new ImportException(getEntityName(),
                    getExternalValue().getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        // Template trovato
        templateWorkEffortId = templates.get(0).getString("workEffortId");
        addLogInfo("Template trovato: " + templateCode + " [work_effort_id=" + templateWorkEffortId + "]");
    }

    /**
     * Step 2: Crea work_effort_assoc TEMPL tra scheda e template
     * 
     * Struttura:
     * - work_effort_id_from: scheda (work_effort appena creato)
     * - work_effort_id_to: template (lookup da templateCode)
     * - work_effort_assoc_type_id: 'TEMPL'
     * - from_date: estimatedStartDate della scheda
     * 
     * Gestisce idempotenza: se associazione esiste già, skip
     */
    private void createTemplateAssociation() throws GeneralException {
        addLogInfo("Step 2: Creating TEMPL association between card " + getWorkEffortRootId()
                + " and template " + templateWorkEffortId);

        // Verifica se esiste già (idempotenza)
        EntityCondition condition = EntityCondition.makeCondition(
                UtilMisc.toList(
                        EntityCondition.makeCondition("workEffortIdFrom", getWorkEffortRootId()),
                        EntityCondition.makeCondition("workEffortIdTo", templateWorkEffortId),
                        EntityCondition.makeCondition("workEffortAssocTypeId", "TEMPL")),
                EntityOperator.AND);

        List<GenericValue> existing = getManager().getDelegator().findList(
                "WorkEffortAssoc", condition, null, null, null, false);

        if (UtilValidate.isNotEmpty(existing)) {
            addLogInfo("TEMPL association already exists, skip creation");
            return;
        }

        // Crea nuova associazione TEMPL
        GenericValue assoc = getManager().getDelegator().makeValue("WorkEffortAssoc");
        assoc.set("workEffortIdFrom", getWorkEffortRootId());
        assoc.set("workEffortIdTo", templateWorkEffortId);
        assoc.set("workEffortAssocTypeId", "TEMPL");
        assoc.set("fromDate", getEstimatedStartDate());
        assoc.create();

        addLogInfo("TEMPL association created successfully: " + getWorkEffortRootId() + " -> " + templateWorkEffortId);
    }

    /**
     * Step 3-4: Crea automaticamente 6 work_effort_measure COPIANDO TUTTI I CAMPI dal template
     * 
     * Logica:
     * 1. Recupera 6 work_effort_measure dal template (work_effort_id = templateWorkEffortId)
     * 2. Per ogni indicatore, crea work_effort_measure per la nuova scheda COPIANDO TUTTI I CAMPI
     * 
     * Campi copiati identici dal template:
     * - work_effort_measure_type_id
     * - work_effort_score_type_id  
     * - work_effort_convergence_type_id
     * - work_effort_alert_type_id
     * - work_effort_measure_org_management_id
     * - gl_account_id
     * - from_date
     * - thru_date
     * - sequence_id
     * - work_effort_type_period_id
     * - work_effort_with_perf_type_id
     * - acctg_trans_det_type_id
     * - is_posted
     * - is_enabled
     * - sequence_order
     * - kpi_score_weight
     * - created_by_user_login
     * 
     * Campi generati/modificati:
     * - work_effort_measure_id: auto-generato (sequence)
     * - work_effort_id: scheda appena creata (getWorkEffortRootId())
     * 
     * Validazione: template deve avere esattamente 6 indicatori
     */
    private void createWorkEffortMeasuresFromTemplate() throws GeneralException {
        addLogInfo("Step 3-4: Creating work_effort_measure COPYING ALL FIELDS from template " + templateWorkEffortId);

        // 1. Recupera i 6 work_effort_measure del TEMPLATE direttamente
        EntityCondition templateMeasureCondition = EntityCondition.makeCondition(
            "workEffortId", EntityOperator.EQUALS, templateWorkEffortId
        );
        List<GenericValue> templateMeasures = getManager().getDelegator().findList(
            "WorkEffortMeasure", templateMeasureCondition, null, 
            UtilMisc.toList("sequenceId"), null, false
        );

        // Validazione: esattamente 6 indicatori
        if (templateMeasures.size() != 6) {
            String msg = "Template " + templateCode + " (" + templateWorkEffortId + 
                    ") deve avere esattamente 6 indicatori nel work_effort_measure. " +
                    "Trovati: " + templateMeasures.size() + ". " +
                    "Verificare configurazione template nel database.";
            throw new ImportException(getEntityName(),
                    getExternalValue().getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        addLogInfo("Found 6 template indicators to copy");

        // 2. Per ogni work_effort_measure del template, crea copia identica per nuova scheda
        for (GenericValue templateMeasure : templateMeasures) {
            // Crea nuovo work_effort_measure copiando TUTTI i campi dal template
            GenericValue newMeasure = getManager().getDelegator().makeValue("WorkEffortMeasure");
            
            // Genera nuovo ID
            newMeasure.set("workEffortMeasureId",
                    getManager().getDelegator().getNextSeqId("WorkEffortMeasure"));
            
            // Sostituisci work_effort_id con nuova scheda
            newMeasure.set("workEffortId", getWorkEffortRootId());
            
            // Copia TUTTI gli altri campi dal template (USA I NOMI REALI DEI CAMPI)
            newMeasure.set("productId", templateMeasure.get("productId"));
            newMeasure.set("emplPositionTypeId", templateMeasure.get("emplPositionTypeId"));
            newMeasure.set("glFiscalTypeEnumId", templateMeasure.get("glFiscalTypeEnumId"));
            newMeasure.set("partyId", templateMeasure.get("partyId"));
            newMeasure.set("roleTypeId", templateMeasure.get("roleTypeId"));
            newMeasure.set("weMeasureTypeEnumId", templateMeasure.get("weMeasureTypeEnumId"));
            newMeasure.set("weScoreRangeEnumId", templateMeasure.get("weScoreRangeEnumId"));
            newMeasure.set("weScoreConvEnumId", templateMeasure.get("weScoreConvEnumId"));
            newMeasure.set("weAlertRuleEnumId", templateMeasure.get("weAlertRuleEnumId"));
            newMeasure.set("workEffortInfluenceId", templateMeasure.get("workEffortInfluenceId"));
            newMeasure.set("uomRangeId", templateMeasure.get("uomRangeId"));
            newMeasure.set("weOtherGoalEnumId", templateMeasure.get("weOtherGoalEnumId"));
            newMeasure.set("glAccountId", templateMeasure.get("glAccountId"));
            newMeasure.set("fromDate", templateMeasure.get("fromDate"));
            newMeasure.set("thruDate", templateMeasure.get("thruDate"));
            newMeasure.set("uomDescr", templateMeasure.get("uomDescr"));
            newMeasure.set("comments", templateMeasure.get("comments"));
            newMeasure.set("kpiScoreWeight", templateMeasure.get("kpiScoreWeight"));
            newMeasure.set("otherWorkEffortId", templateMeasure.get("otherWorkEffortId"));
            newMeasure.set("periodTypeId", templateMeasure.get("periodTypeId"));
            newMeasure.set("weWithoutPerf", templateMeasure.get("weWithoutPerf"));
            newMeasure.set("orgUnitRoleTypeId", templateMeasure.get("orgUnitRoleTypeId"));
            newMeasure.set("orgUnitId", templateMeasure.get("orgUnitId"));
            newMeasure.set("detailEnumId", templateMeasure.get("detailEnumId"));
            newMeasure.set("dataSourceId", templateMeasure.get("dataSourceId"));
            newMeasure.set("currentStatusId", templateMeasure.get("currentStatusId"));
            newMeasure.set("isPosted", templateMeasure.get("isPosted"));
            newMeasure.set("source", templateMeasure.get("source"));
            newMeasure.set("sequenceId", templateMeasure.get("sequenceId"));
            newMeasure.set("uomDescrLang", templateMeasure.get("uomDescrLang"));
            newMeasure.set("kpiOtherWeight", templateMeasure.get("kpiOtherWeight"));
            newMeasure.set("isInvisible", templateMeasure.get("isInvisible"));
            newMeasure.set("commentsLang", templateMeasure.get("commentsLang"));
            
            // Timestamp campi (rigenera con data corrente)
            newMeasure.set("createdByUserLogin", templateMeasure.get("createdByUserLogin"));
            newMeasure.set("createdStamp", UtilDateTime.nowTimestamp());
            newMeasure.set("createdTxStamp", UtilDateTime.nowTimestamp());
            newMeasure.set("lastUpdatedStamp", UtilDateTime.nowTimestamp());
            newMeasure.set("lastUpdatedTxStamp", UtilDateTime.nowTimestamp());
            
            newMeasure.create();

            addLogInfo("Created work_effort_measure " + newMeasure.getString("workEffortMeasureId")
                    + " copying ALL fields from template measure " + templateMeasure.getString("workEffortMeasureId")
                    + " (glAccountId=" + templateMeasure.getString("glAccountId") + ")");
        }

        addLogInfo("Successfully created 6 work_effort_measure records with ALL fields copied from template");
    }

    /**
     * Step 5: Eredita 11 campi dal template work_effort alla scheda
     * 
     * Campi da copiare:
     * - note_id
     * - effort_uom_id
     * - empl_position_type
     * - org_unit_role_type_id
     * - work_effort_assoc_type_id
     * - is_posted
     * - etch
     * - work_effort_type_period_id
     * - uom_range_score_id
     * 
     * Metodo:
     * 1. Recupera template work_effort
     * 2. Aggiorna scheda con i campi del template
     */
    private void copyFieldsFromTemplate() throws GeneralException {
        addLogInfo("Step 5: Copying 11 fields from template " + templateWorkEffortId + " to card " + getWorkEffortRootId());

        // 1. Recupera template work_effort
        GenericValue templateWorkEffort = getManager().getDelegator().findOne("WorkEffort",
                UtilMisc.toMap("workEffortId", templateWorkEffortId), false);

        if (UtilValidate.isEmpty(templateWorkEffort)) {
            String msg = "Template work_effort not found with id = " + templateWorkEffortId;
            throw new GeneralException(msg);
        }

        // 2. Recupera scheda work_effort
        GenericValue cardWorkEffort = getManager().getDelegator().findOne("WorkEffort",
                UtilMisc.toMap("workEffortId", getWorkEffortRootId()), false);

        if (UtilValidate.isEmpty(cardWorkEffort)) {
            String msg = "Card work_effort not found with id = " + getWorkEffortRootId();
            throw new GeneralException(msg);
        }

        // 3. Copia 10 campi dal template alla scheda (escluso orgUnitRoleTypeId)
        cardWorkEffort.set("noteId", templateWorkEffort.get("noteId"));
        cardWorkEffort.set("effortUomId", templateWorkEffort.get("effortUomId"));
        cardWorkEffort.set("emplPositionTypeId", templateWorkEffort.get("emplPositionTypeId"));
        // NON copiamo orgUnitRoleTypeId dal template - impostiamo direttamente UOC
        cardWorkEffort.set("orgUnitRoleTypeId", "UOC");
        cardWorkEffort.set("workEffortAssocTypeId", templateWorkEffort.get("workEffortAssocTypeId"));
        cardWorkEffort.set("isPosted", templateWorkEffort.get("isPosted"));
        cardWorkEffort.set("etch", templateWorkEffort.get("etch"));
        cardWorkEffort.set("workEffortTypePeriodId", templateWorkEffort.get("workEffortTypePeriodId"));
        cardWorkEffort.set("uomRangeScoreId", templateWorkEffort.get("uomRangeScoreId"));

        cardWorkEffort.store();

        addLogInfo("Successfully copied 11 fields from template to card");
    }

    /**
     * Step 6: Imposta date NULL con valori da estimated_start_date e estimated_completion_date
     * 
     * Logica:
     * - actual_start_date = estimated_start_date
     * - actual_completion_date = estimated_completion_date
     * - scheduled_start_date = estimated_start_date
     * - scheduled_completion_date = estimated_completion_date
     */
    private void setMissingDatesFromEstimated() throws GeneralException {
        addLogInfo("Step 6: Setting missing dates from estimated dates");

        // Recupera scheda work_effort
        GenericValue cardWorkEffort = getManager().getDelegator().findOne("WorkEffort",
                UtilMisc.toMap("workEffortId", getWorkEffortRootId()), false);

        if (UtilValidate.isEmpty(cardWorkEffort)) {
            String msg = "Card work_effort not found with id = " + getWorkEffortRootId();
            throw new GeneralException(msg);
        }

        Timestamp estimatedStartDate = cardWorkEffort.getTimestamp("estimatedStartDate");
        Timestamp estimatedCompletionDate = cardWorkEffort.getTimestamp("estimatedCompletionDate");

        // Imposta actual_start_date solo se NULL
        if (UtilValidate.isEmpty(cardWorkEffort.get("actualStartDate")) 
                && UtilValidate.isNotEmpty(estimatedStartDate)) {
            cardWorkEffort.set("actualStartDate", estimatedStartDate);
            addLogInfo("Set actualStartDate = " + estimatedStartDate);
        }

        // Imposta actual_completion_date solo se NULL
        if (UtilValidate.isEmpty(cardWorkEffort.get("actualCompletionDate")) 
                && UtilValidate.isNotEmpty(estimatedCompletionDate)) {
            cardWorkEffort.set("actualCompletionDate", estimatedCompletionDate);
            addLogInfo("Set actualCompletionDate = " + estimatedCompletionDate);
        }

        // Imposta scheduled_start_date solo se NULL
        if (UtilValidate.isEmpty(cardWorkEffort.get("scheduledStartDate")) 
                && UtilValidate.isNotEmpty(estimatedStartDate)) {
            cardWorkEffort.set("scheduledStartDate", estimatedStartDate);
            addLogInfo("Set scheduledStartDate = " + estimatedStartDate);
        }

        // Imposta scheduled_completion_date solo se NULL
        if (UtilValidate.isEmpty(cardWorkEffort.get("scheduledCompletionDate")) 
                && UtilValidate.isNotEmpty(estimatedCompletionDate)) {
            cardWorkEffort.set("scheduledCompletionDate", estimatedCompletionDate);
            addLogInfo("Set scheduledCompletionDate = " + estimatedCompletionDate);
        }

        cardWorkEffort.store();

        addLogInfo("Successfully set missing dates from estimated dates");
    }

    /**
     * Step 7: Imposta gli stati della scheda durante l'import massivo
     * 
     * Per le schede di valutazione importate massivamente:
     * 1. Legge lo stato corrente dall'Excel (currentStatusIdFromExcel, es. WEEVALST_EXECPEND)
     * 2. Imposta current_status_id sul work_effort con il valore letto dall'Excel
     * 3. Rimuove il record WEGS_CREATED dallo storico work_effort_status
     * 4. Crea due nuovi stati nello storico:
     *    - WEEVALST_PLANINIT con reason "Scheda Inizializzata da Template"
     *    - [Stato letto dall'Excel] con reason "Scheda pronta per la Valutazione"
     */
    private void setCurrentStatusIdFromExcel() throws GeneralException {
        addLogInfo("Step 7: Configurazione stati scheda per import massivo");

        // Verifica che lo stato sia stato letto dall'Excel
        if (UtilValidate.isEmpty(currentStatusIdFromExcel)) {
            throw new GeneralException("currentStatusIdFromExcel is empty - cannot set work effort status");
        }

        // Recupera scheda work_effort
        GenericValue cardWorkEffort = getManager().getDelegator().findOne("WorkEffort",
                UtilMisc.toMap("workEffortId", getWorkEffortRootId()), false);

        if (UtilValidate.isEmpty(cardWorkEffort)) {
            String msg = "Card work_effort not found with id = " + getWorkEffortRootId();
            throw new GeneralException(msg);
        }

        String oldStatusId = cardWorkEffort.getString("currentStatusId");
        addLogInfo("Current status before update: " + oldStatusId);

        // 1. Imposta current_status_id con il valore letto dall'Excel
        cardWorkEffort.set("currentStatusId", currentStatusIdFromExcel);
        cardWorkEffort.store();
        addLogInfo("Set current_status_id = " + currentStatusIdFromExcel + " on work_effort (from Excel)");

        // 2. Rimuovi tutti i vecchi stati dallo storico (es. WEGS_CREATED)
        try {
            java.util.List<GenericValue> oldStatusRecords = getManager().getDelegator().findList(
                "WorkEffortStatus",
                EntityCondition.makeCondition("workEffortId", getWorkEffortRootId()),
                null,
                null,
                null,
                false
            );
            
            if (UtilValidate.isNotEmpty(oldStatusRecords)) {
                getManager().getDelegator().removeAll(oldStatusRecords);
                addLogInfo("Removed " + oldStatusRecords.size() + " old status record(s) from WorkEffortStatus");
            }

            // 3. Crea i due nuovi stati nello storico
            java.sql.Timestamp baseTimestamp = cardWorkEffort.getTimestamp("lastStatusUpdate");
            String userLogin = cardWorkEffort.getString("createdByUserLogin");

            // Primo stato: WEEVALST_PLANINIT (Scheda Inizializzata)
            // Impostiamo un timestamp leggermente precedente (1 secondo prima)
            java.sql.Timestamp planinItTimestamp = new java.sql.Timestamp(baseTimestamp.getTime() - 1000);
            
            GenericValue statusPlanInit = getManager().getDelegator().makeValue("WorkEffortStatus");
            statusPlanInit.set("workEffortId", getWorkEffortRootId());
            statusPlanInit.set("statusId", "WEEVALST_PLANINIT");
            statusPlanInit.set("statusDatetime", planinItTimestamp);
            statusPlanInit.set("setByUserLogin", userLogin);
            statusPlanInit.set("reason", "Scheda Inizializzata da Template");
            statusPlanInit.create();
            addLogInfo("Created WorkEffortStatus: WEEVALST_PLANINIT with reason 'Scheda Inizializzata da Template'");

            // Secondo stato: usa lo statusId letto dall'Excel (es. WEEVALST_EXECPEND)
            GenericValue statusExecPend = getManager().getDelegator().makeValue("WorkEffortStatus");
            statusExecPend.set("workEffortId", getWorkEffortRootId());
            statusExecPend.set("statusId", currentStatusIdFromExcel);
            statusExecPend.set("statusDatetime", baseTimestamp);
            statusExecPend.set("setByUserLogin", userLogin);
            statusExecPend.set("reason", "Scheda pronta per la Valutazione");
            statusExecPend.create();
            addLogInfo("Created WorkEffortStatus: " + currentStatusIdFromExcel + " with reason 'Scheda pronta per la Valutazione'");

            addLogInfo("Successfully configured status history for evaluation card");
            
        } catch (Exception e) {
            String errorMsg = "FATAL ERROR updating WorkEffortStatus history: " + e.getMessage();
            addLogInfo(errorMsg);
            throw new GeneralException(errorMsg, e);
        }
    }

    /**
     * Nota: doImportWEMeasure() è private nella classe base, non può essere sovrascritto
     * 
     * I work_effort_measure sono generati automaticamente dal template
     * in createWorkEffortMeasuresFromTemplate().
     * 
     * Non serve foglio Excel OBIETTIVI (WE_MEASURE_INTERFACE non configurato).
     * La classe base non troverà configurazione e skipperà l'import measure.
     */
}
