package com.mapsengineering.base.util.importexport;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;

public class DoubleCellSetter implements CellValueSetter {

    @Override
    public HSSFCell setCellValue(HSSFCell cell, Object value, Map<String, Object> params) {
        cell.setCellValue((Double)value);
        return cell;
    }

}
