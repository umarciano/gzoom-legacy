RegisterLookupResponder = {
    load : function() {
        UpdateAreaResponder.Responders.register( {
            onLoad : function(newContent) {
                if (typeof LookupMgr != 'undefined') {
                    if (newContent && !Object.isElement($('MB_window'))) {
                        LookupMgr.loadElement(newContent);
                    }
                }
            }
        }, 'register-lookup-responder');

    }

}

document.observe("dom:loaded", RegisterLookupResponder.load);