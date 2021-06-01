package com.mapsengineering.base.test;

import java.io.File;
import java.nio.ByteBuffer;
import java.sql.Timestamp;

import org.apache.commons.io.FileUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;

public class BaseTestStandardImportUploadFile extends BaseTestCase {

	private static final String PROP_OFBIZ_HOME = "ofbiz.home";
	
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected static final Timestamp THRU_DATE_2013 = new Timestamp(UtilDateTime.toDate(11, 12, 2013, 0, 0, 0).getTime());

    protected void getLoadContext(String entityName, String nameFile, boolean isXls) throws Exception {
        ByteBuffer buffer = getButeBufferToPathFile(nameFile);
        setContext(buffer, entityName, isXls);
    }
    
    protected void getLoadContext(String entityName, String nameFile) throws Exception {
        Debug.log("getLoadContext nameFile " + nameFile);
        ByteBuffer buffer = getButeBufferToPathFile(nameFile);
        setContext(buffer, entityName, true);
    }

    private ByteBuffer getButeBufferToPathFile(String nameFile) throws Exception {
        String propValue = System.getProperty(PROP_OFBIZ_HOME);
        String path = new File(propValue).getAbsolutePath() + File.separator + "hot-deploy" + File.separator + "base" + File.separator + "data" + File.separator + "test" + File.separator + "stdimportfile" + File.separator + nameFile;
        File file = new File(path);
        return ByteBuffer.wrap((byte[])(FileUtils.readFileToByteArray(file)));
    }

    private void setContext(ByteBuffer buffer, String entityName, boolean isXls) {
        context.put(entityName + "UploadedFile", buffer);
        context.put("_" + entityName + "UploadedFile_fileName", entityName);
        context.put("_" + entityName + "UploadedFile_contentType", isXls ? "application/vnd.ms-excel": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

}
