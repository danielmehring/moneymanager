package de.xyzerstudios.moneymanager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Utils {

    public static final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy_MM_dd");
    public static final SimpleDateFormat timestampDateDisplayFormat = new SimpleDateFormat("yyyy.MM.dd");
    public static final SimpleDateFormat monthDateFormat = new SimpleDateFormat("MM");
    public static final SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SHARED_PREFS_CURRENT_PORTFOLIO = "currentPortfolio";
    public static final String SHARED_PREFS_IS_BUDGET_EXCEEDED = "isBudgetExceeded";
    public static final String SHARED_PREFS_COUNT_HOMEACTIVITY_CREATED = "countHomeActivityCreated";

    public static final String SPS_CURRENCY_ISO_CODE = "currencyIsoCode";
    public static final String SPS_CURRENCY_DISPLAYED_WITH_UNICODE = "currencyDisplayedWithUnicode";
    public static final String SPS_CURRENCY_CONVERT_USED_LAST = "currencyConvertUsedLast";

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

    public static String translateUsingGoogle(String langFrom, String langTo, String text) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        String urlStr = "https://script.google.com/macros/s/" +
                "AKfycbwCUpWzH3lxoAOauRQxssx0hieNIy8_XkyfJONzCQwPVr6j-aylrkrMn34Kt4HgnculOQ/exec" +
                "?q=" + URLEncoder.encode(text, "UTF-8") +
                "&target=" + langTo +
                "&source=" + langFrom;
        Request request = new Request.Builder().url(urlStr)
                .build();
        return okHttpClient.newCall(request).execute().body().string();
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return (networkInfo != null && networkInfo.isConnected());
    }
}
