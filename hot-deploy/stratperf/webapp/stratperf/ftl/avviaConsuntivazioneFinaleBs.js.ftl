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
        // Tentativo 1: checkboxes selezionati in multi-form (se presenti)
        var checked = document.querySelectorAll('input[type="checkbox"][name*="_rowSubmit_"]:checked');
        if (checked.length) {
            for (var i = 0; i < checked.length; i++) {
                var m = checked[i].name.match(/_rowSubmit_o_(\d+)/);
                if (!m) continue;
                var hid = document.querySelector('input[name="workEffortId_o_' + m[1] + '"]');
                if (hid && hid.value && !seen[hid.value]) { seen[hid.value] = 1; ids.push(hid.value); }
            }
        }
        // Tentativo 2: tutti gli hidden inputs workEffortId_o_N (multi-form senza selezione)
        if (!ids.length) {
            var all = document.querySelectorAll('input[name^="workEffortId_o_"]');
            for (var j = 0; j < all.length; j++) {
                var v = all[j].value;
                if (v && !seen[v]) { seen[v] = 1; ids.push(v); }
            }
        }
        // Tentativo 3: estrai dagli href dei link nella griglia (quando ci sono <a href="...workEffortId=...">)
        if (!ids.length) {
            var links = document.querySelectorAll('td a[href*="workEffortId="]');
            for (var k = 0; k < links.length; k++) {
                var m2 = links[k].href.match(/workEffortId=([^&]+)/);
                if (m2 && !seen[m2[1]]) { seen[m2[1]] = 1; ids.push(m2[1]); }
            }
        }
        // Tentativo 4: hidden inputs "workEffortId" senza suffisso (type="list" form OFBiz non aggiunge _o_N)
        if (!ids.length) {
            var hiddens = document.querySelectorAll('input[type="hidden"][name="workEffortId"]');
            for (var n = 0; n < hiddens.length; n++) {
                var hv = hiddens[n].value;
                if (hv && !seen[hv]) { seen[hv] = 1; ids.push(hv); }
            }
        }
        console.log('[AvviaConsFinaleBs] collectIds: found ' + ids.length + ' ids');
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
        var ids     = collectWorkEffortIds();
        var active  = (descr !== null && descr.trim() === STATO_DESCR) && ids.length > 0;
        console.log('[AvviaConsFinaleBs] updateState descr="' + descr + '" ids=' + ids.length + ' active=' + active);
        btn.style.display  = active ? '' : 'none';
        btn.disabled       = !active;
        btn.title = active
            ? 'Avvia la consuntivazione finale per le ' + ids.length + ' schede in lista'
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
        btn.style.marginLeft = '1rem';
        btn.style.padding = '0.3rem';
        btn.style.cursor = 'pointer';
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

    // MutationObserver sul container risultati: si attiva quando l'AJAX search aggiorna il DOM
    // (piu' affidabile di Ajax.Responders che richiede Prototype.js in flow).
    // searchAreaId="common-container" e' impostato in StratPerfScreens.xml.
    var bootTimer = null;
    function scheduleBoot() {
        clearTimeout(bootTimer);
        bootTimer = setTimeout(boot, 300);
    }
    function startObserver() {
        var container = document.getElementById('common-container') ||
                        document.querySelector('.screenlet-body') ||
                        document.body;
        console.log('[AvviaConsFinaleBs] MutationObserver su: ' + (container.id || container.className || container.tagName));
        new MutationObserver(scheduleBoot).observe(container, { childList: true, subtree: true });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() { boot(); startObserver(); });
    } else {
        boot();
        startObserver();
    }
    // Prototype.js AJAX completion (fallback se Prototype.js e' il meccanismo di search).
    if (typeof Ajax !== 'undefined' && Ajax.Responders) {
        Ajax.Responders.register({ onComplete: scheduleBoot });
    }
})();
