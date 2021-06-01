<#include "component://base/widget/ftl/htm/main-decorator.htm.ftl">
<#-- -->
<#macro workEffortExtMainDecorator body>
    <#if "Y" == (parameters.ajax)?if_exists>
        <div class="gzoom-ajax-decorator-div">
            ${body?if_exists}
        </div>
    <#else>
        <#assign wrappedBody>
            <div class="gzoom-main-decorator-div">
                ${body?if_exists}
            </div>
        </#assign>
        <@addStylesheet "/workeffortpub/images/workeffortpub.css"/>
        <@addJavascript "/images/prototypejs/prototype.js"/>
        <@addJavascript "/workeffortpub/images/workeffortpub.js"/>
        <@baseMainDecorator wrappedBody />
    </#if>
</#macro>
