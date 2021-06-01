import org.ofbiz.base.util.*;

def insertMode = parameters.insertMode;
if (UtilValidate.isEmpty(insertMode)) {
	insertMode = "N";
}

if ("Y".equals(insertMode)) {
	def accountTypeEnumId = context.accountTypeEnumId;
	
	if ("INDICATOR".equals(accountTypeEnumId)) {
		context.inputEnumId="ACCINP_UO";
	} else {
		context.inputEnumId="ACCINP_OBJ";
	}
}