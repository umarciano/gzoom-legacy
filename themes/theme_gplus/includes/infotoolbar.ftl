<#assign parametersMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("parentPortalPageId", parameters.parentPortalPageId?default("GP_WE_PORTAL"))>
<#assign dummy = parameters.putAll(parametersMap)> 
<#assign dummy=Static["org.ofbiz.base.util.GroovyUtil"].runScriptAtLocation("component://common/webcommon/WEB-INF/actions/includes/ListPortalPages.groovy", context) />

<div id="first-header-row" class="toolbar-container">
  <ul>
      <li class="link-main">
          <div id="link-main">
			<#list context.portalPages?if_exists as item>
				<#assign modulo="gzoom"/>
				<#assign portletAttributeList = delegator.findByAnd("PortletAttribute", Static["org.ofbiz.base.util.UtilMisc"].toMap("portalPageId", item.portalPageId, "attrName", "weContextId_value"))/>
        		<#if portletAttributeList?has_content>
		   			<#assign portletAttribute=portletAttributeList[0]/>
		   			<#assign contextIdEnum=Static["com.mapsengineering.base.util.ContextIdEnum"].parse(portletAttribute.attrValue)/>
		   			<#assign modulo=contextIdEnum.webcontext()/>
		  		</#if>
		   		<#assign title=StringUtil.wrapString(Static["com.mapsengineering.base.util.MessageUtil"].getMessage(item.portalPageName, item.description, locale))/> 
		  		<a href="#" class="event link-main<#if parameters.portalPageId?has_content && item.portalPageId == parameters.portalPageId> selezionato</#if>" onclick="ajaxUpdateAreas('common-container,/${modulo}/control/showPortalPageMainContainerOnly,noDataLoaded=Y&amp;externalLoginKey=${requestAttributes.externalLoginKey?if_exists}&portalPageId=${item.portalPageId?if_exists}&parentPortalPageId=${item.parentPortalPageId?if_exists}&saveView=Y&clearSaveView=Y&noLeftBar=${parameters.noLeftBar?if_exists?string}&noInfoToolbar=Y'); return false;" title="${title}">${title}</a>
		    </#list>
		 </div>
      </li>      
     
      <li class="logo-area">
          <div id="breadcrumbs"><#include "component://theme_gplus/includes/breadcrumbs.ftl" /></div>
      </li>
      
      <li class="control-area">
          <#if infoMenuScreenLocation?has_content && infoMenuScreenName?has_content>
          <#if context.helpContentId?has_content>
          ${screens.render(infoMenuScreenLocation,infoMenuScreenName, Static["org.ofbiz.base.util.UtilMisc"].toMap("helpContentId", context.helpContentId))}
          <#else>
          ${screens.render(infoMenuScreenLocation,infoMenuScreenName)}
          </#if>
          </#if>
      </li>
  </ul>
</div>
<br class="clear" />
<script type="text/javascript">
			    if (typeof AjaxUpdateFunction != 'undefined') {
			       AjaxUpdateFunction.load();
			    }
			    var portale = $$(".selezionato")[0];
			    if(Object.isElement($(portale))) {
			    	// simula portale.fire('dom:onclick');
		            var onclickFunc = portale.readAttribute('onclick');
        			if (onclickFunc) {
        				eval('var evalFuncTmp = function() { ' + onclickFunc + '; }');
                        evalFuncTmp();
			        }
		        }
</script>
