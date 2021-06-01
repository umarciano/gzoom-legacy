package com.mapsengineering.standardImport.utils;

public enum ServiceCodeEnum {
	
	CODE_0("0","OK"),
	CODE_0001("01","Missing mandatory field"),
	CODE_0002("02","Generic error");
	
	private String code;
	private String message;
	
	private ServiceCodeEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	
	

}
