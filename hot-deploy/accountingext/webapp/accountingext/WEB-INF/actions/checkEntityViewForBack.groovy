import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

if ("GlAccountClassView".equals(parameters.entityName)) {
	def fromGlAccountTree = parameters.fromGlAccountTree;
	if (UtilValidate.isEmpty(fromGlAccountTree)) {
		fromGlAccountTree = parameters._fromGlAccountTree;
		parameters.fromGlAccountTree = fromGlAccountTree;
	}
	def childFolderFile = parameters.childFolderFile;
	if (UtilValidate.isEmpty(childFolderFile)) {
		childFolderFile = parameters._childFolderFile;
		parameters.childFolderFile = childFolderFile;
	}
	
	if ("Y".equals(fromGlAccountTree)) {
		if ("FILE".equals(childFolderFile)) {
			context.entityName = "GlAccountView";
		}
		
		def selectedId = parameters.selectedId;
		if (UtilValidate.isNotEmpty(selectedId)) {
			def keyValue = selectedId.substring(selectedId.lastIndexOf('_')+1);
			modelEntity = delegator.getModelEntity(context.entityName);
			modelEntity.getPkFieldNames().each { pkFieldName ->
				parameters[pkFieldName] = keyValue;
			}
		}
	}
}