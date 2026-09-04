<#-- CUSTOMIZATION Cardarelli: riquadro riepilogo (lista UO potenzialmente molto lunga) reso scrollabile
     con intestazione "sticky". CSS condivisa da tutti i portali (org/individuale) perche' il FTL e' comune. -->
<style type="text/css">
.perfSummaryScrollBox { max-height: 360px; overflow: auto; }
/* il framework avvolge la tabella in .tableContainer (overflow:auto) a runtime: va reso "visible"
   altrimenti diventa lui il contenitore di scroll e l'header sticky non si aggancia al box. */
.perfSummaryScrollBox .tableContainer { overflow: visible; }
.perfSummaryScrollBox thead th { position: -webkit-sticky; position: sticky; top: 0; z-index: 5; background-color: rgb(216, 211, 224); }
/* riga "Totale" fissata subito sotto l'header (il valore di top e' impostato via JS = altezza header,
   perche' l'header puo' andare a 2 righe su viewport stretti). z-index < header. */
.perfSummaryScrollBox tr.perfSummaryTotaleRow > td { position: -webkit-sticky; position: sticky; z-index: 4; background-color: rgb(246, 240, 250); }
</style>
<div class="perfSummaryScrollBox">
<table id="table_WorkEffortPlanPerformanceSummaryManagementListForm_${parameters.weContextId_value}" class="basic-table list-table padded-row-table hover-bar resizable draggable toggleable selectable customizable headerFixable" cellspacing="0">
   <thead>
        <tr class="header-row-2">
            <th>${uiLabelMap.OrganizationUnit}</th>
            <#list statusItemList?if_exists as statusItem>
                <#if statusItem?has_content>
                <th>${statusItem.get("statusDescr")?if_exists}</th>
                </#if>
            </#list>
            <th>${uiLabelMap.CommonTotal}</th>
        </tr>
    </thead>
    <tbody>
        <tr class="perfSummaryTotaleRow">
        	<td>${uiLabelMap.CommonTotal}</td>
        	<#list statusItemList?if_exists as stItem>
        		<#assign keyStTotal = stItem.sequenceId?if_exists/>
        		<td class="center"><#if statusTotalsMap?has_content><#if statusTotalsMap.get(keyStTotal)?if_exists &gt; 0>${statusTotalsMap.get(keyStTotal)?if_exists}</#if></#if></td>
            </#list>
            <td class="center">${totGeneral?if_exists}</td>       
        </tr>    
        <#assign index=0/>
        <#list listIt?if_exists as workEffort>
        	<#assign orgUnitTotal=0/>
        	<#if orgUnitTotalsMap?has_content>
        		<#assign orgUnitTotal=orgUnitTotalsMap.get(workEffort.orgUnitId?if_exists)?if_exists/>
        	</#if>
        
        
            <tr <#if index%2 != 0>class="alternate-row"</#if>>
                <td>
                    <input type="hidden" name="orgUnitId" id="orgUnitId" value="${workEffort.orgUnitId?if_exists}"/>
                    <input type="hidden" name="entityName" id="entityName" value="WorkEffortRootInqySummaryView"/>
                    <input type="hidden" name="weContextId_value" id="weContextId_value" value="${parameters.weContextId_value}"/>

                    <#if showUoCode?if_exists == "MAIN">
                        <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                            <#assign worEffortItem = workEffort.parentRoleCode?if_exists + " - " + workEffort.partyNameLang?if_exists/>
                        <#else>
                            <#assign worEffortItem = workEffort.parentRoleCode?if_exists + " - " + workEffort.partyName?if_exists/>
                        </#if>
                    <#elseif showUoCode?if_exists == "EXT">
                        <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                            <#assign worEffortItem = workEffort.externalId?if_exists + " - " + workEffort.partyNameLang?if_exists/>
                        <#else>
                            <#assign worEffortItem = workEffort.externalId?if_exists + " - " + workEffort.partyName?if_exists/>
                        </#if>
                    <#else>
                        <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                            <#assign worEffortItem = workEffort.partyNameLang?if_exists/>
                        <#else>
                            <#assign worEffortItem = workEffort.partyName?if_exists/>
                        </#if>                                        
                    </#if>
                    
                    <div class="orgUnitColumn" title="${worEffortItem?if_exists}">
                    	${worEffortItem?if_exists}
                    </div>       
                </td>
                <#list statusItemList?if_exists as item>
                	<#assign keyTotal = workEffort.orgUnitId?if_exists + "_" + item.sequenceId?if_exists/>
                    <td class="center">
                        ${workEffort.get(keyTotal)?if_exists}
                    </td>
                </#list>
                <td class="center">${orgUnitTotal?if_exists}</td>          
            </tr>
            <#assign index = index+1>
        </#list>
    </tbody>
 </table>
</div>
<#-- Fissa la riga "Totale" appena sotto l'header sticky: top = altezza header (ricalcolata al resize,
     perche' le colonne-stato hanno etichette lunghe che vanno a 2 righe su viewport stretti). -->
<script type="text/javascript">
(function(){
  function pinPerfSummaryTotale(){
    try {
      var box = document.querySelector('.perfSummaryScrollBox');
      if (!box) return;
      var thead = box.querySelector('table thead');
      if (!thead) return;
      var h = thead.offsetHeight;
      var tds = box.querySelectorAll('tr.perfSummaryTotaleRow > td');
      for (var i = 0; i < tds.length; i++) { tds[i].style.top = h + 'px'; }
    } catch (e) {}
  }
  pinPerfSummaryTotale();
  setTimeout(pinPerfSummaryTotale, 300);
  if (window.addEventListener && !window.__perfSummaryTotalePinBound) {
    window.__perfSummaryTotalePinBound = true;
    window.addEventListener('resize', pinPerfSummaryTotale, false);
  }
})();
</script>
