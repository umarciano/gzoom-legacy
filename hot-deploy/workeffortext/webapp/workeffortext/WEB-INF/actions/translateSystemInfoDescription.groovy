import org.ofbiz.base.util.*;
import org.ofbiz.base.util.string.*;

if (UtilValidate.isNotEmpty(context.systemInfoStatus)) {
    systemInfoStatus.each { value ->
        type = value.type;
        noteInfo = value.noteInfo;
		typePortlet = value.moreInfoPortletId;
		
        if (UtilValidate.isNotEmpty(noteInfo) && noteInfo.lastIndexOf(":") != -1) {
            countValue = noteInfo.substring(noteInfo.lastIndexOf(":")+1);
            if (UtilValidate.isNotEmpty(countValue)) {
                countValue = countValue.trim();
                if ("COMMUNICATIONS".equals(type)) {
                    noteInfo = uiLabelMap.SystemInfoStatusCommunication + " " + countValue;
                } else if ("TASK".equals(type)) {
                    noteInfo = uiLabelMap.SystemInfoStatusTasks + " " + countValue;
                } else if("mytasks".equals(typePortlet)){
                	noteInfo = uiLabelMap.SystemInfoStatusTasks + " " + countValue;
                }
            }
        }

        value.noteInfo = noteInfo;
    }
}


/*Utilizzato er la conversione nelle info note*/
if (UtilValidate.isNotEmpty(context.systemInfoNotes)) {
    systemInfoNotes.each { value ->
        type = value.type;
        noteInfo = value.noteInfo;
		typePortlet = value.moreInfoPortletId;
		
        if (UtilValidate.isNotEmpty(noteInfo) && noteInfo.lastIndexOf(":") != -1) {
            countValue = noteInfo.substring(noteInfo.lastIndexOf(":")+1);
            if (UtilValidate.isNotEmpty(countValue)) {
                if ("MyCommunications".equals(typePortlet)) {
                    noteInfo = uiLabelMap.SystemInfoStatusCommunication + " " + countValue;
                }              
                
            }
        } else if(UtilValidate.isNotEmpty(noteInfo) && noteInfo.lastIndexOf("[") != -1){
    		 parameters.workEffortName = noteInfo.substring(noteInfo.indexOf("'")+1,noteInfo.lastIndexOf("'"));
    		 
    		 if("ViewProjectTask".equals(typePortlet)){
            	noteInfo = FlexibleStringExpander.expandString(uiLabelMap.SystemInfoStatusTasksCod, context);
            } 
        }

        value.noteInfo = noteInfo;
    }
}