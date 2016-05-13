package allow.simulator.mobility.data;

import java.util.ArrayList;
import java.util.List;

import allow.simulator.entity.PublicTransportation;
import allow.simulator.entity.TransportationEntity;
import allow.simulator.util.Coordinate;

/**
 * Class representing a stop of public transportation.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class PublicTransportationStop extends Stop {
	// Name of stop
	private String name;
	
	/** 
	 * Creates new instance of a public transportation stop.
	 * 
	 * @param name Name of the stop
	 * @param stopId Id of the stop
	 * @param position Position of the stop
	 */
	public PublicTransportationStop(String name, String stopId, Coordinate position) {
		super(stopId, position);
		this.name = name;
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
	 * Checks if there is a public transportation entity currently waiting at the stop.
	 * 
	 * @return True if there is a waiting public transportation entity, false otherwise.
	 */
	public boolean hasWaitingPublicTransportationEntities() {
		return !transportationEntities.isEmpty();
	}
	
	/**
	 * Returns a list of public transportation entity waiting at the stop.
	 * 
	 * @return Waiting public transportation entity
	 */
	public List<PublicTransportation> getPublicTransportationEntities() {
		List<PublicTransportation> ret = new ArrayList<PublicTransportation>(transportationEntities.size());
		
		for (TransportationEntity transportation : transportationEntities) {
			ret.add((PublicTransportation) transportation);
		}
		return ret;
	}
	
	/**
	 * Adds a public transportation entity to wait at this stop.
	 * 
	 * @param b Public transportation entity to be added to this stop.
	 */
	public void addPublicTransportation(PublicTransportation b) {
		transportationEntities.add(b);
	}
	
	/**
	 * Removes a waiting public transportation entity from this stop.
	 * 
	 * @param b Public transportation entity to remove from this stop.
	 */
	public void removePublicTransportation(PublicTransportation b) {
		transportationEntities.remove(b);
	}
	
	public String toString() {
		return "[PublicTransportationStop" + stopId + "]";
	}
}
