package com.kolomiets.kpi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by oleh on 26.02.16.
 */
public class Analyzer {

    public double calculateMathExpectation(Map<Double, Double> function) {
        long time = System.nanoTime();
        double sum = 0;
        List<Double> funcValues = new ArrayList<>(function.values());
        Double[] keys = new Double[funcValues.size()];
        function.keySet().toArray(keys);
        for (Double value : funcValues) {
            sum += value;
        }
        double res = sum/function.size();
//        System.out.println("expectation time " + (System.nanoTime() - time));
        return res;
    }

    public double calculateVariance(Map<Double, Double> function, double mathExpectation) {
        long time = System.nanoTime();
        double sum = 0;
        List<Double> funcValues = new ArrayList<>(function.values());
        for (Double value : funcValues) {
            sum += Math.pow(value - mathExpectation, 2);
        }
        double res = sum/(funcValues.size() - 1);
//        System.out.println("variance time " + (System.nanoTime() - time));
        return res;
    }

    public Map<Double, Double> calculateCorrelation(Map<Double, Double> function, double delta, double mX) {
        Map<Double, Double> correlationFunction = new HashMap<>();
        Double[] keys = new Double[function.size()];
        function.keySet().toArray(keys);

        double tau = 0;
        while(tau <= findMax(keys)/2) {
            double sum = 0;
            for (double i = 0; i < function.size() - tau; i++) {
                sum += (function.get(i) - mX)*(function.get(i + tau) - mX);
            }

            double res = sum/(function.size() - tau);
            correlationFunction.put(tau, res);
            tau += delta;
        }

        return correlationFunction;
    }

    public Map<Double, Double> calculateCorrelation(Map<Double, Double> functionX,
                                                    Map<Double, Double> functionY,
                                                    double mX,
                                                    double mY,
                                                    double delta) {
        double tau = 0;
        Map<Double,Double> correlationFunction = new HashMap<>();
        Double[] keys = new Double[functionX.size()];
        functionX.keySet().toArray(keys);
        for (int i = 0; i < findMax(keys)/2; i++) {
            double sum = 0;
            for (double j = 0; j < functionX.size() - tau; j++) {
                sum = (functionX.get(j) - mX)*(functionY.get(j + tau) - mY);
            }
            correlationFunction.put(tau, sum/(functionX.size() - tau));
            tau += delta;
        }
        return correlationFunction;
    }

    private double findMax(Double[] a) {
        double max = Double.MIN_VALUE;
        for (double i : a) {
            if(i > max)
                max = i;
        }
        return max;
    }

}
