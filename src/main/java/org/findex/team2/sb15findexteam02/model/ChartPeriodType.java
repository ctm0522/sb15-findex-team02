package org.findex.team2.sb15findexteam02.model;

public enum ChartPeriodType {
    MONTHLY(1), QUARTERLY(3), YEARLY(12);

    private final int months;

    ChartPeriodType(int months) {
        this.months = months;
    }

    public int getMonths() {
        return months;
    }
}
