FormAndMapPositioning = {
	formContainerName : "formContainer",
	mapContainerName : "mapContainer",
	
	load: function() {
		var formContainer = $(FormAndMapPositioning.formContainerName);
		var mapContainer = $(FormAndMapPositioning.mapContainerName);
		
		if (Object.isElement(formContainer) && Object.isElement(mapContainer)) {
			var parent = formContainer.up("div");
			
			var table = new Element("table", {"width": "100%"});
			var tbody = new Element("tbody");
			var tr = new Element("tr");
			var formTd = new Element("td");
			var mapTd = new Element("td");
			
			parent.insert(table);
			table.insert(tbody);
			tbody.insert(tr);
			
			formTd.insert(formContainer);
			mapTd.insert(mapContainer);
			tr.insert(formTd);
			tr.insert(mapTd);
		}
	}
}

FormAndMapPositioning.load();