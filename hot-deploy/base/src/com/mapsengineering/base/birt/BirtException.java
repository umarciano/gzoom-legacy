package com.mapsengineering.base.birt;

import org.ofbiz.base.util.GeneralException;

public class BirtException extends GeneralException {
	public BirtException() {
		super();
	}

	public BirtException(String str) {
		super(str);
	}

	public BirtException(String str, Throwable nested) {
		super(str, nested);
	}
}
