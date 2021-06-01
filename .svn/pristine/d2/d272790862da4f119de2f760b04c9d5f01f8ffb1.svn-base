GeneralChangeBackInterApp = {
    load: function() {
        GeneralChangeBackInterApp.loadBack($$('#back'));
    },
    
    loadBack : function(backs) {
        if (backs) {
        	backs.each(function(back){
	        	href = back.readAttribute('href');
	        	
	        	href =   href.replace('/'+'${(parameters.interApp)?if_exists}', '/'+'${(parameters.interAppBack)?if_exists}');
	        	back.writeAttribute('href', href ); 
	        	
	        	onClickFunction = back.readAttribute('onClick');
	        	onClickFunction = onClickFunction.replace('/'+'${(parameters.interApp)?if_exists}', '/' + '${(parameters.interAppBack)?if_exists}');
	        	back.writeAttribute('onClick', onClickFunction );
        	
        	});
         }
    }  
}

<#if (parameters.interApp)?has_content && (parameters.interAppBack)?has_content>
GeneralChangeBackInterApp.load();
</#if>

