package com.kolomiets.kpi;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by oleh on 25.02.16.
 */
public class Generator {
    private int m;
    private int maxFrequency;
    // number of discrete parts
    private int N;
    // step for t
    private double delta = 1;

    private Random rand = new Random();

    public Generator(int m, int maxFrequency, int N) {
        this.m = m;
        this.maxFrequency = maxFrequency;
        this.N = N;
    }

    public Map<Double, Double> generate() {
        HashMap<Double, Double> result = new HashMap<>();
        double[][] genVal = new double[m][3];
        double A;
        double phi;
        double frequency;
        for (int i = 0; i < m; i++) {
            A = rand.nextDouble();
            phi = 2*Math.PI*rand.nextDouble();
            frequency = i*maxFrequency/m + maxFrequency/m*rand.nextDouble();
            genVal[i][0] = A;
            genVal[i][1] = phi;
            genVal[i][2] = frequency;
        }

        double t = 0;
        for (int i = 0; i < N; i++) {
            double x = 0;
            for (int j = 0; j < m; j++) {
                A = rand.nextDouble();
                phi = 2*Math.PI*rand.nextDouble();
                frequency = j*maxFrequency/m + maxFrequency/m*rand.nextDouble();
//                x += A*Math.sin(frequency*t + phi);

                x += genVal[j][0]*Math.sin(genVal[j][2]*t + genVal[j][1]);
            }
            result.put(t, x);
            t += delta;
        }
        return result;
    }

    public double getDelta() {
        return delta;
    }
}
