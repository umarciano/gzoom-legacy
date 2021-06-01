
/** --------------------------------------------------------------------------------
 * Treeview Manager
 * Nota: per il corretto funzionamento deve essere caricato il js: resolve.conflict.js 
 * che risolve la convivenza di jQuery (instanziando la funzione $j) e Prototype
 * ---------------------------------------------------------------------------------
 */

var TreeviewMgr = Class.create({ });

/**
 * Load all tree in content or document
 */
TreeviewMgr.loadTree = function(content) {
	//Find tree by container
	if (content) {
		var treeList = $j(content).find("table.treeview-outer-table");
		//Init all tree
		treeList.each( function() {
			TreeviewMgr.initTree($j(this));
		});
	}
}

/**
 * Init single tree
 */
TreeviewMgr.initTree = function(treeView) {
	 
	 //Qualche costante (deve essere uguale a quanto specificato per gli stessi div in style.css)
	 TreeviewMgr._globalScreenDisplay = "table-row";
	 TreeviewMgr._leftScreenDisplay = "table-cell";
	 TreeviewMgr._detailScreenDisplay = "table-cell";
	 //Nota bene: questa proprietà deve coincidere con quanto scritto in style.css	 
	 TreeviewMgr._bodyMaxHeight = "maxheight";     //.treeview-container-tree table.treeTable tbody
	 TreeviewMgr._firstColMaxWidth = "maxwidth";  
	 
	 TreeviewMgr._narrow = (typeof(TreeviewMgr._narrow)!="undefined") ? TreeviewMgr._narrow : "Y";
	 
	/**
	 * Init it
	 */
     //Maps spa: add smooth creation effects
     $j(".treeview-tree-holder").hide();
	 treeView.treeTable({initialState: 'expanded'});
	 $j(".treeview-tree-holder").slideDown("slow");	
	 
	/**
	 * Configure draggable nodes into all treeview
	 * folder and .file are span into first td of each row
	 */
	 if (!treeView.hasClass("drag-and-drop-disabled")) {
		treeView.find(".folder, .file").draggable({
		  helper: "clone",
		  opacity: .75,
		  refreshPositions: true, 
		  revert: "true",
		  revertDuration: 300,
		  delay: 500,
		  scroll: true
		});
	 }

	
	/**
	 * Inizialmente controllo altezza massima se devo settarla opportunamente
	 */
	TreeviewMgr._checkMaxHeight(treeView.find("tbody")[0]);	
	
	/**
	 * Configure droppable rows into all treeview
	 * ===== CALL SAVE CHANGES METHOD 
	 * folder and .file are span into first td of each row
	 */
	treeView.find(".folder, .file").each(function() {
		$j(this).parents("tr").droppable({
			
		    accept: ".file, .folder",
			
		    drop: function(e, ui) { 
		      //Call jQuery treeTable plugin to move the branch
		      $j($j(ui.draggable).parents("tr")).appendBranchTo(this);
		      $j(ui.draggable).parents("tr").addClass("moved");
		      //Save changes
			  return TreeviewMgr.saveChanges(ui.draggable);		      
		    },
		    
		    hoverClass: "accept",
		    over: function(e, ui) {
		      //Make the droppable branch expand when a draggable node is moved over it.
		      if(this.id != ui.draggable.parents("tr")[0].id && !$j(this).is(".expanded")) {
		    	  $j(this).expand();
		      }
		    }
		});
	});

	/**
	 * Make visible that a row is clicked
	 */
	treeView.find("tbody tr").mousedown(function() {
		treeView.find("tr.selected").removeClass("selected"); // Deselect currently selected rows
		$j(this).addClass("selected");
	});

	/**
	 * Make sure row is selected when span is clicked
	 */
	treeView.find("tbody tr span").mousedown(function() {
		$j($j(this).parents("tr")[0]).trigger("mousedown");
	});

	
	/**
	 * Decide se l'albero é in stato chiuso o aperto
	 */
	treeView.find("treeview-other-column:first").each(function () {
		if ($j(this).css("display") != "none") {
			TreeviewMgr._narrow = "N";
		} else {
			TreeviewMgr._narrow = "Y";
		}
	});
	
	/**
	 * Splitter handler
	 */
//	$j(".treeview-header-toolbar li.treeview-header-toolbar-splitter").click(function () {
	//treeview.find("div.treeview-header-toolbar-splitter").click(function () {
	$j(treeView.parents("div.treeview-global-container")[0]).find("div.treeview-header-toolbar-splitter").click(function () {
		var globalContainer = $j(treeView.parents("div.treeview-global-container")[0]);
		var leftContainer = $j(treeView.parents("div.treeview-left-screen")[0]);
		var detailContainer = $j(globalContainer.find("div.treeview-detail-screen")[0]);
		
		//setto opportunamente i display relativi e nascondo/mostro detail
		if (TreeviewMgr._narrow == "Y") {
			globalContainer.css({'display' : 'block'});
			leftContainer.css({'display' : 'block'});
			detailContainer.css({'display' : 'none'});
			leftContainer.css({'float' : 'left'});
			globalContainer.find("div.treeview-header-toolbar").css({'float' : 'left'});
			globalContainer.find("div.treeview-header-toolbar-splitter").addClass("expanded");
		} else {
			globalContainer.css({'display' : TreeviewMgr._globalScreenDisplay});
			leftContainer.css({'display' : TreeviewMgr._leftScreenDisplay});
			detailContainer.css({'display' : TreeviewMgr._leftScreenDisplay});
			detailContainer.css({'float' : ''});
			leftContainer.css({'float' : ''});
			globalContainer.find("div.treeview-header-toolbar-hide").removeClass("collapsed");
			globalContainer.find("div.treeview-header-toolbar-splitter").removeClass("expanded");
		}
		
		//Nascondo/mostro le colonne della tabella
		treeView.find(".treeview-other-column").each(function () {
			if ($j(this).css("display") != "none") {
				TreeviewMgr._narrow = "Y";
				$j(this).hide();
			} else {
				TreeviewMgr._narrow = "N";
				$j(this).show();
			}
		});
		
		//Sulla prima colonna sono costretto, quando apro, a togliere l'attributo max-width
		//e a rimetterlo quando chiudo
		treeView.find("td.first-col").each(function () {
			if (TreeviewMgr._narrow == "Y") {
				$j(this).addClass(TreeviewMgr._firstColMaxWidth);
			} else {
				$j(this).removeClass(TreeviewMgr._firstColMaxWidth);
			}
		});
	});
	
	$j(treeView.parents("div.treeview-global-container")[0]).find("div.treeview-header-toolbar-hide").click(function () {
		var globalContainer = $j(treeView.parents("div.treeview-global-container")[0]);
		var leftContainer = $j(treeView.parents("div.treeview-left-screen")[0]);
		var detailContainer = $j(globalContainer.find("div.treeview-detail-screen")[0]);
		
		//setto opportunamente i display relativi e nascondo/mostro detail
		if (TreeviewMgr._narrow == "Y") {
			globalContainer.css({'display' : 'block'});
			leftContainer.css({'display' : 'none'});
			detailContainer.css({'display' : 'block'});
			detailContainer.css({'float' : 'left'});
			globalContainer.find("div.treeview-header-toolbar").css({'float' : 'left'});
			globalContainer.find("div.treeview-header-toolbar-hide").addClass("collapsed");
		} else {
			globalContainer.css({'display' : TreeviewMgr._globalScreenDisplay});
			leftContainer.css({'display' : TreeviewMgr._leftScreenDisplay});
			detailContainer.css({'display' : TreeviewMgr._leftScreenDisplay});
			detailContainer.css({'float' : ''});
			leftContainer.css({'float' : ''});
			globalContainer.find("div.treeview-header-toolbar-hide").removeClass("collapsed");
			globalContainer.find("div.treeview-header-toolbar-splitter").removeClass("expanded");
		}
		
		//Nascondo/mostro le colonne della tabella
		treeView.find(".treeview-other-column").each(function () {
			if ($j(this).css("display") != "none") {
				TreeviewMgr._narrow = "Y";
				$j(this).hide();
			} else {
				TreeviewMgr._narrow = "N";
				$j(this).show();
			}
		});
		
		//Sulla prima colonna sono costretto, quando apro, a togliere l'attributo max-width
		//e a rimetterlo quando chiudo
		treeView.find("td.first-col").each(function () {
			if (TreeviewMgr._narrow == "Y") {
				$j(this).addClass(TreeviewMgr._firstColMaxWidth);
			} else {
				$j(this).removeClass(TreeviewMgr._firstColMaxWidth);
			}
		});
	});

	/**
	*Here manages search filter input
	*/
	treeView.find("thead input.treeview-search-field").keypress( function(event) {
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
			
			treeView.find("tbody tr td:first-child:contains('"+val+"')").each( function() {
				treeView.find("tr.selected").removeClass("selected"); // Deselect currently selected rows
				$j(this).parents("tr").addClass("selected");
				//If closed open it
				$j(this).parents("table.treeview-outer-table tr.collapsed").map( function() {
				    $j(this).removeClass("collapsed").addClass("expanded");
				});
				return false; //stops
			});
			
		} catch(err) { }
		return true;
	});

	/**
	 * Submit handler
	 */
	treeView.find("tbody tr").each( function() {
		$j(this).bind("click", function(event) {
			/*var cachableForm = $(".management-").descendants().find(function(element){
                return FormKit.isCachable(element);
            });*/
			
			var execute = true;
			var tab = Control.Tabs.findByTabId('management_0');
			if (!tab) {
				container = $('management-form-screenlet');
			} else {
				container = tab.getActiveContainer();
				if (!Object.isElement(container)) {
					container = $('management-form-screenlet');
				}
			}
			if (Object.isElement(container)) {
				var cachableForm = $(container).descendants().find(function(element){
                    return FormKit.isCachable(element);
                });
				
				if (Object.isElement(cachableForm)) {
					execute = !FormKitExtension.checkModficationWithAlert(cachableForm);
				}
			}
			
			if (execute) {
				//Stop click on expander
				var el = $j(event.target);
				if (el.hasClass("expander")) {
					//Controllo se devo comprimere l'altezza
					TreeviewMgr._checkMaxHeight(treeView.find("tbody")[0]);
					return;
				}
	
				//grab form for submit 
				var form = $j(this).find("form");
				if (form) {
					//Aggiungo id del ramo 
					var id = $j(this).attr("id");
					var selectedIdField = $j(form).find("input[name=selectedId]");
					if (!selectedIdField || selectedIdField.size() == 0)
						$j("<input type=\"hidden\" name=\"selectedId\" value=\"" + id +"\" />").appendTo(form);
					else
						selectedIdField.value = id;
					
					var classList = $j(this).attr("class").split(' ');
					var parentClassName = null;
					classList.each(function(className) {
						if (className.startsWith('child-of-')) {
							parentClassName = className;
						}
					});
					if (parentClassName) {
						var parentSelectedId = parentClassName.substring(9);
						var parentSelectedIdField = $j(form).find("input[name=parentSelectedId]");
						if (!parentSelectedIdField || parentSelectedIdField.size() == 0)
							$j("<input type=\"hidden\" name=\"parentSelectedId\" value=\"" + parentSelectedId +"\" />").appendTo(form);
						else
							parentSelectedIdField.value = parentSelectedId;
					}
					
					//TODO add id to form
					//get all inputs
					var params = form.serialize();
					if ($j(this).attr("not_managed")) {
	    				params = params +'&treeViewEmptyDetail=Y';
	    			}
	    			//Bug 3506
	    			//salvo l'id del nodo clickato
	    			var jar = new CookieJar({path : "/"});
	    			var treeSelectedId = jar.get("selectedId");
	    			if(treeSelectedId) {
	    				jar.remove("selectedId");
	    			}
	    			jar.put("selectedId", id);
	    			
	    			//Bug 4045
	    			var oldWorkEffortTypeId = jar.get("oldWorkEffortTypeId");
	    			if(oldWorkEffortTypeId) {
	    				var toCompareType;
	    				var workEffortTypeIdTo = $j(form).find("input[name=workEffortTypeIdTo]");
	    				if(workEffortTypeIdTo.size() == 1 && workEffortTypeIdTo[0].value == "") {
	    					var workEffortTypeIdRoot = $j(form).find("input[name=workEffortTypeIdRoot]");
	    					toCompareType = workEffortTypeIdRoot[0].value;	
	    				}
	    				else {
	    					toCompareType = workEffortTypeIdTo[0].value;
	    				}
	    				if(toCompareType && toCompareType != oldWorkEffortTypeId) {
	    					jar.removeRegexp("_VIEW_INDEX_");
	    					jar.removeRegexp("activeLink");
	    					jar.remove("oldWorkEffortTypeId");
	    					jar.put("oldWorkEffortTypeId", toCompareType);
	    				}
	    			}
	    			
	    			var treeParentSelectedId = jar.get("parentSelectedId");
	    			if(treeParentSelectedId) {
	    				jar.remove("parentSelectedId");
	    			}
	    			jar.put("parentSelectedId", parentSelectedId);
	    			
					//read action
					var action = form.attr("action");
					var areaId = form.attr("areaId");
					new Ajax.Request(action, {parameters : params, evalScripts : true, contentToUpdate : areaId, content : "main-container"});
				}
			}
			
		});
	});
	
	//At the end check if selected child correspond to the value of cookie
	var currentSelected = treeView.find("tr.selected")[0];
	if (currentSelected) {
		var currentSelectedId = $j(currentSelected).attr("id");
		
		var jar = new CookieJar({path : "/"});
		var treeSelectedId = jar.get("selectedId");
		if (!treeSelectedId || treeSelectedId !== currentSelectedId) {
			jar.remove("selectedId");
			jar.put("selectedId", currentSelectedId);
			
			//Bug 4045
			var submitForm = currentSelected.down("form.treeview-submit-form");
			if(Object.isElement(submitForm)) {
				var oldWorkEffortTypeId;
				var workEffortTypeIdTo = submitForm.down("input[name='workEffortTypeIdTo']");
				if(Object.isElement(workEffortTypeIdTo) && workEffortTypeIdTo.getValue() != "") {
					oldWorkEffortTypeId = workEffortTypeIdTo.getValue();
				} 
				else {
					var workEffortTypeIdRoot = submitForm.down("input[name='workEffortTypeIdRoot']");
					if(Object.isElement(workEffortTypeIdRoot) && workEffortTypeIdRoot.getValue() != "") {
						oldWorkEffortTypeId = workEffortTypeIdRoot.getValue();
					}
				}
				jar.put("oldWorkEffortTypeId", oldWorkEffortTypeId);
			}
			
			var classList = currentSelected.classNames();
			var parentClassName = null;
			classList.each(function(className) {
				if (className.startsWith('child-of-')) {
					parentClassName = className;
				}
			});
			if (parentClassName) {
				var parentSelectedId = parentClassName.substring(9);
				
				jar.remove("parentSelectedId");
				jar.put("parentSelectedId", parentSelectedId);
			}
			else {
				jar.remove("parentSelectedId");
				jar.put("parentSelectedId", currentSelectedId);
			}
		}
	}
}

 
/**
* Static methods
*/
TreeviewMgr._checkMaxHeight = function(el) {
	//Nota bene: scrollHeight == 500 é circa 50 em che coincide con quanto indicato in style.css nella classe tbody.maxheight
	if (el.scrollHeight > 500 ) {
		$j(el).addClass(TreeviewMgr._bodyMaxHeight);
	} else { 
		$j(el).removeClass(TreeviewMgr._bodyMaxHeight);
	}
}
 
/**
* Static methods
*/
TreeviewMgr.saveChanges = function(draggable) {

	var trArray = $j("table.treeview-outer-table").find("tbody tr.moved");
	if (trArray.size() == 0) {
		return false; 
	}
	var selected = $j("table.treeview-outer-table").find("tbody tr.selected")[0];
	
	//Bug 36
	var jar = new CookieJar({path : "/"});
    var selectedId = jar.get("selectedId");
    if (selectedId == null) {
        selectedId = $j(selected).attr("id"); 
    }

	//2. for each find out first parent and get parent keys
	//  then put data into map (Prototype Map)
	var action = "";
	var reloadTreeAction = "";
	var params = $H({});
	$j(trArray).each( function () {
	    var parentKeys = "";
	    var parentOthers = "";
	    var classNames = this.className.split(' ');
	    var id = this.id;
	    //find out form for submit 
	    var formToSubmit = $j(this).find("form.treeview-submit-form")[0];
	    action = $j(formToSubmit).attr("dragdrop_action");
	    reloadTreeAction = $j(formToSubmit).attr("reloadtree_action");
	    
	    //find out entityName and other parameters
		// every form has the same base parameters, so i extract the first
	    $j(this).find("form.treeview-submit-form input").each( function() {
	    	var item = $j(this);
	    	params.set(item.attr("name"), item.val());
	    });
	    
	    //find out parent of this tr
	    var parent = $j.grep(classNames, function (item, idx) {
	    	if (item.match("child-of-")) {
	    		return true;
	    	} else 
	    		return false;
	    });
	    if (parent!=null) {
	    	parent = parent[0].substring(9);
	    	parentKeys = $j("#" + parent).attr("keys");
	    	parentOthers = $j("#" + parent).attr("others");
	    }
	    
	    //current tr's keys
	    var keys = $j(this).attr("keys");
	    //current tr's others
	    var others = $j(this).attr("others");
	    //current tr's default
	    var defaultValueFields = $j(this).attr("defaultValueFields");
	    
	    //Build 'stringona' with all keys 
	    if (parentOthers!="" && parentOthers!=null) {
	    	parentKeys += "," + parentOthers;
	    }
	    if (others!="" && others!=null) {
	    	keys += "," + others;
	    }
	    var data = parentKeys+";;;" + keys;
	    
	    //store data relative to id
	    //first string into array will be current parent key
	    //last string will be row keys
	    params.set(id, data);
	    
	    params.set("defaultValueFields", defaultValueFields);
	});
	
	params.set("selectedId", selectedId);
	
	//Last parameter: adding tree state (narrow or wide)
	if (TreeviewMgr._narrow) {
		params.set("narrow", TreeviewMgr._narrow);
	}
	params.set("dragdrop", "Y");
	
	
	//Persist into db and callback to updater function
	new Ajax.Request(action, {parameters : params.toObject(), evalScripts : true, 
		contentToUpdate : 'treeview-tree-holder', content : 'treeview-tree-holder', 
		onComplete: TreeviewMgr._postSave.curry(params, draggable, reloadTreeAction, $(trArray[0]).identify())});
}

/**
* Callback after save method
*/
TreeviewMgr._postSave = function(params, draggable, reloadTreeAction, id, transport) {
	 var data = transport.responseText.evalJSON(true); 
	//Shows error messages if any
	 if (typeof(Messages)!="undefined") {
		 
		 new Messages().onAjaxLoad(data, null);
//		 return false;
	 }
	 
	 //Recupero gli auxiliary parameters
	 /*if (!Object.isUndefined(data.id)) {
		 var id = Object.toQueryString(data.id).gsub('&', '|').gsub('=', ';');
		 params.set("id", id);
     }*/
	 var newId = params.get(id);
	 if (newId) {
		 newId = newId.split(';;;')[1];
		 newId = newId.replace(/\|/g, '=');
		 newId = newId.replace(/,/g,'|');
		 params.set("id", newId);
	 }
	 
	//Riporto a casa l'elemento (NOTA BENE: la properties originalPosition é una mia modifica a ui.droppable riga 125)
	 //Reload Tree
	 new Ajax.Request(reloadTreeAction, {parameters : params.toObject(), evalScripts : true, 
		 contentToUpdate : 'treeview-left-screen', content: 'treeview-left-screen'});
	 
	 //esecuzione a buon fine tolgo l'evidenziazione ai tr
	 $j(draggable).parents("tr").removeClass("moved");
	 return true;
}

/**
* Utility method to extract key input from treeview's form
*/
TreeviewMgr._updloadNewInput = function(formTarget, input) {
	if (input) {
		var name = input.readAttribute("name");
		var exist = formTarget.getInputs(null, name).first();
		if (!(exist)) {
			var newInp = new Element("input", {"type": "hidden"});
			newInp.writeAttribute("name", name);
			newInp.writeAttribute("value", input.readAttribute("value"));
			formTarget.insert(newInp);
		}
	}
}

/**
* Utility method to extract all key input
*/
TreeviewMgr._updloadAllInput = function(formTarget, formSource) {
	var ar = formSource.getInputs();
	ar.each( function(input) {
		TreeviewMgr._updloadNewInput(formTarget, input);
	}.bind(this));
}

/**
 * method manages keys to add like parameters to the submit form
 * along insert operations
 */
TreeviewMgr.manageInsertParams = function(form, treeview) {
	//find selected tr
	var tr = treeview.down("tr.selected");
	if (!Object.isUndefined(tr)) {
		//Find submit form hold by tr
		var f = tr.down("form.treeview-submit-form");
		if (!Object.isUndefined(f)) {
			/* collect all input from treeview submit form */
			var keys = f.getInputs();
			if (keys) {
				keys.each( function(input) {
					TreeviewMgr._updloadNewInput(form, input);
				}.bind(this));
			}
//			/* 2 step: replace From and To value for keys */
//			var keys = f.getInputs("hidden", "parentRelKeyFields").first();
//			if (keys) {
//				var val = keys.getValue();
//				if (val.indexOf("|") > -1) {
//					//2 keys: first is 'From' key, last is 'To' key
//					var values = val.split("|");
//					TreeviewMgr._insertNewInput(form, f, values[1]);
//				} else {
//					TreeviewMgr._insertNewInput(form, f, val);
//				}
//			}
			
//			//Collect inputs only relative to supplementaty key field list
//			var otherKeys = f.getInputs("hidden", "parentRelOtherKeyFields").first();
//			if (otherKeys) {
//				var val = otherKeys.getValue();
//				if (val.indexOf("|") > -1) {
//					//2 keys: first is 'From' key, last is 'To' key
//					var values = val.split("|");
//					TreeviewMgr._insertNewInput(form, f, values[1]);
//				} else {
//					TreeviewMgr._insertNewInput(form, f, val);
//				}
//			}
		}
	}
}

/**
* Gets selected row keys
*/
TreeviewMgr.getSelectedKeys = function() {
	var item = $$("table.treeview-outer-table tr.selected");
	if (item.length > 0) {
		return item[0].readAttribute("keys");
	} else {
		return "";
	}
}

//************************************
// Setting up treeTable by jQuery
// at dom loaded event
//************************************

//$j function is defined into resolve.conflict.js
$j(document).ready( function() {
	TreeviewMgr.loadTree(document);
});
	