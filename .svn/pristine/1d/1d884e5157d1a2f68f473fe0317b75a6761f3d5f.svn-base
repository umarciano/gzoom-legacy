package com.mapsengineering.base.client;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TreeReader {
	
	/**
	 * Unchecked Exception thrown by reader
	 * @author sandro
	 */
	@SuppressWarnings("serial")
	public static class TreeReaderException extends Exception {
		public TreeReaderException(Exception e) {
			super(e);
		}
	}
	
	protected TreeReader() {
	}
	
	/**
	 * Reads tree definition from xml trees resource file
	 * @param xmlPath
	 * @param treeName
	 * @return Tree element
	 * @throws TreeReaderException
	 */
	public static Element readTreeElement(String xmlPath, String treeName) throws TreeReaderException  {
		try {
			URL url = FlexibleLocation.resolveLocation(xmlPath);
			Document doc = UtilXml.readXmlDocument(url);
			return UtilXml.firstChildElement(doc.getDocumentElement(), "tree", "name", treeName);
		} catch (Exception e) {
			throw new TreeReaderException(e);
		}
	}
	
	/**
	 * Extract field mapping for a tree
	 * 
	 * @return Map ["label": top label of column; "iconClass": class or expression for row's icon;  fieldGroupSize: number of groups; "fieldGroupList": list of group maps: ["fieldGroupLabel", "fieldList"]]
	 */	
	public static Map<String, Object> getTreeColumnFieldMap(Element columnElement, Map<String, Object> context) throws TreeReaderException {
		try {
			Map<String, Object> res = FastMap.newInstance();
			
			//find out column top label
			String label = "label";
			List<? extends Element> labels =  UtilXml.childElementList(columnElement, "label");
			if (UtilValidate.isNotEmpty(labels)) {
				label = labels.get(0).getAttribute("text");
				if (UtilValidate.isNotEmpty(labels.get(0).getAttribute("style"))) {
					res.put("iconClass",  labels.get(0).getAttribute("style"));
				}
			}
			label = FlexibleStringExpander.expandString(label, context);
			res.put("label", label);


			//find out sub-node list 
			//if there is only one subnode the column header won't be separated into sub-fields
			List<? extends Element> subNodes = UtilXml.childElementList(columnElement, "sub-node");
			
			List<Map<String, Object>> fieldGroupList = FastList.newInstance();
			
			//check subnodes number in order to signal if there is 
			//a multi field column
			res.put("fieldGroupSize", subNodes.size());
			
			//list all sub nodes (everyone is a field group) 
			for (Element subNode: subNodes) {
				Map<String, Object> fieldGroupMap = FastMap.newInstance();
				
				//extract field group label (if exists)
				String fieldGroupLabel = subNode.getAttribute("node-name");
				if (UtilValidate.isNotEmpty(fieldGroupLabel)) {
					fieldGroupLabel = FlexibleStringExpander.expandString(fieldGroupLabel, context);
					fieldGroupMap.put("fieldGroupLabel", fieldGroupLabel);
				}
				//field name list of this fieldGroup
				List<Map<String, String>> fieldList = FastList.newInstance();
				for(Element field:  UtilXml.childElementList(subNode, "out-field-map")) {
					//Costruisco la mappatura di descrizione del campo
					Map<String, String> fieldMap = FastMap.newInstance();
					fieldMap.put("fieldName", field.getAttribute("field-name") );
					fieldMap.put("fieldType", field.getAttribute("field-type") );
					fieldList.add(fieldMap);
				}
				fieldGroupMap.put("fieldList", fieldList);
				
				fieldGroupList.add(fieldGroupMap);
			}
			res.put("fieldGroupList", fieldGroupList);
			
			return res;
		} catch (Exception e) {
			throw new TreeReaderException(e);
		}
	}
}
