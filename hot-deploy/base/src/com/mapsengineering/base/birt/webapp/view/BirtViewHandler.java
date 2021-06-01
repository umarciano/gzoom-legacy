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
package com.mapsengineering.base.birt.webapp.view;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastMap;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.birt.BirtFactory;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.webapp.view.ViewHandler;
import org.ofbiz.webapp.view.ViewHandlerException;
import org.xml.sax.SAXException;

import com.mapsengineering.base.birt.BirtWorker;

public class BirtViewHandler implements ViewHandler {

    public static final String module = BirtViewHandler.class.getName();

    protected ServletContext servletContext = null;

    private String name = "birt-content";

    public void init(ServletContext context) throws ViewHandlerException {
        this.servletContext = context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        try {
            IReportEngine engine = BirtFactory.getReportEngine();

            String reportUrl = null;

            Map<String, Object> requestParameters = UtilHttp.getCombinedMap(request);

            if (UtilValidate.isNotEmpty(requestParameters.get("reportContentId"))) {
                String reportContentId = UtilGenerics.cast(requestParameters.get("reportContentId"));

                Delegator delegator = UtilGenerics.cast(request.getAttribute("delegator"));
                GenericValue reportContent = delegator.findOne("Content", UtilMisc.toMap("contentId", reportContentId), false);
                if (UtilValidate.isNotEmpty(reportContent)) {
                    name = reportContent.getString("contentName");

                    GenericValue dataResource = reportContent.getRelatedOne("DataResource");
                    if (UtilValidate.isNotEmpty(dataResource)) {
                        String objectInfo = dataResource.getString("objectInfo");
                        if (UtilValidate.isNotEmpty(objectInfo)) {
                            if (objectInfo.startsWith("component://")) {
                                reportUrl = objectInfo;
                            } else {
                                reportUrl = servletContext.getRealPath(objectInfo);
                            }
                        }
                    }
                }
            }

            if (UtilValidate.isNotEmpty(reportUrl)) {
                Map<String, Object> appContext = UtilGenerics.cast(engine.getConfig().getAppContext());
                BirtWorker.setWebContextObjects(appContext, request, response);

                Map<String, Object> context = FastMap.newInstance();
                // set parameters from request
                Map<String, Object> parameters = UtilGenerics.cast(request.getAttribute(BirtWorker.BIRT_PARAMETERS));
                if (parameters == null) {
                    parameters = UtilHttp.getParameterMap(request);
                }

                HttpSession session = request.getSession();
                Map<String, Object> userLogin = UtilGenerics.checkMap(session.getAttribute("userLogin"));
                if (UtilValidate.isNotEmpty(userLogin)) {
                    parameters.put("userLoginId", userLogin.get("userLoginId"));
                }
                context.put("userLogin", userLogin);

                parameters.put(BirtWorker.BIRT_OUTPUT_FILE_NAME, name);

                // set locale from request
                Locale locale = (Locale)request.getAttribute(BirtWorker.BIRT_LOCALE);
                if (locale == null) {
                    locale = UtilHttp.getLocale(request);
                }
                context.put(BirtWorker.BIRT_LOCALE, locale);

                //outputFormat
                String outputFormat = (String)requestParameters.get(BirtWorker.OUTPUT_FORMAT);
                if (UtilValidate.isEmpty(outputFormat) || UtilValidate.isEmpty(contentType)) {
                    outputFormat = "html";
                    contentType = "text/html";
                }
                context.put(BirtWorker.OUTPUT_FORMAT, outputFormat);

                response.setHeader("Content-Type", contentType);

                /**Inserisco solo per il formato excel il nome del file*/
                if (UtilValidate.isNotEmpty(name) && !"application/pdf".equals(contentType)) {
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "." + outputFormat + "\"");
                }

                context.put(BirtWorker.BIRT_PARAMETERS, parameters);
                String birtImageDirectory = UtilProperties.getPropertyValue("birt", "birt.html.image.directory");
                context.put(BirtWorker.BIRT_IMAGE_DIRECTORY, birtImageDirectory);
                // GN-992
                // Il flush non e' propagato tramite AJP di Tomcat 6
                // https://issues.apache.org/bugzilla/show_bug.cgi?id=41791
                response.flushBuffer();

                BirtWorker.exportReport(reportUrl, context, contentType, response.getOutputStream());

            }
        } catch (BirtException e) {
            throw new ViewHandlerException("Birt Error create engine: " + e.toString(), e);
        } catch (IOException e) {
            throw new ViewHandlerException("Error in the response writer/output stream: " + e.toString(), e);
        } catch (SQLException e) {
            throw new ViewHandlerException("get connection error: " + e.toString(), e);
        } catch (GenericEntityException e) {
            throw new ViewHandlerException("generic entity error: " + e.toString(), e);
        } catch (GeneralException e) {
            throw new ViewHandlerException("general error: " + e.toString(), e);
        } catch (SAXException se) {
            String errMsg = "Error SAX rendering " + page + " view handler: " + se.toString();
            Debug.logError(se, errMsg, module);
            throw new ViewHandlerException(errMsg, se);
        } catch (ParserConfigurationException pe) {
            String errMsg = "Error parser rendering " + page + " view handler: " + pe.toString();
            Debug.logError(pe, errMsg, module);
            throw new ViewHandlerException(errMsg, pe);
        }
    }
}
