package allow.util.tracesfromgtfs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import allow.simulator.util.Coordinate;
import allow.simulator.world.StreetMap;
import allow.simulator.world.StreetNode;
import allow.simulator.world.StreetSegment;

/**
 * Class implementing an algorithm for matching GPS traces to a given network.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class MapMatching {

	// Street network this map matching is working with.
	private StreetMap map;

	// Quad tree structure to allow fast access to nearest points.
	private QuadTree<StreetNode> quad;

	public MapMatching(StreetMap map) {
		this.map = map;
		final double dim[] = map.getDimensions();
		
		// Initialize quad tree.
		quad = new QuadTree<StreetNode>(dim[0], dim[2], dim[1], dim[3]);

		for (StreetNode node : map.getStreetNodes()) {
			quad.put(node.getPosition().x, node.getPosition().y, node);
		}
	}

	public ScoredPath mapMatch(List<Coordinate> gpsTrace, List<StreetSegment> initial) {
		// Catch empty trace.
		if (gpsTrace.size() == 0) {
			return null;
		}
		List<ScoredPath> initialPaths = new ArrayList<ScoredPath>(1);
		
		for (int i = 0; i < initial.size(); i++) {
			initialPaths.add(new ScoredPath(map, gpsTrace.get(0), initial.get(i)));
		}
		return match(gpsTrace, initialPaths);
	}

	public ScoredPath mapMatch(List<Coordinate> gpsTrace) {
		// Catch empty trace.
		if (gpsTrace.size() == 0) {
			return null;
		}

		// Get initial candidate segments using the quad tree structure.
		Coordinate firstPoint = gpsTrace.get(0);
		List<StreetNode> nearest = quad.get(firstPoint.x, firstPoint.y, 0.002);

		// If no nodes were found near the starting point, return null.
		if (nearest.size() == 0) {
			return null;
		}
		
		// Create initial scored path for each individual segment.
		List<ScoredPath> initialPaths = new ArrayList<ScoredPath>(nearest.size());

		// Otherwise add all segments incident to the candidate nodes to the set.
		for (StreetNode n : nearest) {
			Collection<StreetSegment> incidentEdges = map.getIncidentEdges(n);

			for (StreetSegment incident : incidentEdges) {
				ScoredPath newPath = new ScoredPath(map, gpsTrace.get(0), incident);
				
				if (newPath.getScore() <= 25.0 && !initialPaths.contains(newPath)) {
					initialPaths.add(newPath);
				}
			}
		}
		
		if (initialPaths.size() == 0) {
			System.out.println("Error: No initial paths.");
		}
		return match(gpsTrace, initialPaths);
	}
	
	private ScoredPath match(List<Coordinate> gpsTrace, List<ScoredPath> candidatePaths) {
		// Initialize buffer queues creating one path per candidate segment.
		Queue<ScoredPath> inputBuffer = new PriorityQueue<ScoredPath>(1024);
		Queue<ScoredPath> outputBuffer = new PriorityQueue<ScoredPath>(1024);

		for (ScoredPath candidatePath : candidatePaths) {

			if (!inputBuffer.contains(candidatePath)) {
				inputBuffer.offer(candidatePath);
			}
		}
		
		// Match each remaining point of trace.
		for (int j = 1; j < gpsTrace.size(); j++) {
			//System.out.println("Matching point " + j + " of " + gpsTrace.size());
			int loopMax = Math.min(inputBuffer.size(), 150);
			final Coordinate next = gpsTrace.get(j);

			for (int i = 0; i < loopMax; i++) {
				// Add point to current path.
				final List<ScoredPath> addPaths = inputBuffer.poll().addPoint(next);
				
				// Add new paths to output buffer and check for duplicates.
				for (ScoredPath pa : addPaths) {

					if (!outputBuffer.contains(pa)) {
						outputBuffer.add(pa);
					}
				}
			}
			inputBuffer.clear();
			
			// Swap buffer handles.
			Queue<ScoredPath> temp = inputBuffer;
			inputBuffer = outputBuffer;
			outputBuffer = temp;
			outputBuffer.clear();
		}
		return inputBuffer.peek();
	}
}
