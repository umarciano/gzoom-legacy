GeneralChangeAjaxUpdateAreaInterAppContextLink = {
    load: function() {
        GeneralChangeAjaxUpdateAreaInterAppContextLink.loadMeasureContext($$('${contextName}'));       
    },
    
    loadMeasureContext : function(contextName) {
    
    	contextNameFirst = contextName.first();
    	if (contextNameFirst) {
        
        	href = contextNameFirst.readAttribute('href');
                	
        	subInterApp = href.substring(href.lastIndexOf('interApp='));
        	moduloEnd = '/' + subInterApp.substring(subInterApp.indexOf('=')+1, subInterApp.indexOf('&'));
        	
        	subInterAppBack = href.substring(href.lastIndexOf('interAppBack='));
        	moduloStart = '/' + subInterAppBack.substring(subInterAppBack.indexOf('=')+1, subInterAppBack.indexOf('&'));
        	
        	
        	target = moduloStart + '/control/emptyView';
        	
			contextNameFirst.onPreClickFunction = function(callback) {
	        		new Ajax.Request(
        				target, {
        					method: 'post',
	        				parameters: {
        						saveView: 'Y',
        						ajaxRequest: 'Y'
    						},
	        				onComplete: function() {
	        					if (callback)
									callback();
        					}
						}
    				);
    				
    				//Per riselezionare il primo folder quando torno indietro
                    var jar = new CookieJar({path : "/"});
                    jar.removeRegexp("activeLink");
			};        	
        	
        	href =  href.replace(moduloStart, moduloEnd);        	
        	contextNameFirst.writeAttribute('href', href );  
        	
        	onClickFunction = contextNameFirst.onClickFunction;
        	onClickFunction =  onClickFunction.replace(moduloStart, moduloEnd);        	
        	contextNameFirst.onClickFunction = onClickFunction;

        	
         }
    }  
}
GeneralChangeAjaxUpdateAreaInterAppContextLink.load();
