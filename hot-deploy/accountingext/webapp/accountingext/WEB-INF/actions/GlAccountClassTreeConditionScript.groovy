import org.ofbiz.base.util.*;

accountTypeEnumId = parameters.accountTypeEnumId;
screenNameListIndex = parameters.screenNameListIndex;

//Caso creazione primo livello gerarchia GlAccountClassView
// Bug 3843
if(UtilValidate.isEmpty(accountTypeEnumId)) {
	if("GlAccountView".equals(context.entityName) || "GlAccountClassView".equals(context.entityName)){
		if(UtilValidate.isNotEmpty(parameters.id)) {
			def idField = parameters.id.substring(0, parameters.id.lastIndexOf("="));
			def id = parameters.id.substring(parameters.id.lastIndexOf("=") + 1);
			if(UtilValidate.isNotEmpty(id) && UtilValidate.isNotEmpty(idField)) {
				entity = delegator.findOne(context.entityName, UtilMisc.toMap(idField, id), false); //GlAccountView oppure GlAccountClassView
				if(UtilValidate.isNotEmpty(entity.accountTypeEnumId)) {
					accountTypeEnumId = entity.accountTypeEnumId;
				}
			}
		}
	} 
}

if(UtilValidate.isEmpty(accountTypeEnumId)) {
	if(UtilValidate.isNotEmpty(parameters.glAccountId)) {
		entity = delegator.findOne("GlAccountView", ["glAccountId" : parameters.glAccountId], false); //GlAccountView oppure GlAccountClassView
		if(UtilValidate.isNotEmpty(entity.accountTypeEnumId)) {
			accountTypeEnumId = entity.accountTypeEnumId;
		}
	} else if(UtilValidate.isNotEmpty(parameters.glAccountClassId)) {
		entity = delegator.findOne("GlAccountClassView", ["glAccountClassId" : parameters.glAccountClassId], false); //GlAccountView oppure GlAccountClassView
		if(UtilValidate.isNotEmpty(entity.accountTypeEnumId)) {
			accountTypeEnumId = entity.accountTypeEnumId;
		}
	}
}

switch (accountTypeEnumId) {
	case "FINANCIAL":
	    screenNameListIndex = "1";
		break;
	case "ACCOUNT":
	    screenNameListIndex = "2";
		break;
	case "INDICATOR":
	    screenNameListIndex = "3";
		break;	
	default:
		break;
}

return screenNameListIndex;