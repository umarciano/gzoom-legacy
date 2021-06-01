UpdateAreaResponder = {
  onComplete: function(request, response) {
      UpdateAreaResponder._onEventCallBack(request, response);
  },
  onException: function(request, response) {
      UpdateAreaResponder._onEventCallBack(request, response);
  },

  _onEventCallBack : function(request, response) {
      if (!request.options.contentToUpdate) {
          return;
      }

      var responseText = response.responseText;
      if (responseText) {
          UpdateAreaResponder._updateElement(response, request.options.contentToUpdate, request.options.preBeforeLoadElaborateContent, request.options.preLoadElaborateContent, request.options.postLoadElaborateContent, request.options.messageContext)
      }
  },

  _insertTmpElement : function(responseText) {
      var tmpElement = new Element('div', {style : 'position:absolute; top:-15000px', id : 'tmpElement'});
      tmpElement.update(responseText);

      document.body.insert(tmpElement);

      return tmpElement;
  },
  
  _preBeforeLoadElaborateContent : function(contentToUpdate) {
	  return contentToUpdate;
  },

  _preLoadElaborateContent : function(contentToUpdate, tmpElement) {
      Object.extend(tmpElement, {proceed : true});

      $(contentToUpdate).childElements().each(function(element) {
           element.remove();
      });

      return tmpElement;
  },

  _postLoadElaborateContent : function(contentToUpdate, tmpElement) {
      return tmpElement;
  },

  loginExecuted : false,

  _updateElement : function(response, contentToUpdate, preBeforeLoadElaborateContent, preLoadElaborateContent, postLoadElaborateContent, messageContext) {
      var data = response.responseJSON;
      if (!data) {
    	  try {
    		  data = response.responseText.evalJSON(true);
    	  } catch(e) {}
      }
      var responseText = response.responseText;

      if (responseText.indexOf("loginform") != -1) {
          UpdateAreaResponder.Responders.dispatch('onLogin', contentToUpdate, {'response' : response, 'contentToUpdate' : contentToUpdate});

          UpdateAreaResponder.loginExecuted = true;
      } else {
          if (UpdateAreaResponder.loginExecuted) {
              UpdateAreaResponder.Responders.dispatch('onLoginExecuted', contentToUpdate, {'response' : response, 'afterHideModal' : UpdateAreaResponder.elaborateNewContent.curry(data, contentToUpdate, responseText, preBeforeLoadElaborateContent, preLoadElaborateContent, postLoadElaborateContent, messageContext)});
            //Utils.hideModalBox();
            UpdateAreaResponder.loginExecuted = false;
          } else {
              UpdateAreaResponder.elaborateNewContent(data, contentToUpdate, responseText, preBeforeLoadElaborateContent, preLoadElaborateContent, postLoadElaborateContent, messageContext);
          }


      }
  },

  elaborateNewContent : function(data, contentToUpdate, responseText, preBeforeLoadElaborateContent, preLoadElaborateContent, postLoadElaborateContent, messageContext) {
    if ($(contentToUpdate)) {
            if (messageContext && data && !data["messageContext"]) {
                data["messageContext"] = messageContext;
            }
            if (!data || (data && data._ERROR_MESSAGE_LIST_ == undefined && data._ERROR_MESSAGE_ == undefined)) {
              //Fare check dei register validi ed eliminare quelli non validi
              UpdateAreaResponder.Responders.checkResponders();
            
              preBeforeLoadElaborateContent = preBeforeLoadElaborateContent || UpdateAreaResponder._preBeforeLoadElaborateContent;
              contentToUpdate = preBeforeLoadElaborateContent(contentToUpdate);
              
              var res = UpdateAreaResponder.Responders.dispatch('onBeforeLoad', contentToUpdate);

              if (res) {
                  preLoadElaborateContent = preLoadElaborateContent || UpdateAreaResponder._preLoadElaborateContent;
                  postLoadElaborateContent = postLoadElaborateContent || UpdateAreaResponder._postLoadElaborateContent;

                  var tmpElement = UpdateAreaResponder._insertTmpElement(responseText);
	              if (Object.isElement(tmpElement.down())) {
	            	  var newTmpElement = preLoadElaborateContent(contentToUpdate, tmpElement.down());
	
	                  UpdateAreaResponder.Responders.dispatch('onLoad', newTmpElement, {'contentToUpdate': contentToUpdate});
	                  newTmpElement = postLoadElaborateContent(contentToUpdate, newTmpElement);
	                  
	                  if ('proceed' in $(newTmpElement) && $(newTmpElement).proceed) {
	                      $A(tmpElement.childElements()).each(function(element) {
	                          $(contentToUpdate).insert(element);
	                      });
	                  }    // Bug 36 aggiunto condizione Object.isElement($(tmpElement.childNodes[0].nodeValue))
                  } else if (tmpElement.childNodes.length > 0 && tmpElement.childNodes[0].nodeType === 3 && Object.isElement($(tmpElement.childNodes[0].nodeValue))){
                	  $(contentToUpdate).update(tmpElement.childNodes[0].nodeValue);
                  }

                  if (tmpElement)
                	  tmpElement.remove();

                  UpdateAreaResponder.Responders.dispatch('onAfterLoad', $(contentToUpdate));
              }

              //Commentato perche sovrascrive un eventuale messaggio precedente (ad esempio messaggio di failure)
              //modal_box_messages.onAjaxLoad(data, Prototype.K);
              //Toolbar.enableToolbar();
            } else {
                modal_box_messages.onAjaxLoad(data, Prototype.K);
                Toolbar.enableToolbar();
            }
      } else {
          if (data._ERROR_MESSAGE_LIST_ == undefined && data._ERROR_MESSAGE_ == undefined) {
              var res = UpdateAreaResponder.Responders.dispatch('onBeforeLoad', contentToUpdate, {'data' : data});

              if (res) {
                //Bug 4903 punto 3 modificato anche messages.js
                  //UpdateAreaResponder.executeAjaxUpdateAreas(contentToUpdate, data);
                  
                  modal_box_messages.onAjaxLoad(data, UpdateAreaResponder.executeAjaxUpdateAreas.curry(contentToUpdate, data));
                  //modal_box_messages.onAjaxLoad(data, Prototype.K);
              }
         } else {
              modal_box_messages.onAjaxLoad(data, Prototype.K)
              Toolbar.enableToolbar();
          }
      }
  },

  executeAjaxUpdateAreas : function(contentToUpdate, data, elaborateContentFunction) {
      if (Object.isString(contentToUpdate) && !Object.isElement($(contentToUpdate))) {
          if (!Object.isUndefined(data.auxiliaryParameters)) {
              var parameters = $H(data.auxiliaryParameters.toQueryParams('|'));
              if (parameters) {
                  var areaArray = contentToUpdate.split(",");
                  var numAreas = parseInt(areaArray.length / 3);
                  for (var i = 0; i < numAreas * 3; i = i + 3) {
                	  var newParameters = $H({});
                      if (areaArray[i+2]);
                      	newParameters = parameters.merge($H(areaArray[i+2].toQueryParams()));
                      areaArray[i+2] = newParameters.toQueryString();
                  }
                  contentToUpdate = areaArray.join(',');
              }
          }

          if (Object.isFunction(elaborateContentFunction)) {
              contentToUpdate = elaborateContentFunction.apply(this, [contentToUpdate, data])
          }

          ajaxUpdateAreas(contentToUpdate, {messageContext : data["messageContext"]});
      }
  }
}
Ajax.Responders.register(UpdateAreaResponder);


UpdateAreaResponder.Responders = {
  responders: $H({}),
  responderList: $A([]),

  _each: function(iterator) {
    //this.responders._each(iterator);
	this.responderList._each(iterator);
  },
  
  checkResponders : function() {
    this.responders.each(function(pair) {
        
        if(Object.isFunction(pair.value['unLoadCondition']) && (pair.value['unLoadCondition']() === true)) {
            UpdateAreaResponder.Responders.unregister(pair, pair.key);
        }
    });
  },

  register: function(responder, name, index) {
      if (name) {
          UpdateAreaResponder.Responders.unregister(responder, name);
          this.responders.unset(name);
          this.responders.set(name, responder);
          
          if (Object.isUndefined(index) || index === null) {
        	  this.responderList.push(responder);
          } else if (index > 0) {
        	  var currentSize = this.responderList.size();
        	  if (index >= currentSize) {
        		  this.responderList.push(responder);
        	  } 
          } else if (index === 0) {
    		  this.responderList = $A([responder].concat(this.responderList));
    	  } else {
    		  var tmpList = this.responderList.slice(0, index);
    		  tmpList.push(responder);
    		  tmpList.concat(this.responderList.slice(index))
    		  this.responderList = $A(tmpList);
    	  }
          
          
      } else if (!this.responders.values().include(responder)){
          var counter = 1;
          do { name = counter++ } while (this.responders.get(name));
          this.responders.set(name, responder);
          this.responderList.push(responder);
      }
  },

  unregister: function(responder, name) {
	  var value = null;
      if (name) {
          if (this.responders.get(name) && Object.isFunction(this.responders.get(name)['unLoad'])) {
              this.responders.get(name).unLoad();
          }
          value = this.responders.unset(name);
      } else if (this.responders.values().include(responder)) {
          this.responders.keys().each(function(key) {
              var r = this.responders.get(key);
              if (r === responder) {
                  if (Object.isFunction(r['unLoad'])) {
                      this.responders.get(key).unLoad();
                  }
                  value = this.responders.unset(key);
                  throw $break;
              }
          }.bind(this));
      }
      
      if (!Object.isUndefined(value) && value !== null) {
    	  this.responderList = $A(this.responderList.without(value));
      }

  },

  unregisterAll: function() {
    var oldResponders = this.responders;
    this.responders = $H({});
    this.responderList = $A([]);
    return oldResponders;
  },

  registerAll: function(responders) {
    this.responders = $H(responders);
    this.responderList = $A(responders.values());
  },

  indexOf: function(responder) {
    return this.responderList.indexOf(responder);
  },

  dispatch: function(callback, newContent, options) {
      var res = true;
    /*this.each(function(pair, index) {
    	var responder = pair.value;
      if (Object.isFunction(responder[callback])) {
        try {
          var localRes = responder[callback].apply(responder, [newContent, options]);
          if (Object.isUndefined(localRes)) {
              localRes = true;
          }
          res = localRes && res;
        } catch (e) { }
      }
    });*/
      
      this.each(function(value, index) {
      	var responder = value;
        if (Object.isFunction(responder[callback])) {
          try {
            var localRes = responder[callback].apply(responder, [newContent, options]);
            if (Object.isUndefined(localRes)) {
                localRes = true;
            }
            res = localRes && res;
          } catch (e) { }
        }
      });

    return res;
  }
};
Object.extend(UpdateAreaResponder.Responders, Enumerable);


AjaxUpdateFunction = {
    load: function() {
        ajaxUpdateArea = ajaxUpdateArea.wrap(
            function(proceed, areaId, target, targetParams, options) {
                var params = null;
                if (Object.isString(targetParams)) {
                    params = $H(targetParams.toQueryParams());
                } else {
                    params = $H(targetParams);
                }
                if (!params.get("ajaxCall")) {
                    params.set("ajaxCall", "Y");
                }
//                if (!params.get("backAreaId"))
                    params.set('backAreaId', areaId);

                options = Object.extend({
                    parameters: params.toObject(),
                    content : areaId,
                    contentToUpdate : areaId }, options || {});
                new Ajax.Request(target, options);
            }
        );

        ajaxUpdateAreas = ajaxUpdateAreas.wrap(
            function(proceed, areaCsvString, options) {
                var areaArray = areaCsvString.split(",");
                var numAreas = parseInt(areaArray.length / 3);
                for (var i = 0; i < numAreas * 3; i = i + 3) {
                    
                	var params = areaArray[i + 2].replace(/\+/g , '%20');
                	params = $H(params.toQueryParams());
                    if (!params.get("ajaxCall")) {
                        params.set("ajaxCall", "Y");
                    }
                    if (!params.get("forcedBackAreaId")) {
                        params.set('backAreaId', areaArray[i]);
                    } else {
                        params.set('backAreaId', params.get("forcedBackAreaId"));
                        params.unset("forcedBackAreaId");
                    }

                    var currentOptions = Object.extend({parameters: params.toObject(), content : areaArray[i], contentToUpdate : areaArray[i] }, options || {});

                    new Ajax.Request(areaArray[i + 1], currentOptions);
                }
            }
        );
        ajaxSubmitFormUpdateAreas = ajaxSubmitFormUpdateAreas.wrap(
            function(proceed, form, updater, areaCsvString, options) {
                //submitFormDisableSubmits($(form));
                updateFunction = function(transport) {
                    UpdateAreaResponder._updateElement(transport, updater || areaCsvString);
                }

                if (updater) {
                    if (areaCsvString) {
                        var areaArray = areaCsvString.split(",");
                        form = $(form);

                        var currentAction = form.readAttribute('action');

                        if (areaArray && areaArray.length > 1) {
                            if (currentAction) {
                                var parameterObj = null;
                                if (currentAction.indexOf('?') != -1)
                                    parameterObj = currentAction.toQueryParams();
                                if (parameterObj) {
                                    if (areaArray.length > 2 && areaArray[2]) {
                                        var otherParameterObj = null;
                                        if (areaArray[2].indexOf('?') != -1)
                                            otherParameterObj = areaArray[2].toQueryParams();
                                        var h = $H({});
                                        $H(parameterObj).each(function(pair) {
                                            if (otherParameterObj && otherParameterObj[pair.key]) {
                                                h.set(pair.key, otherParameterObj[pair.key]);
                                            } else {
                                                h.set(pair.key, pair.value);
                                            }
                                        });
                                        parameterObj = h.toObject();

                                        areaArray[2] = $H(parameterObj).toQueryString();
                                    } else if (areaArray.length < 2){
                                        areaArray.push($H(parameterObj).toQueryString());
                                    } else if (areaArray.length > 2 && !areaArray[2]) {
                                        areaArray[2] = $H(parameterObj).toQueryString();
                                    }
                                }
                            }

                            currentAction = areaArray[1];
                            if (areaArray.length > 2 && areaArray[2]) {
                                if (currentAction.indexOf('?') == -1)
                                    currentAction += '?';
                                currentAction += areaArray[2];
                            }
                        }

                        form.action = currentAction;
                    }

                    var params = $H($(form).serialize(true));
                    if (!params.get("ajaxCall")) {
                        params.set("ajaxCall", "Y");
                    }

                     if (!params.get("forcedBackAreaId")) {
                            params.set('backAreaId', Object.isElement(updater) ? $(updater).identify() : updater);
                        } else {
                            params.set('backAreaId', params.get("forcedBackAreaId"));
                            params.unset("forcedBackAreaId");
                        }

                    options = Object.extend({
                        parameters: params.toObject(),
                        content:updater, contentToUpdate : updater }, options || {});
                    new Ajax.Request($(form).action, options);
                } else {
                    var params = $H($(form).serialize(true));
                    if (!params.get("ajaxCall")) {
                        params.set("ajaxCall", "Y");
                    }

                    options = Object.extend({
                        parameters: params.toObject(),
                        onComplete: updateFunction }, options || {});
                    new Ajax.Request($(form).action, options);
                }
            }
        );
        
        Autocompleter.Base.prototype.baseInitialize = Autocompleter.Base.prototype.baseInitialize.wrap(
        	function(proceed, element, update, options) {
        		element          = $(element)
			    this.element     = element; 
			    this.update      = $(update);  
			    this.hasFocus    = false; 
			    this.changed     = false; 
			    this.active      = false; 
			    this.index       = 0;     
			    this.entryCount  = 0;
			    this.oldElementValue = this.element.value;

			    if(this.setOptions)
			      this.setOptions(options);
			    else
			      this.options = options || { };
			
			    this.options.paramName    = this.options.paramName || this.element.name;
			    this.options.tokens       = this.options.tokens || [];
			    this.options.frequency    = this.options.frequency || 0.4;
			    this.options.minChars     = this.options.minChars || 1;
			    this.options.onShow       = this.options.onShow || 
			      function(element, update){ 
			        if(!update.style.position || update.style.position=='absolute') {
			          update.style.position = 'absolute';
			          Position.clone(element, update, {
			            setHeight: false, 
			            offsetTop: element.offsetHeight
			          });
			        }
			        Effect.Appear(update,{duration:0.15});
			      };
			    this.options.onHide = this.options.onHide || 
			      function(element, update){ new Effect.Fade(update,{duration:0.15}) };
			
			    if(typeof(this.options.tokens) == 'string') 
			      this.options.tokens = new Array(this.options.tokens);
			    // Force carriage returns as token delimiters anyway
			    if (!this.options.tokens.include('\n'))
			      this.options.tokens.push('\n');
			
			    this.observer = null;
			    
			    this.element.setAttribute('autocomplete','off');
			
			    Element.hide(this.update);

			   // Event.observe(this.element, 'blur', this.onBlur.bindAsEventListener(this));
			  	Event.observe(document.body, 'click', this.onBlur.bindAsEventListener(this));
			    Event.observe(this.element, 'keydown', this.onKeyPress.bindAsEventListener(this));
        	}
        );
    }
}


document.observe("dom:loaded", AjaxUpdateFunction.load);