package com.mapsengineering.base.datamigration.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelIndex;
import org.ofbiz.entity.model.ModelViewEntity;

/**
 * Utility for Database
 *
 */
public class DatabaseUtil extends org.ofbiz.entity.jdbc.DatabaseUtil {
    public static final String module = DatabaseUtil.class.getName();
    public static final String DEFAULT_GROUP_NAME = "org.ofbiz";

    /**
     * Constructor
     * @param helperInfo
     */
    public DatabaseUtil(GenericHelperInfo helperInfo) {
        super(helperInfo);
    }

    /**
     * Delete index
     * @param tableName
     * @param indexName
     * @throws Exception
     */
    public void deleteDeclaredIndex(String tableName, String indexName) throws Exception {
        Connection connection = getConnection();
        try {
            removeForeignKeyIndex(connection, tableName, indexName);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Debug.logError(e, module);
            }
        }
    }

    /**
     * Delete foreignKey and index
     * @param tableName
     * @param indexName
     * @throws Exception
     */
    public void deleteForeignKeyAndIndex(String tableName, String indexName) throws Exception {
        Connection connection = getConnection();

        try {
            if (isMySql()) {
                deleteForeignKey(connection, tableName, indexName);
                deleteForeignKeyIndex(connection, tableName, indexName);
            } else {
                deleteForeignKeyIndex(connection, tableName, indexName);
                deleteForeignKey(connection, tableName, indexName);
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Debug.logError(e, module);
            }
        }
    }
    
    /**
     * Modify column
     * @param tableName
     * @param columnName
     * @param dataType
     * @throws Exception
     */
    public void modifyColumnType(String tableName, String columnName, String dataType) throws Exception {
        Connection connection = getConnection();

        try {
        	modifyColumnType(connection, tableName, columnName, dataType, false);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Debug.logError(e, module);
            }
        }
    }
    
    /**
     * Modify column
     * @param tableName
     * @param columnName
     * @param dataType
     * @throws Exception
     */
    public void addNotNullConstraint(String tableName, String columnName, String dataType) throws Exception {
        Connection connection = getConnection();

        try {
            StringBuilder sqlBuf = new StringBuilder("ALTER TABLE ");
            sqlBuf.append(getNameWithSchema(tableName));
            if (isMsSql() || isPostgres()) {
            	sqlBuf.append(" ALTER COLUMN ");
            } else if (isMySql()) {
            	sqlBuf.append(" MODIFY COLUMN ");
            } else {
            	sqlBuf.append(" MODIFY ");
            }
            sqlBuf.append(columnName);
            
            if (isPostgres()) {
                sqlBuf.append(" SET NOT NULL");
            } else {
            	ModelFieldType modelFieldType = modelFieldTypeReader.getModelFieldType(dataType);
                if (modelFieldType != null) {
                	sqlBuf.append(" " + modelFieldType.getSqlType() + " NOT NULL");
                }
            }
            
            String addNotNullConstraint = sqlBuf.toString();

            Debug.log("[addNotNullConstraint] sql=" + addNotNullConstraint, module);

            executeQuery(connection, addNotNullConstraint);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Debug.logError(e, module);
            }
        }
    }
    
    /**
     * add foreign key
     * @param tableName
     * @param fkName
     * @param columnName
     * @param refTableName
     * @param refColumnName
     * @throws Exception
     */
    public void addForeignKey(String tableName, String fkName, String columnName, String refTableName, String refColumnName) throws Exception {
        Connection connection = getConnection();
        try {
        	addForeignKey(connection, tableName, fkName, columnName, refTableName, refColumnName);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Debug.logError(e, module);
            }
        }
    }
    
    /**
     * Modify column and pk
     * @param tableName
     * @param columnName
     * @param dataType
     * @param pkFields
     * @throws Exception
     */
    public void modifyColumnTypeAndRedefinePk(String tableName, String columnName, String dataType, List<String> pkFields) throws Exception {
    	Connection connection = getConnection();
    	
    	try {
    		if (isMsSql()) {
    			dropPkConstraint(connection, tableName);
    			modifyColumnType(connection, tableName, columnName, dataType, true);
    			addPkConstraint(connection, tableName, pkFields);    			
    		} else {
    			modifyColumnType(connection, tableName, columnName, dataType, false);
    		}
    	} finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Debug.logError(e, module);
            }
        }
    }
    
    public void modifyColumnSizeAndDeleteIndex(ModelEntity modelEntity, ModelField modelField, Map<String, ModelEntity> modelEntities) throws Exception {
        Connection connection = getConnection();
        
        // step 1 - remove FK indices
        Debug.logImportant("Removing all foreign key indices", module);
        deleteForeignKeyIndices(modelEntity, null);

        // step 2 - remove FKs
        Debug.logImportant("Removing all foreign keys", module);
        deleteForeignKeys(modelEntity, modelEntities, null);
        

        // step 3 - remove PKs
        Debug.logImportant("Removing all primary keys", module);
        deletePrimaryKey(modelEntity, null);
        
        // step 4 - remove declared indices
        Debug.logImportant("Removing all declared indices", module);
        deleteDeclaredIndices(modelEntity, null);
        

        // step 5 - repair field sizes
        Debug.logImportant("Updating column field size changes", module);
        modifyColumnSize(connection, modelEntity, modelField);

        // step 6 - create PKs
        Debug.logImportant("Creating all primary keys", module);
        createPrimaryKey(modelEntity, null);

        // step 7 - create FK indices
        Debug.logImportant("Creating all foreign key indices", module);
        createForeignKeyIndices(modelEntity, null);

        // step 8 - create FKs
        Debug.logImportant("Creating all foreign keys", module);
        createForeignKeys(modelEntity, modelEntities, null);

        // step 8 - create FKs
        Debug.logImportant("Creating all declared indices", module);
        createDeclaredIndices(modelEntity, null);

        // step 8 - checkdb
        Debug.logImportant("Running DB check with add missing enabled", module);
        // TODO checkDb(modelEntities, null, true);

    }
    
    /**
     * ridefinisce la pk
     * @param tableName
     * @param pkConstraintName
     * @param pkFields
     * @throws Exception
     */
    public void redefinePK(String tableName, String pkConstraintName, List<String> pkFields) throws Exception {
    	Connection connection = getConnection();
    	
    	try {
    		dropPkConstraint(connection, tableName, pkConstraintName);
    		addPkConstraint(connection, tableName, pkConstraintName, pkFields);
    	} finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Debug.logError(e, module);
            }
        }
    }

    /**
     * Get info
     */
	public Map<String, Map<String, ReferenceCheckInfo>> getReferenceInfo(Set<String> tableNames, Collection<String> messages) {
        Connection connection = null;
        try {
            connection = getConnection();
        } catch (SQLException e) {
            String message = "Unable to establish a connection with the database... Error was:" + e.toString();
            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            return null;
        } catch (GenericEntityException e) {
            String message = "Unable to establish a connection with the database... Error was:" + e.toString();
            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            return null;
        }

        DatabaseMetaData dbData = null;
        try {
            dbData = connection.getMetaData();
        } catch (SQLException e) {
            String message = "Unable to get database meta data... Error was:" + e.toString();
            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);

            try {
                connection.close();
            } catch (SQLException e2) {
                String message2 = "Unable to close database connection, continuing anyway... Error was:" + e2.toString();
                Debug.logError(message2, module);
                if (messages != null)
                    messages.add(message2);
            }
            return null;
        }

        if (Debug.infoOn())
            Debug.logInfo("Getting Foreign Key (Reference) Info From Database", module);

        Map<String, Map<String, ReferenceCheckInfo>> refInfo = FastMap.newInstance();

        try {
            String lookupSchemaName = null;

            if (dbData.supportsSchemasInTableDefinitions()) {
                if (UtilValidate.isNotEmpty(this.datasourceInfo.schemaName)) {
                    lookupSchemaName = this.datasourceInfo.schemaName;
                } else {
                    lookupSchemaName = dbData.getUserName();
                }
            }

            boolean needsUpperCase = false;
            try {
                needsUpperCase = dbData.storesLowerCaseIdentifiers() || dbData.storesMixedCaseIdentifiers();
            } catch (SQLException e) {
                String message = "Error getting identifier case information... Error was:" + e.toString();
                Debug.logError(message, module);
                if (messages != null)
                    messages.add(message);
            }

            ResultSet rsCols = null;
            int totalFkRefs = 0;
            try {
                rsCols = dbData.getImportedKeys(null, lookupSchemaName, null);
                totalFkRefs = getReferenceInfoFromResultSet(tableNames, messages, refInfo, rsCols, needsUpperCase);

            } catch (Exception e) {
                Iterator<String> tableNamesIter = tableNames.iterator();
                while (tableNamesIter.hasNext()) {
                    String tableName = (String)tableNamesIter.next();
                    rsCols = dbData.getImportedKeys(null, lookupSchemaName, tableName);
                    Debug.log("Getting imported keys for table " + tableName, module);
                    totalFkRefs += getReferenceInfoFromResultSet(tableNames, messages, refInfo, rsCols, needsUpperCase);
                }
            }

            if (Debug.infoOn())
                Debug.logInfo("There are " + totalFkRefs + " foreign key refs in the database", module);

        } catch (SQLException e) {
            String message = "Error getting fk reference meta data Error was:" + e.toString() + ". Not checking fk refs.";
            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            refInfo = null;
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                String message = "Unable to close database connection, continuing anyway... Error was:" + e.toString();
                Debug.logError(message, module);
                if (messages != null)
                    messages.add(message);
            }
        }
        return refInfo;
    }

    private int getReferenceInfoFromResultSet(Set<String> tableNames, Collection<String> messages, Map<String, Map<String, ReferenceCheckInfo>> refInfo, ResultSet rsCols, boolean needsUpperCase) throws SQLException {
        int totalFkRefs = 0;

        while (rsCols.next()) {
            try {
                ReferenceCheckInfo rcInfo = new ReferenceCheckInfo();

                rcInfo.pkTableName = rsCols.getString("PKTABLE_NAME");
                if (needsUpperCase && rcInfo.pkTableName != null) {
                    rcInfo.pkTableName = rcInfo.pkTableName.toUpperCase();
                }
                rcInfo.pkColumnName = rsCols.getString("PKCOLUMN_NAME");
                if (needsUpperCase && rcInfo.pkColumnName != null) {
                    rcInfo.pkColumnName = rcInfo.pkColumnName.toUpperCase();
                }

                rcInfo.fkTableName = rsCols.getString("FKTABLE_NAME");
                if (needsUpperCase && rcInfo.fkTableName != null) {
                    rcInfo.fkTableName = rcInfo.fkTableName.toUpperCase();
                }
                // ignore the column info if the FK table name is not in the list we are concerned with
                if (!tableNames.contains(rcInfo.fkTableName)) {
                    continue;
                }
                rcInfo.fkColumnName = rsCols.getString("FKCOLUMN_NAME");
                if (needsUpperCase && rcInfo.fkColumnName != null) {
                    rcInfo.fkColumnName = rcInfo.fkColumnName.toUpperCase();
                }
                rcInfo.fkName = rsCols.getString("FK_NAME");
                if (needsUpperCase && rcInfo.fkName != null) {
                    rcInfo.fkName = rcInfo.fkName.toUpperCase();
                }

                if (Debug.verboseOn())
                    Debug.log("Got: " + rcInfo.toString(), module);

                Map<String, ReferenceCheckInfo> tableRefInfo = refInfo.get(rcInfo.fkTableName);
                if (tableRefInfo == null) {
                    tableRefInfo = FastMap.newInstance();
                    refInfo.put(rcInfo.fkTableName, tableRefInfo);
                    if (Debug.verboseOn())
                        Debug.log("Adding new Map for table: " + rcInfo.fkTableName, module);
                }
                if (!tableRefInfo.containsKey(rcInfo.fkName))
                    totalFkRefs++;

                tableRefInfo.put(rcInfo.fkName, rcInfo);
            } catch (SQLException e) {
                String message = "Error getting fk reference info for table. Error was:" + e.toString();
                Debug.logError(message, module);
                if (messages != null)
                    messages.add(message);
                continue;
            }
        }

        try {
            rsCols.close();
        } catch (SQLException e) {
            String message = "Unable to close ResultSet for fk reference list, continuing anyway... Error was:" + e.toString();
            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
        }

        return totalFkRefs;
    }

    private void deleteForeignKeyIndex(Connection connection, String tableName, String indexName) throws Exception {
        TreeSet<String> indexTableNames = new TreeSet<String>();
        indexTableNames.add(tableName);

        List<String> messages = FastList.newInstance();
        Map<String, Set<String>> tableIndexListMap = this.getIndexInfo(indexTableNames, messages);
        checkErrorMessages(messages);

        if (tableIndexListMap != null) {
            Set<String> tableIndexList = tableIndexListMap.get(tableName);
            if (tableIndexList != null && tableIndexList.contains(indexName)) {
                removeForeignKeyIndex(connection, tableName, indexName);
            }
        }
    }

    private void removeForeignKeyIndex(Connection connection, String tableName, String indexName) throws SQLException {
        StringBuilder indexSqlBuf = new StringBuilder("");
        if (isMsSql()) {
            indexSqlBuf.append(" DROP INDEX ");
            indexSqlBuf.append(getNameWithSchema(tableName));
            indexSqlBuf.append(".");
            indexSqlBuf.append(indexName);
        } else {
            if (isMySql()) {
                indexSqlBuf.append("ALTER TABLE " + getNameWithSchema(tableName));
            }
            indexSqlBuf.append(" DROP INDEX ");
            indexSqlBuf.append(isMySql() ? indexName : getNameWithSchema(indexName));
        }

        String deleteIndexSql = indexSqlBuf.toString();

        Debug.log("[deleteForeignKeyIndex] index sql=" + deleteIndexSql, module);

        executeQuery(connection, deleteIndexSql);
    }

    private void deleteForeignKey(Connection connection, String tableName, String indexName) throws Exception {
        TreeSet<String> fkTableNames = new TreeSet<String>();
        fkTableNames.add(tableName);

        List<String> messages = FastList.newInstance();
        Map<String, Map<String, ReferenceCheckInfo>> refTableInfoMap = this.getReferenceInfo(fkTableNames, messages);
        checkErrorMessages(messages);

        if (refTableInfoMap != null) {
            Map<String, ReferenceCheckInfo> refCheckInfo = refTableInfoMap.get(tableName);
            if (refCheckInfo != null && refCheckInfo.containsKey(indexName)) {
                removeForeignKey(connection, tableName, indexName);
            }
        }
    }

    private void removeForeignKey(Connection connection, String tableName, String indexName) throws SQLException {
        // now add constraint clause
        StringBuilder sqlBuf = new StringBuilder("ALTER TABLE ");
        sqlBuf.append(getNameWithSchema(tableName));
        if (datasourceInfo.dropFkUseForeignKeyKeyword) {
            sqlBuf.append(" DROP FOREIGN KEY ");
        } else {
            sqlBuf.append(" DROP CONSTRAINT ");
        }
        sqlBuf.append(indexName);

        String deleteForeignKeySql = sqlBuf.toString();

        Debug.log("[deleteForeignKey] sql=" + deleteForeignKeySql, module);

        executeQuery(connection, deleteForeignKeySql);

    }
    
    /**
     * Create index
     * @param entity
     * @param indexName
     */
    public void createDeclaredIndex(ModelEntity entity, String indexName) {
        if (entity == null) {
            String message = "ERROR: ModelEntity was null and is required to create declared indices for a table";
            Debug.logError(message, module);
            return;
        }
        if (entity instanceof ModelViewEntity) {
            String message = "WARNING: Cannot create declared indices for a view entity";
            Debug.logWarning(message, module);
            return;
        }
        
        ModelIndex modelIndex = entity.getIndex(indexName);
        String retMsg = createDeclaredIndex(entity, modelIndex);
        if (UtilValidate.isNotEmpty(retMsg)) {
            String message = "Could not create declared indices for entity [" + entity.getEntityName() + "]: " + retMsg;
            Debug.logError(message, module);
        } else {
            String message = "Created declared index " + indexName + " for entity [" + entity.getEntityName() + "]";
            Debug.logImportant(message, module);               	
        }
    }
    
    private void modifyColumnSize(Connection connection, ModelEntity modelEntity, ModelField modelField) throws SQLException {
        String columnName = modelField.getColName();
        String tableName = modelEntity.getTableName(datasourceInfo);
        
        String tempName = "tmp_" + columnName;
        if (tempName.length() > 30) {
            tempName = tempName.substring(0, 30);
        }
        
        renameColumn(modelEntity, modelField, tempName);
        addColumn(modelEntity, modelField);
        
        // copy the data from old to new
        StringBuilder sqlBuf1 = new StringBuilder("UPDATE ");
        sqlBuf1.append(tableName);
        sqlBuf1.append(" SET ");
        sqlBuf1.append(columnName);
        sqlBuf1.append(" = ");
        sqlBuf1.append(tempName);

        String sql1 = sqlBuf1.toString();
        if (Debug.infoOn()) Debug.logInfo("[moveData] sql=" + sql1, module);

        executeQuery(connection, sql1);

        // remove the old column
        StringBuilder sqlBuf2 = new StringBuilder("ALTER TABLE ");
        sqlBuf2.append(tableName);
        sqlBuf2.append(" DROP COLUMN ");
        sqlBuf2.append(tempName);

        String sql2 = sqlBuf2.toString();
        if (Debug.infoOn()) Debug.logInfo("[dropColumn] sql=" + sql2, module);
        executeQuery(connection, sql2);
    }
    
    private void modifyColumnType(Connection connection, String tableName, String columnName, String dataType, boolean notNullable) throws SQLException {
        // now add constraint clause
        StringBuilder sqlBuf = new StringBuilder("ALTER TABLE ");
        sqlBuf.append(getNameWithSchema(tableName));
        if (isMsSql() || isPostgres()) {
        	sqlBuf.append(" ALTER COLUMN ");
        } else if (isMySql()) {
        	sqlBuf.append(" MODIFY COLUMN ");
        } else {
        	sqlBuf.append(" MODIFY ");
        }
        sqlBuf.append(columnName);
        ModelFieldType modelFieldType = modelFieldTypeReader.getModelFieldType(dataType);
        if (modelFieldType != null) {
        	sqlBuf.append(" ");
        	if (isPostgres()) {
                sqlBuf.append("TYPE ");
            }
        	sqlBuf.append(modelFieldType.getSqlType());
        }
        if (notNullable) {
        	sqlBuf.append(" NOT NULL");
        }

        String modifyColumnTypeSql = sqlBuf.toString();

        Debug.log("[modifyColumnTypeSql] sql=" + modifyColumnTypeSql, module);

        executeQuery(connection, modifyColumnTypeSql);
    }
    
    private void addForeignKey(Connection connection, String tableName, String fkName, String columnName, String refTableName, String refColumnName) throws SQLException {
    	StringBuilder sqlBuf = new StringBuilder("ALTER TABLE ");
    	sqlBuf.append(getNameWithSchema(tableName));
    	sqlBuf.append(" ADD CONSTRAINT ");
    	sqlBuf.append(fkName);
    	sqlBuf.append(" FOREIGN KEY (");
    	sqlBuf.append(columnName);
    	sqlBuf.append(") REFERENCES ");
    	sqlBuf.append(refTableName);
    	sqlBuf.append(" (");
    	sqlBuf.append(refColumnName);
    	sqlBuf.append(")");
    	
        String addForeignKeySql = sqlBuf.toString();

        Debug.log("[addForeignKey] sql=" + addForeignKeySql, module);

        executeQuery(connection, addForeignKeySql);
    }
    
    private void dropPkConstraint(Connection connection, String tableName) throws SQLException {
    	StringBuilder sqlBuf = new StringBuilder("ALTER TABLE ");
    	sqlBuf.append(tableName);
    	sqlBuf.append(" DROP CONSTRAINT PK_");
    	sqlBuf.append(tableName);
    	
    	String sql = sqlBuf.toString();
    	Debug.log("[dropPkConstraint] sql=" + sql, module);
    	
    	executeQuery(connection, sql);
    }
    
    private void dropPkConstraint(Connection connection, String tableName, String pkConstraintName) throws SQLException {
    	StringBuilder sqlBuf = new StringBuilder("ALTER TABLE ");
    	sqlBuf.append(tableName);
    	if (isMySql()) {
    		sqlBuf.append(" DROP PRIMARY KEY");
    	} else {
    	    sqlBuf.append(" DROP CONSTRAINT ");
    	    sqlBuf.append(pkConstraintName);
    	}
    	
    	String sql = sqlBuf.toString();
    	Debug.log("[dropPkConstraint] sql=" + sql, module);
    	
    	executeQuery(connection, sql);
    }
    
    private void executeQuery(Connection connection, String sql) throws SQLException {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            Debug.logError("SQL Exception while executing the following:\n" + sql, module);
            throw e;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                Debug.logError(e, module);
            }
        }
    }

    private void addPkConstraint(Connection connection, String tableName, List<String> pkFields) throws SQLException {
    	StringBuilder sqlBuf = new StringBuilder("ALTER TABLE ");
    	sqlBuf.append(tableName);
    	sqlBuf.append(" ADD CONSTRAINT PK_");
    	sqlBuf.append(tableName);
    	sqlBuf.append(" PRIMARY KEY (");
    	
    	ListIterator<String> pkFieldsListIterator = pkFields.listIterator();
    	while(pkFieldsListIterator.hasNext()) {
    		String pkField = pkFieldsListIterator.next();
    		int i = pkFieldsListIterator.nextIndex();
    		sqlBuf.append(pkField);
    		if (i < pkFields.size()) {
    			sqlBuf.append(", ");
    		}
    	}
    	sqlBuf.append(")");
    	
    	String sql = sqlBuf.toString();
    	Debug.log("[addPkConstraint] sql=" + sql, module);
    	
    	executeQuery(connection, sql);
    }
    
    private void addPkConstraint(Connection connection, String tableName, String pkConstraintName, List<String> pkFields) throws SQLException {
    	StringBuilder sqlBuf = new StringBuilder("ALTER TABLE ");
    	sqlBuf.append(tableName);
    	if (isMySql()) {
    		sqlBuf.append(" ADD PRIMARY KEY (");
    	} else {
        	sqlBuf.append(" ADD CONSTRAINT ");
        	sqlBuf.append(pkConstraintName);
        	sqlBuf.append(" PRIMARY KEY (");	
    	}
    	
    	ListIterator<String> pkFieldsListIterator = pkFields.listIterator();
    	while(pkFieldsListIterator.hasNext()) {
    		String pkField = pkFieldsListIterator.next();
    		int i = pkFieldsListIterator.nextIndex();
    		sqlBuf.append(pkField);
    		if (i < pkFields.size()) {
    			sqlBuf.append(", ");
    		}
    	}
    	sqlBuf.append(")");
    	
    	String sql = sqlBuf.toString();
    	Debug.log("[addPkConstraint] sql=" + sql, module);
    	
    	executeQuery(connection, sql);
    }

    private String getNameWithSchema(String name) {
        if (UtilValidate.isNotEmpty(datasourceInfo) && UtilValidate.isNotEmpty(datasourceInfo.schemaName)) {
            return datasourceInfo.schemaName + "." + name;
        } else {
            return name;
        }
    }

    private void checkErrorMessages(List<String> messages) throws Exception {
        StringBuffer str = new StringBuffer();
        if (UtilValidate.isNotEmpty(messages)) {
            for (String message : messages) {
                if (str.length() > 0) {
                    str.append('\n');
                }
                str.append(message);
            }
        }
        if (str.length() > 0) {
            throw new Exception(str.toString());
        }
    }

    private boolean isMySql() {
        if ("mysql".equals(datasourceInfo.fieldTypeName)) {
            return true;
        }
        return false;
    }

    private boolean isMsSql() {
        if ("mssql".equals(datasourceInfo.fieldTypeName)) {
            return true;
        }
        return false;
    }
    
    private boolean isPostgres() {
        if ("postgres".equals(datasourceInfo.fieldTypeName)) {
            return true;
        }
        return false;
    }
}
