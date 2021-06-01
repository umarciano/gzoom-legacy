package com.mapsengineering.base.comparator;

public class GenericComparator<T> extends NullableComparator<T> {

    @Override
    public int compareNotNull(T o1, T o2) {
        @SuppressWarnings("unchecked")
        Comparable<T> c1 = (Comparable<T>)o1;
        return c1.compareTo(o2);
    }
}
