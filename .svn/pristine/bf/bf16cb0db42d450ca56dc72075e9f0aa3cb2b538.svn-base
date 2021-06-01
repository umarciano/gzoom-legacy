<#if listIt?has_content>	
    <#assign index = 0>
    <#list listIt as print>   
        <#assign dataResource = delegator.findOne("DataResource", Static["org.ofbiz.base.util.UtilMisc"].toMap("dataResourceId", print.dataResourceId), true)>
        <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y' && (print.etchLang?has_content || print.descriptionLang?has_content)>
        	<p><input type="radio" class="print-radio" serviceName="${print.serviceName?if_exists}" name="reportContentId" value="${print.contentId}" onClick="radioOnChange.load(this.getValue())" <#if index=0> checked=true </#if>/><span class="readonly">${print.etchLang?default(print.descriptionLang)}</span> </p>
        <#else>
        	<p><input type="radio" class="print-radio" serviceName="${print.serviceName?if_exists}" name="reportContentId" value="${print.contentId}" onClick="radioOnChange.load(this.getValue())" <#if index=0> checked=true </#if>/><span class="readonly">${print.etch?default(print.description)}</span> </p>
        </#if>
        <#assign index = index+1>  		
    </#list>    
</#if>


<#-- Inserimento del javascript per l'evento change del radio -->
<script type="text/javascript">
	radioOnChange = {
		load: function(value) {
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
                	saveView : 'N'
            	}, {
            		onSuccess : function(response, responseText) {
                		$('select-report-param-print-row').show();
        			}
            	}
        	);
		}
	}
	//prendo l'elemento selezionato		
	var display = $('select-print-row').getStyle('display');	
	if(display != "none") {
		var list = $("select-print-row").select('input[checked="true"]');
		if(list !== undefined && list.length > 0){	
			radioOnChange.load(list[0].getValue());
		} else {
		   //se la lista è vuota non faccio visualizzare la riga della lista report
		   $('select-print-row').hide();
		}
	}
	
</script>