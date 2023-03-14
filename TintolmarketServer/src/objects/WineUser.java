package objects;

public class WineUser extends Wine {

    private float price;
    private int quantity;

    public WineUser(int id, String name, float price, int quantity) {
        super(id, name);
        this.price = price;
        this.quantity = quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void removeQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Wine [price=" + price + ", quantity=" + quantity + ", id=" + getId() + ", name=" + getName() + "]";
    }
}
