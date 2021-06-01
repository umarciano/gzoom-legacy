RegisterInputMaskResponder = {
	load : function() {
		UpdateAreaResponder.Responders.register({
			onLoad : function(newContent) {
				if (!Object.isUndefined(InputMaskManager)) {
					InputMaskManager.loadAllMask(newContent);
				}
			}
		}, 'register-inputmask-responder');
	}
}

document.observe("dom:loaded", RegisterInputMaskResponder.load);