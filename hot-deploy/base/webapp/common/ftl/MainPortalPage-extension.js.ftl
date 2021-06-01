ShowPortalPage = {
    load : function() {
        var formScreenlet = $('management-form-screenlet');
        if (Object.isElement(formScreenlet)) {
        	var titleBar = formScreenlet.down('div.screenlet-title-bar');
        	if (Object.isElement(titleBar)) {
        		titleBar.hide();
        	}
        	
        	//gestiona pulsante del menu
        	var mainContainer = $j("div.right-column");
            var leftCol = $j("div#left-bar-container");
            if (mainContainer.hasClass("right-column-fullopen")) {
                $j("li.open").hide();
                $j("li.close").show();
            } 
        }
        
        
    }
}
    
ShowPortalPage.load();
