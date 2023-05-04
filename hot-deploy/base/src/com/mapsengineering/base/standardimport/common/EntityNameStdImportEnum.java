package com.mapsengineering.base.standardimport.common;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * Enumeration 
 *
 */
public enum EntityNameStdImportEnum {
	WE_ROOT_INTERFACE("WeRootInterface", UtilMisc.toList(E.sourceReferenceRootId.name())),
	WE_INTERFACE("WeInterface", UtilMisc.toList(E.sourceReferenceRootId.name(), E.sourceReferenceId.name(), E.workEffortName.name(), E.workEffortTypeId.name())),
	WE_ASSOC_INTERFACE("WeAssocInterface", null),
	WE_NOTE_INTERFACE("WeNoteInterface", null),
	WE_PARTY_INTERFACE("WePartyInterface", null),
    WE_MEASURE_INTERFACE("WeMeasureInterface", UtilMisc.toList(E.sourceReferenceRootId.name(), E.sourceReferenceId.name(), E.workEffortName.name(), E.workEffortTypeId.name(), E.accountCode.name(), E.accountName.name(), E.uomDescr.name(), E.uomDescrLang.name(), E.workEffortMeasureCode.name())),
    ACCTG_TRANS_INTERFACE("AcctgTransInterface", UtilMisc.toList(E.refDate.name(), E.glAccountCode.name(), E.glFiscalTypeId.name(), E.uorgCode.name(), E.productCode.name(), E.workEffortCode.name(), E.partyCode.name(), E.voucherRef.name(), E.uomDescr.name())),
    GL_ACCOUNT_INTERFACE("GlAccountInterface", UtilMisc.toList(E.refDate.name(), E.accountCode.name(), E.accountTypeId.name(), E.accountName.name())),
    ORGANIZATION_INTERFACE("OrganizationInterface", null),
    ORG_RESP_INTERFACE("OrgRespInterface", null),
    PERSON_INTERFACE("PersonInterface", null),
    PERS_RESP_INTERFACE("PersRespInterface", null),
    ALLOCATION_INTERFACE("AllocationInterface", null);
    
    private final String interfaceEntityName;
	
	private final List<String> logicalPrimaryKey;
	
	/**
	 * Constructor
	 * @param roleTypeId
	 * @param interfaceFieldName
	 * @param partyRelationshipTypeId
	 * @param description
	 * @param code
	 */
	EntityNameStdImportEnum(String interfaceEntityName, List<String> logicalPrimaryKey) {
		this.interfaceEntityName = interfaceEntityName;
		this.logicalPrimaryKey = logicalPrimaryKey;
	}

	private static final Map<String, EntityNameStdImportEnum> STRING_2_TYPE = new Hashtable<String, EntityNameStdImportEnum>();

	public static Map<String, Object> getLogicalPrimaryKey(String entityName, GenericValue gv) throws GenericEntityException {
	    EntityNameStdImportEnum entity = EntityNameStdImportEnum.parse(entityName);
        Map<String, Object> key = null;
        switch (entity) {
        case WE_ROOT_INTERFACE:
        case WE_INTERFACE:
        case ACCTG_TRANS_INTERFACE:
        case GL_ACCOUNT_INTERFACE:
        case WE_MEASURE_INTERFACE:
            key = gv.getFields(entity.getLogicalPrimaryKey());
            break;
        default:
            key = gv.getPrimaryKey();
            break;
        }
        return key;
    }
	
	static {
        for (EntityNameStdImportEnum ss : values()) {
        	STRING_2_TYPE.put(ss.interfaceEntityName, ss);
        }
	}
	
	/**
	 * Return Map
	 * @param code
	 * @return
	 */
	public static EntityNameStdImportEnum parse(String interfaceEntityName) {
		return STRING_2_TYPE.get(interfaceEntityName);
	}
	
	public String getInterfaceEntityName() {
        return interfaceEntityName;
    }

    public List<String> getLogicalPrimaryKey() {
        return logicalPrimaryKey;
    }

    
}
