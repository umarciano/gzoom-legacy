package com.mapsengineering.base.services.async;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.LoginWorker;

public final class AsyncJobEvents {

    private static final String MODULE = AsyncJobEvents.class.getName();

    private AsyncJobEvents() {
        // empty
    }

    public static String runAsyncJob(HttpServletRequest request, HttpServletResponse response) throws IOException, GeneralException {
        Map<String, Object> parameters = getCombinedMap(request);
        String serviceName = (String)parameters.get("jobServiceName");
        LocalDispatcher dispatcher = (LocalDispatcher)parameters.get("dispatcher");
        DispatchContext dctx = dispatcher.getDispatchContext();
        Map<String, Object> serviceContext = dctx.makeValidContext(serviceName, ModelService.IN_PARAM, parameters);

        // Create the job
        AsyncJobOfbizService job = new AsyncJobOfbizService(dispatcher, serviceName, serviceContext);
        Integer priority = AsyncJobUtil.getInteger(parameters.get("jobPriority"), null);
        if (priority != null) {
            job.setPriority(priority);
        }
        // Run the service async
        AsyncJobManager.submit(job);

        // Redirect to the waiting page
        String params = "?saveView=N&jobId=" + job.getJobId() + "&externalLoginKey=" + LoginWorker.getExternalLoginKey(request);
        response.sendRedirect("waitAsyncJob" + params);

        return "success";
    }

    public static String waitAsyncJob(HttpServletRequest request, HttpServletResponse response) throws IOException, GeneralException {
        Map<String, Object> parameters = getCombinedMap(request);
        Locale locale = (Locale)parameters.get("locale");
        String jobId = (String)parameters.get("jobId");

        Debug.logInfo("waiting job " + jobId, MODULE);

        AsyncJob job = getJob(request, jobId, locale);
        if (job == null) {
            return "error";
        }

        Map<String, Object> progress = job.getProgress();
        request.setAttribute("job", job);
        request.setAttribute("progress", progress);
        if (job.isRunning()) {
            final String waitTitleMessage = UtilProperties.getMessage("BaseUiLabels", job.isQueued() ? "BaseAsyncJobQueued" : "BaseAsyncJobRunning", locale);
            request.setAttribute("waitTitleMessage", waitTitleMessage);
            return "success";
        }

        if (ServiceUtil.isSuccess(progress)) {
            String contentId = (String)progress.get("contentId");
            if (UtilValidate.isNotEmpty(contentId)) {
                Debug.logInfo("contentId=" + contentId, MODULE);
                String params = "?saveView=N&contentId=" + contentId + "&externalLoginKey=" + LoginWorker.getExternalLoginKey(request);;
                response.sendRedirect("streamAsyncJob" + params);
                return "complete";
            }
        }

        Debug.logInfo("result=" + progress, MODULE);
        String params = "?saveView=N&jobId=" + jobId + "&externalLoginKey=" + LoginWorker.getExternalLoginKey(request);
        response.sendRedirect("resultAsyncJob" + params);

        return "complete";
    }

    public static String stopAsyncJob(HttpServletRequest request, HttpServletResponse response) throws GeneralException {
        Map<String, Object> parameters = getCombinedMap(request);
        Locale locale = (Locale)parameters.get("locale");
        String jobId = (String)parameters.get("jobId");

        Debug.logInfo("stop job " + jobId, MODULE);

        AsyncJob job = getJob(request, jobId, locale);
        if (job == null) {
            return "error";
        }

        job.requestInterrupt();

        return "success";
    }

    public static String resultAsyncJob(HttpServletRequest request, HttpServletResponse response) throws GeneralException {
        Map<String, Object> parameters = getCombinedMap(request);
        Locale locale = (Locale)parameters.get("locale");
        String jobId = (String)parameters.get("jobId");

        Debug.logInfo("result job " + jobId, MODULE);

        AsyncJob job = getJob(request, jobId, locale);
        if (job == null) {
            return "error";
        }

        Map<String, Object> result = job.getProgress();
        Debug.logInfo("result=" + result, MODULE);

        if (!ServiceUtil.isSuccess(result)) {
            String errorMessage = ServiceUtil.getErrorMessage(result);
            Debug.logInfo("error=" + errorMessage, MODULE);
            ServiceUtil.setMessages(request, errorMessage, null, null);
            return "error";
        }

        request.setAttribute("jobServiceResult", result);
        return "success";
    }

    private static Map<String, Object> getCombinedMap(HttpServletRequest request) {
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        if (UtilValidate.isEmpty(parameters.get("locale"))) {
            parameters.put("locale", UtilHttp.getLocale(request));
        }
        if (UtilValidate.isEmpty(parameters.get("timeZone"))) {
            parameters.put("timeZone", UtilHttp.getTimeZone(request));
        }
        return parameters;
    }

    private static AsyncJob getJob(HttpServletRequest request, String jobId, Locale locale) throws GeneralException {
        AsyncJob job = AsyncJobManager.get(jobId);
        if (job == null) {
            ServiceUtil.setMessages(request, UtilProperties.getMessage("BaseUiLabels", "BaseErrorJobNotFound", locale), null, null);
            return null;
        }
        if (!hasPermission(request, job)) {
            ServiceUtil.setMessages(request, UtilProperties.getMessage("BaseUiLabels", "BaseErrorJobAccessDenied", locale), null, null);
            return null;
        }
        return job;
    }

    private static boolean hasPermission(HttpServletRequest request, AsyncJob job) {
        // The full admin can do all.
        Security security = (Security)request.getAttribute("security");
        GenericValue sessionUserLogin = (GenericValue)request.getSession().getAttribute("userLogin");
        if (security != null && security.hasPermission("BASE_ASYNCJOB_ADMIN", sessionUserLogin)) {
            return true;
        }
        // The owner of the job can do all.
        Map<String, Object> jobContext = job.getContext();
        if (jobContext != null) {
            GenericValue jobUserLogin = (GenericValue)jobContext.get("userLogin");
            if (jobUserLogin != null && jobUserLogin.equals(sessionUserLogin)) {
                return true;
            }
        }
        return false;
    }
}
