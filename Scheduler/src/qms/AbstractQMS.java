package qms;

import event.Event;

import java.util.List;

public abstract class AbstractQMS {

//    public final Relevance deadline;

//    public final double[] k;

    public final int N;
    public int completedTasks = 0;

    public final List<Event> inEvents;

    public double totalReactTime;
    public double totalInSystemTime;
    public double totalServeTime;
    public double totalWaitTime;
    public double totalRelevance;

    public double deviationInSystem;

    public double averageServeTime;
    public double averageInSystemTime;
    public double averageReactTime;
    public double averageWaitTime;
    public double lostTasksCount;
    public double percentOfLostTasks;
    public double procesorFreetime;
    public double totalProcessorTime;
    //public double averageRelevance;

    public AbstractQMS(List<Event> inEvents) {
//        this.deadline = deadline;
//        this.k = k;
        this.inEvents = inEvents;
        this.N = inEvents.size();
    }

    public abstract void run();

    public void calculateValues() {

        totalInSystemTime = totalReactTime = totalServeTime = totalWaitTime = 0.0;
        for (Event e : inEvents) {
            if (e.isCompleted()) {
                totalInSystemTime += e.getInSystemTime();
                totalReactTime += e.getReactTime();
                totalWaitTime += e.getWaitTime();
                totalServeTime += e.serveTime;
                totalRelevance += e.getDeadline();
            } else {
                lostTasksCount++;
                totalServeTime += e.getPartServedTime();
            }
        }

        averageInSystemTime = totalInSystemTime / N;
        averageReactTime = totalReactTime / N;
        averageWaitTime = totalWaitTime / N;
        averageServeTime = totalServeTime / N;
        percentOfLostTasks = lostTasksCount / N;

        deviationInSystem = 0;

        for (Event e : inEvents) {
            deviationInSystem += (averageInSystemTime - e.getInSystemTime()) * (averageInSystemTime - e.getInSystemTime());
        }

        deviationInSystem /= N;
    }

    public List<Event> getInEvents() {
        return inEvents;
    }

}
