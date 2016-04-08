package allow.simulator.entity;

import java.util.ArrayList;
import java.util.List;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.entity.utility.Utility;
import allow.simulator.mobility.data.Stop;
import allow.simulator.mobility.data.Trip;

/**
 * Abstract class modelling a means of public transportation characterized by
 * a transport agency running the means, a list of passengers, and a capacity.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public abstract class PublicTransportation extends Entity {
	// Agency the bus is used by.
	protected TransportAgency agency;
		
	// List of passengers.
	protected List<Person> passengers;
			
	// Capacity.
	protected int capacity;
			
	// Current trip and stop the transportation mean is operating. 
	protected Trip currentTrip;
	protected Stop currentStop;
	
	// Current delay.
	protected long currentDelay;
	
	/**
	 * Constructor.
	 * Creates a new instance of a means of public transportation.
	 * 
	 * @param id Id of means.
	 * @param type Type of means.
	 * @param utility Utility function.
	 * @param context Context of means.
	 * @param capacity Capacity of means.
	 */
	protected PublicTransportation(long id, Type type, Utility utility, Preferences prefs, Context context, int capacity) {
		super(id, type, utility, prefs, context);
		this.capacity = capacity;
		passengers = new ArrayList<Person>(capacity);
		currentDelay = 0;
	}
	
	/**
	 * Returns the current delay of the means of transportation.
	 * 
	 * @return Current delay of the means of transportation.
	 */
	public long getCurrentDelay() {
		return currentDelay;
	}
	
	/**
	 * Sets the current delay of the means of transportation.
	 * 
	 * @param newDelay New delay.
	 */
	public void setCurrentDelay(long newDelay) {
		currentDelay = newDelay;
	}
	
	/**
	 * Returns the stop the means of public transportation is currently waiting at.
	 * 
	 * @return Stop the bus is currently waiting at or null, if bus is 
	 * 	       inactive or driving.
	 */
	public Stop getCurrentStop() {
		return currentStop;
	}
	
	/**
	 * Set stop the means of public transportation is currently waiting at.
	 * 
	 * @param s Stop the means of public transportation is waiting at. Can be
	 * null to indicate that means of public transportation is inactive or driving.
	 */
	public void setCurrentStop(Stop s) {
		currentStop = s;
	}
	
	/**
	 * Set trip of the means of public transportation is operating.
	 * 
	 * @param route Route the means of public transportation operates.
	 */
	public void setCurrentTrip(Trip trip) {
		currentTrip = trip;
	}
	
	/**
	 * Returns trip the means of public transportation is operating.
	 * 
	 * @return Trip the means of public transportation is operating.
	 */
	public Trip getCurrentTrip() {
		return currentTrip;
	}
	
	/**
	 * Adds a passenger to the means of public transportation if the bus has 
	 * capacity.
	 * 
	 * @param p The person to add to the means of public transportation.
	 * @return True, if person was added to the means of public transportation,
	 * false otherwise.
	 */
	public boolean addPassenger(Person p) {
		boolean added = false;
		
		synchronized(passengers) {
			
			if (passengers.size() < capacity) {
				added = passengers.add(p);
			}
		}
		return added;
	}
	
	/**
	 * Removes a passenger from the means of public transportation.
	 * 
	 * @param p The passenger to remove.
	 */
	public void removePassenger(Person p) {
		
		synchronized(passengers) {
			passengers.remove(p);
		}
	}
	
	/**
	 * Get list of passengers of the means of public transportation.
	 * 
	 * @return List of passengers of the means of public transportation.
	 */
	public List<Person> getPassengers() {
		return passengers;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * Returns the agency the means of public transportation is used by.
	 * 
	 * @return Agency the bus is used by.
	 */
	public TransportAgency getTransportAgency() {
		return agency;
	}

	/**
	 * Sets the agency the bus belongs to.
	 * 
	 * @param agency Agency the bus belongs to.
	 */
	public void setTransportAgency(TransportAgency agency) {
		this.agency = agency;
	}

	public String toString() {
		return "[PublicTransportation" + id + "]";
	}

	@Override
	public boolean isActive() {
		return (currentTrip != null);
	}
}
