<!-- 
variabili
 mandatory = "mandatori" per definire il tipo mandatori
 workEffortIdAll = Y/N, per estrarre tutti i workEffort senza considerare il filtro parentTypeId

-->


<tr>
       
   <#assign mandatory=mandatory?default("")/>
   <#assign workEffortIdAll=workEffortIdAll?default("N")/>
   
   <td class="label">${uiLabelMap.WorkeffortRoot}</td>
    <!--snapshot -->
   <#if parameters.snapshot?if_exists?default("N") == 'Y'>	
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="${parameters.snapshot}"/> 	
   </#if>
   <td class="widget-area-style">
   <div id="select-report-work-effort-print-row">
	   <#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_reloadWorkEffortId.ftl" />
   </div>
   </td>
</tr>

<script>
    WorkEffortTypeIdWorkEffortIdExtPrintBirtExtraParameter = {
    	load: function() {
    	    var form = $('${printBirtFormId?default("ManagementPrintBirtForm")}');
    	    var checkedTypeEkement = form.select('input[name=workEffortTypeId]').find(function(element) {
                return element.checked;
            });
            
            if (checkedTypeEkement) {
            	ajaxUpdateArea('select-report-work-effort-print-row', '<@ofbizUrl>loadReportWorkEffortPrintBirtList</@ofbizUrl>', 
            		{
                		workEffortTypeId : checkedTypeEkement.getValue(), 
                		mandatory : '${mandatory?if_exists}',
                		workEffortIdAll :  '${workEffortIdAll?if_exists}',
                		parentTypeId: '${parameters.parentTypeId?if_exists}',
                		snapshot : '${parameters.snapshot?if_exists}',
                		useFilter : '${parameters.useFilter?if_exists}',
                		saveView : 'N'
            		}
        		); 
        	}          
    	},
    
        changeRadioButtons : function(value) {   
        	//devo caricare la lista di stampa da visualizzare
            ajaxUpdateArea('select-report-work-effort-print-row', '<@ofbizUrl>loadReportWorkEffortPrintBirtList</@ofbizUrl>', 
            	{
                	workEffortTypeId : value, 
                	mandatory : '${mandatory?if_exists}',
                	workEffortIdAll :  '${workEffortIdAll?if_exists}',
                	parentTypeId: '${parameters.parentTypeId?if_exists}',
                	snapshot : '${parameters.snapshot?if_exists}',
                	useFilter : '${parameters.useFilter?if_exists}',
                	saveView : 'N'
            	}
        	);           
        }
    }
    
    //WorkEffortTypeIdWorkEffortIdExtPrintBirtExtraParameter.load();
    document.observe("dom:loaded", jQuery(WorkEffortTypeIdWorkEffortIdExtPrintBirtExtraParameter.load));   
</script>


