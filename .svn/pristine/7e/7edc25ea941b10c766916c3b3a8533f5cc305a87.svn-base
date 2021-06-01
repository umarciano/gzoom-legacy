

function getFormatPattern(decimalScale,um){

var pattern = "#,##0";
	if (decimalScale > 0) {
		pattern = pattern + ".";
		
		for(i=0; i<decimalScale; i++ ){
			pattern += "0";
		}
	}
	if ("%".equals(um) || (um != null && um.indexOf("Perc") != -1)) {
		pattern = pattern + "%";
	}
return pattern;

}

function getFormatPatternWithoutZero(decimalScale,um){

var pattern = "#,##0";
	if (decimalScale > 0) {
		pattern = pattern + ".";
		
		for(i=0; i<decimalScale; i++ ){
			pattern += "#";
		}
	}
	if ("%".equals(um) || (um != null && um.indexOf("Perc") != -1)) {
		pattern = pattern + "%";
	}
return pattern;

}