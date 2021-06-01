Paginator = {
    paginators: new Array(),

    load: function(newContentToExplore, withoutResponder) {
        // load with many different newContentToExplore 
        
        // newContentToExplore is null only the first time
        // then is the new div
        var newContentToExplore = Object.isElement($(newContentToExplore)) ? $(newContentToExplore) : $(document.body);
        // console.log("Paginator : load newContentToExplore " + newContentToExplore.id);
        
        if (document.getElementsByClassName == undefined) {
            document.getElementsByClassName = function(className)
                {
                    var hasClassName = new RegExp("(?:^|\\s)" + className + "(?:$|\\s)");
                    var allElements = document.getElementsByTagName("*");
                    var results = [];

                    var element;
                    for (var i = 0; (element = allElements[i]) != null; i++) {
                        var elementClass = element.className;
                        if (elementClass && elementClass.indexOf(className) != -1 && hasClassName.test(elementClass))
                            results.push(element);
                    }

                    return results;
                }
        }

        // Search only in new div to explore
        var first = newContentToExplore.select("li.nav-first");
        var previous = newContentToExplore.select("li.nav-previous");
        var next = newContentToExplore.select("li.nav-next");
        var last = newContentToExplore.select("li.nav-last");

        Paginator.fillPaginator(first, 0);
        Paginator.fillPaginator(previous, 1);
        Paginator.fillPaginator(next, 2);
        Paginator.fillPaginator(last, 3);

		// the first time withoutResponder is undefined, so register load with name 'paginator'.
        if (!withoutResponder) {
                UpdateAreaResponder.Responders.register( {
                    onAfterLoad : function(newContent) {
                            Paginator.load(newContent, true);
                    }
                }, 'paginator');
        }
    },

    getPaginatorNumber: function(anchor)  {
        //var href = anchor.href;
        var href = anchor.getAttribute("onclick");
        if (href)
            return Paginator.getPropertyValue(href, "PAGINATOR_NUMBER");
        else
            return null;
    },

    fillPaginator: function(toInsert, inPosition) {
        for(i = 0; i < toInsert.length; i++) {
            var li = $(toInsert[i]);
            var paginatorNumber = Paginator.getPaginatorNumber(li.firstChild);
            if (!this.paginators[paginatorNumber])
                this.paginators[paginatorNumber] = new Array(4);
            this.paginators[paginatorNumber][inPosition] = toInsert[i].firstChild;
            var anchor = toInsert[i].firstChild;
            // Observe click, and check modification
            Event.observe(anchor, "click", function(e) {
                Paginator.writeCookie(this);
				// search only in active container
                var myTabs = Control.Tabs.instances[0];
                var containerSelected = $('main-container');
                if(myTabs){
                    containerSelected = $(myTabs.getActiveContainer());
                }
                var cachableForms = $(containerSelected).select("form.cachable");
                // console.log("Paginator : FormKitExtension.checkModficationWithAlert for " + cachableForms);
                for (i = 0; i < cachableForms.length; i++) {
                    cachableForm = cachableForms[i];
                    // console.log("Paginator : cachableForms " + cachableForm.id);
                    // console.log("Paginator : init resultcheckModification " + resultcheckModification);
                    var resultcheckModification = FormKitExtension.checkModficationWithAlert(cachableForms[i]);
                    // console.log("Paginator : FormKitExtension.checkModficationWithAlert(" + cachableForm.id + ") = " + resultcheckModification);
                    // if resultcheckModification false there is not modification or the user click "Cancel"
                    // else resultcheckModification is the object with modification
                }
                return true;           
            });
        }
    },

    writeCookie: function(anchor) {
    	var paginatorNumber = Paginator.getPaginatorNumber(anchor);
        var jar = new CookieJar({path : "/"});
        var onclickAttribute = anchor.getAttribute("onclick");
		var entityName = Paginator.getPropertyValue(onclickAttribute, "entityName");
        var prop = Paginator.getPropertyValue(onclickAttribute, "VIEW_INDEX_" + paginatorNumber);
        
        //aggiungo per il caso di folder con la stessa entityName
        var folderIndex = Paginator.getPropertyValue(onclickAttribute, "folderIndex");
        if (folderIndex != null && folderIndex != "") {
        	entityName = entityName + "_" + folderIndex;
        }

        if (jar.get(entityName + "_VIEW_INDEX_" + paginatorNumber)) {
            jar.remove(entityName + "_VIEW_INDEX_" + paginatorNumber);
        }
        jar.put(entityName + "_VIEW_INDEX_" + paginatorNumber, prop);
    },

    getPropertyValue: function(string, propertyName) {
        var propertyIndex = string.lastIndexOf(propertyName);
        if (propertyIndex < 0) {
            return null;
        }
        var sub = string.substring(propertyIndex + propertyName.length + "=".length, string.length - 2);
        var andIndex = sub.indexOf("&");
        if (andIndex != -1) {
            sub = sub.substring(0, andIndex);
        }
        else {
        	var lastProperty = sub.indexOf("');");
        	if (lastProperty != -1) {
        		sub = sub.substring(0, lastProperty);
        	}
        }

        return sub;
    }
};

document.observe("dom:loaded", Paginator.load);