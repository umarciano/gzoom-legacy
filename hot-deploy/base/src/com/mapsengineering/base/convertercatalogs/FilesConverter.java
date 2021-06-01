package com.mapsengineering.base.convertercatalogs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mapsengineering.base.translatorcatalogs.common.E;
import com.mapsengineering.base.util.JSONUtils;

/**
 * File Converter for create label in json file
 * @author dain
 *
 */
public class FilesConverter {
    public static final String MODULE = FilesConverter.class.getName();

    private static final String FORM_FIELD_TITLE = "FormFieldTitle_";

    private Locale sl;
    private Locale tl;

    private Document doc;

    /**
     * Constructor
     * @param sl2
     * @param tl2
     */
    public FilesConverter(Locale sl2, Locale tl2) {
        this.sl = sl2;
        this.tl = tl2;
    }

    /**
     * Main
     * @param files
     * @param filename
     * @throws TransformerException
     * @throws IOException
     */
    public void filesConverter(List<File> files, String filename) throws TransformerException, IOException {
        Map<String, String> catalogs = new TreeMap<String, String>();
        for (File file : files) {
            catalogs.putAll(filesConverter(file));
        }
        writeFile(filename, catalogs);
    }

    /**
     * Read file 
     * @param file
     * @return
     */
    public Map<String, String> filesConverter(File file) {
        Map<String, String> catalogs = new TreeMap<String, String>();
        Debug.log("*** Start Translator file : " + file.getName());
        try {
            catalogs = readXml(file);
        } catch (IOException e) {
            Debug.logError(e, "################ ERROR IOException : " + file.getName(), MODULE);
        } catch (ParserConfigurationException e) {
            Debug.logError(e, "################ ERROR ParserConfigurationException : " + file.getName(), MODULE);
        } catch (Exception e) {
            Debug.logError(e, "################ ERROR Exception : " + file.getName(), MODULE);
        }
        Debug.log("*** End Translator file : " + file.getName());
        return catalogs;
    }

    private Map<String, String> readXml(File file) throws SAXException, IOException, ParserConfigurationException {
        Map<String, String> catalogs = new TreeMap<String, String>();
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        doc = docBuilder.parse(file);

        NodeList listOfProperty = doc.getElementsByTagName(E.property.name());
        for (int i = 0; i < listOfProperty.getLength(); i++) {

            Node node = listOfProperty.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                Debug.log("*** node Element : " + element.getAttributeNode(E.key.name()));
                Map<String, String> catalog = getChildNode(element);
                catalogs.putAll(catalog);
            }
        }
        return catalogs;
    }

    private Map<String, String> getChildNode(Element elementParent) {
        Map<String, String> catalog = new TreeMap<String, String>();
        Element nodeSl = null;
        Element nodeTl = null;
        boolean foundLanguage = false;
        NodeList listOfValue = elementParent.getElementsByTagName(E.value.name());

        for (int i = 0; i < listOfValue.getLength(); i++) {

            Node node = listOfValue.item(i);
            Element element = (Element)node;
            String language = element.getAttribute("xml:lang");
            Debug.log("***language : " + language);
            Debug.log("***value : " + element.getTextContent());

            if (language.equals(sl.getLanguage()) || language.equals(sl.toString())) {
                nodeSl = element;
                foundLanguage = true;
            } else if (language.equals(tl.getLanguage()) || language.equals(tl.toString())) {
                nodeTl = element;
            }

        }

        if (nodeSl != null && nodeTl != null) {
            catalog.put(nodeSl.getTextContent(), nodeTl.getTextContent());
        } else if (!foundLanguage && nodeSl == null && nodeTl != null) {
            String keyString = elementParent.getAttribute("key");
            if (keyString.indexOf(FORM_FIELD_TITLE) == 0) {
                keyString = keyString.substring(FORM_FIELD_TITLE.length());
                keyString = StringUtils.capitalize(insertSpaceInMultiCaseStringAtCaps(keyString));
            }
            catalog.put(keyString, nodeTl.getTextContent());
        }
        return catalog;
    }

    /**
     * Format
     * @param s
     * @return
     */
    public static String insertSpaceInMultiCaseStringAtCaps(String s) {
        char[] cArray = s.toCharArray();
        StringBuffer result = new StringBuffer();
        result = result.append(cArray[0]);
        for (int i = 1; i < cArray.length; i++) {
            char c = cArray[i];
            if (c >= 65 && c <= 90) {
                result.append(" " + c);
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * Write in file the translations (merge from file json and label in xml) and the formats
     * @param filename
     * @param catalogs: Map<String, String>, label from *Labels.xml
     * @throws TransformerException
     * @throws IOException
     */
    @SuppressWarnings({"unchecked"})
    private void writeFile(String filename, Map<String, String> catalogs) throws TransformerException, IOException {
        BufferedReader in = null;
        FileOutputStream outputStream = null;
        try {
        	File file = new File(filename);
        	file.createNewFile();
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));

            in = new BufferedReader(reader);
            String inputLine;
            StringBuilder buf = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                buf.append(inputLine).append("\n");
            }
            
            Map<String, Object> catalog = new HashMap<String,Object>();
            Map<String, String> catalogsIn = new HashMap<String,String>();
            if(buf!=null && !buf.toString().equals(""))
            {
            	catalog = JSONUtils.fromJson(buf.toString());
            	catalogsIn = (Map<String, String>)catalog.get("translations");
            }

            Iterator<Entry<String, String>> entryIt = catalogsIn.entrySet().iterator();
            while (entryIt.hasNext()) {
                Entry<String, String> entry = entryIt.next();
                catalogs.put(entry.getKey(), (String) entry.getValue());
            }
            Map<String, String> formats = (Map<String, String>)catalog.get("formats"); //TODO - il format non viene scritto se e' un nuovo file

            Map<String, Object> mappa = UtilMisc.toMap("translations", (Object)catalogs, "formats", (Object)formats);
            
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mappa);
            
            outputStream = new FileOutputStream(new File(filename));
            Debug.log("*** jsonStr=" + indented);
            outputStream.write(indented.getBytes("UTF-8"), 0, indented.getBytes("UTF-8").length);
        } finally {
            if (in != null) {
                in.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
