package com.mapsengineering.base.convertercatalogs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.crypto.dsig.TransformException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.translatorcatalogs.common.E;

/**
 * Converter Catalogs from *Labels.xml to json
 *
 */
public class ConverterCatalogs {

    public static final String MODULE = ConverterCatalogs.class.getName();
    
    public static final String PATH = File.separator + "hot-deploy";
    public static final String FILE_EXTENSION = ".json";
    public static final String END_FILE_NAME = "labels.xml";
    public static final String PROP_OFBIZ_HOME = "ofbiz.home";
    private static Locale sl;
    private static Locale tl;

    /**
     * Main
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> converterCatalogs(DispatchContext dctx, Map<String, ? extends Object> context) {

        try {
            Debug.log("********************** INIZIO CONVERSIONE ********************");

            String slString = (String)context.get(E.sl.name());
            String tlString = (String)context.get(E.tl.name());
            
            if (UtilValidate.isNotEmpty(slString)) {
                sl = UtilMisc.parseLocale(slString);
            }
            tl = UtilMisc.parseLocale(tlString);

            String propValue = System.getProperty(PROP_OFBIZ_HOME);
            
            //prendo la lista di label.xml dalla radice
            List<File> files = FileUtil.getFiles(propValue+PATH, END_FILE_NAME);

            //vado a unire le etichette del file json con le etichette dei files xml
            new FilesConverter(sl, tl).filesConverter(files, propValue+tl.toString() + FILE_EXTENSION);

        } catch (IOException e) {
            Debug.logError(e, "################ ERROR IOException : " + tl.toString() + FILE_EXTENSION, MODULE);
        } catch (TransformException e) {
            Debug.logError(e, "################ ERROR TransformException : " + e, MODULE);
            return ServiceUtil.returnError("Error translator");
        } catch (Exception e) {
            Debug.logError(e, "################ ERROR Exception : " + e, MODULE);
            return ServiceUtil.returnError("Error translator");
        } 

        Debug.log("********************** FINE CONVERSIONE ********************");

        return ServiceUtil.returnSuccess();
    }

}
