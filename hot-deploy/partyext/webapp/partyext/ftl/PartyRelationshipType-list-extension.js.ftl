PartyRelationshipTypeList = {
    load: function() {
        var partyRelationshipTypeTable = $('table_PRTMM001_PartyRelationshipType');
        if(partyRelationshipTypeTable) {
            if (TableKit.isSelectable(partyRelationshipTypeTable)) {
                PartyRelationshipTypeList.checkSelection(partyRelationshipTypeTable);
                Tabs.CheckSelectionOnTabRules.register($('management_0'),PartyRelationshipTypeList.checkSelection);
            }
        }
    },
    checkSelection : function(container) {
        var currentInstance = Control.Tabs.findByTabId($(container).identify());;
        var table = $('table_PRTMM001_PartyRelationshipType');

        var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
        if(selectedRow) {
            var explicitRole = $A(selectedRow.select('input', 'select')).find(function(s) {
                return s.readAttribute('name').indexOf('explicitRole') > -1;
            });
            if(explicitRole.getValue() == 'Y') {
                return true;
            }
        }

        return false;
    }
}

PartyRelationshipTypeList.load();