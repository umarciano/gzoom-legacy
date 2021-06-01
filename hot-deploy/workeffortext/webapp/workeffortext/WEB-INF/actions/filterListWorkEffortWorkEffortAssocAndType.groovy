import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.*

parameters.workEffortParentCodeName = null;


workEffortWorkEffortAssocAndTypeList = context.get("workEffortWorkEffortAssocAndTypeList");
if (UtilValidate.isNotEmpty(workEffortWorkEffortAssocAndTypeList)){
  
    listFilterParentWorkEffort = [];
    //filtro la lista in base hai miei paranetri
    for(i= 0; i<workEffortWorkEffortAssocAndTypeList.size(); i++){
    	element = workEffortWorkEffortAssocAndTypeList[i];
    	
    	if(  ((element.assocTypeParentTypeId == 'HIE' && element.workEffortParentId == we.workEffortParentId) || element.assocWorkEffortAssocTypeId == element.workEffortAssocTypeId)
    		&& (element.assocThruDate == element.estimatedCompletionDate || element.assocThruDate == we.estimatedCompletionDate)  ){
    		if(UtilValidate.isEmpty(element.assocTypeParentTypeId)){
    			element.assocTypeParentTypeId = 'ZZZ';
    		}
    		listFilterParentWorkEffort.add(element);
    	}    
    }
    
    //OrderBy
    listFilterParentWorkEffort = EntityUtil.orderBy(listFilterParentWorkEffort, ['assocTypeParentTypeId', '-assocAssocWeight', '-workEffortId']);
    
    if(UtilValidate.isNotEmpty(listFilterParentWorkEffort)){
    	localeSecondarySet = context.localeSecondarySet;
    	parameters.workEffortParentCodeName = listFilterParentWorkEffort[0].sourceReferenceId +" - "+ ("Y".equals(localeSecondarySet) ? listFilterParentWorkEffort[0].workEffortNameLang : listFilterParentWorkEffort[0].workEffortName);
    }
}

