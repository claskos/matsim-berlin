package org.matsim.homework2;

import org.matsim.api.core.v01.network.Link;
import org.matsim.core.network.NetworkUtils;

import java.util.HashMap;

public class NetworkReader {
    public static void main(String[] args) {
        var network = NetworkUtils.readNetwork("/home/asdf/IdeaProjects/matsim-berlin/scenarios/berlin-v5.5-1pct/output-berlin-v5.5-1pct-baseline/berlin-v5.5-1pct.output_network.xml.gz");
        var types = new HashMap<String, Integer>();
        for (Link link : network.getLinks().values()) {
            Object type_obj = link.getAttributes().getAttribute("type");
            if(type_obj != null) {
                String type = type_obj.toString();
                if (!types.containsKey(type)) {
                    types.put(type, 0);
                }
            }

        }
        System.out.println(types.keySet());
    }
}
