import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.*;
import com.mapsengineering.base.util.*;

if ("Y".equals(parameters.fromDelete) && UtilValidate.isNotEmpty(parameters.id)) {
	def modelRelation = null;
	if (UtilValidate.isEmpty(context.parentEntityName) && UtilValidate.isNotEmpty(parameters.parentEntityName)) {
		context.parentEntityName = parameters.parentEntityName;
	}
	
	if (UtilValidate.isNotEmpty(context.parentEntityName) && (UtilValidate.isEmpty(context.noParentEntityName) || "N".equals(context.noParentEntityName))) {
		modelEntity = delegator.getModelEntity(context.entityName);
	
		if (UtilValidate.isNotEmpty(modelEntity)) {
			relationTitle = UtilValidate.isNotEmpty(context.relationTitle) ? context.relationTitle : parameters.relationTitle;
			if (UtilValidate.isEmpty(relationTitle)) {
				relationTitle = "";
			}
			relationTitle += context.parentEntityName;
	
			modelRelation = modelEntity.getRelation(relationTitle);
			
			if (UtilValidate.isEmpty(modelRelation)) {
				for (m in modelEntity.getRelationsOneList()) {
					findRelation = false;
					for (modelKeyMap in m.getKeyMapsClone()) {
						parameterRelFieldName = modelKeyMap.getRelFieldName();
						parameterName = modelKeyMap.getFieldName();
	
						findRelation = UtilValidate.isNotEmpty(context[parameterName]) || UtilValidate.isNotEmpty(parameters[parameterName]) || UtilValidate.isNotEmpty(context[parameterRelFieldName]) || UtilValidate.isNotEmpty(parameters[parameterRelFieldName]);
						if (!findRelation) {
							break;
						}
					}
					if (findRelation) {
						modelRelation = m;
						break;
					}
				}
			}
		}
	}
	
	if (parameters.id.startsWith("["))
		parameters.id = parameters.id.substring(1);
	if (parameters.id.endsWith("]"))
		parameters.id = parameters.id.substring(0, parameters.id.length()-1);
	
	idMap = StringUtil.strToMap(parameters.id);
	if (UtilValidate.isNotEmpty(idMap)) {
		idMap.each { fieldName, fieldValue ->
			if (UtilValidate.isEmpty(modelRelation) || (UtilValidate.isEmpty(modelRelation.findKeyMap(fieldName)) && UtilValidate.isEmpty(modelRelation.findKeyMapByRelated(fieldName)))) {
				parameters.remove(fieldName);
			}
		}
	}
	
//	parameters.remove("id");
	
	context.cleanedIdAfterDelete = "Y";
}