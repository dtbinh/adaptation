package allow.simulator.mobility.data.gtfs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)

/**
 * Utility class describing a route of public transportation from GTFS.
 * (https://developers.google.com/transit/gtfs/reference)
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public final class GTFSRoute {
	
	// Id of the route.
	private String id;
	
	// Id of the agency this route is offered by.
	private String agencyId;
	
	// Mode of transportation of the route.
	// 0: Tram, Streetcar, Light rail. Any light rail or street level system
	//    within a metropolitan area.
	// 1: Subway, Metro. Any underground rail system within a metropolitan area.
	// 2: Rail. Used for intercity or long-distance travel.
	// 3: Bus. Used for short- and long-distance bus routes.
	// 4: Ferry. Used for short- and long-distance boat service.
	// 5: Cable car. Used for street-level cable cars where the cable runs
	//    beneath the car.
	// 6: Gondola, Suspended cable car. Typically used for aerial cable cars
	//    where the car is suspended from the cable.
	// 7: Funicular. Any rail system designed for steep inclines.
	private int mode;
	
	/**
	 * Constructor.
	 * Creates a new route info class describing a route of a public
	 * transportation agency from GTFS.
	 * 
	 * @param id Id of the route.
	 * @param name Id of the agency this route belongs to.
	 * @param mode Mode of transportation.
	 */
	@JsonCreator
	public GTFSRoute(@JsonProperty("id") String id,
			@JsonProperty("agencyId") String agencyId,
			@JsonProperty("mode") int mode) {
		this.id = id;
		this.agencyId = agencyId;
		this.mode = mode;
	}
	
	/**
	 * Returns the Id of the route.
	 * 
	 * @return Id of the route.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the Id of the agency this route belongs to.
	 * 
	 * @return Id of the agency this route belongs to.
	 */
	public String getAgencyId() {
		return agencyId;
	}
	
	/**
	 * Returns the mode of transportation of the route.
	 * 
	 * 0: Tram, Streetcar, Light rail. Any light rail or street level system
	 *    within a metropolitan area.
	 * 1: Subway, Metro. Any underground rail system within a metropolitan area.
	 * 2: Rail. Used for intercity or long-distance travel.
	 * 3: Bus. Used for short- and long-distance bus routes.
	 * 4: Ferry. Used for short- and long-distance boat service.
	 * 5: Cable car. Used for street-level cable cars where the cable runs
	 *    beneath the car.
	 * 6: Gondola, Suspended cable car. Typically used for aerial cable cars
	 *    where the car is suspended from the cable.
	 * 7: Funicular. Any rail system designed for steep inclines.
	 * 
	 * @return Mode of transportation of the route.
	 */
	public int getMode() {
		return mode;
	}
	
	// Constants when route is read from a GTFS file directly.
	private static int ROUTE_ID = 0;
	private static int ROUTE_AGENCY_ID = 1;
	private static int ROUTE_MODE = 4;
	
	/**
	 * Parse route info from line in GTFS file (routes.txt).
	 * 
	 * @param line Line from GTFS file (routes.txt).
	 * @return Route info parsed from GTFS file.
	 */
	public static GTFSRoute fromGTFS(String line) {
		String tokens[] = line.split(",");
		return new GTFSRoute(tokens[ROUTE_ID], tokens[ROUTE_AGENCY_ID], Integer.parseInt(tokens[ROUTE_MODE]));
	}
}
