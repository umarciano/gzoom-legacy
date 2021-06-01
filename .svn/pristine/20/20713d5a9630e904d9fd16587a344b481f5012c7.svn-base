		<#if listTypePrintIt?if_exists?has_content>
			<tr>
				<td class="label" style="width: 18%;">${uiLabelMap.BaseSelectTypePrint}</td>
				<td colspan="2" > 
					<#assign index = 0>
					<#list listTypePrintIt as print>   
					    <p><input type="radio" class="print-type-radio" name="outputFormat" value="${print.mimeTypeId}" <#if index=0> checked=true </#if>/><span class="readonly">${print.description}</span> </p>       
					    <#assign index = index+1>  		
					</#list> 
				</td>
			</tr>
			<tr>
				<td><br></td>
				<td><br></td>
			</tr>
		</#if>
		
		<!-- Condizione per mostrare la lista delle lingue possibili per le stampe -->
		<#assign languageMultiType = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("BaseConfig","Language.multi.type")/>
		<#if languageMultiType = "BILING" || languageMultiType = "REPORTING">
			<tr> 
				<td class="label" style="width: 18%;">${uiLabelMap.BaseSelectLanguage}</td>
				<td colspan="2">
				    <div>
			          <select name="birtLocale">
			            <#assign availableLocales = Static["com.mapsengineering.base.util.UtilLanguageLocale"].availableBaseConfigLocales()/>
			            <#if availableLocales.size() &gt;= 0 >
			            	<#assign availableLocale = availableLocales[0]/>
				            <option value="${availableLocale.toString()}" <#if locale.toString() = availableLocale.toString()> selected</#if>>${availableLocale.getDisplayName(availableLocale)}</option>
				            <#if availableLocales.size() &gt;= 1 >
				            <#assign availableLocale = availableLocales[1]/>
				            <option value="${availableLocale.toString()}_LANG" <#if locale.toString() = availableLocale.toString()> selected</#if>>${availableLocale.getDisplayName(availableLocale)}</option>
				            </#if>
			            </#if>
			            
			          </select>
				    </div>
				</td>
			</tr>
			<tr>
				<td><br></td>
				<td><br></td>
			</tr>
		</#if>
		
		<#if listAddParamsIt?if_exists?has_content>
			<tr>
				<td class="label" style="width: 18%;">${uiLabelMap.BaseSelectAdditionalParams}</td>
				<td colspan="2"> 					
					<#assign index = 0>
					<#list listAddParamsIt as print>   
					    <p><input type="radio" class="print-addparams-radio" name="filterContentId" value="${print.contentId}"<#if index=0> checked=true </#if>/>${print.description}</span></p>       
					    <#assign index = index+1>  		
					</#list>
				</td>
			</tr>
			<tr>
				<td><br></td>
				<td><br></td>
			</tr>
		</#if>
