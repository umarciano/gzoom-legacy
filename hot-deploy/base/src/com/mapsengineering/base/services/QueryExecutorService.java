package com.mapsengineering.base.services;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;

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
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.MessageUtil;

public class QueryExecutorService extends GenericServiceLoop {
	
    private GenericHelperInfo helperInfo;
    private String queryId;
    private final String username;
    private static final String ENTITYNAME = "QueryConfig";
    public static final String DEFAULT_GROUP_NAME = "org.ofbiz";
    public static final String MODULE = QueryExecutorService.class.getName();

    private static final String SERVICE_NAME = "QueryExecutorService";
    private static final String SERVICE_TYPE_ID = "QUERY_EXECUTOR";
    private static final String RESOURCE_LABEL = "BaseUiLabels";
    
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



	@Override
	protected void execute() throws Exception {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
				
		String finalQuery = "";
		
		try {
				GenericValue queryConfig = delegator.findOne(ENTITYNAME, false, UtilMisc.toMap("queryId", this.queryId));
				
				if(queryConfig!=null)
				{
				    finalQuery = queryConfig.getString("queryInfo");
				    
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
		            
					connection = ConnectionFactory.getConnection(this.helperInfo);
					
					if(queryConfig.getString("queryType").equals("E"))
					{
						Debug.log("queryExec: "+finalQuery);
					    stmt = connection.prepareStatement(finalQuery);
					    rs = stmt.executeQuery();
					    ResultSetMetaData rsmd = rs.getMetaData();
					    int maxColumn = rsmd.getColumnCount();
					    
					    Workbook wb = new XSSFWorkbook();
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
		                        } else cell.setCellValue((String) valueObject);

		                    }

		                }
		                
		                //Resize width column after population data
		                autoSizeColumns(wb);
		                
		                HttpServletResponse response = (HttpServletResponse) context.get("response");
		                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		                response.setHeader("Content-Disposition", "attachment; filename=\"export_" +queryConfig.getString("queryName")+"_"+ queryConfig.getString("queryId") + ".xlsx\"");
		                wb.write(response.getOutputStream());
		                response.flushBuffer();
		                
		                
		                wb.close();
					}
					else {
						ScriptRunner scriptRunner = null;
		                StringWriter errorWriter = new StringWriter();
		                
		                try {
		                    scriptRunner = new ScriptRunner(connection);
		                    scriptRunner.setStopOnError(true);
		                    scriptRunner.setErrorLogWriter(new PrintWriter(errorWriter));
		                    scriptRunner.runScript(new StringReader(finalQuery));

		                }
		                catch(RuntimeSqlException e) {
		    	            Map<String, Object> logParameters = UtilMisc.toMap(E.errorMsg.name(), e.getMessage());
		    	            JobLogLog errorGeneric = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR_QUERY_EXECUTOR", logParameters, getLocale());
		    	            addLogError(e, errorGeneric.getLogCode(), errorGeneric.getLogMessage(), null, RESOURCE_LABEL, errorGeneric.getParametersJSON());
		    	            getResult().putAll(ServiceUtil.returnError(errorGeneric.getLogMessage()));
		                }
		                catch (Exception e)
		                {
		                	
		    	            Map<String, Object> logParameters = UtilMisc.toMap(E.errorMsg.name(), (Object) MessageUtil.getExceptionMessage(e));
		    	            JobLogLog errorGeneric = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR_QUERY_EXECUTOR", logParameters, getLocale());
		    	            addLogError(e, errorGeneric.getLogCode(), errorGeneric.getLogMessage(), null, RESOURCE_LABEL, errorGeneric.getParametersJSON());
		    	            getResult().putAll(ServiceUtil.returnError(errorGeneric.getLogMessage()));
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


}
