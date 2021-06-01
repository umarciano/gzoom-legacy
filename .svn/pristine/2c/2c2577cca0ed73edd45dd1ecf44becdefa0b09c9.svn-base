package com.mapsengineering.base.test;

public class TestGlAccountStandardImportUpdate extends BaseGlAccountStandardImportUploadFileTestCase {

    public void testGlAccountStandardImport() {
        try {
            setContextAndRunGlAccountInterface("GlAccountInterface.xls", 3, 3, true);
        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }
    
    public void testGlAccountStandardImportXlsx() {
        try {
            setContextAndRunGlAccountInterface("GlAccountInterface.xlsx", 3, 3, false);
        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }
    
    public void testGlAccountStandardImportUpdate() {
        try {
            setContextAndRunGlAccountInterface("GlAccountInterfaceUpdate.xls", 0, 1, true);
        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }

    public void testGlAccountStandardImportUpdateRole() {
        try {
            setContextAndRunGlAccountInterface("GlAccountInterfaceUpdateRole.xls", 0, 1, true);
        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }
}
