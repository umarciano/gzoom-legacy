package org.ofbiz.widget.poi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilValidate;

public class PoiHssfContext {

    private final HSSFWorkbook workbook;
    private final PoiHssfStyleCatalog styleCatalog;
    private final Map<String, Boolean> skipMap;
    private boolean initialized;

    public PoiHssfContext() {
        workbook = new HSSFWorkbook();
        styleCatalog = new PoiHssfStyleCatalog(workbook);
        skipMap = new HashMap<String, Boolean>();
        initialized = false;
    }

    public HSSFWorkbook getWorkbook() {
        return this.workbook;
    }

    public PoiHssfStyleCatalog getStyleCatalog() {
        return this.styleCatalog;
    }

    public void initFromContext(Map<String, Object> context) {
        if (!initialized) {
            Map<String, Object> parameters = UtilGenerics.toMap(context.get("parameters"));
            if (parameters != null) {
                List<String> skipList = UtilGenerics.toList(parameters.get("poiHssfFormRenderer_skipList"));
                if (skipList != null) {
                    for (String skipItem : skipList) {
                        skipMap.put(skipItem, Boolean.TRUE);
                    }
                }
            }
            initialized = true;
        }
    }

    public boolean isToSkip(String id) {
        return skipMap.get(id) == Boolean.TRUE;
    }

    public HSSFSheet addSheet(String sheetName) {
        return addSheet(sheetName, null);
    }

    public HSSFSheet addSheet(String sheetName, HSSFSheet lastSheet) {
        HSSFSheet sheet = lastSheet;
        if (sheet == null && workbook.getNumberOfSheets() > 0) {
            // set name for default sheet
            sheet = workbook.getSheetAt(0);
            if (!UtilValidate.isEmpty(sheetName))
                workbook.setSheetName(0, sheetName);
        } else {
            // create a new sheet
            if (!UtilValidate.isEmpty(sheetName))
                sheet = workbook.createSheet(sheetName);
            else
                sheet = workbook.createSheet();
        }
        return sheet;
    }

    public int getLastRowNum(HSSFSheet sheet) {
        int i = sheet.getLastRowNum();
        if (i > 0)
            return i;
        if (sheet.getPhysicalNumberOfRows() > 0)
            return 0;
        return -1;
    }

    public HSSFRow addRow(HSSFSheet sheet) {
        int lastRowNum = getLastRowNum(sheet);
        return sheet.createRow(lastRowNum + 1);
    }

    public SortedSet<Integer> getColumnIndexes(HSSFSheet sheet) {
        SortedSet<Integer> columns = new TreeSet<Integer>();
        for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();) {
            Row row = rit.next();
            for (Iterator<Cell> cit = row.cellIterator(); cit.hasNext();) {
                Cell cell = cit.next();
                int idx = cell.getColumnIndex();
                columns.add(idx);
            }
        }
        return columns;
    }

    public void autoSizeColumns(HSSFSheet sheet, boolean useMergedCells) {
        Set<Integer> columns = getColumnIndexes(sheet);
        for (Iterator<Integer> it = columns.iterator(); it.hasNext();) {
            int colNum = it.next();
            sheet.autoSizeColumn(colNum, useMergedCells);
        }
    }

    public void setRowStyle(HSSFRow row, String styleName) {
        if (UtilValidate.isNotEmpty(styleName)) {
            HSSFCellStyle style = styleCatalog.getBuilt(styleName);
            row.setRowStyle(style);
        }
    }

    public void setColStyle(HSSFSheet sheet, int colNum, String styleName) {
        if (UtilValidate.isNotEmpty(styleName)) {
            HSSFCellStyle style = styleCatalog.getBuilt(styleName);
            sheet.setDefaultColumnStyle(colNum, style);
        }
    }

    public void setCellStyle(HSSFCell cell, String styleName) {
        if (UtilValidate.isNotEmpty(styleName)) {
            HSSFCellStyle style = styleCatalog.getBuilt(styleName);
            cell.setCellStyle(style);
        }
    }
}
