import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelRelation;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.base.util.*;

/**
*  Devo caricare la keyMap
*/

keyMap = [:];

keyMap.put("partyIdTo", valueMap.get("partyIdTo"));

context.keyMap = keyMap;

