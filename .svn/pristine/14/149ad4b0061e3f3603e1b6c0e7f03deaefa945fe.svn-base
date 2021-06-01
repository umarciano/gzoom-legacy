/* Popup Manager */
var PopupMgr = Class.create({ });

PopupMgr.defaultIcon = "/images/defaultIcon.png";
PopupMgr.parentForm = null;

/**
 * Create structure for every popup in form URSMM001
 */
PopupMgr.loadElement = function(newContent, withoutResponder) {
    var newContent = Object.isElement($(newContent)) ? $(newContent) : $(document.body);
    
	//Cerco gli ancho di limk senza content
	if (Object.isElement(newContent)) {
		var iconNoContentFieldArray = newContent.select("a.icon-no-content");
		if (iconNoContentFieldArray.size() > 0) {
			PopupMgr.parentForm = iconNoContentFieldArray[0].up('form');
			iconNoContentFieldArray.each(function(anchorElement) {
				Event.observe(anchorElement, "click", PopupMgr.onClick.bind(anchorElement));
			});
		}
		var iconWithContentFieldArray = newContent.select("a.icon-with-content");
		if (iconWithContentFieldArray.size() > 0) {
			if (!PopupMgr.parentForm || !Object.isElement($(PopupMgr.parentForm.identify()))) {
				PopupMgr.parentForm = iconWithContentFieldArray[0].up('form');
			}
			PopupMgr.buildContextMenu();
			Utils.observeEvent(new Array($(document.body)), "click", PopupMgr.hideContextMenu);
			Utils.observeEvent(iconWithContentFieldArray, (Prototype.Browser.Opera ? "click" : "contextmenu"), PopupMgr.showContextMenu);
		}
		var iconFieldArray = newContent.select("a.icon");
		if (iconFieldArray.size() > 0) {
			PopupMgr.buildContextMenu();
			iconFieldArray.each(function(anchorElement) {
				Event.observe(anchorElement, "click", PopupMgr.onClick.bind(anchorElement));
			});
			Utils.observeEvent(new Array($(document.body)), "click", PopupMgr.hideContextMenu);
			Utils.observeEvent(iconFieldArray, (Prototype.Browser.Opera ? "click" : "contextmenu"), PopupMgr.showContextMenu);
		}
	}
    
     if (!withoutResponder) {
     	UpdateAreaResponder.Responders.register( {
        	onAfterLoad : function(c) {
        	    PopupMgr.loadElement(c);
               }
            }, 'popup');
     }
}

PopupMgr.onClick = function(event) {
	Event.stop(event);
	var parms = $H({});
    var href = "";
    var element = this;
    
    var viewWidth = document.viewport.getWidth() - (document.viewport.getWidth() * 0.30);
    var viewHeight = document.viewport.getHeight() - (document.viewport.getHeight() * 0.45);
    var title = "Choices";
    
    if (element.tagName === "A") {
    	href = element.readAttribute("href");
    	if (href.indexOf('?') != 1) {
	    	var hrefMap = $H(href.toQueryParams());
	    	if (!Object.isUndefined(hrefMap.get('viewWidth'))) {
	    		viewWidth = hrefMap.get('viewWidth');
	    		hrefMap.unset('viewWidth');
	    	}
	    	if (!Object.isUndefined(hrefMap.get('viewHeight'))) {
	    		viewHeight = hrefMap.get('viewHeight');
	    		hrefMap.unset('viewHeight');
	    	}
	    	if (!Object.isUndefined(hrefMap.get('title'))) {
	    		title = hrefMap.get('title');
	    		hrefMap.unset('title');
	    	}
	    	href = href.substring(0, href.indexOf('?')+1) + hrefMap.toQueryString();
    	}
    }
    else {
    	return;
    }
    
    var options = {'title': title, width: viewWidth, height: viewHeight,
      				afterLoadModal: PopupMgr.afterLoadModal, beforeHideModal: PopupMgr.beforeHideModal, afterHideModal: PopupMgr.afterHideModal};
    Utils.showModalBox(href, options);
   
    PopupMgr.setActiveIconField(element);
}

PopupMgr.afterLoadModal = function() {
    // sulla ModalBox bisogna mettersi in ascolto della submit della form e creare la lista di icone
    var uploadForm = $("ContentUpload");
    if (!Object.isElement(uploadForm)) {
    	uploadForm = $('MB_content').down(".content-upload-form");
    }
    if(Object.isElement(uploadForm)) {
    	Utils.stopObserveEvent(new Array($(document.body)), "click", null);
    	Utils.observeEvent(new Array(uploadForm), "submit", PopupMgr.SubmitNewImage);
     	PopupMgr.renderListIcon();   
     	LookupProperties.afterLoadModal({	registerMenu: false,
	    									registerDropList: false,
	    									registerTabs: true,
	    									registerLookup: false,
	    									registerTable: false,
	    									registerForm: false,
	    									registerAjaxReloading: false,
	    									registerForcusEvent: false,
	    									registerCalendar: false});
    }
}

PopupMgr.beforeHideModal = function() {
	LookupProperties.afterHideModal({	registerMenu: false,
	    								registerDropList: false,
	    								registerTabs: true,
	    								registerLookup: false,
	    								registerTable: false,
	    								registerForm: false,
	    								registerForcusEvent: false,
	    								registerCalendar: false});
}

PopupMgr.afterHideModal = function() {
	Utils.observeEvent(new Array($(document.body)), "click", PopupMgr.hideContextMenu);
	LookupProperties.afterHideModal({	registerAjaxReloading: false});
}

PopupMgr.delegateDblClickEvent = function(table, e) {
    if (typeof PopupMgr != "undefined")
        //Delegate to PopupMgr
        PopupMgr.rowSelectedHandler(table, e);
}

PopupMgr.SubmitNewImage = function(event) {
    Event.stop(event);
    var uploadForm = $("ContentUpload"); 
    if (!Object.isElement(uploadForm)) {
    	uploadForm = $('MB_content').down(".content-upload-form");
    }
    if (Object.isElement(uploadForm)) {
    	var targetFrame = $("target_upload");
    	if(!Object.isElement(targetFrame)){
    		var targetFrame = new Element("iframe", {"id": "target_upload", "name": "target_upload"});
    		targetFrame.setStyle({display: "none"});
    		uploadForm.insert(targetFrame);
    		if (!uploadForm.hasClassName('reload-parent-table')) {
    			Utils.observeEvent(new Array(targetFrame), "load", PopupMgr.uploadCompleted);
    		} else {
    			Utils.observeEvent(new Array(targetFrame), "load", PopupMgr.uploadCompletedReloadParent);
    		}
    	}
    	uploadForm.target="target_upload";
    	uploadForm.submit();
    }
}

PopupMgr.uploadCompleted = function(event){
	LookupProperties.afterHideModal({	registerMenu: false,
	    								registerDropList: false,
	    								registerTabs: true,
	    								registerLookup: false,
	    								registerTable: false,
	    								registerForm: false,
	    								registerAjaxReloading: true,
	    								registerForcusEvent: false,
	    								registerCalendar: false});
	    								
    var parms = "lookup=Y&lookupScreenLocation=component://base/widget/BaseScreens.xml&actionMenuName=PopupMenuBar&searchTabMenuName=MainPopupTabBar&noConditionFind=Y&entityName=Content&contentTypeId=ICON";
	new Ajax.Updater("main-lookup-container-screenlet", "popupContentUpload", {parameters: parms,
																			   evalScripts: true, 
																			   onComplete: PopupMgr.afterLoadModal});
}

PopupMgr.uploadCompletedReloadParent = function(event){
	LookupProperties.afterHideModal({	registerMenu: false,
	    								registerDropList: false,
	    								registerTabs: true,
	    								registerLookup: false,
	    								registerTable: false,
	    								registerForm: false,
	    								registerAjaxReloading: true,
	    								registerForcusEvent: false,
	    								registerCalendar: false});
	    								
    if (Object.isElement(PopupMgr.parentForm)) {
    	var script = PopupMgr.parentForm.readAttribute('onsubmit');
    	
    	PopupMgr.evalFunc(script);
    }
    Utils.hideModalBox();
}
    
PopupMgr.setActiveIconField = function(a) {
    PopupMgr._lastIconField = a;
}

PopupMgr.evalFunc = function(str) {
    eval('var evalFuncTmp = function() { ' + str + '; }');
    return evalFuncTmp();
}

/**
 * Get Active IconField
 */
PopupMgr.getActiveIconField = function() {
    if (PopupMgr._lastIconField) {
        return PopupMgr._lastIconField;
    }
    return;
}

PopupMgr.renderListIcon = function() {
    CanvasGallery.elementsNum = 0;
	$("main-container").setStyle({overflow: "hidden"});
    if (typeof TableKit != "undefined") {
        var divImageContainer = $("");
        var table;
        var searchListContainer = $("searchListContainer");
        if (Object.isElement(searchListContainer)) {
        	table = searchListContainer.down("table.selectable");
        }
        if (Object.isElement(table)) {
    	    TableKit.register(table, {resizable: true, draggable: true, toggleable: true, selectable: true, customizable: true, headerFixable: true, editable: false, sortable:false});
        	TableKit.registerObserver(table, "onDblClickSelectEnd", PopupMgr.delegateDblClickEvent);
        	TableKit.reloadTable(table);
        	table.addClassName("hidden");
        	divImageContainer = table.up("div");
        	/*
        	var al = new Element("a", {"id" : "ml", "class" : "scroll-left fa fa-2x"}); 
        	var ar = new Element("a", {"id" : "md", "class" : "scroll-right fa fa-2x"}); 
        	
        	var scrollLabel = new Element("span", {"id": "scrollLabel", "class": "label"});
        	modal_box_messages._loadMessage(null, "scrollLabel", function(msg) {
                scrollLabel.innerHTML = msg;
            });
        	
        	divImageContainer.insert({"before" : scrollLabel});
        	divImageContainer.insert({"before" : al});
        	divImageContainer.insert({"before" : ar});*/
        	if (Object.isElement($("img-holder"))) {
        		$("img-holder").remove();
        	}
       		var imgHolder = new Element("div", {"id" : "img-holder"});
       		divImageContainer.insert({"before" : imgHolder});
            
       		PopupMgr.numImages = TableKit.getBodyRows(table).size();
        	for(var row = 0; row < TableKit.getBodyRows(table).size(); row++) {
                var divImage = new Element("div", {"class" : "gallery-item"});
        		// var canvas = new Element("canvas", {"id": "canvas" + row.toString(), "width": "102", "height": "102"}); 
            	var id = "img"+row.toString();
            	var img = new Element("img", {"id" : id, "src" : TableKit.getRow(table, row).down("img").readAttribute("src")}); 
            	
            	var contentId;
            	var contentIdField = TableKit.getRow(table, row).down("input[name='contentId']");
            	contentId = contentIdField.getValue();
                var hiddenContentId = new Element("input", {"type" : "hidden", "name" : "contentId", "value" : contentId}); 
                imgHolder.insert(divImage); 
                divImage.insert(img);
                //divImage.insert(canvas);
                divImage.insert(hiddenContentId);
                Event.observe(img, "load", CanvasGallery.loadElement.curry(img));
            }
            
           // CanvasGallery.loadElement(); 
        }
    }
}

PopupMgr.rowSelectedHandler = function(table, event) {
    var parms = $H({});
    if (TableKit.isSelectable(table)) {
        var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
        if (selectedRow) {
            $A(selectedRow.select("input")).each( function(element) {
                var elementName = element.readAttribute("name");
                var elementValue = element.getValue();
                if(elementName === "contentId") {
                    parms.set("contentId", elementValue);
                }
            });
            $A(selectedRow.select("img")).each( function(element) {
                var src = element.readAttribute("src");
                parms.set("src", src);
            });
            var imageAnchor = PopupMgr.getActiveIconField();
            if (Object.isElement(imageAnchor)) {
                var img = imageAnchor.down("img");
                if(Object.isElement(img)) {
                    img.writeAttribute("src",parms.get("src"));
                }
                else 
                {
                    img = new Element("img", {"src" : parms.get("src")});
                    imageAnchor.down("a").innerHTML = "";
                    imageAnchor.down("a").insert(img);
                }
                var tr = imageAnchor.up("tr");
                if(Object.isElement(tr)) {
                	var targetField = tr.down("input.iconContentIdTargetField");
                	targetField.setValue(parms.get("contentId"));
                }
            }
            Modalbox.hide();
        }            
    }
}

PopupMgr.buildContextMenu = function() {
	var  container = new Element("div", {"id": "icon_contextmenu"});
    container.setStyle({
    	position : "absolute",
    	background : "#eee",
        zIndex : 99999
    });
    var eraseLink = new Element("a", {"id": "eraseLink", "class": "fa fa-trash-alt", "style": "color: gray"});
    var eraseText = new Element("p", {"id": "eraseText"});
    modal_box_messages._loadMessage(null, "defaultEraseText", function(msg) {
        eraseText.innerHTML = msg;
    }); 

    eraseLink.setStyle({"cursor": "pointer"});
    
    var table = new Element("table", {"id": "contextmenu-table"});
    var tbody = new Element("tbody");
    table.insert(tbody);
    var tr = new Element("tr");
    tbody.appendChild(tr);
    var td1 = new Element("td");
    td1.update(eraseText);
    tr.insert(td1);
    var td2 = new Element("td");
    td2.insert(eraseLink);
    tr.insert(td2);
   // container.insert(eraseText);
   // container.insert(eraseLink);
   	container.insert(table);
    container.hide();
    $(document.body).insert(container);
}

PopupMgr.showContextMenu = function(event) {
	Event.stop(event);
	var imageAnchor = Event.element(event);
	if (imageAnchor.tagName === "IMG") {
    	imageAnchor = imageAnchor.up("a");
    }
	var container = $("icon_contextmenu");
	if (Object.isElement(container)) {
		var eraseLink = container.down("a#eraseLink");
		Utils.stopObserveEvent(new Array(eraseLink), "click", null);
		Utils.observeEvent(new Array(eraseLink), "click", PopupMgr.eraseIcon.curry(imageAnchor));
    	container.show();
    	TableKit.Toggleable.positionContainer(container, Event.pointerY(event), Event.pointerX(event));
	}
}

PopupMgr.hideContextMenu = function(event) {
	//Event.stop(event);
	var container = $("icon_contextmenu");
	if (Object.isElement(container)) {
		container.hide();
	}
}

PopupMgr.eraseIcon = function(imageAnchor, event) {
	Event.stop(event);
	
	if (!Object.isElement(PopupMgr.parentForm)) {
		var img = imageAnchor.down("img");
	    if(Object.isElement(img)) {
	    	imageAnchor.addClassName("image_anchor");
	    	img.writeAttribute("src", PopupMgr.defaultIcon);
            img.writeAttribute("height", "24");
	    	img.writeAttribute("width", "24");
	    }
	    var table = imageAnchor.up("form").down("table");
		var multiForm = table ? table.hasClassName("multi-editable") : false;
		var iconContentIdTargetField;
		if (multiForm) {
			iconContentIdTargetField = imageAnchor.up("tr").down("input.iconContentIdTargetField");
		}
		else {
			iconContentIdTargetField = imageAnchor.up("form").down("input.iconContentIdTargetField");
		}
	    if (Object.isElement(iconContentIdTargetField)) {
	    	iconContentIdTargetField.setValue("");
		}
	    
	    PopupMgr.hideContextMenu(event);
	} else {
		PopupMgr.hideContextMenu(event);
		
		var table = imageAnchor.up("form").down("table");
		var multiForm = table ? table.hasClassName("multi-editable") : false;
		
		var parentContainer = null;
		if (multiForm) {
			parentContainer = imageAnchor.up("tr");
		}
		else {
			parentContainer = imageAnchor.up("form");
		}
		
		var iconContentIdTargetField = parentContainer.down("input.iconContentIdTargetField");
		if (Object.isElement(iconContentIdTargetField)) {
			var elementName = iconContentIdTargetField.readAttribute('name');
	    	iconContentIdTargetField.setValue("");
	    	
	    	var deleteContentField = parentContainer.down("input.deleteContentField");
			if (!Object.isElement(deleteContentField)) {
				var suffix = '';
				if (elementName.indexOf('_o_') != -1) {
					suffix = elementName.substring(elementName.indexOf('_o_'));
				}
				parentContainer.insert(new Element('input',  {type : 'hidden', name : 'deleteContent' + suffix, 'class' : 'deleteContentField', value : 'Y'}));
			} else {
				deleteContentField.setValue('Y');
			}
	    	
	    	var script = PopupMgr.parentForm.readAttribute('onsubmit');
	    	if (Object.isString(script)) {
	    		PopupMgr.evalFunc(script);
	    	}
		}
	}
}

CanvasGallery = Class.create({ });

/**
 * Create structure for 
 */
CanvasGallery.loadElement = function(baseElement) {

	var Gallery= new ReflectionObject;

	Gallery.init();
	
	Gallery.setOptions("borderColor","acacac");
	Gallery.setOptions("borderOnColor","ffff00");
	Gallery.setOptions("refWidth",100);
	Gallery.setOptions("refHeight",100);
	Gallery.setOptions("height",100);
	Gallery.setOptions("width",100);
	Gallery.setOptions("opacity",0.4);

/*	var images = $("img-holder").select("img");
	images.each(function(element) {
	    Gallery.addItem(element.readAttribute("id").substring(3));
	}); */
	
    if (Object.isElement(baseElement)) {
		Gallery.addItem(baseElement.readAttribute("id").substring(3));
	}
	else {
		var images = $("img-holder").select("img");
		images.each(function(element) {
	    	Gallery.addItem(element.readAttribute("id").substring(3));
	});
	}
}


//Yes, i adimt, this was bit inspired by moo.fx :]
var MyFX = Class.create({ });

MyFX.Methods = {
//set some basic options, like timeout and step for scroll
	// (less means smoother scroll)
	
    setFXOptions: function(element,timeout,step){
		var element=$(element);
		element.fxoptions={
			timeout:timeout,
			step:step,
			timeoutHandler:{},
			active:false
		}

	},

	scrolLeft: function(element,step){
		if(element.fxoptions.active) element.fxoptions.active=false;
		element=$(element);
		var margin=element.getStyle("marginLeft");
		margin=parseInt(margin.replace("px",""));
		margin=margin+element.fxoptions.step;
		element.setStyle({marginLeft:margin+"px"});
 
		element.fxoptions.timeoutHandler=setTimeout(element.scrolLeft.bind(element), element.fxoptions.timeout);

	},
	
	scrolRight: function(element,step){
		element=$(element);
		var margin=element.getStyle("marginLeft");
		margin=parseInt(margin.replace("px",""));
		margin=margin-element.fxoptions.step;
		element.setStyle({marginLeft:margin+"px"});

		element.fxoptions.timeoutHandler=setTimeout(element.scrolRight.bind(element), element.fxoptions.timeout);

	},
	
	stopScroll: function(element){
		element=$(element);
		clearTimeout(element.fxoptions.timeoutHandler);
		element.fxoptions.active=true;
	}
}


//Extend element methods with our FX functions that we use for scrolling
Element.addMethods(MyFX.Methods);

var ReflectionObject = function(){
	
	this.init=function(){
		this.options={};
		this.options["elementsNum"]=0;
	}
	
	this.setOptions=function(name,value){
		this.options[name]=value;
	}
	
	this.getOptions=function(name){
		return this.options[name];
	}
	
	
	this.start=function(){
	
	//	$("img-holder").setStyle({width:((this.options["elementsNum"]*this.options["width"])+(this.options["elementsNum"])*12)+"px"});
	//	$("img-holder").setStyle({marginLeft:-(((this.options["elementsNum"]*this.options["width"])+(this.options["elementsNum"])*12))/2+300+"px"});
	
		$("img-holder").setStyle({width:((PopupMgr.numImages*this.options["width"])+(PopupMgr.numImages)*12)+"px"});
		$("img-holder").setStyle({marginLeft:-(((PopupMgr.numImages*this.options["width"])+(PopupMgr.numImages)*12))/2+300+"px"});

		//Add event listeners on scroll buttons
		$("md").observe("mouseover",function(event){
			$("img-holder").scrolRight();
			
		});	
		$("md").observe("mouseout",function(event){ 
			$("img-holder").stopScroll();
		
		});	

		$("ml").observe("mouseover",function(event){
			$("img-holder").scrolLeft();
		});	
		$("ml").observe("mouseout",function(event){
			$("img-holder").stopScroll();
		});
		
	}

	this.addItem=function(imageNUMID){
		var image=$("img"+imageNUMID);
		var canvas = $("canvas"+imageNUMID);
        //this.BindFunction(image);
	//	this.options["elementsNum"]=this.options["elementsNum"]+1;
		CanvasGallery.elementsNum += 1;
		image.NUMID=imageNUMID;
		
		image.observe("click",function(event){
			
			var parms = $H({});
            var cell = Event.element(event);
            var selectedDiv = cell.up("div");
            var contentIdField = selectedDiv.down("input[name='contentId']");
            parms.set("contentId", contentIdField.getValue());
            
            var src = selectedDiv.down("img").readAttribute("src");
            parms.set("src", src);
            var imageAnchor = PopupMgr.getActiveIconField();
            if (Object.isElement(imageAnchor)) {
            	var table = imageAnchor.up("form").down("table");
		        var multiForm = table ? table.hasClassName("multi-editable") : false;
		        
                var img = imageAnchor.down("img");
                if(Object.isElement(img)) {
                	imageAnchor.removeClassName("image_anchor");
                	img.writeAttribute("max-height", "46");
                	img.writeAttribute("max-width", "46");
                    img.writeAttribute("src",parms.get("src"));
                }
                else 
                {
                    img = new Element("img", {"src" : parms.get("src")});
                    imageAnchor.removeClassName("image_anchor");
                    img.writeAttribute("max-height", "46");
                	img.writeAttribute("max-width", "46");
                  //  imageAnchor.innerHTML = ""; Why ???
                    imageAnchor.insert(img);
                }
                if (multiForm) { 
                	var tr = imageAnchor.up("tr");
                	var target = tr.down("input.iconContentIdTargetField");
                	target.setValue(parms.get("contentId"));
                }
                else {
                	var form = imageAnchor.up("form");
                	var iconContentIdField = form.down("input.iconContentIdTargetField");
                	if (Object.isElement(iconContentIdField)) {
                		iconContentIdField.setValue(parms.get("contentId"));
                	}
                }
            }
            Modalbox.hide();
    		
			var element=Event.element(event);
		})
	}
}
	
	ReflectionObject.prototype.BindFunction=function(image){
		
		var offColor=this.options["borderColor"];
		var onColor=this.options["borderOnColor"];
		
		offFunction=function(event) {
			var element = Event.element(event);
			element.setStyle({"border" :  "solid 1px #"+offColor});
		}
		
		onFunction=function(event) {
			var element = Event.element(event);
			element.setStyle({"border" :  "solid 1px #"+onColor});
		}
		
		$(image).observe("mouseout", offFunction);
		$(image).observe("mouseover", onFunction);
	}
	


//*****************
//Init object
//*****************
document.observe("dom:loaded", function() {
    if (!Object.isUndefined(PopupMgr)) {
        PopupMgr.loadElement();
    }
});
