package com.mapsengineering.base.entity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelRelation;

public class FkViolationFileHandler extends FkViolationBaseHandler {
    private final String QUERY_SEPARATOR = "\n\n";
    private final String OUT_DIR = "runtime/output";
    private final String OUT_FILE_NAME = "fk-violations.sql";
    private StringBuffer tempOut = new StringBuffer();

    private PrintWriter getValidWriter() {
        PrintWriter writer = null;
        List<String> results = FastList.newInstance();

        if (UtilValidate.isNotEmpty(OUT_DIR)) {
            File outdir = new File(OUT_DIR);
            if (!outdir.exists()) {
                outdir.mkdir();
            }
            if (outdir.isDirectory() && outdir.canWrite()) {
                try {
                    File oldFile = new File(OUT_DIR + "/" + OUT_FILE_NAME);
                    if (oldFile.exists() && oldFile.isFile()) {
                        oldFile.delete();
                    }
                    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outdir, OUT_FILE_NAME)), "UTF-8")));
                } catch (UnsupportedEncodingException e) {
                    Debug.log(e);
                } catch (FileNotFoundException e) {
                    Debug.log(e);
                }

            } else {
                results.add("Path not found or no write access.");
            }
        } else {
            results.add("No path specified, doing nothing.");
        }
        // send the notification
        return writer;
    }

    @Override
    protected void iterateEnd(ModelEntity modelEntity, ModelRelation modelRelation) {
        try {
            FkViolationSqlQuery sqlQuery = new FkViolationSqlQuery(getDelegatorName(), modelEntity, modelRelation);
            String strSqlQuery = sqlQuery.getQuery();
            if (strSqlQuery != null) {
                tempOut.append(strSqlQuery + QUERY_SEPARATOR);
            }
        } catch (GenericEntityException e) {
            Debug.log(e);
        }
        if (tempOut != null && tempOut.length() > 0) {
            PrintWriter writer = getValidWriter();
            writer.println(tempOut);
            writer.close();

        }
    }

}
