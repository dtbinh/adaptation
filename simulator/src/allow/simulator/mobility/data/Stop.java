package allow.simulator.mobility.data;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import allow.simulator.entity.Person;
import allow.simulator.entity.TransportationEntity;
import allow.simulator.util.Coordinate;

public abstract class Stop {
	// Id of stop
	protected final String stopId;

	// Position of the stop
	protected final Coordinate position;

	// Transportation entities waiting at the stop
	protected final Queue<TransportationEntity> transportationEntities;

	// Passengers waiting at the stop
	protected final Queue<Person> passengers;

	/**
	 * Creates new instance of a stop.
	 * 
	 * @param stopId Id of the stop.
	 * @param position Location of the stop.
	 */
	protected Stop(String stopId, Coordinate position) {
		this.stopId = stopId;
		this.position = position;
		transportationEntities = new ConcurrentLinkedQueue<TransportationEntity>();
		passengers = new ConcurrentLinkedQueue<Person>();
	}

	/**
	 * Returns the Id of the stop.
	 * 
	 * @return Id of the stop.
	 */
	public String getStopId() {
		return stopId;
	}

	/**
	 * Returns the position of this stop.
	 * 
	 * @return Position of this stop.
	 */
	public Coordinate getPosition() {
		return position;
	}
	
	/**
	 * Checks if there are any passengers waiting at this stop.
	 * 
	 * @return True if there are passengers waiting, false otherwise
	 */
	public boolean hasWaitingPersons() {
		return passengers.isEmpty();
	}

	/**
	 * Adds a waiting passengers to this stop.
	 * 
	 * @param passenger Passenger to add to this stop
	 */
	public void addWaitingPerson(Person passenger) {
		passengers.add(passenger);
	}

	/**
	 * Removes a waiting passenger from this stop.
	 * 
	 * @param p Passenger to remove from this stop
	 */
	public void removeWaitingPerson(Person passenger) {
		passengers.remove(passenger);
	}

	public String toString() {
		return "[Stop" + stopId + "]";
	}
}
