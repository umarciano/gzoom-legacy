GeneralChangeAjaxUpdateAreaInterApp = {
    load: function() {
        <#if tableName?has_content>
        GeneralChangeAjaxUpdateAreaInterApp.loadHyperlink($("${tableName}"));
        </#if>
    },
    
    loadHyperlink : function(table) {
    	
        if (table) {
        	//utilizzato per il link della lista
        	list = table.select("tr");
            list.each(function(element, index){                
                aElement = element.select("a");                
            	if((index != 0) && aElement.size() != 0){
            		aElement = aElement.first();

	             	href = aElement.readAttribute('href');
	            	
		        	modulo = "/" + href.substring(href.lastIndexOf('=')+1, href.lenght);	
		        	
		        	href =  href.replace('/gzoom', modulo);
		        	aElement.writeAttribute('href', href );  
		        		
		        	onClickFunction = aElement.readAttribute('onClick');		        	
		        	onClickFunction =  onClickFunction.replace('/gzoom', modulo);		        	
		        	aElement.writeAttribute('onClick', onClickFunction );
            	}
            	
            });
         }
    }
}

GeneralChangeAjaxUpdateAreaInterApp.load();
