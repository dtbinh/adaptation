package allow.simulator.entity.relation;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import allow.simulator.entity.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class RelationGraph {
	// The underlying graph.
	Map<Relation.Type, Relation> relations;
	
	// Entity.
	@JsonBackReference
	Entity entity;
	
	// List of relations to update.
	Set<Relation.Type> toUpdate;
	
	Set<Long> blackList;
	
	/**
	 * Constructor.
	 * Creates a new instance of an empty relation graph.
	 */
	public RelationGraph(Entity entity) {
		// Create relations map.
		relations = new EnumMap<Relation.Type, Relation>(Relation.Type.class);
		toUpdate = new HashSet<Relation.Type>();
		blackList = new HashSet<Long>();
		
		// Initialize map for each relation type.
		relations.put(Relation.Type.DISTANCE, new DistanceRelation(entity));
		relations.put(Relation.Type.BUS, new BusRelation(entity));
		this.entity = entity;
	}
	
	/**
	 * Adds a new type of relation to update calling updateRelations().
	 * 
	 * @param type Type of relation to update.
	 */
	public void addToUpdate(Relation.Type type) {
		toUpdate.add(type);
	}
	
	public void addToBlackList(Entity entity) {
		blackList.add(entity.getId());
	}
	
	public void resetBlackList() {
		blackList.clear();
	}
	
	/**
	 * Updates the relations of an entity based on the types that have been
	 * registered with addToUpdate() method and returns a list of entities
	 * which are new in the relations graph.
	 * 
	 * New entities are stored in newEntities buffer.
	 * 
	 * @return List of entities which are new in this relation graph.
	 */
	public void updateRelations(List<Entity> newEntities) {
		
		for (Iterator<Relation.Type> it = toUpdate.iterator(); it.hasNext(); ) {
			Relation rel = relations.get(it.next());
			rel.updateRelation(newEntities, blackList);
		}
		
		for (Entity e : newEntities) {
			e.getRelations().addToBlackList(entity);
		}
		toUpdate.clear();
	}
	
	/**
	 * Resets the relations in this graph.
	 */
	public void resetRelations() {
		
		for (Relation r : relations.values()) {
			r.reset();
		}
	}

}
