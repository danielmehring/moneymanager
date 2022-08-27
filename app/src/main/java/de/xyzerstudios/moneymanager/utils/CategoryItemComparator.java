package de.xyzerstudios.moneymanager.utils;

import java.util.Comparator;

import de.xyzerstudios.moneymanager.utils.charting.CategoryItem;

public class CategoryItemComparator implements Comparator<CategoryItem> {

        @Override
        public int compare(CategoryItem categoryItem1, CategoryItem categoryItem2) {
                return categoryItem2.getCategoryPercentage()
                        - categoryItem1.getCategoryPercentage();
        }
}
