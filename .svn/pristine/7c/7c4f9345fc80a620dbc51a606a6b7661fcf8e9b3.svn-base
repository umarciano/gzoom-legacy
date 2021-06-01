GGeoLocalization = {
	inputs : null,
	dataAvailable : true,
	referenceForms : ["PAMF_001", "PE_PCM_001"],
	addressInputs : ["PostalAddressManagementForm_address1", "PartyContactMechCreateForm_address1"],
	postalCodeInputs : ["PostalAddressManagementForm_postalCode", "PartyContactMechCreateForm_postalCode"],
	cityInputs : ["PostalAddressManagementForm_city", "PartyContactMechCreateForm_city"],
	countryInputs : ["PostalAddressManagementForm_countryGeoId_edit_field", "PartyContactMechCreateForm_countryGeoId_edit_field"],
	index : null,
	lngInput : "longitude",
	latInput : "latitude",
	elevUomIdInput : "elevationUomId",
	elevUomIdValue : "LEN_m",
	dataSourceInput : "dataSourceId",
	dataSourceValue : "GEOPT_GOOGLE",
	toolbarInstance:  null,
	saveItem : null,
	oldCallback : Prototype.K,
	wizardNextButton : null,
	
	load : function(unused, withoutResponder) {
		for (var i = 0; i < GGeoLocalization.referenceForms.length; i++) {
			if (Object.isElement($(GGeoLocalization.referenceForms[i]))) {
				if (!$(GGeoLocalization.referenceForms[i]).hasClassName("hidden"))
					GGeoLocalization.index = i;
					break;
			}
		}
		
		if (GGeoLocalization.index !== null) {
			
			this.inputs = {"address": $(this.addressInputs[GGeoLocalization.index]),
						   "postalCode": $(this.postalCodeInputs[GGeoLocalization.index]),
						   "city": $(this.cityInputs[GGeoLocalization.index]),
						   "country": $(this.countryInputs[GGeoLocalization.index])};
						   
			if (GGeoLocalization.index === 1) {
				//WIZARD
				var wizardNextButton = $$("[name=\"wizardNext\"]")[0];
				if (Object.isElement(wizardNextButton)) {
					Event.observe(wizardNextButton, "click", GGeoLocalization.handler);
				}
			}
			
			if (GGeoLocalization.index === 0) {
				// PartyContactMech accesso da dashboard
				GGeoLocalization.toolbarInstance = Toolbar.getInstance();
				GGeoLocalization.saveItem = GGeoLocalization.toolbarInstance.getItem(".save");
			
				GGeoLocalization.oldCallback = GGeoLocalization.saveItem.eventCallbackFunction;
				GGeoLocalization.toolbarInstance._clearEvent(GGeoLocalization.saveItem);
			
				Event.observe(GGeoLocalization.saveItem, "click", GGeoLocalization.handler);
			}
		}
		else {
			if (!withoutResponder) {
	                UpdateAreaResponder.Responders.register( {
	                    onAfterLoad : function() {
	                    		GGeoLocalization.index = null;
	                            GGeoLocalization.load(null, true);
	                    }
	                }, 'google-geolocalization');
	        }
		}
	},
	
	handler : function (event) {

		var element = Event.element(event);
		
		if (element.readAttribute("name") === "wizardNext") {
			GGeoLocalization.wizardNextButton = element;
			element.stopObserving("click", GGeoLocalization.handler);
		}
		
		for (var key in GGeoLocalization.inputs) {
			GGeoLocalization.dataAvailable = GGeoLocalization.dataAvailable && (GGeoLocalization.inputs[key].value !== "");
		}
		if (GGeoLocalization.dataAvailable) {
			var address = GGeoLocalization.inputs["address"].value + " " + 
						  GGeoLocalization.inputs["postalCode"].value + " " + 
						  GGeoLocalization.inputs["city"].value + " " + 
						  GGeoLocalization.inputs["country"].value;
											
			GGeoLocalization.addressToCoords(address);
		}
		else {
			GGeoLocalization.dataAvailable = true;
			if (Object.isFunction(GGeoLocalization.oldCallback) && GGeoLocalization.oldCallback != Prototype.K) {
				GGeoLocalization.toolbarInstance._observeEvent(GGeoLocalization.saveItem, GGeoLocalization.oldCallback);
				GGeoLocalization.saveItem.fire("dom:click");
			}
		}
	},
	
	addressToCoords : function(address) {
		try {
			var geocoder = new GClientGeocoder();
			geocoder.getLatLng(address, function(point) {
				if (!point) {
					GGeoLocalization.storePoint(0, 0);
				}
				else {
					var lng = point.lng();
					var lat = point.lat();
					GGeoLocalization.storePoint(lng, lat);
				}
			});
		} catch (ex) {
		}
		
		if (Object.isFunction(GGeoLocalization.oldCallback) && GGeoLocalization.oldCallback != Prototype.K) {
			GGeoLocalization.toolbarInstance._observeEvent(GGeoLocalization.saveItem, GGeoLocalization.oldCallback);
			GGeoLocalization.saveItem.fire("dom:click");
		}
		
		if (GGeoLocalization.wizardNextButton != null) {
			GGeoLocalization.wizardNextButton.click();
		}
	},
	
	storePoint: function(lng, lat) {
		if (GGeoLocalization.index !== null) {
			var lngInput = $$('[name="'+GGeoLocalization.lngInput+'"]')[0];
			var latInput = $$('[name="'+GGeoLocalization.latInput+'"]')[0];
			var dataSourceInput = $$('[name="'+GGeoLocalization.dataSourceInput+'"]')[0];
			var elevUomIdInput = $$('[name="'+GGeoLocalization.elevUomIdInput+'"]')[0]
			if (Object.isElement(lngInput) && Object.isElement(latInput)) {
				var lngStr = new String(lng);
				var latStr = new String(lat);
	            lngStr = lngStr.replace(".", ",");
	            latStr = latStr.replace(".", ",");			
				lngInput.writeAttribute("value", lngStr);
				latInput.writeAttribute("value", latStr);
				dataSourceInput.writeAttribute("value", GGeoLocalization.dataSourceValue);
				if (Object.isElement(elevUomIdInput)) {
					if (!elevUomIdInput.value) {
						elevUomIdInput.writeAttribute("value", GGeoLocalization.elevUomIdValue);
					}
				}
			}
		}
		
	}
}

document.observe("dom:loaded", GGeoLocalization.load);