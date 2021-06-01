<#include "component://base/widget/ftl/htm/asyncJob/main-decorator.htm.ftl">
<#escape x as x?xhtml>
<#assign body>
    <#assign uiLabelMap = loadPropertyMap(uiLabelMap, "BaseUiLabels")>
    <#assign applicationTitle = (parameters.job.jobId)?if_exists>

    <div class="message-panel">
        <p>
            <img src="../../theme_gplus/js/protoload/img/waiting.gif">${(parameters.waitTitleMessage)?if_exists}</img>
        </p>
        <p>${uiLabelMap.BaseAsyncJobInterruptWarning}</p>
        <#if (parameters.progress.progressMessage)?has_content>
        <p>
            ${parameters.progress.progressMessage?replace("\n", "<br/>")}
        </p>
        </#if>
    </div>

    <script type="text/javascript">
        stopAsyncJobEvent = function (e) {
            if (!window._my_reload_page) {
                new Ajax.Request('stopAsyncJob', {
                    parameters: {
                        jobId: '${(parameters.job.jobId)?if_exists}'
                    }
                });
            }
        };
        window.onunload = stopAsyncJobEvent;
        setTimeout(function() {
            window._my_reload_page = true;
            document.location.reload(true);
        }, ${(parameters.job.nextClientWait)?default("5000")});
    </script>

</#assign>
<@baseAsyncJobMainDecorator body />
</#escape>
