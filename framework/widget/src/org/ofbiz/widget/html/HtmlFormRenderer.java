/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.widget.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;

import org.apache.commons.lang.StringEscapeUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.StringUtil.SimpleEncoder;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.finder.EntityFinderUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.webapp.control.RequestHandler;
import org.ofbiz.webapp.taglib.ContentUrlTag;
import org.ofbiz.widget.ModelWidget;
import org.ofbiz.widget.Paginator;
import org.ofbiz.widget.WidgetDataResourceWorker;
import org.ofbiz.widget.WidgetWorker;
import org.ofbiz.widget.form.FormStringRenderer;
import org.ofbiz.widget.form.ModelForm;
import org.ofbiz.widget.form.ModelForm.UpdateArea;
import org.ofbiz.widget.form.ModelFormField;
import org.ofbiz.widget.form.ModelFormField.AlternateLanguageManagement;
import org.ofbiz.widget.form.ModelFormField.CheckField;
import org.ofbiz.widget.form.ModelFormField.ContainerField;
import org.ofbiz.widget.form.ModelFormField.DateFindField;
import org.ofbiz.widget.form.ModelFormField.DateTimeField;
import org.ofbiz.widget.form.ModelFormField.DisplayEntityField;
import org.ofbiz.widget.form.ModelFormField.DisplayField;
import org.ofbiz.widget.form.ModelFormField.DropDownField;
import org.ofbiz.widget.form.ModelFormField.EntityOptions;
import org.ofbiz.widget.form.ModelFormField.FileField;
import org.ofbiz.widget.form.ModelFormField.HiddenField;
import org.ofbiz.widget.form.ModelFormField.HyperlinkField;
import org.ofbiz.widget.form.ModelFormField.IgnoredField;
import org.ofbiz.widget.form.ModelFormField.ImageField;
import org.ofbiz.widget.form.ModelFormField.LookupField;
import org.ofbiz.widget.form.ModelFormField.OptionSource;
import org.ofbiz.widget.form.ModelFormField.PasswordField;
import org.ofbiz.widget.form.ModelFormField.RadioField;
import org.ofbiz.widget.form.ModelFormField.RangeFindField;
import org.ofbiz.widget.form.ModelFormField.ResetField;
import org.ofbiz.widget.form.ModelFormField.SubHyperlink;
import org.ofbiz.widget.form.ModelFormField.SubmitField;
import org.ofbiz.widget.form.ModelFormField.TextField;
import org.ofbiz.widget.form.ModelFormField.TextFindField;
import org.ofbiz.widget.form.ModelFormField.TextareaField;

import com.ibm.icu.util.Calendar;

/**
 * Widget Library - HTML Form Renderer implementation
 */
public class HtmlFormRenderer extends HtmlWidgetRenderer implements FormStringRenderer {

	public static final String module = HtmlFormRenderer.class.getName();

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected RequestHandler rh;
	protected String lastFieldGroupId = "";
	protected boolean renderPagination = true;
	protected boolean javaScriptEnabled = false;
	private SimpleEncoder internalEncoder;

	protected HtmlFormRenderer() {}

	public HtmlFormRenderer(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
		this.rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
		this.javaScriptEnabled = UtilHttp.isJavaScriptEnabled(request);
		internalEncoder = StringUtil.getEncoder("string");
	}

	public boolean getRenderPagination() {
		return this.renderPagination;
	}

	public void setRenderPagination(boolean renderPagination) {
		this.renderPagination = renderPagination;
	}

	public void appendOfbizUrl(Appendable writer, String location) throws IOException {
		writer.append(this.rh.makeLink(this.request, this.response, location));
	}

	public void appendContentUrl(Appendable writer, String location) throws IOException {
		ContentUrlTag.appendContentPrefix(this.request, writer);
		writer.append(location);
	}

	public void appendTooltip(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
		// render the tooltip, in other methods too
		String tooltip = modelFormField.getTooltip(context);
		if (UtilValidate.isNotEmpty(tooltip)) {
			writer.append("<span class=\"");
			String tooltipStyle = modelFormField.getTooltipStyle();
			if (UtilValidate.isNotEmpty(tooltipStyle)) {
				writer.append(tooltipStyle);
			} else {
				writer.append("tooltip");
			}
			writer.append("\">");
			writer.append(tooltip);
			writer.append("</span>");
		}
	}

	public boolean appendFloatingTooltip(Appendable writer, String floatingTooltip, String description) throws IOException {
		boolean esit = false;
		if (UtilValidate.isNotEmpty(floatingTooltip)) {
			writer.append("<span class=\"title\" ");
			writer.append("title=\"");
			writer.append(floatingTooltip);
			writer.append("\">");
			writer.append(description);
			writer.append("</span>");
			esit = true;
		}

		return esit;
	}

	public void addAsterisks(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {

		boolean requiredField = modelFormField.getRequiredField();
		if (requiredField) {
			String requiredStyle = modelFormField.getRequiredFieldStyle();

			if (UtilValidate.isEmpty(requiredStyle)) {
				writer.append("*");
			}
		}
	}

	public String concatClassNames(String classNames, String classNameAdded) {
		if (UtilValidate.isEmpty(classNames))
			classNames = "";
		if (!classNames.contains(classNameAdded)) {
			if (classNames.length() > 0)
				classNames += " ";
			classNames += classNameAdded;
		}
		return classNames;
	}

	public void appendClassNames(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
		appendClassNames(writer, null, context, modelFormField);
	}

	public void appendClassNamesOnly(StringBuilder allClass, String classes, Map<String, Object> context, ModelFormField modelFormField) throws IOException {

		if (UtilValidate.isNotEmpty(classes))
			allClass.append(classes);

		String className = modelFormField.getWidgetStyle();
		if (UtilValidate.isNotEmpty(className)) {
			if (allClass.length() > 0)
				allClass.append(" ");
			allClass.append(className);
		}
		if (modelFormField.shouldBeRed(context)) {
			if (allClass.length() > 0)
				allClass.append(" ");
			allClass.append("alert");
		}
		if (modelFormField.getRequiredField()) {
			if (allClass.length() > 0)
				allClass.append(" ");
			allClass.append("mandatory");
		}
		if (modelFormField.getEncodeOutput()) {
            if (allClass.length() > 0)
                allClass.append(" ");
            allClass.append("encode_output");
        }
	}

	public void appendClassNames(Appendable writer, String classes, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
		StringBuilder allClass=new StringBuilder();

		appendClassNamesOnly(allClass, classes, context, modelFormField);

		if (allClass.length() > 0) {
			writer.append(" class=\"");
			writer.append(allClass.toString());
			writer.append('"');
		}
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderDisplayField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.DisplayField)
	 */
	public void renderDisplayField(Appendable writer, Map<String, Object> context, DisplayField displayField) throws IOException {
		ModelFormField modelFormField = displayField.getModelFormField();
		StringBuilder str = new StringBuilder();

		String idName = modelFormField.getCurrentContainerId(context);

		if (UtilValidate.isNotEmpty(modelFormField.getWidgetStyle()) || modelFormField.shouldBeRed(context)) {
			str.append("<span class=\"");
			str.append(modelFormField.getWidgetStyle());
			// add a style of red if this is a date/time field and redWhen is true
			if (modelFormField.shouldBeRed(context)) {
				str.append(" alert");
			}
			str.append('"');
			if (UtilValidate.isNotEmpty(idName)) {
				str.append(" id=\"");
				str.append(idName).append("_sp");
				str.append('"');
			}
			// add a style of red if this is a date/time field and redWhen is true
			if (modelFormField.shouldBeRed(context)) {
				str.append(" alert");
			}
			
			String tooltipValue = displayField.getTooltip(context);
			if (UtilValidate.isNotEmpty(tooltipValue)) {
				str.append(" title=\"");
				str.append(tooltipValue);
				str.append('"');
			}
			
			str.append('>');
		}

		if (str.length() > 0) {
			writer.append(str.toString());
		}
		String description = displayField.getDescription(context);
		//Replace new lines with <br/>
		description = description.replaceAll("\n", "<br/>");

		if (UtilValidate.isEmpty(description)) {
			this.renderFormatEmptySpace(writer, context, modelFormField.getModelForm());
		} else {
			if (!this.appendFloatingTooltip(writer, displayField.getFloatingTooltip(context), description))
				writer.append(description);
		}

		if (str.length() > 0) {
			writer.append("</span>");
		}

		ModelFormField.InPlaceEditor inPlaceEditor = displayField.getInPlaceEditor();
		boolean ajaxEnabled = inPlaceEditor != null && this.javaScriptEnabled;

		if (ajaxEnabled) {
			writer.append("<script language=\"JavaScript\" type=\"text/javascript\">");
			StringBuilder url = new StringBuilder(inPlaceEditor.getUrl(context));
			Map<String, Object> fieldMap = inPlaceEditor.getFieldMap(context);
			if (fieldMap != null) {
				url.append('?');
				int count = 0;
				for (Entry<String, Object> field: fieldMap.entrySet()) {
					count++;
					url.append(field.getKey()).append('=').append(field.getValue());
					if (count < fieldMap.size()) {
						url.append('&');
					}
				}
			}
			writer.append("ajaxInPlaceEditDisplayField('");
			writer.append(idName).append("', '").append(url).append("', {");
			if (UtilValidate.isNotEmpty(inPlaceEditor.getParamName())) {
				writer.append("paramName: '").append(inPlaceEditor.getParamName()).append("'");
			} else {
				writer.append("paramName: '").append(modelFormField.getFieldName()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getCancelControl())) {
				writer.append(", cancelControl: ");
				if (!"false".equals(inPlaceEditor.getCancelControl())) {
					writer.append("'");
				}
				writer.append(inPlaceEditor.getCancelControl());
				if (!"false".equals(inPlaceEditor.getCancelControl())) {
					writer.append("'");
				}
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getCancelText())) {
				writer.append(", cancelText: '").append(inPlaceEditor.getCancelText()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getClickToEditText())) {
				writer.append(", clickToEditText: '").append(inPlaceEditor.getClickToEditText()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getFieldPostCreation())) {
				writer.append(", fieldPostCreation: ");
				if (!"false".equals(inPlaceEditor.getFieldPostCreation())) {
					writer.append("'");
				}
				writer.append(inPlaceEditor.getFieldPostCreation());
				if (!"false".equals(inPlaceEditor.getFieldPostCreation())) {
					writer.append("'");
				}
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getFormClassName())) {
				writer.append(", formClassName: '").append(inPlaceEditor.getFormClassName()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getHighlightColor())) {
				writer.append(", highlightColor: '").append(inPlaceEditor.getHighlightColor()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getHighlightEndColor())) {
				writer.append(", highlightEndColor: '").append(inPlaceEditor.getHighlightEndColor()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getHoverClassName())) {
				writer.append(", hoverClassName: '").append(inPlaceEditor.getHoverClassName()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getHtmlResponse())) {
				writer.append(", htmlResponse: ").append(inPlaceEditor.getHtmlResponse());
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getLoadingClassName())) {
				writer.append(", loadingClassName: '").append(inPlaceEditor.getLoadingClassName()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getLoadingText())) {
				writer.append(", loadingText: '").append(inPlaceEditor.getLoadingText()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getOkControl())) {
				writer.append(", okControl: ");
				if (!"false".equals(inPlaceEditor.getOkControl())) {
					writer.append("'");
				}
				writer.append(inPlaceEditor.getOkControl());
				if (!"false".equals(inPlaceEditor.getOkControl())) {
					writer.append("'");
				}
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getOkText())) {
				writer.append(", okText: '").append(inPlaceEditor.getOkText()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getSavingClassName())) {
				writer.append(", savingClassName: '").append(inPlaceEditor.getSavingClassName()).append("', ");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getSavingText())) {
				writer.append(", savingText: '").append(inPlaceEditor.getSavingText()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getSubmitOnBlur())) {
				writer.append(", submitOnBlur: ").append(inPlaceEditor.getSubmitOnBlur());
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getTextBeforeControls())) {
				writer.append(", textBeforeControls: '").append(inPlaceEditor.getTextBeforeControls()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getTextAfterControls())) {
				writer.append(", textAfterControls: '").append(inPlaceEditor.getTextAfterControls()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getTextBetweenControls())) {
				writer.append(", textBetweenControls: '").append(inPlaceEditor.getTextBetweenControls()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getUpdateAfterRequestCall())) {
				writer.append(", updateAfterRequestCall: ").append(inPlaceEditor.getUpdateAfterRequestCall());
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getRows())) {
				writer.append(", rows: '").append(inPlaceEditor.getRows()).append("'");
			}
			if (UtilValidate.isNotEmpty(inPlaceEditor.getCols())) {
				writer.append(", cols: '").append(inPlaceEditor.getCols()).append("'");
			}
			writer.append("});");
			writer.append("</script>");
		}

		if (displayField instanceof DisplayEntityField) {
			this.makeHyperlinkString(writer, ((DisplayEntityField) displayField).getSubHyperlink(), context);
		}

		this.appendTooltip(writer, context, modelFormField);

		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderHyperlinkField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.HyperlinkField)
	 */
	public void renderHyperlinkField(Appendable writer, Map<String, Object> context, HyperlinkField hyperlinkField) throws IOException {
		this.request.setAttribute("image", hyperlinkField.getImage());
		ModelFormField modelFormField = hyperlinkField.getModelFormField();

		boolean ajaxEnabled = false;
		List<UpdateArea> updateAreas = modelFormField.getOnClickUpdateAreas();
		if (this.javaScriptEnabled) {
			if (UtilValidate.isNotEmpty(updateAreas)) {
				ajaxEnabled = true;
			}
		}
		String ajaxParameters = null;
		if (ajaxEnabled) {
			ajaxParameters = createAjaxParamsFromUpdateAreas(updateAreas.subList(0, 1), null, context);
		}

		String description = encode(hyperlinkField.getDescription(context), modelFormField, context);
		String confirmation = encode(hyperlinkField.getConfirmation(context), modelFormField, context);
		
		String tooltipValue = hyperlinkField.getTooltip(context);
		String tooltip = null;
        if(hyperlinkField.showTooltip() && UtilValidate.isNotEmpty(tooltipValue)) {
            tooltip = tooltipValue;
        }
		WidgetWorker.makeHyperlinkByType(writer, hyperlinkField.getLinkType(), modelFormField.getWidgetStyle(), hyperlinkField.getTargetType(), hyperlinkField.getTarget(context),
				hyperlinkField.getParameterMap(context), description, hyperlinkField.getTargetWindow(context), confirmation, modelFormField, ajaxParameters, tooltip,
				this.request, this.response, context);
		//        String target = "";
		//        String event = "";
		//        String action = "";
		//        boolean ajaxEnabled = false;
		//        List<UpdateArea> updateAreas = modelFormField.getOnClickUpdateAreas();
		//        if (this.javaScriptEnabled) {
		//            if (UtilValidate.isNotEmpty(updateAreas)) {
		//                ajaxEnabled = true;
		//            }
		//        }
		//        if (ajaxEnabled) {
		//            event = "onclick";
		//            action = "ajaxUpdateAreas('"+createAjaxParamsFromUpdateAreas(updateAreas.subList(0, 1), null, context) + "'); return false;";
		//        } else {
		//            event = modelFormField.getEvent();
		//            action = modelFormField.getAction(context);
		//        }
		//        target = hyperlinkField.getTarget(context);
		//
		//        this.makeHyperlinkString(
		//                writer,
		//                modelFormField.getWidgetStyle(),
		//                hyperlinkField.getTargetType(),
		//                target,
		//                hyperlinkField.getDescription(context),
		//                hyperlinkField.getTargetWindow(context),
		//                event,
		//                action);
		//        this.appendTooltip(writer, context, modelFormField);
		//appendWhitespace(writer);
	}

	public void makeHyperlinkString(Appendable writer, ModelFormField.SubHyperlink subHyperlink, Map<String, Object> context) throws IOException {
		makeHyperlinkString(writer, subHyperlink, context, true);
	}

	public void makeHyperlinkString(Appendable writer, ModelFormField.SubHyperlink subHyperlink, Map<String, Object> context, boolean encodeDescription) throws IOException {
		if (subHyperlink == null) {
			return;
		}
		if (subHyperlink.shouldUse(context)) {
			writer.append(' ');
			String description = encodeDescription  ? encode(subHyperlink.getDescription(context), subHyperlink.getModelFormField(), context) : subHyperlink.getDescription(context);
			WidgetWorker.makeHyperlinkByType(writer, subHyperlink.getLinkType(), subHyperlink.getLinkStyle(), subHyperlink.getTargetType(), subHyperlink.getTarget(context),
					subHyperlink.getParameterMap(context), description, subHyperlink.getTargetWindow(context), subHyperlink.getConfirmation(context), subHyperlink.getModelFormField(),
					this.request, this.response, context);
		}
	}

	private String encode(String value, ModelFormField modelFormField, Map<String, Object> context) {
		if (UtilValidate.isEmpty(value)) {
			return value;
		}
		StringUtil.SimpleEncoder encoder = (StringUtil.SimpleEncoder)context.get("simpleEncoder");
		if (modelFormField.getEncodeOutput() && encoder != null) {
			value = encoder.encode(value);
		} else {
			value = internalEncoder.encode(value);
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderTextField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.TextField)
	 */
	public void renderTextField(Appendable writer, Map<String, Object> context, TextField textField) throws IOException {
		ModelFormField modelFormField = textField.getModelFormField();
		ModelForm modelForm = modelFormField.getModelForm();

		List<ModelForm.UpdateArea> alternateManagementUpdateAreas = modelFormField.getAlternateLanguageManagementUpdateAreas();
		boolean alternateManagementAjaxEnabled = alternateManagementUpdateAreas != null && this.javaScriptEnabled;

		if (alternateManagementAjaxEnabled) {
			writer.append("<div class=\"alternate_language_field_container\">");
			if ("list".equals(modelForm.getType()) || "multi".equals(modelForm.getType())) {
				writer.append("<div class=\"text_input_field_container\">");
			}
		}

		writer.append("<input type=\"text\"");

		if (textField.isReadOnly(context))
			writer.append(" readonly=\"readonly\"");

		//MAps spa : added validity classes
		String validityFieldType = textField.getValidityFieldType();
		if (UtilValidate.isNotEmpty(validityFieldType)) {
			String type = "mask_" + validityFieldType;
			String classes = "input_mask " + type;
			appendClassNames(writer, classes, context, modelFormField);
			//Now add validity attributes
			if (UtilValidate.isNotEmpty(textField.getValidityMaxValue())) {
				writer.append(" validity-max-value=\"");
				writer.append(textField.getValidityMaxValue());
				writer.append("\"");
			}
			if (UtilValidate.isNotEmpty(textField.getDecimalDigits(context))) {
				writer.append(" decimal_digits=\"");
				writer.append(textField.getDecimalDigits(context));
				writer.append("\"");
			}
		} else {
			appendClassNames(writer, context, modelFormField);
		}

		writer.append(" name=\"");
		writer.append(modelFormField.getParameterName(context));
		writer.append('"');

		String value = modelFormField.getEntry(context, textField.getDefaultValue(context));
		if (UtilValidate.isNotEmpty(value)) {
			writer.append(" value=\"");
			writer.append(value);
			writer.append('"');
		}

		writer.append(" size=\"");
		writer.append(Integer.toString(textField.getSize()));
		writer.append('"');

		Integer maxlength = textField.getMaxlength();
		if (maxlength != null) {
			writer.append(" maxlength=\"");
			writer.append(maxlength.toString());
			writer.append('"');
		}

		String idName = modelFormField.getCurrentContainerId(context);
		if (UtilValidate.isNotEmpty(idName)) {
			writer.append(" id=\"");
			writer.append(idName);
			writer.append('"');
		}
		
		String tooltipValue = textField.getTooltip(context);
        if(textField.showTooltip() && UtilValidate.isNotEmpty(tooltipValue)) {
            writer.append(" title=\"");
            writer.append(tooltipValue);
            writer.append("\"");
        }
        
		String event = modelFormField.getEvent();
		String action = modelFormField.getAction(context);
		if (UtilValidate.isNotEmpty(event) && UtilValidate.isNotEmpty(action)) {
			writer.append(" ");
			writer.append(event);
			writer.append("=\"");
			writer.append(action);
			writer.append('"');
		}

		List<ModelForm.UpdateArea> updateAreas = modelFormField.getOnChangeUpdateAreas();
		boolean ajaxEnabled = updateAreas != null && this.javaScriptEnabled;
		if (!textField.getClientAutocompleteField() || ajaxEnabled) {
			writer.append(" autocomplete=\"off\"");
		}

		writer.append("/>");

		if (alternateManagementAjaxEnabled) {
			AlternateLanguageManagement alternateManagement = modelFormField.getAlternateLanguageManagement();
			writer.append("<input type=\"hidden\"");

			appendClassNames(writer, context, modelFormField);

			writer.append(" name=\"");
			writer.append("contentFieldAssociation");
			writer.append('"');

			String contentFieldAssociation = alternateManagement.getContentFieldAssociation();
			contentFieldAssociation = contentFieldAssociation.concat(":"+modelFormField.getParameterName(context));

			if (UtilValidate.isNotEmpty(contentFieldAssociation)) {
				writer.append(" value=\"");
				writer.append(contentFieldAssociation);
				writer.append('"');
			}
			writer.append("/>");

			if ("list".equals(modelForm.getType()) || "multi".equals(modelForm.getType())) {
				writer.append("</div>");
				writer.append("<div class=\"alternate_language_icon_container\">");
			}
			writer.append("<span class=\"alternate-language-anchor\"><a href=\"#\" class=\"alternate_language\" onclick=\"javascript:ajaxAutoCompleter('").append(createAjaxParamsFromUpdateAreas(alternateManagementUpdateAreas, null, context)).append("')\">Alternate</a></span>");
			if ("list".equals(modelForm.getType()) || "multi".equals(modelForm.getType())) {
				writer.append("</div>");
			}
			writer.append("</div>");

		}

		// this.addAsterisks(writer, context, modelFormField);

		this.makeHyperlinkString(writer, textField.getSubHyperlink(), context);

		this.appendTooltip(writer, context, modelFormField);

		if (ajaxEnabled) {
			appendWhitespace(writer);
			writer.append("<script language=\"JavaScript\" type=\"text/javascript\">");
			appendWhitespace(writer);
			writer.append("ajaxAutoCompleter('").append(createAjaxParamsFromUpdateAreas(updateAreas, null, context)).append("');");
			appendWhitespace(writer);
			writer.append("</script>");
		}
		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderTextareaField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.TextareaField)
	 */
	public void renderTextareaField(Appendable writer, Map<String, Object> context, TextareaField textareaField) throws IOException {
		ModelFormField modelFormField = textareaField.getModelFormField();
		ModelForm modelForm = modelFormField.getModelForm();

		List<ModelForm.UpdateArea> alternateManagementUpdateAreas = modelFormField.getAlternateLanguageManagementUpdateAreas();

		boolean alternateManagementAjaxEnabled = alternateManagementUpdateAreas != null && this.javaScriptEnabled;

		if (alternateManagementAjaxEnabled) {
			if ("list".equals(modelForm.getType()) || "multi".equals(modelForm.getType())) {
				writer.append("<div class=\"textarea_input_field_container\">");
			}
		}

		writer.append("<textarea");

		appendClassNames(writer, context, modelFormField);

		writer.append(" name=\"");
		writer.append(modelFormField.getParameterName(context));
		writer.append('"');

		writer.append(" cols=\"");
		writer.append(Integer.toString(textareaField.getCols()));
		writer.append('"');

		writer.append(" rows=\"");
		writer.append(Integer.toString(textareaField.getRows()));
		writer.append('"');

		String idName = modelFormField.getCurrentContainerId(context);
		if (UtilValidate.isNotEmpty(idName)) {
			writer.append(" id=\"");
			writer.append(idName);
			writer.append('"');
		} else if (textareaField.getVisualEditorEnable()) {
			writer.append(" id=\"");
			writer.append("htmlEditArea");
			writer.append('"');
		}

		if (textareaField.isReadOnly(context)) {
			writer.append(" readonly=\"readonly\"");
		}

		writer.append('>');

		String value = modelFormField.getEntry(context, textareaField.getDefaultValue(context));
		if (UtilValidate.isNotEmpty(value)) {
			writer.append(value);
		}

		writer.append("</textarea>");
		if (alternateManagementAjaxEnabled) {
			if ("list".equals(modelForm.getType()) || "multi".equals(modelForm.getType())) {
				writer.append("</div>");
				writer.append("<div class=\"alternate_language_icon_container\">");
			}
			writer.append("<span class=\"alternate-language-anchor\"><a href=\"#\" class=\"alternate_language\" onclick=\"javascript:ajaxAutoCompleter('" + createAjaxParamsFromUpdateAreas(alternateManagementUpdateAreas, null, context) + "')\">Alternate</a></span>");
			if ("list".equals(modelForm.getType()) || "multi".equals(modelForm.getType())) {
				writer.append("</div>");
			}
		}

		if (textareaField.getVisualEditorEnable()) {
			writer.append("<script language=\"javascript\" src=\"/images/htmledit/whizzywig.js\" type=\"text/javascript\"></script>");
			writer.append("<script language=\"javascript\" type=\"text/javascript\"> buttonPath = \"/images/htmledit/\"; cssFile=\"/images/htmledit/simple.css\";makeWhizzyWig(\"");
			if (UtilValidate.isNotEmpty(idName)) {
				writer.append(idName);
			} else {
				writer.append("htmlEditArea");
			}
			writer.append("\",\"");
			String buttons = textareaField.getVisualEditorButtons(context);
			if (UtilValidate.isNotEmpty(buttons)) {
				writer.append(buttons);
			} else {
				writer.append("all");
			}
			writer.append("\") </script>");
		}

		//this.addAsterisks(writer, context, modelFormField);

		this.appendTooltip(writer, context, modelFormField);

		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderDateTimeField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.DateTimeField)
	 */
	public void renderDateTimeField(Appendable writer, Map<String, Object> context, DateTimeField dateTimeField) throws IOException {
		ModelFormField modelFormField = dateTimeField.getModelFormField();
		ModelForm modelForm = modelFormField.getModelForm();
		String paramName = modelFormField.getParameterName(context);
		String defaultDateTimeString = dateTimeField.getDefaultValue(context);

		Map<String, String> uiLabelMap = UtilGenerics.checkMap(context.get("uiLabelMap"));
		if (uiLabelMap == null) {
			Debug.logWarning("Could not find uiLabelMap in context", module);
		}
		String localizedInputTitle = "" , localizedIconTitle = "", localizedDefaultFormatToShow="", localizedDateFormat="";

		boolean shortDateInput = "date".equals(dateTimeField.getType()) || "time-dropdown".equals(dateTimeField.getInputMethod());
		boolean isReadOnly = dateTimeField.isReadOnly(context);

		// the default values for a timestamp
		int maxlength = 30;
		int size = 25;

		if (shortDateInput) {
			size = maxlength = 10;
			if (uiLabelMap != null) {
				localizedInputTitle = uiLabelMap.get("CommonFormatDate");
				localizedDefaultFormatToShow = uiLabelMap.get("ShowedCommonFormatDate");
				localizedDateFormat = UtilDateTime.getDateFormat((Locale)context.get("locale"));
				maxlength = localizedDefaultFormatToShow.length();
			}
		} else if ("time".equals(dateTimeField.getType())) {
			size = maxlength = 8;
			if (uiLabelMap != null) {
				localizedInputTitle = uiLabelMap.get("CommonFormatTime");
				localizedDefaultFormatToShow = uiLabelMap.get("ShowedCommonFormatTime");
				localizedDateFormat = UtilDateTime.getTimeFormat((Locale)context.get("locale"));
				maxlength = localizedDefaultFormatToShow.length();
			}
		} else {
			if (uiLabelMap != null) {
				localizedInputTitle = uiLabelMap.get("CommonFormatDateTime");
				localizedDefaultFormatToShow = uiLabelMap.get("ShowedCommonFormatDateTime");
				localizedDateFormat = UtilDateTime.getDateTimeFormat((Locale)context.get("locale"));
				maxlength = localizedDefaultFormatToShow.length();
			}
		}

		String idName = modelFormField.getCurrentContainerId(context);
		if (UtilValidate.isEmpty(idName)) {
			idName = paramName;
		}

		//costruisco il rndering dei campi
		int day = 0;
		int month = 0;
		int year = 0;
		int hour = 0;
		int minutes = 0;

		String dateTimeValue = modelFormField.getEntry(context, defaultDateTimeString);
		Calendar calendar = Calendar.getInstance();

		if ("single".equals(modelForm.getType()) || "upload".equals(modelForm.getType()))
			writer.append("<div class=\"datePanel calendarSingleForm\" id=\"").append(paramName).append("_datePanel_").append(Long.toString(UtilDateTime.nowTimestamp().getTime())).append("\">");
		else
			writer.append("<div class=\"datePanel\" id=\"").append(paramName).append("_datePanel_").append(Long.toString(UtilDateTime.nowTimestamp().getTime())).append("\">");

		if (!this.javaScriptEnabled) {
			DateFormatSymbols dfs = new DateFormatSymbols((Locale)context.get("locale"));
			String[] months = dfs.getMonths();

			try {
				if (UtilValidate.isNotEmpty(dateTimeValue)) {
					if (!Pattern.matches("^.+([0-1]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)(\\.\\d+){0,1}$", dateTimeValue)) {
						if (Pattern.matches("^.+([0-1]\\d|2[0-3]):([0-5]\\d)$", dateTimeValue)) {
							dateTimeValue += ":00";
						} else if (Pattern.matches("^.+\\\\s+([0-1]\\d|2[0-3])$", dateTimeValue)) {
							dateTimeValue += ":00:00";
						} else {
							dateTimeValue += " 00:00:00";
						}
					}

					Timestamp defaultTimestamp = (Timestamp)ObjectType.simpleTypeConvert(dateTimeValue, "java.sql.Timestamp", null, (Locale)context.get("locale"));

					if (UtilValidate.isNotEmpty(defaultTimestamp)) {
						calendar.setTime(defaultTimestamp);

						day = calendar.get(Calendar.DAY_OF_MONTH);
						year = calendar.get(Calendar.YEAR);
						month = calendar.get(Calendar.MONTH)+1;
						hour = calendar.get(Calendar.HOUR_OF_DAY);
						minutes = calendar.get(Calendar.MINUTE);
					}
				}
			} catch (IllegalArgumentException e) {
				Debug.logWarning("Form widget field [" + paramName + "] with input-method=\"time-dropdown\" was not able to understand the default time ["
						+ defaultDateTimeString + "]. The parsing error was: " + e.getMessage(), module);
			} catch (GeneralException e) {}

			String firstFieldName = "";
			List<String> dateArray = new FastList<String>();

			// keep the two cases separate because it's hard to understand a combined loop
			if (dateTimeField.getType().equals("date") || dateTimeField.getType().equals("timestamp")) {
				int yearPosition=-1;
				int dayPosition=-1;

				//Render dell'anno
				StringBuilder yearRenderer = new StringBuilder("<input type=\"text\" ");

				yearRenderer.append(" name=\"");
				yearRenderer.append(UtilHttp.makeCompositeParam(paramName, "year"));
				yearRenderer.append('"');
				yearRenderer.append(" id=\"");
				yearRenderer.append(UtilHttp.makeCompositeParam(idName, "year"));
				yearRenderer.append('"');
				yearRenderer.append(" maxlength=\"4\" ");

				appendClassNames(yearRenderer, "year date", context, modelFormField);
				yearRenderer.append(" value=\"");
				if (year > 0)
					yearRenderer.append(year);
				yearRenderer.append("\" />");

				//Render del giorno
				StringBuilder dayRenderer = new StringBuilder("<input type=\"text\" ");

				dayRenderer.append(" name=\"");
				dayRenderer.append(UtilHttp.makeCompositeParam(paramName, "day"));
				dayRenderer.append('"');
				dayRenderer.append(" id=\"");
				dayRenderer.append(UtilHttp.makeCompositeParam(idName, "day"));
				dayRenderer.append('"');
				dayRenderer.append(" maxlength=\"2\" ");

				appendClassNames(dayRenderer, "day date", context, modelFormField);
				dayRenderer.append(" value=\"");
				if (day > 0)
					dayRenderer.append(day);
				dayRenderer.append("\" />");

				StringBuilder monthRenderer = new StringBuilder("<select ");

				monthRenderer.append(" name=\"");
				monthRenderer.append(UtilHttp.makeCompositeParam(paramName, "month"));
				monthRenderer.append('"');
				monthRenderer.append(" id=\"");
				monthRenderer.append(UtilHttp.makeCompositeParam(idName, "month"));
				monthRenderer.append('"');

				appendClassNames(monthRenderer, "month date", context, modelFormField);
				monthRenderer.append(" >");
				if (UtilValidate.isNotEmpty(monthRenderer)) {
					for (int i = 0; i < months.length; i++) {
						if (UtilValidate.isNotEmpty(months[i])) {
							monthRenderer.append("<option value=\"");
							monthRenderer.append(""+(i+1));
							if (month-1 == i)
								monthRenderer.append(" selected=\"selected\"");
							monthRenderer.append("\">");
							monthRenderer.append(months[i]);
							monthRenderer.append("</option>");
						}
					}
				}
				monthRenderer.append("</select>");

				firstFieldName = UtilHttp.makeCompositeParam(paramName, "month");
				dateArray.add(monthRenderer.toString());
				if (UtilValidate.isNotEmpty(localizedDateFormat)) {
					if (Pattern.matches("^(yy|yyyy)[/-\\\\s].+[/-\\\\s].+$", localizedDateFormat)) {
						yearPosition=0;
						firstFieldName = UtilHttp.makeCompositeParam(paramName, "year");
					} else if (Pattern.matches("^.+[/-\\\\s](yy|yyyy)[/-\\\\s].+$", localizedDateFormat)) {
						yearPosition = 1;
					} else if (Pattern.matches("^.+[/-\\\\s].+[/-\\\\s](yy|yyyy).+$", localizedDateFormat)) {
						yearPosition = 2;
					}
					if (Pattern.matches("^(dd)[/-\\\\s].+[/-\\\\s].+$", localizedDateFormat)) {
						dayPosition=0;
						firstFieldName = UtilHttp.makeCompositeParam(paramName, "day");
					} else if (Pattern.matches("^.+[/-\\\\s](dd)[/-\\\\s].+$", localizedDateFormat)) {
						dayPosition = 1;
					} else if (Pattern.matches("^.+[/-\\\\s].+[/-\\\\s](dd).+$", localizedDateFormat)) {
						dayPosition = 2;
					}
				}
				if (yearPosition==-1 && dayPosition ==-1) {
					dateArray.add(dayRenderer.toString());
					dateArray.add(yearRenderer.toString());
				} else {
					if (yearPosition >= dateArray.size()) {
						dateArray.add(yearRenderer.toString());
					} else if (yearPosition != -1){
						dateArray.add(yearPosition, yearRenderer.toString());
					}
					if (dayPosition >= dateArray.size()) {
						dateArray.add(dayRenderer.toString());
					} else if (dayPosition != -1){
						dateArray.add(dayPosition, dayRenderer.toString());
					}
				}
			}

			if (Pattern.matches("^.+[/-\\\\s].+[/-\\\\s].+\\\\s(HH):(mm)$", localizedDateFormat) || dateTimeField.getType().equals("time") || dateTimeField.getType().equals("timestamp")) {
				StringBuilder timeRenderer = new StringBuilder();

				// if we have an input method of time-dropdown, then render two dropdowns
				if ("time-dropdown".equals(dateTimeField.getInputMethod())) {
					boolean isTwelveHour = "12".equals(dateTimeField.getClock());

					// write the select for hours
					timeRenderer.append("&nbsp;<select name=\"").append(UtilHttp.makeCompositeParam(paramName, "hour")).append("\"");
					timeRenderer.append(" id=\"").append(UtilHttp.makeCompositeParam(idName, "hour")).append("\" ");
					appendClassNames(timeRenderer,"hour time", context, modelFormField);
					if (isReadOnly)
						timeRenderer.append(" readonly=\"readonly\"");
					timeRenderer.append(">");

					// keep the two cases separate because it's hard to understand a combined loop
					if (isTwelveHour) {
						for (int i = 1; i <= 12; i++) {
							timeRenderer.append("<option value=\"").append(Integer.toString(i)).append("\"");
							if (calendar != null) {
								if (hour == 0) hour = 12;
								if (hour > 12) hour -= 12;
								if (i == hour) writer.append(" selected=\"selected\"");
							}
							timeRenderer.append(">").append(Integer.toString(i)).append("</option>");
						}
					} else {
						for (int i = 0; i < 24; i++) {
							timeRenderer.append("<option value=\"").append(Integer.toString(i)).append("\"");
							if (calendar != null && i == calendar.get(Calendar.HOUR_OF_DAY)) {
								timeRenderer.append(" selected=\"selected\"");
							}
							timeRenderer.append(">").append(Integer.toString(i)).append("</option>");
						}
					}

					// write the select for minutes
					timeRenderer.append("</select>");
					timeRenderer.append("<span id=\"time-separator\">:</span>");
					timeRenderer.append("<select name=\"");
					timeRenderer.append(UtilHttp.makeCompositeParam(paramName, "minutes")).append("\"");
					timeRenderer.append(" id=\"").append(UtilHttp.makeCompositeParam(idName, "minutes")).append("\" ");
					appendClassNames(timeRenderer,"minutes time", context, modelFormField);
					if (isReadOnly)
						timeRenderer.append(" readonly=\"readonly\"");
					timeRenderer.append(">");
					for (int i = 0; i < 60; i++) {
						timeRenderer.append("<option value=\"").append(Integer.toString(i)).append("\"");
						if (calendar != null && i == calendar.get(Calendar.MINUTE)) {
							timeRenderer.append(" selected=\"selected\"");
						}
						timeRenderer.append(">").append(Integer.toString(i)).append("</option>");
					}
					timeRenderer.append("</select>");

					// if 12 hour clock, write the AM/PM selector
					if (isTwelveHour) {
						String amSelected = ((calendar != null && calendar.get(Calendar.AM_PM) == Calendar.AM) ? "selected=\"selected\"" : "");
						String pmSelected = ((calendar != null && calendar.get(Calendar.AM_PM) == Calendar.PM) ? "selected=\"selected\"" : "");
						timeRenderer.append("<select name=\"").append(UtilHttp.makeCompositeParam(paramName, "ampm")).append("\"");
						timeRenderer.append(" id=\"").append(UtilHttp.makeCompositeParam(idName, "ampm")).append("\"");
						appendClassNames(timeRenderer, context, modelFormField);
						if (isReadOnly)
							timeRenderer.append(" readonly=\"readonly\"");
						timeRenderer.append(">");
						timeRenderer.append("<option value=\"AM\" ").append(amSelected).append(">AM</option>");
						timeRenderer.append("<option value=\"PM\" ").append(pmSelected).append(">PM</option>");
						timeRenderer.append("</select>");
					}

				} else if ("text".equals(dateTimeField.getInputMethod())) {
					timeRenderer.append("<input type=\"text\" ");
					if (isReadOnly)
						timeRenderer.append(" readonly=\"readonly\"");

					timeRenderer.append(" name=\"");
					timeRenderer.append(UtilHttp.makeCompositeParam(paramName, "hour"));
					timeRenderer.append('"');
					timeRenderer.append(" id=\"");
					timeRenderer.append(UtilHttp.makeCompositeParam(idName, "hour"));
					timeRenderer.append('"');

					appendClassNames(timeRenderer, "hour time", context, modelFormField);
					timeRenderer.append(" value=\"");
					if (hour > 0)
						timeRenderer.append(hour);
					timeRenderer.append("\" />");
					timeRenderer.append("<span id=\"time-separator\">:</span>");

					timeRenderer.append("<input type=\"text\" ");
					if (isReadOnly)
						timeRenderer.append(" readonly=\"readonly\"");

					timeRenderer.append(" name=\"");
					timeRenderer.append(UtilHttp.makeCompositeParam(paramName, "minutes"));
					timeRenderer.append('"');
					timeRenderer.append(" id=\"");
					timeRenderer.append(UtilHttp.makeCompositeParam(idName, "minutes"));
					timeRenderer.append('"');

					appendClassNames(timeRenderer, "minutes time", context, modelFormField);
					timeRenderer.append(" value=\"");
					if (minutes > 0)
						timeRenderer.append(minutes);
					timeRenderer.append("\" />");
				}

				if (UtilValidate.isEmpty(firstFieldName)) {
					firstFieldName = UtilHttp.makeCompositeParam(paramName, "hour");
				}

				dateArray.add(timeRenderer.toString());
			}

			// create a hidden field for the composite type, which is a Timestamp
			StringBuilder compositeTypeRenderer = new StringBuilder("<input type=\"hidden\" name=\"");
			compositeTypeRenderer.append(UtilHttp.makeCompositeParam(paramName, "compositeType"));
			compositeTypeRenderer.append("\" value=\"").append(dateTimeField.getType().substring(0, 1).toUpperCase()).append(dateTimeField.getType().substring(1)).append("\"/>");
			dateArray.add(compositeTypeRenderer.toString());

			for(String stringToRender : dateArray) {
				writer.append(stringToRender);
			}
		}
		else {
			String value =  dateTimeField.getValue(context);
			DateFormat dfDate = UtilDateTime.toDateFormat(UtilDateTime.getDateFormat((Locale)context.get("locale")),(TimeZone)context.get("timeZone"), (Locale)context.get("locale"));
			try {
				if(UtilValidate.isNotEmpty(value) && "time-dropdown".equals(dateTimeField.getInputMethod())) {
					Date d = UtilDateTime.toSqlDate(month, day, year);
					value = dfDate.format(d);
				}
			} catch(Exception e) {
				value=null;
			}

			//this.addAsterisks(writer, context, modelFormField);

			// search for a localized label for the icon
			if (uiLabelMap != null) {
				localizedIconTitle = uiLabelMap.get("CommonViewCalendar");
			}
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"paramName\" value=\"").append(("time-dropdown".equals(dateTimeField.getInputMethod()) ? UtilHttp.makeCompositeParam(paramName, "date") : paramName)).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"time\" value=\"").append(Boolean.toString("time".equals(dateTimeField.getType()))).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"shortDateInput\" value=\"").append(Boolean.toString(shortDateInput)).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"dateTimeValue\" value=\"").append(UtilHttp.encodeBlanks(dateTimeValue)).append("\"/>");
			if (dateTimeField.getShowFormat() && UtilValidate.isEmpty(value))
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"localizedDefaultFormatToShow\" value=\"").append(localizedDefaultFormatToShow).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"localizedInputTitle\" value=\"").append(localizedInputTitle).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"localizedIconTitle\" value=\"").append(localizedIconTitle).append("\"/>");
			String yearRange = "";
			if (UtilValidate.isNotEmpty(dateTimeField.getYearRange())) {
				yearRange = dateTimeField.getYearRange().toString();
			}
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"yearRange\" value=\"").append(yearRange).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"localizedValue\" value=\"").append(value).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"size\" value=\"").append(Integer.toString(size)).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"maxlength\" value=\"").append(Integer.toString(maxlength)).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"locale\" value=\"").append(UtilHttp.getLocale(request).getLanguage()).append("\"/>");
			StringBuilder allClass = new StringBuilder();
			appendClassNamesOnly(allClass, null, context, modelFormField);
			if (isReadOnly)
				allClass.append(" readonly");
			if (allClass.length() > 0)
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"classNames\" value=\""+ allClass.toString() +"\"/>");
		}
		writer.append("</div>");
		appendTooltip(writer, context, modelFormField);

		appendWhitespace(writer);
	}

	private String makeCsvString(List<String> list) {
		return this.makeCsvString(list, ", ");
	}
	/**
	 * Tranform a string list into csv string surrounded with []
	 * @param list
	 * @return
	 */
	private String makeCsvString(List<String> list, String delim) {
		if (UtilValidate.isEmpty(list))
			return null;
		if (UtilValidate.isEmpty(delim))
			delim = ", ";
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (String item: list) {
			if (sb.length()>1) {
				sb.append(delim);
			}
			sb.append(item);
		}
		sb.append("]");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderDropDownField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.DropDownField)
	 */
	@SuppressWarnings("unchecked")
	public void renderDropDownField(Appendable writer, Map<String, Object> context, DropDownField dropDownField) throws IOException {

		ModelFormField modelFormField = dropDownField.getModelFormField();
		ModelForm modelForm = modelFormField.getModelForm();
		ModelFormField.AutoComplete autoComplete = dropDownField.getAutoComplete();
		
		boolean ajaxEnabled = this.javaScriptEnabled;
		List<ModelFormField.OptionValue> allOptionValues = null;
		
		String event = modelFormField.getEvent();
		String action = modelFormField.getAction(context);

		String currentValue = modelFormField.getEntry(context, dropDownField.getNoCurrentSelectedKey(context), true);
		// Get the current value's description from the option value. If there
		// is a localized version it will be there.
		String currentDescription = null;
		
		Map<String, ? extends Object> currentValueMap = null;
		if (UtilValidate.isNotEmpty(currentValue)) {
		    ModelFormField.OptionValue optionValueFound = null;
            boolean renderByPK = "true".equals(UtilProperties.getPropertyValue("BaseConfig", "HtmlFormRenderer.autocomplete.renderByPK"));
            if (renderByPK && UtilValidate.isNotEmpty(dropDownField.getOptionSources()) && ObjectType.instanceOf(EntityOptions.class, dropDownField.getOptionSources().get(0))) {
                EntityOptions entityOptions = (EntityOptions)dropDownField.getOptionSources().get(0);
                optionValueFound = entityOptions.findOptionValueByPK(StringEscapeUtils.unescapeHtml(currentValue), context, WidgetWorker.getDelegator(context));
            } else {
                allOptionValues = allOptionValues != null ? allOptionValues : dropDownField.getAllOptionValues(context, WidgetWorker.getDelegator(context));
                for (ModelFormField.OptionValue optionValue : allOptionValues) {
                    if (encode(optionValue.getKey(), modelFormField, context).equals(currentValue)) {
                        optionValueFound = optionValue;
                        break;
                    }
                }
            }
            if (optionValueFound != null) {
                currentDescription = optionValueFound.getDescription();
                currentValueMap = optionValueFound.getAuxiliaryMap();
            }
		}
		
		// uso attributo tooltip della lookup per popolare il tooltip,
		// i valori della FlexibleStringExpander sono un merge di context e currentValueMap
		MapStack<String> localContext = null;
		String tooltipValue = "";
		if(UtilValidate.isNotEmpty(currentValueMap)){
			localContext = MapStack.create(context);
    		localContext.push((Map<String, Object>) currentValueMap);
		}
		if(dropDownField.showTooltip()){
			if(UtilValidate.isNotEmpty(localContext) && UtilValidate.isNotEmpty(dropDownField.getTooltip(localContext))){
				tooltipValue = dropDownField.getTooltip(localContext);
			} else if(UtilValidate.isNotEmpty(currentDescription)){
				tooltipValue = currentDescription;
			}
		}

		String classNames = "";
		boolean isReadOnly = dropDownField.isReadOnly(context);
		if (isReadOnly)
			classNames = "readonly";

		String parameterName = modelFormField.getParameterName(context);
		String idName = modelFormField.getCurrentContainerId(context);

		if (ajaxEnabled && dropDownField.isDropList()) {
			writer.append("<div ");

			appendClassNames(writer, concatClassNames(classNames, "droplist_field"), context, modelFormField);
			writer.append(" id=\"");
			writer.append(idName);
			writer.append("\">");

			//Autocompleter option
			if (UtilValidate.isNotEmpty(autoComplete.getChoices())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"choices\" value=\"");
				writer.append(autoComplete.getChoices());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getFrequency())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"frequency\" value=\"");
				writer.append(autoComplete.getFrequency());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getFullSearch())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"fullSearch\" value=\"");
				writer.append(autoComplete.getFullSearch());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getIgnoreCase())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"ignoreCase\" value=\"");
				writer.append(autoComplete.getIgnoreCase());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getMinChars())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"minChars\" value=\"");
				writer.append(autoComplete.getMinChars());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getPartialChars())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"partialChars\" value=\"");
				writer.append(autoComplete.getPartialChars());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getPartialSearch())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"partialSearch\" value=\"");
				writer.append(autoComplete.getPartialSearch());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getPartialSearch())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"partialSearch\" value=\"");
				writer.append(autoComplete.getPartialSearch());
				writer.append("\"/>");
			}

			//Autocompleter field
			if (!dropDownField.isLocalAutocompleter()) {
				String target = autoComplete.getTarget();
				if (UtilValidate.isEmpty(target)) {
					target = "ajaxAutocompleteOptions";
				}
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"target\" value=\"");
				writer.append(this.rh.makeLink(request, response, target));
				writer.append("\"/>");

				List<OptionSource> allOptionSource = dropDownField.getOptionSources();
				if (UtilValidate.isNotEmpty(allOptionSource) && ObjectType.instanceOf(EntityOptions.class, allOptionSource.get(0))) {
					List<EntityOptions> allEntityOption = UtilGenerics.checkList(allOptionSource);

					List<String> entityNameList = null;
					List<String> selectFieldList = null;
					List<String> findFieldList = null;					
					List<String> orderByFieldList = null;
					List<String> displayFieldList = null;
                    Map<String, String> descriptionFieldMap = null;
					List<String> constraintParm = null;
					List<String> distinctList = null;
					for(EntityOptions entityOption : allEntityOption) {
						boolean distinct = entityOption.getDistinct();
						if (UtilValidate.isEmpty(distinctList)) {
							distinctList = new FastList<String>();
						}
						if (distinct) {
							distinctList.add("Y");
						}
						else {
							distinctList.add("N");
						}

						String entityName = entityOption.getEntityName(context);
						if (UtilValidate.isNotEmpty(entityName)) {
							if (UtilValidate.isEmpty(entityNameList)) {
								entityNameList = new FastList<String>();
							}
							entityNameList.add(entityName);
						}

						List<String> originalSelectField = entityOption.getSelectField(context);
						if (UtilValidate.isNotEmpty(originalSelectField)) {
							if (UtilValidate.isEmpty(selectFieldList)) {
								selectFieldList = new FastList<String>();
							}
							selectFieldList.add(this.makeCsvString(originalSelectField));
						}
						
						
						List<String> originalFindField = entityOption.getFindField(context);
                        if (UtilValidate.isNotEmpty(originalFindField)) {
                            if (UtilValidate.isEmpty(findFieldList)) {
                                findFieldList = new FastList<String>();
                            }
                            findFieldList.add(this.makeCsvString(originalFindField));
                        }

						List<String> originalOrderByList = entityOption.getOrderByField(context);
						if (UtilValidate.isNotEmpty(originalOrderByList)) {
							if (UtilValidate.isEmpty(orderByFieldList)) {
								orderByFieldList = new FastList<String>();
							}
							orderByFieldList.add(this.makeCsvString(originalOrderByList));
						}

						List<String> originalDisplayFieldList = entityOption.getDisplayField(context);
						if (UtilValidate.isNotEmpty(originalDisplayFieldList)) {
							if (UtilValidate.isEmpty(displayFieldList)) {
								displayFieldList = new FastList<String>();
							}
							displayFieldList.add(this.makeCsvString(originalDisplayFieldList));
						}

                        descriptionFieldMap = entityOption.getDescriptionFieldMap(context);

                        if (UtilValidate.isNotEmpty(entityOption.getConstraintList())) {
                            for (EntityFinderUtil.ConditionExpr constraint: entityOption.getConstraintList()) {
								List<String> parmsList = FastList.newInstance();
								parmsList.add(constraint.getFieldName(context));
								parmsList.add(constraint.getOperator(context));
								String value = "";
						        Object valueObj = constraint.getValue(context);
						        if (valueObj instanceof List) {
						            value = StringUtil.join((List<?>)valueObj, ",");
						        } else {
						            value = valueObj != null ? valueObj.toString() : "";
						        }
								String lookupForFieldName = constraint.getLookupForFieldName(context);
	                            List<String> lookupForFieldNameSplittedList = StringUtil.split(lookupForFieldName, ",");
								if (UtilValidate.isNotEmpty(lookupForFieldNameSplittedList)) {
								    for (String lookupForFieldNameItem : lookupForFieldNameSplittedList) {
	                                    if (UtilValidate.isNotEmpty(lookupForFieldNameItem)) {
	                                        if (value.length() > 0)
                                                value += ",";
                                            value += "field:" + lookupForFieldNameItem;
	                                    }
									}
	                            }
								if (UtilValidate.isNotEmpty(value)) {
									parmsList.add(value);
								}

								if (UtilValidate.isEmpty(constraintParm)) {
									constraintParm = new FastList<String>();
								}
								constraintParm.add(this.makeCsvString(parmsList, "| "));
							}
						}
					}

					writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"entityName\" value=\"");
					writer.append(this.makeCsvString(entityNameList));
					writer.append("\"/>");

					if (UtilValidate.isNotEmpty(distinctList)) {
						writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"distincts\" value=\"");
						writer.append(this.makeCsvString(distinctList, "; "));
						writer.append("\"/>");
					}

					if (UtilValidate.isNotEmpty(selectFieldList)) {
						writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"selectFields\" value=\"");
						writer.append(this.makeCsvString(selectFieldList, "; "));
						writer.append("\"/>");
					}
					
					if (UtilValidate.isNotEmpty(findFieldList)) {
                        writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"findFields\" value=\"");
                        writer.append(this.makeCsvString(findFieldList, "; "));
                        writer.append("\"/>");
                    }

					if (UtilValidate.isNotEmpty(orderByFieldList)) {
						writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"sortByFields\" value=\"");
						writer.append(this.makeCsvString(orderByFieldList, "; "));
						writer.append("\"/>");
					}

					if (UtilValidate.isNotEmpty(displayFieldList)) {
                        writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"displayFields\" value=\"");
                        writer.append(this.makeCsvString(displayFieldList, "; "));
                        writer.append("\"/>");
                    }

                    if (UtilValidate.isNotEmpty(descriptionFieldMap)) {
                        for (String fieldName : descriptionFieldMap.keySet()) {
                            writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"");
                            writer.append(fieldName).append("_description");
                            writer.append("\" value=\"");
                            writer.append(descriptionFieldMap.get(fieldName));
                            writer.append("\"/>");
                        }
                    }

					if (UtilValidate.isNotEmpty(constraintParm)) {
						List<String> tmpConstrParam = new FastList<String>();
						tmpConstrParam.add(this.makeCsvString(constraintParm, "! "));

						constraintParm = tmpConstrParam;

						writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"constraintFields\" value=\"");
						writer.append(UtilFormatOut.encodeXmlValue(this.makeCsvString(constraintParm, "; ")));
						writer.append("\"/>");
					}

					writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"saveView\" value=\"N\"/>");
				}
			} else {
				writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"localAutocompleter\" value=\"Y\"/>");
				//Considerare il caso di Autocompleter.Local
				allOptionValues = allOptionValues != null ? allOptionValues : dropDownField.getAllOptionValues(context, WidgetWorker.getDelegator(context));
				for (ModelFormField.OptionValue optionValue: allOptionValues) {
					writer.append("<input type=\"hidden\" class=\"autocompleter_local_data\" id=\"" + idName + "_" + optionValue.getKey() + "\" name=\"" + parameterName + "_" + optionValue.getKey() + "\"");
					writer.append(" value=\"" + optionValue.getDescription() + "\"/>");
				}
			}

			//Now add entity-key-field if any
			if (UtilValidate.isNotEmpty(dropDownField.getDropListKeyField())) {
				writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"entityKeyField\" value=\"");
				writer.append(dropDownField.getDropListKeyField());
				writer.append("\"/>");
			}
			//Now add entity-description-field if any
			if (UtilValidate.isNotEmpty(dropDownField.getDropListDisplayField(context))) {
				writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"entityDescriptionField\" value=\"");
				writer.append(dropDownField.getDropListDisplayField(context));
				writer.append("\"/>");
			}

			writer.append("<div class=\"droplist_container\">");

			//Code field
			//writer.append("<input type=\"hidden\" class=\"droplist_code_field\" name=\"");
			writer.append("<input type=\"hidden\" name=\"");
			writer.append(parameterName);
			writer.append("\" value=\"");
			String currentConvertedValue = modelFormField.getEntry(context, dropDownField.getNoCurrentSelectedKey(context), true);
			if (UtilValidate.isNotEmpty(currentConvertedValue)) {
				writer.append(currentConvertedValue);
			} else {
				writer.append(currentValue);
			}
			writer.append("\" ");
			appendClassNames(writer, concatClassNames(classNames, "droplist_code_field"), context, modelFormField);
			writer.append("/>");

			//            writer.append("<div ");
			if ("list".equals(modelForm.getType()) || "multi".equals(modelForm.getType())) {
				//            	writer.append("\"/>");
				writer.append("<div ");
				appendClassNames(writer, concatClassNames(classNames, "droplist_input_field"), context, modelFormField);
				writer.append("style=\"width:90%\">");
			}

			writer.append("<input type=\"text\"");
			writer.append("id=\"" + idName + "_edit_field" + "\"");

			if (isReadOnly)
				writer.append(" readonly=\"readonly\"");

			writer.append(" name=\"");
			writer.append("description_" + parameterName);
			writer.append("\"");

			if (UtilValidate.isNotEmpty(dropDownField.getSize())&&(!dropDownField.getSize().equals("1"))) {
				writer.append(" size=\"");
				writer.append(dropDownField.getSize());
				writer.append("\"");
			}

			if (UtilValidate.isNotEmpty(dropDownField.getMaxLength())&&(!dropDownField.getMaxLength().equals("1"))) {
				writer.append(" maxlength=\"");
				writer.append(dropDownField.getMaxLength());
				writer.append("\"");
			}

			writer.append("value=\"");
			if (UtilValidate.isNotEmpty(currentDescription)) {
				writer.append(currentDescription);
			}
			writer.append("\" ");

			appendClassNames(writer, concatClassNames(classNames, "droplist_edit_field"), context, modelFormField);

			if(dropDownField.showTooltip() && UtilValidate.isNotEmpty(tooltipValue)) {
				writer.append(" title=\"");
				writer.append(tooltipValue);
				writer.append("\"");
			}
			
			writer.append("/>");

			//writer.append("</div>");

			// writer.append("<div ");
			if ("list".equals(modelForm.getType()) || "multi".equals(modelForm.getType())) {
				writer.append("</div>");
				writer.append("<div ");
				appendClassNames(writer, concatClassNames(classNames, "droplist_icon"), context, modelFormField);
				writer.append(">");
			}

			writer.append("<span class=\"droplist-anchor fa fa-2x\">");
			writer.append("<a class=\"droplist_submit_field\" href=\"#\"></a>");
			writer.append("</span>");

			if ("list".equals(modelForm.getType()) || "multi".equals(modelForm.getType())) {
				writer.append("</div>");
			}

			//Chiudo droplist_container
			writer.append("</div>");

			//Chiudo droplist_field
			writer.append("</div>");
		} else {
			writer.append("<select");

			if (isReadOnly)
				writer.append(" disabled=\"disabled\"");

			writer.append(" name=\"");
			writer.append(parameterName);

			writer.append('"');

			if (UtilValidate.isNotEmpty(idName)) {
				writer.append(" id=\"");
				writer.append(idName);
				writer.append('"');
			}

			appendClassNames(writer, context, modelFormField);

			if (dropDownField.isAllowMultiple()) {
				writer.append(" multiple=\"multiple\"");
			}

			int otherFieldSize = dropDownField.getOtherFieldSize();
			String otherFieldName = dropDownField.getParameterNameOther(context);
			if (otherFieldSize > 0) {
				//writer.append(" onchange=\"alert('ONCHANGE, process_choice:' + process_choice)\"");
				//writer.append(" onchange='test_js()' ");
				writer.append(" onchange=\"process_choice(this,document.");
				writer.append(modelForm.getName());
				writer.append(".");
				writer.append(otherFieldName);
				writer.append(")\" ");
			}


			if (UtilValidate.isNotEmpty(event) && UtilValidate.isNotEmpty(action)) {
				writer.append(" ");
				writer.append(event);
				writer.append("=\"");
				writer.append(action);
				writer.append('"');
			}

			writer.append(" size=\"" + dropDownField.getSize() + "\">");

			allOptionValues = allOptionValues != null ? allOptionValues : dropDownField.getAllOptionValues(context, WidgetWorker.getDelegator(context));

			// if the current value should go first, stick it in
			if (UtilValidate.isNotEmpty(currentValue) && "first-in-list".equals(dropDownField.getCurrent())) {
				writer.append("<option");
				writer.append(" selected=\"selected\"");
				writer.append(" value=\"");
				String currentConvertedValue = modelFormField.getEntry(context, dropDownField.getNoCurrentSelectedKey(context), false);
				if (UtilValidate.isNotEmpty(currentConvertedValue))
					writer.append(currentConvertedValue);
				else
					writer.append(currentValue);
				writer.append("\">");
				String explicitDescription = (currentDescription != null ? currentDescription : dropDownField.getCurrentDescription(context));
				if (UtilValidate.isNotEmpty(explicitDescription)) {
					writer.append(explicitDescription);
				} else {
					writer.append(ModelFormField.FieldInfoWithOptions.getDescriptionForOptionKey(currentValue, allOptionValues));
				}
				writer.append("</option>");

				//add a "separator" option
				writer.append("<option value=\"");
				if (UtilValidate.isNotEmpty(currentConvertedValue))
					writer.append(currentConvertedValue);
				else
					writer.append(currentValue);
				writer.append("\">---</option>");

			}

			// if allow empty is true, add an empty option
			if (dropDownField.isAllowEmpty()) {
				writer.append("<option value=\"\">&nbsp;</option>");
			}

			// list out all options according to the option list
			for (ModelFormField.OptionValue optionValue: allOptionValues) {
				String noCurrentSelectedKey = dropDownField.getNoCurrentSelectedKey(context);
				writer.append("<option");
				// if current value should be selected in the list, select it
				if (UtilValidate.isNotEmpty(currentValue) && currentValue.equals(optionValue.getKey()) && "selected".equals(dropDownField.getCurrent())) {
					writer.append(" selected=\"selected\"");
				} else if (UtilValidate.isEmpty(currentValue) && noCurrentSelectedKey != null && noCurrentSelectedKey.equals(optionValue.getKey())) {
					writer.append(" selected=\"selected\"");
				}

				writer.append(" value=\"");
				writer.append(optionValue.getKey());
				writer.append("\">");
				writer.append(optionValue.getDescription());
				writer.append("</option>");
			}

			writer.append("</select>");


			// Adapted from work by Yucca Korpela
			// http://www.cs.tut.fi/~jkorpela/forms/combo.html
			if (otherFieldSize > 0) {

				String fieldName = modelFormField.getParameterName(context);
				Map<String, ? extends Object> dataMap = modelFormField.getMap(context);
				if (dataMap == null) {
					dataMap = context;
				}
				Object otherValueObj = dataMap.get(otherFieldName);
				String otherValue = (otherValueObj == null) ? "" : otherValueObj.toString();

				writer.append("<noscript>");
				writer.append("<input type='text' name='");
				writer.append(otherFieldName);
				writer.append("'/> ");
				writer.append("</noscript>");
				writer.append("\n<script type='text/javascript' language='JavaScript'><!--");
				writer.append("\ndisa = ' disabled';");
				writer.append("\nif(other_choice(document.");
				writer.append(modelForm.getName());
				writer.append(".");
				writer.append(fieldName);
				writer.append(")) disa = '';");
				writer.append("\ndocument.write(\"<input type=");
				writer.append("'text' name='");
				writer.append(otherFieldName);
				writer.append("' value='");
				writer.append(otherValue);
				writer.append("' size='");
				writer.append(Integer.toString(otherFieldSize));
				writer.append("' ");
				writer.append("\" +disa+ \" onfocus='check_choice(document.");
				writer.append(modelForm.getName());
				writer.append(".");
				writer.append(fieldName);
				writer.append(")'/>\");");
				writer.append("\nif(disa && document.styleSheets)");
				writer.append(" document.");
				writer.append(modelForm.getName());
				writer.append(".");
				writer.append(otherFieldName);
				writer.append(".style.visibility  = 'hidden';");
				writer.append("\n//--></script>");
			}
		}

		this.makeHyperlinkString(writer, dropDownField.getSubHyperlink(), context);

		this.appendTooltip(writer, context, modelFormField);

		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderCheckField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.CheckField)
	 */
	public void renderCheckField(Appendable writer, Map<String, Object> context, CheckField checkField) throws IOException {
		ModelFormField modelFormField = checkField.getModelFormField();
		ModelForm modelForm = modelFormField.getModelForm();
		String currentValue = modelFormField.getEntry(context);
		Boolean allChecked = checkField.isAllChecked(context);

		List<ModelFormField.OptionValue> allOptionValues = checkField.getAllOptionValues(context, WidgetWorker.getDelegator(context));
		String event = modelFormField.getEvent();
		String action = modelFormField.getAction(context);

		// list out all options according to the option list
		for (ModelFormField.OptionValue optionValue: allOptionValues) {

			writer.append("<input type=\"checkbox\"");

			appendClassNames(writer, context, modelFormField);

			// if current value should be selected in the list, select it
			if (Boolean.TRUE.equals(allChecked)) {
				writer.append(" checked=\"checked\"");
			} else if (Boolean.FALSE.equals(allChecked)) {
				// do nothing
			} else if (UtilValidate.isNotEmpty(currentValue) && currentValue.equals(optionValue.getKey())) {
				writer.append(" checked=\"checked\"");
			}
			writer.append(" name=\"");
			writer.append(modelFormField.getParameterName(context));
			writer.append('"');
			writer.append(" value=\"");
			writer.append(encode(optionValue.getKey(), modelFormField, context));
			writer.append("\"");

			if (UtilValidate.isNotEmpty(event) && UtilValidate.isNotEmpty(action)) {
				writer.append(" ");
				writer.append(event);
				writer.append("=\"");
				writer.append(action);
				writer.append('"');
			}

			writer.append("/>");

			writer.append(encode(optionValue.getDescription(), modelFormField, context));
		}

		this.appendTooltip(writer, context, modelFormField);

		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderRadioField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.RadioField)
	 */
	public void renderRadioField(Appendable writer, Map<String, Object> context, RadioField radioField) throws IOException {
		ModelFormField modelFormField = radioField.getModelFormField();
		ModelForm modelForm = modelFormField.getModelForm();
		List<ModelFormField.OptionValue> allOptionValues = radioField.getAllOptionValues(context, WidgetWorker.getDelegator(context));
		String currentValue = modelFormField.getEntry(context);
		String event = modelFormField.getEvent();
		String action = modelFormField.getAction(context);

		// list out all options according to the option list
		for (ModelFormField.OptionValue optionValue: allOptionValues) {

			writer.append("<span");

			appendClassNames(writer, context, modelFormField);

			writer.append("><input type=\"radio\"");

			// if current value should be selected in the list, select it
			String noCurrentSelectedKey = radioField.getNoCurrentSelectedKey(context);
			if (UtilValidate.isNotEmpty(currentValue) && currentValue.equals(optionValue.getKey())) {
				writer.append(" checked=\"checked\"");
			} else if (UtilValidate.isEmpty(currentValue) && noCurrentSelectedKey != null && noCurrentSelectedKey.equals(optionValue.getKey())) {
				writer.append(" checked=\"checked\"");
			}
			writer.append(" name=\"");
			writer.append(modelFormField.getParameterName(context));
			writer.append('"');
			writer.append(" value=\"");
			writer.append(encode(optionValue.getKey(), modelFormField, context));
			writer.append("\"");

			if (UtilValidate.isNotEmpty(event) && UtilValidate.isNotEmpty(action)) {
				writer.append(" ");
				writer.append(event);
				writer.append("=\"");
				writer.append(action);
				writer.append('"');
			}

			writer.append("/>");

			writer.append(encode(optionValue.getDescription(), modelFormField, context));
			writer.append("</span>");
		}

		this.appendTooltip(writer, context, modelFormField);

		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderSubmitField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.SubmitField)
	 */
	public void renderSubmitField(Appendable writer, Map<String, Object> context, SubmitField submitField) throws IOException {
		ModelFormField modelFormField = submitField.getModelFormField();
		ModelForm modelForm = modelFormField.getModelForm();
		String event = null;
		String action = null;
		String confirmation =  encode(submitField.getConfirmation(context), modelFormField, context);

		if ("text-link".equals(submitField.getButtonType())) {
			writer.append("<a");

			appendClassNames(writer, context, modelFormField);

			if (UtilValidate.isNotEmpty(confirmation)) {
				writer.append(" onclick=\"return confirm('");
				writer.append(confirmation);
				writer.append("'); \" ");
			}

			writer.append(" href=\"javascript:document.");
			writer.append(modelForm.getCurrentFormName(context));
			writer.append(".submit()\">");

			writer.append(encode(modelFormField.getTitle(context), modelFormField, context));

			writer.append("</a>");
		} else if ("image".equals(submitField.getButtonType())) {
			writer.append("<input type=\"image\"");

			appendClassNames(writer, context, modelFormField);

			writer.append(" name=\"");
			writer.append(modelFormField.getParameterName(context));
			writer.append('"');

			String title = modelFormField.getTitle(context);
			if (UtilValidate.isNotEmpty(title)) {
				writer.append(" alt=\"");
				writer.append(encode(title, modelFormField, context));
				writer.append('"');
			}

			writer.append(" src=\"");
			this.appendContentUrl(writer, submitField.getImageLocation());
			writer.append('"');

			event = modelFormField.getEvent();
			action = modelFormField.getAction(context);
			if (UtilValidate.isNotEmpty(event) && UtilValidate.isNotEmpty(action)) {
				writer.append(" ");
				writer.append(event);
				writer.append("=\"");
				writer.append(action);
				writer.append('"');
			}

			if (UtilValidate.isNotEmpty(confirmation)) {
				writer.append("onclick=\" return confirm('");
				writer.append(confirmation);
				writer.append("); \" ");
			}

			writer.append("/>");
		} else {
			// default to "button"

			String formId = modelForm.getContainerId(context);
			List<ModelForm.UpdateArea> updateAreas = null;
			//            List<ModelForm.UpdateArea> updateAreas = modelForm.getOnSubmitUpdateAreas();
			// This is here for backwards compatibility. Use on-event-update-area
			// elements instead.
			String backgroundSubmitRefreshTarget = submitField.getBackgroundSubmitRefreshTarget(context);
			if (UtilValidate.isNotEmpty(backgroundSubmitRefreshTarget)) {
				if (updateAreas == null) {
					updateAreas = FastList.newInstance();
				}
				updateAreas.add(new ModelForm.UpdateArea("submit", formId, backgroundSubmitRefreshTarget));
			}

			boolean ajaxEnabled = (updateAreas != null || UtilValidate.isNotEmpty(backgroundSubmitRefreshTarget)) && this.javaScriptEnabled;
			if (ajaxEnabled) {
				writer.append("<input type=\"button\"");
			} else {
				writer.append("<input type=\"submit\"");
			}

			appendClassNames(writer, context, modelFormField);

			writer.append(" name=\"");
			writer.append(modelFormField.getParameterName(context, true));
			writer.append('"');

			String title = modelFormField.getTitle(context);
			if (UtilValidate.isNotEmpty(title)) {
				writer.append(" value=\"");
				writer.append(encode(title, modelFormField, context));
				writer.append('"');
			}


			event = modelFormField.getEvent();
			action = modelFormField.getAction(context);
			if (UtilValidate.isNotEmpty(event) && UtilValidate.isNotEmpty(action)) {
				writer.append(" ");
				writer.append(event);
				writer.append("=\"");
				writer.append(action);
				writer.append('"');
			} else {
				//add single click JS onclick
				// disabling for now, using form onSubmit action instead: writer.append(singleClickAction);
			}

			if (ajaxEnabled) {
				writer.append(" onclick=\"");
				if (UtilValidate.isNotEmpty(confirmation)) {
					writer.append("if  (confirm('");
					writer.append(confirmation);
					writer.append(");) ");
				}
				writer.append("ajaxSubmitFormUpdateAreas('");
				writer.append(formId);
				writer.append("','").append(getOnlySubmitTargetUpdateArea(updateAreas, null, context));
				writer.append("','").append(createAjaxParamsFromUpdateAreas(updateAreas, null, context));
				writer.append("')\"");
			}

			writer.append("/>");
		}

		this.appendTooltip(writer, context, modelFormField);

		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderResetField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.ResetField)
	 */
	public void renderResetField(Appendable writer, Map<String, Object> context, ResetField resetField) throws IOException {
		ModelFormField modelFormField = resetField.getModelFormField();

		writer.append("<input type=\"reset\"");

		appendClassNames(writer, context, modelFormField);

		writer.append(" name=\"");
		writer.append(modelFormField.getParameterName(context));
		writer.append('"');

		String title = modelFormField.getTitle(context);
		if (UtilValidate.isNotEmpty(title)) {
			writer.append(" value=\"");
			writer.append(encode(title, modelFormField, context));
			writer.append('"');
		}

		writer.append("/>");

		this.appendTooltip(writer, context, modelFormField);

		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderHiddenField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.HiddenField)
	 */
	public void renderHiddenField(Appendable writer, Map<String, Object> context, HiddenField hiddenField) throws IOException {
		ModelFormField modelFormField = hiddenField.getModelFormField();
		String value = hiddenField.getValue(context);
		this.renderHiddenField(writer, context, modelFormField, value);
	}

	public void renderHiddenField(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, String value) throws IOException {
		writer.append("<input type=\"hidden\"");

		writer.append(" name=\"");
		writer.append(modelFormField.getParameterName(context));
		writer.append('"');

		if (UtilValidate.isNotEmpty(value)) {
			writer.append(" value=\"");
			writer.append(value);
			writer.append('"');
		}

		appendClassNames(writer, null, context, modelFormField);

		writer.append("/>");
		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderIgnoredField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.IgnoredField)
	 */
	public void renderIgnoredField(Appendable writer, Map<String, Object> context, IgnoredField ignoredField) throws IOException {
		// do nothing, it's an ignored field; could add a comment or something if we wanted to
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFieldTitle(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField)
	 */
	public void renderFieldTitle(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
		String tempTitleText = modelFormField.getTitle(context);
		String titleText = UtilHttp.encodeAmpersands(tempTitleText);
		if (UtilValidate.isNotEmpty(titleText)) {
			if (UtilValidate.isNotEmpty(modelFormField.getTitleStyle())) {
				writer.append("<span class=\"");
				writer.append(modelFormField.getTitleStyle());
				writer.append("\">");
			}
			if (modelFormField.getRequiredField() && "multi".equals(modelFormField.getModelForm().getType()))
				addAsterisks(writer, context, modelFormField);

			if (" ".equals(titleText)) {
				// If the title content is just a blank then render it colling renderFormatEmptySpace:
				// the method will set its content to work fine in most browser
				this.renderFormatEmptySpace(writer, context, modelFormField.getModelForm());
			} else {
				renderHyperlinkTitle(writer, context, modelFormField, titleText);
			}

			if (UtilValidate.isNotEmpty(modelFormField.getTitleStyle())) {
				writer.append("</span>");
			}

			//appendWhitespace(writer);
		}
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFieldTitle(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField)
	 */
	public void renderSingleFormFieldTitle(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
		boolean requiredField = modelFormField.getRequiredField();
		if (requiredField) {

			String requiredStyle = modelFormField.getRequiredFieldStyle();
			if (UtilValidate.isEmpty(requiredStyle)) {
				requiredStyle = modelFormField.getTitleStyle();
			}

			if (UtilValidate.isNotEmpty(requiredStyle)) {
				writer.append("<span class=\"");
				writer.append(requiredStyle);
				writer.append("\">");
			}
			renderHyperlinkTitle(writer, context, modelFormField, modelFormField.getTitle(context));
			if (UtilValidate.isNotEmpty(requiredStyle)) {
				writer.append("</span>");
			}

			//appendWhitespace(writer);
		} else {
			renderFieldTitle(writer, context, modelFormField);
		}
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormOpen(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm)
	 */
	public void renderFormOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
	    this.widgetCommentsEnabled = ModelWidget.widgetBoundaryCommentsEnabled(context);
		renderBeginningBoundaryComment(writer, "Form Widget - Form Element", modelForm);
		writer.append("<form method=\"post\" ");
		String targetType = modelForm.getTargetType();
		String targ = modelForm.getTarget(context, targetType);
		// The 'action' attribute is mandatory in a form definition,
		// even if it is empty.
		writer.append(" action=\"");
		if (UtilValidate.isNotEmpty(targ)) {
			//this.appendOfbizUrl(writer, "/" + targ);

			ModelForm.UpdateArea onlySubmitUpdateArea = getOnlySubmitUpdateArea(modelForm.getOnSubmitUpdateAreas());
			if (UtilValidate.isNotEmpty(onlySubmitUpdateArea) && this.javaScriptEnabled) {
				//writer.append(onlySubmitUpdateArea.getAreaTarget(context));
				targ = onlySubmitUpdateArea.getAreaTarget(context);
			} /*else {
                WidgetWorker.buildHyperlinkUrl(writer, targ, targetType, request, response, context);
            }*/
			WidgetWorker.buildHyperlinkUrl(writer, targ, targetType, null, null, false, false, true, request, response, context);
		}
		writer.append("\" ");

		String formType = modelForm.getType();
		if (formType.equals("upload") ) {
			writer.append(" enctype=\"multipart/form-data\"");
		}

		String targetWindow = modelForm.getTargetWindow(context);
		if (UtilValidate.isNotEmpty(targetWindow)) {
			writer.append(" target=\"");
			writer.append(targetWindow);
			writer.append("\"");
		}

		String containerId =  modelForm.getContainerId(context);
		if (UtilValidate.isNotEmpty(containerId)) {
			writer.append(" id=\"");
			writer.append(containerId);
			writer.append("\"");
		}

		writer.append(" class=\"");
		String containerStyle =  modelForm.getContainerStyle();
		if (UtilValidate.isNotEmpty(containerStyle)) {
			writer.append(containerStyle);
		} else {
			writer.append("basic-form");
		}
		writer.append("\"");

		String formId = modelForm.getContainerId(context);
		List<ModelForm.UpdateArea> updateAreas = modelForm.getOnSubmitUpdateAreas();

		boolean ajaxEnabled = updateAreas != null && this.javaScriptEnabled;
		if (ajaxEnabled) {
			writer.append(" onsubmit=\"");
			writer.append("javascript:ajaxSubmitFormUpdateAreas('");
			writer.append(formId);
			writer.append("','").append(getOnlySubmitTargetUpdateArea(updateAreas, null, context));
			writer.append("','").append(createAjaxParamsFromUpdateAreas(updateAreas, null, context));
			writer.append("'); return false;\"");
		}

		//        writer.append(" onSubmit=\"javascript:submitFormDisableSubmits(this)\"");

		if (!modelForm.getClientAutocompleteFields()) {
			writer.append(" autocomplete=\"off\"");
		}

		writer.append(" name=\"");
		writer.append(modelForm.getCurrentFormName(context));
		writer.append("\">");

		boolean useRowSubmit = modelForm.getUseRowSubmit();
		if (useRowSubmit) {
			writer.append("<input type=\"hidden\" name=\"_useRowSubmit\" value=\"Y\"/>");
		}

		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormClose(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm)
	 */
	public void renderFormClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append("</form>");
		String focusFieldName = modelForm.getfocusFieldName();
		if (UtilValidate.isNotEmpty(focusFieldName)) {
			appendWhitespace(writer);
			writer.append("<script language=\"JavaScript\" type=\"text/javascript\">");
			appendWhitespace(writer);
			writer.append("document.").append(modelForm.getCurrentFormName(context)).append(".");
			writer.append(focusFieldName).append(".focus();");
			appendWhitespace(writer);
			writer.append("</script>");
		}
		appendWhitespace(writer);
		renderEndingBoundaryComment(writer, "Form Widget - Form Element", modelForm);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormClose(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm)
	 */
	public void renderMultiFormClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		for (ModelFormField submitField: modelForm.getMultiSubmitFields()) {
			if (submitField != null) {

				// Threw this in that as a hack to keep the submit button from expanding the first field
				// Needs a more rugged solution
				// WARNING: this method (renderMultiFormClose) must be called after the
				// table that contains the list has been closed (to avoid validation errors) so
				// we cannot call here the methods renderFormatItemRowCell*: for this reason
				// they are now commented.

				// this.renderFormatItemRowCellOpen(writer, context, modelForm, submitField);
				// this.renderFormatItemRowCellClose(writer, context, modelForm, submitField);

				// this.renderFormatItemRowCellOpen(writer, context, modelForm, submitField);

				submitField.renderFieldString(writer, context, this);

				// this.renderFormatItemRowCellClose(writer, context, modelForm, submitField);

			}
		}
		writer.append("</form>");
		appendWhitespace(writer);

		// see if there is anything that needs to be added outside of the multi-form
		Map<String, Object> wholeFormContext = UtilGenerics.checkMap(context.get("wholeFormContext"));
		Appendable postMultiFormWriter = wholeFormContext != null ? (Appendable) wholeFormContext.get("postMultiFormWriter") : null;
		if (postMultiFormWriter != null) {
			writer.append(postMultiFormWriter.toString());
			appendWhitespace(writer);
		}

		renderEndingBoundaryComment(writer, "Form Widget - Form Element (Multi)", modelForm);
	}

	public void renderFormatListWrapperOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {

		Map<String, Object> inputFields = UtilGenerics.checkMap(context.get("requestParameters"));
		Map<String, Object> queryStringMap = UtilGenerics.toMap(context.get("queryStringMap"));
		if (UtilValidate.isNotEmpty(queryStringMap)) {
			inputFields.putAll(queryStringMap);
		}
		if (modelForm.getType().equals("multi")) {
			inputFields = UtilHttp.removeMultiFormParameters(inputFields);
		}
		String queryString = UtilHttp.urlEncodeArgs(inputFields);
		context.put("_QBESTRING_", queryString);

		this.widgetCommentsEnabled = ModelWidget.widgetBoundaryCommentsEnabled(context);
		renderBeginningBoundaryComment(writer, "Form Widget", modelForm);

		if (this.renderPagination && !"bottom".equals(modelForm.getPagerPosition(context))) {
			this.renderNextPrev(writer, context, modelForm, false);
		}

        // open div for freeze header row
		writer.append(" <table id=\"table_"+ modelForm.getContainerId(context) +"\" cellspacing=\"0\" cellpadding=\"0\" class=\"");
		if (UtilValidate.isNotEmpty(modelForm.getDefaultTableStyle())) {
			writer.append(modelForm.getDefaultTableStyle());
		} else {
			writer.append("basic-table form-widget-table dark-grid");
		}
		writer.append("\">");
		appendWhitespace(writer);
	}

    public void renderFormatListWrapperClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append(" </table>");

		appendWhitespace(writer);
		if (this.renderPagination && !"top".equals(modelForm.getPagerPosition(context))) {
			this.renderNextPrev(writer, context, modelForm, true);
		}
		renderEndingBoundaryComment(writer, "Form Widget - Formal List Wrapper", modelForm);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatHeaderRowOpen(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm)
	 */
	public void renderFormatHeaderRowOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append("  <tr");
		String headerStyle = modelForm.getHeaderRowStyle();
		writer.append(" class=\"");
		if (UtilValidate.isNotEmpty(headerStyle)) {
			writer.append(headerStyle);
		} else {
			writer.append("header-row");
		}
		writer.append("\">");
		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatHeaderRowClose(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm)
	 */
	public void renderFormatHeaderRowClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append("  </tr>");
		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatHeaderRowCellOpen(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm, org.ofbiz.widget.form.ModelFormField, int positionSpan)
	 */
	public void renderFormatHeaderRowCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField, int positionSpan) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("   <th id=\"");
		sb.append(modelForm.getFieldName(modelFormField, context));
		sb.append("\"");
		writer.append(sb.toString());
		String areaStyle = "";
		String fieldGroupId = "";
		if(UtilValidate.isNotEmpty(modelFormField)) {
			FlexibleStringExpander areaStyleNotExpanded = FlexibleStringExpander.getInstance(modelFormField.getTitleAreaStyle());
			areaStyle = areaStyleNotExpanded.expandString(context);			
		}
		if (modelFormField.getWidth() > 0) {
			writer.append(" style=\"width:");
			writer.append(Integer.toString(modelFormField.getWidth()));
			writer.append("px\"");
		}
		if (positionSpan > 1) {
			writer.append(" colspan=\"");
			writer.append(Integer.toString(positionSpan));
			writer.append("\"");
		}
		fieldGroupId = modelFormField.getModelForm().getFieldGroupId(modelFormField);
		String classes = "";
		if(UtilValidate.isNotEmpty(areaStyle)){
			classes = classes.concat(areaStyle);
		}
		if(UtilValidate.isNotEmpty(fieldGroupId)) {
			classes = classes.concat(" fieldGroupId_"+fieldGroupId);
		}

		if (UtilValidate.isNotEmpty(fieldGroupId) || UtilValidate.isNotEmpty(areaStyle)) {
			writer.append(" class=");
			if (UtilValidate.isNotEmpty(classes)) {
				writer.append("\""+classes+"\"");
			}
		}
		writer.append(">");
		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatHeaderRowCellClose(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm, org.ofbiz.widget.form.ModelFormField)
	 */
	public void renderFormatHeaderRowCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField) throws IOException {
		writer.append("</th>");
		appendWhitespace(writer);
	}

	public void renderFormatHeaderRowFormCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append("   <th ");
		String areaStyle = modelForm.getFormTitleAreaStyle();
		writer.append(" class=\"");
		if (UtilValidate.isNotEmpty(areaStyle)) {
			writer.append(areaStyle);
			writer.append("\"");
		}
		writer.append(">");
		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatHeaderRowFormCellClose(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm)
	 */
	public void renderFormatHeaderRowFormCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append("</th>");
		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatHeaderRowFormCellTitleSeparator(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm, boolean)
	 */
	public void renderFormatHeaderRowFormCellTitleSeparator(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField, boolean isLast) throws IOException {

		String titleStyle = modelFormField.getTitleStyle();
		if (UtilValidate.isNotEmpty(titleStyle)) {
			writer.append("<span class=\"");
			writer.append(titleStyle);
			writer.append("\">");
		}
		if (isLast) {
			writer.append(" - ");
		} else {
			writer.append(" - ");
		}
		if (UtilValidate.isNotEmpty(titleStyle)) {
			writer.append("</span>");
		}
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatItemRowOpen(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm)
	 */
	public void renderFormatItemRowOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		Integer itemIndex = (Integer)context.get("itemIndex");

		if (itemIndex!=null) {
			if(itemIndex.intValue() == 0){
				writer.append("  <tbody>");
			}
			writer.append("  <tr");
			String altRowStyles = modelForm.getStyleAltRowStyle(context);
			if (itemIndex.intValue() % 2 == 0) {
				String evenRowStyle = modelForm.getEvenRowStyle();
				if (UtilValidate.isNotEmpty(evenRowStyle)) {
					writer.append(" class=\"");
					writer.append(evenRowStyle);
					if (UtilValidate.isNotEmpty(altRowStyles)) {
						writer.append(altRowStyles);
					}
					writer.append("\"");
				} else {
					if (UtilValidate.isNotEmpty(altRowStyles)) {
						writer.append(" class=\"");
						writer.append(altRowStyles);
						writer.append("\"");
					}
				}
			} else {
				String oddRowStyle = modelForm.getOddRowStyle();
				if (UtilValidate.isNotEmpty(oddRowStyle)) {
					writer.append(" class=\"");
					writer.append(oddRowStyle);
					if (UtilValidate.isNotEmpty(altRowStyles)) {
						writer.append(altRowStyles);
					}
					writer.append("\"");
				} else {
					if (UtilValidate.isNotEmpty(altRowStyles)) {
						writer.append(" class=\"");
						writer.append(altRowStyles);
						writer.append("\"");
					}
				}
			}
		}
		writer.append(">");
		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatItemRowClose(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm)
	 */
	public void renderFormatItemRowClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		Integer itemIndex = (Integer)context.get("itemIndex");
		Integer highIndex = (Integer) context.get("highIndex");
		Integer lowIndex = (Integer)context.get("lowIndex");
		Integer listSize = (Integer)context.get("listSize");
		writer.append("  </tr>");
		appendWhitespace(writer);
		if (itemIndex!=null && highIndex!=null && listSize!=null && lowIndex!=null) {
			if(itemIndex.intValue() == highIndex.intValue()-1){
				writer.append("  </tbody>");
			}
			if(itemIndex.intValue() == (listSize.intValue() - lowIndex.intValue()-1)){
				writer.append("  </tbody>");
			}
		}
		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatItemRowCellOpen(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm, org.ofbiz.widget.form.ModelFormField)
	 */
	public void renderFormatItemRowCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField, int positionSpan) throws IOException {
		writer.append("   <td");
		FlexibleStringExpander areaStyleNotExpanded = FlexibleStringExpander.getInstance(modelFormField.getWidgetAreaStyle());
		String areaStyle = areaStyleNotExpanded.expandString(context);
		if (positionSpan > 1) {
			writer.append(" colspan=\"");
			writer.append(Integer.toString(positionSpan));
			writer.append("\"");
		}
		if (UtilValidate.isNotEmpty(areaStyle)) {
			writer.append(" class=\"");
			writer.append(areaStyle);
			writer.append("\"");
		}
		writer.append(">");
		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatItemRowCellClose(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm, org.ofbiz.widget.form.ModelFormField)
	 */
	public void renderFormatItemRowCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm, ModelFormField modelFormField) throws IOException {
		writer.append("</td>");
		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatItemRowFormCellOpen(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm)
	 */
	public void renderFormatItemRowFormCellOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append("   <td");
		String areaStyle = modelForm.getFormWidgetAreaStyle();
		if (UtilValidate.isNotEmpty(areaStyle)) {
			writer.append(" class=\"");
			writer.append(areaStyle);
			writer.append("\"");
		}
		writer.append(">");
		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatItemRowFormCellClose(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm)
	 */
	public void renderFormatItemRowFormCellClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append("</td>");
		appendWhitespace(writer);
	}

	public void renderFormatSingleWrapperOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append(" <table cellspacing=\"0\" cellpadding=\"0\"");
		if (UtilValidate.isNotEmpty(modelForm.getDefaultTableStyle())) {
			writer.append(" class=\"").append(modelForm.getDefaultTableStyle()).append("\"");
		}
		writer.append(">");
		appendWhitespace(writer);
	}

	public void renderFormatSingleWrapperClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append(" </table>");
		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatFieldRowOpen(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm)
	 */
	public void renderFormatFieldRowOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append("  <tr>");
		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatFieldRowClose(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelForm)
	 */
	public void renderFormatFieldRowClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append("  </tr>");
		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatFieldRowTitleCellOpen(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField)
	 */
	public void renderFormatFieldRowTitleCellOpen(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
		writer.append("   <td");
		FlexibleStringExpander areaStyleNotExpanded = FlexibleStringExpander.getInstance(modelFormField.getTitleAreaStyle());
		String areaStyle = areaStyleNotExpanded.expandString(context);			
		if (UtilValidate.isNotEmpty(areaStyle)) {
			writer.append(" class=\"");
			writer.append(areaStyle);
			writer.append("\"");
		} else {
			writer.append(" class=\"label\"");
		}
		writer.append(">");
		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatFieldRowTitleCellClose(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField)
	 */
	public void renderFormatFieldRowTitleCellClose(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
		writer.append("</td>");
		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatFieldRowSpacerCell(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField)
	 */
	public void renderFormatFieldRowSpacerCell(Appendable writer, Map<String, Object> context, ModelFormField modelFormField) throws IOException {
		// Embedded styling - bad idea
		//writer.append("<td>&nbsp;</td>");

		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatFieldRowWidgetCellOpen(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField, int)
	 */
	public void renderFormatFieldRowWidgetCellOpen(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, int positions, int positionSpan, Integer nextPositionInRow) throws IOException {
		//        writer.append("<td width=\"");
		//        if (nextPositionInRow != null || modelFormField.getPosition() > 1) {
		//            writer.append("30");
		//        } else {
		//            writer.append("80");
		//        }
		//        writer.append("%\"");
		writer.append("   <td");
		if (positionSpan > 0) {
			writer.append(" colspan=\"");
			// do a span of 1 for this column, plus 3 columns for each spanned
			//position or each blank position that this will be filling in
			writer.append(Integer.toString(1 + (positionSpan * 3)));
			writer.append("\"");
		}
		FlexibleStringExpander areaStyleNotExpanded = FlexibleStringExpander.getInstance(modelFormField.getWidgetAreaStyle());
		String areaStyle = areaStyleNotExpanded.expandString(context);
		if (UtilValidate.isNotEmpty(areaStyle)) {
			writer.append(" class=\"");
			writer.append(areaStyle);
			writer.append("\"");
		}
		writer.append(">");

		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFormatFieldRowWidgetCellClose(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField, int)
	 */
	public void renderFormatFieldRowWidgetCellClose(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, int positions, int positionSpan, Integer nextPositionInRow) throws IOException {
		writer.append("</td>");
		appendWhitespace(writer);
	}

	public void renderFormatEmptySpace(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append("&nbsp;");
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderTextFindField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.TextFindField)
	 */
	public void renderTextFindField(Appendable writer, Map<String, Object> context, TextFindField textFindField) throws IOException {

		ModelFormField modelFormField = textFindField.getModelFormField();
		ModelForm modelForm = modelFormField.getModelForm();
		ModelFormField.AutoComplete autoComplete = textFindField.getAutoComplete();
		//        List<ModelFormField.AutoCompleteConstraint> constraintList = textFindField.getAutoCompleteConstraint();

		Locale locale = (Locale)context.get("locale");

		String parameterName = modelFormField.getParameterName(context);
		String idName = modelFormField.getIdName(context);

		String parameterNameOp = parameterName + "_fld0_op";
		String operation = (String)context.get(parameterNameOp);
		String defaultOption;
		if(operation != null) defaultOption = operation;
		else defaultOption = textFindField.getDefaultOption();

		boolean ajaxEnabled = this.javaScriptEnabled;

		List<ModelFormField.OptionValue> allOptionValues = textFindField.getAllOptionValues(context, WidgetWorker.getDelegator(context));
		if (ajaxEnabled && !textFindField.isDropListComponent()) {
			writer.append("<div class=\"text-find\">");
		} else if (ajaxEnabled) {
			writer.append("<div ");

			appendClassNames(writer, "droplist_field", context, modelFormField);
			writer.append(" id=\"");
			writer.append(idName + "_fld0_value");
			writer.append("\">");

			//Autocompleter option
			if (UtilValidate.isNotEmpty(autoComplete.getChoices())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"choices\" value=\"");
				writer.append(autoComplete.getChoices());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getFrequency())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"frequency\" value=\"");
				writer.append(autoComplete.getFrequency());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getFullSearch())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"fullSearch\" value=\"");
				writer.append(autoComplete.getFullSearch());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getIgnoreCase())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"ignoreCase\" value=\"");
				writer.append(autoComplete.getIgnoreCase());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getMinChars())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"minChars\" value=\"");
				writer.append(autoComplete.getMinChars());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getPartialChars())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"partialChars\" value=\"");
				writer.append(autoComplete.getPartialChars());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getPartialSearch())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"partialSearch\" value=\"");
				writer.append(autoComplete.getPartialSearch());
				writer.append("\"/>");
			}

			String target = autoComplete.getTarget();
			if (UtilValidate.isEmpty(target)) {
				target = "ajaxAutocompleteOptions";
			}
			writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"target\" value=\"");
			writer.append(this.rh.makeLink(request, response, target));
			writer.append("\"/>");

			//Autocompleter field
			if (!textFindField.isLocalAutocompleter()) {
				List<OptionSource> allOptionSource = textFindField.getOptionSources();
				if (UtilValidate.isNotEmpty(allOptionSource) && ObjectType.instanceOf(EntityOptions.class, allOptionSource.get(0))) {
					List<EntityOptions> allEntityOption = UtilGenerics.checkList(allOptionSource);

					List<String> entityNameList = null;
					List<String> selectFieldList = null;
					List<String> orderByFieldList = null;
					List<String> displayFieldList = null;
					List<String> constraintParm = FastList.newInstance();
					for(EntityOptions entityOption : allEntityOption) {
						String entityName = entityOption.getEntityName(context);
						if (UtilValidate.isNotEmpty(entityName)) {
							if (UtilValidate.isEmpty(entityNameList)) {
								entityNameList = new FastList<String>();
							}
							entityNameList.add(entityName);
						}

						List<String> originalSelectField = entityOption.getSelectField(context);
						if (UtilValidate.isNotEmpty(originalSelectField)) {
							if (UtilValidate.isEmpty(selectFieldList)) {
								selectFieldList = new FastList<String>();
							}
							selectFieldList.add(this.makeCsvString(originalSelectField));
						}

						List<String> originalOrderByList = entityOption.getOrderByField(context);
						if (UtilValidate.isNotEmpty(originalOrderByList)) {
							if (UtilValidate.isEmpty(orderByFieldList)) {
								orderByFieldList = new FastList<String>();
							}
							orderByFieldList.add(this.makeCsvString(originalOrderByList));
						}

						List<String> originalDisplayFieldList = entityOption.getDisplayField(context);
						if (UtilValidate.isNotEmpty(originalDisplayFieldList)) {
							if (UtilValidate.isEmpty(displayFieldList)) {
								displayFieldList = new FastList<String>();
							}
							displayFieldList.add(this.makeCsvString(originalDisplayFieldList));
						}

						if (UtilValidate.isNotEmpty(entityOption.getConstraintList())) {
							for (EntityFinderUtil.ConditionExpr constraint: entityOption.getConstraintList()) {
								List<String> parmsList = FastList.newInstance();
								parmsList.add(constraint.getFieldName(context));
								parmsList.add(constraint.getOperator(context));
								String value = (String)constraint.getValue(context);
								if (UtilValidate.isNotEmpty(value)) {
									parmsList.add(value);
								}
								else {
									String lookupForFieldName = constraint.getLookupForFieldName(context);
									List<String> lookupForFieldNameSplittedList = StringUtil.split(lookupForFieldName, ",");
									if (UtilValidate.isNotEmpty(lookupForFieldNameSplittedList)) {
										value = "";
										for (String lookupForFieldNameItem : lookupForFieldNameSplittedList) {
											ModelFormField lookupForFormField = modelFormField.getModelForm().getModelFormField(lookupForFieldNameItem, context);
											if (UtilValidate.isNotEmpty(lookupForFormField)) {
												if (value.length() > 0)
													value += ",";
												value += "field:" + lookupForFormField.getFieldInfo().getParameterName(context);
											}
										}
										if (UtilValidate.isNotEmpty(value)) {
											parmsList.add(value);
										}
									}
								}

								constraintParm.add(this.makeCsvString(parmsList, "| "));
							}
						}
					}

					writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"entityName\" value=\"");
					writer.append(this.makeCsvString(entityNameList));
					writer.append("\"/>");

					if (UtilValidate.isNotEmpty(selectFieldList)) {
						writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"selectFields\" value=\"");
						writer.append(this.makeCsvString(selectFieldList, "; "));
						writer.append("\"/>");
					}

					if (UtilValidate.isNotEmpty(orderByFieldList)) {
						writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"sortByFields\" value=\"");
						writer.append(this.makeCsvString(orderByFieldList, "; "));
						writer.append("\"/>");
					}

					if (UtilValidate.isNotEmpty(displayFieldList)) {
						writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"displayFields\" value=\"");
						writer.append(this.makeCsvString(displayFieldList, "; "));
						writer.append("\"/>");
					}

					if (UtilValidate.isNotEmpty(constraintParm)) {
						List<String> tmpConstrParam = new FastList<String>();
						tmpConstrParam.add(this.makeCsvString(constraintParm, "! "));

						constraintParm = tmpConstrParam;

						writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"constraintFields\" value=\"");
						writer.append(UtilFormatOut.encodeXmlValue(this.makeCsvString(constraintParm, "; ")));
						writer.append("\"/>");
					}

					writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"saveView\" value=\"N\"/>");
				}
			} else {
				//TODO
				//Considerare il caso di Autocompleter.Local
				writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"localAutocompleter\" value=\"Y\"/>");
				for (ModelFormField.OptionValue optionValue: allOptionValues) {
					writer.append("<input type=\"hidden\" class=\"autocompleter_local_data\" id=\"" + idName + "_fld0_value_" + optionValue.getKey() + "\" name=\"" + parameterName + "_fld0_value_" + optionValue.getKey() + "\"");
					writer.append(" value=\"" + optionValue.getDescription() + "\"/>");
				}
			}

			//Now add entity-key-field if any
			if (UtilValidate.isNotEmpty(textFindField.getDropListKeyField())) {
				writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"entityKeyField\" value=\"");
				writer.append(textFindField.getDropListKeyField());
				writer.append("\"/>");
			}
			//Now add entity-description-field if any
			if (UtilValidate.isNotEmpty(textFindField.getDropListDisplayField(context))) {
				writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"entityDescriptionField\" value=\"");
				writer.append(textFindField.getDropListDisplayField(context));
				writer.append("\"/>");
			}
		}

		writer.append("<div class=\"droplist_container\">");

		if (!textFindField.getHideOptions()) {
			String opEquals = UtilProperties.getMessage("conditional", "equals", locale);
			String opBeginsWith = UtilProperties.getMessage("conditional", "begins_with", locale);
			String opContains = UtilProperties.getMessage("conditional", "contains", locale);
			String opIsEmpty = UtilProperties.getMessage("conditional", "is_empty", locale);
			String opNotEqual = UtilProperties.getMessage("conditional", "not_equal", locale);
			String opGreaterThan = UtilProperties.getMessage("conditional", "greater_than", locale);
			String opLessThan = UtilProperties.getMessage("conditional", "less_than", locale);
			String opIsNotEmpty = UtilProperties.getMessage("conditional", "is_not_empty", locale);

			writer.append(" <select name=\"");
			writer.append(parameterName);
			writer.append("_fld0_op\" class=\"selectBox filter-field-selection\">");
			if (textFindField.isDropListComponent()) {
				writer.append("<option value=\"equals\"").append(("equals".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opEquals).append("</option>");
				writer.append("<option value=\"notEqual\"").append(("notEqual".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opNotEqual).append("</option>");
				writer.append("<option value=\"empty\"").append(("empty".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opIsEmpty).append("</option>");
				writer.append("<option value=\"notEmpty\"").append(("notEmpty".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opIsNotEmpty).append("</option>");
			} else {
				if (textFindField.isTextFindComponent()) {
					String opBetween = UtilProperties.getMessage("conditional", "between", locale);
					String opNotBetween = UtilProperties.getMessage("conditional", "not_between", locale);
					writer.append("<option value=\"between\"").append(("between".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opBetween).append("</option>");
					writer.append("<option value=\"notBetween\"").append(("notBetween".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opNotBetween).append("</option>");
				}
				writer.append("<option value=\"like\"").append(("like".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opBeginsWith).append("</option>");
				writer.append("<option value=\"equals\"").append(("equals".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opEquals).append("</option>");
				writer.append("<option value=\"notEqual\"").append(("notEqual".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opNotEqual).append("</option>");
				writer.append("<option value=\"lessThan\"").append(("lessThan".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opLessThan).append("</option>");
				writer.append("<option value=\"greaterThan\"").append(("greaterThan".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opGreaterThan).append("</option>");
				writer.append("<option value=\"empty\"").append(("empty".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opIsEmpty).append("</option>");
				writer.append("<option value=\"notEmpty\"").append(("notEmpty".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opIsNotEmpty).append("</option>");
				writer.append("<option value=\"contains\"").append(("contains".equals(defaultOption)? " selected=\"selected\"": "")).append(">").append(opContains).append("</option>");
			}

			writer.append("</select>");
		} else {
			writer.append(" <input type=\"hidden\" name=\"");
			writer.append(parameterName);
			writer.append("_fld0_op\" value=\"").append(defaultOption).append("\"/>");
		}
		writer.append("<input type=\"text\"");

		String value = modelFormField.getEntry(context, textFindField.getDefaultValue(context), "_fld0_value");

		writer.append(" size=\"");
		writer.append(Integer.toString(textFindField.getSize()));
		writer.append('"');

		Integer maxlength = textFindField.getMaxlength();
		if (maxlength != null) {
			writer.append(" maxlength=\"");
			writer.append(maxlength.toString());
			writer.append('"');
		}

		if (!textFindField.getClientAutocompleteField() || ajaxEnabled) {
			writer.append(" autocomplete=\"off\"");
		}

		if (!textFindField.isDropListComponent()) {
			appendClassNames(writer, "text-find-element", context, modelFormField);


			writer.append(" name=\"");
			writer.append(parameterName);
			writer.append("_fld0_value\"");

			writer.append(" id=\"");
			writer.append(idName);
			writer.append("_fld0_value\"");

			if (UtilValidate.isNotEmpty(value)) {
				writer.append(" value=\"");
				writer.append(value);
				writer.append('"');
			}

			writer.append("/>");
		} else {
			String currentDescription = null;
			if (UtilValidate.isNotEmpty(value)) {
				for (ModelFormField.OptionValue optionValue : allOptionValues) {
					if (optionValue.getKey().equals(value)) {
						currentDescription = optionValue.getDescription();
						break;
					}
				}
			}

			writer.append(" value=\"");
			if (UtilValidate.isNotEmpty(currentDescription)) {
				writer.append(currentDescription);
			} else if (UtilValidate.isNotEmpty(value)) {
				writer.append(value);
			}
			writer.append('"');

			appendClassNames(writer, "droplist_edit_field", context, modelFormField);

			writer.append(" name=\"");
			writer.append(parameterName);
			writer.append("_edit_value_fld0_value\"");

			writer.append(" id=\"");
			writer.append(idName);
			writer.append("_fld0_value_edit_value\"");
			writer.append("/>");

			writer.append("<input type=\"hidden\" class=\"droplist_code_field\" name=\"");
			writer.append(parameterName + "_fld0_value\"");
			if (UtilValidate.isNotEmpty(value)) {
				writer.append(" value=\"");
				writer.append(value);
				writer.append('"');
			}
			writer.append("/>");

			writer.append("<span class=\"droplist-anchor fa fa-2x\"><a style=\"cursor: pointer;\" class=\"droplist_submit_field\" href=\"#\"></a></span>");
		}

		if (textFindField.isTextFindComponent()) {
			writer.append("<input type=\"text\"");

			appendClassNames(writer, "text-find-element", context, modelFormField);

			writer.append(" name=\"");
			writer.append(modelFormField.getParameterName(context));
			writer.append("_fld1_value\"");

			writer.append(" id=\"");
			writer.append(modelFormField.getParameterName(context));
			writer.append("_fld1_value\"");

			value = modelFormField.getEntry(context, textFindField.getDefaultValue(context), "_fld1_value");
			if (UtilValidate.isNotEmpty(value)) {
				writer.append(" value=\"");
				writer.append(value);
				writer.append('"');
			}

			writer.append(" size=\"");
			writer.append(Integer.toString(textFindField.getSize()));
			writer.append('"');

			maxlength = textFindField.getMaxlength();
			if (maxlength != null) {
				writer.append(" maxlength=\"");
				writer.append(maxlength.toString());
				writer.append('"');
			}

			if (!textFindField.getClientAutocompleteField() || ajaxEnabled) {
				writer.append(" autocomplete=\"off\"");
			}
			writer.append("/>");
		}

		if (UtilValidate.isNotEmpty(modelFormField.getTitleStyle())) {
			writer.append(" <span class=\"");
			writer.append(modelFormField.getTitleStyle());
			writer.append("\">");
		}

		String ignoreCase = UtilProperties.getMessage("conditional", "ignore_case", locale);
		boolean ignCase = textFindField.getIgnoreCase();

		if (!textFindField.getHideIgnoreCase()) {
			writer.append(" <input type=\"checkbox\" name=\"");
			writer.append(modelFormField.getParameterName(context));
			writer.append("_fld0_ic\" value=\"Y\"").append((ignCase ? " checked=\"checked\"" : "") + "/>");
			writer.append(ignoreCase);
		} else {
			writer.append( "<input type=\"hidden\" name=\"");
			writer.append(modelFormField.getParameterName(context));
			writer.append("_fld0_ic\" value=\"").append((ignCase ? "Y" : "") + "\"/>");
		}

		if (UtilValidate.isNotEmpty(modelFormField.getTitleStyle())) {
			writer.append("</span>");
		}
		writer.append("</div>");

		this.appendTooltip(writer, context, modelFormField);

		if (ajaxEnabled) {
			writer.append("</div>");
		}

		appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderRangeFindField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.RangeFindField)
	 */
	public void renderRangeFindField(Appendable writer, Map<String, Object> context, RangeFindField rangeFindField) throws IOException {

		ModelFormField modelFormField = rangeFindField.getModelFormField();
		Locale locale = (Locale)context.get("locale");
		String opEquals = UtilProperties.getMessage("conditional", "equals", locale);
		String opGreaterThan = UtilProperties.getMessage("conditional", "greater_than", locale);
		String opGreaterThanEquals = UtilProperties.getMessage("conditional", "greater_than_equals", locale);
		String opLessThan = UtilProperties.getMessage("conditional", "less_than", locale);
		String opLessThanEquals = UtilProperties.getMessage("conditional", "less_than_equals", locale);
		//String opIsEmpty = UtilProperties.getMessage("conditional", "is_empty", locale);

		writer.append("<input type=\"text\"");

		appendClassNames(writer, context, modelFormField);

		writer.append(" name=\"");
		writer.append(modelFormField.getParameterName(context));
		writer.append("_fld0_value\"");

		String value = modelFormField.getEntry(context, rangeFindField.getDefaultValue(context));
		if (UtilValidate.isNotEmpty(value)) {
			writer.append(" value=\"");
			writer.append(value);
			writer.append('"');
		}

		writer.append(" size=\"");
		writer.append(Integer.toString(rangeFindField.getSize()));
		writer.append('"');

		Integer maxlength = rangeFindField.getMaxlength();
		if (maxlength != null) {
			writer.append(" maxlength=\"");
			writer.append(maxlength.toString());
			writer.append('"');
		}

		if (!rangeFindField.getClientAutocompleteField()) {
			writer.append(" autocomplete=\"off\"");
		}

		writer.append("/>");

		if (UtilValidate.isNotEmpty(modelFormField.getTitleStyle())) {
			writer.append(" <span class=\"");
			writer.append(modelFormField.getTitleStyle());
			writer.append("\">");
		}

		String defaultOptionFrom = rangeFindField.getDefaultOptionFrom();
		writer.append(" <select name=\"");
		writer.append(modelFormField.getParameterName(context));
		writer.append("_fld0_op\" class=\"selectBox\">");
		writer.append("<option value=\"equals\"").append(("equals".equals(defaultOptionFrom)? " selected=\"selected\"": "")).append(">").append(opEquals).append("</option>");
		writer.append("<option value=\"greaterThan\"").append(("greaterThan".equals(defaultOptionFrom)? " selected=\"selected\"": "")).append(">").append(opGreaterThan).append("</option>");
		writer.append("<option value=\"greaterThanEqualTo\"").append(("greaterThanEqualTo".equals(defaultOptionFrom)? " selected=\"selected\"": "")).append(">").append(opGreaterThanEquals).append("</option>");
		writer.append("</select>");

		writer.append("</span>");

		writer.append(" <br/> ");

		writer.append("<input type=\"text\"");

		appendClassNames(writer, context, modelFormField);

		writer.append(" name=\"");
		writer.append(modelFormField.getParameterName(context));
		writer.append("_fld1_value\"");

		value = modelFormField.getEntry(context);
		if (UtilValidate.isNotEmpty(value)) {
			writer.append(" value=\"");
			writer.append(value);
			writer.append('"');
		}

		writer.append(" size=\"");
		writer.append(Integer.toString(rangeFindField.getSize()));
		writer.append('"');

		if (maxlength != null) {
			writer.append(" maxlength=\"");
			writer.append(maxlength.toString());
			writer.append('"');
		}

		if (!rangeFindField.getClientAutocompleteField()) {
			writer.append(" autocomplete=\"off\"");
		}

		writer.append("/>");

		String defaultOptionThru = rangeFindField.getDefaultOptionThru();
		writer.append(" <select name=\"");
		writer.append(modelFormField.getParameterName(context));
		writer.append("_fld1_op\" class=\"selectBox\">");
		writer.append("<option value=\"lessThan\"").append(("lessThan".equals(defaultOptionThru)? " selected=\"selected\"": "")).append(">").append(opLessThan).append("</option>");
		writer.append("<option value=\"lessThanEqualTo\"").append(("lessThanEqualTo".equals(defaultOptionThru)? " selected=\"selected\"": "")).append(">").append(opLessThanEquals).append("</option>");
		writer.append("</select>");

		if (UtilValidate.isNotEmpty(modelFormField.getTitleStyle())) {
			writer.append("</span>");
		}

		this.appendTooltip(writer, context, modelFormField);

		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderDateFindField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.DateFindField)
	 */
	public void renderDateFindField(Appendable writer, Map<String, Object> context, DateFindField dateFindField) throws IOException {
		ModelFormField modelFormField = dateFindField.getModelFormField();
		String paramName = modelFormField.getParameterName(context);
		String defaultDateTimeString = dateFindField.getDefaultValue(context);

		Locale locale = (Locale)context.get("locale");

		String opEquals = UtilProperties.getMessage("conditional", "equals", locale);
		String opIsEmpty = UtilProperties.getMessage("conditional", "is_empty", locale);
		String opNotEqual = UtilProperties.getMessage("conditional", "not_equal", locale);
		String opGreaterThan = UtilProperties.getMessage("conditional", "greater_than", locale);
		String opLessThan = UtilProperties.getMessage("conditional", "less_than", locale);
		String opIsNotEmpty = UtilProperties.getMessage("conditional", "is_not_empty", locale);

		Map<String, String> uiLabelMap = UtilGenerics.checkMap(context.get("uiLabelMap"));
		if (uiLabelMap == null) {
			Debug.logWarning("Could not find uiLabelMap in context", module);
		}
		String localizedInputTitle = "", localizedIconTitle = "", localizedDefaultFormatToShow="", localizedDateFormat="";

		boolean shortDateInput = "date".equals(dateFindField.getType()) || "time-dropdown".equals(dateFindField.getInputMethod());

		// the default values for a timestamp
		int size = 25;
		int maxlength = 30;

		if (shortDateInput) {
			size = maxlength = 10;
			if (uiLabelMap != null) {
				localizedInputTitle = uiLabelMap.get("CommonFormatDate");
				localizedDefaultFormatToShow = uiLabelMap.get("ShowedCommonFormatDate");
				localizedDateFormat = UtilDateTime.getDateFormat((Locale)context.get("locale"));
				maxlength = localizedDefaultFormatToShow.length();
			}
		} else if ("time".equals(dateFindField.getType())) {
			size = maxlength = 8;
			if (uiLabelMap != null) {
				localizedInputTitle = uiLabelMap.get("CommonFormatTime");
				localizedDefaultFormatToShow = uiLabelMap.get("ShowedCommonFormatTime");
				localizedDateFormat = UtilDateTime.getTimeFormat((Locale)context.get("locale"));
				maxlength = localizedDefaultFormatToShow.length();
			}
		} else {
			if (uiLabelMap != null) {
				localizedInputTitle = uiLabelMap.get("CommonFormatDateTime");
				localizedDefaultFormatToShow = uiLabelMap.get("ShowedCommonFormatDateTime");
				localizedDateFormat = UtilDateTime.getDateTimeFormat((Locale)context.get("locale"));
				maxlength = localizedDefaultFormatToShow.length();
			}
		}

		String idName = modelFormField.getCurrentContainerId(context);
		if (UtilValidate.isEmpty(idName)) {
			idName = paramName;
		}

		//costruisco il rndering dei campi
		int day = 0;
		int month = 0;
		int year = 0;
		int hour = 0;
		int minutes = 0;

		String dateTimeValue = modelFormField.getEntry(context, defaultDateTimeString, "_fld0_value");
		Calendar calendar = Calendar.getInstance();

		DateFormatSymbols dfs = new DateFormatSymbols((Locale)context.get("locale"));
		String[] months = dfs.getMonths();

		try {
			if (UtilValidate.isNotEmpty(dateTimeValue)) {
				if (!Pattern.matches("^.+([0-1]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)(\\.\\d+){0,1}$", dateTimeValue)) {
					if (Pattern.matches("^.+([0-1]\\d|2[0-3]):([0-5]\\d)$", dateTimeValue)) {
						dateTimeValue += ":00";
					} else if (Pattern.matches("^.+\\\\s+([0-1]\\d|2[0-3])$", dateTimeValue)) {
						dateTimeValue += ":00:00";
					} else {
						dateTimeValue += " 00:00:00";
					}
				}

				Timestamp defaultTimestamp = (Timestamp)ObjectType.simpleTypeConvert(dateTimeValue, "java.sql.Timestamp", null, (Locale)context.get("locale"));

				if (UtilValidate.isNotEmpty(defaultTimestamp)) {
					calendar.setTime(defaultTimestamp);

					day = calendar.get(Calendar.DAY_OF_MONTH);
					year = calendar.get(Calendar.YEAR);
					month = calendar.get(Calendar.MONTH)+1;
					hour = calendar.get(Calendar.HOUR_OF_DAY);
					minutes = calendar.get(Calendar.MINUTE);
				}
			}
		} catch (IllegalArgumentException e) {
			Debug.logWarning("Form widget field [" + paramName + "] with input-method=\"time-dropdown\" was not able to understand the default time ["
					+ defaultDateTimeString + "]. The parsing error was: " + e.getMessage(), module);
		} catch (GeneralException e) {}

		writer.append("<div class=\"datePanel\">");

		String defaultOptionThru = dateFindField.getDefaultOptionThru();
		writer.append(" <select name=\"");
		writer.append(paramName);
		if (!dateFindField.getSingleOption()) {
			writer.append("_fld1_op\" class=\"selectBox filter-field-selection\">");
			String operation = (String)context.get(paramName + "_fld1_op");
			if(operation != null) defaultOptionThru = operation;
		} else {
			writer.append("_fld0_op\" class=\"selectBox filter-field-selection\">");
			String operation = (String)context.get(paramName + "_fld0_op");
			if(operation != null) defaultOptionThru = operation;
		}
		if (dateFindField.getRangeComponent()) {
			String opBetween = UtilProperties.getMessage("conditional", "between", locale);
			String opNotBetween = UtilProperties.getMessage("conditional", "not_between", locale);
			writer.append("<option value=\"between\"").append(("between".equals(defaultOptionThru)? " selected=\"selected\"": "")).append(">").append(opBetween).append("</option>");
			writer.append("<option value=\"notBetween\"").append(("notBetween".equals(defaultOptionThru)? " selected=\"selected\"": "")).append(">").append(opNotBetween).append("</option>");
		}

		writer.append("<option value=\"equals\"").append(("equals".equals(defaultOptionThru)? " selected=\"selected\"": "")).append(">").append(opEquals).append("</option>");
		writer.append("<option value=\"notEqual\"").append(("notEqual".equals(defaultOptionThru)? " selected=\"selected\"": "")).append(">").append(opNotEqual).append("</option>");
		writer.append("<option value=\"lessThan\"").append(("lessThan".equals(defaultOptionThru)? " selected=\"selected\"": "")).append(">").append(opLessThan).append("</option>");
		writer.append("<option value=\"greaterThan\"").append(("greaterThan".equals(defaultOptionThru)? " selected=\"selected\"": "")).append(">").append(opGreaterThan).append("</option>");
		writer.append("<option value=\"empty\"").append(("empty".equals(defaultOptionThru)? " selected=\"selected\"": "")).append(">").append(opIsEmpty).append("</option>");
		writer.append("<option value=\"notEmpty\"").append(("notEmpty".equals(defaultOptionThru)? " selected=\"selected\"": "")).append(">").append(opIsNotEmpty).append("</option>");
		writer.append("</select>");
		writer.append("</div>");

		writer.append("<div class=\"datePanel calendarSingleForm\" id=\"").append(paramName).append("_fld0_value"+"_datePanel\">");

		if (!this.javaScriptEnabled) {
			String firstFieldName = "";
			List<String> dateArray = new FastList<String>();

			if (dateFindField.getType().equals("date") || dateFindField.getType().equals("timestamp")) {
				int yearPosition=-1;
				int dayPosition=-1;

				//Render dell'anno
				StringBuilder yearRenderer = new StringBuilder("<input type=\"text\" ");

				yearRenderer.append(" name=\"");
				yearRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld0_value", "year"));
				yearRenderer.append('"');
				yearRenderer.append(" id=\"");
				yearRenderer.append(UtilHttp.makeCompositeParam(idName + "_fld0_value", "year"));
				yearRenderer.append('"');
				yearRenderer.append(" maxlength=\"4\" ");

				appendClassNames(yearRenderer, "year date", context, modelFormField);
				yearRenderer.append(" value=\"");
				if (year > 0)
					yearRenderer.append(year);
				yearRenderer.append("\" />");

				//Render del giorno
				StringBuilder dayRenderer = new StringBuilder("<input type=\"text\" ");

				dayRenderer.append(" name=\"");
				dayRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld0_value", "day"));
				dayRenderer.append('"');
				dayRenderer.append(" id=\"");
				dayRenderer.append(UtilHttp.makeCompositeParam(idName + "_fld0_value", "day"));
				dayRenderer.append('"');
				dayRenderer.append(" maxlength=\"2\" ");

				appendClassNames(dayRenderer, "day date", context, modelFormField);
				dayRenderer.append(" value=\"");
				if (day > 0)
					dayRenderer.append(day);
				dayRenderer.append("\" />");

				StringBuilder monthRenderer = new StringBuilder("<select ");

				monthRenderer.append(" name=\"");
				monthRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld0_value", "month"));
				monthRenderer.append('"');
				monthRenderer.append(" id=\"");
				monthRenderer.append(UtilHttp.makeCompositeParam(idName + "_fld0_value", "month"));
				monthRenderer.append('"');

				appendClassNames(monthRenderer, "month date", context, modelFormField);
				monthRenderer.append(" >");
				if (UtilValidate.isNotEmpty(monthRenderer)) {
					for (int i = 0; i < months.length; i++) {
						if (UtilValidate.isNotEmpty(months[i])) {
							monthRenderer.append("<option value=\"");
							monthRenderer.append(Integer.toString(i+1));
							if (month-1 == i)
								monthRenderer.append(" selected=\"selected\"");
							monthRenderer.append("\">");
							monthRenderer.append(months[i]);
							monthRenderer.append("</option>");
						}
					}
				}
				monthRenderer.append("</select>");

				firstFieldName = UtilHttp.makeCompositeParam(paramName, "month");
				dateArray.add(monthRenderer.toString());
				if (UtilValidate.isNotEmpty(localizedDateFormat)) {
					if (Pattern.matches("^(yy|yyyy)[/-\\\\s].+[/-\\\\s].+$", localizedDateFormat)) {
						yearPosition=0;
						firstFieldName = UtilHttp.makeCompositeParam(paramName, "year");
					} else if (Pattern.matches("^.+[/-\\\\s](yy|yyyy)[/-\\\\s].+$", localizedDateFormat)) {
						yearPosition = 1;
					} else if (Pattern.matches("^.+[/-\\\\s].+[/-\\\\s](yy|yyyy).+$", localizedDateFormat)) {
						yearPosition = 2;
					}
					if (Pattern.matches("^(dd)[/-\\\\s].+[/-\\\\s].+$", localizedDateFormat)) {
						dayPosition=0;
						firstFieldName = UtilHttp.makeCompositeParam(paramName, "day");
					} else if (Pattern.matches("^.+[/-\\\\s](dd)[/-\\\\s].+$", localizedDateFormat)) {
						dayPosition = 1;
					} else if (Pattern.matches("^.+[/-\\\\s].+[/-\\\\s](dd).+$", localizedDateFormat)) {
						dayPosition = 2;
					}
				}
				if (yearPosition==-1 && dayPosition ==-1) {
					dateArray.add(dayRenderer.toString());
					dateArray.add(yearRenderer.toString());
				} else {
					if (yearPosition >= dateArray.size()) {
						dateArray.add(yearRenderer.toString());
					} else if (yearPosition != -1){
						dateArray.add(yearPosition, yearRenderer.toString());
					}
					if (dayPosition >= dateArray.size()) {
						dateArray.add(dayRenderer.toString());
					} else if (dayPosition != -1){
						dateArray.add(dayPosition, dayRenderer.toString());
					}
				}
			}

			if (Pattern.matches("^.+[/-\\\\s].+[/-\\\\s].+\\\\s(HH):(mm)$", localizedDateFormat) || dateFindField.getType().equals("time") || dateFindField.getType().equals("timestamp")) {
				StringBuilder timeRenderer = new StringBuilder();

				// if we have an input method of time-dropdown, then render two dropdowns
				if ("time-dropdown".equals(dateFindField.getInputMethod())) {
					boolean isTwelveHour = "12".equals(dateFindField.getClock());

					// write the select for hours
					timeRenderer.append("&nbsp;<select name=\"").append(UtilHttp.makeCompositeParam(paramName + "_fld0_value", "hour")).append("\"");
					timeRenderer.append(" id=\"").append(UtilHttp.makeCompositeParam(idName + "_fld0_value", "hour")).append("\" ");
					appendClassNames(timeRenderer,"hour time", context, modelFormField);
					timeRenderer.append(">");

					// keep the two cases separate because it's hard to understand a combined loop
					if (isTwelveHour) {
						for (int i = 1; i <= 12; i++) {
							timeRenderer.append("<option value=\"").append(Integer.toString(i)).append("\"");
							if (calendar != null) {
								if (hour == 0) hour = 12;
								if (hour > 12) hour -= 12;
								if (i == hour) writer.append(" selected=\"selected\"");
							}
							timeRenderer.append(">").append(Integer.toString(i)).append("</option>");
						}
					} else {
						for (int i = 0; i < 24; i++) {
							timeRenderer.append("<option value=\"").append(Integer.toString(i)).append("\"");
							if (calendar != null && i == calendar.get(Calendar.HOUR_OF_DAY)) {
								timeRenderer.append(" selected=\"selected\"");
							}
							timeRenderer.append(">").append(Integer.toString(i)).append("</option>");
						}
					}

					// write the select for minutes
					timeRenderer.append("</select>");
					timeRenderer.append("<span id=\"time-separator\">:</span>");
					timeRenderer.append("<select name=\"");
					timeRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld0_value", "minutes") + "\"");
					timeRenderer.append(" id=\"").append(UtilHttp.makeCompositeParam(idName + "_fld0_value", "minutes")).append("\" ");
					appendClassNames(timeRenderer,"minutes time", context, modelFormField);
					timeRenderer.append(">");
					for (int i = 0; i < 60; i++) {
						timeRenderer.append("<option value=\"").append(Integer.toString(i)).append("\"");
						if (calendar != null && i == calendar.get(Calendar.MINUTE)) {
							timeRenderer.append(" selected=\"selected\"");
						}
						timeRenderer.append(">").append(Integer.toString(i)).append("</option>");
					}
					timeRenderer.append("</select>");

					// if 12 hour clock, write the AM/PM selector
					if (isTwelveHour) {
						String amSelected = ((calendar != null && calendar.get(Calendar.AM_PM) == Calendar.AM) ? "selected=\"selected\"" : "");
						String pmSelected = ((calendar != null && calendar.get(Calendar.AM_PM) == Calendar.PM) ? "selected=\"selected\"" : "");
						timeRenderer.append("<select name=\"").append(UtilHttp.makeCompositeParam(paramName + "_fld0_value", "ampm")).append("\"");
						timeRenderer.append(" id=\"").append(UtilHttp.makeCompositeParam(idName + "_fld0_value", "ampm")).append("\"");
						appendClassNames(timeRenderer, context, modelFormField);
						timeRenderer.append(">");
						timeRenderer.append("<option value=\"AM\" ").append(amSelected).append(">AM</option>");
						timeRenderer.append("<option value=\"PM\" ").append(pmSelected).append(">PM</option>");
						timeRenderer.append("</select>");
					}

				} else if ("text".equals(dateFindField.getInputMethod())) {
					timeRenderer.append("<input type=\"text\" ");

					timeRenderer.append(" name=\"");
					timeRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld0_value", "hour"));
					timeRenderer.append('"');
					timeRenderer.append(" id=\"");
					timeRenderer.append(UtilHttp.makeCompositeParam(idName + "_fld0_value", "hour"));
					timeRenderer.append('"');

					appendClassNames(timeRenderer, "hour time", context, modelFormField);
					timeRenderer.append(" value=\"");
					if (hour > 0)
						timeRenderer.append(hour);
					timeRenderer.append("\" />");
					timeRenderer.append("<span id=\"time-separator\">:</span>");

					timeRenderer.append("<input type=\"text\" ");

					timeRenderer.append(" name=\"");
					timeRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld0_value", "minutes"));
					timeRenderer.append('"');
					timeRenderer.append(" id=\"");
					timeRenderer.append(UtilHttp.makeCompositeParam(idName + "_fld0_value", "minutes"));
					timeRenderer.append('"');

					appendClassNames(timeRenderer, "minutes time", context, modelFormField);
					timeRenderer.append(" value=\"");
					if (minutes > 0)
						timeRenderer.append(minutes);
					timeRenderer.append("\" />");
				}

				if (UtilValidate.isEmpty(firstFieldName)) {
					firstFieldName = UtilHttp.makeCompositeParam(paramName + "_fld0_value", "hour");
				}

				dateArray.add(timeRenderer.toString());
			}

			// create a hidden field for the composite type, which is a Timestamp
			StringBuilder compositeTypeRenderer = new StringBuilder("<input type=\"hidden\" name=\"");
			compositeTypeRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld0_value", "compositeType"));
			compositeTypeRenderer.append("\" value=\"").append(dateFindField.getType().substring(0, 1).toUpperCase()).append(dateFindField.getType().substring(1)).append("\"/>");
			dateArray.add(compositeTypeRenderer.toString());

			for(String stringToRender : dateArray) {
				writer.append(stringToRender);
			}
		} else {
			String value =  dateFindField.getValue(context, "_fld0_value");
			DateFormat dfDate = UtilDateTime.toDateFormat(UtilDateTime.getDateFormat((Locale)context.get("locale")),(TimeZone)context.get("timeZone"), (Locale)context.get("locale"));
			try {
				if(UtilValidate.isNotEmpty(value) && "time-dropdown".equals(dateFindField.getInputMethod())) {
					Date d = UtilDateTime.toSqlDate(month, day, year);
					value = dfDate.format(d);
				}
			} catch(Exception e) {
				value=null;
			}

			// search for a localized label for the icon
			if (uiLabelMap != null) {
				localizedIconTitle = (String) uiLabelMap.get("CommonViewCalendar");
			}
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"paramName\" value=\"").append(("time-dropdown".equals(dateFindField.getInputMethod()) ? UtilHttp.makeCompositeParam(paramName+ "_fld0_value", "date") : paramName+ "_fld0_value")).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"time\" value=\"").append(Boolean.toString("time".equals(dateFindField.getType()))).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"shortDateInput\" value=\"").append(Boolean.toString(shortDateInput)).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"dateTimeValue\" value=\"").append(UtilHttp.encodeBlanks(dateTimeValue)).append("\"/>");
			if (dateFindField.getShowFormat() && UtilValidate.isEmpty(value))
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"localizedDefaultFormatToShow\" value=\"").append(localizedDefaultFormatToShow).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"localizedInputTitle\" value=\"").append(localizedInputTitle).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"localizedIconTitle\" value=\"").append(localizedIconTitle).append("\"/>");

			String yearRange = "";
			if (UtilValidate.isNotEmpty(dateFindField.getYearRange())) {
				yearRange = dateFindField.getYearRange().toString();
			}
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"yearRange\" value=\"").append(yearRange).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"localizedValue\" value=\"").append(value).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"size\" value=\"").append(Integer.toString(size)).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"maxlength\" value=\"").append(Integer.toString(maxlength)).append("\"/>");
			writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"locale\" value=\"").append(UtilHttp.getLocale(request).getLanguage()).append("\"/>");
			//            writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"imagesrc\" value=\"");
			//            this.appendContentUrl(writer, dateFindField.getImageSrc());
			//            writer.append("\"/>");
		}

		if (!dateFindField.getSingleOption()) {
			String defaultOptionFrom = dateFindField.getDefaultOptionFrom();
			String operation = (String)context.get(paramName + "_fld0_op");
			if(operation != null) defaultOptionFrom = operation;
			writer.append(" <select name=\"");
			writer.append(paramName);
			writer.append("_fld0_op\" class=\"selectBox\">");

			if (dateFindField.getRangeComponent()) {
				String opBetween = UtilProperties.getMessage("conditional", "between", locale);
				String opNotBetween = UtilProperties.getMessage("conditional", "not_between", locale);
				writer.append("<option value=\"between\"").append(("between".equals(defaultOptionFrom)? " selected=\"selected\"": "")).append(">").append(opBetween).append("</option>");
				writer.append("<option value=\"notBetween\"").append(("notBetween".equals(defaultOptionFrom)? " selected=\"selected\"": "")).append(">").append(opNotBetween).append("</option>");
			}

			writer.append("<option value=\"equals\"").append(("equals".equals(defaultOptionFrom)? " selected=\"selected\"": "")).append(">").append(opEquals).append("</option>");
			writer.append("<option value=\"notEqual\"").append(("notEqual".equals(defaultOptionFrom)? " selected=\"selected\"": "")).append(">").append(opNotEqual).append("</option>");
			writer.append("<option value=\"lessThan\"").append(("lessThan".equals(defaultOptionFrom)? " selected=\"selected\"": "")).append(">").append(opLessThan).append("</option>");
			writer.append("<option value=\"greaterThan\"").append(("greaterThan".equals(defaultOptionFrom)? " selected=\"selected\"": "")).append(">").append(opGreaterThan).append("</option>");
			writer.append("<option value=\"empty\"").append(("empty".equals(defaultOptionFrom)? " selected=\"selected\"": "")).append(">").append(opIsEmpty).append("</option>");
			writer.append("<option value=\"notEmpty\"").append(("notEmpty".equals(defaultOptionFrom)? " selected=\"selected\"": "")).append(">").append(opIsNotEmpty).append("</option>");

			writer.append("</select>");
		}

		writer.append("</div>");

		if (!dateFindField.getSingleOption()) {
			writer.append(" <br/> ");
		}

		if (dateFindField.getRangeComponent()) {
			dateTimeValue = modelFormField.getEntry(context, defaultDateTimeString, "_fld1_value");

			try {
				if (UtilValidate.isNotEmpty(dateTimeValue)) {
					if (!Pattern.matches("^.+([0-1]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)(\\.\\d+){0,1}$", dateTimeValue)) {
						if (Pattern.matches("^.+([0-1]\\d|2[0-3]):([0-5]\\d)$", dateTimeValue)) {
							dateTimeValue += ":00";
						} else if (Pattern.matches("^.+\\\\s+([0-1]\\d|2[0-3])$", dateTimeValue)) {
							dateTimeValue += ":00:00";
						} else {
							dateTimeValue += " 00:00:00";
						}
					}

					Timestamp defaultTimestamp = (Timestamp)ObjectType.simpleTypeConvert(dateTimeValue, "java.sql.Timestamp", null, (Locale)context.get("locale"));

					if (UtilValidate.isNotEmpty(defaultTimestamp)) {
						calendar.setTime(defaultTimestamp);

						day = calendar.get(Calendar.DAY_OF_MONTH);
						year = calendar.get(Calendar.YEAR);
						month = calendar.get(Calendar.MONTH)+1;
						hour = calendar.get(Calendar.HOUR_OF_DAY);
						minutes = calendar.get(Calendar.MINUTE);
					}
				}
			} catch (IllegalArgumentException e) {
				Debug.logWarning("Form widget field [" + paramName + "] with input-method=\"time-dropdown\" was not able to understand the default time ["
						+ defaultDateTimeString + "]. The parsing error was: " + e.getMessage(), module);
			} catch (GeneralException e) {}

			writer.append("<div class=\"datePanel calendarSingleForm\" id=\"").append(paramName).append("_fld1_value_datePanel\">");
			if (!this.javaScriptEnabled) {
				String firstFieldName = "";
				List<String> dateArray = new FastList<String>();

				if (dateFindField.getType().equals("date") || dateFindField.getType().equals("timestamp")) {
					int yearPosition=-1;
					int dayPosition=-1;

					//Render dell'anno
					StringBuilder yearRenderer = new StringBuilder("<input type=\"text\" ");

					yearRenderer.append(" name=\"");
					yearRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld1_value", "year"));
					yearRenderer.append('"');
					yearRenderer.append(" id=\"");
					yearRenderer.append(UtilHttp.makeCompositeParam(idName + "_fld1_value", "year"));
					yearRenderer.append('"');
					yearRenderer.append(" maxlength=\"4\" ");

					appendClassNames(yearRenderer, "year date", context, modelFormField);
					yearRenderer.append(" value=\"");
					if (year > 0)
						yearRenderer.append(year);
					yearRenderer.append("\" />");

					//Render del giorno
					StringBuilder dayRenderer = new StringBuilder("<input type=\"text\" ");

					dayRenderer.append(" name=\"");
					dayRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld1_value", "day"));
					dayRenderer.append('"');
					dayRenderer.append(" id=\"");
					dayRenderer.append(UtilHttp.makeCompositeParam(idName + "_fld1_value", "day"));
					dayRenderer.append('"');
					dayRenderer.append(" maxlength=\"2\" ");

					appendClassNames(dayRenderer, "day date", context, modelFormField);
					dayRenderer.append(" value=\"");
					if (day > 0)
						dayRenderer.append(day);
					dayRenderer.append("\" />");

					StringBuilder monthRenderer = new StringBuilder("<select ");

					monthRenderer.append(" name=\"");
					monthRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld1_value", "month"));
					monthRenderer.append('"');
					monthRenderer.append(" id=\"");
					monthRenderer.append(UtilHttp.makeCompositeParam(idName + "_fld1_value", "month"));
					monthRenderer.append('"');

					appendClassNames(monthRenderer, "month date", context, modelFormField);
					monthRenderer.append(" >");
					if (UtilValidate.isNotEmpty(monthRenderer)) {
						for (int i = 0; i < months.length; i++) {
							if (UtilValidate.isNotEmpty(months[i])) {
								monthRenderer.append("<option value=\"");
								monthRenderer.append(Integer.toString(i+1));
								if (month-1 == i)
									monthRenderer.append(" selected=\"selected\"");
								monthRenderer.append("\">");
								monthRenderer.append(months[i]);
								monthRenderer.append("</option>");
							}
						}
					}
					monthRenderer.append("</select>");

					firstFieldName = UtilHttp.makeCompositeParam(paramName, "month");
					dateArray.add(monthRenderer.toString());
					if (UtilValidate.isNotEmpty(localizedDateFormat)) {
						if (Pattern.matches("^(yy|yyyy)[/-\\\\s].+[/-\\\\s].+$", localizedDateFormat)) {
							yearPosition=0;
							firstFieldName = UtilHttp.makeCompositeParam(paramName, "year");
						} else if (Pattern.matches("^.+[/-\\\\s](yy|yyyy)[/-\\\\s].+$", localizedDateFormat)) {
							yearPosition = 1;
						} else if (Pattern.matches("^.+[/-\\\\s].+[/-\\\\s](yy|yyyy).+$", localizedDateFormat)) {
							yearPosition = 2;
						}
						if (Pattern.matches("^(dd)[/-\\\\s].+[/-\\\\s].+$", localizedDateFormat)) {
							dayPosition=0;
							firstFieldName = UtilHttp.makeCompositeParam(paramName, "day");
						} else if (Pattern.matches("^.+[/-\\\\s](dd)[/-\\\\s].+$", localizedDateFormat)) {
							dayPosition = 1;
						} else if (Pattern.matches("^.+[/-\\\\s].+[/-\\\\s](dd).+$", localizedDateFormat)) {
							dayPosition = 2;
						}
					}
					if (yearPosition==-1 && dayPosition ==-1) {
						dateArray.add(dayRenderer.toString());
						dateArray.add(yearRenderer.toString());
					} else {
						if (yearPosition >= dateArray.size()) {
							dateArray.add(yearRenderer.toString());
						} else if (yearPosition != -1){
							dateArray.add(yearPosition, yearRenderer.toString());
						}
						if (dayPosition >= dateArray.size()) {
							dateArray.add(dayRenderer.toString());
						} else if (dayPosition != -1){
							dateArray.add(dayPosition, dayRenderer.toString());
						}
					}
				}

				if (Pattern.matches("^.+[/-\\\\s].+[/-\\\\s].+\\\\s(HH):(mm)$", localizedDateFormat) || dateFindField.getType().equals("time") || dateFindField.getType().equals("timestamp")) {
					StringBuilder timeRenderer = new StringBuilder();

					// if we have an input method of time-dropdown, then render two dropdowns
					if ("time-dropdown".equals(dateFindField.getInputMethod())) {
						boolean isTwelveHour = "12".equals(dateFindField.getClock());

						// write the select for hours
						timeRenderer.append("&nbsp;<select name=\"").append(UtilHttp.makeCompositeParam(paramName + "_fld1_value", "hour")).append("\"");
						timeRenderer.append(" id=\"" + UtilHttp.makeCompositeParam(idName + "_fld1_value", "hour") + "\" ");
						appendClassNames(timeRenderer,"hour time", context, modelFormField);
						timeRenderer.append(">");

						// keep the two cases separate because it's hard to understand a combined loop
						if (isTwelveHour) {
							for (int i = 1; i <= 12; i++) {
								timeRenderer.append("<option value=\"").append(Integer.toString(i)).append("\"");
								if (calendar != null) {
									if (hour == 0) hour = 12;
									if (hour > 12) hour -= 12;
									if (i == hour) writer.append(" selected=\"selected\"");
								}
								timeRenderer.append(">").append(Integer.toString(i)).append("</option>");
							}
						} else {
							for (int i = 0; i < 24; i++) {
								timeRenderer.append("<option value=\"").append(Integer.toString(i)).append("\"");
								if (calendar != null && i == calendar.get(Calendar.HOUR_OF_DAY)) {
									timeRenderer.append(" selected=\"selected\"");
								}
								timeRenderer.append(">").append(Integer.toString(i)).append("</option>");
							}
						}

						// write the select for minutes
						timeRenderer.append("</select>");
						timeRenderer.append("<span id=\"time-separator\">:</span>");
						timeRenderer.append("<select name=\"");
						timeRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld1_value", "minutes") + "\"");
						timeRenderer.append(" id=\"").append(UtilHttp.makeCompositeParam(idName + "_fld1_value", "minutes")).append("\" ");
						appendClassNames(timeRenderer,"minutes time", context, modelFormField);
						timeRenderer.append(">");
						for (int i = 0; i < 60; i++) {
							timeRenderer.append("<option value=\"").append(Integer.toString(i)).append("\"");
							if (calendar != null && i == calendar.get(Calendar.MINUTE)) {
								timeRenderer.append(" selected=\"selected\"");
							}
							timeRenderer.append(">").append(Integer.toString(i)).append("</option>");
						}
						timeRenderer.append("</select>");

						// if 12 hour clock, write the AM/PM selector
						if (isTwelveHour) {
							String amSelected = ((calendar != null && calendar.get(Calendar.AM_PM) == Calendar.AM) ? "selected=\"selected\"" : "");
							String pmSelected = ((calendar != null && calendar.get(Calendar.AM_PM) == Calendar.PM) ? "selected=\"selected\"" : "");
							timeRenderer.append("<select name=\"").append(UtilHttp.makeCompositeParam(paramName + "_fld1_value", "ampm")).append("\"");
							timeRenderer.append(" id=\"").append(UtilHttp.makeCompositeParam(idName + "_fld1_value", "ampm")).append("\"");
							appendClassNames(timeRenderer, context, modelFormField);
							timeRenderer.append(">");
							timeRenderer.append("<option value=\"AM\" ").append(amSelected).append(">AM</option>");
							timeRenderer.append("<option value=\"PM\" ").append(pmSelected).append(">PM</option>");
							timeRenderer.append("</select>");
						}

					} else if ("text".equals(dateFindField.getInputMethod())) {
						timeRenderer.append("<input type=\"text\" ");

						timeRenderer.append(" name=\"");
						timeRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld1_value", "hour"));
						timeRenderer.append('"');
						timeRenderer.append(" id=\"");
						timeRenderer.append(UtilHttp.makeCompositeParam(idName + "_fld1_value", "hour"));
						timeRenderer.append('"');

						appendClassNames(timeRenderer, "hour time", context, modelFormField);
						timeRenderer.append(" value=\"");
						if (hour > 0)
							timeRenderer.append(hour);
						timeRenderer.append("\" />");
						timeRenderer.append("<span id=\"time-separator\">:</span>");

						timeRenderer.append("<input type=\"text\" ");

						timeRenderer.append(" name=\"");
						timeRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld1_value", "minutes"));
						timeRenderer.append('"');
						timeRenderer.append(" id=\"");
						timeRenderer.append(UtilHttp.makeCompositeParam(idName + "_fld1_value", "minutes"));
						timeRenderer.append('"');

						appendClassNames(timeRenderer, "minutes time", context, modelFormField);
						timeRenderer.append(" value=\"");
						if (minutes > 0)
							timeRenderer.append(minutes);
						timeRenderer.append("\" />");
					}

					if (UtilValidate.isEmpty(firstFieldName)) {
						firstFieldName = UtilHttp.makeCompositeParam(paramName + "_fld1_value", "hour");
					}

					dateArray.add(timeRenderer.toString());
				}

				// create a hidden field for the composite type, which is a Timestamp
				StringBuilder compositeTypeRenderer = new StringBuilder("<input type=\"hidden\" name=\"");
				compositeTypeRenderer.append(UtilHttp.makeCompositeParam(paramName + "_fld1_value", "compositeType"));
				compositeTypeRenderer.append("\" value=\"").append(dateFindField.getType().substring(0, 1).toUpperCase()).append(dateFindField.getType().substring(1)).append("\"/>");
				dateArray.add(compositeTypeRenderer.toString());

				for(String stringToRender : dateArray) {
					writer.append(stringToRender);
				}
			} else {
				String value =  dateFindField.getValue(context, "_fld1_value");
				DateFormat dfDate = UtilDateTime.toDateFormat(UtilDateTime.getDateFormat((Locale)context.get("locale")),(TimeZone)context.get("timeZone"), (Locale)context.get("locale"));
				try {
					if(UtilValidate.isNotEmpty(value) && "time-dropdown".equals(dateFindField.getInputMethod())) {
						Date d = UtilDateTime.toSqlDate(month, day, year);
						value = dfDate.format(d);
					}
				} catch(Exception e) {
					value=null;
				}

				// search for a localized label for the icon
				if (uiLabelMap != null) {
					localizedIconTitle = (String) uiLabelMap.get("CommonViewCalendar");
				}
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"paramName\" value=\"").append(("time-dropdown".equals(dateFindField.getInputMethod()) ? UtilHttp.makeCompositeParam(paramName+ "_fld1_value", "date") : paramName+ "_fld1_value")).append("\"/>");
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"time\" value=\"").append(Boolean.toString("time".equals(dateFindField.getType()))).append("\"/>");
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"shortDateInput\" value=\"").append(Boolean.toString(shortDateInput)).append("\"/>");
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"dateTimeValue\" value=\"").append(UtilHttp.encodeBlanks(dateTimeValue)).append("\"/>");
				if (dateFindField.getShowFormat() && UtilValidate.isEmpty(value))
					writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"localizedDefaultFormatToShow\" value=\"").append(localizedDefaultFormatToShow).append("\"/>");
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"localizedInputTitle\" value=\"").append(localizedInputTitle).append("\"/>");
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"localizedIconTitle\" value=\"").append(localizedIconTitle).append("\"/>");

				String yearRange = "";
				if (UtilValidate.isNotEmpty(dateFindField.getYearRange())) {
					yearRange = dateFindField.getYearRange().toString();
				}
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"yearRange\" value=\"").append(yearRange).append("\"/>");
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"localizedValue\" value=\"").append(value).append("\"/>");
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"size\" value=\"").append(Integer.toString(size)).append("\"/>");
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"maxlength\" value=\"").append(Integer.toString(maxlength)).append("\"/>");
				writer.append("<input type=\"hidden\" class=\"dateParams\" name=\"locale\" value=\"").append(UtilHttp.getLocale(request).getLanguage()).append("\"/>");
			}

			writer.append("</div>");

			/*          String defaultOptionThru = dateFindField.getDefaultOptionThru();
            writer.append(" <select name=\"");
            writer.append(modelFormField.getParameterName(context));
            if (!dateFindField.getSingleOption()) {
                writer.append("_fld1_op\" class=\"selectBox\">");
            } else {
                writer.append("_op\" class=\"selectBox\">");
            }
            if (dateFindField.getRangeComponent()) {
                String opBetween = UtilProperties.getMessage("conditional", "between", locale);
                String opNotBetween = UtilProperties.getMessage("conditional", "not_between", locale);
                writer.append("<option value=\"between\"" + ("between".equals(defaultOptionThru)? " selected": "") + ">" + opBetween + "</option>");
                writer.append("<option value=\"notBetween\"" + ("notBetween".equals(defaultOptionThru)? " selected": "") + ">" + opNotBetween + "</option>");
            }

            writer.append("<option value=\"equals\"" + ("equals".equals(defaultOptionThru)? " selected": "") + ">" + opEquals + "</option>");
            writer.append("<option value=\"notEqual\"" + ("notEqual".equals(defaultOptionThru)? " selected": "") + ">" + opNotEqual + "</option>");
            writer.append("<option value=\"lessThan\"" + ("lessThan".equals(defaultOptionThru)? " selected": "") + ">" + opLessThan + "</option>");
            writer.append("<option value=\"greaterThan\"" + ("greaterThan".equals(defaultOptionThru)? " selected": "") + ">" + opGreaterThan + "</option>");
            writer.append("<option value=\"empty\"" + ("empty".equals(defaultOptionThru)? " selected": "") + ">" + opIsEmpty + "</option>");
            writer.append("<option value=\"notEmpty\"" + ("notEmpty".equals(defaultOptionThru)? " selected": "") + ">" + opIsNotEmpty + "</option>");
            writer.append("</select>");
			 */
		}

		this.appendTooltip(writer, context, modelFormField);

		appendWhitespace(writer);
	}

	/**
	 * @author Maps spa
	 * Added modal lookup behavior
	 * @param writer
	 * @param context
	 * @param lookupField
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected void renderModalLookupField(Appendable writer, Map<String, Object> context, LookupField lookupField) throws IOException {

		ModelFormField modelFormField = lookupField.getModelFormField();
		ModelForm modelForm = modelFormField.getModelForm();
		ModelFormField.AutoComplete autoComplete = lookupField.getAutoComplete();

		// Fields in the form
		String idName = modelFormField.getIdName(context);
		String currentValue = modelFormField.getEntry(context, lookupField.getDefaultValue(context));
		String lookupValueFieldName = modelFormField.getParameterName(context);

		// Fields from entity to search
		String editFieldName = lookupField.getEditFieldName();
		String keyFieldName = lookupField.getKeyFieldName();
		if (UtilValidate.isEmpty(keyFieldName))
			keyFieldName = modelFormField.getFieldName();
		String descFieldName = lookupField.getDescriptionFieldName();
		String searchFieldName = (UtilValidate.isNotEmpty(editFieldName) ? editFieldName : keyFieldName);

		String lookupEditFieldName = (UtilValidate.isNotEmpty(editFieldName) ? editFieldName + "_" + lookupValueFieldName : lookupValueFieldName);

		String classNames = "";
		boolean isReadOnly = lookupField.isReadOnly(context);
		if (isReadOnly)
			classNames = "readonly";
		boolean isFormList = "list".equals(modelForm.getType());
		boolean isFormMulti = "multi".equals(modelForm.getType());
		boolean isFormSingle = !(isFormList || isFormMulti);
		int size = lookupField.getSize();

		// Get the current value's description from the option value. If there
		// is a localized version it will be there.
		Map<String, ? extends Object> currentValueMap = null;

		if (UtilValidate.isNotEmpty(currentValue)) {
            ModelFormField.OptionValue optionValueFound = null;
            boolean renderByPK = "true".equals(UtilProperties.getPropertyValue("BaseConfig", "HtmlFormRenderer.autocomplete.renderByPK"));
            if (renderByPK && UtilValidate.isNotEmpty(lookupField.getOptionSources()) && ObjectType.instanceOf(EntityOptions.class, lookupField.getOptionSources().get(0))) {
                EntityOptions entityOptions = (EntityOptions)lookupField.getOptionSources().get(0);
                optionValueFound = entityOptions.findOptionValueByPK(StringEscapeUtils.unescapeHtml(currentValue), context, WidgetWorker.getDelegator(context));
            } else {
                List<ModelFormField.OptionValue> allOptionValues = lookupField.getAllOptionValues(context, WidgetWorker.getDelegator(context));

                for (ModelFormField.OptionValue optionValue : allOptionValues) {
                    if (optionValue.getKey().equals(currentValue)) {
                        optionValueFound = optionValue;
                        break;
                    }
                }                               
            }
            if (optionValueFound != null) {
                currentValueMap = optionValueFound.getAuxiliaryMap();
            }
		}

		final String brClear = "<br class=\"clear\"/>";

		// uso attributo tooltip della lookup per popolare il tooltip,
		// i valori della FlexibleStringExpander sono un merge di context e currentValueMap
		MapStack<String> localContext = null;
		String tooltipValue = "";
		if(UtilValidate.isNotEmpty(currentValueMap)){
			localContext = MapStack.create(context);
    		localContext.push((Map<String, Object>) currentValueMap);
		}
		if(lookupField.showTooltip() && UtilValidate.isNotEmpty(localContext) && UtilValidate.isNotEmpty(lookupField.getTooltip(localContext))){
			tooltipValue = lookupField.getTooltip(localContext);
		}
		
		if (isReadOnly && !isFormSingle) {

			// input hidden for field value

			writer.append("<input type=\"hidden\" name=\"").append(lookupValueFieldName).append("\" value=\"").append(currentValue).append("\"");
			writer.append(" id=\"").append(lookupValueFieldName).append("\"");
			writer.append(" />");

			// input text read-only for description

			String descValue = "";
			if (UtilValidate.isNotEmpty(currentValueMap)) {
				Object obj = currentValueMap.get(searchFieldName);
				if (UtilValidate.isNotEmpty(obj))
					descValue += obj.toString();
				if (UtilValidate.isNotEmpty(descFieldName)) {
					obj = currentValueMap.get(descFieldName);
					if (UtilValidate.isNotEmpty(obj)) {
						if (UtilValidate.isNotEmpty(descValue))
							descValue += " - ";
						descValue += obj.toString();
					}
				}
			}

			String descFieldNameRO = UtilValidate.isNotEmpty(descFieldName) ? descFieldName : "description";
			writer.append("<input type=\"text\" name=\"").append(descFieldNameRO).append("_").append(lookupValueFieldName).append("\"");
			writer.append(" value=\"").append(descValue).append("\" ");
			if(lookupField.showTooltip() && UtilValidate.isNotEmpty(tooltipValue)){
				writer.append(" title=\"").append(tooltipValue).append("\" ");
			}
			writer.append(" size=\"").append(Integer.toString(size)).append("\"");
			appendClassNames(writer, concatClassNames(classNames, "lookup_field_description"), context, modelFormField);
			writer.append(" readonly=\"readonly\" />");

		} else {

			//Start field construction
			writer.append("<div ");

			appendClassNames(writer, concatClassNames(classNames, "lookup_field"), context, modelFormField);

			writer.append(" id=\"");
			writer.append(idName);
			writer.append("\">");

			//Autocompleter option
			if (UtilValidate.isNotEmpty(autoComplete.getChoices())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"choices\" value=\"");
				writer.append(autoComplete.getChoices());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getFrequency())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"frequency\" value=\"");
				writer.append(autoComplete.getFrequency());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getFullSearch())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"fullSearch\" value=\"");
				writer.append(autoComplete.getFullSearch());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getIgnoreCase())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"ignoreCase\" value=\"");
				writer.append(autoComplete.getIgnoreCase());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getMinChars())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"minChars\" value=\"");
				writer.append(autoComplete.getMinChars());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getPartialChars())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"partialChars\" value=\"");
				writer.append(autoComplete.getPartialChars());
				writer.append("\"/>");
			}
			if (UtilValidate.isNotEmpty(autoComplete.getPartialSearch())) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"partialSearch\" value=\"");
				writer.append(autoComplete.getPartialSearch());
				writer.append("\"/>");
			}
			String target = autoComplete.getTarget();
			if (UtilValidate.isEmpty(target)) {
				target = "ajaxAutocompleteOptions";
			}
			writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"target\" value=\"");
			writer.append(this.rh.makeLink(request, response, target));
			writer.append("\"/>");

			if (lookupField.isLookupAutocomplete()) {
				writer.append("<input  class=\"autocompleter_option\" type=\"hidden\" name=\"lookupAutocomplete\" value=\"Y\"/>");
			}

			//Now add target-parameters
			List<String> targetParameterList = lookupField.getTargetParameterList(context);
			if (UtilValidate.isNotEmpty(targetParameterList)) {
				for (String parameter : targetParameterList) {
					List<String> parameterSplitted = StringUtil.split(parameter, "=");
					if (UtilValidate.isNotEmpty(parameterSplitted)) {
						if (parameterSplitted.size() == 2) {
							writer.append("<input  class=\"lookup_parameter\" type=\"hidden\" name=\"").append(parameterSplitted.get(0)).append("\" value=\"");
							writer.append(parameterSplitted.get(1));
							writer.append("\"/>");
						}
					}
				}
			}

			List<String> displayFieldList = null;
			List<String> hiddenFieldList = null;
			List<String> hiddenDisabledFieldList = null;

			//Autocompleter field
			List<OptionSource> allOptionSource = lookupField.getOptionSources();
			if (UtilValidate.isNotEmpty(allOptionSource) && ObjectType.instanceOf(EntityOptions.class, allOptionSource.get(0))) {
				List<EntityOptions> allEntityOption = UtilGenerics.checkList(allOptionSource);
				EntityOptions entityOptions = allEntityOption.get(0);

				List<String> entityNameList = null;
				List<String> selectFieldList = null;
				List<String> orderByFieldList = null;
				List<String> constraintParm = null;

				String entityName = entityOptions.getEntityName(context);
				if (UtilValidate.isNotEmpty(entityName)) {
					if (UtilValidate.isEmpty(entityNameList)) {
						entityNameList = new FastList<String>();
					}
					entityNameList.add(entityName);
				}

				List<String> originalSelectField = entityOptions.getSelectField(context);
				if (UtilValidate.isNotEmpty(originalSelectField)) {
					if (UtilValidate.isEmpty(selectFieldList)) {
						selectFieldList = new FastList<String>();
					}
					selectFieldList.add(this.makeCsvString(originalSelectField));
				}

				List<String> originalOrderByList = entityOptions.getOrderByField(context);
				if (UtilValidate.isNotEmpty(originalOrderByList)) {
					if (UtilValidate.isEmpty(orderByFieldList)) {
						orderByFieldList = new FastList<String>();
					}
					orderByFieldList.add(this.makeCsvString(originalOrderByList));
				}

				List<String> originalDisplayFieldList = entityOptions.getDisplayField(context);
				if (UtilValidate.isNotEmpty(originalDisplayFieldList)) {
					if (UtilValidate.isEmpty(displayFieldList)) {
						displayFieldList = new FastList<String>();
					}
					displayFieldList.add(this.makeCsvString(originalDisplayFieldList));
				}

				List<String> originalHiddenFieldList = entityOptions.getHiddenField(context);
				if (UtilValidate.isNotEmpty(originalHiddenFieldList)) {
					if (UtilValidate.isEmpty(hiddenFieldList)) {
						hiddenFieldList = new FastList<String>();
					}
					hiddenFieldList.add(this.makeCsvString(originalHiddenFieldList));
				}

				List<String> originalHiddenDisabledFieldList = entityOptions.getHiddenDisabledField(context);
				if (UtilValidate.isNotEmpty(originalHiddenDisabledFieldList)) {
					if (UtilValidate.isEmpty(hiddenDisabledFieldList)) {
						hiddenDisabledFieldList = new FastList<String>();
					}
					hiddenDisabledFieldList.add(this.makeCsvString(originalHiddenDisabledFieldList));
				}

				if (UtilValidate.isNotEmpty(entityOptions.getConstraintList())) {
					for (EntityFinderUtil.ConditionExpr constraint: entityOptions.getConstraintList()) {
						List<String> parmsList = FastList.newInstance();
						parmsList.add(constraint.getFieldName(context));
						parmsList.add(constraint.getOperator(context));

						String value = "";
						String lookupForFieldName = constraint.getLookupForFieldName(context);
						List<String> lookupForFieldNameSplittedList = StringUtil.split(lookupForFieldName, ",");
						if (UtilValidate.isNotEmpty(lookupForFieldNameSplittedList)) {
							for (String lookupForFieldNameItem : lookupForFieldNameSplittedList) {
								ModelFormField lookupForFormField = modelForm.getModelFormField(lookupForFieldNameItem, context);
								if (UtilValidate.isNotEmpty(lookupForFormField)) {
									if (value.length() > 0)
										value += ",";
									value += "field:" + lookupForFormField.getFieldInfo().getParameterName(context);
								}
							}
							if (UtilValidate.isNotEmpty(value)) {
								parmsList.add(value);
							}
						} else {
						    Object valueObj = constraint.getValue(context);
						    if (valueObj instanceof java.sql.Timestamp) {
						        value = ((java.sql.Timestamp)valueObj).toString();
						    } else if (valueObj != null) {
						        value = String.valueOf(valueObj);
						    }
						    if (UtilValidate.isNotEmpty(value)) {
								parmsList.add(value);
							}
						}

						if (UtilValidate.isEmpty(constraintParm)) {
							constraintParm = new FastList<String>();
						}
						constraintParm.add(this.makeCsvString(parmsList, "| "));
					}
				}

				writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"entityName\" value=\"");
				writer.append(this.makeCsvString(entityNameList));
				writer.append("\"/>");

				String lookupTarget = lookupField.getTarget(context);
				if (UtilValidate.isEmpty(lookupTarget))
					lookupTarget = "lookup";
				lookupTarget = this.rh.makeLink(request, response, lookupTarget);
				writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"lookupTarget\" value=\"");
				writer.append(lookupTarget);
				writer.append("\"/>");

				if (UtilValidate.isNotEmpty(selectFieldList)) {
					writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"selectFields\" value=\"");
					writer.append(this.makeCsvString(selectFieldList, "; "));
					writer.append("\"/>");
				}

				if (UtilValidate.isNotEmpty(orderByFieldList)) {
					writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"sortByFields\" value=\"");
					writer.append(this.makeCsvString(orderByFieldList, "; "));
					writer.append("\"/>");
				}

				if (UtilValidate.isNotEmpty(displayFieldList)) {
					writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"displayFields\" value=\"");
					writer.append(this.makeCsvString(displayFieldList, "; "));
					writer.append("\"/>");
				}

				if (UtilValidate.isNotEmpty(constraintParm)) {
					List<String> tmpConstrParam = new FastList<String>();
					tmpConstrParam.add(this.makeCsvString(constraintParm, "! "));

					constraintParm = tmpConstrParam;

					writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"constraintFields\" value=\"");
					writer.append(UtilFormatOut.encodeXmlValue(this.makeCsvString(constraintParm, "; ")));
					writer.append("\"/>");
				}

			} else {
				//TODO
				//Considerare il caso di Autocompleter.Local
				//            for (ModelFormField.OptionValue optionValue: allOptionValues) {
				//                writer.append("<input type=\"hidden\" class=\"autocompleter_local_data\" id=\"" + baseId + "_" + optionValue.getKey() + "\" name=\"" + parameterName + "_" + optionValue.getKey() + "\"");
				//                writer.append(" value=\"" + optionValue.getDescription() + "\"/>");
				//            }
			}

			//Now add entity-key-field if any
			writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"entityKeyField\" value=\"").append(searchFieldName).append("\"/>");

			//Specifica se il valore ritornato dalla lookup deve essere modificato
			if (lookupField.isChangeValueMandatory()) {
				writer.append("<input  class=\"change_value_mandatory\" type=\"hidden\" name=\"change_value_mandatory\" value=\"Y\"/>");
			}

			//Now add screenLocation if any
			//        if (UtilValidate.isNotEmpty(lookupScreenLocation)) {
			//            writer.append("<input  class=\"autocompleter_parameter\" type=\"hidden\" name=\"lookupScreenLocation\" value=\"");
			//            writer.append(lookupScreenLocation);
			//            writer.append("\"/>");
			//        }

			writer.append("<div class=\"lookup_container\">");

			if (!isFormSingle)
				writer.append("<div class=\"lookup_field_container\">");
			//Input field

			String codeClassNames = concatClassNames(classNames, "lookup_field_code");
			codeClassNames = concatClassNames(codeClassNames, "lookup_field_" + searchFieldName);
			String codeFieldValue = "";
			if (UtilValidate.isNotEmpty(editFieldName)) {
				if (UtilValidate.isNotEmpty(currentValueMap)) {
					Object codeFieldValueObj = currentValueMap.get(editFieldName);
					if (UtilValidate.isNotEmpty(codeFieldValueObj))
						codeFieldValue = codeFieldValueObj.toString();
				}
			} else {
				codeFieldValue = currentValue;
			}

			writer.append("<input type=\"text\"");
			appendClassNames(writer, codeClassNames, context, modelFormField);
			writer.append("  id=\"").append(idName).append("_edit_field");
			writer.append("\" name=\"");
			writer.append(lookupEditFieldName);
			writer.append("\" value=\"");
			writer.append(codeFieldValue);
			writer.append("\" ");
			if(lookupField.showTooltip() && UtilValidate.isNotEmpty(tooltipValue)){
				writer.append(" title=\"").append(tooltipValue).append("\" ");
			}
			writer.append("size=\"");
			writer.append(Integer.toString(size));
			writer.append("\"");
			if (isReadOnly)
				writer.append(" readonly=\"readonly\"");
			if (!lookupField.getShowEditField()) {
				writer.append(" style=\"display: none\"");
			} 
			writer.append("/>");

			//Anchor
			if (isFormSingle)
				writer.append("<span class=\"lookup-anchor fa fa-2x\"><a style=\"cursor: pointer;\" class=\"lookup_field_submit\"></a></span>");

			//Display fields
			//TODO: Change to label

			if (UtilValidate.isNotEmpty(displayFieldList)) {
				List<String> firstDisplayFieldList = new FastList<String>();
				String firstDisplayField = displayFieldList.get(0);
				try {
					firstDisplayFieldList = StringUtil.toList(firstDisplayField);
				} catch (Exception e) {
					firstDisplayFieldList.add(firstDisplayField);
				}
				for (String field: firstDisplayFieldList) {
					if (!field.equals(editFieldName)) {
						writer.append(brClear);
						String fieldName;
						if (field.equals(keyFieldName))
							fieldName = lookupValueFieldName;
						else
							fieldName = field + "_" + lookupValueFieldName;
						writer.append("<input type=\"text\" class=\"lookup_field_description ");
						writer.append("lookup_field_").append(field);
						
						if(UtilValidate.isEmpty(lookupField.getDescriptionFieldSize())) {
						    writer.append(" lookup_field_nosize");
						}
						
						writer.append("\" name=\"" + fieldName + "\"");
						if (UtilValidate.isNotEmpty(currentValueMap) && UtilValidate.isNotEmpty(currentValueMap.get(field))) {
							writer.append(" value=\"").append(currentValueMap.get(field).toString()).append("\" ");
						}
						if(UtilValidate.isNotEmpty(lookupField.getDescriptionFieldSize())) {
							writer.append(" size=\"" + lookupField.getDescriptionFieldSize() + "\"");
						}
						writer.append(" readonly=\"readonly\"/>");
					}
				}
			}

			if (UtilValidate.isNotEmpty(hiddenFieldList)) {
				// Ho una lista di field esplicitati nel select-field con display = true da mostrare
				// e una lista di field esplicitati nel select-field con display = false da nascondere
				List<String> firstHiddenFieldList = new FastList<String>();
				//if (UtilValidate.isNotEmpty(displayFieldList)) {
				String firstHiddenField =  hiddenFieldList.get(0);
				try {
					firstHiddenFieldList = StringUtil.toList(firstHiddenField);
				} catch(Exception e) {
					firstHiddenFieldList.add(firstHiddenField);
				}
				//}
			for (String field: firstHiddenFieldList) {
				if (!field.equals(editFieldName)) {
					String fieldName;
					if (field.equals(keyFieldName))
						fieldName = lookupValueFieldName;
					else
						fieldName = field + "_" + lookupValueFieldName;
					writer.append("<input type=\"hidden\" class=\"lookup_field_description ");
					writer.append(field);
					writer.append("\" name=\"").append(fieldName).append("\"");
					writer.append(" id=\"").append(fieldName).append("\"");
					if (UtilValidate.isNotEmpty(currentValueMap) && UtilValidate.isNotEmpty(currentValueMap.get(field)))
						writer.append(" value=\"").append(currentValueMap.get(field).toString()).append("\" ");
					writer.append(" />");
				}
			}
			}

			if (UtilValidate.isNotEmpty(hiddenDisabledFieldList)) {
				// Ho una lista di field esplicitati nel select-field con display = true da mostrare
				// e una lista di field esplicitati nel select-field con display = false da nascondere
				List<String> firstHiddenDisabledFieldList = new FastList<String>();
				//if (UtilValidate.isNotEmpty(displayFieldList)) {
				String firstHiddenDisabledField =  hiddenDisabledFieldList.get(0);
				try {
					firstHiddenDisabledFieldList = StringUtil.toList(firstHiddenDisabledField);
				} catch(Exception e) {
					firstHiddenDisabledFieldList.add(firstHiddenDisabledField);
				}
				//}
			for (String field: firstHiddenDisabledFieldList) {
				if (!field.equals(editFieldName)) {
					String fieldName;
					if (field.equals(keyFieldName))
						fieldName = lookupValueFieldName;
					else
						fieldName = field + "_" + lookupValueFieldName;
					writer.append("<input type=\"hidden\" disabled=\"disabled\" class=\"lookup_field_description ");
					writer.append(field);
					writer.append("\" name=\"").append(fieldName).append("\"");
					writer.append(" id=\"").append(fieldName).append("\"");
					if (UtilValidate.isNotEmpty(currentValueMap) && UtilValidate.isNotEmpty(currentValueMap.get(field)))
						writer.append(" value=\"").append(currentValueMap.get(field).toString()).append("\" ");
					writer.append(" readonly=\"readonly\"/>");
				}
			}
			}

			if (!isFormSingle) {
				writer.append("</div>");
				writer.append("<div class=\"lookup_icon_container\">");
				writer.append("<span class=\"lookup-anchor fa fa-2x\"><a style=\"cursor: pointer;\" class=\"lookup_field_submit\"></a></span>");
				writer.append("</div>");
			}

			writer.append("</div>");
			writer.append("</div>");
		}

		this.appendTooltip(writer, context, modelFormField);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderLookupField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.LookupField)
	 */
	public void renderLookupField(Appendable writer, Map<String, Object> context, LookupField lookupField) throws IOException {
		ModelFormField modelFormField = lookupField.getModelFormField();

		//Maps spa - added modal behavior
		if (lookupField.isModalLookup()) {
			this.renderModalLookupField(writer, context, lookupField);
			return;
		}

		writer.append("<input type=\"text\"");

		appendClassNames(writer, context, modelFormField);

		writer.append(" name=\"");
		writer.append(modelFormField.getParameterName(context));
		writer.append('"');

		String value = modelFormField.getEntry(context, lookupField.getDefaultValue(context));
		if (UtilValidate.isNotEmpty(value)) {
			writer.append(" value=\"");
			writer.append(value);
			writer.append('"');
		}

		writer.append(" size=\"");
		writer.append(Integer.toString(lookupField.getSize()));
		writer.append('"');

		Integer maxlength = lookupField.getMaxlength();
		if (maxlength != null) {
			writer.append(" maxlength=\"");
			writer.append(maxlength.toString());
			writer.append('"');
		}

		String idName = modelFormField.getIdName();
		if (UtilValidate.isNotEmpty(idName)) {
			writer.append(" id=\"");
			writer.append(idName);
			writer.append('"');
		}

		List<ModelForm.UpdateArea> updateAreas = modelFormField.getOnChangeUpdateAreas();
		boolean ajaxEnabled = updateAreas != null && this.javaScriptEnabled;
		if (!lookupField.getClientAutocompleteField() || ajaxEnabled) {
			writer.append(" autocomplete=\"off\"");
		}

		writer.append("/>");

		// add lookup pop-up button
		String descriptionFieldName = lookupField.getDescriptionFieldName();
		if (UtilValidate.isNotEmpty(descriptionFieldName)) {
			writer.append("<a href=\"javascript:call_fieldlookup3(document.");
			writer.append(modelFormField.getModelForm().getCurrentFormName(context));
			writer.append('.');
			writer.append(modelFormField.getParameterName(context));
			writer.append(",'");
			writer.append(descriptionFieldName);
			writer.append(",'");
		} else {
			writer.append("<a href=\"javascript:call_fieldlookup2(document.");
			writer.append(modelFormField.getModelForm().getCurrentFormName(context));
			writer.append('.');
			writer.append(modelFormField.getParameterName(context));
			writer.append(",'");
		}
		writer.append(appendExternalLoginKey(lookupField.getFormName(context)));
		writer.append("'");
		List<String> targetParameterList = lookupField.getTargetParameterList(context);
		for (String targetParameter: targetParameterList) {
			// named like: document.${formName}.${targetParameter}.value
			writer.append(", document.");
			writer.append(modelFormField.getModelForm().getCurrentFormName(context));
			writer.append(".");
			writer.append(targetParameter);
			writer.append(".value");
		}
		writer.append(");\">");
		writer.append("<img src=\"");
		this.appendContentUrl(writer, "/images/fieldlookup.gif");
		writer.append("\" width=\"15\" height=\"14\" border=\"0\" alt=\"Lookup\"/></a>");

		//this.addAsterisks(writer, context, modelFormField);

		this.makeHyperlinkString(writer, lookupField.getSubHyperlink(), context);
		this.appendTooltip(writer, context, modelFormField);

		if (ajaxEnabled) {
			appendWhitespace(writer);
			writer.append("<script language=\"JavaScript\" type=\"text/javascript\">");
			appendWhitespace(writer);
			writer.append("ajaxAutoCompleter('").append(createAjaxParamsFromUpdateAreas(updateAreas, null, context)).append("');");
			appendWhitespace(writer);
			writer.append("</script>");
		}
		appendWhitespace(writer);

		//appendWhitespace(writer);
	}

	protected String appendExternalLoginKey(String target) {
		String result = target;
		String sessionId = ";jsessionid=" + request.getSession().getId();
		int questionIndex = target.indexOf("?");
		if (questionIndex == -1) {
			result += sessionId;
		} else {
			result.replace("?", sessionId + "?");
		}
		return result;
	}

	public void renderNextPrev(Appendable writer, Map<String, Object> context, ModelForm modelForm, boolean last) throws IOException {
		boolean ajaxEnabled = false;
		List<ModelForm.UpdateArea> updateAreas = modelForm.getOnPaginateUpdateAreas();
		String targetService = modelForm.getPaginateTarget(context);
		if (this.javaScriptEnabled) {
			if (UtilValidate.isNotEmpty(updateAreas)) {
				ajaxEnabled = true;
			}
		}
		if (targetService == null) {
			targetService = "${targetService}";
		}
		if (UtilValidate.isEmpty(targetService) && updateAreas == null) {
			Debug.logWarning("Cannot paginate because TargetService is empty for the form: " + modelForm.getName(), module);
			return;
		}

		// get the parametrized pagination index and size fields
		int paginatorNumber = -1;
		Map<String, Object> parameters = UtilGenerics.checkMap(context.get("parameters"));
		if (parameters != null) {
			String paginatorNumberStr = (String) parameters.get("PAGINATOR_NUMBER");
			if (UtilValidate.isNotEmpty(paginatorNumberStr)) {
				paginatorNumber = Integer.parseInt(paginatorNumberStr);
			}
		}
		if (paginatorNumber == -1) {
			paginatorNumber = WidgetWorker.getPaginatorNumber(context);
		}
		String viewIndexParam = modelForm.getMultiPaginateIndexField(context);
		String viewSizeParam = modelForm.getMultiPaginateSizeField(context);

		int viewIndex = modelForm.getViewIndex(context);
		int viewSize = getViewSize(modelForm, context);
		int listSize = modelForm.getListSize(context);

		int lowIndex = modelForm.getLowIndex(context);
		int highIndex = modelForm.getHighIndex(context);
		int actualPageSize = modelForm.getActualPageSize(context);

		if (viewIndex * viewSize > listSize && actualPageSize > 0) {
			viewIndex = lowIndex / actualPageSize;
		}

		if (listSize == 0 || (! "list".equals(modelForm.getType()) && ! "multi".equals(modelForm.getType()))) {
			return;
		}

		// needed for the "Page" and "rows" labels
		Map<String, String> uiLabelMap = UtilGenerics.checkMap(context.get("uiLabelMap"));
		String pageLabel = "";
		String commonDisplaying = "";
		if (uiLabelMap == null) {
			Debug.logWarning("Could not find uiLabelMap in context", module);
		} else {
			pageLabel = uiLabelMap.get("CommonPage");
			Map<String, Integer> messageMap = UtilMisc.toMap("lowCount", Integer.valueOf(lowIndex + 1), "highCount", Integer.valueOf(lowIndex + actualPageSize), "total", Integer.valueOf(listSize));
			commonDisplaying = UtilProperties.getMessage("CommonUiLabels", "CommonDisplaying", messageMap, (Locale) context.get("locale"));
		}

		// for legacy support, the viewSizeParam is VIEW_SIZE and viewIndexParam is VIEW_INDEX when the fields are "viewSize" and "viewIndex"
		if (viewIndexParam.equals("viewIndex" + "_" + paginatorNumber)) viewIndexParam = "VIEW_INDEX" + "_" + paginatorNumber;
		if (viewSizeParam.equals("viewSize" + "_" + paginatorNumber)) viewSizeParam = "VIEW_SIZE" + "_" + paginatorNumber;

		String str = (String) context.get("_QBESTRING_");

		// strip legacy viewIndex/viewSize params from the query string
		String queryString = UtilHttp.stripViewParamsFromQueryString(str, "" + paginatorNumber);

		// strip parametrized index/size params from the query string
		HashSet<String> paramNames = new HashSet<String>();
		paramNames.add(viewIndexParam);
		paramNames.add(viewSizeParam);
		// paramNames.add("saveView");
		queryString = UtilHttp.stripNamedParamsFromQueryString(queryString, paramNames);

		if (ajaxEnabled) {
			String viewIndexParamNoPaginator = modelForm.getMultiPaginateIndexField(context, false);
			String viewSizeParamNoPaginator = modelForm.getMultiPaginateSizeField(context, false);
			if (viewIndexParamNoPaginator.equals("viewIndex")) viewIndexParamNoPaginator = "VIEW_INDEX";
			if (viewSizeParamNoPaginator.equals("viewSize")) viewSizeParamNoPaginator = "VIEW_SIZE";
			paramNames = new HashSet<String>();
			paramNames.add(viewIndexParamNoPaginator + "_");
			paramNames.add(viewSizeParamNoPaginator + "_");
			queryString = UtilHttp.stripLikeNamedParamsFromQueryString(queryString, paramNames);
		}

		String anchor = "";
		String paginateAnchor = modelForm.getPaginateTargetAnchor();
		if ((paginateAnchor != null) && !paginateAnchor.equals("")) anchor = "#" + paginateAnchor;

		// Create separate url path String and request parameters String,
		// add viewIndex/viewSize parameters to request parameter String
		String urlPath = UtilHttp.removeQueryStringFromTarget(targetService);
		StringBuilder prepLinkBuffer = new StringBuilder();
		String prepLinkQueryString = UtilHttp.getQueryStringFromTarget(targetService);
		if (prepLinkQueryString != null) {
			prepLinkBuffer.append(prepLinkQueryString);
		}
		if (prepLinkBuffer.indexOf("?") < 0) {
			prepLinkBuffer.append("?");
		} else if (prepLinkBuffer.indexOf("?", prepLinkBuffer.length() - 1) > 0) {
			prepLinkBuffer.append("&amp;");
		}
		if (!UtilValidate.isEmpty(queryString) && !queryString.equals("null")) {
			prepLinkBuffer.append(queryString).append("&amp;");
		}
		if (ajaxEnabled)
			prepLinkBuffer.append("PAGINATOR_NUMBER=").append(paginatorNumber).append("&amp;");
		prepLinkBuffer.append(viewSizeParam).append("=").append(viewSize).append("&amp;").append(viewIndexParam).append("=");
		String prepLinkText = prepLinkBuffer.toString();
		if (ajaxEnabled) {
			// Prepare params for prototype.js
			prepLinkText = prepLinkText.replace("?", "");
			prepLinkText = prepLinkText.replace("&amp;", "&");
		}

		writer.append("<div class=\"").append(modelForm.getPaginateStyle()).append("\">");
		appendWhitespace(writer);
		writer.append(" <ul>");
		appendWhitespace(writer);

		/***
		 * Aggiungo nella ajax gli extraParams per rootTree per la paginazione dopo il back
		 */
		prepLinkText = context.get("extraParams")+ "&" + prepLinkText;
		
		String linkText;
		
		Paginator paginator = new Paginator(viewIndex, viewSize, highIndex, listSize);
		
		// First button
		writer.append("  <li class=\"").append(modelForm.getPaginateFirstStyle());
		if (paginator.showFirstPage()) {
			writer.append(" fa\"><a href=\"");						
			if (ajaxEnabled) {
				writer.append("#\" onclick=\"javascript:ajaxUpdateAreas('").append(createAjaxParamsFromUpdateAreas(updateAreas, prepLinkText + paginator.getFirstPage() + anchor, context)).append("'); return false;");
			} else {
				linkText = prepLinkText + paginator.getFirstPage() + anchor;
				appendOfbizUrl(writer, urlPath + linkText);
			}
			writer.append("\"></a>");
		} else {
			// disabled button
			writer.append("-disabled disabled fa\"><a href=\"#\"></a>");				
		}
		writer.append("</li>");
		appendWhitespace(writer);

		// Previous button
		writer.append("  <li class=\"").append(modelForm.getPaginatePreviousStyle());
		if (paginator.showPreviousPage()) {
			writer.append(" fa\"><a href=\"");
			if (ajaxEnabled) {
				writer.append("#\" onclick=\"javascript:ajaxUpdateAreas('").append(createAjaxParamsFromUpdateAreas(updateAreas, prepLinkText + paginator.getPreviousPage() + anchor, context)).append("'); return false;");
			} else {
				linkText = prepLinkText + paginator.getPreviousPage() + anchor;
				appendOfbizUrl(writer, urlPath + linkText);
			}
			writer.append("\"></a>");
		} else {
			// disabled button
			writer.append("-disabled disabled fa\"><a href=\"#\"></a>");
		}
		writer.append("</li>");
		appendWhitespace(writer);

		// Page select dropdown
		if (listSize > 0 && this.javaScriptEnabled) {
			writer.append("  <li class=\"page-selector\">").append(pageLabel).append(" <select name=\"page\" size=\"1\" onchange=\"");
			if (ajaxEnabled) {
				writer.append("javascript:ajaxUpdateAreas('").append(createAjaxParamsFromUpdateAreas(updateAreas, prepLinkText, context)).append("'+this.value)");
			} else {
				linkText = prepLinkText;
				if (linkText.startsWith("/")) {
					linkText = linkText.substring(1);
				}
				writer.append("location.href = '");
				appendOfbizUrl(writer, urlPath + linkText);
				writer.append("' + this.value;");
			}
			writer.append("\">");
			// actual value
			int page = 0;
			for (int i = 0; i < listSize;) {
				if (page == viewIndex) {
					writer.append("<option selected=\"selected\" value=\"");
				} else {
					writer.append("<option value=\"");
				}
				writer.append(Integer.toString(page));
				writer.append("\">");
				writer.append(Integer.toString(1 + page));
				writer.append("</option>");
				// increment page and calculate next index
				page++;
				i = page * viewSize;
			}
			writer.append("</select></li>");
		}

		//  show row count
		writer.append("<li class=\"pager\"><div><span>");
		writer.append(commonDisplaying);
		writer.append("</span></div></li>");
		appendWhitespace(writer);

		// Next button
		writer.append("  <li class=\"").append(modelForm.getPaginateNextStyle());
		if (paginator.showNextPage()) {
			writer.append(" fa\"><a href=\"");
			if (ajaxEnabled) {
				writer.append("#\" onclick=\"javascript:ajaxUpdateAreas('").append(createAjaxParamsFromUpdateAreas(updateAreas, prepLinkText + paginator.getNextPage() + anchor, context)).append("'); return false;");
			} else {
				linkText = prepLinkText + paginator.getNextPage() + anchor;
				appendOfbizUrl(writer, urlPath + linkText);
			}
			writer.append("\"></a>");
		} else {
			// disabled button
			writer.append("-disabled disabled fa\"><a href=\"#\"></a>");
		}
		writer.append("</li>");
		appendWhitespace(writer);

		// Last button
		writer.append("  <li class=\"").append(modelForm.getPaginateLastStyle());
		if (paginator.showLastPage()) {
			writer.append(" fa\"><a href=\"");
			if (ajaxEnabled) {
				writer.append("#\" onclick=\"javascript:ajaxUpdateAreas('").append(createAjaxParamsFromUpdateAreas(updateAreas, prepLinkText + paginator.getLastPage() + anchor, context)).append("'); return false;");
			} else {
				linkText = prepLinkText + paginator.getLastPage() + anchor;
				appendOfbizUrl(writer, urlPath + linkText);
			}
			writer.append("\"></a>");
		} else {
			// disabled button
			writer.append("-disabled disabled fa\"><a href=\"#\"></a>");
		}
		writer.append("</li>");
		appendWhitespace(writer);

		writer.append(" </ul>");
		appendWhitespace(writer);
		writer.append("</div>");
		appendWhitespace(writer);

		if (UtilValidate.isNotEmpty(context.get("REMOVE_PAGINATOR_NUMBER")) && last) {
			if (((Boolean)context.get("REMOVE_PAGINATOR_NUMBER")).booleanValue()) {
				parameters.remove("PAGINATOR_NUMBER");
			}
		}
	}

	public void renderSortField(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, String titleText) throws IOException {
		boolean ajaxEnabled = false;
		ModelForm modelForm = modelFormField.getModelForm();
		List<ModelForm.UpdateArea> updateAreas = modelForm.getOnPaginateUpdateAreas();
		String targetService = modelForm.getPaginateTarget(context);
		if (this.javaScriptEnabled) {
			if (UtilValidate.isNotEmpty(updateAreas)) {
				ajaxEnabled = true;
			}
		}
		if (targetService == null) {
			targetService = "${targetService}";
		}
		if (UtilValidate.isEmpty(targetService) && updateAreas == null) {
			Debug.logWarning("Cannot sort because TargetService is empty for the form: " + modelForm.getName(), module);
			return;
		}

		String str = (String) context.get("_QBESTRING_");
		String oldSortField = modelForm.getSortField(context);
		String sortFieldStyle = modelFormField.getSortFieldStyle();

		// if the entry-name is defined use this instead of field name
		String columnField = modelFormField.getEntryName();
		if (UtilValidate.isEmpty(columnField)) {
			columnField = modelFormField.getFieldName();
		}

		// switch beetween asc/desc order
		String newSortField = columnField;
		if (UtilValidate.isNotEmpty(oldSortField)) {
			if (oldSortField.equals(columnField)) {
				newSortField = "-" + columnField;
				sortFieldStyle = modelFormField.getSortFieldStyleDesc();
			} else if (oldSortField.equals("-" + columnField)) {
				newSortField = columnField;
				sortFieldStyle = modelFormField.getSortFieldStyleAsc();
			}
		}

		//  strip sortField param from the query string
		HashSet<String> paramName = new HashSet<String>();
		paramName.add("sortField");
		String queryString = UtilHttp.stripNamedParamsFromQueryString(str, paramName);
		String urlPath = UtilHttp.removeQueryStringFromTarget(targetService);
		StringBuilder prepLinkBuffer = new StringBuilder();
		String prepLinkQueryString = UtilHttp.getQueryStringFromTarget(targetService);
		if (prepLinkQueryString != null) {
			prepLinkBuffer.append(prepLinkQueryString);
		}
		if (prepLinkBuffer.indexOf("?") < 0) {
			prepLinkBuffer.append("?");
		} else if (prepLinkBuffer.indexOf("?", prepLinkBuffer.length() - 1) > 0) {
			prepLinkBuffer.append("&amp;");
		}
		if (!UtilValidate.isEmpty(queryString) && !queryString.equals("null")) {
			prepLinkBuffer.append(queryString).append("&amp;");
		}
		prepLinkBuffer.append("sortField").append("=").append(newSortField);
		String prepLinkText = prepLinkBuffer.toString();
		if (ajaxEnabled) {
			prepLinkText = prepLinkText.replace("?", "");
			prepLinkText = prepLinkText.replace("&amp;", "&");
		}

		writer.append("<a");
		if (UtilValidate.isNotEmpty(sortFieldStyle)) {
			writer.append(" class=\"");
			writer.append(sortFieldStyle);
			writer.append("\"");
		}

		writer.append(" href=\"");
		if (ajaxEnabled) {
			writer.append("javascript:ajaxUpdateAreas('").append(createAjaxParamsFromUpdateAreas(updateAreas, prepLinkText, context)).append("')");
		} else {
			appendOfbizUrl(writer, urlPath + prepLinkText);
		}
		writer.append("\">").append(titleText).append("</a>");
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderFileField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.FileField)
	 */
	public void renderFileField(Appendable writer, Map<String, Object> context, FileField textField) throws IOException {
		ModelFormField modelFormField = textField.getModelFormField();

		writer.append("<input type=\"file\"");

		appendClassNames(writer, context, modelFormField);

		writer.append(" name=\"");
		writer.append(modelFormField.getParameterName(context));
		writer.append('"');

		String value = modelFormField.getEntry(context, textField.getDefaultValue(context));
		if (UtilValidate.isNotEmpty(value)) {
			writer.append(" value=\"");
			writer.append(value);
			writer.append('"');
		}

		writer.append(" size=\"");
		writer.append(Integer.toString(textField.getSize()));
		writer.append('"');

		Integer maxlength = textField.getMaxlength();
		if (maxlength != null) {
			writer.append(" maxlength=\"");
			writer.append(maxlength.toString());
			writer.append('"');
		}

		if (!textField.getClientAutocompleteField()) {
			writer.append(" autocomplete=\"off\"");
		}

		writer.append("/>");

		this.makeHyperlinkString(writer, textField.getSubHyperlink(), context);

		this.appendTooltip(writer, context, modelFormField);

		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderPasswordField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.PasswordField)
	 */
	public void renderPasswordField(Appendable writer, Map<String, Object> context, PasswordField passwordField) throws IOException {
		ModelFormField modelFormField = passwordField.getModelFormField();

		writer.append("<input type=\"password\"");

		if (passwordField.isReadOnly(context))
			writer.append(" readonly=\"readonly\"");

		appendClassNames(writer, context, modelFormField);

		writer.append(" name=\"");
		writer.append(modelFormField.getParameterName(context));
		writer.append('"');

		String value = modelFormField.getEntry(context, passwordField.getDefaultValue(context));
		if (UtilValidate.isNotEmpty(value)) {
			writer.append(" value=\"");
			writer.append(value);
			writer.append('"');
		}

		writer.append(" size=\"");
		writer.append(Integer.toString(passwordField.getSize()));
		writer.append('"');

		Integer maxlength = passwordField.getMaxlength();
		if (maxlength != null) {
			writer.append(" maxlength=\"");
			writer.append(maxlength.toString());
			writer.append('"');
		}

		String idName = modelFormField.getCurrentContainerId(context);
		if (UtilValidate.isNotEmpty(idName)) {
			writer.append(" id=\"");
			writer.append(idName);
			writer.append('"');
		}

		if (!passwordField.getClientAutocompleteField()) {
			writer.append(" autocomplete=\"off\"");
		}

		writer.append("/>");

		//this.addAsterisks(writer, context, modelFormField);

		this.makeHyperlinkString(writer, passwordField.getSubHyperlink(), context);

		this.appendTooltip(writer, context, modelFormField);

		//appendWhitespace(writer);
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.widget.form.FormStringRenderer#renderImageField(java.io.Writer, java.util.Map, org.ofbiz.widget.form.ModelFormField.ImageField)
	 */
	public void renderImageField(Appendable writer, Map<String, Object> context, ImageField imageField) throws IOException {
		ModelFormField modelFormField = imageField.getModelFormField();

		SubHyperlink subHyperLink = imageField.getSubHyperlink();
		StringBuilder image = new StringBuilder();
		
		String value = modelFormField.getEntry(context, imageField.getValue(context));
		String type = imageField.getType();
		String fileName = "";
		if (UtilValidate.isNotEmpty(type)) {
		    if("icon".equals(type)) {
		        image.append("<i ");
                if (UtilValidate.isNotEmpty(value)) {
                    image.append(" src=\"");
                     // ASMA: aggiungo per visualizzare immagine
                    //appendContentUrl(writer, value);
                    image.append(value);
                    image.append('"');
                }
            }else if("content-url".equals(type)) {
                image.append("<img ");
                if (UtilValidate.isNotEmpty(value)) {
					image.append(" src=\"");
					 // ASMA: aggiungo per visualizzare immagine
					//appendContentUrl(writer, value);
					image.append(value);
					image.append('"');
				}
			}else {
			    image.append("<img ");
			    if (UtilValidate.isEmpty(value)) {
					String errMsg = "contentId is empty.";
					Debug.logError(errMsg, module);
					return;
				} else {
					GenericDelegator delegator = (GenericDelegator) context.get("delegator");
					GenericValue content = null;
					try {
						Locale locale = UtilMisc.ensureLocale(context.get("locale"));

						List<GenericValue> contentList = delegator.findByAnd("ContentAssocViewTo", UtilMisc.toMap("contentIdStart", value, "caContentAssocTypeId", "ALTERNATE_LOCALE", "localeString", locale.toString()));
						if (UtilValidate.isEmpty(contentList)) {
							content = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", value));
						} else {
							contentList = EntityUtil.filterByDate(contentList, UtilDateTime.nowTimestamp(), "caFromDate", "caThruDate", true);
							contentList = EntityUtil.orderBy(contentList, UtilMisc.toList("-caFromDate"));
							content = EntityUtil.getFirst(contentList);
						}

						//content = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", value));
					} catch (GenericEntityException e) {
						Debug.logError(e, module);
						request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
						return;
					}
					if (UtilValidate.isNotEmpty(content)) {
						value = content.getString("dataResourceId");
					} else {
						String errMsg = "Could not find content with contentId [" + value + "] ";
						Debug.logError(errMsg, module);
						//                        throw new RuntimeException(errMsg);
					}
					// get the data resource
					GenericValue dataResource;
					try {
						dataResource = delegator.findByPrimaryKey("DataResource", UtilMisc.toMap("dataResourceId", value));
					} catch (GenericEntityException e) {
						Debug.logError(e, module);
						request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
						return;
					}
					HttpSession session = request.getSession();
					String contextRoot = (String) request.getAttribute("_CONTEXT_ROOT_");
					String webSiteId = (String) session.getAttribute("webSiteId");
					Locale locale = UtilHttp.getLocale(request);

					String https = "false";
					String protocol = request.getProtocol();
					if ("https".equalsIgnoreCase(protocol)) {
						https = "true";
					}

					// get the data resource stream and conent length
					ByteBuffer resourceDataByteBuffer;
					try {
						if (WidgetDataResourceWorker.dataresourceWorker != null) {
							resourceDataByteBuffer = WidgetDataResourceWorker.dataresourceWorker.getContentDataAsByteBuffer(delegator, value, https, webSiteId, locale, contextRoot);
							if(UtilValidate.isNotEmpty(resourceDataByteBuffer)){
								// obtain a reference to the file

								File file = null;

								//Recupero il nome del file
								String dataResourceName = (String) dataResource.get("dataResourceName");

								//Sulla base del tipo so se ho gia un url da poter costruire o se
								//dovro' creare un file copia in una posizione pubblica (relativamente al content->images se
								//di tipo OFBIZ_FILE, altrimenti sotto il modulo indicato in un'apposito parametro
								String dataResourceTypeId = (String) dataResource.get("dataResourceTypeId");

								StringBuilder urlImage = new StringBuilder();
								StringBuilder imageLocation = new StringBuilder();
								if (UtilValidate.isEmpty(dataResourceTypeId) || dataResourceTypeId.equals("OFBIZ_FILE_BIN") || dataResourceTypeId.equals("LOCAL_FILE") || dataResourceTypeId.equals("LOCAL_FILE_BIN")) {
									ContentUrlTag.appendContentPrefix(request, imageLocation);
									if (!imageLocation.toString().endsWith("/")) {
										imageLocation.append("/");
									}
									if (!"OFBIZ_FILE_BIN".equals(dataResourceTypeId))
										imageLocation.append("framework/images/webapp/images/tmp");
									else {
										imageLocation.append(dataResource.get("objectInfo"));
									}

									urlImage.append(imageLocation.substring(imageLocation.indexOf("webapp") + "webapp".length()));

									imageLocation = new StringBuilder(System.getProperty("ofbiz.home") + "/" + imageLocation.toString());
									File directory = new File(imageLocation.toString());
									if (!directory.exists()) {
										directory.mkdirs();
									}

									if (!"OFBIZ_FILE_BIN".equals(dataResourceTypeId)) {
										if (!dataResourceName.startsWith("/")) {
											imageLocation.append("/");
											urlImage.append("/");
										}
										imageLocation.append(dataResourceName);
										urlImage.append(dataResourceName);
									}
									
									file = new File(imageLocation.toString());

									try {
										RandomAccessFile out = new RandomAccessFile(file, "rw");
										out.write(resourceDataByteBuffer.array());
										out.close();
									} catch (FileNotFoundException e) {
										Debug.logError(e, module);
										return;
									} catch (IOException e) {
										Debug.logError(e, module);
										return;
									}

									fileName = urlImage.toString();
								} else  {
									String objectInfo = dataResource.getString("objectInfo");
									if (UtilValidate.isNotEmpty(objectInfo)) {
										if (objectInfo.contains("webapp")) {
											fileName = objectInfo.substring(objectInfo.indexOf("webapp") + "webapp".length());
										}
									}
								}

							} else {
								return;
							}
						}
					} catch (IOException e) {
						Debug.logError(e, "Error getting DataResource stream", module);
						request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
						// bug 4416
						// se l'immagine non viene recuperato, viene visulaizzata un'immagine di default
						String objectInfo = "framework/images/webapp/images/defaultIcon.png";
						fileName = objectInfo.substring(objectInfo.indexOf("webapp") + "webapp".length());
						//return;
					} catch (GeneralException e) {
						Debug.logError(e, "Error getting DataResource stream", module);
						request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
						return;
					}
					if (UtilValidate.isNotEmpty(fileName)) {
						image.append(" src=\"");
						image.append(fileName);
						image.append('"');
					}
				}
			}
		}

		image.append(" border=\"");
		image.append("").append(imageField.getBorder());
		image.append('"');

		Integer width = imageField.getWidth();
		if (width != null) {
			image.append(" width=\"");
			image.append(width.toString());
			image.append('"');
		}

		Integer height = imageField.getHeight();
		if (height != null) {
			image.append(" height=\"");
			image.append(height.toString());
			image.append('"');
		}

		String event = modelFormField.getEvent();
		String action = modelFormField.getAction(context);
		if (UtilValidate.isNotEmpty(event) && UtilValidate.isNotEmpty(action)) {
			image.append(" ");
			image.append(event);
			image.append("=\"");
			image.append(action);
			image.append('"');
		}

		if (UtilValidate.isNotEmpty(modelFormField.getWidgetStyle())) {
			if(UtilValidate.isEmpty(subHyperLink) || UtilValidate.isNotEmpty(subHyperLink.getDescription(context))) {
				image.append(" class=\"").append(modelFormField.getWidgetStyle()).append("\"");
			}
		}

		String tooltipValue = imageField.getTooltip(context);
		if (UtilValidate.isNotEmpty(tooltipValue)) {
			image.append(" title=\"");
			image.append(tooltipValue);
			image.append('"');
		}
		
		image.append("/>");

		if(UtilValidate.isEmpty(subHyperLink) || UtilValidate.isNotEmpty(subHyperLink.getDescription(context))) {
			writer.append(image);
			this.makeHyperlinkString(writer, subHyperLink, context);
		}
		else {// render Image in anchor element
			if(UtilValidate.isNotEmpty(subHyperLink)) {
				subHyperLink.setDescription(image.toString());
				subHyperLink.setLinkStyle(modelFormField.getWidgetStyle());
				this.makeHyperlinkString(writer, subHyperLink, context, false);
				subHyperLink.setDescription("");
			}
		}
		this.appendTooltip(writer, context, modelFormField);

		//appendWhitespace(writer);
	}

	public void renderFieldGroupOpen(Appendable writer, Map<String, Object> context, ModelForm.FieldGroup fieldGroup) throws IOException {
		ModelForm modelForm = fieldGroup.getModelForm();

		String style = fieldGroup.getStyle();
		String id = fieldGroup.getId();
		FlexibleStringExpander titleNotExpanded = FlexibleStringExpander.getInstance(fieldGroup.getTitle());
		String title = titleNotExpanded.expandString(context);

		if ("single".equals(modelForm.getType()) || "upload".equals(modelForm.getType())) {
			Boolean collapsed = fieldGroup.initiallyCollapsed();
			String collapsibleAreaId = fieldGroup.getId() + "_body";

			if (UtilValidate.isNotEmpty(style) || UtilValidate.isNotEmpty(id) || UtilValidate.isNotEmpty(title)) {

				writer.append("<div class=\"fieldgroup");
				if (UtilValidate.isNotEmpty(style)) {
					writer.append(" ");
					writer.append(style);
				}
				writer.append("\"");
				if (UtilValidate.isNotEmpty(id)) {
					writer.append(" id=\"");
					writer.append(id);
					writer.append("\"");
				}
				writer.append(">");

				writer.append("<div class=\"fieldgroup-title-bar\"><table><tr><td class=\"collapse\">");

				if (fieldGroup.collapsible()) {
					String expandToolTip = null;
					String collapseToolTip = null;
					Map<String, Object> uiLabelMap = UtilGenerics.checkMap(context.get("uiLabelMap"));
					if (uiLabelMap != null) {
						expandToolTip = (String) uiLabelMap.get("CommonExpand");
						collapseToolTip = (String) uiLabelMap.get("CommonCollapse");
					}

					writer.append("<ul><li class=\"");
					if (collapsed) {
						writer.append("collapsed\"><a ");
						writer.append("onclick=\"javascript:toggleCollapsiblePanel(this, '").append(collapsibleAreaId).append("', '").append(expandToolTip).append("', '").append(collapseToolTip).append("');\"");
					} else {
						writer.append("expanded\"><a ");
						writer.append("onclick=\"javascript:toggleCollapsiblePanel(this, '").append(collapsibleAreaId).append("', '").append(expandToolTip).append("', '").append(collapseToolTip).append("');\"");
					}
					writer.append(">&nbsp&nbsp&nbsp</a></li></ul>");

					appendWhitespace(writer);
				}
				writer.append("</td><td>");

				if (UtilValidate.isNotEmpty(title)) {
					writer.append("<div class=\"title\">");
					writer.append(title);
					writer.append("</div>");
				}

				writer.append("</td></tr></table></div>");

				writer.append("<div id=\"").append(collapsibleAreaId).append("\" class=\"fieldgroup-body\"");
				if (fieldGroup.collapsible() && collapsed) {
					writer.append(" style=\"display: none;\"");
				}
				writer.append(">");
			}
		} else {
			int colspan = fieldGroup.getFieldNumber();
			if(colspan == 0) {
				colspan = 1;
				style = style.concat(" hidden");
			}
			if ((UtilValidate.isNotEmpty(style) || UtilValidate.isNotEmpty(id) || UtilValidate.isNotEmpty(title)) && colspan > 0) {
				writer.append("<th scope=\"col\" class=\"fieldgroup");
				if (UtilValidate.isNotEmpty(style)) {
					writer.append(" ");
					writer.append(style);
				}
				writer.append("\"");
				if (UtilValidate.isNotEmpty(id)) {
					writer.append(" id=\"");
					writer.append(id);
					writer.append("\"");
				}
				writer.append(" colspan=\"");
				writer.append(Integer.toString(colspan));
				writer.append("\"");
				writer.append(">");
				if (UtilValidate.isNotEmpty(title)) {
					//writer.append("<div class=\"title\">");
					writer.append(title);
					//writer.append("</div>");
				}
			}
		}
	}

	public void renderFieldGroupClose(Appendable writer, Map<String, Object> context, ModelForm.FieldGroup fieldGroup) throws IOException {
		String style = fieldGroup.getStyle();
		String id = fieldGroup.getId();
		String title = fieldGroup.getTitle();
		if ("single".equals(fieldGroup.getModelForm().getType()) || "upload".equals(fieldGroup.getModelForm().getType())) {
			if (UtilValidate.isNotEmpty(style) || UtilValidate.isNotEmpty(id) || UtilValidate.isNotEmpty(title)) {
				writer.append("</div>");
				writer.append("</div>");
			}
		} else {
			writer.append("</th>");
		}
	}

	// TODO: Remove embedded styling
	public void renderBanner(Appendable writer, Map<String, Object> context, ModelForm.Banner banner) throws IOException {
		writer.append(" <table width=\"100%\">  <tr>");
		String style = banner.getStyle(context);
		String leftStyle = banner.getLeftTextStyle(context);
		if (UtilValidate.isEmpty(leftStyle)) leftStyle = style;
		String rightStyle = banner.getRightTextStyle(context);
		if (UtilValidate.isEmpty(rightStyle)) rightStyle = style;

		String leftText = banner.getLeftText(context);
		if (UtilValidate.isNotEmpty(leftText)) {
			writer.append("   <td align=\"left\">");
			if (UtilValidate.isNotEmpty(leftStyle)) {
				writer.append("<div");
				writer.append(" class=\"");
				writer.append(leftStyle);
				writer.append("\"");
				writer.append(">");
			}
			writer.append(leftText);
			if (UtilValidate.isNotEmpty(leftStyle)) {
				writer.append("</div>");
			}
			writer.append("</td>");
		}

		String text = banner.getText(context);
		if (UtilValidate.isNotEmpty(text)) {
			writer.append("   <td align=\"center\">");
			if (UtilValidate.isNotEmpty(style)) {
				writer.append("<div");
				writer.append(" class=\"");
				writer.append(style);
				writer.append("\"");
				writer.append(">");
			}
			writer.append(text);
			if (UtilValidate.isNotEmpty(style)) {
				writer.append("</div>");
			}
			writer.append("</td>");
		}

		String rightText = banner.getRightText(context);
		if (UtilValidate.isNotEmpty(rightText)) {
			writer.append("   <td align=\"right\">");
			if (UtilValidate.isNotEmpty(rightStyle)) {
				writer.append("<div");
				writer.append(" class=\"");
				writer.append(rightStyle);
				writer.append("\"");
				writer.append(">");
			}
			writer.append(rightText);
			if (UtilValidate.isNotEmpty(rightStyle)) {
				writer.append("</div>");
			}
			writer.append("</td>");
		}
		writer.append("</tr> </table>");
	}

	/**
	 * Renders a link for the column header fields when there is a header-link="" specified in the <field > tag, using
	 * style from header-link-style="".  Also renders a selectAll checkbox in multi forms.
	 * @param writer
	 * @param context
	 * @param modelFormField
	 * @param titleText
	 */
	public void renderHyperlinkTitle(Appendable writer, Map<String, Object> context, ModelFormField modelFormField, String titleText) throws IOException {
		if (UtilValidate.isNotEmpty(modelFormField.getHeaderLink())) {
		    StringBuffer targetBuffer = new StringBuffer();
			FlexibleStringExpander target = FlexibleStringExpander.getInstance(modelFormField.getHeaderLink());
			String fullTarget = target.expandString(context);
			targetBuffer.append(fullTarget);
			String targetType = HyperlinkField.DEFAULT_TARGET_TYPE;
			if (UtilValidate.isNotEmpty(targetBuffer.toString()) && targetBuffer.toString().toLowerCase().startsWith("javascript:")) {
				targetType="plain";
			}
			WidgetWorker.makeHyperlinkString(writer, modelFormField.getHeaderLinkStyle(), targetType, targetBuffer.toString(), null, titleText, null, modelFormField, this.request, this.response, null, null);
		} else if (modelFormField.isSortField()) {
			renderSortField (writer, context, modelFormField, titleText);
		} else if (modelFormField.isRowSubmit()) {
			if (UtilValidate.isNotEmpty(titleText)) writer.append(titleText).append("<br/>");
			writer.append("<input type=\"checkbox\" name=\"selectAll\" value=\"Y\" onclick=\"javascript:toggleAll(this, '");
			writer.append(modelFormField.getModelForm().getName());
			writer.append("');\"/>");
		} else {
			writer.append(titleText);
		}
	}

	public void renderContainerFindField(Appendable writer, Map<String, Object> context, ContainerField containerField) throws IOException {
		writer.append("<div ");
		String id = containerField.getId();
		if (UtilValidate.isNotEmpty(id)) {
			writer.append("id=\"");
			writer.append(id);
			writer.append("\" ");
		}
		String className = containerField.getModelFormField().getWidgetStyle();
		if (UtilValidate.isNotEmpty(className)) {
			writer.append("class=\"");
			writer.append(className);
			writer.append("\" ");
		}
		writer.append("/>");
	}

	/** Create an ajaxXxxx JavaScript CSV string from a list of UpdateArea objects. See
	 * <code>selectall.js</code>.
	 * @param updateAreas
	 * @param extraParams Renderer-supplied additional target parameters
	 * @param context
	 * @return Parameter string or empty string if no UpdateArea objects were found
	 */
	public String createAjaxParamsFromUpdateAreas(List<ModelForm.UpdateArea> updateAreas, String extraParams, Map<String, ? extends Object> context) {
		if (updateAreas == null) {
			return "";
		}
		StringBuilder ajaxUrl = new StringBuilder();
		boolean firstLoop = true;
		for (ModelForm.UpdateArea updateArea : updateAreas) {
			if (!updateArea.getOnlySubmit()) {
				if (firstLoop) {
					firstLoop = false;
				} /*else {
                    ajaxUrl.append(",");
                }*/

				//E' possibile che l'area l'area id specifichi piu' id separati da |, come se fossero indicati
				//piu' update area
				String areaId = updateArea.getAreaId(context);

				if (UtilValidate.isNotEmpty(areaId)) {
					List<String> areaIdList = StringUtil.split(areaId, "0$;");

					String areaTarget = updateArea.getAreaTarget(context);
					String targetType = updateArea.getTargetType();
					String targetUrl = UtilHttp.removeQueryStringFromTarget(areaTarget);
					String queryString = UtilHttp.getQueryStringFromTarget(areaTarget);
					List<String> queryStringList = StringUtil.split(queryString, "0$;");

					for (int i = 0; i < areaIdList.size(); i++) {
						/*if (i > 0)
	                		ajaxUrl.append(",");*/
						if (ajaxUrl.length() > 0) {
							ajaxUrl.append(",");
						}
						String currentAreaId = areaIdList.get(i);
						String ajaxParams = "";
						if (UtilValidate.isNotEmpty(queryStringList) && queryStringList.size() > i) {
							String currentQueryString = queryStringList.get(i);
							if (!currentQueryString.startsWith("?"))
								currentQueryString = "?" + currentQueryString;
							ajaxParams = getAjaxParamsFromTarget(currentQueryString);
							if (UtilValidate.isNotEmpty(extraParams)) {
								Map<String, String> ajaxParamsMap = StringUtil.strToMap(ajaxParams, "&");
								if (UtilValidate.isNotEmpty(ajaxParamsMap)) {
									Map<String, String> extraParamsMap = StringUtil.strToMap(extraParams, "&");
									for(String key : ajaxParamsMap.keySet()) {
										if (extraParamsMap.containsKey(key)) {
											extraParamsMap.remove(key);
										}
									}
									extraParams = StringUtil.mapToStr(extraParamsMap, "&");
								}
								if (ajaxParams.length() > 0 && !extraParams.startsWith("&")) {
									ajaxParams += "&";
								}
								ajaxParams += extraParams;
							}
						}
						ajaxUrl.append(currentAreaId).append(",");
						if ("inter-app".equals(targetType)) {
							//	                		targetUrl = this.rh.makeLink(this.request, this.response, targetUrl);
							String externalLoginKey = (String) request.getAttribute("externalLoginKey");
							if (UtilValidate.isNotEmpty(externalLoginKey)) {
								if (targetUrl.indexOf("?") == -1) {
									targetUrl += "?";
								} else {
									targetUrl += "&amp;";
								}
								targetUrl += "externalLoginKey=" + externalLoginKey;
							}
							ajaxUrl.append(targetUrl);
						} else {
							//	                	else if ("intra-app".equals(targetType)) {
							//	                		targetUrl = this.rh.makeLink(this.request, this.response, targetUrl);
							//	                	}
							try {
								appendOfbizUrl(ajaxUrl, targetUrl);
							} catch (IOException e) {
								throw UtilMisc.initCause(new InternalError(e.getMessage()), e);
							}
						}
						ajaxUrl.append(",").append(ajaxParams);
					}
				}
			}
		}
		return ajaxUrl.toString();
	}

	public ModelForm.UpdateArea getOnlySubmitUpdateArea(List<ModelForm.UpdateArea> updateAreas) {
		if (updateAreas == null) {
			return null;
		}
		ModelForm.UpdateArea res = null;
		for (ModelForm.UpdateArea updateArea : updateAreas) {
			if (updateArea.getOnlySubmit()) {
				res = updateArea;
				break;
			}
		}
		return res;
	}

	public String getOnlySubmitTargetUpdateArea(List<ModelForm.UpdateArea> updateAreas, String extraParams, Map<String, ? extends Object> context) {
		if (updateAreas == null) {
			return "";
		}
		String res = "";
		ModelForm.UpdateArea updateArea = getOnlySubmitUpdateArea(updateAreas);
		if (UtilValidate.isNotEmpty(updateArea))
			res = updateArea.getAreaId(context);
		return res;
	}

	/** Open tag < thead> in table */
	public void renderFormatHeaderRowGroupOpen(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append("   <thead>");
	}

	/** Close tag < thead> in table */
	public void renderFormatHeaderRowGroupClose(Appendable writer, Map<String, Object> context, ModelForm modelForm) throws IOException {
		writer.append("   </thead>");
	}
}
