package com.kolomiets.kpi;

import javafx.util.Pair;

import java.util.List;

/**
 * Created by oleh on 26.02.16.
 */
public class Analyzer {
    private List<Pair<Double, Double>> function;

    public Analyzer(List<Pair<Double, Double>> function) {
        this.function = function;
    }

    public double calculateMathExpectation() {
        double sum = 0;
        for (Pair<Double, Double> pair : function) {
            sum += pair.getValue();
        }
        return sum/function.size();
    }

    public double calculateVariance(double mathExpectation) {
        double sum = 0;
        for (Pair<Double, Double> pair : function) {
            sum += Math.pow(pair.getValue() - mathExpectation, 2);
        }
        return sum/function.size();
    }

}
