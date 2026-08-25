<#-- Riga "Totale punteggio" della Performance Individuale (CTX_EP): somma dei voti (ACTUAL_PY) sui conti
     competenza. Max grezzo = n competenze x 5 (/30). Rende SOLO se la scheda e' CTX_EP (layout condiviso).
     Posizionamento: la riga viene spostata via JS DENTRO la griglia, subito PRIMA del paginatore
     (div.nav-pager), cosi' appare sotto le competenze e prima della navigazione. -->
<#assign _weId = (parameters.workEffortId)!(workEffortId!"")>
<#if _weId?has_content && delegator??>
  <#attempt>
    <#assign _um = Static["org.ofbiz.base.util.UtilMisc"]>
    <#assign _we = delegator.findOne("WorkEffort", _um.toMap("workEffortId", _weId), false)!>
    <#if _we?? && ((_we.getString("workEffortTypeId"))!"") == "CTX_EP">
      <#assign _measures = delegator.findByAnd("WorkEffortMeasure", _um.toMap("workEffortId", _weId))>
      <#assign _acctIds = {}>
      <#list _measures as _m>
        <#assign _ga = (_m.getString("glAccountId"))!"">
        <#if _ga?has_content && _ga != "SCORE" && _ga != "SCOREKPI">
          <#assign _acctIds = _acctIds + {_ga: true}>
        </#if>
      </#list>
      <#assign _tot = 0>
      <#assign _trans = delegator.findByAnd("AcctgTrans", _um.toMap("workEffortId", _weId, "glFiscalTypeId", "ACTUAL_PY"))>
      <#list _trans as _t>
        <#assign _entries = delegator.findByAnd("AcctgTransEntry", _um.toMap("acctgTransId", _t.getString("acctgTransId")))>
        <#list _entries as _e>
          <#assign _eGa = (_e.getString("glAccountId"))!"">
          <#if _acctIds[_eGa]?? && (_e.get("amount"))??>
            <#assign _tot = _tot + _e.getBigDecimal("amount")>
          </#if>
        </#list>
      </#list>
      <#assign _max = _acctIds?size * 5>
      <div id="emplScoreTotalRow"
           style="display:flex; justify-content:flex-end; align-items:center; gap:1.2rem; padding:.55rem 1rem;
                  border-top:2px solid #d0d5dd; background:#f5f7fa; font-family:'Trebuchet MS',Tahoma,Arial,sans-serif;">
        <span style="color:#4a515c; font-weight:700;">Totale punteggio</span>
        <span style="color:#2b6cff; font-weight:700; font-size:1.05rem;">
          ${_tot?string("0.##")}<#if (_max > 0)> / ${_max}</#if>
        </span>
      </div>
      <script type="text/javascript">
      //<![CDATA[
        (function(){
          function placeEmplTotal(tries){
            var tot = document.getElementById('emplScoreTotalRow');
            if (!tot) return;
            var inp = document.querySelector('input[name^="indicatorScore_o_"]');
            var form = inp ? inp.form : null;
            var pager = form ? form.querySelector('.nav-pager') : null;
            if (pager && pager.parentNode){
              pager.parentNode.insertBefore(tot, pager);   // totale subito PRIMA del paginatore
            } else if ((tries||0) < 10){
              setTimeout(function(){ placeEmplTotal((tries||0)+1); }, 150); // la griglia carica in AJAX: riprova
            }
          }
          placeEmplTotal(0);
        })();
      //]]>
      </script>
    </#if>
  <#recover>
  </#attempt>
</#if>
