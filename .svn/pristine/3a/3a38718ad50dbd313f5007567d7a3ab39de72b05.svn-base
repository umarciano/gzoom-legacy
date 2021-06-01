import org.ofbiz.base.util.*;
import com.mapsengineering.workeffortext.widgets.*;

//
//Costruzione immagine tachimetro
//

def fieldsMap = context.fieldsMap;

if (UtilValidate.isNotEmpty(fieldsMap)) {
	def renderImage = context.renderImage;
	if (UtilValidate.isEmpty(renderImage)) {
		renderImage = true;
	}
	//
	//Url per caricare immagini
	//
	def srvRootUrl = request.getAttribute("_SERVER_ROOT_URL_");
	
	//
	//Path immagini locale al modulo
	//
	def contextImagesPath = "images/tmp";
	
	// Controllo se ho delle fasce valori o se devo generare il valore target
	def typeBalanceScoreTarId = fieldsMap.get("typeBalanceScoreTarId") ? fieldsMap.get("typeBalanceScoreTarId") : "";
	def budgetFlag = typeBalanceScoreTarId.equalsIgnoreCase(fieldsMap.get("glFiscalTypeIdBudget"));
	
	
	def targetAmount = fieldsMap.get("targetAmount"); //Se l'obiettivo non é specificato é 100
	def value = UtilValidate.isNotEmpty(fieldsMap.get("actualAmount")) ? fieldsMap.get("actualAmount") : -1;
	src = "";
	if (value != -1) {
		if (budgetFlag) {
			if (renderImage) {
				// Tachimetro con target, costruisco io le fasce,
				// condizionando al valore raggiunto / target
				// e l'indicatore del tachimetro va sul valore target non sul actual
				String differentialColor;
				def greenUpperBound;
				def diffLowerBound;
				def diffUpperBound;
				def yellowLowerBound;
				if (value < targetAmount) {
					differentialColor = "RED";
					greenUpperBound = value;
					diffLowerBound = value;
					diffUpperBound = targetAmount;
					yellowLowerBound = targetAmount;
				} else {
					differentialColor = "BLUE";
					greenUpperBound = targetAmount;
					diffLowerBound = targetAmount;
					diffUpperBound = value;
					yellowLowerBound = value;
				}
				
				def ranges = [];
				ranges.add(new Range("GREEN", 0, greenUpperBound));
				ranges.add(new Range(differentialColor, diffLowerBound, diffUpperBound));
				ranges.add(new Range("YELLOW", yellowLowerBound, 100));
				
				src = ValutationWidgets.buildAndSaveMeter(request, contextImagesPath, value, ranges, 60, 60);
			}
			
			//Inserisco in mappa i valori
			fieldsMap.put("imageValue", value);
			fieldsMap.put("budgetValue", targetAmount); //Obiettivo
			
		} else {
			if (renderImage) {
				// Tachimetro con fasce valori
				//GN-378	
				String uomRangeScoreId = UtilValidate.isEmpty(fieldsMap.get("weUomRangeScoreId")) ? UtilValidate.isEmpty(fieldsMap.get("uomRangeScoreId")) ? "" : fieldsMap.get("uomRangeScoreId") : fieldsMap.get("weUomRangeScoreId");
				
				src = ValutationWidgets.buildAndSaveMeter(delegator, request, contextImagesPath, value, uomRangeScoreId, 60, 60);
			}
			//Inserisco in mappa i valori
			fieldsMap.put("imageValue", value);
//			fieldsMap.put("budgetValue", 100); //In questi caso l'obiettivo é 100
		}
	}
	
	//
	//Aggiungo alla mappa dei campi i campi con il sorgente dell'immagine ed il valore
	//
	fieldsMap.put("imageSrc", src);
	
	//
	//Per birt, a differenza del browser al quale basta il percorso relativo, devo ricostruire il percorso assoluto
	//
	path = "";
	if (UtilValidate.isNotEmpty(src)) {
		path = request.getAttribute("_CONTEXT_ROOT_") + contextImagesPath + src.substring(src.lastIndexOf('/'));
	}
	fieldsMap.put("birtImageSrc", path);
	
	//
	//Reperisco contentid per alert
	//
	alertContentId = null;
	if ("Y".equalsIgnoreCase(fieldsMap.get("hasAlert"))) {
		alertContentId = ValutationWidgets.getHasAlertContentId(delegator,  "ALERT", "Y");
	}
	fieldsMap.put("alertContentId", alertContentId);
	
	
	//
	//Inserisco se richiesta l'immagine per segnale di pericolo
	//
	hasScoreAlertSrc = null;
	if ("Y".equalsIgnoreCase(fieldsMap.get("hasScoreAlert"))) {
		hasScoreAlertSrc = " /workeffortext/images/messagebox_warning.gif";
	}
	fieldsMap.put("hasScoreAlertSrc", hasScoreAlertSrc);
	
	//
	//Per birt, a differenza del browser al quale basta il percorso relativo, devo dare anche il server ed il protocollo
	//
	alertSrc = srvRootUrl + "/content/control/stream?contentId=" + alertContentId;
	fieldsMap.put("alertSrc", alertSrc);
}

return fieldsMap;