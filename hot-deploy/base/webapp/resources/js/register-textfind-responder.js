RegisterTextFindResponder = {
    load : function() {
        UpdateAreaResponder.Responders.register( {
            onLoad : function(newContent) {
                if (typeof TextFind != 'undefined') {
                    if (newContent) {
                        TextFind.load(newContent);
                    }
                }
            }
        }, 'register-textfind-responder');

    }

}

document.observe("dom:loaded", RegisterTextFindResponder.load);