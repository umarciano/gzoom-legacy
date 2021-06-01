package com.mapsengineering.base.translatorcatalogs;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.ofbiz.base.util.Debug;

/**
 * FilePropertyCatalogs
 *
 */
public class FilePropertyCatalogs {
    
    public static final String MODULE = FilePropertyCatalogs.class.getName();
    public static final String END_FILE_NAME = ".properties";
    private static final String SEP = "_";
    
    private Locale sl;
    
    /**
     * Constuctor
     * @param sl
     */
    public FilePropertyCatalogs(Locale sl) {
        this.sl = sl;
    }

    /**
     * Search file in directory
     * @param location
     * @return
     * @throws Exception
     */
    public List<File> getFileCatalogs(String location) throws Exception {
        Debug.log("*** Find all catalogs");
        
        List<File> files = null;        
        File loadDir = new File(location);
        if (loadDir.exists() && loadDir.isDirectory()) {
            files = listFileTree(loadDir);
        } else {
            String errorMsg = "Could not find directory: \"" + location + "\"";
            throw new Exception(errorMsg);
        }
        Debug.log("*** total catalogs = "+ files.size());
        return files;
        
    }
    
    /**
     * Collect file in directory
     * @param dir
     * @return
     */
    public List<File> listFileTree(File dir) {
        List<File> fileTree = new LinkedList<File>();
        if( dir == null || dir.listFiles() == null){
            return fileTree;
        }
        for (File entry : dir.listFiles()) {
            if (entry.isFile() && entry.getName().toLowerCase().indexOf(SEP + sl.toString().toLowerCase()) > 0 && entry.getName().toLowerCase().endsWith(END_FILE_NAME)) {
                fileTree.add(entry);
            } else {
                fileTree.addAll(listFileTree(entry));
            }
        }
        return fileTree;
    }
    

}
