import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/evaluateCtxTypeParams.groovy", context);

def codeField = "";
if ("MAIN".equals(context.showUoCode)) {
	codeField = "parentRoleCode";
}
if ("EXT".equals(context.showUoCode)) {
	codeField = "externalId";
}

context.codeField = codeField;
context.displayPartyField = "Y".equals(context.localeSecondarySet) ? "partyNameLang" : "partyName";
