<#--
    Doppio ciclo CTX_BS - bottone toolbar "Avvia consuntivazione finale"
    Iniettato in WorkEffortRootExecViewSearchFormScreen (Gestione -> Valutazione).
    Comportamento:
      - Il bottone e' presente ma NON abilitato di default.
      - Si abilita SOLO quando:
          (a) il filtro stato applicato e' WEORCARD_ACC_INT
          (b) c'e' almeno una riga nella tabella dei risultati
      - Al click:
          1. Raccoglie i workEffortId visibili in tabella (o le righe selezionate se
             ci sono checkbox di riga presenti)
          2. Chiama POST /stratperf/control/avviaConsuntivazioneFinaleBs
          3. Mostra il risultato in una popup e ricarica la lista
    Nessun pre-filtro anno: e' l'utente a impostarlo dalla maschera.
-->
<script type="text/javascript">
(function() {
    'use strict';
    var STATO_ACC_INT = 'WEORCARD_ACC_INT';
    var BUTTON_ID = 'btnAvviaConsFinaleBs';

    function findStatusFilter() {
        // Cerca la select del filtro stato nella search form. Il name canonico e'
        // "currentStatusId" nella maschera CTX_BS.
        var candidates = document.querySelectorAll('select[name="currentStatusId"], select[name="currentStatusId_value"]');
        for (var i = 0; i < candidates.length; i++) {
            if (candidates[i].value) return candidates[i].value;
        }
        return null;
    }

    function findResultTable() {
        // La tabella dei risultati e' quella con le righe (multi-form) che espone
        // "workEffortId" come input/data-attribute.
        return document.querySelector('form input[name*="workEffortId"], table [data-work-effort-id]');
    }

    function collectWorkEffortIds() {
        var ids = new Set();
        // Priorita': se ci sono checkbox di selezione riga, prendi solo le selezionate.
        var checked = document.querySelectorAll('input[type="checkbox"][name*="_rowSubmit_"]:checked');
        if (checked.length) {
            checked.forEach(function(cb) {
                var idx = cb.name.match(/_rowSubmit_o_(\d+)/);
                if (!idx) return;
                var hidden = document.querySelector('input[name="workEffortId_o_' + idx[1] + '"]');
                if (hidden && hidden.value) ids.add(hidden.value);
            });
        }
        if (ids.size === 0) {
            // Fallback: prendi tutti i workEffortId presenti nel DOM della lista.
            document.querySelectorAll('input[name^="workEffortId_o_"], [data-work-effort-id]').forEach(function(el) {
                var v = el.value || el.getAttribute('data-work-effort-id');
                if (v) ids.add(v);
            });
        }
        return Array.from(ids);
    }

    function refreshList() {
        // Riesegue la search: click sul submit della form di ricerca, se disponibile.
        var searchForm = document.querySelector('form[name*="Search"], form#search-form');
        if (searchForm) {
            var submitBtn = searchForm.querySelector('button[type="submit"], input[type="submit"]');
            if (submitBtn) submitBtn.click();
        } else {
            window.location.reload();
        }
    }

    function callServer(ids) {
        var csv = ids.join(',');
        var xhr = new XMLHttpRequest();
        xhr.open('POST', '/stratperf/control/avviaConsuntivazioneFinaleBs', true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onload = function() {
            var msg = 'Operazione completata';
            try {
                var data = JSON.parse(xhr.responseText);
                msg = 'Schede avanzate: ' + (data.advancedCount || 0) +
                      '\nSchede ignorate: ' + (data.skippedCount || 0);
            } catch (e) { /* ignora */ }
            alert(msg);
            refreshList();
        };
        xhr.onerror = function() { alert('Errore: impossibile eseguire l\'operazione'); };
        xhr.send('workEffortIds=' + encodeURIComponent(csv));
    }

    function updateButtonState(btn) {
        var isAccInt = findStatusFilter() === STATO_ACC_INT;
        var hasRows = collectWorkEffortIds().length > 0;
        btn.disabled = !(isAccInt && hasRows);
        btn.title = btn.disabled
            ? 'Disponibile solo con filtro stato = "Consuntivata - intermedio" e risultati in lista'
            : 'Avvia la consuntivazione finale per le schede in lista';
    }

    function ensureButton() {
        if (document.getElementById(BUTTON_ID)) return;
        // Toolbar dei risultati: cerca un container ".buttonTdArea" o simili.
        var toolbar = document.querySelector('.tabletext .buttonTdArea, .basicRightWrap, .basicNavGroup, .toolbar');
        if (!toolbar) return;
        var btn = document.createElement('button');
        btn.type = 'button';
        btn.id = BUTTON_ID;
        btn.className = 'buttontext';
        btn.style.marginLeft = '8px';
        btn.textContent = 'Avvia consuntivazione finale';
        btn.addEventListener('click', function() {
            var ids = collectWorkEffortIds();
            if (!ids.length) { alert('Nessuna scheda in lista'); return; }
            if (!confirm('Confermi l\'avanzamento di ' + ids.length + ' scheda/e a "Da consuntivare"?')) return;
            callServer(ids);
        });
        toolbar.appendChild(btn);
        updateButtonState(btn);
    }

    function boot() {
        ensureButton();
        var btn = document.getElementById(BUTTON_ID);
        if (btn) updateButtonState(btn);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', boot);
    } else {
        boot();
    }
    // Reboot dopo refresh AJAX della lista
    document.addEventListener('ajaxComplete', boot);
})();
</script>
