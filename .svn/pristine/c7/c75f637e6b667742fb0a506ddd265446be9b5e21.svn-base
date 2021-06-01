package com.mapsengineering.base.test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import javolution.util.FastMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.ofbiz.base.util.UtilDateTime;

import com.mapsengineering.base.util.importexport.E;
import com.mapsengineering.base.util.importexport.NumericCellGetter;
import com.mapsengineering.base.util.importexport.TimestampCellSetter;

public class TestTimestampCell extends BaseTestCase {

    private Map<String, Object> params;
    
    private HSSFCell cell;
    
    private NumericCellGetter getter;

    protected void setUp() throws Exception {
        super.setUp();
        
        HSSFWorkbook workbook = new HSSFWorkbook(); 
        cell = workbook.createSheet().createRow(0).createCell(0);
        getter = new NumericCellGetter();
        
        params = FastMap.newInstance();
        params.put(E.locale.name(), context.get(BaseTestCase.LOCALE));
        params.put(E.workbook.name(), workbook);
    }

    public void testSetCellValue() {
        Timestamp expected = UtilDateTime.nowTimestamp();

        TimestampCellSetter setter = new TimestampCellSetter();
        HSSFCell resultCell = setter.setCellValue(cell, expected, params);
        assertNotNull(resultCell);
        assertEquals(expected.getTime(), ((Date)getter.getCellValue(resultCell)).getTime());
    }
}
