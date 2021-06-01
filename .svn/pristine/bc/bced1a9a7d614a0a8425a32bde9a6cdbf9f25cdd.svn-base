RegisterManagementListFormResponder = {
    load : function() {
        /*UpdateAreaResponder.Responders.register( {
            onLoad : function(newContent) {
                if (typeof TableKit != 'undefined') {
                    var firstManagementContainer =  (!Object.isElement(newContent) ? $('management_0') : newContent.down('#management_0'));
                    if (firstManagementContainer) {
                        var table = firstManagementContainer.down('table.selectable');
                        if (table && TableKit.isSelectable(table)) {
                            TableKit.registerObserver(table, 'onSelectEnd', RegisterManagementListFormResponder.selectedRow);
                        }
                    }
                }
            }
        });*/

    }/*,
    selectedRow : function(table, e) {
        if (TableKit.isSelectable(table)) {
            if (RegisterManagementListFormResponder.CheckSelectionRules.dispatch(table)) {
                var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
                if (selectedRow) {
                    var inputRow = $A($(selectedRow).select('input'));

                    var insertMode = inputRow.find(function(element) {
                        return (/^insertMode/).exec(element.readAttribute('name'));
                    });

                    if ('N' === insertMode.readAttribute('value')) {
                        var formId = table.identify() + "_virtual_form"
                        inputRow = inputRow.select(function(element) {
                            return !element.hasClassName('autocompleter_parameter');
                        });

                        var form = RegisterManagementListFormResponder._createForm(getOfbizUrl('subFolderManagementContainerOnly'), inputRow, Object.extend({'id' : formId, 'name' : formId, 'target' : target}));
                        var saveViewElement = new Element('input', {'type' : 'hidden', 'value' : 'N', 'name' : 'saveView'});
                        form.insert(saveViewElement);
                        document.body.insert(form);

                        var links = [];
                        var tab = Control.Tabs.findByTabId('management_0');
                        if (tab) {
                            var linkLength = tab.getLinkSize();
                            if (linkLength) {
                                for(i = 1; i < linkLength; i++) {
                                    var link = tab.getLinkAtIndex(i);
                                    if (link)
                                        links.push($(link).up('li'));
                                }
                            }
                        }

                        new Ajax.Request($(form).action, {parameters : $(form).serialize(true), evalScripts : true, contentToUpdate : 'sub-folder-container', content : $A(links)});

                        form.remove();
                    }
                }
            }
        }
    },
    _createForm : function(actionUrl, elementCollection, option) {
        option = Object.extend(option || {}, {'class' : 'basic-form', 'method' : 'POST'});
        var form = new Element('form', option);
        RegisterManagementListFormResponder._initFormFromAction(form, actionUrl, null, elementCollection);
        return form;
    },
    _initFormFromAction : function(form, actionUrl, actionTarget, elementCollection) {
        if (form) {
            form.writeAttribute("action", (actionUrl.indexOf('?') != -1 ? actionUrl.substring(0, actionUrl.indexOf('?')) : actionUrl));
            if (actionTarget)
                form.writeAttribute("target", actionTarget);
            var queryParams = $H({}).merge(actionUrl.toQueryParams());
            if (queryParams) {
                $A(queryParams.each(function(pair) {
                    var findElement = $A(form.getElements()).find(function(elm) {
                        return pair.key === elm.readAttribute("name");
                    });

                    if (!findElement) {
                        var inputHidden = RegisterManagementListFormResponder._setHiddenFormField(form, pair.key, pair.value);
                        inputHidden.addClassName('url-params');
                    }
                }));
            }

            RegisterManagementListFormResponder._populateForm(form, elementCollection);
        }
    },
    _populateForm : function(form, elementCollection) {
        $A(elementCollection).each(function(element) {
            var elementName = element.readAttribute("name");
            if(elementName.indexOf('_') > -1) {
                elementName = elementName.substring(0,elementName.indexOf('_'));
            }
            if (element.readAttribute("value")) {
                var findElement = $A(form.getElements()).find(function(elm) {
                    return elementName === elm.readAttribute("name");
                });

                if (findElement && !findElement.hasClassName('url-params')) {
                    var valueHidden = findElement.readAttribute('value');
                    if (valueHidden) {
                        if (valueHidden.indexOf(']') != -1) {
                            valueHidden = valueHidden.replace(']', '|]');
                        } else {
                            valueHidden = valueHidden.concat('|]');
                        }
                    }
                    valueHidden = valueHidden.replace(']', element.readAttribute('value')+ ']');
                    if (valueHidden.indexOf('[') == -1)
                        valueHidden = '['.concat(valueHidden);
                    findElement.writeAttribute('value', valueHidden);
                } else if (!findElement || (findElement && !findElement.hasClassName('url-params'))) {
                    var inputHidden = new Element('input', {'type' : 'hidden', 'value' : element.readAttribute('value'), 'name' : elementName, 'id' : elementName});
                    form.insert(inputHidden);
                }
            }
        });
    },
    _setHiddenFormField : function(form, fieldName, fieldValue) {
        if (form) {
            var field = form.getInputs('hidden', fieldName).first();
            if (field) {
                field.writeAttribute('value', fieldValue);
            } else {
                field = new Element('input', { 'type': 'hidden', 'name': fieldName, 'value': fieldValue });
                form.insert(field);
            }
        }
        return field;
    }*/
}

/*RegisterManagementListFormResponder.CheckSelectionRules = {
    selectionRules: $H({}),

    _each: function(iterator) {
        this.selectionRules._each(iterator);
    },

    register: function(table, rule) {
        var rules = this.selectionRules.get(table.identify());
        if (!rules) {
            rules = [];
        }
        rules.push(rule);
        this.selectionRules.set(table.identify(), rules);
    },

    unregister: function(table, rule) {
        var rules = this.selectionRules.get(table.identify());
        if (rules) {
            rules = rules.without(rule);
            if (rules.size() == 0) {
                this.selectionRules.unset(table.identify());
                return;
            }
            this.selectionRules.set(table.identify(), rules);
        }
    },

    dispatch: function(table) {
        var returnValue = true;
        var rules = this.selectionRules.get(table.identify());
        if (rules && rules.size() > 0) {
            rules.each(function(rule) {
                if (Object.isFunction(rule)) {
                    try {
                        returnValue = returnValue && rule.apply(rule, [table]);
                    } catch (e) { }
                }
            });
        }

        return returnValue;
    }
};
Object.extend(RegisterManagementListFormResponder.CheckSelectionRules, Hash);*/

document.observe("dom:loaded", RegisterManagementListFormResponder.load);