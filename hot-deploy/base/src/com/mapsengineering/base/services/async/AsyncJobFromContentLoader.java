package com.mapsengineering.base.services.async;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityListIterator;

import com.mapsengineering.base.services.async.AsyncJobCleaner.E;

public abstract class AsyncJobFromContentLoader {

    private final Delegator delegator;

    public AsyncJobFromContentLoader(Delegator delegator) {
        this.delegator = delegator;
    }

    protected void load() throws GenericEntityException {
        EntityCondition condition = EntityCondition.makeCondition(E.contentTypeId.name(), AsyncJobUtil.CONTENT_TYPE_TMP_ENCLOSE);
        List<String> orderBy = UtilMisc.toList(E.createdDate.name(), AsyncJobUtil.CONTENT_ID_FIELD);
        EntityListIterator it = delegator.find(E.Content.name(), condition, null, null, orderBy, null);
        try {
            GenericValue gv;
            while ((gv = it.next()) != null) {
                AsyncJob job = convert(gv);
                if (!load(job)) {
                    break;
                }
            }
        } finally {
            it.close();
        }
    }

    protected AsyncJob convert(GenericValue gv) throws GenericEntityException {
        return new AsyncJobFromContent(gv);
    }

    protected abstract boolean load(AsyncJob job) throws GenericEntityException;
}
