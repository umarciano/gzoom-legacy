<#-- Riga "Totale punteggio" sotto il grid "Indicatori di valutazione" (Performance Strategica).
     Dati da getIndicatorScoreTotal.groovy: scoreTotal (somma SCOREKPI) e scoreTotalMax (somma pesi). -->
<#assign _tot = (scoreTotal!0)>
<#assign _max = (scoreTotalMax!0)>
<div class="indicator-score-total-row"
     style="display:flex; justify-content:flex-end; align-items:center; gap:1.2rem; padding:.55rem 1rem;
            border-top:2px solid #d0d5dd; background:#f5f7fa; font-family:'Trebuchet MS',Tahoma,Arial,sans-serif;">
  <span style="color:#4a515c; font-weight:700;">Totale punteggio</span>
  <span style="color:#2b6cff; font-weight:700; font-size:1.05rem;">
    ${_tot?string("0.##")}<#if (_max > 0)> / ${_max?string("0.##")}</#if>
  </span>
</div>
