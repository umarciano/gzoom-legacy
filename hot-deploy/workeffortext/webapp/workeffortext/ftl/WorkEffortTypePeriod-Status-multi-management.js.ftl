WorkEffortTypePeriodMulti = {
			
	load: function() {
		var tables = new Array();
		tables.push($("table_WETPI001_WorkEffortTypePeriod"));
		tables.push($("table_WETPI001_WorkEffortTypePeriodView"));
		WorkEffortTypePeriodMulti.loadTables(tables);
	},
	
	loadTables: function(tables) {
		tables.each(function(table) {
			WorkEffortTypePeriodMulti.loadTable(table);
		});
	},
	
	loadTable: function(table) {
		if(Object.isElement(table) && (typeof TableKit != "undefined")) {
			if (TableKit.isSelectable(table)) {
				WorkEffortTypePeriodMulti.manageSelectedRow(table);
                TableKit.registerObserver(table, 'onSelectEnd', 'WorkEffortTypePeriodMulti', function(table, e) {
                	WorkEffortTypePeriodMulti.manageSelectedRow(table);
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
			var dropListId = formName + "_statusEnumId_o_" + rowIndex;
			var statusDropList = DropListMgr.getDropList(dropListId);
			if(statusDropList) {
				statusDropList.registerOnChangeListener(WorkEffortTypePeriodMulti.statusDropListHandler.curry(form, dropListId, rowIndex), "statusDropListHandlerMulti_" + rowIndex);				
			}
		}
	},
	
	statusDropListHandler : function(form, droplistDiv, rowIndex) {		
		var dropListInput = $(droplistDiv).down('input.droplist_code_field');
		var cachedStatusId = FormKit.getCachedValue(form, dropListInput);
		if(cachedStatusId != "OPEN" &&  $(dropListInput).getValue() == "OPEN") {
			var row = $(droplistDiv).up("tr");
			if(confirm('${uiLabelMap.WorkEffortTypePeriodOpenAllRootsConfirm}?')) {
				WorkEffortTypePeriodMulti.setOpenAllRoots(row, rowIndex, 'Y');
			} else {
				WorkEffortTypePeriodMulti.setOpenAllRoots(row, rowIndex, 'N');
			}
    		var onsubmit = $(form).readAttribute("onsubmit");
        	var containerId = onsubmit.split(",")[2].substring(2);
     		var saveItem = Toolbar.getInstance(containerId).getItem('.save');
            if (Object.isElement(saveItem)) {
            	saveItem.fire('dom:click');
            }
		}
	},
	
	setOpenAllRoots : function(row, rowIndex, fieldValue) {
		var fields = $A($(row).select("input")).select(function(element) {
			var name = element.readAttribute("name");
	        if (name.indexOf("openAllRoots_o_" + rowIndex) != -1) {
	            element.setValue(fieldValue);
	        }
	    }.bind(this));
	}
}

WorkEffortTypePeriodMulti.load();