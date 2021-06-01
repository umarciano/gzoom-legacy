package com.mapsengineering.base.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * Common validation utility method
 * @author sandro
 *
 */
public class ValidationUtil {
	
    /**
     * Reserved record flag name
     */
    public static final String RESERVED_RECORD_FIELD = "isReserved";

    /**
     * Default value for empty pk field
     */
    public static final String _NA_ = "_NA_";
    
    private ValidationUtil() {}

    /**
     * Check datetime field with Locale
     * @param datetime to Check
     * @param locale
     * @return Date if converted, null otherwise
     */
    public static Timestamp checkDateTimeAgainstLocale(String dateTime, Locale locale) {

        try {
            return (Timestamp)ObjectType.simpleTypeConvert(dateTime, "Timestamp", null, locale);
        } catch (GeneralException e1) {

        }

        return null;
    }

    /**
     * Check date string field with Locale
     * @param date to Check
     * @param locale
     * @return Date if converted, null otherwise
     */
    public static Date checkDateAgainstLocale(String date, Locale locale) {

        //Testo prima col locale
        try {
            return (Date)ObjectType.simpleTypeConvert(date, "Date", null, locale);
        } catch (GeneralException e1) {

        }

        return null;
    }

    /**
     * Check time string field with Locale
     * @param time to Check
     * @param locale
     * @return Time if converted, null otherwise
     */
    public static Time checkTimeAgainstLocale(String time, Locale locale) {

        //Tento prima col locale
        try {
            return (Time)ObjectType.simpleTypeConvert(time, "Time", null, locale);
        } catch (GeneralException e1) {

        }

        return null;
    }

    /**
     * Check if record reserved, no updatable.
     * @param delegator
     * @param GenericValue with Pk setted
     * @return True if reserved (not updatable)
     */
    public static boolean checkIsReserved(Delegator delegator, GenericValue value) throws GenericEntityException {

        GenericValue found = delegator.findByPrimaryKey(value.getEntityName(), value.getPrimaryKey());

        if (UtilValidate.isNotEmpty(found)) {
            if (found.containsKey(RESERVED_RECORD_FIELD)
                    && "Y".equalsIgnoreCase(found.getString(RESERVED_RECORD_FIELD))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate integer number against locale.
     * The method accept separators and +/- sign before number.
     * @param num to convert
     * @param locale
     * @return Long converted or null if there are errors
     */
    public static Long checkIntegerAgainstLocale(String num, Locale locale)  {

        try {
            num = num.trim();

            //Controllo se la stringa contiene caratteri non numerici a parte i sepratori perché il parse successivo
            //cercherebbe di tradurre soli i numeri senza segnalare errore
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
            String groupSep = String.valueOf(dfs.getGroupingSeparator());
            String decimalSep = String.valueOf(dfs.getDecimalSeparator());

            String regex = "[-+]?\\b[0-9\\" + groupSep + "\\" + decimalSep + "]+\\b";

            if (!num.matches(regex)) {
                return null;
            }

            return DecimalFormat.getIntegerInstance(locale).parse(num).longValue();
        } catch (Exception e) {
            Debug.logError("Long conversion error: " + e.getMessage(), ValidationUtil.class.getName());
            return null;
        }
    }

    /**
     * Validate floating point number against locale.
     * The method accept separators and +/- sign before number.
     * @param num to convert
     * @param locale
     * @return Double converted or null if there are errors
     */
    public static Double checkFloatingPointAgainstLocale(String num, Locale locale)  {

        try {
            num = num.trim();

            //Controllo se la stringa contiene caratteri non numerici a parte i sepratori perché il parse successivo
            //cercherebbe di tradurre soli i numeri senza segnalare errore
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
            String groupSep = String.valueOf(dfs.getGroupingSeparator());
            String decSep = String.valueOf(dfs.getDecimalSeparator());

            String regex = "[-+]?\\b[0-9\\" + groupSep + "]+(\\" + decSep + "[0-9]+)?\\b";

            if (!num.matches(regex)) {
                return null;
            }
            
            NumberFormat nf = DecimalFormat.getNumberInstance(locale);
            Number tempNum = nf.parse(num);
            return tempNum.doubleValue();
        } catch (Exception e) {
            Debug.logError("Double conversion error : " + e.getMessage(), ValidationUtil.class.getName());
            return null;
        }
    }

    
    /**
     * 
     * @param s
     * @return
     */
    public static boolean isEmptyOrNA(String s) {
    	return ((s == null) || (s.length() == 0) || (_NA_.equals(s)));
    }
    
    
    /**
     * 
     * @param s
     * @return
     */
    public static String emptyIfNull(String s) {
        if(s == null) {
            return "";
        }
        return s;
    }
    
    /**
     * Controlla se le due stringhe fanno rifermiento alla stesso nodo dell'albero,
     *  perche se creo nuova scheda:
      WorkEffortAssocExtView_root_42266 e WorkEffortAssocExtView_child_42266 indicano in realta lo stesso nodo
      invece nell'organigramma:
      GlAccountClassFolder_root_null e GlAccountClassFolder_root indicano in realta lo stesso nodo
     * @param idStr1
     * @param idStr2
     * @return
     */
    public static  boolean checkSameTreeNodeId(String idStr1, String idStr2) {
        if (idStr1.equals(idStr2)) {
            return true;
        }
        List<String> id1 = splitTreeNodeRootAndChild(idStr1);
        List<String> id2 = splitTreeNodeRootAndChild(idStr2);
        
        return checkSameTreeNodeListId(id1, id2);
    }

    private static List<String> splitTreeNodeRootAndChild(String idStr) {
        List<String> ids = new ArrayList<String>();
        if(idStr.indexOf("_root_") > 0) {
            ids = StringUtil.split(idStr, "_root_");
        }
        if(idStr.indexOf("_child_") > 0) {
            ids = StringUtil.split(idStr, "_child_");
        }
        
        return ids;
    }

    private static boolean checkSameTreeNodeListId(List<String> id1, List<String> id2) {
        if(UtilValidate.isNotEmpty(id1) && UtilValidate.isNotEmpty(id2)) {
            // return false se l'entityName e' diversa
            if(!id1.get(0).equals(id2.get(0))) {
                return false;
            }
            
            // return false se l'id e' diverso
            if (id1.size() == id2.size()) {
                if (!id1.get(1).equals(id2.get(1))) {
                     return false;
                }
                return true;
            }
            
            // return false se sono nel caso particolare in cui un id e' null e l'altro e' vuoto
            if (!checkIsNull(id1, id2.size()) && !checkIsNull(id2, id1.size())){
                return false;
            }
            
        }
        return true;
    }

    /**
     * 
     * @param idList1
     * @param size of idList2
     * @return
     */
    private static boolean checkIsNull(List<String>  id1List, int id2Size) {
        return id1List.size() > 1 && "null".equals(id1List.get(1)) && id2Size == 1;
    }
}
