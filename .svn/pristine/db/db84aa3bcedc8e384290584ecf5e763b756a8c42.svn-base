RegisterGalleryTableKitResponder = {
    load : function() {
        if (typeof CanvasGallery != 'undefined') {
            Object.extend(CanvasGallery, {loadModal : RegisterGalleryTableKitResponder.loadModal});
        }
    },
    delegateDblClickEvent : function(table, e) {
        if (typeof IconGallery != 'undefined')
            //Delegate to IconGallery
            IconGallery.rowSelectedHandler(table, e);
    },
    loadModal : function() {
        if (this.MBContent) {
            RegisterGalleryTableKitResponder.reloadGalleryTableKit($(this.MBContent.firstDescendant()));
        }
    },
    reloadGalleryTableKit : function(newContent) {
        /**
            * Talekit loading
            */
        if (typeof TableKit != 'undefined') {

            var tables = $$("div#search-result-screenlet table.selectable");
            if (tables) {
                tables.each( function(table) {

                    TableKit.register(table, {resizable: true, draggable: true, toggleable: true, selectable: true, customizable: true, headerFixable: true, editable: false, sortable:false});
                    TableKit.registerObserver(table, 'onDblClickSelectEnd', 'dblclick-select-table', delegateDblClickEvent);
                    TableKit.reloadTable(table);
                });
            }
        }
    }

}

document.observe("dom:loaded", RegisterGalleryTableKitResponder.load);