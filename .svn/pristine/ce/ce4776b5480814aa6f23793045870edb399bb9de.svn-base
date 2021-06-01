function registerRichTextAreas() {
	var multiTypeLang = '${multiTypeLang?default("")}';
	if (multiTypeLang && multiTypeLang.length > 0 && multiTypeLang != 'NONE') {
		registerRichTextArea_${textAreaId?if_exists}(200);
		registerRichTextArea_${textAreaIdLang?if_exists}(200);
	} else {
		registerRichTextArea_${textAreaId?if_exists}(400);
	}
}

function registerRichTextArea_${textAreaId?if_exists}(elrteHeight) {
	var textAreas = $$('#${textAreaId?if_exists}');
	registerRichTextArea(textAreas, 'noteInfo', elrteHeight);
}

function registerRichTextArea_${textAreaIdLang?if_exists}(elrteHeight) {
	var textAreas = $$('#${textAreaIdLang?if_exists}');
	registerRichTextArea(textAreas, 'noteInfoLang', elrteHeight);
}

function registerRichTextArea(textAreas, noteFieldName, elrteHeight) {
    var paramNoteId = '${parameters.noteId?if_exists}';
		
	var textArea;
	var formTextaArea;
	var formTextaAreaNoteId;
	
    if(textAreas) {
	    textAreas.each(function(ta) {
	         formTextaArea = ta.up('form');
	         formTextaAreaNoteId = null;
	         if(formTextaArea) {
	             formTextaAreaNoteId = formTextaArea['noteId'].getValue();
	         }
	         if(paramNoteId == formTextaAreaNoteId) {
	             textArea = ta;
	         }
	    });	
	}
	
	var cssFiles = $A(['<@ofbizContentUrl>/resources/css/elrte-custom.css</@ofbizContentUrl>']);
    
    var isPosted = '${isPosted?if_exists}';
    var isReadOnly = '${parameters.isReadOnly?if_exists}';
    var isObiettivo = '${isObiettivo?if_exists}';
	
    //log.debug('********************* cssFiles=' + cssFiles);
    if (Object.isElement(textArea)) {
		
		<#if (isReadOnly?has_content && isReadOnly == true) || (isPosted?has_content && isPosted == 'Y' && isObiettivo != 'Y')>
		//devo disabilitare tutto il pannello
		
		textArea.addClassName("hidden");
		
		//Bug 4768
		var divtextarea = textArea.up("td." + noteFieldName);
		if(Object.isElement(divtextarea)) {
		  <#if listTable?has_content && listTable == 'Y'>
		      divtextarea.insert("<div  ><iframe name='" + noteFieldName + "'  style='height: 200px; width: 100%; border: 0px;'/></div>");
		      <#else>
		      divtextarea.insert("<div  style='height: 200px; width: 95%;'><iframe name='" + noteFieldName + "' class='disabled cella'  style='height: 200px; width: 95%;'/></div>");
		  </#if>
		   
			divtextarea.select('iframe').each(function(iframe) {
				
				html = '<html xmlns="http://www.w3.org/1999/xhtml"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /></head><body style="padding:0;margin:0;"></body></html>';
				iframe.contentWindow.document.open();
				iframe.contentWindow.document.write(html);
				//iframe.contentDocument.body.innerHTML = textArea.innerHTML;
				iframe.contentWindow.document.write(textArea.innerHTML.unescapeHTML());
				iframe.contentWindow.document.close();
			
			});
			
		}

		
		<#else>
		elRTE.prototype.options.panels.web2pyPanel = [
	      'bold', 'italic', 'underline', 'forecolor', 'justifyleft', 'justifyright',
	      'justifycenter', 'justifyfull', 'insertorderedlist', 'insertunorderedlist',
	      'link', 'image'
	  	];
	  	
	  	elRTE.prototype.options.buttons.customformatlist = 'Formato';
	  	
	  	elRTE.prototype.options.panels.custom_style = ['bold', 'italic', 'underline'];
	  	elRTE.prototype.options.panels.custom_copypaste = ['copy', 'cut', 'paste', 'pastetext', 'pasteformattext', 'removeformat'];
	  	elRTE.prototype.options.panels.custom_elements = ['blockquote', 'div', 'stopfloat', 'nbsp', 'pagebreak', 'smiley', 'image'];
	  	elRTE.prototype.options.panels.tables = ['table', 'tableprops', 'tablerm',  'tbrowafter', 'tbrowrm', 'tbcolafter', 'tbcolrm', 'tbcellprops', 'tbcellsmerge', 'tbcellsplit'],
	  	
	  	elRTE.prototype.options.panels.custom_format = ['customformatlist'];
	  	
	  	<#if !defaultStyleList?has_content>
	  	elRTE.prototype.options.toolbars.web2pyToolbar = ['undoredo', 'custom_copypaste', 'custom_style', 'alignment', 'format', 'lists', 'custom_elements', 'tables', 'links'];
	  	<#else>
	  	elRTE.prototype.options.toolbars.web2pyToolbar = ['undoredo', 'custom_copypaste', 'custom_style', 'alignment', 'custom_format', 'lists', 'custom_elements', 'tables', 'links'];
	  	</#if>
		
		var opts = {
					lang         : '${locale.getLanguage()}',   // set your language
					styleWithCSS : false,
					height       : elrteHeight,
					toolbar      : 'web2pyToolbar',
					//toolbar      : 'maxi',
					resizable    : false,
					allowSource  : true,
					absoluteURLs : false
					/*,
					cssfiles     : cssFiles*/
					 
			};
			
		var editor = new elRTE(textArea, opts);
		
		textArea.getValue = textArea.getValue.wrap(
			function(proceed) {
				return editor.doc.body.innerHTML;
			}
		)	
					
		</#if>
		
	}
}


registerRichTextAreas();