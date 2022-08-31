package de.xyzerstudios.moneymanager.utils.adapters.items;

public class CurrencyItem {

    private String isoCode;
    private String name;
    private String symbol;

    public CurrencyItem(String isoCode, String name, String symbol) {
        this.isoCode = isoCode;
        this.name = name;
        this.symbol = symbol;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}
