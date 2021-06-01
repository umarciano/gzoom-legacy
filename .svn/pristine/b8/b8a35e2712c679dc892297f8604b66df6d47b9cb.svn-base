RegisterTableKitResponder = {
    load : function() {
        UpdateAreaResponder.Responders.register( {
            onBeforeLoad : function(contentToUpdate) {
                contentToUpdate = $(contentToUpdate);
                TableKit.unload(contentToUpdate);
            },
            onLoad : function(newContent) {
                if (typeof TableKit != 'undefined') {
                    if (newContent) {
                    	TableKit.unload(newContent);
                        TableKit.reload(newContent);
                    }
                }
            }
        }, 'register-tablekit-responder');

    }

}

document.observe("dom:loaded", RegisterTableKitResponder.load);