import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

//Nota bene: il parametro parentTypeId é passato dal menu principale attraverso la form di ricerca 
//ed é variabile in base alla voce di menu scelta 

if (org.ofbiz.base.util.UtilValidate.isNotEmpty(context.listIt)) {
	
	if (org.ofbiz.base.util.UtilValidate.isNotEmpty(parameters.parentTypeId)) {
		context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, parameters.parentTypeId));
	}
	
}