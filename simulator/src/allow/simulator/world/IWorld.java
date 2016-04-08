package allow.simulator.world;

import java.util.Collection;
import java.util.List;

import allow.simulator.entity.Entity;
import allow.simulator.entity.UrbanMobilitySystem;

/**
 * Interface for world representations used in the Allow Ensembles
 * mobility simulation.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public interface IWorld {
	
	/**
	 * Adds a new entity to the world.
	 * 
	 * @param e Entity to add.
	 */
	void addEntity(Entity e);
	
	/**
	 * Removes the entity with given Id.
	 * 
	 * @param entityId Id of entity to remove.
	 * @return Removed entity.
	 */
	 Entity removeEntity(long entityId);
	
	/**
	 * Returns the entity with given Id.
	 * 
	 * @param entityId Id of entity to return.
	 * @return Entity with given Id.
	 */
	 Entity getEntityById(long entityId);
	
	/**
	 * Returns all entities of the given type.
	 * 
	 * @param type Type of entities to return.
	 * @return Collection of entities of given type.
	 */
	 Collection<Entity> getEntitiesOfType(Entity.Type type);
	
	/**
	 * Returns entities with certain distance entity with given Id.
	 * 
	 * @param entity Entity to find other entities near to.
	 * @param distance The maximal distance of the entity to others.
	 * @return List of entities within the given maximal distance to entity.
	 */
	 List<Entity> getNearEntities(Entity entity, double distance, List<Entity> buffer);
	
	/**
	 * Returns the street map (graph) of this world.
	 * 
	 * @return Street map of this world.
	 */
	 StreetMap getStreetMap();
	
	/**
	 * Returns the Urban Mobility System entity of the world.
	 * 
	 * @return Urban Mobility System entity of the world or null, if no Urban
	 * Mobility System entity exists.
	 */
	 UrbanMobilitySystem getUrbanMobilitySystem();
	
	/**
	 * Returns the mapping of rectangular world space used in this world to
	 * another coordinate space of e.g. visualization GUI.
	 * 
	 * @return Mapping between rectangular world space and another rectangular
	 *         space.
	 */
	 WorldTransformation getTransformation();
	
}
