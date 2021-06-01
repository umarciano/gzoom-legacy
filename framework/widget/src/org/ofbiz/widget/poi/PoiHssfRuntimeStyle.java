package org.ofbiz.widget.poi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;

public class PoiHssfRuntimeStyle {

    private static final List<String> EMPTY_LIST = new ArrayList<String>();

    private final HSSFWorkbook workbook;
    private final HSSFDataFormat dataFormat;
    private final Map<String, Object> attributes;
    private final String name;

    public PoiHssfRuntimeStyle(HSSFWorkbook workbook, String name) {
        this.workbook = workbook;
        this.dataFormat = this.workbook.createDataFormat();
        this.attributes = FastMap.newInstance();
        this.name = name;
    }

    public PoiHssfRuntimeStyle(HSSFWorkbook workbook, String name, Object... attributes) {
        this(workbook, name);
        for (int i = 0; i < attributes.length; ++i) {
            String attrName = (String)attributes[i];
            Object attrValue = attributes[++i];
            set(attrName, attrValue);
        }
    }

    public static List<String> getStyleNameList(String name) {
        if (UtilValidate.isEmpty(name)) {
            return EMPTY_LIST;
        }
        return StringUtil.split(name, " ");
    }

    public static String getStyleName(List<String> names) {
        return StringUtil.join(names, " ");
    }

    public static String normalizeStyleNameList(String name) {
        return getStyleName(getStyleNameList(name));
    }

    public static boolean hasStyle(String styles, String styleItem) {
        List<String> styleList = PoiHssfRuntimeStyle.getStyleNameList(styles);
        for (String styleItemInList : styleList) {
            if (styleItem.equals(styleItemInList)) {
                return true;
            }
        }
        return false;
    }

    public static String concat(String name, String nameAdd) {
        if (name == null)
            name = "";
        if (name.length() > 0)
            name += " ";
        return name + nameAdd;
    }

    public static String concat(String name, PoiHssfRuntimeStyle styleAdd) {
        return concat(name, styleAdd.getName());
    }

    public static String concatNames(PoiHssfRuntimeStyle style, String nameAdd) {
        return concat(style.getName(), nameAdd);
    }

    public static String concatNames(PoiHssfRuntimeStyle style, PoiHssfRuntimeStyle styleAdd) {
        return concat(style.getName(), styleAdd.getName());
    }

    public String getName() {
        return this.name;
    }

    protected Object getStaticFieldValue(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getField(fieldName);
            return field.get(null);
        } catch (Exception e) {
            return new RuntimeException(String.format("Error reading value for ${0}.${1}", clazz.getName(), fieldName), e);
        }
    }

    public void set(String attrName, Object value) {
        if (value instanceof String) {
            String strValue = (String)value;
            if ("dataFormat".equals(attrName)) {
                value = this.dataFormat.getFormat(strValue);
            } else if ("alignment".equals(attrName)) {
                value = getStaticFieldValue(HSSFCellStyle.class, "ALIGN_" + strValue.toUpperCase());
            }
        }
        if (value != null)
            attributes.put(attrName, value);
    }

    public void build(HSSFCellStyle style) throws Exception {
        buildName(style);
        for (Map.Entry<String, Object> entry : attributes.entrySet())
            buildAttributes(style, entry.getKey(), entry.getValue());
    }

    protected void buildName(HSSFCellStyle style) {
        String name = concat(style.getUserStyleName(), this.name);
        style.setUserStyleName(name);
    }

    protected void buildAttributes(HSSFCellStyle style, String attrName, Object value) throws Exception {
        String methodName = "set" + Character.toUpperCase(attrName.charAt(0)) + attrName.substring(1);
        Class<?> styleType = style.getClass();
        Class<?> valueType = value.getClass();
        Method method = null;
        try {
            method = styleType.getMethod(methodName, new Class[] { valueType });
        } catch (NoSuchMethodException e) {
            Field field = null;
            try {
                field = valueType.getField("TYPE");
            } catch (NoSuchFieldException e2) {
                throw e;
            }
            Object obj = field.get(null);
            if (obj instanceof Class) {
                valueType = (Class<?>)obj;
                method = styleType.getMethod(methodName, new Class[] { valueType });
            }
        }
        method.invoke(style, value);
    }
}
