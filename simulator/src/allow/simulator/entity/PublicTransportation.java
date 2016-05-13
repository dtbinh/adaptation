package allow.simulator.entity;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.entity.utility.Utility;
import allow.simulator.mobility.data.PublicTransportationStop;
import allow.simulator.mobility.data.Trip;

/**
 * Represents a bus entity which is a subtype of a transportation entity.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public final class PublicTransportation extends TransportationEntity {
	/**
	 * Creates new instance of a public transportation mean.
	 * 
	 * @param id Id of the transportation mean.
	 * @param utility Utility function of the transportation mean.
	 * @param context Context of the transportation mean.
	 * @param capacity Capacity of the transportation mean.
	 */
	public PublicTransportation(long id, Utility utility, Preferences prefs, Context context, int capacity) {
		super(id, Type.BUS, utility, prefs, context, capacity);
	}
	
	/**
	 * Returns the stop the public transportation entity is currently waiting at.
	 * 
	 * @return Stop the public transportation entity is currently waiting at or null, 
	 * if entity is inactive or moving.
	 */
	public PublicTransportationStop getCurrentStop() {
		return (PublicTransportationStop) currentStop;
	}
	
	/**
	 * Sets the stop the public transportation entity is currently waiting at.
	 * 
	 * @param s Stop the public transportation entity is waiting at. Can be null to
	 * indicate that the public transportation entity is inactive or driving.
	 */
	public void setCurrentStop(PublicTransportationStop s) {
		currentStop = s;
	}
	
	/**
	 * Sets trip of the public transportation entity is currently operating.
	 * 
	 * @param trip Trip the public transportation entity operates. Can be null to
	 * indicate that the public transportation entity does not operate a trip
	 */
	public void setCurrentTrip(Trip trip) {
		currentTrip = trip;
	}
	
	/**
	 * Returns trip the public transportation entity is operating.
	 * 
	 * @return Trip the public transportation entity is operating
	 */
	public Trip getCurrentTrip() {
		return currentTrip;
	}
	
	/**
	 * Returns the agency the public transportation entity is used by.
	 * 
	 * @return Agency the public transportation entity is used by.
	 */
	public PublicTransportationAgency getTransportationAgency() {
		return (PublicTransportationAgency) agency;
	}

	/**
	 * Sets the agency the public transportation entity is used by.
	 * 
	 * @param agency Agency the public transportation entity is used by.
	 */
	public void setTransportAgency(PublicTransportationAgency agency) {
		this.agency = agency;
	}
	
	@Override
	public String toString() {
		return "[PublicTransportation" + id + "]";
	}
}
