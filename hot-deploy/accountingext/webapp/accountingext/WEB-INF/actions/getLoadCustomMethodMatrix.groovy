import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

import java.util.*;



listHeaderColumn = [];
listMatrix = [];
listRowReturn = [];

for (GenericValue ele: context.listIt) {

	Map rowMap = [:];
	rowMap.rowInputValue = ele.rowInputValue;
	
	if(!listRowReturn.contains(ele.rowInputValue)) {	
			
		listRowReturn.add(ele.rowInputValue);
			
		rowMap.put(ele.columnInputValue, ele.outputValue);
		listMatrix.add(rowMap);
		
	} else {
		index = listRowReturn.indexOf(ele.rowInputValue);
		listMatrix.get(index).put(ele.columnInputValue, ele.outputValue);
	}
	
	/**
	 * Aggiungo le colonne
	 */
	if (!listHeaderColumn.contains(ele.columnInputValue)) {		
		listHeaderColumn.add(ele.columnInputValue);		
	}
	
}


 context.listMatrix = listMatrix;
 context.listHeaderColumn = listHeaderColumn.sort();
 
 //Debug.log("...................... listMatrix "+ listMatrix);
 //Debug.log("...................... listHeaderColumn "+ listHeaderColumn);