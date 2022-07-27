package de.xyzerstudios.moneymanager.utils.charting;

public class CategoryItem {

    private int indicatorColor;
    private String categoryText;
    private int categoryPercentage;

    public CategoryItem(int indicatorColor, String categoryText, int categoryPercentage) {
        this.indicatorColor = indicatorColor;
        this.categoryText = categoryText;
        this.categoryPercentage = categoryPercentage;
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
}
