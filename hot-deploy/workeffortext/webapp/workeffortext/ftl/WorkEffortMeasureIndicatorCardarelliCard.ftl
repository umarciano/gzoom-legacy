<#-- ============================================================================
     Card dettaglio indicatore Performance Strategica (Cardarelli)
     Mostra i campi anagrafici del requisito quando l'indicatore e' aperto dalla scheda.
     Context atteso (impostato in WorkEffortMeasureIndicatorCardarelliTransactionPanel):
       glAccount, glResourceType, workEffortMeasureSecondary,
       referentePartyName, fasceList (UomRangeValues), soglieList (WorkEffortTransactionView)
     ============================================================================ -->
<div id="container-${parameters.reloadRequestType!}Transaction-${parameters.contentIdInd!}" class="indicator-card-cardarelli">
  <#if glAccount?has_content>
    <table class="basic-table" cellspacing="0" style="width:100%;">
      <tbody>
        <tr>
          <td style="width:220px; font-weight:bold;">Area</td>
          <td>${(glResourceType.description)!glAccount.glResourceTypeId!""}</td>
        </tr>
        <tr class="alternate-row">
          <td style="font-weight:bold;">Codice</td>
          <td>${glAccount.accountCode!""}</td>
        </tr>
        <tr>
          <td style="font-weight:bold;">Indicatore</td>
          <td>${glAccount.accountName!""}</td>
        </tr>
        <tr class="alternate-row">
          <td style="font-weight:bold;">Descrizione</td>
          <td>${glAccount.description!""}</td>
        </tr>
        <tr>
          <td style="font-weight:bold;">Formula</td>
          <td>
            <#if formulaParlante?has_content>${formulaParlante}<#else>Valore diretto</#if>
          </td>
        </tr>
        <tr class="alternate-row">
          <td style="font-weight:bold;">Fonte dati</td>
          <td>${glAccount.source!""}</td>
        </tr>
        <tr>
          <td style="font-weight:bold;">Peso (sessantesimi)</td>
          <td>${(workEffortMeasureSecondary.kpiScoreWeight)!""}</td>
        </tr>
        <tr class="alternate-row">
          <td style="font-weight:bold;">Referente</td>
          <td>${referentePartyName!"(non assegnato)"}</td>
        </tr>
        <#-- SI_NO: nessuna scala a fasce, target = "Si" -->
        <#assign isSiNo = (glAccount.calcCustomMethodId!"") == "SI_NO"/>
        <#-- Target: per SI_NO e' "Si"; altrimenti il valore singolo (confine banda 100%) -->
        <tr>
          <td style="font-weight:bold;">Target</td>
          <td><#if isSiNo>S&igrave;<#elseif targetValue??>${targetValue}<#else><span style="color:#888; font-style:italic;">n/d</span></#if></td>
        </tr>
        <#-- Range / fasce: le 4 bande reali dell'indicatore (valore → punteggio). Per SI_NO non si applica. -->
        <tr class="alternate-row">
          <td style="font-weight:bold; vertical-align:top;">Range (fasce)</td>
          <td>
            <#if isSiNo>
              <span style="color:#888; font-style:italic;">Esito S&igrave; / No (nessuna scala a fasce)</span>
            <#elseif fasceList?has_content>
              <table class="basic-table" cellspacing="0">
                <thead><tr class="header-row"><th>Fascia</th><th>Punteggio %</th></tr></thead>
                <tbody>
                  <#list fasceList as f>
                    <tr>
                      <td><#if (f.fromValue <= -900000)>&lt; ${(f.thruValue + 0.01)}<#elseif (f.thruValue >= 900000)>&gt;= ${f.fromValue}<#else>${f.fromValue} - ${f.thruValue}</#if></td>
                      <td>${f.rangeValuesFactor!""}</td>
                    </tr>
                  </#list>
                </tbody>
              </table>
            <#else>
              <span style="color:#888; font-style:italic;">nessuna scala fasce associata</span>
            </#if>
          </td>
        </tr>
        <tr>
          <td style="font-weight:bold; vertical-align:top;">Commento</td>
          <td>
            <#if canEditIndicatorComment?? && canEditIndicatorComment>
              <form method="post" action="/stratperf/control/updateStratPerfMeasureComment" onsubmit="var form = this; var button = form.querySelector('button[type=submit]'); button.disabled = true; new Ajax.Request(form.action, {method: 'post', parameters: Form.serialize(form), onSuccess: function(data) { modal_box_messages.onAjaxLoad(data, null); modal_box_messages.alert('Commento salvato.'); }, onFailure: function() { button.disabled = false; modal_box_messages.alert('Errore nel salvataggio del commento.'); }}); return false;">
                <input type="hidden" name="workEffortMeasureId" value="${workEffortMeasureSecondary.workEffortMeasureId!""}"/>
                <textarea name="comments" rows="4" cols="100" oninput="this.form.querySelector('button[type=submit]').disabled = false;">${workEffortMeasureSecondary.comments!""}</textarea>
                <button type="submit" disabled="disabled">Salva</button>
              </form>
            <#else>
              ${workEffortMeasureSecondary.comments!""}
            </#if>
          </td>
        </tr>
      </tbody>
    </table>
  <#else>
    <div style="color:#cc0000;">Indicatore non trovato.</div>
  </#if>
</div>
