package com.mapsengineering.base.standardimport;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * Restituisce lo stato di avanzamento di un import standard asincrono, interrogando JobLog per sessionId.
 * Usato dal polling lato client (StandardImportUploadFileListener.js.ftl / WorkEffortStandardImportUploadFileListener.js.ftl)
 * per aggiornare il loader senza attendere il completamento dell'iframe di upload.
 */
public class GetStandardImportStatus {

    public static Map<String, Object> getStatus(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        String sessionId = (String) context.get("sessionId");
        String uploadJobLogId = (String) context.get("jobLogId");
        Map<String, Object> result = ServiceUtil.returnSuccess();

        if (UtilValidate.isEmpty(sessionId)) {
            result.put("completed", "N");
            result.put("jobCount", Long.valueOf(0));
            return result;
        }

        try {
            List<GenericValue> jobLogs = delegator.findList("JobLog",
                    EntityCondition.makeCondition("sessionId", sessionId), null, null, null, false);

            // NB: usare il MAX (non la SOMMA) tra i JobLog della sessione: le entita' interne
            // in cascata (es. WeSchedaInterface -> WeRootInterface) scrivono ciascuna un proprio
            // JobLog per le STESSE righe fisiche gia' contate, sommandole si otterrebbe un conteggio doppio.
            long recordElaborated = 0L;
            long blockingErrors = 0L;
            long warningMessages = 0L;
            boolean allClosed = UtilValidate.isNotEmpty(jobLogs);

            if (UtilValidate.isNotEmpty(jobLogs)) {
                for (GenericValue jobLog : jobLogs) {
                    long jobRecordElaborated = jobLog.get("recordElaborated") != null ? jobLog.getLong("recordElaborated") : 0L;
                    long jobBlockingErrors = jobLog.get("blockingErrors") != null ? jobLog.getLong("blockingErrors") : 0L;
                    long jobWarningMessages = jobLog.get("warningMessages") != null ? jobLog.getLong("warningMessages") : 0L;
                    recordElaborated = Math.max(recordElaborated, jobRecordElaborated);
                    blockingErrors = Math.max(blockingErrors, jobBlockingErrors);
                    warningMessages = Math.max(warningMessages, jobWarningMessages);
                    if (jobLog.get("logEndDate") == null) {
                        allClosed = false;
                    }
                }
            }

            // Il JobLog della fase di upload (jobLogId passato dal client) e' scritto PRIMA che il
            // sessionId venga generato in ImportManagerUploadFileHelper.runStandardImport(), quindi
            // ha sessionId vuoto e NON compare tra i jobLogs filtrati sopra: va cercato per jobLogId
            // diretto. E' l'unico conteggio affidabile, non soggetto all'accumulo del job asincrono.
            if (UtilValidate.isNotEmpty(uploadJobLogId)) {
                GenericValue uploadJobLog = delegator.findOne("JobLog", org.ofbiz.base.util.UtilMisc.toMap("jobLogId", uploadJobLogId), false);
                if (uploadJobLog != null) {
                    recordElaborated = uploadJobLog.get("recordElaborated") != null ? uploadJobLog.getLong("recordElaborated") : 0L;
                    blockingErrors = uploadJobLog.get("blockingErrors") != null ? uploadJobLog.getLong("blockingErrors") : 0L;
                    warningMessages = uploadJobLog.get("warningMessages") != null ? uploadJobLog.getLong("warningMessages") : 0L;
                }
            }

            result.put("completed", allClosed ? "Y" : "N");
            result.put("jobCount", Long.valueOf(UtilValidate.isNotEmpty(jobLogs) ? jobLogs.size() : 0));
            result.put("recordElaborated", Long.valueOf(recordElaborated));
            result.put("blockingErrors", Long.valueOf(blockingErrors));
            result.put("warningMessages", Long.valueOf(warningMessages));
        } catch (Exception e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        return result;
    }
}
