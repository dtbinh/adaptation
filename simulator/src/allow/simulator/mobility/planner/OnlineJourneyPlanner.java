package allow.simulator.mobility.planner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import allow.simulator.mobility.data.IDataService;
import allow.simulator.mobility.data.TType;
import allow.simulator.world.Street;
import allow.simulator.world.StreetMap;
import allow.simulator.world.StreetNode;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a journey planner for the Allow Ensembles urban traffic
 * simulation. Implements the IPlannerService interface. Encapsulates a client
 * querying OpenTripPlanner web service.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public final class OnlineJourneyPlanner extends OTPJourneyPlanner {
	// URI to send requests to.
	private static URI routingURI;

	static {

		try {
			routingURI = new URI("/otp/routers/default/plan");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	// Client to send requests.
	private HttpClient client;

	// Host requests are send to.
	private HttpHost target;

	/**
	 * Creates a new instance of OnlineJourneyPlanner sending requests to
	 * an OpenTripPlanner server instance.
	 * 
	 * @param host Host running OpenTripPlanner service.
	 * @param port Port of OpenTripPlanner service.
	 * @param world Simulated world.
	 * @param tracesFile Path to file to write request/response pairs to.
	 */
	public OnlineJourneyPlanner(String host, int port, Path tracesFile) {
		target = new HttpHost(host, port, "http");
		client = new DefaultHttpClient();
	}

//	@Override
//	public synchronized List<Itinerary> requestSingleJourney(JourneyRequest request) {
//		return requestSingleJourney(request, new ArrayList<Itinerary>());
//	}

	@Override
	public synchronized boolean requestSingleJourney(JourneyRequest request, List<Itinerary> itineraries) {
		// Build query string.
		StringBuilder paramBuilder = new StringBuilder();
		paramBuilder.append(routingURI.toString());
		paramBuilder.append("?toPlace=");
		paramBuilder.append(request.To.y + "," + request.To.x);
		paramBuilder.append("&fromPlace=");
		paramBuilder.append(request.From.y + "," + request.From.x);
		paramBuilder.append("&date=" + request.Date);

		if (request.ArrivalTime != null) {
			paramBuilder.append("&time=" + request.ArrivalTime);
			paramBuilder.append("&arriveBy=true");
		} else {
			paramBuilder.append("&time=" + request.DepartureTime);
			paramBuilder.append("&arriveBy=false");
		}
		paramBuilder.append("&optimize=" + request.RouteType.toString());
		paramBuilder.append("&mode=");

		for (int i = 0; i < request.TransportTypes.length - 1; i++) {
			paramBuilder.append(request.TransportTypes[i].toString() + ",");
		}
		paramBuilder.append(request.TransportTypes[request.TransportTypes.length - 1]);
		paramBuilder.append("&numItineraries=" + request.ResultsNumber);
		paramBuilder.append("&walkSpeed=" + 1.29);
		paramBuilder.append("&bikeSpeed=" + 4.68);
		paramBuilder.append("&showIntermediateStops=true");
		// paramBuilder.append("&maxWalkDistance=" +
		// journey.MaximumWalkDistance);

		// Create new get request and set URI.
		HttpGet getRequest = new HttpGet();

		try {
			getRequest.setURI(new URI(paramBuilder.toString()));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		try {
			// Execute request and receive response.
			HttpResponse httpResponse = client.execute(target, getRequest);
			String res = EntityUtils.toString(httpResponse.getEntity());

			// Check for errors.
			JsonNode root = mapper.readTree(res);
			JsonNode error = root.get("error");

			if (error != null)
				return false;

			// Parse response.
			JsonNode travelPlan = root.get("plan");
			JsonNode it = travelPlan.get("itineraries");

			for (Iterator<JsonNode> jt = it.elements(); jt.hasNext();) {
				JsonNode next = jt.next();
				Itinerary nextIt = parseItinerary(next, request.isTaxiRequest);
				nextIt.from = request.From;
				nextIt.to = request.To;
				nextIt.reqId = request.reqId;
				nextIt.reqNumber = request.reqNumber;

				

				if (request.entity != null) {
					StreetMap map = request.entity.getContext().getWorld().getStreetMap();
					
					// Add segments to legs.
					for (Leg l : nextIt.legs) {
						l.streets = new ArrayList<Street>();

						if (l.mode == TType.CAR || l.mode == TType.BICYCLE || l.mode == TType.WALK) {

							for (int j = 0; j < l.osmNodes.size() - 1; j++) {
								String first = normalize(l.osmNodes.get(j), map);
								String second = normalize(l.osmNodes.get(j + 1), map);
								Street street = map.getStreet(first, second);

								if (street != null) {
									l.streets.add(street);
									continue;
								} 
							}

						} else {
							IDataService dataService = request.entity
									.getContext().getDataServices().get(0);
							List<String> allStops = new ArrayList<String>(l.stops.size() + 2);
							allStops.add(l.stopIdFrom);
							
							for (int j = 0; j < l.stops.size(); j++) {
								allStops.add(l.stops.get(j));
							}
							allStops.add(l.stopIdTo);
							
							for (int j = 0; j < allStops.size() - 1; j++) {
								String first = allStops.get(j);
								String second = allStops.get(j + 1);
								List<Street> segs = dataService.getBusstopRoutingStreet(first, second);

								if (segs != null)
									l.streets.addAll(segs);
							}
						}
					}
					nextIt.initialWaitingTime = Math.max((nextIt.startTime - request.entity.getContext().getTime().getTimestamp()) / 1000, 0);
					nextIt.isTaxiItinerary = request.isTaxiRequest;
				}
				itineraries.add(nextIt);
			}
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private static String normalize(String nodeLabel, StreetMap map) {
		if (nodeLabel.startsWith("osm:node") || nodeLabel.startsWith("split"))
			// These are nodes which have the same label as in the planner.
			return nodeLabel;
		String tokens[] = nodeLabel.split("_");

		if (tokens.length == 1) {
			// These are unknown nodes.
			return "";
		}
		// These are intermediate nodes which can be determined by their position.
		// Planner returns "streetname_lat,lon".
		StreetNode n = map.getStreetNodeFromPosition(tokens[1]);

		if (n == null) {
			return "";
		}
		return n.getLabel();
	}
}
