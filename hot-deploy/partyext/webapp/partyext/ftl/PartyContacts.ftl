<#-- PartyContacts -->

<#if contactMeches?has_content>
    <table class="basic-table padded-row-table hover-bar resizable draggable toggleable selectable customizable headerFixable" cellspacing="0">
      <thead>
          <tr class="header-row-2">
            <th>${uiLabelMap.CommonType}</th>
            <th>${uiLabelMap.CommonInformation}</th>
          </tr>
      </thead>
      <tbody>
      <#assign index=0/>
      <#list contactMeches as contactMechMap>
        <#assign contactMech = contactMechMap.contactMech>
        <#assign partyContactMech = contactMechMap.partyContactMech>
        <tr <#if index%2 != 0>class="alternate-row"</#if>>
          <td class="label align-top">
            <input class="mandatory" type="hidden" value="${entityName}" name="entityName"/>
            <input class="mandatory" type="hidden" value="UPDATE" name="operation"/>
            <input type="hidden" value="BaseMessageSaveData" name="messageContext"/>
            <input type="hidden" value="${partyContactMech.partyId}" name="partyId"/>
            <input type="hidden" value="${partyContactMech.contactMechId}" name="contactMechId"/>
            <input type="hidden" value="${contactMech.contactMechTypeId}" name="contactMechTypeId"/>
            <input type="hidden" value="${partyContactMech.fromDate}" name="fromDate"/>
            <input type="hidden" value="PartyContactMechManagementContextMenu" name="contextMenuName"/>
            <input type="hidden" value="component://partyext/widget/menus/PartyExtMenus.xml" name="contextMenuLocation"/>
            
            <#if parentEntityName == "PartyRoleView" && roleTypeId?has_content>
                <#assign partyRoleView = delegator.findOne("PartyRoleView", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId, "roleTypeId", roleTypeId), true)>
                <#if parameters.parentRoleCode?has_content>
                    <#assign parentRoleCode = parameters.parentRoleCode>
                    <#else>
                        <#if partyRoleView?has_content && partyRoleView.parentRoleCode?has_content>
                            <#assign parentRoleCode = partyRoleView.parentRoleCode>
                        </#if>
                </#if>
                <#if parameters.parentRoleTypeId?has_content>
                    <#assign parentRoleTypeId = parameters.parentRoleTypeId>
                    <#else>
                        <#if partyRoleView?has_content && partyRoleView.roleTypeId?has_content>
                            <#assign parentRoleTypeId = partyRoleView.parentRoleTypeId>
                        </#if>
                </#if>
                <#if parameters.roleTypeId?has_content>
                    <#assign roleTypeId = parameters.roleTypeId>
                    <#else>
                        <#if partyRoleView?has_content && partyRoleView.roleTypeId?has_content>
                            <#assign roleTypeId = partyRoleView.roleTypeId>
                        </#if>
                </#if>

                <input type="hidden" value="${parentRoleCode?if_exists}" name="parentRoleCode"/>
                <input type="hidden" value="${parentRoleTypeId?if_exists}" name="parentRoleTypeId"/>
                <input type="hidden" value="${roleTypeId?if_exists}" name="roleTypeId"/>
            </#if>
            ${contactMechMap.contactMechType.get("description",locale)}
          </td>
          <td class="vertical-top-alignment">
            <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
              <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOneCache("ContactMechPurposeType")>
              <div class="contact-paragraphs">
                <#if contactMechPurposeType?has_content>
                  <b>${contactMechPurposeType.get("description",locale)}</b>
                <#else>
                  <b>${uiLabelMap.PartyMechPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"</b>
                </#if>
                <#--<#if partyContactMechPurpose.thruDate?has_content>
                  (${uiLabelMap.CommonExpire}: ${partyContactMechPurpose.thruDate})
                </#if>-->
              </div><br/>
            </#list>
            <#if "POSTAL_ADDRESS" = contactMech.contactMechTypeId>
              <#if contactMechMap.postalAddress?has_content>
              <#assign postalAddress = contactMechMap.postalAddress>
              <div class="contact-paragraphs">
                <#if postalAddress.toName?has_content><b>${uiLabelMap.PartyAddrToName}:</b> ${postalAddress.toName}<br /></#if>
                <#if postalAddress.attnName?has_content><b>${uiLabelMap.PartyAddrAttnName}:</b> ${postalAddress.attnName}<br /></#if>
                <p class="contact-paragraph">${postalAddress.address1?if_exists}</p>
                <#if postalAddress.address2?has_content>${postalAddress.address2}</#if>
                <p class="contact-paragraph">${postalAddress.postalCode?if_exists}
                ${postalAddress.city?if_exists},
                <#if postalAddress.stateProvinceGeoId?has_content>
                  <#assign stateProvince = postalAddress.getRelatedOneCache("StateProvinceGeo")>
                  ${stateProvince.abbreviation?default(stateProvince.geoId)}
                </#if>
                </p>
                <#if postalAddress.countryGeoId?has_content>
                  <#assign country = postalAddress.getRelatedOneCache("CountryGeo")>
                  <p class="contact-paragraph">${country.geoName?default(country.geoId)}</p>
                </#if>
              </div>
              <div class="contact-actions">
              <#--<#if (postalAddress?has_content && !postalAddress.countryGeoId?has_content) || postalAddress.countryGeoId = "USA">
                <#assign addr1 = postalAddress.address1?if_exists>
                <#if addr1?has_content && (addr1.indexOf(" ") > 0)>
                  <#assign addressNum = addr1.substring(0, addr1.indexOf(" "))>
                  <#assign addressOther = addr1.substring(addr1.indexOf(" ")+1)>
                  <a target="_blank" href="http://www.whitepages.com/find_person_results.pl?fid=a&s_n=${addressNum}&s_a=${addressOther}&c=${postalAddress.city?if_exists}&s=${postalAddress.stateProvinceGeoId?if_exists}&x=29&y=18" class="buttontext">lookup:whitepages.com</a>
                </#if>
              </#if> -->
              <#if postalAddress.geoPointId?has_content>
                <#assign popUptitle = uiLabelMap.CommonGeoLocation>
                <#if contactMechPurposeType?has_content>
                    <#assign popUptitle = contactMechPurposeType.get("description",locale) + popUptitle>
                </#if>
                <ul>
                    <li class="gmaps">
                        <a href="javascript:popUp('<@ofbizUrl>geoLocation?geoPointId=${postalAddress.geoPointId}&amp;saveView=N</@ofbizUrl>', '${popUptitle}', '450', '550')">${uiLabelMap.CommonGeoLocation}</a>
                    </li>
                </ul>
              </#if>
              </div>
              </#if>
            <#elseif "TELECOM_NUMBER" = contactMech.contactMechTypeId>
              <#if contactMechMap.telecomNumber?has_content>
              <#assign telecomNumber = contactMechMap.telecomNumber>
              <div>
                <p class="contact-paragraph">${telecomNumber.countryCode?if_exists}
                <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode?default("000")}-</#if>${telecomNumber.contactNumber?default("000-0000")}
                <#if partyContactMech.extension?has_content>${uiLabelMap.PartyContactExt}&nbsp;${partyContactMech.extension}</#if></p>
              </div>
              </#if>
            <#elseif "EMAIL_ADDRESS" = contactMech.contactMechTypeId>
                <div class="contact-paragraphs">
                    <p class="contact-paragraph">${contactMech.infoString?if_exists}</p>
                </div>
                <div class="contact-actions">
                    <ul>
                        <li class="email fa fa-envelope">
                            <a href="#" onclick="javascript: Utils.showModalBox('<@ofbizUrl>EditCommunicationEvent?partyIdFrom=${userLogin.partyId?if_exists}&partyIdTo=${party.partyId}&communicationEventTypeId=EMAIL_COMMUNICATION&contactMechIdTo=${contactMech.contactMechId}&contactMechTypeId=EMAIL_ADDRESS<#if thisUserPrimaryEmail?has_content>&contactMechIdFrom=${thisUserPrimaryEmail.contactMechId}</#if></@ofbizUrl>', {width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.50), afterLoadModal : LookupProperties.afterLoadModal, beforeHideModal: LookupProperties.beforeHideModal, afterHideModal : LookupProperties.afterHideModal}); return false;"/>
                        </li>
                    </ul>
                </div>
            <#elseif "WEB_ADDRESS" = contactMech.contactMechTypeId>
              <div>
                <p class="contact-paragraph">${contactMech.infoString?if_exists}</p>
                <#assign openAddress = contactMech.infoString?default("")>
                <#if !openAddress?starts_with("http") && !openAddress?starts_with("HTTP")><#assign openAddress = "http://" + openAddress></#if>
                <a target="_blank" href="${openAddress}" class="buttontext">${uiLabelMap.CommonOpenPageNewWindow}</a>
              </div>
            <#else>
              <div><p class="contact-paragraph">${contactMech.infoString?if_exists}</p></div>
            </#if>
            <#if partyContactMech.thruDate?has_content><div class="contact-paragraphs"><b>${uiLabelMap.PartyContactEffectiveThru}:&nbsp;${partyContactMech.thruDate?string(Static["org.ofbiz.base.util.UtilDateTime"].getDateFormat(locale))}</b></div></#if>
            <#-- create cust request -->
            <#if custRequestTypes?exists>
              <form name="createCustRequestForm" action="<@ofbizUrl>createCustRequest</@ofbizUrl>" method="POST">
                <input type="hidden" name="partyId" value="${party.partyId}"/>
                <input type="hidden" name="fromPartyId" value="${party.partyId}"/>
                <input type="hidden" name="fulfillContactMechId" value="${contactMech.contactMechId}"/>
                <select name="custRequestTypeId">
                  <#list custRequestTypes as type>
                    <option value="${type.custRequestTypeId}">${type.get("description", locale)}</option>
                  </#list>
                </select>
                <input type="submit" class="smallSubmit" value="${uiLabelMap.PartyCreateNewCustRequest}"/>
              </form>
            </#if>
          </td>
        </tr>
        <#assign index = index+1>
      </#list>
      </tbody>
    </table>
</#if>
