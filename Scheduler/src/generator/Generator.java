package generator;

public interface Generator {
    double generate();

    default public double[] generateSeq(int n) {
        double[] x = new double[n];

        for (int i = 0; i < n; i++) {
            x[i] = generate();
        }

        return x;
    }
}
