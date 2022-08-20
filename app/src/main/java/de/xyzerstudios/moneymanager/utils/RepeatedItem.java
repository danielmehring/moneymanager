package de.xyzerstudios.moneymanager.utils;

public class RepeatedItem {

    private int categoryId;
    private int amountToAdd;

    public RepeatedItem(int categoryId, int amountToAdd) {
        this.categoryId = categoryId;
        this.amountToAdd = amountToAdd;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getAmountToAdd() {
        return amountToAdd;
    }
}
