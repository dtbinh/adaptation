package allow.simulator.mobility.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import allow.simulator.mobility.data.gtfs.GTFSAgency;
import allow.simulator.mobility.data.gtfs.GTFSRoute;
import allow.simulator.mobility.data.gtfs.GTFSService;
import allow.simulator.mobility.data.gtfs.GTFSServiceException;
import allow.simulator.mobility.data.gtfs.GTFSStop;
import allow.simulator.mobility.data.gtfs.GTFSStopTimes;
import allow.simulator.mobility.data.gtfs.GTFSTrip;
import allow.simulator.world.Street;
import allow.simulator.world.StreetSegment;

public class OfflineDataService implements IDataService {
	
	// The mobility repository to answer requests.
	MobilityRepository repos;

	public OfflineDataService(MobilityRepository repos) {
		this.repos = repos;
	}

	@Override
	public List<GTFSAgency> getAgencies() {
		Map<String, GTFSAgency> temp = repos.getAgencies();
		return new ArrayList<GTFSAgency>(temp.values());
	}

	@Override
	public List<GTFSRoute> getRoutes(String agencyId) {
		Map<String, List<GTFSRoute>> temp = repos.getRoutes();
		return temp.get(agencyId);
	}

	@Override
	public List<GTFSStop> getStops(String routeId) {
		// Get list of trips of route.
		List<GTFSTrip> trips = repos.getTrips().get(routeId);

		// If no trips are found for given routeId, return null (routeId is unknown).
		if (trips == null) {
			return null;
		}
		
		// Get all stops from repository.
		Map<String, GTFSStop> stops = repos.getStops();
		
		// List to return.
		List<GTFSStop> ret = new ArrayList<GTFSStop>();
		for (GTFSTrip trip : trips) {
			GTFSStopTimes times = repos.getStopTimes().get(trip.getTripId());

			// If not stop times are found for given trip, return null (tripId is unknown).
			if (times == null) {
				return null;
			}
			
			for (String stopId : times.getStopIds()) {
				GTFSStop toAdd = stops.get(stopId);

				if (!ret.contains(toAdd)) {
					ret.add(toAdd);
				}
			}
		}
		return ret;
	}

	@Override
	public TimeTable getTimeTable(String routeId) {
		List<GTFSTrip> trips = repos.getTrips().get(routeId);
		
		if (trips == null) {
			return null;
		}
		Map<String, GTFSService> serviceIds = repos.getServices();
		List<GTFSStopTimes> times = new ArrayList<GTFSStopTimes>(trips.size() - 1);
		List<GTFSService> services = new ArrayList<GTFSService>(trips.size() - 1);
		
		for (GTFSTrip trip : trips) {
			GTFSStopTimes temp = repos.getStopTimes().get(trip.getTripId());
			times.add(temp);
			services.add(serviceIds.get(trip.getServiceId()));
		}
		return new TimeTable(routeId, times, services);
	}

	@Override
	public List<StreetSegment> getBusstopRouting(String start, String end) {
		return repos.getRouting(start, end);
	}
	
	public List<Street> getBusstopRoutingStreet(String start, String end) {
		return repos.getRoutingStreet(start, end);
	}

	@Override
	public List<GTFSServiceException> getServiceExceptions(String serviceId) {
		return repos.getServiceExceptions().containsKey(serviceId) 
				? repos.getServiceExceptions().get(serviceId) : new ArrayList<GTFSServiceException>(0);
	}

	@Override
	public GTFSService getServiceId(String routeId, String tripId) {
		List<GTFSTrip> trips = repos.getTrips().get(routeId);
		
		for (int i = 0; i < trips.size(); i++) {
			
			if (trips.get(i).getTripId().equals(tripId)) {
				return repos.getServices().get(trips.get(i).getServiceId());
			}
		}
		return null;
	}

}
