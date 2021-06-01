<#include "component://base/widget/ftl/htm/asyncJob/main-decorator.htm.ftl">
<#escape x as x?xhtml>
<#assign body>
    <#assign uiLabelMap = loadPropertyMap(uiLabelMap, "BaseUiLabels")>
    <#assign applicationTitle = (parameters.jobId)?if_exists>
    <#assign job = (Static["com.mapsengineering.base.services.async.AsyncJobManager"].get(parameters.jobId))?if_exists>
    <#assign result = (job.progress)?if_exists>

    <div class="message-panel">
        <#if result?has_content>
        <table border="1">
            <thead>
                <tr>
                    <th>${uiLabelMap.FormFieldTitle_attrName}</th>
                    <th>${uiLabelMap.FormFieldTitle_attrValue}</th>
                </tr>
            </thead>
            <tbody>
            <#list result.entrySet() as resultEntry>
                <tr>
                    <td style="font-weight: bold;">
                        ${resultEntry.key}
                    </td>
                    <td>
                        <#assign resultValue = resultEntry.value?if_exists>
                        <#if resultValue?has_content>${resultValue}<#else>&nbsp;</#if>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
        </#if>
    </div>

</#assign>
<@baseAsyncJobMainDecorator body />
</#escape>
