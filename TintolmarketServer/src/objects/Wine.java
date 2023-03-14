package objects;

public class Wine {
        //WINES:
    // id, name, evaluation, image 

    private int id;
    private String name;

    public Wine(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Wine [evaluation=" + ", id=" + id + ", image=" + ", name=" + name + "]";
    }
}
