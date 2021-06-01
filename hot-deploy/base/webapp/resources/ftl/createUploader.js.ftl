function createUploader(){
	var elementList = $$('.uploaded-content');
	$A(elementList).each(function(element) {
		var href = element.href;
		
		var url = href;
		if (href.indexOf('?') != -1)
			url = href.substring(0, href.indexOf('?'));
		var hrefParams = href.toQueryParams();
		var parentForm = element.up('form');
		
		var uploader = new qq.MapsFileUploader({
	        'element': element.up(),
	        action: url,
	        params : hrefParams,
	        showMessage : function(message){
	            modal_box_messages.alert(message);
	        },            
	        onComplete: function(id, fileName, responseJSON){
	        	var form = parentForm;
	        	var script = form.readAttribute('onsubmit');
	        	if (script.indexOf('ajaxSubmitFormUpdateAreas') != -1) {
	        		script = script.substring(script.indexOf('ajaxSubmitFormUpdateAreas'));
	        		
	        		script = script.replace(/ajaxSubmitFormUpdateAreas/, 'ajaxUpdateAreas');
	        		
	        		var splittedScript = $A(script.split(','));
	        		var tmpScript = script.substring(0, 'ajaxUpdateAreas('.length);
	        		splittedScript = splittedScript.slice(2);
	        		tmpScript += splittedScript.join(',');
	        		
	        		script = tmpScript;
	        	}
	        	
	        	eval('var evalFuncTmp = function() { ' + script + '; }');
    			return evalFuncTmp();
	        },
	        template: '<div class="qq-uploader">' + 
                '<div class="qq-upload-button"><span class="qq-upload-text">${uiLabelMap.BaseUploadAttachment}</span></div>' +
             '</div>',
        	handlerClass : 'MapsUploadHandlerForm',
        	debug : true
	    });
	});
               
}

// in your app create uploader as soon as the DOM is ready
// don't wait for the window to load  
createUploader(); 