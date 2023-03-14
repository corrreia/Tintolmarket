package objects;

import java.util.LinkedList;

public class User {
    //USERS:
    // id , name, wines (id of wines, price, quantity), inbox (for messages)

    private int id;
    private String name;
    private LinkedList<WineUser> wines;

    public User(int id, String name) {
        this.id = id;
        this.name = name;

        this.wines = new LinkedList<WineUser>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LinkedList<WineUser> getWines() {
        return wines;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWines(LinkedList<WineUser> wines) {
        this.wines = wines;
    }

    public void addWine(WineUser wine) {
        this.wines.add(wine);
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", wines=" + wines + "]";
    }
}
