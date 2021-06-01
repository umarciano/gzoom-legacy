
/**
 * Maps ValidationManager.  
 * Sfrutta la classe Validation già presente in ofbiz senza modificarla
 * I Validatori custom (array CustomValidationArray) sono definiti in header.ftl per la localizzazione 
 */

var ValidationManager = Class.create({});

ValidationManager.validateForm = function(formToCheck) {
	formToCheck = $(formToCheck);
	if (formToCheck) {
		if (typeof(Validation)!="undefined") {
			//per la definizione di CustomValidationArray vedi: header.ftl
			Validation.addAllThese(CustomValidationArray);
			//Valido la form
			var v = new Validation(formToCheck, {focusOnError: false, onSubmit: false});
			return v.validate();
		}
	}
}
