package com.mapsengineering.workeffortext.services.trans;


/**
 * Enumeration with showPeriod 
 *
 */
public enum ShowPeriodsEnum {
    OPEN(false, false, true, false, false), //
    TWO_PREV(false, true, true, false, false), //
    TWO_NEXT(false, false, true, true, false), //
    THREE_MIDDLE(false, true, true, true, false), //
    THREE_PREV(true, true, true, false, false), //
    THREE_NEXT(false, false, true, true, true);

    private boolean prev1;
    private boolean prev2;
    private boolean current;
    private boolean next1;
    private boolean next2;

    /**
     * Constructor
     * @param prev2
     * @param prev1
     * @param current
     * @param next1
     * @param next2
     */
    ShowPeriodsEnum(boolean prev2, boolean prev1, boolean current, boolean next1, boolean next2) {
        this.prev1 = prev1;
        this.prev2 = prev2;
        this.current = current;
        this.next1 = next1;
        this.next2 = next2;
    }

    /**
     * @return the current 
     */
    public boolean current() {
        return current;
    }

    /**
     * @return the prev1
     */
    public boolean isPrev1() {
        return prev1;
    }

    /**
     * @return the prev2
     */
    public boolean isPrev2() {
        return prev2;
    }

    /**
     * @return the next1
     */
    public boolean isNext1() {
        return next1;
    }

    /**
     * @return the next2
     */
    public boolean isNext2() {
        return next2;
    }
}
