RegisterLazierLoadResponder = {
    load : function() {
        if (typeof JS_BRAMUS != 'undefined') {
            UpdateAreaResponder.Responders.register( {
                onAfterLoad : function(newContent) {
                    if (newContent) {
                        new JS_BRAMUS.lazierLoad({treshold: 200}, newContent);
                    }
                }
            }, 'register-lazierLoad-responder');
        }
    }

}

JS_BRAMUS.lazierLoad.prototype      = {

    initialize          : function(options, newContent) {
        // find all images and lazyLoad 'm
        (Object.isElement(newContent) ? newContent.select('img') : $$('img')).each(function(image) {
            new JS_BRAMUS.lazierLoadImage(image, options);
        });
    }

}

document.observe("dom:loaded", RegisterLazierLoadResponder.load);