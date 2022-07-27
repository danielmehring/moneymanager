package de.xyzerstudios.moneymanager.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy_MM_dd");
    //public static final SimpleDateFormat timestampDateFormat = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
    public static final SimpleDateFormat timestampDateDisplayFormat = new SimpleDateFormat("yyyy.MM.dd");
    public static final SimpleDateFormat monthDateFormat = new SimpleDateFormat("MM");
    public static final SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SHARED_PREFS_CURRENT_PORTFOLIO = "currentPortfolio";

    public String formatCurrency(int amount) {
        int nachKomma = amount % 100;
        double d = amount / 100 + (nachKomma / 100.0);
        NumberFormat numberFormatter = NumberFormat.getCurrencyInstance();
        return numberFormatter.format(d).split(" ")[0];
    }

    public String formatCurrency(long amount) {
        long nachKomma = amount % 100;
        double d = amount / 100 + (nachKomma / 100.0);
        NumberFormat numberFormatter = NumberFormat.getCurrencyInstance();
        return numberFormatter.format(d).split(" ")[0];
    }
}
