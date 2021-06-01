
function ajaxUpdate(container, url, parameters, options) {
    parameters = parameters || {};
    parameters.ajax = 'Y';
    options = options || {};
    options.parameters = parameters;
    options.evalScripts = true;
    new Ajax.Updater(container, url, options);
}

function observeAll(selector, eventName, handler) {
    $$(selector).each(function(elem) {
        Event.observe(elem, eventName, handler.bindAsEventListener(elem));
    });
}

function hideAll(selector) {
    $$(selector).each(function(elem) {
        elem.hide();
    });
}

function clearAll(selector) {
    $$(selector).each(function(elem) {
        elem.update();
    });
}
