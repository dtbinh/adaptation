package allow.simulator.world.layer;

import java.util.List;

import allow.simulator.util.Coordinate;

public final class DistrictArea extends Area {
	// Type of the area e.g. residential, industrial,...
	private DistrictType type;
	
	/**
	 * Constructor.
	 * Creates a new instance of a DistrictArea given its name, its vertices, and its
	 * type (residential, industrial,...).
	 * 
	 * @param name (String) Name of the area.
	 * @param boundary List of vertices describing the bounding polygon of the area.
	 * @param type District type of the area.
	 */
	public DistrictArea(String name, List<Coordinate> boundary, DistrictType type) {
		super(name, boundary);
		this.type = type;
	}
	
	/**
	 * Returns the district type of the area.
	 * 
	 * @return District type of the area.
	 */
	public DistrictType getType() {
		return type;
	}

}
