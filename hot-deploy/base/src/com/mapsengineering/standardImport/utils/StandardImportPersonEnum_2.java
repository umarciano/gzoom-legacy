package com.mapsengineering.standardImport.utils;

public enum StandardImportPersonEnum_2 implements StandardImportEnum{
	
	dataSource(true),
	refDate(true),
	personCode(true),
	firstName(true),
	lastName(true),
	employmentAmount(false),
	emplPositionTypeId(false),
	qualifCode(false),
	qualifFromDate(false),
	employmentOrgCode(false),
	personRoleTypeId(false),
	comments(false),
	description(false),
	fromDate(false),
	employmentRoleTypeId(false),
	employmentOrgComments(false),
	allocationOrgCode(false),
	allocationRoleTypeId(false),
	allocationOrgComments(false),
	evaluatorCode(false),
	evaluatorFromDate(false),
	contactMail(false),
	contactMobile(false),
	userLoginId(false),
	groupId(false),
	isEvalManager(false),
	approverCode(false),
	workEffortAssignmentCode(false),
	workEffortDate(false),
	employmentOrgDescription(false),
	allocationOrgDescription(false),
	allocationOrgFromDate(false),
	allocationOrgThruDate(false),
	fiscalCode(false),
	employmentOrgFromDate(false),
	employmentOrgThruDate(false),
	emplPositionTypeDate(false),
	thruDate(false);
	
	private boolean mandatory;
	
	private StandardImportPersonEnum_2(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	
	public String getName() {
		return this.name();
	}
	
	
}
