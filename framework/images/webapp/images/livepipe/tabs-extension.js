/**
* @author Ryan Johnson <http://syntacticx.com/>
* @copyright 2008 PersonalGrid Corporation <http://personalgrid.com/>
* @package LivePipe UI
* @license MIT
* @url http://livepipe.net/control/tabs
* @require prototype.js, livepipe.js
*/

if(typeof(Control.Tabs) == "undefined")
  throw "Control.Tabs requires Prototype to be loaded.";

Object.extend(Control.Tabs, {
    reload : function() {
        if (Control.Tabs.instances) {
            $A(Control.Tabs.instances).each(function(instance) {
                instance.reloadInstance();
                instance.refreshInstance();
            });
        }
    },
    clearInstance : function(position) {
        if (Control.Tabs.instances && Control.Tabs.instances.size() > 0) {
            var instanceToRemove = null;
            if (position && !Object.isNumber(position)) {
                instanceToRemove = position;
            } else {
                position = position || 0;
            }
            if (!instanceToRemove && position && position < Control.Tabs.instances.size()) {
                instanceToRemove = Control.Tabs.instances[position]
            }

            if (instanceToRemove) {
                instanceToRemove.removeObservers();

                Control.Tabs.instances = Control.Tabs.instances.without(instanceToRemove);
            }
        }
    },
    clearInstances : function() {
        Control.Tabs.instances = [];
    },
    refreshInstances : function() {
        if (Control.Tabs.instances) {
            var newInstanceList = new Array();
            $A(Control.Tabs.instances).each(function(instance) {
                if (instance.refreshInstance()) {
                    newInstanceList.push(instance);
                }
            });
            Control.Tabs.instances = newInstanceList;
       }
    },
    massiveRegisterEvent : function(newContent, checkRegistration, callBack, functionKey, checkExecutability, forceRegistration, dispatchEvent) {
        res = false;
        if(!Object.isElement($(newContent))) {
            newContent = $(document.body);
        }
        if (Object.isArray(Control.Tabs.instances) && Control.Tabs.instances.size() > 0) {
            var tabInstanceList = $A([]);

            Control.Tabs.instances.each(function(tabInstance) {
                var tabSize = tabInstance.getLinkSize();
                for(var i = 0 ; i < tabSize; i++ ) {
                    var container = tabInstance.getContainerAtIndex(i);
                    if (forceRegistration || (Object.isElement(container) && newContent.descendants().indexOf(container)/* && Object.isElement(newContent.down('#' + $(container).identify()))*/)) {
                        var contentToElaborate = container;

                        if (Object.isFunction(checkRegistration)) {
                            contentToElaborate = checkRegistration(container);
                        }
                        tabInstance.registerEvent(container.identify(), callBack.curry(contentToElaborate), functionKey, checkExecutability);

                        if (tabInstanceList.indexOf(tabInstance) == -1)
                            tabInstanceList.push(tabInstance);
                    }
                }

            });

			if (tabInstanceList.size() > 0) {
				res = true;
			}
			if(dispatchEvent) {
	            tabInstanceList.each(function(tabInstance) {
	               // res = true;
	
	                //serve perchè la prima volta deve caricare i menu
	                if (Object.isElement(tabInstance.getActiveContainer()))
	                    tabInstance.dispatchEvent(tabInstance.getActiveContainer().identify(), functionKey, newContent);
	            });
			}
        }

        return res;
    }
});

Control.Tabs.addMethods(
{
      getId : function() {
          return this.id;
      },
      getActiveContainer: function(instance){
          var currentInstance = instance ? instance : this;
          if (currentInstance) {
              return currentInstance.activeContainer;
              }
          return null;
      },
      getActiveContainerIndex : function(instance) {
          var currentInstance = instance ? instance : this;
          if (currentInstance && currentInstance.activeLink) {
              return currentInstance.links.indexOf(currentInstance.activeLink)
          }
          return -1;
      },
      getContainerIndex : function(id, instance) {
          var res = -1;
          var currentInstance = instance ? instance : this;
          if (id && currentInstance && currentInstance.links.length > 0) {
              currentInstance.links.each(function(link, index) {
                  if (link.key === id)
                      res = index;
                  return link.key === id;
              });
          }
          return res;
      },
      getContainerAtIndex: function(i, instance){
          var currentInstance = instance ? instance : this;
          if (currentInstance && i < currentInstance.containers.size()) {
            if(currentInstance.getLinkAtIndex(i)) {
                return currentInstance.containers.get(currentInstance.getLinkAtIndex(i).key);
            }
          }
          return null;
      },
      getLink : function(id, instance) {
          var currentInstance = instance ? instance : this;
          if (id && currentInstance && currentInstance.links.length > 0) {
              return currentInstance.links.find(function(link) {
                  return link.key === id;
              });
          }
          return null;
      },
      getLinkSize : function(instance) {
          var currentInstance = instance ? instance : this;
          if (currentInstance)
              return currentInstance.links ? currentInstance.links.length : 0;
          return 0;
      },
      getLinkAtIndex : function(i, instance) {
          var currentInstance = instance ? instance : this;
          if (currentInstance)
              return currentInstance.links && i < currentInstance.links.length ? currentInstance.links[i] : null;
          return null;
      },
      disableLink : function(i, instance) {
          var currentInstance = instance ? instance : this;
          if (currentInstance) {
              var link = currentInstance.getLinkAtIndex(i);
              if (!Object.isUndefined(link)) {
                  Object.extend(link, {saveUrl : link.readAttribute('href')});
                  if (Object.isFunction(link['onclick']) && link['onclick'] !== Prototype.emptyFunction && link['onclick'] !== Prototype.K) {
                      Object.extend(link, {freezeOnClick : link['onclick']});
                  }
                  Event.stopObserving(link, 'click');
                  link['onclick'] = Prototype.emptyFunction;

                  Event.observe(link, 'click', function(e) {
                      Event.stop(e);
                      return false;
                  });
              }
          }
      },
      enableLink : function(i, instance) {
          var currentInstance = instance ? instance : this;
          if (currentInstance) {
              var link = currentInstance.getLinkAtIndex(i);
              if (!Object.isUndefined(link)) {
                  if ('saveUrl' in $(link)) {
                      link.writeAttribute('href', $(link).saveUrl);
                  }
                  if ('freezeOnClick' in $(link)) {
                      Event.stopObserving(link, 'click');

                      link['onclick'] = $(link).freezeOnClick;
                  }
              }
          }
      },
      reloadInstance : function(instance) {
          var currentInstance = instance ? instance : this;
          if (currentInstance && currentInstance.links && currentInstance.links.length) {
              var activeTab = currentInstance.getActiveContainer();
              if (activeTab)
                  activeTab = $(activeTab).identify();
              currentInstance.containers = $H({});
              $A(currentInstance.links).each(function(link) {
                  var container = $(link.key);
                  if (container) {
                      currentInstance.containers.set(link.key, container);
                  }
              });
              currentInstance.setActiveTab(activeTab);
          }
      },
      reloadContainer : function(id, instance) {
          var currentInstance = instance ? instance : this;
          var activeTab = currentInstance.getActiveContainer();
          if (activeTab)
              activeTab = $(activeTab).identify();
          var container = $(id)
          if (container)
              currentInstance.containers.set(id, container);

          if (id === activeTab){
            currentInstance.setActiveTab(activeTab);
            }
          else
            currentInstance.options.hideFunction(container)
      },
      refreshInstance : function(instance) {
          var currentInstance = instance ? instance : this;

          if (currentInstance.containers) {
              var newLinks = [];
              var newContainers = $H({});

              currentInstance.links.each(function(link) {

                  var key = link.key;
                  var newLink = (typeof(currentInstance.options.linkSelector == 'string')
                            ? $(document.body).select(currentInstance.options.linkSelector)
                            : currentInstance.options.linkSelector($(document.body))
                          ).findAll(function(element) {
                              return (/^#/).exec((Prototype.Browser.WebKit ? decodeURIComponent(element.href) : element.href).replace(window.location.href.split('#')[0],''));
                          }).find(function(element) {
                              return (Prototype.Browser.WebKit ? decodeURIComponent(element.href) : element.href).replace(window.location.href.split('#')[0],'') == '#' + key;
                          });
                  if (newLink) {

                      newLink.key = key;
                      newLinks.push(newLink);

                      var container = $(key);
                      if (Object.isElement(container)) {
                          newContainers.set(key, container);
                      }
                  }

              });

              currentInstance.links = newLinks;
              currentInstance.containers = newContainers;
              currentInstance.deregisterEvent();

              if (currentInstance.containers.keys().size() == 0) {
                return false;
              }
          }

          return true;
      },
      removeObservers : function(instance) {
          var currentInstance = instance ? instance : this;

          if (currentInstance.links && currentInstance.links.size() > 0) {
              $A(currentInstance.links).each(function(link) {
                  Event.stopObserving(link, 'click');
              });
          }
      },
      
      isMassiveRegistered : function(functionKey) {
    	  res = false;
    	  
    	  if (Object.isHash(this.registeredEvent)) {
    		  this.registeredEvent.each(function(pair) {
    			  if (Object.isHash(pair.value)) {
    				  res = pair.value.get(functionKey);
    				  if (!res)
    					  throw $break;
    			  }
    		  });
    	  }
    	  
    	  return res;
      },

    // registra una funzione per un container
    registerEvent : function(container, funzione, functionKey, checkExecutability) {
        if (!Object.isHash(this.registeredEvent)) {
            this.registeredEvent= $H({});
        }
        if (Object.isFunction(funzione)) {
        	var functionMap = $H(this.registeredEvent.get(container));
        	if (functionMap && functionMap.keys().size() > 0) {
        		functionMap.set(functionKey, funzione);
        	} else {
        		functionMap = $H({});
        		functionMap.set(functionKey, funzione);
        	}
        	
        	if (Object.isFunction(checkExecutability)) {
        	    Object.extend(funzione, {'checkExecutability' : checkExecutability});
    		}
        	this.registeredEvent.set(container, functionMap);
        }
    },

    // deregistra tutte le funzioni
    deregisterEvent : function(container) {
    	
        if (Object.isHash(this.registeredEvent)) {
	        if(this.registeredEvent.size() > 0) {
	        	if (!container) {
	                this.registeredEvent.keys().each(function(key) {
	                    this.registeredEvent.unset(key);
	                    //throw $break;
	                }.bind(this));
	            } else {
	            	var functionMap = $H(this.registeredEvent.get(container));
	            	if (functionMap) {
	            		functionMap.keys().each(function(key) {
	            			functionMap.unset(key);
	            		});
	            	}
	            	this.registeredEvent.set(container, functionMap);
	            }
        	}
    	}
    },

    // richiama la funzion eregistrata per un container
    dispatchEvent : function(container, eventName, contentToUpdate){
    	var thisArgs = arguments;
        if (Object.isHash(this.registeredEvent)) {
            if(this.registeredEvent.get(container)) {
                var functionMap = $H(this.registeredEvent.get(container));
                var eventRegistered;
            	if (functionMap && functionMap.keys().size() > 0) {
	            	if (eventName) {
	            		eventRegistered = functionMap.get(eventName);
	            		if (Object.isFunction(eventRegistered)) {
		            		var executability = true;
	            			if (Object.isFunction(eventRegistered.checkExecutability)) {
	            				if (thisArgs.length > 2) {
	            					executability = eventRegistered.checkExecutability.apply(this, $A(thisArgs).slice(3));
	            				}
	            			}
	            			if (executability) {
	            				// in alcuni casi la function ha gia un primo argomento impostato, con un precedente curry
	            				eventRegistered.call(this, contentToUpdate);
	            			}
	            		}
	            	} else {
	            		functionMap.each(function(pair) {
	            			var executability = true;
	            			if (Object.isFunction(pair.value.checkExecutability)) {
	            				if (thisArgs.length > 2) {
	            					executability = pair.value.checkExecutability.apply(this, $A(thisArgs).slice(3));
	            				}
	            			}
	            			if (executability) {
	            				// in alcuni casi la function ha gia un primo argomento impostato, con un precedente curry 
	            				eventRegistered = pair.value;
	            				eventRegistered.call(this, contentToUpdate);
	            			}
	            		});
	            	}
            	}
            }
        }
    }
});