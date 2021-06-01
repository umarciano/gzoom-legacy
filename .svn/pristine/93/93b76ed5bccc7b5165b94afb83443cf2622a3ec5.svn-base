GlAccountBackInterApp = {
    load: function() {
        GlAccountBackInterApp.loadBack($$('#back'));
    },
    
    loadBack : function(backs) {
        if (backs) {
        
        	backs.each(function(back){
	        	href = back.readAttribute('href');
	        	
	        	href =   href.replace('/accountingext', '/'+'${(parameters.interApp)?if_exists}');
	        	back.writeAttribute('href', href ); 
	        	
	        	onClickFunction = back.readAttribute('onClick');
	        	onClickFunction = onClickFunction.replace('/accountingext', '/' + '${(parameters.interApp)?if_exists}');
	        	back.writeAttribute('onClick', onClickFunction );
        	
        	});
         }
    }  
}

<#if (parameters.interApp)?has_content>
GlAccountBackInterApp.load();
</#if>

