SystemInfoNotesLinkListener = {

    systemInfoNotesLinkClass : ".system-info-notes-link",
    systemInfoNotesDeleteLinkClass : ".system-info-notes-delete-link",
    systemInfoNotesStatusLinkClass : ".system-info-notes-status-link",

    load: function(newContent, withoutResponder) {
         var newContent = Object.isElement($(newContent)) ? $(newContent) : $(document.body);

         var systemInfoNotesCreateLink = newContent.select(SystemInfoNotesLinkListener.systemInfoNotesLinkClass);

         Utils.stopObserveEvent(systemInfoNotesCreateLink, "click", SystemInfoNotesLinkListener.deleteAllLinkListener);
         Utils.observeEvent(systemInfoNotesCreateLink, "click", SystemInfoNotesLinkListener.createLinkListener);

         var systemInfoNotesDeleteAllLink = newContent.select(SystemInfoNotesLinkListener.systemInfoNotesDeleteLinkClass);
         Utils.stopObserveEvent(systemInfoNotesDeleteAllLink, "click", SystemInfoNotesLinkListener.deleteAllLinkListener);
         Utils.observeEvent(systemInfoNotesDeleteAllLink, "click", SystemInfoNotesLinkListener.deleteAllLinkListener);

         var systemInfoNotesStatusLink = newContent.select(SystemInfoNotesLinkListener.systemInfoNotesStatusLinkClass);
         Utils.stopObserveEvent(systemInfoNotesStatusLink, "click", SystemInfoNotesLinkListener.infoNotesStatusListener);
         Utils.observeEvent(systemInfoNotesStatusLink, "click", SystemInfoNotesLinkListener.infoNotesStatusListener);

         if (!withoutResponder) {
             UpdateAreaResponder.Responders.register( {
                onLoad : function(newContent) {
                    if (newContent) {
                        SystemInfoNotesLinkListener.load(newContent, true);
                    }
                }
            }, 'SystemInfoNotesLink');
         }
    },

    infoNotesStatusListener: function(event) {
        var element = Event.element(event);
        if (element.tagName == "A") {
            var href = element.getAttribute("href");
            var params = $H(href.toQueryParams());

            var extraTargetParameters = params.get("extraTargetParameters");
            if (extraTargetParameters) {
                Event.stop(event);

                params.unset("extraTargetParameters");

                extraTargetParameters = extraTargetParameters.replace(/\*/g,'&');

                var extraParams = $H(extraTargetParameters.toQueryParams());

                var currentPortlet = element.up('.portlet-main-container');
                if (!Object.isElement(currentPortlet.next('#portlet-container_' + extraParams.get('portalPageId') + '_' + extraParams.get('portalPortletId'))))
                    Element.insert(currentPortlet, {'after' : new Element('div', {'id' : 'portlet-container_' + + extraParams.get('portalPageId') + '_' + extraParams.get('portalPortletId'), class : 'portlet-main-container'})});

                ajaxUpdateArea('portlet-container_' + extraParams.get('portalPageId') + '_' + extraParams.get('portalPortletId'), href.substring(0, href.indexOf('?')), extraTargetParameters)
            }
        }
    },

    createLinkListener: function(event) {
        Event.stop(event);
        var element = Event.element(event);
        if (element.tagName == "A") {
            var href = element.getAttribute("href");
            var params = $H(href.toQueryParams());
            var title = "";
            if (params.get("title")) {
                title = params.get("title");
                title = title.replace("[", "");
                title = title.replace("]", "");
            }
            params.unset("title");
            Utils.showModalBox(href, {'title' : title, afterLoadModal: LookupProperties.afterLoadModal, beforeHideModal: LookupProperties.beforeHideModal, afterHideModal: LookupProperties.afterHideModal, width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.50), height: document.viewport.getHeight() - (document.viewport.getHeight() * 0.75)});
        }

    },

    deleteAllLinkListener: function(event) {
        var element = Event.element(event);
        if (element.tagName == "A") {
            var href = element.getAttribute("href");
            var params = $H(href.toQueryParams());

            var extraAreaTarget = params.get("extraAreaTarget");
            var extraAreaId = params.get("extraAreaId");
            var extraTargetParameters = params.get("extraTargetParameters");

            if (extraAreaTarget && extraAreaId && extraTargetParameters) {
                Event.stop(event);

                params.unset("extraAreaTarget");
                params.unset("extraAreaId");
                params.unset("extraTargetParameters");

                href = href.substring(0, href.indexOf('?'));
                var form = new Element('form', {'action' : href});
                params.each(function(pair) {
                    form.insert(new Element('input', {'type' : 'hidden', 'name' : pair.key, 'value' : pair.value}));
                });

                document.body.insert(form);

                extraTargetParameters = extraTargetParameters.replace(/\*/g,'&');
                ajaxSubmitFormUpdateAreas(form.identify(), '', extraAreaId + ',' + extraAreaTarget + ',' + extraTargetParameters);

                form.remove();
            }
        }
    },

    _evalFunc : function(str) {
        eval('var evalFuncTmp = function() { ' + str + '; }');
        return evalFuncTmp();
    },

    _evalBool : function(str) {
        return (this._evalFunc(str) != false);
    }
}

document.observe("dom:loaded", SystemInfoNotesLinkListener.load);