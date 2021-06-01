package com.mapsengineering.base.services.async;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.service.ModelService;

public class AsyncJobFromContent extends AsyncJob {

    private enum E {
        UserLogin, userLogin, userLoginId, contentName, createdDate
    }

    private final GenericValue content;

    public AsyncJobFromContent(GenericValue content) throws GenericEntityException {
        super(AsyncJobIdGenerator.getIdFromContent(content));
        this.content = content;
        init();
    }

    public GenericValue getContent() {
        return content;
    }

    @Override
    protected void callJob() throws Exception {
        // do nothing
    }

    protected String getContentId() {
        return content.getString(AsyncJobUtil.CONTENT_ID_FIELD);
    }

    protected String getUserLoginId() {
        return content.getString(ModelEntity.CREATE_USER_LOGIN_ID_FIELD);
    }

    protected void initAsyncJobFromContent() throws GenericEntityException {
        init();
    }

    private final void init() throws GenericEntityException {
        // Init as already completed with results
        Map<String, Object> result = getResult();
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put(AsyncJobUtil.CONTENT_ID_FIELD, getContentId());
        setJobName(content.getString(E.contentName.name()));
        setCreatedDate(content.getTimestamp(E.createdDate.name()));
        setPriority(Integer.MAX_VALUE);
        // Persistent job never expires 
        getConfig().setKeepAliveTimeout(0L);
        // Populate context
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(AsyncJobUtil.ASYNC_JOB_PARAM_NAME, this);
        context.put(E.userLogin.name(), loadUserLogin());
        setContext(context);
    }

    private GenericValue loadUserLogin() throws GenericEntityException {
        return content.getDelegator().findOne(E.UserLogin.name(), true, E.userLoginId.name(), getUserLoginId());
    }
}
