package me.raducapatina.client.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Account {

    protected String username;
    protected String password;

    protected String ownerName;
    protected String id;
    protected int balance = 0;
    protected List<Card> cards = new ArrayList<>(0);
    protected MerchantType merchantType = MerchantType.UNKNOWN;
    protected boolean sysAdmin = false;

    public Account() {}

    Account(String username, String password, String ownerName, String id, MerchantType merchantType, boolean sysAdmin) {
        this.username = username;
        this.password = password;
        this.ownerName = ownerName;
        this.id = id;
        this.merchantType = merchantType;
        this.sysAdmin = sysAdmin;
    }

    public enum MerchantType {
        INDIVIDUAL,
        TOURISM,
        GROCERIES,
        TRANSPORT,
        ENTERTAINMENT,

        UNKNOWN
    }

    public String getUsername() {
        return username;
    }

    public Account setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Account setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Account setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        return this;
    }

    public String getId() {
        return id;
    }

    public Account setId(String id) {
        this.id = id;
        return this;
    }

    public int getBalance() {
        return balance;
    }

    public Account setBalance(int balance) {
        this.balance = balance;
        return this;
    }

    public MerchantType getMerchantType() {
        return merchantType;
    }

    public Account setMerchantType(MerchantType merchantType) {
        this.merchantType = merchantType;
        return this;
    }

    public Account addCard(Card card) {
        this.cards.add(card);
        return this;
    }

    public void removeCard(Card card) {
        this.cards.remove(card);
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean isSysAdmin() {
        return sysAdmin;
    }

    public Account setSysAdmin(boolean sysAdmin) {
        this.sysAdmin = sysAdmin;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (balance != account.balance) return false;
        if (sysAdmin != account.sysAdmin) return false;
        if (!Objects.equals(username, account.username)) return false;
        if (!Objects.equals(password, account.password)) return false;
        if (!Objects.equals(ownerName, account.ownerName)) return false;
        if (!Objects.equals(id, account.id)) return false;
        if (!Objects.equals(cards, account.cards)) return false;
        return merchantType == account.merchantType;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (ownerName != null ? ownerName.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + balance;
        result = 31 * result + (cards != null ? cards.hashCode() : 0);
        result = 31 * result + (merchantType != null ? merchantType.hashCode() : 0);
        result = 31 * result + (sysAdmin ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", id='" + id + '\'' +
                ", balance=" + balance +
                ", cards=" + cards +
                ", merchantType=" + merchantType +
                ", sysAdmin=" + sysAdmin +
                '}';
    }
}