// messages.js

var Messages = Class.create({

    _title: null,
    _alertTitle: null,
    _confirmTitle: null,

    _messageContext: null,
    _eventMessage: null,
    _errorMessage: null,
    _failMessage: null,

    _returnValue: false,

    initialize: function() {
    },

    value: function() {
        return _returnValue;
    },

    onPageLoad: function(callback) {
        var elem = $$(".message-boxes-container .message-title-container").first();
        this._title = (elem ? elem.innerHTML : null);
        elem = $$(".message-boxes-container .message-context-container").first();
        this._messageContext = (elem ? elem.innerHTML : null);
        elem = $$(".message-boxes-container .message-event-container").first();
        this._eventMessage = (elem ? elem.innerHTML : null);
        elem = $$(".message-boxes-container .message-error-container").first();
        this._errorMessage = (elem ? elem.innerHTML : null);
        this._checkMessages(callback);
    },

    onAjaxLoad: function(data, callback) {
        // data must be a json object
    	this._resetMessages();
    	if (data) {
	        this._messageContext = data["messageContext"];
	        var errorMessage = this._formatMessageList(data["_ERROR_MESSAGE_"], data["_ERROR_MESSAGE_LIST_"]);
	        if(errorMessage) {
	           this._errorMessage = errorMessage;
	           this._failMessage = null;
	        } else {
	           for(var key in data) {
	               if(key.indexOf("failMessage_o_") != -1) {
	                   if(!data["failMessageList"]) {
	                       data["failMessageList"] = [];
	                   }
	                   data["failMessageList"].push(data[key]);
	               }
	           }
	           
	           var failMessage = this._formatMessageList(data["failMessage"], data["failMessageList"]);
    	        if(failMessage) {
    	           this._failMessage = failMessage;
    	           this._errorMessage = null;
    	        }
    	        else {
    	           this._errorMessage = null;
    	           this._failMessage = null;
    	        }
	        }
	        if (!this._errorMessage && !this._failMessage) {
	            this._eventMessage = this._messageContext;
	        } else {
	            this._eventMessage = null;
	        }
	        this._checkMessages(callback);
    	}
    },

    _checkMessages: function(callback) {
        var msg = this._getErrorMessage();
        if (msg) {
            if (this._messageContext && this._messageContext != "")
                msg = [this._messageContext + "Error"];
        } else {
        	msg = this._getFailMessage();
        	if(msg) {
        		if (this._messageContext && this._messageContext != "")
                msg = [this._messageContext + "Fail"];
        	} else {
	            msg = this._getEventMessage();
	            if (msg) {
	                if (msg === this._messageContext)
	                    this._eventMessage = null;
	                if (this._messageContext && this._messageContext != "")
	                    msg = [this._messageContext + "Info"];
	            }
	        }
        }
        
        if (msg)
            this.alert(msg, null, callback);
        else if (callback)
            callback();
    },

    _resetMessages: function() {
        this._messageContext = null;
        this._eventMessage = null;
        this._errorMessage = null;
        this._failMessage = null;
    },

    _getErrorMessage: function() {
        return (this._errorMessage && this._errorMessage != "" ? this._errorMessage : null);
    },
    
     _getFailMessage: function() {
        return (this._failMessage && this._failMessage != "" ? this._failMessage : null);
    },

    _getEventMessage: function() {
        return (this._eventMessage && this._eventMessage != "" ? this._eventMessage : null);
    },

    _formatMessageList: function(msg, msgList) {
        var stringList = "";
        if (msg && msg != "")
            stringList = stringList + "<p>" + msg + "</p>\n";
        if (msgList) {
            for (var i = 0; i < msgList.length; i++) {
                    var element = msgList[i];
                    var message = element["message"];
                    if (message)
                        stringList += "<p>" + message + "</p>\n";
                    else
                        stringList += "<p>" + element + "</p>\n";
            }
        }
        return stringList;
    },

    messageCache: $H({}),

    _loadMessage: function(resource, property, callback) {
        var params = {};
        var key = '';
        if (resource) {
            params.resource = resource;
            key = resource;
        }
        if (property) {
            params.property = property;
            key += ':' + property;
        }

        if (this.messageCache.get(key)) {
            callback(this.messageCache.get(key));
        } else {
            new Ajax.Request('getMessage', {
                parameters: params,
                evalJSON: 'force',
                onSuccess: function(response) {
                    var obj = response.responseJSON;
                    if (obj && obj.value) {
                        if (Object.isString(obj.value)) {
                            obj = obj.value;
                            this.messageCache.set(key, obj);
                        }
                        else
                            obj = null;
                    } else {
                        obj = null;
                    }

                    callback(obj);
                }.bind(this)
            });
        }
    },

    _getMessage: function(obj, callback) {
        if (Object.isArray(obj)) {
            if (obj.length >= 2)
                this._loadMessage(obj[0], obj[1], callback);
            else if (obj.length >= 1)
                this._loadMessage(null, obj[0], callback);
        } else {
            if (!obj)
                obj = "[message]";
            callback(obj);
        }
    },

    _getMessageBoxOrginalContainer : function() {
        return $$(".message-boxes-container .message-box-container").first();
    },

    _getButton: function(container, buttonName) {
        return (container ? container.down(".button-" + buttonName) : null);
    },

    _getIcon: function(container) {
        return (container ? container.down(".body-container") : null);
    },

    _getTextContainer: function(container) {
        return (container ? container.down(".text-container") : null);
    },

    _getTextInformationsContainer: function(container) {
        return (container ? container.down(".text-informations-container") : null);
    },

    _showButton: function(container, buttonName, flag) {
        var button = this._getButton(container, buttonName);
        if (button) {
            if (flag)
                button.show();
            else
                button.hide();
        }
    },

    _showIcon: function(container, iconName, flag) {
        var icon = this._getIcon(container, iconName);
        var style = "icon-container-" + iconName;
        if (icon) {
            if (flag)
                icon.addClassName(style);
            else
                icon.removeClassName(style);
        }
    },

    _showInformations: function(container, flag) {
        var panel = this._getTextInformationsContainer(container);
        if (panel) {
            if (flag == undefined || flag == null)
                panel.toggle();
            else if (flag)
                panel.show();
            else
                panel.hide();
        }
    },

    _configConfirmDialog: function(container) {
        if (container) {
            this._configInformationDialog(container, false);
            this._showButton(container, "close", false);
            this._showButton(container, "cancel", true);
            this._showButton(container, "ok", true);
            this._showIcon(container, "error", false);
            this._showIcon(container, "info", false);
            this._showIcon(container, "question", true);
        }
    },

    _configAlertDialog: function(container) {
        var option;
    	if (container) {
            var errors = this._getErrorMessage();
            option = this._configInformationDialog(container, true);
            this._showButton(container, "close", true);
            this._showButton(container, "cancel", false);
            this._showButton(container, "ok", false);
            this._showIcon(container, "error", (errors != null));
            this._showIcon(container, "info", (errors == null));
            this._showIcon(container, "question", false);
        }
    	return option;
    },

    _configInformationDialog: function(container, showInfo) {
    	var modalOptions = {};
    	if (container) {
            var textContainer = this._getTextInformationsContainer(container);
            if (textContainer) {
                var errors = this._getErrorMessage() || this._getFailMessage();
                var events = this._getEventMessage();
                var hasInfo = (errors || events);
                this._showButton(container, "informations", false);
                if (hasInfo && showInfo) {
                    options = {
                        height: 90
                    };
                    // inizialize modalBox before resize it
                    if (textContainer.empty()) {
                        this._populateTextInformationsContainer(textContainer, errors, events);
                        modalOptions.afterLoad = function() {
                        	Modalbox.resizeToInclude(textContainer);
                        };
                        textContainer.show();
                    } else {
                    	// call populate for text-container and text-information-container
                    	this._populateTextInformationsContainer(textContainer, errors, events);
                        modalOptions.afterLoad = function() {
                        	Modalbox.resizeToInclude(textContainer);
                        };
                        textContainer.show();
                    }
                } else {
                    textContainer.update("");
                    textContainer.hide();
                }
            }
        }
    	return modalOptions;
    },

    _populateTextInformationsContainer: function(textContainer, errors, events) {
        if (textContainer) {
            var hasContent = false;
            if (errors != null) {
                textContainer.update(errors);
                hasContent = true;
            }
            if (events) {
                if (hasContent)
                    textContainer.insert(events);
                else
                    textContainer.update(events);
                hasContent = true;
            }
        }
    },

	// clear messages and hide ModalBox
    _hide: function(returnValue) {
        this._returnValue = returnValue;
        this._resetMessages();
        Modalbox.hide();
    },

    _show: function(content, title, callback, remoteOptions) {
        var formWidth = document.viewport.getWidth() * 0.50;
        var formHeight = 90; // document.viewport.getHeight() * 0.50;
        var options = {
            overlayClose: false,
            width: formWidth,
            height: formHeight
        };
        if (!title)
            title = this._title;
        if (title)
            options.title = title;
        if (callback)
            options.afterHide = callback;
        options.afterLoad = function() {
            Modalbox.resizeToContent();
        };
        
        // Merge local options and remoteOptions
        // remoteOptions maybe contains afterLoad with resizeToInclude
        options = Object.extend(options, remoteOptions || {});
        
        Modalbox.show(content, options);
    },

    _showDialog: function(container, content, title, defaultTitle, callback, options) {
        this._returnValue = false;
        this._getMessage(content, function(msg) {
            var property= null;

            if (msg) {
                if (content) {
                    if (Object.isArray(content)) {
                        if (content.length >= 2)
                            property = content[1];
                        else if (content.length >= 1)
                            property = content[0];
                    } else {
                        property= true;
                    }
                } else {
                    property = true;
                }

                if (property && property !== msg) {
                    var textContainer = this._getTextContainer(container);
                    if (textContainer)
                        textContainer.update(msg);
                    if (!title)
                        title = defaultTitle;
                    this._show(container, title, callback, options);
                }
                else {
                //Bug 4903 punto 3
                //Vedi reload-component-ajax-responder elaborateNewContent, callback chiamata sempre
                    if(Object.isFunction(callback)) {
                        callback();
                    }
                }
            }
        }.bind(this));
    },

    alert: function(content, title, callback) {
    	// resetMessage non qui perche il metodo e' invocato anche senza passare dall'ajaxLoad che fa il reset dei messaggi
        var container = this._getMessageBoxOrginalContainer();
        var options = this._configAlertDialog(container);
        return this._showDialog(container, content, title, this._alertTitle, callback, options);
    },

    confirm: function(content, title, callbackOk, callbackCancel, options) {
        if (content) {
            var container = this._getMessageBoxOrginalContainer();
            this._resetMessages();
            this._configConfirmDialog(container);
            return this._showDialog(container, content, title, this._confirmTitle, function() {
                if (this._returnValue) {
                    if (callbackOk)
                        callbackOk();
                } else if (callbackCancel) {
                    callbackCancel();
                }
            }.bind(this), options);
        } else {
            callbackOk();
        }
    },

    buttonInfo: function() {
        this._configInformationDialog($("MB_content"), true);
    },

    buttonCancel: function() {
        this._hide(false);
    },

    buttonOK: function() {
        this._hide(true);
    }
});

Modalbox.Methods.options.overlayOpacity = 0;
Modalbox.Methods.options.transitions = false;

modal_box_messages = new Messages();

document.observe("dom:loaded", function() {
    modal_box_messages.onPageLoad();
});
