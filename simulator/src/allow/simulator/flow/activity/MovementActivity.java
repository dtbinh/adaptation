package allow.simulator.flow.activity;

import java.util.ArrayList;
import java.util.List;

import allow.simulator.entity.Entity;
import allow.simulator.entity.knowledge.Experience;
import allow.simulator.util.Coordinate;
import allow.simulator.world.Street;
import allow.simulator.world.StreetSegment;

/**
 * Abstract class representing an Activity to move an entity.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public abstract class MovementActivity extends Activity {
	
	// Path to move.
	protected List<Street> path;
	
	// Traveled distance.
	protected StreetSegment currentSegment;
	protected Coordinate currentPosition;
	
	// Distance travelled on path.
	// protected double distance;
	
	// Utility variables.
	protected int streetIndex;
	protected double distOnStreet;
	protected int segmentIndex;
	protected double distOnSeg;
		
	// Utility variables to log travel statistics per segment.
	protected double streetTravelTime;
	protected List<Experience> experiences;

	/**
	 * Creates a new Activity to move an entity.
	 * 
	 * @param type Type of the Activity.
	 * @param entity Entity executing the Activity (i.e. the entity to move).
	 * @param polyline Path to move along.
	 */
	protected MovementActivity(Type type, Entity entity, List<Street> polyLine) {
		// Call constructor of superclass specifying type of activity and entity. 
		super(type, entity);
		this.path = polyLine;
		initialize();
	}

	private void initialize() {
		if (!path.isEmpty()) {
			
			// Calculate overall distance.
			// distance = 0.0;
		
			/*for (StreetSegment seg : path) {
				distance += seg.getLength();
			}*/
			streetIndex = 0;
			distOnStreet = 0.0;
			
			segmentIndex = 0;
			distOnSeg = 0.0;
			
			currentSegment = getCurrentSegment();
			currentPosition = new Coordinate(currentSegment.getStartingPoint());
			
			// Reset travel statistics.
			streetTravelTime = 0.0;
			/*distanceIndex = 0;
			distOnSeg = 0;*/
		
			// Prepare logging.
			experiences = new ArrayList<Experience>(path.size());

			// Prepare entity state.
			entity.setPosition(getStartPoint());

		} else {
			// Otherwise set finished.
			setFinished();
		}
	}
	/**
	 * Executes the movement activity for travelTime seconds and returns the used
	 * time in seconds which may be lower as travelTime, if activity requires less
	 * time than travelTime before finishing.
	 * 
	 * @param travelTime Time to travel in seconds.
	 * @return Used time in seconds up to a maximum of travelTime seconds.
	 */
	@Override
	public abstract double execute(double travelTime);
	
	/**
	 * Returns the current position from activity execution.
	 * 
	 * @return Current position.
	 */
	protected Coordinate getCurrentPosition() {
		if (path.size() == 0) {
			return null;
		}
		// Get current segment.
		StreetSegment currentSeg = getCurrentSegment();
		
		// Use distanceIndex to calculate position using linear interpolation.
		double r = distOnSeg / currentSeg.getLength();
		double r_inv = 1.0 - r;
		currentPosition.x = r_inv * currentSeg.getStartingPoint().x + r * currentSeg.getEndPoint().x;
		currentPosition.y = r_inv * currentSeg.getStartingPoint().y + r * currentSeg.getEndPoint().y;
		// return new Coordinate(r_inv * currentSeg.getStartingPoint().x + r * currentSeg.getEndPoint().x, r_inv * currentSeg.getStartingPoint().y + r * currentSeg.getEndPoint().y);
		return currentPosition;
	}
	
	/**
	 * Returns the current street segment during movement execution.
	 * 
	 * @return Current street segment.
	 */
	protected Street getCurrentStreet() {
		return (path.size() != 0) ? (isFinished() ? path.get(path.size() - 1) : path.get(streetIndex)) : null;
	}
	
	/**
	 * Returns the current street segment during movement execution.
	 * 
	 * @return Current street segment.
	 */
	protected StreetSegment getCurrentSegment() {
		Street s = getCurrentStreet();
		
		if (s == null)
			return null;
		
		return (s.getSubSegments().size() != 0) ? 
				(isFinished() ? s.getSubSegments().get(s.getSubSegments().size() - 1) 
						: s.getSubSegments().get(segmentIndex)) : null;
	}
	
	/**
	 * Returns the current street segment during movement execution.
	 * 
	 * @return Current street segment.
	 */
	/*protected StreetSegment getCurrentSegment() {
		return (path.size() != 0) ? (isFinished() ? path.get(path.size() - 1) : path.get(distanceIndex)) : null;
	}*/
	
	/**
	 * Returns the first GPS point of this path.
	 * 
	 * @return First GPS point of this path.
	 */
	public Coordinate getStartPoint() {
		return (path.size() != 0) ? path.get(0).getStartingNode().getPosition() : null;
	}
	
	/**
	 * Returns the last GPS point of this path.
	 * 
	 * @return Last GPS point of this path.
	 */
	/*public Coordinate getEndPoint() {
		return(path.size() != 0) ? path.get(path.size() - 1).getEndPoint() : null;
	}*/
	
	/*public double getLength() {
		return distance;
	}*/
}