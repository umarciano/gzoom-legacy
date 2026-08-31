// il refresh ajax dopo il primo riepilogo re-esegue questo script, ridefinendo l'oggetto:
// jobs/pendingCounter vanno tenuti su window per sopravvivere alla ridefinizione.
if (!window.__stdImportState) {
	window.__stdImportState = { jobs: [], pendingCounter: 0 };
}
StandardImportUploadFileListener = {
	uploadFile : "uploadFile",
	form : false,
	jobs : window.__stdImportState.jobs,
	queueId : "standardImportQueue",
	defaultJobType : "Importazione Standard",
	pendingCounter : 0,
	
	ensureQueue : function() {
		var container = document.getElementById(this.queueId);
		var anchor = this.getLiveForm();
		if (container) {
			// il refresh ajax dopo il primo riepilogo sostituisce il form: riaggancia il container
			// al form live corrente se e' rimasto orfano o non e' piu' subito dopo di esso.
			if (Object.isElement(anchor) && anchor.parentNode && container.previousSibling !== anchor) {
				anchor.parentNode.insertBefore(container, anchor.nextSibling);
			}
			return container;
		}
		container = document.createElement("div");
		container.id = this.queueId;
		container.style.marginTop = "14px";
		container.style.marginBottom = "14px";
		container.style.width = "100%";
		container.style.boxSizing = "border-box";
		container.style.maxHeight = "220px";
		container.style.overflow = "auto";
		container.style.fontSize = "12px";
		container.style.border = "1px solid #d4d4d4";
		container.style.background = "rgba(255,255,255,0.96)";
		container.style.boxShadow = "0 2px 6px rgba(0,0,0,0.08)";
		container.style.borderRadius = "6px";
		container.style.padding = "8px 10px";
		if (Object.isElement(anchor) && anchor.parentNode) {
			anchor.parentNode.insertBefore(container, anchor.nextSibling);
		} else {
			document.body.appendChild(container);
		}
		return container;
	},
	
	getLiveForm : function() {
		var formEls = $$(".uploadFile");
		if (Object.isArray(formEls) && formEls.size() > 0) {
			return formEls[0];
		}
		return this.form;
	},
	
	renderQueue : function() {
		var container = this.ensureQueue();
		if (!this.jobs || this.jobs.length === 0) {
			container.innerHTML = "";
			container.style.display = "none";
			container.style.border = "none";
			container.style.padding = "0";
			container.style.margin = "0";
			return;
		}
		container.style.display = "";
		container.style.border = "1px solid #d4d4d4";
		container.style.padding = "8px 10px";
		container.style.margin = "14px 0";
		var html = "<div style='font-weight:bold; margin-bottom:6px; color:#333;'>Import in corso</div>";
		this.jobs.forEach(function(job) {
			var status = job.status || "in corso";
			var color = status === "completato" ? "#2a7f3b" : status === "errore" ? "#a12828" : "#1d5fa5";
			var label = job.type || StandardImportUploadFileListener.defaultJobType;
			var ref = job.sessionId || job.pendingId || "job";
			html += "<div style='padding:5px 8px; margin:4px 0; border-left:4px solid " + color + "; background:#f8f9fa; border-radius:4px;'>" +
				"job <b>" + ref + "</b> di <b>" + label + "</b> - <span style='color:" + color + "; font-weight:bold;'>" + status + "</span>" +
				"</div>";
		});
		container.innerHTML = html;
	},
	
	addPendingJob : function(type) {
		window.__stdImportState.pendingCounter += 1;
		var job = {
			type: type || this.defaultJobType,
			pendingId: "pending-" + window.__stdImportState.pendingCounter,
			status: "in corso"
		};
		this.jobs.push(job);
		this.renderQueue();
		return job;
	},
	
	showLoadingMessage : function() {
		var container = this.ensureQueue();
		var html = "<div style='font-weight:bold; margin-bottom:6px; color:#333;'>Import in corso</div>";
		var current = this.jobs && this.jobs.length > 0 ? this.jobs[this.jobs.length - 1] : null;
		if (current) {
			var ref = current.sessionId || current.pendingId || "job";
			var label = current.type || this.defaultJobType;
			html += "<div style='padding:7px 9px; margin:4px 0; border-left:4px solid #1d5fa5; background:#f8f9fa; border-radius:4px; color:#333;'>job <b>" + ref + "</b> di <b>" + label + "</b> - <span style='color:#1d5fa5; font-weight:bold;'>in corso...</span></div>";
		}
		container.innerHTML = html;
	},
	
	bindSessionToPendingJob : function(sessionId, jobLogId) {
		if (!sessionId) {
			return;
		}
		for (var i = 0; i < this.jobs.length; i++) {
			if (!this.jobs[i].sessionId && this.jobs[i].pendingId) {
				this.jobs[i].sessionId = sessionId;
				this.jobs[i].jobLogId = jobLogId || null;
				this.jobs[i].status = "in corso";
				this.renderQueue();
				this.pollJobStatus(sessionId, jobLogId);
				return;
			}
		}
	},
	
	pollJobStatus : function(sessionId, jobLogId) {
		if (!sessionId) {
			return;
		}
		var pollParams = {sessionId: sessionId};
		if (jobLogId) {
			pollParams.jobLogId = jobLogId;
		}
		new Ajax.Request('<@ofbizUrl>getStandardImportStatus</@ofbizUrl>', {
			method: 'post',
			parameters: pollParams,
			onSuccess: function(transport) {
				var data = {};
				try {
					data = transport.responseText.evalJSON(true);
				} catch (e) {
					data = {};
				}
				if (data.completed === 'Y') {
					StandardImportUploadFileListener.updateJob(sessionId, "completato");
					StandardImportUploadFileListener.showCompletedSummary(sessionId, data);
				} else {
					// riattacca/ridisegna il container ad ogni tick: il refresh ajax dopo il primo riepilogo
					// puo' averlo rimosso dal DOM, e senza questa chiamata non si ripresenterebbe fino al completamento.
					StandardImportUploadFileListener.renderQueue();
					setTimeout(function() {
						StandardImportUploadFileListener.pollJobStatus(sessionId, jobLogId);
					}, 4000);
				}
			},
			onFailure: function() {
				setTimeout(function() {
					StandardImportUploadFileListener.pollJobStatus(sessionId, jobLogId);
				}, 8000);
			}
		});
	},
	
	showCompletedSummary : function(sessionId, data) {
		var msg = '${uiLabelMap.BaseMessageStandardRecordElaborated}'.replace("recordElaborated", data.recordElaborated || 0) + "<br>";
		msg += '${uiLabelMap.BaseMessageStandardBlockingErrors}'.replace("blockingErrors", data.blockingErrors || 0) + "<br>";
		msg += '${uiLabelMap.BaseMessageStandardSessionId}'.replace("sessionId", sessionId);
		modal_box_messages.alert(msg, null, function() {StandardImportUploadFileListener.refreshForm('completed')});
	},
	
	notifyParentStatus : function(status, payload) {
		try {
			if (window.parent && window.parent !== window) {
				window.parent.postMessage({
					event: 'legacyImportStatus',
					status: status || 'in corso',
					type: this.defaultJobType,
					payload: payload || {}
				}, '*');
			}
		} catch (e) {
			// cross-origin access is not allowed; the loader must be driven by the message channel only
		}
	},
	
	updateJob : function(sessionId, status) {
		if (!sessionId) {
			return;
		}
		for (var i = 0; i < this.jobs.length; i++) {
			if (this.jobs[i].sessionId === sessionId) {
				this.jobs[i].status = status || this.jobs[i].status || "in corso";
				this.renderQueue();
				if (status === "completato" || status === "errore") {
					setTimeout(function() {
						// muta l'array in place (non riassegnare): deve restare lo stesso riferimento di window.__stdImportState.jobs
						var jobs = StandardImportUploadFileListener.jobs;
						for (var j = jobs.length - 1; j >= 0; j--) {
							if (jobs[j].sessionId === sessionId) {
								jobs.splice(j, 1);
							}
						}
						StandardImportUploadFileListener.renderQueue();
					}, 5000);
				}
				return;
			}
		}
	},
	
	load : function() {
	    var form = $$(".uploadFile");
		if(Object.isArray(form) && form.size() > 0) {
		    form = form[0];
		}

		if (Object.isElement(form) && form.tagName === "FORM") {
			StandardImportUploadFileListener.form = form;
			Element.addMethods("FORM", {
				onAfterSubmit : function(element) {
					return element;
				}
			});
			StandardImportUploadFileListener.registerForm(form);
		}
	},
	
	registerForm : function(form) {
		var targetFrame = $("target_upload");

    	if(Object.isElement(targetFrame)) {
    		Event.observe(targetFrame, "load", StandardImportUploadFileListener.uploadCompleted);
    	}
		if (Object.isElement(form)) {
			Event.observe(form, "submit", function() {
				StandardImportUploadFileListener.addPendingJob(StandardImportUploadFileListener.defaultJobType);
				StandardImportUploadFileListener.showLoadingMessage();
				StandardImportUploadFileListener.notifyParentStatus('in corso', {type: StandardImportUploadFileListener.defaultJobType});
				if (typeof Utils !== "undefined" && Utils.startWaiting) {
					Utils.startWaiting();
				}
			});
			
			// il bottone "Esegui" del toolbar chiama form.submit() via JS (perche' il form ha classe uploadFile),
			// e questa chiamata NON genera l'evento 'submit': va intercettata sovrascrivendo il metodo stesso.
			var nativeSubmit = form.submit;
			form.submit = function() {
				StandardImportUploadFileListener.addPendingJob(StandardImportUploadFileListener.defaultJobType);
				StandardImportUploadFileListener.showLoadingMessage();
				StandardImportUploadFileListener.notifyParentStatus('in corso', {type: StandardImportUploadFileListener.defaultJobType});
				return nativeSubmit.apply(form, arguments);
			};
		}
	},
	
	uploadCompleted : function(event) {
	    //force stopWaiting becauase is not ajax.Request
		Utils.stopWaiting();
		
		var doc = StandardImportUploadFileListener.getIframeDocument($("target_upload"));
		var sessionId = null;
		if (!doc || !doc.getElementById) {
			StandardImportUploadFileListener.notifyParentStatus('errore', {type: StandardImportUploadFileListener.defaultJobType});
			return;
		}
		// jobLogId della fase di upload: unico conteggio affidabile, non soggetto all'accumulo
		// che il job asincrono produce riusando lo stesso jobLogId su piu' entita' in cascata.
		var uploadJobLogId = null;
		var resultListUploadFileNode = doc.getElementById("resultListUploadFile");
		if (resultListUploadFileNode != null && resultListUploadFileNode.innerHTML != "") {
			try {
				var parsedUploadList = resultListUploadFileNode.innerHTML.evalJSON(true);
				if (parsedUploadList && parsedUploadList.length > 0 && parsedUploadList[0].jobLogId) {
					uploadJobLogId = parsedUploadList[0].jobLogId;
				}
			} catch (e) {
				uploadJobLogId = null;
			}
		}
		var sessionIdNode = doc.getElementById("sessionId");
		if (sessionIdNode != null && sessionIdNode.innerHTML != "") {
			sessionId = sessionIdNode.innerHTML;
			StandardImportUploadFileListener.bindSessionToPendingJob(sessionId, uploadJobLogId);
		}
    	var errorMessageDiv = doc.getElementsByClassName("errorMessage")[0];
    	if (Object.isElement(errorMessageDiv)) {
    	
    		var data = $H({});
    		data["_ERROR_MESSAGE_"] = errorMessageDiv.innerHTML; 
    		modal_box_messages.onAjaxLoad(data, null);
			if (sessionId) {
				StandardImportUploadFileListener.updateJob(sessionId, "errore");
			}
    		modal_box_messages.alert("Errore:");
    		return;
    	} else {
    	
	    	var data = $H({});
			data["_ERROR_MESSAGE_"] = "";
			modal_box_messages.onAjaxLoad(data, null);
			
		}
		
		var messTotale = "";
		
		var resultETLList = doc.getElementById("resultETLList");
        if(resultETLList != null && resultETLList.innerHTML != ""){
            resultETLList = resultETLList.innerHTML.evalJSON(true);
            resultETLList.each(function(element){    
                msg = element.entityName + "<br>";
                msg += '${uiLabelMap.BaseMessageStandardRecordToImport}'.replace("recordElaborated", element.recordElaborated) + "<br>";
                msg += '${uiLabelMap.BaseMessageStandardBlockingErrors}'.replace("blockingErrors", element.blockingErrors) + "<br>";
                msg += '${uiLabelMap.BaseMessageStandardJobLogId}'.replace("jobLogId", element.jobLogId) + "<br><br>";
                
                messTotale += msg;            
            });
        }   
         
    	var resultListUploadFile = doc.getElementById("resultListUploadFile");
    	if(resultListUploadFile != null && resultListUploadFile.innerHTML != ""){
    		resultListUploadFile = resultListUploadFile.innerHTML.evalJSON(true);
    		resultListUploadFile.each(function(element){
    		
				msg = element.entityName + "<br>";
				msg += '${uiLabelMap.BaseMessageStandardRecordToImport}'.replace("recordElaborated", element.recordElaborated) + "<br>";
				msg += '${uiLabelMap.BaseMessageStandardBlockingErrors}'.replace("blockingErrors", element.blockingErrors) + "<br>";
				msg += '${uiLabelMap.BaseMessageStandardJobLogId}'.replace("jobLogId", element.jobLogId) + "<br><br>";
				
	    		messTotale += msg;    		
			});
    	}
    	
    	var resultList = doc.getElementById("resultList");
    	if(resultList != null && resultList.innerHTML != ""){
    		resultList = resultList.innerHTML.evalJSON(true);
    		resultList.each(function(element){
    		    if(element.entityName != null && element.entityName != ""){
    				msg = element.entityName + "<br>";
    				msg += '${uiLabelMap.BaseMessageStandardRecordToImport}'.replace("recordElaborated", element.recordElaborated) + "<br>";
    				msg += '${uiLabelMap.BaseMessageStandardBlockingErrors}'.replace("blockingErrors", element.blockingErrors) + "<br>";
    				msg += '${uiLabelMap.BaseMessageStandardJobLogId}'.replace("jobLogId", element.jobLogId) + "<br><br>";
    	    		
    	    		messTotale += msg;
    		    }
			});
    	}	
    	
    	var sessionId = doc.getElementById("sessionId");
        if(sessionId != null && sessionId.innerHTML != ""){
            sessionId = sessionId.innerHTML;
            messTotale += '${uiLabelMap.BaseMessageStandardSessionId}'.replace("sessionId", sessionId);
        }
        
    	
    	if(messTotale != ""){
  	  		modal_box_messages.alert(messTotale, null, function() {StandardImportUploadFileListener.refreshForm('upload')});
    	}
	},
	
	refreshForm : function(source) {
		// source distingue il chiamante: 'upload' = chiusura primo riepilogo, 'completed' = chiusura riepilogo finale
		console.log('[STDIMPORT-DEBUG] refreshForm invocata da:', source);
		//	Aggiunto passaggio del parametro 'noInfoToolbar' (fix della issue GN-5156, re-opening)
		// alert('StandardImportUploadFileListener.js.ftl - refreshForm, noInfoToolbar = ${parameters.noInfoToolbar?if_exists}');
		// console.log('[StandardImportUploadFileListener.js.ftl::refreshForm] (GN-5156) noInfoToolbar = ${parameters.noInfoToolbar?if_exists}');
	    ajaxUpdateAreas('main-section-container,<@ofbizUrl>${parameters._LAST_VIEW_NAME_?if_exists}</@ofbizUrl>,externalLoginKey=${parameters.externalLoginKey?if_exists}&noInfoToolbar=${parameters.noInfoToolbar?if_exists}&ajaxRequest=Y&clearSaveView=Y&cleanAccountingSession=Y&ownerContentId=GP_MENU_00231');
	    LookupProperties.afterHideModal();
    },
	
	getIframeDocument : function(frameElement) {
		if (!frameElement) {
			return null;
		}
		var doc = null;
		try {
  		if (frameElement.contentDocument) {
    		doc = frameElement.contentDocument; 
  		} else if (frameElement.contentWindow) {
    		doc = frameElement.contentWindow.document;
  		} else if (frameElement.document) {
    		doc = frameElement.document;
  		}
		} catch (e) {
			return null;
		}
  		return doc;
	}
}

StandardImportUploadFileListener.load();