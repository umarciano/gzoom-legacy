package org.ofbiz.widget.poi;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.widget.WidgetWorker;
import org.ofbiz.widget.form.FormStringRenderer;
import org.ofbiz.widget.form.ModelForm;
import org.ofbiz.widget.form.ModelFormField;
import org.ofbiz.widget.form.ModelFormField.ContainerField;
import org.ofbiz.widget.form.ModelFormField.FieldInfo;
import org.ofbiz.widget.form.ModelFormField.OptionValue;
import org.ofbiz.widget.form.ModelFormField.TextField;

public class PoiHssfFormRenderer implements FormStringRenderer {

    private static final String MODULE = PoiHssfFormRenderer.class.getName();

    public static final String STYLE_NAME_CENTER = "ofbiz-priv-center";
    public static final String STYLE_NAME_WRAP = "ofbiz-priv-wrap";
    public static final String STYLE_NAME_BOLD = "ofbiz-priv-bold";

    public static final String STYLE_NAME_DATE = "ofbiz-priv-date";
    public static final String STYLE_NAME_TIME = "ofbiz-priv-time";
    public static final String STYLE_NAME_DATETIME = "ofbiz-priv-date-time";

    private static final String STYLE_HIDDEN = "hidden";
    private static final String CONTEXT_NAME = "poiHssfContext";

    private final MethodCallTracer tracer = new MethodCallTracer(Debug.VERBOSE, MODULE, "**** call ", "\t", 0);
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PoiHssfContext poiHssfContext;
    private HSSFSheet sheet;
    private int rowNum;
    private int rowSpan;
    private int colNum;
    private int colSpan;
    private HSSFRow row;
    private HSSFCell cell;
    private boolean skipForm;
    private boolean skipRenderFormOpen;
    private ModelForm modelForm;

    public PoiHssfFormRenderer() {
        this(null, null);
    }

    public PoiHssfFormRenderer(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        skipForm = false;
        skipRenderFormOpen = false;
        modelForm = null;
    }

    protected boolean isList() {
        return "list".equals(modelForm.getType()) || "multi".equals(modelForm.getType());
    }

    protected boolean isSingle(ModelForm modelForm) {
        if ("single".equals(modelForm.getType())) {
            return true;
        }
        if ("upload".equals(modelForm.getType())) {
            return true;
        }
        return false;
    }

    protected boolean isHidden(ModelFormField modelFormField) {
        return PoiHssfRuntimeStyle.hasStyle(modelFormField.getWidgetAreaStyle(), STYLE_HIDDEN);
    }

    protected boolean isHidden(ModelForm modelForm) {
        return PoiHssfRuntimeStyle.hasStyle(modelForm.getContainerStyle(), STYLE_HIDDEN);
    }

    protected boolean isSubmit(ModelFormField modelFormField) {
        return modelFormField.getFieldInfo().getFieldType() == ModelFormField.FieldInfo.SUBMIT;
    }

    protected boolean isHyperlink(ModelFormField modelFormField) {
        return modelFormField.getFieldInfo().getFieldType() == ModelFormField.FieldInfo.HYPERLINK;
    }

    protected void initPoiHssfContext(Map<String, Object> context, ModelForm modelForm) {
        this.modelForm = modelForm;
        if (poiHssfContext == null) {
            poiHssfContext = (PoiHssfContext)context.get(CONTEXT_NAME);
        }
        if (poiHssfContext == null) {
            poiHssfContext = new PoiHssfContext();
            context.put(CONTEXT_NAME, poiHssfContext);
        }
        //
        poiHssfContext.initFromContext(context);
        skipForm = poiHssfContext.isToSkip(modelForm.getContainerId(context)) || isHidden(modelForm);
        if (!skipForm) {
            //
            sheet = poiHssfContext.addSheet(modelForm.getName(), sheet);
            rowNum = 0;
            colNum = 0;
            getGenericStyle(STYLE_NAME_CENTER, "alignment", "center");
            getGenericStyle(STYLE_NAME_WRAP, "wrapText", Boolean.TRUE);
            //
            HSSFFont font = poiHssfContext.getWorkbook().createFont();
            font.setBold(true);
            getGenericStyle(STYLE_NAME_BOLD, "font", font);
        }
    }

    protected void finalizePoiHssfContext(Map<String, Object> context, ModelForm modelForm) {
        if (!skipForm) {
            poiHssfContext.autoSizeColumns(sheet, true);
        }
        skipForm = false;
        skipRenderFormOpen = false;
        this.modelForm = null;
    }

    protected void initRow() {
        row = sheet.createRow(rowNum);
        rowSpan = 1;
        colNum = 0;
    }

    protected void finalizeRow() {
        rowNum += rowSpan;
        colNum = 0;
    }

    protected void skipColumn(int colSpan) {
        initColumn(colSpan, false);
        finalizeColumn();
    }

    protected void initColumn(int colSpan) {
        initColumn(colSpan, true);
    }

    protected void initColumn(int colSpan, boolean create) {
        if (create) {
            cell = row.createCell(colNum);
        } else {
            cell = null;
        }
        if (colSpan < 0) {
            colSpan = 1;
        }
        this.colSpan = colSpan;
        if (create && colSpan > 1) {
            CellRangeAddress range = new CellRangeAddress(rowNum, rowNum, colNum, colNum + colSpan - 1);
            sheet.addMergedRegion(range);
        }
    }

    protected void finalizeColumn() {
        colNum += colSpan;
    }

    protected PoiHssfRuntimeStyle getGenericStyle(String styleName, Object... attributes) {
        PoiHssfRuntimeStyle style = poiHssfContext.getStyleCatalog().get(styleName);
        if (style == null) {
            style = new PoiHssfRuntimeStyle(poiHssfContext.getWorkbook(), styleName, attributes);
            poiHssfContext.getStyleCatalog().put(style);
        }
        return style;
    }

    protected PoiHssfRuntimeStyle getDataFormatStyle(String styleNamePrefix, Locale locale, String format) {
        String styleName = styleNamePrefix;
        if (locale != null) {
            styleName += "-" + locale.toString();
        }
        PoiHssfRuntimeStyle style = poiHssfContext.getStyleCatalog().get(styleName);
        if (style == null) {
            style = new PoiHssfRuntimeStyle(poiHssfContext.getWorkbook(), styleName);
            style.set("dataFormat", format);
            poiHssfContext.getStyleCatalog().put(style);
        }
        return style;
    }

    // **** ***

    protected void renderCellValue(Appendable writer, Map<String, Object> context, String style, String value) {
        style = PoiHssfRuntimeStyle.concat(style, STYLE_NAME_WRAP);
        cell.setCellValue(new HSSFRichTextString(value));
        poiHssfContext.setCellStyle(cell, style);
    }

    protected void renderCellValueDateTime(Appendable writer, Map<String, Object> context, String style, Date value) {
        cell.setCellValue(value);
        poiHssfContext.setCellStyle(cell, style);
    }

    protected void renderCellValueNumber(Appendable writer, Map<String, Object> context, String style, double value) {
        cell.setCellValue(value);
        poiHssfContext.setCellStyle(cell, style);
    }

    protected void renderCellValueHyperlink(Appendable writer, Map<String, Object> context, String style, String value, String description, HyperlinkType type) {
        if (UtilValidate.isEmpty(description)) {
            description = value;
        }
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFCreationHelper helper = wb.getCreationHelper();
        HSSFHyperlink link = helper.createHyperlink(type);
        link.setAddress(value);
        link.setLabel(description);
        cell.setHyperlink(link);
        poiHssfContext.setCellStyle(cell, style);
        try {
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    // *** ***

    public void renderDisplayField(Appendable writer, Map<String, Object> context, ModelFormField.DisplayField displayField) throws IOException {
        if (skipForm) {
            return;
        }
        try {
            ModelFormField modelFormField = displayField.getModelFormField();
            if (!isHidden(modelFormField)) {

                int fieldType = displayField.getFieldType();
                String style = modelFormField.getWidgetStyle();
                String type = displayField.getType();

                String strValue;
                if (fieldType == FieldInfo.DISPLAY_ENTITY) {
                    strValue = displayField.getDescription(context);
                } else {
                    strValue = displayField.getStringValue(context);
                }

                if (UtilValidate.isEmpty(strValue)) {
                    renderCellValue(writer, context, style, "");
                } else {
                    Locale locale = displayField.getLocaleValue(context);
                    if ("currency".equals(type)) {
                        BigDecimal value = displayField.getCurrencyValue(context, locale, strValue);
                        renderCellValueNumber(writer, context, style, value.doubleValue());
                    } else {
                        TimeZone timeZone = displayField.getTimeZoneValue(context);
                        if ("date".equals(type)) {
                            Date value = displayField.getDateValue(context, locale, timeZone, strValue);
                            style = PoiHssfRuntimeStyle.concat(style, getDataFormatStyle(STYLE_NAME_DATE, locale, UtilDateTime.getDateFormat(locale)));
                            renderCellValueDateTime(writer, context, style, value);
                        } else if ("date-time".equals(type)) {
                            Date value = displayField.getDateTimeValue(context, locale, timeZone, strValue);
                            style = PoiHssfRuntimeStyle.concat(style, getDataFormatStyle(STYLE_NAME_DATETIME, locale, UtilDateTime.getDateTimeFormat(locale)));
                            renderCellValueDateTime(writer, context, style, value);
                        } else if ("time".equals(type)) {
                            Date value = displayField.getTimeValue(context, locale, timeZone, strValue);
                            style = PoiHssfRuntimeStyle.concat(style, getDataFormatStyle(STYLE_NAME_TIME, locale, UtilDateTime.getTimeFormat(locale)));
                            renderCellValueDateTime(writer, context, style, value);
                        } else {
                            renderCellValue(writer, context, style, strValue);
                        }
                    }
                }
            }
        } catch (GeneralException e) {
            throw new IOException(e.getMessage());
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void renderHyperlinkField(Appendable writer, Map<String, Object> context, ModelFormField.HyperlinkField hyperlinkField) throws IOException {
        if (skipForm) {
            return;
        }
        ModelFormField modelFormField = hyperlinkField.getModelFormField();
        if (!isHidden(modelFormField)) {
            StringWriter swUrl = new StringWriter();
            WidgetWorker.buildHyperlinkUrl(swUrl, hyperlinkField.getTarget(context), hyperlinkField.getTargetType(), null, null, false, false, true, request, response, context);
            //TODO prima del rinnovo librerie l'ultimo parametro della successiva chiamata era un int ed era fissato ad 1. Da verificare se  utilizzando URL ci siano dei problemi in casi particolari.
            renderCellValueHyperlink(writer, context, modelFormField.getWidgetStyle(), swUrl.toString(), hyperlinkField.getDescription(context), HyperlinkType.URL);
        }
    }

    // *** ***

    public void renderTextField(Appendable writer, Map<String, Object> context, ModelFormField.TextField textField) throws IOException {
        if (skipForm) {
            return;
        }
        ModelFormField modelFormField = textField.getModelFormField();
        if (!isHidden(modelFormField)) {
            renderCellValue(writer, context, modelFormField.getWidgetStyle(), modelFormField.getEntry(context, textField.getDefaultValue(context)));
        }
    }

    public void renderTextareaField(Appendable writer, Map<String, Object> context, ModelFormField.TextareaField textareaField) throws IOException {
        if (skipForm) {
            return;
        }
        ModelFormField modelFormField = textareaField.getModelFormField();
        if (!isHidden(modelFormField)) {
            renderCellValue(writer, context, modelFormField.getWidgetStyle(), modelFormField.getEntry(context, textareaField.getDefaultValue(context)));
        }
    }

    public void renderDateTimeField(Appendable writer, Map<String, Object> context, ModelFormField.DateTimeField dateTimeField) throws IOException {
        if (skipForm) {
            return;
        }
        ModelFormField modelFormField = dateTimeField.getModelFormField();
        if (!isHidden(modelFormField)) {
            String strValue = modelFormField.getEntry(context, dateTimeField.getDefaultValue(context));
            if (UtilValidate.isNotEmpty(strValue)) {

                if (!Pattern.matches("^.+([0-1]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)(\\.\\d+){0,1}$", strValue)) {
                    if (Pattern.matches("^.+([0-1]\\d|2[0-3]):([0-5]\\d)$", strValue)) {
                        strValue += ":00";
                    } else if (Pattern.matches("^.+\\\\s+([0-1]\\d|2[0-3])$", strValue)) {
                        strValue += ":00:00";
                    } else {
                        strValue += " 00:00:00";
                    }
                }

                try {
                    Timestamp dateValue = (Timestamp)ObjectType.simpleTypeConvert(strValue, "java.sql.Timestamp", null, (Locale)context.get("locale"));
                    String style = modelFormField.getWidgetStyle();

                    boolean shortDateInput = "date".equals(dateTimeField.getType()) || "time-dropdown".equals(dateTimeField.getInputMethod());
                    Locale locale = (Locale)context.get("locale");
                    if (locale == null) {
                        locale = Locale.getDefault();
                    }

                    if (shortDateInput) {
                        style = PoiHssfRuntimeStyle.concat(style, getDataFormatStyle(STYLE_NAME_DATE, locale, UtilDateTime.getDateFormat(locale)));
                    } else if ("time".equals(dateTimeField.getType())) {
                        style = PoiHssfRuntimeStyle.concat(style, getDataFormatStyle(STYLE_NAME_TIME, locale, UtilDateTime.getTimeFormat(locale)));
                    } else {
                        style = PoiHssfRuntimeStyle.concat(style, getDataFormatStyle(STYLE_NAME_DATETIME, locale, UtilDateTime.getDateTimeFormat(locale)));
                    }
                    renderCellValueDateTime(writer, context, style, dateValue);
                } catch (GeneralException e) {
                }
            }
        }
    }

    // *** ***

    public void renderDropDownField(Appendable writer, Map<String, Object> context, ModelFormField.DropDownField dropDownField) throws IOException {
        if (skipForm) {
            return;
        }
        ModelFormField modelFormField = dropDownField.getModelFormField();
        if (!isHidden(modelFormField)) {
            String value = "";
            String currentValue = modelFormField.getEntry(context);
            List<ModelFormField.OptionValue> allOptionValues = dropDownField.getAllOptionValues(context, WidgetWorker.getDelegator(context));
            // if the current value should go first, display it
            if (UtilValidate.isNotEmpty(currentValue) && "first-in-list".equals(dropDownField.getCurrent())) {
                String explicitDescription = dropDownField.getCurrentDescription(context);
                if (UtilValidate.isNotEmpty(explicitDescription)) {
                    value = explicitDescription;
                } else {
                    value = ModelFormField.FieldInfoWithOptions.getDescriptionForOptionKey(currentValue, allOptionValues);
                }
            } else {
                boolean optionSelected = false;
                for (ModelFormField.OptionValue optionValue : allOptionValues) {
                    String noCurrentSelectedKey = dropDownField.getNoCurrentSelectedKey(context);
                    if ((UtilValidate.isNotEmpty(currentValue) && currentValue.equals(optionValue.getKey()) && "selected".equals(dropDownField.getCurrent())) || (UtilValidate.isEmpty(currentValue) && noCurrentSelectedKey != null && noCurrentSelectedKey.equals(optionValue.getKey()))) {
                        value = optionValue.getDescription();
                        optionSelected = true;
                        break;
                    }
                }
                if (!optionSelected) {
                    value = "";
                }
            }
            renderCellValue(writer, context, modelFormField.getWidgetStyle(), value);
        }
    }

    public void renderCheckField(Appendable writer, Map<String, Object> context, ModelFormField.CheckField checkField) throws IOException {
        if (skipForm) {
            return;
        }
        ModelFormField modelFormField = checkField.getModelFormField();
        if (!isHidden(modelFormField)) {
            String value = "";
            String currentValue = modelFormField.getEntry(context);
            List<OptionValue> allOptionValues = checkField.getAllOptionValues(context, WidgetWorker.getDelegator(context));
            for (OptionValue optionValue : allOptionValues) {
                if (UtilValidate.isNotEmpty(currentValue) && currentValue.equals(optionValue.getKey())) {
                    value = optionValue.getDescription();
                    if (UtilValidate.isEmpty(value)) {
                        value = currentValue;
                    }
                    break;
                }
            }
            renderCellValue(writer, context, modelFormField.getWidgetStyle(), value);
        }
    }

    public void renderRadioField(Appendable writer, Map<String, Object> context, ModelFormField.RadioField radioField) throws IOException {
        if (skipForm) {
            return;
        }
        ModelFormField modelFormField = radioField.getModelFormField();
        if (!isHidden(modelFormField)) {
            String value = "";
            List<OptionValue> allOptionValues = radioField.getAllOptionValues(context, WidgetWorker.getDelegator(context));
            String currentValue = modelFormField.getEntry(context);
            String noCurrentSelectedKey = radioField.getNoCurrentSelectedKey(context);
            for (OptionValue optionValue : allOptionValues) {
                if (UtilValidate.isNotEmpty(currentValue) && currentValue.equals(optionValue.getKey())) {
                    value = optionValue.getDescription();
                    if (UtilValidate.isEmpty(value)) {
                        value = currentValue;
                    }
                } else if (UtilValidate.isEmpty(currentValue) && noCurrentSelectedKey != null && noCurrentSelectedKey.equals(optionValue.getKey())) {
                    value = optionValue.getDescription();
                    if (UtilValidate.isEmpty(value)) {
                        value = noCurrentSelectedKey;
                    }
                }
            }
            renderCellValue(writer, context, modelFormField.getWidgetStyle(), value);
        }
    }

    // *** Submit buttons ***

    public void renderSubmitField(Appendable writer, Map<String, Object> context, ModelFormField.SubmitField submitField) throws IOException {
        // nop
    }

    public void renderResetField(Appendable writer, Map<String, Object> context, ModelFormField.ResetField resetField) throws IOException {
        // nop
    }

    // *** Hidden / Ignored fields ***

    public void renderHiddenField(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, String value) throws IOException {
        // nop
    }

    public void renderHiddenField(Appendable writer, Map<String, Object> context, ModelFormField.HiddenField hiddenField) throws IOException {
        // nop
    }

    public void renderIgnoredField(Appendable writer, Map<String, Object> context, ModelFormField.IgnoredField ignoredField) throws IOException {
        // nop
    }

    // *** Title fields ***

    public void renderFieldTitle(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(0);
        if (!(isHidden(modelFormField)) && !(isSubmit(modelFormField)) && !(isHyperlink(modelFormField))) {
            renderCellValue(writer, context, modelFormField.getWidgetStyle(), modelFormField.getTitle(context));
        }
    }

    public void renderSingleFormFieldTitle(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
        if (skipForm) {
            return;
        }
        renderFieldTitle(writer, context, modelFormField);
    }

    // *** Form Single/Multi boundaries ***

    public void renderFormOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        tracer.trace(1);
        if (this.modelForm == null) {
            skipRenderFormOpen = false;
            initPoiHssfContext(context, modelForm);
        }
    }

    public void renderFormClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        tracer.trace(-1);
        if (!skipRenderFormOpen) {
            finalizePoiHssfContext(context, modelForm);
        }
    }

    public void renderMultiFormClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        tracer.trace(-1);
        renderFormClose(writer, context, modelForm);
    }

    // *** Form List boundaries ***

    public void renderFormatListWrapperOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        tracer.trace(1);
        if (this.modelForm == null) {
            skipRenderFormOpen = true;
            initPoiHssfContext(context, modelForm);
        }
    }

    public void renderFormatListWrapperClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        tracer.trace(-1);
        if (skipRenderFormOpen) {
            finalizePoiHssfContext(context, modelForm);
        }
    }

    // *** ***

    public void renderFormatHeaderRowOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(1);
        initRow();
    }

    public void renderFormatHeaderRowClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(-1);
        finalizeRow();
    }

    public void renderFormatHeaderRowCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField, int positionSpan) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(1);
        if (!(isSubmit(modelFormField)) && !(isHidden(modelFormField)) && !(isHyperlink(modelFormField))) {
            initColumn(positionSpan);
            // TODO set column style by modelFormField
        }
    }

    public void renderFormatHeaderRowCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(-1);
        if (!(isSubmit(modelFormField)) && !(isHidden(modelFormField)) && !(isHyperlink(modelFormField))) {
            finalizeColumn();
        }
    }

    // *** ***

    public void renderFormatHeaderRowFormCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(1);
        skipColumn(1);
    }

    public void renderFormatHeaderRowFormCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(-1);
        // nop
    }

    public void renderFormatHeaderRowFormCellTitleSeparator(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField, boolean isLast) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(0);
        // nop
    }

    // *** ***

    public void renderFormatItemRowOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(1);
        initRow();
    }

    public void renderFormatItemRowClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(-1);
        finalizeRow();
    }

    public void renderFormatItemRowCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField, int positionSpan) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(1);
        if (!(isSubmit(modelFormField)) && !(isHidden(modelFormField)) && !(isHyperlink(modelFormField))) {
            initColumn(positionSpan);
        }
    }

    public void renderFormatItemRowCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(-1);
        if (!(isSubmit(modelFormField)) && !(isHidden(modelFormField)) && !(isHyperlink(modelFormField))) {
            finalizeColumn();
        }
    }

    public void renderFormatItemRowFormCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(1);
    }

    public void renderFormatItemRowFormCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(-1);
    }

    // *** ***

    public void renderFormatSingleWrapperOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(1);
        // nop
    }

    public void renderFormatSingleWrapperClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(-1);
        // nop
    }

    // *** ***

    public void renderFormatFieldRowOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(1);
        initRow();
    }

    public void renderFormatFieldRowClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(-1);
        finalizeRow();
    }

    public void renderFormatFieldRowTitleCellOpen(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(1);
        initColumn(1);
    }

    public void renderFormatFieldRowTitleCellClose(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(-1);
        finalizeColumn();
    }

    public void renderFormatFieldRowSpacerCell(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(0);
        // nop
    }

    public void renderFormatFieldRowWidgetCellOpen(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, int positions, int positionSpan, Integer nextPositionInRow) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(1);
        initColumn(positionSpan);
    }

    public void renderFormatFieldRowWidgetCellClose(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, int positions, int positionSpan, Integer nextPositionInRow) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(-1);
        finalizeColumn();
    }

    // *** ***

    public void renderFormatEmptySpace(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(0);
        skipColumn(1);
    }

    // *** ***

    public void renderTextFindField(Appendable writer, Map<String, Object> context, ModelFormField.TextFindField textFindField) throws IOException {
        if (skipForm) {
            return;
        }
        TextField textField = new TextField(textFindField.getModelFormField());
        textField.setSize(textFindField.getSize());
        textField.setClientAutocompleteField(textFindField.getClientAutocompleteField());
        textField.setMaxlength(textFindField.getMaxlength());
        textField.setDefaultValue(textFindField.getDefaultValue(context));

        renderTextField(writer, context, textField);
    }

    public void renderDateFindField(Appendable writer, Map<String, Object> context, ModelFormField.DateFindField textField) throws IOException {
        if (skipForm) {
            return;
        }
        renderDateTimeField(writer, context, textField);
    }

    public void renderRangeFindField(Appendable writer, Map<String, Object> context, ModelFormField.RangeFindField textField) throws IOException {
        if (skipForm) {
            return;
        }
        renderTextField(writer, context, textField);
    }

    public void renderLookupField(Appendable writer, Map<String, Object> context, ModelFormField.LookupField lookupField) throws IOException {
        if (skipForm) {
            return;
        }
        TextField textField = new TextField(lookupField.getModelFormField());
        textField.setSize(lookupField.getSize());
        textField.setClientAutocompleteField(lookupField.getClientAutocompleteField());
        textField.setMaxlength(lookupField.getMaxlength());
        textField.setDefaultValue(lookupField.getDefaultValue(context));

        renderTextField(writer, context, textField);
    }

    public void renderFileField(Appendable writer, Map<String, Object> context, ModelFormField.FileField textField) throws IOException {
        if (skipForm) {
            return;
        }
        renderTextField(writer, context, textField);
    }

    public void renderPasswordField(Appendable writer, Map<String, Object> context, ModelFormField.PasswordField textField) throws IOException {
        // nop
    }

    public void renderImageField(Appendable writer, Map<String, Object> context, ModelFormField.ImageField textField) throws IOException {
        // nop
    }

    public void renderBanner(Appendable writer, Map<String, Object> context, ModelForm.Banner banner) throws IOException {
        // nop
    }

    public void renderFieldGroupOpen(Appendable writer, Map<String, Object> context, ModelForm.FieldGroup fieldGroup) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(1);
        ModelForm modelForm = fieldGroup.getModelForm();
        int colSpan;
        if (isSingle(modelForm)) {
            initRow();
            colSpan = 2;
        } else {
            colSpan = fieldGroup.getFieldNumber();
        }
        FlexibleStringExpander titleNotExpanded = FlexibleStringExpander.getInstance(fieldGroup.getTitle());
        String title = titleNotExpanded.expandString(context);
        String style = modelForm.getFormTitleAreaStyle();
        style = PoiHssfRuntimeStyle.concat(style, STYLE_NAME_CENTER);
        initColumn(colSpan);
        renderCellValue(writer, context, style, title);
        if (isSingle(modelForm)) {
            finalizeColumn();
            finalizeRow();
        }
    }

    public void renderFieldGroupClose(Appendable writer, Map<String, Object> context, ModelForm.FieldGroup fieldGroup) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(-1);
        ModelForm modelForm = fieldGroup.getModelForm();
        if (!isSingle(modelForm)) {
            finalizeColumn();
        }
    }

    // *** ***

    public void renderFormatHeaderRowGroupOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(1);
        // nop
    }

    public void renderFormatHeaderRowGroupClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
        if (skipForm) {
            return;
        }
        tracer.trace(-1);
        sheet.createFreezePane(colNum, rowNum);
    }

    public void renderContainerFindField(Appendable writer, Map<String, Object> context, ContainerField containerField) throws IOException {
    }
}

/******************************************************************************
*** formStringRenderer call hierarchy in ModelForm

ModelForm.renderSingleFormString
    renderFormOpen // check skip start/end
        render{typed}Field // hidden/ignored fields
        renderHiddenField
        // loop currentFormField
        renderBanner
        renderFieldGroupOpen // start group field
            renderFormatSingleWrapperOpen
                // loop grouped fields
                renderFormatFieldRowOpen
                    renderFormatFieldRowTitleCellOpen
                        renderFieldTitle / renderFormatEmptySpace
                    renderFormatFieldRowSpacerCell
                    renderFormatFieldRowWidgetCellOpen
                        render{typed}Field

ModelForm.renderListFormString
    renderFormatListWrapperOpen
        // headers loop
        renderFormatHeaderRowGroupOpen
            // HeaderRow of FieldGroup
            renderFormatHeaderRowOpen
                renderFieldGroupOpen
                renderFormatHeaderRowFormCellOpen
            // HeaderRow of Fields
            renderFormatHeaderRowOpen
                renderFormatHeaderRowCellOpen
                    renderFieldTitle
        // items loop
        renderFormatItemRowOpen
            renderFormOpen // if formPerItem
                // field loop
                renderFormatItemRowCellOpen
                    // hidden fields (if formPerItem)
                    render{typed}Field
                    renderHiddenField
                    // visible fields
                    render{typed}Field

ModelForm.renderMultiFormString
    renderFormOpen // check skip start/end
        renderFormatListWrapperOpen
            // headers loop (see ModelForm.renderListFormString)
            // items loop
            renderFormatItemRowOpen
                // field loop
                renderFormatItemRowCellOpen
                    // hidden fields (first column)
                    render{typed}Field
                    renderHiddenField
                    // visible fields
                    render{typed}Field

******************************************************************************/
