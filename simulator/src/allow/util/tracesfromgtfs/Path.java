package allow.util.tracesfromgtfs;

import java.util.ArrayList;
import java.util.List;

import allow.simulator.world.StreetSegment;

public class Path implements Comparable<Path> {

	// Path, i.e. list of segments.
	private ArrayList<StreetSegment> path;
	
	// Length of the path.
	private double length;
	
	public Path() {
		// Allocate new segment list.
		path = new ArrayList<StreetSegment>();
		
		// Length is zero.
		length = 0.0;
	}
	
	public Path(Path toCopy) {
		// Copy original path and length.
		path = new ArrayList<StreetSegment>(toCopy.path);
		length = toCopy.length;
	}
	
	public List<StreetSegment> getPath() {
		return path;
	}
	
	public double getLength() {
		return length;
	}
	
	public void addSegment(StreetSegment newSegment) {
		path.add(newSegment);
		length += newSegment.getLength();
	}
	
	public void removeLastSegment() {
		StreetSegment toRemove = path.remove(path.size() - 1);
		length -= toRemove.getLength();
	}

	@Override
	public int compareTo(Path o) {

		if (path.size() < o.path.size()) {
			return -1;
		
		} else if (path.size() == o.path.size()) {
			return 0;
			
		} else {
			return 1;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof Path)) {
			return false;
			
		}
		Path p = (Path) other;
		return path.equals(p.path);

	}
	
	public String toString() {
		return path.toString();
	}
}
