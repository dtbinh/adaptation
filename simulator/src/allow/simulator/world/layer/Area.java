package allow.simulator.world.layer;

import java.util.List;

import allow.simulator.util.Coordinate;

public abstract class Area {
	// Name/identifier of the area, e.g. "city center".
	protected String name;
	
	// Shape of the area.
	protected Shape shape;
	
	/**
	 * Constructor.
	 * Creates a new instance of an area given its name and its boundary. 
	 * 
	 * @param name Name of the area to create.
	 * @param boundary Boundary of the area. In case there is only one point
	 *        a single point area is created.
	 */
	protected Area(String name, List<Coordinate> boundary) {
		this.name = name;
		
		if (boundary.size() == 1) {
			shape = new SinglePointShape(boundary.get(0));
			
		} else if (boundary.size() > 1) {
			shape = new PolygonShape(boundary);
		}
	}

	protected Area(String name, Coordinate point) {
		this.name = name;
		shape = new SinglePointShape(point);
	}
	
	/**
	 * Returns the name/identifier of the area, e.g. "city center".
	 * 
	 * @return Name/identifier of the area.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Determines whether a given point lies inside the area or not.
	 * 
	 * @param point Point to test.
	 * 
	 * @return True, if point lies within the area, false otherwise.
	 */
	public boolean pointInArea(Coordinate point) {
		return shape.contains(point);
	}
	
	/**
	 * Returns the boundary of the area given by a set of points/vertices.
	 * 
	 * @return Boundary of the area.
	 */
	public List<Coordinate> getBoundary() {
		return shape.getBoundary();
	}
	
	/**
	 * Returns the center of the area.
	 * 
	 * @return Center of the area.
	 */
	public Coordinate getCenter() {
		return shape.getCenter();
	}
	
	public String toString() {
		return name;
	}
}
