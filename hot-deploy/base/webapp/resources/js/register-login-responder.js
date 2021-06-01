RegisterLoginResponder = {
    load : function() {
        UpdateAreaResponder.Responders.register( {
            onLogin : function(newContent, options) {
                let msg={event : "login"};
                w.postMessage(msg, '*');
            
        		var response = options['response'];
                var responseText = response.responseText;
                var contentToUpdate = options['contentToUpdate'];
                if (Object.isString(responseText)) {
                    Utils.showModalBox(responseText, {form: false, width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.40),
                                                        afterLoadModal: LookupProperties.afterLoadModal,
                                                        afterHideModal: LookupProperties.afterHideModal,
                                                        height: document.viewport.getHeight() - (document.viewport.getHeight() * 0.40),
                                                        afterLoadModal: function() {
                                                            var lookupWindow = $('MB_window');

                                                            this.form = lookupWindow.down('form');
                                                            if (Object.isElement(this.form)) {
                                                                Event.observe(this.form, 'submit', function(e) {
                                                                    Event.stop(e);

                                                                    submitFormDisableSubmits(this);
                                                                    ajaxSubmitFormUpdateAreas(this, Object.isString(contentToUpdate) ? contentToUpdate : (Object.isElement($(contentToUpdate)) ? $(contentToUpdate).identify() : ''));
                                                               });
                                                            }
                                                        }.bind(this),
                                                        beforeHide: function() {
                                                            if (Object.isElement($(this.form))) {
                                                                Event.stopObserving(this.form);
                                                            }
                                                        }.bind(this)});
                }
            },

            onLoginExecuted : function(newContent, options) {
            	var response = options['response'];
            	var headerJSON = response.headerJSON;
            	if (headerJSON && 'externalLoginKey' in headerJSON) {
            		var externalLoginKey = headerJSON['externalLoginKey'];
            		
            		var mainmenuFunctionToolbar = $('mainmenu-function-toolbar');
            		if (Object.isElement(mainmenuFunctionToolbar)) {
            			var externalLoginKeyField = mainmenuFunctionToolbar.down('input#home-externalLoginKey');
            			if (Object.isElement(externalLoginKeyField)) {
            				externalLoginKeyField.writeAttribute('value', externalLoginKey);
            			}
            		}
            		
            		var mainmenuBody = $('mainmenu-body');
            		if (Object.isElement(mainmenuFunctionToolbar)) {
            			mainmenuBody.select('input[name=externalLoginKey]').each(function(element) {
            				element.writeAttribute('value',externalLoginKey );
            			});
            		}
            	}
                var afterLoadModalExecution = options['afterLoadModal'];

                if (Object.isFunction(afterLoadModalExecution)) {
                    options["afterLoad"] = function(options) {
                        LookupProperties.afterLoadModal(options);

                        afterLoadModalExecution();
                   }
                }

                var afterHideModalExecution = options['afterHideModal'];

                if (Object.isFunction(afterHideModalExecution)) {
                    options["afterHide"] = function(options) {
                        LookupProperties.afterHideModal(options);

                        afterHideModalExecution();
                   }
                }

                Utils.hideModalBox(options);
            }
        }, 'register-login-responder');

    }

}

document.observe("dom:loaded", RegisterLoginResponder.load);