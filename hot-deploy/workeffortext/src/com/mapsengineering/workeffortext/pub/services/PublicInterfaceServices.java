package com.mapsengineering.workeffortext.pub.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.util.OfbizServiceContext;
import com.mapsengineering.workeffortext.pub.data.PubGlAccountData;
import com.mapsengineering.workeffortext.pub.data.PubGlAccountHistoryData;
import com.mapsengineering.workeffortext.pub.data.PubWorkEffortData;

public class PublicInterfaceServices {

    private static final String MODULE = PublicInterfaceServices.class.getName();

    public static Map<String, Object> getWorkEffortData(DispatchContext dctx, Map<String, Object> context) throws Exception {
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
            Debug.log("parameters=" + context, MODULE);
            Timestamp periodDate = (Timestamp)ctx.get("d");
            PublicInterface obj = PublicInterfaceFactory.create();
            List<PubWorkEffortData> list;
            List<String> idList = UtilGenerics.toList(ctx.get("id"));
            if (UtilValidate.isEmpty(idList)) {
                list = obj.getWorkEffortDataByType(periodDate, (String)ctx.get("t"), (String)ctx.get("wt"));
            } else {
                list = new ArrayList<PubWorkEffortData>();
                for (String sourceReferenceId : idList) {
                    PubWorkEffortData data = obj.getWorkEffortData((Timestamp)ctx.get("d"), (String)ctx.get("t"), sourceReferenceId);
                    list.add(data);
                }
            }
            ctx.getResult().put("periodDate", periodDate);
            ctx.getResult().put("list", list);
            return ctx.getResult("publicGetWorkEffortData");
        } catch (PublicInterfaceException ex) {
            return ServiceUtil.returnError(ex.getMessage());
        } finally {
            ctx.close();
        }
    }

    public static Map<String, Object> getGlAccountData(DispatchContext dctx, Map<String, Object> context) throws Exception {
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
            Debug.log("parameters=" + context, MODULE);
            Timestamp periodDate = (Timestamp)ctx.get("d");
            PublicInterface obj = PublicInterfaceFactory.create();
            List<PubGlAccountData> list = obj.getGlAccountData(periodDate, (String)ctx.get("t"), (String)ctx.get("id"));
            ctx.getResult().put("periodDate", periodDate);
            ctx.getResult().put("list", list);
            return ctx.getResult("publicGetGlAccountData");
        } catch (PublicInterfaceException ex) {
            return ServiceUtil.returnError(ex.getMessage());
        } finally {
            ctx.close();
        }
    }

    public static Map<String, Object> getGlAccountHistoryData(DispatchContext dctx, Map<String, Object> context) throws Exception {
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
            Debug.log("parameters=" + context, MODULE);
            Timestamp periodDate = (Timestamp)ctx.get("d");
            PublicInterface obj = PublicInterfaceFactory.create();
            PubGlAccountHistoryData data = obj.getGlAccountHistoryData(periodDate, (String)ctx.get("t"), (String)ctx.get("wid"), (String)ctx.get("id"));
            ctx.getResult().put("periodDate", periodDate);
            ctx.getResult().put("data", data);
            return ctx.getResult("publicGetGlAccountHistoryData");
        } catch (PublicInterfaceException ex) {
            return ServiceUtil.returnError(ex.getMessage());
        } finally {
            ctx.close();
        }
    }
}
