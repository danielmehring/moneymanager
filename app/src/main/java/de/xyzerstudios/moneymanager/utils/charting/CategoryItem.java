package de.xyzerstudios.moneymanager.utils.charting;

public class CategoryItem {

    private final int indicatorColor;
    private final String categoryText;
    private final int categoryPercentage;
    private final boolean budgetExceeded;

    public CategoryItem(int indicatorColor, String categoryText, int categoryPercentage, boolean budgetExceeded) {
        this.indicatorColor = indicatorColor;
        this.categoryText = categoryText;
        this.categoryPercentage = categoryPercentage;
        this.budgetExceeded = budgetExceeded;
    }

    public CategoryItem(int indicatorColor, String categoryText, int categoryPercentage) {
        this.indicatorColor = indicatorColor;
        this.categoryText = categoryText;
        this.categoryPercentage = categoryPercentage;
        this.budgetExceeded = false;
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
