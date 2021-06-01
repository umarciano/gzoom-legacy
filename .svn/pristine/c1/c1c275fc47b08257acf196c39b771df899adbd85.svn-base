package com.mapsengineering.base.standardimport;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.service.DispatchContext;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.BaseImportManager;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.FieldConfig;
import com.mapsengineering.base.standardimport.helper.ImportManagerUploadFileHelper;
import com.mapsengineering.base.util.ExcelReaderUtil;
import com.mapsengineering.base.util.JobLogLog;

/**
 * Import file csv without header
 *
 */
public class ImportManagerFileCsv {
	public static final String MODULE = ImportManagerFileCsv.class.getName();
	private ImportManagerUploadFile importManagerUploadFile;
	private ImportManagerUploadFileHelper importManagerUploadFileHelper;
    private Delegator delegator;
    private Map<String, Object> context;
    private FieldConfig fieldConfig;
    private Map<String, String> entityMap;
    private final String separator = ";";
    private int dataSourceIndex;
    
    /**
     * Constructor
     * @param importManagerUploadFile
     * @param importManagerUploadFileHelper
     * @param dctx
     * @param entityMap
     */
    public ImportManagerFileCsv(ImportManagerUploadFile importManagerUploadFile, ImportManagerUploadFileHelper importManagerUploadFileHelper, DispatchContext dctx, Map<String, String> entityMap) {
    	this.importManagerUploadFile = importManagerUploadFile;
    	this.importManagerUploadFileHelper = importManagerUploadFileHelper;
        this.delegator = importManagerUploadFile.getDelegator();
        this.context = importManagerUploadFile.getContext();
        this.fieldConfig = new FieldConfigService(dctx);
        this.entityMap = entityMap;
        this.dataSourceIndex = -1;
    }
    
    /**
     * importa il csv
     * @param entitiesToImport
     * @param entityName
     * @param uploadedFileName
     * @param uploadedFileRefDate 
     * @param uploadedFileDatasourceId 
     * @return
     */
    public Set<String> importCsv(Set<String> entitiesToImport, String entityName, String uploadedFileName, String uploadedFileDatasourceId, Timestamp uploadedFileRefDate) {
    	try {
    		ByteBuffer buffer = (ByteBuffer) context.get(entityName + "UploadedFile");
    		String encodingCsv = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.encoding.csv");
    		String csvFile = Charset.forName(encodingCsv).decode(buffer).toString();
    		csvFile = csvFile.replaceAll("\\r", "");
    		String[] records = csvFile.split("\\n");
        
    		// solo per la prima riga cerca il dataSource
    		if (records != null && records.length > 0) {
    			String dataSource = "";
    			if (records[0] != null) {
    				String str = records[0].trim();
    				String[] row = str.split(separator);
    				if (row != null && row.length > 0) {
    					dataSource = getDataSource(uploadedFileName, row, entityName, uploadedFileDatasourceId);
    				}
    			}
    			if (UtilValidate.isNotEmpty(dataSource)) {
    				List<GenericValue> standardImportFieldConfigList = fieldConfig.getStandardImportFieldConfigItems(dataSource);
    				if (UtilValidate.isNotEmpty(standardImportFieldConfigList)) {
    					for (GenericValue standardImportFieldConfigItem : standardImportFieldConfigList) {
    					    // one import for every entityName
    					    if (UtilValidate.isNotEmpty(standardImportFieldConfigItem)) {
    						    importManagerUploadFile.deleteKoRecordsFromInterface(this.entityMap.get(standardImportFieldConfigItem.getString(E.standardInterface.name())));
    							Map<String, String> mapField = fieldConfig.getMapField(standardImportFieldConfigItem.getString(E.standardInterface.name()), dataSource, standardImportFieldConfigItem.getLong(E.interfaceSeq.name()));
                                Map<String, Map<String, String>> mapValue = fieldConfig.getMapValue(standardImportFieldConfigItem.getString(E.standardInterface.name()), dataSource, standardImportFieldConfigItem.getLong(E.interfaceSeq.name()));
                                for (int i = 0; i < records.length; i++) {
                                	String entityInterfaceName = this.entityMap.get(standardImportFieldConfigItem.getString(E.standardInterface.name()));
                                	if (! entitiesToImport.contains(entityInterfaceName) && ! entityInterfaceName.equals(entityName)) {
                                		entitiesToImport.add(entityInterfaceName);
                                	}
                                    ModelEntity modelEntity = delegator.getModelEntity(entityInterfaceName);
                    				String str = records[i].trim();
                    				String[] row = str.split(separator);
                    				if (row != null && row.length > 0) {
                    					GenericValue element = importManagerUploadFileHelper.getElementRowCsv(row, modelEntity, mapField, mapValue, dataSource, dataSourceIndex, uploadedFileRefDate);
                                        if (!ExcelReaderUtil.isEmpty(element) && !importManagerUploadFile.elementExists(entityInterfaceName, element)) {
                                        	importManagerUploadFile.importValue(element, entityInterfaceName);
                                        }
                    				}
                                }
    						}
    					}
    				}
    			} else {
    			    // TODO  eccezione
    				throw new Exception("DataSource for entity " + entityName + " not found");
    			}
    		}
    	} catch(Exception e) {
    	    // TODO  eccezione
            String errMsg = "Import failed for file " + uploadedFileName;
            importManagerUploadFile.addLogError(e, errMsg, MODULE);
    	}

    	return entitiesToImport;	
    }
    
    /**
     * ritorna il dataSource
     * per prima cosa controllo se c'e' un record su Stfc con datasourceId = nome file
     * poi se entityName e' valorizzato, controllo se e' presente nel file 
     * e infine uso il default
     * @param row
     * @param entityName
     * @param uploadedFileDatasourceId 
     * @return
     * @throws Exception
     */
    private String getDataSource(String filename, String[] row, String entityName, String uploadedFileDatasourceId) throws Exception {
        // not search colonum with name  dataSource, because it is a csv, with no header
        
        // search dataSource from context, if exists
        GenericValue stfc = fieldConfig.getStfcDataSource(uploadedFileDatasourceId);
        if(UtilValidate.isNotEmpty(stfc)) {
            Map<String, Object> logParameters = UtilMisc.toMap(E.dataSourceId.name(), (Object) uploadedFileDatasourceId);
            JobLogLog jllDS = new JobLogLog().initLogCode(BaseImportManager.RESOURCE_LABEL, "DS_FOUND_DB", logParameters, (Locale) context.get(ServiceLogger.LOCALE));
            importManagerUploadFile.addLogDebug(jllDS.getLogCode(), jllDS.getLogMessage(), null, BaseImportManager.RESOURCE_LABEL, jllDS.getParametersJSON(), MODULE);
            return stfc.getString(E.dataSourceId.name());
        }

        // poi controllo se e' presente nel file ad una certa posizione, grazie all'entityName
        if(UtilValidate.isNotEmpty(entityName)) {
            GenericValue stfcDataSourceGenericValue = fieldConfig.getStfcDataSourceDefault(entityName);
            if (UtilValidate.isNotEmpty(stfcDataSourceGenericValue)) {
                String externalFieldName = stfcDataSourceGenericValue.getString(E.externalFieldName.name());
                if (UtilValidate.isNotEmpty(externalFieldName)) {
                    int i = -1;
                    try {
                        i = Integer.parseInt(externalFieldName);
                    } catch(Exception e) {
                        i = -1;
                    }
                    if (i > 0 && i <= row.length) {
                        dataSourceIndex = i - 1;
                        String dataSource = row[i - 1];
                        if (UtilValidate.isNotEmpty(dataSource) && dataSource.startsWith("\"")) {
                            dataSource = dataSource.substring(1, dataSource.length()-1);
                        }
                        Map<String, Object> logParameters = UtilMisc.toMap(E.dataSourceId.name(), (Object) dataSource, E.entityName.name(), entityName, E.externalFieldName.name(), externalFieldName);
                        JobLogLog jllDS = new JobLogLog().initLogCode(BaseImportManager.RESOURCE_LABEL, "DS_FOUND_FILE", logParameters, (Locale) context.get(ServiceLogger.LOCALE));
                        importManagerUploadFile.addLogDebug(jllDS.getLogCode(), jllDS.getLogMessage(), null, BaseImportManager.RESOURCE_LABEL, jllDS.getParametersJSON(), MODULE);
                        return dataSource;
                    }
                }
                // e infine uso il default
                Map<String, Object> logParameters = UtilMisc.toMap(E.dataSourceId.name(), (Object) stfcDataSourceGenericValue.getString(E.defaultValue.name()));
                JobLogLog jllDS = new JobLogLog().initLogCode(BaseImportManager.RESOURCE_LABEL, "DS_FOUND_DEF", logParameters, (Locale) context.get(ServiceLogger.LOCALE));
                importManagerUploadFile.addLogDebug(jllDS.getLogCode(), jllDS.getLogMessage(), null, BaseImportManager.RESOURCE_LABEL, jllDS.getParametersJSON(), MODULE);
                return stfcDataSourceGenericValue.getString(E.defaultValue.name());
            }
        }
        
    	return "";
    }
}
