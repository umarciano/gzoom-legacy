<#include "component://base/widget/ftl/htm/main-decorator.htm.ftl">
<#macro baseAsyncJobMainDecorator body>
    <#assign wrappedBody>
        <div class="gzoom-main-decorator-div">
            ${body?if_exists}
        </div>
    </#assign>
    <@addMetaTag "<meta http-equiv=\"Cache-Control\" content=\"no-cache, no-store, must-revalidate\" />"/>
    <@addMetaTag "<meta http-equiv=\"Pragma\" content=\"no-cache\" />"/>
    <@addMetaTag "<meta http-equiv=\"Expires\" content=\"0\" />"/>
    <@addStylesheet "/resources/css/asyncJob.css"/>
    <@addJavascript "/images/prototypejs/prototype.js"/>
    <@addJavascript "/resources/js/asyncJob.js"/>
    <@baseMainDecorator wrappedBody />
</#macro>
