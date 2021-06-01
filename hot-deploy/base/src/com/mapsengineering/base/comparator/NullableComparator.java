package com.mapsengineering.base.comparator;

import java.util.Comparator;

public abstract class NullableComparator<T> implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        if (o1 == null) {
            return o2 == null ? 0 : -1;
        }
        if (o2 == null) {
            return 1;
        }
        return compareNotNull(o1, o2);
    }

    /**
     * Called if o1 and o2 are both not null.
     * @param o1 right value
     * @param o2 left value
     * @return o1 - o2
     */
    protected abstract int compareNotNull(T o1, T o2);
}
