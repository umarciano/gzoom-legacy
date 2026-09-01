<#--
   Parametri di stampa CTX_BS - Scheda 3 (Consuntivazione), report REPORT_BS_DETT.

   Dialog RIDOTTO (richiesta cliente): restano visibili SOLO
     - "Seleziona la Stampa" (renderizzata dal form padre managementPrintBirtForm)
     - Scheda            (managementPrintBirtForm_workEffortId.ftl, con lo scoping getPrintBirtScopeCtxBs)
     - Unità Responsabile (managementPrintBirtForm_orgUnitId_bs.ftl: sbiancata per Dir San/Amm)
     - Data al             (managementPrintBirtForm_monitoringDate.ftl)
   RIMOSSI rispetto al generico (workeffortAllPrintBirtExtraParameters.ftl): tipo obiettivo, obiettivo,
   codice tipo obiettivo, ruolo, soggetto, da, a, e TUTTI i 16 "Parametri Opzionali".

   NB: file risolto per convenzione da getReportParamPrintBirtList.groovy (<contentName>_param.ftl),
   contentName = SchedaConsuntivazioneObiettiviBS. Se manca, si torna al generico con tutti i parametri.
-->
<tr>
    <td colspan="1" style="width: 15%;"><hr></td>
    <td><hr></td>
</tr>
<#include "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_workEffortId.ftl" />
<#include "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_monitoringDate.ftl" />
<#include "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_orgUnitId_bs.ftl" />
