package com.mapsengineering.base.entity;

import java.util.Iterator;
import java.util.List;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelRelation;
import org.ofbiz.entity.util.EntityListIterator;

/**
 * Builds a DynamicViewEntity to find FK violations.
 * @author sivi
 *
 */
public class FkViolationQuery {

    private static final String MAIN_ALIAS = "A";
    private static final String RELATED_ALIAS = "B";

    private final Delegator delegator;
    private final ModelEntity modelEntity;
    private final ModelRelation modelRelation;
    private DynamicViewEntity view;
    private EntityCondition conditions;
    private List<String> orderBy;

    /**
     * 
     * @param delegator Delegator
     * @param modelEntity ModelEntity
     * @param modelRelation ModelRelation
     */
    public FkViolationQuery(Delegator delegator, ModelEntity modelEntity, ModelRelation modelRelation) {
        this.delegator = delegator;
        this.modelEntity = modelEntity;
        this.modelRelation = modelRelation;
        init();
    }

    /**
     * 
     * @return DynamicViewEntity
     */
    public DynamicViewEntity getDynamicView() {
        return view;
    }

    /**
     * 
     * @return EntityCondition
     */
    public EntityCondition getCondition() {
        return conditions;
    }

    /**
     * 
     * @return order by fields
     */
    public List<String> getOrderBy() {
        return orderBy;
    }

    /**
     * Performs a find of the violations.
     * @return
     * @throws GenericEntityException
     */
    public EntityListIterator find() throws GenericEntityException {
        return view != null ? delegator.findListIteratorByCondition(view, conditions, null, null, orderBy, null) : null;
    }

    private void init() {
        if (modelRelation.getKeyMapsSize() > 0) {
            view = new DynamicViewEntity();
            view.setEntityName(modelEntity.getEntityName());
            view.addMemberEntity(MAIN_ALIAS, modelEntity.getEntityName());
            view.addMemberEntity(RELATED_ALIAS, modelRelation.getRelEntityName());
            view.addAliasAll(MAIN_ALIAS, "");
            parseKeyMaps();
            view.addViewLink(MAIN_ALIAS, RELATED_ALIAS, true, modelRelation.getKeyMapsClone());
            orderBy = modelEntity.getPkFieldNames();
        }
    }

    private void parseKeyMaps() {
        EntityCondition mainCond = null;
        EntityCondition relCond = null;

        for (Iterator<ModelKeyMap> it = modelRelation.getKeyMapsIterator(); it.hasNext();) {
            ModelKeyMap keyMap = it.next();

            String relFieldAlias = getRelFieldAlias(keyMap.getRelFieldName());
            view.addAlias(RELATED_ALIAS, relFieldAlias, keyMap.getRelFieldName(), null, Boolean.FALSE, null, null, null);

            EntityCondition cond = EntityCondition.makeCondition(keyMap.getFieldName(), EntityOperator.NOT_EQUAL, null);
            mainCond = mainCond == null ? cond : EntityCondition.makeCondition(mainCond, EntityOperator.OR, cond);

            cond = EntityCondition.makeCondition(relFieldAlias, EntityOperator.EQUALS, null);
            relCond = relCond == null ? cond : EntityCondition.makeCondition(relCond, EntityOperator.AND, cond);
        }

        conditions = EntityCondition.makeCondition(mainCond, EntityOperator.AND, relCond);
    }

    private String getRelFieldAlias(String relFieldName) {
        StringBuffer buffer = new StringBuffer(RELATED_ALIAS);
        buffer.append('_').append(relFieldName);
        return buffer.toString();
    }
}
