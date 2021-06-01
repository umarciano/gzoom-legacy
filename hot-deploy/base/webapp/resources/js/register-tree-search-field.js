RegisterTreeSearchField = {
    load : function(newContent, withoutResponder) {
        RegisterTreeSearchField._cleanSearchFieldList();

        (Object.isElement(newContent) ? $(newContent) : $(document.body)).select('.search-field').each(function(element) {
            element.observe('focus', RegisterTreeSearchField._focusEvent);
            element.observe('change', RegisterTreeSearchField._changeEvent);
            element.observe('blur', RegisterTreeSearchField._blurEvent);

            if (!Object.isArray(RegisterTreeSearchField.treeSearchFieldList))
                RegisterTreeSearchField.treeSearchFieldList = new Array();
            RegisterTreeSearchField.treeSearchFieldList.push(element);
            RegisterTreeSearchField.treeSearchFieldDefaultValueMap.set(element.identify(), element.getValue());
        });

        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register( {
                onLoad : function(newContent) {
                    RegisterTreeSearchField.load(newContent, true);
                },

                unLoad : function() {
                    RegisterTreeSearchField._cleanSearchFieldList();
                }
            }, 'register-tree-search-field');
        }
    },

    _cleanSearchFieldList : function() {
        if (Object.isArray(RegisterTreeSearchField.treeSearchFieldList) && RegisterTreeSearchField.treeSearchFieldList.size()  > 0) {
            RegisterTreeSearchField.treeSearchFieldList.each(function(treeSearchField) {
                var treeSearchFieldId = null;
                if (Object.isString(treeSearchField)) {
                    treeSearchFieldId = treeSearchField;
                } else if (Object.isElement(treeSearchField)) {
                    treeSearchFieldId = treeSearchField.identify();
                }

                if (treeSearchFieldId && !Object.isElement($(treeSearchFieldId))) {
                    if (Object.isElement(treeSearchField)) {
                        Event.stopObserving(treeSearchField, 'focus', RegisterTreeSearchField._focusEvent);
                        Event.stopObserving(treeSearchField, 'change', RegisterTreeSearchField._changeEvent);
                        Event.stopObserving(treeSearchField, 'blur', RegisterTreeSearchField._blurEvent);

                        RegisterTreeSearchField.treeSearchFieldList = RegisterTreeSearchField.treeSearchFieldList.without(treeSearchField);
                        RegisterTreeSearchField.treeSearchFieldDefaultValueMap.unset(treeSearchFieldId);
                    }
                }

            });
        }
    },

    _focusEvent : function(e) {
        var elm = Event.element(e);

        elm = $(elm);
        if (elm) {
            if (!('changed' in elm) || ('changed' in elm && !elm['changed'])) {
                elm.setValue('');
            }
        }
    },

    _blurEvent : function(e) {
        var elm = Event.element(e);

        if (elm) {
            var val = elm.getValue();
            if (val.length == 0 && (!('changed' in elm) || ('changed' in elm && !elm['changed']))) {
                elm.setValue(RegisterTreeSearchField.treeSearchFieldDefaultValueMap.get(elm.identify()));
            }
        }
    },

    _changeEvent : function(e) {
        var elm = Event.element(e);

        elm = $(elm);
        if (elm) {
            var val = elm.getValue();

            if (val.length == 0) {
                Object.extend(elm, {'changed' : false});
                elm.setValue(RegisterTreeSearchField.treeSearchFieldDefaultValueMap.get(elm.identify()));
            } else {
                Object.extend(elm, {'changed' : true});
            }
        }
    },

    treeSearchFieldList : new Array(),
    treeSearchFieldDefaultValueMap : $H({})

}

document.observe("dom:loaded", RegisterTreeSearchField.load);