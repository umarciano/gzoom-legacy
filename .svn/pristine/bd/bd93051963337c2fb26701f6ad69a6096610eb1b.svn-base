<#if importTransactionFromMeasureScreenLocation?has_content && importTransactionFromMeasureScreenName?has_content>
    ${screens.render(importTransactionFromMeasureScreenLocation, importTransactionFromMeasureScreenName, context)}
    </#if>
<script type="text/javascript">
    ImportTransaction = {
        submitForm : function(formId) {
            var form = $(formId);
            if (Object.isElement(form)) {
                var radio = form.down("input[checked='checked']");
                if (Object.isElement(radio)) {
                    if (radio.getValue() == "EXCEL") {
                        var uploadedFile = form.down("input[name='uploadedFile']");
                        if (!Object.isElement(uploadedFile) ||  uploadedFile.getValue() == "") {
                            var messages = new Messages();
                            messages._errorMessage = "${uiLabelMap.Import_file_excel_empty}";
                            messages.alert("${uiLabelMap.Import_file_excel_empty}", "");
                        }
                        else {
                            form.target = "_blank";
                            form.submit();
                        }
                        return;
                    }
                }
                ajaxSubmitFormUpdateAreas("ImportTransactionFromMeasureForm","common-container","",{
                    onSuccess: function(transport) {
                        var data = {};
                        try {
                            data = response.responseText.evalJSON(true);
                        } catch(e) {}

                        if (data && data._ERROR_MESSAGE_LIST_ == undefined && data._ERROR_MESSAGE_ == undefined) {
                            var mex = new Messages();
                            mex.alert("${uiLabelMap.Import_completed}", "", Prototype.K);
                        } else {
                            //data['messageContext'] = 'BaseValidateReportParameters'
                            modal_box_messages.onAjaxLoad(data, Prototype.K)
                        }
                        
                    }
                } );
            }
        },
        disableUploadField: function(disable) {
            var uploadFile = $$("input[type='file']")[0];
            if (Object.isElement(uploadFile)) {
                if(disable) {
                    uploadFile.writeAttribute("disabled");
                }
                else {
                    uploadFile.removeAttribute("disabled");
                }
            }
        }
    }
    
    var radios = $$("input[name='origin']");
    if(Object.isArray(radios)) {
        var excelRadio;
        radios.each(function(radio) {
            Event.observe(radio, "change", function(event) {
                var radio = Event.element(event);
                if(radio.getValue() == "EXCEL") {
                    ImportTransaction.disableUploadField(false);
                }
                else {
                    ImportTransaction.disableUploadField(true);
                }
            });
        });
    }
    
    var button = $$(".search-execute")[0];
    if(Object.isElement(button)) {
        button.observe("click", ImportTransaction.submitForm.curry('ImportTransactionFromMeasureForm'));
    }
</script>