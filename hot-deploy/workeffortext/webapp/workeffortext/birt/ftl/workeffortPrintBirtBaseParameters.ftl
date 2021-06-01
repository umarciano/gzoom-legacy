<tr id="select-print-row">
 <#if listReport?has_content>	
	<td  class="label" style="width: 18%;">	${uiLabelMap.BaseSelectPrint}</td>
	<td class="widget-area-style" id="select-print-cell" >	
	    <#assign index = 0>
	    <#list listReport as print>  	    	
	        <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y' && (print.etchLang?has_content || print.descriptionLang?has_content)>
	        	<p><input type="radio" class="print-radio" useFilter="${print.useFilter?if_exists}" serviceName="${print.serviceName?if_exists}"  workEffortAnalysisId="${print.workEffortAnalysisId?if_exists}" seq="${print.sequenceNum?if_exists}" etch="${print.etchLang?default(print.descriptionLang)}" name="reportContentId" value="${print.contentId}" onClick="WorkEffortPrintBirtExtraParameter.radioOnChange(this)" <#if index=0> checked=true </#if>/><span class="readonly">${print.etchLang?default(print.descriptionLang)}</span> </p>
	        <#else>
	        	<p><input type="radio" class="print-radio" useFilter="${print.useFilter?if_exists}" serviceName="${print.serviceName?if_exists}"  workEffortAnalysisId="${print.workEffortAnalysisId?if_exists}" seq="${print.sequenceNum?if_exists}" etch="${print.etch?default(print.description)}" name="reportContentId" value="${print.contentId}" onClick="WorkEffortPrintBirtExtraParameter.radioOnChange(this)" <#if index=0> checked=true </#if>/><span class="readonly">${print.etch?default(print.description)}</span> </p>
	        </#if>	
	        <#assign index = index+1>  		
	    </#list>
	</td>
<#else>
	<td class="label" style="width: 18%;" > ${uiLabelMap.NoReportFound}</td>	
</#if>
</tr>

<script type="text/javascript">
    WorkEffortPrintBirtExtraParameter = {        
        radioOnChange: function(element) {
        	value = element.getValue();
        	//console.log(value);
			$('button-ok').show();
            $('button-ok-disabled').hide(); 
            
            //visualizzazione bottone email
            var buttonMail = $('button-mail');
            if (value && value.toUpperCase() && value.toUpperCase().startsWith('REMINDER')) {
            	if (Object.isElement(buttonMail)) {
            		buttonMail.show();
            	}
            } else {
            	if (Object.isElement(buttonMail)) {
            		buttonMail.hide();
            	}
            }
			//devo caricare la lista di stampa da visualizzare           
            ajaxUpdateArea('select-report-param-print-row', '<@ofbizUrl>loadReportParamPrintBirtList</@ofbizUrl>', 
            	{
            		contentId : value,
            		popup : '${context.popup?if_exists}', 
                	monitoringDate : '${parameters.monitoringDate?if_exists}'.unescapeHTML(), 
                	workEffortTypeId : '${parameters.workEffortTypeId?if_exists}', 
                	parentTypeId: '${parameters.parentTypeId?if_exists}',
                	snapshot : '${parameters.snapshot?if_exists}',
                	uomRangeId : '${parameters.uomRangeId?if_exists}',
                	etch: element.readAttribute('etch'),
                	sequenceNum: element.readAttribute('seq'),
                	workEffortAnalysisId: element.readAttribute('workEffortAnalysisId'),
                	useFilter: element.readAttribute('useFilter'),
                	saveView : 'N'
            	}, {
            		onSuccess : function(response, responseText) {
                		$('select-report-param-print-row').show();
        			}
            	}
        	);
		},
	    load: function() {
	    	$('button-ok').hide();
	        $('button-ok-disabled').show();
	        var selectPrintRow = $("select-print-row");
			if (selectPrintRow != null) {
				var list = selectPrintRow.select('input[checked="true"]');
				if(list !== undefined && list.length > 0) {	
					WorkEffortPrintBirtExtraParameter.radioOnChange(list[0]);
				} 	
			}
	    }
    }
    
    document.observe("dom:loaded", jQuery(WorkEffortPrintBirtExtraParameter.load));   
</script>