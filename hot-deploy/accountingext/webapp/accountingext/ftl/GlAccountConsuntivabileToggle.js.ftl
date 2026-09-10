GlAccountConsuntivabileToggle = {
    init: function() {
        // glAccountTypeId e' un drop-list: hidden input per la chiave, text per la descrizione
        var tipoInput = $$('input[name=glAccountTypeId]')[0];
        if (!tipoInput) return;

        var fieldRow = GlAccountConsuntivabileToggle.findFieldRow();
        if (!fieldRow) return;

        // stato iniziale (in insert mode la chiave e' vuota -> nascosto)
        var lastVal = tipoInput.value;
        GlAccountConsuntivabileToggle.toggle(lastVal, fieldRow);

        // l'autocompleter setta il valore via JS senza sparare eventi nativi:
        // si usa un polling leggero per rilevare il cambio
        window.setInterval(function() {
            var current = tipoInput.value;
            if (current !== lastVal) {
                lastVal = current;
                GlAccountConsuntivabileToggle.toggle(current, fieldRow);
            }
        }, 250);
    },

    findFieldRow: function() {
        var fields = $$('[name=consuntivabileParzialmente]');
        if (!fields || fields.length === 0) return null;
        return fields[0].up('tr');
    },

    toggle: function(value, row) {
        if (value === 'WECAL') {
            row.show();
        } else {
            row.hide();
        }
    }
};

if (document.loaded) {
    GlAccountConsuntivabileToggle.init();
} else {
    document.observe('dom:loaded', function() {
        GlAccountConsuntivabileToggle.init();
    });
}
