<#-- Pulsante "Salva voti" per la Performance Individuale (CTX_EP): salva i voti digitati inline nella
     colonna "Valore" della griglia competenze via AJAX (evento saveEmplScores -> ACTUAL_PY sul conto
     competenza). Stile coerente con il pulsante "Condividi Valutazione al Valutato".
     Rende SOLO se: scheda CTX_EP  E  NON siamo in Interrogazione (rootInqyTree != 'Y'). -->
<#assign _weId = (parameters.workEffortId)!(workEffortId!"")>
<#assign _inqy = (parameters.rootInqyTree)!"">
<#if _weId?has_content && _inqy != "Y" && delegator??>
  <#attempt>
    <#assign _um = Static["org.ofbiz.base.util.UtilMisc"]>
    <#assign _we = delegator.findOne("WorkEffort", _um.toMap("workEffortId", _weId), false)!>
    <#-- Il pulsante "Salva voti" si mostra SOLO al valutatore (WEM_EVAL_MANAGER) di questa scheda,
         stesso vincolo di scoreEditable e del salvataggio: il valutato e gli altri non lo vedono. -->
    <#assign _myParty = (userLogin.partyId)!"">
    <#assign _isEvalMgr = false>
    <#if _myParty?has_content>
      <#assign _wepa = delegator.findByAnd("WorkEffortPartyAssignment", _um.toMap("workEffortId", _weId, "partyId", _myParty, "roleTypeId", "WEM_EVAL_MANAGER"))!>
      <#if _wepa?has_content><#assign _isEvalMgr = true></#if>
    </#if>
    <#if _we?? && ((_we.getString("workEffortTypeId"))!"") == "CTX_EP" && _isEvalMgr>
      <div class="empl-score-save-row" style="text-align:right; padding:10px 12px;">
        <button id="saveEmplScoresBtn_${_weId}" type="button" class="mediumSubmit"
                style="font-size:12px; padding:6px 12px; background-color:#4169E1; color:#fff; border:none; border-radius:3px; cursor:pointer; transition:all .2s ease;"
                onmouseover="this.style.backgroundColor='#365bb3';"
                onmouseout="this.style.backgroundColor='#4169E1';">
          <i class="fa fa-save" style="margin-right:6px;"></i>Salva voti
        </button>
      </div>
      <script type="text/javascript">
      //<![CDATA[
        (function(){
          function reloadEmplGrid(){
            var lis = $$('li'), li = null;
            for (var i=0;i<lis.length;i++){ if((lis[i].textContent||'').strip() === 'Valutazione Scheda'){ li = lis[i]; break; } }
            if (li){ var a = li.down('a'); (a||li).click(); }
          }
          function initEmplScoreValidation(){
            // Validazione inline 0-5 sulle celle "Valore" mentre si digita: bordo rosso + tooltip quando
            // il voto e' fuori range. Dependency-free (solo Prototype, gia' presente); LiveValidation NON
            // e' caricata in questo contesto legacy.
            $$('input[name^="indicatorScore_o_"]').each(function(inp){
              if (inp._emplValAttached) return;
              inp._emplValAttached = true;
              var check = function(){
                var v = (inp.getValue() || '').strip();
                var bad = false;
                if (v !== ''){ var n = parseFloat(v.replace(',', '.')); bad = isNaN(n) || n < 0 || n > 5; }
                if (bad){
                  inp.setStyle({ border: '1px solid #d33', backgroundColor: '#fff0f0' });
                  inp.writeAttribute('title', 'Voto ammesso: da 0 a 5');
                } else {
                  inp.setStyle({ border: '', backgroundColor: '' });
                  inp.writeAttribute('title', '');
                }
              };
              inp.observe('keyup', check);
              inp.observe('change', check);
              inp.observe('blur', check);
              check();
            });
          }
          function initSaveBtn(){
            var btn = $('saveEmplScoresBtn_${_weId}');
            if (!Object.isElement(btn)) return;
            initEmplScoreValidation();
            btn.stopObserving('click');
            btn.observe('click', function(ev){
              Event.stop(ev);
              var wemIds = [], scores = [];
              $$('input[name^="indicatorScore_o_"]').each(function(inp){
                var idx = inp.readAttribute('name').substring('indicatorScore_o_'.length);
                var wemEl = $$('input[name="workEffortMeasureId_o_' + idx + '"]').first();
                if (Object.isElement(wemEl)) { wemIds.push(wemEl.getValue()); scores.push(inp.getValue()); }
              });
              if (wemIds.length === 0){ modal_box_messages.alert(['BaseMessageSaveDataNoData']); return; }
              // Controllo range 0-5 prima di inviare (coerente con la validazione server + inline).
              var invalid = [];
              for (var k = 0; k < scores.length; k++){
                var v = (scores[k] || '').toString().strip();
                if (v === '') continue; // vuoto = "non toccare" (lato server: skip, non azzera). Per azzerare: 0
                var num = parseFloat(v.replace(',', '.'));
                if (isNaN(num) || num < 0 || num > 5){ invalid.push(v); }
              }
              if (invalid.length > 0){
                modal_box_messages.alert('Voti non validi (ammessi da 0 a 5): ' + invalid.join(', '));
                return;
              }
              var oldHtml = btn.innerHTML; btn.disabled = true;
              btn.innerHTML = '<i class="fa fa-spinner fa-spin" style="margin-right:6px;"></i>Salvataggio...';
              new Ajax.Request('<@ofbizUrl>saveEmplScores</@ofbizUrl>', {
                method: 'post',
                parameters: { workEffortId: '${_weId}', wemIds: wemIds.join(','), scores: scores.join(',') },
                onSuccess: function(t){
                  btn.disabled = false; btn.innerHTML = oldHtml;
                  var r; try { r = t.responseText.evalJSON(); } catch(e){ r = { success:false }; }
                  if (r.success){
                    reloadEmplGrid();
                    modal_box_messages.alert('Voti salvati con successo.');
                  } else {
                    modal_box_messages.alert('Errore nel salvataggio: ' + (r.error || 'operazione non riuscita'));
                  }
                },
                onFailure: function(){
                  btn.disabled = false; btn.innerHTML = oldHtml;
                  modal_box_messages.alert('Errore di connessione durante il salvataggio.');
                }
              });
            });
          }
          if (document.loaded){ initSaveBtn(); } else { document.observe('dom:loaded', initSaveBtn); }
        })();
      //]]>
      </script>
    </#if>
  <#recover>
  </#attempt>
</#if>
