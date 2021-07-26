public class Customer {

    private int id;
    private String firstname;
    private String surname;
    private String contactNumber;
    private String email;
    private String address;
    private String city;

    public Customer (int id, String firstname, String surname, String contactNumber, String email, String address, String city) {
        this.id = id;
        this.firstname = firstname;
        this.surname = surname;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.city = city;
    }

    public int getId()  {
        return this.id;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getSurname() {
        return this.surname;
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

    public String toString() {
        return ("firstname       : " + firstname +
                "\nsurname       : " + surname +
                "\nemail         : " + email +
                "\ncontact number: " + contactNumber +
                "\naddress       : " + address +
                "\ncity          : " + city);
    }
}
