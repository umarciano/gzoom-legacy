RegisterValidationResponder = {
    load : function() {
        UpdateAreaResponder.Responders.register( {
            onLoad : function(newContent) {
                if (typeof LiveValidationManager != 'undefined') {
                	//Sandro: TODO nuova validazione LiveValidationManager.loadFields('input[type!=hidden].mandatory', { validMessage: '', wait: 500}, Validate.Presence, {failureMessage: 'MandatoryField'});
                	//LiveValidationManager.onSave();
                }
            }
        }, 'register-validation-responder');

    }

}

document.observe("dom:loaded", RegisterValidationResponder.load);