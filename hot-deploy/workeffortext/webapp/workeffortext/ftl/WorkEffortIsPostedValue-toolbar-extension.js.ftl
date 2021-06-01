WorkEffortIsPostedExtension = {
    load: function() {
    	var tabInstance = Control.Tabs.findByTabId("management_0");
		if (tabInstance) {
	    	var elencoDiv = $$('.data-management-container');
	    	if(Object.isArray(elencoDiv)){
	    		elencoDiv.each(function(containerSelected){
		    		if(Object.isElement(containerSelected)) {		    			
		            	tabInstance.registerEvent(containerSelected.identify(), WorkEffortIsPostedExtension.gestisciIsPosted);
		            }
		        });
    		
    		}
    	}    	
    	WorkEffortIsPostedExtension.gestisciIsPosted("management_0");
    	
    	UpdateAreaResponder.Responders.register(WorkEffortIsPostedExtension.responder, 'WorkEffortIsPostedExtension');
     },
     
     classNameProperty : 'disabledByRowSelected',
          
     responder : {
        onLoad : function(newContent) {
            WorkEffortIsPostedExtension.gestisciIsPosted(newContent);
        },
        unLoad : function() {
            return typeof 'WorkEffortIsPostedExtension' === 'undefined';
        },
        unLoadCondition : function() {
            return false;
        }
    },

     gestisciIsPosted : function(newContent) {
     	var myTabs = Control.Tabs.instances[0];
     	var containerSelected = null;
     	/* Se il tab non esiste, sono nel caso che nn ho tab **/
     	if(!myTabs){
     		containerSelected = $('main-container')
     	}else{
     		containerSelected = $(myTabs.getActiveContainer());
     	}
    	if (containerSelected) {
	     	
	     	var formSelected = null;
	     	
	     	var childDivContainer = $(containerSelected).down('div.child-management-container');
	     	if(Object.isElement(childDivContainer)){
	     		formSelected = $(childDivContainer).down('form.basic-form');
	     		// caso form di tipo lista
	     		if(!Object.isElement(formSelected)) {
                    formSelected = $(childDivContainer).down('div.child-management'); // in realta e un div che contiene l lista
                    
                }
	     	}else{
	     		formSelected = $(containerSelected).down('form.basic-form');
	     	}
	     	if(!Object.isElement(formSelected)) {
	     	    newContent = $(newContent);
	     	    formSelected = newContent.down('form.basic-form');
	     	}
	     	
	     	var toolbarSelected = $(document.body).down('div.screenlet-title-bar');
	     	
	     	var icons = $(toolbarSelected).select('li.save', 'li.delete');     	

	     	if(Object.isElement(formSelected)){
	     		var workEffortGenericTable = $(formSelected).down('table');
		        if(Object.isElement(workEffortGenericTable)) {
		            if (typeof TableKit != 'undefined') {
		                if (TableKit.isSelectable(workEffortGenericTable)) {		                    
		                    WorkEffortIsPostedExtension.manageSelectedRow(workEffortGenericTable);
		                    TableKit.registerObserver(workEffortGenericTable, 'onSelectEnd', 'WorkEffortIsPostedValue-toolbar-extension', function(workEffortGenericTable, e) {		                        
		                        WorkEffortIsPostedExtension.manageSelectedRow(workEffortGenericTable);
		                    });
		                    
		                    /** Registro evento sul paginatore**/
		                    var navFirst = $(formSelected).down('li.nav-first');
		                    if(Object.isElement(navFirst)) {
					            navFirst.observe("click", WorkEffortIsPostedExtension.clickListener.curry(workEffortGenericTable));
					        }
					        
					        var navLast = $(formSelected).down('li.nav-last');
		                    if(Object.isElement(navLast)) {
					            navLast.observe("click", WorkEffortIsPostedExtension.clickListener.curry(workEffortGenericTable));
					        }
					        
		                    var navNext = $(formSelected).down('li.nav-next');
		                    if(Object.isElement(navNext)) {
					            navNext.observe("click", WorkEffortIsPostedExtension.clickListener.curry(workEffortGenericTable));
					        }
					        
					        var navPrevius = $(formSelected).down('li.nav-previous');
		                    if(Object.isElement(navPrevius)) {
					            navPrevius.observe("click", WorkEffortIsPostedExtension.clickListener.curry(workEffortGenericTable));
					        }
		                    
		                } else {
		                	var isPosted = formSelected.down('input[name=isPosted]');
		                	var isObiettivo = formSelected.down('input[name=isObiettivo]');
		                	if(Object.isElement(isPosted)){
		                		WorkEffortIsPostedExtension.setIconsClassName(isPosted, icons, isObiettivo);
		                	}
		                }
		            }
		        }
	        }
	    }
    },    
    
    clickListener: function(table) {
		/*** Abilito i bottoni prima di paginare **/        
        var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
    	if(selectedRow) {
        	var icons = [];
        	var toolbarSelected = $(document.body).down('div.screenlet-title-bar');
        	
        	/** Prendi gli icone disabilitate e le abilito **/
            icons = $(toolbarSelected).select('li.save-disabled', 'li.delete-disabled');
            
            if (Object.isArray(icons)) {
    			icons.each(function(element, index){
    				if(element.hasClassName(WorkEffortIsPostedExtension.classNameProperty)){
    					element.removeClassName(WorkEffortIsPostedExtension.classNameProperty);
    					element.removeClassName('disabled');
    					element.className = element.className.replace('save-disabled', 'save');            			
            			element.className = element.className.replace('delete-disabled', 'management-delete delete');
           			
	            		var anchor = element.down('a');
	                    if (Object.isElement(anchor)) {
	                        anchor.show();
	                    }
            		}
    			});
            }
            
        }
        
    },
    
    
    
    manageSelectedRow : function(table) {
    	var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
    	if(selectedRow) {
        	var icons = [];
        	var toolbarSelected = $(document.body).down('div.screenlet-title-bar');
        	
        	var isPosted = $A(selectedRow.select('input')).find(function(s) {
                return s.readAttribute('name').indexOf('isPosted') > -1;
            });
            var isObiettivo = $A(selectedRow.select('input')).find(function(s) {
                return s.readAttribute('name').indexOf('isObiettivo') > -1;
            });
            
            if (Object.isElement(isPosted) && isPosted.getValue() != 'Y') {
                icons = $(toolbarSelected).select('li.save-disabled', 'li.delete-disabled');
            }
            else {
                icons = $(toolbarSelected).select('li.save', 'li.delete');
            }
            
            WorkEffortIsPostedExtension.setIconsClassName(isPosted, icons, isObiettivo);
        }
    }, 
    
    setIconsClassName: function(isPosted, icons, isObiettivo){
    	isPosted = $(isPosted);
    	isObiettivo = $(isObiettivo);
		if(Object.isElement(isPosted) && isPosted.readAttribute('value') == 'Y' ) {
        	if(!isObiettivo || isObiettivo.readAttribute('value') != 'Y' ) {
	        	if (Object.isArray(icons)) {
	            	icons.each(function(element, index){
	            		element.addClassName(WorkEffortIsPostedExtension.classNameProperty);
	            		element.className = element.className.replace('save', 'save-disabled disabled ');
	            		element.className = element.className.replace('management-delete delete', 'delete-disabled disabled ');
	            		
	            		var anchor = element.down('a');
	                    if (Object.isElement(anchor)) {
	                        anchor.hide();
	                    }	                    
	            	});
	            }
            }
        }else{
        	if (Object.isArray(icons)) {
    			icons.each(function(element, index){
    				if(element.hasClassName(WorkEffortIsPostedExtension.classNameProperty)){
    					element.removeClassName(WorkEffortIsPostedExtension.classNameProperty);
    					element.removeClassName('disabled');
    					element.className = element.className.replace('save-disabled', 'save');
            			element.className = element.className.replace('delete-disabled', 'management-delete delete');
            			
	            		var anchor = element.down('a');
	                    if (Object.isElement(anchor)) {
	                        anchor.show();
	                    }
            		}
    			});
            }
        }
    }
}


    WorkEffortIsPostedExtension.load();
