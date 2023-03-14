package objects;

import java.util.LinkedList;

public class User {
    //USERS:
    // id , name, wines (id of wines, price, quantity), inbox (for messages)

    private int id;
    private String name;
    private float balance;
    private LinkedList<WineUser> wines;

    public User(int id, String name, float balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.wines = new LinkedList<WineUser>();
    }

    public void addWine(WineUser wine) {
        this.wines.add(wine);
    }

    public void removeWine(int wineId) {
        for (WineUser wine : this.wines) {
            if (wine.getId() == wineId) {
                this.wines.remove(wine);
                break;
            }
        }
    }

    public WineUser getWine(int wineId) {
        for (WineUser wine : this.wines) {
            if (wine.getId() == wineId) {
                return wine;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getBalance() {
        return balance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", wines=" + wines + "]";
    }
}
