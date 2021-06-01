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
package org.ofbiz.widget.form;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.BshUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.finder.EntityFinderUtil;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;
import org.ofbiz.widget.WidgetWorker;
import org.ofbiz.widget.form.ModelForm.UpdateArea;
import org.w3c.dom.Element;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Widget Library - Form model class
 */
public class ModelFormField {

    public static final String module = ModelFormField.class.getName();

    protected ModelForm modelForm;

    protected String name;
    protected FlexibleMapAccessor<Map<String, ? extends Object>> mapAcsr;
    protected String entityName;
    protected String serviceName;
    protected FlexibleMapAccessor<Object> entryAcsr;
    protected String parameterName;
    protected String fieldName;
    protected String attributeName;
    protected FlexibleStringExpander title;
    protected FlexibleStringExpander tooltip;
    protected String titleAreaStyle;
    protected String widgetAreaStyle;
    protected String titleStyle;
    protected String widgetStyle;
    protected String tooltipStyle;
    protected String requiredFieldStyle;
    protected String sortFieldStyle;
    protected String sortFieldAscStyle;
    protected String sortFieldDescStyle;
    protected Integer position = null;
    protected String redWhen;
    protected FlexibleStringExpander useWhen;
    protected boolean encodeOutput = true;
    protected String event;
    protected FlexibleStringExpander action;

    protected FieldInfo fieldInfo = null;
    protected String idName;
    protected boolean separateColumn = false;
    protected Boolean requiredField = null;
    protected Boolean sortField = null;
    protected String headerLink;
    protected String headerLinkStyle;
    protected int width = 0;

    /** On Change Event areas to be updated. */
    protected List<UpdateArea> onChangeUpdateAreas;
    /** On Click Event areas to be updated. */
    protected List<UpdateArea> onClickUpdateAreas;
    /** Alternate Management Language areas to be update */
    protected List<UpdateArea> alternateLanguageManagementUpdateAreas;
    /** Alternate Management Language field that contains the contentId */
    protected AlternateLanguageManagement alternateLanguageManagement;

    // ===== CONSTRUCTORS =====
    /** Default Constructor */
    public ModelFormField(ModelForm modelForm) {
        this.modelForm = modelForm;
    }

    /** XML Constructor */
    public ModelFormField(Element fieldElement, ModelForm modelForm) {
        this.modelForm = modelForm;
        this.name = fieldElement.getAttribute("name");
        this.setMapName(fieldElement.getAttribute("map-name"));
        this.entityName = fieldElement.getAttribute("entity-name");
        this.serviceName = fieldElement.getAttribute("service-name");
        this.setEntryName(UtilXml.checkEmpty(fieldElement.getAttribute("entry-name"), this.name));
        this.parameterName = UtilXml.checkEmpty(fieldElement.getAttribute("parameter-name"), this.name);
        this.fieldName = UtilXml.checkEmpty(fieldElement.getAttribute("field-name"), this.name);
        this.attributeName = UtilXml.checkEmpty(fieldElement.getAttribute("attribute-name"), this.name);
        this.setTitle(fieldElement.hasAttribute("title")?fieldElement.getAttribute("title"):null);
        this.setTooltip(fieldElement.getAttribute("tooltip"));
        this.titleAreaStyle = fieldElement.getAttribute("title-area-style");
        this.widgetAreaStyle = fieldElement.getAttribute("widget-area-style");
        this.titleStyle = fieldElement.getAttribute("title-style");
        this.widgetStyle = fieldElement.getAttribute("widget-style");
        this.tooltipStyle = fieldElement.getAttribute("tooltip-style");
        this.requiredFieldStyle = fieldElement.getAttribute("required-field-style");
        this.sortFieldStyle = fieldElement.getAttribute("sort-field-style");
        this.sortFieldAscStyle = fieldElement.getAttribute("sort-field-asc-style");
        this.sortFieldDescStyle = fieldElement.getAttribute("sort-field-desc-style");
        this.redWhen = fieldElement.getAttribute("red-when");
        this.setUseWhen(fieldElement.getAttribute("use-when"));
        this.encodeOutput = !"false".equals(fieldElement.getAttribute("encode-output"));
        this.event = fieldElement.getAttribute("event");
        this.setAction(fieldElement.hasAttribute("action")? fieldElement.getAttribute("action"): null);
        this.idName = fieldElement.getAttribute("id-name");
        this.separateColumn = "true".equals(fieldElement.getAttribute("separate-column"));
        this.requiredField = fieldElement.hasAttribute("required-field") ? "true".equals(fieldElement.getAttribute("required-field")) : null;
        this.sortField = fieldElement.hasAttribute("sort-field") ? "true".equals(fieldElement.getAttribute("sort-field")) : null;
        this.headerLink = fieldElement.getAttribute("header-link");
        this.headerLinkStyle = fieldElement.getAttribute("header-link-style");


        String positionStr = fieldElement.getAttribute("position");
        try {
        	if (UtilValidate.isNotEmpty(positionStr)) {
                position = Integer.valueOf(positionStr);
            }
        } catch (Exception e) {
            Debug.logError(
                e,
                "Could not convert position attribute of the field element to an integer: [" + positionStr + "], using the default of the form renderer",
                module);
        }

        // get sub-element and set fieldInfo
        List<? extends Element> subElements = UtilXml.childElementList(fieldElement);
        for (Element subElement : subElements) {
            String subElementName = subElement.getTagName();
            if (Debug.verboseOn())
                Debug.logVerbose("Processing field " + this.name + " with type info tag " + subElementName, module);

            if (UtilValidate.isEmpty(subElementName)) {
                this.fieldInfo = null;
                this.induceFieldInfo(null); //no defaultFieldType specified here, will default to edit
            } else if ("display".equals(subElementName)) {
                this.fieldInfo = new DisplayField(subElement, this);
            } else if ("display-entity".equals(subElementName)) {
                this.fieldInfo = new DisplayEntityField(subElement, this);
            } else if ("hyperlink".equals(subElementName)) {
                this.fieldInfo = new HyperlinkField(subElement, this);
            } else if ("text".equals(subElementName)) {
                this.fieldInfo = new TextField(subElement, this);
            } else if ("textarea".equals(subElementName)) {
                this.fieldInfo = new TextareaField(subElement, this);
            } else if ("date-time".equals(subElementName)) {
                this.fieldInfo = new DateTimeField(subElement, this);
            } else if ("drop-down".equals(subElementName)) {
                this.fieldInfo = new DropDownField(subElement, this);
            } else if ("check".equals(subElementName)) {
                this.fieldInfo = new CheckField(subElement, this);
            } else if ("radio".equals(subElementName)) {
                this.fieldInfo = new RadioField(subElement, this);
            } else if ("submit".equals(subElementName)) {
                this.fieldInfo = new SubmitField(subElement, this);
            } else if ("reset".equals(subElementName)) {
                this.fieldInfo = new ResetField(subElement, this);
            } else if ("hidden".equals(subElementName)) {
                this.fieldInfo = new HiddenField(subElement, this);
            } else if ("ignored".equals(subElementName)) {
                this.fieldInfo = new IgnoredField(subElement, this);
            } else if ("text-find".equals(subElementName)) {
                this.fieldInfo = new TextFindField(subElement, this);
            } else if ("date-find".equals(subElementName)) {
                this.fieldInfo = new DateFindField(subElement, this);
            } else if ("range-find".equals(subElementName)) {
                this.fieldInfo = new RangeFindField(subElement, this);
            } else if ("lookup".equals(subElementName)) {
                this.fieldInfo = new LookupField(subElement, this);
            } else if ("file".equals(subElementName)) {
                this.fieldInfo = new FileField(subElement, this);
            } else if ("password".equals(subElementName)) {
                this.fieldInfo = new PasswordField(subElement, this);
            } else if ("image".equals(subElementName)) {
                this.fieldInfo = new ImageField(subElement, this);
            } else if ("container".equals(subElementName)) {
            	this.fieldInfo = new ContainerField(subElement, this);
            } else if ("on-field-event-update-area".equals(subElementName)) {
                addOnEventUpdateArea(new UpdateArea(subElement));
            } else if ("alternate-language-management".equals(subElementName)) {
                addAlternateLanguageManagementUpdateArea(new UpdateArea(subElement));
                this.alternateLanguageManagement = new AlternateLanguageManagement(subElement);

            } else {
                throw new IllegalArgumentException("The field sub-element with name " + subElementName + " is not supported");
            }
        }
    }

    public void addOnEventUpdateArea(UpdateArea updateArea) {
        // Event types are sorted as a convenience for the rendering classes
        Debug.logVerbose(this.modelForm.getName() + ":" + this.name + " adding UpdateArea type " + updateArea.getEventType(), module);
        if ("change".equals(updateArea.getEventType())) {
            addOnChangeUpdateArea(updateArea);
        } else if ("click".equals(updateArea.getEventType())) {
            addOnClickUpdateArea(updateArea);
        }
    }

    protected void addOnChangeUpdateArea(UpdateArea updateArea) {
        if (onChangeUpdateAreas == null) {
            onChangeUpdateAreas = FastList.newInstance();
        }
        onChangeUpdateAreas.add(updateArea);
        Debug.logInfo(this.modelForm.getName() + ":" + this.name + " onChangeUpdateAreas size = " + onChangeUpdateAreas.size(), module);
    }

    protected void addOnClickUpdateArea(UpdateArea updateArea) {
        if (onClickUpdateAreas == null) {
            onClickUpdateAreas = FastList.newInstance();
        }
        onClickUpdateAreas.add(updateArea);
    }

    protected void addAlternateLanguageManagementUpdateArea(UpdateArea updateArea) {
        if (alternateLanguageManagementUpdateAreas == null) {
            alternateLanguageManagementUpdateAreas = FastList.newInstance();
        }
        alternateLanguageManagementUpdateAreas.add(updateArea);
    }

    public void mergeOverrideModelFormField(ModelFormField overrideFormField) {
        if (overrideFormField == null)
            return;
        // incorporate updates for values that are not empty in the overrideFormField
        if (UtilValidate.isNotEmpty(overrideFormField.name))
            this.name = overrideFormField.name;
        if (overrideFormField.mapAcsr != null && !overrideFormField.mapAcsr.isEmpty()) {
            //Debug.logInfo("overriding mapAcsr, old=" + (this.mapAcsr==null?"null":this.mapAcsr.getOriginalName()) + ", new=" + overrideFormField.mapAcsr.getOriginalName(), module);
            this.mapAcsr = overrideFormField.mapAcsr;
        }
        if (UtilValidate.isNotEmpty(overrideFormField.entityName))
            this.entityName = overrideFormField.entityName;
        if (UtilValidate.isNotEmpty(overrideFormField.serviceName))
            this.serviceName = overrideFormField.serviceName;
        if (overrideFormField.entryAcsr != null && !overrideFormField.entryAcsr.isEmpty())
            this.entryAcsr = overrideFormField.entryAcsr;
        if (UtilValidate.isNotEmpty(overrideFormField.parameterName))
            this.parameterName = overrideFormField.parameterName;
        if (UtilValidate.isNotEmpty(overrideFormField.fieldName))
            this.fieldName = overrideFormField.fieldName;
        if (UtilValidate.isNotEmpty(overrideFormField.attributeName))
            this.attributeName = overrideFormField.attributeName;
        if (overrideFormField.title != null && !overrideFormField.title.isEmpty()) // title="" can be used to override the original value
            this.title = overrideFormField.title;
        if (overrideFormField.tooltip != null && !overrideFormField.tooltip.isEmpty())
            this.tooltip = overrideFormField.tooltip;
        if (overrideFormField.requiredField != null)
            this.requiredField = overrideFormField.requiredField;
        if (overrideFormField.sortField != null)
            this.sortField = overrideFormField.sortField;
        if (UtilValidate.isNotEmpty(overrideFormField.titleAreaStyle))
            this.titleAreaStyle = overrideFormField.titleAreaStyle;
        if (UtilValidate.isNotEmpty(overrideFormField.widgetAreaStyle))
            this.widgetAreaStyle = overrideFormField.widgetAreaStyle;
        if (UtilValidate.isNotEmpty(overrideFormField.titleStyle))
            this.titleStyle = overrideFormField.titleStyle;
        if (UtilValidate.isNotEmpty(overrideFormField.widgetStyle))
            this.widgetStyle = overrideFormField.widgetStyle;
        if (overrideFormField.position != null)
            this.position = overrideFormField.position;
        if (UtilValidate.isNotEmpty(overrideFormField.redWhen))
            this.redWhen = overrideFormField.redWhen;
        if (UtilValidate.isNotEmpty(overrideFormField.event))
            this.event = overrideFormField.event;
        if (overrideFormField.action != null && !overrideFormField.action.isEmpty())
            this.action = overrideFormField.action;
        if (overrideFormField.useWhen != null && !overrideFormField.useWhen.isEmpty())
            this.useWhen = overrideFormField.useWhen;
        if (overrideFormField.fieldInfo != null) {
            this.setFieldInfo(overrideFormField.fieldInfo);
        }
        if (overrideFormField.fieldInfo != null) {
            this.setHeaderLink(overrideFormField.headerLink);
        }
        if (UtilValidate.isNotEmpty(overrideFormField.idName)) {
            this.idName = overrideFormField.idName;
        }
        if (overrideFormField.onChangeUpdateAreas != null) {
            this.onChangeUpdateAreas = overrideFormField.onChangeUpdateAreas;
        }
        if (overrideFormField.onClickUpdateAreas != null) {
            this.onClickUpdateAreas = overrideFormField.onClickUpdateAreas;
        }
        if (overrideFormField.alternateLanguageManagementUpdateAreas != null) {
            this.alternateLanguageManagementUpdateAreas = overrideFormField.alternateLanguageManagementUpdateAreas;
        }
        if (overrideFormField.alternateLanguageManagement != null) {
            this.alternateLanguageManagement = overrideFormField.alternateLanguageManagement;
        }
        this.encodeOutput = overrideFormField.encodeOutput;
    }

    public boolean induceFieldInfo(String defaultFieldType) {
        if (this.induceFieldInfoFromEntityField(defaultFieldType)) {
            return true;
        }
        if (this.induceFieldInfoFromServiceParam(defaultFieldType)) {
            return true;
        }
        return false;
    }

    public boolean induceFieldInfoFromServiceParam(String defaultFieldType) {
        if (UtilValidate.isEmpty(this.getServiceName()) || UtilValidate.isEmpty(this.getAttributeName())) {
            return false;
        }
        DispatchContext dispatchContext = this.getModelForm().dispatchContext;
        try {
            ModelService modelService = dispatchContext.getModelService(this.getServiceName());
            if (modelService != null) {
                ModelParam modelParam = modelService.getParam(this.getAttributeName());
                if (modelParam != null) {
                    if (UtilValidate.isNotEmpty(modelParam.entityName) && UtilValidate.isNotEmpty(modelParam.fieldName)) {
                        this.entityName = modelParam.entityName;
                        this.fieldName = modelParam.fieldName;
                        if (this.induceFieldInfoFromEntityField(defaultFieldType)) {
                            return true;
                        }
                    }

                    this.induceFieldInfoFromServiceParam(modelService, modelParam, defaultFieldType);
                    return true;
                }
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, "error getting service parameter definition for auto-field with serviceName: " + this.getServiceName() + ", and attributeName: " + this.getAttributeName(), module);
        }
        return false;
    }

    public boolean induceFieldInfoFromServiceParam(ModelService modelService, ModelParam modelParam, String defaultFieldType) {
        if (modelService == null || modelParam == null) {
            return false;
        }

        this.serviceName = modelService.name;
        this.attributeName = modelParam.name;

        if ("find".equals(defaultFieldType)) {
            if (modelParam.type.indexOf("Double") != -1
                || modelParam.type.indexOf("Float") != -1
                || modelParam.type.indexOf("Long") != -1
                || modelParam.type.indexOf("Integer") != -1) {
                ModelFormField.RangeFindField textField = new ModelFormField.RangeFindField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, this);
                textField.setSize(6);
                this.setFieldInfo(textField);
            } else if (modelParam.type.indexOf("Timestamp") != -1) {
                ModelFormField.DateFindField dateTimeField = new ModelFormField.DateFindField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, this);
                dateTimeField.setType("timestamp");
                this.setFieldInfo(dateTimeField);
            } else if (modelParam.type.indexOf("Date") != -1) {
                ModelFormField.DateFindField dateTimeField = new ModelFormField.DateFindField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, this);
                dateTimeField.setType("date");
                this.setFieldInfo(dateTimeField);
            } else if (modelParam.type.indexOf("Time") != -1) {
                ModelFormField.DateFindField dateTimeField = new ModelFormField.DateFindField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, this);
                dateTimeField.setType("time");
                this.setFieldInfo(dateTimeField);
            } else {
                ModelFormField.TextFindField textField = new ModelFormField.TextFindField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, this);
                this.setFieldInfo(textField);
            }
        } else if ("display".equals(defaultFieldType)) {
            ModelFormField.DisplayField displayField = new ModelFormField.DisplayField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, this);
            this.setFieldInfo(displayField);
        } else if ("hidden".equals(defaultFieldType)) {
            ModelFormField.HiddenField hiddenField = new ModelFormField.HiddenField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, this);
            this.setFieldInfo(hiddenField);
        } else {
            // default to "edit"
            if (modelParam.type.indexOf("Double") != -1
                || modelParam.type.indexOf("Float") != -1
                || modelParam.type.indexOf("Long") != -1
                || modelParam.type.indexOf("Integer") != -1) {
                ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, this);
                textField.setSize(6);
                this.setFieldInfo(textField);
            } else if (modelParam.type.indexOf("Timestamp") != -1) {
                ModelFormField.DateTimeField dateTimeField = new ModelFormField.DateTimeField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, this);
                dateTimeField.setType("timestamp");
                this.setFieldInfo(dateTimeField);
            } else if (modelParam.type.indexOf("Date") != -1) {
                ModelFormField.DateTimeField dateTimeField = new ModelFormField.DateTimeField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, this);
                dateTimeField.setType("date");
                this.setFieldInfo(dateTimeField);
            } else if (modelParam.type.indexOf("Time") != -1) {
                ModelFormField.DateTimeField dateTimeField = new ModelFormField.DateTimeField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, this);
                dateTimeField.setType("time");
                this.setFieldInfo(dateTimeField);
            } else {
                ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_SERVICE, this);
                this.setFieldInfo(textField);
            }
        }

        return true;
    }

    public boolean induceFieldInfoFromEntityField(String defaultFieldType) {
        if (UtilValidate.isEmpty(this.getEntityName()) || UtilValidate.isEmpty(this.getFieldName())) {
            return false;
        }
        ModelReader entityModelReader = this.getModelForm().entityModelReader;
        try {
            ModelEntity modelEntity = entityModelReader.getModelEntity(this.getEntityName());
            if (modelEntity != null) {
                ModelField modelField = modelEntity.getField(this.getFieldName());
                if (modelField != null) {
                    // okay, populate using the entity field info...
                    this.induceFieldInfoFromEntityField(modelEntity, modelField, defaultFieldType);
                    return true;
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return false;
    }

    public boolean induceFieldInfoFromEntityField(ModelEntity modelEntity, ModelField modelField, String defaultFieldType) {
        if (modelEntity == null || modelField == null) {
            return false;
        }

        this.entityName = modelEntity.getEntityName();
        this.fieldName = modelField.getName();

        if ("find".equals(defaultFieldType)) {
            if ("id".equals(modelField.getType()) || "id-ne".equals(modelField.getType())) {
                ModelFormField.TextFindField textField = new ModelFormField.TextFindField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(20);
                textField.setMaxlength(Integer.valueOf(20));
                this.setFieldInfo(textField);
            } else if ("id-long".equals(modelField.getType()) || "id-long-ne".equals(modelField.getType())) {
                ModelFormField.TextFindField textField = new ModelFormField.TextFindField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(40);
                textField.setMaxlength(Integer.valueOf(60));
                this.setFieldInfo(textField);
            } else if ("id-vlong".equals(modelField.getType()) || "id-vlong-ne".equals(modelField.getType())) {
                ModelFormField.TextFindField textField = new ModelFormField.TextFindField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(60);
                textField.setMaxlength(Integer.valueOf(250));
                this.setFieldInfo(textField);
            } else if ("very-short".equals(modelField.getType())) {
                ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(6);
                textField.setMaxlength(Integer.valueOf(10));
                this.setFieldInfo(textField);
            } else if ("name".equals(modelField.getType()) || "short-varchar".equals(modelField.getType())) {
                ModelFormField.TextFindField textField = new ModelFormField.TextFindField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(40);
                textField.setMaxlength(Integer.valueOf(60));
                this.setFieldInfo(textField);
            } else if (
                "value".equals(modelField.getType())
                    || "comment".equals(modelField.getType())
                    || "description".equals(modelField.getType())
                    || "long-varchar".equals(modelField.getType())
                    || "url".equals(modelField.getType())
                    || "email".equals(modelField.getType())) {
                ModelFormField.TextFindField textField = new ModelFormField.TextFindField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(60);
                textField.setMaxlength(Integer.valueOf(250));
                this.setFieldInfo(textField);
            } else if (
                "floating-point".equals(modelField.getType()) || "currency-amount".equals(modelField.getType()) || "numeric".equals(modelField.getType())) {
                ModelFormField.RangeFindField textField = new ModelFormField.RangeFindField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(6);
                this.setFieldInfo(textField);
            } else if ("date-time".equals(modelField.getType()) || "date".equals(modelField.getType()) || "time".equals(modelField.getType())) {
                ModelFormField.DateFindField dateTimeField = new ModelFormField.DateFindField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                if ("date-time".equals(modelField.getType())) {
                    dateTimeField.setType("timestamp");
                } else if ("date".equals(modelField.getType())) {
                    dateTimeField.setType("date");
                } else if ("time".equals(modelField.getType())) {
                    dateTimeField.setType("time");
                }
                this.setFieldInfo(dateTimeField);
            } else {
                ModelFormField.TextFindField textField = new ModelFormField.TextFindField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                this.setFieldInfo(textField);
            }
        } else if ("display".equals(defaultFieldType)) {
            ModelFormField.DisplayField displayField = new ModelFormField.DisplayField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
            this.setFieldInfo(displayField);
        } else if ("hidden".equals(defaultFieldType)) {
            ModelFormField.HiddenField hiddenField = new ModelFormField.HiddenField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
            this.setFieldInfo(hiddenField);
        } else {
            if ("id".equals(modelField.getType()) || "id-ne".equals(modelField.getType())) {
                ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(20);
                textField.setMaxlength(Integer.valueOf(20));
                this.setFieldInfo(textField);
            } else if ("id-long".equals(modelField.getType()) || "id-long-ne".equals(modelField.getType())) {
                ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(40);
                textField.setMaxlength(Integer.valueOf(60));
                this.setFieldInfo(textField);
            } else if ("id-vlong".equals(modelField.getType()) || "id-vlong-ne".equals(modelField.getType())) {
                ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(60);
                textField.setMaxlength(Integer.valueOf(250));
                this.setFieldInfo(textField);
            } else if ("indicator".equals(modelField.getType())) {
                ModelFormField.DropDownField dropDownField = new ModelFormField.DropDownField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                dropDownField.setAllowEmpty(false);
                dropDownField.addOptionSource(new ModelFormField.SingleOption("Y", null, dropDownField));
                dropDownField.addOptionSource(new ModelFormField.SingleOption("N", null, dropDownField));
                this.setFieldInfo(dropDownField);
                //ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                //textField.setSize(1);
                //textField.setMaxlength(Integer.valueOf(1));
                //this.setFieldInfo(textField);
            } else if ("very-short".equals(modelField.getType())) {
                ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(6);
                textField.setMaxlength(Integer.valueOf(10));
                this.setFieldInfo(textField);
            } else if ("very-long".equals(modelField.getType())) {
                ModelFormField.TextareaField textareaField = new ModelFormField.TextareaField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textareaField.setCols(60);
                textareaField.setRows(2);
                this.setFieldInfo(textareaField);
            } else if ("name".equals(modelField.getType()) || "short-varchar".equals(modelField.getType())) {
                ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(40);
                textField.setMaxlength(Integer.valueOf(60));
                this.setFieldInfo(textField);
            } else if (
                "value".equals(modelField.getType())
                    || "comment".equals(modelField.getType())
                    || "description".equals(modelField.getType())
                    || "long-varchar".equals(modelField.getType())
                    || "url".equals(modelField.getType())
                    || "email".equals(modelField.getType())) {
                ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(60);
                textField.setMaxlength(Integer.valueOf(250));
                this.setFieldInfo(textField);
            } else if (
                "floating-point".equals(modelField.getType()) || "currency-amount".equals(modelField.getType()) || "numeric".equals(modelField.getType())) {
                ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                textField.setSize(6);
                this.setFieldInfo(textField);
            } else if ("date-time".equals(modelField.getType()) || "date".equals(modelField.getType()) || "time".equals(modelField.getType())) {
                ModelFormField.DateTimeField dateTimeField = new ModelFormField.DateTimeField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                if ("date-time".equals(modelField.getType())) {
                    dateTimeField.setType("timestamp");
                } else if ("date".equals(modelField.getType())) {
                    dateTimeField.setType("date");
                } else if ("time".equals(modelField.getType())) {
                    dateTimeField.setType("time");
                }
                this.setFieldInfo(dateTimeField);
            } else {
                ModelFormField.TextField textField = new ModelFormField.TextField(ModelFormField.FieldInfo.SOURCE_AUTO_ENTITY, this);
                this.setFieldInfo(textField);
            }
        }

        return true;
    }

    public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
        this.fieldInfo.renderFieldString(writer, context, formStringRenderer);
    }

    public List<UpdateArea> getOnChangeUpdateAreas() {
        return onChangeUpdateAreas;
    }

    public List<UpdateArea> getOnClickUpdateAreas() {
        return onClickUpdateAreas;
    }

    public List<UpdateArea> getAlternateLanguageManagementUpdateAreas() {
        return alternateLanguageManagementUpdateAreas;
    }

    public AlternateLanguageManagement getAlternateLanguageManagement() {
        return alternateLanguageManagement;
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }

    public ModelForm getModelForm() {
        return modelForm;
    }

    /**
     * @param fieldInfo
     */
    public void setFieldInfo(FieldInfo fieldInfo) {
        if (fieldInfo == null)
            return;

        // field info is a little different, check source for priority
        if (this.fieldInfo == null || (fieldInfo.getFieldSource() <= this.fieldInfo.getFieldSource())) {
            this.fieldInfo = fieldInfo.cloneFieldInfo(this);
        }
    }

    /**
     * Gets the name of the Service Attribute (aka Parameter) that corresponds
     * with this field. This can be used to get additional information about the field.
     * Use the getServiceName() method to get the Entity name that the field is in.
     *
     * @return
     */
    public String getAttributeName() {
        if (UtilValidate.isNotEmpty(this.attributeName)) {
            return this.attributeName;
        } else {
            return this.name;
        }
    }

    public String getEntityName() {
        if (UtilValidate.isNotEmpty(this.entityName)) {
            return this.entityName;
        } else {
            return this.modelForm.getDefaultEntityName();
        }
    }

    public String getEntryName() {
        if (this.entryAcsr != null && !this.entryAcsr.isEmpty()) {
            return this.entryAcsr.getOriginalName();
        } else {
            return this.name;
        }
    }

    /**
     * Gets the entry from the context that corresponds to this field; if this
     * form is being rendered in an error condition (ie isError in the context
     * is true) then the value will be retreived from the parameters Map in
     * the context.
     *
     * @param context
     * @param encoder
     * @return
     */
    public String getEntry(Map<String, ? extends Object> context) {
        return this.getEntry(context, "", true);
    }
    
    public String getEntry(Map<String, ? extends Object> context, boolean convert) {
        return this.getEntry(context, "", convert);
    }

    public String getEntry(Map<String, ? extends Object> context , String defaultValue) {
        return this.getEntry(context, defaultValue, "", true);
    }
    
    public String getEntry(Map<String, ? extends Object> context , String defaultValue, boolean convert) {
        return this.getEntry(context, defaultValue, "", convert);
    }
    
    public String getEntry(Map<String, ? extends Object> context , String defaultValue, String suffix) {
    	return getEntry(context , defaultValue, suffix, true);
    }

    public String getEntry(Map<String, ? extends Object> context , String defaultValue, String suffix, boolean convert) {
        Boolean isError = (Boolean) context.get("isError");
        Boolean useRequestParameters = (Boolean) context.get("useRequestParameters");

        Locale locale = (Locale) context.get("locale");
        if (locale == null) locale = Locale.getDefault();
        TimeZone timeZone = (TimeZone) context.get("timeZone");
        if (timeZone == null) timeZone = TimeZone.getDefault();

        String returnValue;

        // if useRequestParameters is TRUE then parameters will always be used, if FALSE then parameters will never be used
        // if isError is TRUE and useRequestParameters is not FALSE (ie is null or TRUE) then parameters will be used
        if ((Boolean.TRUE.equals(isError) && !Boolean.FALSE.equals(useRequestParameters)) || (Boolean.TRUE.equals(useRequestParameters))) {
            //Debug.logInfo("Getting entry, isError true so getting from parameters for field " + this.getName() + " of form " + this.modelForm.getName(), module);
        	Map<String, Object> parameters = UtilGenerics.checkMap(context.get("parameters"), String.class, Object.class);
            String parameterName = this.getParameterName(context);
            if (parameters != null && parameters.get(parameterName) != null) {
                Object parameterValue = parameters.get(parameterName);
                if (parameterValue instanceof String) {
                    returnValue = (String) parameterValue;
                } else {
                    // we might want to do something else here in the future, but for now this is probably best
                    Debug.logWarning("Found a non-String parameter value for field [" + this.getModelForm().getName() + "." + this.getFieldName() + "]", module);
                    returnValue = defaultValue;
                }
            } else if (parameters != null && UtilValidate.isNotEmpty(suffix) && parameters.get(parameterName + suffix) != null) {
                Object parameterValue = parameters.get(parameterName + suffix);
                if (parameterValue instanceof String) {
                    returnValue = (String) parameterValue;
                } else {
                    // we might want to do something else here in the future, but for now this is probably best
                    Debug.logWarning("Found a non-String parameter value for field [" + this.getModelForm().getName() + "." + this.getFieldName() + "]", module);
                    returnValue = defaultValue;
                }
            } else {
                returnValue = defaultValue;
            }
        } else {
            //Debug.logInfo("Getting entry, isError false so getting from Map in context for field " + this.getName() + " of form " + this.modelForm.getName(), module);
            Map<String, ? extends Object> dataMap = this.getMap(context);
            boolean dataMapIsContext = false;
            if (dataMap == null) {
                //Debug.logInfo("Getting entry, no Map found with name " + this.getMapName() + ", using context for field " + this.getName() + " of form " + this.modelForm.getName(), module);
                dataMap = context;
                dataMapIsContext = true;
            }
            Object retVal = null;
            if (this.entryAcsr != null && !this.entryAcsr.isEmpty()) {
                //Debug.logInfo("Getting entry, using entryAcsr for field " + this.getName() + " of form " + this.modelForm.getName(), module);
                if (dataMap instanceof GenericEntity) {
                    GenericEntity genEnt = (GenericEntity) dataMap;
                    if (genEnt.getModelEntity().isField(this.entryAcsr.getOriginalName())) {
                        retVal = genEnt.get(this.entryAcsr.getOriginalName(), locale);
                    } else {
                        //TODO: this may never come up, but if necessary use the FlexibleStringExander to eval the name first: String evaled = this.entryAcsr
                    }
                } else {
                    if (UtilValidate.isNotEmpty(suffix)) {
                        String original = this.entryAcsr.getOriginalName();

                        this.setEntryName(original + suffix);
                        retVal = this.entryAcsr.get(dataMap, locale);
                        this.setEntryName(original);
                    }
                    if (UtilValidate.isEmpty(retVal)) {
                        if (UtilValidate.isEmpty(suffix))
                            retVal = this.entryAcsr.get(dataMap, locale);
                        else
                            retVal = defaultValue;
                    }

                }
            } else {
                //Debug.logInfo("Getting entry, no entryAcsr so using field name " + this.name + " for field " + this.getName() + " of form " + this.modelForm.getName(), module);
                // if no entry name was specified, use the field's name
                if (UtilValidate.isNotEmpty(suffix))
                    retVal = dataMap.get(this.name + suffix);
                if (UtilValidate.isEmpty(retVal)) {
                    retVal = dataMap.get(this.name);
                }

            }

            // this is a special case to fill in fields during a create by default from parameters passed in
            if (dataMapIsContext && retVal == null && !Boolean.FALSE.equals(useRequestParameters)) {
                Map<String, ? extends Object> parameters = UtilGenerics.checkMap(context.get("parameters"));
                if (parameters != null) {
                    if (this.entryAcsr != null && !this.entryAcsr.isEmpty()) {
                        retVal = this.entryAcsr.get(parameters);
                    } else {
                        if (UtilValidate.isNotEmpty(suffix))
                            retVal = parameters.get(this.name + suffix);
                        if (UtilValidate.isEmpty(retVal)) {
                            retVal = parameters.get(this.name);
                        }
                    }
                }
            }

            if (UtilValidate.isNotEmpty(retVal)) {
            	if (convert) {
	                // format string based on the user's locale and time zone
	                if (retVal instanceof Double || retVal instanceof Float || retVal instanceof BigDecimal) {
	                    NumberFormat nf = NumberFormat.getInstance(locale);
	                    nf.setMaximumFractionDigits(10);
	                    return nf.format(retVal);
	                } else if (retVal instanceof java.sql.Date) {
	                    DateFormat df = UtilDateTime.toDateFormat(UtilDateTime.DATE_FORMAT, timeZone, locale);
	                    return df.format((java.util.Date) retVal);
	                } else if (retVal instanceof java.sql.Time) {
	                    DateFormat df = UtilDateTime.toTimeFormat(UtilDateTime.TIME_FORMAT, timeZone, locale);
	                    return df.format((java.util.Date) retVal);
	                } else if (retVal instanceof java.sql.Timestamp) {
	                    DateFormat df = UtilDateTime.toDateTimeFormat(UtilDateTime.DATE_TIME_FORMAT, timeZone, locale);
	                    return df.format((java.util.Date) retVal);
	                } else if (retVal instanceof java.util.Date) {
	                    DateFormat df = UtilDateTime.toDateTimeFormat("EEE MMM dd hh:mm:ss z yyyy", timeZone, locale);
	                    return df.format((java.util.Date) retVal);
	                } else {
	                    returnValue = retVal.toString();
	                }
            	}
            	else {
            		returnValue = retVal.toString();
            	}
            } else {
                returnValue = defaultValue;
            }
        }

        if (this.getEncodeOutput() && returnValue != null) {
            StringUtil.SimpleEncoder simpleEncoder = (StringUtil.SimpleEncoder) context.get("simpleEncoder");
            if (simpleEncoder != null) {
                returnValue = simpleEncoder.encode(returnValue);
            }
        }
        return returnValue;
    }

    public Map<String, ? extends Object> getMap(Map<String, ? extends Object> context) {
        if (this.mapAcsr == null || this.mapAcsr.isEmpty()) {
            //Debug.logInfo("Getting Map from default of the form because of no mapAcsr for field " + this.getName(), module);
            return this.modelForm.getDefaultMap(context);
        } else {
            //Debug.logInfo("Getting Map from mapAcsr for field " + this.getName(), module);
            return mapAcsr.get(context);
        }
    }

    /**
     * Gets the name of the Entity Field that corresponds
     * with this field. This can be used to get additional information about the field.
     * Use the getEntityName() method to get the Entity name that the field is in.
     *
     * @return
     */
    public String getFieldName() {
        if (UtilValidate.isNotEmpty(this.fieldName)) {
            return this.fieldName;
        } else {
            return this.name;
        }
    }

    /** Get the name of the Map in the form context that contains the entry,
     * available from the getEntryName() method. This entry is used to
     * pre-populate the field widget when not in an error condition. In an
     * error condition the parameter name is used to get the value from the
     * parameters Map.
     *
     * @return
     */
    public String getMapName() {
        if (this.mapAcsr != null && !this.mapAcsr.isEmpty()) {
            return this.mapAcsr.getOriginalName();
        } else {
            return this.modelForm.getDefaultMapName();
        }
    }

    public String getName() {
        return name;
    }

    public String getParameterName(Map<String, ? extends Object> context) {
        return getParameterName(context, false);
    }

    /**
     * Get the name to use for the parameter for this field in the form interpreter.
     * For HTML forms this is the request parameter name.
     *
     * @return
     */
    public String getParameterName(Map<String, ? extends Object> context, boolean withoutItemIndex) {
        String baseName;
        if (UtilValidate.isNotEmpty(this.parameterName)) {
            baseName = this.parameterName;
        } else {
            baseName = this.name;
        }

        Integer itemIndex = (Integer) context.get("itemIndex");
        if (!withoutItemIndex && itemIndex != null && "multi".equals(this.modelForm.getType())) {
            return baseName + this.modelForm.getItemIndexSeparator() + itemIndex.intValue();
        } else {
            return baseName;
        }
    }

    public int getPosition() {
        if (this.position == null) {
            return 1;
        } else {
            return position.intValue();
        }
    }

    public String getRedWhen() {
        return redWhen;
    }


    public String getEvent() {
        return event;
    }

    public String getAction(Map<String, ? extends Object> context) {
        if (this.action != null && this.action.getOriginal() != null) {
            return action.expandString(context);
        } else {
            return null;
        }
    }

/**
     * the widget/interaction part will be red if the date value is
     *  before-now (for ex. thruDate), after-now (for ex. fromDate), or by-name (if the
     *  field's name or entry-name or fromDate or thruDate the corresponding
     *  action will be done); only applicable when the field is a timestamp
     *
     * @param context
     * @return
     */
    public boolean shouldBeRed(Map<String, Object> context) {
        // red-when ( never | before-now | after-now | by-name ) "by-name"

        String redCondition = this.redWhen;

        if ("never".equals(redCondition)) {
            return false;
        }

        // for performance resaons we check this first, most fields will be eliminated here and the valueOfs will not be necessary
        if (UtilValidate.isEmpty(redCondition) || "by-name".equals(redCondition)) {
            if ("fromDate".equals(this.name) || (this.entryAcsr != null && "fromDate".equals(this.entryAcsr.getOriginalName()))) {
                redCondition = "after-now";
            } else if ("thruDate".equals(this.name) || (this.entryAcsr != null && "thruDate".equals(this.entryAcsr.getOriginalName()))) {
                redCondition = "before-now";
            } else {
                return false;
            }
        }

        boolean isBeforeNow = false;
        if ("before-now".equals(redCondition)) {
            isBeforeNow = true;
        } else if ("after-now".equals(redCondition)) {
            isBeforeNow = false;
        } else {
            return false;
        }

        java.sql.Date dateVal = null;
        java.sql.Time timeVal = null;
        java.sql.Timestamp timestampVal = null;

        //now before going on, check to see if the current entry is a valid date and/or time and get the value
        String value = this.getEntry(context, null);
        try {
            timestampVal = java.sql.Timestamp.valueOf(value);
        } catch (Exception e) {
            // okay, not a timestamp...
        }

        if (timestampVal == null) {
            try {
                dateVal = java.sql.Date.valueOf(value);
            } catch (Exception e) {
                // okay, not a date...
            }
        }

        if (timestampVal == null && dateVal == null) {
            try {
                timeVal = java.sql.Time.valueOf(value);
            } catch (Exception e) {
                // okay, not a time...
            }
        }

        if (timestampVal == null && dateVal == null && timeVal == null) {
            return false;
        }

        long nowMillis = System.currentTimeMillis();
        if (timestampVal != null) {
            java.sql.Timestamp nowStamp = new java.sql.Timestamp(nowMillis);
            if (!timestampVal.equals(nowStamp)) {
                if (isBeforeNow) {
                    if (timestampVal.before(nowStamp)) {
                        return true;
                    }
                } else {
                    if (timestampVal.after(nowStamp)) {
                        return true;
                    }
                }
            }
        } else if (dateVal != null) {
            java.sql.Date nowDate = new java.sql.Date(nowMillis);
            if (!dateVal.equals(nowDate)) {
                if (isBeforeNow) {
                    if (dateVal.before(nowDate)) {
                        return true;
                    }
                } else {
                    if (dateVal.after(nowDate)) {
                        return true;
                    }
                }
            }
        } else if (timeVal != null) {
            java.sql.Time nowTime = new java.sql.Time(nowMillis);
            if (!timeVal.equals(nowTime)) {
                if (isBeforeNow) {
                    if (timeVal.before(nowTime)) {
                        return true;
                    }
                } else {
                    if (timeVal.after(nowTime)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public String getServiceName() {
        if (UtilValidate.isNotEmpty(this.serviceName)) {
            return this.serviceName;
        } else {
            return this.modelForm.getDefaultServiceName();
        }
    }

    public String getTitle(Map<String, Object> context) {
    	if (this.title != null && !this.title.isEmpty()) {
            return title.expandString(context);
        } else {
            // create a title from the name of this field; expecting a Java method/field style name, ie productName or productCategoryId
        	if (UtilValidate.isEmpty(this.name)) {
                // this should never happen, ie name is required
                return "";
            }

            // search for a localized label for the field's name
        	Map<String, String> uiLabelMap = UtilGenerics.checkMap(context.get("uiLabelMap"));
            if (uiLabelMap != null) {
                String titleFieldName = "FormFieldTitle_" + this.name;
                String localizedName = uiLabelMap.get(titleFieldName);
                if (!localizedName.equals(titleFieldName)) {
                    return localizedName;
                }
            } else {
                Debug.logWarning("Could not find uiLabelMap in context while rendering form " + this.modelForm.getName(), module);
            }

            // create a title from the name of this field; expecting a Java method/field style name, ie productName or productCategoryId
            StringBuilder autoTitlewriter = new StringBuilder();

            // always use upper case first letter...
            autoTitlewriter.append(Character.toUpperCase(this.name.charAt(0)));

            // just put spaces before the upper case letters
            for (int i = 1; i < this.name.length(); i++) {
                char curChar = this.name.charAt(i);
                if (Character.isUpperCase(curChar)) {
                    autoTitlewriter.append(' ');
                }
                autoTitlewriter.append(curChar);
            }

            return autoTitlewriter.toString();
        }
    }

    public String getTitleAreaStyle() {
        if (UtilValidate.isNotEmpty(this.titleAreaStyle)) {
            return this.titleAreaStyle;
        } else {
            return this.modelForm.getDefaultTitleAreaStyle();
        }
    }

    public String getTitleStyle() {
        if (UtilValidate.isNotEmpty(this.titleStyle)) {
            return this.titleStyle;
        } else {
            return this.modelForm.getDefaultTitleStyle();
        }
    }

    public String getRequiredFieldStyle() {
        if (UtilValidate.isNotEmpty(this.requiredFieldStyle)) {
            return this.requiredFieldStyle;
        } else {
            return this.modelForm.getDefaultRequiredFieldStyle();
        }
    }

    public String getSortFieldStyle() {
        if (UtilValidate.isNotEmpty(this.sortFieldStyle)) {
            return this.sortFieldStyle;
        }
        return this.modelForm.getDefaultSortFieldStyle();
    }

    public String getSortFieldStyleAsc() {
        if (UtilValidate.isNotEmpty(this.sortFieldAscStyle)) {
            return this.sortFieldAscStyle;
        }
        return this.modelForm.getDefaultSortFieldAscStyle();
    }

    public String getSortFieldStyleDesc() {
        if (UtilValidate.isNotEmpty(this.sortFieldDescStyle)) {
            return this.sortFieldDescStyle;
        }
        return this.modelForm.getDefaultSortFieldDescStyle();
    }

    public String getTooltip(Map<String, Object> context) {
        if (tooltip != null && !tooltip.isEmpty()) {
            return tooltip.expandString(context);
        } else {
            return "";
        }
    }

    public String getUseWhen(Map<String, Object> context) {
        if (this.useWhen != null && !this.useWhen.isEmpty()) {
            return this.useWhen.expandString(context);
        } else {
            return "";
        }
    }

    public boolean getEncodeOutput() {
        return this.encodeOutput;
    }

    public String getIdName() {
        if (UtilValidate.isNotEmpty(idName)) {
            return idName;
        } else {
            return this.modelForm.getName() + "_" + this.getFieldName();
        }
    }

    public String getCurrentContainerId(Map<String, Object> context) {
    	ModelForm modelForm = this.getModelForm();
    	if (modelForm != null) {
    		Integer itemIndex = (Integer) context.get("itemIndex");
    		if (modelForm != null && ("list".equals(modelForm.getType()) || "multi".equals(modelForm.getType() ))) {
    			if (itemIndex != null) {
    				return this.getIdName() + modelForm.getItemIndexSeparator() + itemIndex.intValue();
    			}
    		}
    	}
    	return this.getIdName();
    }

    public String getIdName(Map<String, ? extends Object> context) {
        String baseIdName = this.getIdName();
        Integer itemIndex = (Integer) context.get("itemIndex");
        if (itemIndex != null && "multi".equals(this.modelForm.getType())) {
            return baseIdName + this.modelForm.getItemIndexSeparator() + itemIndex.intValue();
        } else {
            return baseIdName;
        }
    }

    public String getHeaderLink() {
        return headerLink;
    }

    public String getHeaderLinkStyle() {
        return headerLinkStyle;
    }


    /**
     * @param string
     */
    public void setIdName(String string) {
        idName = string;
    }


    public boolean isUseWhenEmpty() {
        if (this.useWhen == null) {
            return true;
        }

        return this.useWhen.isEmpty();
    }

    public boolean shouldUse(Map<String, Object> context) {
        String useWhenStr = this.getUseWhen(context);
        return bshEvalBoolean(context, useWhenStr, "use-when", true);
    }

    public boolean bshEvalBoolean(Map<String, Object> context, String expressionStr, String expressionName, boolean defValue) {
        if (UtilValidate.isEmpty(expressionStr)) {
            return defValue;
        } else {
            try {
                Interpreter bsh = this.modelForm.getBshInterpreter(context);
                Object retVal = bsh.eval(StringUtil.convertOperatorSubstitutions(expressionStr));
                boolean condTrue = false;
                // retVal should be a Boolean, if not something weird is up...
                if (retVal instanceof Boolean) {
                    Boolean boolVal = (Boolean) retVal;
                    condTrue = boolVal.booleanValue();
                } else {
                    throw new IllegalArgumentException("Return value from " + expressionName + " condition [" + expressionStr + "] was not a Boolean: "
                            + (retVal != null ? retVal.getClass().getName() : null) + " [" + retVal + "] on the field " + this.name + " of form " + this.modelForm.getName());
                }

                return condTrue;
            } catch (EvalError e) {
                String errMsg = "Error evaluating BeanShell " + expressionName + " condition [" + expressionStr + "] on the field "
                        + this.name + " of form " + this.modelForm.getName() + ": " + e.toString();
                Debug.logError(e, errMsg, module);
                //Debug.logError("For " + expressionName + " eval error context is: " + context, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }

    /**
     * Checks if field is a row submit field.
     */
    public boolean isRowSubmit() {
        if (!"multi".equals(getModelForm().getType())) return false;
        if (getFieldInfo().getFieldType() != ModelFormField.FieldInfo.CHECK) return false;
        if (!CheckField.ROW_SUBMIT_FIELD_NAME.equals(getName())) return false;
        return true;
    }

    public String getWidgetAreaStyle() {
        if (UtilValidate.isNotEmpty(this.widgetAreaStyle)) {
            return this.widgetAreaStyle;
        } else {
            return this.modelForm.getDefaultWidgetAreaStyle();
        }
    }

    public String getWidgetStyle() {
        if (UtilValidate.isNotEmpty(this.widgetStyle)) {
            return this.widgetStyle;
        } else {
            return this.modelForm.getDefaultWidgetStyle();
        }
    }

    public String getTooltipStyle() {
        if (UtilValidate.isNotEmpty(this.tooltipStyle)) {
            return this.tooltipStyle;
        } else {
            return this.modelForm.getDefaultTooltipStyle();
        }
    }

    /**
     * @param string
     */
    public void setAttributeName(String string) {
        attributeName = string;
    }

    /**
     * @param string
     */
    public void setEntityName(String string) {
        entityName = string;
    }

    /**
     * @param string
     */
    public void setEntryName(String string) {
        entryAcsr = FlexibleMapAccessor.getInstance(string);
    }

    /**
     * @param string
     */
    public void setFieldName(String string) {
        fieldName = string;
    }

    /**
     * @param string
     */
    public void setMapName(String string) {
        this.mapAcsr = FlexibleMapAccessor.getInstance(string);
    }

    /**
     * @param string
     */
    public void setName(String string) {
        name = string;
    }

    /**
     * @param string
     */
    public void setParameterName(String string) {
        parameterName = string;
    }

    /**
     * @param i
     */
    public void setPosition(int i) {
        position = Integer.valueOf(i);
    }

    /**
     * @param string
     */
    public void setRedWhen(String string) {
        redWhen = string;
    }


    /**
     * @param string
     */
    public void setEvent(String string) {
        event = string;
    }

    /**
     * @param string
     */
    public void setAction(String string) {
        this.action = FlexibleStringExpander.getInstance(string);
    }

    /**
     * @param string
     */
    public void setServiceName(String string) {
        serviceName = string;
    }

    /**
     * @param string
     */
    public void setTitle(String string) {
        this.title = FlexibleStringExpander.getInstance(string);
    }

    /**
     * @param string
     */
    public void setTitleAreaStyle(String string) {
        this.titleAreaStyle = string;
    }

    /**
     * @param string
     */
    public void setTitleStyle(String string) {
        this.titleStyle = string;
    }

    /**
     * @param string
     */
    public void setTooltip(String string) {
        this.tooltip = FlexibleStringExpander.getInstance(string);
    }

    /**
     * @param string
     */
    public void setUseWhen(String string) {
        this.useWhen = FlexibleStringExpander.getInstance(string);
    }

    public void setEncodeOutput(boolean encodeOutput) {
        this.encodeOutput = encodeOutput;
    }

    /**
     * @param string
     */
    public void setWidgetAreaStyle(String string) {
        this.widgetAreaStyle = string;
    }

    /**
     * @param string
     */
    public void setWidgetStyle(String string) {
        this.widgetStyle = string;
    }

    /**
     * @param string
     */
    public void setTooltipStyle(String string) {
        this.tooltipStyle = string;
    }

    public boolean getSeparateColumn() {
        return this.separateColumn;
    }

    /**
     * @param string
     */
    public void setHeaderLink(String string) {
        this.headerLink = string;
    }

    /**
     * @param string
     */
    public void setHeaderLinkStyle(String string) {
        this.headerLinkStyle = string;
    }


    public boolean getRequiredField() {
        return this.requiredField != null ? this.requiredField : false;
    }

    /**
     * @param boolean
     */
    public void setRequiredField(boolean required) {
        this.requiredField = required;
    }

    public boolean isSortField() {
    	return this.sortField != null && this.sortField.booleanValue();
    }

    /**
     * @param boolean
     */
    public void setSortField(boolean sort) {
        this.sortField = Boolean.valueOf(sort);
    }

    /**
     * @param ModelForm
     */
    public void setModelForm(ModelForm modelForm) {
        this.modelForm = modelForm;
    }


    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }


    public static abstract class FieldInfo {

        public static final int DISPLAY = 1;
        public static final int HYPERLINK = 2;
        public static final int TEXT = 3;
        public static final int TEXTAREA = 4;
        public static final int DATE_TIME = 5;
        public static final int DROP_DOWN = 6;
        public static final int CHECK = 7;
        public static final int RADIO = 8;
        public static final int SUBMIT = 9;
        public static final int RESET = 10;
        public static final int HIDDEN = 11;
        public static final int IGNORED = 12;
        public static final int TEXTQBE = 13;
        public static final int DATEQBE = 14;
        public static final int RANGEQBE = 15;
        public static final int LOOKUP = 16;
        public static final int FILE = 17;
        public static final int PASSWORD = 18;
        public static final int IMAGE = 19;
        public static final int DISPLAY_ENTITY = 20;
        public static final int CONTAINER = 21;

        // the numbering here represents the priority of the source;
        //when setting a new fieldInfo on a modelFormField it will only set
        //the new one if the fieldSource is less than or equal to the existing
        //fieldSource, which should always be passed as one of the following...
        public static final int SOURCE_EXPLICIT = 1;
        public static final int SOURCE_AUTO_ENTITY = 2;
        public static final int SOURCE_AUTO_SERVICE = 3;

        public static Map<String, Integer> fieldTypeByName = new HashMap<String, Integer>();

        static {
            fieldTypeByName.put("display", Integer.valueOf(1));
            fieldTypeByName.put("hyperlink", Integer.valueOf(2));
            fieldTypeByName.put("text", Integer.valueOf(3));
            fieldTypeByName.put("textarea", Integer.valueOf(4));
            fieldTypeByName.put("date-time", Integer.valueOf(5));
            fieldTypeByName.put("drop-down", Integer.valueOf(6));
            fieldTypeByName.put("check", Integer.valueOf(7));
            fieldTypeByName.put("radio", Integer.valueOf(8));
            fieldTypeByName.put("submit", Integer.valueOf(9));
            fieldTypeByName.put("reset", Integer.valueOf(10));
            fieldTypeByName.put("hidden", Integer.valueOf(11));
            fieldTypeByName.put("ignored", Integer.valueOf(12));
            fieldTypeByName.put("text-find", Integer.valueOf(13));
            fieldTypeByName.put("date-find", Integer.valueOf(14));
            fieldTypeByName.put("range-find", Integer.valueOf(15));
            fieldTypeByName.put("lookup", Integer.valueOf(16));
            fieldTypeByName.put("file", Integer.valueOf(17));
            fieldTypeByName.put("password", Integer.valueOf(18));
            fieldTypeByName.put("image", Integer.valueOf(19));
            fieldTypeByName.put("display-entity", Integer.valueOf(20));
            fieldTypeByName.put("container", Integer.valueOf(21));
        }

        protected int fieldType;
        protected int fieldSource;
        protected ModelFormField modelFormField;
        
        public abstract FieldInfo cloneFieldInfo(ModelFormField modelFormField);

        /** Don't allow the Default Constructor */
        protected FieldInfo() {}

        /** Value Constructor */
        public FieldInfo(int fieldSource, int fieldType, ModelFormField modelFormField) {
            this.fieldType = fieldType;
            this.fieldSource = fieldSource;
            this.modelFormField = modelFormField;
        }

        /** XML Constructor */
        public FieldInfo(Element element, ModelFormField modelFormField) {
            this.fieldSource = FieldInfo.SOURCE_EXPLICIT;
            this.fieldType = findFieldTypeFromName(element.getTagName());
            this.modelFormField = modelFormField;
        }

        public ModelFormField getModelFormField() {
            return modelFormField;
        }

        public int getFieldType() {
            return fieldType;
        }

        public int getFieldSource() {
            return this.fieldSource;
        }

        public static int findFieldTypeFromName(String name) {
        	Integer fieldTypeInt = FieldInfo.fieldTypeByName.get(name);
            if (fieldTypeInt != null) {
                return fieldTypeInt.intValue();
            } else {
                throw new IllegalArgumentException("Could not get fieldType for field type name " + name);
            }
        }

        public String getParameterName(Map<String, Object> context) {
            return modelFormField.getParameterName(context);
        }

        public abstract void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException;
    }

    public static abstract class FieldInfoWithOptions extends FieldInfo {
        protected FieldInfoWithOptions() {
            super();
        }

        protected FlexibleStringExpander noCurrentSelectedKey = null;
        protected List<OptionSource> optionSources = new LinkedList<OptionSource>();

        public FieldInfoWithOptions(int fieldSource, int fieldType, ModelFormField modelFormField) {
            super(fieldSource, fieldType, modelFormField);
        }

        public FieldInfoWithOptions(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);

            noCurrentSelectedKey = FlexibleStringExpander.getInstance(element.getAttribute("no-current-selected-key"));

            // read all option and entity-options sub-elements, maintaining order
            List<? extends Element> childElements = UtilXml.childElementList(element);
            if (childElements.size() > 0) {
            	for (Element childElement: childElements) {
                    if ("option".equals(childElement.getTagName())) {
                        this.addOptionSource(new SingleOption(childElement, this));
                    } else if ("list-options".equals(childElement.getTagName())) {
                        this.addOptionSource(new ListOptions(childElement, this));
                    } else if ("entity-options".equals(childElement.getTagName())) {
                        this.addOptionSource(new EntityOptions(childElement, this));
                    }
                }
            } else {
                // this must be added or the multi-form select box options would not show up
                this.addOptionSource(new SingleOption("Y", " ", this));
            }
        }

        public List<OptionValue> getAllOptionValues(Map<String, Object> context, Delegator delegator) {
            List<OptionValue> optionValues = new LinkedList<OptionValue>();
            for (OptionSource optionSource: this.optionSources) {
                optionSource.addOptionValues(optionValues, context, delegator);
            }
            return optionValues;
        }

        public List<OptionSource> getOptionSources() {
            return optionSources;
        }

        public static String getDescriptionForOptionKey(String key, List<OptionValue> allOptionValues) {
            if (UtilValidate.isEmpty(key)) {
                return "";
            }

            if (UtilValidate.isEmpty(allOptionValues)) {
                return key;
            }

            for (OptionValue optionValue: allOptionValues) {
                if (key.equals(optionValue.getKey())) {
                    return optionValue.getDescription();
                }
            }

            // if we get here we didn't find a match, just return the key
            return key;
        }

        public String getNoCurrentSelectedKey(Map<String, Object> context) {
            if (this.noCurrentSelectedKey == null) {
                return null;
            }
            return this.noCurrentSelectedKey.expandString(context);
        }

        public void setNoCurrentSelectedKey(String string) {
            this.noCurrentSelectedKey = FlexibleStringExpander.getInstance(string);
        }

        public void addOptionSource(OptionSource optionSource) {
            this.optionSources.add(optionSource);
        }
        
        protected void cloneFieldInfo(FieldInfoWithOptions res) {
        	if (UtilValidate.isNotEmpty(this.optionSources)) {
				for (OptionSource optionSource : this.optionSources) {
					res.addOptionSource(optionSource.cloneOptionSource(res));
				}
			}
        	res.noCurrentSelectedKey = this.noCurrentSelectedKey;
        }
    }

    public static class AlternateLanguageManagement {
        protected String contentFieldAssociation;

        public AlternateLanguageManagement(Element subElement) {
            this.contentFieldAssociation = subElement.getAttribute("content-field-association");
        }

        public String getContentFieldAssociation() {
            return this.contentFieldAssociation;
        }
    }

    public static class OptionValue {
        protected String key;
        protected String description;
        protected Map<String, ? extends Object> auxiliaryMap = null;

        public OptionValue(String key, String description) {
            this.key = key;
            this.description = description;
        }

        public String getKey() {
            return key;
        }

        public String getDescription() {
            return description;
        }

        public void setAuxiliaryMap(Map<String, ? extends Object> auxiliaryMap) {
            this.auxiliaryMap = auxiliaryMap;
        }

        public Map<String, ? extends Object> getAuxiliaryMap() {
            return auxiliaryMap;
        }
    }

    public static abstract class OptionSource {
        protected FieldInfo fieldInfo;

        public abstract void addOptionValues(List<OptionValue> optionValues, Map<String, Object> context, Delegator delegator);
        
        public abstract OptionSource cloneOptionSource(FieldInfo fieldInfo);
    }

    public static class SingleOption extends OptionSource {
        protected FlexibleStringExpander key;
        protected FlexibleStringExpander description;

        public SingleOption(FieldInfo fieldInfo) {
        	this.fieldInfo = fieldInfo;
        }
        
        public SingleOption(String key, String description, FieldInfo fieldInfo) {
            this.key = FlexibleStringExpander.getInstance(key);
            this.description = FlexibleStringExpander.getInstance(UtilXml.checkEmpty(description, key));
            this.fieldInfo = fieldInfo;
        }

        public SingleOption(Element optionElement, FieldInfo fieldInfo) {
            this.key = FlexibleStringExpander.getInstance(optionElement.getAttribute("key"));
            this.description = FlexibleStringExpander.getInstance(UtilXml.checkEmpty(optionElement.getAttribute("description"), optionElement.getAttribute("key")));
            this.fieldInfo = fieldInfo;
        }

        @Override
        public void addOptionValues(List<OptionValue> optionValues, Map<String, Object> context, Delegator delegator) {
            optionValues.add(new OptionValue(key.expandString(context), description.expandString(context)));
        }

		@Override
		public OptionSource cloneOptionSource(FieldInfo fieldInfo) {
			SingleOption res = new SingleOption(fieldInfo);
			if (UtilValidate.isNotEmpty(this.key)) {
				res.key = FlexibleStringExpander.getInstance(this.key.getOriginal());
			} else {
				res.key = FlexibleStringExpander.getInstance("");
			}
			if (UtilValidate.isNotEmpty(this.description)) {
				res.description = FlexibleStringExpander.getInstance(this.description.getOriginal());
			} else {
				res.description = FlexibleStringExpander.getInstance("");
			}
			
			return res;
		}
    }

    public static class ListOptions extends OptionSource {
        protected FlexibleMapAccessor<List<? extends Object>> listAcsr;
        protected String listEntryName;
        protected FlexibleMapAccessor<Object> keyAcsr;
        protected FlexibleStringExpander description;

        public ListOptions(FieldInfo fieldInfo) {
        	this.fieldInfo = fieldInfo;
        }
        
        public ListOptions(String listName, String listEntryName, String keyName, String description, FieldInfo fieldInfo) {
            this.listAcsr = FlexibleMapAccessor.getInstance(listName);
            this.listEntryName = listEntryName;
            this.keyAcsr = FlexibleMapAccessor.getInstance(keyName);
            this.description = FlexibleStringExpander.getInstance(description);
            this.fieldInfo = fieldInfo;
        }

        public ListOptions(Element optionElement, FieldInfo fieldInfo) {
            this.listEntryName = optionElement.getAttribute("list-entry-name");
            this.keyAcsr = FlexibleMapAccessor.getInstance(optionElement.getAttribute("key-name"));
            this.listAcsr = FlexibleMapAccessor.getInstance(optionElement.getAttribute("list-name"));
            this.listEntryName = optionElement.getAttribute("list-entry-name");
            this.description = FlexibleStringExpander.getInstance(optionElement.getAttribute("description"));
            this.fieldInfo = fieldInfo;
        }
        
        @Override
		public OptionSource cloneOptionSource(FieldInfo fieldInfo) {
        	ListOptions res = new ListOptions(fieldInfo);
			if (UtilValidate.isNotEmpty(this.listAcsr)) {
				res.listAcsr = FlexibleMapAccessor.getInstance(this.listAcsr.getOriginalName());
			} else {
				res.listAcsr = FlexibleMapAccessor.getInstance("");
			}
			res.listEntryName = this.listEntryName;
			if (UtilValidate.isNotEmpty(this.keyAcsr)) {
				res.keyAcsr = FlexibleMapAccessor.getInstance(this.keyAcsr.getOriginalName());
			} else {
				res.keyAcsr = FlexibleMapAccessor.getInstance("");
			}
			if (UtilValidate.isNotEmpty(this.description)) {
				res.description = FlexibleStringExpander.getInstance(this.description.getOriginal());
			} else {
				res.description = FlexibleStringExpander.getInstance("");
			}
			
			return res;
		}

        @Override
        public void addOptionValues(List<OptionValue> optionValues, Map<String, Object> context, Delegator delegator) {
        	Locale locale = UtilMisc.ensureLocale(context.get("locale"));
            List<? extends Object> dataList = UtilGenerics.checkList(this.listAcsr.get(context, locale));
            if (dataList != null && dataList.size() != 0) {
                for (Object data: dataList) {
                    MapStack<String> localContext = MapStack.create(context);
                    if (UtilValidate.isNotEmpty(this.listEntryName)) {
                    	localContext.put(this.listEntryName, data);
                    } else {
                    	Map<String, Object> dataMap = UtilGenerics.checkMap(data);
                    	localContext.push(dataMap);
                    }
                    Object keyObj = keyAcsr.get(localContext);
                    String key = null;
                    if (keyObj instanceof String) {
                    	key = (String) keyObj;
                    } else {
                    	try {
                    		key = (String) ObjectType.simpleTypeConvert(keyObj, "String", null, null);
                    	} catch (GeneralException e) {
                    		String errMsg = "Could not convert field value for the field: [" + this.keyAcsr.toString() + "] to String for the value [" + keyObj + "]: " + e.toString();
                    		Debug.logError(e, errMsg, module);
                    	}
                    }
                    optionValues.add(new OptionValue(key, description.expandString(localContext)));
                }
            }
        }
    }

    public static class EntityOptions extends OptionSource {
        protected FlexibleStringExpander entityName;
        protected FlexibleStringExpander keyFieldName;
        protected FlexibleStringExpander descriptionFieldName;
        protected FlexibleStringExpander description;
        protected boolean cache = true;
        protected String filterByDate;
        protected boolean distinct = false;
        protected List<EntityFinderUtil.ConditionExpr> constraintList = null;
        protected List<FlexibleStringExpander> orderByList = null;
        protected List<FlexibleStringExpander> selectFieldList = null;
        protected Map<FlexibleStringExpander, FlexibleStringExpander> descriptionFieldMap = null;
        protected List<FlexibleStringExpander> displayFieldList = null;
        protected List<FlexibleStringExpander> hiddenFieldList = null;
        protected List<FlexibleStringExpander> hiddenDisabledFieldList = null;
        protected List<FlexibleStringExpander> findFieldList = null;

        public EntityOptions(FieldInfo fieldInfo) {
            this.fieldInfo = fieldInfo;
        }

        public EntityOptions(Element entityOptionsElement, FieldInfo fieldInfo) {
            this.entityName = FlexibleStringExpander.getInstance(entityOptionsElement.getAttribute("entity-name"));
            this.keyFieldName = FlexibleStringExpander.getInstance(entityOptionsElement.getAttribute("key-field-name"));
            this.descriptionFieldName = FlexibleStringExpander.getInstance(entityOptionsElement.getAttribute("description-field-name"));
            this.description = FlexibleStringExpander.getInstance(entityOptionsElement.getAttribute("description"));
            this.cache = !"false".equals(entityOptionsElement.getAttribute("cache"));
            this.distinct =  UtilXml.checkBoolean(entityOptionsElement.getAttribute("distinct"), false);
            this.filterByDate = entityOptionsElement.getAttribute("filter-by-date");

            List<? extends Element> constraintElements = UtilXml.childElementList(entityOptionsElement, "entity-constraint");
            if (UtilValidate.isNotEmpty(constraintElements)) {
                this.constraintList = new LinkedList<EntityFinderUtil.ConditionExpr>();
                for (Element constraintElement: constraintElements) {
                    constraintList.add(new EntityFinderUtil.ConditionExpr(constraintElement));
                }
            }

            List<? extends Element> orderByElements = UtilXml.childElementList(entityOptionsElement, "entity-order-by");
            if (UtilValidate.isNotEmpty(orderByElements)) {
                this.orderByList = new LinkedList<FlexibleStringExpander>();
                for (Element orderByElement: orderByElements) {
                    orderByList.add(FlexibleStringExpander.getInstance(orderByElement.getAttribute("field-name")));
                }
            }

            List<? extends Element> selectFieldElements = UtilXml.childElementList(entityOptionsElement, "select-field");
            if (UtilValidate.isNotEmpty(selectFieldElements)) {
                this.selectFieldList = new LinkedList<FlexibleStringExpander>();
                this.findFieldList = new FastList<FlexibleStringExpander>();
                
                for (Element selectFieldElement : selectFieldElements) {
                    selectFieldList.add(FlexibleStringExpander.getInstance(selectFieldElement.getAttribute("field-name")));
                                        
                    FlexibleStringExpander displayFieldValue = FlexibleStringExpander.getInstance(selectFieldElement.getAttribute("display"));                    
                    if (!"hidden-no-find".equals(displayFieldValue.toString())) {                        
                        this.findFieldList.add(FlexibleStringExpander.getInstance(selectFieldElement.getAttribute("field-name")));
                    }
                    
                    if ("true".equals(displayFieldValue.toString())) {
                        if (UtilValidate.isEmpty(this.displayFieldList)) {
                            this.displayFieldList = new FastList<FlexibleStringExpander>();
                            this.descriptionFieldMap = new FastMap<FlexibleStringExpander, FlexibleStringExpander>();
                        }
                        this.displayFieldList.add(FlexibleStringExpander.getInstance(selectFieldElement.getAttribute("field-name")));
                        this.descriptionFieldMap.put(FlexibleStringExpander.getInstance(selectFieldElement.getAttribute("field-name")), FlexibleStringExpander.getInstance(selectFieldElement.getAttribute("description")));
                    }
                    else if ("hidden".equals(displayFieldValue.toString())) {
                        if (UtilValidate.isEmpty(this.hiddenFieldList)) {
                            this.hiddenFieldList = new FastList<FlexibleStringExpander>();
                        }
                        this.hiddenFieldList.add(FlexibleStringExpander.getInstance(selectFieldElement.getAttribute("field-name")));
                    }
                    else if ("hidden-disabled".equals(displayFieldValue.toString())) {
                        if (UtilValidate.isEmpty(this.hiddenDisabledFieldList)) {
                            this.hiddenDisabledFieldList = new FastList<FlexibleStringExpander>();
                        }
                        this.hiddenDisabledFieldList.add(FlexibleStringExpander.getInstance(selectFieldElement.getAttribute("field-name")));
                    }
                     
                }
            }

            this.fieldInfo = fieldInfo;
        }
        
        @Override
		public OptionSource cloneOptionSource(FieldInfo fieldInfo) {
        	EntityOptions res = new EntityOptions(fieldInfo);
        	if (UtilValidate.isNotEmpty(this.entityName)) {
				res.entityName = FlexibleStringExpander.getInstance(this.entityName.getOriginal());
			} else {
				res.entityName = FlexibleStringExpander.getInstance("");
			}
        	if (UtilValidate.isNotEmpty(this.keyFieldName)) {
				res.keyFieldName = FlexibleStringExpander.getInstance(this.keyFieldName.getOriginal());
			} else {
				res.keyFieldName = FlexibleStringExpander.getInstance("");
			}
        	if (UtilValidate.isNotEmpty(this.descriptionFieldName)) {
				res.descriptionFieldName = FlexibleStringExpander.getInstance(this.descriptionFieldName.getOriginal());
			} else {
				res.descriptionFieldName = FlexibleStringExpander.getInstance("");
			}
			if (UtilValidate.isNotEmpty(this.description)) {
				res.description = FlexibleStringExpander.getInstance(this.description.getOriginal());
			} else {
				res.description = FlexibleStringExpander.getInstance("");
			}
			res.cache = this.cache;
			res.filterByDate = this.filterByDate;
			res.distinct = this.distinct;
			res.constraintList = this.constraintList;
			res.orderByList = this.orderByList;
			res.selectFieldList = this.selectFieldList;
			res.findFieldList = this.findFieldList;
			res.displayFieldList = this.displayFieldList;
			res.hiddenFieldList = this.hiddenFieldList;
			res.hiddenDisabledFieldList = this.hiddenDisabledFieldList;
            res.descriptionFieldMap = this.descriptionFieldMap;
			
			return res;
		}

        public String getKeyFieldName(Map<String, ? extends Object> context) {
            String value = null;
            if (UtilValidate.isNotEmpty(this.keyFieldName)) {
                value = this.keyFieldName.expandString(context);
            }
            if (UtilValidate.isEmpty(value)) {
                // get the modelFormField fieldName
                value = this.fieldInfo.getModelFormField().getFieldName();
            }
            return value;
        }

        public List<EntityFinderUtil.ConditionExpr> getConstraintList() {
            return this.constraintList;
        }

        public String getEntityName(Map<String, ? extends Object> context) {
            return this.entityName.expandString(context);
        }

        public List<String> getDisplayField(Map<String, ? extends Object> context) {
            List<String> res = new FastList<String>();
            if (UtilValidate.isNotEmpty(this.displayFieldList)) {
                for(FlexibleStringExpander f : this.displayFieldList) {
                    res.add(f.expandString(context));
                }
            }
            return res;
        }

        public Map<String, String> getDescriptionFieldMap(Map<String, ? extends Object> context) {
            Map<String, String> res = new FastMap<String, String>();
            if (UtilValidate.isNotEmpty(this.descriptionFieldMap)) {
                for(FlexibleStringExpander f : this.descriptionFieldMap.keySet()) {
                    res.put(f.expandString(context), this.descriptionFieldMap.get(f).expandString(context));
                }
            }
            return res;
        }
        
        public FlexibleStringExpander getDescription() {
            return this.description;
        }

        public List<String> getSelectField(Map<String, ? extends Object> context) {
            List<String> res = new FastList<String>();
            if (UtilValidate.isNotEmpty(this.selectFieldList)) {
                for(FlexibleStringExpander f : this.selectFieldList) {
                    res.add(f.expandString(context));
                }
            }
            return res;
        }
        
        public List<String> getFindField(Map<String, ? extends Object> context) {
            List<String> res = new FastList<String>();
            if (UtilValidate.isNotEmpty(this.findFieldList)) {
                for(FlexibleStringExpander f : this.findFieldList) {
                    res.add(f.expandString(context));
                }
            }
            return res;
        }

        public List<String> getHiddenField(Map<String, ? extends Object> context) {
            List<String> res = new FastList<String>();
            if (UtilValidate.isNotEmpty(this.hiddenFieldList)) {
                for(FlexibleStringExpander f : this.hiddenFieldList) {
                    res.add(f.expandString(context));
                }
            }
            return res;
        }

        public List<String> getHiddenDisabledField(Map<String, ? extends Object> context) {
            List<String> res = new FastList<String>();
            if (UtilValidate.isNotEmpty(this.hiddenDisabledFieldList)) {
                for(FlexibleStringExpander f : this.hiddenDisabledFieldList) {
                    res.add(f.expandString(context));
                }
            }
            return res;
        }

        public List<String> getOrderByField(Map<String, ? extends Object> context) {
            List<String> res = new FastList<String>();
            if (UtilValidate.isNotEmpty(this.orderByList)) {
                for(FlexibleStringExpander f : this.orderByList) {
                    res.add(f.expandString(context));
                }
            }
            return res;
        }

        public boolean getDistinct() {
            return this.distinct;
        }

        @Override
        public void addOptionValues(List<OptionValue> optionValues, Map<String, Object> context, Delegator delegator) {
        	// first expand any conditions that need expanding based on the current context
            EntityCondition findCondition = null;
            if (UtilValidate.isNotEmpty(this.constraintList)) {
            	List<EntityCondition> expandedConditionList = new LinkedList<EntityCondition>();
            	for (EntityFinderUtil.Condition condition: constraintList) {
            		ModelEntity modelEntity = delegator.getModelEntity(this.getEntityName(context));
            		expandedConditionList.add(condition.createCondition(context, modelEntity, delegator.getModelFieldTypeReader(modelEntity)));
            	}
            	findCondition = EntityCondition.makeCondition(expandedConditionList);
            }

            try {
                Locale locale = UtilMisc.ensureLocale(context.get("locale"));
                List<GenericValue> values = findValues(findCondition, context, delegator);

                // filter-by-date if requested
                if ("true".equals(this.filterByDate)) {
                    values = EntityUtil.filterByDate(values, true);
                } else if (!"false".equals(this.filterByDate)) {
                	// not explicitly true or false, check to see if has fromDate and thruDate, if so do the filter
                	ModelEntity modelEntity = delegator.getModelEntity(this.getEntityName(context));
                	if (modelEntity != null && modelEntity.isField("fromDate") && modelEntity.isField("thruDate")) {
                		values = EntityUtil.filterByDate(values, true);
                	}
                }

                for (GenericValue value: values) {
                	OptionValue optionValue = makeOptionValue(value, context, locale);
                	optionValues.add(optionValue);
                }
            } catch (GenericEntityException e) {
            	Debug.logError(e, "Error getting entity options in form", module);
            }
        }

        public OptionValue findOptionValueByPK(String keyFieldValue, Map<String, Object> context, Delegator delegator) {
            try {
                final String fieldInfoName = this.fieldInfo.getModelFormField().getFieldName();
                EntityCondition findCondition = makeFindByPkCondition(keyFieldValue, context, delegator);
                List<GenericValue> values = findValues(findCondition, context, delegator);
                if (values != null && values.size() > 1) {
                    Debug.logWarning("More than one value found for field " + fieldInfoName + " of " + getEntityName(context) + ", conditions " + findCondition.toString(), module);
                }
                GenericValue value = EntityUtil.getFirst(values);
                if (value == null) {
                    Debug.logWarning("No value found for field " + fieldInfoName + " of " + getEntityName(context) + ", conditions " + findCondition.toString(), module);
                } else {
                    Locale locale = UtilMisc.ensureLocale(context.get("locale"));
                    return makeOptionValue(value, context, locale);
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting entity options in form", module);
            }
            return null;
        }

        protected EntityCondition makeFindByPkCondition(String keyFieldValue, Map<String, Object> context, Delegator delegator) throws GenericEntityException {
            final Locale locale = UtilMisc.ensureLocale(context.get("locale"));
            final ModelEntity modelEntity = delegator.getModelEntity(getEntityName(context));
            final String keyFieldName = this.getKeyFieldName(context);
            final ModelField keyModelField = modelEntity.getField(keyFieldName);
            final String keyFieldJavaType = delegator.getEntityFieldType(modelEntity, keyModelField.getType()).getJavaType();
            final List<String> pkFieldNames = modelEntity.getPkFieldNames();
            final List<EntityCondition> expandedConditionList = new LinkedList<EntityCondition>();

            Object keyFieldObject = null;
            try {
                keyFieldObject = ObjectType.simpleTypeConvert(keyFieldValue, keyFieldJavaType, null, locale);
            } catch (GeneralException e) {
                keyFieldObject = keyFieldValue.toString();
            }
            expandedConditionList.add(EntityCondition.makeCondition(keyFieldName, keyFieldObject));

            for (String pkFieldName : pkFieldNames) {
                if (!pkFieldName.equals(keyFieldName)) {
                    EntityCondition pkCond = null;
                    if (UtilValidate.isNotEmpty(constraintList)) {
                        for (EntityFinderUtil.Condition condition: constraintList) {
                            if (condition instanceof EntityFinderUtil.ConditionExpr && pkFieldName.equals(((EntityFinderUtil.ConditionExpr)condition).getFieldName(context))) {
                                pkCond = condition.createCondition(context, modelEntity, delegator.getModelFieldTypeReader(modelEntity));
                                break;
                            }
                        }
                    }
                    if (pkCond != null) {
                        expandedConditionList.add(pkCond);
                    }
                }
            }
            return EntityCondition.makeCondition(expandedConditionList);
        }

        protected List<GenericValue> findValues(EntityCondition findCondition, Map<String, Object> context, Delegator delegator) throws GenericEntityException {
            List<String> expandedSelectFieldList = null;
            if (UtilValidate.isNotEmpty(selectFieldList)) {
                for(FlexibleStringExpander selectField : selectFieldList) {
                    if (UtilValidate.isEmpty(expandedSelectFieldList)) {
                        expandedSelectFieldList = new FastList<String>();
                    }
                    expandedSelectFieldList.add(selectField.expandString(context));
                }
            }
            List<String> expandedOrderByList = null;
            if (UtilValidate.isNotEmpty(orderByList)) {
                for(FlexibleStringExpander orderBy : orderByList) {
                    if (UtilValidate.isEmpty(expandedOrderByList)) {
                        expandedOrderByList = new FastList<String>();
                    }
                    expandedOrderByList.add(orderBy.expandString(context));
                }
            }

            EntityFindOptions findOpts = new EntityFindOptions();
            // Add distinct options
            findOpts.setDistinct(this.distinct);
            return delegator.findList(this.getEntityName(context), findCondition, UtilMisc.toSet(expandedSelectFieldList), expandedOrderByList, findOpts, this.cache);
        }

        protected OptionValue makeOptionValue(GenericValue value, Map<String, Object> context, Locale locale) {
            // add key and description with string expansion, ie expanding ${} stuff, passing locale explicitly to expand value string because it won't be found in the Entity
            MapStack<String> localContext = MapStack.create(context);
            localContext.push(value);

            // expand with the new localContext, which is locale aware
            String optionDesc = this.description.expandString(localContext, locale);

            Object keyFieldObject = value.get(this.getKeyFieldName(localContext));
            if (keyFieldObject == null) {
                throw new IllegalArgumentException("The entity-options identifier (from key-name attribute, or default to the field name) [" + this.getKeyFieldName(localContext) + "], may not be a valid key field name for the entity [" + this.entityName + "].");
            }
            String keyFieldValue = null;
            try {
                keyFieldValue = (String)ObjectType.simpleTypeConvert(keyFieldObject, "String", null, locale);
            } catch (GeneralException e) {
                keyFieldValue = keyFieldObject.toString();
            }

            OptionValue optionValue = new OptionValue(keyFieldValue, optionDesc);
            optionValue.setAuxiliaryMap(value);

            return optionValue;
        }
    }

    public static class InPlaceEditor {
    	protected FlexibleStringExpander url;
    	protected String cancelControl;
    	protected String cancelText;
    	protected String clickToEditText;
    	protected String fieldPostCreation;
    	protected String formClassName;
        protected String highlightColor;
        protected String highlightEndColor;
        protected String hoverClassName;
        protected String htmlResponse;
        protected String loadingClassName;
        protected String loadingText;
        protected String okControl;
        protected String okText;
        protected String paramName;
        protected String savingClassName;
        protected String savingText;
        protected String submitOnBlur;
        protected String textBeforeControls;
        protected String textAfterControls;
        protected String textBetweenControls;
        protected String updateAfterRequestCall;
        protected String rows;
        protected String cols;
        protected Map<FlexibleMapAccessor<Object>, Object> fieldMap;

        public InPlaceEditor (Element element) {
            this.setUrl(element.getAttribute("url"));
            this.cancelControl = element.getAttribute("cancel-control");
            this.cancelText = element.getAttribute("cancel-text");
            this.clickToEditText = element.getAttribute("click-to-edit-text");
            this.fieldPostCreation = element.getAttribute("field-post-creation");
            this.formClassName = element.getAttribute("form-class-name");
            this.highlightColor = element.getAttribute("highlight-color");
            this.highlightEndColor = element.getAttribute("highlight-end-color");
            this.hoverClassName = element.getAttribute("hover-class-name");
            this.htmlResponse = element.getAttribute("html-response");
            this.loadingClassName = element.getAttribute("loading-class-name");
            this.loadingText = element.getAttribute("loading-text");
            this.okControl = element.getAttribute("ok-control");
            this.okText = element.getAttribute("ok-text");
            this.paramName = element.getAttribute("param-name");
            this.savingClassName = element.getAttribute("saving-class-name");
            this.savingText = element.getAttribute("saving-text");
            this.submitOnBlur = element.getAttribute("submit-on-blur");
            this.textBeforeControls = element.getAttribute("text-before-controls");
            this.textAfterControls = element.getAttribute("text-after-controls");
            this.textBetweenControls = element.getAttribute("text-between-controls");
            this.updateAfterRequestCall = element.getAttribute("update-after-request-call");

            Element simpleElement = UtilXml.firstChildElement(element, "simple-editor");
            this.rows = simpleElement.getAttribute("rows");
            this.cols = simpleElement.getAttribute("cols");

            this.fieldMap = EntityFinderUtil.makeFieldMap(element);
        }

        public String getUrl(Map<String, Object> context) {
            if (this.url != null) {
                return this.url.expandString(context);
            } else {
                return "";
            }
        }

        public String getCancelControl() {
            return this.cancelControl;
        }

        public String getCancelText() {
            return this.cancelText;
        }

        public String getClickToEditText() {
            return this.clickToEditText;
        }

        public String getFieldPostCreation() {
           return this.fieldPostCreation;
        }

        public String getFormClassName() {
            return this.formClassName;
        }

        public String getHighlightColor() {
            return this.highlightColor;
        }

        public String getHighlightEndColor() {
            return this.highlightEndColor;
        }

        public String getHoverClassName() {
            return this.hoverClassName;
        }

        public String getHtmlResponse() {
            return this.htmlResponse;
        }

        public String getLoadingClassName() {
            return this.loadingClassName;
        }

        public String getLoadingText() {
            return this.loadingText;
        }

        public String getOkControl() {
            return this.okControl;
        }

        public String getOkText() {
            return this.okText;
        }

        public String getParamName() {
            return this.paramName;
        }

        public String getSavingClassName() {
            return this.savingClassName;
        }

        public String getSavingText() {
            return this.savingText;
        }

        public String getSubmitOnBlur() {
            return this.submitOnBlur;
        }

        public String getTextBeforeControls() {
            return this.textBeforeControls;
        }

        public String getTextAfterControls() {
            return this.textAfterControls;
        }

        public String getTextBetweenControls() {
            return this.textBetweenControls;
        }

        public String getUpdateAfterRequestCall() {
            return this.updateAfterRequestCall;
        }

        public String getRows() {
            return this.rows;
        }

        public String getCols() {
            return this.cols;
        }

        public Map<String, Object> getFieldMap(Map<String, Object> context) {
            Map<String, Object> inPlaceEditorContext = new HashMap<String, Object>();
            EntityFinderUtil.expandFieldMapToContext(this.fieldMap, context, inPlaceEditorContext);
            return inPlaceEditorContext;
        }

        public void setUrl(String url) {
            this.url = FlexibleStringExpander.getInstance(url);
        }

        public void setCancelControl(String string) {
            this.cancelControl = string;
        }

        public void setCancelText(String string) {
            this.cancelText = string;
        }

        public void setClickToEditText(String string) {
            this.clickToEditText = string;
        }

        public void setFieldPostCreation(String string) {
            this.fieldPostCreation = string;
        }

        public void setFormClassName(String string) {
            this.formClassName = string;
        }

        public void setHighlightColor(String string) {
            this.highlightColor = string;
        }

        public void setHighlightEndColor(String string) {
            this.highlightEndColor = string;
        }

        public void setHoverClassName(String string) {
            this.hoverClassName = string;
        }

        public void setHtmlResponse(String string) {
            this.htmlResponse = string;
        }

        public void setLoadingClassName(String string) {
            this.loadingClassName = string;
        }

        public void setLoadingText(String string) {
            this.loadingText = string;
        }

        public void setOkControl(String string) {
            this.okControl = string;
        }

        public void setOkText(String string) {
            this.okText = string;
        }

        public void setParamName(String string) {
            this.paramName = string;
        }

        public void setSavingClassName(String string) {
            this.savingClassName = string;
        }

        public void setSavingText(String string) {
            this.savingText = string;
        }

        public void setSubmitOnBlur(String string) {
            this.submitOnBlur = string;
        }

        public void setTextBeforeControls(String string) {
            this.textBeforeControls = string;
        }

        public void setTextAfterControls(String string) {
            this.textAfterControls = string;
        }

        public void setTextBetweenControls(String string) {
            this.textBetweenControls = string;
        }

        public void setUpdateAfterRequestCall(String string) {
            this.updateAfterRequestCall = string;
        }

        public void setRows(String string) {
            this.rows = string;
        }

        public void setCols(String string) {
            this.cols = string;
        }

        public void setFieldMap(Map<FlexibleMapAccessor<Object>, Object> fieldMap) {
            this.fieldMap = fieldMap;
        }
    }

    public static class DisplayField extends FieldInfo {
        protected boolean alsoHidden = true;
        protected FlexibleStringExpander description;
        protected String type;  // matches type of field, currently text or currency
        protected String imageLocation;
        protected FlexibleStringExpander currency;
        protected FlexibleStringExpander date;
        protected InPlaceEditor inPlaceEditor;
        protected FlexibleStringExpander floatingTooltip;

        /*
         * Maps SPA, added drop list
         */
        protected FlexibleStringExpander tooltip;
        // Maps end
        
        protected DisplayField() {
            super();
        }

        public DisplayField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.DISPLAY, modelFormField);
        }

        public DisplayField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.DISPLAY, modelFormField);
        }

        public DisplayField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.type = element.getAttribute("type");
            this.imageLocation = element.getAttribute("image-location");
            this.setCurrency(element.getAttribute("currency"));
            this.setDescription(element.getAttribute("description"));
            this.setDate(element.getAttribute("date"));
            this.setFloatingTooltip(element.getAttribute("floating-tooltip"));
            this.alsoHidden = !"false".equals(element.getAttribute("also-hidden"));

            Element inPlaceEditorElement = UtilXml.firstChildElement(element, "in-place-editor");
            if (inPlaceEditorElement != null) {
                this.inPlaceEditor = new InPlaceEditor(inPlaceEditorElement);
            }
            
            this.setTooltip(element.getAttribute("tooltip"));
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
			DisplayField res = new DisplayField(modelFormField);
			
			this.cloneFieldInfo(res);
			
			return res;
		}
        
        public void cloneFieldInfo(DisplayField res) {
        	res.alsoHidden = this.alsoHidden;
			if (UtilValidate.isNotEmpty(this.description))
				res.description = FlexibleStringExpander.getInstance(this.description.getOriginal());
			else
				res.description = FlexibleStringExpander.getInstance("");
			res.type = this.type;
			if (UtilValidate.isNotEmpty(this.currency))
				res.currency = FlexibleStringExpander.getInstance(this.currency.getOriginal());
			else
				res.currency = FlexibleStringExpander.getInstance("");
			if (UtilValidate.isNotEmpty(this.date))
				res.date = FlexibleStringExpander.getInstance(this.date.getOriginal());
			else
				res.date = FlexibleStringExpander.getInstance("");
			res.inPlaceEditor = this.inPlaceEditor;
			if (UtilValidate.isNotEmpty(this.floatingTooltip))
				res.floatingTooltip = FlexibleStringExpander.getInstance(this.floatingTooltip.getOriginal());
			else
				res.floatingTooltip = FlexibleStringExpander.getInstance("");
			res.imageLocation = this.imageLocation;
			if (UtilValidate.isNotEmpty(this.tooltip))
				res.tooltip = FlexibleStringExpander.getInstance(this.tooltip.getOriginal());
			else
				res.tooltip = FlexibleStringExpander.getInstance("");
			
        }

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderDisplayField(writer, context, this);
        }

        public boolean getAlsoHidden() {
        	return alsoHidden;
        }

        public String getType(){
        	return this.type;
        }

        public String getImageLocation(){
        	return this.imageLocation;
        }

        public Locale getLocaleValue(Map<String, Object> context) {
            Locale locale = (Locale) context.get("locale");
            if (locale == null) locale = Locale.getDefault();
            return locale;
        }

        public TimeZone getTimeZoneValue(Map<String, Object> context) {
            TimeZone timeZone = (TimeZone) context.get("timeZone");
            if (timeZone == null) timeZone = TimeZone.getDefault();
            return timeZone;
        }

        public String getIsoCodeValue(Map<String, Object> context) {
            String isoCode = null;
            if (this.currency != null && !this.currency.isEmpty()) {
                isoCode = this.currency.expandString(context);
            }
            return isoCode;
        }

        public String getStringValue(Map<String, Object> context) {
            String retVal = null;

            // FIXME: to not show enconded characters in pdf report
            modelFormField.setEncodeOutput(false);

            if (this.description != null && !this.description.isEmpty()) {
                retVal = this.description.expandString(context);
            } else {
                retVal = modelFormField.getEntry(context);
            }

            return retVal;
        }

        public BigDecimal getCurrencyValue(Map<String, Object> context, Locale locale, String retVal) throws GeneralException {
            return (BigDecimal) ObjectType.simpleTypeConvert(retVal, "BigDecimal", null, null, locale, true);
        }

        public Date getDateValue(Map<String, Object> context, Locale locale, TimeZone timeZone, String retVal) throws ParseException {
            if (retVal.length() > 10)
                retVal = retVal.substring(0,10);
            try {
                DateFormat df = UtilDateTime.toDateFormat(UtilDateTime.DATE_FORMAT,timeZone, locale);
                Date d = df.parse(retVal);
                return d;
            } catch (Exception e) {
                DateFormat df = UtilDateTime.toDateFormat(UtilDateTime.getDateFormat(locale),timeZone, locale);
                Date d = df.parse(retVal);
                return d;
            }
        }

        public Date getDateTimeValue(Map<String, Object> context, Locale locale, TimeZone timeZone, String retVal) throws ParseException {
            try {
                DateFormat df = UtilDateTime.toDateTimeFormat(UtilDateTime.DATE_TIME_FORMAT,timeZone, locale);

                /*
                // FIXME: get back to the original unencoded value
                //        to avoid DATE_TIME_FORMAT parsing problem
                //        with PDF
                HTMLEntityCodec htmlCodec = new HTMLEntityCodec();
                retVal = htmlCodec.decode(retVal);
                */

                Date d = df.parse(retVal);
                return d;
            } catch (Exception e) {
                DateFormat df = UtilDateTime.toDateTimeFormat(UtilDateTime.getDateTimeFormat(locale),timeZone, locale);
                Date d = df.parse(retVal);
                return d;
            }
        }

        public Date getTimeValue(Map<String, Object> context, Locale locale, TimeZone timeZone, String retVal) throws ParseException {
            if (retVal.length() > 10)
                retVal = retVal.substring(0,10);
            try {
                DateFormat df = UtilDateTime.toTimeFormat(UtilDateTime.TIME_FORMAT,timeZone, locale);
                Date d = df.parse(retVal);
                return d;
            } catch (Exception e) {
                DateFormat df = UtilDateTime.toTimeFormat(UtilDateTime.getTimeFormat(locale),timeZone, locale);
                Date d = df.parse(retVal);
                return d;
            }
        }

        public String getDescription(Map<String, Object> context) {
            String retVal = getStringValue(context);

            if (UtilValidate.isEmpty(retVal)) {
                retVal = "";
            } else if ("currency".equals(type)) {
            	retVal = retVal.replaceAll("&nbsp;", " "); // FIXME : encoding currency is a problem for some locale, we should not have any &nbsp; in retVal other case may arise in future...
                Locale locale = getLocaleValue(context);
                String isoCode = getIsoCodeValue(context);
                try {
                    BigDecimal parsedRetVal = getCurrencyValue(context, locale, retVal);
                    retVal = UtilFormatOut.formatCurrency(parsedRetVal, isoCode, locale, 10); // we set the max to 10 digits as an hack to not round numbers in the ui
                } catch (GeneralException e) {
                    String errMsg = "Error formatting currency value [" + retVal + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new IllegalArgumentException(errMsg);
                }
            } else if ("date".equals(type)) {
                Locale locale = getLocaleValue(context);
                TimeZone timeZone = getTimeZoneValue(context);
                try {
                    Date d = getDateValue(context, locale, timeZone, retVal);
                    DateFormat df = new SimpleDateFormat(UtilDateTime.getDateFormat(locale), locale);
                    retVal = df.format(d);
                } catch (Exception e) {
                    String errMsg = "Error formatting date value [" + retVal + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new IllegalArgumentException(errMsg);
                }
            } else if ("date-time".equals(type)) {
                Locale locale = getLocaleValue(context);
                TimeZone timeZone = getTimeZoneValue(context);
                try {
                    Date d = getDateTimeValue(context, locale, timeZone, retVal);
                    DateFormat df = new SimpleDateFormat(UtilDateTime.getDateTimeFormat(locale), locale);
                    retVal = df.format(d);
                } catch (Exception e) {
                    String errMsg = "Error formatting date value [" + retVal + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new IllegalArgumentException(errMsg);
                }
            } else if ("time".equals(type)) {
            	Locale locale = getLocaleValue(context);
            	TimeZone timeZone = getTimeZoneValue(context);
            	try {
            		Date d = getTimeValue(context, locale, timeZone, retVal);
            		DateFormat df = new SimpleDateFormat(UtilDateTime.getTimeFormat(locale), locale);
            		retVal = df.format(d);
            	} catch (Exception e) {
            		String errMsg = "Error formatting date value [" + retVal + "]: " + e.toString();
            		Debug.logError(e, errMsg, module);
            		throw new IllegalArgumentException(errMsg);
            	}
            } else if ("accounting-number".equals(this.type)) {
            	Locale locale = getLocaleValue(context);
            	try {
            		Double parsedRetVal = (Double) ObjectType.simpleTypeConvert(retVal, "Double", null, locale, false);
            		String template = UtilProperties.getPropertyValue("arithmetic", "accounting-number.format", "#,##0.00;(#,##0.00)");
            		retVal = UtilFormatOut.formatDecimalNumber(parsedRetVal.doubleValue(), template, locale);
            	} catch (GeneralException e) {
            		String errMsg = "Error formatting number [" + retVal + "]: " + e.toString();
            		Debug.logError(e, errMsg, module);
            		throw new IllegalArgumentException(errMsg);
            	}
            }
            return retVal;
        }
        
        public String getFloatingTooltip(Map<String, Object> context) {
        	return this.floatingTooltip.expandString(context);
        }
        
        public InPlaceEditor getInPlaceEditor() {
            return this.inPlaceEditor;
        }

        /**
         * @param b
         */
        public void setAlsoHidden(boolean b) {
            alsoHidden = b;
        }

        /**
         * @param Description
         */
        public void setDescription(String string) {
            description = FlexibleStringExpander.getInstance(string);
        }

        /**
         * @param string
         */
        public void setCurrency(String string) {
            currency = FlexibleStringExpander.getInstance(string);
        }
        /**
         * @param date
         */
        public void setDate(String string) {
            date = FlexibleStringExpander.getInstance(string);
        }

        public void setInPlaceEditor(InPlaceEditor newInPlaceEditor) {
            this.inPlaceEditor = newInPlaceEditor;
        }
        
        public void setFloatingTooltip(String string) {
        	this.floatingTooltip = FlexibleStringExpander.getInstance(string);
        }
        
        public String getTooltip(Map<String, Object> context) {
			return (this.tooltip != null && !this.tooltip.isEmpty()) ? this.tooltip.expandString(context) : "";
		}

		public void setTooltip(String str) {
			this.tooltip = FlexibleStringExpander.getInstance(str);
		}
    }

    public static class DisplayEntityField extends DisplayField {
        protected String entityName;
        protected String keyFieldName;
        protected boolean cache = true;
        protected SubHyperlink subHyperlink;

        protected DisplayEntityField() {
            super();
        }

        public DisplayEntityField(ModelFormField modelFormField) {
            super(modelFormField);
            this.fieldType = FieldInfo.DISPLAY_ENTITY;
        }

        public DisplayEntityField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, modelFormField);
            this.fieldType = FieldInfo.DISPLAY_ENTITY;
        }

        public DisplayEntityField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);

            this.entityName = element.getAttribute("entity-name");
            this.keyFieldName = element.getAttribute("key-field-name");
            this.cache = !"false".equals(element.getAttribute("cache"));

            if (this.description == null || this.description.isEmpty()) {
                this.setDescription("${description}");
            }

            Element subHyperlinkElement = UtilXml.firstChildElement(element, "sub-hyperlink");
            if (subHyperlinkElement != null) {
            	this.subHyperlink = new SubHyperlink(subHyperlinkElement, this.getModelFormField());
            }
        }
        
        @Override
        public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	DisplayEntityField res = new DisplayEntityField(modelFormField);
        	
        	res.entityName = this.entityName;
        	res.keyFieldName = this.keyFieldName;
        	res.cache = this.cache;
        	res.subHyperlink = this.subHyperlink;
        	
        	super.cloneFieldInfo(res);
        	
        	return res;
        }

        public String getDescription(Map<String, Object> context) {
            Locale locale = UtilMisc.ensureLocale(context.get("locale"));

            // rather than using the context to expand the string, lookup the given entity and use it to expand the string
            GenericValue value = null;
            String fieldKey = this.keyFieldName;
            if (UtilValidate.isEmpty(fieldKey)) {
                fieldKey = this.modelFormField.fieldName;
            }
            Delegator delegator = WidgetWorker.getDelegator(context);
            String fieldValue = modelFormField.getEntry(context);
            try {
                value = delegator.findOne(this.entityName, this.cache, fieldKey, fieldValue);
            } catch (GenericEntityException e) {
                String errMsg = "Error getting value from the database for display of field [" + this.modelFormField.getName() + "] on form [" + this.modelFormField.modelForm.getName() + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }

            String retVal = null;
            if (value != null) {
                // expanding ${} stuff, passing locale explicitly to expand value string because it won't be found in the Entity
                MapStack<String> localContext = MapStack.create(context);
                localContext.push(value);

                // expand with the new localContext, which is locale aware
                retVal = this.description.expandString(localContext, locale);
            }
            // try to get the entry for the field if description doesn't expand to anything
            if (UtilValidate.isEmpty(retVal)) {
                retVal = fieldValue;
            }
            if (UtilValidate.isEmpty(retVal)) {
                retVal = "";
            }
            return retVal;
        }

        public SubHyperlink getSubHyperlink() {
            return this.subHyperlink;
        }
        public void setSubHyperlink(SubHyperlink newSubHyperlink) {
            this.subHyperlink = newSubHyperlink;
        }
    }

    public static class HyperlinkField extends FieldInfo {
        public static String DEFAULT_TARGET_TYPE = "intra-app";

        protected boolean alsoHidden = true;
        protected String linkType;
        protected String targetType;
        protected String image;
        protected FlexibleStringExpander target;
        protected FlexibleStringExpander description;
        protected FlexibleStringExpander alternate;
        protected FlexibleStringExpander imageTitle;
        protected FlexibleStringExpander targetWindowExdr;
        protected FlexibleMapAccessor<Map<String, String>> parametersMapAcsr;
        protected List<WidgetWorker.Parameter> parameterList = FastList.newInstance();
        
        protected boolean requestConfirmation = false;
        protected FlexibleStringExpander confirmationMsgExdr;

        protected HyperlinkField() {
            super();
        }

        public HyperlinkField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.HYPERLINK, modelFormField);
        }

        public HyperlinkField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.HYPERLINK, modelFormField);
        }

        public HyperlinkField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);

            this.setDescription(element.getAttribute("description"));
            this.setAlternate(element.getAttribute("alternate"));
            this.setImageTitle(element.getAttribute("image-title"));
            this.setTarget(element.getAttribute("target"));
            this.alsoHidden = !"false".equals(element.getAttribute("also-hidden"));
            this.linkType = element.getAttribute("link-type");
            this.targetType = element.getAttribute("target-type");
            this.targetWindowExdr = FlexibleStringExpander.getInstance(element.getAttribute("target-window"));
            this.parametersMapAcsr = FlexibleMapAccessor.getInstance(element.getAttribute("parameters-map"));
            this.image = element.getAttribute("image-location");
            this.setRequestConfirmation("true".equals(element.getAttribute("request-confirmation")));
            this.setConfirmationMsg(element.getAttribute("confirmation-message"));
            List<? extends Element> parameterElementList = UtilXml.childElementList(element, "parameter");
            for (Element parameterElement: parameterElementList) {
            	this.parameterList.add(new WidgetWorker.Parameter(parameterElement));
            }
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	HyperlinkField res = new HyperlinkField(modelFormField);
			
			res.alsoHidden = this.alsoHidden;
			res.targetType = this.targetType;
			if (UtilValidate.isNotEmpty(this.target))
				res.target = FlexibleStringExpander.getInstance(this.target.getOriginal());
			else
				res.target = FlexibleStringExpander.getInstance("");
			if (UtilValidate.isNotEmpty(this.description))
				res.description = FlexibleStringExpander.getInstance(this.description.getOriginal());
			else
				res.description = FlexibleStringExpander.getInstance("");
			if (UtilValidate.isNotEmpty(this.targetWindowExdr))
				res.targetWindowExdr = FlexibleStringExpander.getInstance(this.targetWindowExdr.getOriginal());
			else
				res.targetWindowExdr = FlexibleStringExpander.getInstance("");
			if (UtilValidate.isNotEmpty(this.imageTitle))
				res.imageTitle = FlexibleStringExpander.getInstance(this.imageTitle.getOriginal());
			else
				res.imageTitle = FlexibleStringExpander.getInstance("");
			if (UtilValidate.isNotEmpty(this.alternate))
				res.alternate = FlexibleStringExpander.getInstance(this.alternate.getOriginal());
			else
				res.alternate = FlexibleStringExpander.getInstance("");
			if (UtilValidate.isNotEmpty(this.parametersMapAcsr))
				res.parametersMapAcsr = FlexibleMapAccessor.getInstance(this.parametersMapAcsr.getOriginalName());
			else
				res.parametersMapAcsr = FlexibleMapAccessor.getInstance("");
			res.image = this.image;
			res.requestConfirmation = this.requestConfirmation;
			res.linkType = this.linkType;
			if (UtilValidate.isNotEmpty(this.confirmationMsgExdr))
				res.confirmationMsgExdr = FlexibleStringExpander.getInstance(this.confirmationMsgExdr.getOriginal());
			else
				res.confirmationMsgExdr = FlexibleStringExpander.getInstance("");
			
			for (WidgetWorker.Parameter parameter: this.parameterList) {
				this.parameterList.add(new WidgetWorker.Parameter(parameter));
			}
			
			return res;
		}

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderHyperlinkField(writer, context, this);
        }

        public boolean getAlsoHidden() {
        	return this.alsoHidden;
        }

        public boolean getRequestConfirmation() {
        	return this.requestConfirmation;
        }

        public String getConfirmation(Map<String, Object> context) {
        	String message = getConfirmationMsg(context);
        	if (UtilValidate.isNotEmpty(message)) {
        		return message;
        	}
        	else if (getRequestConfirmation()) {
        		String defaultMessage = UtilProperties.getPropertyValue("general", "default.confirmation.message", "${uiLabelMap.CommonConfirm}");
        		setConfirmationMsg(defaultMessage);
        		return getConfirmationMsg(context);
        	}
        	return "";
        }

        public String getConfirmationMsg(Map<String, Object> context) {
        	return this.confirmationMsgExdr.expandString(context);
        }

        public String getLinkType() {
        	return this.linkType;
        }

        public String getTargetType() {
            if (UtilValidate.isNotEmpty(this.targetType)) {
                return this.targetType;
            } else {
                return HyperlinkField.DEFAULT_TARGET_TYPE;
            }
        }

        public String getTargetWindow(Map<String, Object> context) {
            String targetWindow = this.targetWindowExdr.expandString(context);
            return targetWindow;
        }

        public String getDescription(Map<String, Object> context) {
            return this.description.expandString(context);
        }

        public String getAlternate(Map<String, Object> context) {
        	return this.alternate.expandString(context);
        }

        public String getImageTitle(Map<String, Object> context) {
        	return this.imageTitle.expandString(context);
        }

        public String getTarget(Map<String, Object> context) {
        	return this.target.expandString(context);
        }

        public Map<String, String> getParameterMap(Map<String, Object> context) {
        	Map<String, String> fullParameterMap = FastMap.newInstance();

        	Map<String, String> addlParamMap = this.parametersMapAcsr.get(context);
        	if (addlParamMap != null) {
        		fullParameterMap.putAll(addlParamMap);
        	}

        	for (WidgetWorker.Parameter parameter: this.parameterList) {
        		fullParameterMap.put(parameter.getName(), parameter.getValue(context));
        	}

        	return fullParameterMap;
        }

        public String getImage() {
        	return this.image;
        }

        /**
         * @param b
         */
        public void setAlsoHidden(boolean b) {
            this.alsoHidden = b;
        }

        /**
         * @param string
         */
        public void setTargetType(String string) {
            this.targetType = string;
        }

        /**
         * @param string
         */
        public void setDescription(String string) {
        	this.description = FlexibleStringExpander.getInstance(string);
        }

        /**
         * @param string
         */
        public void setImageTitle(String string) {
        	this.imageTitle = FlexibleStringExpander.getInstance(string);
        }

        /**
         * @param string
         */
        public void setAlternate(String string) {
        	this.alternate = FlexibleStringExpander.getInstance(string);
        }

        /**
         * @param string
         */
        public void setTarget(String string) {
        	this.target = FlexibleStringExpander.getInstance(string);
        }

        public void setRequestConfirmation(boolean val) {
        	this.requestConfirmation = val;
        }

        public void setConfirmationMsg(String val) {
        	this.confirmationMsgExdr = FlexibleStringExpander.getInstance(val);
        }
    }

    public static class SubHyperlink {
        protected FlexibleStringExpander useWhen;
        protected String linkType;
        protected String linkStyle;
        protected String targetType;
        protected FlexibleStringExpander target;
        protected FlexibleStringExpander description;
        protected FlexibleStringExpander targetWindowExdr;
        protected List<WidgetWorker.Parameter> parameterList = FastList.newInstance();
        protected boolean requestConfirmation = false;
        protected FlexibleStringExpander confirmationMsgExdr;
        protected ModelFormField modelFormField;

        public SubHyperlink(Element element, ModelFormField modelFormField) {
            this.setDescription(element.getAttribute("description"));
            this.setTarget(element.getAttribute("target"));
            this.setUseWhen(element.getAttribute("use-when"));
            this.linkType = element.getAttribute("link-type");
            this.linkStyle = element.getAttribute("link-style");
            this.targetType = element.getAttribute("target-type");
            this.targetWindowExdr = FlexibleStringExpander.getInstance(element.getAttribute("target-window"));
            List<? extends Element> parameterElementList = UtilXml.childElementList(element, "parameter");
            for (Element parameterElement: parameterElementList) {
            	this.parameterList.add(new WidgetWorker.Parameter(parameterElement));
            }
            setRequestConfirmation("true".equals(element.getAttribute("request-confirmation")));
            setConfirmationMsg(element.getAttribute("confirmation-message"));

            this.modelFormField = modelFormField;
        }

        public String getLinkStyle() {
            return this.linkStyle;
        }

        public String getTargetType() {
            if (UtilValidate.isNotEmpty(this.targetType)) {
                return this.targetType;
            } else {
                return HyperlinkField.DEFAULT_TARGET_TYPE;
            }
        }

        public String getDescription(Map<String, Object> context) {
            if (this.description != null) {
                return this.description.expandString(context);
            } else {
                return "";
            }
        }

        public String getTargetWindow(Map<String, Object> context) {
            String targetWindow = this.targetWindowExdr.expandString(context);
            return targetWindow;
        }

        public String getTarget(Map<String, Object> context) {
            if (this.target != null) {
                return this.target.expandString(context);
            } else {
                return "";
            }
        }

        public String getLinkType() {
        	return this.linkType;
        }

        public Map<String, String> getParameterMap(Map<String, Object> context) {
        	Map<String, String> fullParameterMap = FastMap.newInstance();

        	/* leaving this here... may want to add it at some point like the hyperlink element:
        	            Map<String, String> addlParamMap = this.parametersMapAcsr.get(context);
        	            if (addlParamMap != null) {
        	                fullParameterMap.putAll(addlParamMap);
        	            }
        	 */

        	for (WidgetWorker.Parameter parameter: this.parameterList) {
        		fullParameterMap.put(parameter.getName(), parameter.getValue(context));
        	}

        	return fullParameterMap;
        }

        public String getUseWhen(Map<String, Object> context) {
            if (this.useWhen != null) {
                return this.useWhen.expandString(context);
            } else {
                return "";
            }
        }

        public boolean getRequestConfirmation() {
        	return this.requestConfirmation;
        }

        public String getConfirmationMsg(Map<String, Object> context) {
        	return this.confirmationMsgExdr.expandString(context);
        }

        public String getConfirmation(Map<String, Object> context) {
        	String message = getConfirmationMsg(context);
        	if (UtilValidate.isNotEmpty(message)) {
        		return message;
        	}
        	else if (getRequestConfirmation()) {
        		String defaultMessage = UtilProperties.getPropertyValue("general", "default.confirmation.message", "${uiLabelMap.CommonConfirm}");
        		setConfirmationMsg(defaultMessage);
        		return getConfirmationMsg(context);
        	}
        	return "";
        }

        public ModelFormField getModelFormField() {
        	return this.modelFormField;
        }

        public boolean shouldUse(Map<String, Object> context) {
            boolean shouldUse = true;
            String useWhen = this.getUseWhen(context);
            if (UtilValidate.isNotEmpty(useWhen)) {
                try {
                    Interpreter bsh = (Interpreter) context.get("bshInterpreter");
                    if (bsh == null) {
                        bsh = BshUtil.makeInterpreter(context);
                        context.put("bshInterpreter", bsh);
                    }

                    Object retVal = bsh.eval(StringUtil.convertOperatorSubstitutions(useWhen));

                    // retVal should be a Boolean, if not something weird is up...
                    if (retVal instanceof Boolean) {
                        Boolean boolVal = (Boolean) retVal;
                        shouldUse = boolVal.booleanValue();
                    } else {
                        throw new IllegalArgumentException(
                            "Return value from target condition eval was not a Boolean: " + retVal.getClass().getName() + " [" + retVal + "]");
                    }
                } catch (EvalError e) {
                    String errmsg = "Error evaluating BeanShell target conditions";
                    Debug.logError(e, errmsg, module);
                    throw new IllegalArgumentException(errmsg);
                }
            }
            return shouldUse;
        }

        /**
         * @param string
         */
        public void setLinkStyle(String string) {
            this.linkStyle = string;
        }

        /**
         * @param string
         */
        public void setTargetType(String string) {
            this.targetType = string;
        }

        /**
         * @param string
         */
        public void setDescription(String string) {
            this.description = FlexibleStringExpander.getInstance(string);
        }

        /**
         * @param string
         */
        public void setTarget(String string) {
            this.target = FlexibleStringExpander.getInstance(string);
        }

        /**
         * @param string
         */
        public void setUseWhen(String string) {
        	this.useWhen = FlexibleStringExpander.getInstance(string);
        }

        public void setRequestConfirmation(boolean val) {
        	this.requestConfirmation = val;
        }

        public void setConfirmationMsg(String val) {
        	this.confirmationMsgExdr = FlexibleStringExpander.getInstance(val);
        }
    }

    public static class AutoComplete {
        protected String autoSelect;
        protected String frequency;
        protected String minChars;
        protected String choices;
        protected String partialSearch;
        protected String partialChars;
        protected String ignoreCase;
        protected String fullSearch;
        protected String target;

        public AutoComplete() {

        }

        public AutoComplete(Element element) {
            this.autoSelect = element.getAttribute("auto-select");
            this.frequency = element.getAttribute("frequency");
            this.minChars = element.getAttribute("min-chars");
            this.choices = element.getAttribute("choices");
            this.partialSearch = element.getAttribute("partial-search");
            this.partialChars = element.getAttribute("partial-chars");
            this.ignoreCase = element.getAttribute("ignore-case");
            this.fullSearch = UtilXml.checkEmpty(element.getAttribute("full-search"), "true") ;
            this.target = element.getAttribute("target");
        }

        public String getAutoSelect() {
            return this.autoSelect;
        }

        public String getFrequency() {
            return this.frequency;
        }

        public String getMinChars() {
            return this.minChars;
        }

        public String getChoices() {
            return this.choices;
        }

        public String getPartialSearch() {
            return this.partialSearch;
        }

        public String getPartialChars() {
            return this.partialChars;
        }

        public String getIgnoreCase() {
            return this.ignoreCase;
        }

        public String getFullSearch() {
            return this.fullSearch;
        }

        public String getTarget() {
            return this.target;
        }

        public void setAutoSelect(String string) {
            this.autoSelect = string;
        }

        public void setFrequency(String string) {
            this.frequency = string;
        }

        public void setMinChars(String string) {
            this.minChars = string;
        }

        public void setChoices(String string) {
            this.choices = string;
        }

        public void setPartialSearch(String string) {
            this.partialSearch = string;
        }

        public void setPartialChars(String string) {
            this.partialChars = string;
        }

        public void setIgnoreCase(String string) {
            this.ignoreCase = string;
        }

        public void setFullSearch(String string) {
            this.fullSearch = string;
        }

        public void setTarget(String target) {
            this.target = target;
        }

    }

    public static class TextField extends FieldInfo {
        protected int size = 25;
        protected Integer maxlength;
        protected FlexibleStringExpander defaultValue;
        protected SubHyperlink subHyperlink;
        protected boolean disabled;
        protected boolean clientAutocompleteField;
        //Maps field type and attributes for validation
        protected String validityFieldType = "";
        protected FlexibleStringExpander decimalDigits;
        protected String validityMaxValue = "";
        protected FlexibleStringExpander readOnly;
        protected boolean inheritReadOnly = true;
        /**
         * MAPS 02/12/2009 Marco Ruocco Inizio
         */
        /** Struttura di condizioni per la verifica dello stato read-only generale della form */
        protected ModelFormCondition readOnlyCondition;
        /** Tooltip */
        protected boolean showTooltip = false;
        protected FlexibleStringExpander tooltip;
        /** FINE   */

        protected TextField() {
            super();
        }

        public TextField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.TEXT, modelFormField);
        }

        public TextField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.TEXT, modelFormField);
        }

        public TextField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.setDefaultValue(element.getAttribute("default-value"));
            this.setReadOnly(element.getAttribute("read-only"));
            this.inheritReadOnly = "true".equals(element.getAttribute("inherit-read-only"));

            Element readOnlyElement = UtilXml.firstChildElement(element, "read-only");
            if (readOnlyElement != null) {
                this.readOnlyCondition = new ModelFormCondition(readOnlyElement);
            }

            String sizeStr = element.getAttribute("size");
            try {
                size = Integer.parseInt(sizeStr);
            } catch (Exception e) {
            	if (UtilValidate.isNotEmpty(sizeStr)) {
                    Debug.logError("Could not parse the size value of the text element: [" + sizeStr + "], setting to the default of " + size, module);
                }
            }

            String maxlengthStr = element.getAttribute("maxlength");
            try {
                maxlength = Integer.valueOf(maxlengthStr);
            } catch (Exception e) {
                maxlength = null;
                if (UtilValidate.isNotEmpty(maxlengthStr)) {
                    Debug.logError("Could not parse the max-length value of the text element: [" + maxlengthStr + "], setting to null; default of no maxlength will be used", module);
                }
            }

            this.disabled = "true".equals(element.getAttribute("disabled"));

            this.clientAutocompleteField = !"false".equals(element.getAttribute("client-autocomplete-field"));

            Element subHyperlinkElement = UtilXml.firstChildElement(element, "sub-hyperlink");
            if (subHyperlinkElement != null) {
            	this.subHyperlink = new SubHyperlink(subHyperlinkElement, this.getModelFormField());
            }

            //Maps -  attributes added
            validityFieldType = element.getAttribute("validity-field-type");
            validityMaxValue = element.getAttribute("validity-max-value");
            this.setDecimalDigits(element.getAttribute("decimal-digits"));
            
            this.showTooltip = "true".equals(element.getAttribute("show-tooltip"));
            this.setTooltip(element.getAttribute("tooltip"));
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	TextField res = new TextField(modelFormField);
			
			this.cloneFieldInfo(res);
			
			return res;
		}
        
        protected void cloneFieldInfo(TextField res) {
        	res.size = this.size;
			res.maxlength = this.maxlength;
			if (UtilValidate.isNotEmpty(this.defaultValue))
				res.defaultValue = FlexibleStringExpander.getInstance(this.defaultValue.getOriginal());
			else
				res.defaultValue = FlexibleStringExpander.getInstance("");
			res.subHyperlink = this.subHyperlink;
			res.disabled = this.disabled;
			res.clientAutocompleteField = this.clientAutocompleteField;
			res.validityFieldType = this.validityFieldType;
			if (UtilValidate.isNotEmpty(this.decimalDigits))
				res.decimalDigits = FlexibleStringExpander.getInstance(this.decimalDigits.getOriginal());
			else
				res.decimalDigits = FlexibleStringExpander.getInstance("");
			res.validityMaxValue = this.validityMaxValue;
			if (UtilValidate.isNotEmpty(this.readOnly))
				res.readOnly = FlexibleStringExpander.getInstance(this.readOnly.getOriginal());
			else
				res.readOnly = FlexibleStringExpander.getInstance("");
			res.readOnlyCondition = this.readOnlyCondition;
			res.inheritReadOnly = this.inheritReadOnly;
			
			res.showTooltip = this.showTooltip;
            if (UtilValidate.isNotEmpty(this.tooltip)) {
			    res.tooltip = FlexibleStringExpander.getInstance(this.tooltip.getOriginal());
			} else {
			    res.tooltip = FlexibleStringExpander.getInstance("");
			}
        }

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderTextField(writer, context, this);
        }

        public Integer getMaxlength() {
            return maxlength;
        }

        public int getSize() {
            return size;
        }

        public boolean getDisabled() {
            return this.disabled;
        }

        public void setDisabled(boolean b) {
            this.disabled = b;
        }

        public void setReadOnly(String str) {
            this.readOnly = FlexibleStringExpander.getInstance(str);;
        }

        public String getReadOnly(Map<String, Object> context) {
            return (this.readOnly != null && !this.readOnly.isEmpty()) ? this.readOnly.expandString(context) : "";
        }

        public boolean isReadOnly(Map<String, Object> context) {
        	if (!inheritReadOnly) {
	        	String thisReadOnly = getReadOnly(context);
	        	if (UtilValidate.isNotEmpty(thisReadOnly)) {
	        		return this.modelFormField.bshEvalBoolean(context, thisReadOnly, "field/read-only", false);
	        	} else if (UtilValidate.isNotEmpty(this.readOnlyCondition)) {
	        		return this.readOnlyCondition.eval(context);
	        	}
	
	        	return modelFormField.getModelForm().isReadOnly(context);
        	} else {
        		return (UtilValidate.isNotEmpty(this.readOnlyCondition) ? this.readOnlyCondition.eval(context) : false) || this.modelFormField.bshEvalBoolean(context, getReadOnly(context), "field/read-only", false) || modelFormField.getModelForm().isReadOnly(context);
        	}
        }

        public boolean getClientAutocompleteField() {
            return this.clientAutocompleteField;
        }

        public void setClientAutocompleteField(boolean b) {
            this.clientAutocompleteField = b;
        }

        public String getDefaultValue(Map<String, Object> context) {
            if (this.defaultValue != null) {
                return this.defaultValue.expandString(context);
            } else {
                return "";
            }
        }

        public String getValidityFieldType() {
            return validityFieldType;
        }

        public String getValidityMaxValue() {
            return validityMaxValue;
        }

        public void setDecimalDigits(String str) {
            this.decimalDigits = FlexibleStringExpander.getInstance(str);
        }
        
        public String getDecimalDigits(Map<String, Object> context) {
            if (this.decimalDigits != null) {
                return this.decimalDigits.expandString(context);
            } else {
                return "";
            }
        }
		
        /**
         * @param integer
         */
        public void setMaxlength(Integer integer) {
            maxlength = integer;
        }

        /**
         * @param i
         */
        public void setSize(int i) {
            size = i;
        }

        /**
         * @param str
         */
        public void setDefaultValue(String str) {
            this.defaultValue = FlexibleStringExpander.getInstance(str);
        }

        public SubHyperlink getSubHyperlink() {
            return this.subHyperlink;
        }
        public void setSubHyperlink(SubHyperlink newSubHyperlink) {
            this.subHyperlink = newSubHyperlink;
        }
        
        public String getTooltip(Map<String, Object> context) {
            return (this.tooltip != null && !this.tooltip.isEmpty()) ? this.tooltip.expandString(context) : "";
        }

        public void setTooltip(String str) {
            this.tooltip = FlexibleStringExpander.getInstance(str);
        }
        
        public boolean showTooltip() {
            return this.showTooltip;
        }
    }

    public static class TextareaField extends FieldInfo {
        protected int cols = 60;
        protected int rows = 2;
        protected FlexibleStringExpander defaultValue;
        protected boolean visualEditorEnable = false;
        protected FlexibleStringExpander readOnly;
        protected boolean inheritReadOnly = true;
        protected FlexibleStringExpander visualEditorButtons;
        /**
         * MAPS 02/12/2009 Marco Ruocco Inizio
         */
        /** Struttura di condizioni per la verifica dello stato read-only generale della form */
        protected ModelFormCondition readOnlyCondition;
        /** FINE   */

        protected TextareaField() {
            super();
        }

        public TextareaField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.TEXTAREA, modelFormField);
        }

        public TextareaField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.TEXTAREA, modelFormField);
        }

        public TextareaField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.setDefaultValue(element.getAttribute("default-value"));

            visualEditorEnable = "true".equals(element.getAttribute("visual-editor-enable"));
            visualEditorButtons = FlexibleStringExpander.getInstance(element.getAttribute("visual-editor-buttons"));

            String colsStr = element.getAttribute("cols");
            try {
                cols = Integer.parseInt(colsStr);
            } catch (Exception e) {
            	if (UtilValidate.isNotEmpty(colsStr)) {
                    Debug.logError("Could not parse the size value of the text element: [" + colsStr + "], setting to default of " + cols, module);
                }
            }

            String rowsStr = element.getAttribute("rows");
            try {
                rows = Integer.parseInt(rowsStr);
            } catch (Exception e) {
            	if (UtilValidate.isNotEmpty(rowsStr)) {
                    Debug.logError("Could not parse the size value of the text element: [" + rowsStr + "], setting to default of " + rows, module);
                }
            }

            this.setReadOnly(element.getAttribute("read-only"));
            this.inheritReadOnly = "true".equals(element.getAttribute("inherit-read-only"));
            Element readOnlyElement = UtilXml.firstChildElement(element, "read-only");
            if (readOnlyElement != null) {
                this.readOnlyCondition = new ModelFormCondition(readOnlyElement);
            }
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	TextareaField res = new TextareaField(modelFormField);
			
			res.cols = this.cols;
			res.rows = this.rows;
			if (UtilValidate.isNotEmpty(this.defaultValue))
				res.defaultValue = FlexibleStringExpander.getInstance(this.defaultValue.getOriginal());
			else
				res.defaultValue = FlexibleStringExpander.getInstance("");
			res.visualEditorEnable = this.visualEditorEnable;
			if (UtilValidate.isNotEmpty(this.readOnly))
				res.readOnly = FlexibleStringExpander.getInstance(this.readOnly.getOriginal());
			else
				res.readOnly = FlexibleStringExpander.getInstance("");
			res.readOnlyCondition = this.readOnlyCondition;
			res.inheritReadOnly = this.inheritReadOnly;
			if (UtilValidate.isNotEmpty(this.visualEditorButtons))
				res.visualEditorButtons = FlexibleStringExpander.getInstance(this.visualEditorButtons.getOriginal());
			else
				res.visualEditorButtons = FlexibleStringExpander.getInstance("");
			
			return res;
		}

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderTextareaField(writer, context, this);
        }

        public int getCols() {
            return cols;
        }

        public int getRows() {
            return rows;
        }

        public String getDefaultValue(Map<String, Object> context) {
            if (this.defaultValue != null) {
                return this.defaultValue.expandString(context);
            } else {
                return "";
            }
        }

        public boolean getVisualEditorEnable() {
            return this.visualEditorEnable;
        }

        public String getVisualEditorButtons(Map<String, Object> context) {
            return this.visualEditorButtons.expandString(context);
        }

        public void setReadOnly(String str) {
            this.readOnly = FlexibleStringExpander.getInstance(str);;
        }

        public String getReadOnly(Map<String, Object> context) {
            return (this.readOnly != null && !this.readOnly.isEmpty()) ? this.readOnly.expandString(context) : "";
        }

        public boolean isReadOnly(Map<String, Object> context) {
        	if (!inheritReadOnly) {
	        	String thisReadOnly = getReadOnly(context);
	        	if (UtilValidate.isNotEmpty(thisReadOnly)) {
	        		return this.modelFormField.bshEvalBoolean(context, thisReadOnly, "field/read-only", false);
	        	} else if (UtilValidate.isNotEmpty(this.readOnlyCondition)) {
	        		return this.readOnlyCondition.eval(context);
	        	}
	
	        	return modelFormField.getModelForm().isReadOnly(context);
        	} else {
        		return (UtilValidate.isNotEmpty(this.readOnlyCondition) ? this.readOnlyCondition.eval(context) : false) || this.modelFormField.bshEvalBoolean(context, getReadOnly(context), "field/read-only", false) || modelFormField.getModelForm().isReadOnly(context);
        	}
        }

        /**
         * @param i
         */
        public void setCols(int i) {
            cols = i;
        }

        /**
         * @param i
         */
        public void setRows(int i) {
            rows = i;
        }

        /**
         * @param str
         */
        public void setDefaultValue(String str) {
            this.defaultValue = FlexibleStringExpander.getInstance(str);
        }

        /**
         * @param i
         */
        public void setVisualEditorEnable(boolean visualEditorEnable) {
            this.visualEditorEnable = visualEditorEnable;
        }

        /**
         * @param i
         */
        public void setVisualEditorButtons(String eb) {
            this.visualEditorButtons = FlexibleStringExpander.getInstance(eb);
        }
    }

    public static class DateTimeField extends FieldInfo {
        protected String type;
        protected FlexibleStringExpander defaultValue;
        protected String inputMethod;
        protected String clock;
        protected Integer yearRange;
        protected boolean showFormat = false;
        protected FlexibleStringExpander readOnly;
        protected boolean inheritReadOnly = true;
        /**
         * MAPS 02/12/2009 Marco Ruocco Inizio
         */
        /** Struttura di condizioni per la verifica dello stato read-only generale della form */
        protected ModelFormCondition readOnlyCondition;
        /** FINE   */

        protected DateTimeField() {
            super();
        }

        public DateTimeField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.DATE_TIME, modelFormField);
        }

        public DateTimeField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.DATE_TIME, modelFormField);
        }

        public DateTimeField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.setDefaultValue(element.getAttribute("default-value"));
            type = element.getAttribute("type");
            inputMethod = element.getAttribute("input-method");
            clock = element.getAttribute("clock");
            String yearRangeStr = element.getAttribute("year-range");
            if (UtilValidate.isNotEmpty(yearRangeStr)) {
                yearRange = Integer.valueOf(yearRangeStr);
            }
            this.showFormat = "true".equals(element.getAttribute("show-format"));
            this.setReadOnly(element.getAttribute("read-only"));
            this.inheritReadOnly = "true".equals(element.getAttribute("inherit-read-only"));

            Element readOnlyElement = UtilXml.firstChildElement(element, "read-only");
            if (readOnlyElement != null) {
                this.readOnlyCondition = new ModelFormCondition(readOnlyElement);
            }
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	DateTimeField res = new DateTimeField(modelFormField);
			
			res.type = this.type;
			if (UtilValidate.isNotEmpty(this.defaultValue))
				res.defaultValue = FlexibleStringExpander.getInstance(this.defaultValue.getOriginal());
			else
				res.defaultValue = FlexibleStringExpander.getInstance("");
			res.inputMethod = this.inputMethod;
			res.clock = this.clock;
			res.yearRange = this.yearRange;
			res.showFormat = this.showFormat;
			if (UtilValidate.isNotEmpty(this.readOnly))
				res.readOnly = FlexibleStringExpander.getInstance(this.readOnly.getOriginal());
			else
				res.readOnly = FlexibleStringExpander.getInstance("");
			res.readOnlyCondition = this.readOnlyCondition;
			res.inheritReadOnly = this.inheritReadOnly;
			
			return res;
		}

        public void setReadOnly(String str) {
            this.readOnly = FlexibleStringExpander.getInstance(str);;
        }

        public String getReadOnly(Map<String, Object> context) {
            return (this.readOnly != null && !this.readOnly.isEmpty()) ? this.readOnly.expandString(context) : "";
        }

        public boolean isReadOnly(Map<String, Object> context) {
        	if (!inheritReadOnly) {
	        	String thisReadOnly = getReadOnly(context);
	        	if (UtilValidate.isNotEmpty(thisReadOnly)) {
	        		return this.modelFormField.bshEvalBoolean(context, thisReadOnly, "field/read-only", false);
	        	} else if (UtilValidate.isNotEmpty(this.readOnlyCondition)) {
	        		return this.readOnlyCondition.eval(context);
	        	}
	
	        	return modelFormField.getModelForm().isReadOnly(context);
        	} else {
        		return (UtilValidate.isNotEmpty(this.readOnlyCondition) ? this.readOnlyCondition.eval(context) : false) || this.modelFormField.bshEvalBoolean(context, getReadOnly(context), "field/read-only", false) || modelFormField.getModelForm().isReadOnly(context);
        	}
        }

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderDateTimeField(writer, context, this);
        }

        public boolean getShowFormat() {
            return showFormat;
        }

        public String getType() {
            return type;
        }

        public Integer getYearRange() {
            return yearRange;
        }

        public String getDefaultValue(Map<String, Object> context) {
            if (this.defaultValue != null) {
                return this.defaultValue.expandString(context);
            } else {
                return "";
            }
        }

        public String getValue(Map<String, Object> context) {
            return getValue(context, "");
        }

        public String getValue(Map<String, Object> context, String suffix) {
            Locale locale = (Locale) context.get("locale");
            if (locale == null) locale = Locale.getDefault();
            TimeZone timeZone = (TimeZone) context.get("timeZone");
            if (timeZone == null) timeZone = TimeZone.getDefault();

            String res = "";

            try {
                boolean originalEncodingOutput = modelFormField.getEncodeOutput();
                modelFormField.setEncodeOutput(false);
                res = modelFormField.getEntry(context, getDefaultValue(context), suffix);
                modelFormField.setEncodeOutput(originalEncodingOutput);
                if ("timestamp".equals(getType())) {
                    res = UtilDateTime.getFormattedDateTime(res, UtilDateTime.getDateTimeFormat(locale), timeZone, locale);
                } else if ("date".equals(getType())) {
                    res = UtilDateTime.getFormattedDate(res, UtilDateTime.getDateFormat(locale), timeZone, locale);
                } else if ("time".equals(getType())) {
                    res = UtilDateTime.getFormattedTime(res, UtilDateTime.getTimeFormat(locale), timeZone, locale);
                }
            } catch (Exception e) {
//                Debug.logError(e, e.getMessage(), module);
            }
            return res;
        }

        public String getInputMethod() {
            return this.inputMethod;
        }

        public String getClock() {
            return this.clock;
        }

        /**
         * @param string
         */
        public void setShowFormat(boolean bool) {
            showFormat = bool;
        }

        /**
         * @param string
         */
        public void setType(String string) {
            type = string;
        }

        /**
         * @param str
         */
        public void setDefaultValue(String str) {
            this.defaultValue = FlexibleStringExpander.getInstance(str);
        }

        public void setInputMethod(String str) {
            this.inputMethod = str;
        }

        public void setClock(String str) {
            this.clock = str;
        }

        /**
         * Returns the default-value if specified, otherwise the current date, time or timestamp
         *
         * @param context Context Map
         * @return Default value string for date-time
         */
        public String getDefaultDateTimeString(Map<String, Object> context) {
            Locale locale = (Locale) context.get("locale");
            if (locale == null) locale = Locale.getDefault();
            TimeZone timeZone = (TimeZone) context.get("timeZone");
            if (timeZone == null) timeZone = TimeZone.getDefault();

            if (this.defaultValue != null && !this.defaultValue.isEmpty()) {
                return this.getDefaultValue(context);
            }

            if ("date".equals(this.type)) {
                DateFormat df = UtilDateTime.toDateFormat(null, timeZone, locale);
                return df.format(new java.sql.Date(System.currentTimeMillis()));
            } else if ("time".equals(this.type)) {
                DateFormat df = UtilDateTime.toDateFormat(null, timeZone, locale);
                return df.format(new java.sql.Time(System.currentTimeMillis()));
            } else {
                DateFormat df = UtilDateTime.toDateFormat(null, timeZone, locale);
                return df.format(UtilDateTime.nowTimestamp());
            }
        }
    }

    public static class DropDownField extends FieldInfoWithOptions {
        protected boolean allowEmpty = false;
        protected boolean allowMulti = false;
        /*
         * Maps SPA, added drop list
         */
        protected boolean dropList = false;
        protected FlexibleStringExpander dropListDisplayField;
        protected String dropListKeyField = "";
        protected FlexibleStringExpander readOnly;
        protected boolean inheritReadOnly = true;
        protected boolean showTooltip = false;
        protected FlexibleStringExpander tooltip;
        
        /**
         * MAPS 02/12/2009 Marco Ruocco Inizio
         */
        /** Struttura di condizioni per la verifica dello stato read-only generale della form */
        protected ModelFormCondition readOnlyCondition;
        /** FINE   */
        // Maps end

        protected String current;
        protected String size;
        protected String maxLength;
        protected FlexibleStringExpander currentDescription;
        protected SubHyperlink subHyperlink;
        protected int otherFieldSize = 0;
        protected AutoComplete autoComplete;

        protected boolean localAutocompleter = true;

        protected DropDownField() {
            super();
        }

        public DropDownField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.DROP_DOWN, modelFormField);
        }

        public DropDownField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.DROP_DOWN, modelFormField);
        }

        public DropDownField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);

            this.current = element.getAttribute("current");
            this.size = element.getAttribute("size");
            this.maxLength = element.getAttribute("maxlength");
            this.allowEmpty = "true".equals(element.getAttribute("allow-empty"));
            this.allowMulti = "true".equals(element.getAttribute("allow-multiple"));
            this.currentDescription = FlexibleStringExpander.getInstance(element.getAttribute("current-description"));
            //MAPS Spa added drop-list field
            try {
                this.setReadOnly(element.getAttribute("read-only"));
                this.dropList = "drop-list".equals(element.getAttribute("type"));
                this.dropListDisplayField = FlexibleStringExpander.getInstance(element.getAttribute("drop-list-display-field"));
                this.dropListKeyField = element.getAttribute("drop-list-key-field");
            } catch(Exception e) { }

            // set the default size
            if (size == null) {
                size = "1";
            }
            
            this.showTooltip = "true".equals(element.getAttribute("show-tooltip"));
            this.setTooltip(element.getAttribute("tooltip"));
            
            String sizeStr = element.getAttribute("other-field-size");
            try {
                this.otherFieldSize = Integer.parseInt(sizeStr);
            } catch (Exception e) {
            	if (UtilValidate.isNotEmpty(sizeStr)) {
                    Debug.logError("Could not parse the size value of the text element: [" + sizeStr + "], setting to the default of " + this.otherFieldSize, module);
                }
            }

            this.localAutocompleter = UtilXml.checkBoolean(element.getAttribute("local-autocompleter"), true);

            Element subHyperlinkElement = UtilXml.firstChildElement(element, "sub-hyperlink");
            if (subHyperlinkElement != null) {
            	this.subHyperlink = new SubHyperlink(subHyperlinkElement, this.getModelFormField());
            }

            Element autoCompleteElement = UtilXml.firstChildElement(element, "auto-complete");
            if (autoCompleteElement != null) {
                this.autoComplete = new AutoComplete(autoCompleteElement);
            } else if (this.isDropList()) {
                this.autoComplete = new AutoComplete();
            }

            this.setReadOnly(element.getAttribute("read-only"));
            Element readOnlyElement = UtilXml.firstChildElement(element, "read-only");
            if (readOnlyElement != null) {
                this.readOnlyCondition = new ModelFormCondition(readOnlyElement);
            }
            this.inheritReadOnly = "true".equals(element.getAttribute("inherit-read-only"));
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	DropDownField res = new DropDownField(modelFormField);
			
			res.allowEmpty = this.allowEmpty;
			res.allowMulti = this.allowMulti;
			res.dropList = this.dropList;
			res.dropListDisplayField = this.dropListDisplayField;
			res.dropListKeyField = this.dropListKeyField;
			if (UtilValidate.isNotEmpty(this.readOnly))
				res.readOnly = FlexibleStringExpander.getInstance(this.readOnly.getOriginal());
			else
				res.readOnly = FlexibleStringExpander.getInstance("");
			res.inheritReadOnly = this.inheritReadOnly;
			res.readOnlyCondition = this.readOnlyCondition;
			res.current = this.current;
			res.size = this.size;
			res.maxLength = this.maxLength;
			if (UtilValidate.isNotEmpty(this.currentDescription))
				res.currentDescription = FlexibleStringExpander.getInstance(this.currentDescription.getOriginal());
			else
				res.currentDescription = FlexibleStringExpander.getInstance("");
			res.subHyperlink = this.subHyperlink;
			res.otherFieldSize = this.otherFieldSize;
			res.autoComplete = this.autoComplete;
			
			res.showTooltip = this.showTooltip;
			if (UtilValidate.isNotEmpty(this.tooltip))
				res.tooltip = FlexibleStringExpander.getInstance(this.tooltip.getOriginal());
			else
				res.tooltip = FlexibleStringExpander.getInstance("");
			
			// Sandro bug: 2836
			res.localAutocompleter = this.localAutocompleter;
			
			super.cloneFieldInfo(res);
			
			return res;
		}

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderDropDownField(writer, context, this);
        }

        public void setReadOnly(String str) {
            this.readOnly = FlexibleStringExpander.getInstance(str);;
        }

        public String getReadOnly(Map<String, Object> context) {
            return (this.readOnly != null && !this.readOnly.isEmpty()) ? this.readOnly.expandString(context) : "";
        }

        public boolean isReadOnly(Map<String, Object> context) {
        	if (!inheritReadOnly) {
	        	String thisReadOnly = getReadOnly(context);
	        	if (UtilValidate.isNotEmpty(thisReadOnly)) {
	        		return this.modelFormField.bshEvalBoolean(context, thisReadOnly, "field/read-only", false);
	        	} else if (UtilValidate.isNotEmpty(this.readOnlyCondition)) {
	        		return this.readOnlyCondition.eval(context);
	        	}
	
	        	return modelFormField.getModelForm().isReadOnly(context);
        	} else {
        		return (UtilValidate.isNotEmpty(this.readOnlyCondition) ? this.readOnlyCondition.eval(context) : false) || this.modelFormField.bshEvalBoolean(context, getReadOnly(context), "field/read-only", false) || modelFormField.getModelForm().isReadOnly(context);
        	}
        }

        public boolean isAllowEmpty() {
            return this.allowEmpty;
        }

        public boolean isAllowMultiple() {
            return this.allowMulti;
        }

        public boolean isDropList() {
            return dropList;
        }

        public boolean isLocalAutocompleter() {
            return localAutocompleter;
        }

        public String getDropListDisplayField(Map<String, Object> context) {
            return this.dropListDisplayField.expandString(context);
        }

        public String getDropListKeyField() {
            return dropListKeyField;
        }

        public String getCurrent() {
            if (UtilValidate.isEmpty(this.current)) {
                return "first-in-list";
            } else {
                return this.current;
            }
        }

        public String getCurrentDescription(Map<String, Object> context) {
            if (this.currentDescription == null)
                return null;
            else
                return this.currentDescription.expandString(context);
        }

        public void setAllowEmpty(boolean b) {
            this.allowEmpty = b;
        }

        public void setCurrent(String string) {
            this.current = string;
        }

        public void setCurrentDescription(String string) {
            this.currentDescription = FlexibleStringExpander.getInstance(string);
        }

        public SubHyperlink getSubHyperlink() {
            return this.subHyperlink;
        }
        public void setSubHyperlink(SubHyperlink newSubHyperlink) {
            this.subHyperlink = newSubHyperlink;
        }

        public AutoComplete getAutoComplete() {
            return this.autoComplete;
        }

        public int getOtherFieldSize() {
            return this.otherFieldSize;
        }

        public String getSize() {
            return this.size;
        }

        public String getMaxLength() {
            return this.maxLength;
        }
        
        public void setShowTootltip(boolean showtooltip) {
        	this.showTooltip = showTooltip;
        }
        
        public boolean showTooltip() {
        	return this.showTooltip;
        }

        /**
         * Get the name to use for the parameter for this field in the form interpreter.
         * For HTML forms this is the request parameter name.
         *
         * @return
         */
        public String getParameterNameOther(Map<String, Object> context) {
            String baseName;
            if (UtilValidate.isNotEmpty(this.modelFormField.parameterName)) {
                baseName = this.modelFormField.parameterName;
            } else {
                baseName = this.modelFormField.name;
            }

            baseName += "_OTHER";
            Integer itemIndex = (Integer) context.get("itemIndex");
            if (itemIndex != null && "multi".equals(this.modelFormField.modelForm.getType())) {
                return baseName + this.modelFormField.modelForm.getItemIndexSeparator() + itemIndex.intValue();
            } else {
                return baseName;
            }
        }

        public String getTooltip(Map<String, Object> context) {
			return (this.tooltip != null && !this.tooltip.isEmpty()) ? this.tooltip.expandString(context) : "";
		}

		public void setTooltip(String str) {
			this.tooltip = FlexibleStringExpander.getInstance(str);
		}
    }

    public static class RadioField extends FieldInfoWithOptions {
        protected RadioField() {
            super();
        }

        public RadioField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.RADIO, modelFormField);
        }

        public RadioField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.RADIO, modelFormField);
        }

        public RadioField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	RadioField res = new RadioField(modelFormField);
        	
        	super.cloneFieldInfo(res);
			
			return res;
		}

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderRadioField(writer, context, this);
        }
    }

    public static class CheckField extends FieldInfoWithOptions {
        public final static String ROW_SUBMIT_FIELD_NAME = "_rowSubmit";
        protected FlexibleStringExpander allChecked = null;

        protected CheckField() {
            super();
        }

        public CheckField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.CHECK, modelFormField);
        }

        public CheckField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.CHECK, modelFormField);
        }

        public CheckField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            allChecked = FlexibleStringExpander.getInstance(element.getAttribute("all-checked"));
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	CheckField res = new CheckField(modelFormField);
			
        	if (UtilValidate.isNotEmpty(this.allChecked))
        		res.allChecked = FlexibleStringExpander.getInstance(this.allChecked.getOriginal());
        	else
        		res.allChecked = FlexibleStringExpander.getInstance("");
        	
        	super.cloneFieldInfo(res);
        	
			return res;
		}

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderCheckField(writer, context, this);
        }

        public Boolean isAllChecked(Map<String, Object> context) {
            String allCheckedStr = this.allChecked.expandString(context);
            if (UtilValidate.isNotEmpty(allCheckedStr)) {
                return Boolean.valueOf("true".equals(allCheckedStr));
            } else {
                return null;
            }
        }
    }

    public static class SubmitField extends FieldInfo {
        protected String buttonType;
        protected String imageLocation;
        protected FlexibleStringExpander backgroundSubmitRefreshTargetExdr;
        protected boolean requestConfirmation = false;
        protected FlexibleStringExpander confirmationMsgExdr;

        protected SubmitField() {
            super();
        }

        public SubmitField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.SUBMIT, modelFormField);
        }

        public SubmitField(int fieldInfo, ModelFormField modelFormField) {
            super(fieldInfo, FieldInfo.SUBMIT, modelFormField);
        }

        public SubmitField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.buttonType = element.getAttribute("button-type");
            this.imageLocation = element.getAttribute("image-location");
            this.backgroundSubmitRefreshTargetExdr = FlexibleStringExpander.getInstance(element.getAttribute("background-submit-refresh-target"));
            setRequestConfirmation("true".equals(element.getAttribute("request-confirmation")));
            setConfirmationMsg(element.getAttribute("confirmation-message"));
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	SubmitField res = new SubmitField(modelFormField);
			
			res.buttonType = this.buttonType;
			res.imageLocation = this.imageLocation;
			if (UtilValidate.isNotEmpty(this.backgroundSubmitRefreshTargetExdr))
				res.backgroundSubmitRefreshTargetExdr = FlexibleStringExpander.getInstance(this.backgroundSubmitRefreshTargetExdr.getOriginal());
			else
				res.backgroundSubmitRefreshTargetExdr = FlexibleStringExpander.getInstance("");
			res.requestConfirmation = this.requestConfirmation;
			if (UtilValidate.isNotEmpty(this.confirmationMsgExdr))
				res.confirmationMsgExdr = FlexibleStringExpander.getInstance(this.confirmationMsgExdr.getOriginal());
			else
				res.confirmationMsgExdr = FlexibleStringExpander.getInstance("");
			
			return res;
		}

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderSubmitField(writer, context, this);
        }

        public String getButtonType() {
            return buttonType;
        }

        public String getImageLocation() {
            return imageLocation;
        }

        public boolean getRequestConfirmation() {
        	return this.requestConfirmation;
        }

        public String getConfirmationMsg(Map<String, Object> context) {
        	return this.confirmationMsgExdr.expandString(context);
        }

        public String getConfirmation(Map<String, Object> context) {
        	String message = getConfirmationMsg(context);
        	if (UtilValidate.isNotEmpty(message)) {
        		return message;
        	}
        	else if (getRequestConfirmation()) {
        		String defaultMessage = UtilProperties.getPropertyValue("general", "default.confirmation.message", "${uiLabelMap.CommonConfirm}");
        		setConfirmationMsg(defaultMessage);
        		return getConfirmationMsg(context);
        	}
        	return "";
        }

        /**
         * @param string
         */
        public void setButtonType(String string) {
            buttonType = string;
        }

        /**
         * @param string
         */
        public void setImageLocation(String string) {
            imageLocation = string;
        }

        public String getBackgroundSubmitRefreshTarget(Map<String, Object> context) {
            return this.backgroundSubmitRefreshTargetExdr.expandString(context);
        }
        
        public void setRequestConfirmation(boolean val) {
        	this.requestConfirmation = val;
        }

        public void setConfirmationMsg(String val) {
        	this.confirmationMsgExdr = FlexibleStringExpander.getInstance(val);
        }
    }

    public static class ResetField extends FieldInfo {
        protected ResetField() {
            super();
        }

        public ResetField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.RESET, modelFormField);
        }

        public ResetField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.RESET, modelFormField);
        }

        public ResetField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	ResetField res = new ResetField(modelFormField);
			
			return res;
		}

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderResetField(writer, context, this);
        }
    }

    public static class HiddenField extends FieldInfo {
        protected FlexibleStringExpander value;

        protected HiddenField() {
            super();
        }

        public HiddenField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.HIDDEN, modelFormField);
        }

        public HiddenField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.HIDDEN, modelFormField);
        }

        public HiddenField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.setValue(element.getAttribute("value"));
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	HiddenField res = new HiddenField(modelFormField);
			
        	if (UtilValidate.isNotEmpty(this.value))
        		res.value = FlexibleStringExpander.getInstance(this.value.getOriginal());
        	else
        		res.value = FlexibleStringExpander.getInstance("");
			
			return res;
		}

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderHiddenField(writer, context, this);
        }

        public String getValue(Map<String, Object> context) {
        	if (this.value != null && !this.value.isEmpty()) {
        		String valueEnc = this.value.expandString(context);
        		StringUtil.SimpleEncoder simpleEncoder = (StringUtil.SimpleEncoder) context.get("simpleEncoder");
        		if (simpleEncoder != null) {
        			valueEnc = simpleEncoder.encode(valueEnc);
        		}
        		return valueEnc;
        	} else {
        		return modelFormField.getEntry(context);
        	}
        }

        public void setValue(String string) {
            this.value = FlexibleStringExpander.getInstance(string);
        }
    }

    public static class IgnoredField extends FieldInfo {
        protected IgnoredField() {
            super();
        }

        public IgnoredField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.IGNORED, modelFormField);
        }

        public IgnoredField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.IGNORED, modelFormField);
        }

        public IgnoredField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	IgnoredField res = new IgnoredField(modelFormField);
			
			return res;
		}

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderIgnoredField(writer, context, this);
        }
    }

    public static class TextFindField extends FieldInfoWithOptions {
        //Part form Text component
        protected int size = 25;
        protected Integer maxlength;
        protected FlexibleStringExpander defaultValue;
        protected SubHyperlink subHyperlink;
        protected boolean disabled;
        protected boolean clientAutocompleteField;
        ////////////////////////////////

        protected boolean ignoreCase = true;
        protected boolean hideIgnoreCase = false;
        protected String defaultOption = "like";
        protected boolean hideOptions = false;
        protected boolean rangeComponent = false;
        protected String type = "text-find";
        protected AutoComplete autoComplete;

        //Part from DropDown
        protected boolean localAutocompleter = true;
        protected FlexibleStringExpander dropListDisplayField;
        protected String dropListKeyField = "";

        protected TextFindField() {
            super();
        }

        public TextFindField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.TEXT, modelFormField);
        }

        public TextFindField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.TEXT, modelFormField);
        }

        public TextFindField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);

            this.setDefaultValue(element.getAttribute("default-value"));

            String sizeStr = element.getAttribute("size");
            try {
                size = Integer.parseInt(sizeStr);
            } catch (Exception e) {
                if (sizeStr != null && sizeStr.length() > 0) {
                    Debug.logError("Could not parse the size value of the text element: [" + sizeStr + "], setting to the default of " + size, module);
                }
            }

            String maxlengthStr = element.getAttribute("maxlength");
            try {
                maxlength = Integer.valueOf(maxlengthStr);
            } catch (Exception e) {
                maxlength = null;
                if (maxlengthStr != null && maxlengthStr.length() > 0) {
                    Debug.logError("Could not parse the max-length value of the text element: [" + maxlengthStr + "], setting to null; default of no maxlength will be used", module);
                }
            }

            this.disabled = "true".equals(element.getAttribute("disabled"));

            this.clientAutocompleteField = !"false".equals(element.getAttribute("client-autocomplete-field"));

            Element subHyperlinkElement = UtilXml.firstChildElement(element, "sub-hyperlink");
            if (subHyperlinkElement != null) {
                this.subHyperlink = new SubHyperlink(subHyperlinkElement, this.getModelFormField());
            }

            this.ignoreCase = "true".equals(element.getAttribute("ignore-case"));
            this.hideIgnoreCase = "true".equals(element.getAttribute("hide-options")) ||
            	"ignore-case".equals(element.getAttribute("hide-options")) ? true : false;
            if(element.hasAttribute("default-option")) {
            	this.defaultOption = element.getAttribute("default-option");
            } else {
            	this.defaultOption = UtilProperties.getPropertyValue("widget", "widget.form.defaultTextFindOption", "like");
            }
            this.hideOptions = "true".equals(element.getAttribute("hide-options")) ||
                "options".equals(element.getAttribute("hide-options")) ? true : false;
            this.rangeComponent = "true".equals(element.getAttribute("range-component"));
            this.type = element.getAttribute("type");

            this.dropListDisplayField = FlexibleStringExpander.getInstance(element.getAttribute("drop-list-display-field"));
            this.dropListKeyField = element.getAttribute("drop-list-key-field");

            Element autoCompleteElement = UtilXml.firstChildElement(element, "auto-complete");
            if (autoCompleteElement != null) {
                this.autoComplete = new AutoComplete(autoCompleteElement);
            } else if (this.isDropListComponent()) {
                this.autoComplete = new AutoComplete();
            }

            this.localAutocompleter = UtilXml.checkBoolean(element.getAttribute("local-autocompleter"), true);
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	TextFindField res = new TextFindField(modelFormField);
			
        	res.size = this.size;
        	res.maxlength = this.maxlength;
        	if (UtilValidate.isNotEmpty(this.defaultValue))
        		res.defaultValue = FlexibleStringExpander.getInstance(this.defaultValue.getOriginal());
        	else
        		res.defaultValue = FlexibleStringExpander.getInstance("");
        	res.subHyperlink = this.subHyperlink;
        	res.disabled = this.disabled;
        	res.clientAutocompleteField = this.clientAutocompleteField;
        	res.ignoreCase = this.ignoreCase;
        	res.hideIgnoreCase = this.hideIgnoreCase;
        	res.defaultOption = this.defaultOption;
        	res.hideOptions = this.hideOptions;
        	res.rangeComponent = this.rangeComponent;
        	res.type = this.type;
        	res.autoComplete = this.autoComplete;
        	res.localAutocompleter = this.localAutocompleter;
			res.dropListDisplayField = this.dropListDisplayField;
			res.dropListKeyField = this.dropListKeyField;
			
			super.cloneFieldInfo(res);
			
			return res;
		}

        public String getParameterName(Map<String, Object> context) {
            return getParameterName(context, 0);
        }

        public String getParameterName(Map<String, Object> context, int index) {
            String res = null;
            res = modelFormField.getParameterName(context, true);
            res += "_fld" + index + "_value";

            return res;
        }

        public Integer getMaxlength() {
            return maxlength;
        }

        public int getSize() {
            return size;
        }

        public boolean getDisabled() {
            return this.disabled;
        }

        public void setDisabled(boolean b) {
            this.disabled = b;
        }

        public boolean getClientAutocompleteField() {
            return this.clientAutocompleteField;
        }

        public void setClientAutocompleteField(boolean b) {
            this.clientAutocompleteField = b;
        }

        public String getDefaultValue(Map<String, Object> context) {
            if (this.defaultValue != null) {
                return this.defaultValue.expandString(context);
            } else {
                return "";
            }
        }

        /**
         * @param integer
         */
        public void setMaxlength(Integer integer) {
            maxlength = integer;
        }

        /**
         * @param i
         */
        public void setSize(int i) {
            size = i;
        }

        /**
         * @param str
         */
        public void setDefaultValue(String str) {
            this.defaultValue = FlexibleStringExpander.getInstance(str);
        }

        public SubHyperlink getSubHyperlink() {
            return this.subHyperlink;
        }
        public void setSubHyperlink(SubHyperlink newSubHyperlink) {
            this.subHyperlink = newSubHyperlink;
        }

        public boolean isTextFindComponent() {
            return "text-find".equals(this.type);
        }

        public boolean isSingleTextComponent() {
            return "single-text".equals(this.type);
        }

        public boolean isDropListComponent() {
            return "drop-list".equals(this.type);
        }

        public boolean getIgnoreCase() {
            return this.ignoreCase;
        }

        public String getDefaultOption() {
            return this.defaultOption;
        }

        public boolean getHideIgnoreCase() {
            return this.hideIgnoreCase;
        }

        public boolean getHideOptions() {
            return this.hideOptions;
        }

        public String getType() {
            return this.type;
        }

        public AutoComplete getAutoComplete() {
            return this.autoComplete;
        }

        public void setAutoComplete(AutoComplete newAutoComplete) {
            this.autoComplete = newAutoComplete;
        }

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderTextFindField(writer, context, this);
        }

        public boolean isLocalAutocompleter() {
            return localAutocompleter;
        }

        public String getDropListDisplayField(Map<String, Object> context) {
            return dropListDisplayField.expandString(context);
        }

        public String getDropListKeyField() {
            return dropListKeyField;
        }
    }

    public static class DateFindField extends DateTimeField {
        protected String defaultOptionFrom = "greaterThanEqualTo";
        protected String defaultOptionThru = "lessThanEqualTo";
        protected boolean rangeComponent = false;
        protected boolean singleOption = false;

        public DateFindField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.defaultOptionFrom = element.getAttribute("default-option-from");
            this.defaultOptionThru = element.getAttribute("default-option-thru");
            this.rangeComponent = "true".equals(element.getAttribute("range-component"));
            this.singleOption = "true".equals(element.getAttribute("single-option"));
        }

        public DateFindField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, modelFormField);
        }

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderDateFindField(writer, context, this);
        }

        public String getParameterName(Map<String, Object> context) {
            return getParameterName(context, 0);
        }

        public String getParameterName(Map<String, Object> context, int index) {
            String res = null;
            res = modelFormField.getParameterName(context, true);
            res += "_fld" + index + "_value";

            return res;
        }

        public String getDefaultOptionFrom() {
            return this.defaultOptionFrom;
        }

        public String getDefaultOptionThru() {
            return this.defaultOptionThru;
        }

        public boolean getRangeComponent() {
            return this.rangeComponent;
        }

        public boolean getSingleOption() {
            return this.singleOption;
        }
    }

    public static class RangeFindField extends TextField {
        protected String defaultOptionFrom = "greaterThanEqualTo";
        protected String defaultOptionThru = "lessThanEqualTo";
        
        public RangeFindField(ModelFormField modelFormField) {
        	super(modelFormField);
        	this.fieldType = FieldInfo.RANGEQBE;
        }

        public RangeFindField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.fieldType = FieldInfo.RANGEQBE;
            this.defaultOptionFrom = element.getAttribute("default-option-from");
            this.defaultOptionThru = element.getAttribute("default-option-thru");
        }

        public RangeFindField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, modelFormField);
            this.fieldType = FieldInfo.RANGEQBE;
        }

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderRangeFindField(writer, context, this);
        }

        public String getDefaultOptionFrom() {
            return this.defaultOptionFrom;
        }

        public String getDefaultOptionThru() {
            return this.defaultOptionThru;
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	RangeFindField res = new RangeFindField(modelFormField);
        	
        	super.cloneFieldInfo(res);
        	
        	res.defaultOptionFrom = this.defaultOptionFrom;
        	res.defaultOptionThru = this.defaultOptionThru;
        	
        	return res;
        }
    }

    public static class LookupField extends FieldInfoWithOptions {
      //Part form Text component
        protected int size = 25;
        protected Integer maxlength;
        protected FlexibleStringExpander defaultValue;
        protected boolean disabled;
        protected boolean clientAutocompleteField;
        ////////////////////////////////

        protected FlexibleStringExpander formName;
        protected String descriptionFieldName;
        protected FlexibleStringExpander targetParameterExdr;
        protected SubHyperlink subHyperlink;
        protected String lookupPresentation;
        protected String lookupWidth;
        protected String lookupHeight;
        protected String lookupPosition;
        protected String fadeBackground;
        //Maps spa - attributes added
        protected FlexibleStringExpander targetExdr;
        protected AutoComplete autoComplete;
//        protected List<AutoCompleteConstraint> autoCompleteConstraintList = FastList.newInstance();
        protected String keyFieldName = "";
        protected boolean modalLookup = false;
        protected boolean lookupAutocomplete = true;
        protected FlexibleStringExpander readOnly;
        protected boolean inheritReadOnly = true;
        protected boolean showEditField = true;
        protected String descriptionFieldSize;
        /**
         * MAPS 02/12/2009 Marco Ruocco Inizio
         */
        /** Struttura di condizioni per la verifica dello stato read-only generale della form */
        protected ModelFormCondition readOnlyCondition;
        protected String editFieldName = "";
        protected boolean showTooltip = false;
        protected FlexibleStringExpander tooltip;
        /** FINE   */

        // MAPS Specifica una lookup particolare che obbliga l'inserimento di un valore diverso da quelli esistenti
        protected boolean changeValueMandatory = false;

        protected LookupField() {
            super();
        }

        public LookupField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.TEXT, modelFormField);
        }

        public LookupField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.TEXT, modelFormField);
        }

        public LookupField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.formName = FlexibleStringExpander.getInstance(element.getAttribute("target-form-name"));
            this.descriptionFieldName = element.getAttribute("description-field-name");
            this.keyFieldName = element.getAttribute("key-field-name");
            this.editFieldName = element.getAttribute("edit-field-name");
            this.targetParameterExdr = FlexibleStringExpander.getInstance(element.getAttribute("target-parameter"));
            this.lookupPresentation = element.getAttribute("presentation");
            this.lookupHeight = element.getAttribute("height");
            this.lookupWidth = element.getAttribute("width");
            this.lookupPosition = element.getAttribute("position");
            this.fadeBackground = element.getAttribute("fade-background");
            this.targetExdr = FlexibleStringExpander.getInstance(element.getAttribute("target"));
            this.defaultValue = FlexibleStringExpander.getInstance(element.getAttribute("default-value"));
            
            if(UtilValidate.isNotEmpty(element.getAttribute("size"))){
            	this.size = Integer.parseInt(element.getAttribute("size"));
            }
            
            
            if (UtilValidate.isNotEmpty(this.editFieldName)) {
                // Add editFieldName as selected field if not found.
                if (UtilValidate.isNotEmpty(this.optionSources)) {
                    for (OptionSource optionSource: this.optionSources) {
                        if (optionSource instanceof EntityOptions) {
                            EntityOptions entityOptions = (EntityOptions)optionSource;
                            boolean found = false;
                            for (FlexibleStringExpander fse: entityOptions.selectFieldList) {
                                if (this.editFieldName.equals(fse.getOriginal())) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                entityOptions.selectFieldList.add(FlexibleStringExpander.getInstance(this.editFieldName));
                            }
                        }
                    }
                }
            }

            //MAPS Spa added attributes
            this.changeValueMandatory = UtilXml.checkBoolean(element.getAttribute("change-value-mandatory"));
            try {
                this.setReadOnly(element.getAttribute("read-only"));
                this.modalLookup = "true".equals(element.getAttribute("modal-lookup")) ? true : false ;
                if (modalLookup) {
                    Element autoCompleteElement = UtilXml.firstChildElement(element, "auto-complete");
                    if (autoCompleteElement != null) {
                        this.autoComplete = new AutoComplete(autoCompleteElement);
//                        for (Element constraintElement:  UtilXml.childElementList(autoCompleteElement, "entity-constraint")) {
//                            autoCompleteConstraintList.add(new AutoCompleteConstraint(constraintElement));
//                        }
                    } else {
                        this.autoComplete = new AutoComplete();
                    }
//                    if (autoCompleteElement.hasAttribute("entity-key-field")) {
//                        entityKeyField = autoCompleteElement.getAttribute("entity-key-field");
//                    }
                }
                this.lookupAutocomplete = UtilXml.checkBoolean(element.getAttribute("lookup-autocomplete"), true);
            } catch(Exception e) { }
            
            this.showEditField = !"false".equals(element.getAttribute("show-edit-field"));
            this.descriptionFieldSize = element.getAttribute("description-field-size");

            Element subHyperlinkElement = UtilXml.firstChildElement(element, "sub-hyperlink");
            if (subHyperlinkElement != null) {
            	this.subHyperlink = new SubHyperlink(subHyperlinkElement, this.getModelFormField());
            }

            this.setReadOnly(element.getAttribute("read-only"));
            this.inheritReadOnly = "true".equals(element.getAttribute("inherit-read-only"));
            Element readOnlyElement = UtilXml.firstChildElement(element, "read-only");
            if (readOnlyElement != null) {
                this.readOnlyCondition = new ModelFormCondition(readOnlyElement);
            }
            this.showTooltip = "true".equals(element.getAttribute("show-tooltip"));
            this.setTooltip(element.getAttribute("tooltip"));
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	LookupField res = new LookupField(modelFormField);
			
        	res.size = this.size;
        	res.maxlength = this.maxlength;
        	if (UtilValidate.isNotEmpty(this.defaultValue))
        		res.defaultValue = FlexibleStringExpander.getInstance(this.defaultValue.getOriginal());
        	else
        		res.defaultValue = FlexibleStringExpander.getInstance("");
        	res.disabled = this.disabled;
        	res.clientAutocompleteField = this.clientAutocompleteField;
        	if (UtilValidate.isNotEmpty(this.formName))
        		res.formName = FlexibleStringExpander.getInstance(this.formName.getOriginal());
        	else
        		res.formName = FlexibleStringExpander.getInstance("");
        	res.descriptionFieldName = this.descriptionFieldName;
        	if (UtilValidate.isNotEmpty(this.targetParameterExdr))
        		res.targetParameterExdr = FlexibleStringExpander.getInstance(this.targetParameterExdr.getOriginal());
        	else
        		res.targetParameterExdr = FlexibleStringExpander.getInstance("");
        	res.subHyperlink = this.subHyperlink;
        	if (UtilValidate.isNotEmpty(this.targetExdr))
        		res.targetExdr = FlexibleStringExpander.getInstance(this.targetExdr.getOriginal());
        	else
        		res.targetExdr = FlexibleStringExpander.getInstance("");
        	res.autoComplete = this.autoComplete;
        	res.keyFieldName = this.keyFieldName;
        	res.modalLookup = this.modalLookup;
        	res.lookupAutocomplete = this.lookupAutocomplete;
        	if (UtilValidate.isNotEmpty(this.readOnly))
        		res.readOnly = FlexibleStringExpander.getInstance(this.readOnly.getOriginal());
        	else
        		res.readOnly = FlexibleStringExpander.getInstance("");
        	res.readOnlyCondition = this.readOnlyCondition;
			res.inheritReadOnly = this.inheritReadOnly;
        	res.editFieldName = this.editFieldName;
        	res.changeValueMandatory = this.changeValueMandatory;
        	res.lookupPresentation = this.lookupPresentation;
        	res.lookupHeight = this.lookupHeight;
        	res.lookupWidth = this.lookupWidth;
        	res.lookupPosition = this.lookupPosition;
        	res.fadeBackground = this.fadeBackground;
        	res.showEditField = this.showEditField;
        	res.descriptionFieldSize = this.descriptionFieldSize;
        	
        	res.showTooltip = this.showTooltip;
        	if (UtilValidate.isNotEmpty(this.tooltip))
        		res.tooltip = FlexibleStringExpander.getInstance(this.tooltip.getOriginal());
        	else
        		res.tooltip = FlexibleStringExpander.getInstance("");
        	super.cloneFieldInfo(res);
			
			return res;
		}

        public void setReadOnly(String str) {
            this.readOnly = FlexibleStringExpander.getInstance(str);;
        }

        public String getReadOnly(Map<String, Object> context) {
            return (this.readOnly != null && !this.readOnly.isEmpty()) ? this.readOnly.expandString(context) : "";
        }

        public boolean isReadOnly(Map<String, Object> context) {
        	if (!inheritReadOnly) {
	        	String thisReadOnly = getReadOnly(context);
	        	if (UtilValidate.isNotEmpty(thisReadOnly)) {
	        		return this.modelFormField.bshEvalBoolean(context, thisReadOnly, "field/read-only", false);
	        	} else if (UtilValidate.isNotEmpty(this.readOnlyCondition)) {
	        		return this.readOnlyCondition.eval(context);
	        	}
	
	        	return modelFormField.getModelForm().isReadOnly(context);
        	} else {
        		return (UtilValidate.isNotEmpty(this.readOnlyCondition) ? this.readOnlyCondition.eval(context) : false) || this.modelFormField.bshEvalBoolean(context, getReadOnly(context), "field/read-only", false) || modelFormField.getModelForm().isReadOnly(context);
        	}
        }
        

        public String getTooltip(Map<String, Object> context) {
			return (this.tooltip != null && !this.tooltip.isEmpty()) ? this.tooltip.expandString(context) : "";
		}

		public void setTooltip(String str) {
			this.tooltip = FlexibleStringExpander.getInstance(str);
		}

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderLookupField(writer, context, this);
        }

        public Integer getMaxlength() {
            return maxlength;
        }

        public int getSize() {
            return size;
        }

        public boolean getDisabled() {
            return this.disabled;
        }

        public void setDisabled(boolean b) {
            this.disabled = b;
        }

        public boolean getClientAutocompleteField() {
            return this.clientAutocompleteField;
        }

        public void setClientAutocompleteField(boolean b) {
            this.clientAutocompleteField = b;
        }

        public String getDefaultValue(Map<String, Object> context) {
            if (this.defaultValue != null) {
                return this.defaultValue.expandString(context);
            } else {
                return "";
            }
        }

        /**
         * @param integer
         */
        public void setMaxlength(Integer integer) {
            maxlength = integer;
        }

        /**
         * @param i
         */
        public void setSize(int i) {
            size = i;
        }

        /**
         * @param str
         */
        public void setDefaultValue(String str) {
            this.defaultValue = FlexibleStringExpander.getInstance(str);
        }

        public String getFormName(Map<String, Object> context) {
            return this.formName.expandString(context);
        }

        public String getTarget(Map<String, Object> context) {
            return this.targetExdr.expandString(context);
        }

        public List<String> getTargetParameterList(Map<String, Object> context) {
            List<String> paramList = FastList.newInstance();

            String targetParameter = this.targetParameterExdr.expandString(context);

            if (UtilValidate.isNotEmpty(targetParameter)) {
                StringTokenizer stk = new StringTokenizer(targetParameter, ", ");
                while (stk.hasMoreTokens()) {
                    paramList.add(stk.nextToken());
                }
            }
            return paramList;
        }

        public void setFormName(String str) {
            this.formName = FlexibleStringExpander.getInstance(str);
        }

        public String getDescriptionFieldName() {
            return this.descriptionFieldName;
        }

        public void setDescriptionFieldName(String str) {
            this.descriptionFieldName = str;
        }

        public boolean isChangeValueMandatory() {
            return this.changeValueMandatory;
        }

        public SubHyperlink getSubHyperlink() {
        	return this.subHyperlink;
        }

        public String getLookupPresentation() {
        	return this.lookupPresentation;
        }

        public void setLookupPresentation(String str) {
        	this.lookupPresentation = str;
        }

        public String getLookupWidth() {
        	return this.lookupWidth;
        }

        public void setLookupWidth(String str) {
        	this.lookupWidth = str;
        }

        public String getLookupHeight() {
        	return this.lookupHeight;
        }

        public void setLookupHeight(String str) {
        	this.lookupHeight = str;
        }

        public String getLookupPosition() {
        	return this.lookupPosition;
        }

        public void setLookupPosition(String str) {
        	this.lookupPosition = str;
        }

        public String getFadeBackground() {
        	return this.fadeBackground;
        }

        public void setFadeBackground(String str) {
        	this.fadeBackground = str;
        }

        public AutoComplete getAutoComplete() {
        	return autoComplete;
        }

//        public List<AutoCompleteConstraint> getAutoCompleteConstraint() {
//            return autoCompleteConstraintList;
//        }

        public boolean isModalLookup() {
            return modalLookup;
        }

        public boolean isLookupAutocomplete() {
            return lookupAutocomplete;
        }

        public String getKeyFieldName() {
            return keyFieldName;
        }

        public String getEditFieldName() {
            return editFieldName;
        }
        
        public boolean getShowEditField() {
        	return showEditField;
        }
        
        public void setShowEditField(boolean showEditField) {
        	this.showEditField = showEditField;
        }
        
        public String getDescriptionFieldSize() {
        	return descriptionFieldSize;
        }
        
        public void setDescriptionFieldSize(String descriptionFieldSize) {
        	this.descriptionFieldSize = descriptionFieldSize;
        }

		public boolean showTooltip() {
			return this.showTooltip;
		}
    }

    public static class FileField extends TextField {

    	public FileField(ModelFormField modelFormField) {
            super(modelFormField);
            this.fieldType = FieldInfo.FILE;
        }
    	
        public FileField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.fieldType = FieldInfo.FILE;
        }

        public FileField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, modelFormField);
            this.fieldType = FieldInfo.FILE;
        }

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderFileField(writer, context, this);
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	FileField res = new FileField(modelFormField);
			
        	super.cloneFieldInfo(res);
        	
			return res;
		}
    }

    public static class PasswordField extends TextField {

    	public PasswordField(ModelFormField modelFormField) {
            super(modelFormField);
            this.fieldType = FieldInfo.PASSWORD;
        }
    	
        public PasswordField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.fieldType = FieldInfo.PASSWORD;
        }

        public PasswordField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, modelFormField);
            this.fieldType = FieldInfo.PASSWORD;
        }

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderPasswordField(writer, context, this);
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	PasswordField res = new PasswordField(modelFormField);
			
        	super.cloneFieldInfo(res);
        	
			return res;
		}
    }

    public static class ImageField extends FieldInfo {
        protected int border = 0;
        protected Integer width;
        protected Integer height;
        protected FlexibleStringExpander defaultValue;
        protected FlexibleStringExpander value;
        protected SubHyperlink subHyperlink;
        protected FlexibleStringExpander description;
        protected FlexibleStringExpander alternate;
        protected String type = "content-url";
        
        protected FlexibleStringExpander tooltip;

        protected ImageField() {
            super();
        }

        public ImageField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.IMAGE, modelFormField);
        }

        public ImageField(int fieldSource, ModelFormField modelFormField) {
            super(fieldSource, FieldInfo.IMAGE, modelFormField);
        }

        public ImageField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            this.setValue(element.getAttribute("value"));
            this.setDescription(element.getAttribute("description"));
            this.setAlternate(element.getAttribute("alternate"));
            this.setType(element.getAttribute("type"));
            
            this.setTooltip(element.getAttribute("tooltip"));

            String borderStr = element.getAttribute("border");
            try {
                border = Integer.parseInt(borderStr);
            } catch (Exception e) {
            	if (UtilValidate.isNotEmpty(borderStr)) {
                    Debug.logError("Could not parse the border value of the text element: [" + borderStr + "], setting to the default of " + border, module);
                }
            }

            String widthStr = element.getAttribute("width");
            try {
                width = Integer.valueOf(widthStr);
            } catch (Exception e) {
                width = null;
                if (UtilValidate.isNotEmpty(widthStr)) {
                    Debug.logError(
                        "Could not parse the size value of the text element: [" + widthStr + "], setting to null; default of no width will be used",
                        module);
                }
            }

            String heightStr = element.getAttribute("height");
            try {
                height = Integer.valueOf(heightStr);
            } catch (Exception e) {
                height = null;
                if (UtilValidate.isNotEmpty(heightStr)) {
                    Debug.logError(
                        "Could not parse the size value of the text element: [" + heightStr + "], setting to null; default of no height will be used",
                        module);
                }
            }

            Element subHyperlinkElement = UtilXml.firstChildElement(element, "sub-hyperlink");
            if (subHyperlinkElement != null) {
                this.subHyperlink = new SubHyperlink(subHyperlinkElement, this.getModelFormField());
            }
        }
        
        @Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
        	ImageField res = new ImageField(modelFormField);
			
        	res.border = this.border;
        	res.width = this.width;
        	res.height = this.height;
        	if (UtilValidate.isNotEmpty(this.defaultValue))
        		res.defaultValue = FlexibleStringExpander.getInstance(this.defaultValue.getOriginal());
        	else
        		res.defaultValue = FlexibleStringExpander.getInstance("");
        	if (UtilValidate.isNotEmpty(this.value))
        		res.value = FlexibleStringExpander.getInstance(this.value.getOriginal());
        	else
        		res.value = FlexibleStringExpander.getInstance("");
        	res.subHyperlink = this.subHyperlink;
        	res.type = type;
        	if (UtilValidate.isNotEmpty(this.description))
        		res.description = FlexibleStringExpander.getInstance(this.description.getOriginal());
        	else
        		res.description = FlexibleStringExpander.getInstance("");
        	if (UtilValidate.isNotEmpty(this.alternate))
        		res.alternate = FlexibleStringExpander.getInstance(this.alternate.getOriginal());
        	else
        		res.alternate = FlexibleStringExpander.getInstance("");
        	
        	if (UtilValidate.isNotEmpty(this.tooltip))
				res.tooltip = FlexibleStringExpander.getInstance(this.tooltip.getOriginal());
			else
				res.tooltip = FlexibleStringExpander.getInstance("");
			return res;
		}

        @Override
        public void renderFieldString(Appendable writer, Map<String, Object> context, FormStringRenderer formStringRenderer) throws IOException {
            formStringRenderer.renderImageField(writer, context, this);
        }


        /**
         * @param str
         */
        public void setDefaultValue(String str) {
            this.defaultValue = FlexibleStringExpander.getInstance(str);
        }

        public SubHyperlink getSubHyperlink() {
            return this.subHyperlink;
        }
        public void setSubHyperlink(SubHyperlink newSubHyperlink) {
            this.subHyperlink = newSubHyperlink;
        }
        public Integer getWidth() {
            return width;
        }
        public Integer getHeight() {
            return height;
        }

        public int getBorder() {
            return border;
        }

        public String getDefaultValue(Map<String, Object> context) {
            if (this.defaultValue != null) {
                return this.defaultValue.expandString(context);
            } else {
                return "";
            }
        }

        public String getValue(Map<String, Object> context) {
            if (this.value != null && !this.value.isEmpty()) {
                return this.value.expandString(context);
            } else {
                return modelFormField.getEntry(context);
            }
        }

        public void setValue(String string) {
            this.value = FlexibleStringExpander.getInstance(string);
        }

        public String getType() {
            return this.type;
        }

        public void setType(String string) {
        	this.type = string;
        }

        public String getDescription(Map<String, Object> context) {
        	if (this.description != null && !this.description.isEmpty()) {
        		return this.description.expandString(context);
        	} else {
        		return "";
        	}
        }

        public void setDescription(String description) {
        	this.description = FlexibleStringExpander.getInstance(description);
        }

        public String getAlternate(Map<String, Object> context) {
        	if (this.alternate != null && !this.alternate.isEmpty()) {
        		return this.alternate.expandString(context);
        	} else {
        		return "";
        	}
        }

        public void setAlternate(String alternate) {
        	this.alternate = FlexibleStringExpander.getInstance(alternate);
        }
        
        public String getTooltip(Map<String, Object> context) {
			return (this.tooltip != null && !this.tooltip.isEmpty()) ? this.tooltip.expandString(context) : "";
		}

		public void setTooltip(String str) {
			this.tooltip = FlexibleStringExpander.getInstance(str);
		}
    }

    public static class ContainerField extends FieldInfo {
    	protected String id;

    	public ContainerField() {
    		super();
    	}
    	
    	public ContainerField(ModelFormField modelFormField) {
            super(FieldInfo.SOURCE_EXPLICIT, FieldInfo.CONTAINER, modelFormField);
        }

    	public ContainerField(Element element, ModelFormField modelFormField) {
    		super(element, modelFormField);
    		this.setId(modelFormField.getIdName());
    	}

    	public ContainerField(int fieldSource, int fieldType,
    			ModelFormField modelFormField) {
    		super(fieldSource, fieldType, modelFormField);
    	}

    	@Override
    	public void renderFieldString(Appendable writer,
    			Map<String, Object> context,
    			FormStringRenderer formStringRenderer) throws IOException {
    		formStringRenderer.renderContainerFindField(writer, context, this);
    	}

    	public String getId() {
    		return id;
    	}

    	public void setId(String id) {
    		this.id = id;
    	}

		@Override
		public FieldInfo cloneFieldInfo(ModelFormField modelFormField) {
			ContainerField res = new ContainerField(modelFormField);
			
			res.id = this.id;
			
			return res;
		}
    }
}
