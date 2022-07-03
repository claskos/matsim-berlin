package org.matsim.analysis.homework;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
This class takes a Coord.toString() and checks which district it lies in from the used shape file
 */
public class DistrictFinder {

    // Transform a coordinate from DHDN / 3-degree Gauss-Kruger zone 4 to WGS84 Web Mercator
    private static final CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation("EPSG:31468", "EPSG:3857");

    // Check if a coordinate lies inside a geometry
    public static Boolean isInGeometry(Coord location, Geometry area) {

        // Check if a given coordinate is located inside a geometry
        var geotoolsPoint = MGC.coord2Point(location);
        return area.contains(geotoolsPoint);
    }

    // Retrieve all districts (geometries mapped to names) from the used shape file. Can also be used to retrieve
    // any other attribute from an arbitrary shape file by changing the shape file and attribute name.
    public static Map<Geometry, String> getAllDistricts(String shapeFileName) {

        // Retrieve all features/shapes from the file
        var features = ShapeFileReader.getAllFeatures(shapeFileName);

        // Stream the features to collect all included geometries/districts
        var districtGeometries = features.stream()
                .map(simpleFeature -> (Geometry) simpleFeature.getDefaultGeometry())
                .collect(Collectors.toList());

        // Retrieve the names associated with the districts
        var districtNames = features.stream()
                .map(simpleFeature -> (String) simpleFeature.getAttribute("Gemeinde_n"))
                .collect(Collectors.toList());

        // Map the district geometries to their names
        Map<Geometry, String> districtMap = new HashMap<>();
        for (int i = 0; i < districtNames.size(); i++) {
            districtMap.put(districtGeometries.get(i), districtNames.get(i));
        }

        return districtMap;
    }

    public static String getDistrict(Double x, Double y) {

        // Used shape file
        String shapeFileName = "/home/asdf/IdeaProjects/matsim-berlin/scenarios/berlin-v5.5-1pct/shape files/Berlin_Bezirke.shp";

        // Retrieve relevant district data
        Map<Geometry, String> districtMap = getAllDistricts(shapeFileName);

        // Transform the target coordinate to the coordinate system used by org.locationtech.jts.geom.Geometry
        var transformedLoc = transformation.transform(new Coord(x, y));
        var location = MGC.coord2Point(transformedLoc);

        // If any of the districts contains the given coordinate, return the district name
        for (Geometry g : districtMap.keySet()) {
            if (g.covers(location)) {
                return districtMap.get(g);
            }
        }

        return "null";
    }

    // Pass Coord objects as strings with .toString() method
    public static void main(String[] args) {

        String district = "null";
        double x = -1;
        double y = -1;
        String[] coordString;
        String xString = "null";
        String yString = "null";

        if (args.length > 0) {
            if (!args[0].equals("null")) {
                // Split Coord string representation into the parts containing the x and y coordinates
                coordString = args[0].split("y");
                // Eliminate non-relevant characters from String
                xString = coordString[0].replaceAll("[^\\d\\.]", "");
                yString = coordString[1].replaceAll("[^\\d\\.]", "");
                // Extract number values
                x = Double.parseDouble(xString);
                y = Double.parseDouble(yString);

                // Retrieve district name where the target coordinate is located
                district = DistrictFinder.getDistrict(x, y);
                if (!district.equals("null")) System.out.println("The coordinate is in: " + district);
            }

        }
        else {
            // Feedback for wrong method call
            System.out.println("DistrictFinder was called without arguments.");
        }

    }

}
