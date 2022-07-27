package de.xyzerstudios.moneymanager.utils;

public class ExpensesItem {

    private int entryId;
    private String name;
    private int amount;
    private String date;
    private String paymentMethod;
    private String category;

    public ExpensesItem(int entryId, String name, int amount, String date, String paymentMethod, String category) {
        this.entryId = entryId;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getCategory() {
        return category;
    }

    public int getEntryId() {
        return entryId;
    }
}
