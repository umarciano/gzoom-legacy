WorkEffortTransactionViewContext = {
	load: function() {
		WorkEffortTransactionViewContext.loadTable($("table_WETVCLMMF001_WorkEffortTransactionView"));
	},
	loadTable: function(table) {
		if(Object.isElement(table) && (typeof TableKit != "undefined")) {
			if (TableKit.isSelectable(table)) {
				WorkEffortTransactionViewContext.manageSelectedRow(table);
                TableKit.registerObserver(table, 'onSelectEnd', 'WorkEffortTransactionViewContext', function(table, e) {
                    WorkEffortTransactionViewContext.manageSelectedRow(table);
                });
			}
		}
	},
	
	
	manageSelectedRow: function(table){
		var form = table.up("form");
		var formName = form.readAttribute("name");
			
		var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
    	if(selectedRow) {
        	var rowIndex = TableKit.getRowIndex(selectedRow);        	
			
			/***
			* Controllo per la gestione della calcellazione in base al valModId			
			*/
			WorkEffortTransactionViewContext.disabledDelete(selectedRow, formName, rowIndex);
		}
	},
	
	classNameProperty : 'disabledByRowSelectedValMod',
	arrayAnchor : false,
	
	disabledDelete: function(selectedRow, formName, rowIndex) {
		
		var valModId = selectedRow.down("input[name='valModId_o_" + rowIndex + "']");
		var weTransTypeValueId =selectedRow.down("input[name='weTransTypeValueId_o_" + rowIndex + "']");
		
		if (Object.isElement(valModId)) {
			/**
			* Devo disabilitare la cancellazione
			*/
			var toolbarSelected = $(document.body).down('div.screenlet-title-bar');
			var icons = $(toolbarSelected).select('li.delete', 'li.delete-disabled');
			
			var valModIdValue = valModId.getValue();
			if (valModIdValue == 'ALL_NOT_MOD' || (valModIdValue == 'ACTUAL_NOT_MOD' && weTransTypeValueId.getValue() == 'ACTUAL') || (valModIdValue == 'BUDGET_NOT_MOD' && weTransTypeValueId.getValue() == 'BUDGET')) {
				
				if (Object.isArray(icons)) {
	            	icons.each(function(element, index){
	            		element.addClassName(WorkEffortTransactionViewContext.classNameProperty);
	            		element.className = element.className.replace('management-delete delete', 'delete-disabled disabled ');
	            		if(!Object.isArray(WorkEffortTransactionViewContext.arrayAnchor)){
	            			WorkEffortTransactionViewContext.arrayAnchor = $A();
	            		} 
	            		
	            		if (Object.isElement(element.down('a'))) {
            		        WorkEffortTransactionViewContext.arrayAnchor[index] = element.down('a').remove();
            		    }
            		    if (Object.isElement(WorkEffortTransactionViewContext.arrayAnchor[index])) {
	                       element.innerHTML = WorkEffortTransactionViewContext.arrayAnchor[index].innerHTML;
	                    }
	                    
	            	});
	            }
			} else {
				if (Object.isArray(icons)) {
	    			icons.each(function(element, index){
	    			
	    				if(element.hasClassName(WorkEffortTransactionViewContext.classNameProperty)){
	    					element.removeClassName(WorkEffortTransactionViewContext.classNameProperty);
	    					element.removeClassName('disabled');
	            			element.className = element.className.replace('delete-disabled', 'management-delete delete');
	            			
	            			if(!Object.isArray(WorkEffortTransactionViewContext.arrayAnchor)){
		            			WorkEffortTransactionViewContext.arrayAnchor = $A();
		            		} 
	            			if (Object.isElement(WorkEffortTransactionViewContext.arrayAnchor[index])) {
	                            element.innerHTML = '';
	                            element.insert(WorkEffortTransactionViewContext.arrayAnchor[index]);
	            			}
	            		}
	    			});
	            }
			}
			
		}
	}

}

WorkEffortTransactionViewContext.load();