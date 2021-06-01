
AsyncJobList = {

    load: function() {
        var form = $('table_B_AJL001_AsyncJobList');
        if (form) {
            form.select('.actions-area-link').each(function(elem) {
                var form = elem.select('form').first();
                if (form) {
                    var action = Element.readAttribute(form, 'action');
                    if (action && action.include('stopAsyncJob')) {
                        if(Object.isElement(elem)) {
                    		Event.stopObserving(elem, "click");
					 		Event.observe(elem, "click", function(event) {
					 			Event.stop(event);
					 			
					 			modal_box_messages.confirm('${uiLabelMap.AsyncJob_confirmMessage}', null, AsyncJobList.stopAsyncJob.curry(form), null);
					 			return false;
					 		});
					 	}
                    }
                }
            });
        }
    },

    stopAsyncJob: function(form) {
        ajaxSubmitFormUpdateAreas(form.identify(),"","portlet-container_${portalPageId}_${portalPortletId}_${portletSeqId},<@ofbizUrl>showPortletContainerOnly</@ofbizUrl>,&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}&portalPageId=${portalPageId}&portalPortletId=${portalPortletId}&portletSeqId=${portletSeqId}&saveView=N");
        return false;
    }
}

AsyncJobList.load();
