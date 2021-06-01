package com.mapsengineering.base.birt.util;


import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.ofbiz.base.util.UtilValidate;
import com.ibm.icu.util.Calendar;

public class UtilDateTime {
	
	public static final int FIRST_MONTH = 1;
	public static final int LAST_MONTH = 12;
	public static final Timestamp DATE_MEASURE_REFERENCE = org.ofbiz.base.util.UtilDateTime.toTimestamp(12, 31, 1899, 0, 0, 0);
	
	private UtilDateTime() {}
	
	public static Date getMonthStartFromDatetime(String monthStr, TimeZone timeZone, int year, Locale locale) {
		Date res=null;
		if (monthStr != null && locale != null) {
			if (timeZone == null) {
				timeZone = TimeZone.getDefault();
			} 
			int month = Integer.valueOf(monthStr);
			if (month >= FIRST_MONTH && month <= LAST_MONTH) {
				Timestamp currentTimestamp = org.ofbiz.base.util.UtilDateTime.getMonthStart(org.ofbiz.base.util.UtilDateTime.nowTimestamp());
				currentTimestamp = org.ofbiz.base.util.UtilDateTime.getDayStart(currentTimestamp);
				Calendar cal = org.ofbiz.base.util.UtilDateTime.toCalendar(currentTimestamp, timeZone, locale);
				if (cal != null) {
					cal.set(Calendar.MONTH, month-1);
					cal.set(Calendar.YEAR, year);
					
					res = cal.getTime();
				}
			}
		}
		
		return res;
	}
	
	public static Date getMonthStartFromDatetime(String monthStr, String timeZone, int year, Locale locale) {
		TimeZone currentTimeZone = null;
		if (UtilValidate.isNotEmpty(timeZone)) {
			currentTimeZone = TimeZone.getTimeZone(timeZone);
		} else {
			currentTimeZone = TimeZone.getDefault();
		}		
		return getMonthStartFromDatetime(monthStr, currentTimeZone, year, locale);
	}
	
	public static Date getMonthEndFromDatetime(String monthStr, TimeZone timeZone, int year, Locale locale) {
		Timestamp res = null;
		Date res2=null;
		if (monthStr != null && locale != null) {
			if (timeZone == null) {
				timeZone = TimeZone.getDefault();
			} 
			int month = Integer.valueOf(monthStr);
			if (month >= FIRST_MONTH && month <= LAST_MONTH) {
				Timestamp currentTimestamp = org.ofbiz.base.util.UtilDateTime.getMonthStart(org.ofbiz.base.util.UtilDateTime.nowTimestamp());
				Calendar cal = org.ofbiz.base.util.UtilDateTime.toCalendar(currentTimestamp, timeZone, locale);
				if (cal != null) {
					cal.set(Calendar.MONTH, month-1);
					cal.set(Calendar.YEAR, year);
					
					res = org.ofbiz.base.util.UtilDateTime.getMonthEnd(org.ofbiz.base.util.UtilDateTime.toTimestamp(cal.getTime()), timeZone, locale);
					res = org.ofbiz.base.util.UtilDateTime.getDayStart(res);
					res2=new Date(res.getTime());
				}
			}
		}
		
		return res2;
	}
	
	public static Date getMonthEndFromDatetime(String monthStr, String timeZone, int year, Locale locale) {
		
		TimeZone currentTimeZone = null;		
		if (UtilValidate.isNotEmpty(timeZone)) {
			currentTimeZone = TimeZone.getTimeZone(timeZone);
		} else {
			currentTimeZone = TimeZone.getDefault();
		}		
		return getMonthEndFromDatetime(monthStr, currentTimeZone, year, locale);
				
	}
	
	/**
	 * Converte una data in un valore numerico
	 * @param value
	 * @param timeZone
	 * @param locale
	 * @return
	 * @throws ParseException 
	 */
	public static String dateConvertNumberString(String value, TimeZone timeZone, Locale locale) {		
		Date data = org.ofbiz.base.util.UtilDateTime.toDate(value, timeZone, locale);		
		
		Timestamp timestamp = getTimestampFromDate(data);
		int number = getIntervalInDays(DATE_MEASURE_REFERENCE, timestamp);
		return String.valueOf(number);	
	}
	
	/**
	 * Converte la data in valore numerico, e ritorna l'intero direttamente
	 * @param value
	 * @param timeZone
	 * @param locale
	 * @return
	 */
	public static int dateConvertNumber(String value, TimeZone timeZone, Locale locale) {      
	    Date data = org.ofbiz.base.util.UtilDateTime.toDate(value, timeZone, locale);       

	    Timestamp timestamp = getTimestampFromDate(data);
	    return getIntervalInDays(DATE_MEASURE_REFERENCE, timestamp);  
	}
	
	/**
	 * Converte un numero in una data
	 * @param number
	 * @param locale
	 * @return
	 */
	public static String numberConvertToDate(Double number, Locale locale) {
		if (UtilValidate.isEmpty(number) || number == 0) {
			return null;
		}
		
		Timestamp timestamp = org.ofbiz.base.util.UtilDateTime.addDaysToTimestamp(DATE_MEASURE_REFERENCE, number.intValue());
		return org.ofbiz.base.util.UtilDateTime.toDateString(new Date(timestamp.getTime()), locale);
	}
	
	
	/**
	 * Converte un numero in una data
	 * @param number
	 * @param stringLocale
	 * @return
	 */
	public static String numberConvertToDate(Double number, String stringLocale) {
		//TODO - controllare cosa arriva
		// Debug.log(numberConvertToDate(number, new Locale(stringLocale)));
		return numberConvertToDate(number, new Locale(stringLocale));
	}
	
	/**
	 * Intervallo in giorni tra due date considerante approssimazione nell'intero
	 * @param from
	 * @param thru
	 * @return
	 */
	public static int getIntervalInDays(Timestamp from, Timestamp thru) {
        if(thru == null){
        	return 0;
        }
        float diff = thru.getTime() - from.getTime();
        int gg = (int) Math.round( diff / (24 *60 *60 *1000));		
		
		return gg; 
    }
	
	/**
	 * ritorna ilTimestamp corrispondente alla data in input, o null se la data è null
	 * @param data
	 * @return
	 */
	public static Timestamp getTimestampFromDate(Date data) {
	    if(data == null) {
	        return null;
	    }
	    return new Timestamp(data.getTime());
	}
	
}
