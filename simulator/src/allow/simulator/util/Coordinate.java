package allow.simulator.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a 2D pair of coordinates.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Coordinate {
	
	/**
	 * x coordinate.
	 */
	public double x;
	
	/**
	 * y coordinate.
	 */
	public double y;
	
	/**
	 * Constructor.
	 * Creates a new 2D coordinate with given x and y entries.
	 * 
	 * @param x x coordinate.
	 * @param y y coordinate.
	 */
	@JsonCreator
	public Coordinate(@JsonProperty("lon") double x, @JsonProperty("lat") double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Constructor.
	 * Creates a new 2D coordinate with x = 0 and y = 0.
	 */
	public Coordinate() { }

	/**
	 * 
	 * @param toCopy
	 */
	public Coordinate(Coordinate toCopy) {
		this(toCopy.x, toCopy.y);
	}
	/**
	 * Returns the Euclidean distance to another 2D coordinate.
	 * 
	 * @param other Other coordinate.
	 * @return Euclidean distance to another coordinate.
	 */
	public double distance(Coordinate other) {
		double diffX = x - other.x;
		double diffY = y - other.y;
		return Math.sqrt(diffX * diffX + diffY * diffY);
	}
	
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		
		if (other == this) {
			return true;
		}
		
		if (!(other instanceof Coordinate)) {
			return false;
			
		}
		Coordinate otherC = (Coordinate) other;
		return (otherC.x == x) && (otherC.y == y);
	}
	
	public int hashCode() {
		return (int) (37 + 31 * (((x == 0.0) ? 0L : Double.doubleToLongBits(x)) + ((y == 0.0) ? 0L : Double.doubleToLongBits(y))));
	}
}
