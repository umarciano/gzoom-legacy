package com.mapsengineering.workeffortext.scorecard;

/**
 * Risultati elaborazione esterni
 */
public class ExternalAchievesResult extends GenericResult {
    private double scoreCount = 0d;

    /**
     * Counter
     * @return
     */
    public double getScoreCount() {
        return scoreCount;
    }

    /**
     * set Counter
     * @param scoreCount
     */
    public void setScoreCount(double scoreCount) {
        this.scoreCount = scoreCount;
    }

    /**
     * increment Counter
     * @param toAdd
     */
    public void incrementScoreCount(double toAdd) {
        this.scoreCount += toAdd;
    }

}
