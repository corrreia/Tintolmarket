package objects;

public class Wine {
    // WINES:
    // id, name, evaluation, image
    private String name;

    public Wine(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Wine [name=" + name + "]";
    }
}
