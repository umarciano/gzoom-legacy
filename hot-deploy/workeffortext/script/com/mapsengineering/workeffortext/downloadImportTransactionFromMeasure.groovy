import org.ofbiz.base.util.*;

if (UtilValidate.isNotEmpty(parameters.stream)) {
	UtilHttp.streamContentToBrowser(response, parameters.stream.toByteArray(), "application/vnd.ms-excel", "ImportazioneValoriMisure.xls")
}