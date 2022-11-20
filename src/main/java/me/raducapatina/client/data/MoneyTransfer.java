package me.raducapatina.client.data;

import java.util.Date;

public class MoneyTransfer {

    private String merchantName;
    private Date date;
    private int amount;

    public MoneyTransfer(String merchantName, Date date, int amount) {
        this.merchantName = merchantName;
        this.date = date;
        this.amount = amount;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public MoneyTransfer setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public MoneyTransfer setDate(Date date) {
        this.date = date;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public MoneyTransfer setAmount(int amount) {
        this.amount = amount;
        return this;
    }
}
