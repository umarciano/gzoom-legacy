package com.mapsengineering.base.util.importexport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;

import javolution.util.FastMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

public class ExcelWriter implements ResourceWriter {

    public static final Map<Class, CellValueSetter> CELL_SETTER_MAP;

    static {
        CELL_SETTER_MAP = FastMap.newInstance();
        CELL_SETTER_MAP.put(String.class, new StringCellSetter());
        CELL_SETTER_MAP.put(Timestamp.class, new TimestampCellSetter());
        CELL_SETTER_MAP.put(BigDecimal.class, new BigDecimalCellSetter());
        CELL_SETTER_MAP.put(Double.class, new DoubleCellSetter());
    }

    private OutputStream out;

    private HSSFWorkbook workbook;

    private HSSFSheet sheet;

    private Map<String, Object> context;

    public ExcelWriter() {
        this.context = FastMap.newInstance();
    }

    public ExcelWriter(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public void open(String entityName) {
        out = new ByteArrayOutputStream();
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet(entityName);
        sheet.createFreezePane(0, 1);
    }

    @Override
    public void write(GenericValue gv) {
        Map<String, Object> toWrite = null;
        HSSFRow row = null;
        HSSFRow firstRow = sheet.getRow(0);
        if (firstRow == null) {
            row = sheet.createRow(0);
            Iterator<String> it = gv.getModelEntity().getAllFieldNames().iterator();
            int colIndex = 0;
            HSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillBackgroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
            while (it.hasNext()) {
                String title = it.next();
                HSSFCell cell = row.createCell(colIndex);
                cell.setCellStyle(headerStyle);
                cell.setCellValue(new HSSFRichTextString(title));
                sheet.setColumnWidth(colIndex, calculateColWidth(title.length() + 15));
                colIndex++;
            }
        }

        row = sheet.createRow(sheet.getLastRowNum() + 1);
        toWrite = gv.getAllFields();
        Iterator<String> it = gv.getModelEntity().getAllFieldNames().iterator();
        int colIndex = 0;
        while (it.hasNext()) {
            HSSFCell cell = row.createCell(colIndex);
            Object value = toWrite.get(it.next());

            if (UtilValidate.isNotEmpty(value)) {
                CellValueSetter cellSetter = CELL_SETTER_MAP.get(value.getClass());
                if (UtilValidate.isNotEmpty(cellSetter)) {
                    context.put(E.workbook.name(), workbook);
                    cell = cellSetter.setCellValue(cell, value, context);
                }
            }

            colIndex++;
        }
    }

    @Override
    public void close() throws IOException {
        workbook.write(out);
        if (out != null) {
            out.flush();
            out.close();
        }
    }

    @Override
    public OutputStream getStream() {
        return out;
    }

    private int calculateColWidth(int width) {
        // default to column size 1 if zero, one or negative number is passed.
        int def = 450;
        if (width > 254) {
            // Maximum allowed column width.
            return 65280;
        }
        if (width > 1) {
            int floor = (int)(Math.floor(((double)width) / 5));
            int factor = (30 * floor);
            return 450 + factor + ((width - 1) * 250);
        }

        return def;
    }

}
