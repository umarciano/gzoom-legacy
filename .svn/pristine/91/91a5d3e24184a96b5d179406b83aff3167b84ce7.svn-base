ModalboxExtension = {
    load: function() {
        Modalbox._kbdHandler = Modalbox._kbdHandler.wrap(function(proceed, event) {
            var node = event.element();
            switch(event.keyCode) {
                case Event.KEY_TAB:
                    break;
                default:
                    proceed(event);
            }
        });
    }
}

document.observe("dom:loaded", ModalboxExtension.load);