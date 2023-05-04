PartyRoleMultipleSelecionOrgHumanResView = {	
	load: function() {
	    var table = $$('#table_PE_PRVSRL001_PartyRoleView')[0];
	    if (Object.isElement(table)) {
	        table.select("td").each(function(td) {
                if (Object.isElement(td)) {
                    td.observe("dblclick", function() {
                        var i = 0;
                        var str = "";
                        if (document.getElementById('party-list-cnt')) {
                            str = document.getElementById('party-list-cnt').innerHTML;
                        }
                        var partyList = ""; 
                        if (document.getElementById('party-list')) {
                            partyList = document.getElementById('party-list').value;
                        }  
                        var arr = partyList != "" ? partyList.split(';#') : new Array();
            
                        table.select("tr").each(function(tr) {
                			if (tr && tr.hasClassName('selected-row')) {
                    			i++;
                    			var partyIdField = tr.down("input[name='partyId']");
                    			var partyId = Object.isElement(partyIdField) ? partyIdField.getValue() : "";
                    			var partyNameField = tr.down("input[name='partyName']");
                    			var partyName = Object.isElement(partyNameField) ? partyNameField.getValue() : "";
                    			var parentRoleCodeField = tr.down("input[name='parentRoleCode']");
                    			var parentRoleCode = Object.isElement(parentRoleCodeField) ? parentRoleCodeField.getValue() : "";
                    			if (arr.indexOf(partyId) < 0) {
                    				arr.push(partyId);
                   				    str += '<div id="party-cnt-' + partyId + '">';
                    				str += '<table>';
                    				str += '<tr><td>';
                    				str += '<input id="partyId_o_' + i + '" type="hidden" value="' + partyId + '"/>'; 
                    				str += '<input id="party_o_' + i + '" type="text" readonly="readonly" size="100" value="' + parentRoleCode + ' - ' + partyName + '"/>';
                    				str += '</td>';
                    				str += '<td>';
                    				str += '<a href="#" class="party-remove" style="vertical-align: middle;" onclick="PartyRoleMultipleSelecionOrgHumanResView.removeParty(\'' + partyId +'\');">' + '${uiLabelMap.PartyRemove}' + '</a>';
                    				str += '</td>';
                    				str += '</tr></table>';
                    				str += '</div>';
                    			}     
                			}
            			});
            			if (document.getElementById('party-list-cnt')) {
            	    		document.getElementById('party-list-cnt').innerHTML = str;
            			}
            			if (document.getElementById('party-list')) {
            	    		document.getElementById('party-list').value = arr.join(';#');
            			}          
                    });
                }
            });
	    }
	    
	},
	
	removeParty: function (partyId) {    
        var partyList = ""; 
        if (document.getElementById('party-list')) {
        	partyList = document.getElementById('party-list').value;
        }                      
        var arr = partyList != "" ? partyList.split(';#') : new Array();
        var arr2 = new Array();
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] != partyId) {
                arr2.push(arr[i]);
            }
        }
        var cnt = document.getElementById('party-cnt-' + partyId);
        if (cnt) {
         	cnt.remove();
        }
        if (document.getElementById('party-list')) {
         	document.getElementById('party-list').value = arr2.join(';#');
        }
	}
}
PartyRoleMultipleSelecionOrgHumanResView.load();