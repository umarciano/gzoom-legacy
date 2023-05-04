package com.mapsengineering.base.services;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import org.apache.ibatis.jdbc.RuntimeSqlException;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.MessageUtil;

import javolution.util.FastMap;

public class QueryExecutorService extends GenericServiceLoop {
	
    private GenericHelperInfo helperInfo;
    private String queryId;
    private final String username;
    public static final String ENTITYNAME = "QueryConfig";
    public static final String DEFAULT_GROUP_NAME = "org.ofbiz";
    public static final String MODULE = QueryExecutorService.class.getName();
    
    private static final String SERVICE_NAME = "QueryExecutorService";
    private static final String SERVICE_TYPE_ID = "QUERY_EXECUTOR";
    private static final String RESOURCE_LABEL = "BaseUiLabels";
    public static final String RESULT_LIST = "resultList";
    public static final String MESSAGE = "message";
    public static final String MESSAGE_LANG = "messageLang";
    public static final String WARNING_MESSAGE = "warningMessage";
    public static final String WARNING_MESSAGE_LANG = "warningMessageLang";
    public static final String PARTY = "PARTY_ID";
    
    public enum FIELDS {
        QueryConfig, queryId, queryActive, queryInfo, queryType, R, E, A, S, queryCode, queryName, cond0Info, cond1Info, histJobLogId, queryExecutorService, 
        //
        userLoginId, Y, MESSAGE, MESSAGE_LANG, WARNING_MESSAGE, WARNING_MESSAGE_LANG
    }
    
    public QueryExecutorService(DispatchContext dctx, Map<String, Object> context) throws Exception {
    	super(dctx, context, SERVICE_NAME, SERVICE_TYPE_ID, MODULE);
    	this.helperInfo = dctx.getDelegator().getGroupHelperInfo(DEFAULT_GROUP_NAME);
    	this.queryId = context.get("queryId").toString();
    	this.username = ((GenericValue) context.get("userLogin")).get("userLoginId").toString();
    }
    
    public static Map<String, Object> runExecuteQuery(DispatchContext dctx, Map<String, Object> context) throws Exception {
    	QueryExecutorService exe = new QueryExecutorService(dctx, context);
    	exe.mainLoop();
    	return exe.getResult();
    }

    protected Map<String, Object> getServiceParameters() {
        Map<String, Object> serviceParameters = new HashMap<String, Object>();

        if(UtilValidate.isNotEmpty(context.get("cond0Info")))
            serviceParameters.put("cond0Info", context.get("cond0Info"));
        if(UtilValidate.isNotEmpty(context.get("cond1Info")))
            serviceParameters.put("cond1Info", context.get("cond1Info"));
        if(UtilValidate.isNotEmpty(context.get("cond2Info")))
            serviceParameters.put("cond2Info", context.get("cond2Info"));
        if(UtilValidate.isNotEmpty(context.get("cond3Info")))
            serviceParameters.put("cond3Info", context.get("cond3Info"));
        if(UtilValidate.isNotEmpty(context.get("cond4Info")))
            serviceParameters.put("cond4Info", context.get("cond4Info"));
        if(UtilValidate.isNotEmpty(context.get("cond5Info")))
            serviceParameters.put("cond5Info", context.get("cond5Info"));
        if(UtilValidate.isNotEmpty(context.get("cond6Info")))
            serviceParameters.put("cond6Info", context.get("cond6Info"));
        if(UtilValidate.isNotEmpty(context.get("cond7Info")))
            serviceParameters.put("cond7Info", context.get("cond7Info"));
        if(UtilValidate.isNotEmpty(context.get("cond8Info")))
            serviceParameters.put("cond8Info", context.get("cond8Info"));
        
        serviceParameters.put("userLoginId", this.username); 
        return serviceParameters;
    }
    

	@Override
	protected void execute() throws Exception {
		GenericValue queryConfig = delegator.findOne(ENTITYNAME, false, UtilMisc.toMap("queryId", this.queryId));
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			
			if(queryConfig!=null)
			{
				if(queryConfig.getString(FIELDS.queryType.name()).equals(FIELDS.E.name())) {
					//GN-4940
	                if(!isContain(queryConfig.getString(FIELDS.queryInfo.name()),"UPDATE") &&
	                        !isContain(queryConfig.getString(FIELDS.queryInfo.name()),"DELETE") &&
	                        !isContain(queryConfig.getString(FIELDS.queryInfo.name()),"TRUNCATE") &&
	                        !isContain(queryConfig.getString(FIELDS.queryInfo.name()),"INSERT") &&
	                        !isContain(queryConfig.getString(FIELDS.queryInfo.name()),"DROP")
	                    ) {
						Workbook wb = generateWorkBook(queryConfig);
						HttpServletResponse response = (HttpServletResponse) context.get("response");
			            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			            response.setHeader("Content-Disposition", "attachment; filename=\"export_" +queryConfig.getString("queryName")+"_"+ queryConfig.getString("queryId") + ".xlsx\"");
			            wb.write(response.getOutputStream());
			            response.flushBuffer();
			            wb.close();
					}
					else {
						throw new RuntimeException("Extraction type query admit only read operations, no statement executed");
					}
				}
				else {
					List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
					String finalQuery = "";
					String queryCode = "";
				    queryCode = queryConfig.getString(E.queryCode.name());
				    finalQuery = replaceAllCond(queryConfig.getString("queryInfo"));
				    Map<String, Object> queryCodeLogParameters = UtilMisc.toMap(E.queryCode.name(), queryCode);
			        JobLogLog execStart = new JobLogLog().initLogCode(RESOURCE_LABEL, "START_QUERY_EXEC", queryCodeLogParameters, getLocale());
			        addLogInfo(execStart.getLogCode(), execStart.getLogMessage(), queryCode, RESOURCE_LABEL, execStart.getParametersJSON());
			        connection = ConnectionFactory.getConnection(this.helperInfo);
			        queryCodeLogParameters.put("finalQuery", finalQuery);
			        JobLogLog execQuery = new JobLogLog().initLogCode(RESOURCE_LABEL, "FINAL_QUERY_EXEC", queryCodeLogParameters, getLocale());
					if(queryConfig.getString(FIELDS.queryType.name()).equals(FIELDS.A.name()) || queryConfig.getString(FIELDS.queryType.name()).equals(FIELDS.S.name()))
			        {
					    addLogInfo(execQuery.getLogCode(), execQuery.getLogMessage(), queryCode, RESOURCE_LABEL, execQuery.getParametersJSON());
			            scriptRunnerExecuteQuery(queryCode , finalQuery, connection);
			        } else {
			            addLogInfo(execQuery.getLogCode(), execQuery.getLogMessage(), queryCode, RESOURCE_LABEL, execQuery.getParametersJSON());
			            stmt = connection.prepareStatement(finalQuery);
			            rs = stmt.executeQuery();
			            if(queryConfig.getString(FIELDS.queryType.name()).equals(FIELDS.R.name()))
			            {
						    while (rs.next()) {
			                    Map<String, Object> row = getGenericValue(rs);
			                    resultList.add(row);
			                }
			                getResult().put(RESULT_LIST, resultList);
			            }					
			        }
				}
			}							
			} catch (Exception e) {
	            Map<String, Object> logParameters = UtilMisc.toMap(E.errorMsg.name(), (Object) MessageUtil.getExceptionMessage(e));
	            JobLogLog errorGeneric = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR_QUERY_EXECUTOR", logParameters, getLocale());
	            addLogError(e, errorGeneric.getLogCode(), errorGeneric.getLogMessage(), null, RESOURCE_LABEL, errorGeneric.getParametersJSON());
	            getResult().putAll(ServiceUtil.returnError(errorGeneric.getLogMessage()));
			}finally {
				if(rs!=null)
					rs.close();
				if(stmt!=null)
					stmt.close();
				if(connection!=null)
					connection.close();
			}	
	}
	
	

    private boolean isContain(String source, String item) {
        String pattern = "\\b"+item+"\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source.toUpperCase());
        return m.find();
    }
	
	
	public Workbook generateWorkBook(GenericValue queryConfig) throws Exception{
		Workbook wb = new XSSFWorkbook();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		//List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		String finalQuery = "";
		String queryCode = "";
	    queryCode = queryConfig.getString(FIELDS.queryCode.name());
	    finalQuery = replaceAllCond(queryConfig.getString("queryInfo"));
	    Map<String, Object> queryCodeLogParameters = UtilMisc.toMap(FIELDS.queryCode.name(), queryCode);
        JobLogLog execStart = new JobLogLog().initLogCode(RESOURCE_LABEL, "START_QUERY_EXEC", queryCodeLogParameters, getLocale());
        addLogInfo(execStart.getLogCode(), execStart.getLogMessage(), queryCode, RESOURCE_LABEL, execStart.getParametersJSON());
        connection = ConnectionFactory.getConnection(this.helperInfo);
        queryCodeLogParameters.put("finalQuery", finalQuery);
        JobLogLog execQuery = new JobLogLog().initLogCode(RESOURCE_LABEL, "FINAL_QUERY_EXEC", queryCodeLogParameters, getLocale());
        addLogInfo(execQuery.getLogCode(), execQuery.getLogMessage(), queryCode, RESOURCE_LABEL, execQuery.getParametersJSON());
        stmt = connection.prepareStatement(finalQuery);
        rs = stmt.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
	    int maxColumn = rsmd.getColumnCount();
        Sheet sheet = wb.createSheet("ExecuteQuery");                
        // create header
        Row headerRow = sheet.createRow(0);
        for (int i = 1; i <= maxColumn; i++) {
            String columnName = rsmd.getColumnName(i);
            Cell headerCell = headerRow.createCell(i - 1);
            headerCell.setCellValue(columnName);
            formatHeader(wb,headerCell);
        }
        //create detail
        int rowCount = 1;
        while (rs.next()) {
            Row row = sheet.createRow(rowCount++);

            for (int i = 1; i <= maxColumn; i++) {
                Object valueObject = rs.getObject(i);

                Cell cell = row.createCell(i - 1);

                if (valueObject instanceof Boolean)
                    cell.setCellValue((Boolean) valueObject);
                else if (valueObject instanceof Integer)
                    cell.setCellValue((Integer) valueObject);
                else if (valueObject instanceof BigDecimal)
                    cell.setCellValue(((BigDecimal) valueObject).doubleValue());
                else if (valueObject instanceof Double)
                    cell.setCellValue((Double) valueObject);
                else if (valueObject instanceof Long)
                    cell.setCellValue((Long) valueObject);
                else if (valueObject instanceof Float)
                    cell.setCellValue((Float) valueObject);
                else if (valueObject instanceof Date) {
                    cell.setCellValue((Date) valueObject);
                    formatDateCell(wb, cell);
                } else if (valueObject instanceof Timestamp) {
                    cell.setCellValue((Timestamp) valueObject);
                    formatDateCell(wb, cell);
                } else if (valueObject instanceof Clob) {
                    cell.setCellValue(((Clob) valueObject).getSubString(1, (int)((Clob) valueObject).length()));
                }
                else cell.setCellValue((String) valueObject);
            }
        }
        //Resize width column after population data
        autoSizeColumns(wb);
		return wb;
	}
	
	
	
	private void scriptRunnerExecuteQuery(String queryCode, String finalQuery, Connection connection) {
	    ScriptRunner scriptRunner = null;
        StringWriter errorWriter = new StringWriter();
        Map<String, Object> queryCodeLogParameters = UtilMisc.toMap(FIELDS.queryCode.name(), queryCode);
        
        try {
            scriptRunner = new ScriptRunner(connection);
            scriptRunner.setStopOnError(true);
            scriptRunner.setErrorLogWriter(new PrintWriter(errorWriter));
            scriptRunner.runScript(new StringReader(finalQuery));
            JobLogLog execSuccesfull = new JobLogLog().initLogCode(RESOURCE_LABEL, "SUCCESS_QUERY_EXEC", queryCodeLogParameters, getLocale());
            addLogInfo(execSuccesfull.getLogCode(), execSuccesfull.getLogMessage(), queryCode, RESOURCE_LABEL, execSuccesfull.getParametersJSON());
        }
        catch(RuntimeSqlException e) {
            Map<String, Object> logParameters = UtilMisc.toMap(E.errorMsg.name(), e.getMessage());
            JobLogLog errorGeneric = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR_QUERY_EXECUTOR", logParameters, getLocale());
            addLogError(e, errorGeneric.getLogCode(), errorGeneric.getLogMessage(), queryCode, RESOURCE_LABEL, errorGeneric.getParametersJSON());
            getResult().putAll(ServiceUtil.returnError(errorGeneric.getLogMessage()));
        }
        catch (Exception e)
        {
            Map<String, Object> logParameters = UtilMisc.toMap(E.errorMsg.name(), (Object) MessageUtil.getExceptionMessage(e));
            JobLogLog errorGeneric = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR_QUERY_EXECUTOR", logParameters, getLocale());
            addLogError(e, errorGeneric.getLogCode(), errorGeneric.getLogMessage(), queryCode, RESOURCE_LABEL, errorGeneric.getParametersJSON());
            getResult().putAll(ServiceUtil.returnError(errorGeneric.getLogMessage()));
        }
        
    }


    public String replaceAllCond(String finalQuery) {
	    if(UtilValidate.isNotEmpty(context.get("cond0Info")))
            finalQuery = finalQuery.replaceAll("(?i)#COND0#",(String)context.get("cond0Info"));
        if(UtilValidate.isNotEmpty(context.get("cond1Info")))
            finalQuery = finalQuery.replaceAll("(?i)#COND1#",(String)context.get("cond1Info"));
        if(UtilValidate.isNotEmpty(context.get("cond2Info")))
            finalQuery = finalQuery.replaceAll("(?i)#COND2#",(String)context.get("cond2Info"));
        if(UtilValidate.isNotEmpty(context.get("cond3Info")))
            finalQuery = finalQuery.replaceAll("(?i)#COND3#",(String)context.get("cond3Info"));
        if(UtilValidate.isNotEmpty(context.get("cond4Info")))
            finalQuery = finalQuery.replaceAll("(?i)#COND4#",(String)context.get("cond4Info"));
        if(UtilValidate.isNotEmpty(context.get("cond5Info")))
            finalQuery = finalQuery.replaceAll("(?i)#COND5#",(String)context.get("cond5Info"));
        if(UtilValidate.isNotEmpty(context.get("cond6Info")))
            finalQuery = finalQuery.replaceAll("(?i)#COND6#",(String)context.get("cond6Info"));
        if(UtilValidate.isNotEmpty(context.get("cond7Info")))
            finalQuery = finalQuery.replaceAll("(?i)#COND7#",(String)context.get("cond7Info"));
        if(UtilValidate.isNotEmpty(context.get("cond8Info")))
            finalQuery = finalQuery.replaceAll("(?i)#COND8#",(String)context.get("cond8Info"));

        finalQuery = finalQuery.replaceAll("(?i)#USERID#", this.username);
        Debug.log("queryExec: "+finalQuery);
        return finalQuery;
    }


    /**
	 * Read the MESSAGE and MESSAGE_LANG
	 * @param ele
	 * @return Map<String, Object>
	 * @throws SQLException
	 */
	private Map<String, Object> getGenericValue(ResultSet ele) throws SQLException {
        Map<String, Object> genericValue = FastMap.newInstance();
        try {
            genericValue.put(MESSAGE,ele.getString(FIELDS.MESSAGE.name()));
        } catch (SQLException e) {
            genericValue.put(MESSAGE, null);
            addColumnNameLogWarn(FIELDS.MESSAGE.name());
        }
        try {
            genericValue.put(MESSAGE_LANG,ele.getString(FIELDS.MESSAGE_LANG.name()));
        } catch (SQLException e) {
            genericValue.put(MESSAGE_LANG, null);
            addColumnNameLogWarn(FIELDS.MESSAGE_LANG.name());
        }
        try {
            genericValue.put(WARNING_MESSAGE,ele.getString(FIELDS.WARNING_MESSAGE.name()));
        } catch (SQLException e) {
            genericValue.put(WARNING_MESSAGE, null);
            addColumnNameLogWarn(FIELDS.WARNING_MESSAGE.name());
        }
        try {
            genericValue.put(WARNING_MESSAGE_LANG,ele.getString(FIELDS.WARNING_MESSAGE_LANG.name()));
        } catch (SQLException e) {
            genericValue.put(WARNING_MESSAGE_LANG, null);
            addColumnNameLogWarn(FIELDS.WARNING_MESSAGE_LANG.name());
        }
        return genericValue;
	}

	 private void addColumnNameLogWarn(String columnName) {
	     Map<String, Object> columnNameLogParameters = UtilMisc.toMap(E.columnName.name(), columnName);
         JobLogLog colWarn = new JobLogLog().initLogCode(RESOURCE_LABEL, "COLUMN_WARN", columnNameLogParameters, getLocale());
         addLogWarning(colWarn.getLogCode(), colWarn.getLogMessage(), "", RESOURCE_LABEL, colWarn.getParametersJSON());
    }


    private static void formatDateCell(Workbook workbook, Cell cell) {
        CellStyle cellStyle = workbook.createCellStyle();
        CreationHelper creationHelper = workbook.getCreationHelper();
        cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        cell.setCellStyle(cellStyle);
    }

    private static void formatHeader(Workbook workbook, Cell cell) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
    }

    private static void autoSizeColumns(Workbook workbook) {
	        int numberOfSheets = workbook.getNumberOfSheets();
	        for (int i = 0; i < numberOfSheets; i++) {
	            Sheet sheet = workbook.getSheetAt(i);
	            if (sheet.getPhysicalNumberOfRows() > 0) {
	                Row row = sheet.getRow(sheet.getFirstRowNum());
	                Iterator<Cell> cellIterator = row.cellIterator();
	                while (cellIterator.hasNext()) {
	                    Cell cell = cellIterator.next();
	                    int columnIndex = cell.getColumnIndex();
	                    sheet.autoSizeColumn(columnIndex);
	                }
	            }
	        }
    }

    /** Ritorna le condizioni di ricerca del queryConfig
     * @param queryCode 
     * @return 
     * @throws GeneralException
     */
    public static List<EntityCondition> getQueryConfigConditions(String queryCode, String queryType) throws GeneralException {
        List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
        conditionList.add(EntityCondition.makeCondition(FIELDS.queryType.name(), queryType));
        conditionList.add(EntityCondition.makeCondition(FIELDS.queryActive.name(), FIELDS.Y.name()));
        conditionList.add(EntityCondition.makeCondition(FIELDS.queryCode.name(), queryCode));
        return conditionList;
    }
}
