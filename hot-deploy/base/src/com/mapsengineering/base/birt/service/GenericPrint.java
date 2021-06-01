package com.mapsengineering.base.birt.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.birt.E;
import com.mapsengineering.base.birt.model.ModelReader;
import com.mapsengineering.base.birt.model.ModelReport;
import com.mapsengineering.base.birt.service.BirtService;
import com.mapsengineering.base.birt.util.Utils;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;

public class GenericPrint extends GenericService {

    public static final String MODULE = GenericPrint.class.getName();
    public static final String SERVICE_NAME = "genericPrint";
    public static final String SERVICE_TYPE = "GENERIC_PRINT";
    private static final String DEFAULT_CONTENT_MIME_TYPE_ID = "text/plain";
    private static final String DEFAULT_REPORT_CONTENT_MIME_TYPE_ID = "application/pdf";
    private static final String DEFAULT_REPORT_OUTPUT_FORMAT = "pdf";
    
    private Map<String, Object> birtContext;
       
    public GenericPrint(DispatchContext dctx, Map<String, Object> context, JobLogger jobLogger) {
        super(dctx, context, jobLogger, SERVICE_NAME, SERVICE_TYPE, MODULE);
        
        birtContext = FastMap.newInstance();
        birtContext.put(E.reportContentId.name(), context.get(E.reportContentId.name()));
        birtContext.put(E.outputFormat.name(), getElementOrDefault((String)context.get(E.outputFormat.name()), DEFAULT_REPORT_OUTPUT_FORMAT));
        birtContext.put(E.contentType.name(), getElementOrDefault((String)context.get(E.contentType.name()), DEFAULT_REPORT_CONTENT_MIME_TYPE_ID));
        birtContext.put(E.userLogin.name(), getUserLogin());
        birtContext.put(E.locale.name(), getLocale());
    }
    
    public void setName(String name) {
    	birtContext.put(E.name.name(), name);
    }
    
    public String getElementOrDefault(String value, String defaultValue) {
        if (UtilValidate.isEmpty(value)) {
        	value = defaultValue;
        }
        return value;
    }

    public Map<String, Object> print(Map<String, Object> ele) {
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        setResult(ServiceUtil.returnSuccess());
        JobLogLog session = new JobLogLog().initLogCode("BaseUiLabels", "StartGenericPrint", null, getLocale());
        addLogInfo(session.getLogMessage() + " " + getSessionId() + " " + startTimestamp );
        
        try {
        	Map<String, Object> checkReportParameters = Utils.checkReportParameters(context, ele, getReportId(), getLocale(), getLocalDispatcherName());
            if (checkReportParameters == null) {
            	JobLogLog reportLog = new JobLogLog().initLogCode("BaseUiLabels", "ERROR_CONTENT_ID", UtilMisc.toMap(E.reportId.name(), (Object)getReportId()), getLocale());
                throw new GeneralException(reportLog.getLogMessage() + " - " + reportLog);
            }
            
            Map<String, Object> birtResult = createBirtReport(checkReportParameters);
            getResult().put(E.contentId.name(), birtResult.get(E.contentId.name()));
        } catch (Exception e) {
            setResult(ServiceUtil.returnError(e.getMessage()));
            JobLogLog print  = new JobLogLog().initLogCode("BaseUiLabels", "ERROR_MAIL", null, getLocale());
            addLogError(e, print.getLogCode(), print.getLogMessage());
        } 
        
        Timestamp endTimestamp = UtilDateTime.nowTimestamp();
        JobLogLog print = new JobLogLog().initLogCode("BaseUiLabels", "EndGenericPrint", null, getLocale());
        addLogInfo(print.getLogMessage() + " - " + endTimestamp, null);
        
        return getResult();
    }
    
    public String getReportId() {
        return (String) context.get(E.reportContentId.name());
    }
    
    public String getLocalDispatcherName() {
        return (String) context.get(E.localDispatcherName.name());
    }

    
    private Map<String, Object>  createBirtReport(Map<String, Object> ele) throws GeneralException {
        Map<String, Object> birtParameters = FastMap.newInstance();
        birtParameters.putAll(ele);
        birtContext.put(BirtService.BIRT_PARAMETERS, birtParameters);
        JobLogLog print = new JobLogLog().initLogCode("BaseUiLabels", "CreatedReport", null, getLocale());
        addLogInfo(print.getLogMessage() + birtParameters.values());
        return runSync(E.createBirtReport.name(), birtContext, "success createBirtReport", "error createBirtReport", true, true, null);
    }
    
    
    public List<Map> getListFileName(List<String> listContent) throws GenericEntityException {
        List<Map> list = FastList.newInstance();
    	EntityCondition cond = EntityCondition.makeCondition(EntityCondition.makeCondition(E.contentId.name(), EntityOperator.IN, listContent));
        List<GenericValue> listGenericValue = getDelegator().findList(E.ContentDataResourceView.name(),  cond, UtilMisc.toSet(E.drObjectInfo.name(), E.drDataResourceName.name()), null, null, false);
        for(GenericValue gv: listGenericValue){
        	list.add(UtilMisc.toMap(E.pathFile.name(), gv.getString(E.drObjectInfo.name()),E.nameFile.name(), gv.getString(E.drDataResourceName.name())));
        }
        return list;
    }
    
 }
