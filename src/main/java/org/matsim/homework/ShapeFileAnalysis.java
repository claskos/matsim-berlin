package org.matsim.homework;

import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Collectors;


// Reads a shape file (.shp) and returns the point coordinates for a single specified shape
// Check the attribute and ID key-value pair in Via
public class ShapeFileAnalysis {

    public static Coord @NotNull [] getShapeCoordinates(String shapeFileName, String shapeAttribute, String shapeID) {

        // Retrieve all features/shapes from the file
        var features = ShapeFileReader.getAllFeatures(shapeFileName);

        // Filter the features by attribute for the wanted geometries unique ID
        var geometries = features.stream()
                .filter(simpleFeature -> simpleFeature.getAttribute(shapeAttribute).equals(shapeID))
                .map(simpleFeature -> (Geometry) simpleFeature.getDefaultGeometry())
                .collect(Collectors.toList());
        // The required geometry is then the only one in the list
        Geometry shapeGeometry = geometries.get(0);

        // Transform from geo Coordinate[] to matsim Coord[]
        var geotoolsCoordinate = shapeGeometry.getCoordinates();
        Coord[] matsimCoord = new Coord[geotoolsCoordinate.length];
        for(int i = 0; i < geotoolsCoordinate.length; i++) {
            matsimCoord[i] = MGC.coordinate2Coord(geotoolsCoordinate[i]);
        }

        // Returns matsim Coord[]. Access XY from a Coordinate with .getX() or .getY() respectively
        return matsimCoord;
    }


    public static void main(String[] args) {

        String shapeFileName = null;
        String shapeAttribute = null;
        String shapeID = null;

        if (args.length > 0) {
            if (!args[0].equals("null")) shapeFileName = args[0];
            if (!args[1].equals("null")) shapeAttribute = args[1];
            if (!args[2].equals("null")) shapeID = args[2];
        }
        else {
            shapeFileName = "C:\\Users\\Nico\\Documents\\Nico\\Uni\\Module\\Multi-agent transport simulation\\Shape Umweltzone\\Umweltzone Berlin.shp";
            shapeAttribute = "spatial_na";
            shapeID = "Umweltzone";
        }

        // Retrieve list containing the coordinates of every boundary point of the shape
        Coord[] shapeCoord = ShapeFileAnalysis.getShapeCoordinates(shapeFileName, shapeAttribute, shapeID);

        // Write to a .txt file
        String txtFileName = Paths.get(shapeFileName).getParent().toString() + "\\Coordinates.txt";
        try {
            FileWriter writer = new FileWriter(txtFileName, true);
            for (Coord coord : shapeCoord) {
                writer.write(coord.getX() + "," + coord.getY() + "\r\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
