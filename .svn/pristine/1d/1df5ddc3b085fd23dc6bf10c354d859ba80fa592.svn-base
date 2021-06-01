package com.mapsengineering.standardImport.utils;

public enum StandardImportOrganizationEnum implements StandardImportEnum{
	
	dataSource(true),
	refDate(true),
	orgCode(true),
	description(true),
	longDescription(false),
	parentOrgCode(false),
	responsibleCode(false),
	thruDate(false),
	orgRoleTypeId(false),
	parentRoleTypeId(false),
	responsibleFromDate(false),
	responsibleThruDate(false),
	responsibleComments(false),
	responsibleRoleTypeId(false),
	descriptionLang(false),
	longDescriptionLang(false),
	vatCode(false),
	parentFromDate(false);
	
	private boolean mandatory;
	
	private StandardImportOrganizationEnum(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	
	public String getName() {
		return this.name();
	}
	
	
}
