public class Item {

    private String name;
    private double price;
    private int quantity;

    // Constructor for when quantity isn't required
    public Item(String name, double price) {
        this.name = name;
        this.price = price;
        this.quantity = 1;
    }

    // Constructor for when quantity is required
    public Item(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String toString() {
        return (name + ", " + price + ", " + quantity);
    }
}
