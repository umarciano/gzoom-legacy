package com.mapsengineering.base.comparator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PartialMapComparator<K, V> extends NullableComparator<Map<K, V>> {

    private final List<K> keyList;
    private final Comparator<V> valueComparator;

    public PartialMapComparator(List<K> keyList, Comparator<V> valueComparator) {
        this.keyList = keyList;
        this.valueComparator = valueComparator != null ? valueComparator : new GenericComparator<V>();
    }

    public PartialMapComparator(List<K> keyList) {
        this(keyList, null);
    }

    public List<K> getKeyList() {
        return keyList;
    }

    public Comparator<V> getValueComparator() {
        return valueComparator;
    }

    @Override
    public int compareNotNull(Map<K, V> o1, Map<K, V> o2) {
        for (K key : keyList) {
            int diff = valueComparator.compare(o1.get(key), o2.get(key));
            if (diff != 0) {
                return diff;
            }
        }
        return 0;
    }
}
