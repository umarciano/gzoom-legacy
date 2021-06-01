function anchorFormSubstitution(anchor, formId) {
    var url = anchor.readAttribute('href');
    var target = anchor.readAttribute('target');

    var form = createForm(url, null, {'id' : formId, 'name' : formId, 'target' : target});
    document.body.insert(form);

    return form;
}

function createForm(actionUrl, elementCollection, option) {
    option = Object.extend(option || {}, {'class' : 'basic-form', 'method' : 'POST'});
    var form = new Element('form', option);
    initFormFromAction(form, actionUrl, null, elementCollection);
    return form;
}

function initFormFromAction(form, actionUrl, actionTarget, elementCollection) {
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
                    var inputHidden = setHiddenFormField(form, pair.key, pair.value);
                    inputHidden.addClassName('url-params');
                }
            }));
        }

        populateForm(form, elementCollection);
    }
}

function populateForm(form, elementCollection) {
    $A(elementCollection).each(function(element) {
        var elementName = element.readAttribute("name");
        if(elementName.indexOf('_o_') > -1) {
            elementName = elementName.substring(0,elementName.indexOf('_o_'));
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
}

// ============================================================================
// Evaluate any JS expression as a separate function

function evalFunc(str) {
    eval('var evalFuncTmp = function() { ' + str + '; }');
    return evalFuncTmp();
}

function evalBool(str) {
    return (evalFunc(str) != false);
}

// ============================================================================
// Link functions

function splitLink(link) {
    var obj = {
        location: null,
        query: null,
        fragment: null
    };
    var idx_query = link.indexOf('?');
    if (idx_query >= 0) {
        obj.location = link.substring(0, idx_query);
        var idx_fragment = link.lastIndexOf('#');
        if (idx_fragment >= 0) {
            obj.query = link.substring(idx_query + 1, idx_fragment);
            obj.fragment = link.substring(idx_fragment + 1);
        } else {
            obj.query = link.substring(idx_query + 1);
        }
    } else {
        obj.location = url;
    }
    return obj;
}

function collateLink(obj) {
    var link = '';
    if (obj.location)
        link += obj.location;
    if (obj.query)
        link += '?' + obj.query;
    if (obj.fragment)
        link += '#' + obj.fragment;
    return link;
}

// ============================================================================
// Hidden elements

function hideButton(button) {
    // TODO lo stile hidden-button si usa solo per le toolbar?
    if (button)
        button.hide(); //button.addClassName('hidden-button');
}

function hideAnchor(anchor) {
    if (anchor) {
        if (anchor.up('tr'))
            anchor.up('tr').hide();
        else
            anchor.hide();
    }
}

function setHiddenFormField(form, fieldName, fieldValue) {
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
}

// ============================================================================
// Simulate user events.

function submitForm(form, messageContext) {
    if (form) {
        if (messageContext)
            setHiddenFormField(form, 'messageContext', messageContext);
        var action = form.readAttribute('onsubmit');
        if (action && !evalBool(action))
            return false;
        form.submit();
    }
    return true;
}

function submitAnchor(form, anchor, messageContext) {
    if (form && anchor) {
        var onclickFunc = anchor.readAttribute('onclick');
        if (onclickFunc) {
            if (!evalBool(onclickFunc))
                return false;
        }
        var action = anchor.readAttribute('href');
        var target = anchor.readAttribute('target');
        initFormFromAction(form, action, target, null);
        if (messageContext)
            setHiddenFormField(form, 'messageContext', messageContext);
        form.submit();
    }
    return true;
}

function clickAnchor(anchor, messageContext) {
    if (anchor) {
        var onclickFunc = anchor.readAttribute('onclick');
        if (onclickFunc) {
            if (!evalBool(onclickFunc))
                return false;
        }
        var formId = anchor.identify() + '_form';
        var form = anchorFormSubstitution(anchor, formId);
        if (messageContext)
            setHiddenFormField(form, 'messageContext', messageContext);
        form.submit();
    }
    return true;
}

// ============================================================================
// Observe/replace active tab form buttons behaviour.

function observeToolbarButtonFromActiveTabForm(tabId, toolbarButtonSelector, callback) {
    var toolbarButtons = $$(toolbarButtonSelector);
    if (toolbarButtons) {
        toolbarButtons.each(function(toolbarButton) {
            var anchor = toolbarButton.down('a');
            anchor.observe('click', function(event) {
                Event.stop(event);
                var tab = Control.Tabs.findByTabId(tabId);
                if (tab) {
                    var activeContainer = tab.getActiveContainer();
                    if (activeContainer) {
                        var form = activeContainer.down('form');
                        if (form)
                            callback(anchor, form);
                    }
                }
            });
        });
    }
}

function replaceActiveTabFormSubmitButton(tabId, formButtonSelector, toolbarButtonSelector, messageContext, confirmMsg) {
    $$(formButtonSelector).each(hideButton);
    observeToolbarButtonFromActiveTabForm(tabId, toolbarButtonSelector, function(anchor, form) {
        modal_box_messages.confirm(confirmMsg, null,
            submitForm.curry(form, messageContext)
        );
    });
}

function replaceActiveTabFormSubmitAnchor(tabId, formAnchorSelector, toolbarButtonSelector, messageContext, confirmMsg) {
    $$(formAnchorSelector).each(hideAnchor);
    observeToolbarButtonFromActiveTabForm(tabId, toolbarButtonSelector, function(anchor, form) {
        var formAnchor = form.down(formAnchorSelector);
        modal_box_messages.confirm(confirmMsg, null,
            submitAnchor.curry(form, formAnchor, messageContext)
        );
    });
}

function replaceActiveTabFormAnchor(tabId, formAnchorSelector, toolbarButtonSelector, messageContext, confirmMsg) {
    $$(formAnchorSelector).each(hideAnchor);
    observeToolbarButtonFromActiveTabForm(tabId, toolbarButtonSelector, function(anchor, form) {
        var formAnchor = form.down(formAnchorSelector);
        modal_box_messages.confirm(confirmMsg, null,
            clickAnchor.curry(formAnchor, messageContext)
        );
    });
}

// ============================================================================
