package allow.simulator.world;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nlogo.agent.Agent;
import org.nlogo.agent.AgentSet;
import org.nlogo.agent.AgentSet.Iterator;
import org.nlogo.agent.Link;
import org.nlogo.agent.Patch;
import org.nlogo.agent.Turtle;
import org.nlogo.api.AgentException;

import allow.simulator.entity.Bus;
import allow.simulator.entity.Entity;
import allow.simulator.entity.Person;
import allow.simulator.entity.TransportAgency;
import allow.simulator.flow.activity.Activity.Type;
import allow.simulator.netlogo.agent.BusAgent;
import allow.simulator.netlogo.agent.IAgent;
import allow.simulator.netlogo.agent.PersonAgent;
import allow.simulator.netlogo.agent.TransportAgencyAgent;
import allow.simulator.util.Coordinate;
import allow.simulator.util.Geometry;
import allow.simulator.util.Pair;

/**
 * Represents the simulated world and manages the street graph and the
 * entities. Interfaces the NetLogo simulation environment.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class NetLogoWorld extends World {

	// Reference to NetLogo world.
	private org.nlogo.agent.World netlogoWorld;
	
	// Mapping of simulator Ids to NetLogo Ids.
	private Map<Long, Long> simToNetLogo;
	private Map<Long, Long> netLogoToSim;
	
	// Mapping of street segments to NetLogo links.
	private Map<StreetSegment, Link> links;
	
	private List<List<List<IAgent>>> agentsPerPatch;
	
	/**
	 * Constructor.
	 * Creates a new instance of a simulated world bound to the NetLogo
	 * simulation environment.
	 * 
	 * @param w NetLogo world.
	 * @param mapUrl Path to map containing the street graph description.
	 * @throws IOException 
	 */
	public NetLogoWorld(org.nlogo.agent.World w, Path worldConfig) throws IOException {
		super(worldConfig);
		simToNetLogo = new HashMap<Long, Long>();
		netLogoToSim = new HashMap<Long, Long>();
		netlogoWorld = w;
		int width = Math.abs(netlogoWorld.minPxcor()) + Math.abs(netlogoWorld.maxPxcor()) + 1;
		int height = Math.abs(netlogoWorld.minPycor()) + Math.abs(netlogoWorld.maxPycor()) + 1;
		agentsPerPatch = new ArrayList<List<List<IAgent>>>(width);
		
		for (int i = 0; i < width; i++) {
			List<List<IAgent>> newList = new ArrayList<List<IAgent>>(height);
			
			for (int j = 0; j < height; j++) {
				newList.add(new ArrayList<IAgent>());
			}
			agentsPerPatch.add(newList);
		}		
		// Create NetLogo street network.
		double worldEnvelope[] = new double[] { netlogoWorld.minPxcor(), netlogoWorld.maxPxcor(), netlogoWorld.minPycor(), netlogoWorld.maxPxcor() };
						
		// Get envelope of loaded world.
		double gisEnvelope[] = streetNetwork.getDimensions();
				
		// Set transformation between NetLogo and loaded world.
		transformation.setTransformation(gisEnvelope, worldEnvelope);
		
		// Create NetLogo bindings for street nodes.
		Coordinate temp = new Coordinate();
		Map<Long, Turtle> util = new HashMap<Long, Turtle>();
				
		for (StreetNode node : streetNetwork.getStreetNodes()) {
			transformation.GISToNetLogo(node.getPosition(), temp);
			Turtle newNode = new Turtle(netlogoWorld, netlogoWorld.getBreed("NODES"), temp.x, temp.y);
			util.put(node.getId(), newNode);
			netlogoWorld.turtles().add(newNode);
			newNode.hidden(true);
		}
				
		// Create NetLogo bindings for street segments.
		Collection<StreetSegment> segments = streetNetwork.getStreetSegments();
		links = new HashMap<StreetSegment, Link>(segments.size());
				
		for (StreetSegment segment : segments) {
			Pair<StreetNode, StreetNode> in = streetNetwork.getIncidentNodes(segment);
			Link newLink = netlogoWorld.linkManager.createLink(util.get(in.first.getId()), util.get(in.second.getId()), netlogoWorld.links());
			netlogoWorld.links().add(newLink);
			newLink.colorDouble(5.0);
			newLink.lineThickness(0.05);
			newLink.hidden(false);
			links.put(segment, newLink);
		}
	}
	
	/**
	 * Adds a new entity to the world and creates a binding to NetLogo.
	 * 
	 * @param e Entity to add.
	 */
	@Override
	public void addEntity(Entity e) {
		super.addEntity(e);
		
		// Create NetLogo agent to add.
		Agent newAgent = null;
		
		try {
			
			switch (e.getType()) {
			
			case BUS:
			case FLEXIBUS:
				newAgent = new BusAgent(netlogoWorld, (Bus) e);
				break;
			
			case PERSON:
				newAgent = new PersonAgent(netlogoWorld, (Person) e);
				break;
				
			case PUBLICTRANSPORTAGENCY:
			case FLEXIBUSAGENCY:
				newAgent = new TransportAgencyAgent(netlogoWorld, (TransportAgency) e);
				break;
				
			default:
				break;
			}
						
			if (newAgent != null) {
				// Add new agent to NetLogo world.
				netlogoWorld.turtles().add(newAgent);
			
				// Add mapping of simulation Id to world Id.
				if (netLogoToSim.get(newAgent.id) != null) 
					throw new IllegalStateException("Error: NetLogo entity Id" + newAgent.id + " already in use.");
				netLogoToSim.put(newAgent.id, e.getId());	
				
				if (simToNetLogo.get(e.getId()) != null) 
					throw new IllegalStateException("Error: Simulator entity Id" + e.getId() + " already in use.");
				simToNetLogo.put(e.getId(), newAgent.id);
			}
		} catch (AgentException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Removes an entity from the world and removes the corresponding agent
	 * from the NetLogo world.
	 * 
	 * @param entityId Id of the entity to remove.
	 * @return Removed entity.
	 */
	@Override
	public Entity removeEntity(long entityId) {
		// Remove entity from entity map.
		Entity e = super.removeEntity(entityId);
		
		if (e != null) {
			// Make corresponding NetLogo agent die.
			Turtle t = (Turtle) netlogoWorld.turtles().agent(simToNetLogo.get(entityId));
			netLogoToSim.remove(t.id);
			simToNetLogo.remove(entityId);
			t.die();
		}
		return e;
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
	public List<Entity> getNearEntities(Entity entity, double distance, List<Entity> buffer) {
		// Get patch entity is on.
		Patch currentPatch = ((Turtle) netlogoWorld.turtles().agent(simToNetLogo.get(entity.getId()))).getPatchHere();
		
		List<IAgent> agentsOnCurrentPatch = agentsPerPatch.get(currentPatch.pxcor + netlogoWorld.maxPxcor()).get(currentPatch.pycor + netlogoWorld.maxPycor());
		
		for (IAgent temp : agentsOnCurrentPatch) {
			
			if (temp.getEntity().isActive() 
					&& !temp.getEntity().getFlow().isIdle()
					&& (temp.getEntity().getFlow().getCurrentActivity().getType() == Type.DRIVE
						|| temp.getEntity().getFlow().getCurrentActivity().getType() == Type.WALK
						|| temp.getEntity().getFlow().getCurrentActivity().getType() == Type.CYCLE
						|| temp.getEntity().getFlow().getCurrentActivity().getType() == Type.USE_PUBLIC_TRANSPORT)
					&& (temp.getEntity().getId() != entity.getId())
					&& (Geometry.haversine(entity.getPosition(), temp.getEntity().getPosition()) <= distance)) {
				buffer.add(temp.getEntity());
			}
		}
		return buffer;
	}
	
	public void updateGrid() {
		AgentSet patches = netlogoWorld.patches();
		int maxX = netlogoWorld.maxPxcor();
		int maxY = netlogoWorld.maxPycor();
	
		for (Iterator patchesIt = patches.iterator(); patchesIt.hasNext(); ) {
			Patch patch = (Patch) patchesIt.next();
			List<IAgent> agentsOnCurrentPatch = agentsPerPatch.get(patch.pxcor + maxX).get(patch.pycor + maxY);
			agentsOnCurrentPatch.clear();

			for (Turtle t : patch.turtlesHere()) {
				 
				if (t instanceof PersonAgent) {
					agentsOnCurrentPatch.add((IAgent) t);
				}
			}
		}
	}
	
	public void updateHeatMap() {
		AgentSet patches = netlogoWorld.patches();
		int maxX = netlogoWorld.maxPxcor();
		int maxY = netlogoWorld.maxPycor();
		
		for (Iterator patchesIt = patches.iterator(); patchesIt.hasNext(); ) {
			 Patch patch = (Patch) patchesIt.next();
			 AgentSet neighbours = patch.getNeighbors();
			 double n = agentsPerPatch.get(patch.pxcor + maxX).get(patch.pycor + maxY).size();
			 double weight = 1.0 / (double) neighbours.count();
			 
			 for (Iterator nIt = neighbours.iterator(); nIt.hasNext(); ) {
				 Patch nei = (Patch) nIt.next();
				 n += (weight * agentsPerPatch.get(nei.pxcor + maxX).get(nei.pycor + maxY).size());
			 }
			 n *= 0.5;
			 
			 if (n < 4) {
				 patch.pcolor(9.9);
				 
			 } else if (n < 12) {
				 patch.pcolor(96.0);
				 
			 } else if (n < 20) {
				 patch.pcolor(66.0);
				 
			 } else if (n < 28) {
				 patch.pcolor(46.0);
				 
			 } else {
				 patch.pcolor(16.0);
			 }
		}
		
	}
}
