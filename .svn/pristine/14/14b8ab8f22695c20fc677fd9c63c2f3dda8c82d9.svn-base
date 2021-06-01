import java.io.*;
import org.ofbiz.base.util.*;
import org.ofbiz.party.content.PartyContentWrapper;

partyContent = PartyContentWrapper.getFirstPartyContentByType(parameters.partyId, null, "IMAGE", delegator);
if (UtilValidate.isNotEmpty(partyContent)) {
    content = partyContent.getRelatedOneCache("Content");
    if (UtilValidate.isNotEmpty(content)) {
        dataResource = content.getRelatedOneCache("DataResource");
        if (UtilValidate.isNotEmpty(dataResource)) {
            fileName = dataResource.objectInfo;
            if (UtilValidate.isNotEmpty(fileName)) {
                file = new File(fileName);
                if (file.exists()) {
                    context.contentId = content.contentId;
                }
            }
        }
    }
}
