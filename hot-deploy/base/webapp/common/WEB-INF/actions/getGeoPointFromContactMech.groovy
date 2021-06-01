import org.ofbiz.base.util.*;

if (UtilValidate.isNotEmpty(context.contactMechId)) {
	contactMechId = context.contactMechId;
	postalAddress = delegator.findOne("PostalAddress", ["contactMechId": contactMechId], true);
	if (UtilValidate.isNotEmpty(postalAddress) && UtilValidate.isNotEmpty(postalAddress.geoPointId)) {
		context.geoPoint = delegator.findOne("GeoPoint", ["geoPointId": postalAddress.geoPointId], true);
	}
}