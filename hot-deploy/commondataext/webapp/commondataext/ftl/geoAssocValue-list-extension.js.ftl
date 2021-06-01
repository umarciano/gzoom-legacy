GeoAssocValueList = {
    load: function(withoutResponder) {
        GeoAssocValueList.loadGeoTable($('table_GMM001_Geo'), true);
        GeoAssocValueList.loadGeoAssocTable($('table_GAMM001_GeoAssoc'), true);
        if(!withoutResponder){
            UpdateAreaResponder.Responders.register( {
                onLoad : function(newContent) {
                    var geoTable = Object.isElement(newContent) ? newContent.down('#table_GMM001_Geo') : null;
                    if(geoTable) {
                        GeoAssocValueList.loadGeoTable(newContent, true);
                    }
                    var geoAssocTable = Object.isElement(newContent) ? newContent.down('#table_GAMM001_GeoAssoc') : null;
                    if(geoAssocTable) {
                        GeoAssocValueList.loadGeoAssocTable(newContent, true);
                    }
                },
                unLoad : function() {
                    return typeof 'GeoAssocValueList' === 'undefined';
                }
            }, 'GeoAssocValueList');
        }
    },
    
    loadGeoTable : function(geoTable) {
        if(geoTable) {
            if (typeof TableKit != 'undefined') {
                if (TableKit.isSelectable(geoTable)) {
                    GeoAssocValueList.managContextLink(geoTable, '.geoMenu', 'hasGeoRef');
                    TableKit.registerObserver(geoTable, 'onSelectEnd', function(geoTable, e) {
                        GeoAssocValueList.managContextLink(geoTable, '.geoMenu', 'hasGeoRef');
                    });
                    //TableKit.registerObserver(geoTable, 'onDblClickSelectEnd', GeoAssocValueList.openManagement);
                }
            }
         }
    },
    
    loadGeoAssocTable : function(geoAssocTable) {
        if(geoAssocTable) {
           if (typeof TableKit != 'undefined') {
             if (TableKit.isSelectable(geoAssocTable)) {
                    GeoAssocValueList.managContextLink(geoAssocTable, '.geoAssocValueMenu', 'hasValue');
                    TableKit.registerObserver(geoAssocTable, 'onSelectEnd', function(geoAssocTable, e) {
                        GeoAssocValueList.managContextLink(geoAssocTable, '.geoAssocValueMenu', 'hasValue');
                    });
                }
            }
        }
        
    },
    
    managContextLink : function(table, contextLink, condition) {
    	var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
        if(selectedRow) {
            var value = $A(selectedRow.select('input')).find(function(s) {
                return s.readAttribute('name').indexOf(condition) > -1;
            });
            if(value.readAttribute('value') == 'Y') {
                var context_link = $$(contextLink).first();
                context_link.removeClassName('hidden');
            }
            else {
                var context_link = $$(contextLink).first();
                context_link.addClassName('hidden');
            }
        }
    }
}

GeoAssocValueList.load();

