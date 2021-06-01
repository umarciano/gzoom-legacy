RegisterWizardResponder = {

    formRegistered : false,

    load : function(newContent, withoutResponder) {
        var wizardContainers =  (!Object.isElement(newContent) ? $(document.body).select('div.wizard') : newContent.select('div.wizard'));
        if (Object.isArray(wizardContainers) && wizardContainers.size() > 0) {
            wizardContainers.each(function(wizardContainer) {
                var form = wizardContainer.down('form.wizard');
                if (Object.isElement(form)) {
                    var submitButton = form.descendants().find(function(button) {
                        return button.readAttribute('name') === 'wizardNext';
                    });

                    if (Object.isElement(submitButton)) {
                        if (!Object.isArray(RegisterWizardResponder.formRegistered)) {
                            RegisterWizardResponder.formRegistered = [];
                        }
                        RegisterWizardResponder.formRegistered.push(form);

                        var onsubmit = null;
                        if (form.readAttribute('onsubmit')) {
                            onsubmit = form.readAttribute('onsubmit');

                            form.writeAttribute('onsubmit', '');
                        }

                        Event.observe(form, 'submit', function(callback, e) {
                            Event.stop(e);

                            var form = Event.element(e);
                            if (!ValidationManager.validateForm(form)) {
                            	modal_box_messages._resetMessages();
                                
                                modal_box_messages.alert(['BaseMessageSaveDataMandatoryField']);
                            } else {
                                if (Object.isFunction(callback)) {
                                    callback();
                                } else if (Object.isString(callback)) {
                                    /*form.writeAttribute('onsubmit', callback);
                                    form.submit();*/
                                    eval('var evalFuncTmp = function() { ' + callback + '; }');
                                    return evalFuncTmp();
                                }
                            }
                        }.bind(this, onsubmit));
                    }
                }
            });
        }


        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register( {
                onLoad : function(newContent) {
                    //Prima di tutto faccio una pulizia della cache dei submit
                    if (Object.isArray(RegisterWizardResponder.formRegistered) && RegisterWizardResponder.formRegistered.size() > 0) {
                        newSubmitRegistered = [];

                        RegisterWizardResponder.formRegistered.each(function(form) {
                            if (!(!Object.isElement($(form)) || !Object.isElement($(form.identify())) ||
                                (Object.isElement($(form)) && Object.isElement($(form.identify())) && $(form) !== $(form.identify())))) {
                                newSubmitRegistered.push(form);
                            } else {
                                Event.stopObserving(form, 'submit');
                                Event.stopObserving(form, 'dom:submit');
                            }

                        });

                        if (newSubmitRegistered.size() > 0) {
                            RegisterWizardResponder.formRegistered = newSubmitRegistered;
                        } else {
                            RegisterWizardResponder.formRegistered = false;
                        }
                    }

                   RegisterWizardResponder.load(newContent, true);
                }
            }, 'register-wizard-responder');
        }
    }
}

document.observe("dom:loaded", RegisterWizardResponder.load);