<#assign divIndex = 1/>
<#list parameters.workEffortTypeHierarchyViewList as item>
    <#if item.weFromName1?has_content || item.weFromName2?has_content || item.weFromName3?has_content
    || item.weFromName4?has_content || item.weFromName5?has_content || item.weFromName6?has_content || item.weFromName7?has_content>
        <br>
        <div class="screenlet-lookup">
            <div id="screenlet-WorkEffortTypeHierarchyView-${divIndex}" class="screenlet">
                <div class="screenlet-title-bar" id="screenlet-title-bar_${divIndex}">
                    <ul>
                    <li class="h3"></li>
                    </ul>
                    <br class="clear">
                </div>
            
                <div class="fieldgroup-body" id="_${divIndex}__body"> 
                    <table cellspacing="0" cellpadding="0" class="single-editable" id="table_${divIndex}">
                      <tbody>
                        <#if item.weFromName7?has_content>
                            <tr>
                                <td class="label">${item.weFromTypeEtch7?if_exists}</td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromEtch7" size="20" value="${item.weFromEtch7?if_exists}" readonly="readonly" style="width: 90%"></td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromName7" size="50" value="${item.weFromName7?if_exists}" readonly="readonly" style="width: 90%"></td>
                            </tr>
                        </#if>
                        <#if item.weFromName6?has_content>
                            <tr>
                                <td class="label">${item.weFromTypeEtch6?if_exists}</td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromEtch6" size="20" value="${item.weFromEtch6?if_exists}" readonly="readonly" style="width: 90%"></td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromName6" size="30" value="${item.weFromName6?if_exists}" readonly="readonly" style="width: 90%"></td>
                            </tr>
                        </#if>
                        <#if item.weFromName5?has_content>
                            <tr>
                                <td class="label">${item.weFromTypeEtch5?if_exists}</td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromEtch5" size="20" value="${item.weFromEtch5?if_exists}" readonly="readonly" style="width: 90%"></td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromName5" size="30" value="${item.weFromName5?if_exists}" readonly="readonly" style="width: 90%"></td>
                            </tr>
                        </#if>
                        <#if item.weFromName4?has_content>
                            <tr>
                                <td class="label">${item.weFromTypeEtch4?if_exists}</td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromEtch4" size="20" value="${item.weFromEtch4?if_exists}" readonly="readonly" style="width: 90%"></td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromName4" size="30" value="${item.weFromName4?if_exists}" readonly="readonly" style="width: 90%"></td>
                            </tr>
                        </#if>
                        <#if item.weFromName3?has_content>
                            <tr>
                                <td class="label">${item.weFromTypeEtch3?if_exists}</td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromEtch3" size="20" value="${item.weFromEtch3?if_exists}" readonly="readonly" style="width: 90%"></td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromName3" size="30" value="${item.weFromName3?if_exists}" readonly="readonly" style="width: 90%"></td>
                            </tr>
                        </#if>
                        <#if item.weFromName2?has_content>
                            <tr>
                                <td class="label">${item.weFromTypeEtch2?if_exists}</td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromEtch2" size="20" value="${item.weFromEtch2?if_exists}" readonly="readonly" style="width: 90%"></td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromName2" size="30" value="${item.weFromName2?if_exists}" readonly="readonly" style="width: 90%"></td>
                            </tr>
                        </#if>
                        <#if item.weFromName1?has_content>
                            <tr>
                                <td class="label">${item.weFromTypeEtch1?if_exists}</td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromEtch1" size="20" value="${item.weFromEtch1?if_exists}" readonly="readonly" style="width: 90%"></td>
                                <td><input type="text" id="WorkEffortTypeHierarchyView_weFromName1" size="30" value="${item.weFromName1?if_exists}" readonly="readonly" style="width: 90%"></td>
                            </tr>
                        </#if>
                      </tbody>
                    </table>
                </div>
            </div>
        </div>
        <br>
        <br>
        <#assign divIndex = divIndex + 1/>
    </#if>
</#list>