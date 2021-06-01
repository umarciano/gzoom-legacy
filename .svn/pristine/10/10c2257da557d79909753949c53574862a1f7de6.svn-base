package com.mapsengineering.accountingext.util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;

/**
 * Base CustomMethod Calculator Util
 *
 */
public class BaseCustomMethodCalculatorUtil {

    public static final String DUMMY_A = "A";

    public static final String AMOUNT = "AMOUNT";
    public static final String MEDIA = "MEDIA";
    public static final String SUM_AMOUNT = "SUM_AMOUNT";
    public static final String COUNT_AMOUNT = "COUNT_AMOUNT";

    private Map<String, ? extends Object> context;

    private Delegator delegator;

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public BaseCustomMethodCalculatorUtil(Delegator delegator, Map<String, ? extends Object> context) {
        this.context = context;
        this.delegator = delegator;
    }

    /**
     * 
     * @return context.get("A")
     * @throws GenericEntityException
     * @throws SQLException 
     * @throws IOException 
     */
    public Double execute() throws GenericEntityException, SQLException, IOException {
        return new Double((Double)context.get(DUMMY_A));
    }

    @SuppressWarnings("unchecked")
    protected MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = (MapContext<String, Object>)MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }

    /**
     * @return the delegator
     */
    public Delegator getDelegator() {
        return delegator;
    }

    /**
     * @param delegator the delegator to set
     */
    public void setDelegator(Delegator delegator) {
        this.delegator = delegator;
    }

}
