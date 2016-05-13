package allow.simulator.mobility.planner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import allow.simulator.mobility.data.TType;
import allow.simulator.util.Coordinate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class OTPJourneyPlanner implements IPlannerService {
	// Json mapper to parse planner responses.
	protected static ObjectMapper mapper = new ObjectMapper();

	/**
	 * Parses an itinerary from a Json node.
	 * 
	 * @param it Json node.
	 * @return Itinerary parsed from given node.
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	protected Itinerary parseItinerary(JsonNode it, boolean isTaxi) throws JsonParseException,
			JsonMappingException, IOException {
		Itinerary newIt = new Itinerary();
		newIt.startTime = it.get("startTime").asLong();
		newIt.endTime = it.get("endTime").asLong();
		newIt.walkTime = it.get("walkTime").asLong();
		newIt.transitTime = it.get("transitTime").asLong();
		newIt.waitingTime = it.get("waitingTime").asLong();
		newIt.walkDistance = it.get("walkDistance").asDouble();
		newIt.transfers = it.get("transfers").asInt();
		newIt.duration = it.get("duration").asLong();
		JsonNode legNode = it.get("legs");

		for (Iterator<JsonNode> jt = legNode.elements(); jt.hasNext();) {
			Leg newLeg = parseLeg(jt.next(), isTaxi);
			newIt.addLeg(newLeg);
			newIt.costs += newLeg.costs;
		}
		newIt.itineraryType = Itinerary.getItineraryType(newIt);
		
		if (newIt.itineraryType == 1) {
			newIt.costs = 1.2;
		}
		return newIt;
	}

	/**
	 * Parses a leg from a Json node.
	 * 
	 * @param lt Json node.
	 * @return Leg parsed from given Json node.
	 */
	protected Leg parseLeg(JsonNode lt, boolean isTaxi) {
		Leg newLeg = new Leg();
		newLeg.startTime = Long.parseLong(lt.get("startTime").asText());
		newLeg.endTime = Long.parseLong(lt.get("endTime").asText());
		newLeg.distance = lt.get("distance").asDouble();
		newLeg.from = parsePlace(lt.get("from"));
		newLeg.stopIdFrom = parseStopId(lt.get("from"));
		newLeg.to = parsePlace(lt.get("to"));
		newLeg.stopIdTo = parseStopId(lt.get("to"));
		newLeg.legGeometry = lt.get("legGeometry").get("points").asText();
		newLeg.mode = TType.valueOf(lt.get("mode").asText());
		
		switch (newLeg.mode) {
		case WALK:
			newLeg.costs = 0.0;
			break;
			
		case BICYCLE:
			newLeg.costs = newLeg.distance * 0.000005;
			break;
			
		case BUS:
		case CABLE_CAR:
			newLeg.routeId = lt.get("routeId").asText();
			newLeg.agencyId = lt.get("agencyId").asText();
			newLeg.tripId = lt.get("tripId").asText();			
			newLeg.costs = 1.2;
			newLeg.stops = parseStopIds(lt.get("intermediateStops"));
			break;
			
		case CAR:
			if (isTaxi) {
				newLeg.costs = newLeg.distance * 0.001; // 0.0004;
						
			} else {
				newLeg.costs = newLeg.distance * 0.00035; // 0.00025;
			}
			break;
			
		case RAIL:
			newLeg.costs = 20.0;
			break;
			
		case TRANSIT:
		default:
			throw new IllegalArgumentException("Error: Unknown transport type " + newLeg.mode);
		}
		
		List<String> nodes = new ArrayList<String>();
		for (Iterator<JsonNode> it = lt.get("osmNodes").iterator(); it.hasNext(); ) {
			nodes.add(it.next().asText());
		}
		newLeg.osmNodes = nodes;
		return newLeg;
	}

	/**
	 * Parses a stop Id from a Json node.
	 * 
	 * @param lt Json node.
	 * @return Stop Id parsed from given Json node.
	 */
	protected String parseStopId(JsonNode place) {
		JsonNode stopId = place.get("stopId");
		
		if (stopId != null) {
			String tokens[] = stopId.asText().split(":");
			return tokens[1];
		}
		return null;
	}
	
	protected List<String> parseStopIds(JsonNode stops) {
		List<String> ids = new ArrayList<String>();
		
		for (Iterator<JsonNode> it = stops.iterator(); it.hasNext(); ) {
			ids.add(parseStopId(it.next()));
		}
		return ids;
	}
	/**
	 * Parses a coordinate from a Json node.
	 * 
	 * @param lt Json node.
	 * @return Coordinate parsed from given Json node.
	 */
	protected Coordinate parsePlace(JsonNode place) {
		Coordinate c = new Coordinate(place.get("lon").doubleValue(),
				place.get("lat").doubleValue());
		return c;
	}
	
}
