package allow.simulator.mobility.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import allow.simulator.core.Simulator;
import allow.simulator.entity.Entity;
import allow.simulator.entity.FlexiBusAgency;
import allow.simulator.entity.PublicTransportation;
import allow.simulator.entity.PublicTransportationAgency;
import allow.simulator.entity.Taxi;
import allow.simulator.entity.TaxiAgency;
import allow.simulator.mobility.data.gtfs.GTFSAgency;
import allow.simulator.mobility.data.gtfs.GTFSRoute;
import allow.simulator.mobility.data.gtfs.GTFSStop;
import allow.simulator.util.Coordinate;

public class TransportationRepository {
	// Collection of available GTFS transportation agencies.
	private Map<String, PublicTransportationAgency> gtfsAgencies;

	// FlexiBus agency providing dynamic on-request bus scheduling.
	private FlexiBusAgency flexiBusAgency;
	
	// Taxi agency providing single and shared taxi rides
	private TaxiAgency taxiAgency;
	
	// Instance of object.
	private static TransportationRepository instance;
	
	private TransportationRepository(Simulator simulator) {
		// Load GTFS agencies and FlexiBus.
		reload(simulator);
	}
	
	private void reload(Simulator simulator) {
		// Load public transportation from GTFS.
		List<GTFSAgency> agencyInfos = simulator.getDataService().get(0).getAgencies();
		gtfsAgencies = new HashMap<String, PublicTransportationAgency>();
		
		for (int i = 0; i < agencyInfos.size(); i++) {
			// Current agency.
			GTFSAgency currentAgency = agencyInfos.get(i);
			
			// Create new public transportation agency.
			PublicTransportationAgency newAgency = (PublicTransportationAgency) simulator.addEntity(Entity.Type.PUBLICTRANSPORTAGENCY);
			newAgency.setAgencyId(currentAgency.getId());
			
			// Request available routes.
			List<GTFSRoute> availableRoutes = simulator.getDataService().get(0).getRoutes(currentAgency.getId());
		
			for (Iterator<GTFSRoute> it = availableRoutes.iterator(); it.hasNext(); ) {
				// Get Id of next route.				
				String routeId = it.next().getId();
				
				// Get time table of this route.
				TimeTable tt = simulator.getDataService().get(0).getTimeTable(routeId);
				
				// Create stops of route from data service query.
				List<GTFSStop> stops = simulator.getDataService().get(0).getStops(routeId);
				Map<String, PublicTransportationStop> stopMap = new HashMap<String, PublicTransportationStop>(stops.size());
				
				for (GTFSStop current : stops) {
					PublicTransportationStop s = new PublicTransportationStop(current.getName(), current.getId(), new Coordinate(current.getLon(), current.getLat()));
					stopMap.put(current.getId(), s);
				}
				
				// Create a new route for the agency and add it.
				Route newRoute = new Route(routeId, tt, stopMap);
				newAgency.addRoute(newRoute);
				
				for (int k = 0; k < tt.getMaximalNumberOfTrips(); k++) {
					PublicTransportation b = (PublicTransportation) simulator.addEntity(Entity.Type.BUS);
					b.setTransportAgency(newAgency);
					newAgency.addPublicTransportation(b);
				}
			}
			gtfsAgencies.put(newAgency.getAgencyId(), newAgency);
		}
		
		// Create FlexiBus agency and assign buses.
		flexiBusAgency = (FlexiBusAgency) simulator.addEntity(Entity.Type.FLEXIBUSAGENCY);
		flexiBusAgency.setAgencyId("flexibusagency");
		
		for (int i = 0; i < 200; i++) {
			// PublicTransportation b = (PublicTransportation) simulator.addEntity(Entity.Type.BUS);
			//b.setTransportationAgency(flexiBusAgency);
			//flexiBusAgency.addVehicle(b);
		}
		
		// Create taxi agency and assign taxis
		taxiAgency = (TaxiAgency) simulator.addEntity(Entity.Type.TAXIAGENCY);
		taxiAgency.setAgencyId("taxiagency");
		
		for (int i = 0; i < 400; i++) {
			Taxi b = (Taxi) simulator.addEntity(Entity.Type.TAXI);
			b.setTransportAgency(taxiAgency);
			taxiAgency.addTaxi(b);
		}
	}
	
	public static TransportationRepository loadPublicTransportation(Simulator simulator) {
		instance = new TransportationRepository(simulator);
		return instance;
	}
	
	public static TransportationRepository Instance() {
		if (instance == null)
			throw new IllegalStateException("Error: Buslines not loaded. Execute loadBuslines() before.");
		
		return instance;
	}
	
	public PublicTransportationAgency getGTFSTransportAgency(String agencyId) {
		return gtfsAgencies.get(agencyId);
	}
	
	public FlexiBusAgency getFlexiBusAgency() {
		return flexiBusAgency;
	}
	
	public TaxiAgency getTaxiAgency() {
		return taxiAgency;
	}
	
	public Map<String, PublicTransportationAgency> getGTFSTransportAgencies() {
		return gtfsAgencies;
	}
}
