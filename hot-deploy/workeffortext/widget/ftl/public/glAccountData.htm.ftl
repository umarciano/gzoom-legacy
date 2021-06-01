<#include "component://workeffortext/widget/ftl/public/main-decorator.htm.ftl">
<#escape x as x?xhtml>
<#-- -->
<#function formatValue value type="">
    <#if type == "RATING_SCALE">
        <#return value?default("")?string>
    <#elseif type = "DATE_MEASURE">
        <#return Static["com.mapsengineering.base.birt.util.UtilDateTime"].numberConvertToDate(value, locale)?default("")>
    </#if>
    <#return Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(value.doubleValue(), "#,##0.########", locale)>
</#function>
<#-- -->
<#assign body>
    <#assign uiLabelMap = loadPropertyMap(uiLabelMap, "WorkeffortExtUiLabels")>
    <#assign list = request.getAttribute("list")?if_exists>
    <#if list?has_content>
        <table>
            <thead>
                <tr>
                    <th>${uiLabelMap.FormFieldTitle_indicatorGlAccountId}</th>
                    <th>${uiLabelMap.WorkeffortTargetBalance}</th>
                    <th>${uiLabelMap.WorkeffortActualBalance}</th>
                    <th>${uiLabelMap.WorkEffortAchieveViewPerformance}</th>
                </tr>
            </thead>
            <tbody>
            <#list list as item>
                <#assign indicatorHistoryLink = "indicatorHistory?id=${item.id?if_exists}&wid=${parameters.id?if_exists}&d=${parameters.d?if_exists}&t=${parameters.t?if_exists}">
                <tr>
                    <td>
                        <a href="<@ofbizUrl>${indicatorHistoryLink}</@ofbizUrl>"
                            title="${item.name?if_exists}"
                            class="gzoom-indicatorHistoryLink"
                        >
                            ${item.name?if_exists}
                        </a>
                    </td>
                    <td class="gzoom-indicatorTarget">
                        <span>${formatValue(item.targetValue, item.uomType)}</span>
                    </td>
                    <td class="gzoom-indicatorActual">
                        <span>${formatValue(item.actualValue, item.uomType)}</span>
                    </td>
                    <td class="gzoom-indicatorIcon">
                        <a href="<@ofbizUrl>${indicatorHistoryLink}</@ofbizUrl>"
                            class="gzoom-indicatorHistoryLink"
                        >
                            <img src="${item.imageUrl?if_exists}">
                        </a>
                    </td>
                </tr>
                <tr class="gzoom-indicatorHistoryDetailParent" style="display: none;">
                    <td colspan="4">
                        <div class="gzoom-indicatorHistoryDetail" />
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
        <script type="text/javascript">
            observeAll('a.gzoom-indicatorHistoryLink', 'click', function(event) {
                var containerParent = this.up('tr').next('.gzoom-indicatorHistoryDetailParent');
                var container = containerParent.down('.gzoom-indicatorHistoryDetail');
                var wasVisible = containerParent.visible();
                hideAll('.gzoom-indicatorHistoryDetailParent');
                clearAll('.gzoom-indicatorHistoryDetail');
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
