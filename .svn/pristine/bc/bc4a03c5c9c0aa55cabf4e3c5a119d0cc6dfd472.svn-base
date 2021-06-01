package com.mapsengineering.base.util.importexport;

import java.sql.Timestamp;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

public class NumericCellGetter implements CellValueGetter {

    @Override
    public Object getCellValue(HSSFCell cell) {
        Object fieldValue = cell.getNumericCellValue();
        if (HSSFDateUtil.isCellDateFormatted(cell)) {
            fieldValue = new Timestamp(HSSFDateUtil.getJavaDate((Double)fieldValue).getTime());
        } else {
            fieldValue = (Double)fieldValue;
        }
        return fieldValue;
    }

}
