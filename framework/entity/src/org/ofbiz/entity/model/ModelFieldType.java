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
package org.ofbiz.entity.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.jdbc.SqlTypes.SqlObjectHandler;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Generic Entity - FieldType model class
 *
 */

@SuppressWarnings("serial")
public class ModelFieldType implements Serializable {

    public static final String module = ModelFieldType.class.getName();

    /** The type of the Field */
    protected String type = null;

    /** The java-type of the Field */
    protected String javaType = null;

    /** The SQL object handler for this Field */
    protected SqlObjectHandler sqlObjectHandler = null;

    /** The sql-type of the Field */
    protected String sqlType = null;

    /** The JDBC data type of the Field */
    protected String jdbcDataType = null;

    /** The sql-type-alias of the Field, this is optional */
    protected String sqlTypeAlias = null;

    /** validators to be called when an update is done */
    protected List<ModelFieldValidator> validators = new ArrayList<ModelFieldValidator>();

    /** Default Constructor */
    public ModelFieldType() {}

    /** XML Constructor */
    public ModelFieldType(Element fieldTypeElement) {
        this.type = UtilXml.checkEmpty(fieldTypeElement.getAttribute("type")).intern();
        this.javaType = UtilXml.checkEmpty(fieldTypeElement.getAttribute("java-type")).intern();
        this.sqlType = UtilXml.checkEmpty(fieldTypeElement.getAttribute("sql-type")).intern();
        this.jdbcDataType = UtilXml.checkEmpty(fieldTypeElement.getAttribute("jdbc-data-type")).intern();
        this.sqlTypeAlias = UtilXml.checkEmpty(fieldTypeElement.getAttribute("sql-type-alias")).intern();
        NodeList validateList = fieldTypeElement.getElementsByTagName("validate");
        for (int i = 0; i < validateList.getLength(); i++) {
            Element element = (Element) validateList.item(i);
            String methodName = element.getAttribute("method");
            String className = element.getAttribute("class");
            if (methodName != null) {
                this.validators.add(new ModelFieldValidator(className.intern(), methodName.intern()));
            }
        }
        ((ArrayList<ModelFieldValidator>)this.validators).trimToSize();
    }

    /** The type of the Field */
    public String getType() {
        return this.type;
    }

    /** The java-type of the Field */
    public String getJavaType() {
        return this.javaType;
    }

    /** The SQL object handler for this Field */
    public SqlObjectHandler getSqlObjectHandler() {
        return this.sqlObjectHandler;
    }

    /** The sql-type of the Field */
    public String getSqlType() {
        return this.sqlType;
    }

    /** The JDBC data type of the Field */
    public String getJdbcDataType() {
        return this.jdbcDataType;
    }

    /** The sql-type-alias of the Field */
    public String getSqlTypeAlias() {
        return this.sqlTypeAlias;
    }

    /** validators to be called when an update is done */
    public List<ModelFieldValidator> getValidators() {
        return this.validators;
    }

    /** Sets the <code>SqlObjectHandler</code> for this field.
     *
     * @param sqlObjectHandler
     */
    public synchronized void setSqlObjectHandler(SqlObjectHandler sqlObjectHandler) {
        this.sqlObjectHandler = sqlObjectHandler;
    }

    /** A simple function to derive the max length of a String created from the field value, based on the sql-type
     * @return max length of a String representing the Field value
     */
    public int stringLength() {
       String sqlTypeUpperCase = sqlType.toUpperCase();
        if (sqlTypeUpperCase.indexOf("VARCHAR") >= 0) {
            if (sqlTypeUpperCase.indexOf("(") > 0 && sqlTypeUpperCase.indexOf(")") > 0) {
                String length = sqlTypeUpperCase.substring(sqlTypeUpperCase.indexOf("(") + 1, sqlTypeUpperCase.indexOf(")"));
                if(length.indexOf(" ") > 0) {
                    length = sqlTypeUpperCase.substring(sqlTypeUpperCase.indexOf("(") + 1, sqlTypeUpperCase.indexOf(" "));
                }
                
                return Integer.parseInt(length);
            } else {
                return 255;
            }
        } else if (sqlTypeUpperCase.indexOf("CHAR") >= 0) {
            if (sqlTypeUpperCase.indexOf("(") > 0 && sqlTypeUpperCase.indexOf(")") > 0) {
                String length = sqlTypeUpperCase.substring(sqlTypeUpperCase.indexOf("(") + 1, sqlTypeUpperCase.indexOf(")"));

                return Integer.parseInt(length);
            } else {
                return 255;
            }
        } else if (sqlTypeUpperCase.indexOf("TEXT") >= 0 || sqlTypeUpperCase.indexOf("LONG") >= 0 || sqlTypeUpperCase.indexOf("CLOB") >= 0) {
            return 5000;
        }
        return 20;
    }

    class ModelFieldValidator implements Serializable {

        protected String validatorClass = null;
        protected String validatorMethod = null;

        public ModelFieldValidator(String className, String methodName) {
            this.validatorClass = className;
            this.validatorMethod = methodName;
        }

        public String getClassName() {
            if (UtilValidate.isNotEmpty(validatorClass) && UtilValidate.isNotEmpty(validatorMethod)) {
                return validatorClass;
            }
            return null;
        }

        public String getMethodName() {
            if (UtilValidate.isNotEmpty(validatorClass) && UtilValidate.isNotEmpty(validatorMethod)) {
                return validatorMethod;
            }
            return null;
        }
    }
}
