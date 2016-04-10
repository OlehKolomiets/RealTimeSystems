package qms;

import event.Event;

import java.util.ArrayList;
import java.util.List;

public class RR extends AbstractQMS {

    ArrayList<Event> eventQueue;

    public double theta = 0.25;

    public RR(List<Event> inEvents) {
        super(inEvents);
        eventQueue = new ArrayList<>(inEvents);
        clearData();
    }

    private void clearData() {
        for (Event e : eventQueue) {
            e.clear();
        }
    }

    @Override
    public void run() {

        double time = 0;

        while (!eventQueue.isEmpty()) {

            Event e = eventQueue.get(0);

            if (time < e.bornTime) {
                procesorFreetime += e.bornTime - time;
                time = e.bornTime;
            }

            if (e.getDeadline() > (time - e.bornTime)) {

//                double newTime = time + e.serveTime;

                double needTime = e.serveTime - e.getPartServedTime();
                if (needTime <= theta) {
                    finishEventBy(e, needTime, time);
                    time += needTime;
                } else {
                    processEventBy(e, theta, time);
                    time += theta;

                    // insert at proper position
                    int i = 1;
                    while (i < eventQueue.size() && eventQueue.get(i).getLastServeTime() < time)
                        i++;
                    eventQueue.add(i, e);
                }

            }
            eventQueue.remove(0);
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


}

