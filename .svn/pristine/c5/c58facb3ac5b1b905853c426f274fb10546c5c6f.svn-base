package com.mapsengineering.base.util.importexport;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;

public class BigDecimalCellSetter implements CellValueSetter {

    @Override
    public HSSFCell setCellValue(HSSFCell cell, Object value, Map<String, Object> params) {
        cell.setCellValue(((BigDecimal)value).doubleValue());
        return cell;
    }

}
