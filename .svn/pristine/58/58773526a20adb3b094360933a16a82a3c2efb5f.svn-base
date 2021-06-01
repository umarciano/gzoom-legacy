RegisterDropListResponder = {
    load : function() {
        UpdateAreaResponder.Responders.register( {
            onLoad : function(newContent) {
                if (typeof DropListMgr != 'undefined') {
                    if (newContent && !Object.isElement($('MB_window'))) {
                        DropListMgr.loadElement(newContent);
                    }
                }
            }
        }, 'register-droplist-responder');

    }

}

document.observe("dom:loaded", RegisterDropListResponder.load);