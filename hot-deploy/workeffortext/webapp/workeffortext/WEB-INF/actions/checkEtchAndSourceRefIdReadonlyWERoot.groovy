import org.ofbiz.base.util.*;

def canModifyEtch = false;


/** GN-448 Per le specializzazioni:

Se insertMode = 'Y' sempre editabili
Altrimenti
    se permessi %MGR_ADMIN sempre editabili
    altrimenti codeLocked != Y sempre editabili
*/
if (context.isInsertMode == true) {
    canModifyEtch = true;
} else if(checkModifyEtchPermissions()) {
    canModifyEtch = true;
} else{
    // Debug.log("checkModifyEtchFolderAbilitation() " + checkModifyEtchFolderAbilitation());
    canModifyEtch = checkModifyEtchFolderAbilitation();
}

// Debug.log("canModifyEtch " + canModifyEtch);
context.isEtchReadOnly = (canModifyEtch == false);

def checkModifyEtchPermissions() {
    // Debug.log("context.canModifyEtchPermission " + context.canModifyEtchPermission);
    // Debug.log("security.hasPermission " + security.hasPermission(context.canModifyEtchPermission, userLogin));
    return security.hasPermission(context.canModifyEtchPermission, userLogin);
}


def checkModifyEtchFolderAbilitation() {
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
    return (! "Y".equals(codeLocked));
}
