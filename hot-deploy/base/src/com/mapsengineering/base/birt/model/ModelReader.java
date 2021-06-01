package com.mapsengineering.base.birt.model;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.cache.UtilCache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mapsengineering.base.birt.BirtException;

/**
 * Report - Report Definition Reader
 *
 */
@SuppressWarnings("serial")
public class ModelReader implements Serializable {
	public static final String module = ModelReader.class.getName();

	private static final String reportDefLocation = "/report/definition";

	public static UtilCache<String, ModelReport> reports = UtilCache.createUtilCache("report.ModelReport", 0, 0);


	public static ModelReport getModelReport(String reportId) throws BirtException {
		ModelReport report = null;

		if (UtilValidate.isNotEmpty(reportId)) {
			report = reports.get(reportId);
			if (report == null) {
				synchronized (ModelReader.class) {
					report = reports.get(reportId);
					if (report == null) {
						ModelReader reader = new ModelReader();
						reader.loadReports();

						report = reports.get(reportId);
					}
				}
			}
		}

		return report;
	}

	protected void loadReports() throws BirtException {
		Collection<ComponentConfig> components = ComponentConfig.getAllComponents();
		if (UtilValidate.isNotEmpty(components)) {
			for (ComponentConfig component : components) {
				try {
					String fullLocation = component.getFullLocation("report-definition", reportDefLocation);
					File reportConfigurationDirectory = null;
					if (UtilValidate.isNotEmpty(fullLocation)) {
						reportConfigurationDirectory = new File(fullLocation);
						if (UtilValidate.isNotEmpty(reportConfigurationDirectory) && reportConfigurationDirectory.exists() && reportConfigurationDirectory.isDirectory()) {
							File[] listFile = reportConfigurationDirectory.listFiles(new FilenameFilter() {

								@Override
								public boolean accept(File dir, String name) {
									return name.endsWith(".xml");
								}
							});
							if (UtilValidate.isNotEmpty(listFile)) {
								for (int i = 0; i < listFile.length; i++) {
									File f = listFile[i];

									Document reportConfigDocument = UtilXml.readXmlDocument(f.toURI().toURL(), false);
									if (UtilValidate.isNotEmpty(reportConfigDocument)) {
										Element reportConfigElement = reportConfigDocument.getDocumentElement();
										for (Element reportElement : UtilXml.childElementList(reportConfigElement, "report")) {
											ModelReport report = new ModelReport(this, reportElement);
											reports.put(report.getId(), report);
										}
									}

								}
							}
						}
					}
				} catch (ComponentException e) {
					Debug.logVerbose(e, module);
				} catch (MalformedURLException e) {
					Debug.logError(e, module);
					throw new BirtException("Errore nel caricamento delle configurazione dei report nel componente " + component.getComponentName());
				} catch (SAXException e) {
					Debug.logError(e, module);
					throw new BirtException("Errore nel caricamento delle configurazione dei report nel componente " + component.getComponentName());
				} catch (ParserConfigurationException e) {
					Debug.logError(e, module);
					throw new BirtException("Errore nel caricamento delle configurazione dei report nel componente " + component.getComponentName());
				} catch (IOException e) {
					Debug.logVerbose(e, module);
				}
			}
		}
	}
}
