package com.mapsengineering.base.birt.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IEngineTask;
import org.eclipse.birt.report.engine.api.IPDFRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.birt.BirtFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.config.DatasourceInfo;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Element;

import com.mapsengineering.base.services.async.AsyncJob;
import com.mapsengineering.base.services.async.AsyncJobUtil;
import com.mapsengineering.base.util.OfbizServiceContext;
import com.mapsengineering.base.util.closeable.Closeables;

public class BirtService {

    private static final String MODULE = BirtService.class.getName();
    public static final String BIRT_PARAMETERS = "birtParameters";
    public static final String BIRT_LOCALE = "birtLocale";
    private static final String BIRT_IMAGE_DIRECTORY = "birtImageDirectory";
    public static final String BIRT_OUTPUT_FILE_NAME = "birtOutputFileName";
    private static final String OUTPUT_FORMAT = "outputFormat";
    private static final String CONTENT_TYPE = "contentType";

    private static final HTMLServerImageHandler IMAGE_HANDLER = new HTMLServerImageHandler();

    private final OfbizServiceContext ctx;
    private final AsyncJob job;
    private BirtServiceProgress progressMonitor;

    public static Map<String, Object> runSrv(DispatchContext dctx, Map<String, Object> context) throws IOException, GeneralException {
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
            BirtService obj = new BirtService(ctx);
            obj.run();
        } finally {
            ctx.close();
        }
        return ctx.getResult();
    }

    public BirtService(OfbizServiceContext ctx) {
        this.ctx = ctx;
        job = (AsyncJob)ctx.get("asyncJob");
        if (job != null) {
            progressMonitor = new BirtServiceProgress(this);
            job.setCallback(progressMonitor);
        }
    }

    public OfbizServiceContext getCtx() {
        return ctx;
    }

    public AsyncJob getJob() {
        return job;
    }

    private boolean run() throws GeneralException {
        try {
            return runReport();
        } finally {
            if (job != null) {
                job.setCallback(null);
            }
            progressMonitor = null;
        }
    }

    private boolean runReport() throws GeneralException {
        IReportEngine engine = BirtFactory.getReportEngine();

        String contentName = (String) ctx.get("name");
        if (UtilValidate.isEmpty(contentName)) {
        	Map<String, Object> birtParameters = (Map<String, Object>) ctx.get(BIRT_PARAMETERS);
        	if (birtParameters != null) {
        		contentName = (String) birtParameters.get("outputFileName");
        	}
        }
        String reportUrl = null;

        if (UtilValidate.isNotEmpty(ctx.get("reportContentId"))) {
            String reportContentId = UtilGenerics.cast(ctx.get("reportContentId"));

            GenericValue reportContent = ctx.getDelegator().findOne("Content", UtilMisc.toMap("contentId", reportContentId), false);
            if (UtilValidate.isNotEmpty(reportContent)) {
                if(UtilValidate.isEmpty(contentName)) {
                    contentName = reportContent.getString("description");
                    String workEffortTypeId = (String) ((Map<String, Object>)  ctx.get(BIRT_PARAMETERS)).get("workEffortTypeId");
                    String langLocale = (String) ((Map<String, Object>)  ctx.get(BIRT_PARAMETERS)).get("langLocale");
                    // vado aprendere la descrizione in lingua
                    if (UtilValidate.isNotEmpty(workEffortTypeId)) {
                    	GenericValue reportContentType = ctx.getDelegator().findOne("WorkEffortTypeContent", UtilMisc.toMap("contentId", reportContentId, "workEffortTypeId", workEffortTypeId), false);
                    	if (UtilValidate.isNotEmpty(reportContentType)) {                    		
                    		if (UtilValidate.isNotEmpty(langLocale)) {
                    			contentName = UtilValidate.isNotEmpty(reportContentType.getString("etchLang")) ? reportContentType.getString("etchLang") : contentName;
                    		} else {
                    			contentName = UtilValidate.isNotEmpty(reportContentType.getString("etch")) ? reportContentType.getString("etch") : contentName;
                    		}
                    		
                    	}
                    }
                }
                GenericValue dataResource = reportContent.getRelatedOne("DataResource");
                if (UtilValidate.isNotEmpty(dataResource)) {
                    String objectInfo = dataResource.getString("objectInfo");
                    if (UtilValidate.isNotEmpty(objectInfo)) {
                        if (objectInfo.startsWith("component://")) {
                            reportUrl = objectInfo;
                        }
                    }
                }
            }
        }

        if (job != null) {
            job.setJobName(contentName);
        }
        
        if (UtilValidate.isNotEmpty(reportUrl)) {
            Map<String, Object> appContext = UtilGenerics.cast(engine.getConfig().getAppContext());
            setWebContextObjects(appContext);

            Map<String, Object> reportContext = FastMap.newInstance();
            // set parameters from request
            Map<String, Object> birtParameters = UtilGenerics.cast(ctx.get(BIRT_PARAMETERS));
            if (birtParameters == null) {
                birtParameters = ctx;
            }

            GenericValue userLogin = ctx.getUserLogin();
            if (UtilValidate.isNotEmpty(userLogin)) {
                birtParameters.put("userLoginId", userLogin.get("userLoginId"));
            }
            reportContext.put("userLogin", userLogin);

            birtParameters.put(BIRT_OUTPUT_FILE_NAME, contentName);

            // set locale from request
            Locale locale = (Locale)ctx.get(BIRT_LOCALE);
            if (locale == null) {
                locale = ctx.getLocale();
            }
            reportContext.put(BIRT_LOCALE, locale);

            // outputFormat
            String outputFormat = (String)ctx.get(OUTPUT_FORMAT);
            String contentType = (String)ctx.get(CONTENT_TYPE);
            if (UtilValidate.isEmpty(outputFormat) || UtilValidate.isEmpty(contentType)) {
                outputFormat = "html";
                contentType = "text/html";
            }
            reportContext.put(OUTPUT_FORMAT, outputFormat);

            reportContext.put(BIRT_PARAMETERS, birtParameters);
            String birtImageDirectory = UtilProperties.getPropertyValue("birt", "birt.html.image.directory");
            reportContext.put(BIRT_IMAGE_DIRECTORY, birtImageDirectory);

            return exportReport(reportUrl, reportContext, contentType, contentName);
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean exportReport(String reportUrl, Map<String, ? extends Object> context, String contentType, String contentName) throws GeneralException {

        Debug.logInfo("Get report engine", MODULE);
        IReportEngine engine = BirtFactory.getReportEngine();

        IReportRunnable design = null;

        try {
            InputStream stream = BirtFactory.getReportInputStreamFromLocation(reportUrl);
            if (UtilValidate.isNotEmpty(stream)) {
                design = engine.openReportDesign(stream);
            }
        } catch (Exception e) {
            try {
                design = engine.openReportDesign(reportUrl);
            } catch (EngineException e1) {
                throw new GenericServiceException(e1);
            }
        }

        Locale birtLocale = (Locale)context.get(BIRT_LOCALE);
        String birtImageDirectory = (String)context.get(BIRT_IMAGE_DIRECTORY);
        String outputFormat = (String)context.get(OUTPUT_FORMAT);

        if (UtilValidate.isEmpty(contentType)) {
            contentType = "text/html";
        }
        if (birtImageDirectory == null) {
            birtImageDirectory = "/";
        }

        IRunTask runTask = engine.createRunTask(design);
        if (progressMonitor != null) {
            progressMonitor.setTask(runTask);
            runTask.setProgressMonitor(progressMonitor);
        }
        setErrorHandlingOption(runTask, "runTask");

        if (birtLocale != null) {
            Debug.logInfo("Set birt locale:" + birtLocale, MODULE);
            runTask.setLocale(birtLocale);
        }

        // set parameters if exists
        Map<String, Object> parameters = UtilGenerics.cast(context.get(BIRT_PARAMETERS));
        boolean requireTransaction = false;
        boolean beganTrans = false;
        int transactionTimeout = 3600;

        if (parameters != null) {
            Debug.logInfo("Set birt parameters:" + parameters, MODULE);
            runTask.getAppContext().put(BIRT_PARAMETERS, parameters);
            runTask.setParameterValues(parameters);
            requireTransaction = "Y".equals(parameters.get("requireTransaction"));
            int transactionTimeoutConfig = UtilMisc.toInteger(parameters.get("transactionTimeout"));
            if (transactionTimeoutConfig > 0) {
                transactionTimeout = transactionTimeoutConfig;
            }
        }

        File documentDir = new File(engine.getConfig().getTempDir());
        File documentFile = null;
        File reportFile = null;

        try {
            documentFile = File.createTempFile("rpt", ".tmp", documentDir);

            if (requireTransaction) {
                beganTrans = TransactionUtil.begin(transactionTimeout);
            }
            String documentPath = documentFile.getCanonicalPath();
            Debug.logInfo("documentPath: " + documentPath, MODULE);

            try {
                Closeables.add(runTask);
                runTask.run(documentPath);
            } finally {
                Closeables.remove(runTask, true);
                if (progressMonitor != null) {
                    progressMonitor.setTask(null);
                }
            }

            if (checkInterrupted()) {
                return false;
            }

            IReportDocument document = engine.openReportDocument(documentPath);
            IRenderTask renderTask = engine.createRenderTask(document);
            if (progressMonitor != null) {
                progressMonitor.setTask(renderTask);
                renderTask.setProgressMonitor(progressMonitor);
            }
            setErrorHandlingOption(renderTask, "renderTask");

            if (birtLocale != null) {
                Debug.logInfo("Set birt locale:" + birtLocale, MODULE);
                renderTask.setLocale(birtLocale);
            }
            if (parameters != null) {
                Debug.logInfo("Set birt parameters:" + parameters, MODULE);
                renderTask.getAppContext().put(BIRT_PARAMETERS, parameters);
                renderTask.setParameterValues(parameters);
            }

            RenderOption options = new RenderOption();
            if ("text/html".equalsIgnoreCase(contentType)) {
                if (UtilValidate.isEmpty(outputFormat))
                    outputFormat = "html";
                HTMLRenderOption htmlOptions = new HTMLRenderOption(options);
                htmlOptions.setImageDirectory(birtImageDirectory);
                htmlOptions.setBaseImageURL(birtImageDirectory);
                options.setImageHandler(IMAGE_HANDLER);
            } else if ("application/pdf".equalsIgnoreCase(contentType)) {
                if (UtilValidate.isEmpty(outputFormat))
                    outputFormat = "pdf";
                PDFRenderOption pdfOptions = new PDFRenderOption(options);
                pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW, Boolean.TRUE);
            } else if ("application/vnd.ms-word".equalsIgnoreCase(contentType)) {
                if (UtilValidate.isEmpty(outputFormat))
                    outputFormat = "doc";
                options.setOption(IRenderOption.HTML_PAGINATION, true);
            } else if ("application/vnd.oasis.opendocument.text".equalsIgnoreCase(contentType)) {
                if (UtilValidate.isEmpty(outputFormat))
                    outputFormat = "odt";
                options.setOption(IRenderOption.HTML_PAGINATION, true);
            }

            if (UtilValidate.isEmpty(outputFormat))
                outputFormat = "html";

            reportFile = File.createTempFile("rpt", "." + outputFormat, documentDir);
            Debug.logInfo("report tmp path: " + reportFile.getCanonicalPath(), MODULE);

            OutputStream reportFileOutputStream = new BufferedOutputStream(new FileOutputStream(reportFile));
            try {
                options.setOutputFormat(outputFormat);
                options.setOutputStream(reportFileOutputStream);
                renderTask.setRenderOption(options);

                renderTask.getAppContext().put(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, context.get("request"));
                // run report
                Debug.logInfo("Birt's locale is: " + renderTask.getLocale(), MODULE);
                Debug.logInfo("Run render task", MODULE);
                try {
                    Closeables.add(renderTask);
                    renderTask.render();
                } finally {
                    try {
                        try {
                            Closeables.remove(renderTask, true);
                        } finally {
                            renderTask.close();
                        }
                    } finally {
                        document.close();
                    }
                }
            } finally {
                reportFileOutputStream.close();
                if (progressMonitor != null) {
                    progressMonitor.setTask(null);
                }
            }

            if (checkInterrupted()) {
                return false;
            }

            String fileName = contentName + "." + outputFormat;
            return uploadReportFile(contentType, fileName, reportFile);

        } catch (Exception e) {
            if (requireTransaction) {
                try {
                    TransactionUtil.rollback(beganTrans, "Error in birt service", e);
                } catch (GenericTransactionException et) {
                    Debug.logError(et, "Could not rollback transaction: " + et.toString(), MODULE);
                }
            }
            if (e instanceof GeneralException) {
                throw (GenericServiceException) e;
            }
            throw new GenericServiceException(e);
        } finally {
            try {
                if (documentFile != null && documentFile.exists()) {
                    documentFile.delete();
                }
            } finally {
                try {
                    if (reportFile != null && reportFile.exists()) {
                        reportFile.delete();
                    }
                } finally {
                    if (requireTransaction) {
                        try {
                            TransactionUtil.commit(beganTrans);
                        } catch (GenericTransactionException e) {
                            String errMsg = "Could not commit transaction";
                            Debug.logError(e, errMsg, MODULE);
                            if (e.getMessage() != null) {
                                errMsg = errMsg + ": " + e.getMessage();
                            }
                            throw new GenericEntityException(errMsg);
                        }
                    }
                }
            }
        }
    }

    private boolean uploadReportFile(String contentType, String fileName, File reportFile) throws GenericServiceException, IOException {
        ByteBuffer byteBuffer = null;
        FileInputStream fis = new FileInputStream(reportFile);
        try {
            FileChannel channel = fis.getChannel();
            try {
                byteBuffer = ByteBuffer.allocate((int)channel.size());
                while (true) {
                    if (channel.read(byteBuffer) <= 0) {
                        break;
                    }
                }
            } finally {
                channel.close();
            }
        } finally {
            fis.close();
        }

        if (checkInterrupted()) {
            return false;
        }

        String serviceName = "createContentFromUploadedFile";
        Map<String, Object> serviceContext = ctx.getDctx().makeValidContext(serviceName, "IN", ctx);
        serviceContext.put("_uploadedFile_contentType", contentType);
        serviceContext.put("_uploadedFile_fileName", fileName);
        serviceContext.put("uploadedFile", byteBuffer);
        serviceContext.put("isPublic", "N");
        
        serviceContext.put("contentTypeId", AsyncJobUtil.CONTENT_TYPE_TMP_ENCLOSE); // Allegati temporanei e cancellati dopo 24 ore, ma utilizzati per essere mostrati nella portlet
        String contentTypeId = (String)ctx.get("contentTypeId");
        if (UtilValidate.isNotEmpty(contentTypeId)) {
            serviceContext.put("contentTypeId", contentTypeId);
        }
        
        serviceContext.put("roleTypeNotRequired", "Y");

        serviceContext.put("partyId", ctx.getUserLogin().get("partyId"));
        String contentPartyId = (String)ctx.get("contentPartyId");
        if (UtilValidate.isNotEmpty(contentPartyId)) {
            serviceContext.put("partyId", contentPartyId);
        }
        
        if (getJob() != null) {
            serviceContext.put("serviceName", getJob().getJobId());
            serviceContext.put("createdDate", getJob().getCreatedDate());
        } else {
            serviceContext.put("createdDate", UtilDateTime.nowTimestamp());
        }
        
        Map<String, Object> serviceResult = ctx.getDispatcher().runSync(serviceName, serviceContext);
        String contentId = (String)serviceResult.get("contentId");
        ctx.getResult().put("contentId", contentId);
        Debug.logInfo("report output contentId: " + contentId, MODULE);

        return ServiceUtil.isSuccess(serviceResult);
    }

    private boolean checkInterrupted() {
        if (job != null) {
            if (job.isInterrupted()) {
                ctx.getResult().putAll(ServiceUtil.returnError("Service interrupted"));
                return true;
            }
        }
        return false;
    }

    private void setWebContextObjects(Map<String, Object> appContext) {
        // set delegator
        if (UtilValidate.isNotEmpty(ctx.getDelegator())) {
            appContext.put("delegator", ctx.getDelegator());

            Set<String> groupNames = ctx.getDelegator().getModelGroupReader().getGroupNames(ctx.getDelegator().getDelegatorBaseName());

            String[] groupNamesArray = new String[groupNames.size()];
            GenericHelperInfo helperInfo = ctx.getDelegator().getGroupHelperInfo(groupNames.toArray(groupNamesArray)[0]);

            // verifico se esiste una connessione per birt  
            if (!configDataSource(appContext, helperInfo.getHelperBaseName() + "Birt")) {
                configDataSource(appContext, helperInfo.getHelperBaseName());
            }
        }

        // set dispatcher
        if (UtilValidate.isNotEmpty(ctx.getDispatcher())) {
            appContext.put("dispatcher", ctx.getDispatcher());
        }

        // set security
        if (UtilValidate.isNotEmpty(ctx.getSecurity())) {
            appContext.put("security", ctx.getSecurity());
        }

        // set locale 
        Locale locale = (Locale)ctx.get(BIRT_LOCALE);
        if (locale == null) {
            locale = ctx.getLocale();
        }
        appContext.put("locale", locale);
    }

    private static boolean configDataSource(Map<String, Object> appContext, String helperName) {
        DatasourceInfo dataSourceInfo = EntityConfigUtil.getDatasourceInfo(helperName);
        if (UtilValidate.isNotEmpty(dataSourceInfo)) {
            Element inlineJdbcElement = dataSourceInfo.inlineJdbcElement;
            if (UtilValidate.isNotEmpty(inlineJdbcElement)) {
                String odaURL = inlineJdbcElement.getAttribute("jdbc-uri");
                String odaDriverClass = inlineJdbcElement.getAttribute("jdbc-driver");
                String odaUser = inlineJdbcElement.getAttribute("jdbc-username");
                String odaPassword = inlineJdbcElement.getAttribute("jdbc-password");
                if (UtilValidate.isNotEmpty(odaURL) && UtilValidate.isNotEmpty(odaDriverClass) && UtilValidate.isNotEmpty(odaUser)) {
                    appContext.put("odaURL", odaURL);
                    appContext.put("odaDriverClass", odaDriverClass);
                    appContext.put("odaPassword", odaPassword);
                    appContext.put("odaUser", odaUser);
                    String odaIsolationMode = UtilProperties.getPropertyValue("birt", "birt.odaIsolationMode");
                    if (UtilValidate.isNotEmpty(odaIsolationMode)) {
                        appContext.put("odaIsolationMode", odaIsolationMode);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static void setErrorHandlingOption(IEngineTask task, String taskName) {
        Boolean cancelOnError = Boolean.valueOf(UtilProperties.getPropertyValue("birt", "birt." + taskName + ".cancelOnError"));
        task.setErrorHandlingOption(cancelOnError ? IEngineTask.CANCEL_ON_ERROR : IEngineTask.CONTINUE_ON_ERROR);
    }
}
