package com.mapsengineering.base.test.entity;

import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.config.DatasourceInfo;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelRelation;

import com.mapsengineering.base.entity.FkViolationFinder;
import com.mapsengineering.base.entity.FkViolationQuery;
import com.mapsengineering.base.entity.FkViolationSqlQuery;
import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;

public class TestFkViolation extends BaseTestCase {

    private static final String MODULE = TestFkViolation.class.getName();
    private static final char NL = '\n';

    public void testSqlQuery() {
        try {
            final String mainEntityName = "WorkEffort";
            final String relEntityName = "PartyRole";
            final String relName = relEntityName;
            final DatasourceInfo datasourceInfo = EntityConfigUtil.getDatasourceInfo(delegator.getEntityHelperName(mainEntityName));
            final String mainTableName = delegator.getModelEntity(mainEntityName).getTableName(datasourceInfo);
            final String relTableName = delegator.getModelEntity(relEntityName).getTableName(datasourceInfo);
            final String strQueryExpected = "-- WE_ORG_UNIT -- " + relName + NL //
                    + "select "+ mainTableName +".* from " + mainTableName  + NL //
                    + "-- update " + mainTableName + " set ORG_UNIT_ID=null, ORG_UNIT_ROLE_TYPE_ID=null" + NL //
                    + "-- delete from " + mainTableName +  NL //
                    + "where (" + NL //
                    + "    "+ mainTableName +".ORG_UNIT_ID is not null" + NL //
                    + "    or "+mainTableName +".ORG_UNIT_ROLE_TYPE_ID is not null" + NL //
                    + "  ) and not exists (" + NL //
                    + "    select * from " + relTableName + NL //
                    + "    where "+ mainTableName+ ".ORG_UNIT_ID="+relTableName +".PARTY_ID" + NL //
                    + "      and "+mainTableName+".ORG_UNIT_ROLE_TYPE_ID="+relTableName +".ROLE_TYPE_ID" + NL //
                    + "  )" + NL //
                    + "order by "+mainTableName+".WORK_EFFORT_ID" + NL;

            ModelEntity modelEntity = delegator.getModelEntity(mainEntityName);
            ModelRelation modelRelation = modelEntity.getRelation(relName);
            FkViolationSqlQuery query = new FkViolationSqlQuery(delegator.getDelegatorName(), modelEntity, modelRelation);
            String strQuery = query.getQuery();
            assertNotNull(strQuery);
            Debug.log("query=\n" + strQuery);
            assertEquals(strQueryExpected, strQuery);
        } catch (GenericEntityException e) {
            Debug.logError(e, MODULE);
            fail(e.toString());
        }
    }

    public void testDynamicViewEntity() {
        try {
            final String mainEntityName = "Content";
            final String relEntityName = "DataResource";
            final String relName = "Template" + relEntityName;
            ModelEntity modelEntity = delegator.getModelEntity(mainEntityName);
            ModelRelation modelRelation = modelEntity.getRelation(relName);
            FkViolationQuery query = new FkViolationQuery(delegator, modelEntity, modelRelation);
            assertNotNull(query.getDynamicView());
            String xmlQuery = query.getDynamicView().getViewXml(query.getDynamicView().getEntityName());
            assertNotNull(xmlQuery);
            Debug.log("query=\n" + xmlQuery);
            Debug.log("condition=\n" + query.getCondition());
            Debug.log("orderBy=\n" + query.getOrderBy());
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            fail(e.toString());
        }
    }

    public void testFinder() {
        try {
            new TransactionRunner(MODULE, new TransactionItem() {
                @Override
                public void run() throws Exception {
                    new FkViolationFinder(delegator, null, null).find();
                }
            }).execute().rethrow();
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            fail(e.toString());
        }
    }
}
