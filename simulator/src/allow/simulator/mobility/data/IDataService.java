package allow.simulator.mobility.data;

import java.util.List;

import allow.simulator.mobility.data.gtfs.GTFSAgency;
import allow.simulator.mobility.data.gtfs.GTFSRoute;
import allow.simulator.mobility.data.gtfs.GTFSService;
import allow.simulator.mobility.data.gtfs.GTFSServiceException;
import allow.simulator.mobility.data.gtfs.GTFSStop;
import allow.simulator.world.Street;
import allow.simulator.world.StreetSegment;

/**
 * Interface for an urban mobility data service.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public interface IDataService {

	/**
	 * Returns available transport agencies.
	 * 
	 * @return List of available transport agencies.
	 */
	List<GTFSAgency> getAgencies();
	
	/**
	 * Returns available routes of an agency.
	 * 
	 * @param agencyId Id of agency.
	 * 
	 * @return List of available routes served by agency.
	 */
	List<GTFSRoute> getRoutes(String agencyId);
	
	/**
	 * Returns list of stops along a certain route of an agency. 
	 * 
	 * @param routeId Id of route.
	 * 
	 * @return List of stops along a certain route of an agency.
	 */
	List<GTFSStop> getStops(String routeId);
	
	/**
	 * Returns a list of exceptions of a service given its Ids.
	 * 
	 * @param serviceId Id of the service to return exceptions.
	 * @return List of exceptions of the given service.
	 */
	List<GTFSServiceException> getServiceExceptions(String serviceId);
	
	GTFSService getServiceId(String routeId, String tripId);
	
	/**
	 * Returns timetable of a certain route.
	 * 
	 * @param routeId Id of route.
	 * 
	 * @return Timetable for route.
	 */
	TimeTable getTimeTable(String routeId);
	
	/**
	 * Returns the route between two bus stops.
	 * 
	 * @param start First stop.
	 * @param end Second stop.
	 * @return
	 */	
	List<StreetSegment> getBusstopRouting(String start, String end);
	
	/**
	 * Returns the route between two bus stops on the level of streets.
	 * 
	 * @param start First stop.
	 * @param end Second stop.
	 * @return
	 */	
	List<Street> getBusstopRoutingStreet(String start, String end);
}
