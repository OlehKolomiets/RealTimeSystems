package com.kolomiets.kpi;

import javafx.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.List;

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

    private List<Pair<Double, Double>> function;
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
        String chartTitle = "Generated function";
        String xAxisLabel = "X";
        String yAxisLabel = "Y";

        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, dataset);

        return new ChartPanel(chart);
    }

    private XYDataset createDataset() {
        calculateValues();

        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        XYSeries series = new XYSeries("Generated function");

        for (Pair<Double, Double> pair : function) {
            series.add(pair.getKey(), pair.getValue());
        }
        seriesCollection.addSeries(series);

        return seriesCollection;
    }

    private void createUIComponents() {
        chartPanel = createChartPanel();
    }

    private void calculateValues() {
        long start = System.nanoTime();
        generator = new Generator(8, 2000, 256);
        function = generator.generate();
        long generatinTime = System.nanoTime() - start;
        analyzer = new Analyzer(function);
        start = System.nanoTime();
        mathExpectation = analyzer.calculateMathExpectation();
        long expectionCalculatingTime = System.nanoTime() - start;
        start = System.nanoTime();
        variance = analyzer.calculateVariance(mathExpectation);
        long varianceCalculatingtime = System.nanoTime() - start;
        System.out.println("time for generation " + generatinTime + " nanoseconds\n" +
                "expectation calculating time " + expectionCalculatingTime + " nanoseconds\n" +
                "variance calculating time " +varianceCalculatingtime + " nanoseconds");
    }
}
