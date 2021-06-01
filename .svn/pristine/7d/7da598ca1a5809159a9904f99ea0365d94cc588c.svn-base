
import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

//Qui di seguito setto i campi per inserimento
context.put("showFileFolder", false);

//Se insertMode = Y allora sono in insertMode e arrivo dall'albero
//Se isInsertMode = Y allora, siccome é settato dallo screen, sono in insertMode e arrivo dal cambio form di management fatto sulla combo    	
/*if ("Y".equals(parameters.insertMode)) {

	if (UtilValidate.isEmpty(parameters.childFolderFile)) {
		//Se é vuoto arrivo dalla root
		parameters.put("childFolderFile", "FOLDER");
		context.put("parentClassId", null);
	} else {
		if ("FILE".equals(parameters.childFolderFile)) {
			context.put("parentGlAccountId", parameters.childId);
			context.put("glAccountClassId", null);
		}
		if ("FOLDER".equals(parameters.childFolderFile)) {
			context.put("parentClassId", parameters.childId);
			//Segnalo di mostrare la drop list di scelta FILE/FOLDER
			context.put("showFileFolder", true);
		}
	}
	
} else {
	
	if ("Y".equals(parameters.isInsertMode)) {
		
		//Segnalo di mostrare la drop list di scelta FILE/FOLDER
		context.put("showFileFolder", true);

		//Qui risetto insertMode per essere sicuro che il populateManagement finale NON carichi i dati
		parameters.put("insertMode", "Y");
		
		if ("FILE".equals(parameters.childFolderFile)) {
			context.put("glAccountClassId", parameters.childId);
			context.put("parentGlAccountId", null);
		}
		if ("FOLDER".equals(parameters.childFolderFile)) {
			context.put("parentClassId", parameters.childId);
		}
		
	} 
	
}*/

// rimosso isInsertMode perch� nel caso di creazione di file ho bisogno di avere insertMode = Y, altrimenti non nasconde i subfolder
// quindi 
if ("Y".equals(parameters.insertMode)) {
    if (UtilValidate.isEmpty(parameters.childFolderFile)) {
		//Se é vuoto arrivo dalla root
//		Debug.log(" **********creazione non so  ******************************** parameters.insertMode " + parameters.insertMode);
//    	Debug.log(" **********creazione non so  ******************************** parameters.entityName " + parameters.entityName);
    	parameters.put("childFolderFile", "FOLDER");
		context.put("parentClassId", null);
	}
    else if("FILE".equals(parameters.childFolderFile) && "Y".equals(parameters.isFileFile)) {

//    	Debug.log(" **********creazioje file figlio di file ******************************** parameters.insertMode " + parameters.insertMode);
//    	Debug.log(" **********creazioje file figlio di file ******************************** parameters.entityName " + parameters.entityName);
    	context.put("parentGlAccountId", parameters.childId);
    	context.put("glAccountClassId", null);
    }
    else if ("FILE".equals(parameters.childFolderFile) && !"Y".equals(parameters.isFileFile)) {
//		Debug.log(" **********creazioje file figlio di folder ******************************** parameters.insertMode " + parameters.insertMode);
//		Debug.log(" **********creazioje file figlio di folder ******************************** parameters.entityName " + parameters.entityName);
		context.put("glAccountClassId", parameters.childId);
		context.put("parentGlAccountId", null);
		context.put("showFileFolder", true);
	}
    else if ("FOLDER".equals(parameters.childFolderFile)) {
//		Debug.log(" **********creazioje folder figlio di folder ******************************** parameters.insertMode " + parameters.insertMode);
//		Debug.log(" **********creazioje folder figlio di folder ******************************** parameters.entityName " + parameters.entityName);
		context.put("parentClassId", parameters.childId);
		//Segnalo di mostrare la drop list di scelta FILE/FOLDER
		context.put("showFileFolder", true);
	}

}
return res;