<script type="text/javascript">
    PrintBirtFormWrapper = {      
        submitPrint: function(form, query) {       		       	 	
           new Ajax.Request('<@ofbizUrl>validateManagementPrintBirt</@ofbizUrl>', {
                parameters : query,
                onSuccess: function(response) {
                    var data = {};
                    try {
                        data = response.responseText.evalJSON(true);
                    } catch(e) {}

                    if (data && data._ERROR_MESSAGE_LIST_ == undefined && data._ERROR_MESSAGE_ == undefined) {
                        //form.submit();
                        window.open('/gzoom/control/managementPrintBirt?' + query, '_blank');
                    } else {
                        modal_box_messages.onAjaxLoad(data, Prototype.K)
                    }

                }
            });
        }
    }
</script>
     <table id="table_WorkEffortTypeContentAndContentViewListForm" class="basic-table list-table padded-row-table hover-bar resizable draggable toggleable selectable customizable headerFixable" cellspacing="0">
        <thead>
            <tr class="header-row-2">
                <th>${uiLabelMap.Description}</th>               
                <th>${uiLabelMap.Print}</th>
                <th>${uiLabelMap.Export}</th>
            </tr>
        </thead>
        <tbody>
            <#assign index=0/>
            <#list listIt as content>
                <tr <#if index%2 != 0>class="alternate-row"</#if>>
                    <td> 
                    	<input type="hidden" name="entityName" id="entityName" value="WorkEffortTypeContentAndContentView"/>
                    	<input type="hidden" name="workEffortTypeId" id="workEffortTypeId" value="${content.workEffortTypeId?if_exists}"/>
                    	<input type="hidden" name="monitoringDate" id="monitoringDate" value="${parameters.monitoringDate?if_exists}"/>
                    	<input type="hidden" name="pdfContentId" id="pdfContentId" value="${content.pdfContentId?if_exists}"/>
                    	<input type="hidden" name="xlsContentId" id="xlsContentId" value="${content.xlsContentId?if_exists}"/>
                    	<input type="hidden" name="workEffortId" id="workEffortId" value="${parameters.workEffortId?if_exists}"/>                       
                        ${content.etch?if_exists}
                    </td>

                    <td style="width: 15%;">
                          <div class="contact-actions performance-actions">
                            <ul>  
                               <li class="print-birt"> 
                                <a href="#"  title="${uiLabelMap.Pdf}"  onclick="javascript:PrintBirtFormWrapper.submitPrint($('WorkEffortTypeContentAndContentViewListForm_Form'), 'repContextContentId=WE_PRINT&saveView=N&workEffortId=${parameters.workEffortId?if_exists}&reportContentId=${content.pdfContentId?if_exists}&monitoringDate=${parameters.monitoringDate?if_exists}');"></a>
                               </li>                                        
                            </ul>
                          </div>
                    </td>
                    <td style="width: 15%;">
                          <div class="contact-actions performance-actions">
                          	<ul>  
                          	   <#if content.xlsContentId?has_content>                         		
                               <li class="export-birt">                               
                          			<a href="#"  title="${uiLabelMap.Excel}" onclick="javascript:PrintBirtFormWrapper.submitPrint($('WorkEffortTypeContentAndContentViewListForm_Form'), 'repContextContentId=WE_PRINT&saveView=N&workEffortId=${parameters.workEffortId?if_exists}&reportContentId=${content.xlsContentId?if_exists}&monitoringDate=${parameters.monitoringDate?if_exists}');"></a>
                               </li>
                               </#if>                                         
                            </ul>
                          </div>
                    </td>
                </tr>
                <#assign index = index+1>
            </#list>
        </tbody>
     </table>
