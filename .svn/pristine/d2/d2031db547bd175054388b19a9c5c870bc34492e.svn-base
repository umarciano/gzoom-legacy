import com.mapsengineering.base.birt.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.TimeZone;


def parameters = parameters.parameters;

def glAccount = delegator.findOne("GlAccount", ["glAccountId": parameters.weTransAccountId], false);
def uom = delegator.getRelatedOneCache("Uom", glAccount);

if(UtilValidate.isNotEmpty(uom)	&& "RATING_SCALE".equals(uom.uomTypeId) && UtilValidate.isNotEmpty(parameters.weTransValue)) {
	
	def nf = NumberFormat.getNumberInstance(locale);
	def df = (DecimalFormat)nf;
    try {
    	parameters.weTransValue = df.parse(parameters.weTransValue);
    	if(UtilValidate.isNotEmpty(parameters.weTransValueKpiScore)) {
    		parameters.weTransValueKpiScore = df.parse(parameters.weTransValueKpiScore);
    	}
    } catch (Exception e) {}
}


if (UtilValidate.isNotEmpty(uom) && uom.uomTypeId == "DATE_MEASURE") {
	
	if (UtilValidate.isNotEmpty(parameters.weTransValue) && (parameters.weTransValue instanceof String)){
		parameters.weTransValue = UtilDateTime.dateConvertNumberString(parameters.weTransValue, timeZone, locale);
	}
	
	if (UtilValidate.isNotEmpty(parameters.weTransValueKpiScore) && (parameters.weTransValueKpiScore instanceof String)){
		parameters.weTransValueKpiScore = UtilDateTime.dateConvertNumberString(parameters.weTransValueKpiScore, timeZone, locale);
	}
}


