package com.mapsengineering.base.services.async;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.conversion.AbstractConverter;
import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

public class AsyncJobToMap extends AbstractConverter<AsyncJob, Map<String, Object>> {

    private enum E {
        jobId, jobName, priority, sequenceId, responseMessage, progressMessage //
        , userLogin, userLoginId, contentId, createdDate //
    }

    public AsyncJobToMap() {
        super(AsyncJob.class, Map.class);
    }

    @Override
    public Map<String, Object> convert(AsyncJob obj) throws ConversionException {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(E.jobId.name(), obj.getJobId());
        result.put(E.jobName.name(), obj.getJobName());
        result.put(E.createdDate.name(), obj.getCreatedDate());
        result.put(E.priority.name(), obj.getPriority());
        result.put(E.sequenceId.name(), obj.getSequenceId());
        Map<String, Object> progress = obj.getProgress();
        if (progress != null) {
            result.put(E.responseMessage.name(), progress.get(E.responseMessage.name()));
            result.put(E.progressMessage.name(), progress.get(E.progressMessage.name()));
            Object contentId = progress.get(E.contentId.name());
            if (UtilValidate.isNotEmpty(contentId)) {
                result.put(E.contentId.name(), contentId);
            }
        }
        Map<String, Object> context = obj.getContext();
        if (context != null) {
            GenericValue userLogin = (GenericValue)context.get(E.userLogin.name());
            if (userLogin != null) {
                result.put(E.userLogin.name(), userLogin);
                result.put(E.userLoginId.name(), userLogin.get(E.userLoginId.name()));
            }
        }
        return result;
    }
}
