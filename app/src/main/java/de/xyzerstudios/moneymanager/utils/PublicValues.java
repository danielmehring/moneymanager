package de.xyzerstudios.moneymanager.utils;

public class PublicValues {

    private static boolean portfolioChanged = false;

    public static boolean isPortfolioChanged() {
        return portfolioChanged;
    }

    public static void portfolioChanged() {
        portfolioChanged = true;
    }

    public static void portfolioChangeReset() {
        portfolioChanged = false;
    }

}
