package allow.simulator.mobility.planner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import allow.simulator.core.Time;
import allow.simulator.entity.Entity;
import allow.simulator.entity.Person;
import allow.simulator.mobility.data.TType;
import allow.simulator.util.Coordinate;

public class BikeRentalPlanner implements IPlannerService {

	private List<IPlannerService> plannerServices;
	private Coordinate bikeRentalStation;
	private Time time;
	
	public BikeRentalPlanner(List<IPlannerService> plannerServices, Time time, Coordinate bikeRentalStation) {
		this.plannerServices = plannerServices;
		this.bikeRentalStation = bikeRentalStation;
		this.time = time;
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
			
			if (modes[i] == TType.SHARED_BICYCLE)
				newIt = createBikeRentalItinerary(request, planner);
						
			if (newIt != null) {
				success = true;
				itineraries.add(newIt);
			}
		}
		return success;
	}

	private Itinerary createBikeRentalItinerary(JourneyRequest req, IPlannerService planner) {		
		// Request walking leg from starting point to bike rental station
		Leg walkingLeg = createWalkingLeg(req.From, bikeRentalStation, planner, req.entity);
		
		if (walkingLeg == null)
			return null;
				
		// Request cycling leg from bike rental station to destination
		Itinerary bikeIt = createBikeItinerary(bikeRentalStation, req.To,planner, req.entity, walkingLeg.endTime);
	
		if (bikeIt == null)
			return null;
		
		// Compose itinerary
		Itinerary ret = new Itinerary();
		ret.addLeg(walkingLeg);
		ret.legs.addAll(bikeIt.legs);
		ret.costs = walkingLeg.costs + bikeIt.costs;
		ret.startTime = walkingLeg.startTime;
		ret.endTime = bikeIt.endTime;
		ret.duration = (ret.endTime - ret.startTime) / 1000;
		ret.from = req.From;
		ret.to = req.To;
		ret.itineraryType = Itinerary.getItineraryType(ret);
		ret.reqId = req.reqId;
		ret.reqNumber = req.reqNumber;
		ret.walkDistance = walkingLeg.distance + bikeIt.walkDistance;
		ret.walkTime = (walkingLeg.endTime - walkingLeg.startTime) / 1000 + bikeIt.walkTime;
		return ret;
	}
	
	private Leg createWalkingLeg(Coordinate from, Coordinate to, IPlannerService planner, Entity requestor) {
		RequestId reqId = new RequestId();
		JourneyRequest req = JourneyRequest.createRequest(from, to, time.getCurrentDateTime(), false, new TType[] { TType.WALK }, (Person) requestor, reqId);
		List<Itinerary> temp = new ArrayList<Itinerary>();
		planner.requestSingleJourney(req, temp);
		Itinerary candidateIt = null;
		
		for (Itinerary it : temp) {
			
			if (it.itineraryType == 3) {
				candidateIt = it;
				break;
			}
		}
				
		if (candidateIt == null)
			return null;			
		return candidateIt.legs.get(0);
	}
	
	private Itinerary createBikeItinerary(Coordinate from, Coordinate to, IPlannerService planner, Entity requestor, long startTime) {
		RequestId reqId = new RequestId();
		JourneyRequest req = JourneyRequest.createRequest(from, to, time.getCurrentDateTime(), false, new TType[] { TType.BICYCLE }, (Person) requestor, reqId);
		List<Itinerary> temp = new ArrayList<Itinerary>();
		planner.requestSingleJourney(req, temp);
		Itinerary candidateIt = null;
		
		for (Itinerary it : temp) {
			
			if (it.itineraryType == 2) {
				candidateIt = it;
				break;
			}
		}
				
		if (candidateIt == null)
			return null;
		
		List<Leg> legs = candidateIt.legs;
		
		for (int i = 0; i < legs.size(); i++) {
			Leg leg = legs.get(i);
			long duration = leg.endTime - leg.startTime;
			
			if (i == 0) {
				leg.startTime = startTime;
				
			} else {
				leg.startTime = legs.get(i - 1).endTime;
			}
			leg.endTime = (leg.startTime + duration);
		}
		candidateIt.startTime = legs.get(0).startTime;
		candidateIt.endTime = legs.get(legs.size() - 1).endTime;
		return candidateIt;
	}
}
