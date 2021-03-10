import java.util.ArrayList;
import java.util.HashMap;

public class Restaurant {

    private String name;
    private HashMap <String, String> branches;
    private ArrayList<Item> menu;

    public Restaurant (String name, HashMap<String, String> branches, ArrayList<Item> menu) {
        this.name = name;
        this.branches = branches;
        this.menu = menu;
    }

    public String getName() {
        return this.name;
    }

    public HashMap <String, String> getBranches() {
        return this.branches;
    }

    public ArrayList<Item> getMenu() {
        return this.menu;
    }

    public String toString() {
        return (name + "\n" + branches + "\n" + menu + "\n");
    }
}
