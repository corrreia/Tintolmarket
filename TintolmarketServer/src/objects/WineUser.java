package objects;

import java.io.Serializable;

/**
 * Class that represents a wine listed by a user.
 * It contains the name of the wine,
 * the price and the quantity of the wine.
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato   | fc56372
 * @author João Vieira   | fc45677
 */
public class WineUser implements Serializable {

    private String name;
    private float price = 0;
    private int quantity = 0;

    /**
     * Constructor for the WineUser class.
     * 
     * @param name The name of the wine.
     * @param price The price of the wine.
     * @param quantity The quantity of the wine.
     */
    public WineUser(String name, float price, int quantity) {
        this.name = name;
        if (price > 0)
            this.price = price;
        if (quantity > 0)
            this.quantity = quantity;
    }

    /**
     * Adds a quantity to the wine.
     * 
     * @param quantity The quantity to be added.
     */
    public void addQuantity(int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Cannot add negative quantity");
        this.quantity += quantity;
    }

    /**
     * Removes a quantity from the wine.
     * 
     * @param quantity  The quantity to be removed.
     */
    public void removeQuantity(int quantity) {
        if (this.quantity - quantity < 0)
            throw new IllegalStateException("Quantity cannot be negative");
        this.quantity -= quantity;
    }

    /**
     * Gets the price of the wine.
     * 
     * @return The price of the wine.
     */
    public float getPrice() {
        return price;
    }

    /**
     * Gets the quantity of the wine.
     * 
     * @return The quantity of the wine.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the price of the wine.
     * 
     * @param price The price of the wine.
     */
    public void setPrice(float price) {
        if (price < 0)
            throw new IllegalArgumentException("Price cannot be negative");
        else
            this.price = price;
    }

    /**
     * Sets the quantity of the wine.
     * 
     * @param quantity The quantity of the wine.
     */
    public void setQuantity(int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Quantity must be >= 0");
        this.quantity = quantity;
    }

    /**
     * Gets the name of the wine.
     * 
     * @return The name of the wine.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the wine.
     * 
     * @param name The name of the wine.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Wine [price=" + price + ", quantity=" + quantity + ", name=" + getName() + "]";
    }
}
