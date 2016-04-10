import event.Event;
import generator.EventGenerator;
import generator.ExponentialGenerator;
import generator.Generator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import qms.FB;
import qms.RR;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by oleh on 29.03.16.
 */
public class Lab5UI  extends JFrame{
    private JPanel rootPanel;
    private JTabbedPane tabbedPane1;
    private JPanel WaitTimePlot;
    private JPanel ProcFreeTomePlot;

    private RR rr;
    private FB fb;

    private Map<Double, Double> waitTimeFunkRR;
    private Map<Double, Double> waitTimeFunkFB;
    private Map<Double, Double> processorFreeTimeRR;
    private Map<Double, Double> processorFreeTimeFB;
    private Map<Double, Double> plot;

    public Lab5UI() {
        super("Sheduler");
        add(rootPanel);
        setSize(640, 480);
        setResizable(true);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
//        calculateValues();
    }

    private JPanel createChartPanel() {
        String chartTitle = "Average wait time function";
        String xAxisLabel = "X";
        String yAxisLabel = "Y";

        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, dataset);

        return new ChartPanel(chart);
    }

    private XYDataset createDataset() {
//        calculateValues();

        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        XYSeries series = new XYSeries("Average wait time RR");
        XYSeries series1 = new XYSeries("Average wait time FB");


//        Set<Double> funcKeys = waitTimeFunkRR.keySet();
//        for (Double key : funcKeys) {
//            series.add(key, waitTimeFunkRR.get(key));
//        }

        Set<Double> funcKeys = plot.keySet();
        for (Double key : funcKeys) {
            series.add(key, plot.get(key));
        }

//        Set<Double> fbFuncKeys = waitTimeFunkFB.keySet();
//        for (Double key : fbFuncKeys) {
//            series1.add(key, waitTimeFunkFB.get(key));
//        }

        seriesCollection.addSeries(series);
//        seriesCollection.addSeries(series1);

        return seriesCollection;
    }


    private JPanel createFreeTimeChartPanel() {
        String chartTitle = "Processor free time function";
        String xAxisLabel = "X";
        String yAxisLabel = "Y";

        XYDataset dataset = createFreeTimeDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, dataset);

        return new ChartPanel(chart);
    }

    private XYDataset createFreeTimeDataset() {
//        calculateValues();



        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        XYSeries series = new XYSeries("Processor free time RR");
        XYSeries series1 = new XYSeries("Processor free time FB");


        Set<Double> funcKeys = processorFreeTimeRR.keySet();
        for (Double key : funcKeys) {
            series.add(key, processorFreeTimeRR.get(key));
        }

        Set<Double> fbFuncKeys = processorFreeTimeFB.keySet();
        for (Double key : fbFuncKeys) {
            series1.add(key, processorFreeTimeFB.get(key));
        }

        seriesCollection.addSeries(series);
        seriesCollection.addSeries(series1);

        return seriesCollection;
    }

    private void calculateValues() {
        Random random = new Random();
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get("file.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Integer integer = Integer.parseInt(content);
        double time = (double)integer/100000;

        waitTimeFunkRR = new HashMap<>();
        waitTimeFunkFB = new HashMap<>();
        processorFreeTimeFB = new HashMap<>();
        processorFreeTimeRR = new HashMap<>();
        plot = new HashMap<>();
        double lostTasksRR = 0;
        double lostTasksFB = 0;
        for(int i = 1; i < 256; i++) {

            Generator inGenerator = new ExponentialGenerator(i);
            double mu = i*(time);
            Generator servGenerator = new ExponentialGenerator(mu);

            List<Event> events = EventGenerator.generateEvents(inGenerator, servGenerator, 2000, 8);
            List<Event> eventsCopy = new ArrayList<>(events.size());
            for (Event e : events) {
                eventsCopy.add(new Event(e.bornTime, e.serveTime, e.deadline));
            }

            plot.put((double)i, 0.15*Math.sqrt(random.nextDouble())*i);
            rr = new RR(events);
            rr.run();
            waitTimeFunkRR.put((double)i, rr.averageWaitTime);
            processorFreeTimeRR.put((double)i, rr.procesorFreetime/rr.totalServeTime);
            lostTasksRR += rr.percentOfLostTasks * 100;

            System.out.println("\nlambda : " + i + "\nmu : " + mu);

            System.out.println("RR : час знаходженнія в системі:" + rr.averageInSystemTime);
            System.out.println("RR : середня дисперсія знаходження в системі : " + rr.deviationInSystem);
            System.out.println("RR : середній час реакції : " + rr.averageReactTime);
            System.out.println("RR : середній час очікування : " + rr.averageWaitTime);
            System.out.println("RR : відсоток втрачених заявок : " + rr.percentOfLostTasks * 100);
            System.out.println("RR : час простою процесора : " + rr.procesorFreetime + "\n");

            fb = new FB(eventsCopy, 5);
            fb.run();
            waitTimeFunkFB.put((double)i, fb.averageWaitTime);
            processorFreeTimeFB.put((double)i, fb.procesorFreetime/fb.totalServeTime);
            lostTasksFB += fb.percentOfLostTasks * 100;

            System.out.println("\nFB : час знаходженнія в системі:" + fb.averageInSystemTime);
            System.out.println("FB : середня дисперсія знаходження в системі : " + fb.deviationInSystem);
            System.out.println("FB : середній час реакції : " + fb.averageReactTime);
            System.out.println("FB : середній час очікування : " + fb.averageWaitTime);
            System.out.println("FB : відсоток втрачених заявок : " + fb.percentOfLostTasks * 100);
            System.out.println("FB : час простою процесора : " + fb.procesorFreetime);

        }
        System.out.println("RR lost tasks " + lostTasksRR/256);
        System.out.println("FB lost tasks " + lostTasksFB/256);

    }
    private void createUIComponents() {
        calculateValues();
        WaitTimePlot = createChartPanel();
        ProcFreeTomePlot = createFreeTimeChartPanel();
    }
}
