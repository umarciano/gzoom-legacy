package com.mapsengineering.base.util.importexport;

import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.ofbiz.base.util.UtilValidate;

public final class ExcelSheetChecker {

    public static final boolean checkSheet(HSSFSheet sheet) {
        boolean res = false;
        if (UtilValidate.isNotEmpty(sheet)) {
            HSSFRow firstRow = sheet.getRow(0);
            if (firstRow != null) {
                res = true;
                Iterator<Cell> cellIt = firstRow.cellIterator();
                int i = 0;
                while (cellIt.hasNext() && res) {
                    Cell cell = cellIt.next();
                    res = ExcelReader.FIELD_LIST.get(i).equals(cell.getRichStringCellValue().toString());
                    i++;
                }
            }
        }
        return res;
    }
}
