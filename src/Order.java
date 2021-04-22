public class Order {

    private int id;
    private int orderNumber;
    private int customerID;
    private int driverID;
    private int restaurantID;
    private String specialRequest;
    private String status;

    public Order (int orderNumber, int customerID, int driverID, int restaurantID) {
        this.orderNumber = orderNumber;
        this.customerID = customerID;
        this.driverID = driverID;
        this.restaurantID = restaurantID;
    }

    public Order (int id, int orderNumber, int customerID, int driverID, int restaurantID, String specialRequest, String status) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customerID = customerID;
        this.driverID = driverID;
        this.restaurantID = restaurantID;
        this.specialRequest = specialRequest;
        this.status = status;
    }

    public int getId()  {
        return this.id;
    }

    public int getOrderNumber() {
        return this.orderNumber;
    }

    public int getCustomerID() {
        return this.customerID;
    }

    public int getDriverID() {
        return this.driverID;
    }

    public int getRestaurantID() {
        return this.restaurantID;
    }

    public String getSpecialRequest() {
        return this.specialRequest;
    }

    public String getStatus() {
        return this.status;
    }

    public String toString() {
        return ("order number     : " + orderNumber +
                "\ncustomer ID    : " + customerID +
                "\ndriver ID      : " + driverID +
                "\nrestaurant ID  : " + restaurantID +
                "\nspecial request: " + specialRequest +
                "\nstatus         : " + status);
    }
}
