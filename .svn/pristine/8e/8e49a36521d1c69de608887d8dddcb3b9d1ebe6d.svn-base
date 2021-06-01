/* protoload 0.1 beta by Andreas Kalsch
 * last change: 09.07.2007
 *
 * This simple piece of code automates the creating of Ajax loading symbols.
 * The loading symbol covers an HTML element with correct position and size - example:
 * $('myElement').startWaiting() and $('myElement').stopWaiting()
 */
 
Protoload = {
	// the script to wait this amount of msecs until it shows the loading element
	timeUntilShow: 250,
	
	// Start waiting status - show loading element
	startWaiting: function(element, className, timeUntilShow) {
		if (typeof element == 'string')
			element = document.getElementById(element);
		
		var offset = Utils.getOffset(element);
		var left = offset.left,
		 top = offset.top,
		 width = element.offsetWidth,
		 height = element.offsetHeight;
		
		if (timeUntilShow == undefined)
			timeUntilShow = Protoload.timeUntilShow;
		
		element._waiting = true;
		if (!element._loading) {
			var e = document.createElement('div');
			(element.offsetParent || document.body).appendChild(element._loading = e);
		}
		if (className == undefined) {
			className = 'waiting';
			element._loading.style.left = left+'px';
			element._loading.style.top = top+'px';
			element._loading.style.width = width+'px';
			element._loading.style.height = height+'px';
			element._loading.style.display = 'inline';
		}
		
		element._loading.className = className;
	},
	
	// Stop waiting status - hide loading element
	stopWaiting: function(element) {
		if (element._waiting) {
			element._waiting = false;
			element._loading.parentNode.removeChild(element._loading);
			element._loading = null;
		}
	}
};

if (Prototype) {
	Element.addMethods(Protoload);
	Object.extend(Element, Protoload);
}
/* */