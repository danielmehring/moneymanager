package de.xyzerstudios.moneymanager.utils.adapters.items;

public class BalanceItem {

    private final String title;
    private final int saldo;
    private int id;


    public BalanceItem(String title, int saldo, int id) {
        this.title = title;
        this.saldo = saldo;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getSaldo() {
        return saldo;
    }

    public int getSaldoTimesMinusOne() {
        return (saldo * (-1));
    }

    public int getId() {
        return id;
    }
}
