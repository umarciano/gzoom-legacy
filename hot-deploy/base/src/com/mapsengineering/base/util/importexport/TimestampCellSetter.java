package com.mapsengineering.base.util.importexport;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;

public class TimestampCellSetter implements CellValueSetter {

    @Override
    public HSSFCell setCellValue(HSSFCell cell, Object value, Map<String, Object> params) {
        if (UtilValidate.isNotEmpty(params) && UtilValidate.isNotEmpty(params.get(E.locale.name())) && UtilValidate.isNotEmpty(params.get(E.workbook.name()) )) {
            HSSFWorkbook workbook = (HSSFWorkbook)params.get(E.workbook.name());
            HSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(workbook.createDataFormat().getFormat(UtilDateTime.getDateFormat((Locale)params.get("locale"))));
            cell.setCellStyle(cellStyle);
        }
        
        cell.setCellValue(new Date(((Timestamp)value).getTime()));
        return cell;
    }

}
