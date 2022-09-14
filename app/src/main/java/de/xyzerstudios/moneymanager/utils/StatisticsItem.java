package de.xyzerstudios.moneymanager.utils;

import java.util.Date;

public class StatisticsItem {

    private final int month;
    private final int year;
    private final int expenseAmount;
    private final int incomeAmount;
    private final Date timestamp;

    public StatisticsItem(int month, int year, int expenseAmount, int incomeAmount, Date timestamp) {
        this.month = month;
        this.year = year;
        this.expenseAmount = expenseAmount;
        this.incomeAmount = incomeAmount;
        this.timestamp = timestamp;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getExpenseAmount() {
        return expenseAmount;
    }

    public int getIncomeAmount() {
        return incomeAmount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
