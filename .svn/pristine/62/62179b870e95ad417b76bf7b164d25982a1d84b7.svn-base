package com.mapsengineering.base.translatorcatalogs.entityname;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.translatorcatalogs.common.E;

/**
 * Translator Fields from data.xml
 * @author dain
 *
 */
public class TranslatorFields {
    
	private static final String PROP_OFBIZ_HOME = "ofbiz.home";
    public static final String END_FILE_NAME = "data.xml";
    
    private static Locale sl;
    private static Locale tl;
    private static String entityName;
    private static String fieldName;
    private static boolean override;
       
    public static Map<String, Object> translatorFields(DispatchContext dctx, Map<String, ? extends Object> context) {
        
        try {
            Debug.log("********************** INIZIO TRADUZIONE ********************");
            
            String slString = (String)context.get(E.sl.name());
            String tlString = (String)context.get(E.tl.name());
            override = (Boolean)context.get(E.override.name());
            
            sl = UtilMisc.parseLocale(slString);
            tl = UtilMisc.parseLocale(tlString);
            
            entityName = (String)context.get(E.entityName.name());
            fieldName = (String)context.get(E.fieldName.name());
            
            //prendo la lista di label.xml dalla radice
            String propValue = System.getProperty(PROP_OFBIZ_HOME);
            String path = propValue + File.separator + "hot-deploy";
            List<File> files = FileUtil.getFiles(path, END_FILE_NAME);
            
            //vado a tradurre le etichette
            new FilesFieldTranslator(sl, tl, override, entityName, fieldName).filesTranslator(files);      
            
        } catch (Exception e) {
            Debug.log("################ ERROR Exception: "+ e);
            return ServiceUtil.returnError("Error translator");
        }
        
        
        Debug.log("********************** FINE TRADUZIONE ********************");
        
        return ServiceUtil.returnSuccess();
    }
    
    

}
