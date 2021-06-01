import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;

if ("Y".equals(parameters.get("exportSearchButton"))) {
    Debug.log("*** checkExportSearchResult.groovy export-search");
    request.setAttribute("poiHssfFormRenderer_skipList", [ "searchForm" ]);
	return "export-search";
}

if ("Y".equals(parameters.get("massivePrintButton"))) {
    Debug.log("*** checkExportSearchResult.groovy massive-print-search");
    request.setAttribute("workEffortRootList", context.listIt);
	return "massive-print-search";
}

return "success";
