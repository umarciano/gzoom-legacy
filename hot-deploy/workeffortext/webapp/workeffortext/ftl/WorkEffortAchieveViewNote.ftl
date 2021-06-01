<#if context.listIt?has_content>

    <#macro formatDate date=null>
        <#if date?is_date>
            ${date?string("dd/MM/yyyy")}
        </#if>
    </#macro>
    
     <table class="basic-table list-table padded-row-table hover-bar resizable draggable toggleable selectable customizable headerFixable dblclick-open-management" cellspacing="0">
        <thead>
            <tr class="header-row-2">
                <th><div>${uiLabelMap.FormFieldTitle_noteName}<#if workEffortTypeContentId != "DATE_NOT_LAY"> e ${uiLabelMap.FormFieldTitle_noteDateTime}</#if></div></th>
                <th><div>${uiLabelMap.FormFieldTitle_noteInfo}</div></th>
            </tr>
        </thead>
        <tbody>
            <#assign index=0/>
            
            <#list context.listIt as item>
                <#assign textAreaId="noteInfo_${index}"/>
                <script type="text/javascript">
                    <#include "component://base/webapp/resources/ftl/register-rich-textarea.js.ftl">
                </script>
                <tr <#if index%2 != 0>class="alternate-row"</#if>>
                    <td style="width: 25%">
                        <span class="label" style="padding-left: 0px !important;">
                            ${item.noteName?if_exists}<#if workEffortTypeContentId != "DATE_NOT_LAY"> <@formatDate date=item.noteDateTime/> </#if>
                        </span>
                    </td>
                    <td class="noteInfo">
                        <span id="noteInfo_${index}">
                            ${item.noteInfo?if_exists}
                        </span>
                    </td>                   
                </tr>
                <#assign index = index+1>
            </#list>
        </tbody>
     </table>
    </#if>