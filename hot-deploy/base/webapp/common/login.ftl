<#if requestAttributes.uiLabelMap?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#assign useMultitenant = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "multitenant")>

<#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
<#if username != "">
  <#assign focusName = false>
<#else>
  <#assign focusName = true>
</#if>
<center>
  <div class="screenlet login-screenlet">
    <div class="screenlet-title-bar">
      <h3>${uiLabelMap.CommonRegistered}</h3>
    </div>
    <div class="screenlet-body">
      <form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform">
        <table class="basic-table" cellspacing="0">
          <tr>
            <td class="label">${uiLabelMap.CommonUsername}</td>
            <td><input type="text" name="USERNAME" value="${username}" size="20"/></td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.CommonPassword}</td>
            <td><input type="password" name="PASSWORD" value="" size="20"/></td>
          </tr>
          <#if ("Y" == useMultitenant)>
            <tr>
              <td class="label">${uiLabelMap.CommonTenantId}</td>
              <td><input type="text" name="tenantId" value="${parameters.tenantId?if_exists}" size="20"/></td>
            </tr>
          </#if>
          <tr>
            <td colspan="2" align="center">
              <input class="button" type="submit" value="${uiLabelMap.CommonLogin}"/>
            </td>
          </tr>
        </table>
        <input type="hidden" name="JavaScriptEnabled" value="N"/>
        <br/>
        <#assign enableChangePassword = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("security.properties", "enableChangePassword")>
        <#if enableChangePassword == "true" >
        	<a href="<@ofbizUrl>forgotPassword</@ofbizUrl>" tabindex="3">${uiLabelMap.CommonForgotYourPassword}?</a>
        </#if>
        </form>
    </div>
  </div>
  <div>
      <#include "component://base/webapp/common/ftl/version.ftl"/>
  </div>
</center>

<script language="JavaScript" type="text/javascript">
  let msg={event : "login"};
  window.parent.postMessage(msg, '*');
  document.loginform.JavaScriptEnabled.value = "Y";
  <#if focusName>
    document.loginform.USERNAME.focus();
  <#else>
    document.loginform.PASSWORD.focus();
  </#if>
</script>
