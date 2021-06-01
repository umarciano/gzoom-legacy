<div  class="kpiScoreWeightTot" style="float: right;">        
  	<form class="basic-form cachable">
	    <table class="basic-table single-editable-totale">
			<tbody>
				<tr>
					<td class="label">${uiLabelMap.FormFieldTitle_kpiScoreWeightTot}</td>
					<td>
						<input id="${accountTypeEnumId?if_exists}_${contentIdInd?if_exists}_kpiScoreWeightTot" class="numericInList" type="text" size="6" value="${context.kpiScoreWeightTot?if_exists} / ${context.weightKpiControlSum?if_exists}" name="kpiScoreWeightTot" readonly="readonly"/>
						<input  id="${accountTypeEnumId?if_exists}_${contentIdInd?if_exists}_kpiScoreWeightTotHidden" class="numericInList hidden" type="text" size="6" value="${context.kpiScoreWeightTot?if_exists} / ${context.weightKpiControlSum?if_exists}" name="kpiScoreWeightTotHidden" readonly="readonly"/>
					</td>
		  		</tr>
			</tbody>
		</table>
	</form>
</div>
