package objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Class that represents a user in the system.
 * It contains the name of the user,
 * the balance of the user,
 * the wines listed by the user and
 * the inbox of the user.
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato   | fc56372
 * @author João Vieira   | fc45677
 */
public class User implements Serializable {
    // USERS:
    // name, wines (id of wines, price, quantity), inbox (for messages)

    private String name;
    private float balance;
    private LinkedList<WineUser> wines;
    private HashMap<String, String> inbox;

    /**
     * Constructor for the User class.
     * 
     * @param name The name of the user.
     * @param balance The balance of the user.
     */
    public User(String name, float balance) {
        this.name = name;
        this.balance = balance;
        this.wines = new LinkedList<WineUser>();
    }

    /**
     * Adds a wine to the user's list of wines.
     * 
     * @param wine  The wine to be added.
     */
    public void addWineListing(WineUser wine) {
        this.wines.add(wine);
    }

    /**
     * Removes a wine from the user's list of wines.
     * 
     * @param wine  The wine to be removed.
     */
    public void removeWine(String wine) {
        for (WineUser wineO : this.wines)
            if (wineO.getName().equals(wine)) {
                this.wines.remove(wineO);
                return;
            }
    }

    /**
     * Gets a wine from the user's list of wines.
     * 
     * @param wine  The name of the wine to be retrieved.
     * @return The wine with the given name.
     */
    public WineUser getWine(String wine) {
        for (WineUser wineO : this.wines)
            if (wineO.getName().equals(wine))
                return wineO;
        return null;
    }

    /**
     * Gets the name of the user.
     * 
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the balance of the user.
     * 
     * @return The balance of the user.
     */
    public float getBalance() {
        return balance;
    }

    /**
     * Sets the name of the user.
     * 
     * @param name The name of the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the balance of the user.
     * 
     * @param balance The new balance of the user.
     */
    public void setBalance(float balance) {
        if (balance < 0)
            throw new IllegalArgumentException("Balance cannot be negative");
        this.balance = balance;
    }

    /**
     * Gets the list of wines of the user.
     * 
     * @return The list of wines of the user.
     */
    public LinkedList<WineUser> getWines() {
        return wines;
    }

    /**
     * Adds a message to the user's inbox.
     * 
     * @param sender    The sender of the message.
     * @param message   The message to be added.
     */
    public void addMessage(String sender, String message) {
        this.inbox.put(sender, message);
    }

    /**
     * Gets the user's inbox.
     * 
     * @return The user's inbox.
     */
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
