package com.mapsengineering.workeffortext.scorecard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;

/**
 * Search Child Without Score (WorkEffortMeasureChildNoScore)
 *
 */
public class ChildWithoutScoreFinder extends GenericChildFinder{
	private String organizationId;
	
	private static final String queryChildWithoutScore = "sql/score/queryChildWithoutScore.sql.ftl";
    
    /**
     * Constructor
     * @param delegator
     */
    public ChildWithoutScoreFinder(Delegator delegator, String organizationId) {
        super(delegator);
        this.organizationId = organizationId;
    }

    /**
     * Query di estrazione child
     * 
     * @return
     * @throws GenericEntityException
     */
    public List<GenericValue> findChildWithoutScore(String scoreCard, Date thruDate, String scoreValueType, String rootHierarchyAssocTypeId) throws GenericEntityException {
        List<GenericValue> rowList = FastList.newInstance();

        try {
            JdbcQueryIterator queryChildWithoutScoreList = new FtlQuery(getDelegator(), queryChildWithoutScore, mapContextUpdate(scoreCard, thruDate, scoreValueType, rootHierarchyAssocTypeId)).iterate();
            try {
                while (queryChildWithoutScoreList.hasNext()) {
                    ResultSet ele = queryChildWithoutScoreList.next();
                    GenericValue gv = getDelegator().makeValue("WorkEffortAssocNoScore");
                    if (ele != null) {
                    	gv.put("workEffortId", ele.getString("WORK_EFFORT_ID"));
                    	gv.put("workEffortParentId", ele.getString("WORK_EFFORT_PARENT_ID"));
                    	gv.put("workEffortParentIdFrom", ele.getString("WORK_EFFORT_PARENT_ID_FROM"));
                    	gv.put("workEffortIdFrom", ele.getString("WORK_EFFORT_ID_FROM"));
                    	gv.put("workEffortIdTo", ele.getString("WORK_EFFORT_ID_TO"));
                    	gv.put("assocWeight", ele.getDouble("ASSOC_WEIGHT"));
                    	gv.put("workEffortAssocTypeId", ele.getString("WORK_EFFORT_ASSOC_TYPE_ID"));
                    	gv.put("hierarchyAssocTypeId", ele.getString("HIERARCHY_ASSOC_TYPE_ID"));
                    	gv.put("fromDate", ele.getTimestamp("FROM_DATE"));
                    	gv.put("thruDate", ele.getTimestamp("THRU_DATE"));
                    	gv.put("weightKpi", ele.getDouble("WEIGHT_KPI"));
                    	gv.put("weightSons", ele.getDouble("WEIGHT_SONS"));
                    	gv.put("weightReview", ele.getDouble("WEIGHT_REVIEW"));
                    }
                    rowList.add(gv);
                }
            } finally {
            	queryChildWithoutScoreList.close();
            }

        } catch (Exception e) {
        }
        return rowList;
    }
    
    /**
     * prepara il contesto
     * @param scoreCard
     * @param thruDate
     * @param scoreValueType
     * @param rootHierarchyAssocTypeId
     * @return
     * @throws SQLException
     */
    protected Map<String, Object> mapContextUpdate(String scoreCard, Date thruDate, String scoreValueType, String rootHierarchyAssocTypeId) throws SQLException {
        Map<String, Object> mapContext = FastMap.newInstance();
        mapContext.put("scoreCard", scoreCard);
        mapContext.put("fromDate", UtilDateTime.getYearStart(new Timestamp(thruDate.getTime())));
        mapContext.put("thruDate", UtilDateTime.toTimestamp(thruDate));
        mapContext.put("scoreValueType", scoreValueType);
        mapContext.put("rootHierarchyAssocTypeId", rootHierarchyAssocTypeId);
        mapContext.put("organizationId", organizationId);
        return mapContext;
    }
}
