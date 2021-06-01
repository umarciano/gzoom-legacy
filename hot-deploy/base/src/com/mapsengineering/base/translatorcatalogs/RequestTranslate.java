package com.mapsengineering.base.translatorcatalogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.conversion.JSONConverters.JSONToList;
import org.ofbiz.base.conversion.JSONConverters.JSONToMap;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;

/**
 * RequestTranslate
 *
 */
public class RequestTranslate {
    
    private HttpClient client;

    /**
     * Constructor
     */
    public RequestTranslate() {
        client = new HttpClient(new SimpleHttpConnectionManager());
        client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
    }
    
    /**
     * callUrlAndParseResult
     * @param langFrom
     * @param langTo
     * @param word
     * @return
     */
    public String callUrlAndParseResult(Locale langFrom, Locale langTo,  String word) {
        String result = "";
        
        GetMethod get = null;
        InputStream inputStream = null;
        StringBuffer response;
        BufferedReader in = null;
        try {
            String url = "https://translate.googleapis.com/translate_a/single?"+
                    "client=gtx&"+
                    "sl=" + langFrom + 
                    "&tl=" + langTo + 
                    "&dt=t&q=" + URLEncoder.encode(word, "UTF-8"); 
            Debug.log("***URL="+url);
            
            get = new GetMethod(url);
            get.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            get.setFollowRedirects(true);
            client.executeMethod(get);
            inputStream = get.getResponseBodyAsStream();
            
            in = new BufferedReader(new InputStreamReader(get.getResponseBodyAsStream(), Charset.forName("UTF-8")));
            String inputLine;
            response = new StringBuffer();
           
            while ((inputLine = in.readLine()) != null) {
             response.append(inputLine);
            }
            result = parseResult(response.toString());
        } catch (UnsupportedEncodingException e) {
            Debug.log("################ ERROR UnsupportedEncodingException: " + e.getMessage());
        } catch (IOException e) {
            Debug.log("################ ERROR IOException: " + e.getMessage());
        } catch (Exception e) {
            Debug.log("################ ERROR Exception: " + e.getMessage());
        } finally {
            try {
                get.releaseConnection();
                inputStream.close();
                in.close();
            } catch (RuntimeException e) {
                Debug.log("################ ERROR release connection; Error was: " + e.getMessage());
                throw e;
            } catch (IOException e) {
                Debug.log("################ ERROR closing inputStream; Error was: " + e.getMessage());
            }
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private String parseResult(String input) throws Exception {
        Debug.log("*** parseResult="+input); 
        /*
         * input [[["ciao","hello",null,null,1]],,"en"]
         * We have to get 'ciao' from this json.
         */
        JSON jsonObject = JSON.from(input);
        JSONToList jsonList = new JSONToList();
        try {
            List<Object> firstList = jsonList.convert(jsonObject);
            List<Object> secondList = (List<Object>)firstList.get(0);
            List<Object> thirdList = (List<Object>)secondList.get(0);
            String text = (String)thirdList.get(0);
            Debug.log("*** response="+input + "   -> text="+ text); 
            return text;
        } catch (ConversionException e) {
            Debug.logError(e, "Error fromJson str " + input);
        }
        return null;
   }

    
}
