import java.io.File;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class

Main {

    public static void main(String[] args) {

        // Initial database connection component setup
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost;databaseName=QuickFoodMS",
                    "stephan",
                    "pass123"
            );

            // Main menu
            while (true) {
                System.out.println("\nPlease choose an option: " +
                        "\n1 - Add a new order" +
                        "\n2 - Find incomplete orders" +
                        "\n3 - Add a new customer" +
                        "\n4 - Find customer" +
                        "\n5 - Edit customer information" +
                        "\n6 - Prints an invoice and finalizes an order");

                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();

                // Switch statement which reacts to the user's input and calls the related functions
                switch (input) {

                    // Case if the user wishes to add a new order
                    case "1":
                        createOrder(connection);
                        break;

                    // Case if the user wishes to find incomplete orders
                    case "2":
                        findIncompleteOrders(connection);
                        break;

                    // Case if user wants to add a customer
                    case "3":
                        addCustomer(connection);
                        break;

                    // Case if the user wishes to find a customer
                    case "4":
                        findCustomer(connection);
                        break;

                    // Case if the user wishes to edit a customer
                    case "5":
                        editCustomer(connection);
                        break;

                    // Case if the user wishes to finalise an order
                    case "6":
                        finaliseOrder(connection);
                        break;

                    // Default case if user enters an invalid input
                    default:
                        System.out.println("Please enter a valid input.");
                        break;
                }
            }
        } // Exception if a database connection cannot be made
        catch (SQLException e) {
            System.out.println("An error occurred when connecting to the database! Details: ");
            e.printStackTrace();
        }
    }

    // Functions
    // Functions called by the main menu
    // Function used to create a new order
    public static void createOrder(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        // Order number section
        String newOrderNumber;

        // Loop that continuously asks the user for a valid order number until given, or forced to exit
        while (true) {
            System.out.print("\nPlease enter the new order number (leave empty to exit): ");
            newOrderNumber = scanner.nextLine();

            // Condition that exits the function if the user leaves the order number input empty
            if (newOrderNumber.equals("")) {
                return;
            }

            // Section that checks the validity of the order number
            if (checkNumbersOnly(newOrderNumber)) {
                // Condition that checks and alerts the user if the number is already in the database
                int iNewOrderNumber = Integer.parseInt(newOrderNumber);
                if (checkOrderNumberDatabase(connection, iNewOrderNumber)) {
                    System.out.print("Order number already exists.");
                } else {
                    break;
                }
            } else {
                System.out.print("Please enter a valid order number.");
            }
        }

        // Customer email section
        String customerEmail;

        // Loop that continuously asks the user for a valid email until given
        while (true) {
            System.out.print("\nPlease enter the customer's email: ");
            customerEmail = scanner.nextLine();

            // Condition that checks if the entered email is a valid format and alerts if it is not
            if (isValidEmailAddress(customerEmail)) {
                break;
            } else {
                System.out.print("Please enter a valid email address.");
            }
        }

        // Condition that checks if the user email is already in the customers database
        // If the customer exists - continues to adds the new order number information
        // If customer doesn't exist - exits function and returns to main menu
        if (getCustomerByEmail(connection, customerEmail) == null) {
            System.out.println("The customer doesn't exist in the database, please create the customer entry first.");
            return;
        }

        // Restaurant name section
        String restaurant;

        // Loop that continuously asks the user for a valid name until given
        while (true) {
            System.out.print("\nPlease enter the restaurant name: ");
            restaurant = scanner.nextLine();

            // Condition that checks if the restaurant is on the database and alerts if it isn't
            if (checkRestaurantDatabase(connection, restaurant)) {
                break;
            } else {
                System.out.print("This restaurant isn't on the database.");
            }
        }

        // Client city section
        String city = getCustomerByEmail(connection, customerEmail).getCity(); // Extracts the city name from the client database

        // Creates the initial Order object to be added to the database
        int iOrderNumber = Integer.parseInt(newOrderNumber); // Converts the string order number to int
        int customerID = getCustomerByEmail(connection, customerEmail).getId(); // Finds the correct customer via his email

        // Condition that checks if a driver is available in the are
        if (getSuitableDriver(connection, city) == null) {
            System.out.println("Sorry! There are no drivers working in the customer's city.");
            return;
        }

        int driverID = getSuitableDriver(connection, city).getId(); // Find a suitable driver for the newly created order and updates the driver's load
        int restaurantID = getRestaurantByNameAndCity(connection, restaurant, city).getId(); // Finds the correct restaurant via its name and city
        Order newOrder = new Order(iOrderNumber, customerID, driverID, restaurantID); // Creates a new Order object with the above information
        addOrder(connection, newOrder); // Saves the object to the database
        updateDriverLoad(connection, newOrder); // Updates the driver's load after the newly added order

        // Order list section
        printMenu(connection, restaurant);  // Prints the restaurant's menu for the user
        boolean orderCheck = true, firstOrder = false; // booleans used to keep user in the add items loop & to assure at least 1 item is added respectively

        // Loop that allows user to continuously add items until user exits
        while (orderCheck) {
            System.out.print("\nDo you wish to add a item to the order list? " +
                    "\ny - yes" +
                    "\nn - no\n");
            String orderControl = scanner.nextLine();

            // Switch statement that performs tasks according to the user's decision
            switch (orderControl) {

                // Case if user wishes to add an item
                case "y":
                    addOrderItem(connection, newOrder);
                    firstOrder = true;
                    break;

                // Case if user wishes to stop adding items
                case "n":
                    // Condition if user wishes to stop adding more items - exits loop
                    // if no item has been added yet - alerts and loops back to menu
                    if (firstOrder) {
                        orderCheck = false;
                    } else {
                        System.out.println("Please add at least one order item.");
                    }
                    break;

                // Case if user enters an invalid input
                default:
                    System.out.println("Please enter a valid input.");
                    break;
            }
        }

        // Customer request section - If left empty, it will automatically set it as "None"
        System.out.print("\nPlease enter the customer's special request (if none, leave blank): ");
        String request = scanner.nextLine();

        // Condition if the request space is left open/ blank
        if (request.equals("") || request == null) {
            request = "None";
        }

        addCustomerRequest(connection, request, newOrder); // Sets the special request of the order
    }

    // Function used to list incomplete orders on the database
    public static void findIncompleteOrders(Connection connection) throws SQLException {
        // Creates a PreparedStatement to find all the incomplete orders
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Orders WHERE status = 'Incomplete'");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ResultSet result = ps.executeQuery();
        System.out.println("\nIncomplete order numbers: ");

        // Condition that prints the items when found
        while (result.next()) {
            int orderNumber = result.getInt("order_number");
            Order order = getOrderByOrderNumber(connection, orderNumber);
            Customer customer = getCustomerById(connection, order.getCustomerID());
            Restaurant restaurant = getRestaurantById(connection, order.getRestaurantID());
            FoodDriver driver = getDriverById(connection, order.getDriverID());

            System.out.println("\norder number: " + orderNumber +
                    "\ncustomer    : " + customer.getFirstname() + " " + customer.getSurname() +
                    "\ndriver      : " + driver.getFirstname() + " " + driver.getSurname() +
                    "\nrestaurant  : " + restaurant.getName() +
                    "\ncity        : " + customer.getCity() +
                    "\nstatus      : " + order.getStatus());
        }
    }

    // Function used to add a new customer to the database
    public static void addCustomer(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String addEmail;

        // Loop that keeps asking the user for a valid input until given, or forced to exit
        while (true) {
            System.out.print("\nPlease enter the customer's email (leave empty to exit): ");
            addEmail = scanner.nextLine();

            // Condition that exits the function if the user leaves the email field input empty
            if (addEmail.equals("")) {
                return;
            }

            // Condition that checks if the entered email is a valid format and alerts if it is not
            if (isValidEmailAddress(addEmail)) {
                break;
            } else {
                System.out.print("Please enter a valid email address.");
            }
        }

        // Condition that checks if the customer email is already in the database
        if (getCustomerByEmail(connection, addEmail) == null) {
            // Adds the new customer with the given email
            createCustomer(connection, addEmail);
        } else {
            System.out.println("Customer already exists in database.");
        }
    }

    // Function used to find a specified customer in the database
    public static void findCustomer(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter the customer email address: ");
        String findEmail = (scanner.nextLine());

        // Condition that checks if the customer exists in the database and prints their details if found
        if (getCustomerByEmail(connection, findEmail) == null) {
            System.out.println("Customer doesn't exist");
        } else {
            System.out.println("The customer details are: ");
            System.out.println(getCustomerByEmail(connection, findEmail));
        }
    }

    public static void editCustomer(Connection connection) throws SQLException {

        // Loop that asks what information the user wishes to change
        while (true) {
            System.out.println("\nWhat do you wish to change: " +
                    "\n1 - Email address" +
                    "\n2 - First name" +
                    "\n3 - Surname" +
                    "\n4 - Contact number" +
                    "\n5 - Address" +
                    "\n6 - City");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            // Switch statement which reacts to the user's input and calls the related functions
            switch (input) {

                // Case if the user wishes to edit the customer's email
                case "1":
                    editCustomerEmail(connection);
                    return;

                // Case if the user wishes to edit the customer's first name
                case "2":
                    editCustomerFirstname(connection);
                    return;

                // Case if the user wishes to edit the customer's surname
                case "3":
                    editCustomerSurname(connection);
                    return;

                // Case if the user wishes to edit the customer's number
                case "4":
                    editCustomerNumber(connection);
                    return;

                // Case if the user wishes to edit the customer's address
                case "5":
                    editCustomerAddress(connection);
                    return;

                // Case if the user wishes to edit the customer's city
                case "6":
                    editCustomerCity(connection);
                    return;

                // Default case if user enters an invalid input
                default:
                    System.out.println("Please enter a valid input.");
                    break;
            }
        }
    }

    // Function used to find an incomplete order to finalise and initiate the finalising process
    public static void finaliseOrder(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String incompleteOrderNumber;

        // Loop that keeps asking the user for a valid input until given, or forced to exit
        while (true) {
            System.out.print("\nPlease enter an incomplete order number: ");
            incompleteOrderNumber = scanner.nextLine();

            // Condition that checks if the entered number is a valid format and alerts if it is not
            if (checkNumbersOnly(incompleteOrderNumber)) {
                int iOrderNumber = Integer.parseInt(incompleteOrderNumber);

                // Condition that checks if the order exists in the database
                if (getOrderByOrderNumber(connection, iOrderNumber) != null) {
                    Order order = getOrderByOrderNumber(connection, iOrderNumber);

                    // Condition that checks if the order is incomplete
                    if(order.getStatus().equals("Incomplete")) {
                        writeToInvoiceFile(connection, iOrderNumber); // Writes a new invoice file after finding an incompleted order
                        break;
                    } else {
                        System.out.println("That order is already completed.");
                    }
                } else {
                    System.out.print("That order number doesn't exist.");
                }
            } else {
                System.out.print("Please enter a valid order number.");
            }
        }
    }

    // Functions used to create/ add new records
    // Function used to add an order record
    public static void addOrder(Connection connection, Order order) throws SQLException {
        // Creates a PreparedStatement to add the order to the database
        PreparedStatement ps = connection.prepareStatement("INSERT INTO Orders VALUES (?, ?, ?, ?, ?, ?)");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setInt(1, order.getOrderNumber());
        ps.setInt(2, order.getCustomerID());
        ps.setInt(3, order.getDriverID());
        ps.setInt(4, order.getRestaurantID());
        ps.setString(5, "None");
        ps.setString(6, "Incomplete");
        ps.executeUpdate();
    }

    // Function used to add items to an order record
    public static void addOrderItem(Connection connection, Order order) throws SQLException {

        Scanner scanner = new Scanner(System.in);
        String itemName;

        // Loop that continuously asks for an valid item name until given
        while (true) {
            System.out.print("Please enter the item name: ");
            itemName = scanner.nextLine();

            // Condition that checks if the item name isn't a blank input
            if (!itemName.equals("")) {
                break;
            }
            System.out.println("You cannot leave this blank.");
        }

        int restaurantID = order.getRestaurantID();
        String restaurantName = getRestaurantById(connection, restaurantID).getName();

        // Condition that checks if the item name given is on the specified restaurant's menu - alerts if not found
        if (getItemByName(connection, itemName, restaurantName) != null) {

            Item newItem = getItemByName(connection, itemName, restaurantName);
            int orderNumber = order.getOrderNumber();
            int itemID = newItem.getId();
            int quantity;
            String input;

            // Loop that continuously asks for a valid quantity of the specified item
            while (true) {
                System.out.print("Enter the desired quantity of the item: ");
                input = scanner.nextLine();

                // Condition that checks if the quantity isn't a blank field - alerts if not valid
                if (!input.equals("")) {
                    quantity = Integer.parseInt(input);
                    break;
                }
                System.out.println("\nYou have to specify a quantity.");
            }

            // Creates a PreparedStatement to add an Order item to a placed order
            PreparedStatement ps = connection.prepareStatement("INSERT INTO OrderItems VALUES (?, ?, ?)");
            // Updates and executes the PreparedStatement parameters with the given variables from the user
            ps.setInt(1, orderNumber);
            ps.setInt(2, itemID);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        } else {
            System.out.println("That item isn't on this restaurant's menu. Please retry.");
        }
    }

    // Function used to add a customer request to an order
    public static void addCustomerRequest(Connection connection, String request, Order order) throws SQLException {
        // Creates a PreparedStatement to add the request to an existing order
        PreparedStatement ps = connection.prepareStatement("UPDATE Orders SET special_request = ? WHERE order_number = ?;");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setString(1, request);
        ps.setInt(2, order.getOrderNumber());
        ps.executeUpdate();
    }

    // Function that adds a new customer to the customer database
    public static void createCustomer(Connection connection, String email) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        // Name section
        String firstname;

        // Loop that continues to ask for a valid name until given
        while (true) {
            System.out.print("\nPlease enter the customer's first name: ");
            firstname = scanner.nextLine();

            // Condition that checks if the name is not an empty space
            if (!firstname.equals("")) {
                break;
            }
            System.out.print("\nPlease enter a valid first name.");
        }

        // Surname section
        String surname;

        // Loop that continues to ask for a valid name until given
        while (true) {
            System.out.print("\nPlease enter the customer's surname: ");
            surname = scanner.nextLine();

            // Condition that checks if the name is not an empty space
            if (!surname.equals("")) {
                break;
            }
            System.out.print("\nPlease enter a valid surname.");
        }

        // Contact number section
        String contactNumber;

        // Loop that continues to ask for a valid number until given
        while (true) {
            System.out.print("\nPlease enter the customer's contact number: ");
            contactNumber = scanner.nextLine();

            // Condition that checks if the input is numbers only by calling the "checkNumbersOnly" function
            if (checkNumbersOnly(contactNumber)) {
                break;
            } else {
                System.out.print("Invalid contact Number");
            }
        }

        // Customer address section
        String address;

        // Loop that keeps asking the user for a valid address until given
        while (true) {
            System.out.print("\nPlease enter the customer's address (123 Street name - District): ");
            address = scanner.nextLine();

            // Condition that checks if the entered address is a valid format
            if (isValidAddress(address)) {
                break;
            } else {
                System.out.print("Please enter a valid address in the format specified.");
            }
        }

        // Customer city section
        String city;

        // Loop that keeps asking the user for a valid city until given
        while (true) {
            System.out.print("\nPlease enter the customer's city: ");
            city = scanner.nextLine();

            // Condition that checks if the city is not an empty space
            if (!city.equals("")) {
                break;
            }
            System.out.println("You cannot leave this blank.");
        }

        Customer c = new Customer(1, firstname, surname, contactNumber, email, address, city); // Creates a new Customer object with the provided properties

        // Creates a PreparedStatement to add a customer to the table
        PreparedStatement ps = connection.prepareStatement("INSERT INTO Customers VALUES (?, ?, ?, ?, ?, ?)");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setString(1, contactNumber);
        ps.setString(2, email);
        ps.setString(3, firstname);
        ps.setString(4, surname);
        ps.setString(5, address);
        ps.setString(6, city);
        ps.executeUpdate();
    }

    // Functions used to fetch a specific record
    // Function used to find the best driver for a new order
    public static FoodDriver getSuitableDriver(Connection connection, String city) throws SQLException {
        // Creates a PreparedStatement to find a suitable driver
        PreparedStatement ps = connection.prepareStatement("SELECT TOP 1 * FROM Drivers WHERE city = ? ORDER BY load ASC;");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setString(1, city);
        ResultSet result = ps.executeQuery();

        // Condition that returns a driver record found
        if (result.next()) {
            int id = result.getInt("id");
            String firstname = result.getString("firstname");
            String surname = result.getString("surname");
            int load = result.getInt("load");
            return new FoodDriver(id, firstname, surname, city, load);
        }
        return null;
    }

    // Function used to find the existing customer by id
    public static Customer getCustomerById(Connection connection, int id) throws SQLException {
        // Creates a PreparedStatement to find a specific customer
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Customers WHERE id = ?");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setInt(1, id);
        ResultSet result = ps.executeQuery();

        // Condition that returns the customer record if found
        if (result.next()) {
            String firstname = result.getString("firstname");
            String surname = result.getString("surname");
            String number = result.getString("number");
            String email = result.getString("email");
            String address = result.getString("address");
            String city = result.getString("city");
            return new Customer(id, firstname, surname, number, email, address, city);
        }
        return null;
    }

    // Function used to find the existing customer by email
    public static Customer getCustomerByEmail(Connection connection, String email) throws SQLException {
        // Creates a PreparedStatement to find a specific customer
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Customers WHERE email = ?");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setString(1, email);
        ResultSet result = ps.executeQuery();

        // Condition that returns the customer record if found
        if (result.next()) {
            int id = result.getInt("id");
            String firstname = result.getString("firstname");
            String surname = result.getString("surname");
            String number = result.getString("number");
            String address = result.getString("address");
            String city = result.getString("city");
            return new Customer(id, firstname, surname, number, email, address, city);
        }
        return null;
    }

    // Function used to find the existing restaurant by ID
    public static Restaurant getRestaurantById(Connection connection, int id) throws SQLException {
        // Creates a PreparedStatement to find a specific restaurant
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Restaurants WHERE id = ?");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setInt(1, id);
        ResultSet result = ps.executeQuery();

        // Condition that returns the restaurant record if found
        if (result.next()) {
            String name = result.getString("name");
            String number = result.getString("number");
            String city = result.getString("city");
            return new Restaurant(id, name, number, city);
        }
        return null;
    }

    // Function used to find the existing restaurant by name & city
    public static Restaurant getRestaurantByNameAndCity(Connection connection, String name, String city) throws SQLException {
        // Creates a PreparedStatement to find a specific restaurant
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Restaurants WHERE name = ? AND city = ?");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setString(1, name);
        ps.setString(2, city);
        ResultSet result = ps.executeQuery();

        // Condition that returns the restaurant record if found
        if (result.next()) {
            int id = result.getInt("id");
            String number = result.getString("number");
            return new Restaurant(id, name, number, city);
        }
        return null;
    }

    // Function used to find the existing drivers by ID
    public static FoodDriver getDriverById(Connection connection, int id) throws SQLException {
        // Creates a PreparedStatement to find a specific restaurant
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Drivers WHERE id = ?");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setInt(1, id);
        ResultSet result = ps.executeQuery();

        // Condition that returns the restaurant record if found
        if (result.next()) {
            String firstname = result.getString("firstname");
            String surname = result.getString("surname");
            String city = result.getString("city");
            int load = result.getInt("load");
            return new FoodDriver(id, firstname, surname, city, load);
        }
        return null;
    }

    // Function used to find the existing order by order number
    public static Order getOrderByOrderNumber(Connection connection, int orderNumber) throws SQLException {
        // Creates a PreparedStatement to find a specific order
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Orders WHERE order_number = ?");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setInt(1, orderNumber);
        ResultSet result = ps.executeQuery();

        // Condition that returns the order record if found
        if (result.next()) {
            int id = result.getInt("id");
            int customerID = result.getInt("customer_id");
            int driverID = result.getInt("driver_id");
            int restaurantID = result.getInt("restaurant_id");
            String specialRequest = result.getString("special_request");
            String status = result.getString("status");
            return new Order(id, orderNumber, customerID, driverID, restaurantID, specialRequest, status);
        }
        return null;
    }

    // Function used to find the order items of an order
    public static ArrayList<Item> getOrderItems(Connection connection, Order order) throws SQLException {
        ArrayList<Item> orderItems = new ArrayList<>();
        int orderNumber = order.getOrderNumber();

        // Creates a PreparedStatement to find a specific order
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM OrderItems WHERE order_number = ?");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setInt(1, orderNumber);
        ResultSet result = ps.executeQuery();

        // Condition that returns the order record if found
        while (result.next()) {
            int itemNumber = result.getInt("item_number");
            int qty = result.getInt("qty");

            Item item = getItemById(connection, itemNumber);
            int id = item.getId();
            String name = item.getName();
            double price = item.getPrice();
            orderItems.add(new Item(id, name, price, qty));
        }
        return orderItems;
    }

    // Function used to find an item in the database
    public static Item getItemById(Connection connection, int itemNumber) throws SQLException {
        // Creates a PreparedStatement to find a specific item via its item id
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Items WHERE id = ?");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setInt(1, itemNumber);
        ResultSet result = ps.executeQuery();

        // Condition that returns true if the record is found, otherwise false
        if (result.next()) {
            String itemName = result.getString("name");
            double itemPrice = result.getDouble("price");
            return new Item(itemNumber, itemName, itemPrice);
        }
        return null;
    }

    // Function used to find an item in the database
    public static Item getItemByName(Connection connection, String itemName, String restaurant) throws SQLException {
        // Creates a PreparedStatement to find a specific item via its name
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Items WHERE name = ? AND restaurant = ?");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setString(1, itemName);
        ps.setString(2, restaurant);
        ResultSet result = ps.executeQuery();

        // Condition that returns true if the record is found, otherwise false
        if (result.next()) {
            int id = result.getInt("id");
            double itemPrice = result.getDouble("price");
            return new Item(id, itemName, itemPrice);
        }
        return null;
    }

    // Functions used to edit records
    // Function used to add an order record
    public static void updateDriverLoad(Connection connection, Order order) throws SQLException {
        // Creates a PreparedStatement to update the assigned driver's load
        PreparedStatement ps = connection.prepareStatement("UPDATE Drivers SET load = load+1 WHERE id =?");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setInt(1, order.getDriverID());
        ps.executeUpdate();
    }

    // Function used to edit a customer's email
    public static void editCustomerEmail(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean edit = true;

        // Loop that continuously asks for a email until a correct email is given
        while (true) {
            System.out.print("\nPlease enter the customer's email address: ");
            String email = scanner.nextLine();

            // Condition that checks if the email is in a valid format
            if (isValidEmailAddress(email)) {

                // Condition that checks if the email exists in the database
                if (getCustomerByEmail(connection, email) != null) {
                    Customer customer = getCustomerByEmail(connection, email);
                    int id = customer.getId();

                    // Loop that continuously asks for a new email until a correct email is given
                    while (edit) {
                        System.out.print("\nPlease enter the new email address: ");
                        String newEmail = scanner.nextLine();

                        // Condition that checks if the email is in a valid format
                        if (isValidEmailAddress(newEmail)) {
                            // Creates a PreparedStatement to edit the specified email
                            PreparedStatement ps = connection.prepareStatement("UPDATE Customers SET email =? WHERE id =?");
                            // Updates and executes the PreparedStatement parameters with the given variables from the user
                            ps.setString(1, newEmail);
                            ps.setInt(2, id);
                            ps.executeUpdate();
                            System.out.println("Record updated!");
                            edit = false; // Exits the loop after a successful update
                        } else {
                            System.out.println("Please enter a valid email.");
                        }
                    }
                    break;
                } else {
                    System.out.println("That email doesn't exist in the database.");
                }
            } else {
                System.out.println("Please enter a valid email.");
            }
        }
    }

    // Function used to edit a customer's first name
    public static void editCustomerFirstname(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean edit = true;

        // Loop that continuously asks for a email until a correct email is given
        while (true) {
            System.out.print("\nPlease enter the customer's email address: ");
            String email = scanner.nextLine();

            // Condition that checks if the email is in a valid format
            if (isValidEmailAddress(email)) {

                // Condition that checks if the email exists in the database
                if (getCustomerByEmail(connection, email) != null) {
                    Customer customer = getCustomerByEmail(connection, email);
                    int id = customer.getId();

                    // Loop that continuously asks for a new first name until a correct one is given
                    while (edit) {
                        System.out.print("\nPlease enter the new first name: ");
                        String newFirstname = scanner.nextLine();

                        // Condition that checks if the first name is in a valid format
                        if (!newFirstname.equals("")) {
                            // Creates a PreparedStatement to edit the specified firstname
                            PreparedStatement ps = connection.prepareStatement("UPDATE Customers SET firstname =? WHERE id =?");
                            // Updates and executes the PreparedStatement parameters with the given variables from the user
                            ps.setString(1, newFirstname);
                            ps.setInt(2, id);
                            ps.executeUpdate();
                            System.out.println("Record updated!");
                            edit = false; // Exits the loop after a successful update
                        } else {
                            System.out.println("Please enter a valid first name.");
                        }
                    }
                    break;
                } else {
                    System.out.println("That email doesn't exist in the database.");
                }
            } else {
                System.out.println("Please enter a valid email.");
            }
        }
    }

    // Function used to edit a customer's surname
    public static void editCustomerSurname(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean edit = true;

        // Loop that continuously asks for a email until a correct email is given
        while (true) {
            System.out.print("\nPlease enter the customer's email address: ");
            String email = scanner.nextLine();

            // Condition that checks if the email is in a valid format
            if (isValidEmailAddress(email)) {

                // Condition that checks if the email exists in the database
                if (getCustomerByEmail(connection, email) != null) {
                    Customer customer = getCustomerByEmail(connection, email);
                    int id = customer.getId();

                    // Loop that continuously asks for a new surname until a correct one is given
                    while (edit) {
                        System.out.print("\nPlease enter the new surname: ");
                        String newSurname = scanner.nextLine();

                        // Condition that checks if the surname is in a valid format
                        if (!newSurname.equals("")) {
                            // Creates a PreparedStatement to edit the specified surname
                            PreparedStatement ps = connection.prepareStatement("UPDATE Customers SET surname =? WHERE id =?");
                            // Updates and executes the PreparedStatement parameters with the given variables from the user
                            ps.setString(1, newSurname);
                            ps.setInt(2, id);
                            ps.executeUpdate();
                            System.out.println("Record updated!");
                            edit = false; // Exits the loop after a successful update
                        } else {
                            System.out.println("Please enter a valid surname.");
                        }
                    }
                    break;
                } else {
                    System.out.println("That email doesn't exist in the database.");
                }
            } else {
                System.out.println("Please enter a valid email.");
            }
        }
    }

    // Function used to edit a customer's contact number
    public static void editCustomerNumber(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean edit = true;

        // Loop that continuously asks for a email until a correct email is given
        while (true) {
            System.out.print("\nPlease enter the customer's email address: ");
            String email = scanner.nextLine();

            // Condition that checks if the email is in a valid format
            if (isValidEmailAddress(email)) {

                // Condition that checks if the email exists in the database
                if (getCustomerByEmail(connection, email) != null) {
                    Customer customer = getCustomerByEmail(connection, email);
                    int id = customer.getId();

                    // Loop that continuously asks for a new number until a correct one is given
                    while (edit) {
                        System.out.print("\nPlease enter the new contact number: ");
                        String newNumber = scanner.nextLine();

                        // Condition that checks if the number is in a valid format
                        if (checkNumbersOnly(newNumber)) {
                            // Creates a PreparedStatement to edit the specified number
                            PreparedStatement ps = connection.prepareStatement("UPDATE Customers SET number =? WHERE id =?");
                            // Updates and executes the PreparedStatement parameters with the given variables from the user
                            ps.setString(1, newNumber);
                            ps.setInt(2, id);
                            ps.executeUpdate();
                            System.out.println("Record updated!");
                            edit = false; // Exits the loop after a successful update
                        } else {
                            System.out.println("Please enter a valid number.");
                        }
                    }
                    break;
                } else {
                    System.out.println("That email doesn't exist in the database.");
                }
            } else {
                System.out.println("Please enter a valid email.");
            }
        }
    }

    // Function used to edit a customer's address
    public static void editCustomerAddress(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean edit = true;

        // Loop that continuously asks for a email until a correct email is given
        while (true) {
            System.out.print("\nPlease enter the customer's email address: ");
            String email = scanner.nextLine();

            // Condition that checks if the email is in a valid format
            if (isValidEmailAddress(email)) {

                // Condition that checks if the email exists in the database
                if (getCustomerByEmail(connection, email) != null) {
                    Customer customer = getCustomerByEmail(connection, email);
                    int id = customer.getId();

                    // Loop that continuously asks for a new address until a correct one is given
                    while (edit) {
                        System.out.print("\nPlease enter the new address (1234 Street name - District): ");
                        String newAddress = scanner.nextLine();

                        // Condition that checks if the address is in a valid format
                        if (isValidAddress(newAddress)) {
                            // Creates a PreparedStatement to edit the specified address
                            PreparedStatement ps = connection.prepareStatement("UPDATE Customers SET address =? WHERE id =?");
                            // Updates and executes the PreparedStatement parameters with the given variables from the user
                            ps.setString(1, newAddress);
                            ps.setInt(2, id);
                            ps.executeUpdate();
                            System.out.println("Record updated!");
                            edit = false; // Exits the loop after a successful update
                        } else {
                            System.out.println("Please enter a valid address.");
                        }
                    }
                    break;
                } else {
                    System.out.println("That email doesn't exist in the database.");
                }
            } else {
                System.out.println("Please enter a valid email.");
            }
        }
    }

    // Function used to edit a customer's city
    public static void editCustomerCity(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean edit = true;

        // Loop that continuously asks for a email until a correct email is given
        while (true) {
            System.out.print("\nPlease enter the customer's email address: ");
            String email = scanner.nextLine();

            // Condition that checks if the email is in a valid format
            if (isValidEmailAddress(email)) {

                // Condition that checks if the email exists in the database
                if (getCustomerByEmail(connection, email) != null) {
                    Customer customer = getCustomerByEmail(connection, email);
                    int id = customer.getId();

                    // Loop that continuously asks for a new city until a correct one is given
                    while (edit) {
                        System.out.print("\nPlease enter the new city: ");
                        String newCity = scanner.nextLine();

                        // Condition that checks if the city is in a valid format
                        if (!newCity.equals("")) {
                            // Creates a PreparedStatement to edit the specified city
                            PreparedStatement ps = connection.prepareStatement("UPDATE Customers SET city =? WHERE id =?");
                            // Updates and executes the PreparedStatement parameters with the given variables from the user
                            ps.setString(1, newCity);
                            ps.setInt(2, id);
                            ps.executeUpdate();
                            System.out.println("Record updated!");
                            edit = false; // Exits the loop after a successful update
                        } else {
                            System.out.println("Please enter a valid city.");
                        }
                    }
                    break;
                } else {
                    System.out.println("That email doesn't exist in the database.");
                }
            } else {
                System.out.println("Please enter a valid email.");
            }
        }
    }

    // Function used to write to the invoice file
    public static void writeToInvoiceFile(Connection connection, int orderNumber) throws SQLException {
        // DecimalFormat object used to define the price's format of printing
        DecimalFormat df = new DecimalFormat("####0.00");

        // Variable initialization to be used in the invoice printing
        String invoice;
        double total = 0.0;
        Order order = getOrderByOrderNumber(connection, orderNumber);
        String specialInstruction = order.getSpecialRequest();

        int customerID = order.getCustomerID();
        Customer customer = getCustomerById(connection, customerID);
        String firstname = customer.getFirstname();
        String surname = customer.getSurname();
        String email = customer.getEmail();
        String customerNumber = customer.getContactNumber();
        String[] address = customer.getAddress().split(" - ");
        String city = customer.getCity();

        int restaurantID = order.getRestaurantID();
        Restaurant restaurant = getRestaurantById(connection, restaurantID);
        String restaurantName = restaurant.getName();
        String restaurantNumber = restaurant.getNumber();

        int driverID = order.getDriverID();
        FoodDriver driver = getDriverById(connection, driverID);
        String driverName = driver.getFirstname() + " " + driver.getSurname();

        ArrayList<Item> orderItems = getOrderItems(connection, order);

        // Condition that checks the if variable used is in a driver's vicinity - if it is, sets the invoice details, else sets an alternative invoice
        if (getRestaurantById(connection, restaurantID) != null) {
            invoice = "Order number: " + orderNumber +
                    "\nCustomer: " + firstname + " " + surname +
                    "\nEmail: " + email +
                    "\nPhone number: " + customerNumber +
                    "\nLocation: " + city +
                    "\n\nYou have ordered the following from " + restaurantName + " in " + city + ":\n";

            //Loops through the orderItems list to calculate the total of all the items added
            for (Item item : orderItems) {
                invoice += "\n" + item.getQuantity() + " x " + item.getName() + " (R" + df.format(item.getPrice()) + ")";
                total += item.getPrice() * item.getQuantity();
            }

            invoice += "\n\nSpecial instructions: " + specialInstruction +
                    "\n\nTotal: R" + df.format(total) +
                    "\n\n" + driverName + " is nearest to the restaurant and so he/ she will be delivering your order to you at:" +
                    "\n\n" + address[0] +
                    "\n" + address[1] +
                    "\n\nIf you need to contact the restaurant, their number is " + restaurantNumber + ".";

            // Marks the order as finalised in the database after a successful invoice was written
            markAsFinalised(connection, order);
        } else {
            invoice = "Sorry! Our drivers are too far away from you to be able to deliver to your location.";
        }

        // Condition that tries to write to the "invoice.txt" file with a formatter
        try {
            Formatter f = new Formatter("invoice.txt");
            f.format("%s", invoice + "\n");
            f.close();
        }

        // Condition that occurs if the file cannot be written to
        catch (Exception e) {
            System.out.println("Invoice file - Error: " + e);
        }
    }

    // Function used to finalise an order after its invoice was created
    public static void markAsFinalised(Connection connection, Order order) throws SQLException {
        // Creates a PreparedStatement to change the order status to "finalised"
        PreparedStatement ps = connection.prepareStatement("UPDATE Orders SET status = 'Finalised' WHERE order_number = ?;");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setInt(1, order.getOrderNumber());
        ps.executeUpdate();
        System.out.println("Order finalised!");
    }

    // Function used to print the specified restaurant's menu
    public static void printMenu(Connection connection, String restaurant) throws SQLException {
        DecimalFormat df = new DecimalFormat("####0.00"); // DecimalFormat object that defines the format of how the prices should be printed
        System.out.println("\nMenu: ");

        // Creates a PreparedStatement to find all the items on the restaurant menu
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Items WHERE restaurant = ?");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setString(1, restaurant);
        ResultSet result = ps.executeQuery();

        // Condition that prints the items when found
        while (result.next()) {
            String itemName = result.getString("name");
            double itemPrice = result.getDouble("price");
            System.out.println(itemName + " : " + "R" + df.format(itemPrice));
        }
    }

    // Function used to check if a new order number is already in the database
    public static boolean checkOrderNumberDatabase(Connection connection, int orderNumber) throws SQLException {
        // Creates a PreparedStatement to find a specific order via order number
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Orders WHERE order_number = ?");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setInt(1, orderNumber);
        ResultSet result = ps.executeQuery();

        // Condition that checks if the specific record exists and returns accordingly
        if (result.next()) {
            return true;
        }
        return false;
    }

    // Function used to check if a restaurant is on the database
    public static boolean checkRestaurantDatabase(Connection connection, String restaurantName) throws SQLException {
        // Creates a PreparedStatement to find a specific restaurant via name
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Restaurants WHERE name = ?");
        // Updates and executes the PreparedStatement parameters with the given variables from the user
        ps.setString(1, restaurantName);
        ResultSet result = ps.executeQuery();

        // Condition that checks if the specific record exists and returns accordingly
        if (result.next()) {
            return true;
        }
        return false;
    }

    // Function used to check if a string contains only numbers
    public static boolean checkNumbersOnly(String input) {
        boolean longCheck = true;
        boolean doubleCheck = true;

        // Tries to convert the string input to a long - if it fails sets the boolean check to false (for contact and order numbers)
        try {
            Long.parseLong(input);
        } catch (NumberFormatException e) {
            longCheck = false;
        }

        // Tries to convert the string input to a double - if it fails, sets the boolean check to false (for prices)
        try {
            Double.parseDouble(input);
        } catch (NumberFormatException e) {
            doubleCheck = false;
        }

        // Condition that checks if the given number could meet either conditions which assures it is a numbers only string
        return longCheck || doubleCheck;
    }

    // Function used to check the validity of an email address by calling the Pattern specified below
    // Uses a Matcher object that matches the input email with the called pattern and returns the result
    public static boolean isValidEmailAddress(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    // Function used to check the validity of an address by calling the Pattern specified below
    // Uses a Matcher object that matches the input address with the called pattern and returns the result
    public static boolean isValidAddress(String addressStr) {
        Matcher matcher = VALID_ADDRESS_REGEX.matcher(addressStr);
        return matcher.find();
    }

    // Pattern used by the above function that compares the email with a specific regex
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    // Pattern used by the above function that compares the address with a specific regex
    public static final Pattern VALID_ADDRESS_REGEX =
            Pattern.compile("[0-9][0-9]?[0-9]?[0-9]?[0-9]? [A-Za-z]+ ?[A-Za-z]+? ?[A-Za-z]+? - [A-Za-z]+ ?[A-Za-z]+?");
}

// Sources
// https://stackoverflow.com/questions/8204680/java-regex-email
// https://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property