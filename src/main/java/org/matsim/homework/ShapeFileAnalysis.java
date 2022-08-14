package org.matsim.homework;

//import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/*
This class reads a shape file (.shp) and writes all coordinates of a chosen geometry into a .txt file. The geometry is
chosen by specifying an attribute-ID key-value pair. Check the shape file in Via to find the relevant key-value pair.
 */
public class ShapeFileAnalysis {

    // Retrieves all coordinates of a specified geometry from a specified shape file.
    public static Coord[] getShapeCoordinates(String shapeFileName, String shapeAttribute, String shapeID) {

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

        // Returns matsim Coord[]. Access the x-, y-values from the coordinates with .getX() or .getY() respectively
        return matsimCoord;
    }


    public static void main(String[] args) {

        String shapeFileName = null;
        String shapeAttribute = null;
        String shapeID = null;

        if (args.length > 2) {
            if (!args[0].equals("null")) shapeFileName = args[0];
            if (!args[1].equals("null")) shapeAttribute = args[1];
            if (!args[2].equals("null")) shapeID = args[2];
        }
        else {
            // Feedback if no shape file and attribute-ID pair is given
            System.out.println("ShapeFileAnalysis was called without sufficient arguments. Using default arguments.");

            // Default values for testing purposes
            shapeFileName = "/home/asdf/IdeaProjects/matsim-berlin/scenarios/berlin-v5.5-1pct/shape files/Berlin_Bezirke.shp";
            shapeAttribute = "Gemeinde_n";
            shapeID = "Mitte";
        }

        // Retrieve list containing the coordinates of every boundary point of the shape
        Coord[] shapeCoord = ShapeFileAnalysis.getShapeCoordinates(shapeFileName, shapeAttribute, shapeID);

        // Write to a .txt file
        String txtFileName = Paths.get(shapeFileName).getParent().toString() + "/Shape_Coordinates_" + shapeID + ".txt";
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
