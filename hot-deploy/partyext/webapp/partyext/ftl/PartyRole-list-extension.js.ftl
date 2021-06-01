if (typeof(PartyRoleList) === 'undefined') {
    
    PartyRoleList = {
        deleteAnchor: false,
        load: function(newContentToExplore, withoutResponder) {
            var formToExplore = Object.isElement(newContentToExplore) ? newContentToExplore.up('form') : null;
            if(!formToExplore) {
                var formsToExplore = $$('form');
                formsToExplore.each(function(form) {
                    if (form.identify() == 'PRoMM001_PartyRoleTypeView') {
                        PartyRoleList.loadForm(form);
                    }
                });
            }
            else
                PartyRoleList.loadForm(formToExplore);
            <#--if (!withoutResponder) {
                UpdateAreaResponder.Responders.register(PartyRoleList.responder, 'PartyRoleList');
            }-->
        },
    
        <#--responder : {
                    onLoad : function(newContent) {
                        PartyRoleList.load(newContent, true);
                    },
                    unLoad : function() {
                        //TODO Prevedere eventuali deregistrazioni per liberare memoria
                        return typeof 'PartyRoleList' === 'undefined';
                    }
        },-->
    
        loadForm : function(form) {
            var partyRoleTable = form.down('#table_PRoMM001_PartyRoleTypeView');
            if(Object.isElement(partyRoleTable)) {
                if (TableKit.isSelectable(partyRoleTable)) {
                    TableKit.registerObserver(partyRoleTable, 'onSelectEnd', PartyRoleList.checkSelection);
                
                    PartyRoleList.checkSelection(partyRoleTable);
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
                                    PartyRoleList.deleteAnchor = deleteMenuItem.down('a').remove();
                                    deleteMenuItem.innerHTML = PartyRoleList.deleteAnchor.innerHTML;
                                }    
                            } else {
                                var styleToRemove = deleteButtonStyle + '-disabled';
                                if (deleteMenuItem.hasClassName(styleToRemove)) {
                                    deleteMenuItem.removeClassName(styleToRemove);
                                    deleteMenuItem.removeClassName('disabled');
                                    deleteMenuItem.addClassName(deleteButtonStyle);
                                    deleteMenuItem.innerHTML = '';
                                    deleteMenuItem.insert(PartyRoleList.deleteAnchor);
                                }  
                            }
                        }
                    }
                }
            }
        }
    
    }
    
    <#if !parameters.justRegisters?has_content>
    PartyRoleList.load();
    </#if>
}