package com.mapsengineering.base.entity;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelRelation;
import org.ofbiz.entity.util.EntityListIterator;

/**
 * Default base handler of FK violations found, prints them to standard log.
 * @author sivi
 *
 */
public class FkViolationBaseHandler {

    private final String delegatorName;
    private int violationCount = 0;

    /**
     * Init by delegator name.
     * @param delegatorName
     */
    public FkViolationBaseHandler(String delegatorName) {
        this.delegatorName = delegatorName;
    }

    /**
     * Default initializer
     */
    public FkViolationBaseHandler() {
        this("default");
    }

    /**
     * 
     * @return optional delegator name
     */
    public String getDelegatorName() {
        return delegatorName;
    }

    /**
     * 
     * @return Number of violations found.
     */
    public int getViolationCount() {
        return violationCount;
    }

    /**
     * Accepts an entity iterator.
     * @param modelEntity ModelEntity
     * @param modelRelation ModelRelation
     * @param listIt EntityListIterator, the caller must close it
     * @throws GeneralException
     */
    public void accept(ModelEntity modelEntity, ModelRelation modelRelation, EntityListIterator listIt) throws GeneralException {
        violationCount = 0;
        do {
            GenericValue gv = listIt.next();
            if (gv == null)
                break;
            if (violationCount <= 0) {
                ++violationCount;
                iterateBegin(modelEntity, modelRelation);
            }
            iterate(modelEntity, modelRelation, gv);
        } while (true);
        if (violationCount > 0) {
            iterateEnd(modelEntity, modelRelation);
        }
    }

    /**
     * Called when iteration starts.
     * @param modelEntity ModelEntity
     * @param modelRelation ModelRelation
     */
    protected void iterateBegin(ModelEntity modelEntity, ModelRelation modelRelation) {
        Debug.log("relation: " + modelRelation.getCombinedName() + ", fkName: " + modelRelation.getFkName());
    }

    /**
     * Called when iteration ends.
     * @param modelEntity ModelEntity
     * @param modelRelation ModelRelation
     */
    protected void iterateEnd(ModelEntity modelEntity, ModelRelation modelRelation) {
        try {
            FkViolationSqlQuery sqlQuery = new FkViolationSqlQuery(delegatorName, modelEntity, modelRelation);
            String strSqlQuery = sqlQuery.getQuery();
            if (strSqlQuery != null) {
                Debug.log("generated SQL:\n" + strSqlQuery);
            }
        } catch (GenericEntityException e) {
            Debug.log(e);
        }
    }

    /**
     * Called for each GenericValue found with violations.
     * @param modelEntity ModelEntity
     * @param modelRelation ModelRelation
     * @param gv GenericValue
     */
    protected void iterate(ModelEntity modelEntity, ModelRelation modelRelation, GenericValue gv) {
        Debug.log("gv=" + gv);
    }
}
