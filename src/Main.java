import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    // Initialize array lists used across the program for customers, restaurants, drivers and orders
    private static final ArrayList<Customer> customerList = new ArrayList<>();
    private static final ArrayList<Restaurant> restaurantList = new ArrayList<>();
    private static final ArrayList<FoodDriver> driverList = new ArrayList<>();
    private static final ArrayList<Order> orderList = new ArrayList<>();

    public static void main(String[] args) {

        // Load database/ information from text files and populate ArrayLists on startup
        readFromCustomerFile();
        readFromRestaurantFile();
        readFromDriverFile();
        readFromOrderFile();

        // Main menu
        while (true) {
            System.out.println("\nPlease choose an option: " +
                    "\nadd order - Add a new order" +
                    "\nadd cus   - Add a new customer" +
                    "\nadd res   - Add a new restaurant");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            // Switch statement which reacts to the user's input and calls the related functions
            switch (input) {

                // Case if the user wishes to add a new order
                case "add order":
                    addOrder();
                    break;

                // Case if the user wishes to add a new customer
                case "add cus":
                    String email;

                    // Loop that keeps asking the user for a valid input until given, or forced to exit
                    while (true) {
                        System.out.print("\nPlease enter the customer's email: ");
                        email = scanner.nextLine();

                        // Condition that checks if the entered email is a valid format and alerts if it is not
                        if (isValidEmailAddress(email)) {
                            break;
                        } else {
                            System.out.print("Please enter a valid email address.");
                        }
                    }

                    // Adds the new customer with the given email
                    addCustomer(email);
                    break;

                // Case if user wants to add a restaurant
                case "add res":
                    addRestaurant();
                    break;

                // Default case if user enters an invalid input
                default:
                    System.out.println("Please enter a valid input");
                    break;
            }
        }
    }

    // Functions
    // Function used to add a new order
    public static void addOrder() {
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
                if (checkOrderNumberDatabase(newOrderNumber)) {
                    break;
                } else {
                    System.out.print("Order number already exists.");
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
        // If the customer exists - adds the new order number to the existing customer's name
        // If customer doesn't exist - creates the customer first, then adds the order number
        // Saves to the customer.txt file
        if (checkCustomerExists(customerEmail)) {
            Customer customer = getCustomer(customerEmail); // Finds the customer in the database
            customer.addOrder(newOrderNumber); // Adds the new order number to the existing customer
        } else {
            System.out.println("The customer doesn't exist yet, adding customer first.");
            addCustomer(customerEmail); // Creates the customer first
            Customer customer = getCustomer(customerEmail);
            customer.addOrder(newOrderNumber); // Adds the new order number to the new customer object
        }
        writeToFile("customers.txt", customerList); // Saves the new entry to the customers.txt file
        sortCustomers(); // Saves to the customersAlphabetically.txt file
        groupCustomers(); // Saves to the customersCityGroups.txt file

        // Restaurant name section
        String restaurant;

        // Loop that continuously asks the user for a valid name until given
        while (true) {
            System.out.print("\nPlease enter the restaurant name: ");
            restaurant = scanner.nextLine();

            // Condition that checks if the restaurant is on the database and alerts if it isn't
            if (checkRestaurantDatabase(restaurant)) {
                break;
            } else {
                System.out.print("This restaurant isn't on the database.");
            }
        }

        // Client city section
        String city = getCustomer(customerEmail).getCity(); // Extracts the city name from the client database

        // Creates the initial Order object to be added to the database
        Order newOrder = new Order(newOrderNumber, customerEmail, restaurant, city);

        // Order list section
        printMenu(restaurant);  // Prints the restaurant's menu for the user
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
                    addOrderItem(newOrder);
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
        newOrder.setSpecialRequest(request); // Sets the special request of the order
        findSuitableDriver(newOrder); // Find a suitable driver for the newly created order and updates the driver's load
        orderList.add(newOrder); // Adds the completed Order to the order list
        writeToFile("orders.txt", orderList); // Updates the orders.txt file/ database with the new completed order
        writeToInvoiceFile(newOrder); // Writes a new invoice file after the order has been completed
    }

    // Function that adds a new customer to the customer database
    public static void addCustomer(String email) {
        Scanner scanner = new Scanner(System.in);

        // Name section
        String name;

        // Loop that continues to ask for a valid name until given
        while (true) {
            System.out.print("\nPlease enter the customer's name: ");
            name = scanner.nextLine();

            // Condition that checks if the name is not an empty space
            if (!name.equals("")) {
                break;
            }
            System.out.print("\nPlease enter a valid name.");
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

        Customer c = new Customer(name, contactNumber, email, address, city); // Creates a new Customer object with the provided properties
        customerList.add(c); // Adds the complete Customer object to the customer list
        writeToFile("customers.txt", customerList); // Updates the customers.txt file/ database with the newly added customer
        sortCustomers(); // Updates the customersAlphabetically.txt file with the newly added customer
        groupCustomers(); // Updates the customersCityGroups.txt file with the newly added customer
    }

    // Function that adds a new restaurant to the restaurant database
    public static void addRestaurant() {
        Scanner scanner = new Scanner(System.in);

        // Name section
        String name;

        // Loop that continuously asks the user for a valid name until given
        while (true) {
            System.out.print("\nPlease enter the restaurant's name: ");
            name = scanner.nextLine();

            // Condition that checks if the city is not an empty space
            if (!name.equals("")) {
                break;
            }
            System.out.print("Please enter a valid name.");
        }

        // Branch section
        HashMap<String, String> branches = new HashMap<>();
        boolean branchSection = true, firstBranch = true;

        // Loop that continuously asks for a user to add branches until user exits
        while (branchSection) {
            System.out.print("\nDo you wish to add a branch?" +
                    "\ny - yes" +
                    "\nn - no\n");
            String input = scanner.nextLine();

            // Switch statement that performs tasks according to the user's decision
            switch (input) {

                // Case if user wishes to add a branch
                case "y":
                    String branchName, branchNumber;
                    boolean addBranch = true;

                    // Loop that continuously asks for a branch name until a valid input is given - alerts if invalid
                    while (addBranch) {
                        System.out.print("\nPlease enter a branch location: ");
                        branchName = scanner.nextLine();

                        // Condition that checks of the branch name isn't a blank input
                        if (!branchName.equals("")) {

                            // Loop that continuously asks for the branch's number - alerts if invalid
                            while (true) {
                                System.out.print("\nPlease enter the branch's contact number: ");
                                branchNumber = scanner.nextLine();

                                // Condition that checks if the number given is a valid input
                                if (checkNumbersOnly(branchNumber)) {
                                    branches.put(branchName, branchNumber);
                                    firstBranch = false;
                                    addBranch = false;
                                    break;
                                }
                                System.out.print("Please enter a valid number.");
                            }
                        } else {
                            System.out.print("Please enter a valid name.");
                        }
                    }
                    break;

                // Case if user decides to stop adding branches
                case "n":
                    // Condition that checks if the user has added at least one branch
                    if (firstBranch) {
                        System.out.println("You need to add at least one branch.");
                        break;
                    }
                    branchSection = false;
                    break;

                // Case if user enters an invalid input
                default:
                    System.out.println("Please enter a valid input.");
            }
        }

        // Menu section
        ArrayList<Item> menu = new ArrayList<>();
        boolean menuSection = true, firstItem = true;

        // Loop that continuously asks for a user to add an item until user exits
        while (menuSection) {
            System.out.println("\nDo you wish to add a item?" +
                    "\ny - yes" +
                    "\nn - no");
            String input = scanner.nextLine();

            // Switch statement that performs tasks according to the user's decision
            switch (input) {

                // Case if user wishes to add an item
                case "y":
                    boolean addItem = true;

                    // Loop that continuously asks for a valid item name until given
                    while (addItem) {
                        String itemName, sItemPrice;
                        double itemPrice;

                        System.out.print("\nPlease enter the item name: ");
                        itemName = scanner.nextLine();

                        // Condition that checks if the item name isn't blank, otherwise alerts the user
                        if (!itemName.equals("")) {

                            // Loop that continuously asks for a valid item price
                            while (true) {
                                System.out.print("\nPlease enter the item's price: ");
                                sItemPrice = scanner.nextLine();

                                // Condition that checks validity of the item price and adds it to the menu array, otherwise alerts the user
                                if (checkNumbersOnly(sItemPrice)) {
                                    itemPrice = Double.parseDouble(sItemPrice); // Converts the valid price to a double
                                    Item item = new Item(itemName, itemPrice); // Creates an Item object with given properties
                                    menu.add(item); // Adds the item to the menu arraylist
                                    firstItem = false; // Lets the loop know the first item has been added
                                    addItem = false; // Allows the program to break out of the add while loop to go back to main menu
                                    break; // Breaks out of the current while loop checking item prices
                                }
                                System.out.print("Please enter a valid number.");
                            }
                        } else {
                            System.out.print("Please enter a valid name.");
                        }
                    }
                    break;

                // Case if user wishes to stop adding items
                case "n":
                    // Condition that checks if at least one item has been added already
                    if (firstItem) {
                        System.out.println("You need to add at least one item.");
                        break;
                    }
                    menuSection = false;
                    break;

                // Case if user enters an invalid input
                default:
                    System.out.println("Please enter a valid input.");
            }
        }

        Restaurant r = new Restaurant(name, branches, menu); // Creates a new Restaurant object with the provided properties
        restaurantList.add(r); // Adds the completed Restaurant object to the restaurant list
        writeToFile("restaurants.txt", restaurantList); // Updates the restaurants.txt file/ database with the newly added customer
    }

    // Function used to add items to an order list
    public static void addOrderItem(Order order) {

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

        String restaurantName = order.getRestaurant();

        // Condition that checks if the item name given is on the specified restaurant's menu - alerts if not found
        if (itemIsOnMenu(itemName, restaurantName)) {
            double itemPrice = 0.0;

            // Loop that Searches for the restaurant specified from the restaurant database
            for (Restaurant res : restaurantList) {

                // Condition if the restaurant named is found on the database
                if (res.getName().equals(restaurantName)) {
                    ArrayList<Item> menu = res.getMenu();

                    // Loop that searches for the item named on found restaurant's menu
                    for (Item item : menu) {

                        // Condition that gets the found item's price from the menu
                        if (item.getName().equals(itemName)) {
                            itemPrice = item.getPrice();
                        }
                    }
                }
            }

            String input;
            int quantity;

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

            Item newItem = new Item(itemName, itemPrice, quantity); // Creates a new Item object with the given properties
            order.addOrderItem(newItem); // Adds the new Item to the order's array list

        } else {
            System.out.println("That item isn't on the menu.");
        }
    }

    // Function used to print the specified restaurant's menu
    public static void printMenu(String restaurant) {
        DecimalFormat df = new DecimalFormat("####0.00"); // DecimalFormat object that defines the format of how the prices should be printed

        //  Loops through and prints the menu of the specified restaurant
        System.out.println("\nMenu: ");

        // Loop that finds the given restaurant form the restaurant list
        for (Restaurant res : restaurantList) {

            // Condition if the restaurant does exist
            if (res.getName().equals(restaurant)) {
                ArrayList<Item> menu = res.getMenu();

                // Loops through each item on the specified restaurant's menu and prints it out
                for (Item item : menu) {
                    String itemName = item.getName();
                    double itemPrice = item.getPrice();
                    System.out.println(itemName + " : " + "R" + df.format(itemPrice));
                }
            }
        }
    }

    // Function used to find the best driver for a new order
    public static void findSuitableDriver(Order order) {
        String city = order.getCity();
        String driverName = "";
        int minimumLoad = 100;

        // Loop that finds drivers in the same location as that of the order and then looks for the one with the minimum load
        for (FoodDriver driver : driverList) {
            if (city.equals(driver.getCity())) {
                if (minimumLoad > driver.getLoad()) {
                    driverName = driver.getName();
                    minimumLoad = driver.getLoad();
                }
            }
        }

        // Loop that increments the load of the driver that was selected for the job
        for (FoodDriver driver : driverList) {
            if (driverName.equals(driver.getName())) {
                driver.addLoad();
            }
        }

        writeToFile("driver-info.txt", driverList); // Updates the driver.txt file with the incremented load
        order.setDriver(driverName); // Sets the driver name of the order
    }

    // Function used to find the existing customer by email (which is used as the unique identifier)
    public static Customer getCustomer(String email) {

        // Loop that searches the customer list for the given email
        for (Customer customer : customerList) {

            // Condition if the customer was found on the database - else returns null
            if ((customer.getEmail().equals(email))) {
                return customer;
            }
        }
        return null;
    }

    // Function used to find the existing restaurant by name
    public static Restaurant getRestaurant(String name) {

        // Loop that searches the restaurant list for the given restaurant
        for (Restaurant restaurant : restaurantList) {

            // Condition if the restaurant was found on the database - else returns null
            if ((restaurant.getName().equals(name))) {
                return restaurant;
            }
        }
        return null;
    }

    // Function used to sort the customers list alphabetically
    public static void sortCustomers() {
        // Creates a new editable arraylist of String that will be returned as the result
        ArrayList<String> sortedCustomers = new ArrayList<>();

        // Loops through the customersList array list and extracts the name and order numbers only, then adds this to the String arraylist
        for (Customer customer : customerList) {
            String newLine = customer.getName() + ", " + customer.getOrders();
            sortedCustomers.add(newLine);
        }
        // Sorts the name's of the new arraylist alphabetically
        sortedCustomers.sort(String::compareToIgnoreCase);
        // Returns the sorted by name array list containing only names and order numbers
        writeToFile("customersAlphabetically.txt", sortedCustomers);
    }

    // Function used to group customers by their city
    public static void groupCustomers() {
        // Creates a new editable arraylist of Customer type with the information of customersList
        ArrayList<Customer> groupedCustomers = new ArrayList<>(customerList);
        // Creates a new arraylist of String type that will be returned as the result
        ArrayList<String> customersToPrint = new ArrayList<>();

        // Sorts the new arraylist of customers by their city names
        groupedCustomers.sort(Comparator.comparing(Customer::getCity));

        // Loops through the sorted arraylist and extracts the name and city only, then adds this to the String arraylist
        for (Customer customer : groupedCustomers) {
            String newLine = customer.getName() + ", " + customer.getCity();
            customersToPrint.add(newLine);
        }
        // Writes the to print array list with the sorted names and cities to the "customersCityGroups.txt" file
        writeToFile("customersCityGroups.txt", customersToPrint);
    }

    // Functions for handling files
    // Function used to write program data saved on a list to the specified file by using generics
    public static <T> void writeToFile(String filename, ArrayList<T> list) {
        // Condition that tries to write to the "filename" file with a formatter
        try {
            Formatter f = new Formatter(filename);
            for (T object : list) {
                f.format("%s", object + "\n");
            }
            f.close();
        }

        // Condition that occurs if the file cannot be written to
        catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // Function used to read from the customer file and populate the arraylist with that information
    public static void readFromCustomerFile() {
        // Condition that tries to read from the "customers.txt" file with a File and Scanner object
        try {
            File f = new File("customers.txt");
            Scanner scanner = new Scanner(f);

            // Loop that checks if the text file has more content to read and adds them to the lines array if so
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lineParts = line.split(", ", 6);

                String name = lineParts[0]; // Extracts name
                String email = lineParts[1]; // Extracts email
                String contactNumber = lineParts[2]; // Extracts contact number
                String address = lineParts[3]; // Extracts address
                String city = lineParts[4]; // Extracts city

                // Extracts the previous orders list and adds them to the customer object one by one
                String ordersLine = lineParts[5];
                String trim = ordersLine.replace("[", "").replace("]", "");
                String[] ordersParts = trim.split(", ");

                // Creates a new Customer object with all the extracted information
                Customer customer = new Customer(name, contactNumber, email, address, city);

                // Adds the individual order numbers to the new Customer object's order list
                for (String order : ordersParts) {
                    customer.addOrder(order);
                }

                customerList.add(customer); // Adds the completed Customer object to the customer list
            }
        }

        // Condition that occurs if the file cannot be read
        catch (Exception e) {
            System.out.println("Customer file - Error: " + e);
        }
    }

    // Function used to read from the restaurant file and populate the arraylist with that information
    public static void readFromRestaurantFile() {
        // Condition that tries to read from the "restaurant.txt" file with a File and Scanner object
        try {
            File f = new File("restaurants.txt");
            Scanner scanner = new Scanner(f);

            int lineCounter = 0; // Counter that controls how the different lines are handled
            String name = "";
            HashMap<String, String> branches = new HashMap<>();
            ArrayList<Item> menu = new ArrayList<>();

            // Loop that checks if the text file has more content to read and adds them to the lines array if so
            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();

                // Condition if the line is a name of a restaurant
                if (lineCounter == 0) {
                    name = line;
                }

                // Condition if the line is the branches of a restaurant
                if (lineCounter == 1) {
                    String trim = line.replace("{", "").replace("}", "");
                    String[] branchArray = trim.split(", ");

                    for (String s : branchArray) {
                        String[] split = s.split("=");
                        String location = split[0];
                        String contactNumber = split[1];
                        branches.put(location, contactNumber);
                    }
                }

                // Condition if the line is the menu of a restaurant
                if (lineCounter == 2) {
                    String trim = line.replace("[", "").replace("]", "");
                    String[] menuArray = trim.split(", ");

                    for (int i = 0; i < menuArray.length; i += 3) {
                        String itemName = menuArray[i];
                        double itemPrice = Double.parseDouble(menuArray[i + 1]);
                        menu.add(new Item(itemName, itemPrice));
                    }
                }

                lineCounter++; // Increments the line counter after essential information has been extracted

                // Condition if the empty line is reached after the restaurant's details
                if (lineCounter == 4) {
                    // Creates a clone of the branches Hashmap and menu arraylist to create a new Restaurant object with
                    // This is because clearing the branches and menu Map and ArrayList will clear the properties of the restaurant object as well
                    HashMap<String, String> branchMap = (HashMap) branches.clone();
                    ArrayList<Item> menuList = (ArrayList<Item>) menu.clone();

                    // Adds a new Restaurant object to the global restaurantList with the cloned Map and ArrayList
                    restaurantList.add(new Restaurant(name, branchMap, menuList));

                    // Resets all variables for original branches Map and menu ArrayList to be repopulated by new lines
                    name = "";
                    branches.clear();
                    menu.clear();
                    lineCounter = 0; // Resets the line counter to indicate a single object has been added and start over for a new one
                }
            }
        }

        // Condition that occurs if the file cannot be read
        catch (Exception e) {
            System.out.println("Restaurant file - Error: " + e);
        }
    }

    // Function used to read from the drivers file and populate the arraylist with that information
    public static void readFromDriverFile() {
        // Condition that tries to read from the driver-info.txt file with a File and Scanner object
        try {
            File f = new File("driver-info.txt");
            Scanner scanner = new Scanner(f);

            // Loop that checks if the text file has more content to read and adds them to the lines array if so
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lineParts = line.split(", ");

                String name = lineParts[0]; // Extracts driver name
                String city = lineParts[1]; // Extracts driver city
                int load = Integer.parseInt(lineParts[2]); // Extracts driver load

                // Creates a new customer object with all extracted information
                FoodDriver driver = new FoodDriver(name, city, load);
                driverList.add(driver); // Adds the complete Driver object to the customer list
            }
        }

        // Condition that occurs if the file cannot be read
        catch (Exception e) {
            System.out.println("Restaurant file - Error: " + e);
        }
    }

    // Function used to read from the orders file and populate the arraylist with that information
    public static void readFromOrderFile() {
        // Condition that tries to read from the orders.txt file with a File and Scanner object
        try {
            File f = new File("orders.txt");
            Scanner scanner = new Scanner(f);

            // Loop that checks if the text file has more content to read and adds them to the lines array if so
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lineParts = line.split(", ", 7);

                String orderNumber = lineParts[0]; // Extracts order number
                String customerEmail = lineParts[1]; // Extracts customer email
                String driverName = lineParts[2]; // Extracts driver name
                String restaurant = lineParts[3]; // Extracts restaurant name
                String city = lineParts[4]; // Extracts city
                String request = lineParts[5]; // Extracts customer special request

                // Extracts the array list of items of the order
                String itemsLine = lineParts[6];
                String trim = itemsLine.replace("[", "").replace("]", "");
                String[] itemsArray = trim.split(", ");

                // Creates a new order object with all extracted information
                Order order = new Order(orderNumber, customerEmail, restaurant, city);
                order.setDriver(driverName);
                order.setSpecialRequest(request);

                // Loops through order list and extracts item name, price and quantity to add a new item object for the order
                for (int i = 0; i < itemsArray.length; i += 3) {
                    String itemName = itemsArray[i];
                    double itemPrice = Double.parseDouble(itemsArray[i + 1]);
                    int itemQuantity = Integer.parseInt(itemsArray[i + 2]);
                    order.addOrderItem(new Item(itemName, itemPrice, itemQuantity));
                }
                orderList.add(order); // Adds the complete Order object to the customer list
            }
        }
        // Condition that occurs if the file cannot be read
        catch (Exception e) {
            System.out.println("Order file - Error: " + e);
        }
    }

    // Function used to write to the invoice file
    public static void writeToInvoiceFile(Order order) {

        DecimalFormat df = new DecimalFormat("####0.00"); // DecimalFormat object used to define the price's format of printing
        // Variable initialization to be used in the invoice printing
        String invoice;
        String orderNumber = order.getOrderNumber();
        String email = order.getCustomerEmail();
        Customer customer = getCustomer(email);
        String name = customer.getName();
        String customerNumber = customer.getContactNumber();
        String city = order.getCity();
        String[] address = customer.getAddress().split(" - ");
        String restaurant = order.getRestaurant();
        ArrayList<Item> orderItems = order.getOrderItems();
        String specialInstruction = order.getSpecialRequest();
        String driver = order.getDriver();
        String restaurantNumber = getRestaurant(restaurant).getBranches().get(city);
        double total = 0.0;

        // Condition that checks the if variable used is in a driver's vicinity - if it is, sets the invoice details, else sets an alternative invoice
        if (checkCitySupport(order)) {
            invoice = "Order number: " + orderNumber +
                    "\nCustomer: " + name +
                    "\nEmail: " + email +
                    "\nPhone number: " + customerNumber +
                    "\nLocation: " + city +
                    "\n\nYou have ordered the following from " + restaurant + " in " + city + ":\n";

            //Loops through the orderItems list to calculate the total of all the items added
            for (Item item : orderItems) {
                invoice += "\n" + item.getQuantity() + " x " + item.getName() + " (R" + df.format(item.getPrice()) + ")";
                total += item.getPrice() * item.getQuantity();
            }

            invoice += "\n\nSpecial instructions: " + specialInstruction +
                    "\n\nTotal: R" + df.format(total) +
                    "\n\n" + driver + " is nearest to the restaurant and so he/ she will be delivering your order to you at:" +
                    "\n\n" + address[0] +
                    "\n" + address[1] +
                    "\n\nIf you need to contact the restaurant, their number is " + restaurantNumber + ".";
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

    // Functions to check inputs existence/ validity
    // Function that checks if the customer email is already saved to the database
    public static boolean checkCustomerExists(String email) {

        // Loops through the customer list and searches if the given email exists
        for (Customer customer : customerList) {
            if (email.equals(customer.getEmail())) {
                return true;
            }
        }
        return false;
    }

    // Function used to check if the given item is on the specified restaurant menu
    public static boolean itemIsOnMenu(String itemName, String restaurant) {
        boolean onMenu = false;

        // Loops through the restaurant list and searches for the given restaurant
        for (Restaurant res : restaurantList) {

            // Condition if the restaurant is on the list
            if (res.getName().equals(restaurant)) {
                ArrayList<Item> menu = res.getMenu();

                // Loops through the menu of the given restaurant and searches for the item given
                for (Item item : menu) {

                    // Condition if item is on the menu
                    if (item.getName().equals(itemName)) {
                        onMenu = true;
                        break;
                    }
                }
            }
        }
        return onMenu;
    }

    // Function used to check if a new order number is already in the database
    public static boolean checkOrderNumberDatabase(String orderNumber) {
        // Loops through the order list and searches for the given order number
        for (Order order : orderList) {

            // Condition if the order number was found on the list
            if (orderNumber.equals(order.getOrderNumber())) {
                return false;
            }
        }
        return true;
    }

    // Function used to check if a restaurant is on the database
    public static boolean checkRestaurantDatabase(String restaurantName) {
        // Loops through the restaurant list and searches for the given restaurant
        for (Restaurant restaurant : restaurantList) {

            // Condition if the restaurant is found on the list
            if (restaurantName.equals(restaurant.getName())) {
                return true;
            }
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

    // Function used to check if the city can be delivered to
    public static boolean checkCitySupport(Order order) {
        String city = order.getCity();
        boolean citySupported = false;

        // Loop that searches through the driver's cities in the driver list
        for (FoodDriver driver : driverList) {

            // Condition if a driver's city matches the order's city
            if (city.equals(driver.getCity())) {
                citySupported = true;
                break;
            }
        }
        return citySupported;
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
            Pattern.compile("[0-9][0-9]?[0-9]?[0-9]?[0-9]? [A-Za-z]+ ?[A-Za-z]+? - [A-Za-z]+ ?[A-Za-z]+?");
}

// Sources
// https://stackoverflow.com/questions/8204680/java-regex-email
// https://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property