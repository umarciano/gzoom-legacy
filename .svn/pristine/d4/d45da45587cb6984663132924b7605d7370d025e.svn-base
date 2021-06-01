<#include "component://workeffortext/widget/ftl/public/main-decorator.htm.ftl">
<#escape x as x?xhtml>
<#assign body>
    <#assign uiLabelMap = loadPropertyMap(uiLabelMap, "WorkeffortExtUiLabels")>
    <#assign list = request.getAttribute("list")?if_exists>
    <#if list?has_content>
        <table>
            <thead>
                <tr>
                    <th colspan="2">${uiLabelMap.FormFieldTitle_searchDate} ${formatDate(request.getAttribute("periodDate"))}</th>
                </tr>
                <tr>
                    <th>${uiLabelMap.WorkeffortService}</th>
                    <th>${uiLabelMap.WorkEffortAchieveViewPerformance}</th>
                </tr>
            </thead>
            <tbody>
            <#list list as item>
                <#assign indicatorLink = "indicator?id=${item.id?if_exists}&d=${parameters.d?if_exists}&t=${parameters.t?if_exists}">
                <tr>
                    <td>
                        <a href="<@ofbizUrl>${indicatorLink}</@ofbizUrl>"
                            title="${item.description?if_exists}"
                            class="gzoom-indicatorLink"
                        >
                            ${item.name?if_exists}
                        </a>
                    </td>
                    <td class="gzoom-workEffortGauge">
                        <a href="<@ofbizUrl>${indicatorLink}</@ofbizUrl>"
                            class="gzoom-indicatorLink"
                        >
                            <img src="${item.imageUrl?if_exists}">
                        </a>
                    </td>
                </tr>
                <tr class="gzoom-indicatorDetailParent" style="display: none;">
                    <td colspan="2">
                        <div class="gzoom-indicatorDetail" />
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
        <script type="text/javascript">
            observeAll('a.gzoom-indicatorLink', 'click', function(event) {
                var containerParent = this.up('tr').next('.gzoom-indicatorDetailParent');
                var container = containerParent.down('.gzoom-indicatorDetail');
                var wasVisible = containerParent.visible();
                hideAll('.gzoom-indicatorDetailParent');
                clearAll('.gzoom-indicatorDetail');
                if (!wasVisible) {
                    containerParent.show();
                    ajaxUpdate(container, this.getAttribute('href'));
                }
                event.stop();
            });
        </script>
    </#if>
</#assign>
<@workEffortExtMainDecorator body />
</#escape>
