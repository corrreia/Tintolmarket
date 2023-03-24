package objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represents a wine in the store.
 * It contains the name of the wine,
 * the image of the wine and the evaluations of the wine.
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato | fc56372
 * @author João Vieira | fc45677
 */
public class WineStore implements Serializable {

    private String name;
    private String image;
    private ArrayList<Float> evaluations;

    /**
     * Constructor for the WineStore class.
     * 
     * @param name        The name of the wine.
     * @param image       The image of the wine.
     * @param evaluations The evaluations of the wine.
     */
    public WineStore(String name, String image, ArrayList<Float> evaluations) {
        this.name = name;
        this.image = image;
        this.evaluations = evaluations;
    }

    /**
     * Constructor for the WineStore class.
     * 
     * @param name  The name of the wine.
     * @param image The image of the wine.
     */
    public WineStore(String name, String image) {
        this.name = name;
        this.image = image;
        this.evaluations = new ArrayList<Float>();
    }

    /**
     * Adds a new evaluation to the wine.
     * 
     * @param evaluation The evaluation to be added.
     */
    public void newEvaluation(float evaluation) {
        evaluations.add(evaluation);
    }

    /**
     * Gets the number of evaluations of the wine.
     * 
     * @return The number of evaluations of the wine.
     */
    public int getNrOfEvaluations() {
        return evaluations.size();
    }

    /**
     * Gets the evaluation of the wine.
     * 
     * @return The evaluation of the wine.
     */
    public float getEvaluation() {
        float sum = 0;
        if (evaluations.size() == 0) {
            return 0;
        }
        for (float evaluation : evaluations) {
            sum += evaluation;
        }
        if (sum / evaluations.size() == 0) {
            return 0;
        }
        return sum / evaluations.size();
    }

    /**
     * Gets the path of an image of the wine.
     * 
     * @return The path to the image of the wine.
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the number of evaluations of the wine.
     * 
     * @param nrOfEvaluations The number of evaluations of the wine.
     */
    public void setNrOfEvaluations(int nrOfEvaluations) {
        this.evaluations = new ArrayList<Float>();
    }

    /**
     * Sets the evaluation of the wine.
     * 
     * @param evaluation The evaluation of the wine.
     */
    public void setEvaluation(float evaluation) {
        this.evaluations.add(evaluation);
    }

    /**
     * Sets the path of an image of the wine.
     * 
     * @param image The path to the image of the wine.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Gets the name of the wine.
     * 
     * @return The name of the wine.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the wine.
     * 
     * @param name The name of the wine.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Wine [evaluation=" + getEvaluation() + ", image=" + image + ", name=" + name + "]";
    }
}
