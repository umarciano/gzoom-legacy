<form method="post"  action=""  class="basic-form cachable" name="WorkEffortAchieveInqMeasViewPerformanceDetailForm">
    <div >
        <div> 
            <table cellspacing="0" cellpadding="0" class="single-editable" >
                <tr>
                   <td class="label">&nbsp;</td>
                   <td class="widget-area-style">&nbsp;</td>
                </tr>
                <tr>
                    <td class="label field-group-title"><span class="field-group-title">${uiLabelMap.WorkEffortAchieveViewPerformance}</span></td>
                    <td class="field-group-title">&nbsp;</td>
                </tr>
                <tr>
                    <td class="label">&nbsp;</td>
                    <td class="widget-area-style">&nbsp;</td>
                </tr>
                <!-- sandro : bug 3937 #if formIndicator.kpiScoreWeight!=0 -->
                <#if "WEMT_PERF" == formIndicator.weMeasureTypeEnumId>
                <tr>
                    <td class="label">${uiLabelMap.WorkEffortAchieveViewPerformanceIncidence}</td>
                    <td class="widget-area-style">&nbsp;</td>
                </tr>
                <tr>
                    <td class="label">${uiLabelMap.WorkEffortAchieveViewIndicatorWeight} ${uiLabelMap.CommonEquals} ${formIndicator.kpiScoreWeight}</td>
                    <td class="widget-area-style">&nbsp;</td>
                </tr>
                <#else>
                <tr>
                    <td class="label">${uiLabelMap.WorkEffortAchieveViewPerformanceNotIncidence}</td>
                    <td class="widget-area-style">&nbsp;</td>
                </tr>
                </#if>
                <tr>
                    <td class="label">${uiLabelMap.WorkEffortAchieveViewIndicatorSign} <#if formIndicator.debitCreditDefault=="D">${uiLabelMap.WorkEffortAchieveViewIndicatorPositive}<#elseif formIndicator.debitCreditDefault=="C">${uiLabelMap.WorkEffortAchieveViewIndicatorNegative}</#if></td>
                    <td colspan="4" class="widget-area-style">&nbsp;</td>
                </tr>
                
                <tr>
                   <td class="label">&nbsp;</td>
                   <td class="widget-area-style">&nbsp;</td>
                </tr>
                <tr>
                   <td class="label field-group-title"><span class="field-group-title">${uiLabelMap.WorkEffortAchieveViewCalcParams}</span></td>
                   <td class="field-group-title">&nbsp;</td>
                </tr>
                <tr>
                   <td class="label">&nbsp;</td>
                   <td class="widget-area-style">&nbsp;</td>
                </tr>
                <tr>
                    <#assign labelTitle = "WorkEffortAchieveViewCalcParams_" + formIndicator.weScoreConvEnumId>
                    <#if formIndicator.debitCreditDefault=="C">
                        <#assign labelTitle = labelTitle + "_C">
                    </#if>
                    <td class="label">${uiLabelMap.get(labelTitle)}</td>
                    <td class="widget-area-style">&nbsp;</td>
                </tr>
                <#if formIndicator.uomRangeList?has_content>
                <tr>
                   <td class="label">&nbsp;</td>
                   <td class="widget-area-style">&nbsp;</td>
                </tr>
                <tr>
                    <td class="label field-group-title"><span class="field-group-title">${uiLabelMap.WorkEffortAchieveViewValueRule}</span></td>
                    <td class="field-group-title">&nbsp;</td>
                </tr>
                <tr>
                    <td class="label">&nbsp;</td>
                    <td class="widget-area-style">&nbsp;</td>
                </tr>
                <tr>
                    <table class="basic-table list-table padded-row-table" cellspacing="0" style="width: 50%">
                        <thead>
                            <tr class="header-row-2">
                                <th>${uiLabelMap.CommonFrom}</th>
                                <th>${uiLabelMap.CommonTo}</th>
                                <th>${uiLabelMap.WorkEffortAchieveViewScore}</th>
                            </tr>
                        </thead>
                        <tbody>
                            <#assign index = 0>
                            <#list formIndicator.uomRangeList as uomRange>
                                <tr<#if index%2 != 0> class="alternate-row"</#if>>
                                    <td style="text-align: right;">${uomRange.fromValue?string("#,##0.###")}</td>
                                    <td style="text-align: right;">${uomRange.thruValue?string("#,##0.###")}</td>
                                    <td style="text-align: right;">${uomRange.rangeValuesFactor?string("#,##0.###")}</td>
                                </tr>
                                <#assign index = index+1>
                            </#list>
                        </tbody>
                    </table>
                </tr>
                </#if>
            </table>
        </div>
    </div>
</form>