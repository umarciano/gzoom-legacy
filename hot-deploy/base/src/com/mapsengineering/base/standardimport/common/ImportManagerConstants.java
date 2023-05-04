package com.mapsengineering.base.standardimport.common;

/**
 * Constant ImportManager
 *
 */
public interface ImportManagerConstants {

    String SEP = "|";
    String PERSON_INTERFACE = "PersonInterface";
    String PERS_RESP_INTERFACE = "PersRespInterface";
    String ORGANIZATION_INTERFACE = "OrganizationInterface";
    String ORG_RESP_INTERFACE = "OrgRespInterface";
    String ACCTG_TRANS_INTERFACE = "AcctgTransInterface";
    String GL_ACCOUNT_INTERFACE = "GlAccountInterface";
    String WE_ROOT_INTERFACE = "WeRootInterface";
    String WE_INTERFACE = "WeInterface";
    String WE_ASSOC_INTERFACE = "WeAssocInterface";
    String WE_MEASURE_INTERFACE = "WeMeasureInterface";
    String WE_PARTY_INTERFACE = "WePartyInterface";
    String WE_NOTE_INTERFACE = "WeNoteInterface";
    String ALLOCATION_INTERFACE = "AllocationInterface";
    
    String PERSON_INTERFACE_EXT = "PersonInterfaceExt";
    String ORGANIZATION_INTERFACE_EXT = "OrganizationInterfaceExt";
    String ALLOCATION_INTERFACE_EXT = "AllocationInterfaceExt";
    
    String ENTITY_INTERFACE_HIST_SUFFIX = "Hist";

    String RECORD_FIELD_ID = "id";
    String RECORD_FIELD_SEQ = "seq";
    String RECORD_FIELD_STATUS = "stato";
    String RECORD_FIELD_ELAB_RESULT = "elabResult";
    String RECORD_ELAB_RESULT_OK = "Elaborazione ok";

    String RECORD_STATUS_OK = "OK";
    String RECORD_STATUS_KO = "KO";
    String RECORD_STATUS_LOCKED = "L";

    public static final String STR_IS_NOT_VALID = " is not valid";
    public static final String STR_IS_NULL = " is empty";
    
    /**
     * Prefisso riservato per valorizzare l'ID automatico durante l'importazione.
     * Sistemi esterni dovranno utilizzare prefissi diversi.
     */
    String RECORD_ID_PREFIX = "GZ";

    String SERVICE_TYPE = "STD_";
    String SERVICE_NAME = "standardImport";
    
    String SERVICE_TYPE_UPLOAD_FILE = "STD_UP_";
    String SERVICE_NAME_UPLOAD_FILE = "standardImportUploadFile";
    
    int MAX_ROWS = 10;
    
    
    /**
     * MessageCode
     *
     */
    enum MessageCode {
        /**
         * Bloccante
         */
        ERROR_BLOCKING,
        /**
         * Informativo
         */
        ERROR_NOTE,
        /**
         * Generico
         */
        INFO_GENERIC,
        /**
         * Warning
         */
        WARNING;
    }
    /**
     * Standard Import Table
     * @author dain
     *
     */
    enum StandardImportTableEnum {

        PersonInterface, OrganizationInterface, //
        AcctgTransInterface, GlAccountInterface, //
        WeRootInterface, WeInterface, WeAssocInterface, //
        WeMeasureInterface, WePartyInterface, WeNoteInterface, //
        AllocationInterface;
     }
}
