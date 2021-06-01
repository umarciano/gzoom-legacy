
var setInputFieldFromElements = function(elements) {
    var stringEle = "&";
    
    if (Object.isArray(elements) && elements.size() > 0) {
    
    	var multiRowInsertFields = "${parameters.multiRowInsertFields?if_exists}".split("&#124;");
        elements.each(function(element, index) {
            var elementName = element.readAttribute("name");
            var elementValue = element.getValue();
            if(multiRowInsertFields.indexOf(elementName) != -1) {
            	stringEle += elementName+'='+encodeURIComponent(elementValue)+'&';
            }
        });
    }
    return stringEle;
};

var listEle = [];
	    
LookupPropertiesExtension = {
    load: function(newContentToExplore) {
        LookupPropertiesExtension_loaded = true;
        LookupPropertiesExtension.delegateDblClickEvent(newContentToExplore);
        
        if(Object.isElement(newContentToExplore)){
    		Lookup.prototype.afterHideModal_impl = Lookup.prototype.afterHideModal_impl.wrap(
    			function(proceed, options) {
	                //proceed(options);
	                LookupProperties.afterHideModal();
	                LookupPropertiesExtension.afterHideModal(LookupProperties.afterHideModal);
	            }
    		);
    		
    		// si registra anche dopo il load
    		Lookup.prototype.afterLoadModal_impl = Lookup.prototype.afterLoadModal_impl.wrap(
    			function(proceed, options) {
	                LookupProperties.afterLoadModal();
	                LookupPropertiesExtension.afterLoadModal(LookupProperties.afterLoadModal);
	            }
    		);
        } 
    },
    
    
    delegateDblClickEvent: function(newContentToExplore) {
    	if(Object.isElement(newContentToExplore)){
        		var options = {
				    'delegateDblClickEvent' : function(table, e) {
				        if (typeof LookupMgr !== 'undefined' && typeof TableKit !== 'undefined') {
				            if (TableKit.isSelectable(table)) {
				                var selectedRowList = $A(TableKit.Selectable.getSelectedRows(table));
				                if (selectedRowList) {
				                    var lookupActive = LookupMgr.getActiveLookup();
				                    if (lookupActive) {
				                    	var oneSelect = false;
				                    	var first = true;
				                    	selectedRowList.each(function(select){
				                    		var firstCell = $(select.cells[0]);
				                    		/** reloadCurrentRow != Y, fa in modo che venga richiamto il metodo setInputFieldFromElements della lookup,
				                    		 * che cerca nella form i campi con className = lookup_field_description.
				                    		 *  reloadCurrentRow == Y, fa in modo che venga richiamto il metodo setInputFieldFromElements della lookup-properties-extension
				                    		 *  che si occupa di concatenare SOLO gli ID, il recupero degl ialtri campi deve essere fatto con gli screen.
				                    		 */
				                    		<#if !parameters.reloadCurrentRow?has_content || parameters.reloadCurrentRow != "Y">
				                    		if(first){
				                    			first = false;
				                    			lookupActive.setInputFieldFromElements($A(firstCell.select('input')));
				                    			lookupActive.dispatchOnSetInputFieldValue();
				                    		}else if (firstCell) {
					                        	oneSelect = true; 
					                        	listEle.push(setInputFieldFromElements($A(firstCell.select('input'))));
					                        }
					                      <#else>
					                           oneSelect = true; 
                                               listEle.push(setInputFieldFromElements($A(firstCell.select('input'))));
					                      </#if>
				                    	});
				                        if(oneSelect){
				                        	LookupProperties.doubleClickSelected = true;
				                        }else{
				                        	LookupProperties.doubleClickSelected = false;
				                        }				                        
				                    }
				                }
				            }
				        }
				        
				        Modalbox.hide();
	        
				    }
			    };
 
            var tableList = newContentToExplore.select('table');
            var table = $A(tableList).find(function(table) {
                return TableKit.isSelectable(table);
            });
            if (Object.isElement(table)) {
	            TableKit.registerObserver(table, 'onDblClickSelectEnd', 'dblclick-select-table', options.delegateDblClickEvent);
            }
        }
      	
    },
    
    afterHideModal : function(afterHideModal_orig) {
        <#if parameters.resetFormAfterSelect?has_content && parameters.resetFormAfterSelect == "Y">
        var resetItem = Toolbar.getInstance().getItem('.management-reset');
        if(Object.isElement(resetItem)) {
            resetItem.fire('dom:click');
        }
        </#if>
    
    	var insertItem = Toolbar.getInstance().getItem('.management-insertmode');
		if (Object.isElement(insertItem)){
			var insertFunction = insertItem.onclickFunction;
			listEle.each(function(stringEle){
				insertItem.onclickFunction = insertFunction.substring(0,insertFunction.length - 17) + stringEle + insertFunction.substring(insertFunction.length - 17);
				//console.log('insertItem.onclickFunction ' + insertItem.onclickFunction);
				insertItem.fire('dom:click'); 
			});
			insertItem.onclickFunction = insertFunction;
		}
		Lookup.prototype.afterHideModal_impl = afterHideModal_orig;
		LookupPropertiesExtension_loaded = undefined;
    },
    
    afterLoadModal : function(afterLoadModal_orig) {
    	LookupPropertiesExtension.delegateDblClickEvent($('MB_window'));
    	Lookup.prototype.afterLoadModal_impl = afterLoadModal_orig;
	}
}

<#if parameters.enableMultiRowInsert?has_content && parameters.enableMultiRowInsert=="Y">
	<#if lookup?has_content && lookup=="Y">
		if (typeof LookupPropertiesExtension_loaded === 'undefined') {
			LookupPropertiesExtension.load($('MB_window'));
		}else{
			LookupPropertiesExtension.delegateDblClickEvent($('MB_window'));
		}
	</#if>
</#if>