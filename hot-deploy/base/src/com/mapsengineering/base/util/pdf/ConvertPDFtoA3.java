package com.mapsengineering.base.util.pdf;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import com.mapsengineering.base.convertercatalogs.ConverterCatalogs;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collections;
import java.util.GregorianCalendar;

/**
 * The ConvertPDFtoPDFA3 method is used to convert any kind of PDF(.pdf) to
 * PDF/A-3
 * 
 * @author ETDA
 * 
 */
public class ConvertPDFtoA3 {

	private static float pdfVer = 1.7f;
	public ConvertPDFtoA3(){

	}

	public void Convert(PDFA3Components pdfa3Components) throws Exception {
		File inputFile = new File(pdfa3Components.getInputFilePath());
		PDDocument doc = PDDocument.load(inputFile);
		String fileColorProfilePath = new File(System.getProperty(ConverterCatalogs.PROP_OFBIZ_HOME)).getAbsolutePath() + File.separator + "hot-deploy" + File.separator + "base" + File.separator + "config" + File.separator + "pdf" + File.separator + PDFA3Components.colorProfileFileName;
		File colorPFile = new File(fileColorProfilePath);
		InputStream colorProfile = new FileInputStream(colorPFile);
		PDDocumentCatalog cat = makeA3compliant(doc, pdfa3Components);
		if(pdfa3Components.getEmbedFilePath() != null){
			attachFile(doc, pdfa3Components.getEmbedFilePath());
		}
		addOutputIntent(doc, cat, colorProfile);
		doc.setVersion(pdfVer);
		doc.save(pdfa3Components.getOutputFilePath());
		doc.close();
		File outputFile = new File(pdfa3Components.getOutputFilePath());
		if (outputFile.exists()) {
			System.out.println(pdfa3Components.getOutputFilePath());
		} else {
			System.out.println("Failed to convert.");
		}
	}


	public PDDocument Convert(PDDocument inputDoc,PDFA3Components pdfa3Components) throws Exception {
		String fileColorProfilePath = new File(System.getProperty(ConverterCatalogs.PROP_OFBIZ_HOME)).getAbsolutePath() + File.separator + "hot-deploy" + File.separator + "base" + File.separator + "config" + File.separator + "pdf" + File.separator + PDFA3Components.colorProfileFileName;
		File colorPFile = new File(fileColorProfilePath);
		InputStream colorProfile = new FileInputStream(colorPFile);
		PDDocumentCatalog cat = makeA3compliant(inputDoc, pdfa3Components);
		if(pdfa3Components.getEmbedFilePath() != null){
			attachFile(inputDoc, pdfa3Components.getEmbedFilePath());
		}
		addOutputIntent(inputDoc, cat, colorProfile);
		inputDoc.setVersion(pdfVer);
		return inputDoc;
	}


	private void addOutputIntent(PDDocument doc, PDDocumentCatalog cat, InputStream colorProfile)
			throws IOException {
		if (cat.getOutputIntents().isEmpty()) {
			PDOutputIntent oi = new PDOutputIntent(doc, colorProfile);
			oi.setInfo("sRGB IEC61966-2.1");
			oi.setOutputCondition("sRGB IEC61966-2.1");
			oi.setOutputConditionIdentifier("sRGB IEC61966-2.1");
			oi.setRegistryName("http://www.color.org");
			cat.addOutputIntent(oi);
		}

	}

	private void attachFile(PDDocument doc, String embedFilePath) throws IOException {
		PDEmbeddedFilesNameTreeNode efTree = new PDEmbeddedFilesNameTreeNode();
		File embedFile = new File(embedFilePath);
		String subType = Files.probeContentType(FileSystems.getDefault().getPath(embedFilePath));
		String embedFileName = FilenameUtils.getName(embedFilePath);
		// first create the file specification, which holds the embedded file
		PDComplexFileSpecification fs = new PDComplexFileSpecification();
		fs.setFile(embedFileName);
		COSDictionary dict = fs.getCOSObject();
		// Relation "Source" for linking with eg. catalog
		dict.setName("AFRelationship", "Source");
		dict.setString("UF", embedFileName);
		InputStream is = new FileInputStream(embedFile);
		PDEmbeddedFile ef = new PDEmbeddedFile(doc, is);
		// set some of the attributes of the embedded file
		ef.setModDate(GregorianCalendar.getInstance());
		ef.setSize((int) embedFile.length());
		ef.setCreationDate(new GregorianCalendar());
		fs.setEmbeddedFile(ef);
		ef.setSubtype(subType);
		// now add the entry to the embedded file tree and set in the document.
		efTree.setNames(Collections.singletonMap(embedFileName, fs));
		// attachments are stored as part of the "names" dictionary in the
		PDDocumentCatalog catalog = doc.getDocumentCatalog();
		PDDocumentNameDictionary names = new PDDocumentNameDictionary(doc.getDocumentCatalog());
		names.setEmbeddedFiles(efTree);
		catalog.setNames(names);
		COSDictionary dict2 = catalog.getCOSObject();
		COSArray array = new COSArray();
		array.add(fs.getCOSObject());
		dict2.setItem("AF", array);
	}

	private PDDocumentCatalog makeA3compliant(PDDocument doc, PDFA3Components pdfa3Components) throws Exception {
		PDDocumentCatalog cat = doc.getDocumentCatalog();
		PDDocumentInformation pdd = doc.getDocumentInformation();
		PDMetadata metadata = new PDMetadata(doc);
		cat.setMetadata(metadata);
		PDDocumentInformation pdi = new PDDocumentInformation();
		pdi.setProducer(pdd.getProducer());
		pdi.setAuthor(pdd.getAuthor());
		pdi.setTitle(pdd.getTitle());
		pdi.setSubject(pdd.getSubject());
		pdi.setKeywords(pdd.getKeywords());
		// Set OID
		// pdi.setCustomMetadataValue("OID", "10.2.3.65.5");
		doc.setDocumentInformation(pdi);
		// use for eTax invoice only
		Charset charset = StandardCharsets.UTF_8;
		String xmpTemplateFilePath = new File(System.getProperty(ConverterCatalogs.PROP_OFBIZ_HOME)).getAbsolutePath() + File.separator + "hot-deploy" + File.separator + "base" + File.separator + "config" + File.separator + "pdf" + File.separator + PDFA3Components.xmpTemplateFileName;
		File xmpTemplateFile = new File(xmpTemplateFilePath);
		byte[] fileBytes = Files.readAllBytes(xmpTemplateFile.toPath());
		String content = new String(fileBytes, charset);
		content = content.replaceAll("@DocumentFileName", pdfa3Components.getDocumentFileName());
		content = content.replaceAll("@DocumentType", pdfa3Components.getDocumentType());
		content = content.replaceAll("@DocumentVersion", pdfa3Components.getDocumentVersion());
		byte[] editedBytes = content.getBytes(charset);
		metadata.importXMPMetadata(editedBytes);
		return cat;
	}
}
