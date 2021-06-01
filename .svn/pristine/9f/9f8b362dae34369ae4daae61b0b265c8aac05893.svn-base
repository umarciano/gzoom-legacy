import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.*;
import com.mapsengineering.base.util.*;

if(UtilValidate.isEmpty(context.folderIndex) && "Y".equals(parameters.detail)) {
	context.menuBarNameList = context.arrayDetailMenuName;
	context.menuBarLocationList = context.arrayDetailMenuLocation;
	context.contextManagementScreenNameList = context.arrayDetailContextScreenName;
    context.contextManagementScreenLocationList = context.arrayDetailContextScreenLocation;
    context.contextManagementFormNameList = context.arrayDetailContextFormName;
    context.contextManagementFormLocationList = context.arrayDetailContextFormLocation;
    context.arrayManagementFormScreenName = context.arrayDetailScreenName;
    context.arrayManagementFormScreenLocation = context.arrayDetailScreenLocation;
	
	context.actionMenuName = context.arrayDetailMenuName[0];
	context.actionMenuLocation = context.arrayDetailMenuLocation[0];
}