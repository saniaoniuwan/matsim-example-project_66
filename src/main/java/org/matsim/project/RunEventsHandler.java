package org.matsim.project;

import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;

public class RunEventsHandler {
    //main class
    public static void main(String[] args){
        //input
        String inputFile = "output/output_events.xml.gz";
        //an event creation manager, eventsManager the manager, store everything
        // is past to event reader, an object that we just create then puts into the handler, handler
        EventsManager eventsManager = EventsUtils.createEventsManager();

        SimpleEventHandler eventHandler = new SimpleEventHandler();
        eventsManager.addHandler(eventHandler);

        //reader will read the output file, manager is a basic structure of MATSim, where Handler will observe all your events
        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
        eventsReader.readFile(inputFile);

    }

}
