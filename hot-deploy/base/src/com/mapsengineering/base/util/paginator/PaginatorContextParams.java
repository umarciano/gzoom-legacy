package com.mapsengineering.base.util.paginator;

import java.util.Map;

public class PaginatorContextParams extends PaginatorParams {

    public static enum E {
        viewIndex, viewSize
    }

    public PaginatorContextParams(Map<String, ? extends Object> env, Integer defViewSize) {
        super(env, null, E.viewIndex.name(), E.viewSize.name(), null, defViewSize);
    }
}
