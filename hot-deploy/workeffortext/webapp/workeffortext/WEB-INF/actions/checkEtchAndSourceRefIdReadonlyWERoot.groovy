import org.ofbiz.base.util.*;

def canModifyEtch = false;
def canModifySourceRef = false;


/** GN-448 Per le specializzazioni:

Se insertMode = 'Y' sempre editabili
Altrimenti
    se permessi %MGR_ADMIN sempre editabili
    altrimenti codeLocked = N sempre editabili
*/
if (context.isInsertMode == true) {
    canModifyEtch = true;
    canModifySourceRef = true;
} else if(checkModifyPermissions()) {
    canModifyEtch = true;
    canModifySourceRef = true;
} else{
    def codeLocked = getCodeLocked();
    if ("Y".equals(codeLocked)) {
    	canModifyEtch = false;
    	canModifySourceRef = false;
    } else if("ONLY_CODE".equals(codeLocked)) {
    	canModifyEtch = true;
    	canModifySourceRef = false;
    } else {
    	canModifyEtch = true;
        canModifySourceRef = true;
    }
}

context.isEtchReadOnly = (canModifyEtch == false);
context.isSourceRefReadOnly = (canModifySourceRef == false);

def checkModifyPermissions() {
    return security.hasPermission(context.canModifyEtchPermission, userLogin);
}


def getCodeLocked() {
    def codeLocked = '';

    def folderIndex = UtilValidate.isNotEmpty(context.folderIndex) ? context.folderIndex : 0;
    def folderContentIds = context.folderContentIds;
    def contentId = ''; 
    if (folderContentIds != null && folderContentIds.size() > folderIndex) {
        contentId = folderContentIds[folderIndex];
    }
    
    def childView = delegator.findOne("WorkEffortTypeStatusCntChildView", ["workEffortId" : parameters.workEffortId, "contentId" : contentId], 
            false);
    if (UtilValidate.isNotEmpty(childView)) {
        // Debug.log("childView " + childView);
        codeLocked = childView.codeLocked;
    }
    if (UtilValidate.isEmpty(codeLocked)) {
        def parentView = delegator.findOne("WorkEffortTypeStatusCntParentView", ["workEffortId" : parameters.workEffortId, "contentId" : contentId], 
                false);
        if (UtilValidate.isNotEmpty(parentView)) {
            // Debug.log("parentView " + parentView);
            codeLocked = parentView.codeLocked;
        }       
    }
    Debug.log("codeLocked " + codeLocked);
    return codeLocked;
}
