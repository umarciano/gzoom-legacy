package org.ofbiz.widget.poi;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class PoiHssfStyleCatalog {

    private final HSSFWorkbook workbook;
    private final Map<String, PoiHssfRuntimeStyle> styleMap = FastMap.newInstance();
    private final Map<String, HSSFCellStyle> builtStyleMap = FastMap.newInstance();

    public PoiHssfStyleCatalog(HSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public PoiHssfRuntimeStyle get(String name) {
        return styleMap.get(name);
    }

    public void put(PoiHssfRuntimeStyle style) {
        styleMap.put(style.getName(), style);
    }

    public void clearBuilt() {
        builtStyleMap.clear();
    }

    public HSSFCellStyle getBuilt(String name) {
        HSSFCellStyle style = builtStyleMap.get(name);
        if (style == null) {
            style = workbook.createCellStyle();
            List<String> srcNames = PoiHssfRuntimeStyle.getStyleNameList(name);
            for (String srcName : srcNames) {
                PoiHssfRuntimeStyle srcStyle = styleMap.get(srcName);
                if (srcStyle != null) {
                    try {
                        srcStyle.build(style);
                    } catch (Exception e) {
                        throw new RuntimeException("Error building POI HSSF style", e);
                    }
                }
            }
            builtStyleMap.put(name, style);
        }
        return style;
    }
}
