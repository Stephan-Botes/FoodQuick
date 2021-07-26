public class Item {

    private int id;
    private String name;
    private double price;
    private int quantity;

    // Constructor for when quantity isn't required
    public Item(int id, String name, double price) {
        this.id= id;
        this.name = name;
        this.price = price;
        this.quantity = 1;
    }

    // Constructor for when quantity is required
    public Item(int id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId()  {
        return this.id;
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
        return ("name: " + name +
                "\nprice: R" + price +
                "\nquantity: " + quantity);
    }
}
