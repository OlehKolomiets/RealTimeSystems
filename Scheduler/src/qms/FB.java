package qms;

import event.Event;

import java.util.ArrayList;
import java.util.List;

public class FB extends AbstractQMS{

    List<List<Event>> eventQueues;
    // number of queues
    int numOfQueues;

    public double theta = 0.15;
    public FB(List<Event> inEvents, int numOfQueues) {
        super(inEvents);
        eventQueues = new ArrayList<>();
        eventQueues.add(new ArrayList<>(inEvents));
        this.numOfQueues = numOfQueues;
        for (int i = 1; i < this.numOfQueues; i++) {
            eventQueues.add(new ArrayList<>());
        }
    }

    @Override
    public void run() {

        double time = 0;

        while(!eventQueues.get(0).isEmpty()) {

            Event e = eventQueues.get(0).get(0);
            boolean useLowPriorityQueue = false;
            int numberOfQueue = 0;

            if(time < e.bornTime) {
                for (int i = 1; i < eventQueues.size(); i++) {
                    List<Event> l = eventQueues.get(i);
                    if(!l.isEmpty()) {
                        e = l.get(0);
                        l.remove(0);
                        useLowPriorityQueue = true;
                        numberOfQueue = i;
                        break;
                    }
                }
                if(!useLowPriorityQueue) {
                    procesorFreetime += e.bornTime - time;
                    time = e.bornTime;
                }
            }

            time = processComputation(e, time, numberOfQueue);

            if(!useLowPriorityQueue) {
                List<Event> l = eventQueues.get(0);
                l.remove(0);
            }

        }

        for (int i = 1; i < eventQueues.size(); i++) {
            Event e;
            int numberOfQueue = 0;
            List<Event> temp = eventQueues.get(i);
            if(!temp.isEmpty()) {
                e = temp.get(0);
                temp.remove(0);
                numberOfQueue = i;
            } else
                continue;

            time = processComputation(e, time, numberOfQueue);

        }
        calculateValues();
    }

    private void finishEventBy(Event e, double byTime, double fromTime) {
        completedTasks++;

        e.setCompletedTime(fromTime + byTime);

        if (e.getReactTime() < 0.0) {
            e.setReactTime(fromTime - e.bornTime);
        }

        e.setWaitTime(e.getWaitTime() + fromTime - e.getLastServeTime());
        e.setInSystemTime(fromTime + byTime - e.bornTime);

        e.setLastServeTime(fromTime + byTime);


    }

    private void processEventBy(Event e, double byTime, double fromTime) {

        if (e.getReactTime() < 0.0) {
            e.setReactTime(fromTime - e.bornTime);
        }

        e.setWaitTime(e.getWaitTime() + fromTime - e.getLastServeTime());

        e.completeBy(byTime);

        e.setLastServeTime(fromTime + byTime);
    }

    private double processComputation(Event e, double time, int numberOfQueue) {
        if(e.getDeadline() > time - e.bornTime) {
            double needTime = e.serveTime - e.getPartServedTime();
            if(needTime <= theta) {
                finishEventBy(e, needTime, time);
//                    finishEventBy(e, theta, time);
                time += needTime;
            } else {
                processEventBy(e, theta, time);
                time += theta;

                int nextQueue;
                if(numberOfQueue != (eventQueues.size()-1)) {
                    nextQueue = numberOfQueue + 1;
                }else
                    nextQueue = numberOfQueue;

                List l = eventQueues.get(nextQueue);
                l.add(e);
//                    eventQueues.set(nextQueue, l);
            }
        }
        return time;
    }

}

