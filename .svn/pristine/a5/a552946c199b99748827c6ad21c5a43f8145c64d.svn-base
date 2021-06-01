package com.mapsengineering.base.jdbc;

import java.sql.SQLException;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.config.DatasourceInfo;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;

/**
 * JdbcStatement
 *
 */
public class JdbcStatement {

    public static final String STRING_EMPTY = "";
    public static final char DOT = '.';
    public static final String DEFAULT_GROUP_NAME = "org.ofbiz";

    private static final String MODULE = JdbcStatement.class.getName();

    private final Delegator delegator;
    private GenericHelperInfo helperInfo;
    private String defaultSchemaPrefix;
    private String defaultFieldTypeName;

    /**
     * Constructor
     * @param delegator
     */
    public JdbcStatement(Delegator delegator) {
        this.delegator = delegator;
        helperInfo = null;
    }

    /**
     * Return Delegator
     * @return
     */
    public Delegator getDelegator() {
        return delegator;
    }

    /**
     * Return helperInfo
     * @return
     */
    public GenericHelperInfo getHelperInfo() {
        if (helperInfo == null) {
            setEntityGroupName(DEFAULT_GROUP_NAME);
        }
        return helperInfo;
    }

    /**
     * Set helperInfo
     * @param entityGroupName
     */
    public void setEntityGroupName(String entityGroupName) {
        this.helperInfo = delegator.getGroupHelperInfo(entityGroupName);
    }

    /**
     * Return defaultSchemaPrefix.tableName
     * @param tableName
     * @param groupName
     * @return
     */
    public String getTableName(String tableName, String groupName) {
        if (isDefaultGroupName(groupName)) {
            if (defaultSchemaPrefix == null) {
                defaultSchemaPrefix = getSchemaPrefixByGroupName(getHelperInfo().getEntityGroupName());
            }
            return defaultSchemaPrefix + tableName;
        }
        return getSchemaPrefixByGroupName(groupName) + tableName;
    }

    protected String getSchemaPrefixByGroupName(String groupName) {
        DatasourceInfo datasourceInfo = getDatasourceInfo(groupName);
        if (datasourceInfo != null && UtilValidate.isNotEmpty(datasourceInfo.schemaName)){
            return datasourceInfo.schemaName + DOT;
        }
        return STRING_EMPTY;
    }
    
    /**
     * Return datasourceInfo.fieldTypeName
     * @param groupName
     * @return
     */
    public String getFieldTypeName(String groupName) {
        DatasourceInfo datasourceInfo = null;
        if (isDefaultGroupName(groupName)) {
            if (defaultFieldTypeName == null) {
                datasourceInfo = getDatasourceInfo(getHelperInfo().getEntityGroupName());
                if (datasourceInfo != null && UtilValidate.isNotEmpty(datasourceInfo.fieldTypeName)){
                    defaultFieldTypeName = datasourceInfo.fieldTypeName;
                }
            }
            return defaultFieldTypeName;
        }
        if (datasourceInfo == null) {
            datasourceInfo = getDatasourceInfo(groupName);
        }
        return datasourceInfo.fieldTypeName;
    }
    
    protected DatasourceInfo getDatasourceInfo(String groupName) {
        String helperName = getDelegator().getGroupHelperName(groupName);
        if (helperName != null) {
            DatasourceInfo datasourceInfo = EntityConfigUtil.getDatasourceInfo(helperName);
            if (datasourceInfo != null && UtilValidate.isNotEmpty(datasourceInfo)) {
                return datasourceInfo;
            }
        }
        return null;
    }

    protected void applyParams(SQLProcessor sqlP, List<JdbcParam> params) throws SQLException {
        if (params != null) {
            int i = 0;
            for (JdbcParam param : params) {
                try {
                    ++i;
                    param.setValueTo(sqlP);
                } catch (SQLException e) {
                    throw new SQLException("Error setting parameter #" + i + " value: " + param.getValue(), e.getSQLState(), e.getErrorCode(), e);
                }
            }
        }
    }

    protected SQLProcessor prepareStatement(final SQLProcessor sqlP, String command, List<JdbcParam> params) throws GenericDataSourceException, GenericEntityException, SQLException {
        try {
            closeSqlProcessor(sqlP);
        } catch (GenericDataSourceException e) {
            Debug.logWarning(e, "Error closing last SQL processor", MODULE);
        }
        if (command != null && !command.isEmpty()) {
            SQLProcessor newSqlP = new SQLProcessor(getHelperInfo());
            newSqlP.prepareStatement(command);
            applyParams(newSqlP, params);
            return newSqlP;
        }
        return null;
    }

    protected SQLProcessor closeSqlProcessor(SQLProcessor sqlP) throws GenericDataSourceException {
        if (sqlP != null) {
            sqlP.close();
        }
        return null;
    }
    
    protected boolean isDefaultGroupName(String groupName) {
        return groupName == null || groupName.isEmpty() || groupName.equals(getHelperInfo().getEntityGroupName());
    }
}
