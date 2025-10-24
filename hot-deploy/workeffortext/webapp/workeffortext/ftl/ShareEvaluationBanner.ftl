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
        <!-- Banner informativo con pulsante per condividere la valutazione - Layout centrato -->
        <div class="share-evaluation-banner" style="background-color: #f5f5f5; border: 1px solid #ddd; padding: 16px; margin-bottom: 15px; border-radius: 4px; text-align: center;">
            <!-- Pulsante centrato -->
            <div style="margin-bottom: 10px;">
                <button onclick="if(confirm('${uiLabelMap.ShareEvaluationConfirm}')) { window.location.href='shareEvaluationToEvaluated?workEffortId=${workEffortId}'; }" 
                        class="buttontext" 
                        style="background-color: #0066cc; color: #ffffff; border: none; padding: 10px 24px; font-size: 13px; font-weight: normal; cursor: pointer; border-radius: 3px; transition: all 0.2s ease;"
                        onmouseover="this.style.backgroundColor='#0052a3';"
                        onmouseout="this.style.backgroundColor='#0066cc';">
                    <i class="fa fa-share" style="margin-right: 6px;"></i>${uiLabelMap.ShareEvaluationButton}
                </button>
            </div>
            <!-- Label informativa centrata sotto il pulsante -->
            <div style="line-height: 1.6; color: #555; font-size: 12px;">
                <i class="fa fa-info-circle" style="margin-right: 5px; color: #0066cc;"></i>${uiLabelMap.ShareEvaluationWarning}
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
