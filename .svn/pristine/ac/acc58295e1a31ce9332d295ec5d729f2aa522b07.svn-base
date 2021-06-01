import org.ofbiz.base.location.FlexibleLocation
import org.ofbiz.base.util.*

def scriptUrl = context.scriptUrl;
def componentName = context.componentName;
def entityName = context.entityName;
def screenNameListIndex = context.screenNameListIndex;
def inheritScreenList = context.inheritScreenList;
def arrayEntityName = context.arrayEntityName;

def arrayContextValueCheckEntityNameDisabledList = context.arrayContextValueCheckEntityNameDisabledList;
def excludedContentScript = context.excludedContentScript;
def rootFolder = context.rootFolder;
// estrazione ricerca folderContentIds

def arrayEntityNameList = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".entityName");
if (!arrayEntityNameList) {
	if (inheritScreenList)
		arrayEntityNameList = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.entityName");
}

if(UtilValidate.isNotEmpty(arrayEntityNameList)) {
	arrayEntityName.addAll(StringUtil.toList(arrayEntityNameList));
	arrayEntityNameSize = arrayEntityName.size();
}

// Debug.log("**************** setManagementScreenList.groovy -> Prima dell'elaborazione elaborazione: arrayEntityName = " + arrayEntityName);


if (UtilValidate.isNotEmpty(arrayContextValueCheckEntityNameDisabledList)) {
	def arrayContextValueCheckEntityNameDisabled = StringUtil.toList(arrayContextValueCheckEntityNameDisabledList);
	context.arrayContextValueCheckEntityNameDisabled = arrayContextValueCheckEntityNameDisabled;
	if (UtilValidate.isNotEmpty(excludedContentScript)) {
		if (UtilValidate.isNotEmpty(rootFolder)) {
			// perche' puo succedere che un folder e' gia' stato esculso
			// mediante il file di properies
			Debug.log("**************** getFolderContentIds.groovy -> excludedContentScript = " + excludedContentScript);
			GroovyUtil.runScriptAtLocation(excludedContentScript, context);
			
			def localArrayEntityName = [];
			def index = 0;
			def currentIndex = 0;
			// ToDo 3820
			def folderContentIds = [];
			arrayContextValueCheckEntityNameDisabled.each { checkEntityNameValue ->
				// Debug.log("**************** setManagementScreenList.groovy -> Dopo elaborazione: checkEntityNameValue = " + checkEntityNameValue);
				if (UtilValidate.isEmpty(context[checkEntityNameValue])) {
					localArrayEntityName.add(arrayEntityName[index]);
	
					context[checkEntityNameValue+"_index"] = currentIndex
	
					currentIndex++;
	
					// Debug.log("**************** getFolderContentIds.groovy -> Dopo elaborazione: currentIndex = " + currentIndex);
					
					folderContentIds.add(checkEntityNameValue);
				}
				index++;
			}
			// Debug.log("**************** getFolderContentIds.groovy -> Dopo elaborazione: localArrayEntityName = " + localArrayEntityName);
	
			context.arrayEntityName = localArrayEntityName;
			context.arrayEntityNameSize = localArrayEntityName.size();
	
			context.folderContentIds = folderContentIds;
	
			// Debug.log("**************** setManagementScreenList.groovy -> Dopo elaborazione: arrayEntityName = " + context.arrayEntityName);
		}
	}
}
