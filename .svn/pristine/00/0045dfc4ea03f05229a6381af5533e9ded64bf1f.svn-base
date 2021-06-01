WorkEffortNoteAndDataReportViewListener = {

    workeffortNoteReportDeleteLinkClass : ".workeffort-note-report-delete-link",
    workeffortNoteReportInsertLinkClass : ".workeffort-note-report-insert-link",
    workeffortNoteReportDetailLinkClass : ".workeffort-note-report-detail-link",

    load: function(newContent, withoutResponder) {

         var newContent = Object.isElement($(newContent)) ? $(newContent) : $(document.body);

         var workeffortNoteReportDeleteLink = newContent.select(WorkEffortNoteAndDataReportViewListener.workeffortNoteReportDeleteLinkClass);
         Utils.stopObserveEvent(workeffortNoteReportDeleteLink, "click", WorkEffortNoteAndDataReportViewListener.deleteLinkListener);
         Utils.observeEvent(workeffortNoteReportDeleteLink, "click", WorkEffortNoteAndDataReportViewListener.deleteLinkListener);

         var workeffortNoteReportInsertLink = newContent.select(WorkEffortNoteAndDataReportViewListener.workeffortNoteReportInsertLinkClass);
         Utils.stopObserveEvent(workeffortNoteReportInsertLink, "click", WorkEffortNoteAndDataReportViewListener.createLinkListener);
         Utils.observeEvent(workeffortNoteReportInsertLink, "click", WorkEffortNoteAndDataReportViewListener.createLinkListener);
	
         var workeffortNoteReportDetailLink = newContent.select(WorkEffortNoteAndDataReportViewListener.workeffortNoteReportDetailLinkClass);
         Utils.stopObserveEvent(workeffortNoteReportDetailLink, "click", WorkEffortNoteAndDataReportViewListener.detailLinkListener);
         Utils.observeEvent(workeffortNoteReportDetailLink, "click", WorkEffortNoteAndDataReportViewListener.detailLinkListener);
         
         WorkEffortNoteAndDataReportViewListener.responderOnLoad = Object.extend({name: 'WorkEffortNoteAndDataReportViewListener', onBeforeLoad : WorkEffortNoteAndDataReportViewListener.onBeforeLoadListener}, LookupProperties.responderOnLoad);

    },
	
	responderOnLoad : false,
	
	onBeforeLoadListener : function(contentToUpdate, options) {
        if (Object.isString(contentToUpdate) && !Object.isElement($(contentToUpdate))) {
            var data = options['data'];
            Utils.hideModalBox({
                afterHide: LookupProperties.afterHideModal.wrap(function(original, options) {
                    original(options);
                    UpdateAreaResponder.executeAjaxUpdateAreas(contentToUpdate, data);
                })
            });
        } else {
            return true;
        }

        return false;
    },
	    
    detailLinkListener: function(event) {
        
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
            
            //prendo elementi da aggiungere alla tabella e all'elemento selezionato
            var modelListTable = $('table_WorkEffortNoteAndDataReportViewListForm');

	        if(Object.isElement(modelListTable)) {	       
                var selectRow = TableKit.Selectable.getSelectedRows(modelListTable)[0];                
                var queryParams = "";
	            selectRow.select('input[type="hidden"]').each(function(elm) {
	               if(elm.readAttribute('name') != 'noteInfo'){
		                if (queryParams.length > 0) {
		                    queryParams += "&";
		                }	                
		                queryParams += (elm.readAttribute('name') + '=' + elm.getValue());
	                }
	            });
       		 
	         Utils.showModalBox(href + (queryParams.length > 0 ? ('&' + queryParams) : ''), {'title' : title, afterLoadModal: LookupProperties.afterLoadModal.curry({'responderOnLoad' : WorkEffortNoteAndDataReportViewListener.responderOnLoad}), afterHideModal: LookupProperties.afterHideModal.curry({'responderOnLoad' : WorkEffortNoteAndDataReportViewListener.responderOnLoad}), width: 1000, height: 400});
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
            Utils.showModalBox(href, {'title' : title, afterLoadModal: LookupProperties.afterLoadModal.curry({'responderOnLoad' : WorkEffortNoteAndDataReportViewListener.responderOnLoad}), afterHideModal: LookupProperties.afterHideModal.curry({'responderOnLoad' : WorkEffortNoteAndDataReportViewListener.responderOnLoad}), width: 1000, height: 400});
        }

    },

    deleteLinkListener: function(event) {
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
WorkEffortNoteAndDataReportViewListener.load();