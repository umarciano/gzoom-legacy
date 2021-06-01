import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

birtParameters = request.getAttribute("birtParameters");
birtParameters.put("request", request);

return "success";