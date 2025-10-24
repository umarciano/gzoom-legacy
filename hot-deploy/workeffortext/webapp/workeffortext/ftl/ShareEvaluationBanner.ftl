<#-- Banner per la condivisione della valutazione al valutato -->
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - TEMPLATE RENDERING STARTED", "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - hasShareEvaluationPermission: " + (hasShareEvaluationPermission!false)?string, "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - parameters.contentId: " + (parameters.contentId!"NULL"), "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - context.contentId: " + (contentId!"NULL"), "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - context.folderIndex: " + (folderIndex!"NULL"), "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - context.folderContentIds: " + (folderContentIds!"NULL")?string, "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - workEffortId: " + (workEffortId!"NULL"), "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - currentStatusId: " + (currentStatusId!"NULL"), "")>
<#-- Questo banner viene visualizzato solo nel tab Indicatori (WEFLD_IND) -->
<#-- e solo se l'utente ha i permessi necessari (EMPLVALUTATORE_VIEW + WEM_EVAL_MANAGER) -->

<#-- Calcola il contentId corretto dal folderIndex -->
<#assign currentContentId = "">
<#if folderContentIds?? && folderIndex??>
    <#assign currentContentId = folderContentIds[folderIndex?number]!"">
</#if>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - currentContentId calculated: " + currentContentId, "")>

<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - Checking conditions...", "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - Condition 1 - hasShareEvaluationPermission exists: " + hasShareEvaluationPermission???string, "")>
<#if hasShareEvaluationPermission??>
    <#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - Condition 2 - hasShareEvaluationPermission value: " + hasShareEvaluationPermission?string, "")>
</#if>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - Condition 3 - currentContentId check: " + currentContentId, "")>

<#if hasShareEvaluationPermission?? && hasShareEvaluationPermission == true && currentContentId == "WEFLD_IND">
    <#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - ALL CONDITIONS MET - Rendering banner", "")>
    
    <#if currentStatusId?? && currentStatusId != "WEEVALST_EXECSHARED">
        <!-- Banner informativo con pulsante per condividere la valutazione - Stile uguale alla Legenda Valutazione -->
        <div class="share-evaluation-banner" style="background-color: #f5f5f5; border: 1px solid #ddd; padding: 12px; margin-bottom: 15px; border-radius: 4px;">
            <div style="display: flex; align-items: center; justify-content: space-between;">
                <div style="flex: 1;">
                    <div style="font-weight: bold; margin-bottom: 8px; color: #333; font-size: 14px;">
                        <i class="fa fa-share-alt" style="margin-right: 8px;"></i>${uiLabelMap.ShareEvaluationTitle}
                    </div>
                    <div style="line-height: 1.8; color: #555; font-size: 13px;">
                        <i class="fa fa-info-circle" style="margin-right: 5px;"></i>${uiLabelMap.ShareEvaluationWarning}
                    </div>
                </div>
                <div style="margin-left: 20px;">
                    <button onclick="if(confirm('${uiLabelMap.ShareEvaluationConfirm}')) { window.location.href='shareEvaluationToEvaluated?workEffortId=${workEffortId}'; }" 
                            class="buttontext" 
                            style="background-color: #1e90ff; color: #ffffff; border: none; padding: 10px 20px; font-size: 13px; font-weight: normal; cursor: pointer; border-radius: 3px; transition: all 0.2s ease;"
                            onmouseover="this.style.backgroundColor='#1c7ed6';"
                            onmouseout="this.style.backgroundColor='#1e90ff';">
                        <i class="fa fa-share" style="margin-right: 6px;"></i>${uiLabelMap.ShareEvaluationButton}
                    </button>
                </div>
            </div>
        </div>
    <#else>
        <!-- Valutazione già condivisa - Nessun banner da visualizzare -->
        <#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - Evaluation already shared (WEEVALST_EXECSHARED) - No banner displayed", "")>
    </#if>
<#else>
    <#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - CONDITIONS NOT MET - Banner not displayed", "")>
    <#if !hasShareEvaluationPermission??>
        <#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - REASON: hasShareEvaluationPermission is NULL", "")>
    <#elseif hasShareEvaluationPermission == false>
        <#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - REASON: hasShareEvaluationPermission is FALSE", "")>
    </#if>
    <#if currentContentId != "WEFLD_IND">
        <#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - REASON: currentContentId is '" + currentContentId + "' (not WEFLD_IND)", "")>>
    </#if>
</#if>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - TEMPLATE RENDERING COMPLETED", "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", "")>
