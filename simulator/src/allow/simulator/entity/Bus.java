package allow.simulator.entity;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.entity.utility.Utility;

/**
 * Represents a bus entity.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public final class Bus extends PublicTransportation {
	
	/**
	 * Constructor.
	 * Creates new instance of a bus.
	 * 
	 * @param id Id of the bus.
	 * @param utility Utility function of the bus.
	 * @param context Context of the bus.
	 * @param capacity Capacity of the bus.
	 */
	public Bus(long id, Utility utility, Preferences prefs, Context context, int capacity) {
		super(id, Type.BUS, utility, prefs, context, capacity);
	}
	
	public String toString() {
		return "[Bus" + id + "]";
	}
	
}
