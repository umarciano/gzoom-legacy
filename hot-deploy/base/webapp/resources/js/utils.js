Utils = {

    findPosition: function (oElement) {
          if( typeof( oElement.offsetParent ) != "undefined" ) {
            for( var posX = 0, posY = 0; oElement; oElement = oElement.offsetParent ) {
                  posX += oElement.offsetLeft;
                  posY += oElement.offsetTop;
            }
            return [ posX, posY ];
          } else {
            return [ oElement.x, oElement.y ];
            }
    },
    
    getOffset: function(oElement) {
    	var positionedOffset = {};
    	if(oElement.positionedOffset) {
    		var arr = oElement.positionedOffset();
    	    positionedOffset.left = arr[0];
    	    positionedOffset.top = arr[1];
    	} else {
    	    positionedOffset.left = oElement.offsetLeft;
    	    positionedOffset.top = oElement.offsetTop;
    	}   	
    	return positionedOffset;
    },

    isVisible: function(element) {
        if (element == document) return true;

        if (Object.isElement(element)) {
            if (element.getStyle("display") == "none") return false;
            if (element.getStyle("visibility") == "hidden") return false;

            return Utils.isVisible(element.parentNode);
        }
        else return false;

        return true;
    },

    showModalBox: function(content, options) {
        if (typeof Modalbox != 'undefined') {
            options = options || {};
            options.afterLoadModal = options.afterLoadModal || Prototype.K;
            options.afterHideModal = options.afterHideModal || Prototype.K;
            if (!options.resizeToContent) {
	            options.width = options.width || (document.viewport.getWidth() - (document.viewport.getWidth() * 0.20));
	            options.height = options.height || (document.viewport.getHeight() - (document.viewport.getHeight() * 0.20));
            }

            if (Object.isString(content)) {
                var htmlRegExp = new RegExp(/<\/?[^>]+>/gi);
                if(htmlRegExp.test(content)) { // Plain HTML given as a parameter
                    Modalbox.show(content, {title: options.title || ' ', width: options.width, height: options.height,
                                    afterLoad: options.afterLoadModal, load : options.loadModal || Prototype.K, beforeHide: options.beforeHideModal, afterHide : options.afterHideModal});

                } else {
                    var url = content;
                    if (url.indexOf('?') != -1) {
                        options.parameters = url.toQueryParams();

                        url = url.substring(0, url.indexOf('?'));
                    }

                    options.onComplete = function(request) {
                        Modalbox.show(request.responseText, {title: options.title || ' ', width: options.width, height: options.height,
                                afterLoad: options.afterLoadModal, load : options.loadModal || Prototype.K, afterHide : options.afterHideModal});
                    }.bind(this);

                    new Ajax.Request(url, options);
                }
            } else if (typeof this.content == 'object') {// HTML Object is given
                Modalbox.show(content, {title: options.title || ' ', width: formWidth, height: options.height,
                                    afterLoad: options.afterLoadModal, load : options.loadModal || Prototype.K, afterHide : options.afterHideModal});
            }
        }
    },

    hideModalBox: function(options) {
        if (typeof Modalbox != 'undefined') {
            try {
                if (Object.isUndefined(options)) {
                    options = {};
                }
                options['element'] = 'hide';
                Modalbox.hide(options);
            } catch (e) {}
        }
    },

    removejscssfile : function (filename, filetype){
        var targetelement=(filetype=="js")? "script" : (filetype=="css")? "link" : "none" //determine element type to create nodelist from
        var targetattr=(filetype=="js")? "src" : (filetype=="css")? "href" : "none" //determine corresponding attribute to test for
        var allsuspects=document.getElementsByTagName(targetelement);
        for (var i=allsuspects.length; i>=0; i--){ //search backwards within nodelist for matching elements to remove
            if (allsuspects[i] && allsuspects[i].getAttribute(targetattr)!=null && allsuspects[i].getAttribute(targetattr).indexOf(filename)!=-1)
                allsuspects[i].parentNode.removeChild(allsuspects[i]) //remove element by calling parentNode.removeChild()
        }
    },

    createjscssfile : function(filename, filetype){
        if (filetype=="js"){ //if filename is a external JavaScript file
            var fileref=document.createElement('script')
            fileref.setAttribute("type","text/javascript")
            fileref.setAttribute("src", filename)
        }
        else if (filetype=="css"){ //if filename is an external CSS file
            var fileref=document.createElement("link")
            fileref.setAttribute("rel", "stylesheet")
            fileref.setAttribute("type", "text/css")
            fileref.setAttribute("href", filename)
        }
        return fileref
    },

    replacejscssfile : function(oldfilename, newfilename, filetype){
        var targetelement=(filetype=="js")? "script" : (filetype=="css")? "link" : "none" //determine element type to create nodelist using
        var targetattr=(filetype=="js")? "src" : (filetype=="css")? "href" : "none" //determine corresponding attribute to test for
        var allsuspects=document.getElementsByTagName(targetelement)
        for (var i=allsuspects.length; i>=0; i--){ //search backwards within nodelist for matching elements to remove
            if (allsuspects[i] && allsuspects[i].getAttribute(targetattr)!=null && allsuspects[i].getAttribute(targetattr).indexOf(oldfilename)!=-1){
                var newelement=createjscssfile(newfilename, filetype)
                allsuspects[i].parentNode.replaceChild(newelement, allsuspects[i])
            }
        }
    },
    loadjscssfile : function(filename, filetype){
        fileref = Utils.createjscssfile(filename, filetype)
        if (typeof fileref!="undefined")
            document.getElementsByTagName("head")[0].appendChild(fileref)
    },
    getjscssfile : function(filename, filetype){
        var res = false;
        var targetelement=(filetype=="js")? "script" : (filetype=="css")? "link" : "none" //determine element type to create nodelist using
        var targetattr=(filetype=="js")? "src" : (filetype=="css")? "href" : "none" //determine corresponding attribute to test for
        var allsuspects=$A(document.getElementsByTagName(targetelement));
        res = allsuspects.find(function(elm) {
            return elm && elm.getAttribute(targetattr)!=null && elm.getAttribute(targetattr).indexOf(filename)!=-1;
        });

        return res;
    },

    observeEvent: function(objectsArray, eventName, handlerFunction) {
        if (Object.isString(eventName) && Object.isFunction(handlerFunction)) {
            objectsArray.each(function(object) {
                if (Object.isElement(object)) {
                    Event.observe(object, eventName, handlerFunction);
                }
            });
        }
    },

    stopObserveEvent: function(objectsArray, eventName, handlerFunction) {
    	objectsArray.each(function(object) {
        	if (Object.isElement(object)) {
            	object.stopObserving(eventName, handlerFunction);
            }
        });
    },
    
    evalFunc : function(str) {
        eval('var evalFuncTmp = function() { ' + str + '; }');
        return evalFuncTmp();
    },

    evalBool : function(str) {
        return (Utils._evalFunc(str) != false);
    },
    
    startWaiting: function() {  
    	document.body.startWaiting('waitingTrasp', -1);
    },
    
    stopWaiting: function() {  
    	document.body.stopWaiting('waitingTrasp');
    }
}
