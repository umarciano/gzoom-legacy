import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;
import javolution.util.FastList;

def result = ServiceUtil.returnSuccess();

def dispatcher = dctx.getDispatcher();
def delegator = dctx.getDelegator();
def security = dctx.getSecurity();


// check permission
def userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

def appTitle = (String) context.get("appTitle");
if(UtilValidate.isEmpty(appTitle)) {
	return result;
}

def defaultOrganizationPartyId = "Company";
def applTitleNoteName = "APPLICATION_TITLE";


/*
 * cerco se esiste gia' la nota APPLICATION_TITLE del Party Company,
 * che e' la nota dell'application title. Se non esiste la creo,
 * altrimenti non faccio nulla
 */
def condList = FastList.newInstance();
condList.add(EntityCondition.makeCondition("targetPartyId", defaultOrganizationPartyId));
condList.add(EntityCondition.makeCondition("noteName", applTitleNoteName));
		
def partyNoteList = delegator.findList("PartyNoteView", EntityCondition.makeCondition(condList), null, 
		null, null, false);
def defaultPartyNote = EntityUtil.getFirst(partyNoteList);

if(UtilValidate.isEmpty(defaultPartyNote)) {
    def parametersMap = [:];
    parametersMap.targetPartyId = defaultOrganizationPartyId;
    parametersMap.noteName = applTitleNoteName;
    parametersMap.noteInfo = appTitle;
    parametersMap.noteDateTime = UtilDateTime.nowTimestamp();
    parametersMap._AUTOMATIC_PK_ = "Y";
    parametersMap.operation = "CREATE";
        
    dispatcher.runSync("crudServiceDefaultOrchestration_PartyNoteView", ["parameters": parametersMap, "userLogin": context.userLogin, "operation": "CREATE",
                                   "entityName": "PartyNoteView", "locale" : locale]);
}

return result;

