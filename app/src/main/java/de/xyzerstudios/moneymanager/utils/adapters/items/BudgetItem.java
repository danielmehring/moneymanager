package de.xyzerstudios.moneymanager.utils.adapters.items;

public class BudgetItem {

    private int budgetEntryId;
    private int categoryId;
    private String categoryName;
    private int amountLimit;
    private int amountSpent;

    public BudgetItem(int budgetEntryId, int categoryId, String categoryName, int amountLimit, int amountSpent) {
        this.budgetEntryId = budgetEntryId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amountLimit = amountLimit;
        this.amountSpent = amountSpent;
    }

    public int getBudgetEntryId() {
        return budgetEntryId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getAmountLimit() {
        return amountLimit;
    }

    public int getAmountSpent() {
        return amountSpent;
    }

    public int getDifference() {
        int difference = amountLimit - amountSpent;
        if (difference < 0)
            difference = difference * (-1);
        return difference;
    }

    public boolean isExceeded() {
        return amountSpent > amountLimit;
    }
}
