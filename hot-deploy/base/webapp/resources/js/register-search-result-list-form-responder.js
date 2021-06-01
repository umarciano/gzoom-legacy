RegisterSearchResultListFormResponder = {
    load : function(newContentToExplore, withoutResponder, contentToUpdate) {
        RegisterSearchResultListFormResponder.registerDblClick();
        UpdateAreaResponder.Responders.register( {
            onAfterLoad : function(newContent) {            
            	RegisterSearchResultListFormResponder.registerDblClick(newContent);
            }
        }, 'register-search-result-list-responder');

    },
    registerDblClick : function (newContentToExplore){
    	var tableSelectable = !Object.isElement(newContentToExplore) ? $$('table.selectable') : ((newContentToExplore.tagName==='TABLE' && newContentToExplore.hasClassName('selectable')) ? [newContentToExplore] : newContentToExplore.select('table.selectable'));
        tableSelectable.each(function(table) {
            if (TableKit.isSelectable(table) && table.hasClassName('dblclick-open-management')) {
                // in questo modo è possibile capire se il risultato della ricerca si trova nella modalBox
                // e quindi il doppio click non deve aprire il dettaglio
                var modalBoxWindow =  table.up('#MB_window');
            	if(typeof modalBoxWindow === 'undefined') {                                   
                    if (TableKit.isSelectable(table)) {                        
                        TableKit.registerObserver(table, 'onDblClickSelectEnd', 'dblclick-select-table', RegisterSearchResultListFormResponder.openManagement);
                    }
            	}
            }
        });
    },
    openManagement : function(table, e) {
    	var selectedRow = TableKit.Selectable.getSelectedRows(table).first();
    	if (Object.isElement(selectedRow)) {
    		var operationField = selectedRow.select('input').find(function(element) {
    			return element.readAttribute('name').startsWith('operation');
    		});
    		if (!Object.isElement(operationField) || (Object.isElement(operationField) && operationField.getValue() !== 'CREATE')) {
	    		var content = table.up("div.management");
	        	if (!Object.isElement(content))
	        		content = table.up("div#searchListContainer");    	
	        	var item = Toolbar.getInstance(content.identify()).getItem(".management-selected-element");
	        	if (!Object.isElement(item))
	        		item = Toolbar.getInstance(content.identify()).getItem(".search-selected-element");
	        	if (Object.isElement(item)) 
	            	item.fire('dom:click');
    		}
    	}
    }
    
} 

document.observe("dom:loaded", RegisterSearchResultListFormResponder.load);   