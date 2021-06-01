package com.mapsengineering.base.services.async;

import java.io.IOException;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.util.OfbizServiceContext;

public class AsyncJobOfbizExecutor {

    private static final String MODULE = AsyncJobOfbizExecutor.class.getName();

    private OfbizServiceContext ctx;

    public static Map<String, Object> runSrv(DispatchContext dctx, Map<String, Object> context) throws IOException {
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
            AsyncJobOfbizExecutor obj = new AsyncJobOfbizExecutor(ctx);
            obj.run();
            return ctx.getResult();
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            return ServiceUtil.returnError(e.getMessage());
        } finally {
            ctx.close();
        }
    }

    public AsyncJobOfbizExecutor(OfbizServiceContext ctx) {
        this.ctx = ctx;
    }

    public void run() throws Exception {
        AsyncJobQueue queue = (AsyncJobQueue)ctx.get(AsyncJobUtil.ASYNC_QUEUE_PARAM_NAME);
        if (queue != null) {
            AsyncJob job = queue.poll();
            if (job != null) {
                try {
                    Debug.logInfo("Running job " + job.getJobId(), MODULE);
                    job.call();
                } finally {
                    queue.onExecutorTerminated(ctx.getDispatcher(), ctx);
                }
            }
        }
    }
}
