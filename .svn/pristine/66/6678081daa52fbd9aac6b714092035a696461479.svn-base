package com.mapsengineering.base.test;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;

import com.mapsengineering.base.util.ValidationUtil;

public class TestValidationUtil extends GplusTestCase {

	public void testCheckDateTimeAgainstLocale() {
	
		SimpleDateFormat sdf = new SimpleDateFormat(UtilDateTime.getDateTimeFormat(Locale.ITALY));
		String date = sdf.format(new java.util.Date());
		Debug.log("********************************* Check Date Locale ITALY : " +  date);
		assertNotNull(ValidationUtil.checkDateTimeAgainstLocale(date, Locale.ITALY) );
		
		sdf = new SimpleDateFormat(UtilDateTime.getDateTimeFormat(Locale.US));
		date = sdf.format(new java.util.Date());
		Debug.log("********************************* Check Date Locale US : " +  date);
		assertNotNull(ValidationUtil.checkDateTimeAgainstLocale(date, Locale.US) );

		sdf = new SimpleDateFormat(UtilDateTime.getDateTimeFormat(Locale.ENGLISH));
		date = sdf.format(new java.util.Date());
		Debug.log("********************************* Check Date Locale ENGLISH : " +  date);
		assertNotNull(ValidationUtil.checkDateTimeAgainstLocale(date, Locale.ENGLISH) );

		sdf = new SimpleDateFormat(UtilDateTime.getDateTimeFormat(Locale.FRENCH));
		date = sdf.format(new java.util.Date());
		Debug.log("********************************* Check Date Locale FRENCH : " +  date);
		assertNotNull(ValidationUtil.checkDateTimeAgainstLocale(date, Locale.FRENCH) );

		sdf = new SimpleDateFormat(UtilDateTime.getDateTimeFormat(Locale.GERMANY));
		date = sdf.format(new java.util.Date());
		Debug.log("********************************* Check Date Locale GERMANY : " +  date);
		assertNotNull(ValidationUtil.checkDateTimeAgainstLocale(date, Locale.GERMANY) );
	}
	
}
