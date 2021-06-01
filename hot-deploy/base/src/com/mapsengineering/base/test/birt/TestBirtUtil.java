package com.mapsengineering.base.test.birt;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.mapsengineering.base.birt.util.UtilDateTime;
import com.mapsengineering.base.birt.util.UtilNumber;
import com.mapsengineering.base.birt.util.UtilsConvertJdbc;
import com.mapsengineering.base.test.BaseTestCase;


public class TestBirtUtil extends BaseTestCase {
	
	public void testMonthStartFromDatetime()
	{
		Date date = UtilDateTime.getMonthStartFromDatetime("05", "", 2013, Locale.ITALIAN);
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		
		assertNotNull(cal);
		
		assertEquals(1, cal.get(Calendar.DATE));
		assertEquals(4, cal.get(Calendar.MONTH));
		assertEquals(2013, cal.get(Calendar.YEAR));
		
	}
	
	public void testMonthEndFromDatetime()
	{
		Date date = UtilDateTime.getMonthEndFromDatetime("05", "", 2013, Locale.ITALIAN);
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		
		assertNotNull(cal);
		
		assertEquals(31, cal.get(Calendar.DATE));
		assertEquals(4, cal.get(Calendar.MONTH));
		assertEquals(2013, cal.get(Calendar.YEAR));
		
	}
	
	public void testMonthStartFromDatetimeWithTZ()
	{
		TimeZone tz = TimeZone.getDefault();
		Date date = UtilDateTime.getMonthStartFromDatetime("06", tz.getID(), 2013, Locale.ITALIAN);
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		
		assertNotNull(cal);
		
		assertEquals(1, cal.get(Calendar.DATE));
		assertEquals(5, cal.get(Calendar.MONTH));
		assertEquals(2013, cal.get(Calendar.YEAR));
		
	}
	
	public void testMonthEndFromDatetimeWithTZ()
	{
		TimeZone tz = TimeZone.getDefault();
		Date date = UtilDateTime.getMonthEndFromDatetime("06", tz.getID(), 2013, Locale.ITALIAN);
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		
		assertNotNull(cal);
		
		assertEquals(30, cal.get(Calendar.DATE));
		assertEquals(5, cal.get(Calendar.MONTH));
		assertEquals(2013, cal.get(Calendar.YEAR));
		
	}	
	
	public void testConvertNumberToRomanNumeral1(){
		String roman = UtilNumber.convertNumberToRomanNumeral(1);
		assertEquals("I", roman);
	}
	
	public void testConvertNumberToRomanNumeral2(){
		String roman = UtilNumber.convertNumberToRomanNumeral(4);
		assertEquals("IV", roman);
	}
	
	public void testConvertNumberToRomanNumeral3(){
		String roman = UtilNumber.convertNumberToRomanNumeral(10);
		assertEquals("X", roman);
	}
	
	public void testConvertNumberToRomanNumeral4(){
		String roman = UtilNumber.convertNumberToRomanNumeral(35);
		assertEquals("XXXV", roman);
	}
	
	public void testConvertNumberToRomanNumeral5(){
		String roman = UtilNumber.convertNumberToRomanNumeral(101);
		assertEquals("CI", roman);
	}
	
	public void testConvertNumberToRomanNumeral6(){
		String roman = UtilNumber.convertNumberToRomanNumeral(405);
		assertEquals("CDV", roman);
	}
	
	public void testConvertNumberToRomanNumeral7(){
		String roman = UtilNumber.convertNumberToRomanNumeral(-5);
		assertEquals("", roman);
	}
	
	//test UtilsConvertJdbc
	public void testConvertJdbc(){
	    
	    Date date = org.ofbiz.base.util.UtilDateTime.nowDate();
	    String dateString = "'" + org.ofbiz.base.util.UtilDateTime.toSqlDateString(date) + "'";
	    
	    String dateJdbc = UtilsConvertJdbc.getConvertDateToDateJdbc(date, delegator);
	    
	    assertEquals(dateString, dateJdbc);
	}
}