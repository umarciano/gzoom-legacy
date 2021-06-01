package com.mapsengineering.base.birt.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;

import com.mapsengineering.base.birt.event.EventHandlerException;

/**
 * Report - Report model class
 *
 */
@SuppressWarnings("serial")
public class ModelReport implements Serializable, Comparable<ModelReport> {
	public static final String module = ModelReport.class.getName();
	
    protected ModelReader modelReader = null;
	
	private String id = null;
	private String name = null;
	private String description = null;
	private String outputFileName = null;
	/** A List of the Parameter objects for the Report */
    protected List<ModelParameter> parameters = FastList.newInstance();
    protected Map<String, ModelParameter> parametersMap = null;
    
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getOutputFileName() {
		return outputFileName;
	}
	
	// ===== CONSTRUCTORS =====
    /** Default Constructor */
    public ModelReport() {}

    protected ModelReport(ModelReader reader) {
        this.modelReader = reader;
    }
    
    /** XML Constructor */
    protected ModelReport(ModelReader reader, Element reportElement) {
        this(reader);
        this.populateBasicInfo(reportElement);

        for (Element parameterElement: UtilXml.childElementList(reportElement, "parameter")) {
            ModelParameter parameter = new ModelParameter(parameterElement);
            if (parameter != null) {
            	parameter.setModelReport(this);
                this.parameters.add(parameter);
            }
        }
    }
    
    protected void populateBasicInfo(Element reportElement) {
        this.id = UtilXml.checkEmpty(reportElement.getAttribute("id")).intern();
        this.name = UtilXml.checkEmpty(reportElement.getAttribute("name")).intern();
        this.description = UtilXml.checkEmpty(reportElement.getAttribute("description")).intern();
        this.outputFileName = UtilXml.checkEmpty(reportElement.getAttribute("output-file-name")).intern();
    }
    
    public ModelParameter getParameter(String parameterName) {
        if (parameterName == null) return null;
        if (parametersMap == null) {
            createParametersMap();
        }
        ModelParameter modelParameter = parametersMap.get(parameterName);
        if (modelParameter == null) {
            // sometimes weird things happen and this getField method is called before the fields are all populated, so before moving on just re-create the fieldsMap again real quick...
            // the purpose of the fieldsMap is for speed, but if failures are a little slower, no biggie
        	createParametersMap();
        	modelParameter = parametersMap.get(parameterName);
        }
        return modelParameter;
    }

    protected synchronized void createParametersMap() {
        Map<String, ModelParameter> tempMap = FastMap.newInstance();
        for (int i = 0; i < parameters.size(); i++) {
        	ModelParameter parameter = parameters.get(i);
            tempMap.put(parameter.getName(), parameter);
        }
        parametersMap = tempMap;
    }
    
    public void invokeAllEvent(Map<String, Object> context) throws EventHandlerException {
    	invokeAllEvent(context, null);
    }
    
    public void invokeAllEvent(Map<String, Object> context, Locale locale) throws EventHandlerException {
    	for (ModelParameter parameter : this.parameters) {
    		parameter.invokeEvent(context, locale);
    	}
    }
    
    public Map<String, Object> convertAllValue(Map<String, Object> context) {
    	return convertAllValue(context, null);
    }
    
    public Map<String, Object> convertAllValue(Map<String, Object> context, Locale locale) {
    	Map<String, Object> res = new HashMap<String, Object>();
    	
    	for (ModelParameter parameter : this.parameters) {
    		res.put(parameter.getName(), parameter.convertValue(context.get(parameter.getName()), locale));
    	}
    	
    	return res;
    }
    
    public List<String> validateAllValue(Map<String, Object> context, Locale locale) {
    	List<String> res = new ArrayList<String>();
    	for (ModelParameter parameter : this.parameters) {
    		String validationMessage = this.validate(context, parameter.getName(), locale);
    		if (UtilValidate.isNotEmpty(validationMessage)) {
    			res.add(validationMessage);
    		}
    	}
    	
    	return res;
    }
    
    private String validate(Map<String, Object> context, String name, Locale locale) {
    	if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(name)) {
    		ModelParameter parameter = this.getParameter(name);
    		if (UtilValidate.isNotEmpty(parameter)) {
    			if (!parameter.validate(context.get(name))) {
    				return parameter.getErrorMessage(locale);
    			}
    		}
    	}
    	
    	return null;
    }
    		
    
    public Object convertValue(Map<String, Object> context, String name) {
    	return convertValue(context, name, null);
    }
    
    public Object convertValue(Map<String, Object> context, String name, Locale locale) {
    	Object res = null;
    	
    	if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(name)) {
    		ModelParameter parameter = this.getParameter(name);
    		if (UtilValidate.isNotEmpty(parameter)) {
    			res = parameter.convertValue(context.get(name), locale);
    		}
    	}
    	
    	return res;
    }
	
	@Override
	public int compareTo(ModelReport to) {
		if (to == null) {
			return 1;
		}
		return this.getId().compareTo(to.getId());
	}
	
	@Override
	public boolean equals(Object obj) {
	    return this.getId().equals(obj);
	}
	
    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }	
}
