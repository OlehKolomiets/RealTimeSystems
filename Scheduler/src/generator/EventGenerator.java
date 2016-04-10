package generator;

import event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EventGenerator {

    public static List<Event> generateEvents(Generator inputGen, Generator serveGen, int n, double maxDeadline) {
        List<Event> events = new ArrayList<>();

        Random random = new Random();

        double time = 0;
        double servTime;

        for (int i = 0; i < n; i++) {
            time += inputGen.generate();
            servTime = serveGen.generate();
            double deadline = servTime + maxDeadline * random.nextDouble();
            Event e = new Event(time, servTime, deadline);
            events.add(e);
        }

        return events;
    }

}

