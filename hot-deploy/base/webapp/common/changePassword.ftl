<center>
  <div class="screenlet login-screenlet">
    <div class="screenlet-title-bar">
      <h3>${uiLabelMap.ChangePassword}</h3>
    </div>
    <div class="screenlet-body">
    <#if userLoginPasswordHistoryList?has_content>
        <#assign lastUserLoginHistory = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(userLoginPasswordHistoryList) />
        <#if lastUserLoginHistory?has_content>
            <#assign password_expiredMessage = Static["org.ofbiz.base.util.UtilProperties"].getMessage("SecurityextUiLabels_custom","loginservices.password_expired", Static["org.ofbiz.base.util.UtilMisc"].toMap("daysExpired", Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(lastUserLoginHistory.getTimestamp("fromDate"), Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp())), locale) />
            <#if password_expiredMessage?has_content>
                <div class="fieldgroup" style="margin: 9px 0 10px 0; font-size: 120%;">
                    <div class="fieldgroup-body">
                        <table cellspacing="0" cellpadding="0" class="single-editable">
                            <tbody>
                                <tr>
                                    <td class="label">${password_expiredMessage}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </#if>
        </#if>
    </#if>
    ${screens.render("component://base/widget/CommonScreens.xml", "ChangePasswordAfterLoginContainerOnly", context)}
    </div>
  </div>
</center>