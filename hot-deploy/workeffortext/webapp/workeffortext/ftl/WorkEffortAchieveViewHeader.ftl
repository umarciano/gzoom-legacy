<table cellspacing="0" cellpadding="0" class="basic-table list-table padded-row-table" style="background-color: RGB(222,222,222)">
  <tr>
   <td>
   <#assign weAnalisis = delegator.findOne("WorkEffortAnalysis", Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortAnalysisId", parameters.workEffortAnalysisId), true)>
    <div style="padding-bottom: 0.3em; text-align: center;">
        <span style="font-size: 2.3em">${weAnalisis.description?if_exists}</span>
    </div>
   </td>
  </tr>
</table>
