import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/evaluateCtxTypeParams.groovy", context);

def orderByPartyIdField = "parentRoleCode";
if ("EXTCODE".equals(context.orderUoBy)) {
	orderByPartyIdField = "externalId";
}
if ("UONAME".equals(context.orderUoBy)) {
	orderByPartyIdField = "Y".equals(context.localeSecondarySet) ? "partyNameLang" : "partyName";
}
def codeField = "";
if ("MAIN".equals(context.showUoCode)) {
	codeField = "parentRoleCode";
}
if ("EXT".equals(context.showUoCode)) {
	codeField = "externalId";
}
context.orderByPartyIdField = orderByPartyIdField;
context.codeField = codeField;
context.displayPartyIdField = "Y".equals(context.localeSecondarySet) ? "partyNameLang" : "partyName";