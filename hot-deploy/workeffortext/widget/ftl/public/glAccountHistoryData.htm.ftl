<#include "component://workeffortext/widget/ftl/public/main-decorator.htm.ftl">
<#escape x as x?xhtml>
<#assign body>
    <#assign data = request.getAttribute("data")?if_exists>
    <div class="gzoom-indicatorHistoryGraph">
        <img src="${(data.imageUrl)?if_exists}"/>
    <div>
</#assign>
<@workEffortExtMainDecorator body />
</#escape>
