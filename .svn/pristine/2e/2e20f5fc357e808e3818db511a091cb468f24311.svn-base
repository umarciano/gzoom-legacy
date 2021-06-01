<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<#if (requestAttributes.person)?exists><#assign person = requestAttributes.person></#if>
<#if (requestAttributes.partyGroup)?exists><#assign partyGroup = requestAttributes.partyGroup></#if>

<#assign docLangAttr = locale.toString()?replace("_", "-")>
<#assign langDir = "ltr">
<#if "ar.iw"?contains(docLangAttr?substring(0, 2))>
    <#assign langDir = "rtl">
</#if>

<#assign nologout = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("security.properties", "security.saml.nologout")>

<html lang="${docLangAttr}" dir="${langDir}" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <#-- Sandro : aggiunto metatag attivazione GoogleChromeFrame per IE, vedi http://www.chromium.org/developers/how-tos/chrome-frame-getting-started -->
    <meta http-equiv="X-UA-Compatible" content="chrome=1">
    <#-- fine sandro -->
    <title>
        <#if defaultOrganizationPartyId?exists>
            <#if "Y" == localeSecondarySet?default("N")>
                ${defaultOrganizationPartyGroupNameLang?if_exists}
           <#else>
                ${defaultOrganizationPartyGroupName?if_exists}
            </#if>
        <#else>
            ${layoutSettings.companyName} 
        </#if>
    <#if !breadcrumbs?has_content && parameters.breadcrumbs?has_content>
        <#assign breadcrumbs = parameters.breadcrumbs/>
    </#if>

<#if breadcrumbs?has_content>
    <#assign breadcrumbList = StringUtil.toList(breadcrumbs,"\\|")/>
    <#assign count=0>
    <#list breadcrumbList as breadcrumb>
        <#assign b = StringUtil.wrapString(breadcrumb)/>
        ${b}
        <#if count &lt; breadcrumbList?size-1>
        &gt;
        </#if>
        <#assign count=count+1>
    </#list>
</#if>:. <#if (page.titleProperty)?has_content>${StringUtil.wrapString(uiLabelMap[page.titleProperty])}<#else>${(page.title)?if_exists}</#if></title>
	<#assign shortcutIcon = Static["com.mapsengineering.base.appheader.AppHeaderLogo"].getAppContentUrl(delegator, "ICON", defaultOrganizationPartyId?if_exists)>
    <#if shortcutIcon?has_content>
      <link rel="shortcut icon" href="<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon)}</@ofbizContentUrl>" />
    </#if>
    <#if layoutSettings.javaScripts?has_content>
        <#-- layoutSettings.javaScripts is a list of java scripts. -->
        <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
        <#assign javaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
        <#list layoutSettings.javaScripts as javaScript>
            <#if javaScriptsSet.contains(javaScript)>
                <#assign nothing = javaScriptsSet.remove(javaScript)/>
                <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
            </#if>
        </#list>
    </#if>
    <#if layoutSettings.extrajavaScripts?has_content>
        <#-- layoutSettings.extrajavaScripts is a list of java scripts. -->
        <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
        <#assign extrajavaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.extrajavaScripts)/>
        <#list layoutSettings.extrajavaScripts as javaScript>
            <#if extrajavaScriptsSet.contains(javaScript)>
                <#assign nothing = extrajavaScriptsSet.remove(javaScript)/>
                <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
            </#if>
        </#list>
    </#if>
    <#if layoutSettings.jawrScriptResources?has_content>
        <script type="text/javascript">
        <#-- layoutSettings.jawrResource is a list of java scripts. -->
        <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
        <#assign jawrScriptResourcesSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.jawrScriptResources)/>
        <#list layoutSettings.jawrScriptResources as jawrScriptResource>
            <#if jawrScriptResourcesSet.contains(jawrScriptResource)>
                <#assign nothing = jawrScriptResourcesSet.remove(jawrScriptResource)/>
                JAWR.loader.script('<@ofbizContentUrl>${StringUtil.wrapString(jawrScriptResource)}</@ofbizContentUrl>');
            </#if>
        </#list>
        </script>
    </#if>
    <#if layoutSettings.afterJAWRJavaScripts?has_content>
        <#-- layoutSettings.javaScripts is a list of java scripts. -->
        <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
        <#assign afterJAWRJavaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.afterJAWRJavaScripts)/>
        <#list layoutSettings.afterJAWRJavaScripts as afterJAWRJavaScript>
            <#if afterJAWRJavaScriptsSet.contains(afterJAWRJavaScript)>
                <#assign nothing = afterJAWRJavaScriptsSet.remove(afterJAWRJavaScript)/>
                <script src="<@ofbizContentUrl>${StringUtil.wrapString(afterJAWRJavaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
            </#if>
        </#list>
    </#if>
    <#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
        <#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
            <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
        </#list>
    </#if>
    <#if layoutSettings.styleSheets?has_content>
        <#-- layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet. -->
        <#list layoutSettings.styleSheets as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.VT_STYLESHEET?has_content>
        <#list layoutSettings.VT_STYLESHEET as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>

    <#if layoutSettings.rtlStyleSheets?has_content && langDir == "rtl">
        <#-- layoutSettings.rtlStyleSheets is a list of rtl style sheets. -->
        <#list layoutSettings.rtlStyleSheets as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.VT_RTL_STYLESHEET?has_content && langDir == "rtl">
        <#list layoutSettings.VT_RTL_STYLESHEET as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.VT_EXTRA_HEAD?has_content>
        <#list layoutSettings.VT_EXTRA_HEAD as extraHead>
            ${StringUtil.wrapString(extraHead)}
        </#list>
    </#if>
    <script type="text/javascript">
        if (typeof(log4javascript) != 'undefined') {
            var log = log4javascript.getDefaultLogger();
        }
    </script>
    
</head>
<#if layoutSettings.headerImageLinkUrl?exists>
  <#assign logoLinkURL = "${StringUtil.wrapString(layoutSettings.headerImageLinkUrl)}">
<#elseif layoutSettings.commonHeaderImageLinkUrl?exists>
  <#assign logoLinkURL = "${StringUtil.wrapString(layoutSettings.commonHeaderImageLinkUrl)}">
</#if>
<body style="background-color: transparent;">
    <#-- iframe che ricopre tutta la pagina per il calcolo dell'altezza dell\'iframe piu esterno di Gzoom2.0 -->
    <iframe id="page-container-frame">
    </iframe>
<#-- Sandro: attivazione GoogleChromeFrame per IE, vedi http://www.chromium.org/developers/how-tos/chrome-frame-getting-started -->
<!--[if gte IE 6]>
    
   <script type="text/javascript" 
	 src="http://ajax.googleapis.com/ajax/libs/chrome-frame/1/CFInstall.min.js"></script>
	
	<style>
	 .chromeFrameInstallDefaultStyle {
	   width: 100%; /* default is 800px */
	   border: 5px solid blue;
	 }
	</style>

    <script type="text/javascript">
	 // The conditional ensures that this code will only execute in IE,
	 // Therefore we can use the IE-specific attachEvent without worry
	   CFInstall.check({
	     mode: "overlay"
	   });
	</script>
<![endif]-->
	
	<script type="text/javascript">   
	  var w = window.top;
      document.getElementById('page-container-frame').contentWindow.addEventListener('resize', function(ev) {
          let msg={event : "resize", height: ev.target.document.body.clientHeight};
          w.postMessage(msg, '*');
      });
    </script>

  <div class="page-container">
    <div class="hidden">
      <a href="#column-container" title="${StringUtil.wrapString(uiLabelMap.CommonSkipNavigation)}" accesskey="2">
        ${StringUtil.wrapString(uiLabelMap.CommonSkipNavigation)}
      </a>
    </div>
    <#if "N" == parameters.noMasthead?default("N")>
    <div id="masthead">
      <ul>
        <#if (userPreferences.COMPACT_HEADER)?default("N") == "Y">
          <li class="logo-area">
            <#if shortcutIcon?has_content>
              <#if logoLinkURL?has_content> <a href="<@ofbizUrl>${StringUtil.wrapString(logoLinkURL)}</@ofbizUrl>"> </#if><img src="<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon)}</@ofbizContentUrl>"/><#if logoLinkURL?has_content></a></#if>
            </#if>
          </li>
          <ul id="preferences-menu">
              <#if userLogin?exists>
                <li class="user-preferences fa fa-2x"><a href="<@ofbizUrl>LookupVisualThemes</@ofbizUrl>" title="${uiLabelMap.BaseUserPreferences}"></a></li>
                <li class="user-profile fa fa-2x"><a href="#" title="${uiLabelMap.BaseUserProfile}"></a></li>
                <#if nologout == "false">
                	<li class="logout fa fa-2x"><a href="<@ofbizUrl>logout</@ofbizUrl>" title="${uiLabelMap.CommonLogout}"></a></li>
                </#if>
              </#if>
            </ul>
          <li>
            <#if person?has_content>
              ${StringUtil.wrapString(uiLabelMap.BaseUser)} ${StringUtil.wrapString(userLogin.userLoginId)} - ${person.firstName?if_exists} ${person.middleName?if_exists} ${person.lastName?if_exists}
              <#if defaultOrganizationPartyId?exists>
                  <#if "Y" == localeSecondarySet?default("N")>
                      ${StringUtil.wrapString(uiLabelMap.BaseOrganization)} : ${defaultOrganizationPartyGroupNameLang?if_exists}
                  <#else>
                      ${StringUtil.wrapString(uiLabelMap.BaseOrganization)} : ${defaultOrganizationPartyGroupName?if_exists}
                  </#if>
              </#if>
            <#elseif partyGroup?has_content>
              ${StringUtil.wrapString(uiLabelMap.BaseUser)} ${StringUtil.wrapString(userLogin.userLoginId)} - ${partyGroup.groupName?if_exists}
              <#if defaultOrganizationPartyId?exists>
                  <#if "Y" == localeSecondarySet?default("N")>
                      ${StringUtil.wrapString(uiLabelMap.BaseOrganization)} : ${defaultOrganizationPartyGroupNameLang?if_exists}
                  <#else>
                      ${StringUtil.wrapString(uiLabelMap.BaseOrganization)} : ${defaultOrganizationPartyGroupName?if_exists}
                  </#if>
              </#if>
            </#if>
          </li>
        <#else>
		  <#if login?default("false") == "true" || !userLogin?exists>
			<#assign headerImageUrl = Static["com.mapsengineering.base.appheader.AppHeaderLogo"].getAppContentUrl(delegator, "LOGO_LOGIN", defaultOrganizationPartyId?if_exists)>
		  <#else>
			<#assign headerImageUrl = Static["com.mapsengineering.base.appheader.AppHeaderLogo"].getAppContentUrl(delegator, "LOGO", defaultOrganizationPartyId?if_exists)>
		  </#if>

          <#if headerImageUrl?exists>
            <li class="logo-area<#if login?default("false") == "true" || !userLogin?exists>-login</#if>"><#if logoLinkURL?has_content><a href="<@ofbizUrl>${StringUtil.wrapString(logoLinkURL)}</@ofbizUrl>"></#if><#if layoutSettings.companyName?has_content><img alt="${StringUtil.wrapString(layoutSettings.companyName)}" src="<@ofbizContentUrl>${StringUtil.wrapString(headerImageUrl)}</@ofbizContentUrl>"/></#if><#if logoLinkURL?has_content></a></#if></li>
          </#if>
          <li class="organization-area<#if login?default("false") == "true" || !userLogin?exists>-login</#if> bottom-link">
			<#if defaultOrganizationPartyId?has_content>
				<#assign headerApplicationTitle = Static["com.mapsengineering.base.appheader.AppHeaderData"].getAppTitle(delegator, defaultOrganizationPartyId)>
			</#if>
			<div>
			  <div>
                <p style="width: 90%;display: inline;"><span class="application-title">${headerApplicationTitle?default("&nbsp;")}&nbsp;</span></p>
                <#if userLogin?exists>
                <#assign enableChange = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("BaseConfig", "locales.enableChange")>
                <#if enableChange?default("N") == "Y">
                    <div id="language" style="float: right;">
                        <#assign localesAvaibleStr = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("BaseConfig", "locales.available", "it_IT")>
                        <#assign localesAvaible = localesAvaibleStr.split(",")>
                        <#list localesAvaible as localeAvaible>
                            <#assign newlocale = Static["org.ofbiz.base.util.UtilMisc"].parseLocale(localeAvaible)>
                            <#if locale.getLanguage() != newlocale.getLanguage()>
                                <a href="<@ofbizUrl>setSessionLocale?newLocale=${localeAvaible.toString()}</@ofbizUrl>">${newlocale.getDisplayLanguage(newlocale)}</a>
                            </#if>
                        </#list>
                    </div>
                </#if>
                
                <div>
                  <div class="display-inline-block">
                      <#if userLoginValidPartyRoleList?has_content && userLoginValidPartyRoleList?size &gt; 1 >
                          <div style="padding-top: 1em;">
                              ${StringUtil.wrapString(uiLabelMap.BaseOrganization)} :
                              <form style="display: inline;" method="post" name="chooseOrganization" action="<@ofbizUrl>setSessionOrganizzationId</@ofbizUrl>">
                                  <select name="newOrganizzationId" class="selectBox" onchange="submit()">
                                  <#list userLoginValidPartyRoleList as userLoginValidPartyRole>
                                    <option value="${userLoginValidPartyRole.partyId}" <#if userLoginValidPartyRole.partyId == defaultOrganizationPartyId?if_exists> selected="selected"</#if>>
                                        <#if "Y" == localeSecondarySet?default("N")>
                                            ${userLoginValidPartyRole.groupNameLang?if_exists}
                                        <#else>
                                            ${userLoginValidPartyRole.groupName?if_exists}
                                        </#if>
                                    </option>
                                  </#list>
                                  
                                  
                                  </select>
                            
                              </form>
                          </div>
                      <#else>
                          <p><span class="organization-title">          
                              <#if "Y" == localeSecondarySet?default("N")>
                                        ${StringUtil.wrapString(uiLabelMap.BaseOrganization)} : ${defaultOrganizationPartyGroupNameLang?if_exists}
                              <#else>
                                        ${StringUtil.wrapString(uiLabelMap.BaseOrganization)} : ${defaultOrganizationPartyGroupName?if_exists}
                              </#if>          
                          </span></p>
                      </#if>
                  </div>
                  <div class="display-inline-block-fright" style="padding-left: 5px;">           
                      <p><span class="organization-title">
                      <#if person?has_content>
                        ${StringUtil.wrapString(uiLabelMap.BaseUser)} ${StringUtil.wrapString(userLogin.userLoginId)} - ${person.firstName?if_exists} ${person.middleName?if_exists} ${person.lastName?if_exists}
                      <#elseif partyGroup?has_content>
                        ${StringUtil.wrapString(uiLabelMap.BaseUser)} ${StringUtil.wrapString(userLogin.userLoginId)} - ${partyGroup.groupName?if_exists}
                      </#if>
                      </span></p>
                  </div>
                  <div class="display-inline-block-fright actions-area bottom-link">           
                      <ul id="preferences-menu">
                        <#if userLogin?exists>
                          <li class="user-preferences fa fa-2x"><a href="#" onclick="javascript: Utils.showModalBox('<@ofbizUrl>LookupVisualThemesContainerOnly</@ofbizUrl>',{title : '${uiLabelMap.CommonVisualThemes}', afterLoadModal: LookupVisualThemes.afterLoadModal, beforeHideModal: LookupVisualThemes.beforeHideModal, afterHideModal: LookupVisualThemes.afterHideModal, width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.20), height: document.viewport.getHeight() - (document.viewport.getHeight() * 0.20)}); return false;" title="${uiLabelMap.BaseUserPreferences}"></a></li>
                          <#assign enableChangePassword = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("security.properties", "enableChangePassword", "true")>
                          <#if enableChangePassword == "true"><li class="change-password fa fa-2x"><a href="#" onclick="javascript: Utils.showModalBox('<@ofbizUrl>LookupChangePasswordContainerOnly</@ofbizUrl>', {title: '${uiLabelMap.ChangePassword}', afterLoadModal: LookupProperties.afterLoadModal, width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.50), height: document.viewport.getHeight() - (document.viewport.getHeight() * 0.50)}); return false;" title="${uiLabelMap.ChangePassword}"></a></li></#if>
                          <#if nologout == "false">
                          	<li class="logout fa fa-2x"><a href="<@ofbizUrl>logout</@ofbizUrl>" title="${uiLabelMap.CommonLogout}"></a></li>
                          </#if>
                        </#if>
                      </ul>
                  </div>
                  
                  </#if>
                  
                </div>
              </div>
            </div>
          </li>
        </#if>
      </ul>
      <br class="clear" />
    </div>
    <br class="clear" />
	</#if>
