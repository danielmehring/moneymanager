package de.xyzerstudios.moneymanager.utils.currency;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import de.xyzerstudios.moneymanager.utils.Json;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CurrencyConverter {

    private final Context context;

    public CurrencyConverter(Context context) {
        this.context = context;
    }

    public ConvertItem convertAmount(String isoFrom, String isoTo, int amount) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            String url = "https://www.frankfurter.app/latest?" +
                    "from=" + isoFrom +
                    "&to=" + isoTo +
                    "&amount=" + (amount / 100.0);
            Request request = new Request.Builder().url(url).build();
            String response = okHttpClient.newCall(request).execute().body().string();
            JsonNode jsonNode = Json.parse(response);
            int resultAmount = jsonNode.get("rates").get(isoTo).asInt() * 100;
            return new ConvertItem(resultAmount, response);
        } catch (IOException e) {
            return new ConvertItem();
        }
    }

    public double getConversionRate(String isoFrom, String isoTo) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            String url = "https://www.frankfurter.app/latest?" +
                    "from=" + isoFrom +
                    "&to=" + isoTo;
            Request request = new Request.Builder().url(url).build();
            String response = okHttpClient.newCall(request).execute().body().string();
            JsonNode jsonNode = Json.parse(response);
            return jsonNode.get("rates").get(isoTo).asDouble();
        } catch (IOException e) {
            return -1;
        }
    }
}
