package com.kolomiets.kpi;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.Map;
import java.util.Set;

/**
 * Created by oleh on 10.03.16.
 */
public class MainFrame extends JFrame {

    private JPanel rootPanel;
    private JPanel chartPanel;
    private JPanel panel1;
    private JTextField expectationTextField;
    private JTextField varianceTextField;
    private JLabel expectationLable;
    private JLabel varianceLable;
    private JTabbedPane tabbedPane1;
    private JPanel correlationChart;

    private Map<Double, Double> functionX;
    private Map<Double,Double> functionY;
    private Map<Double,Double> selfCorrelationFunction;
    private Map<Double,Double> correlationFunction;
    private Generator generator;
    private Analyzer analyzer;
    private double mathExpectation;
    private double variance;

    public MainFrame() {
        super("Real-time systems");

        expectationTextField.setText(String.valueOf(mathExpectation));
        varianceTextField.setText(String.valueOf(variance));

        add(rootPanel);

        setSize(640, 480);
        setResizable(true);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JPanel createChartPanel() {
        String chartTitle = "Generated functionX";
        String xAxisLabel = "X";
        String yAxisLabel = "Y";

        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, dataset);

        return new ChartPanel(chart);
    }

    private JPanel createCorrelationChartPanel() {
        String chartTitle = "Correlation functionX";
        String xAxisLabel = "X";
        String yAxisLabel = "Y";

        XYDataset dataset = createCorrelationDataSet();

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, dataset);

        return new ChartPanel(chart);
    }

    private XYDataset createDataset() {
        calculateValues();

        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        XYSeries series = new XYSeries("Generated functionX");
        XYSeries series1 = new XYSeries("Selfcorrelation functionX");
        XYSeries series2 = new XYSeries("Correlation function");
        XYSeries series3 = new XYSeries("Generated functionY");

        Set<Double> funcKeys = functionX.keySet();
        for (Double key : funcKeys) {
            series.add(key, functionX.get(key));
        }

        Set<Double> selfCorrelationKeys = selfCorrelationFunction.keySet();
        for (Double key : selfCorrelationKeys) {
            series1.add(key, selfCorrelationFunction.get(key));
        }

        Set<Double> correlationKeys = correlationFunction.keySet();
        for (Double key : correlationKeys) {
            series2.add(key, correlationFunction.get(key));
        }

        for(Double key : functionY.keySet()) {
            series3.add(key, functionY.get(key));
        }

        seriesCollection.addSeries(series);
//        seriesCollection.addSeries(series1);
//        seriesCollection.addSeries(series2);
        seriesCollection.addSeries(series3);

        return seriesCollection;
    }

    private XYDataset createCorrelationDataSet() {
        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        XYSeries series = new XYSeries("Self correlation functionX");
        XYSeries series2 = new XYSeries("Correlation functionX");
        Set<Double> selfCorrelationKeys = selfCorrelationFunction.keySet();
        for (Double key : selfCorrelationKeys) {
            series.add(key, selfCorrelationFunction.get(key));
        }

        Set<Double> correlationKeys = correlationFunction.keySet();
        for (Double key : correlationKeys) {
            series2.add(key, correlationFunction.get(key));
        }
        seriesCollection.addSeries(series);
        seriesCollection.addSeries(series2);
        return seriesCollection;
    }

    private void createUIComponents() {
        chartPanel = createChartPanel();
        correlationChart = createCorrelationChartPanel();
    }

    private void calculateValues() {
        generator = new Generator(8, 2000, 256);
        analyzer = new Analyzer();
        long start = System.nanoTime();
        functionX = generator.generate();
        long generatinTime = System.nanoTime() - start;
        start = System.nanoTime();
        mathExpectation = analyzer.calculateMathExpectation(functionX);
        long expectionCalculatingTime = System.nanoTime() - start;
//        start = System.nanoTime();
        variance = analyzer.calculateVariance(functionX, mathExpectation);
        long varianceCalculatingtime = System.nanoTime() - start;


//        System.out.println("time for generation " + 1 + " nanoseconds\n" +
//                "expectation calculating time " + (double)expectionCalculatingTime/generatinTime*100.0 + " nanoseconds\n" +
//                "variance calculating time " +(double)varianceCalculatingtime/generatinTime*100.0 + " nanoseconds");
        selfCorrelationFunction = analyzer.calculateCorrelation(functionX, generator.getDelta(), mathExpectation);
        functionY = generator.generate();
        double mY = analyzer.calculateMathExpectation(functionY);
        correlationFunction = analyzer.calculateCorrelation(functionX, functionY, mathExpectation, mY, generator.getDelta());
        System.out.println("correlation calculated");
    }
}
