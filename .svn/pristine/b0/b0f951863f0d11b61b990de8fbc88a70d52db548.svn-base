WorkEffortAchieveInqMeasView_LookupIndicator = {
    openLookupIndicator : function(event) {
        Event.stop(event);
        var element = Event.element(event);
        if (element.tagName == "SPAN") {
            var queryParams = "";
            element.adjacent('input[type="hidden"]').each(function(elm) {
                if (queryParams.length > 0) {
                    queryParams += "&";
                }
                queryParams += (elm.readAttribute('name') + '=' + elm.getValue());
            });
            
            Utils.showModalBox('<@ofbizUrl>${lookupFormIndicatorTarget}</@ofbizUrl>' + (queryParams.length > 0 ? ('?' + queryParams) : ''), {'title' : '${uiLabelMap.FormIndicator}' , afterLoadModal: LookupProperties.afterLoadModal, beforeHideModal: LookupProperties.beforeHideModal, afterHideModal: LookupProperties.afterHideModal, width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.50), height: document.viewport.getHeight() - (document.viewport.getHeight() * 0.20)});
        }
    },
    load : function() {
        $('InqMeasureTable').select('span.title_indicator').each(function(element) {
            Event.observe(element, 'click', WorkEffortAchieveInqMeasView_LookupIndicator.openLookupIndicator);
        });
    }
}

WorkEffortAchieveInqMeasView_LookupIndicator.load();