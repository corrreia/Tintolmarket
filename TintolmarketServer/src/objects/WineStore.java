package objects;

public class WineStore extends Wine{

    private String image;
    private float evaluation;
    private int nrOfEvaluations;

    public WineStore(int id, String name, String image, float evaluation, int nrOfEvaluations) {
        super(id, name);
        this.image = image;
        this.evaluation = evaluation;
        this.nrOfEvaluations = nrOfEvaluations;
    }

    public WineStore(int id, String name, String image) {
        super(id, name);
        this.image = image;
        this.evaluation = 0;
        this.nrOfEvaluations = 0;
    }

    public void newEvaluation(float evaluation) {
        this.evaluation = (this.evaluation + evaluation) / (this.nrOfEvaluations + 1);
        this.nrOfEvaluations++;
    }

    public int getNrOfEvaluations() {
        return nrOfEvaluations;
    }

    public float getEvaluation() {
        return evaluation;
    }

    public String getImage() {
        return image;
    }

    public void setNrOfEvaluations(int nrOfEvaluations) {
        this.nrOfEvaluations = nrOfEvaluations;
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
