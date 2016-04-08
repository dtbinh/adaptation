package allow.simulator.world.layer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import allow.simulator.util.Coordinate;
import allow.simulator.world.StreetMap;
import allow.simulator.world.StreetNode;

/**
 * Represents a layer on top of a StreetMap, i.e. a partitioning of the points
 * of the StreetMap into a set of not necessarily disjunct regions.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class DistrictLayer extends Layer {

	// protected String name;
	// protected StreetMap base;

	// Mapping area types to areas.
	private Map<DistrictType, List<Area>> areas;

	// Mapping of area to nodes within that area.
	// private Map<Area, List<StreetNode>> areaToNodesMapping;
	// private Map<StreetNode, List<Area>> nodesToAreaMapping;

	/**
	 * Constructor. Creates a new layer on top of the specified StreetMap base
	 * with the given name.
	 * 
	 * @param name Name of the layer to create.
	 * @param base StreetMap on top of which layer is created. All points will
	 *             initially be added to a default area of type UNKNOWN.
	 */
	public DistrictLayer(Type name, StreetMap base) {
		super(name, base);
		
		// Create default area spanning whole bounding rectangle of underlying StreetMap.
		double envelope[] = base.getDimensions();
		List<Coordinate> defaultArea = new ArrayList<Coordinate>(4);
		defaultArea.add(new Coordinate(envelope[0], envelope[2]));
		defaultArea.add(new Coordinate(envelope[0], envelope[3]));
		defaultArea.add(new Coordinate(envelope[1], envelope[3]));
		defaultArea.add(new Coordinate(envelope[1], envelope[2]));
		DistrictArea unknown = new DistrictArea("default", defaultArea,
				DistrictType.UNKNOWN);

		// Add default area to areas mapping.
		List<Area> defaults = new ArrayList<Area>(1);
		defaults.add(unknown);
		areas = new EnumMap<DistrictType, List<Area>>(DistrictType.class);
		areas.put(unknown.getType(), defaults);

		// Create area to node mapping and initialize with default area.
		areaToNodesMapping.put(unknown, new ArrayList<StreetNode>(base.getStreetNodesReduced()));

		for (StreetNode node : base.getStreetNodesReduced()) {
			List<Area> areas = new ArrayList<Area>(1);
			areas.add(unknown);
			nodesToAreaMapping.put(node, areas);
		}
	}

	public List<Area> getAreasOfType(DistrictType type) {
		return areas.get(type);
	}

	@Override
	public void addArea(Area area) {
		DistrictArea disArea = (DistrictArea) area;
		
		// Add area to known areas.
		List<Area> temp = areas.get(disArea.getType());

		if (temp == null)
			temp = new ArrayList<Area>();
		temp.add(area);
		areas.put(disArea.getType(), temp);

		List<StreetNode> stillDefault = areaToNodesMapping.get(areas.get(DistrictType.UNKNOWN).get(0));
		Map<Long, StreetNode> util = new HashMap<Long, StreetNode>(stillDefault.size());
		for (StreetNode n : stillDefault) {
			util.put(n.getId(), n);
		}

		// Create mapping of points to area and vice versa.
		List<StreetNode> nodes = new ArrayList<StreetNode>();

		for (StreetNode n : base.getStreetNodesReduced()) {

			if (area.pointInArea(n.getPosition())) {
				// If point lies inside area add it to list of nodes.
				nodes.add(n);

				// Check if point is still within default area.
				if (util.containsKey(n.getId())) {
					util.remove(n.getId());
					nodesToAreaMapping.get(n).clear();
				}
				nodesToAreaMapping.get(n).add(area);
			}
		}
		areaToNodesMapping.put(area, nodes);

		// Update still default nodes.
		stillDefault.clear();
		stillDefault.addAll(util.values());
	}
}
