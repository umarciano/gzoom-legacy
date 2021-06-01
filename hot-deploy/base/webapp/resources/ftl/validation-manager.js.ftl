		CustomValidationArray = [
			/* campi obbligatori */
			['mandatory', '${uiLabelMap.BaseMessageMandatoryField}', function(v) { 
				return !Validation.get('IsEmpty').test(v);
			}]
		]        