package com.mapsengineering.base.util;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;

public class FreemarkerWorker {
	
	private FreemarkerWorker() {}
	
	public static String getFieldIdWithTimeStamp(String prefix) {
		String res = prefix;
		
		String currentTimeStamp = Long.toString(UtilDateTime.nowTimestamp().getTime());
		if (UtilValidate.isNotEmpty(res))
			res += "_" + currentTimeStamp;
		else
			res = "id_" + currentTimeStamp;
		
		return res;
	}
}
