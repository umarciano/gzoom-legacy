package com.mapsengineering.base.etl.load;

import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.etl.EtlException;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;

public abstract class LoadAbstract implements LoadInterface {

    private Map<String, Object> result = ServiceUtil.returnFailure();

    /* (non-Javadoc)
     * @see com.mapsengineering.base.etl.load.LoadInterface#execute(java.util.List, org.ofbiz.service.DispatchContext, java.util.Map)
     */
    public Map<String, Object> execute(final List<GenericValue> transformed, DispatchContext dctx, Map<String, Object> context) throws EtlException {
        setResult(ServiceUtil.returnSuccess());

        try {
            new TransactionRunner(getClass().getName(), new TransactionItem() {
                @Override
                public void run() throws Exception {
                    for (GenericValue genericValue : transformed) {
                        genericValue.create();
                    }
                }
            }).execute().rethrow();
        } catch (Exception e) {
            throw new EtlException(e);
        }

        return getResult();
    }

    /* (non-Javadoc)
     * @see com.mapsengineering.base.etl.load.LoadInterface#execute(java.util.List)
     */
    public Map<String, Object> execute(List<GenericValue> transformed) throws EtlException {
        return execute(transformed, null, null);
    }

    /**
     * @return
     */
    public Map<String, Object> getResult() {
        return result;
    }

    /**
     * @param result
     */
    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

}
