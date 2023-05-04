<#if listIt?has_content>
	<#if popup?default("N") == 'Y'>
		<div name="reportContainer" id="reportContainerBirt">  <#-- div che conterr� le varie form caricate per ogni singolo report  --> 		
	    <#assign index = 0>
	    <#list listIt as print>   
	  		<table id="${print.contentId}" style="display:none">
		  		<tbody>
		  	    <#assign listResourceExtra = delegator.findByAnd("ContentAssocDataResourceViewTo", Static["org.ofbiz.base.util.UtilMisc"].toMap("caContentAssocTypeId", "BIRT_EXTRAFIELD_OF", "contentIdStart", print.contentId))/>    		
		   		<#if listResourceExtra?has_content>
		   			<#assign resourceExtra=listResourceExtra[0]/>
		   			<#assign extraParamSplit= Static["org.ofbiz.base.util.StringUtil"].split(resourceExtra.drObjectInfo, "#")/>  
				    ${screens.render(extraParamSplit[0], extraParamSplit[1], context)}
		   		</#if>
		   		</tbody> 	
	   		</table>
	   		<#assign index = index+1>  
	    </#list>
		</div>
	</#if>
	<br/><br/>
    <#assign index = 0>
    <#list listIt as print>   
        <#assign dataResource = delegator.findOne("DataResource", Static["org.ofbiz.base.util.UtilMisc"].toMap("dataResourceId", print.dataResourceId), true)>
        <p><input type="radio" class="print-radio" name="reportContentId" value="${print.contentId}" onClick="RadioOnChange.change(this.getValue())" <#if index=0> checked=true </#if>/><span class="readonly">${print.etch?default(print.description)}</span> </p>
        <#assign index = index+1>  		
    </#list>    
</#if>


<#-- Inserimento del javascript per l'evento change del radio -->
<script type="text/javascript">
	RadioOnChange = Class.create({ });
	
	RadioOnChange.load = function() {
        //prendo l'elemento selezionato		
		var display = $('select-print-row').getStyle('display');
		if(display != "none"){
			var list = $("select-print-row").select('input[checked="true"]');
			
			if(list != undefined){	
				RadioOnChange.change(list[0].getValue());
			}
		} 
    }
    
	RadioOnChange.change = function(value) {
	    if($("reportContainerBirt")){
			var list = $("reportContainerBirt").childElements();
			if (Object.isArray(list) && list.size()>0){
				list.each(function(ele){
					if(value == ele.identify()){
						ele.show();
						ele.select("input#").each(function(tbEle){
							tbEle.addClassName('print-parameters');
						});
					}else{
						ele.hide();
						ele.select("input#").each(function(tbEle){
							tbEle.removeClassName('print-parameters');
						});						
					}
				});
			}
		}            
		//devo caricare la lista di stampa da visualizzare           
        ajaxUpdateArea('select-type-print-cell', '<@ofbizUrl>loadTypePrintBirtList</@ofbizUrl>', $H({'contentId' : value, 'saveView' : 'N'}), {'onSuccess' : function(response, responseText) {
            if (response.responseText && response.responseText.indexOf('<p>') != -1) {
                $('select-type-print-row').show();
            } else {
                $('select-type-print-row').hide();
            }
        }});
        
         ajaxUpdateArea('select-addparams-print-cell', '<@ofbizUrl>loadParamsPrintBirtList</@ofbizUrl>', $H({'contentId' : value, 'saveView' : 'N'}), {'onSuccess' : function(response, responseText) {
         	if (response.responseText && response.responseText.indexOf('<p>') != -1) {
                $('select-addparams-print-row').show();
            } else {
                $('select-addparams-print-row').hide();
            }          
         }});
         
         if($('button-ok')) {
         	$('button-ok').show();
         }
         if ($('button-ok-disabled')) {
         	$('button-ok-disabled').hide();
		}
	}
	
	RadioOnChange.load();
</script>