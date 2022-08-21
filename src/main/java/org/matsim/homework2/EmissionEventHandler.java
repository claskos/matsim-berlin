package org.matsim.homework2;

import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.handler.BasicEventHandler;

import java.util.ArrayList;

public class EmissionEventHandler implements BasicEventHandler {

    ArrayList<String> cold_emissions = new ArrayList<>();
    ArrayList<String> warm_emissions = new ArrayList<>();

    @Override
    public void handleEvent(Event event) {
        if (event.getEventType().equals("coldEmissionEvent")) {
            var event_attributes = event.getAttributes();
            if (event_attributes.get("linkId").contains("pt")) {
                return;
            }
            cold_emissions.add(event_attributes.get("CO2_TOTAL"));
        }
        else if (event.getEventType().equals("warmEmissionEvent")) {
            var event_attributes = event.getAttributes();
            if (event_attributes.get("linkId").contains("pt")) {
                return;
            }
            warm_emissions.add(event_attributes.get("CO2_TOTAL"));
        }
    }
}
