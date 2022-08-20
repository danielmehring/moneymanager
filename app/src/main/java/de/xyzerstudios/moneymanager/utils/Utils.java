package de.xyzerstudios.moneymanager.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class Utils {

    public static final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy_MM_dd");
    public static final SimpleDateFormat timestampDateDisplayFormat = new SimpleDateFormat("yyyy.MM.dd");
    public static final SimpleDateFormat monthDateFormat = new SimpleDateFormat("MM");
    public static final SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SHARED_PREFS_CURRENT_PORTFOLIO = "currentPortfolio";
    public static final String SHARED_PREFS_IS_BUDGET_EXCEEDED = "isBudgetExceeded";
    public static final String SHARED_PREFS_COUNT_HOMEACTIVITY_CREATED = "countHomeActivityCreated";

    public String formatCurrency(int amount) {
        int nachKomma = amount % 100;
        double d = amount / 100 + (nachKomma / 100.0);
        NumberFormat numberFormatter = NumberFormat.getCurrencyInstance();
        return numberFormatter.format(d);
    }

    public String formatCurrency(long amount) {
        long nachKomma = amount % 100;
        double d = amount / 100 + (nachKomma / 100.0);
        NumberFormat numberFormatter = NumberFormat.getCurrencyInstance();
        return numberFormatter.format(d);
    }
}
