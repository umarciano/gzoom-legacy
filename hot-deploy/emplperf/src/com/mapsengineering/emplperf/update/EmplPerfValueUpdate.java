package com.mapsengineering.emplperf.update;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.util.JobLogger;

/**
 * Update workEffort note from previous for emplperf 
 *
 */
public abstract class EmplPerfValueUpdate {

    /**
     * Return key for log
     * @return
     */
    protected abstract List<String> getKey();
    
    /**
     * Return whether execute standardImport 
     * @return
     */
    protected abstract boolean executeStandardImport();
    
    /**
     * return entities to import
     * @return
     */
    protected abstract String getEntityListToImport();    


    /**
     * Update genericValue
     * @param jobLogger 
     * @return 
     */
    protected abstract JobLogger doAction(JobLogger jobLogger, Delegator delegator, GenericValue genericValue, Map<String, Object> context) throws GeneralException;

    /**
     * Return only the field to select
     * @return
     */
    protected abstract Set<String> getFieldsToSelect();
    
    /**
     * 
     * @return
     */
    protected abstract List<String> getOrderBy();
}
