HelpInfoListener = {

    helpLinkId : "helpLink",
    infoLinkId : "infoLink",

    load: function(newContent, withoutResponder) {
         var newContent = Object.isElement($(newContent)) ? $(newContent) : $(document.body);

         var helpLink = $(HelpInfoListener.helpLinkId);
         var infoLink = $(HelpInfoListener.infoLinkId);
         var objectsArray = new Array(helpLink, infoLink);

         Utils.observeEvent(objectsArray, "click", HelpInfoListener.openModalBox);

         if (!withoutResponder) {
            UpdateAreaResponder.Responders.register( {
                onAfterLoad : function(newContent) {
                    HelpInfoListener.load(newContent, true);
                }
            }, 'help-info-listener');
        }
    },


    openModalBox: function(event) {
        Event.stop(event);
        var element = Event.element(event);
        if (element.tagName == "A") {
            var href = element.getAttribute("href");
            Modalbox.show(href, {width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.50)/*, height: document.viewport.getHeight() - (document.viewport.getHeight() * 0.10)*/});
        }

    }
}

document.observe("dom:loaded", HelpInfoListener.load);