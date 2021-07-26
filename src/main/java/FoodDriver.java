public class FoodDriver {

    private int id;
    private String firstname;
    private String surname;
    private String city;
    private int load;

    public FoodDriver(int id, String firstname, String surname, String city, int load) {
        this.id = id;
        this.firstname = firstname;
        this.surname = surname;
        this.city = city;
        this.load = load;
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

    public String toString() {
        return ("first name: " + firstname +
                "\nsurname : " + surname +
                "\ncity    : " + city +
                "\nload    : " + load);
    }
}
