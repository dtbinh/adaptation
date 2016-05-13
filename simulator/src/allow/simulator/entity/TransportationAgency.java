package allow.simulator.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.IUtility;
import allow.simulator.entity.utility.Preferences;

/**
 * Represents a transport agency entity managing a set of trips and vehicles
 * to execute them. 
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public abstract class TransportationAgency extends Entity {
	// Id of the agency
	protected String agencyId;
	
	// List of transport entities used by this agency
	protected final Queue<TransportationEntity> vehicles;
	
	// "Live" information about current trips and vehicles executing trips
	protected final Map<String, TransportationEntity> currentlyUsedVehicles;
	
	/**
	 * Creates new instance of a transportation agency entity.
	 * 
	 * @param id Id of the transportation agency entity
	 * @param utility Utility function
	 * @param context Simulation context
	 */
	protected TransportationAgency(long id, Type type, IUtility utility, Preferences prefs, Context context) {
		super(id, type, utility, prefs, context);
		agencyId = "";
		
		// Create transportation entity repository
		vehicles = new ConcurrentLinkedQueue<TransportationEntity>();
		
		// "Live" information about trips and transportation entities executing trips
		currentlyUsedVehicles = new HashMap<String, TransportationEntity>();
	}
	
	/**
	 * Returns Id of this agency.
	 * 
	 * @return Id of this agency.
	 */
	public String getAgencyId() {
		return agencyId;
	}
	
	/**
	 * Sets the Id of this agency.
	 * 
	 * @param agencyId Id to set.
	 */
	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}

	@Override
	public void exchangeKnowledge() { }
	
	public boolean isActive() {
		return false;
	}
	
	public String toString() {
		return "[TransportationAgency" + id + "]";
	}
}
