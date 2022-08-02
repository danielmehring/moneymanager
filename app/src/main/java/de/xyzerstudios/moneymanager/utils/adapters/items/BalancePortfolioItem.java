package de.xyzerstudios.moneymanager.utils.adapters.items;

public class BalancePortfolioItem {

    private String title;
    private int saldo;
    private int id;

    public BalancePortfolioItem(String title, int saldo) {
        this.title = title;
        this.saldo = saldo;
    }

    public BalancePortfolioItem(String title, int saldo, int id) {
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
