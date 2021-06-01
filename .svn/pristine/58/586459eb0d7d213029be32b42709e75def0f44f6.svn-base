package com.mapsengineering.base.entity;

import java.util.Iterator;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.config.DatasourceInfo;
import org.ofbiz.entity.config.DelegatorInfo;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelGroupReader;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.entity.model.ModelRelation;

/**
 * Builds a SQL command to find FK violations.
 * @author sivi
 *
 */
public class FkViolationSqlQuery {

    private final String delegatorName;
    private final ModelEntity modelEntity;
    private final ModelRelation modelRelation;
    private ModelReader modelReader;
    private ModelEntity relModelEntity;
    private DatasourceInfo datasourceInfo;
    private String query;

    /**
     * 
     * @param delegatorName
     * @param modelEntity ModelEntity
     * @param modelRelation ModelRelation
     * @throws GenericEntityException
     */
    public FkViolationSqlQuery(String delegatorName, ModelEntity modelEntity, ModelRelation modelRelation) throws GenericEntityException {
        this.delegatorName = delegatorName;
        this.modelEntity = modelEntity;
        this.modelRelation = modelRelation;
        if (modelRelation.getKeyMapsSize() > 0) {
            initModel();
            initQuery();
        }
    }

    /**
     * 
     * @return the SQL command
     */
    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return getQuery();
    }

    private void initModel() throws GenericEntityException {
        modelReader = ModelReader.getModelReader(delegatorName);
        relModelEntity = modelReader.getModelEntity(modelRelation.getRelEntityName());
        DelegatorInfo delegatorInfo = EntityConfigUtil.getDelegatorInfo(delegatorName);
        ModelGroupReader modelGroupReader = ModelGroupReader.getModelGroupReader(delegatorName);
        String groupName = modelGroupReader.getEntityGroupName(modelEntity.getEntityName(), delegatorName);
        String helperName = delegatorInfo.groupMap.get(groupName);
        datasourceInfo = EntityConfigUtil.getDatasourceInfo(helperName);
    }

    private void initQuery() {
        String mainTable = modelEntity.getTableName(datasourceInfo);
        String relTable = relModelEntity.getTableName(datasourceInfo);

        StringBuffer sb = new StringBuffer();
        appendTitle(sb);
        sb.append("select ").append(mainTable).append(".* from ").append(mainTable).append("\n");
        if (isAnyPrimaryKey()) {
            appendDelete(sb, mainTable);
            appendUpdate(sb, mainTable);
        } else {
            appendUpdate(sb, mainTable);
            appendDelete(sb, mainTable);
        }
        sb.append("where (\n");
        appendMainCondition(sb, mainTable);
        sb.append("  ) and not exists (\n");
        appendRelSubQuery(sb, mainTable,relTable);
        sb.append("  )\n");
        appendOrderBy(sb, mainTable);

        query = sb.toString();
    }

    private boolean isAnyPrimaryKey() {
        for (Iterator<ModelKeyMap> it = modelRelation.getKeyMapsIterator(); it.hasNext();) {
            if (isPrimaryKey(it.next().getFieldName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isPrimaryKey(String fieldName) {
        ModelField modelField = modelEntity.getField(fieldName);
        if (modelField != null) {
            return modelField.getIsPk();
        }
        return false;
    }

    private void appendTitle(StringBuffer sb) {
        sb.append("-- ").append(modelRelation.getFkName()).append(" -- ").append(modelRelation.getCombinedName()).append('\n');
    }

    private void appendUpdate(StringBuffer sb, String mainTable) {
        sb.append("-- update ").append(mainTable).append(" set ");
        boolean first = true;
        for (Iterator<ModelKeyMap> it = modelRelation.getKeyMapsIterator(); it.hasNext();) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(getColName(modelEntity, it.next().getFieldName())).append("=null");
        }
        sb.append('\n');
    }

    private void appendDelete(StringBuffer sb, String mainTable) {
        sb.append("-- delete from ").append(mainTable).append("\n");
    }

    private void appendMainCondition(StringBuffer sb, String mainTable) {
        boolean first = true;
        for (Iterator<ModelKeyMap> it = modelRelation.getKeyMapsIterator(); it.hasNext();) {
            sb.append("    ");
            if (first) {
                first = false;
            } else {
                sb.append("or ");
            }
            sb.append(mainTable).append(".").append(getColName(modelEntity, it.next().getFieldName())).append(" is not null\n");
        }
    }

    private void appendRelSubQuery(StringBuffer sb, String mainTable, String relTable) {
        sb.append("    select * from ").append(relTable).append("\n");
        sb.append("    where ");
        appendRelCondition(sb, mainTable, relTable);
    }

    private void appendRelCondition(StringBuffer sb,  String mainTable, String relTable) {
        boolean first = true;
        for (Iterator<ModelKeyMap> it = modelRelation.getKeyMapsIterator(); it.hasNext();) {
            ModelKeyMap keyMap = it.next();
            if (first) {
                first = false;
            } else {
                sb.append("      and ");
            }
            sb.append(mainTable).append(".").append(getColName(modelEntity, keyMap.getFieldName()));
            sb.append("=").append(relTable).append(".").append(getColName(relModelEntity, keyMap.getRelFieldName()));
            sb.append('\n');
        }
    }

    private void appendOrderBy(StringBuffer sb, String mainTable) {
        if (modelEntity.getPksSize() > 0) {
            boolean first = true;
            sb.append("order by ");
            for (Iterator<ModelField> it = modelEntity.getPksIterator(); it.hasNext();) {
                ModelField field = it.next();
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(mainTable).append(".").append(field.getColName());
            }
            sb.append('\n');
        }
    }

    private String getColName(ModelEntity modelEntity, String fieldName) {
        ModelField modelField = modelEntity.getField(fieldName);
        return modelField != null ? modelField.getColName() : fieldName;
    }
}
