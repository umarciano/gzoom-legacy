package com.mapsengineering.base.standardimport.helper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.FieldConfigService;
import com.mapsengineering.base.standardimport.ImportManagerUploadFile;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.FieldConfig;
import com.mapsengineering.base.util.ExcelReaderUtil;
import com.mapsengineering.base.util.ValidationUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * Helper for UploadFile
 *
 */
public class ImportManagerUploadFileHelper {

    private Delegator delegator;
    private LocalDispatcher dispatcher;
    private Map<String, Object> context;
    private FieldConfig fieldConfig;

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public ImportManagerUploadFileHelper(DispatchContext dctx, Map<String, Object> context) {
        this.delegator = dctx.getDelegator();
        this.dispatcher = dctx.getDispatcher();
        this.context = context;
        this.fieldConfig = new FieldConfigService(dctx);
    }
    
    /**
     * Definisco l'elemento da inserire data una row dal file 
     * @param rowHeader
     * @param row
     * @param modelEntity
     * @param mapField
     * @param mapValue
     * @param dataSource
     * @param uploadedFileRefDate 
     * @return
     * @throws Exception
     */
    public GenericValue getElementRow(Row rowHeader, Row row, ModelEntity modelEntity,
                                      Map<String, String> mapField,
                                      Map<String, Map<String, String>> mapValue, 
                                      String dataSource, Timestamp uploadedFileRefDate) throws Exception { 	
        GenericValue element = GenericValue.create(modelEntity);
        element = setMandatoryFields(modelEntity, element);
        
        //For each row, iterate through each columns
        Iterator<Cell> cellIterator = rowHeader.cellIterator();
        // contains field on excel with value
        Set<String> excelFields = new HashSet<String>();
        
        // set refDate, value in excel will override this
        if(UtilValidate.isNotEmpty(uploadedFileRefDate)) {
            element.set(E.refDate.name(), uploadedFileRefDate);
            excelFields.add(E.refDate.name());
        }
        // iterate on file excel
        while (cellIterator.hasNext()) {
            Cell cellHeader = cellIterator.next();
            if (!ExcelReaderUtil.cellIsEmpty(cellHeader)) {
                String fieldName = cellHeader.toString();
                String externalFieldName = fieldName;
                if (UtilValidate.isNotEmpty(mapField)) {
                	int count = 0;
                	try {
                		count = Integer.parseInt(mapField.get("###count###")); 
                	} catch(Exception e) {
                		count = 0;
                	}
                	boolean contains = false;
                    for (int i = 1; i <= count; i++) {
                		if (mapField.containsKey(fieldConfig.getKey(dataSource, i, externalFieldName))) {
                			contains = true;
                			String fieldConfigValue = mapField.get(fieldConfig.getKey(dataSource, i, externalFieldName));
                            fieldName = UtilValidate.isNotEmpty(fieldConfigValue) ? fieldConfigValue : fieldName;
                            excelFields.add(fieldName);                           
                            element = setFieldElement(element, fieldName, row, cellHeader, modelEntity);
                		}
                	}
                    if (! contains) {
                        excelFields.add(fieldName);                     
                        element = setFieldElement(element, fieldName, row, cellHeader, modelEntity);                   	
                    }
                } else {
                    excelFields.add(fieldName);                
                    element = setFieldElement(element, fieldName, row, cellHeader, modelEntity);
                }
            }
        }
        
        // ricerca il valore di default per tutte le colonne con valore vuoto oppure non presenti nell'excel
        if(UtilValidate.isNotEmpty(mapValue) &&	mapValue.containsKey(dataSource)){
            Map<String, String> defaults = mapValue.get(dataSource);
            if (UtilValidate.isNotEmpty(defaults)) {
                Set<Entry<String,String>> mapNames = defaults.entrySet();
                for (Entry<String, String> mapName : mapNames) {
                    String columnValue = mapName.getValue();
                    String columnName = mapName.getKey();
                    String columnType = getValidColumnType(modelEntity, columnName);
                    if(((element.containsKey(columnName) && UtilValidate.isEmpty(element.get(columnName))) || !element.containsKey(columnName) || !excelFields.contains(columnName)) 
                            && UtilValidate.isNotEmpty(columnType)){
                        element = setFieldStringElement(element, columnValue.contains(FlexibleStringExpander.openBracket) ? 
                                FlexibleStringExpander.expandString(columnValue, element.getAllFields()) : columnValue, 
                                columnName, columnType, modelEntity);
                    }
                }
            }
        }

        element.set(E.dataSource.name(), dataSource);
        
        return element;
    }
    
    private GenericValue setFieldElement(GenericValue element, String fieldName, Row row, Cell cellHeader, ModelEntity modelEntity) throws Exception {
        Cell cell = row.getCell(cellHeader.getColumnIndex());
        String columnType = getValidColumnType(modelEntity, fieldName);
        if(UtilValidate.isNotEmpty(columnType)){
            element = setFieldElement(element, cell, fieldName, columnType, modelEntity);
        }
        return element;
    }

    /**
     * Verifico che il field abbia il tipo corretto
     * @param modelEntity
     * @param fieldName
     * @return
     * @throws Exception
     */
    private String getValidColumnType(ModelEntity modelEntity, String fieldName) throws Exception{
        ModelField modelField = modelEntity.getField(fieldName);
        if (modelField == null) {
        	// GN-3526: le colonne non presenti nell'entita' devono essere saltate
            return null;
        } 
        return modelField.getType();
    }

    /**
     * In questo metodo viene settato il field della cella, con il field dell'entity da caricare
     * @param element
     * @param cell
     * @param fieldName
     * @param columnType
     * @return
     * @throws Exception
     */
    private GenericValue setFieldElement(GenericValue element, Cell cell, String fieldName,
                                         String columnType, ModelEntity modelEntity) throws Exception {
        try {
            if (ExcelReaderUtil.cellIsEmpty(cell)) {
        	    if(isMandatoryFields(modelEntity, fieldName)) {
        	        return element;
        	    } else {
        	        element.set(fieldName, null);
        	    }
        	} else if (columnType.equals("date-time")) {
                element.set(fieldName, UtilDateTime.toTimestamp(getCellDateValue(cell)));
            } else if (columnType.equals("data")) {
                element.set(fieldName, new java.sql.Date(getCellDateValue(cell).getTime()));
            } else if (columnType.equals("time")) {
                element.set(fieldName, new java.sql.Time(getCellDateValue(cell).getTime()));
            } else if (columnType.equals("currency-amount") || columnType.equals("currency-precise") || columnType.equals("fixed-point")) {
                element.set(fieldName, BigDecimal.valueOf(cell.getNumericCellValue()));
            } else if (columnType.equals("floating-point")) {
                element.set(fieldName, cell.getNumericCellValue());
            } else if(columnType.equals("numeric")) {
            	element.set(fieldName, Double.valueOf(cell.getNumericCellValue()).longValue());
            } else {
                element.set(fieldName, getCellStringValue(cell));
            }
        } catch (Exception e) {

            String errMsg = "Error during conversion for " + fieldName + " with value " + String.valueOf(cell) + " : " + e.getMessage();
            throw new Exception(errMsg, e);

        }
        return element;
    }
    
    /**
     * In questo metodo viene settato il field della cella, con il field dell'entity da caricare
     * @param element
     * @param fieldValue
     * @param fieldName
     * @param columnType
     * @return
     * @throws Exception
     */
    private GenericValue setFieldStringElement(GenericValue element, String fieldValue, String fieldName,
                                         String columnType, ModelEntity modelEntity) throws Exception {
        try {
        	if(isMandatoryFields(modelEntity, fieldName) && UtilValidate.isEmpty(fieldValue)) {
    	        return element;
    	    }
            
        	if (UtilValidate.isEmpty(fieldValue)) {
        	    element.set(fieldName, null);
        	} else if (columnType.equals("date-time")) {
                element.set(fieldName, UtilDateTime.toTimestamp(ValidationUtil.checkDateAgainstLocale(fieldValue, (Locale)context.get("locale"))));
            } else if (columnType.equals("data")) {
                element.set(fieldName, new java.sql.Date(ValidationUtil.checkDateAgainstLocale(fieldValue, (Locale)context.get("locale")).getTime()));
            } else if (columnType.equals("time")) {
                element.set(fieldName, new java.sql.Time(ValidationUtil.checkDateAgainstLocale(fieldValue, (Locale)context.get("locale")).getTime()));
            } else if (columnType.equals("currency-amount") || columnType.equals("currency-precise") || columnType.equals("fixed-point")) {
                element.set(fieldName, new BigDecimal(fieldValue));
            } else if (columnType.equals("floating-point")) {
                element.set(fieldName, new Double(fieldValue));
            } else if(columnType.equals("numeric")) {
            	element.set(fieldName, new Double(fieldValue).longValue());
            } else {
                element.set(fieldName, fieldValue);
            }
        } catch (Exception e) {

            String errMsg = "Error during conversion for " + fieldName + " with value " + fieldValue + " : " + e.getMessage();
            throw new Exception(errMsg, e);

        }
        return element;
    }

    /**
     * Workaround per recuperare il corretto valore della cella in formato stringa - problema aggiunta .0 ad interi numerici
     * @param cell
     * @return
     */
    private String getCellStringValue(Cell cell) {
        String value;
        if (CellType.NUMERIC == cell.getCellType()) {
            DecimalFormat df = new DecimalFormat("#.##########");
            if (cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) {
                df = new DecimalFormat("#");
            }
            value = df.format(cell.getNumericCellValue());
        } else {
            value = cell.getRichStringCellValue() != null ? cell.getRichStringCellValue().getString().trim() : cell.toString().trim();
        }
        return value;
    }
    
    /**
     * Recupera la Data associata ad una cella
     * 
     * @param cell
     * @return
     */
    private Date getCellDateValue(Cell cell){
    	if(CellType.STRING == cell.getCellType()){
    		return ValidationUtil.checkDateAgainstLocale(cell.getStringCellValue(), (Locale)context.get("locale"));
    	} 
    	
    	return cell.getDateCellValue();
    }

    /**
     * Verifico se ci sono ETL da eseguire
     * @param entityName
     * @param checkFromETL
     * @return
     * @throws GenericEntityException
     */
    public boolean checkFromETL(String entityName, String checkFromETL) throws GenericEntityException {
        if ("fromETL".equals(checkFromETL)) {
            List<GenericValue> listEtl = delegator.findList(E.Enumeration.name(), EntityCondition.makeCondition(EntityCondition.makeCondition(E.enumTypeId.name(), "ETL"), EntityCondition.makeCondition(E.enumCode.name(), entityName)), null, null, null, false);
            if (UtilValidate.isNotEmpty(listEtl)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Restituisco la lista di ETL se esistono 
     * @param checkFromETL
     * @return
     * @throws GenericEntityException
     */
    public List<GenericValue> checkFromAllETL(String checkFromETL) throws GenericEntityException {
        if ("fromETL".equals(checkFromETL)) {
            return delegator.findList(E.Enumeration.name(), EntityCondition.makeCondition(E.enumTypeId.name(), "ETL"), null, null, null, false);
        }
        return null;
    }

    /**
     * Esegue il servixio di StandardImport
     * @return
     * @throws GeneralException
     */
    public Map<String, Object> runStandardImport() throws GeneralException {

        /** Alla fine chiamo importazione standard */
        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put(E.entityListToImport.name(), context.get(E.entityListToImport.name()));
        serviceMap.put(E.filterMapList.name(), context.get(E.filterMapList.name()));
        serviceMap.put(E.filterConditions.name(), context.get(E.filterConditions.name()));
        serviceMap.put(E.checkFromETL.name(), context.get(E.checkFromETL.name()));
        serviceMap.put(E.checkEndYearElab.name(), context.get(E.checkEndYearElab.name()));
        serviceMap.put(E.deletePrevious.name(), context.get(E.deletePrevious.name()));
        serviceMap.put(E.defaultOrganizationPartyId.name(), context.get(E.defaultOrganizationPartyId.name()));
        serviceMap.put("userLogin", context.get("userLogin"));
        serviceMap.put("locale", context.get("locale"));
        serviceMap.put("timeZone", context.get("timeZone"));
        serviceMap.put(ServiceLogger.SESSION_ID, context.get(ServiceLogger.SESSION_ID));

        return dispatcher.runSync("standardImport", serviceMap);
    }
    
    /**
     * Inserisce i campi obbligatori a prescindere se siano presenti o meno nel file excel da importare.
     * Questi campi hanno di solito valore _NA_
     * @param modelEntity
     * @param element
     * @return
     * @throws GenericEntityException
     */
    private GenericValue setMandatoryFields(ModelEntity modelEntity, GenericValue element) throws GenericEntityException {
       
        String nameEnumType = "DEFAULT_" + StringUtils.upperCase(modelEntity.getEntityName());
        nameEnumType = nameEnumType.substring(0, nameEnumType.length() > ServiceLogger.MAX_LENGHT_STRING ? ServiceLogger.MAX_LENGHT_STRING : nameEnumType.length());
        
        List<GenericValue> enumerationList = delegator.findList(E.Enumeration.name(), EntityCondition.makeCondition(EntityCondition.makeCondition(E.enumTypeId.name(), nameEnumType)), null, null, null, false);
        for(GenericValue gv: enumerationList){
            element.set(gv.get(E.enumCode.name()).toString(), gv.get(E.description.name()));
        }
        return element;
    }
    
    /**
     * Inserisce i campi obbligatori a prescindere se siano presenti o meno nel file excel da importare.
     * @param modelEntity
     * @param element
     * @return
     * @throws GenericEntityException
     */
    private boolean isMandatoryFields(ModelEntity modelEntity, String fieldName) throws GenericEntityException {
       
        String nameEnumType = "DEFAULT_" + StringUtils.upperCase(modelEntity.getEntityName());
        nameEnumType = nameEnumType.substring(0, nameEnumType.length() > ServiceLogger.MAX_LENGHT_STRING ? ServiceLogger.MAX_LENGHT_STRING : nameEnumType.length());
        
        List<EntityCondition> conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition(E.enumCode.name(), fieldName));
        conditionList.add(EntityCondition.makeCondition(E.enumTypeId.name(), nameEnumType));
        
        EntityCondition condition = EntityCondition.makeCondition(conditionList);
        
        List<GenericValue> enumerationList = delegator.findList(E.Enumeration.name(), condition, null, null, null, false);
        if (UtilValidate.isEmpty(enumerationList)) {
            return false;
        }
        return true;
    }
    
    /**
     * trasforma la riga csv in GenericValue
     * @param row
     * @param modelEntity
     * @param mapField
     * @param mapValue
     * @param dataSource
     * @param dataSourceIndex
     * @param uploadedFileRefDate 
     * @return
     * @throws Exception
     */
    public GenericValue getElementRowCsv(String[] row, ModelEntity modelEntity,
            Map<String, String> mapField,
            Map<String, Map<String, String>> mapValue, 
            String dataSource, int dataSourceIndex, Timestamp uploadedFileRefDate) throws Exception {
        GenericValue element = GenericValue.create(modelEntity);
        element = setMandatoryFields(modelEntity, element);
        
        Set<String> fields = new HashSet<String>();
        // in csv there is not header, so set refDate, and then the row in the file can override it.
        if(UtilValidate.isNotEmpty(uploadedFileRefDate)) {
            element.set(E.refDate.name(), uploadedFileRefDate);
            fields.add(E.refDate.name());
        }
        
        for (int j = 0; j < row.length; j++) {
        	if (j != dataSourceIndex) {
                String externalFieldName = Integer.toString(j+1);
                if (UtilValidate.isNotEmpty(mapField)) {
                	int count = 0;
                	try {
                		count = Integer.parseInt(mapField.get("###count###")); 
                	} catch(Exception e) {
                		count = 0;
                	}
                	for (int k = 1; k <= count; k++) {
                        if (mapField.containsKey(fieldConfig.getKey(dataSource, k, externalFieldName))) {
                			String fieldName = mapField.get(fieldConfig.getKey(dataSource, k, externalFieldName));
                            if (UtilValidate.isNotEmpty(fieldName)) {
                        		fields.add(fieldName);
                                String columnType = getValidColumnType(modelEntity, fieldName);
                                if(UtilValidate.isNotEmpty(columnType)){
                    				String val = row[j];
                                    if (UtilValidate.isNotEmpty(val) && val.startsWith("\"")) {
                    					val = val.substring(1, val.length()-1);
                    				}
                                    element = setFieldStringElement(element, val, fieldName, columnType, modelEntity);
                                }
                        	}
                            break;
                		}
                	}
                }
        	}
        }
        
        if(UtilValidate.isNotEmpty(mapValue) &&	mapValue.containsKey(dataSource)){
        	Map<String, String> defaults = mapValue.get(dataSource);
        	if (UtilValidate.isNotEmpty(defaults)) {
        		Set<Entry<String, String>> mapNames = defaults.entrySet();
                for (Entry<String, String> mapName : mapNames) {
                    String columnValue = mapName.getValue();
                    String columnName = mapName.getKey();
                    String columnType = getValidColumnType(modelEntity, columnName);
                    if(((element.containsKey(columnName ) && UtilValidate.isEmpty(element.get(columnName))) || !element.containsKey(columnName) || !fields.contains(columnName)) 
    	        			&& UtilValidate.isNotEmpty(columnType)){
    	        		element = setFieldStringElement(element, columnValue.contains(FlexibleStringExpander.openBracket) ? 
    	        				FlexibleStringExpander.expandString(columnValue, element.getAllFields()) : columnValue, 
    	        				columnName, columnType, modelEntity);               
    	        	}
        		}
        	}
        }
        element.setString(E.dataSource.name(), dataSource);
        return element;
    }
    
    /**
     * Copia gli elementi della tabella entityName + "Ext" nella tabella entityName
     * 
     * @param entityName
     * @throws GenericEntityException
     */
    public void createInterfaceValueFromExt(String entityName) throws GenericEntityException {
    	
    	List<GenericValue> entityExtList = delegator.findList(entityName + ImportManagerUploadFile.EXT, null, null, null, null, false);
    	
    	if (UtilValidate.isNotEmpty(entityExtList)) {
    		for (GenericValue gvExt: entityExtList) {
    			GenericValue gv = delegator.makeValue(entityName, gvExt);
    			gv.create();
    			
    			gvExt.remove();
    		}
    	}
    }
}
