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

<!-- sandro: 23/04 fix: a) eliminato - iniziale che compariva se non era presente il primo campo;
                        b) basata la lista solo sui display field -->
<#if autocompleteOptions?exists>
  <ul>
    <#list autocompleteOptions as autocompleteOption>
        <#--<#assign fields = autocompleteOption.values()/>-->
        <#assign fieldsName = autocompleteOption.keySet()/>
        <#assign displayFields = parameters.displayFields?default('')/>
        <#assign selectFields = parameters.selectFields?default('')/>
        <#assign entityNameList = Static["org.ofbiz.base.util.StringUtil"].toList(parameters.entityName)/>
        
        <#assign selectFieldList = []/>
    	<#assign selectEntityFieldList = Static["org.ofbiz.base.util.StringUtil"].toList(selectFields)/>
        	
    	<#list selectEntityFieldList as selectEntityField>
        	<#assign a = selectEntityField?replace("&#91;","")/>
        	<#assign b = a?replace("&#93;","")/>
        	<#assign selectFieldList = selectFieldList + [b]/>
    	</#list>
        	
        <#assign countDisplayed = 0/>
        <#assign finalString = ""/>
          <li>
          	<#list selectFieldList as currentFieldName>
          		<#if displayFields?index_of(currentFieldName) != -1>
          		
	          		<#if countDisplayed &gt; 0 && (autocompleteOption.get(currentFieldName))?has_content>
		          		<span class="informal">
		          			<#if finalString?has_content><span class="separator">-</span></#if>
		          		</span>
		          	</#if>
		          	
			        <#if parameters[currentFieldName+"_description"]?has_content>
				        <#assign currentFieldDescription = Static["org.ofbiz.base.util.string.UelUtil"].evaluate(autocompleteOption, Static["org.ofbiz.base.util.StringUtil"].replaceString(parameters[currentFieldName+"_description"], "@{", "${"))>
				        <span class="informal" style="white-space: nowrap" title="${currentFieldDescription?if_exists}">${currentFieldDescription?if_exists}</span>
				        <span class="informal hidden">${currentFieldName?if_exists}_:_${currentFieldDescription?if_exists}</span>
				        <#assign finalString = finalString + currentFieldDescription?if_exists
				        />
			        <#else>
			        	<span class="informal" style="white-space: nowrap" title="${(autocompleteOption.get(currentFieldName?if_exists,locale))?if_exists}">${(autocompleteOption.get(currentFieldName?if_exists,locale))?if_exists}</span>
			        	<#assign finalString = finalString + (autocompleteOption.get(currentFieldName?if_exists,locale))?if_exists/>
			        	
			        </#if>
			        <#assign countDisplayed = 1/>
		        </#if>
		     </#list>
		     <#list selectFieldList as currentFieldName>
		     	<#if !(displayFields?index_of(currentFieldName) != -1 && parameters[currentFieldName+"_description"]?has_content)>
		     		<span class="informal hidden">${currentFieldName?if_exists}_:_${(autocompleteOption.get(currentFieldName?if_exists,locale))?if_exists}</span>
		     	</#if>
		     </#list>
		 </li>
    </#list>
  </ul>
<#else>
  <ul></ul>
</#if>
