package de.xyzerstudios.moneymanager.utils.adapters.items;

public class RepeatedIncomeItem {

    private int entryId;
    private int incomeEntryId;
    private String name;
    private int amount;

    public RepeatedIncomeItem(int entryId, int incomeEntryId, String name, int amount) {
        this.entryId = entryId;
        this.incomeEntryId = incomeEntryId;
        this.name = name;
        this.amount = amount;
    }

    public int getEntryId() {
        return entryId;
    }

    public int getIncomeEntryId() {
        return incomeEntryId;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }
}
