<#if exportTransactionFromMeasureScreenLocation?has_content && exportTransactionFromMeasureScreenName?has_content>
    ${screens.render(exportTransactionFromMeasureScreenLocation, exportTransactionFromMeasureScreenName, context)}
    </#if>
    
    
<script type="text/javascript">
    ExportTransaction = {
        submitForm : function(formId) {
            var form = $(formId);
            
            if (Object.isElement(form)) {
                var radio = form.down("input[checked='checked']");
                if (Object.isElement(radio)) {
                    if (radio.getValue() == "EXCEL") {
                    
                        //controllo sui campi
                        if(ExportTransaction.fieldCheck(form)) {
                            form.submit();
                        }
                        return;
                    }
                }
                ajaxSubmitFormUpdateAreas("ExportTransactionFromMeasureForm","common-container","",{
                    onSuccess: function(transport) {
                        var data = {};
                        try {
                            data = transport.responseText.evalJSON(true);
                        } catch(e) {}

                        if (data && data._ERROR_MESSAGE_LIST_ == undefined && data._ERROR_MESSAGE_ == undefined) {
                          var mex = new Messages();
                          mex.alert("${uiLabelMap.Export_completed}", "", Prototype.K);
                        } else {
                            modal_box_messages.onAjaxLoad(data, Prototype.K)
                        }
                    }
                } );
            }
        },
        
        fieldCheck : function(form) {
            if (Object.isElement(form)) {
                var refDate = form.down("[name='refDate']");
                var workEffortPurposeTypeId = form.down("[name='workEffortPurposeTypeId']");
                if (Object.isElement(refDate) && Object.isElement(workEffortPurposeTypeId)) {
                    //Check campi vuoti
                    if (refDate.getValue() == null || refDate.getValue() == "") {
                        var mex = new Messages();
                        mex._errorMessage = "ERROR";
                        mex.alert("Campo obbligatorio mancante: Data Riferimento");
                        return false;
                    }
                    if (workEffortPurposeTypeId.getValue() == null || workEffortPurposeTypeId.getValue() == "") {
                        var mex = new Messages();
                        mex._errorMessage = "ERROR";
                        mex.alert("Campo obbligatorio mancante: Finalità");
                        return false;
                    }
                } 
            }
            
            return true;
        }
    }
    
    var button = $$(".search-execute")[0];
    if(Object.isElement(button)) {
        button.observe("click", ExportTransaction.submitForm.curry('ExportTransactionFromMeasureForm'));
    }
</script>