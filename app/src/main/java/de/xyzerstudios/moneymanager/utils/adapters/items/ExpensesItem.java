package de.xyzerstudios.moneymanager.utils.adapters.items;

public class ExpensesItem {

    private final int entryId;
    private final String name;
    private final int amount;
    private final String date;
    private final String paymentMethod;
    private final String category;

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
