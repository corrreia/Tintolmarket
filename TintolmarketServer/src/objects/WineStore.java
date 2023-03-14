package objects;

public class WineStore extends Wine{

    private float evaluation;
    private String image;

    public WineStore(int id, String name, float evaluation, String image) {
        super(id, name);
        this.evaluation = evaluation;
        this.image = image;
    }

    public float getEvaluation() {
        return evaluation;
    }

    public String getImage() {
        return image;
    }

    public void setEvaluation(float evaluation) {
        this.evaluation = evaluation;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString(){
        return "Wine [evaluation=" + evaluation + ", id=" + getId() + ", image=" + image + ", name=" + getName() + "]";
    }
    
}
