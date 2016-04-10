package generator;

import java.util.Random;

public class ExponentialGenerator implements Generator {

    final double lambda;

    private final Random rand;

    public ExponentialGenerator(double lambda) {
        this.lambda = lambda;
        rand = new Random();
    }

    @Override
    public double generate() {
        return -Math.log(rand.nextDouble()) / lambda;
    }
}
