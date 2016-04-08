package allow.util.layer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import allow.simulator.util.Coordinate;
import allow.simulator.world.StreetMap;
import allow.simulator.world.StreetNode;
import allow.simulator.world.layer.Area;
import allow.simulator.world.layer.DistrictLayer;
import allow.simulator.world.layer.DistrictType;
import allow.simulator.world.layer.Layer;

public class Main {
	
	public static void main(String args[]) throws IOException {
		Path rawGisOutput = Paths.get("/Users/Andi/Documents/DFKI/Allow Ensembles/Simulator/Trento/poly.txt");
		List<String> lines = Files.readAllLines(rawGisOutput);
		BufferedWriter wr = Files.newBufferedWriter(Paths.get("/Users/Andi/Documents/DFKI/Allow Ensembles/Simulator/Trento/partitioning.layer"), Charset.defaultCharset());
		List<Coordinate> parsedCoordinates = new ArrayList<Coordinate>();
		
		for (String line : lines) {
			System.out.println(line);
			String tokens[] = line.split("\t");
			
			// Parse coordinate part.
			String coordString = tokens[0].substring(0, tokens[0].length() - 2);
			coordString = coordString.substring(9, coordString.length());
			parseCoordinates(coordString, parsedCoordinates);
			wr.write(tokens[1].trim() + ";;" + tokens[2] + ";;");

			for (int i = 0; i < parsedCoordinates.size() - 1; i++) {
				Coordinate c = parsedCoordinates.get(i);
				wr.write(c.x + " " + c.y + ",");
			}
			Coordinate last = parsedCoordinates.get(parsedCoordinates.size() - 1);
			wr.write(last.x + " " + last.y + "\n");
		}

		// Now add schools.
		Path base = Paths.get("/Users/Andi/Documents/DFKI/Allow Ensembles/Repository/repos/Software/DFKI Simulator/NetLogo/data/world");
		StreetMap map = new StreetMap(base.resolve("trento.world"));
		map.addLayer(Layer.Type.DISTRICTS, base.resolve("partitioning.layer"));
		
		DistrictLayer l = (DistrictLayer) map.getLayer(Layer.Type.DISTRICTS);
		List<Area> residentialAreas = l.getAreasOfType(DistrictType.RESIDENTIAL);
		
		for (Area a : residentialAreas) {
			List<StreetNode> nodes = l.getPointsInArea(a);
			Coordinate c = nodes.get(ThreadLocalRandom.current().nextInt(nodes.size())).getPosition();
			
			wr.write(a.getName() + " School" + ";;" + "School" + ";;" + c.x + " " + c.y + "\n");
		}
		wr.close();
	}
	
	private static void parseCoordinates(String coordString, List<Coordinate> ret) {
		ret.clear();
		String coords[] = coordString.split(",");
		
		for (String coord : coords) {
			String xy[] = coord.split(" ");
			Coordinate c = new Coordinate(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
			
			if (!ret.contains(c)) ret.add(c);
		}
		
	}
}
