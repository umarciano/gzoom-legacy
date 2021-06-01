package com.mapsengineering.base.translatorcatalogs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ofbiz.base.util.Debug;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mapsengineering.base.translatorcatalogs.common.E;

/**
 * FilesTranslator
 *
 */
public class FilesTranslator {

    private Locale sl;
    private Locale tl;
    private boolean override;

    private RequestTranslate rt;
    protected Document doc;

    /**
     * Constructor
     * @param sl
     * @param tl
     * @param override
     */
    public FilesTranslator(Locale sl, Locale tl, boolean override) {
        this.setSl(sl);
        this.setTl(tl);
        this.setOverride(override);
        setRt(new RequestTranslate());

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
            readXml(file);
            writeXml(file);

        } catch (TransformerException e) {
            Debug.log("################ ERROR  TransformerException : " + file.getName());
        } catch (SAXException e) {
            Debug.log("################ ERROR SAXException : " + file.getName());
        } catch (IOException e) {
            Debug.log("################ ERROR IOException : " + file.getName());
        } catch (ParserConfigurationException e) {
            Debug.log("################ ERROR ParserConfigurationException : " + file.getName());
        } catch (Exception e) {
            Debug.log("################ ERROR Exception : " + file.getName());
        }
        Debug.log("*** End Translator file : " + file.getName());
    }

    protected void readXml(File file) throws SAXException, IOException, ParserConfigurationException {

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        doc = docBuilder.parse(file);

        NodeList listOfProperty = doc.getElementsByTagName(E.property.name());
        for (int i = 0; i < listOfProperty.getLength(); i++) {

            Node node = listOfProperty.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                Debug.log("*** node Element : " + element.getAttributeNode(E.key.name()));
                getChildNode(element);

            }

        }

    }

    protected void getChildNode(Element elementParent) {

        Element nodeSl = null;
        Element nodeTl = null;

        NodeList listOfValue = elementParent.getElementsByTagName(E.value.name());

        for (int i = 0; i < listOfValue.getLength(); i++) {

            Node node = listOfValue.item(i);
            Element element = (Element)node;
            String language = element.getAttribute("xml:lang");
            //Debug.log("***language : "+language);
            //Debug.log("***value : "+element.getTextContent());

            if (language.equals(getSl().getLanguage()) || language.equals(getSl().toString())) {
                nodeSl = element;
            } else if (language.equals(getTl().getLanguage()) || language.equals(getTl().toString())) {
                nodeTl = element;
            }
        }

        // per a traduzione devo:
        // avere il text nella lingua di origine
        // non devo avere la lingua di destinazione oppure se esiste il flag override deve essere impostato a true
        if (nodeSl != null && (nodeTl == null || isOverride())) {
            String text = getRt().callUrlAndParseResult(getSl(), getTl(), nodeSl.getTextContent());
            Debug.log("***   text origin : " + nodeSl.getTextContent() + " text translated=" + text);

            if (nodeTl == null) {

                // append a new node to staff
                nodeTl = doc.createElement(E.value.name());
                nodeTl.setAttribute("xml:lang", getTl().toString());
                nodeTl.appendChild(doc.createTextNode(text));
                elementParent.appendChild(nodeTl);

            } else {
                nodeTl.setTextContent(text);
            }

        }

    }

    private void writeXml(File file) throws TransformerException {

        // write the content into xml file
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "ASCII");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        //transformer.setOutputProperty(OutputKeys.METHOD, "text");

        transformer.transform(new DOMSource(doc), new StreamResult(file));

    }

    /**
     * @return the sl
     */
    public Locale getSl() {
        return sl;
    }

    /**
     * @param sl the sl to set
     */
    public void setSl(Locale sl) {
        this.sl = sl;
    }

    /**
     * @return the tl
     */
    public Locale getTl() {
        return tl;
    }

    /**
     * @param tl the tl to set
     */
    public void setTl(Locale tl) {
        this.tl = tl;
    }

    /**
     * @return the override
     */
    public boolean isOverride() {
        return override;
    }

    /**
     * @param override the override to set
     */
    public void setOverride(boolean override) {
        this.override = override;
    }

    /**
     * @return the rt
     */
    public RequestTranslate getRt() {
        return rt;
    }

    /**
     * @param rt the rt to set
     */
    public void setRt(RequestTranslate rt) {
        this.rt = rt;
    }
}
