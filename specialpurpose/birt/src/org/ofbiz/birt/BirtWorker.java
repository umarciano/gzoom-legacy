/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.birt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
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
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.config.DatasourceInfo;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.LocalDispatcher;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class BirtWorker {

    public final static String module = BirtWorker.class.getName();

    public final static String BIRT_PARAMETERS = "birtParameters";
    public final static String REPORT_ENGINE = "reportEngine";
    public final static String BIRT_LOCALE = "birtLocale";
    public final static String BIRT_IMAGE_DIRECTORY = "birtImageDirectory";
    public final static String BIRT_CONTENT_TYPE = "birtContentType";
    public final static String BIRT_OUTPUT_FILE_NAME = "birtOutputFileName";
    public final static String OUTPUT_FORMAT = "outputFormat";

    private static HTMLServerImageHandler imageHandler = new HTMLServerImageHandler();

    /**
     * export report
     * @param design
     * @param context
     * @param contentType
     * @param output
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    public static void exportReport(String reportUrl, Map<String, ? extends Object> context, String contentType, OutputStream output) throws EngineException, GeneralException, SQLException, IOException, SAXException, ParserConfigurationException {

        Debug.logInfo("Get report engine", module);
        IReportEngine engine = BirtFactory.getReportEngine();

        IReportRunnable design = null;
        try {
            InputStream stream = BirtFactory.getReportInputStreamFromLocation(reportUrl);
            if (UtilValidate.isNotEmpty(stream)) {
                design = engine.openReportDesign(stream);
            }
        } catch (IllegalArgumentException e) {
            design = engine.openReportDesign(reportUrl);
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

        IRunTask designTask = engine.createRunTask(design);
        if (birtLocale != null) {
            Debug.logInfo("Set birt locale:" + birtLocale, module);
            designTask.setLocale(birtLocale);
        }

        // set parameters if exists
        Map<String, Object> parameters = UtilGenerics.cast(context.get(BirtWorker.BIRT_PARAMETERS));
        boolean requireTransaction = false;
        boolean beganTrans = false;
        int transactionTimeout = 3600;

        if (parameters != null) {
            Debug.logInfo("Set birt parameters:" + parameters, module);
            designTask.getAppContext().put(BIRT_PARAMETERS, parameters);
            designTask.setParameterValues(parameters);
            requireTransaction = "Y".equals(parameters.get("requireTransaction"));
            int transactionTimeoutConfig = UtilMisc.toInteger(parameters.get("transactionTimeout"));
            if (transactionTimeoutConfig > 0) {
                transactionTimeout = transactionTimeoutConfig;
            }
        }

        File documentDir = new File(engine.getConfig().getTempDir());
        File documentFile = File.createTempFile("rpt", ".tmp", documentDir);

        try {

            if (requireTransaction) {
                beganTrans = TransactionUtil.begin(transactionTimeout);
            }
            String documentPath = documentFile.getCanonicalPath();
            Debug.logInfo("documentPath: " + documentPath, module);

            designTask.run(documentPath);

            IReportDocument document = engine.openReportDocument(documentPath);
            IRenderTask task = engine.createRenderTask(document);
            if (birtLocale != null) {
                Debug.logInfo("Set birt locale:" + birtLocale, module);
                task.setLocale(birtLocale);
            }
            if (parameters != null) {
                Debug.logInfo("Set birt parameters:" + parameters, module);
                task.getAppContext().put(BIRT_PARAMETERS, parameters);
                task.setParameterValues(parameters);
            }

            RenderOption options = new RenderOption();
            if ("text/html".equalsIgnoreCase(contentType)) {
                if (UtilValidate.isEmpty(outputFormat))
                    outputFormat = "html";
                HTMLRenderOption htmlOptions = new HTMLRenderOption(options);
                htmlOptions.setImageDirectory(birtImageDirectory);
                htmlOptions.setBaseImageURL(birtImageDirectory);
                options.setImageHandler(imageHandler);
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

            options.setOutputFormat(outputFormat);
            options.setOutputStream(output);
            task.setRenderOption(options);

            task.getAppContext().put(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, context.get("request"));
            // run report
            Debug.logInfo("Birt's locale is: " + task.getLocale(), module);
            Debug.logInfo("Run report's task", module);
            try {
                task.render();
            } finally {
                try {
                    task.close();
                } finally {
                    document.close();
                }
            }
        } catch (Exception e) {
            if (requireTransaction) {
                try {
                    TransactionUtil.rollback(beganTrans, "Error in BirtWorker", e);
                } catch (GenericTransactionException et) {
                    Debug.logError(et, "Could not rollback transaction: " + et.toString(), module);
                }
            }
            exportReportRethrow(e);
        } finally {
            if (documentFile != null && documentFile.exists()) {
                documentFile.delete();
            }
            if (requireTransaction) {
                try {
                    TransactionUtil.commit(beganTrans);
                } catch (GenericTransactionException e) {
                    String errMsg = "Could not commit transaction";
                    Debug.logError(e, errMsg, module);
                    if (e.getMessage() != null) {
                        errMsg = errMsg + ": " + e.getMessage();
                    }
                    throw new GenericEntityException(errMsg);
                }
            }
        }
    }

    public static void setWebContextObjects(Map<String, Object> appContext, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();

        // set delegator
        Delegator delegator = (Delegator)session.getAttribute("delegator");
        if (UtilValidate.isEmpty(delegator)) {
            delegator = (Delegator)servletContext.getAttribute("delegator");
        }
        if (UtilValidate.isEmpty(delegator)) {
            delegator = (Delegator)request.getAttribute("delegator");
        }
        if (UtilValidate.isNotEmpty(delegator)) {
            appContext.put("delegator", delegator);

            Set<String> groupNames = delegator.getModelGroupReader().getGroupNames(delegator.getDelegatorBaseName());

            String[] groupNamesArray = new String[groupNames.size()];
            GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(groupNames.toArray(groupNamesArray)[0]);

            // verifico se esiste una connessione per birt	
            if (!configDataSource(appContext, helperInfo.getHelperBaseName() + "Birt")) {
                configDataSource(appContext, helperInfo.getHelperBaseName());
            }
        }

        // set delegator
        LocalDispatcher dispatcher = (LocalDispatcher)session.getAttribute("dispatcher");
        if (UtilValidate.isEmpty(dispatcher)) {
            dispatcher = (LocalDispatcher)servletContext.getAttribute("dispatcher");
        }
        if (UtilValidate.isEmpty(dispatcher)) {
            dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
        }
        if (UtilValidate.isNotEmpty(dispatcher)) {
            appContext.put("dispatcher", dispatcher);
        }

        // set security
        Security security = (Security)session.getAttribute("security");
        if (UtilValidate.isEmpty(security)) {
            security = (Security)servletContext.getAttribute("security");
        }
        if (UtilValidate.isEmpty(security)) {
            security = (Security)request.getAttribute("security");
        }
        if (UtilValidate.isNotEmpty(security)) {
            appContext.put("security", security);
        }
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
                    return true;
                }
            }
        }
        return false;
    }

    private static void exportReportRethrow(Exception e) throws EngineException, GeneralException, SQLException, IOException, SAXException, ParserConfigurationException {
        if (e instanceof EngineException)
            throw (EngineException)e;
        if (e instanceof GeneralException)
            throw (GeneralException)e;
        if (e instanceof SQLException)
            throw (SQLException)e;
        if (e instanceof IOException)
            throw (IOException)e;
        if (e instanceof SAXException)
            throw (SAXException)e;
        if (e instanceof ParserConfigurationException)
            throw (ParserConfigurationException)e;
        throw new RuntimeException(e);
    }
}
