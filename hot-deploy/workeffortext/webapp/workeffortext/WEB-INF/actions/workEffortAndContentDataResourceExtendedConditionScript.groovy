import org.ofbiz.base.util.*;

screenNameListIndex = "";

//introdotti 2 parametri:
// readOnlyContent = Y, usato nelle "Interrogazioni Schede Obiettivo" 
// cruContent = Y usato nelle "Analisi Risultati Obiettivi"
    
if ("Y".equalsIgnoreCase(parameters.cruContent)) {
	screenNameListIndex = "1";
}
if ("Y".equalsIgnoreCase(parameters.readOnlyContent)) {
	screenNameListIndex = "2";
}

return screenNameListIndex;