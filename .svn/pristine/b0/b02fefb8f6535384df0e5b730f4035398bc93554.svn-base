RegisterKeypress = {
	load : function() {
		document.onkeydown = function(event) {
			
			var holder;
			// IE uses this
			if (window.event) {
				holder = window.event.keyCode;
				if(window.event.srcElement.tagName == 'TEXTAREA')
					return true;			
				
			}
			// FF uses this
			else {
				holder = event.which;
				if (event.target.nodeName == 'TEXTAREA')
					return true;				
				
			}

			return holder != 13
		}
	}
}

document.observe("dom:loaded", RegisterKeypress.load);