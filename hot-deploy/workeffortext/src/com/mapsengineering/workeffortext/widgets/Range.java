package com.mapsengineering.workeffortext.widgets;

/**
 * Inner class stores a range attributes
 * @author sandro
 */
public class Range {
    String rangeColor;
    double lowerBound;
    double upperBound;

    public Range(String rangeColor, double lowerBound, double upperBound) {
        this.rangeColor = rangeColor;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public Range() {
    }
}
