    <script type="text/javascript">
    
	    $j("input#IndicatorCalcSubmitButton").click(function() {
	  		var target = $j(this).attr("ofbiz-target");
	  		
	  		var container = $("IndicatorCalcResult");
	  		//force startWaiting beacuase is not ajax.Request
			Utils.startWaiting();
	  		$(container).startWaiting();
	  		
	  		//recupero la form di submit
	  		var form = $j("div#IndicatorCalcSubmit form").each(function() {
				$j.post(target, $j(this).serialize(), function(data, textStatus, jqXHR) {
						//force stopWaiting because is not ajax.Request
						$(container).stopWaiting();
						Utils.stopWaiting();
					
						var json = data.evalJSON();
						if (json.jobLogId) {
						  var jobLogId = json.jobLogId;
						  var cleanOnlyScoreCard = json.cleanOnlyScoreCard;
						  var blockingErrors = json.blockingErrors;
						  
						  modal_box_messages.alert("${uiLabelMap.ScoreCardCalc_finished}<br><br>${uiLabelMap.ScoreCardCalc_blockingErrors}" + blockingErrors + "<br><br>${uiLabelMap.ScoreCardCalc_jobLogId}" + " " + jobLogId );
							  
						  
						  ajaxUpdateArea($(container).identify(), "<@ofbizUrl>scoreCardCalcResult</@ofbizUrl>", {"jobLogId": jobLogId, "contentId": "WEFLD_ELIN"});					  
						  
						}
				});		  		
	  		});
	    });
    
    </script>


	<!-- 
		Form di submit 
	-->
	
	
	<div id="IndicatorCalcSubmit" style="position: relative;">
		<!-- Le variabili sono settate nnello screen WorkEffortTransactionIndicatorViewManagementScreen -->	
	    ${screens.render(submitFormScreenLocation, submitFormScreenName, Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortId", workEffortId?if_exists))}

    	<input id="IndicatorCalcSubmitButton" <#if context.crudEnumId?has_content && context.crudEnumId == 'NONE'> disabled  </#if> type="button"  style="cursor: pointer;" ofbiz-target="<@ofbizUrl>indicatorCalcObiettivo</@ofbizUrl>" value="${uiLabelMap.WorkEffortMeasureKpi_StartCalcIndicator}" class="smallButton">
	</div>
		
	<div style="height: 10px;"></div>
		
	<!--
	Lista risultati
	-->
	<div id="IndicatorCalcResult">
	    ${screens.render(resultFormScreenLocation, resultFormScreenName, Static["org.ofbiz.base.util.UtilMisc"].toMap("jobLogId", jobLogId?if_exists))}
	</div>	