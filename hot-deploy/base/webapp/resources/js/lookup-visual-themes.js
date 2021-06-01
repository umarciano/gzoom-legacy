LookupVisualThemes = {
    options : {
        registerMenu: false,
        registerDropList: false,
        registerTabs: false,
        registerLookup: false,
        registerTable: true,
        registerForm: false,
        registerAjaxReloading: false,
        registerForcusEvent: false,
        registerCalendar: false,
        'delegateDblClickEvent' : function(table, e) {
                if (typeof LookupMgr !== 'undefined' && typeof TableKit !== 'undefined') {
                    if (TableKit.isSelectable(table)) {
                        var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
                        if (selectedRow) {
                            var parametersToSave = selectedRow.select('.parameter-to-save');
                            if (Object.isArray(parametersToSave) && parametersToSave.size() > 0) {
                                var parameters = $H({});
                                parametersToSave.each(function(elm) {
                                    parameters.set(elm.readAttribute('name'), elm.getValue());
                                });

                                var url = selectedRow.down('#url')
                                new Ajax.Request(url.getValue(), {
                                    parameters: parameters.toObject(),
                                    // asynchronous: false,
                                    onSuccess: function(response) {
                                        var data = {};
                                        try {
                                            data = response.responseText.evalJSON(true);
                                        } catch(e) {}

                                        if (data && data._ERROR_MESSAGE_LIST_ == undefined && data._ERROR_MESSAGE_ == undefined) {
                                            var resources = selectedRow.select('.old-resources');

                                            resources.each(function(elm) {
                                                var elmValue = elm.getValue();
                                                if (elmValue) {
                                                    var resourceList = $w(elmValue);
                                                    resourceList.each(function(resource) {
                                                        if (resource.lastIndexOf('.') != -1)
                                                            Utils.removejscssfile(resource, resource.substring(resource.lastIndexOf('.')+1));
                                                    });

                                                }
                                            });

                                            resources = selectedRow.select('.new-resources');

                                            resources.each(function(elm) {
                                                var elmValue = elm.getValue();
                                                if (elmValue) {
                                                    var resourceList = $w(elmValue);
                                                    resourceList.each(function(resource) {
                                                        if (resource.lastIndexOf('.') != -1) {
                                                            var extension = resource.substring(resource.lastIndexOf('.')+1);

                                                            if (extension === 'js') {
                                                                var scriptExist = Utils.getjscssfile(resource, extension);
                                                                if (!scriptExist)
                                                                    Utils.loadjscssfile(resource, extension);
                                                            } else {
                                                                Utils.loadjscssfile(resource, extension);
                                                            }
                                                        }
                                                    });

                                                }
                                            });
                                        } else {
                                            data['messageContext'] = 'BaseSaveVisualTheme'
                                            modal_box_messages.onAjaxLoad(data, Prototype.K)
                                        }

                                    }
                                });
                            }
                        }
                    }
                }
                Modalbox.hide();
            }
    },
    afterLoadModal : function() {
        LookupProperties.afterLoadModal(LookupVisualThemes.options);
    },

    beforeHideModal : function() {
        LookupProperties.beforeHideModal(LookupVisualThemes.options);
    },

    afterHideModal : function() {
        LookupProperties.afterHideModal(LookupVisualThemes.options);
    }
}