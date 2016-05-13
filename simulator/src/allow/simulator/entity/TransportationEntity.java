package allow.simulator.entity;

import java.util.ArrayList;
import java.util.List;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.entity.utility.Utility;
import allow.simulator.mobility.data.Stop;
import allow.simulator.mobility.data.Trip;

/**
 * Abstract class modelling a transportation entity.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public abstract class TransportationEntity extends Entity {
	
	public enum State {
		/**
		 * Normal mode of operation, i.e. no delay and breakdown
		 */
		NORMAL,
		
		/**
		 * Transportation entity is delay
		 */
		IN_DELAY,
		
		/**
		 * Transportation entity is broke down
		 */
		BROKE_DOWN
	}
	
	// Agency the bus is used by
	protected TransportationAgency agency;
	
	// Capacity of the transportation entity
	protected int capacity;
				
	// List of passengers
	protected List<Person> passengers;
			
	// Current trip the transportation entity is operating
	protected Trip currentTrip;
	
	// Current stop the transportation entity stops at or null, in case
	// the entity is moving or does not have a trip assigned
	protected Stop currentStop;
	
	// Current delay
	protected long currentDelay;
	
	// Current state of transportation entity
	protected State state;
	
	/**
	 * Creates a new instance of a transportation entity
	 * 
	 * @param id Id of entity
	 * @param type Type of entity
	 * @param utility Utility function
	 * @param context Simulation context
	 * @param capacity Capacity of entity
	 */
	protected TransportationEntity(long id, Type type, Utility utility, Preferences prefs, Context context, int capacity) {
		super(id, type, utility, prefs, context);
		this.capacity = capacity;
		passengers = new ArrayList<Person>(capacity);
		state = State.NORMAL;
	}
	
	/**
	 * Returns the current state of the transportation entity.
	 * 
	 * @return Current state of the transportation entity
	 */
	public State getCurrentState() {
		return state;
	}
	
	/**
	 * Sets the current state of the transportation entity.
	 * 
	 * @param New state of the transportation entity
	 */
	public void setCurrentState(State state) {
		this.state = state;
	}
	
	/**
	 * Returns the current delay of the transportation entity.
	 * 
	 * @return Current delay of the transportation entity
	 */
	public long getCurrentDelay() {
		return currentDelay;
	}
	
	/**
	 * Sets the current delay of the transportation entity
	 * 
	 * @param newDelay New delay of transportation entity
	 */
	public void setCurrentDelay(long newDelay) {
		currentDelay = newDelay;
	}
	
	/**
	 * Adds a person entity to the list of passengers of the transportation entity 
	 * if there is enough capacity left. 
	 * 
	 * @param p The person to add to the list of passengers of the transportation entity
	 * @return True, if person was added to the list of passengers, false otherwise
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
	 * Removes a person entity from the list of passengers of the transportation entity.
	 * 
	 * @param p The person to remove.
	 */
	public void removePassenger(Person p) {
		
		synchronized(passengers) {
			passengers.remove(p);
		}
	}
	
	/**
	 * Returns the list of passengers of the transportation entity.
	 * 
	 * @return List of passengers of the transportation entity
	 */
	public List<Person> getPassengers() {
		return passengers;
	}
	
	/**
	 * Returns the capacity, i.e. maximum number of passengers, of the transportation entity.
	 * 
	 * @return Capacity of the transportation entity
	 */
	public int getCapacity() {
		return capacity;
	}

	public String toString() {
		return "[TransportationEntity" + id + "]";
	}

	@Override
	public boolean isActive() {
		return (currentTrip != null);
	}
}
