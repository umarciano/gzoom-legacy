WorkEffortViewSendMail = {
	
	msgValue: "Attenzione. Una volta che il processo e\' chiuso potrebbero non essere ammesse variazioni. Sei sicuro di voler chiudere il processo?",	
		
	load: function() {
		WorkEffortViewSendMail.loadTable($("table_WESMML001_WorkEffortView"));
	},
	
	loadTable: function(table) {
		if(Object.isElement(table) && (typeof TableKit != "undefined")) {
			if (TableKit.isSelectable(table)) {
				WorkEffortViewSendMail.manageSelectedRow(table);
                TableKit.registerObserver(table, 'onSelectEnd', 'WorkEffortViewSendMail', function(table, e) {
                	WorkEffortViewSendMail.manageSelectedRow(table);
                });
			}
		}
	},
	
	manageSelectedRow: function(table){
		var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
    	if(selectedRow) {
        	var rowIndex = TableKit.getRowIndex(selectedRow);
			
        	var element = $(selectedRow).down(".emailIcon");
        	Event.observe($(element), 'click', WorkEffortViewSendMail.sendMail);
 		}
	},
	
	sendMail: function(event){
		Event.stop(event);
        var element = Event.element(event);
        if (element.tagName == "IMG") {
        	element = element.up('A');
        }
        if (element.tagName == "TD") {
        	element = element.down('A');
        }
        if (element.tagName == "A") {
            var href = element.getAttribute("href");
            Utils.showModalBox(href, {width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.50)});
        }
	}
}

WorkEffortViewSendMail.load();