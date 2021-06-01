package com.mapsengineering.base.util.importexport;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.datafile.DataFile;
import org.ofbiz.datafile.DataFileException;
import org.ofbiz.datafile.ModelRecord;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

public class ImportExportUtil {

    public static final Map<ResourceEnum, ResourceWriter> RESOURCE_WRITER_MAP;

    public static final Map<ResourceEnum, ResourceReader> RESOURCE_READER_MAP;

    static {
        RESOURCE_WRITER_MAP = FastMap.newInstance();
        RESOURCE_WRITER_MAP.put(ResourceEnum.ENTITY, new EntityWriter());
        RESOURCE_WRITER_MAP.put(ResourceEnum.EXCEL, new ExcelWriter());

        RESOURCE_READER_MAP = FastMap.newInstance();
        RESOURCE_READER_MAP.put(ResourceEnum.ENTITY, new EntityReader());
        RESOURCE_READER_MAP.put(ResourceEnum.EXCEL, new ExcelReader());
    }

    private ResourceReader reader;

    public ImportExportUtil() {
    }

    public ModelRecord getModelRecord(String dataFileResource, String dataFileName, String recordName) {
        ModelRecord result = null;
        try {
            DataFile df = createDataFile(dataFileResource, dataFileName);
            result = df.getModelDataFile().getModelRecord(recordName);
        } catch (DataFileException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    public OutputStream export(String entityName, ResourceEnum destination, List<GenericValue> toExportList, Map<String, Object> context) throws Exception {
        ResourceWriter writer = RESOURCE_WRITER_MAP.get(destination);
        writer.setContext(context);
        writer.open(entityName);

        Iterator<GenericValue> it = toExportList.iterator();
        while (it.hasNext()) {
            GenericValue gv = it.next();
            writer.write(gv);
        }

        writer.close();

        return writer.getStream();
    }

    public List<GenericValue> read(String entityName, EntityCondition condition, ResourceEnum origin, InputStream uploadedFile, Map<String, Object> context) throws Exception {
        reader = RESOURCE_READER_MAP.get(origin);
        reader.setContext(context);

        String errMessage = reader.open(entityName, uploadedFile);

        if (errMessage == null) {
            List<GenericValue> toImportList = reader.read(condition);
            reader.close();
            return toImportList;
        } else {
            throw new Exception(errMessage);
        }
    }

    public OutputStream writeImportResult(String entityName, String pkColumnName, String pkValue, String resultColumnName, String result, InputStream wbStream) throws Exception {
        return reader.writeImportResult(entityName, pkColumnName, pkValue, resultColumnName, result, wbStream);
    }

    private DataFile createDataFile(String dataFileResource, String dataFileName) throws DataFileException {
        return DataFile.makeDataFile(UtilURL.fromResource(dataFileResource), dataFileName);
    }
}
