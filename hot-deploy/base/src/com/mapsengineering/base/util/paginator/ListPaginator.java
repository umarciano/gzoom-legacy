package com.mapsengineering.base.util.paginator;

import java.util.List;
import java.util.Map;

public class ListPaginator {

    public static enum E {
        list, listSize
    }

    private List<Map<String, Object>> list;

    public ListPaginator(List<Map<String, Object>> list) {
        setList(list);
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public int getListSize() {
        return this.list != null ? this.list.size() : 0;
    }

    public List<Map<String, Object>> paginate(PaginatorParams params) {
        if (list == null)
            return null;
        int fromIndex = params.getViewIndex() * params.getViewSize();
        if (fromIndex >= list.size()) {
            // In alternativa si potrebbe calcolare il viewIndex e fromIndex dell'ultima pagina.
            params.setViewIndex(0);
            fromIndex = 0;
        }
        int toIndex = fromIndex + params.getViewSize();
        if (toIndex > list.size())
            toIndex = list.size();
        return list.subList(fromIndex, toIndex);
    }
}
