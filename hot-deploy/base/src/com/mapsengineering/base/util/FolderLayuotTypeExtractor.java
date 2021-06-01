package com.mapsengineering.base.util;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;


public class FolderLayuotTypeExtractor {
	private Map<String, Object> context;
	private Map<String, Object> parameters;
	
	
	
	/**
	 * la classe recupera il layoutType dal folderIndex
	 * @param context
	 * @param parameters
	 */
	public FolderLayuotTypeExtractor(Map<String, Object> context, Map<String, Object> parameters) {
		this.context = context;
		this.parameters = parameters;
	}
	
	/**
	 * recupera il layoutType, prendendolo di dafault dal context
	 * @return
	 */
	public String getLayoutTypeFromContext() {
		String layoutType = UtilValidate.isNotEmpty(context.get(E.layoutType.name())) 
				? (String) context.get(E.layoutType.name()) 
				: (String) parameters.get(E.layoutType.name());
		return getLayoutTypeFromFolderIndex(layoutType);		
	}
	
	/**
	 * recupera il layoutType dal folderIndex, senza valore di default
	 * @return
	 */
	public String getLayoutTypeFromFolderIndex() {
		return getLayoutTypeFromFolderIndex("");
	}
	
	/**
	 * recupera il layoutType dal folderIndex, con valore di default
	 * @param defaultLayoutType
	 * @return
	 */
	public String getLayoutTypeFromFolderIndex(String defaultLayoutType) {
		String layoutType = defaultLayoutType;
		Integer folderIndex = UtilValidate.isNotEmpty(context.get(E.folderIndex.name())) 
				? (Integer) context.get(E.folderIndex.name()) 
				: (Integer) parameters.get(E.folderIndex.name());
		if(folderIndex != null) {
			List<String> folderContentIds = (List<String>) context.get(E.folderContentIds.name());
			layoutType = UtilValidate.isNotEmpty(folderContentIds) ? (String) folderContentIds.get(folderIndex.intValue()) : defaultLayoutType;
		}
		return layoutType;
	}
		
}
