<!-- 
variabili
 mandatory = "mandatori" per definire il tipo mandatori
 workEffortIdAll = Y/N, per estrarre tutti i workEffort senza considerare il filtro parentTypeId

-->


<tr>
       
   <#assign mandatory=mandatory?default("")/>
   <#assign workEffortIdAll=workEffortIdAll?default("N")/>
   
   <td class="label">${uiLabelMap.WorkEffortMeasure_target}</td>
    <!--snapshot -->
   <#if parameters.snapshot?if_exists?default("N") == 'Y'>	
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="${parameters.snapshot}"/> 	
   </#if>
   <td class="widget-area-style">
   <div id="select-report-work-effort-print-row">
	   <#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_reloadWorkEffortId_20D6.ftl" />
   </div>
   </td>
</tr>

