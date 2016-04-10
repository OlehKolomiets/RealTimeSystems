package com.kolomiets.kpi;

import javafx.util.Pair;

import java.util.*;

/**
 * Created by oleh on 26.02.16.
 */
public class Analyzer {

    private Map<Double, Pair<Double, Double>> fastDiscreteFourierTransform = new HashMap<>();

    synchronized void collectResults(int index, double partSumRe, double partSumIm) {
        Pair<Double,Double> pair = fastDiscreteFourierTransform.get((double)index);
        if(pair != null) {
            double sumRe = pair.getKey();
            double sumImm = pair.getValue();
            Pair p = new Pair(sumRe + partSumRe, sumImm + partSumIm);
            fastDiscreteFourierTransform.replace((double)index, p);
        } else {
            fastDiscreteFourierTransform.put((double)index, new Pair<>(partSumRe, partSumIm));
        }
    }

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
        while(tau <= findMax(keys) - 1) {
            double sum = 0;
            for (double i = 0; i < function.size() - tau; i++) {
                sum += (function.get(i) - mX)*(function.get(i + tau) - mX);
            }

            double res = sum/(function.size());
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

        while(tau <= findMax(keys) - 1) {
            double sum = 0;
            for (double j = 0; j < functionX.size() - tau; j++) {
                sum = (functionX.get(j) - mX)*(functionY.get(j + tau) - mY);
            }
            correlationFunction.put(tau, sum/(functionX.size()));
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

    public Map<Double, Double> discreteFourierTransform(Map<Double, Double> function) {
        Map<Double, Double> DFT = new HashMap<>();
        Set<Double> keys = function.keySet();
        double pi = Math.PI;
        int N = function.size();
        for (int i = 0; i < function.size(); i++) {
            double sumReal = 0;
            double sumImm = 0;
            for (Double key : keys) {
                double arg = (double) ((2*pi)/N)*i*key;

                sumReal += function.get(key)*Math.cos(arg);
                sumImm += function.get(key)*Math.sin(arg);

            }

            double mod = Math.sqrt(Math.pow(sumReal, 2) + Math.pow(sumImm, 2));

            DFT.put((double)i, mod);
        }

        return DFT;
    }

    public Map<Double, Double> fastDiscreteFourierTransform (Map<Double, Double> function) {
        ParityFunction parityFunction = new ParityFunction(function);
        OddnessFunction oddnessFunction = new OddnessFunction(function);
        System.out.printf("threads start");

        oddnessFunction.start();
        parityFunction.start();

        try {
            parityFunction.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            oddnessFunction.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<Double, Double> result = new HashMap<>();

        Set<Double> keySet = fastDiscreteFourierTransform.keySet();

        for (Double key : keySet) {
            Pair<Double, Double> p = fastDiscreteFourierTransform.get(key);
            double realPart = p.getKey();
            double imaginePart = p.getValue();
            double module = Math.sqrt(Math.pow(realPart, 2) + Math.pow(imaginePart, 2));
            result.put(key, module);
        }

        return result;
    }

    public class ParityFunction extends Thread {
        private Map<Double, Double> function;

        public ParityFunction(Map<Double, Double> functionX) {
            this.function = functionX;
        }

        @Override
        public void run() {
            Set<Double> keySet = function.keySet();
            double pi = Math.PI;
            int N = function.size();
            for (int i = 0; i < function.size(); i++) {
                double sumReal = 0;
                double sumImm = 0;
                ArrayList<Double> keys = new ArrayList<>(keySet);
                for (int j = 0; j < keys.size()/2; j++) {
                    double key = keys.get(2*j);

                    double arg = (double) ((2*pi)/N)*i*key;

                    sumReal += function.get(key)*Math.cos(arg);
                    sumImm += function.get(key)*Math.sin(arg);
                }
                collectResults(i, sumReal, sumImm);
            }
        }
    }

    public class OddnessFunction extends Thread {
        private Map<Double, Double> function;

        public OddnessFunction(Map<Double, Double> function) {
            this.function = function;
        }

        @Override
        public void run() {
            Set<Double> keySet = function.keySet();
            double pi = Math.PI;
            int N = function.size();
            for (int i = 0; i < function.size(); i++) {
                double sumReal = 0;
                double sumImm = 0;
                ArrayList<Double> keys = new ArrayList<>(keySet);
                for (int j = 0; j < keys.size()/2; j++) {
                    double key = keys.get(2*j + 1);
                    double arg = (double) ((2*pi)/N)*i*key;

                    sumReal += function.get(key)*Math.cos(arg);
                    sumImm += function.get(key)*Math.sin(arg);
                }
                collectResults(i, sumReal, sumImm);
            }
        }
    }
}