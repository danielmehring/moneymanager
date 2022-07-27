package de.xyzerstudios.moneymanager.utils;

public class ShowCategoryItem {

    private int categoryId;
    private int indicatorColor;
    private String name;

    public ShowCategoryItem(int categoryId, int indicatorColor, String name) {
        this.categoryId = categoryId;
        this.indicatorColor = indicatorColor;
        this.name = name;
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public String getName() {
        return name;
    }

    public int getCategoryId() {
        return categoryId;
    }
}
