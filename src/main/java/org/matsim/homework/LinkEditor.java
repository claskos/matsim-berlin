package org.matsim.homework;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LinkEditor {
    public static void editLinksFromFile(String filename, Network network) {
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

    public static void editLinksInArea(Network network) {
        //Set<String> transport_modes = new HashSet<>();
        //transport_modes.add(TransportMode.bike);
        //transport_modes.add(TransportMode.walk);
        double capacity = 1;
        double free_speed = 1;

        Collection<? extends Node> nodes = network.getNodes().values();
        HashMap<Id<Node>, Node> nodes_to_edit = new HashMap<>();

        //border points of rectangle
        double top = network.getNodes().get(Id.createNodeId(26736196)).getCoord().getY();
        double bottom = network.getNodes().get(Id.createNodeId(26731247)).getCoord().getY();
        double left = network.getNodes().get(Id.createNodeId(26740508)).getCoord().getX();
        double right = network.getNodes().get(Id.createNodeId(3366747771L)).getCoord().getX();

        // get all nodes that are in the area of interest
        for (Node node : nodes) {
            if (isInArea(node, top, bottom, left, right)) {
                if(!node.getId().toString().contains("pt")) {
                    nodes_to_edit.put(node.getId(), node);
                }
            }
        }

        //change incoming and outgoing links of the nodes select, but only if the node on the other end, is also in area of interest
        for (Node node : nodes_to_edit.values()) {
            Collection<? extends Link> in_links = node.getInLinks().values();
            for (Link link : in_links) {
                if (nodes_to_edit.containsKey(link.getFromNode().getId())) {
                    //link.setAllowedModes(transport_modes);
                    link.setCapacity(capacity);
                    link.setFreespeed(free_speed);
                }
            }

            Collection<? extends Link> out_links = node.getOutLinks().values();
            for (Link link : out_links) {
                if (nodes_to_edit.containsKey(link.getToNode().getId())) {
                    //link.setAllowedModes(transport_modes);
                    link.setCapacity(capacity);
                    link.setFreespeed(free_speed);
                }
            }
        }
    }

    private static boolean isInArea(Node node, double top_y, double bottom_y, double left_x, double right_x) {
        // check if given node is in the area of interest
        Coord coord = node.getCoord();

        return coord.getX() > left_x && coord.getX() < right_x && coord.getY() < top_y && coord.getY() > bottom_y;
    }
}
