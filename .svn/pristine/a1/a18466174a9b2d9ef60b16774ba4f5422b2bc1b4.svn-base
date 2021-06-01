package com.mapsengineering.base.translatorcatalogs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;

/**
 * FilesPropertyTranslator
 *
 */
public class FilesPropertyTranslator {

    private static final String SEP = "_";
    private Locale sl;
    private Locale tl;

    private RequestTranslate rt;

    /**
     * Constructor
     * @param sl
     * @param tl
     */
    public FilesPropertyTranslator(Locale sl, Locale tl) {
        this.sl = sl;
        this.tl = tl;
        rt = new RequestTranslate();
    }

    /**
     * Main
     * @param files
     */
    public void filesTranslator(List<File> files) {
        for (File file : files) {
            fileTranslator(file);
        }
    }

    /**
     * translator
     * @param file
     */
    public void fileTranslator(File file) {
        Debug.log("*** Start Translator file : " + file.getName());
        try {
            Map<String, String> catalogs = readProperties(file);
            String filename = file.getAbsolutePath().replace(SEP + sl.toString(), SEP + tl.toString());
            writeProperties(filename, catalogs);

        } catch (IOException e) {
            Debug.log("################ ERROR IOException : " + file.getName());
        } catch (Exception e) {
            Debug.log("################ ERROR Exception : " + file.getName());
        }
        Debug.log("*** End Translator file : " + file.getName());
    }

    private Map<String, String> readProperties(File file) throws MalformedURLException {
        Map<String, String> catalog = new HashMap<String, String>();

        Properties properties = UtilProperties.getProperties(file.getPath());
        for (Iterator<Object> i = properties.keySet().iterator(); i.hasNext();) {
            String key = (String)i.next();
            String label = properties.getProperty(key);

            String text = rt.callUrlAndParseResult(sl, tl, label);
            Debug.log("***   text origin : " + label + " text translated=" + text);
            catalog.put(key, text);
        }
        return catalog;
    }

    private void writeProperties(String filename, Map<String, String> catalogs) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(new File(filename));
        
        
        for (Entry<String, String> entry: catalogs.entrySet()) {
            String key = entry.getKey();
            String label = entry.getValue();
            
            String property = key + "=" + label + "\n";
            Debug.log("***  property : " + property);
            outputStream.write(property.getBytes(), 0, property.getBytes().length);
        }
        outputStream.close();
    }

}
