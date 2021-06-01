RegisterGalleryResponder = {
    load : function() {
        UpdateAreaResponder.Responders.register( {
            onLoad : function(newContent) {
                if (typeof CanvasGallery != 'undefined') {
                    if (newContent) {
                        CanvasGallery.loadElement(newContent);
                    }
                }
            }
        }, 'register-gallery-responder');

    }

}

document.observe("dom:loaded", RegisterGalleryResponder.load);