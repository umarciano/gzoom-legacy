package com.mapsengineering.base.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

/**
 * Generic Util for Date
 *
 */
public class DateUtilService {

    public static final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'Z");
    public static final SimpleDateFormat isoDateFormatNoTzValue = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'");

    /**
     * Constructor
     */
    private DateUtilService() {
    }

    /**
     * Return the day before
     * @param date
     * @return
     */
    public static Date getPreviousDay(Date date) {
        return prepareDay(date, -1);
    }

    /**
     * Prepare the date
     * @param date
     * @return
     */
    private static Date prepareDay(Date date, int dayToAdd) {
        if (UtilValidate.isEmpty(date)) {
            return null;
        }
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy");
        int year = Integer.valueOf(simpleDateformat.format(date));
        SimpleDateFormat simpleDateformatMonth = new SimpleDateFormat("MM");
        int month = Integer.valueOf(simpleDateformatMonth.format(date)) - 1;
        SimpleDateFormat simpleDateformatDay = new SimpleDateFormat("dd");
        int day = Integer.valueOf(simpleDateformatDay.format(date));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.add(Calendar.DAY_OF_YEAR, dayToAdd);
        return cal.getTime();
    }

    /**
     * LastDay of PrevMonth
     * @param date
     * @return
     */
    public static Date getLastDayOfPrevMonth(Date date) {
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy");
        int year = Integer.valueOf(simpleDateformat.format(date));
        SimpleDateFormat simpleDateformatMonth = new SimpleDateFormat("MM");
        int month = Integer.valueOf(simpleDateformatMonth.format(date)) - 1;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }

    /**
     * Return the day after
     * @param date
     * @return
     */
    public static Date getNextDay(Date date) {
        return prepareDay(date, 1);
    }
    
    /**
     * Return date
     * @param dateString
     * @param errorMapList
     * @return
     */
    public static Timestamp parseIsoDateString(String dateString, List<Map<String, String>> errorMapList) {
        if (UtilValidate.isEmpty(dateString)) return null;

        Date dateTimeInvReceived = null;
        try {
            dateTimeInvReceived = isoDateFormat.parse(dateString);
        } catch (ParseException e) {
            Debug.log("Message does not have timezone information in date field");
            try {
                dateTimeInvReceived = isoDateFormatNoTzValue.parse(dateString);
            } catch (ParseException e1) {
                String errMsg = "Error parsing Date: " + e1.toString();
                if (errorMapList != null) errorMapList.add(UtilMisc.<String, String>toMap("reasonCode", "ParseException", "description", errMsg));
                Debug.logError(e, errMsg);
            }
        }

        Timestamp snapshotDate = null;
        if (dateTimeInvReceived != null) {
            snapshotDate = new Timestamp(dateTimeInvReceived.getTime());
        }
        return snapshotDate;
    }
}
