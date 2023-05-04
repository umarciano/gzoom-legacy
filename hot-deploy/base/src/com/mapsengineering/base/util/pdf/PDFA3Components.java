package com.mapsengineering.base.util.pdf;


public class PDFA3Components {
	private String inputFilePath;
	private String embedFilePath;
	private String outputFilePath;
	public static String colorProfileFileName = "sRGBColorSpaceProfile.icm";
	private static String documentType = "Gzoom Report";
	private static String documentFileName = "Piano della performance - Obiettivi strategici.pdf";
	private static String documentVersion = "1.0";
	public static String xmpTemplateFileName = "xmpTemplate.xml";


	public PDFA3Components() {
		super();
		this.embedFilePath = null;
	}


	public PDFA3Components(String inputFilePath, String outputFilePath) {
		super();
		this.inputFilePath = inputFilePath;
		this.outputFilePath = outputFilePath;
		this.embedFilePath = null;
	}


	public String getInputFilePath() {
		return inputFilePath;
	}

	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	public String getEmbedFilePath() {
		return embedFilePath;
	}

	public void setEmbedFilePath(String embedFilePath) {
		this.embedFilePath = embedFilePath;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public String getDocumentType() {
		return documentType;
	}
	
	public String getDocumentFileName() {
		return documentFileName;
	}
	
	public String getDocumentVersion() {
		return documentVersion;
	}
}

