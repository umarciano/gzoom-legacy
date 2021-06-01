import org.ofbiz.base.util.*;


def parentOrgUnitRoleType = context.parentOrgUnitRoleType;

if (UtilValidate.isNotEmpty(parentOrgUnitRoleType)) {
	def localeSecondarySet = context.localeSecondarySet;
	context.wrToOuRoleTypeDesc = "Y".equals(localeSecondarySet) ? parentOrgUnitRoleType.descriptionLang : parentOrgUnitRoleType.description;
}
