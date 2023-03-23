package objects;

public class WineUser extends Wine {

    private float price = 0;
    private int quantity = 0;

    public WineUser(String name, float price, int quantity) {
        super(name);
        if (price > 0)
            this.price = price;
        if (quantity > 0)
            this.quantity = quantity;
    }

    public void addQuantity(int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Cannot add negative quantity");
        this.quantity += quantity;
    }

    public void removeQuantity(int quantity) {
        if (this.quantity - quantity < 0)
            throw new IllegalStateException("Quantity cannot be negative");
        this.quantity -= quantity;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setPrice(float price) {
        if (price < 0)
            throw new IllegalArgumentException("Price cannot be negative");
        else
            this.price = price;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Quantity must be >= 0");
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Wine [price=" + price + ", quantity=" + quantity + ", name=" + getName() + "]";
    }
}
