package allow.simulator.mobility.planner;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import allow.simulator.entity.Entity;
import allow.simulator.entity.Person;
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
	 * Destinations in case of a shared taxi request
	 */
	public List<Coordinate> Destinations = new ArrayList<Coordinate>();
	
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
	
	private JourneyRequest() {}
	
	// DateFormat to format departure date.
	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			
	// DateFormat to format departure time.
	private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mma");
			
	public static JourneyRequest createRequest(Coordinate from, Coordinate to, 
			LocalDateTime date, boolean arriveBy, TType modes[], Person person, 
			RequestId reqId) {
		JourneyRequest s = new JourneyRequest();
		s.entity = person;
		s.reqId = reqId.getRequestId();
		s.reqNumber = reqId.getNextRequestNumber();
		s.Date = date.format(dateFormat);
		LocalTime time = date.toLocalTime();
		
		if (arriveBy) {
			s.ArrivalTime = time.format(timeFormat);
		} else {
			s.DepartureTime = time.format(timeFormat);
		}
		
		s.isTaxiRequest = (person != null) && (!person.hasCar() || (!person.isAtHome() && !person.hasUsedCar()));		
		s.From.x = from.x;
		s.From.y = from.y;
		s.To.x = to.x;
		s.To.y = to.y;
		s.RouteType = RType.QUICK;
		s.TransportTypes = modes;
		s.ResultsNumber = 1;
		s.MaximumCosts = 25;
		s.MaximumWalkDistance = 1000;
		return s;
	}
	
	public static JourneyRequest createRequest(Coordinate from, List<Coordinate> to,
			LocalDateTime date, boolean arriveBy, TType[] types, Person person,
			RequestId reqId) {
		JourneyRequest s = new JourneyRequest();
		s.entity = person;
		s.reqId = reqId.getRequestId();
		s.reqNumber = reqId.getNextRequestNumber();
		s.Date = date.format(dateFormat);
		LocalTime time = date.toLocalTime();

		if (arriveBy) {
			s.ArrivalTime = time.format(timeFormat);
		} else {
			s.DepartureTime = time.format(timeFormat);
		}
		
		s.isTaxiRequest = false;
		
		// Set starting position and destination.
		s.From.x = from.x;
		s.From.y = from.y;
		s.Destinations = to;

		// Set route type.
		s.RouteType = RType.QUICK;

		// Set predefined choice of means of transportation.
		s.TransportTypes = types;
		s.ResultsNumber = 1;
		s.MaximumCosts = 0;
		s.MaximumWalkDistance = 0;
		return s;
	}
}
