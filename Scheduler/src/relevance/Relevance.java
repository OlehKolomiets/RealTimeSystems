package relevance;

public class Relevance {

    private double deadline;

    public Relevance(double deadline) {
        this.deadline = deadline;
    }

    public boolean isRelevant(double t) {
        return t < deadline;
    }
}
