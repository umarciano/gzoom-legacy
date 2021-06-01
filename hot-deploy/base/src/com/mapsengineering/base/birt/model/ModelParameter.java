package com.mapsengineering.base.birt.model;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;

import com.mapsengineering.base.birt.event.EventHandlerException;
import com.mapsengineering.base.birt.event.EventHandlerFactory;
import com.mapsengineering.base.birt.event.IBirtParameterEventHandler;

@SuppressWarnings("serial")
public class ModelParameter implements Serializable {
	protected ModelReport parentModelReport;
	
	private String name = null;
	private String type = null;
	private String format = null;
	private boolean mandatory = false;
	private String validationMessageResource = null;
	private String validationMessage = null;
	private String defaultValue = null;
	
	public Event event;
	
	/** XML Constructor */
    public ModelParameter(Element parameterElement) {
        this.type = UtilXml.checkEmpty(parameterElement.getAttribute("type"), "String").intern();
        this.name = UtilXml.checkEmpty(parameterElement.getAttribute("name")).intern();
        this.format = UtilXml.checkEmpty(parameterElement.getAttribute("format")).intern();
        this.mandatory = UtilXml.checkBoolean(parameterElement.getAttribute("mandatory"), false);
        this.validationMessageResource = UtilXml.checkEmpty(parameterElement.getAttribute("validation-message-resource")).intern();
        this.validationMessage = UtilXml.checkEmpty(parameterElement.getAttribute("validation-message")).intern();
        this.defaultValue = UtilXml.checkEmpty(parameterElement.getAttribute("default-value")).intern();
        // Check for event
        Element eventElement = UtilXml.firstChildElement(parameterElement, "event");
        if (eventElement != null) {
            this.event = new Event(eventElement);
        }
    }

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
	
	public String getFormat() {
		return format;
	}

	public boolean isMandatory() {
		return mandatory;
	}
	
	public String getValidationMessageResource() {
		return validationMessageResource;
	}
	
	public String getValidationMessage() {
		return validationMessage;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public String getErrorMessage(Locale locale) {
		String res = null;
		
		if (UtilValidate.isNotEmpty(validationMessageResource)) {
			res = UtilProperties.getMessage(validationMessageResource, validationMessage, locale);
		} 
		if (UtilValidate.isEmpty(res)) {
			if (UtilValidate.isNotEmpty(validationMessage)) {
				if (validationMessage.indexOf("#") != -1) {
					res = UtilProperties.getMessage(validationMessage.substring(0, validationMessage.indexOf("#")), validationMessage.substring(validationMessage.indexOf("#")+1), locale);
				} else {
					res = validationMessage;
				}
			}
		}
		
		return res;
	}
	
	public ModelReport getModelReport() {
		return parentModelReport;
	}
	public void setModelReport(ModelReport modelReport) {
		this.parentModelReport = modelReport;
	}
	
	protected Object convertValue(Object value, Locale locale) {
		Object res = "";
		
		try {
			if (UtilValidate.isEmpty(value)) {
				if (UtilValidate.isNotEmpty(this.defaultValue)) {
					value = this.defaultValue;
				}
			}
			
			if (UtilValidate.isNotEmpty(value)) {
				Object convertedValue = ObjectType.simpleTypeConvert(value, this.type, this.format, locale);
				if (UtilValidate.isNotEmpty(convertedValue)) {
					res = convertedValue;
				}
			}
		} catch (GeneralException e) {
		}
		
		return res;
	}
	
	protected boolean validate(Object value) {
		if (UtilValidate.isEmpty(value)) {
			if (UtilValidate.isNotEmpty(this.defaultValue)) {
				value = this.defaultValue;
			}
		}
		return !this.mandatory || (this.mandatory && UtilValidate.isNotEmpty(value));
	}
	
	protected void invokeEvent(Map<String, Object> context, Locale locale) throws EventHandlerException {
		if (UtilValidate.isEmpty(context.get("locale")) && UtilValidate.isNotEmpty(locale)) {
			context.put("locale", locale);
		}
		if (UtilValidate.isNotEmpty(this.event)) {
			IBirtParameterEventHandler eventHandler = EventHandlerFactory.getEventHandlerFactory().getEventHandler(this.event.type, context);
			if (eventHandler != null) {
				eventHandler.invoke(this.event, context);
			}
		}
	}
	
	public static class Event {
        public String type;
        public String path;
        public String invoke;

        public Event(Element eventElement) {
            this.type = eventElement.getAttribute("type");
            this.path = eventElement.getAttribute("path");
            this.invoke = eventElement.getAttribute("invoke");
        }

        public Event(String type, String path, String invoke) {
            this.type = type;
            this.path = path;
            this.invoke = invoke;
        }
    }
}
