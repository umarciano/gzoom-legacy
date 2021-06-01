if(typeof(Prototype) == "undefined")
    throw "FormKit requires Prototype to be loaded.";

var FormKit = Class.create( {
    initialize : function(elm, options) {
        var form = $(elm);

        if(form.tagName !== "FORM") {
            return;
        }
        FormKit.register(form,Object.extend(FormKit.options,options || {}));
        this.id = form.identify();
        var op = FormKit.option('cachable', this.id);
        if(op.cachable) {
            FormKit.Cachable.init(form);
        }
    }
})

Object.extend(FormKit, {
    loadFields : function(form) {
        form = $(form);
        var id = form.identify();

        var op = FormKit.option('freeze', id);
        if (op.freeze) {
            if (FormKit.forms[id]) {
                FormKit.forms[id].dom.fields = form.serialize(true);

                /*op.freeze = false;
                FormKit.register(form, op);*/
            }
        }
    },
    resetFields : function(form, resetFunction) {
        form = $(form);
        var id = form.identify();
        if (FormKit.forms[id]) {
            if($H(FormKit.forms[id].dom.fields).keys().size() > 0) {
            	if (!Object.isFunction(resetFunction)) {
            		resetFunction = Prototype.K;
            	}
                var formElements = $A(form.getElements());
                formElements.each(function(element) {
                    var element = resetFunction(element);
                    if(element) {
                        var elementName = element.readAttribute('name');
                        if (elementName && FormKit.forms[id].dom.fields[elementName]) {
                            element.setValue(FormKit.forms[id].dom.fields[elementName]);
                        } else {
                            element.setValue('');
                        }
                    }
                });
            }
        }
    },
    setCachedValue : function(form, formElement) {
    	if (!form) {
            form = $(formElement.up('form'));
        }
    	form = $(form);
    	var id = form.identify();
        if (!FormKit.forms[id]) {
            return null;
        }
        if($H(FormKit.forms[id].dom.fields).keys().size() == 0) {
            return null;
        }
        var map = $H(FormKit.forms[id].dom.fields);
        map.set(formElement.readAttribute('name'), formElement.getValue());
        FormKit.forms[id].dom.fields = map.toObject();
    },
    getCachedValue : function(form, formElement) {
        if (!form) {
            form = $(formElement.up('form'));
        }
        form = $(form);
        var id = form.identify();
        if (!FormKit.forms[id]) {
            return null;
        }
        if($H(FormKit.forms[id].dom.fields).keys().size() == 0) {
            return null;
        }
        return $H(FormKit.forms[id].dom.fields).get(formElement.readAttribute('name'));
    },
    register : function(form, options) {
        if(!form.id) {
            form.id = "formkit-form-" + FormKit._getc();
        }
        var id = form.id;
        FormKit.forms[id] = FormKit.forms[id] ?
                                Object.extend(FormKit.forms[id], options || {}) :
                                Object.extend(
                                  {dom : {fields:{}},cachable:false},
                                  options || {}
                                );
    },
    _registeredExtension : null,
    registerExtension : function(loadNamespace, _options) {
        if (!FormKit._registeredExtension)
            FormKit._registeredExtension = new Array();
        FormKit._registeredExtension.push(loadNamespace);
        FormKit.setup(_options);
    },
    notify : function(eventName, form, event) {
        if(FormKit.forms[form.id] &&  FormKit.forms[form.id].observers && FormKit.forms[form.id].observers[eventName]) {
            FormKit.forms[form.id].observers[eventName](form, event);
        }
        FormKit.options.observers[eventName](form, event);
    },
    isCachable : function(form) {
        return FormKit.forms[form.identify()] ? FormKit.forms[form.identify()].cachable : false;
    },
    setup : function(o) {
        Object.extend(FormKit.options, o || {} );
    },
    option : function(s, id, o1, o2) {
        o1 = o1 || FormKit.options;
        o2 = o2 || (id ? (FormKit.forms[id] ? FormKit.forms[id] : {}) : {});
        var key = id + s;
        if(!FormKit._opcache[key]){
            FormKit._opcache[key] = $A($w(s)).inject([],function(a,v){
                //Maps spa - bug fix
                //a.push(a[v] = o2[v] || o1[v]);
                a.push(a[v] = (o2[v] != null && o2[v] != undefined) ?  o2[v] : o1[v]);
                return a;
            });
        }
        return FormKit._opcache[key];
    },
    e : function(event) {
        return event || window.event;
    },
    forms : {},
    _opcache : {},
    options : {
        autoLoad : true,
        cachable : true,
        freeze : true,
        cachableSelector : ['form.cachable'],
        observers : {
            'onCacheStart' 	        : Prototype.K,
            'onCache' 		        : Prototype.K,
            'onCacheEnd' 	        : Prototype.K
        }
    },
    unloadForm : function(form){
      form = $(form);
      if(!FormKit.forms[form.identify()]) {return;} //if not an existing registered form return
        var op = FormKit.option('cachable freeze', form.identify());
        //unregister all  events
        //TODO
        //delete the cache
        if (op.freeze)
            FormKit.forms[form.identify()].dom = {fields:{}}; // TODO: watch this for mem leaks
        /*var hashForm = $H(FormKit.forms);

        hashForm.unset(form.identify());
        FormKit.forms = hashForm.toObject();*/
    },
    reloadForm : function(form, contentToExplore){
      form = $(form);
      FormKit.unloadForm(form);

      if(FormKit.forms[form.identify()]) {
          var op = FormKit.option('cachable', form.identify());
          if(op.cachable) {FormKit.Cachable.init(form);}
          if (FormKit._registeredExtension) {
              FormKit._registeredExtension.each(function(namespace) {
              if ('reloadForm' in namespace)
                  namespace.reloadForm(form, contentToExplore);
              })
          }
      } else {
          FormKit.load(form);
      }

      return form;

    },
    reload : function(contentToExplore) {
        var res = [];
      if (contentToExplore.tagName === "FORM")
          contentToExplore = contentToExplore.up("div");

      var tmpMap = $H(FormKit.forms);
      if (tmpMap.keys().size() > 0) {
          formToRemove = new Array();
          tmpMap.each(function(pair) {
              var k = pair.key;
              if (!$(k)) {
                  formToRemove.push(k);
              }
              if (contentToExplore && contentToExplore.down('form#' + k)) {
                  FormKit.reloadForm(k, contentToExplore);
              }
          });

          $A(formToRemove).each(function(k) {
              tmpMap.unset(k);
          });
          FormKit.forms = tmpMap.toObject();

          $A(contentToExplore.select('form')).select(function(form) {
              return !FormKit.forms[form.identify()];
          }).each(function(form) {
              var returnForm = FormKit.reloadForm(form, contentToExplore);
              if (returnForm) {
                  res.push(returnForm);
              }
          });
      } else {
          var returnForms = FormKit.load(contentToExplore)
          if (Object.isArray(returnForms)) {
              res = returnForms;
          }
      }

      return res;

    },
    load : function(contentToExplore) {
        var res = [];
        if(FormKit.options.autoLoad) {
            if(FormKit.options.cachable) {
                $A(FormKit.options.cachableSelector).each(function(s){
                    (!Object.isElement(contentToExplore) ? $$(s) : (contentToExplore.tagName === 'FORM' ? (contentToExplore.hasClassName(s.indexOf('.') != -1 ? s.substring(s.indexOf('.')+1) : s) ? [contentToExplore] : [])  : contentToExplore.select(s))).each(function(t) {
                        FormKit.Cachable.init(t);

                        res.push(t);
                    });
                });
            }
        }

        return res;
    }
});

FormKit.Cachable = {
    init : function(elm, options){
        var form = $(elm);
        if(form.tagName !== "FORM") {return;}
        FormKit.register(form,Object.extend(options || {},{cachable:true}));
        FormKit.loadFields(form);
    },
    resetForm : function(form, resetFunction) {
        form = $(form);
        FormKit.resetFields(form, resetFunction);
    },
    update : function(form, filter) {
    	form = $(form);
    	var formElements = $(form.getElements());
        if (!formElements)
            return false;

        if (Object.isFunction(filter)) {
            formElements = formElements.select(filter);
        }
        
        formElements.each(function(element) {
        	if (element.type !== 'submit' && element.tagName !== 'BUTTON' && element.readAttribute('disabled') !== 'disabled') {
        		FormKit.setCachedValue(form, element);
        	}
        });
    },
    // check modification for filtered elements in form 
    checkModification : function(form, filter) {
    	form = $(form);
        var formElements = $(form.getElements());
        if (!formElements)
            return false;

        if (Object.isFunction(filter)) {
            formElements = formElements.select(filter);
        }

        var result = false;
        formElements.each(function(element) {
            if (element.type !== 'submit' && element.tagName !== 'BUTTON' && element.readAttribute('disabled') !== 'disabled') {

                var cachedElementValue = FormKit.getCachedValue(form, element);
                
                if (!cachedElementValue || !Object.isArray(cachedElementValue)) {
                    if (!cachedElementValue) {
                        cachedElementValue = '';
                    }

                    var elementValue = element.getValue();
                    
                    // Bug sul folder note in versione HTML
                    // uso la class encode_output per differenziare i casi in cui,
                    // va fatto l'unescapeHTML per confrontare i due valori, attuale e originale.
                    // nei campi noteInfo l'encode_output e' impostato con false
                    // quindi non c'è la classe encode_output,
                    // in tutti gli altri campi encode_output ha il valore di default, true,
                    // e il confronto e' fatto senza l'unescapeHTML
                    if(elementValue && !element.hasClassName("encode_output")) {
                        elementValue = elementValue.unescapeHTML();
                    }
                    if (cachedElementValue && !element.hasClassName("encode_output")) {
                        cachedElementValue = cachedElementValue.unescapeHTML();
                    }
                    
                     // For checkbox FormKit.getCachedValue(form, element); return value only for the input checked, so
                    if(element.type === 'checkbox') {
                    	if ((element.checked && cachedElementValue == '') || (!element.checked && cachedElementValue)) {
                    		if (!result) {
    	                        result = $H({});
    	                    }
    	                    result.set(element.readAttribute('name'), elementValue);
                    	}
                    } else {
                    	if (cachedElementValue !== elementValue) {
                        	if (!result) {
                            	result = $H({});
                        	}
                        	result.set(element.readAttribute('name'), elementValue);
                        }
                    }
                }
            }
        });

        if (result && Object.isHash(result)) {
            result = result.toObject();
        }
        
        return result;
    }
}

document.observe("dom:loaded", FormKit.load);