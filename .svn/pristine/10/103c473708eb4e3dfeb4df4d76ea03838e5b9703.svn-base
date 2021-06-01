package com.mapsengineering.base.birt.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;

public class UtilNumber {

	private static int concatRomanToken(StringBuffer roman, int number, int tokenValue, String romanToken){
		int n = number;		
		while(n >= tokenValue){
            roman.append(romanToken);
            n -= tokenValue;
        }
		return n;
	}
	
	public static String convertNumberToRomanNumeral(int num){
		StringBuffer roman = new StringBuffer();
		int number = num;
		
		if(num <= 0 || num >=3999){
			return "";
		}
        
		number = concatRomanToken(roman, number, 1000, "M");
		number = concatRomanToken(roman, number, 900, "CM");
        number = concatRomanToken(roman, number, 500, "D");
        number = concatRomanToken(roman, number, 400, "CD");
        number = concatRomanToken(roman, number, 100, "C");
        number = concatRomanToken(roman, number, 90, "XC");
        number = concatRomanToken(roman, number, 50, "L");
        number = concatRomanToken(roman, number, 40, "XL");
        number = concatRomanToken(roman, number, 10, "X");
        number = concatRomanToken(roman, number, 9, "IX");
        number = concatRomanToken(roman, number, 5, "V");
        number = concatRomanToken(roman, number, 4, "IV");
        number = concatRomanToken(roman, number, 1, "I");
        
        return roman.toString();
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static Float sumAssocWeightNumber(Float a, Float b, Float c) {			
		return a + b - c;
	}
	
	/**
	 * Calcola la somma 
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	public static String sumAssocWeightOrKpiScoreWeightNumber(HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> parameters = UtilHttp.getParameterMap(request);        
        Locale locale = UtilHttp.getLocale(request);
        
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        
        Number amountTotValueNumber;
		try {
			amountTotValueNumber = nf.parse((String) parameters.get("amountTotValue"));
		} catch (ParseException e) {
			amountTotValueNumber = 0;
			e.printStackTrace();
		}
		
        Number amountNewNumber;
		try {
			amountNewNumber = nf.parse((String) parameters.get("amountNew"));
		} catch (ParseException e) {
			amountNewNumber = 0;
			e.printStackTrace();
		}
		
        Number amountOldNumber;
		try {
			amountOldNumber = nf.parse((String) parameters.get("amountOld"));
		} catch (ParseException e) {
			amountOldNumber= 0;
			e.printStackTrace();			
		}
        
        Float amountTotValueNew = sumAssocWeightNumber(amountTotValueNumber.floatValue(), amountNewNumber.floatValue(), amountOldNumber.floatValue());
        
        request.setAttribute("amountTotValueNew", nf.format(amountTotValueNew));
        
        return "success";
	}
	
	/**
	 * Moltiplica il valore passato per il peso e poi effettua somma per calcolo totali
	 * @param request
	 * @param response
	 * @return
	 */
	   public static String sumKpiWeightInline(HttpServletRequest request, HttpServletResponse response) {

	        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
	        Locale locale = UtilHttp.getLocale(request);
	        
	        NumberFormat nf = NumberFormat.getNumberInstance(locale);
	        
	        Number valueNew;
	        try {
	            valueNew = nf.parse((String) parameters.get("valueNew"));
	        } catch (ParseException e) {
	            valueNew = 0;
	            e.printStackTrace();
	        }
	        
	        Number kpiScoreWeight;
	        try {
	            kpiScoreWeight = nf.parse((String) parameters.get("kpiScoreWeight"));
	        } catch (ParseException e) {
	            kpiScoreWeight = 0;
	            e.printStackTrace();
	        }
	        
	        Float valueForKpiNew = valueNew.floatValue() * kpiScoreWeight.floatValue();
	        if (valueForKpiNew != null) {
	        	valueForKpiNew = valueForKpiNew  / 100;
	        }
	        
	        request.setAttribute("valueForKpiNew", nf.format(valueForKpiNew));
	        
	        Number valueTotOld;
	        try {
	            valueTotOld = nf.parse((String) parameters.get("valueTotOld"));
	        } catch (ParseException e) {
	            valueTotOld = 0;
	            e.printStackTrace();
	        }
	        
	        Number valueOld;
	        try {
	            valueOld = nf.parse((String) parameters.get("valueOld"));
	        } catch (ParseException e) {
	            valueOld= 0;
	            e.printStackTrace();            
	        }
	        Float valueForKpiOld = valueOld.floatValue() * kpiScoreWeight.floatValue();
	        if (valueForKpiOld != null) {
	        	valueForKpiOld = valueForKpiOld / 100;
	        }
            
	        Float valueTotNew = sumAssocWeightNumber(valueTotOld.floatValue(), valueForKpiNew.floatValue(), valueForKpiOld.floatValue());	        
	        request.setAttribute("valueTotNew", nf.format(valueTotNew));
	        
	        return "success";
	    }
	
	/**
	 * Calcola la somma di amountTotValue, con la lista di valori amountElementList
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	public static String sumAssocWeightOrKpiScoreWeightNumberAddNew(HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> parameters = UtilHttp.getParameterMap(request);        
        Locale locale = UtilHttp.getLocale(request);
        
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        
        Number amountTotValueNumber;
		try {
			amountTotValueNumber = nf.parse((String) parameters.get("amountTotValue"));
		} catch (ParseException e) {
			amountTotValueNumber = 0;
			e.printStackTrace();
		}
        
        Float amountElementTotValueNew = amountTotValueNumber.floatValue();
        
        String amountElementList = (String) parameters.get("amountElementList");        
        List<String> amountElement = StringUtil.toList(amountElementList, ";");        
        for(String amountString: amountElement) {        	
        	Number amount;
			try {
				amount = nf.parse(amountString);
			} catch (ParseException e) {
				amount = 0;
				e.printStackTrace();
			}
        	amountElementTotValueNew += amount.floatValue();
        }   
        
        request.setAttribute("amountElementTotValueNew", nf.format(amountElementTotValueNew));
        
        return "success";
	}
	
	public static String getFormatPattern(int decimalScale, String um){

        String pattern = "#,##0";
        if (decimalScale > 0) {
            pattern = pattern + ".";
            
            for(int i=0; i < decimalScale; i++ ){
                pattern = pattern.concat("0");
            }
        }
        if ("%".equals(um) || (um != null && um.indexOf("Perc") != -1)) {
            pattern = pattern + "%";
        }
        return pattern;
        
    }
        	
}
