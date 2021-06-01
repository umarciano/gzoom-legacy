package com.mapsengineering.workeffortext.widgets;

import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

final class IconContentHelper {

    static String getIconContentId(Delegator delegator, EntityCondition ec) {
        String toReturn = null;
        try {
            EntityListIterator eli = delegator.find("UomRangeValues", ec, null, null, null, null);
            if (UtilValidate.isNotEmpty(eli)) {
                List<GenericValue> values = eli.getCompleteList();
                eli.close();
                GenericValue gv = EntityUtil.getFirst(values);
                toReturn = gv.getString("iconContentId");
            }
        } catch (Exception e) {
            Debug.log("Errore ricerca iconContentId: " + e.getMessage());
        }
        return toReturn;
    }
}
