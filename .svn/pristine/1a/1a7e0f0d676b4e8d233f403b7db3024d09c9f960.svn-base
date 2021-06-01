import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.workeffortext.util.FromAndThruDatesProviderFromParams;
import javolution.util.*;

Debug.log("*************************************  loadWorkEffortRoot.groovy workEffortRootEntity ");
def nowStamp = UtilDateTime.nowTimestamp();


if (parameters.rootInqyTree != "Y") {
    def rootSearchRootInqyServiceMap = [:];
    rootSearchRootInqyServiceMap.put("workEffortRootId", UtilValidate.isNotEmpty(parameters.workEffortIdRoot) ? parameters.workEffortIdRoot : parameters.workEffortId);
    rootSearchRootInqyServiceMap.put("userLogin", context.userLogin);
    def rootSearchRootInqyServiceRes = dispatcher.runSync("getCanViewUpdateWorkEffortRoot", rootSearchRootInqyServiceMap);
    Debug.log("************************************* loadWorkEffortRoot.groovy rootSearchRootInqyServiceRes " + rootSearchRootInqyServiceRes);
    rootInqyTree = (rootSearchRootInqyServiceRes.canUpdateRoot == "Y" ? "N" : "Y" );
    Debug.log("************************************* loadWorkEffortRoot.groovy rootInqyTree " + rootInqyTree);
    context.rootInqyTree = rootInqyTree;
    parameters.rootInqyTree = rootInqyTree;
}

def workEffortViewRootEntity = delegator.findOne("WorkEffortView", ["workEffortId" : UtilValidate.isNotEmpty(parameters.workEffortIdRoot) ? parameters.workEffortIdRoot : parameters.workEffortId], false);
if (UtilValidate.isNotEmpty(workEffortViewRootEntity)) {
    context.workEffortRoot = workEffortViewRootEntity;
    
    // GN-1960
    if("Y".equals(workEffortViewRootEntity.enableMultiYearFlag) && (UtilValidate.isEmpty(parameters.targetCode) || "Y".equals(parameters.survey))) {
        def providerContextMap = new FastMap();
        providerContextMap.timeZone = context.timeZone;
        providerContextMap.locale = locale;
        providerContextMap.workEffortIdRoot = workEffortViewRootEntity.workEffortId;
        providerContextMap.workEffortTypeId = workEffortViewRootEntity.workEffortTypeId;
        
        FromAndThruDatesProviderFromParams fromAndThruDatesProvider = new FromAndThruDatesProviderFromParams(providerContextMap, parameters, delegator, true);
        fromAndThruDatesProvider.run();
        if(UtilValidate.isEmpty(parameters.searchDate) || fromAndThruDatesProvider.isEnableParentPeriodFilter() == true) {
            if (UtilValidate.isNotEmpty(fromAndThruDatesProvider.getDefaultSearchDate())) {
                parameters.searchDate = UtilDateTime.toDateString(fromAndThruDatesProvider.getDefaultSearchDate(), locale);
            }
        }
    }
    context.putAll(context.workEffortRoot);
}

def nowStamp2 = UtilDateTime.nowTimestamp();
Debug.log("************************************* loadWorkEffortRoot.groovy workEffortViewRootEntity " + (nowStamp2.getTime() - nowStamp.getTime() ));

