package allow.simulator.world;

import java.util.Observable;

import allow.simulator.util.Coordinate;

/**
 * Represents a segment of a street.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class StreetSegment extends Observable implements Comparable<StreetSegment> {
	// Minimum driving speed on segment.
	public static final double MIN_DRIVING_SPEED = 0.5;

	// Default driving speed on segment.
	public static final double DEFAULT_DRIVING_SPEED = 11.11;

	// Cycling speed on segment (from OpenTripPlanner).
	public static final double CYCLING_SPEED = 4.68;
	
	// Walking speed on segment (from OpenTripPlanner).
	public static final double WALKING_SPEED = 1.3;
	
	// Id of the segment.
	private long id;
	
	// Starting point of segment.
	private StreetNode start;
	
	// End point of segment.
	private StreetNode end;
	
	// Maximal speed on segment.
	private double maxSpeed;
	
	// Possible speed.
	private double drivingSpeed;
	
	// Length of segment.
	private double length;
	
	// Number of vehicles on the segment.
	private int numberOfVehicles;
	
	/**
	 * Creates a new street segment given its adjacent nodes, maximum allowed
	 * driving speed in m/s and length in m.
	 * 
	 * @param id Id of the segment.
	 * @param start Starting node of the segment.
	 * @param end Ending node of the segment.
	 * @param maxSpeed Maximum driving speed on the segment in m/s.
	 * @param length Length of the segment in m.
	 */
	public StreetSegment(long id, StreetNode start, StreetNode end, double maxSpeed, double length) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.maxSpeed = maxSpeed;
		this.length = length;
		updatePossibleSpeed(0);
	}
	
	/**
	 * Returns the Id of the segment.
	 * 
	 * @return Id of the segment.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Returns the current number of vehicles on the segment.
	 * 
	 * @return Current number of vehicles.
	 */
	public int getNumberOfVehicles() {
		return numberOfVehicles;
	}

	/**
	 * Increases number of vehicles on the segment by 1.
	 */
	public synchronized void addVehicle() {
		numberOfVehicles++;
		setChanged();
		notifyObservers();
	}

	/**
	 * Decreases number of vehicles on the segment by 1.
	 */
	public synchronized void removeVehicle() {
		numberOfVehicles--;
		setChanged();
		notifyObservers();
	}
	
	public StreetNode getStartingNode() {
		return start;
	}
	
	public StreetNode getEndingNode() {
		return end;
	}
	
	/**
	 * Returns the position of the starting point of the segment.
	 * 
	 * @return Position of the starting point of the segment.
	 */
	public Coordinate getStartingPoint() {
		return start.getPosition();
	}
	
	/**
	 * Returns the position of the end point of the segment.
	 * 
	 * @return Position of the end point of the segment.
	 */
	public Coordinate getEndPoint() {
		return end.getPosition();
	}
	
	/**
	 * Returns the length of the segment.
	 * 
	 * @return Length of the segment in m.
	 */
	public double getLength() {
		return length;
	}

	/**
	 * Returns the maximum allowed driving speed on the segment.
	 * 
	 * @return Maximum allowed driving speed on the segment in m/s.
	 */
	public double getMaxSpeed() {
		return maxSpeed;
	}
	
	/**
	 * Returns the currently possible driving speed on the segment.
	 * 
	 * @return Currently possible driving speed on the segment in m/s.
	 */
	public double getDrivingSpeed() {
		return drivingSpeed;
	}
	
	/**
	 * Returns the currently possible bus driving speed on the segment.
	 * 
	 * @return Currently possible bus driving speed on the segment in m/s.
	 */
	public double getBusDrivingSpeed() {
		return maxSpeed * 0.95;
	}
	
	/**
	 * Returns the walking speed on the segment.
	 * Note: The function constantly returns 1.3 m/s.
	 * 
	 * @return Current walking on the segment in m/s.
	 */
	public double getWalkingSpeed() {
		return WALKING_SPEED;
	}
	
	/**
	 * Returns the cycling speed on the segment.
	 * Note: The function constantly returns 5.0 m/s.
	 * 
	 * @return Current cycling on the segment in m/s.
	 */
	public double getCyclingSpeed() {
		return CYCLING_SPEED;
	}
	
	public void updatePossibleSpeed(double carsPerMeter) {
		
		if (numberOfVehicles < 0)
			throw new IllegalStateException("Negative number of vehicles on segment");
		//System.out.println(carsPerMeter);
		// v = (v_max - v_min) / (1 + exp(k * n * carsPerMeter)) + v_min
		// k : constant 
		// n : scaling of impact of single car
		//drivingSpeed = (maxSpeed - MIN_DRIVING_SPEED) / (1 + Math.exp(9 * carsPerMeter - 7)) + MIN_DRIVING_SPEED;
		//drivingSpeed = (maxSpeed - MIN_DRIVING_SPEED) / (1 + Math.exp(9 * carsPerMeter - 5)) + MIN_DRIVING_SPEED;
		drivingSpeed = (maxSpeed - MIN_DRIVING_SPEED) / (1 + Math.exp(9 * carsPerMeter - 5)) + MIN_DRIVING_SPEED;
	}
	
	@Override
	public int compareTo(StreetSegment o) {
		
		if (this.id < o.id) {
			return -1;
			
		} else if (this.id == o.id) {
			return 0;
			
		} else {
			return 1;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		
		if (other == this) 
			return true;
		
		if (getClass() != other.getClass())
			return false;
		
		StreetSegment s = (StreetSegment) other;
		return id == s.id && start.getPosition().equals(s.start.getPosition()) && end.getPosition().equals(s.end.getPosition());
	}
	
	@Override
	public int hashCode() {
		int idHash = (int) (id ^ (id >>> 32));
		int startHash = (int) (start.getId() ^ (start.getId() >>> 32));
		int endHash = (int) (end.getId() ^ (end.getId() >>> 32));
		return 43 + 37 * (idHash + startHash + endHash);
	}
	
	public String toString() {
		return "[StreetSegment" + id + " " + start.getLabel() + " " + end.getLabel() + "]";
	}
}
