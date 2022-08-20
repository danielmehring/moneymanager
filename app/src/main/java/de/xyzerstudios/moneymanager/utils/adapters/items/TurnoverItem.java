package de.xyzerstudios.moneymanager.utils.adapters.items;

import de.xyzerstudios.moneymanager.utils.database.TurnoverType;

public class TurnoverItem {

    private final int entryId;
    private final String name;
    private final int amount;
    private final String date;
    private String paymentMethod;
    private final String category;
    private final TurnoverType turnoverType;

    public TurnoverItem(int entryId, String name, int amount, String date, String category) {
        this.entryId = entryId;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.turnoverType = TurnoverType.REVENUE;
    }

    public TurnoverItem(int entryId, String name, int amount, String date, String paymentMethod, String category) {
        this.entryId = entryId;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.category = category;
        this.turnoverType = TurnoverType.EXPENSE;
    }

    public int getEntryId() {
        return entryId;
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

    public TurnoverType getTurnoverType() {
        return turnoverType;
    }
}
