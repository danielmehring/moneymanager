package de.xyzerstudios.moneymanager.utils;

public class SimpleStatisticsItem {

    private final int month;
    private final int year;
    private final int amount;

    public SimpleStatisticsItem(int month, int year, int amount) {
        this.month = month;
        this.year = year;
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getAmount() {
        return amount;
    }
}
