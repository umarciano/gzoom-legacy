package com.mapsengineering.base.entity;

import java.io.IOException;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.service.DispatchContext;

import com.mapsengineering.base.util.OfbizServiceContext;

public class FkViolationServices {

    /**
     * OFBiz service gzFindFkViolations
     * @param dctx
     * @param context
     * @return
     * @throws GeneralException
     */
    public static Map<String, Object> find(DispatchContext dctx, Map<String, Object> context) throws GeneralException {
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
            new FkViolationFinder(ctx.getDelegator(), (String)context.get("groupName"), new FkViolationFileHandler()).find();
            return ctx.getResult();
        } finally {
            try {
                ctx.close();
            } catch (IOException e) {
                throw new GeneralException(e);
            }
        }
    }
}
