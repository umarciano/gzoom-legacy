package com.mapsengineering.base.util.importexport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.util.ExcelReaderUtil;

public class ExcelReader implements ResourceReader {

    public static final String DELEGATOR_NAME = "default";

    public static final List<String> FIELD_LIST = new ArrayList<String>(Arrays.asList(E.idMisura.name(), E.codiceIndicatore.name(), E.titoloIndicatore.name(), E.descrizioneMisura.name(), E.codiceObiettivo.name(), E.descrizioneObiettivo.name(), E.codiceUnitaOrg.name(), E.descrizioneUnitaOrg.name(), E.dataRiferimento.name(), E.valoreBudget.name(), E.valoreConsuntivo.name(), E.valoreConsuntivoAp.name(), E.valoreRisultato.name(), E.commento.name(), E.finalita.name(), E.descrizioneFinalita.name(), E.codiceFonte.name(), E.descrizioneFonte.name(), E.risultatoImportazione.name()));

    public static final Map<Integer, CellValueGetter> CELL_GETTER_MAP;

    static {
        CELL_GETTER_MAP = FastMap.newInstance();
        CELL_GETTER_MAP.put(CellType.STRING.getCode(), new StringCellGetter());
        CELL_GETTER_MAP.put(CellType.NUMERIC.getCode(), new NumericCellGetter());
    }

    private HSSFWorkbook wb;

    private String entityName;

    private Map<String, Object> context;

    private GenericDelegator delegator;

    public ExcelReader() {
        this.context = FastMap.newInstance();
    }

    public ExcelReader(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public String open(String entityName, InputStream uploadedFile) throws IOException {
        String errMessage = null;
        wb = new HSSFWorkbook(uploadedFile);
        this.entityName = entityName;
        delegator = (GenericDelegator)DelegatorFactory.getDelegator(DELEGATOR_NAME);

        if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get("delegator"))) {
            delegator = (GenericDelegator)context.get("delegator");
        }

        HSSFSheet sheet = wb.getSheetAt(0);
        if (!ExcelSheetChecker.checkSheet(sheet)) {
            errMessage = UtilProperties.getMessage("WorkeffortExtUiLabels", "Import_fileError", (Locale)context.get("locale"));
        }

        return errMessage;
    }

    @Override
    public List<GenericValue> read(EntityCondition condition) {
        List<GenericValue> gvList = new ArrayList<GenericValue>();
        HSSFSheet sheet = wb.getSheetAt(0);

        int lastRowNum = sheet.getLastRowNum();
        for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
            HSSFRow row = sheet.getRow(rowIndex);
            int lastCellNum = row.getLastCellNum();
            GenericValue gv = delegator.makeValue(entityName);
            boolean isNotEmptyRow = false;
            for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                HSSFCell cell = row.getCell(cellIndex);
                if(!ExcelReaderUtil.cellIsEmpty(cell)){
                    isNotEmptyRow = true;
                }
                String fieldName = FIELD_LIST.get(cellIndex);
                Object fieldValue = getFieldValue(cell);
                gv.set(fieldName, fieldValue);
            }
            if(!gv.isEmpty() && isNotEmptyRow) {
                gvList.add(gv);
            }
        }
        return EntityUtil.filterByCondition(gvList, condition);
    }
    
    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public OutputStream writeImportResult(String entityName, String pkColumnName, String pkValue, String resultColumnName, String result, InputStream wbStream) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(wbStream);
        HSSFSheet sheet = workbook.getSheetAt(0);
        if (sheet != null) {
            int pkColumnIndex = FIELD_LIST.indexOf(pkColumnName);
            int resultColumnIndex = FIELD_LIST.indexOf(resultColumnName);

            int lastRowNum = sheet.getLastRowNum();
            for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
                HSSFRow row = sheet.getRow(rowIndex);
                HSSFCell cell = row.getCell(pkColumnIndex);
                Object fieldValue = getFieldValue(cell);
                if (pkValue.equals(fieldValue)) {
                    row.createCell(resultColumnIndex).setCellValue(new HSSFRichTextString(result));
                }
            }
        }

        OutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return out;
    }

    private Object getFieldValue(HSSFCell cell) {
        Object fieldValue = null;
        if (cell != null) {
            CellValueGetter cellGetter = CELL_GETTER_MAP.get(cell.getCellType());
            if (UtilValidate.isNotEmpty(cellGetter)) {
                fieldValue = cellGetter.getCellValue(cell);
            }
        }
        return fieldValue;
    }

}
