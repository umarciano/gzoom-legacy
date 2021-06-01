package com.mapsengineering.base.birt.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.birt.report.engine.api.IEngineTask;
import org.eclipse.birt.report.engine.api.IProgressMonitor;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;

import com.mapsengineering.base.services.async.AsyncJobCallback;

public class BirtServiceProgress implements IProgressMonitor, AsyncJobCallback {

    private static final String MODULE = BirtServiceProgress.class.getName();

    private final BirtService birtService;
    private final AtomicReference<IEngineTask> task;
    private final AtomicInteger queryCount;
    private final AtomicInteger pageCount;

    public BirtServiceProgress(BirtService birtService) {
        this.birtService = birtService;
        task = new AtomicReference<IEngineTask>(null);
        queryCount = new AtomicInteger(0);
        pageCount = new AtomicInteger(0);
    }

    public IEngineTask getTask() {
        return task.get();
    }

    public void setTask(IEngineTask task) {
        this.task.set(task);
        queryCount.set(0);
        pageCount.set(0);
    }

    @Override
    public void onAsyncJobProgress() {
        IEngineTask task = this.task.get();
        if (task != null) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("queryCount", queryCount);
            result.put("pageCount", pageCount);
            String progressMessage = getBirtTaskMessage(task, result);
            result.put("progressMessage", progressMessage);
            checkInterrupted(task, !birtService.getJob().updateResult(result), progressMessage);
        }
    }

    @Override
    public void onProgress(int type, int value) {
        switch (type) {
        case START_QUERY:
            if (Debug.verboseOn()) {
                Debug.logVerbose("START_QUERY " + value, MODULE);
            }
            break;
        case END_QUERY:
            if (Debug.verboseOn()) {
                Debug.logVerbose("END_QUERY " + value, MODULE);
            }
            queryCount.incrementAndGet();
            checkInterrupted(task.get(), birtService.getJob().isInterrupted(), null);
            break;
        case START_PAGE:
            if (Debug.verboseOn()) {
                Debug.logVerbose("START_PAGE " + value, MODULE);
            }
            break;
        case END_PAGE:
            if (Debug.verboseOn()) {
                Debug.logVerbose("END_PAGE " + value, MODULE);
            }
            pageCount.incrementAndGet();
            checkInterrupted(task.get(), birtService.getJob().isInterrupted(), null);
            break;
        }
    }

    private String getBirtTaskMessage(IEngineTask task, Map<String, Object> context) {
        if (task != null) {
            switch (task.getTaskType()) {
            case IEngineTask.TASK_RUN:
                return UtilProperties.getMessage("BaseUiLabels", "BaseBirtTaskRun", context, birtService.getCtx().getLocale());
            case IEngineTask.TASK_RENDER:
                return UtilProperties.getMessage("BaseUiLabels", "BaseBirtTaskRender", context, birtService.getCtx().getLocale());
            }
        }
        return null;
    }

    private void checkInterrupted(IEngineTask task, boolean interrupted, String progressMessage) {
        if (interrupted && task != null && !task.getCancelFlag()) {
            Debug.log("Cancel task, progressMessage: " + progressMessage, MODULE);
            task.cancel();
        }
    }
}
