package org.matsim.homework;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.vehicles.VehicleUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripAnalysis {

    public static void main(String[] args) {
        var plansFilePath_hw = "/home/asdf/IdeaProjects/matsim-berlin/scenarios/berlin-v5.5-1pct/output-berlin-v5.5-1pct-hw/berlin-v5.5-1pct.output_plans.xml.gz";
        var networkFilePath_hw = "/home/asdf/IdeaProjects/matsim-berlin/scenarios/berlin-v5.5-1pct/output-berlin-v5.5-1pct-hw/berlin-v5.5-1pct.output_network.xml.gz";
        //var tripsFilePath_hw = "/home/asdf/IdeaProjects/matsim-berlin/scenarios/berlin-v5.5-1pct/output-berlin-v5.5-1pct-hw/berlin-v5.5-1pct.output_trips.csv.gz";

        var plansFilePath_baseline = "/home/asdf/IdeaProjects/matsim-berlin/scenarios/berlin-v5.5-1pct/output-berlin-v5.5-1pct-baseline/berlin-v5.5-1pct.output_plans.xml.gz";
        var networkFilePath_baseline = "/home/asdf/IdeaProjects/matsim-berlin/scenarios/berlin-v5.5-1pct/output-berlin-v5.5-1pct-baseline/berlin-v5.5-1pct.output_network.xml.gz";

        //var shapeFilePath = "/home/asdf/Downloads/Bezirke_Berlin/Berlin_Bezirke.shp";
        //var transformation = TransformationFactory.getCoordinateTransformation("EPSG:31468", "EPSG:3857");

        /*var features = ShapeFileReader.getAllFeatures(shapeFilePath);

        var geometries = features.stream()
                .filter(simpleFeature -> simpleFeature.getAttribute("Gemeinde_n").equals("Mitte"))
                .map(simpleFeature -> (Geometry) simpleFeature.getDefaultGeometry())
                .collect(Collectors.toList());

        var mitte = geometries.get(0);
        var counter = 0;*/

        {
            var network_hw = NetworkUtils.readNetwork(networkFilePath_hw);
            var population_hw = PopulationUtils.readPopulation(plansFilePath_hw);

            Map<Id<Person>, AffectedPerson> affected_persons_hw = getAffectedPersons(population_hw, network_hw);
            System.out.println(affected_persons_hw.size() + " persons directly affected in hw scenario.");
            writePersonsToJSON(affected_persons_hw, "/home/asdf/IdeaProjects/matsim-berlin/scenarios/berlin-v5.5-1pct/output-berlin-v5.5-1pct-hw/affected_persons_hw.json");
            network_hw = null;
            population_hw = null;
        }
        System.gc();
        {
            var network_baseline = NetworkUtils.readNetwork(networkFilePath_baseline);
            var population_baseline = PopulationUtils.readPopulation(plansFilePath_baseline);

            Map<Id<Person>, AffectedPerson> affected_persons_baseline = getAffectedPersons(population_baseline, network_baseline);
            System.out.println(affected_persons_baseline.size() + " persons directly affected in baseline scenario.");
            writePersonsToJSON(affected_persons_baseline, "/home/asdf/IdeaProjects/matsim-berlin/scenarios/berlin-v5.5-1pct/output-berlin-v5.5-1pct-baseline/affected_persons_baseline.json");
            network_baseline = null;
            population_baseline = null;
        }
        System.gc();

//        var eventsFileBaseline = "/home/asdf/IdeaProjects/matsim-berlin/scenarios/berlin-v5.5-1pct/output-berlin-v5.5-1pct-baseline/berlin-v5.5-1pct.output_events.xml.gz";
//        var handler = new LinkEventHandler();
//        var manager = EventsUtils.createEventsManager();
//        manager.addHandler(handler);
//        EventsUtils.readEvents(manager, eventsFileBaseline);
//        System.out.println(handler.volume.values());
    }

    private static Map<Id<Person>, AffectedPerson> getAffectedPersons(Population population, Network network) {
        Map<Id<Person>, AffectedPerson> affected_persons = new HashMap<>();

        for (Person person : population.getPersons().values()) {
            var plan = person.getSelectedPlan();
            var activities = TripStructureUtils.getActivities(plan, TripStructureUtils.StageActivityHandling.ExcludeStageActivities);

            for (Activity activity : activities) {
                var coord = activity.getCoord();

                if (isInArea(coord, network)) {
                    if (!affected_persons.containsKey(person.getId())) {
                        AffectedPerson new_affected_person = new AffectedPerson();
                        new_affected_person.person_obj = person;
                        new_affected_person.home_zone = person.getAttributes().getAttribute("home-activity-zone").toString();
                        new_affected_person.activities_in_zone.add(activity);
                        affected_persons.put(person.getId(), new_affected_person);
                    }
                    else {
                        AffectedPerson new_affected_person = affected_persons.get(person.getId());
                        new_affected_person.activities_in_zone.add(activity);
                    }
                    // person attributes: { key=home-activity-zone; object=brandenburg }{ key=income; object=1142.0 }{ key=subpopulation; object=person }
                    //System.out.println(activity.getAttributes());
                }
            }
        }
        return affected_persons;
    }

    private static void writePersonsToJSON (Map<Id<Person>, AffectedPerson> affected_persons, String filename) {
        JSONArray ar = new JSONArray();
        for (AffectedPerson ap : affected_persons.values()) {
            JSONObject curr_person = new JSONObject();
            curr_person.put("id", ap.person_obj.getId().toString());
            curr_person.put("home_region", ap.home_zone);
            curr_person.put("income", ap.person_obj.getAttributes().getAttribute("income").toString());
            JSONArray activities = new JSONArray();
            for (Activity act : ap.activities_in_zone) {
                activities.add(act.getType().split("_")[0]);
            }
            curr_person.put("activities", activities);
            ar.add(curr_person);
        }
        JSONObject outJSON = new JSONObject();
        outJSON.put("persons", ar);

        try (FileWriter file = new FileWriter(filename)) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(outJSON.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class AffectedPerson {
        public Person person_obj;
        public String home_zone;
        public List<Activity> activities_in_zone = new ArrayList<>();
        //public List<String> modes_of_transport = new ArrayList<>();
        //public double distance_travelled;
        //public double travel_time;
    }



    private static boolean isInArea(Coord coord, Network network) {
        double top = network.getNodes().get(Id.createNodeId(26736196)).getCoord().getY();
        double bottom = network.getNodes().get(Id.createNodeId(26731247)).getCoord().getY();
        double left = network.getNodes().get(Id.createNodeId(26740508)).getCoord().getX();
        double right = network.getNodes().get(Id.createNodeId(3366747771L)).getCoord().getX();

        return coord.getX() > left && coord.getX() < right && coord.getY() < top && coord.getY() > bottom;
    }
}
