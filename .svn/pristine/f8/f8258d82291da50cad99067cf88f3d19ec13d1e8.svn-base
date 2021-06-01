import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelRelation;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.base.util.*;

/**
*  Devo caricare la keyMap
*/

keyMap = [:];


if(UtilValidate.isNotEmpty(valueMap.get("glAccountClassId"))){
	//keyMap.put("glAccountClassId", "class_" + valueMap.get("glAccountClassId"));
	keyMap.put("glAccountClassId", valueMap.get("glAccountClassId"));
}else{
	keyMap.put("glAccountId", valueMap.get("glAccountId"));
}

context.keyMap = keyMap;

