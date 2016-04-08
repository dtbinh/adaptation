package allow.simulator.mobility.planner;

import allow.simulator.entity.Entity;
import allow.simulator.mobility.data.RType;
import allow.simulator.mobility.data.TType;
import allow.simulator.util.Coordinate;

/**
 * Collection of parameters for a journey request to the planner service.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class JourneyRequest {
	
	/**
	 * Id of the entity requesting the journey.
	 */
	public Entity entity;
	
	/**
	 * Specifies if this request emulates a Taxi request.
	 */
	public boolean isTaxiRequest;
	
	/**
	 * Request id to identify requests belonging together.
	 */
	public long reqId;
	
	/**
	 * Request number identifying individual requests sharing the same request Id.
	 */
	public int reqNumber;
	
	/**
	 * Arrival date of the journey.
	 */
	public String Date = null;
	
	/**
	 * Departure time of the journey.
	 */
	public String DepartureTime = null;
	
	/**
	 * Arrival time of the journey.
	 */
	public String ArrivalTime = null;
	
	/**
	 * Starting position.
	 */
	public Coordinate From = new Coordinate();
	
	/**
	 * Destination.
	 */
	public Coordinate To = new Coordinate();
	
	/**
	 * Type of route to optimize for.
	 */
	public RType RouteType;
	
	/**
	 * Modes of transportation to use.
	 */
	public TType TransportTypes[];
	
	/**
	 * Number of results to return.
	 */
	public int ResultsNumber;
	
	/**
	 * Maximum amount of money a user wants to spent.
	 */
	public double MaximumCosts;
	
	/**
	 * Maximum distance to walk.
	 */
	public int MaximumWalkDistance;
	
}
