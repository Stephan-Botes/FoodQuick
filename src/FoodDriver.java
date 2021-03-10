public class FoodDriver {

    private String name;
    private String city;
    private int load;

    public FoodDriver(String name, String city, int load) {
        this.name = name;
        this.city = city;
        this.load = load;
    }

    public String getName() {
        return this.name;
    }

    public String getCity() {
        return this.city;
    }

    public int getLoad() {
        return this.load;
    }

    public void addLoad() {
        this.load++;
    }

    public String toString() {
        return (name + ", " + city + ", " + load);
    }
}
