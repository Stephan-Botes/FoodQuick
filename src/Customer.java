import java.util.ArrayList;

public class Customer {

    private String name;
    private String contactNumber;
    private String email;
    private String address;
    private String city;
    private ArrayList<String> orders;

    public Customer (String name, String contactNumber, String email, String address, String city) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.city = city;
        this.orders = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public String getContactNumber() {
        return this.contactNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public String getAddress() {
        return this.address;
    }

    public String getCity() {
        return this.city;
    }

    public ArrayList<String> getOrders() {
        return orders;
    }

    public void addOrder(String order) {
        this.orders.add(order);

        // Condition that occurs if the first element is added. It removes the "" element that is saved as the first entry
        // and as a result the true first element is shifted to the first index
        if (this.orders.size() > 1) {
            if (this.orders.get(0).equals("")) {
                this.orders.remove(0);
            }
        }
    }

    public String toString() {
        return (name + ", " + email + ", " + contactNumber + ", " + address + ", " + city + ", " + orders);
    }
}
