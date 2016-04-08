package allow.simulator.world.layer;

import java.util.List;

import allow.simulator.util.Coordinate;

/**
 * Represents a polygon area which is given by a set of points describing its
 * vertices.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class PolygonShape extends Shape {
	// Polygon covering the area.
	private List<Coordinate> boundary;
	private Coordinate center;
	private double constant[];
	private double multiple[];
		
	/**
	 * Constructor.
	 * Creates a new instance of a polygon shape given the vertices that describe
	 * it boundary.
	 * 
	 * @param boundary Set of points/vertices representing the boundary of the
	 *        polygon. 
	 */
	public PolygonShape(List<Coordinate> boundary) {
		this.boundary = boundary;
		center = new Coordinate();
		
		for (Coordinate b : boundary) {
			center.x += b.x;
			center.y += b.y;
		}
		center.x /= boundary.size();
		center.y /= boundary.size();
		
		constant = new double[boundary.size()];
		multiple = new double[boundary.size()];
		precalcValues();
	}

	@Override
	public boolean contains(Coordinate point) {
		int polySize = boundary.size();
		int	i = 0; 
		int j = polySize - 1;
		boolean oddNodes = false;

		for (i = 0; i < polySize; i++) {
			Coordinate p1 = boundary.get(i);
			Coordinate p2 = boundary.get(j);
			
		    if ((p1.y < point.y && p2.y >= point.y || p2.y < point.y && p1.y >= point.y)) {
		    	if (p1.x + (point.y - p1.y) / (p2.y - p1.y) * (p2.x-p1.x) < point.x) oddNodes=!oddNodes; 
		    }
		    j=i; 
		}
		return oddNodes;
	}

	@Override
	public List<Coordinate> getBoundary() {
		return boundary;
	}
	
	private void precalcValues() {
		int polySize = boundary.size();
		int i = 0;
		int j = polySize - 1;

		for(i = 0; i < polySize; i++) {
			Coordinate p1 = boundary.get(i);
			Coordinate p2 = boundary.get(j);
			
			if (p2.y == p1.y) {
				constant[i] = p1.x;
		      	multiple[i] = 0;
		      	
		    } else {
		    	double dY = 1.0 / p2.y - p1.y;     
		    	constant[i]= p1.x - ((p1.y * p2.x) + (p1.y * p1.x)) * dY;
		    	multiple[i]= (p2.x - p1.x) * dY; 
		    }
		    j = i;
		 }
	}

	@Override
	public Coordinate getCenter() {
		return center;
	}

}
