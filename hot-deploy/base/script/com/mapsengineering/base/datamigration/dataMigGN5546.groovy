import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import javolution.util.FastList;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();


// check permission
userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

// delete from PortletAttribute where portalPageId = 'GP_WE_PORTAL' and portalPortletId = 'Communications'
List<EntityCondition> conditionsAtt = FastList.newInstance();
conditionsAtt.add(EntityCondition.makeCondition("portalPageId", "GP_WE_PORTAL"));
conditionsAtt.add(EntityCondition.makeCondition("portalPortletId", "Communications"));

portletAttributeList = delegator.findList("PortletAttribute", EntityCondition.makeCondition(conditionsAtt), null, null, null, false);

if(UtilValidate.isNotEmpty(portletAttributeList)) {
	Debug.log("Elimino portletAttributeList per Communications...");
	delegator.removeAll(portletAttributeList);
}

// delete from PortalPagePortlet where portalPageId = 'GP_WE_PORTAL' and portalPortletId = 'Communications'
conditionsPPP = FastList.newInstance();
conditionsPPP.add(EntityCondition.makeCondition("portalPageId", "GP_WE_PORTAL"));
conditionsPPP.add(EntityCondition.makeCondition("portalPortletId", "Communications"));

portalPagePortletList = delegator.findList("PortalPagePortlet", EntityCondition.makeCondition(conditionsPPP), null, null, null, false);

if(UtilValidate.isNotEmpty(portalPagePortletList)) {
    Debug.log("Elimino portalPagePortlet Communications...");
    delegator.removeAll(portalPagePortletList);
}


conditionsAtt2 = FastList.newInstance();
conditionsAtt2.add(EntityCondition.makeCondition("portalPortletId", "GP_WE_2"));
conditionsAtt2.add(EntityCondition.makeCondition("attrName", "portletAssoc"));

portletAttribute2List = delegator.findList("PortletAttribute", EntityCondition.makeCondition(conditionsAtt2), null, null, null, false);

if(UtilValidate.isNotEmpty(portletAttribute2List)) {
 Debug.log("Elimino portletAttributeList per Comm...");
 delegator.removeAll(portletAttribute2List);
}

conditionsAtt2 = FastList.newInstance();
conditionsAtt2.add(EntityCondition.makeCondition("portalPortletId", "GP_WE_2_A"));
conditionsAtt2.add(EntityCondition.makeCondition("attrName", "portletAssoc"));

portletAttribute2List = delegator.findList("PortletAttribute", EntityCondition.makeCondition(conditionsAtt2), null, null, null, false);

if(UtilValidate.isNotEmpty(portletAttribute2List)) {
 Debug.log("Elimino portletAttributeList per Comm...");
 delegator.removeAll(portletAttribute2List);
}
GenericValue portletAttribute = null;
// PortalPageColumn portalPageId="GP_WE_PORTAL
// <PortletAttribute portalPageId="GP_WE_PORTAL_13" portalPortletId="GP_WE_1"  portletSeqId="00001" attrName="noDataLoaded" attrValue="Y"/>
List<EntityCondition> conditionsAtt3 = FastList.newInstance();
conditionsAtt3.add(EntityCondition.makeCondition("portalPageId", EntityOperator.LIKE, "GP_WE_PORTAL%"));
portletAttribute3List = delegator.findList("PortalPageColumn", EntityCondition.makeCondition(conditionsAtt3), null, null, null, false);
if(UtilValidate.isNotEmpty(portletAttribute3List)) {
    portletAttribute3List.each{ portletAttributeItem ->  
        if ("GP_WE_PORTAL_13".equals(portletAttributeItem.portalPageId) || "GP_WE_PORTAL_2".equals(portletAttributeItem.portalPageId))
            // <PortletAttribute portalPageId="GP_WE_PORTAL_2" portalPortletId="GP_WE_1"  portletSeqId="00001" attrName="portletAssoc" attrValue="GP_WE_2,00003"/>
            portletAttribute = delegator.findOne("PortletAttribute", ["portalPageId": portletAttributeItem.portalPageId, "portalPortletId": "GP_WE_1", "portletSeqId": "00001", "attrName": "portletAssoc"], false);
            if(UtilValidate.isNotEmpty(portletAttribute)) {
                portletAttribute.attrValue = "GP_WE_2,00003";
                Debug.log("Modifico PortletAttribute assoc...");
                delegator.store(portletAttribute);
            }
        else {
            portletAttribute = delegator.findOne("PortletAttribute", ["portalPageId": portletAttributeItem.portalPageId, "portalPortletId": "GP_WE_1", "portletSeqId": "00001", "attrName": "portletAssoc"], false);
            if(UtilValidate.isNotEmpty(portletAttribute)) {
                portletAttribute.attrValue = "GP_WE_2_A,00003";
                Debug.log("Modifico PortletAttribute assoc...");
                delegator.store(portletAttribute);
            }
        }
    
        portletAttribute = delegator.findOne("PortletAttribute", ["portalPageId": portletAttributeItem.portalPageId, "portalPortletId": "GP_WE_1", "portletSeqId": "00001", "attrName": "noDataLoaded"], false);
        Debug.log("portletAttribute " + portletAttribute);
        if(UtilValidate.isEmpty(portletAttribute)) {
            // per tutti i portali
            GenericValue portletAttribute3 = delegator.makeValue("PortletAttribute");
            portletAttribute3.put("portalPageId", portletAttributeItem.portalPageId);
            portletAttribute3.put("portalPortletId", "GP_WE_1");
            portletAttribute3.put("portletSeqId", "00001");  
            portletAttribute3.put("attrName", "noDataLoaded");
            portletAttribute3.put("attrValue", "Y");  
            portletAttribute3.create();
        }
    }

}
return result;

