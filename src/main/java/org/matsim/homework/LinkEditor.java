package org.matsim.homework;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LinkEditor {
    public static void editLinks(String filename, Network network) {
        //int capacity = 10;
        //int free_speed = 10;
        Set<String> transport_modes = new HashSet<>();
        transport_modes.add(TransportMode.bike);
        transport_modes.add(TransportMode.walk);

        try {
            List<String> link_ids = Files.readAllLines(Paths.get(filename));
            Map<Id<Link>, ? extends Link> all_links = network.getLinks();
            for (String link_id : link_ids) {
                if(all_links.containsKey(Id.createLinkId(link_id))) {
                    Link link = all_links.get(Id.createLinkId(link_id));
                    //link.setCapacity(capacity);
                    //link.setFreespeed(free_speed);
                    link.setAllowedModes(transport_modes);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
