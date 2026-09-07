<#--
    Risposta JSON del comando massivo avviaConsuntivazioneFinaleBs.
    Ritorna advancedCount / skippedCount ricavati dai context result-name del servizio.
-->
<#assign adv = (context.advancedCount)!0/>
<#assign skp = (context.skippedCount)!0/>
{"advancedCount": ${adv?c}, "skippedCount": ${skp?c}}
