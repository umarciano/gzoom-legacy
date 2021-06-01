package com.mapsengineering.base.util.importexport;

import org.apache.poi.hssf.usermodel.HSSFCell;

public interface CellValueGetter {
    
    public Object getCellValue(HSSFCell cell);
}
