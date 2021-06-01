<!--
    Tree menu
 -->

 <#assign mainContentId = "GP_MENU"/><!-- ROOT di riferimento -->
 <#assign validSubContentList = Static["com.mapsengineering.base.menu.MenuHelper"].createValidSubContentMap(request, mainContentId, "")/>
 
 <#assign selectedModule = Static["com.mapsengineering.base.menu.MenuHelper"].selectModuleValidSubContent(validSubContentList)/>

 <#if selectedModule?has_content>
  <script type="text/javascript">
  $j(document).ready( function() {
     TreeMenuMgr.openModule("${selectedModule}");
     });
  </script>
  </#if>
  <#if selectedMenuItem?has_content>
   <script type="text/javascript">
      $j(document).ready( function() {
      TreeMenuMgr.selectMenuItem("${selectedMenuItem}");
      });
   </script>
   </#if>

<#-- **********
     menu build structure
     *********** -->

<div id="mainmenu-container" class="treemenu-container">

    <div id="mainmenu-head" class="treemenu-head">

        <!-- Toolbar superiore -->
        <div id="mainmenu-upper-toolbar" class="treemenu-upper-toolbar">
            <@renderModule moduleList=validSubContentList/>
        </div>
        
        <div class="treemenu-separator-toolbar"></div>

        <!-- Functions toobar -->
        <div id="mainmenu-function-toolbar" class="treemenu-function-toolbar">
            <table id="mainmenu-function-table" class="treemenu-function-table">
                <tr>
                    <td id="mainmenu-function-openall" 	    class="treemenu-function-opeclosenall fa" description="${uiLabelMap.treeMenuCloseAllFunction}"><a href="#"></a></td>                   
                    <td id="mainmenu-function-closeall"     class="treemenu-function-opeclosenall fa" style="display: none;" description="${uiLabelMap.treeMenuOpenAllFunction}"><a href="#"></a></td>                  
                    <td id="mainmenu-function-clock" 		class="treemenu-function-clock fa" description="${uiLabelMap.treeMenuClockFunction}"><a href="#"></a></td>
                    <td id="mainmenu-function-preferred" 	class="treemenu-function-preferred fa" description="${uiLabelMap.treeMenuPreferredFunction}"><a href="#"></a></td>
                    <td id="mainmenu-function-home" 		class="treemenu-function-home fa" description="${uiLabelMap.treeMenuHomeFunction}"><input type="hidden" name="externalLoginKey" id="home-externalLoginKey" value="${requestAttributes.externalLoginKey?if_exists}"/><a href="#"></a></td>
                </tr>
            </table>
        </div>
    </div>

    <!-- main part with menu -->
    <div id="mainmenu-body" class="treemenu-body">

        <!-- preferred & history items area -->
        <div id="mainmenu-body-slidearea" class="treemenu-body-slidearea">
            <!-- preferred area -->
            <div id="mainmenu-body-slidearea-preferred" class="treemenu-body-slidearea-preferred"></div>
            <!-- history area -->
            <div id="mainmenu-body-slidearea-history" class="treemenu-body-slidearea-history"></div>
        </div>

        <!-- menu -->
        <div id="mainmenu-body-links" class="treemenu-body-links">
            <table id="mainmenu-outer-table" class="treemenu-outer-table">
                <tbody>
                    <@renderMenu contentList=validSubContentList/>
                </tbody>
            </table>
        </div>

    </div>

</div>


 <#-- ***********
  Macro defs
     ************ -->


<!--
    load & render module
-->
<#macro renderModule moduleList="" >
    <!-- get only module from Db -->
    <#if moduleList?has_content && moduleList.size() &gt; 6 >
        <#local rowEnd = 1>
    <#else>
        <#local rowEnd = 0>
    </#if>

    <table id="mainmenu-module-table" class="treemenu-module-table">
        <!-- render max 12 modules  -->
        <#list 0..rowEnd as row>
            <tr>
                <#list 1..6 as col>
                    <!-- get relative module -->
                    <#assign idx = col + (6 * row)/>
                    <#if moduleList?has_content && idx &lt;= moduleList.size()>
                        <!-- description sarà utilizzata da javascript per mostrare il modulo su evento hover del mouse -->
                        <!-- aggiungo id alla classe per caricare le immagini con link css -->
                        <#assign currentId = moduleList[idx-1].contentId />
                        	<td class="${currentId}">
                        		<div class="menu-module fa" contentid="${currentId}" description="${moduleList[idx-1].title}"><a href="#"></a></div>
                        	</td>	
                    <#else>
                        <td class="disabled-module"><span class="menu-module-disabled"/></td>
                    </#if>
                </#list>
            </tr>
        </#list>
    </table>
</#macro>


<!--
      Print menu upon page
-->
<#macro renderMenu contentList="" contentIdStart="">

    <#list contentList as contentMap>
	        
	        <!--
	           in construzione setto tutti elementi a display:none
	           sarà poi eseguita l'attivazione da javascript quando si clicca sui relativi moduli nella toolbar
	           -->
	        <#if contentMap.child?has_content>
	            <!-- ROOT -->
	            
	        	<!-- looking for parent -->
	            <#local nodeClass = "parent treemenu-module-root"/>
	            <#if contentIdStart?has_content>
	            	<!-- build parent id -->
	                <#local nodeClass = "child-of-" + contentIdStart/>
	            </#if>
	            <#local nodeClass = nodeClass + " menu-folder"/>
	            
	            <!-- folder nodes (parent) -->
	            <tr id="${contentMap.contentId?if_exists}" class="${nodeClass}">
	            	<td style="white-space: nowrap;"><i class="far fa-folder" style="padding-left: 4px;"></i><span class="title" title="${contentMap.contentId?if_exists} ${contentMap.title?if_exists}">${contentMap.title?if_exists}</span></td>
	            </tr>
	            <@renderMenu contentList=contentMap.child contentIdStart=contentMap.contentId/>
	            
	        <#else>
	        	<!-- ITEMS -->
	            
	            <#assign nodeClass="child"/>
	            <#if contentIdStart?has_content>
	                <#local nodeClass = "child-of-" + contentIdStart/>
	            </#if>
	            <#local nodeClass = nodeClass + " menu-item"/>

	            <tr id="${contentMap.contentId?if_exists}" class="${nodeClass} item-draggable">
	            	<td style="white-space: nowrap;"><i class="far fa-file"></i><span class="title" title="${contentMap.contentId?if_exists} ${contentMap.title?if_exists}">${contentMap.title?if_exists}</span></td>
	                 <!-- form for submit -->
	                <td style="display: none">
	                	<span>
	                    	<form class="treemenu-submit-form" action="${contentMap.link?if_exists}" id="${contentMap.contentId?if_exists}" module="${contentMap.module?if_exists}"> <!-- outer form -->
    	                    	<input type="hidden" value="${contentMap.targetType}" name="targetType">
                                <#if !contentMap.targetType?has_content || "plain" != contentMap.targetType>
	                        	    <input type="hidden" value="${requestAttributes.externalLoginKey}" name="externalLoginKey">
	                        	    <input type="hidden" value="${contentMap.contentId}" name="ownerContentId" />
	                            </#if>
                                
	                        	<#if contentMap.parameterList?has_content>
	                            	<#list contentMap.parameterList as params>
	                                    <input type="hidden" value="${params?split("=")[1]}" name="${params?split("=")[0]}">
	                                </#list>
	                            </#if>
	                            <#if contentMap.breadcrumbs?has_content>
	                                <input type="hidden" name="breadcrumbs" value="${contentMap.breadcrumbs}"/>
	                            </#if>
                            </form>
	                     </span>
	                </td>
	            </tr>
	    	</#if>
    </#list>
</#macro>

