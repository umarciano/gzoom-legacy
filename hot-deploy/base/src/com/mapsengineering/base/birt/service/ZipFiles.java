package com.mapsengineering.base.birt.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import com.mapsengineering.base.birt.E;

public class ZipFiles {

	public static ByteArrayOutputStream createZip(String name, List<Map<String, String>> files) throws Exception {
	    String pathFile = null;
	    String nameFile = null;
        try {

           // FileOutputStream fos = new FileOutputStream("runtime\\javatemp\\" + name);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ZipOutputStream zipOut = new ZipOutputStream(baos);

            for (Map<String, String> srcFile : files) {
                pathFile = (String) srcFile.get(E.pathFile.name());
                nameFile = (String) srcFile.get(E.nameFile.name());
                File fileToZip = new File((String)srcFile.get(E.pathFile.name()));
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry((String)srcFile.get(E.nameFile.name()));
                zipOut.putNextEntry(zipEntry);
     
                byte[] bytes = new byte[1024];
                int length;
                while((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
            }

            zipOut.close();
            baos.close();
			
			return baos;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			String msg = "File non trovato: " + nameFile + " (percorso  " + pathFile + ")";
			throw new FileNotFoundException(msg);
		} 
        catch (ZipException e) {
            e.printStackTrace();
            String msg = "Errore nella creazione dello zip : <br/>" + e.getMessage();
            throw new ZipException(msg);
        } 
        catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}


}