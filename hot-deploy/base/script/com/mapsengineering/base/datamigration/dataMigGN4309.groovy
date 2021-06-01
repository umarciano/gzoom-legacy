import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

//check permission
userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

def groupName = (String) context.get("groupName");
if(UtilValidate.isEmpty(groupName)) {
	groupName = DatabaseUtil.DEFAULT_GROUP_NAME;
}

def entityList = ["OrganizationInterfaceHist", "OrgRespInterfaceHist", "PersonInterfaceHist", "PersRespInterfaceHist", "AcctgTransInterfaceHist", "GlAccountInterfaceHist", "WeRootInterfaceHist", "WeInterfaceHist", "WeAssocInterfaceHist", "WeMeasureInterfaceHist", "WePartyInterfaceHist", "WeNoteInterfaceHist"];
entityList.each{ entityItem ->
    def orderBy = ["-id"];
    def gvList = delegator.findList(entityItem, null, null, orderBy, null, false);
    def gvItem = EntityUtil.getFirst(gvList);
    if (UtilValidate.isNotEmpty(gvItem) && UtilValidate.isNotEmpty(gvItem.id)) {
    	def id = gvItem.id;
    	if (id.startsWith("GZ")) {
    		id = id.substring(2, id.length());
    	}
    	def l = new Long(id);
    	def sequenceValueItem = delegator.findOne("SequenceValueItem", ["seqName": entityItem], false);
    	if (UtilValidate.isEmpty(sequenceValueItem)) {
    		def value = l + 1;
    		def sequenceValueItemNew = delegator.makeValue("SequenceValueItem");
    		sequenceValueItemNew.seqName = entityItem;
    		sequenceValueItemNew.seqId = value;
    		delegator.create(sequenceValueItemNew);
    	} else {
    		if (UtilValidate.isNotEmpty(sequenceValueItem.seqId)) {
    			def value = l + 1;
    			if (sequenceValueItem.seqId < value) {
    				sequenceValueItem.seqId = value;
    				delegator.store(sequenceValueItem);
    			}
    		}
    	}
    }
}


return result;

