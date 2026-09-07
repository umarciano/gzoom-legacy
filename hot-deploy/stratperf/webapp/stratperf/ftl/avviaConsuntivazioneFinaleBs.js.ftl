<#--
    Doppio ciclo CTX_BS - bottone "Avvia consuntivazione finale"
    Iniettato via layoutSettings.javaScriptBlocks[] in WorkEffortRootExecViewSearchFormScreen.
    loadjavascript.ftl aggiunge gia' il wrapper <script>: questo file deve contenere solo JS puro.
    Abilitato solo quando: filtro Stato = "Consuntivata - intermedio" + almeno 1 riga in lista.
-->
(function() {
    'use strict';
    var STATO_DESCR = 'Consuntivata - intermedio';
    var BUTTON_ID   = 'btnAvviaConsFinaleBs';

    function findStatusFilter() {
        // weStatusDescr e' un drop-list OFBiz: chiave = descrizione (input hidden).
        var h = document.querySelector('input[type="hidden"][name="weStatusDescr"]');
        return h ? h.value : null;
    }

    function collectWorkEffortIds() {
        var ids = [];
        var seen = {};
        var checked = document.querySelectorAll('input[type="checkbox"][name*="_rowSubmit_"]:checked');
        if (checked.length) {
            for (var i = 0; i < checked.length; i++) {
                var m = checked[i].name.match(/_rowSubmit_o_(\d+)/);
                if (!m) continue;
                var hid = document.querySelector('input[name="workEffortId_o_' + m[1] + '"]');
                if (hid && hid.value && !seen[hid.value]) { seen[hid.value] = 1; ids.push(hid.value); }
            }
        }
        if (!ids.length) {
            var all = document.querySelectorAll('input[name^="workEffortId_o_"]');
            for (var j = 0; j < all.length; j++) {
                var v = all[j].value;
                if (v && !seen[v]) { seen[v] = 1; ids.push(v); }
            }
        }
        return ids;
    }

    function refreshList() {
        var sb = document.querySelector('form input[name="_action_Find"], input[value="Ricerca"]');
        if (sb) { sb.click(); } else { window.location.reload(); }
    }

    function callServer(ids) {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', '/stratperf/control/avviaConsuntivazioneFinaleBs', true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onload = function() {
            var msg = 'Operazione completata';
            try {
                var d = JSON.parse(xhr.responseText);
                msg = 'Schede avanzate: ' + (d.advancedCount || 0) + '\nIgnorate: ' + (d.skippedCount || 0);
            } catch(e) {}
            alert(msg);
            refreshList();
        };
        xhr.onerror = function() { alert('Errore chiamata server'); };
        xhr.send('workEffortIds=' + encodeURIComponent(ids.join(',')));
    }

    function updateButtonState(btn) {
        var descr   = findStatusFilter();
        var hasRows = collectWorkEffortIds().length > 0;
        var active  = (descr === STATO_DESCR) && hasRows;
        btn.disabled = !active;
        btn.style.opacity = active ? '1' : '0.5';
        btn.title = active
            ? 'Avvia la consuntivazione finale per le schede in lista'
            : 'Disponibile solo con filtro "Consuntivata - intermedio" e almeno un risultato';
    }

    function findToolbar() {
        // Prova selettori noti per la toolbar management OFBiz/GZOOM.
        var candidates = [
            'td.buttonTdArea',
            '.tabletext .buttonTdArea',
            '.screenlet-title-bar ul',
            '.screenlet-title-bar',
            '.basicRightWrap',
            '.basicNavGroup',
            '.toolbar',
            'table.tabletext tr td'
        ];
        for (var i = 0; i < candidates.length; i++) {
            var el = document.querySelector(candidates[i]);
            if (el) {
                console.log('[AvviaConsFinaleBs] toolbar trovato con selettore: ' + candidates[i] + ' tag=' + el.tagName + ' class=' + el.className);
                return el;
            }
        }
        // Fallback: vicino al primo pulsante buttontext.
        var btn = document.querySelector('input.buttontext, a.buttontext, .buttontext');
        if (btn && btn.parentNode) {
            console.log('[AvviaConsFinaleBs] toolbar fallback: parentNode di .buttontext, tag=' + btn.parentNode.tagName);
            return btn.parentNode;
        }
        console.log('[AvviaConsFinaleBs] toolbar non trovato, uso document.body');
        return document.body;
    }

    function ensureButton() {
        if (document.getElementById(BUTTON_ID)) return;
        var toolbar = findToolbar();
        var btn = document.createElement('input');
        btn.type    = 'button';
        btn.id      = BUTTON_ID;
        btn.value   = 'Avvia consuntivazione finale';
        btn.className = 'buttontext';
        btn.style.marginLeft = '6px';
        btn.addEventListener('click', function() {
            var ids = collectWorkEffortIds();
            if (!ids.length) { alert('Nessuna scheda in lista'); return; }
            if (!confirm('Confermi l\'avanzamento di ' + ids.length + ' scheda/e a "Da consuntivare"?')) return;
            callServer(ids);
        });
        toolbar.appendChild(btn);
        updateButtonState(btn);
        console.log('[AvviaConsFinaleBs] bottone creato, disabled=' + btn.disabled);
    }

    function boot() {
        try {
            ensureButton();
            var btn = document.getElementById(BUTTON_ID);
            if (btn) updateButtonState(btn);
        } catch(e) {
            console.log('[AvviaConsFinaleBs] errore in boot: ' + e);
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', boot);
    } else {
        boot();
    }
    // Prototype.js AJAX completion (no jQuery 'ajaxComplete' qui).
    if (typeof Ajax !== 'undefined' && Ajax.Responders) {
        Ajax.Responders.register({ onComplete: function() { setTimeout(boot, 300); } });
    }
})();
