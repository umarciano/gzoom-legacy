package com.mapsengineering.workeffortext.services.status;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.accountingext.services.InputAndDetailValue;
import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.workeffortext.services.trans.E;

/**
 * CheckHasMandatoryTransEmptyService
 */
public class CheckHasMandatoryTransEmptyService extends GenericService {

    public static final String MODULE = CheckHasMandatoryTransEmptyService.class.getName();
    private static final String SERVICE_NAME = "checkWorkEffortStatusHasMandatoryTransEmpty";
    private static final String SERVICE_TYPE = null;

    private static final String queryCheckMandatoryTransEmpty = "sql/checkMandatoryTransEmpty/queryCheckMandatoryTransEmpty.sql.ftl";
    private static final String WorkeffortExtErrorLabels = "WorkeffortExtErrorLabels";

    /**
     * CheckHasMandatoryTransEmptyService
     */
    public static Map<String, Object> checkHasMandatoryTransEmpty(DispatchContext dctx, Map<String, Object> context) {

        CheckHasMandatoryTransEmptyService obj = new CheckHasMandatoryTransEmptyService(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     */
    public CheckHasMandatoryTransEmptyService(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        userLogin = (GenericValue)context.get(ServiceLogger.USER_LOGIN);
    }

    /**
     * Main loop <br/>
     * La query estrae i record per cui non esistono i movimenti.
     * Ogni record corrisponde au un movimento mancante.
     * Per ogni movimento viene scritto un messaggio di errore.
     * I diversi messaggi di errore sono concatenati e restituiti al servizio che lo invoca
     */
    public void mainLoop() {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List<String> mandatoryTransEmptyList = new ArrayList<String>();
        long startTime = System.currentTimeMillis();
        int index = 0;
        boolean hasMandatoryTransEmpty = false;
        try {

            JdbcQueryIterator queryCheckMandatoryTransEmptyList = new FtlQuery(getDelegator(), queryCheckMandatoryTransEmpty, mapContextUpdate()).iterate();
            try {
                while (queryCheckMandatoryTransEmptyList.hasNext()) {
                    if(index == 0) {
                        long endTime = System.currentTimeMillis();
                        JobLogLog print = new JobLogLog().initLogCode("BaseUiLabels", "EndFtlService", null, getLocale());
                        addLogInfo(print.getLogMessage() + " " + (endTime - startTime) + " milliseconds ", null);
                        index++;
                    }
                    hasMandatoryTransEmpty = true;
                    ResultSet ele = queryCheckMandatoryTransEmptyList.next();
                    Debug.log(" No movement for "+ getSchedaId(ele) + " - " + getObiettivoId(ele) + " - " + getMisura(ele));                            
                    mandatoryTransEmptyList.add(getErrorMessage(ele));
                }
                
            } finally {
                queryCheckMandatoryTransEmptyList.close();
            }
            
            if (hasMandatoryTransEmpty) {
                String errMsg = "<br>" + StringUtil.join(mandatoryTransEmptyList, "<br>");             
                result = ServiceUtil.returnError(errMsg);
            }
        } catch (Exception e) {
            String msg = "Error: ";
            addLogError(e, msg);
            result = ServiceUtil.returnError(e.getMessage());
        } finally {
            setResult(result);
        }
    }

    private String getMisura(ResultSet ele) throws SQLException {
        if (isModelloSuObiettivo(ele) && UtilValidate.isNotEmpty(getMisuraObj(ele))) {
            return getMisuraObj(ele);
        } else if (isModelloSuProdotto(ele) && UtilValidate.isNotEmpty(getMisuraPrd(ele))) {
            return getMisuraPrd(ele);
        }
        return getMisuraUo(ele);
    }
    
    private String getErrorMessage(ResultSet ele) throws SQLException {
    	String errMsg = UtilProperties.getMessage(WorkeffortExtErrorLabels, "MandatoryFieldMissing", getLocale());
    	StringBuilder errorSb = new StringBuilder();
    	errorSb.append(errMsg);
    	errorSb.append(getObiettivoLanguage(ele) + " ---> ");
    	errorSb.append(getAccountNameLanguage(ele));
    	errorSb.append(getUomDescrLanguage(ele));
    	return errorSb.toString();
    }

    private String getUomDescrLanguage(ResultSet ele) throws SQLException {
        String result = ele.getString(E.MISURA_OBJ.name());
        String resultLang = ele.getString(E.MISURA_OBJ_LANG.name());
        if(!isPrimaryLang() && UtilValidate.isNotEmpty(resultLang)) {
            return " " + resultLang;
        } else if(UtilValidate.isNotEmpty(result)) {
            return " " + result;
        }
        return "";
    }

    private String getAccountNameLanguage(ResultSet ele) throws SQLException {
        String result = ele.getString(E.ACCOUNT_NAME.name());
        String resultLang = ele.getString(E.ACCOUNT_NAME_LANG.name());
        
        if(!isPrimaryLang() && UtilValidate.isNotEmpty(resultLang)) {
            return " " + resultLang;
        }   
        return " " + result;
    }

    private String getObiettivoLanguage(ResultSet ele) throws SQLException {
        String result = getObiettivo(ele);
        String resultLang = getObiettivoLang(ele);
        
        if(!isPrimaryLang() && UtilValidate.isNotEmpty(resultLang)) {
          return " " + resultLang;
        }   
        return " " + result;
    }

    private boolean isModelloSuProdotto(ResultSet ele) throws SQLException {
        return InputAndDetailValue.ACCINP_PRD.equals(getInputEnumId(ele));
    }

    private boolean isModelloSuObiettivo(ResultSet ele) throws SQLException {
        return InputAndDetailValue.ACCINP_OBJ.equals(getInputEnumId(ele));
    }

    private String getSchedaId(ResultSet ele) throws SQLException {
        return ele.getString(E.SCHEDA_ID.name());
    }

    private String getObiettivo(ResultSet ele) throws SQLException {
        return ele.getString(E.OBIETTIVO.name());
    }
    
    private String getObiettivoLang(ResultSet ele) throws SQLException {
        return ele.getString(E.OBIETTIVO_LANG.name());
    }

    private String getObiettivoId(ResultSet ele) throws SQLException {
        return ele.getString(E.OBIETTIVO_ID.name());
    }

    private String getMisuraObj(ResultSet ele) throws SQLException {
        return ele.getString(E.MISURA_OBJ.name());
    }

    private String getMisuraUo(ResultSet ele) throws SQLException {
        return ele.getString(E.MISURA_UO.name());
    }

    private String getMisuraPrd(ResultSet ele) throws SQLException {
        return ele.getString(E.MISURA_PRD.name());
    }

    private String getInputEnumId(ResultSet ele) throws SQLException {
        return ele.getString(E.INPUT_ENUM_ID.name());
    }

    private MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(E.workEffortId.name(), getWorkEffortId());
        mapContext.put(E.statusId.name(), getStatusId());
        return mapContext;
    }

    private String getStatusId() {
        return (String)context.get(E.statusId.name());
    }

    private String getWorkEffortId() {
        return (String)context.get(E.workEffortId.name());
    }

    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
}
