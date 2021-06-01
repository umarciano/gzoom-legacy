Object.extend(TableKit, {
    isCustomizable : function(table) {
        return TableKit.tables[table.id] ? TableKit.tables[table.id].customizable : TableKit.hasClassName(table, TableKit.options.customizableSelector);
    }
});

TableKit.Customizable = {
    init : function(elm, options){
        var table = $(elm);
        if(table.tagName !== "TABLE") {return;}
        TableKit.register(table,Object.extend(options || {},{customizable:true}));

        if(!$('settingSubmitForm')) {
            /* add submit for save user setting */
            var url = getOfbizUrl("ajaxSaveWidgetUserPref"); /* defined into selectall.js */
            var form = new Element('form', {'class' : 'basic-form', 'action' : url, 'id' : 'settingSubmitForm', 'name' : 'settingSubmitForm'});
            document.body.insert(form);
        }
    },

    saveUserpreference : function(table) {
        form = $('settingSubmitForm');
        form.update();

        var tables = new Array();
        if (table)
            tables.push(table);
        else
            tables = $$("table.basic-table");


        $A(tables).each(function(t){
            t = $(t);

            /* add attribute "formName" at the table for save user setting */
            Object.extend(t,{
                formName : false,
                setFormName: function(){
                    formName = t.readAttribute('id');
                },
                getFormName: function(){
                    return formName;
                }
            });
            t.setFormName();
            var inputHidden = new Element('input', {'type' : 'hidden','value' : t.getFormName(), 'name' : 'formName'});
            form.appendChild(inputHidden);
        });

        var content = new Array();
        $A(tables).each(function(t){
            t = $(t);
            /* select the array of headers in the second Header Row */
            if(t.down('thead') != null) {
                var headersLastRow = TableKit.getLastHeaderRow(t);
                var index = 0;

                var inputHidden = new Element('input', {'type' : 'hidden', 'value' :  'BaseMessageSaveLayout', 'name' : 'messageContext'});
                form.appendChild(inputHidden);

                if (headersLastRow && headersLastRow.length > 0) {
                    content.push(t);

                    headersLastRow.each(function(item) {
                    /* set string query = "header.id : position" */
                        if(!$(item).hasClassName('hidden')) {
                            var fieldName = '';
                            fieldName = fieldName.concat("pos_"+item.id);
                            var inputHidden = new Element('input', {'type' : 'hidden', 'value' : index, 'name' : fieldName});
                            form.appendChild(inputHidden);
                            var width = $w(item.readAttribute('style')).find(function(element){
                                return element.indexOf("width");
                            });
                            if(width) {
                                var end = width.indexOf("px");
                                if(end > -1) {
                                    width = width.substring(0, end);
                                }
                                var classFieldGroupName = '';
                                classFieldGroupName = classFieldGroupName.concat("width_"+item.id);
                                var inputHidden = new Element('input', {'type' : 'hidden', 'value' :  width, 'name' : classFieldGroupName});
                                form.appendChild(inputHidden);
                            }
                        }
                        index++;
                    });
                }


            }
        });

        return new Ajax.Request(form.readAttribute('action'), { parameters : form.serialize() , 'content' : content});
    },
    load : function(contentToExplore) {
        if(TableKit.options.customizable) {
            $A(TableKit.options.customizableSelector).each(function(s){
                (!Object.isElement(contentToExplore) ? $$(s) : (contentToExplore.tagName === 'TABLE' ? (contentToExplore.hasClassName(s) ? [contentToExplore] : [])  : contentToExplore.select(s))).each(function(t) {
                    TableKit.Customizable.init(t);
                });
            });
        }
    },
    reloadTable : function(table, contentToExplore) {
        if(TableKit.tables[table.id]) {
            var op = TableKit.option('customizable', table.id);
            if(op.customizable) {TableKit.Customizable.init(table);}
        } else {
          TableKit.Customizable.load(table);
        }
    }
};

TableKit.registerExtension(TableKit.Customizable, {customizable: true, customizableSelector : ['table.customizable']});