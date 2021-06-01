RegisterFormKitResponder = {
    load : function() {
        UpdateAreaResponder.Responders.register( {
            onLoad : function(newContent) {
                if (typeof FormKit != 'undefined') {
                    if (newContent) {
                        FormKit.reload(newContent);
                    }
                }
            }
        }, 'register-formkit-responder');

    }

}

document.observe("dom:loaded", RegisterFormKitResponder.load);