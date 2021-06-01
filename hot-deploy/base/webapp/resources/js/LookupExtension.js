LookupExtension = {
    load : function() {
        if (typeof 'Lookup' !== 'undefined') {
            Lookup.prototype.afterLoadModal_impl = LookupProperties.afterLoadModal;
            Lookup.prototype.afterLoadModal = function() {
            	return Lookup.prototype.afterLoadModal_impl.apply(this, arguments);
            };

            //
            Lookup.prototype.afterHideModal_impl = LookupProperties.afterHideModal;
            Lookup.prototype.afterHideModal = function() {
            	return Lookup.prototype.afterHideModal_impl.apply(this, arguments);
            };

            //
            Lookup.prototype.beforeHideModal_impl = LookupProperties.beforeHideModal;
            Lookup.prototype.beforeHideModal = function() {
                return Lookup.prototype.beforeHideModal_impl.apply(this, arguments);
            };
        }
    }
}

document.observe("dom:loaded", LookupExtension.load);