<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#assign messageContext = "${parameters.messageContext?if_exists}">

<#-- <#include "component://common/webcommon/includes/messages.ftl"/> -->
<#if requestAttributes.errorMessageList?has_content><#assign errorMessageList=requestAttributes.errorMessageList></#if>
<#if requestAttributes.eventMessageList?has_content><#assign eventMessageList=requestAttributes.eventMessageList></#if>
<#if requestAttributes.serviceValidationException?exists><#assign serviceValidationException = requestAttributes.serviceValidationException></#if>
<#if requestAttributes.uiLabelMap?has_content><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<#if !errorMessage?has_content>
  <#assign errorMessage = requestAttributes._ERROR_MESSAGE_?if_exists>
</#if>
<#if !errorMessageList?has_content>
  <#assign errorMessageList = requestAttributes._ERROR_MESSAGE_LIST_?if_exists>
</#if>
<#if !eventMessage?has_content>
  <#assign eventMessage = requestAttributes._EVENT_MESSAGE_?if_exists>
</#if>
<#if !eventMessageList?has_content>
  <#assign eventMessageList = requestAttributes._EVENT_MESSAGE_LIST_?if_exists>
</#if>

<#-- build the error message -->
<#assign errorMessages = "">
<#if (errorMessage?has_content)>
    <#assign errorMessages = errorMessages + "<p>" + errorMessage + "</p>\n">
</#if>
<#if (errorMessageList?has_content)>
    <#list errorMessageList as errorMsg>
        <#if (errorMsg?has_content)>
            <#assign errorMessages = errorMessages + "<p>" + errorMsg + "</p>\n">
        </#if>
    </#list>
</#if>

<#-- build the event message -->
<#assign eventMessages = "">
<#if (eventMessage?has_content)>
    <#assign eventMessages = eventMessages + "<p>" + eventMessage + "</p>\n">
</#if>
<#if (eventMessageList?has_content)>
	<#list eventMessageList as eventMsg>
        <#if (eventMsg?has_content)>
            <#assign eventMessages = eventMessages + "<p>" + eventMsg + "</p>\n">
        </#if>
	</#list>
</#if>

<div class="message-boxes-container">
    <span id="confirm-message-label" style="display: none;" >${uiLabelMap.BaseSaveConfirm}</span>
    
    <span class="message-title-container">${uiLabelMap.CommonMessage}</span>
    <span class="message-context-container">${messageContext?if_exists}</span>
    <span class="message-event-container">${eventMessages?if_exists}</span>
    <span class="message-error-container">${errorMessages?if_exists}</span>

    <div class="message-box-container">
        <div class="body-container">
            <div class="text-container"></div>
            <div class="text-informations-container"></div>
        </div>
        <div class="buttons-container">
            <a href="#" class="button-informations" onclick="modal_box_messages.buttonInfo()">${uiLabelMap.BaseButtonInformations}</a>
            <a href="#" class="smallSubmit button-close" onclick="modal_box_messages.buttonCancel()">${uiLabelMap.BaseButtonClose}</a>
            <a href="#" class="smallSubmit button-cancel" onclick="modal_box_messages.buttonCancel()">${uiLabelMap.BaseButtonCancel}</a>
            <a href="#" class="smallSubmit button-ok" onclick="modal_box_messages.buttonOK()">${uiLabelMap.BaseButtonOK}</a>
        </div>
    </div>

</div>
