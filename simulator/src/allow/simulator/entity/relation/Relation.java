package allow.simulator.entity.relation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import allow.simulator.entity.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Represents a relation among entities.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public abstract class Relation {
	
	public enum Type {
		/**
		 * Entities are physically close to each other.
		 */
		DISTANCE,
	
		/**
		 * Entities are on the same bus.
		 */
		BUS
	}
	
	// Type of this relation.
	protected Type type;
	
	// Entity.
	@JsonBackReference
	protected Entity entity;
		
	// Entities in this relation.
	protected Map<Long, Entity> entities;
	
	protected Relation(Type type, Entity entity) {
		this.entity = entity;
		this.type = type;
		entities = new HashMap<Long, Entity>();
	}
	
	public abstract void updateRelation(List<Entity> newEntities, Set<Long> blackList);
	
	public void reset() {
		entities.clear();
	}
	
	public Type getType() {
		return type;
	}

}
