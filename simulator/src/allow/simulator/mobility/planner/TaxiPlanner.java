package allow.simulator.mobility.planner;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import allow.simulator.core.Time;
import allow.simulator.entity.Entity;
import allow.simulator.entity.Person;
import allow.simulator.mobility.data.TType;
import allow.simulator.mobility.data.TaxiStop;
import allow.simulator.mobility.data.TaxiTrip;
import allow.simulator.util.Coordinate;
import allow.simulator.world.Street;

public final class TaxiPlanner implements IPlannerService {
	// List of planner services to create car trips
	private final List<IPlannerService> plannerServices;
	
	// Position of taxi rank 
	private final Coordinate taxiRank;
	
	// Time of simulation
	private final Time time;
	
	// Buffer to store planned trips which can be requested 
	private final Map<String, TaxiTrip> tripBuffer;

	public TaxiPlanner(List<IPlannerService> plannerServices, Time time, Coordinate taxiRank) {
		this.plannerServices = plannerServices;
		this.time = time;
		this.taxiRank = taxiRank;
		tripBuffer = new ConcurrentHashMap<String, TaxiTrip>();
	}
	
	public TaxiTrip getTaxiTrip(String tripId) {
		return tripBuffer.remove(tripId);
	}
	
//	@Override
//	public List<Itinerary> requestSingleJourney(JourneyRequest request) {
//		return requestSingleJourney(request, new ArrayList<Itinerary>());
//	}

	@Override
	public boolean requestSingleJourney(JourneyRequest request, List<Itinerary> itineraries) {
		// Get planner instance
		int randomPlannerId = ThreadLocalRandom.current().nextInt(plannerServices.size());
		IPlannerService planner = plannerServices.get(randomPlannerId);			
		TType modes[] = request.TransportTypes;
		boolean success = false;
		
		for (int i = 0; i < modes.length; i++) {
			Itinerary newIt = null;
			
			if (modes[i] == TType.TAXI) {
				newIt = createTaxiItinerary(request, planner);
	
			} else if (modes[i] == TType.SHARED_TAXI) {
				newIt = createSharedTaxiItinerary(request, planner);
			}

			if (newIt != null) {
				success = true;
				itineraries.add(newIt);
			}
		}
		return success;
	}
	
	private Itinerary createTaxiItinerary(JourneyRequest req, IPlannerService planner) {
		// Query legs for complete journey (base - pickup - dest - base)
		List<Coordinate> locations = new ArrayList<Coordinate>(3);
		locations.add(taxiRank);
		locations.add(req.From);
		locations.add(req.To);
		locations.add(taxiRank);
		List<Leg> legs = createTripLegs(locations, planner, req);
		
		if (legs == null)
			return null;
		
		// Create TaxiTrip and buffer it
		TaxiTrip taxiTrip = createTaxiTrip(legs);
		tripBuffer.put(taxiTrip.getTripId(), taxiTrip);
		
		// Create itinerary to return
		Leg taxiLeg = legs.get(1);
		Itinerary ret = new Itinerary();
		ret.legs.add(taxiLeg);
		ret.costs = taxiLeg.costs;
		ret.duration = (taxiLeg.endTime - taxiLeg.startTime) / 1000;
		ret.endTime = taxiLeg.endTime;
		ret.from = taxiLeg.from;
		ret.startTime = taxiLeg.startTime;
		ret.to = taxiLeg.to;
		ret.transfers = 1;
		ret.transitTime = ret.duration;
		return ret;
	}
	
	private Itinerary createSharedTaxiItinerary(JourneyRequest req, IPlannerService planner) {
		List<Leg> legs = solveTS(req.Destinations, planner);

		if (legs == null)
			return null;

		// Create TaxiTrip and buffer it
		TaxiTrip taxiTrip = createTaxiTrip(legs);
		tripBuffer.put(taxiTrip.getTripId(), taxiTrip);

		// Create itinerary to return
		Leg taxiLeg = legs.get(1);
		Itinerary ret = new Itinerary();
		ret.legs.add(taxiLeg);
		ret.costs = taxiLeg.costs;
		ret.duration = (taxiLeg.endTime - taxiLeg.startTime) / 1000;
		ret.endTime = taxiLeg.endTime;
		ret.from = taxiLeg.from;
		ret.startTime = taxiLeg.startTime;
		ret.to = taxiLeg.to;
		ret.transfers = 1;
		ret.transitTime = ret.duration;
		return ret;
	}
	
	private List<Leg> solveTS(List<Coordinate> base, IPlannerService planner) {
		PermutationIterator<Coordinate> it = new PermutationIterator<Coordinate>(base);
		double minTime = Double.MAX_VALUE;
		List<Leg> ret = null;
		
		while (it.hasNext()) {
			List<Coordinate> c = it.next();
			List<Leg> temp = createTripLegs(c, planner, null);
			
			double time = 0.0;
			for (Leg l : temp) {
				time += (l.endTime - l.startTime);
			}
			
			if (time < minTime) {
				minTime = time;
				ret = temp;
			}
		}
		return ret;
	}
	
	private List<Leg> createTripLegs(List<Coordinate> locations, IPlannerService planner, JourneyRequest req) {
		if (locations.size() == 0)
			return null;
		
		List<Leg> ret = new ArrayList<Leg>(locations.size() - 1);
		
		for (int i = 0; i < locations.size() - 1; i++) {
			Leg l = createLeg(locations.get(i), locations.get(i + 1), planner, req.entity);
			
			if (l == null) 
				return null;
			ret.add(l);
		}
		String agencyId = "taxiagency";
		String tripId = agencyId + "_trip_" + String.valueOf(ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE));
		ret.get(0).stopIdFrom = tripId + "_0";
		
		for (int i = 0; i < ret.size(); i++) {
			Leg current = ret.get(i);
			current.agencyId = agencyId;
			current.routeId = agencyId + "_route";
			current.tripId = tripId;
			current.stopIdTo = tripId + "_" + (i + 1);
			
			if (i == 0)
				continue;
			Leg previous = ret.get(i - 1);
			long currentDuration = current.endTime - current.startTime;
			current.startTime = previous.endTime;
			current.endTime = current.startTime + currentDuration;
			current.stopIdFrom = previous.stopIdTo;
		}
		return ret;
	}
	
	private Leg createLeg(Coordinate from, Coordinate to, IPlannerService planner, Entity requestor) {
		RequestId reqId = new RequestId();
		JourneyRequest req = JourneyRequest.createRequest(from, to, time.getCurrentDateTime(), false, new TType[] { TType.CAR }, (Person) requestor, reqId);
		List<Itinerary> temp = new ArrayList<Itinerary>();
		planner.requestSingleJourney(req, temp);
		Itinerary candidateIt = null;
		
		for (Itinerary it : temp) {
			
			if ((it.itineraryType == 0) && (it.legs.size() == 1)) {
				candidateIt = it;
				break;
			}
		}
				
		if (candidateIt == null)
			return null;			
		return candidateIt.legs.get(0);
	}
	
	private TaxiTrip createTaxiTrip(List<Leg> tripLegs) {
		List<TaxiStop> stops = new ArrayList<TaxiStop>(3);
		List<LocalTime> stopTimes = new ArrayList<LocalTime>(3);
		List<List<Street>> traces = new ArrayList<List<Street>>(3);
		
		return new TaxiTrip(tripLegs.get(1).tripId, stops, stopTimes, traces);
	}	
}
