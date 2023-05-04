import org.ofbiz.base.util.*;

Debug.log("####################################### checkGzoomVer.groovy");
/*
 * Controllo il pattern l'url per determinare se si tratta del GzoomLegacy o del Gzoom2
 * in base alla presenza o meno della stringa "gzoom2" 
 */
//Debug.log(parameters._CLIENT_REFERER_.toString());
ref = parameters._CLIENT_REFERER_;
if(ref != null && !ref.isEmpty())
{
  gzoom2 = ref.indexOf("gzoom2");
  if(gzoom2 > 0) 
	{
		Debug.log("gzoom2");
		parameters.put("noInfoToolbar", "true");
	}
}
