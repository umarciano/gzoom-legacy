if (typeof(PartyParentRoleList) === 'undefined') {

    PartyParentRoleList = {
        deleteAnchor: false,
        load: function() {
            var partyParentRoleTable = $('table_PPRMM001_PartyParentRole');
            if(Object.isElement(partyParentRoleTable)) {
                if (TableKit.isSelectable(partyParentRoleTable)) {
                    TableKit.registerObserver(partyParentRoleTable, 'onSelectEnd', PartyParentRoleList.checkSelection);
                
                    if (typeof(Control.Tabs) !== 'undefined') {
                        var partyParentRoleContainer = null;
                        var activeContainer = Control.Tabs.instances[0].getActiveContainer();
                        if (Object.isElement(activeContainer.down('#table_PPRMM001_PartyParentRole'))) {
                            PartyParentRoleList.checkSelection(partyParentRoleTable);
                            partyParentRoleContainer = activeContainer;
                        } else {
                            $R(0,Control.Tabs.instances[0].getLinkSize()-1).each(function(index) {
                                var container = Control.Tabs.instances[0].getContainerAtIndex(index);
                                if (Object.isElement(container) && Object.isElement($(container).down('#table_PPRMM001_PartyParentRole'))) {
                                    Control.Tabs.instances[0].registerEvent(container.identify(), function(currentContainer) {
                                        PartyParentRoleList.checkSelection(partyParentRoleTable);
                                    });
                                }
                            });
                        }
                    } else {
                        PartyParentRoleList.checkSelection(partyParentRoleTable);
                    }
                }
            }
        },
        
        checkSelection : function(table, e) {
            var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
            if(selectedRow) {
                var deleteButtonStyleElement = $A(selectedRow.select('input')).find(function(element) {
                    return element.readAttribute('name').startsWith('deleteButtonStyle');
                });
                if (Object.isElement(deleteButtonStyleElement)) {
                    var deleteButtonStyle = deleteButtonStyleElement.getValue();
                    if (!Object.isUndefined(deleteButtonStyle)) {
                        var deleteMenuItem = $('common-container').down('li.management-delete');
                        
                        if (Object.isElement(deleteMenuItem)) {
                            if (deleteButtonStyle.indexOf('disabled') != -1) {
                                var styleToRemove = deleteButtonStyle.substring(0, deleteButtonStyle.indexOf('-disabled'))
                                if (deleteMenuItem.hasClassName(styleToRemove)) {
                                    deleteMenuItem.removeClassName(styleToRemove)
                                    deleteMenuItem.addClassName(deleteButtonStyle);
                                    deleteMenuItem.addClassName('disabled');
                                    PartyParentRoleList.deleteAnchor = deleteMenuItem.down('a').remove();
                                    deleteMenuItem.innerHTML = PartyParentRoleList.deleteAnchor.innerHTML;
                                }    
                            } else {
                                var styleToRemove = deleteButtonStyle + '-disabled';
                                if (deleteMenuItem.hasClassName(styleToRemove)) {
                                    deleteMenuItem.removeClassName(styleToRemove);
                                    deleteMenuItem.removeClassName('disabled');
                                    deleteMenuItem.addClassName(deleteButtonStyle);
                                    deleteMenuItem.innerHTML = '';
                                    deleteMenuItem.insert(PartyParentRoleList.deleteAnchor);
                                }  
                            }
                        }
                    }
                }
            }
        }
    
    }
    
    PartyParentRoleList.load();
}