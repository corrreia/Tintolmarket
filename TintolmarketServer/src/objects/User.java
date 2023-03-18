package objects;

import java.util.LinkedList;

public class User {
    // USERS:
    // name, wines (id of wines, price, quantity), inbox (for messages)

    private String name;
    private float balance;
    private LinkedList<WineUser> wines;

    public User(String name, float balance) {
        this.name = name;
        this.balance = balance;
        this.wines = new LinkedList<WineUser>();
    }

    public void addWineListing(WineUser wine) {
        this.wines.add(wine);
    }

    public void removeWine(int wineId) {
        for (WineUser wine : this.wines)
            if (wine.getId() == wineId) {
                this.wines.remove(wine);
                break;
            }
    }

    public WineUser getWine(int wineId) {
        for (WineUser wine : this.wines)
            if (wine.getId() == wineId)
                return wine;
        return null;
    }

    public String getName() {
        return name;
    }

    public float getBalance() {
        return balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(float balance) {
        if (balance < 0)
            throw new IllegalArgumentException("Balance cannot be negative");
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", wines=" + wines + "]";
    }
}
