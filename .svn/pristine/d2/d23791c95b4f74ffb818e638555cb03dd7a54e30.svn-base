RegisterPopupResponder = {
    load : function() {
        UpdateAreaResponder.Responders.register( {
            onLoad : function(newContent) {
                if (typeof PopupMgr != 'undefined') {
                    if (newContent) {
                        PopupMgr.loadElement(newContent);
                    }
                }
            }
        }, 'register-popup-responder');

    }

}

document.observe("dom:loaded", RegisterPopupResponder.load);