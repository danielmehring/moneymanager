package de.xyzerstudios.moneymanager.utils.adapters.items;

public class ShowCategoryItem {

    private final int categoryId;
    private final int indicatorColor;
    private final String name;

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
