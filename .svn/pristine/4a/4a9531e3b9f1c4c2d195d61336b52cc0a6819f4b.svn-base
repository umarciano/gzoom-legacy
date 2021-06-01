package com.mapsengineering.standardImport.utils;

public enum StandardImportAllocationEnum implements StandardImportEnum{
	
	dataSource(true),
	refDate(true),
	personRoleTypeId(false),
	personCode(true),
	allocationRoleTypeId(false),
	allocationOrgCode(true),
	allocationFromDate(true),
	allocationThruDate(true),
	allocationOrgComments(false),
	allocationOrgDescription(false),
	allocationValue(true);
	
	private boolean mandatory;
	
	private StandardImportAllocationEnum(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	
	public String getName() {
		return this.name();
	}
	
	
}
