<#-- Righe "Totale punteggio" sotto il grid "Indicatori di valutazione" (Performance Strategica).
     Dati da getIndicatorScoreTotal.groovy:
       scoreTotal / scoreTotalMax      -> ciclo finale (tutti gli indicatori)
       scoreTotalInt / scoreTotalMaxInt -> ciclo semestrale (solo misure consuntivabileParzialmente=Y)
       hasIntermediate                 -> true se almeno una misura ha flag=Y -->
<#assign _tot    = (scoreTotal!0)>
<#assign _max    = (scoreTotalMax!0)>
<#assign _totInt = (scoreTotalInt!0)>
<#assign _maxInt = (scoreTotalMaxInt!0)>
<#assign _hasInt = (hasIntermediate!false)>
<div class="indicator-score-total-row"
     style="display:flex; flex-direction:column; align-items:flex-end; gap:.25rem; padding:.55rem 1rem;
            border-top:2px solid #d0d5dd; background:#f5f7fa; font-family:'Trebuchet MS',Tahoma,Arial,sans-serif;">
  <#if _hasInt>
  <div style="display:flex; align-items:center; gap:1.2rem;">
    <span style="color:#4a515c; font-weight:700;">Totale punteggio semestrale</span>
    <span style="color:#2b6cff; font-weight:700; font-size:1.05rem;">
      ${_totInt?string("0.##")}<#if (_maxInt > 0)> / ${_maxInt?string("0.##")}</#if>
    </span>
  </div>
  </#if>
  <div style="display:flex; align-items:center; gap:1.2rem;">
    <span style="color:#4a515c; font-weight:700;">Totale punteggio finale</span>
    <span style="color:#2b6cff; font-weight:700; font-size:1.05rem;">
      ${_tot?string("0.##")}<#if (_max > 0)> / ${_max?string("0.##")}</#if>
    </span>
  </div>
</div>
