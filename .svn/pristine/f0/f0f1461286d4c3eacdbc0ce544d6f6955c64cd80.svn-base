package com.mapsengineering.base.test;

import java.math.BigDecimal;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import com.mapsengineering.base.util.importexport.BigDecimalCellSetter;
import com.mapsengineering.base.util.importexport.NumericCellGetter;

public class TestBigDecimalCell extends BaseTestCase {
    
    private HSSFCell cell;
    
    private NumericCellGetter getter;
    
    protected void setUp() throws Exception {
        super.setUp();
        cell =  new HSSFWorkbook().createSheet().createRow(0).createCell(0, CellType.NUMERIC);
        getter = new NumericCellGetter();
    }

    public void testSetCellValue() {
        BigDecimal expected = new BigDecimal(5);
        
        BigDecimalCellSetter setter = new BigDecimalCellSetter();
        HSSFCell resultCell = setter.setCellValue(cell, expected, null);
        assertNotNull(resultCell);
        assertEquals(expected.doubleValue(), (Double)getter.getCellValue(cell));
    }
}
