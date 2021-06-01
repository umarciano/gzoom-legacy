package com.mapsengineering.base.util.paginator;

import java.util.Map;

public class PaginatorEventParams extends PaginatorParams {

    public enum E {
        PAGINATOR_NUMBER, VIEW_INDEX, VIEW_SIZE
    }

    public PaginatorEventParams(Map<String, ? extends Object> env, Object defPaginatorNumber, Integer defViewSize) {
        super(env, E.PAGINATOR_NUMBER.name(), E.VIEW_INDEX.name(), E.VIEW_SIZE.name(), defPaginatorNumber, defViewSize);
    }
}
