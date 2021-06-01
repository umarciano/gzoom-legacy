package com.mapsengineering.base.util.paginator;

import java.util.Map;

import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

public class PaginatorParams {

    private Map<String, Object> env;
    private String paginatorNumberName;
    private String viewIndexName;
    private String viewSizeName;

    private String paginatorNumber;
    private int viewIndex;
    private int viewSize;

    public PaginatorParams(Map<String, ? extends Object> env, String paginatorNumberName, String viewIndexName, String viewSizeName, Object defPaginatorNumber, Integer defViewSize) {
        this.env = UtilGenerics.toMap(env);
        //
        this.paginatorNumberName = UtilValidate.isNotEmpty(paginatorNumberName) ? paginatorNumberName : null;
        Object paginatorNumber = this.paginatorNumberName != null ? this.env.get(this.paginatorNumberName) : null;
        paginatorNumber = paginatorNumber != null ? paginatorNumber : defPaginatorNumber;
        this.paginatorNumber = paginatorNumber != null ? paginatorNumber.toString() : null;
        //
        this.viewIndexName = this.paginatorNumber != null ? viewIndexName + '_' + this.paginatorNumber : viewIndexName;
        Integer viewIndexInt = UtilMisc.toIntegerObject(this.env.get(this.viewIndexName));
        viewIndex = (viewIndexInt != null && viewIndexInt >= 0) ? viewIndexInt.intValue() : 0;
        //
        this.viewSizeName = this.paginatorNumber != null ? viewSizeName + '_' + this.paginatorNumber : viewSizeName;
        Integer viewSizeInt = UtilMisc.toIntegerObject(this.env.get(this.viewSizeName));
        viewSize = (viewSizeInt != null && viewSizeInt >= 0) ? viewSizeInt.intValue() : getDefViewSize(defViewSize);
    }

    public String getPaginatorNumber() {
        return paginatorNumber;
    }

    public int getViewIndex() {
        return viewIndex;
    }

    public void setViewIndex(int viewIndex) {
        this.viewIndex = viewIndex;
    }

    public int getViewSize() {
        return viewSize;
    }

    public void put() {
        put(env, paginatorNumberName, viewIndexName, viewSizeName);
    }

    public void put(Map<String, ? extends Object> env, String paginatorNumberName, String viewIndexName, String viewSizeName) {
        Map<String, Object> envMap = UtilGenerics.toMap(env);
        if (paginatorNumberName != null)
            envMap.put(paginatorNumberName, paginatorNumber);
        envMap.put(viewIndexName, viewIndex);
        envMap.put(viewSizeName, viewSize);
    }

    private int getDefViewSize(Integer defViewSize) {
        if (defViewSize != null)
            return defViewSize.intValue() >= 0 ? defViewSize.intValue() : Integer.MAX_VALUE;
        return UtilMisc.toInteger(UtilProperties.getPropertyValue("widget", "widget.form.defaultViewSize"));
    }
}
