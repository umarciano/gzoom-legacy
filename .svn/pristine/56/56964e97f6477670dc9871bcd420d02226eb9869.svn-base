    <script type="text/javascript">
    
	    $j("input#ScoreCardSubmitButton").click(function() {
	  		var target = $j(this).attr("ofbiz-target");
	  		
	  		var container = $("ScoreCardCalcResult");
	  		//force startWaiting because is not ajax.Request
			Utils.startWaiting();
	  		$(container).startWaiting();
	  		
	  		//recupero la form di submit
	  		var form = $j("div#ScoreCardSubmit form").each(function() {
				$j.post(target, $j(this).serialize(), function(data, textStatus, jqXHR) {
						//force stopWaiting because is not ajax.Request
						$(container).stopWaiting();
						Utils.stopWaiting();
						
						var json = data.evalJSON();
						if (json.jobLogId) {
						  var jobLogId = json.jobLogId;
						  var cleanOnlyScoreCard = json.cleanOnlyScoreCard;
						  var blockingErrors = json.blockingErrors;
						  
						  if(cleanOnlyScoreCard != 'Y'){
						  	  
							  var warningMessages = json.warningMessages;
							  var valueScoreCardCal = json.valueScoreCardCal;							 
							  modal_box_messages.alert("${uiLabelMap.ScoreCardCalc_finished}<br><br>${uiLabelMap.ScoreCardCalc_blockingErrors}" + blockingErrors + "<br><br>${uiLabelMap.ScoreCardCalc_jobLogId}" + " " + jobLogId + "<br><br> ${uiLabelMap.ScoreCardCalc_scoreCardValue}" + " " + valueScoreCardCal);
							  
						  } else {						  
						  	modal_box_messages.alert("${uiLabelMap.ScoreCardCalc_CleanFinished}<br><br>${uiLabelMap.ScoreCardCalc_blockingErrors}" + blockingErrors + "<br><br>${uiLabelMap.ScoreCardCalc_jobLogId}" + " " + jobLogId);
							  
						  }
						  
						  ajaxUpdateArea($(container).identify(), "<@ofbizUrl>scoreCardCalcResult</@ofbizUrl>", {"jobLogId": jobLogId, "contentId": "WEFLD_ELAB"});
						}
				});		  		
	  		});
	    });
    
    </script>
	
	
	<!-- 
		Form di submit 
	-->
	
	
	<div id="ScoreCardSubmit" style="position: relative;">
		<!-- Le variabili sono settate nnello screen WorkEffortMeasureKpiManagementScreen -->	
	    ${screens.render(submitFormScreenLocation, submitFormScreenName, Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortId", workEffortId?if_exists))}

    	<input id="ScoreCardSubmitButton" <#if context.crudEnumId?has_content && context.crudEnumId == 'NONE'> disabled  </#if> type="button"  style="cursor: pointer;" ofbiz-target="<@ofbizUrl>scoreCardCalc</@ofbizUrl>" value="${uiLabelMap.WorkEffortMeasureKpi_StartCalc}" class="smallButton">
	</div>
		
	<div style="height: 10px;"></div>
		
	<!--
	Lista risultati
	-->
	<div id="ScoreCardCalcResult">
	    ${screens.render(resultFormScreenLocation, resultFormScreenName, Static["org.ofbiz.base.util.UtilMisc"].toMap("jobLogId", jobLogId?if_exists))}
	</div>	
	
	