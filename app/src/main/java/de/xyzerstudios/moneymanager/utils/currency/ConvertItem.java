package de.xyzerstudios.moneymanager.utils.currency;

public class ConvertItem {

    private int amount;
    private String fullResponse;

    public ConvertItem(int amount, String fullResponse) {
        this.amount = amount;
        this.fullResponse = fullResponse;
    }

    public ConvertItem() {
        amount = -1;
        fullResponse = "";
    }

    public int getAmount() {
        return amount;
    }

    public String getFullResponse() {
        return fullResponse;
    }
}
