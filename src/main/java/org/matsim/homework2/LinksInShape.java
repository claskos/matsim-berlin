package org.matsim.homework2;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LinksInShape {

    public static List<Link> getLinksInShape(Network network) {
        var shapeFilePath = "Bezirke_Berlin/Berlin_Bezirke.shp";
        // Choose what shape to filter for
        String attributeName = "Land_name";
        String attributeValue = "Berlin";

        // Get all geometries by filtering the shape file
        var features = ShapeFileReader.getAllFeatures(shapeFilePath);
        var geometries = features.stream()
                .filter(simpleFeature -> simpleFeature.getAttribute(attributeName).equals(attributeValue))
                .map(simpleFeature -> (Geometry) simpleFeature.getDefaultGeometry())
                .collect(Collectors.toList());

        // List to collect all links in shape
        List<Link> linksInShape = new ArrayList<>();

        // Get all links from the network
        var links = network.getLinks().values();

        // Coordinate transformation for casting Coord to Point
        final CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation("EPSG:31468", "EPSG:3857");

        // Filter all links for the links located inside or on the edges of the selected shape
        for (var l : links) {
            var fromCoord = transformation.transform(l.getFromNode().getCoord());
            var toCoord = transformation.transform(l.getToNode().getCoord());
            var fromPoint = MGC.coord2Point(fromCoord);
            var toPoint = MGC.coord2Point(toCoord);
            for (Geometry g : geometries) {
                if (g.covers(fromPoint) || g.covers(toPoint)) {
                    if (!l.getId().toString().contains("pt")) {
                        linksInShape.add(l);
                    }
                }
            }
        }

        // Remove duplicate links
        Set<Link> linksWithoutDuplicates = new HashSet<>(linksInShape);
        linksInShape.clear();
        linksInShape.addAll(linksWithoutDuplicates);

        // Return all links found contained in and crossing the chosen geometries
        return linksInShape;
    }
}
