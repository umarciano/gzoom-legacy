package com.mapsengineering.base.translatorcatalogs;

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
 * TranslatorCatalogs
 *
 */
public class TranslatorCatalogs {
    
	private static final String PROP_OFBIZ_HOME = "ofbiz.home";
    public static final String END_FILE_NAME = "labels.xml";
    
    private static Locale sl;
    private static Locale tl;
    private static boolean override;
       
    /**
     * translator Catalogs
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> translatorCatalogs(DispatchContext dctx, Map<String, ? extends Object> context) {
        
        try {
            Debug.log("********************** INIZIO TRADUZIONE ********************");
            
            String slString = (String)context.get(E.sl.name());
            String tlString = (String)context.get(E.tl.name());
            override = (Boolean)context.get(E.override.name());
            
            sl = UtilMisc.parseLocale(slString);
            tl = UtilMisc.parseLocale(tlString);
            
            //prendo la lista di label.xml dalla radice
            String propValue = System.getProperty(PROP_OFBIZ_HOME);
            String path = propValue + File.separator + "hot-deploy";
            List<File> files = FileUtil.getFiles(path, END_FILE_NAME);
            
            //vado a tradurre le etichette
            new FilesTranslator(sl, tl, override).filesTranslator(files);
            
            // X TEST: creare un file e inserire il percorso
            //new FilesTranslator(sl, tl, override).fileTranslator(new File("C:\\Users\\asma\\workspaceGZoom\\GZoom\\hot-deploy\\base\\config\\APIPPOUiLabels.xml"));
          
            //prendo la lista di <sl>.properties dalla radice
            files = new FilePropertyCatalogs(sl).getFileCatalogs(path);
            
            //vado a tradurre le etichette
            new FilesPropertyTranslator(sl, tl).filesTranslator(files);
            
            
        } catch (Exception e) {
            Debug.log("################ ERROR Exception: "+ e);
            return ServiceUtil.returnError("Error translator");
        }
        
        
        Debug.log("********************** FINE TRADUZIONE ********************");
        
        return ServiceUtil.returnSuccess();
    }
    
    

}
