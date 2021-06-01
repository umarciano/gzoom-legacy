package org.ofbiz.widget;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Manages user preferences
 * @author Maps spa
 *
 */
public class WidgetUserPrefWorker {
	
	public static final String module = WidgetUserPrefWorker.class.getName();

	/**
	 * Xml root
	 */
	public static final String ROOT_ELEMENT = "userpreference";
	/**
	 * xml form element
	 */
	public static final String FORM_ELEMENT = "form";
	/**
	 * Form name attribute 
	 */
	public static final String FORM_NAME_ATTR = "name";
	/**
	 * Xml field element
	 */
	public static final String FIELD_ELEMENT = "field";
	/**
	 * Field name attribute
	 */
	public static final String FIELD_NAME_ATTR = "name";
	/**
	 * Field position attribute
	 */
	public static final String FIELD_POSITION_ATTR = "position";
	/**
	 * Field group-id attribute
	 */
	public static final String FIELD_GROUPID_ATTR = "group-id";
	/**
	 * Field group-id attribute
	 */
	public static final String FIELD_WIDTH_ATTR = "width";
	/**
	 * Userpref_Group_Type_Id Key to store widget user preference 
	 */
	public static final String WIDGET_USERPREF_GROUP_TYPE_ID = "WIDGET_USER_PREF";
	/**
	 * Key to store xml string into parameter map
	 */
	public static final String XML_KEY = "xml";
	/**
	 * Key to store userPrefMap into result
	 */
	public static final String USERPREF_MAP_KEY = "userPrefMap";
	
	/**
	 * Converts xml content into field map
	 * @param xml
	 * @return Map of xml form and fields or "errorMessage" idf any
	 */
	public static Map<String, Object> buildUserPrefMapFromXml(String xml) {
		Map<String, Object> resultMap = FastMap.newInstance();
		try {
			
			Document doc = UtilXml.readXmlDocument(xml);
			Element userpreference = doc.getDocumentElement();
			
			List<? extends Element> forms = UtilXml.childElementList(userpreference, WidgetUserPrefWorker.FORM_ELEMENT); 
			if (UtilValidate.isEmpty(forms)) {
				return resultMap;
			}
			
			Element form = forms.get(0);
			//Gets form name from attribute
			String name = form.getAttribute(WidgetUserPrefWorker.FORM_NAME_ATTR);
			resultMap.put("formName", name);
			
			//gets form child nodes
			List<? extends Element> fieldList = UtilXml.childElementList(form, WidgetUserPrefWorker.FIELD_ELEMENT);
			for (Element field: fieldList)  {
				//Get field name
				String fieldName = field.getAttribute(WidgetUserPrefWorker.FIELD_NAME_ATTR);
				//Get other field's attribute and make it's own map
				Map<String, Object> fieldAttrMap = FastMap.newInstance();
				String fieldPos = field.getAttribute(WidgetUserPrefWorker.FIELD_POSITION_ATTR);
				String groupId = field.getAttribute(WidgetUserPrefWorker.FIELD_GROUPID_ATTR);
				String width = field.getAttribute(WidgetUserPrefWorker.FIELD_WIDTH_ATTR);
				fieldAttrMap.put("position", fieldPos);
				if (UtilValidate.isNotEmpty(groupId)) {
					fieldAttrMap.put("groupId", groupId);
				}
				if (UtilValidate.isNotEmpty(width)) {
					fieldAttrMap.put("width", width);
				}
				//Puts field in the result map 
				resultMap.put(fieldName, fieldAttrMap);
			}
			
		} catch (Exception e) {
			String msg = String.format("%s: Error: %s, message: %s", module, e.getClass().toString(), e.getMessage());
			Debug.logError(msg, module);
			resultMap.putAll(ServiceUtil.returnError(msg));			
		}
		return resultMap;
	}
	
	/**
	 * Converts form user pref map into xml 
	 * @param userPrefMap
	 * @return Map with 'xml' key containings xml string, or 'errorMessage' key containings error message
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> buildXmlFromUserPrefMap(Map<String, Object> userPrefMap) {
		
		Map<String, Object> result = FastMap.newInstance();
		try {
			//Make document and get root
			Document document = UtilXml.makeEmptyXmlDocument(WidgetUserPrefWorker.ROOT_ELEMENT);
			Element root = document.getDocumentElement();
			//Make form child
			Element form = document.createElement(FORM_ELEMENT);
			root.appendChild(form);
			form.setAttribute(WidgetUserPrefWorker.FORM_NAME_ATTR, (String)userPrefMap.get("formName"));
			//Fields loop
			for(String key: userPrefMap.keySet()) {
				//form name has been set before
				if ("formName".equalsIgnoreCase(key)) {
					continue;
				}
				
				Map<String, Object> fieldMap = (Map<String, Object>)userPrefMap.get(key);
				
				Element field = document.createElement(FIELD_ELEMENT);
				form.appendChild(field);
				field.setAttribute(WidgetUserPrefWorker.FIELD_NAME_ATTR, key);
				field.setAttribute(WidgetUserPrefWorker.FIELD_POSITION_ATTR, (String)fieldMap.get("position"));
				String groupId = (String)fieldMap.get("groupId");
				if (UtilValidate.isNotEmpty(groupId)) {
					field.setAttribute(WidgetUserPrefWorker.FIELD_GROUPID_ATTR, groupId);
				}
				String width = (String)fieldMap.get("width");
				if (UtilValidate.isNotEmpty(width)) {
					field.setAttribute(WidgetUserPrefWorker.FIELD_WIDTH_ATTR, width);
				}
			}
			//Create xml
			DOMSource src = new DOMSource(document);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			StringWriter sw = new StringWriter();
			StreamResult sr = new StreamResult(sw);
			transformer.transform(src, sr);
			
			//Store xml into result map
			result.put(WidgetUserPrefWorker.XML_KEY, sw.toString());
			
		} catch (Exception e) {
			String msg = String.format("%s: Error: %s, message: %s", module, e.getClass().toString(), e.getMessage());
			Debug.logError(msg, module);
			result.putAll(ServiceUtil.returnError(msg));
		}
		return result;
	}
	
	/**
	 * Gets user preference from persistence
	 * @param userLoginId
	 * @param formName
	 * @param delegator
	 * @return Map with userPrefMap parameter containing Map, or 'errorMessage' if any
	 */
	public static Map<String, Object> getUserPref(Delegator delegator, String userLoginId, String formName) {
		
		Map<String, Object> res = FastMap.newInstance();
		try {
			GenericValue userPref = delegator.findOne("UserPreference", UtilMisc.toMap("userLoginId", userLoginId, "userPrefTypeId", formName), false);
			if (UtilValidate.isEmpty(userPref)) {
				//Set empty map
				res.put(WidgetUserPrefWorker.USERPREF_MAP_KEY, FastMap.newInstance());
				return res;
			}
			//For xsd definition see:  userpreference.xsd
			String xml = userPref.getString("xmlUserPref");
			Map<String, Object> userPrefMap = WidgetUserPrefWorker.buildUserPrefMapFromXml(xml);
			if (userPrefMap.containsKey(ModelService.ERROR_MESSAGE)) {
				//if any error quit
				return userPrefMap;
			}
			
			//store result
			res.put(WidgetUserPrefWorker.USERPREF_MAP_KEY, userPrefMap);
			
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			res.putAll(ServiceUtil.returnError(e.getMessage()));
		}
		return res;
	}
	
	/**
	 * save user preference
	 * @param delegator
	 * @param userLoginId
	 * @param formName
	 * @param formMap
	 * @return Map with error message if any
	 */
	public static Map<String, Object> saveUserPreference(Delegator delegator, String userLoginId, String formName, Map<String, Object> formMap) {
		Map<String, Object> res = FastMap.newInstance();
		try {
			//Build xml from map
			Map<String, Object> resMap = WidgetUserPrefWorker.buildXmlFromUserPrefMap(formMap);
			if (resMap.containsKey(ModelService.ERROR_MESSAGE)) {
				return resMap;
			}
			//now save xml
			GenericValue gv = delegator.makeValue("UserPreference");
			gv.set("userLoginId", userLoginId);
			gv.set("userPrefTypeId", formName);
			gv.set("userPrefGroupTypeId", WidgetUserPrefWorker.WIDGET_USERPREF_GROUP_TYPE_ID);
			gv.set("xmlUserPref", resMap.get(WidgetUserPrefWorker.XML_KEY));
			delegator.storeAll(UtilMisc.toList(gv));
			
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			res.putAll(ServiceUtil.returnError(e.getMessage()));
		}
		return res;
	}

}
