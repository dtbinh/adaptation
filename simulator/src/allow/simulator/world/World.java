package allow.simulator.world;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import allow.simulator.entity.Entity;
import allow.simulator.entity.UrbanMobilitySystem;

/**
 * Abstract class representing a simulated world.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public abstract class World implements IWorld {
	
	// Entities of the simulated world grouped by entity type.
	protected Map<Entity.Type, Map<Long, Entity>> entities;
		
	// Street network.
	protected StreetMap streetNetwork;
	
	// Transformation from world coordinate space to e.g. a visualization
	// coordinate space.
	protected WorldTransformation transformation;
	
	/**
	 * Constructor.
	 * Creates new World loading the street network encoded in mapUrl.
	 *  
	 * @param mapUrl File containing the street network.
	 * @throws IOException 
	 */
	public World(Path worldConfig) throws IOException {
		// Prepare entity mapping.
		entities = new ConcurrentHashMap<Entity.Type, Map<Long, Entity>>();
		
		// Load map from file.
		streetNetwork = new StreetMap(worldConfig);
		
		// Create (unit) transformation.
		transformation = new WorldTransformation();
	}
	
	/**
	 * Adds a new Entity to the simulated world.
	 * 
	 * @param e Entity to add.
	 */
	@Override
	public void addEntity(Entity e) {
		// Get mapping of entity type.
		Map<Long, Entity> entitiesOfType = entities.get(e.getType());
		
		if (entitiesOfType == null) {
			entitiesOfType = new ConcurrentHashMap<Long, Entity>();
			entities.put(e.getType(), entitiesOfType);
		}
		if (entitiesOfType.get(e.getId()) != null) 
			throw new IllegalStateException("Error: Simulator entity Id" + e.getId() + " already in use.");
		entitiesOfType.put(e.getId(), e);
	}
	
	/**
	 * Removes an entity from the world given its Id.
	 * 
	 * @param entityId Id of the entity to remove.
	 * @return Removed entity or null if no entity with given Id existed.
	 */
	@Override
	public Entity removeEntity(long entityId) {
		
		for (Entity.Type temp : entities.keySet()) {
			Entity e = entities.get(temp).get(entityId);
			
			if (e != null) {
				entities.get(temp).remove(entityId);
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Returns an entity given its Id.
	 * 
	 * @param entityId Id of the entity to return.
	 * @return Entity with given Id or null if no entity with given Id exists.
	 */
	@Override
	public Entity getEntityById(long entityId) {
		
		for (Entity.Type temp : entities.keySet()) {
			Entity e = entities.get(temp).get(entityId);
			
			if (e != null) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Returns all entities of the given type.
	 * 
	 * @param type Type of entities to return.
	 * @return Collection of entities of given type.
	 */
	public Collection<Entity> getEntitiesOfType(Entity.Type type) {
		Map<Long, Entity> ret = entities.get(type);
		return (ret != null) ? ret.values() : new ArrayList<Entity>(0);
	}
	
	/**
	 * Returns a list of entities which are physically close to a given entity.
	 * Closeness is defined by the distance parameter.
	 * 
	 * @param entityId Id of the entity to return near entities.
	 * @param distance Maximal distance within which entities are close.
	 * @return List of close entities.
	 */
	@Override
	public abstract List<Entity> getNearEntities(Entity entity, double distance, List<Entity> buffer);

	@Override
	public StreetMap getStreetMap() {
		return streetNetwork;
	}
	
	@Override
	public WorldTransformation getTransformation() {
		return transformation;
	}
	
	@Override
	public UrbanMobilitySystem getUrbanMobilitySystem() {
		Collection<Entity> planners = entities.get(Entity.Type.URBANMOBILITYSYSTEM).values();
		
		if (planners.isEmpty()) {
			return null;
		}
		return (UrbanMobilitySystem) planners.iterator().next();
	}
}
