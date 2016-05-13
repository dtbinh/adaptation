package allow.util.tracesfromgtfs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import allow.simulator.util.Coordinate;
import allow.simulator.util.Geometry;
import allow.simulator.util.Pair;
import allow.simulator.world.StreetMap;
import allow.simulator.world.StreetNode;
import allow.simulator.world.StreetSegment;


public class ScoredPath implements Comparable<ScoredPath> {

	// Matching of point to segments.
	private List<Match> matches;
	
	// Path through street network.
	private Path path;
	
	// Graph to use.
	private StreetMap graph;
	
	// Score of path (accumulated distances of points to segments).
	private double score;
	
	// Length of current segment.
	private double lengthOfCurrentSegment;
	
	// Length moved on current segment.
	private double lengthOnCurrentSegment;
	
	public ScoredPath(StreetMap g, Coordinate initialPos, StreetSegment initialSegment) {
		// Initialize graph, match list, and path list.
		this.graph = g;
		matches = new ArrayList<Match>();
		path = new Path();
		
		// Initialize length of current segment.
		lengthOfCurrentSegment = initialSegment.getLength();
						
		// Add initial match and update score.
		Coordinate proj = projectPointToSegment(initialPos, initialSegment);
		score = Geometry.haversine(proj, initialPos);
		matches.add(new Match(initialSegment, initialPos, score));

		// Add initial match.
		path.addSegment(initialSegment);

		// Initialize length on current segment.
		Pair<StreetNode, StreetNode> nodes = graph.getIncidentNodes(initialSegment);
		lengthOnCurrentSegment = Geometry.haversine(proj, nodes.first.getPosition());	
	}
		
	public ScoredPath(ScoredPath toCopy) {
		this.graph = toCopy.graph;
		this.matches = new ArrayList<Match>(toCopy.matches);
		this.path = new Path(toCopy.path);
		this.score = toCopy.score;
		this.lengthOfCurrentSegment = toCopy.lengthOfCurrentSegment;
		this.lengthOnCurrentSegment = toCopy.lengthOnCurrentSegment;
	}
	
	public ScoredPath() {
		matches = new ArrayList<Match>();
		path = new Path();
		graph = null;
		score = 0.0;
		lengthOfCurrentSegment = 0.0;
		lengthOnCurrentSegment = 0.0;
	}
	
	public List<ScoredPath> addPoint(Coordinate newPoint) {
		// Return list.
		List<ScoredPath> ret = new ArrayList<ScoredPath>();
		
		// Check if end of segment is reached.
		Match previousMatch = matches.get(matches.size() - 1);
		double distLast = Geometry.haversine(newPoint, projectPointToSegment(previousMatch.getPoint(), previousMatch.getSegment()));
		Coordinate projTemp = projectPointToSegment(newPoint, previousMatch.getSegment());

		if (lengthOnCurrentSegment + distLast < lengthOfCurrentSegment * 0.6) {
			// If end of current segment is not yet reached, project current point
			// to current segment and check lengthOnCurrentSegment.
			//Coordinate projTemp = projectPointToSegment(newPoint, previousMatch.getSegment());
			//double lengthOnCurrentSegmentTemp = PolylinesUtil.haversine(projTemp, graph.getSource(previousMatch.getSegment()).getPosition());
			double newScore = Geometry.haversine(projTemp, newPoint);
			score += newScore;
			lengthOnCurrentSegment = Geometry.haversine(projTemp, previousMatch.getSegment().getStartingPoint());
			Match newMatch = new Match(previousMatch.getSegment(), newPoint, newScore);
			matches.add(newMatch);
			ret.add(this);

		} else {
			// If distance between last and current point exceeds current segment
			// length, create new paths of appropriate length.
			List<Path> newPaths = getSuccessivePaths(distLast, previousMatch.getPoint(), newPoint);
			
			for (Path newPath : newPaths) {
				// List of segments of current path.
				List<StreetSegment> segments = newPath.getPath();
				
				// Get segment from list with minimal distance to new point.
				int minIndex = path.getPath().size() - 1;
				StreetSegment nearest = segments.get(minIndex);
				double distTempMin = Geometry.haversine(projectPointToSegment(newPoint, nearest), newPoint);
				
				for (int i = minIndex; i < segments.size(); i++) {
					StreetSegment segment = segments.get(i);
					double distTemp = Geometry.haversine(projectPointToSegment(newPoint, segment), newPoint);
					
					if (distTemp < distTempMin) {
						distTempMin = distTemp;
						nearest = segment;
						minIndex = i;
					}
				}
				int toRemove = minIndex + 1;
					
				for (int i = toRemove; i < segments.size(); i++) {
					newPath.removeLastSegment();
				}
				
				// Compute projection score.
				Coordinate proj = projectPointToSegment(newPoint, nearest);
				double newScore = Geometry.haversine(proj, newPoint);
				
				//if (newScore <= distLast + 35) {
					// Create new ScoredPath instance for each path.
					ScoredPath newScoredPath = new ScoredPath();
					newScoredPath.graph = graph;
				
					// Update path.
					newScoredPath.path = newPath;
					newScoredPath.score = score + newScore;
				
					// Update length on and of current segment.
					StreetNode source = graph.getSource(nearest);
					newScoredPath.lengthOnCurrentSegment = Geometry.haversine(proj, source.getPosition());
					newScoredPath.lengthOfCurrentSegment = nearest.getLength();
				
					// Update matches.
					Match newMatch = new Match(nearest, newPoint, newScore);
					newScoredPath.matches = new ArrayList<Match>(matches);
					newScoredPath.matches.add(newMatch);				

					// Add to return.
					ret.add(newScoredPath);
				//}
			}
		}
		return ret;
	}
 	
	public double getLength() {
		return path.getLength();
	}
 	public List<StreetSegment> getPath() {
 		return path.getPath();
 	}
 	
 	public double getScore() {
 		int scoreTemp = (int) (Math.ceil(score / 7.0) * 7);
 		return scoreTemp;
 	}
 	
 	public int countMatches() {
 		return matches.size();
 	}
 	
	@Override
	public int compareTo(ScoredPath o) {
		if (getScore() < o.getScore()) {
			return -1;
			
		} else if (getScore() == o.getScore()) {
			//int matchScore = matches.get(matches.size() - 1).compareTo(o.matches.get(o.matches.size() - 1));
			
			//if (matchScore == 0) {
				return path.compareTo(o.path);
			//}
			//return matchScore;
		} else {
			return 1;
		}
	}
	
	private static Coordinate projectPointToSegment(Coordinate c, StreetSegment s) {
		Coordinate start = s.getStartingPoint();
		Coordinate end = s.getEndPoint();
		Coordinate a = new Coordinate(end.x - start.x, end.y - start.y);
		Coordinate b = new Coordinate(c.x - start.x, c.y - start.y);
		
		double norm_a_square = a.x * a.x + a.y * a.y;
		double r = (a.x * b.x + a.y * b.y) / norm_a_square;
		
		if (r < 0.0) {
			return start;
			
		} else if (r >= 0.0 && r < 1.0) {
			return new Coordinate(start.x + r * a.x, start.y + r * a.y);
		
		} else {
			return end;
		}
	}
	
	private static double distancePointSegment(Coordinate c, StreetSegment s) {
		Coordinate t = projectPointToSegment(c, s);
		return Geometry.haversine(c, t);
	}
	
	private List<Path> getSuccessivePaths(double length, Coordinate lastPoint, Coordinate currentPoint) {
		StreetSegment temp = new StreetSegment(-1, new StreetNode(-1, "", lastPoint), new StreetNode(-1, "", currentPoint), 0, Geometry.haversine(lastPoint, currentPoint));
		
		// Buffer for loops.
		Queue<Pair<Match, Path>> input = new PriorityQueue<Pair<Match, Path>>(512, new PathComparator());
		Queue<Pair<Match, Path>> output = new PriorityQueue<Pair<Match, Path>>(512, new PathComparator());

		Path init = new Path(path);
		StreetSegment initSeg = init.getPath().get(init.getPath().size() - 1);
		Coordinate proj = projectPointToSegment(currentPoint, initSeg);
		Match initMatch = new Match(initSeg, proj, distancePointSegment(proj, temp));
		
		//System.out.println(currentPoint + " " + initMatch);
		// Match initMatch = new Match(initSeg, currentPoint, PolylinesUtil.haversine(currentPoint, proj));
		input.add(new Pair<Match, Path>(initMatch, init));
		
		// List of paths to return.
		List<Path> ret = new ArrayList<Path>();
		double maxLength = init.getLength() + length;

		while (input.size() > 0) {
			int maxLoop = Math.min(input.size(), 60);
			
			for (int i = 0; i < maxLoop; i++) {
				Pair<Match, Path> match = input.poll();
				Path inPath = match.second;
				if (inPath.getLength() >= maxLength) {
	
					if (!ret.contains(inPath)) {
						ret.add(inPath);
					}
					
				} else {
					// Get last inserted segment.
					List<StreetSegment> segs = inPath.getPath();
					StreetSegment seg = segs.get(segs.size() - 1);
					
					// Get destination node and all its outgoing segments.
					StreetNode segDest = graph.getDestination(seg);
					//StreetNode segSource = graph.getSource(seg);
					Collection<StreetSegment> outgoingSegs = graph.getOutGoingSegments(segDest);
					
					for (StreetSegment out : outgoingSegs) {
						//StreetNode outDest = graph.getDestination(out);
						
						if (/*(outDest != segSource) &&*/ !segs.contains(out)) {
							Path newPath = new Path(inPath);
							newPath.addSegment(out);
							Coordinate t = projectPointToSegment(currentPoint, out);
							Match newMatch = new Match(out, t, distancePointSegment(t, temp));
							Pair<Match, Path> newPair = new Pair<Match, Path>(newMatch, newPath);
							
							if (newMatch.getScore() <= 500.0 && !output.contains(newPair)) {
								output.add(newPair);
							}
						}
					}
				}
			}
			// Swap handles.
			Queue<Pair<Match, Path>> tempBuffer = input;
			input = output;
			output = tempBuffer;
			output.clear();
		}
		//System.out.println();
		return ret;
		
		/*Queue<Path> input = new PriorityQueue<Path>();
		Queue<Path> output = new PriorityQueue<Path>();
		
		// Initial list of segments.
		Path init = new Path(path);
		input.add(init);
		
		// List of paths to return.
		List<Path> ret = new ArrayList<Path>();
		double maxLength = init.getLength() + length;

		while (input.size() > 0) {
			//int maxLoop = Math.min(input.size(), 500);
			int maxLoop = input.size();
			
			for (int i = 0; i < maxLoop; i++) {
				Path inPath = input.poll();
				
				if (inPath.getLength() >= maxLength) {
					
					if (!ret.contains(inPath)) {
						ret.add(inPath);
					}
					
				} else {
					// Get last inserted segment.
					List<StreetSegment> segs = inPath.getPath();
					StreetSegment seg = segs.get(segs.size() - 1);
					
					// Get destination node and all its outgoing segments.
					StreetNode segDest = graph.getDestination(seg);
					StreetNode segSource = graph.getSource(seg);
					Collection<StreetSegment> outgoingSegs = graph.getOutGoingSegments(segDest);
					
					for (StreetSegment out : outgoingSegs) {
						StreetNode outDest = graph.getDestination(out);
						
						if ((outDest != segSource) && !segs.contains(out)) {
							Path newPath = new Path(inPath);
							newPath.addSegment(out);
							
							if (!output.contains(newPath)) {
								output.add(newPath);
							}
						}
					}
				}
			}
			// Swap handles.
			Queue<Path> temp = input;
			input = output;
			output = temp;
			output.clear();	
		}
		return ret;*/
	}

	public String toString() {
		return "[ScoredPath " + score / matches.size() + " " + matches.get(matches.size() - 1) + "]";
		

	}
	
	@Override
	public boolean equals(Object other) {
		
		if (other == this) {
			return true;
		}
		
		if (!(other instanceof ScoredPath)) {
			return false;
		}
		ScoredPath p = (ScoredPath) other;
		return score == p.score 
				&& matches.equals(p.matches)
				&& path.equals(p.path)
				&& lengthOnCurrentSegment == p.lengthOnCurrentSegment
				&& lengthOfCurrentSegment == p.lengthOfCurrentSegment;
	}
	
}
