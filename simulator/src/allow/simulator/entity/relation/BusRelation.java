package allow.simulator.entity.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import allow.simulator.entity.Entity;
import allow.simulator.entity.TransportationEntity;
import allow.simulator.flow.activity.Activity;
import allow.simulator.flow.activity.person.UsePublicTransport;

/**
 * Represents a relation of entities based on being in the same bus.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class BusRelation extends Relation {

	/**
	 * Constructor.
	 * Creates new entity relation based on being in the same bus.
	 * 
	 * @param entity Entity.
	 */
	public BusRelation(Entity entity) {
		super(Relation.Type.BUS, entity);
		
	}

	@Override
	public void updateRelation(List<Entity> newEntities, Set<Long> blackList) {
		// Get current activity of entity.
		Activity a = entity.getFlow().getCurrentActivity();
		List<Entity> busEntities = new ArrayList<Entity>();
		
		if (a.getType() == Activity.Type.USE_PUBLIC_TRANSPORT) {
			// If entity is using public transportation, add bus and passengers as
			// possible exchange candidates.
			TransportationEntity b = ((UsePublicTransport) a).getMeansOfTransportation();
			
			if ((b != null) && (b.getId() != entity.getId())) {
				busEntities.add(b);
				busEntities.addAll(b.getPassengers());
				busEntities.remove(entity);
			}
		}
		// Add all entities which are new (i.e. in the difference of closeEntities and entities).
		for (Entity e : busEntities) {
					
			if (!entities.containsKey(e.getId())) {
				newEntities.add(e);
			}
		}
		// Finally, update entities in this relation.
		entities.clear();
		for (Entity e : busEntities) {
			entities.put(e.getId(), e);
		}
	}

}
