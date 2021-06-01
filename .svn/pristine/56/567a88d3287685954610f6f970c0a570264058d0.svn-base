/**
 * Class that creates upload widget with drag-and-drop and file list
 * @inherits qq.FileUploaderBasic
 */
qq.MapsFileUploader = function(o){
	// call parent constructor
    qq.FileUploaderBasic.apply(this, arguments);
	
    // additional options    
    qq.extend(this._options, {
    	element: null,
        // if set, will be used instead of qq-upload-list in template
        listElement: null,
        
        template: '<div class="qq-uploader">' + 
                '<div class="qq-upload-button"></div>' +
             '</div>',

        // template for one item in file list
        fileTemplate: null,        
        
        classes: {
            // used to get elements from templates
            button: 'qq-upload-button',
        }
    });
    // overwrite options with user supplied    
    qq.extend(this._options, o);
    
    this._element = this._options.element;
    this._element.innerHTML = this._options.template;        
    
    this._classes = this._options.classes;
        
    this._button = this._createUploadButton(this._find(this._element, 'button'));        
}

//inherit from Basic Uploader
qq.extend(qq.MapsFileUploader.prototype, qq.FileUploader.prototype);

qq.extend(qq.MapsFileUploader.prototype, {
	_onSubmit: function(id, fileName){
	    qq.FileUploaderBasic.prototype._onSubmit.apply(this, arguments);
	},
	_onProgress: function(id, fileName, loaded, total){
	    qq.FileUploaderBasic.prototype._onProgress.apply(this, arguments);
	},
	_onComplete: function(id, fileName, result){
	    qq.FileUploaderBasic.prototype._onComplete.apply(this, arguments);
	},
	_createUploadHandler: function(){
	    var self = this,
	        handlerClass;        
	    
	    if (this._options.handlerClass) {
	    	handlerClass = this._options.handlerClass;
	    } else {
	    	if(qq.UploadHandlerXhr.isSupported()){           
		        handlerClass = 'UploadHandlerXhr';                        
		    } else {
		        handlerClass = 'UploadHandlerForm';
		    }
	    }
	
	    var handler = new qq[handlerClass]({
	        debug: this._options.debug,
	        action: this._options.action,         
	        maxConnections: this._options.maxConnections,   
	        onProgress: function(id, fileName, loaded, total){                
	            self._onProgress(id, fileName, loaded, total);
	            self._options.onProgress(id, fileName, loaded, total);                    
	        },            
	        onComplete: function(id, fileName, result){
	            self._onComplete(id, fileName, result);
	            self._options.onComplete(id, fileName, result);
	        },
	        onCancel: function(id, fileName){
	            self._onCancel(id, fileName);
	            self._options.onCancel(id, fileName);
	        }
	    });
	
	    return handler;
	}
});

/**
 * Class for uploading files using form and iframe
 * @inherits qq.UploadHandlerAbstract
 */
qq.MapsUploadHandlerForm = function(o){
    qq.UploadHandlerAbstract.apply(this, arguments);
       
    this._inputs = {};
};
// @inherits qq.UploadHandlerAbstract
qq.extend(qq.MapsUploadHandlerForm.prototype, qq.UploadHandlerForm.prototype);

qq.extend(qq.MapsUploadHandlerForm.prototype, {
	add: function(fileInput){
	    fileInput.setAttribute('name', 'uploadedFile');
	    var id = 'qq-upload-handler-iframe' + qq.getUniqueId();       
	    
	    this._inputs[id] = fileInput;
	    
	    // remove file input from DOM
	    if (fileInput.parentNode){
	        qq.remove(fileInput);
	    }
	            
	    return id;
	},
	/**
     * Creates form, that will be submitted to iframe
     */
    _createForm: function(iframe, params){
        // We can't use the following code in IE6
        // var form = document.createElement('form');
        // form.setAttribute('method', 'post');
        // form.setAttribute('enctype', 'multipart/form-data');
        // Because in this case file won't be attached to request
        var form = qq.toElement('<form method="post" enctype="multipart/form-data"></form>');

        if (params) {
        	$H(params).each(function(pair) {
        		form.insert(new Element('input', {type : 'hidden', name : pair.key, value : pair.value}));
        	});
        }

        form.setAttribute('action', this._options.action);
        form.setAttribute('target', iframe.name);
        form.style.display = 'none';
        document.body.appendChild(form);

        return form;
    }
});