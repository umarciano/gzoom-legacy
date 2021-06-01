import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;



def assocWeightTot = 0;
def kpiScoreWeightTot = 0;

if (UtilValidate.isNotEmpty(context.listIt)) {
	for (GenericValue value: context.listIt) {
		
		if (value.containsKey("assocWeight") && UtilValidate.isNotEmpty(value.assocWeight)) {
			assocWeightTot += value.assocWeight;
		}
		
		if (value.containsKey("kpiScoreWeight") && UtilValidate.isNotEmpty(value.kpiScoreWeight)) {
			kpiScoreWeightTot += value.kpiScoreWeight;
		}
		
		
	}
}


context.assocWeightTot = assocWeightTot; 
context.kpiScoreWeightTot = kpiScoreWeightTot;
