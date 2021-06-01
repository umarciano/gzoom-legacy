package com.mapsengineering.base.test;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.service.ModelService;
import org.ofbiz.widget.WidgetUserPrefWorker;

public class TestWidgetUserPreference extends GplusTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	
	public void testSaveUserPreference() {
		Map<String, Object> map = FastMap.newInstance();
		map.put("formName", "TestForm");
		
		Map<String, Object> field = FastMap.newInstance();
		field.put("position", "0");
		field.put("groupId", "gruppo1");
		map.put("TestForm.field1", field);

		field = FastMap.newInstance();
		field.put("position", "1");
		map.put("TestForm.field2", field);
		Map<String, Object> res = WidgetUserPrefWorker.saveUserPreference(delegator, "admin", "TestForm", map); 
		assertFalse(res.containsKey(ModelService.ERROR_MESSAGE));
	}
	
	
	public void testGetUserPreference() {
		Map<String, Object> res = WidgetUserPrefWorker.getUserPref(delegator, "admin", "TestForm"); 
		assertFalse(res.containsKey(ModelService.ERROR_MESSAGE));
		assertTrue(res.containsKey(WidgetUserPrefWorker.USERPREF_MAP_KEY));
	}

	
	public void testBuildXmlFromMap() {
		Map<String, Object> map = FastMap.newInstance();
		map.put("formName", "TestForm");
		
		Map<String, Object> field = FastMap.newInstance();
		field.put("position", "0");
		field.put("groupId", "gruppo1");
		map.put("TestForm.field1", field);

		field = FastMap.newInstance();
		field.put("position", "1");
		map.put("TestForm.field2", field);
		
		Map<String, Object> res = WidgetUserPrefWorker.buildXmlFromUserPrefMap(map);
		assertFalse(res.containsKey(ModelService.ERROR_MESSAGE));
		assertTrue(res.containsKey(WidgetUserPrefWorker.XML_KEY));
	}
	
	public void testBuildMapFromXml() {
		
		String xml = "<userpreference>" +
		"<form name=\"prova-field-group1\">" + 
		"<field name=\"prova-field-group1.roleTypeId\" position=\"0\" group-id=\"pippo\"/>" +
		"<field name=\"prova-field-group1.parentTypeId\" position=\"1\" group-id=\"pippo\"/>" +
		"<field name=\"prova-field-group1.hasTable\" position=\"3\" group-id=\"\"/></form></userpreference>";
		
		Map<String, Object> res = WidgetUserPrefWorker.buildUserPrefMapFromXml(xml);
		assertFalse(res.containsKey(ModelService.ERROR_MESSAGE));
	}

	
}
