package com.mapsengineering.base.test;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import com.mapsengineering.base.util.importexport.StringCellGetter;
import com.mapsengineering.base.util.importexport.StringCellSetter;

public class TestStringCell extends BaseTestCase {
    
    private HSSFCell cell;
    
    private StringCellGetter getter;
    
    protected void setUp() throws Exception {
        super.setUp();
        cell =  new HSSFWorkbook().createSheet().createRow(0).createCell(0, CellType.STRING);
        getter = new StringCellGetter();
    }

    public void testSetCellValue() {
        String expected = "String to write";
        StringCellSetter setter = new StringCellSetter();
        HSSFCell resultCell = setter.setCellValue(cell, expected, null);
        assertNotNull(resultCell);
        assertEquals(expected, (String)getter.getCellValue(resultCell));
    }
}
