import org.ofbiz.base.util.*;
import com.mapsengineering.workeffortext.util.FromAndThruDatesProviderFromParams;



FromAndThruDatesProviderFromParams fromAndThruDatesProvider = new FromAndThruDatesProviderFromParams(context, parameters, delegator, false);
fromAndThruDatesProvider.run();
if(UtilValidate.isNotEmpty(fromAndThruDatesProvider.getFromDate())) {
	context.inputFields.fromDate_op = 'lessThanEqualTo';
	context.inputFields.fromDate_value = fromAndThruDatesProvider.getFromDate();
}
if(UtilValidate.isNotEmpty(fromAndThruDatesProvider.getThruDate())) {
	context.inputFields.thruDate_op = 'greaterThanEqualTo';
	context.inputFields.thruDate_value = fromAndThruDatesProvider.getThruDate();
}
