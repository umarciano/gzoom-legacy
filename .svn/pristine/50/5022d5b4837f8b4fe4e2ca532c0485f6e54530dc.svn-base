package com.mapsengineering.base.svnutil;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.MessageUtil;

/**
 * File svninfo Reader
 *
 */
public class SvnInfoXmlReader {

    public static final char DIR_SEPARATOR = '/';
    public static final String ELEM_ENTRY = "entry";
    public static final String ELEM_COMMIT = "commit";
    public static final String ELEM_URL = "url";
    public static final String ELEM_DATE = "date";
    public static final String ATTR_REVISION = "revision";

    private String url;
    private String revision;
    private String isoDate;

    /**
     * Get svninfo url
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get svninfo revision
     * @return
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Get svninfo isoDate
     * @return
     */
    public String getIsoDate() {
        return isoDate;
    }

    /**
     * Reset field
     */
    public void reset() {
        url = null;
        revision = null;
        isoDate = null;
    }

    /**
     * Read location of svninfo
     * @param location
     * @return
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public Document read(String location) throws SAXException, ParserConfigurationException, IOException {
        reset();
        URL docUrl = FlexibleLocation.resolveLocation(location);
        Document doc = UtilXml.readXmlDocument(docUrl, false);
        if (doc != null) {
            Element infoElem = doc.getDocumentElement();
            Element infoEntryElem = UtilXml.firstChildElement(infoElem, ELEM_ENTRY);
            Element infoEntryCommitElem = UtilXml.firstChildElement(infoEntryElem, ELEM_COMMIT);
            url = UtilXml.childElementValue(infoEntryElem, ELEM_URL, null);
            revision = UtilXml.elementAttribute(infoEntryCommitElem, ATTR_REVISION, null);
            isoDate = UtilXml.childElementValue(infoEntryCommitElem, ELEM_DATE, null);
        }
        return doc;
    }

    /**
     * Return url, trunk if file does not exists
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> getVersions(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        SvnInfoXmlReader svnInfo = SvnInfoXmlReaderFactory.newSvnInfoXmlReader();
        Locale locale = (Locale)context.get(ServiceLogger.LOCALE);
        String baseProductLabel = UtilProperties.getMessage("BaseUiLabels", "BaseProduct", locale);
        String strVersionLabel = "trunk";
        String customVersionLabel = DIR_SEPARATOR + "trunk";
        try {
            svnInfo.read("component://base/config/svninfo.xml");
            if(UtilValidate.isNotEmpty(svnInfo.url)) {
                strVersionLabel = svnInfo.baseName(svnInfo.url);
            }
        } catch (Exception e) {
            Debug.logError(e, "Error read standard svninfo " + MessageUtil.getExceptionMessage(e));
        }
        try {
            svnInfo.read("component://custom/config/svninfo.xml");
            if(UtilValidate.isNotEmpty(svnInfo.url)) {
                customVersionLabel = DIR_SEPARATOR + svnInfo.baseName(svnInfo.dirName(svnInfo.dirName(svnInfo.url)));
            }
        } catch (Exception e) {
            Debug.logError(e, "Error read custom svninfo " + MessageUtil.getExceptionMessage(e));
        }
        result.put("versions", baseProductLabel + " " + strVersionLabel + customVersionLabel);
        return result;
    }

    /**
     * Return date
     * @return
     */
    public String getDate() {
        String str = getIsoDate();
        if (str != null && str.length() > 10) {
            str = str.substring(0, 10);
        }
        return str;
    }

    /**
     * Return baseName
     */
    public String baseName(String str) {
        if (str != null) {
            int idx = str.lastIndexOf(DIR_SEPARATOR);
            if (idx >= 0 && ++idx < str.length()) {
                str = str.substring(idx);
            }
        }
        return str;
    }

    /**
     * Return dirName
     */
    public String dirName(String str) {
        if (str != null) {
            int idx = str.lastIndexOf(DIR_SEPARATOR);
            if (idx >= 0) {
                str = str.substring(0, idx);
            }
        }
        return str;
    }
}
