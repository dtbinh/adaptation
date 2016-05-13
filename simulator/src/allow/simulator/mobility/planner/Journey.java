package allow.simulator.mobility.planner;

import allow.simulator.util.Coordinate;

/**
 * Utility class holding the geographic parameters, i.e. starting position
 * and destination of a journey.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public final class Journey {
	// Starting position.
	private Coordinate start;
	
	// Destination.
	private Coordinate destination;
	
	/**
	 * Constructor.
	 * Creates a new instance of a journey specifying starting position and
	 * destination.
	 * 
	 * @param start Starting position.
	 * @param destination Destination.
	 */
	public Journey(Coordinate start, Coordinate destination) {
		this.start = start;
		this.destination = destination;
	}
	
	/**
	 * Returns the starting position.
	 * 
	 * @return Starting position.
	 */
	public Coordinate getStart() {
		return start;
	}
	
	/**
	 * Returns the destination.
	 * 
	 * @return Destination.
	 */
	public Coordinate getDestination() {
		return destination;
	}
}
