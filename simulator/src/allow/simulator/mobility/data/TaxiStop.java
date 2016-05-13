package allow.simulator.mobility.data;

import allow.simulator.entity.Taxi;
import allow.simulator.util.Coordinate;

public class TaxiStop extends Stop {
	/** 
	 * Creates new instance of a public transportation stop.
	 * 
	 * @param stopId Id of the stop
	 * @param position Position of the stop
	 */
	public TaxiStop(String stopId, Coordinate position) {
		super(stopId, position);
	}
	
	/**
	 * Checks if there is a taxi entity currently waiting at the stop.
	 * 
	 * @return True if there is a waiting taxi entity, false otherwise.
	 */
	public boolean hasWaitingTaxi() {
		return !transportationEntities.isEmpty();
	}
	
	/**
	 * Returns a list of taxi entities waiting at the stop.
	 * 
	 * @return Waiting taxi entity or null, if no taxi entity is currently
	 * waiting at the stop
	 */
	public Taxi getTaxi() {
		return (Taxi) transportationEntities.peek();
	}
	
	/**
	 * Adds a taxi entity to wait at this stop.
	 * 
	 * @param b Taxi entity to be added to this stop.
	 */
	public void addTaxi(Taxi taxi) {
		transportationEntities.add(taxi);
	}
	
	/**
	 * Removes a waiting public transportation entity from this stop.
	 * 
	 * @param b Public transportation entity to remove from this stop.
	 */
	public void removeTaxi(Taxi taxi) {
		transportationEntities.poll();
	}
	
	public String toString() {
		return "[PublicTransportationStop" + stopId + "]";
	}
}
