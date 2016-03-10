package com.kolomiets.kpi;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
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

    public ArrayList<Pair<Double, Double>> generate() {
        ArrayList<Pair<Double, Double>> result = new ArrayList<>();
        double t = 0;
        for (int i = 0; i < N; i++) {
            double x = 0;
            for (int j = 0; j < m; j++) {
                double A = rand.nextDouble();
                double phi = rand.nextDouble();
                double frequency = j*maxFrequency/m + maxFrequency/m*rand.nextDouble();
                x += A*Math.sin(frequency*t + phi);
            }
            result.add(new Pair<>(t, x));
            t += delta;
        }
        return result;
    }

}
