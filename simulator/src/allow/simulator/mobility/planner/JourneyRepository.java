package allow.simulator.mobility.planner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import allow.simulator.util.Coordinate;

public class JourneyRepository {

	// The possible trips.
	private List<Coordinate[]> journeys;

	// Solutions.
	private Map<String, String> solutions;
	
	// Random generator.
	private static Random rg = new Random(System.currentTimeMillis());

	public JourneyRepository(String path) throws IOException {
		loadJourneys(path);
	}

	private void loadJourneys(String path) throws IOException {
		journeys = new ArrayList<Coordinate[]>();
		solutions = new HashMap<String, String>();
		String file = path + "journeys";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		boolean notEOF = true;

		while (notEOF) {
			// Read agencyId and response.
			String req = reader.readLine();
			String res = reader.readLine();

			if (req == null || res == null) {
				// If end of file reached, stop reading.
				notEOF = false;

			} else {
				// Create trip and store trip and solution.
				String tokens[] = req.split(" ");
				Coordinate start = new Coordinate(Double.valueOf(tokens[0]), Double.valueOf(tokens[1]));
				Coordinate dest = new Coordinate(Double.valueOf(tokens[2]), Double.valueOf(tokens[3]));
				solutions.put(start.x + " " + start.y + " " + dest.x + " " + dest.y, res);
				journeys.add(new Coordinate[] { start, dest });
			}
		}
		reader.close();
	}

	public String getItineraries(JourneyRequest t) {
		String key = t.From.y + " " + t.From.x + " " + t.To.y + " " + t.To.x;
		return solutions.get(key);
	}

	/**
	 * Returns a random journey from the repository.
	 * 
	 * @return Random journey from the repository.
	 */
	public Coordinate[] getRandomTrip() {
		return journeys.get(rg.nextInt(journeys.size()));
	}
	
}
