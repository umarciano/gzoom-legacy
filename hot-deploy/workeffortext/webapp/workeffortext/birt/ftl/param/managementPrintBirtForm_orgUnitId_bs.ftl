<#--
   Unità Responsabile per le stampe CTX_BS (Performance Strategica).

   Regola (richiesta cliente): i Direttori Amministrativo/Sanitario strategici
   (gruppi STRATPERF_DIR_SAN / STRATPERF_DIR_AMM) sono ANCHE Dir UO/valutatori, quindi il
   ramo VALUTATORE dell'include standard (managementPrintBirtForm_orgUnitId.ftl) precompilerebbe
   il campo con la loro UO direzionale. Per loro il campo va SBIANCATO (vuoto).
   Per tutti gli altri (Dir UO puro, admin) si delega all'include standard, comportamento INVARIATO.

   Sono solo 2 utenti oggi (mariomassimo.mensorio, marcella.abbate) ma il controllo è per GRUPPO,
   quindi resta valido se cambiano le persone.

   NB tecnici:
   - in questo FTL 'parameters.userLogin' non è valorizzato: si prova session -> context.
   - si usa la findByAnd a 2 argomenti (entita', mappa): la 4-arg qui risolve su un overload
     sbagliato e torna vuota. Le membership si filtrano per data (solo attive).
-->
<#assign bsUid = (parameters.userLogin.userLoginId)!"" />
<#if bsUid == ""><#assign bsUid = (session.getAttribute("userLogin").userLoginId)!"" /></#if>
<#if bsUid == ""><#assign bsUid = (userLogin.userLoginId)!"" /></#if>

<#assign isDirSanAmmBs = false />
<#if bsUid != "">
    <#assign bsUserGroups = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(delegator.findByAnd("UserLoginSecurityGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("userLoginId", bsUid)))![] />
    <#list bsUserGroups as bsG>
        <#if (bsG.groupId!"") == "STRATPERF_DIR_SAN" || (bsG.groupId!"") == "STRATPERF_DIR_AMM">
            <#assign isDirSanAmmBs = true />
        </#if>
    </#list>
</#if>

<#if isDirSanAmmBs>
    <#-- SBIANCATO: autocomplete Unità Responsabile vuoto (nessuna precompilazione) -->
    <tr>
        <td class="label">${uiLabelMap.FormFieldTitle_orgUnitId}</td>
        <td class="widget-area-style">
            <div class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId">
                <input class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
                <input class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyAndPartyParentRoleAndRoleTypeView]"/>
                <input class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
                <input class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, parentRoleCode, externalId, partyName]]"/>
                <input class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[parentRoleCode]]"/>
                <input class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName]]"/>
                <input class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[parentRoleTypeId| equals| ORGANIZATION_UNIT]! [organizationId| equals| ${defaultOrganizationPartyId?if_exists}]]]"/>
                <input class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
                <input class="autocompleter_parameter" type="hidden" name="partyName_description" value="@{partyName}"/>
                <input class="autocompleter_parameter" type="hidden" name="entityKeyField" value="partyId"/>
                <input class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="partyName"/>
                <div class="droplist_container">
                    <input type="hidden" class="droplist_code_field" name="orgUnitId"/>
                    <input type="text" size="100" maxlength="255" value="" class="droplist_edit_field" name="partyName_orgUnitId" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId_edit_value"/>
                    <span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span>
                </div>
            </div>
        </td>
    </tr>
<#else>
    <#include "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_orgUnitId.ftl" />
</#if>
