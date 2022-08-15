package org.matsim.homework2;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import scala.xml.Null;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LinkEditorHw2 {

    public static void editLinks(List<Link> links, int hw_case) {
        // link_types: [secondary, unclassified, residential, primary_link,
        //              tertiary, living_street, motorway_link, secondary_link,
        //              trunk, motorway, trunk_link, primary]
        if (hw_case == 1) {
            // blocked traffic in residential and living streets
            double capacity = 1;
            double free_speed = 1;

            for (Link link : links) {
                Object link_type_obj = link.getAttributes().getAttribute("type");
                if (link_type_obj != null) {
                    String link_type = link_type_obj.toString();
                    if (link_type.equals("residential") || link_type.equals("living_street")) {
                        //link.setAllowedModes(transport_modes);
                        link.setCapacity(capacity);
                        link.setFreespeed(free_speed);
                    }
                }
            }
        }
        else if (hw_case == 2) {
            // 10 km/h traffic in residential and living streets
            double free_speed = 2.8;

            for (Link link : links) {
                Object link_type_obj = link.getAttributes().getAttribute("type");
                if (link_type_obj != null) {
                    String link_type = link_type_obj.toString();
                    if (link_type.equals("residential") || link_type.equals("living_street")) {
                        if (link.getFreespeed() > free_speed) {
                            link.setFreespeed(free_speed);
                        }
                    }
                }
            }
        }
        else if (hw_case == 3) {
            // 30 km/h in primary and secondary streets, 10 km/h in residential and living streets
            double free_speed_residential = 2.8;
            double free_speed_main = 8.3;

            for (Link link : links) {
                Object link_type_obj = link.getAttributes().getAttribute("type");
                if (link_type_obj != null) {
                    String link_type = link_type_obj.toString();
                    if (link_type.equals("residential") || link_type.equals("living_street")) {
                        if (link.getFreespeed() > free_speed_residential) {
                            link.setFreespeed(free_speed_residential);
                        }
                    } else if (link_type.equals("primary") || link_type.equals("secondary") ||
                            link_type.equals("primary_link") || link_type.equals("secondary_link") ||
                            link_type.equals("tertiary")) {
                        if (link.getFreespeed() > free_speed_main) {
                            link.setFreespeed(free_speed_main);
                        }
                    }
                }
            }
        }
    }

}