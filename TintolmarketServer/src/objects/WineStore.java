package objects;

import java.io.Serializable;
import java.util.ArrayList;

public class WineStore implements Serializable {

    private String name;
    private String image;
    private ArrayList<Float> evaluations;

    /**
     *
     * @param name
     * @param image
     * @param evaluations
     */
    public WineStore(String name, String image, ArrayList<Float> evaluations) {
        this.name = name;
        this.image = image;
        this.evaluations = evaluations;
    }

    public WineStore(String name, String image) {
        this.name = name;
        this.image = image;
        this.evaluations = new ArrayList<Float>();
    }

    public void newEvaluation(float evaluation) {
        evaluations.add(evaluation);
    }

    public int getNrOfEvaluations() {
        return evaluations.size();
    }

    public float getEvaluation() {
        float sum = 0;
        for (float evaluation : evaluations) {
            sum += evaluation;
        }
        if (sum / evaluations.size() == 0) {
            return 0;
        }
        return sum / evaluations.size();
    }

    public String getImage() {
        return image;
    }

    public void setNrOfEvaluations(int nrOfEvaluations) {
        this.evaluations = new ArrayList<Float>();
    }

    public void setEvaluation(float evaluation) {
        this.evaluations.add(evaluation);
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
        return "Wine [evaluation=" + getEvaluation() + ", image=" + image + ", name=" + name + "]";
    }
}
