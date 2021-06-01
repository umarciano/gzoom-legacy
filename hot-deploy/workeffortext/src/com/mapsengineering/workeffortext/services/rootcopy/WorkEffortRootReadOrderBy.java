package com.mapsengineering.workeffortext.services.rootcopy;

import java.util.List;

import javolution.util.FastList;

import com.mapsengineering.workeffortext.services.E;

/**
 * Field for order by 
 *
 */
public class WorkEffortRootReadOrderBy {

    /**
     * List of field for order by
     * @return
     */
    public static List<String> getOrderBy() {
        List<String> orderBy = FastList.newInstance();
        orderBy.add(E.workEffortTypeId.name());
        orderBy.add("sourceReferenceId");
        orderBy.add(E.workEffortId.name());
        return orderBy;
    }
}
