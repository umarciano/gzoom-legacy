package com.mapsengineering.workeffortext.services.rootcopy;

import java.util.Set;

import javolution.util.FastSet;

import com.mapsengineering.workeffortext.services.E;

/**
 * Return field list
 *
 */
public class WorkEffortRootReadFieldSelect {

    /**
     * Return field list
     * @return
     */
    public static Set<String> getFieldsToSelect() {
        Set<String> toSelect = FastSet.newInstance();
        toSelect.add(E.workEffortId.name());
        toSelect.add(E.currentStatusId.name());
        toSelect.add(E.workEffortTypeId.name());
        toSelect.add(E.workEffortName.name());
        return toSelect;
    }
}
