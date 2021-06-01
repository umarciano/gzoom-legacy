package com.mapsengineering.base.util.importexport;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;

public interface CellValueSetter {
    
    public HSSFCell setCellValue(HSSFCell cell, Object value, Map<String, Object> params);
}
