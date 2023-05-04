package com.mapsengineering.base.birt.util;

import java.util.Date;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.config.DatasourceInfo;
import org.ofbiz.entity.config.EntityConfigUtil;

public class UtilsConvertJdbc {
    
    public static final String DEFAULT_GROUP_NAME = "org.ofbiz";
    public static final String DATE_FORMAT_ORACLE = "MM/DD/YYYY";
    
    /**
     * Utilizzato per convertire la data nel formato corretto in base al tipo di DB utilizzato
     * @param date
     * @param delegator
     * @return
     */
    public static String getConvertDateToDateJdbc(Date date, Delegator delegator) {
        
        String dateString = "'" + org.ofbiz.base.util.UtilDateTime.toSqlDateString(date) + "'";
        
        DatasourceInfo datasourceInfo = getDatasourceInfo(DEFAULT_GROUP_NAME, delegator);
        String fieldTypeName = datasourceInfo.fieldTypeName;
        if (isOracle(fieldTypeName)) {
            dateString = getConvertDateToDateOracle(date);
        } else if (isMsSql(fieldTypeName)) {
            dateString = getConvertDateToDateMsSql(dateString);
        }
        return dateString;
    }
    
    /**
     * Utilizzato per ricavare l'ultimo giorno di un anno
     * @param date
     * @param delegator
     * @return 
     */
    public static String getFineAnno(String date, Delegator delegator) {
        
        String dateString = " DATE_FORMAT(" + date + " ,'%Y-12-31') ";
        
        DatasourceInfo datasourceInfo = getDatasourceInfo(DEFAULT_GROUP_NAME, delegator);
        String fieldTypeName = datasourceInfo.fieldTypeName;
        if (isOracle(fieldTypeName) || isPostgres(fieldTypeName)) {
            dateString = " TO_DATE(TO_CHAR(" + date + ", 'YY-12-31'), 'YY-MM-DD') ";
        } else if (isMsSql(fieldTypeName)) {
            dateString = " DATEADD(yy, DATEDIFF(yy, 0, " + date + ") + 1, -1) ";
        }
        return dateString;
    }
    
    /**
     * Utilizzato per ricavare il primo giorno di un anno
     * @param date
     * @param delegator
     * @return
     */
    public static String getInizioAnno(String date, Delegator delegator) {
        
        String dateString = " DATE_FORMAT(" + date + " ,'%Y-01-01') ";
        
        DatasourceInfo datasourceInfo = getDatasourceInfo(DEFAULT_GROUP_NAME, delegator);
        String fieldTypeName = datasourceInfo.fieldTypeName;
        if (isOracle(fieldTypeName) || isPostgres(fieldTypeName)) {
            dateString = " TO_DATE(TO_CHAR(" + date + ", 'YY-01-01'), 'YY-MM-DD') ";
        } else if (isMsSql(fieldTypeName)) {
            dateString = " DATEADD(yy, DATEDIFF(yy, 0, " + date + ")), 0) ";
        }
        return dateString;
    }
    
    private static String getConvertDateToDateMsSql(String dateString) {
        return "CONVERT(DATETIME, " + dateString + ", 121)";
    }
    
    private static String getConvertDateToDateOracle(Date date) {
        String dateString = org.ofbiz.base.util.UtilDateTime.toDateString(date);
        return "TO_DATE('" + dateString + "','" + DATE_FORMAT_ORACLE +"')";
    }
    
    private static DatasourceInfo getDatasourceInfo(String groupName, Delegator delegator) {
        String helperName = delegator.getGroupHelperName(groupName);
        if (helperName != null) {
            DatasourceInfo datasourceInfo = EntityConfigUtil.getDatasourceInfo(helperName);
            if (datasourceInfo != null && UtilValidate.isNotEmpty(datasourceInfo)) {
                return datasourceInfo;
            }
        }
        return null;
    }
    
    private static boolean isMsSql(String type) {
        if ("mssql".equals(type)) {
            return true;
        }
        return false;
    }
    
    private static boolean isPostgres(String type) {
        if ("postgres".equals(type)) {
            return true;
        }
        return false;
    }
    
    private static boolean isOracle(String type) {
        if ("oracle".equals(type)) {
            return true;
        }
        return false;
    }
    
    /**
     * Utilizzato per convertire la funziona di adddate
     * @param interval = day per aggiungere giorni alla data
     * @param number = campo che contiene il numero di giorni che devono essere aggiunti
     * @param date = campo da dove prendere la data
     * @param delegator
     * @return
     */
    public static String getConvertAddDateToJdbc(String interval, String number, String date, Delegator delegator) {
        
        String addDateString = "";
        
        DatasourceInfo datasourceInfo = getDatasourceInfo(DEFAULT_GROUP_NAME, delegator);
        String fieldTypeName = datasourceInfo.fieldTypeName;
        if (isMsSql(fieldTypeName)) {
            addDateString = getConvertAddDateToDateMsSql(interval, number, date);
        } else if (isOracle(fieldTypeName)) {
            addDateString = getConvertAddDateToDateOracle(number, date);
        } else if (isPostgres(fieldTypeName)) {
            addDateString = getConvertAddDateToDatePostgres(interval, number, date);
        } else {
            addDateString = getConvertAddDateToDateMySql(interval, number, date);
        }
        
        return addDateString;
    }
    
    /**
     * 
     * @param interval = day per aggiungere giorni alla data
     * @param number = campo che contiene il numero di giorni che devono essere aggiunti
     * @param date 
     * @param delegator
     * @return
     */
    public static String getConvertAddDateToJdbc(String interval, String number, Date date, Delegator delegator) {
        
        String addDateString = "";
        DatasourceInfo datasourceInfo = getDatasourceInfo(DEFAULT_GROUP_NAME, delegator);
        String fieldTypeName = datasourceInfo.fieldTypeName;
        if (isMsSql(fieldTypeName)) {
            addDateString = getConvertAddDateToDateMsSql(interval, number, date);
        } else if (isOracle(fieldTypeName)) {
            addDateString = getConvertAddDateToDateOracle(number, date);
        } else if (isPostgres(fieldTypeName)) {
            addDateString = getConvertAddDateToDatePostgres(interval, number, date);
        } else{
            addDateString = getConvertAddDateToDateMySql(interval, number, date);
        }
        
        return addDateString;
    }
    

    
    /**
     * 
     * DATEADD (datepart, number, date) 
     * @param interval
     * @param number
     * @param date
     * @return
     */
    private static String getConvertAddDateToDateMsSql(String interval, String number, String date) {
        return "DATEADD(" + interval + ", " + number + ", " + date +")";
    }
    
    private static String getConvertAddDateToDateMsSql(String interval, String number, Date date) {
        String dateString = "'" + org.ofbiz.base.util.UtilDateTime.toSqlDateString(date) + "'";
        return "DATEADD(" + interval + ", " + number + ", " + getConvertDateToDateMsSql(dateString) +")";
        //return "DATEADD(" + interval + ", " + number + ", " + dateString +")";
    }
    
    
    /**
     * ADDDATE(date, INTERVAL expr unit)
     * @param interval
     * @param number
     * @param date
     * @return
     */
    private static String getConvertAddDateToDateMySql(String interval, String number, String date) {
        return "ADDDATE(" + date + ", INTERVAL " + number + " " + interval +")";
    }
    private static String getConvertAddDateToDateMySql(String interval, String number, Date date) {
        String dateString = "'" + org.ofbiz.base.util.UtilDateTime.toSqlDateString(date) + "'";
        return "ADDDATE(" + dateString + ", INTERVAL " + number + " " + interval +")";
    }
    
    /**
     * date + 'number day'
     * @param interval
     * @param number
     * @param date
     * @return
     */
    private static String getConvertAddDateToDatePostgres(String interval, String number, String date) {
        return date + " + (interval '1 " + interval + "' * " + number + ")";
    }
    private static String getConvertAddDateToDatePostgres(String interval, String number, Date date) {
        String dateString = "'" + org.ofbiz.base.util.UtilDateTime.toSqlDateString(date) + "'";
        return dateString + " + (interval '1 " + interval + "' * " + number + ")";
    }
    
    /**
     * date + 1  
     * @param interval
     * @param number
     * @param date
     * @return
     */
    private static String getConvertAddDateToDateOracle(String number, String date) {
        return date + " + " + number ;
    }
    private static String getConvertAddDateToDateOracle(String number, Date date) {
        return getConvertDateToDateOracle(date) + " + " + number ;
    }
    
    /**
     * Toglie l'ora da una data
     * 
     * @param date
     * @param delegator
     * @return 
     */
    public static String getFormatDateWithoutTime(String date, Delegator delegator) {
        
        String dateString = "DATE_FORMAT(" + date +  " ,'%Y-%m-%d')";
        
        DatasourceInfo datasourceInfo = getDatasourceInfo(DEFAULT_GROUP_NAME, delegator);
        String fieldTypeName = datasourceInfo.fieldTypeName;
        if (isOracle(fieldTypeName) || isPostgres(fieldTypeName)) {
            dateString = " TO_DATE(TO_CHAR(" + date + ", 'YYYY-MM-DD'), 'YYYY-MM-DD') ";
        } else if (isMsSql(fieldTypeName)) {
            dateString = " CONVERT(date, "  + date + " ) ";
        }
        return dateString;
    }
}
