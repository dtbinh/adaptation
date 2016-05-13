package allow.simulator.entity;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.entity.utility.Utility;
import allow.simulator.mobility.data.TaxiStop;
import allow.simulator.mobility.data.TaxiTrip;

public final class Taxi extends TransportationEntity {
	
	public Taxi(long id, Utility utility, Preferences prefs, Context context, int capacity) {
		super(id, Type.TAXI, utility, prefs, context, capacity);
	}

	/**
	 * Returns the stop the taxi entity is currently waiting at.
	 * 
	 * @return Stop the taxi is currently waiting at or null, if entity is 
	 * 	       inactive or moving.
	 */
	public TaxiStop getCurrentStop() {
		return (TaxiStop) currentStop;
	}
	
	/**
	 * Sets the stop the taxi entity is currently waiting at.
	 * 
	 * @param s Stop the taxi entity is waiting at. Can be null to
	 * indicate that the taxi entity is inactive or driving.
	 */
	public void setCurrentStop(TaxiStop s) {
		currentStop = s;
	}
	
	/**
	 * Sets trip of the taxi entity is currently operating.
	 * 
	 * @param trip Trip the taxi entity operates. Can be null to
	 * indicate that the taxi entity does not operate a trip
	 */
	public void setCurrentTrip(TaxiTrip trip) {
		currentTrip = trip;
	}
	
	/**
	 * Returns trip the taxi entity is operating.
	 * 
	 * @return Trip the taxi entity is operating
	 */
	public TaxiTrip getCurrentTrip() {
		return (TaxiTrip) currentTrip;
	}
	
	/**
	 * Returns the agency the taxi entity is used by.
	 * 
	 * @return Agency the taxi entity is used by.
	 */
	public TaxiAgency getTransportationAgency() {
		return (TaxiAgency) agency;
	}

	/**
	 * Sets the agency the taxi entity is used by.
	 * 
	 * @param agency Agency the taxi entity is used by.
	 */
	public void setTransportAgency(TaxiAgency agency) {
		this.agency = agency;
	}
	
	@Override
	public String toString() {
		return "[Taxi" + id + "]";
	}
	
}
