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
 * Class representing driving Activity.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Drive extends MovementActivity {
	// Indicates whether taxi journey should be emulated
	private boolean isTaxi;
	
	// Base costs per segment in case taxi journey is emulated
	private double taxiBaseCostPerSegment;
	
	/**
	 * Creates new instance of the driving Activity.
	 * 
	 * @param person The person moving.
	 * @param path The path to drive.
	 */
	public Drive(Person entity, List<Street> path, boolean isTaxi) {
		super(Activity.Type.DRIVE, entity, path);
		this.isTaxi = isTaxi;
		
		if (!path.isEmpty()) {
			currentSegment.addVehicle();
			
			//if (isTaxi)
			//	taxiBaseCostPerSegment = 3.0 / path.size();
		}
	}

	@Override
	public double execute(double deltaT) {
		if (currentSegment != null) currentSegment.removeVehicle();
		
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
			
			for (Experience entry : experiences) {
				entity.getKnowledge().collect(entry);
			}
		} else {
			currentSegment = getCurrentSegment();
			currentSegment.addVehicle();
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
			double v = isTaxi ? s.getDrivingSpeed() : s.getDrivingSpeed(); // * entity.getContext().getWeather().getCurrentState().getSpeedReductionFactor();
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
							(isTaxi ? (taxiBaseCostPerSegment + street.getLength() * 0.001/*0.0004*/) : (street.getLength() * 0.00035)),
							TType.CAR, 
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
	/*private double travel(double travelTime) {
		double deltaT = 0.0;
		
		while (deltaT < travelTime && !isFinished()) {
			// Get current state.
			StreetSegment s = getCurrentSegment();
			double v = isTaxi ? s.getDrivingSpeed() : s.getDrivingSpeed(); // * entity.getContext().getWeather().getCurrentState().getSpeedReductionFactor();
			Coordinate p = getCurrentPosition();
			
			// Compute distance to next segment (i.e. end of current segment).
			double distToNextSeg = Geometry.haversine(p, s.getEndPoint());
			
			// Compute distance to travel within deltaT seconds.
			double distToTravel = (travelTime - deltaT) * v;
					
			if (distToTravel >= distToNextSeg) {
				// If distance to travel is bigger than distance to next segment,
				// a new log entry needs to be created.
				double tNextSegment = distToNextSeg / v;
				
				double sumTravelTime = segmentTravelTime + tNextSegment;
				tEnd = tStart + (long) (sumTravelTime * 1000);
				Experience newEx = new TravelExperience(s,
						sumTravelTime,
						(isTaxi ? (taxiBaseCostPerSegment + s.getLength() * 0.0004) : (s.getLength() * 0.00035)),
						TType.CAR, 
						tStart,
						tEnd,
						s.getNumberOfVehicles(),
						0,
						null,
						entity.getContext().getWeather().getCurrentState());
				experiences.add(newEx);
				segmentTravelTime = 0.0;
				distOnSeg = 0.0;
				tStart = tEnd;
				deltaT += tNextSegment;
				distanceIndex++;
				
			} else {
				// If distance to next segment is bigger than distance to travel,
				// update time on segment, travelled distance, and reset deltaT.
				segmentTravelTime += (travelTime - deltaT);
				distOnSeg += distToTravel;
				deltaT += (travelTime - deltaT);
			}
			if (experiences.size() == path.size())
				setFinished();
		}
		return deltaT;
	}*/
	
	public String toString() {
		return "Drive " + entity;
	}
}
