import org.ofbiz.base.util.*;
import org.ofbiz.base.util.string.*;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants.StandardImportTableEnum;


for (tableName in StandardImportTableEnum.values()) {
    List<GenericValue> lista = delegator.findList(tableName.toString(), null, null, null, null, false);
    if (UtilValidate.isNotEmpty(lista)) {
         context[tableName.toString() + "_size"] = lista.size();
         def stdMessage = "";
         if (lista.size() > 1) {
	         stdMessage = UtilProperties.getMessage("BaseUiLabels", "StandardImportTableNotEmptyLabel", UtilMisc.toMap("size", lista.size()), locale);
    	 } else {
        	 stdMessage = UtilProperties.getMessage("BaseUiLabels", "StandardImportTableNotEmptyLabel_1", UtilMisc.toMap("size", lista.size()), locale);         
         }
         context[tableName.toString() + "ListSizeLabel"] = stdMessage;
    }
}