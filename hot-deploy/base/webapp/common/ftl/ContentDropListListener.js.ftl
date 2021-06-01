ContentDropListListener = {
	uploadContainerId: "uploadContainer",
	managementContainerId: "managementContainer",
	contentTypeDescriptionSuffix : "contentTypeDescription",
	contentTypeIdSuffix : "contentTypeId",
	
	load: function() {
		var container = $(ContentDropListListener.uploadContainerId) || $(ContentDropListListener.managementContainerId);
		if (Object.isElement(container)) {
			var form = container.down("form.content-upload-form") || container.down("form.content-management-form");
			if (Object.isElement(form) && form.tagName === "FORM") {
				ContentDropListListener.registerDropList(form);		
			}
		}
	},
	
	registerDropList: function(form) {
		var formName = form.readAttribute("name");
		var dropListToListen = form.down(".drop-list-to-listen");
		if (Object.isElement(dropListToListen)) {
	        var dropList = DropListMgr.getDropList(dropListToListen.identify());
	        var contentTypeDescriptionField = form.down("input#" + formName + "_" + ContentDropListListener.contentTypeDescriptionSuffix);
	        var contentTypeIdField = form.down("[name=\"" + ContentDropListListener.contentTypeIdSuffix + "\"]");
	        if (dropList && Object.isElement(contentTypeDescriptionField) && Object.isElement(contentTypeIdField)) {
	            dropList.registerOnChangeListener(ContentDropListListener.f.curry(contentTypeDescriptionField, contentTypeIdField), "...ContentTypeId");
	        }
        }
	},
	
	f: function(contentTypeDescriptionField, contentTypeIdField, span1, span2, span3, span4) {
		var array = new Array(span1, span2, span3, span4);
		array.each(function(span) {
			if (Object.isElement(span)) {
				var infoList = span.innerHTML.split(":");
				if (Object.isArray(infoList)) {
					for (var i = 0; i < infoList.size(); i++) {
						if (infoList[i] === "description") {
							contentTypeDescriptionField.value = infoList[i + 1];
						}
						if (infoList[i] === "contentTypeId") {
							contentTypeIdField.writeAttribute("value", infoList[i + 1]);
						} 
					}
				}
			} 
		});  
	}
}

ContentDropListListener.load();