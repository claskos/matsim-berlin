package org.matsim.homework2;

import org.matsim.api.core.v01.network.Link;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.network.NetworkUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CO2Analysis {
    public static void main(String[] args) {
        List<String> cases = Arrays.asList("baseline", "case1", "case2", "case3", "case4", "case5");

        for (String curr_case : cases) {
            var events_file = "scenario/air-pollution/" + curr_case + ".xml.gz";
            var event_handler = new EmissionEventHandler();
            var event_manager = EventsUtils.createEventsManager();
            event_manager.addHandler(event_handler);
            EventsUtils.readEvents(event_manager, events_file);
//        System.out.println(event_handler.cold_emissions.size());
//        System.out.println(event_handler.warm_emissions.size());
            try {
                Files.write(Paths.get("hw2_correct_speed_evaluation/air-pollution/" + curr_case + "_cold.txt"), event_handler.cold_emissions);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                Files.write(Paths.get("hw2_correct_speed_evaluation/air-pollution/" + curr_case + "_warm.txt"), event_handler.warm_emissions);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
