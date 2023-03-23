package objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class User implements Serializable {
    // USERS:
    // name, wines (id of wines, price, quantity), inbox (for messages)

    private String name;
    private float balance;
    private LinkedList<WineUser> wines;
    private HashMap<String, String> inbox;

    public User(String name, float balance) {
        this.name = name;
        this.balance = balance;
        this.wines = new LinkedList<WineUser>();
    }

    public void addWineListing(WineUser wine) {
        this.wines.add(wine);
    }

    public void removeWine(String wine) {
        for (WineUser wineO : this.wines)
            if (wineO.getName().equals(wine)) {
                this.wines.remove(wineO);
                return;
            }
    }

    public WineUser getWine(String wine) {
        for (WineUser wineO : this.wines)
            if (wineO.getName().equals(wine))
                return wineO;
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

    public LinkedList<WineUser> getWines() {
        return wines;
    }

    public void addMessage(String sender, String message) {
        this.inbox.put(sender, message);
    }

    public HashMap<String, String> readInbox() {
        HashMap<String, String> inbox = this.inbox;
        this.inbox = new HashMap<String, String>();
        return inbox;
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", wines=" + wines + "]";
    }
}
