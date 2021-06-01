RoleTypeAttrList = {
    load: function() {
        var roleTypeTable = $('table_RTMM001_RoleType');
        if(roleTypeTable) {
            if (TableKit.isSelectable(roleTypeTable)) {
                RoleTypeAttrList.checkSelection(roleTypeTable);
                Tabs.CheckSelectionOnTabRules.register($('management_0'), RoleTypeAttrList.checkSelection);
            }
        }
    },
    checkSelection : function(container) {
        var table = $('table_RTMM001_RoleType');

        var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
        if(selectedRow) {
            var element = $A(selectedRow.select('input')).find(function(s) {
                return s.readAttribute('name').indexOf('roleTypeId') > -1;
            });
            var parentTypeId = $A(selectedRow.select('input')).find(function(s) {
                return s.readAttribute('name').indexOf('parentTypeId') > -1;
            });

            if(element.getValue() == parentTypeId.getValue() || parentTypeId.getValue() == '') {
                return true;
            }
        }

        return false;
    }
}

RoleTypeAttrList.load();