package objects;

import java.io.Serializable;

public class WineStore implements Serializable {

    private String name;
    private String image;
    private float evaluation;
    private int nrOfEvaluations;

    public WineStore(String name, String image, float evaluation, int nrOfEvaluations) {
        this.name = name;
        this.image = image;
        this.evaluation = evaluation;
        this.nrOfEvaluations = nrOfEvaluations;
    }

    public WineStore(String name, String image) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Wine [evaluation=" + evaluation + ", image=" + image + ", name=" + getName() + "]";
    }
}
