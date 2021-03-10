import java.util.ArrayList;

public class Order {

    private String orderNumber;
    private String customerEmail;
    private String driver;
    private String restaurant;
    private String city;
    private String specialRequest;
    private ArrayList<Item> orderItems;

    public Order (String orderNumber, String customerEmail, String restaurant, String city) {
        this.orderNumber = orderNumber;
        this.customerEmail = customerEmail;
        this.restaurant = restaurant;
        this.city = city;
        this.orderItems = new ArrayList<>();
    }

    public String getOrderNumber() {
        return this.orderNumber;
    }

    public String getCustomerEmail() {
        return this.customerEmail;
    }

    public String getRestaurant() {
        return this.restaurant;
    }

    public String getCity() {
        return this.city;
    }

    public String getSpecialRequest() {
        return this.specialRequest;
    }

    public String getDriver() {
        return this.driver;
    }

    public ArrayList<Item> getOrderItems() {
        return this.orderItems;
    }

    public void setDriver(String driver) {
        this.driver = driver; }

    public void addOrderItem(Item item) {
        this.orderItems.add(item);
    }

    public void setSpecialRequest(String request) {
        this.specialRequest = request;
    }

    public String toString() {
        return (orderNumber + ", " + customerEmail + ", " + driver + ", " + restaurant + ", " + city + ", " + specialRequest + ", " + orderItems);
    }
}
