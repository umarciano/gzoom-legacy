package com.mapsengineering.base.entity;

import java.util.Iterator;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelRelation;
import org.ofbiz.entity.model.ModelViewEntity;
import org.ofbiz.entity.util.EntityListIterator;

/**
 * Finds FK violations for OFBiz entities.
 * @author sivi
 *
 */
public class FkViolationFinder {

    private Delegator delegator;
    private String groupName;
    private FkViolationBaseHandler handler;

    /**
     * 
     * @param delegator Delegator
     * @param groupName optional, defaults to "org.ofbiz"
     * @param handler optional, by default prints violations to standard log.
     */
    public FkViolationFinder(Delegator delegator, String groupName, FkViolationBaseHandler handler) {
        this.delegator = delegator;
        this.groupName = groupName != null ? groupName : "org.ofbiz";
        this.handler = handler != null ? handler : new FkViolationBaseHandler(delegator.getDelegatorName());
    }

    /**
     * Finds violations for all entities.
     * @throws GeneralException
     */
    public void find() throws GeneralException {
        Map<String, ModelEntity> modelEntities = delegator.getModelEntityMapByGroup(groupName);
        for (Map.Entry<String, ModelEntity> entry : modelEntities.entrySet()) {
            findByEntity(entry.getValue());
        }
    }

    /**
     * Finds violations for all the relations of an entity.
     * @param modelEntity
     * @throws GeneralException
     */
    public void findByEntity(ModelEntity modelEntity) throws GeneralException {
        Iterator<ModelRelation> relations = modelEntity.getRelationsIterator();
        while (relations.hasNext()) {
            findByRelation(modelEntity, relations.next());
        }
    }

    /**
     * Finds violations for a relation of an entity and pass it to the handler.
     * @param modelEntity
     * @param modelRelation
     * @throws GeneralException
     */
    public void findByRelation(ModelEntity modelEntity, ModelRelation modelRelation) throws GeneralException {
        if (!(modelEntity instanceof ModelViewEntity) && "one".equals(modelRelation.getType())) {
            FkViolationQuery query = new FkViolationQuery(delegator, modelEntity, modelRelation);
            EntityListIterator listIt = query.find();
            if (listIt != null) {
                try {
                    handler.accept(modelEntity, modelRelation, listIt);
                } finally {
                    listIt.close();
                }
            }
        }
    }
}
