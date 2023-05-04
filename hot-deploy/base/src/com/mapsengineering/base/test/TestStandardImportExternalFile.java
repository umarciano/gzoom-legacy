package com.mapsengineering.base.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.standardimport.common.E;

/**
 * Test import external file
 *
 */
public class TestStandardImportExternalFile extends BaseTestCase {

	private static final String PROP_OFBIZ_HOME = "ofbiz.home";
	
    protected void setUp() throws Exception {
        super.setUp();
        context.put(GenericService.DEFAULT_ORGANIZATION_PARTY_ID, COMPANY);
    }
    
    /**
     * Test import external file
     * @throws Exception
     */
    public void testExternalFile() throws Exception {
        String directoryPathInXls = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.folder.in");
        String directoryPathOutXls = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.folder.out");
        Debug.log(" moveFile " + directoryPathInXls);
        moveFile(directoryPathInXls, directoryPathOutXls, ".xls");
        Map<String, Object> serviceParams = dispatcher.getDispatchContext().makeValidContext("standardImportExternalFile", ModelService.IN_PARAM, context);
        Debug.log(" - serviceParams " + serviceParams);
        Map<String, Object> firstResult = dispatcher.runSync("standardImportExternalFile", serviceParams);
        Debug.log(" - firstResult " + firstResult);
        // TODO assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), firstResult.get(E.responseMessage.name()));
        // TODO manageResultList(firstResult, "resultListUploadFile", "Importazione Risorse Umane Standard Upload", 0, 3);
        // TODO manageResultList(firstResult, "resultListUploadFile", "Importazione Unit\u00E0 organizzativa Upload", 3, 3); // non si riesce a testare solo il primo
        // TODO manageResultList(firstResult, "resultList", "Importazione Risorse Umane Standard", 4, 3);
        // TODO manageResultList(firstResult, "resultList", "Importazione Unit\u00E0 organizzativa Standard", 0, 1); // riesco a testare solo il primo
        moveFile(directoryPathInXls, directoryPathOutXls, ".xlsx");
        Map<String, Object> secondResult = dispatcher.runSync("standardImportExternalFile", serviceParams);
        Debug.log(" - secondResult " + secondResult);
        moveFile(directoryPathInXls, directoryPathOutXls, ".csv");
        Map<String, Object> thirdResult = dispatcher.runSync("standardImportExternalFile", serviceParams);
        Debug.log(" - thirdResult " + thirdResult);
        
    }

    private void moveFile(String directoryPathInXls, String directoryPathOutXls, String fileExtension) throws Exception {
        String propValue = System.getProperty(PROP_OFBIZ_HOME);
        String path = new File(propValue).getAbsolutePath() + directoryPathOutXls;
        Debug.log(" path " + path);
        path = path.replaceAll("\\\\", "/");
        Debug.log(" path " + path);
        List<File> files = FileUtil.getFiles(path, fileExtension);
        for (File file : files) {
            FileOutputStream outputStream = null;
            String filename = file.getName();
            Debug.log(" filename " + filename);
            try {
                if (file.exists()) {
                    String outputPath = new File(propValue).getAbsolutePath() + directoryPathInXls;
                    ByteBuffer buffer = ByteBuffer.wrap((byte[])(FileUtils.readFileToByteArray(file)));
                    String outfile = FileUtil.getPatchedFileName(outputPath, filename);
                    outputStream = new FileOutputStream(new File(outfile));
                    outputStream.write(buffer.array());
                }
            } catch (IOException e) {
                Debug.logError(e, "TestStandardImportExternalFile");
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        }
    }
}
