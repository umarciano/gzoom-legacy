/**
 * AllocationInterfaceExt.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mapsengineering.standardImport.ws;

public class AllocationInterfaceExt  implements java.io.Serializable {
    private java.lang.String dataSource;

    private java.util.Date refDate;

    private java.lang.String personCode;

    private java.lang.String personRoleTypeId;

    private java.lang.String allocationRoleTypeId;

    private java.lang.String allocationOrgCode;

    private java.lang.String allocationOrgComments;

    private java.lang.String allocationOrgDescription;

    private java.util.Date allocationFromDate;

    private java.util.Date allocationThruDate;

    private int allocationValue;

    public AllocationInterfaceExt() {
    }

    public AllocationInterfaceExt(
           java.lang.String dataSource,
           java.util.Date refDate,
           java.lang.String personCode,
           java.lang.String personRoleTypeId,
           java.lang.String allocationRoleTypeId,
           java.lang.String allocationOrgCode,
           java.lang.String allocationOrgComments,
           java.lang.String allocationOrgDescription,
           java.util.Date allocationFromDate,
           java.util.Date allocationThruDate,
           int allocationValue) {
           this.dataSource = dataSource;
           this.refDate = refDate;
           this.personCode = personCode;
           this.personRoleTypeId = personRoleTypeId;
           this.allocationRoleTypeId = allocationRoleTypeId;
           this.allocationOrgCode = allocationOrgCode;
           this.allocationOrgComments = allocationOrgComments;
           this.allocationOrgDescription = allocationOrgDescription;
           this.allocationFromDate = allocationFromDate;
           this.allocationThruDate = allocationThruDate;
           this.allocationValue = allocationValue;
    }


    /**
     * Gets the dataSource value for this AllocationInterfaceExt.
     * 
     * @return dataSource
     */
    public java.lang.String getDataSource() {
        return dataSource;
    }


    /**
     * Sets the dataSource value for this AllocationInterfaceExt.
     * 
     * @param dataSource
     */
    public void setDataSource(java.lang.String dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Gets the refDate value for this AllocationInterfaceExt.
     * 
     * @return refDate
     */
    public java.util.Date getRefDate() {
        return refDate;
    }


    /**
     * Sets the refDate value for this AllocationInterfaceExt.
     * 
     * @param refDate
     */
    public void setRefDate(java.util.Date refDate) {
        this.refDate = refDate;
    }


    /**
     * Gets the personCode value for this AllocationInterfaceExt.
     * 
     * @return personCode
     */
    public java.lang.String getPersonCode() {
        return personCode;
    }


    /**
     * Sets the personCode value for this AllocationInterfaceExt.
     * 
     * @param personCode
     */
    public void setPersonCode(java.lang.String personCode) {
        this.personCode = personCode;
    }


    /**
     * Gets the personRoleTypeId value for this AllocationInterfaceExt.
     * 
     * @return personRoleTypeId
     */
    public java.lang.String getPersonRoleTypeId() {
        return personRoleTypeId;
    }


    /**
     * Sets the personRoleTypeId value for this AllocationInterfaceExt.
     * 
     * @param personRoleTypeId
     */
    public void setPersonRoleTypeId(java.lang.String personRoleTypeId) {
        this.personRoleTypeId = personRoleTypeId;
    }


    /**
     * Gets the allocationRoleTypeId value for this AllocationInterfaceExt.
     * 
     * @return allocationRoleTypeId
     */
    public java.lang.String getAllocationRoleTypeId() {
        return allocationRoleTypeId;
    }


    /**
     * Sets the allocationRoleTypeId value for this AllocationInterfaceExt.
     * 
     * @param allocationRoleTypeId
     */
    public void setAllocationRoleTypeId(java.lang.String allocationRoleTypeId) {
        this.allocationRoleTypeId = allocationRoleTypeId;
    }


    /**
     * Gets the allocationOrgCode value for this AllocationInterfaceExt.
     * 
     * @return allocationOrgCode
     */
    public java.lang.String getAllocationOrgCode() {
        return allocationOrgCode;
    }


    /**
     * Sets the allocationOrgCode value for this AllocationInterfaceExt.
     * 
     * @param allocationOrgCode
     */
    public void setAllocationOrgCode(java.lang.String allocationOrgCode) {
        this.allocationOrgCode = allocationOrgCode;
    }


    /**
     * Gets the allocationOrgComments value for this AllocationInterfaceExt.
     * 
     * @return allocationOrgComments
     */
    public java.lang.String getAllocationOrgComments() {
        return allocationOrgComments;
    }


    /**
     * Sets the allocationOrgComments value for this AllocationInterfaceExt.
     * 
     * @param allocationOrgComments
     */
    public void setAllocationOrgComments(java.lang.String allocationOrgComments) {
        this.allocationOrgComments = allocationOrgComments;
    }


    /**
     * Gets the allocationOrgDescription value for this AllocationInterfaceExt.
     * 
     * @return allocationOrgDescription
     */
    public java.lang.String getAllocationOrgDescription() {
        return allocationOrgDescription;
    }


    /**
     * Sets the allocationOrgDescription value for this AllocationInterfaceExt.
     * 
     * @param allocationOrgDescription
     */
    public void setAllocationOrgDescription(java.lang.String allocationOrgDescription) {
        this.allocationOrgDescription = allocationOrgDescription;
    }


    /**
     * Gets the allocationFromDate value for this AllocationInterfaceExt.
     * 
     * @return allocationFromDate
     */
    public java.util.Date getAllocationFromDate() {
        return allocationFromDate;
    }


    /**
     * Sets the allocationFromDate value for this AllocationInterfaceExt.
     * 
     * @param allocationFromDate
     */
    public void setAllocationFromDate(java.util.Date allocationFromDate) {
        this.allocationFromDate = allocationFromDate;
    }


    /**
     * Gets the allocationThruDate value for this AllocationInterfaceExt.
     * 
     * @return allocationThruDate
     */
    public java.util.Date getAllocationThruDate() {
        return allocationThruDate;
    }


    /**
     * Sets the allocationThruDate value for this AllocationInterfaceExt.
     * 
     * @param allocationThruDate
     */
    public void setAllocationThruDate(java.util.Date allocationThruDate) {
        this.allocationThruDate = allocationThruDate;
    }


    /**
     * Gets the allocationValue value for this AllocationInterfaceExt.
     * 
     * @return allocationValue
     */
    public int getAllocationValue() {
        return allocationValue;
    }


    /**
     * Sets the allocationValue value for this AllocationInterfaceExt.
     * 
     * @param allocationValue
     */
    public void setAllocationValue(int allocationValue) {
        this.allocationValue = allocationValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AllocationInterfaceExt)) return false;
        AllocationInterfaceExt other = (AllocationInterfaceExt) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dataSource==null && other.getDataSource()==null) || 
             (this.dataSource!=null &&
              this.dataSource.equals(other.getDataSource()))) &&
            ((this.refDate==null && other.getRefDate()==null) || 
             (this.refDate!=null &&
              this.refDate.equals(other.getRefDate()))) &&
            ((this.personCode==null && other.getPersonCode()==null) || 
             (this.personCode!=null &&
              this.personCode.equals(other.getPersonCode()))) &&
            ((this.personRoleTypeId==null && other.getPersonRoleTypeId()==null) || 
             (this.personRoleTypeId!=null &&
              this.personRoleTypeId.equals(other.getPersonRoleTypeId()))) &&
            ((this.allocationRoleTypeId==null && other.getAllocationRoleTypeId()==null) || 
             (this.allocationRoleTypeId!=null &&
              this.allocationRoleTypeId.equals(other.getAllocationRoleTypeId()))) &&
            ((this.allocationOrgCode==null && other.getAllocationOrgCode()==null) || 
             (this.allocationOrgCode!=null &&
              this.allocationOrgCode.equals(other.getAllocationOrgCode()))) &&
            ((this.allocationOrgComments==null && other.getAllocationOrgComments()==null) || 
             (this.allocationOrgComments!=null &&
              this.allocationOrgComments.equals(other.getAllocationOrgComments()))) &&
            ((this.allocationOrgDescription==null && other.getAllocationOrgDescription()==null) || 
             (this.allocationOrgDescription!=null &&
              this.allocationOrgDescription.equals(other.getAllocationOrgDescription()))) &&
            ((this.allocationFromDate==null && other.getAllocationFromDate()==null) || 
             (this.allocationFromDate!=null &&
              this.allocationFromDate.equals(other.getAllocationFromDate()))) &&
            ((this.allocationThruDate==null && other.getAllocationThruDate()==null) || 
             (this.allocationThruDate!=null &&
              this.allocationThruDate.equals(other.getAllocationThruDate()))) &&
            this.allocationValue == other.getAllocationValue();
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
        if (getDataSource() != null) {
            _hashCode += getDataSource().hashCode();
        }
        if (getRefDate() != null) {
            _hashCode += getRefDate().hashCode();
        }
        if (getPersonCode() != null) {
            _hashCode += getPersonCode().hashCode();
        }
        if (getPersonRoleTypeId() != null) {
            _hashCode += getPersonRoleTypeId().hashCode();
        }
        if (getAllocationRoleTypeId() != null) {
            _hashCode += getAllocationRoleTypeId().hashCode();
        }
        if (getAllocationOrgCode() != null) {
            _hashCode += getAllocationOrgCode().hashCode();
        }
        if (getAllocationOrgComments() != null) {
            _hashCode += getAllocationOrgComments().hashCode();
        }
        if (getAllocationOrgDescription() != null) {
            _hashCode += getAllocationOrgDescription().hashCode();
        }
        if (getAllocationFromDate() != null) {
            _hashCode += getAllocationFromDate().hashCode();
        }
        if (getAllocationThruDate() != null) {
            _hashCode += getAllocationThruDate().hashCode();
        }
        _hashCode += getAllocationValue();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AllocationInterfaceExt.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.standardImport.mapsengineering.com/", "allocationInterfaceExt"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataSource");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataSource"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("refDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "refDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("personCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "personCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("personRoleTypeId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "personRoleTypeId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allocationRoleTypeId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "allocationRoleTypeId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allocationOrgCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "allocationOrgCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allocationOrgComments");
        elemField.setXmlName(new javax.xml.namespace.QName("", "allocationOrgComments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allocationOrgDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("", "allocationOrgDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allocationFromDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "allocationFromDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allocationThruDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "allocationThruDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allocationValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "allocationValue"));
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
