RegisterPopupTableKitResponder = {
    load : function() {
        if (typeof PopupMgr != 'undefined') {
            Object.extend(PopupMgr, {loadModal : RegisterPopupTableKitResponder.loadModal});
        }
    },
    delegateDblClickEvent : function(table, e) {
        if (typeof PopupMgr != 'undefined')
            //Delegate to PopupMgr
            PopupMgr.rowSelectedHandler(table, e);
    },
    loadModal : function() {
        if (this.MBContent) {
            RegisterPopupTableKitResponder.reloadPopupTableKit($(this.MBContent.firstDescendant()));
        }
    },
    reloadPopupTableKit : function(newContent) {
        /**
            * Talekit loading
            */
        if (typeof TableKit != 'undefined') {

            var popupContainer = $('')
            var tables = $$("div#search-result-screenlet table.selectable");
            if (tables) {
                tables.each( function(table) {

                    TableKit.register(table, {resizable: true, draggable: true, toggleable: true, selectable: true, customizable: true, headerFixable: true, editable: false, sortable:false});

                    //Gets observers
                    /*var observers = TableKit.option('observers', table.identify());
                    //Set event to delegate it
                    Object.extend(observers, {
                       'onDblClickSelectEnd' : delegateDblClickEvent
                    })

                    TableKit.register(table, {'observers' : observers});*/

                    TableKit.registerObserver(table, 'onDblClickSelectEnd', 'dblclick-select-table', delegateDblClickEvent);

                    TableKit.reloadTable(table);
                });
            }
        }
    }

}

document.observe("dom:loaded", RegisterPopupTableKitResponder.load);