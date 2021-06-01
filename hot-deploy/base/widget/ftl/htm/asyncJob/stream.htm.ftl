<#include "component://base/widget/ftl/htm/asyncJob/main-decorator.htm.ftl">
<#escape x as x?xhtml>
<#assign body>
    <#assign uiLabelMap = loadPropertyMap(uiLabelMap, "BaseUiLabels")>
    <#assign downloadUrl = "stream?contentId=" + (parameters.contentId)?if_exists>

    <div class="message-panel">
        <a href="${downloadUrl}&externalLoginKey=${parameters.externalLoginKey}" target="_self">${uiLabelMap.BaseDownload}</a>
    </div>

    <script type="text/javascript">
        window.open('${downloadUrl}&externalLoginKey=${parameters.externalLoginKey}', target='_self');
    </script>

</#assign>
<@baseAsyncJobMainDecorator body />
</#escape>
