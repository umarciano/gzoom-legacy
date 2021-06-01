WorkEffortMeasureGlAccountExtension = {
        // Used in contextLink WorkEffortMeasureGlAccount for add emptyView
        load: function() {
        WorkEffortMeasureGlAccountExtension.loadMeasureContext($$('.workEffortMeasureGlAccount${accountTypeEnumId?if_exists}'));
    },
    
    loadMeasureContext : function(measureGlAccount) {
    	measureGlAccountFirst = measureGlAccount.first();
        if (measureGlAccountFirst) {
        
        	href = measureGlAccountFirst.readAttribute('href');
        	modulo = "/" + href.substring(href.lastIndexOf('=')+1, href.lenght);
        	target = modulo + '/control/emptyView';
        	
			measureGlAccountFirst.onPreClickFunction = function(callback) {
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

        	
        	
        	href =  href.replace(modulo, '/accountingext');
        	measureGlAccountFirst.writeAttribute('href', href );   
        		
        	onClickFunction = measureGlAccountFirst.onClickFunction;
        	onClickFunction =  onClickFunction.replace(modulo, '/accountingext');
        	
        	measureGlAccountFirst.onClickFunction = onClickFunction;
        	
         }
    }  
}

WorkEffortMeasureGlAccountExtension.load();
