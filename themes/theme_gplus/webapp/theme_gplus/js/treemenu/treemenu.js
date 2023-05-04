/** --------------------------------------------------------------------------------
 * Classe gestione form submit (Prototype based) 
 * ---------------------------------------------------------------------------------
 */

var SubmitHandler = Class.create({ });

SubmitHandler.postForm = function(form) {
	form = $(form);
	//get all inputs
	var params = $H(form.serialize(true));
	
	if (params.get("menuItemInNewWindow") == "Y") {
		form.writeAttribute("target", "_blank");
		form.submit();
	}
	else {
	    //read action
		var action = form.readAttribute("action");
		params.set("ajaxCall", "Y"); //TODO: Inserire nei ContentAttributes
		// params.set("ajaxRequest", "Y"); e' stato inserito nei ContentAttributes
        params.set("backAreaId", "main-section-container");
		//Pulisco i cookie prima della request
		if (typeof(CleanCookie) != "undefined") {
			CleanCookie.load();	
		}
		new Ajax.Request(action, {parameters : params.toObject(), evalScripts : true, contentToUpdate : "content-main-section", content : "content-main-section"});
	}
}

SubmitHandler.postFormBySelector = function(selector) {
	form = $$(selector);
	if (form) {
		SubmitHandler.postForm(form[0]);
	}
}

/** --------------------------------------------------------------------------------
* Classe gestione History links
* ---------------------------------------------------------------------------------
*/
var HistoryHandler = Class.create({
	/**
	 * Class constructor
	 */
	initialize: function(containerSelector) {
		this._container = $$(containerSelector);
		//create link list 
		this._linkList = new Element("ul");
		//Max link option
		this._maxLinks = 5;
		//add to container 
		this._container[0].insert(this._linkList);
	},
	
	/**
	 * Add new Element into link list
	 * new Element is a link to form to submit 
	 */
	addLink: function(formSelector, title, toolTip) {
		//check if already into list
		if (this.checkLink(formSelector)) {
			return;
		}
		
		//build new preferred element element 
		var li = new Element("li");
		var div = new Element("div");
		div.innerHTML = title;
		//Setto classe per item titolo
		div.addClassName("treemenu-item-facility");
		//agiungo attributo Id da linkare per submit
		div.writeAttribute("form-selector" , formSelector);
		// testo da visualizzare come toolTip
		if (toolTip != undefined) {
			div.writeAttribute("title", toolTip);
		}			
		//Build elemento
		Element.insert(li, div);
		//add on click event on li 
		li.observe("click", function(event) {
			var div = $(this).down("div");
			SubmitHandler.postFormBySelector(div.readAttribute("form-selector"));
		});
		
		//Add new li into ul
		var size = this._linkList.select("li").size();
		if (size >= this._maxLinks) {
			var toRemove = this._linkList.down("li");
			if (toRemove) 
				toRemove.remove();
		}
		this._linkList.insert(li);
	},
	
	/**
	 * Check if a link element is in the list yet
	 */
	checkLink: function(formSelector) {
		return this._linkList.select("li").find( function(item) {
			var div = item.down("div");
			return (formSelector == div.readAttribute("form-selector"));
		}.bind(this));		
	}
});


/** --------------------------------------------------------------------------------
 * Classe gestione preferiti (Prototype based)
 * ---------------------------------------------------------------------------------
 */
var PreferredHandler = Class.create({
	/**
	 * Class constructor
	 */
	initialize: function(containerSelector) {
		this._container = $$(containerSelector);
		//create link list 
		this._linkList = new Element("ul");
		//add to container 
		this._container[0].insert(this._linkList);
		//load preferred from server
		this.loadPreferred();
	},
	
	/**
	 * Add new Element into link list
	 * new Element is a link to form to submit 
	 */
	addLink: function(formSelector, title, toolTip) {
		//build new preferred element element 
		var li = new Element("li");

		//Aggiungo div per immagine/testo pre delete dell'item
		var deleteDiv = new Element("div");
		deleteDiv.addClassName("treemenu-item-facility-delete");
		deleteDiv.innerHTML = "-";
		//add azione di cancellazione
		deleteDiv.observe("click", function(e) {
			Event.stop(e);
			var li = $(e.target).up("li");
			li.remove();
			this.savePreferred();
		}.bind(this));
		Element.insert(li, deleteDiv);
		
		//Aggiungo div principale voce
		var div = new Element("div");
		//testo visibile
		div.innerHTML = title;
		//Setto classe per item titolo
		div.addClassName("treemenu-item-facility");
		//aggiungo attributo Id da linkare per submit
		div.writeAttribute("form-selector" , formSelector);
		// testo da visualizzare come toolTip
		if (toolTip != undefined) {
			div.writeAttribute("title", toolTip);
		}
		//Build elemento li
		Element.insert(li, div);
		
		//add on click event on li 
		div.observe("click", function(event) {
			SubmitHandler.postFormBySelector($(this).readAttribute("form-selector"));
		});
		
		//Add new li into ul
		this._linkList.insert(li);
	},
	
	
	/**
	 * Check if a link element is in the list yet
	 */
	checkLink: function(formSelector) {
		var list = this._linkList.select("li");
		return list.find( function(item) {
			var div = item.down("div.treemenu-item-facility");
			return (formSelector == div.readAttribute("form-selector"));
		}.bind(this));		
	},
	
	/**
	 * Build json style parameter
	 */ 
	menuToJson : function() {
		var res = $H( {});
		var liArray = $A( {});
		// read menu
		var liList = this._linkList.select("li");
		// collect items
		liList.each( function(item) {
			var div = item.down("div.treemenu-item-facility");
			if (div) {
				var itemMap = $H( {});
				itemMap.set("title", div.innerHTML);
				itemMap.set("formSelector", div.readAttribute("form-selector"));
				itemMap.set("toolTip", div.readAttribute("title"));
				liArray.push(itemMap);
			}
		});
		res.set("preferredList", liArray);
		return res.toJSON();
	}, 

	
	/**
	 * Json to Menu: rebuild a menu list by json parameter
	 */
	jsonToMenu: function(jsonString) {
		//transform json string to json object
		var obj = jsonString.evalJSON(true);
		if (obj && obj.preferredList) {
			//preferred list is an array of map
			var prefList = obj.preferredList;
			$A(prefList).each( function(listItem) {
				if (listItem.title && listItem.formSelector) {
					this.addLink(listItem.formSelector, listItem.title, listItem.toolTip);
				}
			}.bind(this));
		}
	},
	
	/**
	 * Action save preferred list
	 */
	savePreferred: function() {
		//gets li's array
		var menu = this.menuToJson();
		new Ajax.Request("saveTreeMenuPreferred", {parameters : {"preferences": menu}});
	},

	/**
	 * Action load preferred list
	 */
	loadPreferred: function() {
		new Ajax.Request("getTreeMenuPreferred", {
            evalJSON: 'force',
			onSuccess: function(transport) {
                var obj = transport.responseJSON;
                if (obj && obj.preferences) {
                	this.jsonToMenu(obj.preferences);
                }                	
			}.bind(this)
		});
	}
	
});


 /** --------------------------------------------------------------------------------
 * Gestione treemenu (main gzoom menu) 
 * Nota: per il corretto funzionamento deve essere caricato il js: resolve.conflict.js 
 * che risolve la convivenza di jQuery (instanziando la funzione $j) e Prototype
 * ---------------------------------------------------------------------------------
 */

var TreeMenuMgr = Class.create({ });

TreeMenuMgr.Responders = {
  responders : $H({}),
  
   _each: function(iterator) {
    this.responders._each(iterator);
  },
  
  register: function(responder, name) {
  	if (name) {
  		TreeMenuMgr.Responders.unregister(name);
        this.responders.unset(name);
        this.responders.set(name, responder);
    } else  {
    	if (!this.responders.values().include(responder)){
    		var counter = 1;
          	do { name = counter++ } while (this.responders.get(name));
          	this.responders.set(name, responder);
      	}
    }
  },
  
  unregister: function(responder, name) {
      if (name) {
          if (this.responders.get(name) && Object.isFunction(this.responders.get(name)['unLoad'])) {
              this.responders.get(name).unLoad();
          }
          this.responders.unset(name);
      } else if (this.responders.values().include(responder)) {
          this.responders.keys().each(function(key) {
              var r = this.responders.get(key);
              if (r === responder) {
                  if (Object.isFunction(r['unLoad'])) {
                      this.responders.get(key).unLoad();
                  }
                  this.responders.unset(key);
                  throw $break;
              }
          }.bind(this));
      }

  },
  
  unregisterAll: function() {
    var oldResponders = this.responders;
    this.responders = $H({});
    return oldResponders;
  },

  registerAll: function(responders) {
    this.responders = $H(responders);
  },
  
  indexOf: function(responder) {
    return this.responders.values().indexOf(responder);
  },
  
  dispatch: function(callback, module, options) {
  	var res = true;
    this.each(function(pair) {
        var responder = pair.value;
      	if (Object.isFunction(responder[callback])) {
        	try {
          		var localRes = responder[callback].apply(responder, [module, options]);
          		if (Object.isUndefined(localRes)) {
              		localRes = true;
          		}
          		res = localRes && res;
        		} catch (e) { }
      	}
    });

    return res;
  },
  
  getResponder: function(callback) {
  	this.callbackFunction = false;
  	this.each(function(pair) {
        var responder = pair.value;
  		this.callbackFunction = Object.isFunction(responder[callback]) ? responder[callback] : false;
  		if(Object.isFunction(this.callbackFunction)) {
  			return this.callbackFunction;
  		}
  	}.bind(this));
  	
  	return this.callbackFunction;
  }
};

Object.extend(TreeMenuMgr.Responders, Enumerable);

TreeMenuMgr.openModule = function(selectedModule) {
    var treeList = $j(document).find("div.treemenu-container table.treemenu-outer-table");
    //Init all container
    treeList.each( function() {
        var treeMenu = $j(this);

        treeMenu.showModule(selectedModule);
        openedModuleId = selectedModule;
        treeOpen = true;
    });
}
TreeMenuMgr.selectMenuItem = function(selectedItem) {
    var treeList = $j(document).find("div.treemenu-container table.treemenu-outer-table");
    //Init all container
    treeList.each( function() {
        var treeMenu = $j(this);

        treeMenu.find("tbody tr:visible.selected").toggleClass("selected");
        treeMenu.find("tbody tr:visible:#" + selectedItem).each( function() { $j(this).addClass("selected"); });
    });
}

/**
 * Load all tree in content or document
 */
TreeMenuMgr.loadTree = function(content, selectedModule, selectedItem) {
	//Find tree by container
	if (content) {
		var treeList = $j(content).find("div.treemenu-container table.treemenu-outer-table");
		//Init all container
		treeList.each( function() {
			TreeMenuMgr.initTree(this, selectedModule, selectedItem);
		});
	}
}

/**
 * Init single tree menu
 */
TreeMenuMgr.initTree = function(treeMenu, selectedModule, selectedItem) {
	 //Nota bene: treemenu é la treemenu-outer-table 
	 treeMenu = $j(treeMenu);
 
	 //find parents (container because treeView is table)
	 var container = treeMenu.parents("div.treemenu-container");
 	 
 	 //Id last module showed
 	 var openedModuleId = "";
 	 
 	 //all menu branches are open or closed?
 	 var treeOpen = true;
	 
	 //
	 //Controllo se sono passati modulo e item da selezionare quindi devo presentare albero aperto e selezionato
	 //
	 var preLoad = false;
	 if (selectedModule && selectedItem) {
		 preLoad = true;
	 }
	 
	 //
	 // Init it (wrappo tramite jquery da jquery.treeTable.js)
	 //
	 if (preLoad) {
		 treeMenu.treeTable({initialState: 'expanded', indent: 13});
		 treeMenu.showModule(selectedModule);
		 openedModuleId = selectedModule;
		 treeOpen = true;
		 //Setto selected l'item voluto
		 treeMenu.find("tbody tr:visible:#" + selectedItem).each( function() { $j(this).addClass("selected"); });
	 } else {
		 treeMenu.treeTable({initialState: 'collapsed', indent: 13});
		 // hide all menus parents
		 treeMenu.hideAll();		 
	 }
	 
	 //
	 //initially preferred and history are closed
	 //
	 var preferredArea = $j(container.find(".treemenu-body div.treemenu-body-slidearea-preferred"));
	 preferredArea.hide();
	 
	 var historyArea = $j(container.find(".treemenu-body div.treemenu-body-slidearea-history"));
	 historyArea.hide();
	 
     //istanza gestione preferiti
 	 var prefs = new PreferredHandler("div.treemenu-body-slidearea-preferred");
 	 
     //istanza gestione history
 	 var history = new HistoryHandler("div.treemenu-body-slidearea-history");
 	 
 	 //array dei moduli presenti nel menu
 	 var moduleArray = container.find("div.treemenu-upper-toolbar td");
 	 
	 //
	 // Attach click (open) events and tooltip on modules TDs
	 //
 	moduleArray.each( function() {		 
		 //get inner span element
		 var div = $j($j(this).find("div.menu-module")[0]);

		 //click event 
		 //if span has contentid attribute (see treeMenu.ftl)
		 var contentId = div.attr("contentid");
		 if (contentId) {
			 //set onclick event 
			 $j(this).click( function (event) {
				 //Tolgo selected a modulo precedente
				 moduleArray.each( function() {
					$j(this).removeClass("selected"); 
				 });
				 //aggiungo a target attuale
				 $j(event.target).addClass("selected");
				 //Mostro il modulo relativo
				 treeMenu.showModule(contentId);
				 openedModuleId = contentId;
				 //start with menu open
				 treeOpen = true;
				 
				 TreeMenuMgr.Responders.dispatch("onModuleChange", contentId);
			 });
		 }
		 
		 //attach tooltip
		 //N.B necessita aver caricato jquery.tooltip.js (see http://jquery.bassistance.de/tooltip for docs)
		 if (!div.is(".menu-module")) {
		 	div = $j($j(this).find("div.menu-module-disabled")[0])
		 }
		 var description = div.attr("description");
		 if (description) {
				$j(this).tooltip( {
					bodyHandler : function() {
						return description;
					},
					showURL : false
					//,extraClass: "fancy"
				});
			}
	 });
	 
	 //
	 // Attach tooltip on function TDs
	 //
	 container.find("div.treemenu-function-toolbar td").each( function() {
		 //N.B necessita aver caricato jquery.tooltip.js (see http://jquery.bassistance.de/tooltip for docs)
		 var description = $j(this).attr("description");
		 if (description) {
				$j(this).tooltip( {
					bodyHandler : function() {
						return description;
					}
				});
			}
	 });
	 	 
	 //
	 // Configure draggable nodes
	 //
	treeMenu.find("tr.item-draggable").draggable({
	  helper: "clone",
	  opacity: .75,
	  revert: "true",
	  refreshPositions: true, 
	  delay: 500
	});

	//
	// Configure droppable rows into preferred items (button & and preferred area)
	//
	container.find("td.treemenu-function-preferred, div.treemenu-body-slidearea-preferred").each(function() {
		$j(this).droppable({
			//accept only
		    accept: "tr.item-draggable",
		    //drop event
			drop : function(e, ui) {
				//get object dragged (clone)
				var obj = $j(ui.helper[0]);
				//extract a inner first form 
				var form = obj.find("form")[0];
				var id = $j(form).attr("id");
				//extract span with title
				var title = $j(obj.find("span.title")[0]).text();
				//Build exact selector to find correctly form to submit
				var sel = "div.treemenu-container";
				sel += " form#" + id;
				//Add new link to Preferred Handler
				if (!prefs.checkLink(sel)) {
					prefs.addLink(sel, title, id + " " + title);
					//persist changes
					prefs.savePreferred();
				}
			}
		});		
	});

	//
	//Make visible that a row is clicked
	//
	treeMenu.find("tbody tr").mousedown(function() {
		treeMenu.find("tr.selected").removeClass("selected"); // Deselect currently selected rows
		$j(this).addClass("selected");
	});

	//
	//Make sure row is selected when span is clicked
	//
	treeMenu.find("tbody tr span").mousedown(function() {
		$j($j(this).parents("tr")[0]).trigger("mousedown");
	});

	//
	//Functions handlers 
	//
	
	//Personal settings function handler
	container.find("td#mainmenu-function-console").click( function() {
		//TODO
		//Ricorda di registrare come callback TreeMenuResize.afterToggle alfi
	});
	
	//Openall function handler
	container.find("td.treemenu-function-opeclosenall").click( function() {		
		if (treeOpen) {
			$('mainmenu-function-closeall').show();
			$('mainmenu-function-openall').hide();
		} else {
			$('mainmenu-function-openall').show();
			$('mainmenu-function-closeall').hide();			
		}
		
		var arrayItem = treeMenu.find("tbody tr:visible:not(.treemenu-module-root)");
		for (i= arrayItem.length - 1; i >= 0; i--) {
			var item = arrayItem[i];
			if (treeOpen) {
				$j(item).collapse();
			} else {
				$j(item).expand();
			}
		}
		treeOpen = !treeOpen;
		
		TreeMenuMgr.Responders.dispatch("onAfterToggle");
	});	
	
	//Preferred function handler
	container.find("td#mainmenu-function-preferred").click( function() {
		preferredArea.toggle("slow", TreeMenuResize.afterToggle);
	});
	
	//History function handler
	container.find("td#mainmenu-function-clock").click( function() {
		historyArea.toggle("slow", TreeMenuResize.afterToggle);
	});
	
	//Home function handler
	container.find("td#mainmenu-function-home").click( function() {
		var externalLoginKey = $j($j(this).find("input#home-externalLoginKey")).attr("value");
		
		var form = new Element("form", {action: "/gzoom/control/main", method: 'POST'});
		form.setStyle("display: none");
		form.insert(new Element("input", {type: "hidden", name:'externalLoginKey', value: externalLoginKey}));
		document.body.insert(form);		
		//force startWaiting because is not ajax.Request
        Utils.startWaiting();
        form.submit();
	});
	
	//Search handler
	container.find("input.treemenu-search-field").keypress( function(event) {
		var code = null;
		//see: http://unixpapa.com/js/key.html
		if (event.which == null) {
			code= event.keyCode;    // IE
		} else if (event.which > 0) {
			code= event.which;	  // All others
		}
		
		if (code < 48 || code > 122) {
			//Se enter stop event
			if (code==13) {
				return false;
			}
			return true;
		}
		try {
			var val = $j(this).attr("value") + String.fromCharCode(code);
			
			treeMenu.find("tbody tr:visible td:first-child:contains('"+val+"')").each( function() {
				treeMenu.find("tr.selected").removeClass("selected"); // Deselect currently selected rows
				$j(this).parents("tr").addClass("selected");
				return false; //stops
			});
			
		} catch(err) { }
		return true;
	});

	//
	// Menu link click handler
	//
	treeMenu.find("tbody tr.menu-item").each( function() {
		$j(this).bind("click", function(event) {
			
			//Stop click on expander
			var el = $j(event.target);
			if (el.hasClass("expander")) {
				return;
			}
			//grab form for submit 
			var form = $j(this).find("form");
			if (form) {
				SubmitHandler.postForm(form[0]);
			}
			
			//last - add to history
			var id = $j(form).attr("id");
			//extract span with title
			var title = $j($j(this).find("span.title")[0]).text();
			//Build exact selector to find correctly form to submit
			var sel = "div.treemenu-container";
			sel += " form#" + id;			
			history.addLink(sel, title, id + " " + title);
			
			return false;
		});
	});
	
	
	//
	// Menu folder doubleclick handler
	//
	treeMenu.find("tbody tr.parent").each( function() {
		$j(this).bind("dblclick", function(event) {
			$j(this).toggleBranch();
			//stops event
			return false;
		});
	});

}

//***********************************
// Setting up treeTable by jQuery
// at dom loaded event
//************************************

//$j function is defined into resolve.conflict.js
$j(document).ready( function() {
	
	TreeMenuMgr.loadTree(document);
	
});

