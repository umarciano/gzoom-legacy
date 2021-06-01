package com.mapsengineering.base.test;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.standardimport.common.E;

/**
 * Test for update Organization
 *
 */
public class TestOrganizationStandardImportUpdate extends BaseOrganizationStandardImportUploadFileTestCase {
    public static final String MODULE = TestOrganizationStandardImportUpdate.class.getName();
    private static final String ORG19 = "ORG19";
    private static final String RESP24 = "RESP24";
    private static final String RESP25 = "RESP25";
    private static final String ORG_4097_1 = "ORG.4097.1";
    private static final String ORG_4097_2 = "ORG.4097.2";
    private static final String B020306A = "020306A";
    private static final String B027802I = "027802I";
    private static final String ROLE_EMPLOYEE = "EMPLOYEE";
    private static final String COMM_1 = "commento uno upd";
    private static final String COMM_2 = "commento due";
    
    /**
     * Different check: update and null value for emplPositionTypeId, comments, description, employmentAmount
     */
    public void testOrganizationInterfaceUpdate() {
        try {
            setContextAndRunOrganizationInterfaceUpdate("OrganizationInterfaceInsert.xls", 0, 1);

            setContextAndRunOrganizationInterfaceUpdate("OrganizationInterfaceUpdate.xls", 0, 1);

            assertResponsible(RESP24, 0);
            assertResponsible(RESP25, 1);
            
            setContextAndRunOrganizationInterfaceUpdate("OrganizationInterfaceInsertGN4097.xls", 0, 1);
            setContextAndRunOrganizationInterfaceUpdate("OrganizationInterfaceUpdateGN4097.xls", 0, 1);
            assertRelationships(B020306A, ORG_4097_1, COMM_1, ROLE_EMPLOYEE);
            assertRelationships(B027802I, ORG_4097_2, COMM_2, ROLE_EMPLOYEE);
            
        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }

    private List<GenericValue> assertResponsible(String partyCode, int size) throws GenericEntityException {
    	String partyId = getPartyId(partyCode);
    	EntityCondition condition = EntityCondition.makeCondition(
                EntityCondition.makeCondition(E.partyIdFrom.name(), getPartyId(ORG19)),
                EntityCondition.makeCondition(E.partyIdTo.name(), partyId),
                EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.ORG_RESPONSIBLE.name())
                );
    	List<GenericValue> listaTo = delegator.findList(E.PartyRelationship.name(), condition,null, null, null, false);
        assertEquals(size, listaTo.size()); 
    	
        return listaTo;
    }
    
    private void assertRelationships(String partyCode, String orgCode, String comments, String roleTypeId) throws GenericEntityException {
    	String partyId = getPartyId(partyCode);
    	String orgId = getPartyId(orgCode);
    	
    	List<EntityCondition> conditionsWithThruDate = new ArrayList<EntityCondition>();
    	conditionsWithThruDate.add(EntityCondition.makeCondition(E.partyIdFrom.name(), orgId));
    	conditionsWithThruDate.add(EntityCondition.makeCondition(E.partyIdTo.name(), partyId));
    	conditionsWithThruDate.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.ORG_RESPONSIBLE.name()));
    	conditionsWithThruDate.add(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.NOT_EQUAL, GenericEntity.NULL_FIELD));
    	List<GenericValue> lista1 = delegator.findList(E.PartyRelationship.name(), EntityCondition.makeCondition(conditionsWithThruDate), null, null, null, false);
    	assertEquals(1, lista1.size()); 
    	
    	List<EntityCondition> conditionsWithoutThruDate = new ArrayList<EntityCondition>();
    	conditionsWithoutThruDate.add(EntityCondition.makeCondition(E.partyIdFrom.name(), orgId));
    	conditionsWithoutThruDate.add(EntityCondition.makeCondition(E.partyIdTo.name(), partyId));
    	conditionsWithoutThruDate.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.ORG_RESPONSIBLE.name()));
    	conditionsWithoutThruDate.add(EntityCondition.makeCondition(E.thruDate.name(), GenericEntity.NULL_FIELD));
    	List<GenericValue> lista2 = delegator.findList(E.PartyRelationship.name(), EntityCondition.makeCondition(conditionsWithoutThruDate), null, null, null, false);
    	assertEquals(1, lista2.size()); 
    	GenericValue element = EntityUtil.getFirst(lista2);
    	assertEquals(comments, element.getString(E.comments.name()));
    	assertEquals(roleTypeId, element.getString(E.roleTypeIdTo.name()));
    }
}
