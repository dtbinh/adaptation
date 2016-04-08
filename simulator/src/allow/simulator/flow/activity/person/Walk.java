package allow.simulator.flow.activity.person;

import java.util.List;

import allow.simulator.entity.Person;
import allow.simulator.entity.knowledge.Experience;
import allow.simulator.entity.knowledge.TravelExperience;
import allow.simulator.entity.relation.Relation;
import allow.simulator.flow.activity.Activity;
import allow.simulator.flow.activity.MovementActivity;
import allow.simulator.mobility.data.TType;
import allow.simulator.util.Coordinate;
import allow.simulator.util.Geometry;
import allow.simulator.world.Street;
import allow.simulator.world.StreetSegment;

/**
 * Class representing walking Activity.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public final class Walk extends MovementActivity {

	/**
	 * Creates new instance of the walking Activity.
	 * 
	 * @param person The person moving.
	 * @param path The path to walk.
	 */
	public Walk(Person entity, List<Street> path) {
		super(Activity.Type.WALK, entity, path);
	}
	
	@Override
	public double execute(double deltaT) {
		if (isFinished())
			return 0.0;

		// Note tStart.
		if (tStart == -1) {
			tStart = entity.getContext().getTime().getTimestamp();
		}
		entity.getRelations().addToUpdate(Relation.Type.DISTANCE);
		double rem = travel(deltaT);
		entity.setPosition(getCurrentPosition());

		if (isFinished()) {
			
			for (Experience ex : experiences) {
				entity.getKnowledge().collect(ex);
			}
		}
		return rem;
	}
	
	/**
	 * 
	 * 
	 * @param travelTime Time interval for travelling.
	 * @return Time used to travel which may be less than travelTime,
	 * if journey finishes before travelTime is over.
	 */
	private double travel(double travelTime) {
		double deltaT = 0.0;
		
		while (deltaT < travelTime && !isFinished()) {
			// Get current state.
			StreetSegment s = getCurrentSegment();
			double v = s.getWalkingSpeed();
			Coordinate p = getCurrentPosition();
			
			// Compute distance to next segment (i.e. end of current segment).
			double distToNextSeg = Geometry.haversine(p, s.getEndPoint());

			// Compute distance to travel within deltaT seconds.
			double distToTravel = (travelTime - deltaT) * v;
					
			if (distToTravel >= distToNextSeg) {
				// If distance to travel is bigger than distance to next segment,
				// a new log entry needs to be created.
				double tNextSegment = distToNextSeg / v;
				streetTravelTime += tNextSegment;
				distOnSeg = 0.0;
				segmentIndex++;
				
				Street street = getCurrentStreet();

				if (segmentIndex == street.getNumberOfSubSegments()) {
					double sumTravelTime = streetTravelTime; // + tNextSegment;
					tEnd = tStart + (long) sumTravelTime;
					
					Experience newEx = new TravelExperience(street,
							sumTravelTime,
							0.0,
							TType.WALK, 
							tStart,
							tEnd,
							s.getNumberOfVehicles(),
							0,
							null,
							entity.getContext().getWeather().getCurrentState());
					experiences.add(newEx);
					streetTravelTime = 0.0;
					distOnStreet = 0.0;
					streetIndex++;
					segmentIndex = 0;
					tStart = tEnd;
				}
				deltaT += tNextSegment;
				
			} else {
				// If distance to next segment is bigger than distance to travel,
				// update time on segment, travelled distance, and reset deltaT.
				streetTravelTime += (travelTime - deltaT);
				distOnSeg += distToTravel;
				distOnStreet += distToTravel;
				deltaT += (travelTime - deltaT);
			}
			if (experiences.size() == path.size())
				setFinished();
		}
		return deltaT;
	}
	
	public String toString() {
		return "Walk " + entity;
	}
}
