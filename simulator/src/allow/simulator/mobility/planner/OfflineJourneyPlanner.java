package allow.simulator.mobility.planner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a journey planner for the Allow Ensembles urban traffic
 * simulation. Implements the IPlannerService interface. Accesses a
 * repository of stored journeys to answer queries.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class OfflineJourneyPlanner extends OTPJourneyPlanner {

	// Repository of journeys to answer requests.
	private JourneyRepository repos;
	
	/**
	 * Constructor.
	 * Creates a new instance of OfflineJourneyPlanner accessing an underlying
	 * repository of journeys.
	 * 
	 * @param repos Underlying journey repository.
	 * @param tracesFile Path to file to write request/response pairs to.
	 */
	public OfflineJourneyPlanner(JourneyRepository repos, Path tracesFile) {
		this.repos = repos;
	}

//	@Override
//	public List<Itinerary> requestSingleJourney(JourneyRequest request) {
//		String itString = repos.getItineraries(request);
//		List<Itinerary> it = new ArrayList<Itinerary>();
//
//		// Parse answer here.
//		try {
//			JsonNode root = mapper.readTree(itString);
//			JsonNode error = root.get("error");
//
//			if (error != null) return null;
//
//			// Parse answer.
//			JsonNode travelPlan = root.get("plan");
//			JsonNode itineraries = travelPlan.get("itineraries");
//			
//			for (Iterator<JsonNode> jt = itineraries.elements(); jt.hasNext();) {
//				JsonNode next = jt.next();
//				Itinerary nextIt = parseItinerary(next, request.isTaxiRequest);
//				nextIt.from = request.From;
//				nextIt.to = request.To;
//				it.add(nextIt);
//			}
//
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return it;
//	}

	@Override
	public boolean requestSingleJourney(JourneyRequest request, List<Itinerary> itineraries) {
		String itString = repos.getItineraries(request);

		// Parse answer here.
		try {
			JsonNode root = mapper.readTree(itString);
			JsonNode error = root.get("error");

			if (error != null) return false;

			// Parse answer.
			JsonNode travelPlan = root.get("plan");
			JsonNode it = travelPlan.get("itineraries");
			
			for (Iterator<JsonNode> jt = it.elements(); jt.hasNext();) {
				JsonNode next = jt.next();
				Itinerary nextIt = parseItinerary(next, request.isTaxiRequest);
				nextIt.from = request.From;
				nextIt.to = request.To;
				itineraries.add(nextIt);
			}

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

}
