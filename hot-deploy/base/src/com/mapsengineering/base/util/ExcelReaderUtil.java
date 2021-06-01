package com.mapsengineering.base.util;

import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

public class ExcelReaderUtil {

    /**
     * Verifica che il genericValue passato non ha tutti campi vuoti o null
     * @param row
     * @return
     */
    public static boolean isEmpty(Row row) {
        boolean isEmpty = true;

        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cellHeader = cellIterator.next();
            if (!cellIsEmpty(cellHeader)) {
                isEmpty = false;
            }
        }
        return isEmpty;
    }

    /**
     * Verifica che il contenuto della cella del file excel sia vuoto o null
     * @param cell
     * @return
     */
    public static boolean cellIsEmpty(Cell cell) {
        boolean cellIsEmpty = false;

        if (UtilValidate.isEmpty(cell) || UtilValidate.isEmpty(cell.toString().trim())) {
            cellIsEmpty = true;
        }
        return cellIsEmpty;
    }

    /**
     * Verifica che il genericValue passato non ha tutti campi vuoti o null
     * @param row
     * @return
     */
    public static boolean isEmpty(GenericValue value) {
        boolean isEmpty = true;

        Map<String, Object> mappa = value.getAllFields();
        for (Map.Entry<String, ? extends Object> item : mappa.entrySet()) {
            if (UtilValidate.isNotEmpty(item.getValue())) {
                isEmpty = false;
            }
        }
        return isEmpty;
    }

}
