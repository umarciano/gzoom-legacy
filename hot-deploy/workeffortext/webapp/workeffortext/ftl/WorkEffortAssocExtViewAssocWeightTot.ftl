<div  class="assocWeightTot" style="float: right;">        
  	<form class="basic-form cachable">
	    <table class="basic-table single-editable-totale">
			<tbody>
				<tr>
					<td class="label">${uiLabelMap.FormFieldTitle_assocWeightTot}</td>
					<td>
						<input  id="${workEffortAssocExtViewType?if_exists}_assocWeightTot" class="numericInList" type="text" size="6" value="${context.assocWeightTot?if_exists} / ${context.weightControlSum?if_exists}" name="assocWeightTot" readonly="readonly"/>
						<input  id="${workEffortAssocExtViewType?if_exists}_assocWeightTotHidden" class="numericInList hidden" type="text" size="6" value="${context.assocWeightTot?if_exists} / ${context.weightControlSum?if_exists}" name="assocWeightTotHidden" readonly="readonly"/>
					</td>
		  		</tr>
			</tbody>
		</table>
	</form>
</div>