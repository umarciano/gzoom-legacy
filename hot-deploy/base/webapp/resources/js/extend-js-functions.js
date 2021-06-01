ExtendJsFunction = {
    afterEffectAppearFinish : function(effect) {
        if (effect.options.areaId) {
            var area = $(effect.options.areaId);
            if (Object.isElement(area)) {
                var forms = $A(area.select('form'));
                forms.each(function(form) {
                    var elementToFocus = ExtendJsFunction._findFirstElement(form, ['input']);
                    if (elementToFocus) {
                        elementToFocus.focus();
                        throw $break;
                    }
                });
            }
        }
    },
    _findFirstElement: function(container, typeElement) {
        typeElement = typeElement || ['input', 'select', 'textarea'];
        var elements = $(container).descendants().findAll(function(element) {
          return 'hidden' != element.type && !element.disabled;
        });
        var firstByIndex = elements.findAll(function(element) {
          return element.hasAttribute('tabIndex') && element.tabIndex >= 0;
        }).sortBy(function(element) { return element.tabIndex }).first();

        return firstByIndex ? firstByIndex : elements.find(function(element) {
          return typeElement.include(element.tagName.toLowerCase());
        });
    },
    load: function() {
        toggleScreenlet = toggleScreenlet.wrap(
            function(proceed, link, areaId, options, expandTxt, collapseTxt) {
                if (link.up('li').hasClassName('collapsed'))
                    options = Object.extend({afterFinish: ExtendJsFunction.afterEffectAppearFinish, 'areaId' : areaId}, options || {});
                proceed(link, areaId, options, expandTxt, collapseTxt);
            }
        );
    }
};



document.observe("dom:loaded", ExtendJsFunction.load);