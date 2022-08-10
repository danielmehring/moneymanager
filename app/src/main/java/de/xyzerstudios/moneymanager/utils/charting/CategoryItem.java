package de.xyzerstudios.moneymanager.utils.charting;

public class CategoryItem {

    private int indicatorColor;
    private String categoryText;
    private int categoryPercentage;
    private boolean budgetExceeded;

    public CategoryItem(int indicatorColor, String categoryText, int categoryPercentage, boolean budgetExceeded) {
        this.indicatorColor = indicatorColor;
        this.categoryText = categoryText;
        this.categoryPercentage = categoryPercentage;
        this.budgetExceeded = budgetExceeded;
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public String getCategoryText() {
        return categoryText;
    }

    public int getCategoryPercentage() {
        return categoryPercentage;
    }

    public boolean isBudgetExceeded() {
        return budgetExceeded;
    }
}
