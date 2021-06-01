package com.mapsengineering.base.birt.webapp.event;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.birt.BirtWorker;

import com.mapsengineering.base.birt.BirtException;
import com.mapsengineering.base.birt.event.EventHandlerException;
import com.mapsengineering.base.birt.model.ModelReader;
import com.mapsengineering.base.birt.model.ModelReport;

public class BirtEvents {
	private static final String SUCCESS = "success";

	private static final String ERROR = "error";
    private static final String BIRT_EVENTS_APP_MODULE = UtilProperties.getPropertyValue("general.properties", "logMDC.birtEventsAppModule");

	public static final String MODULE = BirtEvents.class.getName();

	public static final String RESOURCE_ERR = "BaseErrorLabels";	
	public static final String ERROR_MSG = "_ERROR_MESSAGE_";	
	public static final String ERROR_MSG_LIST = "_ERROR_MESSAGE_LIST_";
	
	public static final String LOCAL_DISPACHER_NAME = "localDispatcherName";   
	
	private BirtEvents() {}

	public static String validateReportParameters(HttpServletRequest request, HttpServletResponse response) {
		return checkReportParameters(request, response, true);
	}

	public static String loadReportParameters(HttpServletRequest request, HttpServletResponse response) {
		return checkReportParameters(request, response, false);
	}
	
	/** Validate parameters,</BR> Add birtParameters in request if isValidate = false */
	private static String checkReportParameters(HttpServletRequest request, HttpServletResponse response, boolean isValidate) {
        Debug.putMDC("appModule", BIRT_EVENTS_APP_MODULE);
		Locale locale = UtilHttp.getLocale(request);

		try {
			Map<String, Object> parameters = UtilHttp.getCombinedMap(request);

			String reportId = UtilGenerics.cast(parameters.get("reportContentId"));
			ModelReport report = ModelReader.getModelReport(reportId);
			if (UtilValidate.isEmpty(report)) {
				Debug.logError("Nessun report trovato", MODULE);
				request.setAttribute(ERROR_MSG, UtilProperties.getMessage(RESOURCE_ERR, "report.reportNotFound", locale));
				return ERROR;
			}
			
			report.invokeAllEvent(parameters, locale);

			List<String> errorMessage = report.validateAllValue(parameters, locale);
			
			if (UtilValidate.isEmpty(errorMessage)) {
				if(!isValidate) {
					Map<String, Object> birtParameters = report.convertAllValue(parameters, locale);
					
					//aggiungo il localDispatcherName
					String localDispatcherName = (String)parameters.get(LOCAL_DISPACHER_NAME);
					birtParameters.put(LOCAL_DISPACHER_NAME, localDispatcherName);
					
					/**
			         * Prendo il default selezionato nella stampa se non c'e prendo il locale
			         */
			        String langLocale = "";
			        String birtLocale = (String)parameters.get(BirtWorker.BIRT_LOCALE);
			        if (UtilValidate.isNotEmpty(birtLocale)) {
			            if (birtLocale.contains("_LANG")){
			                langLocale = birtLocale.substring(birtLocale.lastIndexOf("_"));
			                birtLocale = birtLocale.substring(0, birtLocale.lastIndexOf("_"));
			            }
			            request.setAttribute(BirtWorker.BIRT_LOCALE, new Locale(birtLocale));
			            
			        }
			        birtParameters.put("langLocale", langLocale);
					request.setAttribute(BirtWorker.BIRT_PARAMETERS, birtParameters);
					request.setAttribute(BirtWorker.BIRT_OUTPUT_FILE_NAME, report.getOutputFileName());
				}
			} else {
				request.setAttribute(ERROR_MSG_LIST, errorMessage);
				return ERROR;
			}
		} catch (BirtException e) {
			Debug.logError(e, MODULE);
			request.setAttribute(ERROR_MSG, UtilProperties.getMessage(RESOURCE_ERR, "report.reportLoadingError", locale));
			return ERROR;
		} catch (EventHandlerException e) {
			Debug.logError(e, MODULE);
			request.setAttribute(ERROR_MSG, UtilProperties.getMessage(RESOURCE_ERR, "report.reportParameterInvokeError", locale));
			return ERROR;
		}

		return SUCCESS;
	}
}
