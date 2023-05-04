/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.base.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.ofbiz.base.location.ComponentLocationResolver;

import org.apache.commons.io.FileUtils;

/**
 * File Utilities
 *
 */
public class FileUtil {

    public static final String module = FileUtil.class.getName();

    /**
     * Creates a new File instance from a pathname string.
     * @param path The pathname string
     * @return file
     */
    public static File getFile(String path) {
        return getFile(null, path);
    }

    /**
     * Creates a new File instance from a parent abstract pathname and a child pathname string.
     * @param root The parent abstract pathname 
     * @param path The child pathname string
     * @return file
     */
    public static File getFile(File root, String path) {
        if (path.startsWith("component://")) {
            try {
                path = ComponentLocationResolver.getBaseLocation(path).toString();
            } catch (MalformedURLException e) {
                Debug.logError(e, module);
                return null;
            }
        }
        String fileNameSeparator = ("\\".equals(File.separator)? "\\" + File.separator: File.separator);
        return new File(root, path.replaceAll("/+|\\\\+", fileNameSeparator));
    }

    /**
     * Write string s in file fileName
     * @param fileName
     * @param s
     * @throws IOException
     */
    public static void writeString(String fileName, String s) throws IOException {
        writeString(null, fileName, s);
    }

    /**
     * Write string s in file in path with name
     * @param fileName
     * @param s
     * @throws IOException
     */
    public static void writeString(String path, String name, String s) throws IOException {
        Writer out = getBufferedWriter(path, name);

        try {
            out.write(s + System.getProperty("line.separator"));
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Debug.logError(e, module);
                }
            }
        }
    }
    
    /**
     * Writes a file from a string with a specified encoding.
     * Creates the directory if not exists
     *
     * @param path
     * @param name
     * @param encoding
     * @param s
     * @throws IOException
     */
    public static void writeString(String path, String name, String encoding, String s) throws IOException {
        String fileName = getPatchedFileName(path, name);
        if (UtilValidate.isEmpty(fileName)) {
            throw new IOException("Cannot obtain buffered writer for an empty filename!");
        }

        try {
            FileUtils.writeStringToFile(new File(fileName), s, encoding);
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        }
    }
    
    /**
     * Writes a file from a string with a specified encoding.
     * 
     * @param encoding
     * @param s
     * @param outFile
     * @throws IOException
     */
    public static void writeString(String encoding, String s, File outFile) throws IOException {
        try {
            FileUtils.writeStringToFile(outFile, s, encoding);
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        }
    }
    
    /**
     * getBufferedWriter
     * @param path
     * @param name
     * @return
     * @throws IOException
     */
    public static Writer getBufferedWriter(String path, String name) throws IOException {
        String fileName = getPatchedFileName(path, name);
        if (UtilValidate.isEmpty(fileName)) {
            throw new IOException("Cannot obtain buffered writer for an empty filename!");
        }

        return new BufferedWriter(new FileWriter(fileName));
    }

    public static OutputStream getBufferedOutputStream(String path, String name) throws IOException {
        String fileName = getPatchedFileName(path, name);
        if (UtilValidate.isEmpty(fileName)) {
            throw new IOException("Cannot obtain buffered writer for an empty filename!");
        }

        return new BufferedOutputStream(new FileOutputStream(fileName));
    }

    /**
     * Return filename, creates the directory if not exists
     * @param path
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String getPatchedFileName(String path, String fileName) throws IOException {
        
        if (Debug.timingOn()) {
            Debug.log("getPatchedFileName path : " + path + " - " + fileName, module);
        }
        // make sure the export directory exists
        if (UtilValidate.isNotEmpty(path)) {
            path = path.replaceAll("\\\\", "/");
            File parentDir = new File(path);
            if (!parentDir.exists()) {
                if (!parentDir.mkdir()) {
                    throw new IOException("Cannot create directory for path: " + path);
                }
            }

            // build the filename with the path
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            if (fileName.startsWith("/")) {
                fileName = fileName.substring(1);
            }
            fileName = path + fileName;
        }

        return fileName;
    }

    /**
     * Read text file
     * @param file
     * @param newline
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static StringBuffer readTextFile(File file, boolean newline) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        StringBuffer buf = new StringBuffer();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));

            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
                if (newline) {
                    buf.append(System.getProperty("line.separator"));
                }
            }
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Debug.logError(e, module);
                }
            }
        }
        
        return buf;
    }
    /**
     * Read text file
     * @param fileName
     * @param newline
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static StringBuffer readTextFile(String fileName, boolean newline) throws FileNotFoundException, IOException {
        File file = new File(fileName);
        return readTextFile(file, newline);
    }
    
    /**
     * 
     * @param encoding
     * @param inFile
     * @return
     * @throws IOException
     */
    public static String readString(String encoding, File inFile) throws IOException {
        String readString = "";
        try {
            readString = FileUtils.readFileToString(inFile, encoding);
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        }
        return readString;
    }
    
    /**
     * Search File
     * @param fileList
     * @param path
     * @param filter
     * @param includeSubfolders
     * @throws IOException
     */
    public static void searchFiles(List<File> fileList, File path, FilenameFilter filter, boolean includeSubfolders) throws IOException {
        // Get filtered files in the current path
    	File[] files = path.listFiles(filter);
    	if (files == null) {
    		return;
    	}
        
        // Process each filtered entry
        for (int i = 0; i < files.length; i++) {
            // recurse if the entry is a directory
            if (files[i].isDirectory() && includeSubfolders && !files[i].getName().startsWith(".")) {
                searchFiles(fileList, files[i], filter, true);
            } else {
                // add the filtered file to the list
                fileList.add(files[i]);
            }
        }
    }
    
    /**
     * find Files
     * @param fileExt
     * @param basePath
     * @param partialPath
     * @param stringToFind
     * @return
     * @throws IOException
     */
    public static List<File> findFiles(String fileExt, String basePath, String partialPath, String stringToFind) throws IOException {
        if (basePath == null) {
            basePath = System.getProperty("ofbiz.home");
        }
        
        Set<String> stringsToFindInPath = FastSet.newInstance();
        Set<String> stringsToFindInFile = FastSet.newInstance();
        
        if (partialPath != null) {
           stringsToFindInPath.add(partialPath);
        }
        if (stringToFind != null) {
           stringsToFindInFile.add(stringToFind);
        }
        
        List<File> fileList = FastList.newInstance();
        FileUtil.searchFiles(fileList, new File(basePath), new SearchTextFilesFilter(fileExt, stringsToFindInPath, stringsToFindInFile), true);
        
        return fileList;
    }
    
    /**
     * find Xml Files
     * @param basePath
     * @param partialPath
     * @param rootElementName
     * @param xsdOrDtdName
     * @return
     * @throws IOException
     */
    public static List<File> findXmlFiles(String basePath, String partialPath, String rootElementName, String xsdOrDtdName) throws IOException {
        if (basePath == null) {
            basePath = System.getProperty("ofbiz.home");
        }
        
        Set<String> stringsToFindInPath = FastSet.newInstance();
        Set<String> stringsToFindInFile = FastSet.newInstance();
        
        if (partialPath != null) stringsToFindInPath.add(partialPath);
        if (rootElementName != null) stringsToFindInFile.add("<" + rootElementName + " ");
        if (xsdOrDtdName != null) stringsToFindInFile.add(xsdOrDtdName);
        
        List<File> fileList = FastList.newInstance();
        FileUtil.searchFiles(fileList, new File(basePath), new SearchTextFilesFilter("xml", stringsToFindInPath, stringsToFindInFile), true);
        return fileList;
    }
    
    /**
     * SearchTextFilesFilter
     *
     */
    public static class SearchTextFilesFilter implements FilenameFilter {
        String fileExtension;
        Set<String> stringsToFindInFile = FastSet.newInstance();
        Set<String> stringsToFindInPath = FastSet.newInstance();
        
        public SearchTextFilesFilter(String fileExtension, Set<String> stringsToFindInPath, Set<String> stringsToFindInFile) {
            this.fileExtension = fileExtension;
            if (stringsToFindInPath != null) {
                this.stringsToFindInPath.addAll(stringsToFindInPath);
            }
            if (stringsToFindInFile != null) {
                this.stringsToFindInFile.addAll(stringsToFindInFile);
            }
        }
        
        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            if (file.getName().startsWith(".")) {
                return false;
            }
            if (file.isDirectory()) {
                return true;
            }
            
            boolean hasAllPathStrings = true;
            String fullPath = dir.getPath().replace('\\', '/');
            for (String pathString: stringsToFindInPath) {
                if (fullPath.indexOf(pathString) < 0) {
                    hasAllPathStrings = false;
                    break;
                }
            }
            
            if (hasAllPathStrings && name.endsWith("." + fileExtension)) {
            	if (stringsToFindInFile.size() == 0) {
            		return true;
            	}
                StringBuffer xmlFileBuffer = null;
                try {
                    xmlFileBuffer = FileUtil.readTextFile(file, true);
                } catch (FileNotFoundException e) {
                    Debug.logWarning("Error reading xml file [" + file + "] for file search: " + e.toString(), module);
                    return false;
                } catch (IOException e) {
                    Debug.logWarning("Error reading xml file [" + file + "] for file search: " + e.toString(), module);
                    return false;
                }
                if (UtilValidate.isNotEmpty(xmlFileBuffer)) {
                    boolean hasAllStrings = true;
                    for (String stringToFile: stringsToFindInFile) {
                        if (xmlFileBuffer.indexOf(stringToFile) < 0) {
                            hasAllStrings = false;
                            break;
                        }
                    }
                    return hasAllStrings;
                }
            } else {
                return false;
            }
            return false;
        }
    }
    
    /**
     * Get file from directory
     * @param location
     * @param endFileName
     * @return
     * @throws Exception
     */
    public static List<File> getFiles(String location, String endFileName) throws Exception {
        List<File> files = null;        
        File loadDir = new File(location);
        if (loadDir.exists() && loadDir.isDirectory()) {
            files = listFileTree(loadDir, endFileName);
        } else {
            String errorMsg = "Could not find directory: \"" + location + "\"";
            throw new Exception(errorMsg);
        }
        return files;
    }
    
    /**
     * Get file from directory, recursive
     * @param dir directory
     * @param endFileName
     * @return
     */
    public static List<File> listFileTree(File dir, String endFileName) {
        List<File> fileTree = new LinkedList<File>();
        if( dir == null || dir.listFiles() == null){
            return fileTree;
        }
        for (File entry : dir.listFiles()) {
            if (entry.isFile() && entry.getName().toLowerCase().endsWith(endFileName)) {
                fileTree.add(entry);
            } else {
                fileTree.addAll(listFileTree(entry, endFileName));
            }
        }
        return fileTree;
    }
}
