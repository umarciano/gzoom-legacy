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

<#-- Banner visibile solo se:
     1. Utente ha i permessi (hasShareEvaluationPermission == true)
     2. Siamo nel tab Indicatori (currentContentId == "WEFLD_IND")
     3. Stato è "Valutazione da Completare" (currentStatusId == "WEEVALST_EXECPEND")
-->
<#if hasShareEvaluationPermission?? && hasShareEvaluationPermission == true && currentContentId == "WEFLD_IND" && currentStatusId?? && currentStatusId == "WEEVALST_EXECPEND">
    <#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - ALL CONDITIONS MET - Rendering banner", "")>
        <!-- Banner informativo con pulsante per condividere la valutazione - Layout centrato -->
        <div class="share-evaluation-banner" style="background-color: #f5f5f5; border: 1px solid #ddd; padding: 16px; margin-bottom: 15px; border-radius: 4px; text-align: center;">
            <!-- Pulsante centrato -->
            <div style="margin-bottom: 10px;">
                <button id="shareEvaluationButton_${workEffortId}" 
                        class="mediumSubmit" 
                        style="font-size: 12px; padding: 6px 12px; background-color: #4169E1; color: white; border: none; border-radius: 3px; cursor: pointer; transition: all 0.2s ease;"
                        onmouseover="this.style.backgroundColor='#365bb3';"
                        onmouseout="this.style.backgroundColor='#4169E1';">
                    <i class="fa fa-share" style="margin-right: 6px;"></i>${uiLabelMap.ShareEvaluationButton}
                </button>
            </div>
            <!-- Label informativa centrata sotto il pulsante -->
            <div style="line-height: 1.6; color: #555; font-size: 12px;">
                <i class="fa fa-info-circle" style="margin-right: 5px; color: #4169E1;"></i>${uiLabelMap.ShareEvaluationWarning}
            </div>
        </div>
        
        <!-- Script per gestire il click con modale dell'applicazione -->
        <script type="text/javascript">
        //<![CDATA[
            (function() {
                // Funzione per inizializzare il pulsante
                function initShareButton() {
                    var shareButton = $('shareEvaluationButton_${workEffortId}');
                    if (Object.isElement(shareButton)) {
                        // Rimuovi eventuali observer precedenti
                        shareButton.stopObserving("click");
                        
                        // Aggiungi il nuovo observer
                        shareButton.observe("click", function(event) {
                            Event.stop(event);
                            
                            // Debug log
                            console.log("ShareEvaluationButton clicked - Opening modal");
                            
                            // Usa la modale dell'applicazione invece del confirm del browser
                            modal_box_messages.confirm(
                                '${uiLabelMap.ShareEvaluationConfirm}',
                                null,
                                function() {
                                    // Callback eseguito quando l'utente conferma
                                    console.log("User confirmed - Redirecting to shareEvaluationToEvaluated");
                                    window.location.href = '<@ofbizUrl>shareEvaluationToEvaluated</@ofbizUrl>?workEffortId=${workEffortId}';
                                }
                            );
                        });
                        
                        console.log("ShareEvaluationButton observer attached successfully");
                    } else {
                        console.warn("ShareEvaluationButton not found: shareEvaluationButton_${workEffortId}");
                    }
                }
                
                // Esegui immediatamente se il DOM è già pronto, altrimenti aspetta
                if (document.loaded) {
                    initShareButton();
                } else {
                    document.observe("dom:loaded", initShareButton);
                }
            })();
        //]]>
        </script>
<#else>
    <#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - CONDITIONS NOT MET - Banner not displayed", "")>
    <#if !hasShareEvaluationPermission??>
        <#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - REASON: hasShareEvaluationPermission is NULL", "")>
    <#elseif hasShareEvaluationPermission == false>
        <#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - REASON: hasShareEvaluationPermission is FALSE", "")>
    </#if>
    <#if currentContentId != "WEFLD_IND">
        <#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - REASON: currentContentId is '" + currentContentId + "' (not WEFLD_IND)", "")>
    </#if>
    <#if !currentStatusId?? || currentStatusId != "WEEVALST_EXECPEND">
        <#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - REASON: currentStatusId is '" + (currentStatusId!"NULL") + "' (required: WEEVALST_EXECPEND)", "")>
    </#if>
</#if>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo("ShareEvaluationBanner.ftl - TEMPLATE RENDERING COMPLETED", "")>
<#assign dummy = Static["org.ofbiz.base.util.Debug"].logInfo(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", "")>
