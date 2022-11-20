package me.raducapatina.client.data;

import java.util.Date;
import java.util.Objects;

public class Card {
    private long id;
    private int balance;
    private String cardName;
    private Date expirationDate;
    private char[] cvv;

    public Card() {
    }

    public Card(long id, int balance, String cardName, Date expirationDate, char[] cvv) {
        this.id = id;
        this.balance = balance;
        this.cardName = cardName;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
    }


    public long getId() {
        return id;
    }

    public Card setId(long id) {
        this.id = id;
        return this;
    }

    public int getBalance() {
        return balance;
    }

    public Card setBalance(int balance) {
        this.balance = balance;
        return this;
    }

    public String getCardName() {
        return cardName;
    }

    public Card setCardName(String cardName) {
        this.cardName = cardName;
        return this;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public Card setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public char[] getCvv() {
        return cvv;
    }

    public Card setCvv(char[] cvv) {
        this.cvv = cvv;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Card) obj;
        return this.id == that.id &&
                this.balance == that.balance &&
                Objects.equals(this.cardName, that.cardName) &&
                Objects.equals(this.expirationDate, that.expirationDate) &&
                Objects.equals(this.cvv, that.cvv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance, cardName, expirationDate, cvv);
    }

    @Override
    public String toString() {
        return "Card[" +
                "id=" + id + ", " +
                "balance=" + balance + ", " +
                "cardName=" + cardName + ", " +
                "expirationDate=" + expirationDate + ", " +
                "cvv=" + cvv + ']';
    }
}