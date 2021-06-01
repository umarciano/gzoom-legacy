/**
 * StandardImportResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mapsengineering.standardImport.ws;

public class StandardImportResponse  implements java.io.Serializable {
    private java.lang.String code;

    private java.lang.String message;

    private int recordElaborated;

    private int blockingErrors;

    private int warningMessages;

    public StandardImportResponse() {
    }

    public StandardImportResponse(
           java.lang.String code,
           java.lang.String message,
           int recordElaborated,
           int blockingErrors,
           int warningMessages) {
           this.code = code;
           this.message = message;
           this.recordElaborated = recordElaborated;
           this.blockingErrors = blockingErrors;
           this.warningMessages = warningMessages;
    }


    /**
     * Gets the code value for this StandardImportResponse.
     * 
     * @return code
     */
    public java.lang.String getCode() {
        return code;
    }


    /**
     * Sets the code value for this StandardImportResponse.
     * 
     * @param code
     */
    public void setCode(java.lang.String code) {
        this.code = code;
    }


    /**
     * Gets the message value for this StandardImportResponse.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this StandardImportResponse.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }


    /**
     * Gets the recordElaborated value for this StandardImportResponse.
     * 
     * @return recordElaborated
     */
    public int getRecordElaborated() {
        return recordElaborated;
    }


    /**
     * Sets the recordElaborated value for this StandardImportResponse.
     * 
     * @param recordElaborated
     */
    public void setRecordElaborated(int recordElaborated) {
        this.recordElaborated = recordElaborated;
    }


    /**
     * Gets the blockingErrors value for this StandardImportResponse.
     * 
     * @return blockingErrors
     */
    public int getBlockingErrors() {
        return blockingErrors;
    }


    /**
     * Sets the blockingErrors value for this StandardImportResponse.
     * 
     * @param blockingErrors
     */
    public void setBlockingErrors(int blockingErrors) {
        this.blockingErrors = blockingErrors;
    }


    /**
     * Gets the warningMessages value for this StandardImportResponse.
     * 
     * @return warningMessages
     */
    public int getWarningMessages() {
        return warningMessages;
    }


    /**
     * Sets the warningMessages value for this StandardImportResponse.
     * 
     * @param warningMessages
     */
    public void setWarningMessages(int warningMessages) {
        this.warningMessages = warningMessages;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StandardImportResponse)) return false;
        StandardImportResponse other = (StandardImportResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.code==null && other.getCode()==null) || 
             (this.code!=null &&
              this.code.equals(other.getCode()))) &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            this.recordElaborated == other.getRecordElaborated() &&
            this.blockingErrors == other.getBlockingErrors() &&
            this.warningMessages == other.getWarningMessages();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCode() != null) {
            _hashCode += getCode().hashCode();
        }
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        _hashCode += getRecordElaborated();
        _hashCode += getBlockingErrors();
        _hashCode += getWarningMessages();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StandardImportResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.standardImport.mapsengineering.com/", "standardImportResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("code");
        elemField.setXmlName(new javax.xml.namespace.QName("", "code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordElaborated");
        elemField.setXmlName(new javax.xml.namespace.QName("", "recordElaborated"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("blockingErrors");
        elemField.setXmlName(new javax.xml.namespace.QName("", "blockingErrors"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("warningMessages");
        elemField.setXmlName(new javax.xml.namespace.QName("", "warningMessages"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
