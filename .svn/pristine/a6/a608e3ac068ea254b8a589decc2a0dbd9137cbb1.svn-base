TreeMenuResize = {
	load: function() {
		if (Object.isElement($("mainmenu-container"))) {
			var mainmenuUpperToolbar = $("mainmenu-container").down("div#mainmenu-upper-toolbar");
			if (Object.isElement(mainmenuUpperToolbar)) {
				TreeMenuResize.registerModuleListeners();
			}
		}
	},
	
	registerModuleListeners: function() {
		TreeMenuMgr.Responders.register(TreeMenuResize.responder, "TreeMenuResize");
	},
	
	responder: {
		onModuleChange: function() {
			TreeMenuResize.resizeMenu();
		//	Utils.stopObserveEvent($("mainmenu-container").select(".expander"), "click", TreeMenuResize.expanderClickHandler);
			Utils.observeEvent($("mainmenu-container").select(".expander"), "click", TreeMenuResize.expanderClickHandler);
		},
		
		onAfterToggle: function() {
			TreeMenuResize.afterToggle();
		}
	}, 
	
	resizeMenu: function() {
		var mainmenuBodyLinks = $("mainmenu-body-links");
		if (Object.isElement(mainmenuBodyLinks)) {
			mainmenuBodyLinks.setStyle({height: "auto"});
			var viewportHeight = document.viewport.getHeight();
			var mainmenuBody = $("mainmenu-body");
		    var mainmenuBodyLinksPosition = Utils.findPosition(mainmenuBodyLinks)[1];
		    var oldMainmenuBodyLinksHeight = mainmenuBodyLinks.getHeight();
			var newMainmenuBodyLinksHeight = viewportHeight - mainmenuBodyLinksPosition;
			
		//	log.debug("----------- viewportHeight = " + viewportHeight);
		//	log.debug("----------- mainmenuBodyLinksPosition = " + mainmenuBodyLinksPosition);
		//	log.debug("----------- oldMainmenuBodyLinksHeight = " + oldMainmenuBodyLinksHeight);
		//	log.debug("----------- newMainmenuBodyLinksHeight = " + newMainmenuBodyLinksHeight);
			
			if (newMainmenuBodyLinksHeight < oldMainmenuBodyLinksHeight) {
				mainmenuBodyLinks.setStyle({"height": newMainmenuBodyLinksHeight - 4 + "px"});
			}
		}
	},
	
	afterToggle: function() {
		TreeMenuResize.resizeMenu();
	},
	
	expanderClickHandler: function(event) {
		TreeMenuResize.resizeMenu();
	}

}

document.observe("dom:loaded", TreeMenuResize.load);