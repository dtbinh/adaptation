package allow.simulator.mobility.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import allow.simulator.entity.Person;
import allow.simulator.entity.PublicTransportation;
import allow.simulator.util.Coordinate;

/**
 * Class representing a stop of public transportation.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Stop {
	// Name of stop.
	private String name;
	
	// Id of stop.
	private String stopId;
	
	// Position of the stop.
	private Coordinate position;
	
	// Busses waiting at the stop.
	private Queue<PublicTransportation> vehicles;
	
	// Persons waiting at the stop.
	private Queue<Person> persons;
	
	/** 
	 * Constructor.
	 * Creates new instance of a stop of a transport agency.
	 * 
	 * @param name Name of the stop.
	 * @param stopId Id of the stop.
	 * @param position Location of the stop.
	 */
	public Stop(String name, String stopId, Coordinate position) {
		this.name = name;
		this.stopId = stopId;
		this.position = position;
		
		// Queues for waiting busses and persons must support concurrency. 
		vehicles = new ConcurrentLinkedQueue<PublicTransportation>();
		persons = new ConcurrentLinkedQueue<Person>();
	}
	
	/**
	 * Returns the name of the stop.
	 * 
	 * @return Name of the stop.
	 */
	public String getName() {
		return name;
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
	 * Check if there is a means of transportation currently waiting at the stop.
	 * 
	 * @return True if there is a waiting bus, false otherwise.
	 */
	public boolean hasWaitingVehicle() {
		return !vehicles.isEmpty();
	}
	
	/**
	 * Returns a means of transportation waiting at this stop.
	 * 
	 * @return Waiting means of transportation or null, if there is no means currently waiting.
	 */
	public List<PublicTransportation> getWaitingVehicle() {
		return new ArrayList<PublicTransportation>(vehicles);
	}
	
	/**
	 * Add a means of transportation to wait at this stop.
	 * 
	 * @param b Means to be added to this stop.
	 */
	public void addWaitingVehicle(PublicTransportation b) {
		vehicles.add(b);
	}
	
	/**
	 * Remove a waiting means of transportation from this stop.
	 * 
	 * @param b Means to remove from this stop.
	 */
	public void removeWaitingVehicle(PublicTransportation b) {
		vehicles.remove(b);
	}
	
	/**
	 * Check if there are any persons waiting at this stop.
	 * 
	 * @return True if there are persons waiting, false otherwise.
	 */
	public boolean hasWaitingPersons() {
		return persons.isEmpty();
	}
	
	/**
	 * Add a waiting person to this stop.
	 * 
	 * @param person Person to add to this stop.
	 */
	public void addWaitingPerson(Person person) {
		persons.add(person);
	}
	
	/**
	 * Remove a waiting person from this stop.
	 * 
	 * @param p Person to remove from this stop.
	 */
	public void removeWaitingPerson(Person p) {
		persons.remove(p);
	}
	
	/**
	 * Returns the position of this stop.
	 * 
	 * @return Position of this stop.
	 */
	public Coordinate getPosition() {
		return position;
	}
	
	public String toString() {
		return "[Stop" + stopId + "]";
	}
}
