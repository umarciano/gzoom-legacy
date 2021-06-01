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
import java.math.BigDecimal;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.control.RequestHandler;
import org.ofbiz.webapp.taglib.ContentUrlTag;
import org.ofbiz.widget.ModelWidget;
import org.ofbiz.widget.WidgetContentWorker;
import org.ofbiz.widget.WidgetDataResourceWorker;
import org.ofbiz.widget.WidgetWorker;
import org.ofbiz.widget.form.FormStringRenderer;
import org.ofbiz.widget.form.ModelForm;
import org.ofbiz.widget.menu.MenuStringRenderer;
import org.ofbiz.widget.menu.ModelMenu;
import org.ofbiz.widget.screen.ModelScreenWidget;
import org.ofbiz.widget.screen.ScreenStringRenderer;
import org.ofbiz.widget.screen.ModelScreenWidget.Form;

/**
 * Widget Library - HTML Form Renderer implementation
 */
public class HtmlScreenRenderer extends HtmlWidgetRenderer implements ScreenStringRenderer {

    public static final String module = HtmlScreenRenderer.class.getName();
    protected int elementId = 999;

    public HtmlScreenRenderer() {}

    protected String getNextElementId() {
        elementId++;
        return "hsr" + elementId;
    }
    
    public String getRendererName() {
    	return "html";
    }

    public void renderScreenBegin(Appendable writer, Map<String, Object> context) throws IOException {
    	writer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
    	appendWhitespace(writer);
    }

    public void renderScreenEnd(Appendable writer, Map<String, Object> context) throws IOException {
    }

    public void renderSectionBegin(Appendable writer, Map<String, Object> context, ModelScreenWidget.Section section) throws IOException {
    	if (section.isMainSection) {
    		this.widgetCommentsEnabled = ModelWidget.widgetBoundaryCommentsEnabled(context);
    	}
    	renderBeginningBoundaryComment(writer, section.isMainSection?"Screen":"Section Widget", section);
    }
    public void renderSectionEnd(Appendable writer, Map<String, Object> context, ModelScreenWidget.Section section) throws IOException {
        renderEndingBoundaryComment(writer, section.isMainSection?"Screen":"Section Widget", section);
    }

    public void renderContainerBegin(Appendable writer, Map<String, Object> context, ModelScreenWidget.Container container) throws IOException {
        String containerId = container.getId(context);
        String autoUpdateTarget = container.getAutoUpdateTargetExdr(context);
        HttpServletRequest request = (HttpServletRequest) context.get("request");
        if (UtilValidate.isNotEmpty(autoUpdateTarget) && UtilHttp.isJavaScriptEnabled(request)) {
            if (UtilValidate.isEmpty(containerId)) {
                containerId = getNextElementId();
            }
            HttpServletResponse response = (HttpServletResponse) context.get("response");
            ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
            RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");

            writer.append("<script type=\"text/javascript\">ajaxUpdateAreaPeriodic('");
            writer.append(containerId);
            writer.append("', '");
            writer.append(rh.makeLink(request, response, autoUpdateTarget));
            writer.append("', '");
            writer.append("', '").append(container.getAutoUpdateInterval()).append("');</script>");
            appendWhitespace(writer);
        }
        writer.append("<div");

        if (UtilValidate.isNotEmpty(containerId)) {
            writer.append(" id=\"");
            writer.append(containerId);
            writer.append("\"");
        }

        String style = container.getStyle(context);
        if (UtilValidate.isNotEmpty(style)) {
            writer.append(" class=\"");
            writer.append(style);
            writer.append("\"");
        }

        writer.append(">");
        appendWhitespace(writer);
    }
    public void renderContainerEnd(Appendable writer, Map<String, Object> context, ModelScreenWidget.Container container) throws IOException {
        writer.append("</div>");
        appendWhitespace(writer);
    }

    public void renderHorizontalSeparator(Appendable writer, Map<String, Object> context, ModelScreenWidget.HorizontalSeparator separator) throws IOException {
        writer.append("<hr");
        String className = separator.getStyle(context);
        if (UtilValidate.isNotEmpty(className)) {
        	writer.append(" class=\"").append(className).append("\"");
        }
        String idName = separator.getId(context);
        if (UtilValidate.isNotEmpty(idName)) {
        	writer.append(" id=\"").append(idName).append("\"");
        }
        writer.append("/>");
        appendWhitespace(writer);
    }

    public void renderScreenletBegin(Appendable writer, Map<String, Object> context, boolean collapsed, ModelScreenWidget.Screenlet screenlet) throws IOException {
        Debug.log("******************** renderScreenletBegin ************************* ");
        HttpServletRequest request = (HttpServletRequest) context.get("request");
        HttpServletResponse response = (HttpServletResponse) context.get("response");
        boolean javaScriptEnabled = UtilHttp.isJavaScriptEnabled(request);

        ModelScreenWidget.Menu tabMenu = screenlet.getTabMenu(context);
        if (tabMenu != null) {
            tabMenu.renderWidgetString(writer, context, this);
        }
        writer.append("<div class=\"screenlet\"");
        String id = screenlet.getId(context);
        if (UtilValidate.isNotEmpty(id)) {
            writer.append(" id=\"");
            writer.append(id);
            writer.append("\"");
        }
        writer.append(">");
        appendWhitespace(writer);

        String title = screenlet.getTitle(context);
        ModelScreenWidget.Menu navMenu = screenlet.getNavigationMenu(context);
        ModelScreenWidget.Form navForm = screenlet.getNavigationForm(context);
        String collapsibleAreaId = null;
        if (title != null || navMenu != null || navForm != null || screenlet.collapsible()) {
            writer.append("<div class=\"screenlet-title-bar\">");
            appendWhitespace(writer);
            writer.append("<ul>");
            appendWhitespace(writer);
            if (UtilValidate.isNotEmpty(title)) {
                writer.append("<li class=\"h3\">");
                writer.append(title);
                writer.append("</li>");
                appendWhitespace(writer);
            }
            if (screenlet.collapsible()) {
                collapsibleAreaId = this.getNextElementId();
                String expandToolTip = null;
                String collapseToolTip = null;
                Map<String, Object> uiLabelMap = UtilGenerics.checkMap(context.get("uiLabelMap"));
                Map<String, Object> paramMap = UtilGenerics.checkMap(context.get("requestParameters"));
                Map<String, Object> requestParameters = new HashMap<String, Object>(paramMap);
                if (uiLabelMap != null) {
                    expandToolTip = (String) uiLabelMap.get("CommonExpand");
                    collapseToolTip = (String) uiLabelMap.get("CommonCollapse");
                }
                writer.append("<li class=\"");
                if (collapsed) {
                    writer.append("collapsed\"><a style='display: block ! important; height: 15px ! important; width: 15px ! important;'");
                    if (javaScriptEnabled) {
                    	writer.append("onclick=\"javascript:toggleScreenlet(this, '").append(collapsibleAreaId).append("', null, '").append(expandToolTip).append("', '").append(collapseToolTip).append("');\"");
                    } else {
                        requestParameters.put(screenlet.getPreferenceKey(context) + "_collapsed", "false");
                        String queryString = UtilHttp.urlEncodeArgs(requestParameters);
                        writer.append("href=\"").append(request.getRequestURI()).append("?").append(queryString).append("\"");
                    }
                    if (UtilValidate.isNotEmpty(expandToolTip)) {
                        writer.append(" title=\"").append(expandToolTip).append("\"");
                    }
                } else {
                    writer.append("expanded\"><a style='display: block ! important; height: 15px ! important; width: 15px ! important;'");
                    if (javaScriptEnabled) {
                        writer.append("onclick=\"javascript:toggleScreenlet(this, '").append(collapsibleAreaId).append("', null, '").append(expandToolTip).append("', '").append(collapseToolTip).append("');\"");
                    } else {
                        requestParameters.put(screenlet.getPreferenceKey(context) + "_collapsed", "true");
                        String queryString = UtilHttp.urlEncodeArgs(requestParameters);
                        writer.append("href=\"").append(request.getRequestURI()).append("?").append(queryString).append("\"");
                    }
                    if (UtilValidate.isNotEmpty(collapseToolTip)) {
                    	writer.append(" title=\"").append(collapseToolTip).append("\"");
                    }
                }
                writer.append(">&nbsp</a></li>");
                appendWhitespace(writer);
            }
            if (!collapsed) {
                if (navMenu != null) {
                    MenuStringRenderer savedRenderer = (MenuStringRenderer) context.get("menuStringRenderer");
                    MenuStringRenderer renderer = new ScreenletMenuRenderer(request, response);
                    context.put("menuStringRenderer", renderer);
                    navMenu.renderWidgetString(writer, context, this);
                    context.put("menuStringRenderer", savedRenderer);

                    Map<String, Object> globalCtx = UtilGenerics.checkMap(context.get("globalContext"));
                    globalCtx.put(navMenu.getExpandedName(context) + "_just_rendered", new Boolean(true));
                }
                if (navForm != null) {
                    renderScreenletPaginateMenu(writer, context, navForm);
                }
            }
            writer.append("</ul>");
            appendWhitespace(writer);
            writer.append("<br class=\"clear\" />");
            appendWhitespace(writer);
            writer.append("</div>");
            appendWhitespace(writer);
            writer.append("<div");
            if (UtilValidate.isNotEmpty(collapsibleAreaId)) {
            	writer.append(" id=\"").append(collapsibleAreaId).append("\"");
                if (collapsed) {
                    writer.append(" style=\"display: none;\"");
                }
            }
            if (screenlet.padded()) {
                writer.append(" class=\"screenlet-body\"");
            }
            writer.append(">");
            appendWhitespace(writer);
        }
    }

    /**Maps spa - added ajax pagination
     * Create an ajaxXxxx JavaScript CSV string from a list of UpdateArea objects. See
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
        HttpServletResponse response = (HttpServletResponse) context.get("response");
        HttpServletRequest request = (HttpServletRequest) context.get("request");
        ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
        RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
        String ajaxUrl = "";

        boolean firstLoop = true;
        for (ModelForm.UpdateArea updateArea : updateAreas) {
            if (firstLoop) {
                firstLoop = false;
            } else {
                ajaxUrl += ",";
            }
            String targetUrl = updateArea.getAreaTarget(context);
            String ajaxParams = getAjaxParamsFromTarget(targetUrl);
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
            ajaxUrl += updateArea.getAreaId(context) + ",";
            ajaxUrl += rh.makeLink(request, response, UtilHttp.removeQueryStringFromTarget(targetUrl));
            ajaxUrl += "," + ajaxParams;
        }
        return ajaxUrl;
    }

    protected void renderScreenletPaginateMenu(Appendable writer, Map<String, Object> context, ModelScreenWidget.Form form) throws IOException {
        HttpServletResponse response = (HttpServletResponse) context.get("response");
        HttpServletRequest request = (HttpServletRequest) context.get("request");
        ModelForm modelForm = form.getModelForm(context);
        modelForm.runFormActions(context);
        modelForm.preparePager(context);

        Map<String, Object> globalCtx = UtilGenerics.checkMap(context.get("globalContext"));
        globalCtx.put("NO_PAGINATOR_" + modelForm.getName(), true);

        String targetService = modelForm.getPaginateTarget(context);
        if (targetService == null) {
            targetService = "${targetService}";
        }

        //********************************
        //Maps spa - added ajax pagination
        //
        boolean ajaxEnabled = false;
        List<ModelForm.UpdateArea> updateAreas = modelForm.getOnPaginateUpdateAreas();
        if (UtilHttp.isJavaScriptEnabled(request)) {
            if (UtilValidate.isNotEmpty(updateAreas)) {
                ajaxEnabled = true;
            }
        }

        // get the parametrized pagination index and size fields
        int paginatorNumber = modelForm.getPaginatorNumber(context);
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
        int viewSize = modelForm.getViewSize(context);
        int listSize = modelForm.getListSize(context);

        int lowIndex = modelForm.getLowIndex(context);
        int highIndex = modelForm.getHighIndex(context);
        int actualPageSize = modelForm.getActualPageSize(context);

        // if this is all there seems to be (if listSize < 0, then size is unknown)
        if (actualPageSize >= listSize && listSize >= 0) return;

        // needed for the "Page" and "rows" labels
        Map<String, String> uiLabelMap = UtilGenerics.cast(context.get("uiLabelMap"));
        String commonDisplaying = "";
        if (uiLabelMap == null) {
            Debug.logWarning("Could not find uiLabelMap in context", module);
        } else {
            Map<String, Integer> messageMap = UtilMisc.toMap("lowCount", Integer.valueOf(lowIndex + 1), "highCount", Integer.valueOf(lowIndex + actualPageSize), "total", Integer.valueOf(listSize));
            commonDisplaying = UtilProperties.getMessage("CommonUiLabels", "CommonScreenletDisplaying", messageMap, (Locale) context.get("locale"));
        }

        // for legacy support, the viewSizeParam is VIEW_SIZE and viewIndexParam is VIEW_INDEX when the fields are "viewSize" and "viewIndex"
        if (viewIndexParam.equals("viewIndex" + "_" + paginatorNumber)) viewIndexParam = "VIEW_INDEX" + "_" + paginatorNumber;
        if (viewSizeParam.equals("viewSize" + "_" + paginatorNumber)) viewSizeParam = "VIEW_SIZE" + "_" + paginatorNumber;

        ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
        RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");

        Map<String,Object> inputFields = UtilGenerics.toMap(request.getAttribute("requestParameters"));
        if (UtilValidate.isEmpty(inputFields))
            inputFields = new FastMap<String, Object>();
        Map<String,Object> requestParametersInputFields = UtilGenerics.toMap(context.get("requestParameters"));
        if (UtilValidate.isNotEmpty(requestParametersInputFields))
            inputFields.putAll(requestParametersInputFields);
        // strip out any multi form fields if the form is of type multi
        if (modelForm.getType().equals("multi")) {
            inputFields = UtilHttp.removeMultiFormParameters(inputFields);
        }
        String queryString = UtilHttp.urlEncodeArgs(inputFields);
        // strip legacy viewIndex/viewSize params from the query string
        queryString = UtilHttp.stripViewParamsFromQueryString(queryString, "" + paginatorNumber);
        // strip parametrized index/size params from the query string
        HashSet<String> paramNames = new HashSet<String>();
        paramNames.add(viewIndexParam);
        paramNames.add(viewSizeParam);
        paramNames.add("saveView");
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
        if (UtilValidate.isNotEmpty(paginateAnchor)) anchor = "#" + paginateAnchor;

        // preparing the link text, so that later in the code we can reuse this and just add the viewIndex
        StringBuilder prepLinkTextBuffer = new StringBuilder(targetService);
        if (prepLinkTextBuffer.indexOf("?") < 0) {
        	prepLinkTextBuffer.append("?");
        } else if (prepLinkTextBuffer.indexOf("?", prepLinkTextBuffer.length() - 1) > 0) {
        	prepLinkTextBuffer.append("&amp;");
        }
        if (!UtilValidate.isEmpty(queryString) && !queryString.equals("null")) {
        	prepLinkTextBuffer.append(queryString).append("&amp;");
        }
        if (ajaxEnabled)
        	prepLinkTextBuffer.append("PAGINATOR_NUMBER=").append(paginatorNumber).append("&amp;");
        prepLinkTextBuffer.append(viewSizeParam).append("=").append(viewSize).append("&amp;").append(viewIndexParam).append("=");
        String prepLinkText = prepLinkTextBuffer.toString();
        
        //**********************
        //Maps spa - added ajax pagination
        if (ajaxEnabled) {
            //Sandro: grab from link initial target
            prepLinkText = prepLinkText.replaceFirst(targetService, "");
            // Prepare params for prototype.js
            prepLinkText = prepLinkText.replace("?", "");
            prepLinkText = prepLinkText.replace("&amp;", "&");
        }


        String linkText;

        appendWhitespace(writer);
        // The current screenlet title bar navigation syling requires rendering
        // these links in reverse order
        // Last button
        writer.append("<li class=\"").append(modelForm.getPaginateLastStyle());
        if (highIndex < listSize) {
            writer.append(" fa\"><a href=\"");
            int page = ((listSize-1) / viewSize);
            if (page == 0)
                page = 1;
            //Maps spa -added ajax pagination
            if (ajaxEnabled) {
                writer.append("#\" onclick=\"javascript:ajaxUpdateAreas('").append(createAjaxParamsFromUpdateAreas(updateAreas, prepLinkText + page + anchor, context)).append("'); return false;");
            } else {
                linkText = prepLinkText + page + anchor;
                // - make the link
                writer.append(rh.makeLink(request, response, linkText));
            }
            writer.append("\"></a>");
        } else {
            // disabled button
            writer.append("-disabled disabled fa\"><a href=\"#\"></a>");
        }
        writer.append("</li>");
        appendWhitespace(writer);
        // Next button
        writer.append("<li class=\"").append(modelForm.getPaginateNextStyle());
        if (highIndex < listSize) {
            writer.append(" fa\"><a href=\"");
            if (ajaxEnabled) {
                writer.append("#\" onclick=\"javascript:ajaxUpdateAreas('").append(createAjaxParamsFromUpdateAreas(updateAreas, prepLinkText + (viewIndex + 1) + anchor, context)).append("'); return false;");
            } else {
                linkText = prepLinkText + (viewIndex + 1) + anchor;
                // - make the link
                writer.append(rh.makeLink(request, response, linkText));
            }
            writer.append("\"></a>");
        } else {
            // disabled button
            writer.append("-disabled disabled fa\"><a href=\"#\"></a>");
        }
        writer.append("</li>");
        appendWhitespace(writer);
        if (listSize > 0) {
            writer.append("<li class=\"pager\"><div><span>");
            writer.append(commonDisplaying);
            writer.append("</span></div></li>");
            appendWhitespace(writer);
        }
        // Previous button
        writer.append("<li class=\"nav-previous");
        if (viewIndex > 0) {
            writer.append(" fa\"><a href=\"");
            if (ajaxEnabled) {
                writer.append("#\" onclick=\"javascript:ajaxUpdateAreas('").append(createAjaxParamsFromUpdateAreas(updateAreas, prepLinkText + (viewIndex - 1) + anchor, context)).append("'); return false;");
            } else {
                linkText = prepLinkText + (viewIndex - 1) + anchor;
                // - make the link
                writer.append(rh.makeLink(request, response, linkText));
            }
            writer.append("\"></a>");
        } else {
            // disabled button
            writer.append("-disabled disabled fa\"><a href=\"#\"></a>");
        }
        writer.append("</li>");
        appendWhitespace(writer);
        // First button
        writer.append("<li class=\"nav-first");
        if (viewIndex > 0) {
            writer.append(" fa\"><a href=\"");
            if (ajaxEnabled) {
                writer.append("#\" onclick=\"javascript:ajaxUpdateAreas('").append(createAjaxParamsFromUpdateAreas(updateAreas, prepLinkText + 0 + anchor, context)).append("'); return false;");
            } else {
                linkText = prepLinkText + 0 + anchor;
                writer.append(rh.makeLink(request, response, linkText));
            }
            writer.append("\"></a>");
        } else {
            writer.append("-disabled disabled fa\"><a href=\"#\"></a>");
        }
        writer.append("</li>");
        appendWhitespace(writer);

        if (UtilValidate.isNotEmpty(context.get("REMOVE_PAGINATOR_NUMBER"))) {
            if (((Boolean)context.get("REMOVE_PAGINATOR_NUMBER")).booleanValue()) {
                parameters.remove("PAGINATOR_NUMBER");
            }
        }
    }

    public void renderScreenletSubWidget(Appendable writer, Map<String, Object> context, ModelScreenWidget subWidget, ModelScreenWidget.Screenlet screenlet) throws GeneralException, IOException {
        Form f = screenlet.getNavigationForm(context);
        if (subWidget.equals(f)) {
            HttpServletRequest request = (HttpServletRequest) context.get("request");
            HttpServletResponse response = (HttpServletResponse) context.get("response");
            if (request != null && response != null) {
                Map<String, Object> globalCtx = UtilGenerics.checkMap(context.get("globalContext"));
                globalCtx.put("NO_PAGINATOR_" + f.getName(), true);
                FormStringRenderer savedRenderer = (FormStringRenderer) context.get("formStringRenderer");
                HtmlFormRenderer renderer = new HtmlFormRenderer(request, response);
                renderer.setRenderPagination(false);
                context.put("formStringRenderer", renderer);
                subWidget.renderWidgetString(writer, context, this);
                context.put("formStringRenderer", savedRenderer);
            }
        } else {
            subWidget.renderWidgetString(writer, context, this);
        }
    }

    public void renderScreenletEnd(Appendable writer, Map<String, Object> context, ModelScreenWidget.Screenlet screenlet) throws IOException {
        writer.append("</div>");
        appendWhitespace(writer);
        writer.append("</div>");
        appendWhitespace(writer);
    }

    public static class ScreenletMenuRenderer extends HtmlMenuRenderer {
        public ScreenletMenuRenderer(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }
        @Override
        public void renderMenuOpen(Appendable writer, Map<String, Object> context, ModelMenu modelMenu) {}
        @Override
        public void renderMenuClose(Appendable writer, Map<String, Object> context, ModelMenu modelMenu) {}
    }

    public void renderLabel(Appendable writer, Map<String, Object> context, ModelScreenWidget.Label label) throws IOException {
        String labelText = label.getText(context);
        if (UtilValidate.isEmpty(labelText)) {
            // nothing to render
            return;
        }
        // open tag
        String style = label.getStyle(context);
        String id = label.getId(context);
        if (UtilValidate.isNotEmpty(style) || UtilValidate.isNotEmpty(id) ) {
               writer.append("<span");

            if (UtilValidate.isNotEmpty(id)) {
                writer.append(" id=\"");
                writer.append(id);
                writer.append("\"");
            }
            if (UtilValidate.isNotEmpty(style)) {
                writer.append(" class=\"");
                writer.append(style);
                writer.append("\"");
            }
            writer.append(">");

            // the text
            writer.append(labelText);

            // close tag
               writer.append("</span>");

        } else {
            writer.append(labelText);
        }

        appendWhitespace(writer);
    }

    public void renderLink(Appendable writer, Map<String, Object> context, ModelScreenWidget.Link link) throws IOException {
    	// open tag
    	HttpServletResponse response = (HttpServletResponse) context.get("response");
    	HttpServletRequest request = (HttpServletRequest) context.get("request");

    	String targetWindow = link.getTargetWindow(context);
    	String target = link.getTarget(context);

    	String uniqueItemName = link.getModelScreen().getName() + "_LF_" + UtilMisc.<String>addToBigDecimalInMap(context, "screenUniqueItemIndex", BigDecimal.ONE);

    	String linkType = WidgetWorker.determineAutoLinkType(link.getLinkType(), target, link.getUrlMode(), request);
    	if ("hidden-form".equals(linkType)) {
    		writer.append("<form method=\"post\"");
    		writer.append(" action=\"");
    		// note that this passes null for the parameterList on purpose so they won't be put into the URL
    		WidgetWorker.buildHyperlinkUrl(writer, target, link.getUrlMode(), null, link.getPrefix(context),
    				link.getFullPath(), link.getSecure(), link.getEncode(), request, response, context);
    		writer.append("\"");

    		if (UtilValidate.isNotEmpty(targetWindow)) {
    			writer.append(" target=\"");
    			writer.append(targetWindow);
    			writer.append("\"");
    		}

    		writer.append(" onsubmit=\"javascript:submitFormDisableSubmits(this)\"");

    		writer.append(" name=\"");
    		writer.append(uniqueItemName);
    		writer.append("\">");

    		for (Map.Entry<String, String> parameter: link.getParameterMap(context).entrySet()) {
    			writer.append("<input name=\"");
    			writer.append(parameter.getKey());
    			writer.append("\" value=\"");
    			writer.append(parameter.getValue());
    			writer.append("\" type=\"hidden\"/>");
    		}

    		writer.append("</form>");
    	}
    	
        writer.append("<a");
        String id = link.getId(context);
        if (UtilValidate.isNotEmpty(id)) {
            writer.append(" id=\"");
            writer.append(id);
            writer.append("\"");
        }
        String style = link.getStyle(context);
        if (UtilValidate.isNotEmpty(style)) {
            writer.append(" class=\"");
            writer.append(style);
            writer.append("\"");
        }
        String name = link.getName(context);
        if (UtilValidate.isNotEmpty(name)) {
            writer.append(" name=\"");
            writer.append(name);
            writer.append("\"");
        }
        if (UtilValidate.isNotEmpty(targetWindow)) {
            writer.append(" target=\"");
            writer.append(targetWindow);
            writer.append("\"");
        }
        if (UtilValidate.isNotEmpty(target)) {
            writer.append(" href=\"");
            if ("hidden-form".equals(linkType)) {
            	writer.append("javascript:document.");
            	writer.append(uniqueItemName);
            	writer.append(".submit()");
            } else {
            	WidgetWorker.buildHyperlinkUrl(writer, target, link.getUrlMode(), link.getParameterMap(context), link.getPrefix(context),
            			link.getFullPath(), link.getSecure(), link.getEncode(), request, response, context);
            }

            writer.append("\"");
        }
        writer.append(">");

        // the text
        ModelScreenWidget.Image img = link.getImage();
        if (img == null) {
            writer.append(link.getText(context));
        } else {
            renderImage(writer, context, img);
        }

        // close tag
        writer.append("</a>");

        appendWhitespace(writer);
    }

    public void renderImage(Appendable writer, Map<String, Object> context, ModelScreenWidget.Image image) throws IOException {
        // open tag

        String urlMode = image.getUrlMode();
        String value = image.getContentId(context);
        String src = image.getSrc(context);
        if ((UtilValidate.isEmpty(src) && !"content".equalsIgnoreCase(urlMode)) || ("content".equalsIgnoreCase(urlMode) && UtilValidate.isEmpty(value))) {
            return;
        }
        writer.append("<img ");
        String id = image.getId(context);
        if (UtilValidate.isNotEmpty(id)) {
            writer.append(" id=\"");
            writer.append(id);
            writer.append("\"");
        }
        String style = image.getStyle(context);
        if (UtilValidate.isNotEmpty(style)) {
            writer.append(" class=\"");
            writer.append(style);
            writer.append("\"");
        }
        String wid = image.getWidth(context);
        if (UtilValidate.isNotEmpty(wid)) {
            writer.append(" width=\"");
            writer.append(wid);
            writer.append("\"");
        }
        String hgt = image.getHeight(context);
        if (UtilValidate.isNotEmpty(hgt)) {
            writer.append(" height=\"");
            writer.append(hgt);
            writer.append("\"");
        }
        String border = image.getBorder(context);
        if (UtilValidate.isNotEmpty(border)) {
            writer.append(" border=\"");
            writer.append(border);
            writer.append("\"");
        }
        String alt = image.getAlt(context);
        if (UtilValidate.isNotEmpty(alt)) {
            writer.append(" alt=\"");
            writer.append(alt);
            writer.append("\"");
        }

        writer.append(" src=\"");

        boolean fullPath = false;
        boolean secure = false;
        boolean encode = false;
        HttpServletResponse response = (HttpServletResponse) context.get("response");
        HttpServletRequest request = (HttpServletRequest) context.get("request");
        if (urlMode != null && urlMode.equalsIgnoreCase("intra-app")) {
            if (request != null && response != null) {
                ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
                RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
                String urlString = rh.makeLink(request, response, src, fullPath, secure, encode);
                writer.append(urlString);
            } else {
                writer.append(src);
            }
        } else  if (urlMode != null && urlMode.equalsIgnoreCase("content")) {
            if (request != null && response != null) {
                if (UtilValidate.isEmpty(src)) {
                    Delegator delegator = (Delegator) context.get("delegator");
                    GenericValue content = null;
                    try {
                        Locale locale = UtilMisc.ensureLocale(context.get("locale"));

                        List<GenericValue> contentList = delegator.findList("ContentAssocViewTo", EntityCondition.makeCondition(UtilMisc.toMap("contentIdStart", value, "caContentAssocTypeId", "ALTERNATE_LOCALE", "localeString", locale.toString())), null, null, null, true);
                        if (UtilValidate.isEmpty(contentList)) {
                            content = delegator.findOne("Content", UtilMisc.toMap("contentId", value), true);
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
                        dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", value), true);
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
                                if (UtilValidate.isEmpty(dataResourceTypeId) || dataResourceTypeId.equals("LOCAL_FILE") || dataResourceTypeId.equals("LOCAL_FILE_BIN")) {
                                    ContentUrlTag.appendContentPrefix(request, imageLocation);
                                    if (!imageLocation.toString().endsWith("/")) {
                                        imageLocation.append("/");
                                    }
                                    imageLocation.append("framework/images/webapp/images/tmp");

                                    urlImage.append(imageLocation.substring(imageLocation.indexOf("webapp") + "webapp".length()));

                                    imageLocation = new StringBuilder(System.getProperty("ofbiz.home") + "/" + imageLocation.toString());
                                    File directory = new File(imageLocation.toString());
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                    }

                                    if (!dataResourceName.startsWith("/")) {
                                        imageLocation.append("/");
                                        urlImage.append("/");
                                    }
                                    imageLocation.append(dataResourceName);
                                    urlImage.append(dataResourceName);

                                    file = new File(imageLocation.toString());

                                    boolean readOk = true;
                                    try {
                                        RandomAccessFile out = new RandomAccessFile(file, "rw");
                                        out.write(resourceDataByteBuffer.array());
                                        out.close();
                                    } catch (FileNotFoundException e) {
                                        Debug.logError(e, module);
                                        readOk = false;
//                                        return;
                                    } catch (IOException e) {
                                        Debug.logError(e, module);
                                        readOk = false;
//                                        return;
                                    }

                                    if (readOk)
                                        src = urlImage.toString();
                                } else  {
                                    String objectInfo = dataResource.getString("objectInfo");
                                    if (UtilValidate.isNotEmpty(objectInfo)) {
                                        if (objectInfo.contains("webapp")) {
                                            src = objectInfo.substring(objectInfo.indexOf("webapp") + "webapp".length());
                                        }
                                    }
                                }

                            }
                        }
                    } catch (IOException e) {
                        Debug.logError(e, "Error getting DataResource stream", module);
//                        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
//                        return;
                    } catch (GeneralException e) {
                        Debug.logError(e, "Error getting DataResource stream", module);
//                        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
//                        return;
                    }
                }

                if (UtilValidate.isNotEmpty(src)) {
                	StringBuilder newURL = new StringBuilder();
                    ContentUrlTag.appendContentPrefix(request, newURL);
                    newURL.append(src);
                    writer.append(newURL.toString());
                }
            }
        } else {
            writer.append(src);
        }

        writer.append("\"/>");

        appendWhitespace(writer);
    }

    public void renderContentBegin(Appendable writer, Map<String, Object> context, ModelScreenWidget.Content content) throws IOException {
        String editRequest = content.getEditRequest(context);
        String editContainerStyle = content.getEditContainerStyle(context);
        String enableEditName = content.getEnableEditName(context);
        String enableEditValue = (String)context.get(enableEditName);

        if (Debug.verboseOn()) Debug.logVerbose("directEditRequest:" + editRequest, module);

        if (UtilValidate.isNotEmpty(editRequest) && "true".equals(enableEditValue)) {
            writer.append("<div");
            writer.append(" class=\"").append(editContainerStyle).append("\"> ");
            appendWhitespace(writer);
        }
    }

    public void renderContentBody(Appendable writer, Map<String, Object> context, ModelScreenWidget.Content content) throws IOException {
        Locale locale = UtilMisc.ensureLocale(context.get("locale"));
        //Boolean nullThruDatesOnly = Boolean.valueOf(false);
        String mimeTypeId = "text/html";
        String expandedContentId = content.getContentId(context);
        String expandedDataResourceId = content.getDataResourceId(context);
        String renderedContent = null;
        LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
        Delegator delegator = (Delegator) context.get("delegator");

        //MAPS INIZIO
        // make a new map for content rendering; so our current map does not get clobbered
        MapStack<String> contentContext = null;
        if (!(context instanceof MapStack)) {
            contentContext = MapStack.create(context);
            contentContext.push();
        } else {
            contentContext = ((MapStack<String>)context).standAloneChildStack();
        }
        //In this manner, in the renderDataResourceAsTextExt it put new ScreenRenderer with the correct writer
        contentContext.put("screens", null);
        //MAPS FINE

        String dataResourceId = (String)contentContext.get("dataResourceId");
        if (Debug.verboseOn()) Debug.logVerbose("expandedContentId:" + expandedContentId, module);

        try {
            if (UtilValidate.isNotEmpty(dataResourceId)) {
                if (WidgetDataResourceWorker.dataresourceWorker != null) {
                    renderedContent = WidgetDataResourceWorker.dataresourceWorker.renderDataResourceAsTextExt(delegator, dataResourceId, contentContext, locale, mimeTypeId, false);
                } else {
                	Debug.logError("Not rendering content, WidgetDataResourceWorker.dataresourceWorker not found.", module);
                }
            } else if (UtilValidate.isNotEmpty(expandedContentId)) {
                if (WidgetContentWorker.contentWorker != null) {
                    renderedContent = WidgetContentWorker.contentWorker.renderContentAsTextExt(dispatcher, delegator, expandedContentId, contentContext, locale, mimeTypeId, true);
                } else {
                	Debug.logError("Not rendering content, WidgetContentWorker.contentWorker not found.", module);
                }
            } else if (UtilValidate.isNotEmpty(expandedDataResourceId)) {
                if (WidgetDataResourceWorker.dataresourceWorker != null) {
                    renderedContent = WidgetDataResourceWorker.dataresourceWorker.renderDataResourceAsTextExt(delegator, expandedDataResourceId, contentContext, locale, mimeTypeId, false);
                } else {
                	Debug.logError("Not rendering content, WidgetDataResourceWorker.dataresourceWorker not found.", module);
                }
            }
            if (UtilValidate.isEmpty(renderedContent)) {
                String editRequest = content.getEditRequest(context);
                if (UtilValidate.isNotEmpty(editRequest)) {
                    if (WidgetContentWorker.contentWorker != null) {
                        WidgetContentWorker.contentWorker.renderContentAsTextExt(dispatcher, delegator, "NOCONTENTFOUND", writer, contentContext, locale, mimeTypeId, true);
                    } else {
                    	Debug.logError("Not rendering content, WidgetContentWorker.contentWorker not found.", module);
                    }
                }
            } else {
                if (content.xmlEscape()) {
                    renderedContent = UtilFormatOut.encodeXmlValue(renderedContent);
                }

                writer.append(renderedContent);
            }

        } catch(GeneralException e) {
            String errMsg = "Error rendering included content with id [" + expandedContentId + "] : " + e.toString();
            Debug.logError(e, errMsg, module);
            //throw new RuntimeException(errMsg);
        } catch(IOException e2) {
            String errMsg = "Error rendering included content with id [" + expandedContentId + "] : " + e2.toString();
            Debug.logError(e2, errMsg, module);
            //throw new RuntimeException(errMsg);
        }
    }

    public void renderContentEnd(Appendable writer, Map<String, Object> context, ModelScreenWidget.Content content) throws IOException {

                //Debug.logInfo("renderContentEnd, context:" + context, module);
        String expandedContentId = content.getContentId(context);
        String editMode = "Edit";
        StringBuilder editRequest = new StringBuilder(content.getEditRequest(context));
        String editContainerStyle = content.getEditContainerStyle(context);
        String enableEditName = content.getEnableEditName(context);
        String enableEditValue = (String)context.get(enableEditName);
        if (editRequest != null && editRequest.toString().toUpperCase().indexOf("IMAGE") > 0) {
            editMode += " Image";
        }
        //String editRequestWithParams = editRequest + "?contentId=${currentValue.contentId}&drDataResourceId=${currentValue.drDataResourceId}&directEditRequest=${directEditRequest}&indirectEditRequest=${indirectEditRequest}&caContentIdTo=${currentValue.caContentIdTo}&caFromDate=${currentValue.caFromDate}&caContentAssocTypeId=${currentValue.caContentAssocTypeId}";

        if (UtilValidate.isNotEmpty(editRequest) && "true".equals(enableEditValue)) {
            HttpServletResponse response = (HttpServletResponse) context.get("response");
            HttpServletRequest request = (HttpServletRequest) context.get("request");
            if (request != null && response != null) {
                if (editRequest.indexOf("?") < 0)  editRequest.append("?");
                else editRequest.append("&amp;");
                editRequest.append("contentId=").append(expandedContentId);
                ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
                RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
                writer.append("<a href=\"").append(rh.makeLink(request, response, editRequest.toString(), false, false, false)).append("\">").append(editMode).append("</a>");
            }
            if (UtilValidate.isNotEmpty(editContainerStyle)) {
                writer.append("</div>");
            }
            appendWhitespace(writer);
        }
    }

    public void renderContentFrame(Appendable writer, Map<String, Object> context, ModelScreenWidget.Content content) throws IOException {


    	HttpServletRequest request = (HttpServletRequest) context.get("request");
    	HttpServletResponse response = (HttpServletResponse) context.get("response");
    	if (request != null && response != null) {
    		ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
    		RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
    		String dataResourceId = content.getDataResourceId(context);
    		//          String urlString = "/content/control/ViewSimpleContent?dataResourceId=" + dataResourceId;
    		String urlString = "/ViewSimpleContent?dataResourceId=" + dataResourceId;

    		writer.append("<iframe src=\"").append(rh.makeLink(request, response, urlString, true, false, false)).append("\" ");
    		writer.append(" width=\"").append(content.getWidth()).append("\"");
    		writer.append(" height=\"").append(content.getHeight()).append("\"");
    		String border = content.getBorder();
    		if (UtilValidate.isNotEmpty(border)) {
    			writer.append(" border=\"").append(border).append("\"");
    		}
    		writer.append("></iframe>");
    	}

    }

    public void renderSubContentBegin(Appendable writer, Map<String, Object> context, ModelScreenWidget.SubContent content) throws IOException {

        String editRequest = content.getEditRequest(context);
        String editContainerStyle = content.getEditContainerStyle(context);
        String enableEditName = content.getEnableEditName(context);
        String enableEditValue = (String)context.get(enableEditName);
        if (UtilValidate.isNotEmpty(editRequest) && "true".equals(enableEditValue)) {
            writer.append("<div");
            writer.append(" class=\"").append(editContainerStyle).append("\"> ");

            appendWhitespace(writer);
        }
    }

    public void renderSubContentBody(Appendable writer, Map<String, Object> context, ModelScreenWidget.SubContent content) throws IOException {
            Locale locale = Locale.getDefault();
            String mimeTypeId = "text/html";
            String expandedContentId = content.getContentId(context);
            String expandedMapKey = content.getMapKey(context);
            String renderedContent = null;
            LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
            Delegator delegator = (Delegator) context.get("delegator");

            // create a new map for the content rendering; so our current context does not get overwritten!
            Map<String, Object> contentContext = FastMap.newInstance();
            contentContext.putAll(context);

            try {
                if (WidgetContentWorker.contentWorker != null) {
                    renderedContent = WidgetContentWorker.contentWorker.renderSubContentAsTextExt(dispatcher, delegator, expandedContentId, expandedMapKey, contentContext, locale, mimeTypeId, true);
                    //Debug.logInfo("renderedContent=" + renderedContent, module);
                } else {
                	Debug.logError("Not rendering content, WidgetContentWorker.contentWorker not found.", module);
                }
                if (UtilValidate.isEmpty(renderedContent)) {
                    String editRequest = content.getEditRequest(context);
                    if (UtilValidate.isNotEmpty(editRequest)) {
                        if (WidgetContentWorker.contentWorker != null) {
                            WidgetContentWorker.contentWorker.renderContentAsTextExt(dispatcher, delegator, "NOCONTENTFOUND", writer, contentContext, locale, mimeTypeId, true);
                        } else {
                        	Debug.logError("Not rendering content, WidgetContentWorker.contentWorker not found.", module);
                        }
                    }
                } else {
                    if (content.xmlEscape()) {
                        renderedContent = UtilFormatOut.encodeXmlValue(renderedContent);
                    }

                    writer.append(renderedContent);
                }

            } catch(GeneralException e) {
                String errMsg = "Error rendering included content with id [" + expandedContentId + "] : " + e.toString();
                Debug.logError(e, errMsg, module);
                //throw new RuntimeException(errMsg);
            } catch(IOException e2) {
                String errMsg = "Error rendering included content with id [" + expandedContentId + "] : " + e2.toString();
                Debug.logError(e2, errMsg, module);
                //throw new RuntimeException(errMsg);
            }
    }

    public void renderSubContentEnd(Appendable writer, Map<String, Object> context, ModelScreenWidget.SubContent content) throws IOException {

        String editMode = "Edit";
        String editRequest = content.getEditRequest(context);
        String editContainerStyle = content.getEditContainerStyle(context);
        String enableEditName = content.getEnableEditName(context);
        String enableEditValue = (String)context.get(enableEditName);
        String expandedContentId = content.getContentId(context);
        String expandedMapKey = content.getMapKey(context);
        if (editRequest != null && editRequest.toUpperCase().indexOf("IMAGE") > 0) {
            editMode += " Image";
        }
        if (UtilValidate.isNotEmpty(editRequest) && "true".equals(enableEditValue)) {
            HttpServletResponse response = (HttpServletResponse) context.get("response");
            HttpServletRequest request = (HttpServletRequest) context.get("request");
            if (request != null && response != null) {
                if (editRequest.indexOf("?") < 0)  editRequest += "?";
                else editRequest += "&amp;";
                editRequest += "contentId=" + expandedContentId;
                if (UtilValidate.isNotEmpty(expandedMapKey)) {
                    editRequest += "&amp;mapKey=" + expandedMapKey;
                }
                //HttpSession session = request.getSession();
                //GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
                /* don't know why this is here. might come to me later. -amb
                Delegator delegator = (Delegator) context.get("delegator");
                String contentIdTo = content.getContentId(context);
                String mapKey = content.getAssocName(context);
                GenericValue view = null;
                try {
                    view = ContentWorker.getSubContentCache(delegator, contentIdTo, mapKey, userLogin, null, UtilDateTime.nowTimestamp(), Boolean.valueOf(false), null);
                } catch(GenericEntityException e) {
                    throw new IOException("Originally a GenericEntityException. " + e.getMessage());
                }
                */
                ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
                RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
                writer.append("<a href=\"").append(rh.makeLink(request, response, editRequest, false, false, false)).append("\">").append(editMode).append("</a>");
            }
            if (UtilValidate.isNotEmpty(editContainerStyle)) {
                writer.append("</div>");
            }
            appendWhitespace(writer);
        }
    }
}
