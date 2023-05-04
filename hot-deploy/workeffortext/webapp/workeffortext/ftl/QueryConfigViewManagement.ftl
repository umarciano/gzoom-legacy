<table>
  <tr>
      <td class="label">${uiLabelMap.FormFieldTitle_queryName}</td>
      <td width="50%"><span class="cella">${queryName?if_exists}</span></td>
      <td style="width: auto;">
          <a href="#" onclick="QueryConfigViewManagement.showQueryInfo();">${uiLabelMap.QueryConfigPreview}</a>&nbsp;&nbsp;
          <#if queryType?if_exists == 'E'>
              <a href="#" onclick="QueryConfigViewManagement.downloadExcel();">${uiLabelMap.QueryConfigExecute}</a>
          <#elseif queryType?if_exists == 'A'>
              <a href="#" onclick="QueryConfigViewManagement.executeQuery();">${uiLabelMap.QueryConfigExecute}</a>
          </#if>
      </td>
  </tr>
</table>

<div id="query-info-cnt" style="display: none;">
  <div><pre>${queryInfo?if_exists}</pre></div>
</div>

<a id="query-excel-download" style="display: none;" href="" target="_blank"></a>