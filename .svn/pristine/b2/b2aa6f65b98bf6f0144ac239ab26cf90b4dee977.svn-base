package com.mapsengineering.base.translatorcatalogs.entityname;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mapsengineering.base.translatorcatalogs.FilesTranslator;

/**
 * FilesFieldTranslator
 *
 */
public class FilesFieldTranslator extends FilesTranslator {
    public static final String MODULE = FilesFieldTranslator.class.getName();

    private static final String SEP = " - ";

    private static final Object _NA_ = "_NA_";

    private String entityName;
    private String fieldName;

    /**
     * Constructor
     * @param sl
     * @param tl
     * @param override
     * @param entityName
     * @param fieldName
     */
    public FilesFieldTranslator(Locale sl, Locale tl, boolean override, String entityName, String fieldName) {
        super(tl, tl, override);
        this.entityName = entityName;
        this.fieldName = fieldName;
    }

    protected void readXml(File file) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        doc = docBuilder.parse(file);

        NodeList listOfProperty = doc.getElementsByTagName(entityName);
        for (int i = 0; i < listOfProperty.getLength(); i++) {

            Node node = listOfProperty.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                getChildNode(element);

            }

        }

    }

    protected void getChildNode(Element element) {
        String language = element.getAttribute(fieldName);
        if (!_NA_.equals(language)) {
         // per la traduzione devo:
            // avere il text nella lingua di origine
            // non deve gia essere presente il separatore oppure se esiste il flag override deve essere impostato a true
            if (language.indexOf(SEP) == -1 || isOverride()) {
                String text = getRt().callUrlAndParseResult(getSl(), getTl(), language);
                element.setAttribute(fieldName, text + SEP + language);
            } else if (language.equals(getTl().getLanguage()) || language.equals(getTl().toString())) {
                Debug.logWarning("******" + language, MODULE);
            }
        }
    }

}
