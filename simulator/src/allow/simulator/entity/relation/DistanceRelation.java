package allow.simulator.entity.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import allow.simulator.entity.Entity;

/**
 * Represents a relation of entities based on physical distance.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class DistanceRelation extends Relation {

	/**
	 * Distance threshold.
	 */
	public static final double DISTANCE = 50.0;
	
	private List<Entity> closeEntityBuffer;
	
	/**
	 * Constructor.
	 * Creates new entity relation based on physical distance.
	 * 
	 * @param entity Entity.
	 */
	public DistanceRelation(Entity entity) {
		super(Relation.Type.DISTANCE, entity);
		closeEntityBuffer = new ArrayList<Entity>(128);
	}

	/**
	 * Updates this relation adding entities which are new to newEntities list.
	 * newEntities is modified when calling this method.
	 * 
	 * @param newEntities List to add entities which are new in this relation.
	 */
	@Override
	public void updateRelation(List<Entity> newEntities, Set<Long> blackList) {
		// Get entities which are physically close.
		closeEntityBuffer.clear();
		entity.getContext().getWorld().getNearEntities(entity, DISTANCE, closeEntityBuffer);

		// Add all entities which are new (i.e. in the difference of closeEntities and entities).
		for (Entity e : closeEntityBuffer) {
			
			if (!entities.containsKey(e.getId()) && !blackList.contains(e.getId())) {
				newEntities.add(e);
			}
		}
		
		// Finally, update entities in this relation.
		entities.clear();
		for (Entity e : closeEntityBuffer) {
			entities.put(e.getId(), e);
		}
		return;
	}
}
