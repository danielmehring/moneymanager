package de.xyzerstudios.moneymanager.utils.adapters.items;

public class CurrencyItem {

    private String isoCode;
    private String name;

    public CurrencyItem(String isoCode, String name) {
        this.isoCode = isoCode;
        this.name = name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getName() {
        return name;
    }
}
