package com.mapsengineering.base.util.importexport;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;

public class StringCellSetter implements CellValueSetter {

    @Override
    public HSSFCell setCellValue(HSSFCell cell, Object value, Map<String, Object> params) {
        cell.setCellValue(new HSSFRichTextString((String)value));
        return cell;
    }


}
