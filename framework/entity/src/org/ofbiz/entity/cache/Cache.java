/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.entity.cache;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.model.ModelEntity;

public class Cache {

    public static final String module = Cache.class.getName();

    protected EntityCache entityCache;
    protected EntityListCache entityListCache;
    protected EntityObjectCache entityObjectCache;

    protected String delegatorName;

    public Cache(String delegatorName) {
        this.delegatorName = delegatorName;
        entityCache = new EntityCache(delegatorName);
        entityObjectCache = new EntityObjectCache(delegatorName);
        entityListCache = new EntityListCache(delegatorName);
    }

    public void clear() {
        entityCache.clear();
        entityListCache.clear();
        entityObjectCache.clear();
    }

    public void remove(String entityName) {
        entityCache.remove(entityName);
        entityListCache.remove(entityName);
    }

    public GenericValue get(GenericPK pk) {
        return entityCache.get(pk);
    }

    public List<GenericValue> get(String entityName, EntityCondition condition, List<String> orderBy) {
        return entityListCache.get(entityName, condition, orderBy);
    }

    public <T> T get(String entityName, EntityCondition condition, String name) {
        return UtilGenerics.<T>cast(entityObjectCache.get(entityName, condition, name));
    }

    public List<GenericValue> put(String entityName, EntityCondition condition, List<String> orderBy, List<GenericValue> entities) {
        return entityListCache.put(entityName, condition, orderBy, entities);
    }

    public <T> T put(String entityName, EntityCondition condition, String name, T value) {
        return UtilGenerics.<T>cast(entityObjectCache.put(entityName, condition, name, value));
    }

    public GenericValue put(GenericValue entity) {
        GenericValue oldEntity = entityCache.put(entity.getPrimaryKey(), entity);
        if (entity.getModelEntity().getAutoClearCache()) {
            entityListCache.storeHook(entity);
            entityObjectCache.storeHook(entity);
        }
        return oldEntity;
    }

    public GenericValue put(GenericPK pk, GenericValue entity) {
        GenericValue oldEntity = entityCache.put(pk, entity);
        if (pk.getModelEntity().getAutoClearCache()) {
            entityListCache.storeHook(pk, entity);
            entityObjectCache.storeHook(pk, entity);
        }
        return oldEntity;
    }

    public List<GenericValue> remove(String entityName, EntityCondition condition, List<String> orderBy) {
        entityCache.remove(entityName, condition);
        entityObjectCache.remove(entityName, condition);
        return entityListCache.remove(entityName, condition, orderBy);
    }

    public void remove(String entityName, EntityCondition condition) {
        entityCache.remove(entityName, condition);
        entityListCache.remove(entityName, condition);
        entityObjectCache.remove(entityName, condition);
    }

    public <T> T remove(String entityName, EntityCondition condition, String name) {
        return UtilGenerics.<T>cast(entityObjectCache.remove(entityName, condition, name));
    }

    public GenericValue remove(GenericEntity entity) {
        if (Debug.verboseOn()) Debug.logVerbose("Cache remove GenericEntity: " + entity, module);
        GenericValue oldEntity = entityCache.remove(entity.getPrimaryKey());
        removeViews(entity.getPrimaryKey());

        if (oldEntity == null || oldEntity == GenericEntity.NULL_ENTITY || oldEntity == GenericValue.NULL_VALUE) {
            // Unable to find the old entity in the case, so we will create a version of the original entity to properly remove the items from the related caches
            GenericEntity origEntity = GenericEntity.createGenericEntity(entity);
            if (entity instanceof GenericValue) {
                origEntity.setFields(((GenericValue)entity).getOriginalDbValues());
            }
            entityListCache.storeHook(origEntity, null);
            entityObjectCache.storeHook(origEntity, null);

        } else {
            // Remove the old entity from the related caches
            entityListCache.storeHook(oldEntity, null);
            entityObjectCache.storeHook(oldEntity, null);
        }

        return oldEntity;
    }

    public GenericValue remove(GenericPK pk) {
        if (Debug.verboseOn()) Debug.logVerbose("Cache remove GenericPK: " + pk, module);
        GenericValue oldEntity = entityCache.remove(pk);
        entityListCache.storeHook(pk, null);
        entityObjectCache.storeHook(pk, null);
        return oldEntity;
    }
    
    protected void removeViews(GenericPK pk) {
        // Remove for all related views
        ModelEntity model = pk.getModelEntity();
        Iterator<String> it = model.getViewConvertorsIterator();
        while (it.hasNext()) {
            String targetEntityName = it.next();
            List<? extends Map<String, Object>> convertedList = model.convertToViewValues(targetEntityName, pk);
            if (convertedList != null) {
                for (Map<String, Object> convertedPK: convertedList) {
                    if (convertedPK != null && !convertedPK.isEmpty()) {
                        EntityCondition condition = EntityCondition.makeCondition(convertedPK);
                        entityListCache.remove(targetEntityName, condition);
//                        if (!removed)
//                            removeSubViews(targetEntityName, convertedPK);
                    }
                }
            }
        }
    }

//    protected void removeSubViews(String entityName, Map<String, Object> fields) {
//        ModelEntity modelEntity = this.getDelegator().getModelEntity(entityName);
//        GenericPK pk = GenericPK.create(modelEntity);
//        pk.setFields(fields);
//        removeViews(pk);
//    }
}
