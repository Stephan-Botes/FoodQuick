public class Restaurant {

    private int id;
    private String name;
    private String number;
    private String city;

    public Restaurant (int id, String name, String number, String city) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.city = city;
    }

    public int getId()  {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getNumber() {
        return this.number;
    }

    public String toString() {
        return ("name    : " + name +
                "\nnumber: " + number +
                "\ncity  : " + city);
    }
}
