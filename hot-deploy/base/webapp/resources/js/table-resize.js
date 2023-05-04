TableResize = {
    load: function(newContent, withoutResponder) {
        var newContent = Object.isElement($(newContent)) ? $(newContent) : $(document.body);
        
        // if content-main-section has a single-column-fullopen classname, not resize table
        // <div id="content-main-section" class="single-column-fullopen">
        if($$('#content-main-section.single-column-fullopen').size() != 0) {
            return;
        }
        
        var fixableTables;
        var tableInParentSection;
        if (Object.isElement(newContent)) {
                fixableTables = newContent.select("table.headerFixable");

                tableInParentSection = $A(newContent.getElementsByTagName("div")).find(function(div) {
                    if (div.identify().include("parent")) {
                        return div.down("table.headerFixable") ? true : false;
                    }
                });
        }

        if (!tableInParentSection) {
            switch(newContent.identify()) {
                case "treeview-detail-screen":
                case "common-container":
                    {
                        var folders = newContent.getElementsBySelector("div").findAll(function(div) {
                            return div.identify().include("management_") ? true : false;
                        });

                        if (folders.size() > 0) {
                            for(var i = 0; i < folders.size(); i++) {
                                TableResize.resize(folders[i]);
                            }
                        }
                        else {
                            TableResize.resize(newContent);
                        }

                        break;
                    }
                case "sub-folder-container":
                    {

                        var subFolders = newContent.getElementsBySelector("div").findAll(function(div) {
                               return div.identify().include("management_") ? true : false;
                           });
                           newContent = newContent.down("div");

                           if (subFolders.size() > 0) {
                               for(var i = 0; i < subFolders.size(); i++) {
                                   TableResize.resize(subFolders[i]);
                               }
                           }

                        break;
                    }
                default: {
                    TableResize.resize(newContent);
                }
            }
        }

        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register( {
                onAfterLoad : function(newContent) {
                    TableResize.load(newContent, true);
                }
            }, 'table-resize');
        }
    },

    resize: function(content) {    	
        var fixableTables = content.select("table.headerFixable");
        if (fixableTables.size() > 1)
            return;
        
        var toReduce = 0;
        var viewportHeight = document.viewport.getHeight();
        
        if (content.identify().startsWith("management_")) {
            var commonContainer = $("common-container");
            var screenletBody = content.up(".screenlet-body");
            var margin = $("main-container-screenlet").getStyle("margin-bottom");
            margin = margin.substring(0, margin.length - 2);

            toReduce = viewportHeight - Utils.findPosition(screenletBody)[1] - margin - content.getHeight() - 1;

            if (content.identify().endsWith("_0")) {
                toReduce = viewportHeight - Utils.findPosition(commonContainer.up())[1] - commonContainer.up().getHeight();
            }
        }

        else if (content.identify().startsWith("child-management")) {
        	var referenceContainer = content;
        	var referenceContainerHeight = referenceContainer.getHeight();
           	toReduce = viewportHeight - Utils.findPosition(referenceContainer)[1] - referenceContainer.getHeight();
        }
        
        else if (content.identify() === "common-container" || content.identify() === "treeview-detail-screen") {
            toReduce = viewportHeight - Utils.findPosition(content.up())[1] - content.up().getHeight();
        } 
        else if (content.hasClassName('portlet-main-container')) {
        	toReduce = 1;
        }
        
        if (content.hasClassName("data-management-container") && fixableTables.size() > 0) {
        	if (toReduce < 0) {
        		var contentHeight = content.getHeight();       		
        		content.setStyle({"height": contentHeight + toReduce + "px"});
        	}
        }
		
		// Ridimensionamento tabelle
        var divTableContainer = content.down("div.tableContainer");
        if (Object.isElement(divTableContainer)) {
            var activeTabId;
            if (Control.Tabs.instances.size() > 0) {
                activeTabId = new CookieJar({path : "/"}).get("activeLink" + Control.Tabs.instances[0].id);
            }

            if (activeTabId && content.identify().startsWith("management_") && content.identify() !== activeTabId) {// .startsWith("management_") && !content.identify().endsWith("0")) {
                content.show();
            }

            var divTableContainerHeight = divTableContainer.getHeight();
            var table = divTableContainer.down("table");
            var tbody = table.down("tbody");
            var tbodyHeight = tbody.getHeight();
            var tableSize = TableKit.getBodyRows(table).size();
            var rowHeight = TableKit.getBodyRows(table).first().getHeight();
            var theadHeight = table.down("thead").getHeight();
            
           
            /**
             * Controllo se sono nel caso dove la tabella non deve essere ridimensionata,
             * perchè ha delle tabelle caricate in js sotto, problema riscontrato solo su Crome
             */
            var form = table.up("form");
        	if((form && form.hasClassName('noTableResizeHeight')) || table.hasClassName('noTableResizeHeight')) {
        		// DO NOTHING, esiste gia' un javascript che ricalcola l'altezza nel caso di form o div con noTableResizeHeight
        	    // divTableContainer.setStyle({height: "auto"});        		
        	}else{
	            if (Object.isElement(divTableContainer) && Object.isElement(tbody)) {
	                var divNewHeight;
	                var newTbodyHeight;
	                
	                if (Object.isElement(content.up(".data-management-container"))
	                		|| content.hasClassName("data-management-container")) {
	                		
	                		var dataManagementContainerHeight = Object.isElement(content.up(".data-management-container"))
	                				? content.up(".data-management-container").getHeight()
	                				: content.hasClassName("data-management-container")
	                					? content.getHeight()
	                					: divTableContainerHeight;
	                					
	                if (toReduce == 0) {
	                		// Ho precedentemente ridimensionato il data-management-container
	                		// quindi la tabella si deve adattare alla sua altezza
	                		divTableContainer.setStyle({height: dataManagementContainerHeight + "px"});
	                		newTbodyHeight = dataManagementContainerHeight - theadHeight;
	                		TableKit.setTbodyHeight(tbody, newTbodyHeight);
	                	}
	                }
	                else if (toReduce < 0) {
	                    divNewHeight = divTableContainerHeight + toReduce;	                   
	
	                    if (tableSize * rowHeight < divNewHeight) {
	                        divNewHeight = tableSize * rowHeight + table.down("thead").getHeight();
	                    }
	
	                    divTableContainer.setStyle({height: divNewHeight + "px"});
	                    	
	                    newTbodyHeight = divNewHeight - table.down("thead").getHeight();
	                    var oldTbodyHeight = tbody.getHeight();
	                    TableKit.setTbodyHeight(tbody, newTbodyHeight);
	                    
	                    if (newTbodyHeight < oldTbodyHeight) {
			                if (!Object.isElement(table.down("th.scrollBarHeader"))) {
			                  	var newTh = new Element("th");
			                   	newTh.addClassName("scrollBarHeader");
			                   	var tHead = table.down("thead").down("tr").insert(newTh);
			                }
	                    }
	                }
	                
	                else if (toReduce > 0) {
	                    divNewHeight =  divTableContainerHeight + toReduce;
	
	                    if (tableSize * rowHeight < divNewHeight) {
	                        divTableContainer.setStyle({height: "auto"});
	                    }
	                    else {
	                        newTbodyHeight =  divNewHeight - theadHeight;
	                        TableKit.setTbodyHeight(tbody, newTbodyHeight);
	                        divTableContainer.setStyle({height: "auto"});
	                    }	
	
	                }
	            }
        	}
            
            if (activeTabId && content.identify().startsWith("management_") && content.identify() !== activeTabId) {
                content.hide();
            } 
        }
    }
}

document.observe("dom:loaded", TableResize.load);    
