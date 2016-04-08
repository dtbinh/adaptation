package allow.util.tracesfromgtfs;

import allow.simulator.util.Coordinate;
import allow.simulator.world.StreetSegment;

public class Match implements Comparable<Match> {

	// Segment point is matched to.
	private StreetSegment segment;
	
	// Point which is matched.
	private Coordinate point;
	
	// Score of this match.
	private double score;
	
	/**
	 * Constructor.
	 * Creates a new instance of a matching of a point to a segment.
	 * 
	 * @param seg Segment of match.
	 * @param point Point of match.
	 * @param score Score of the match.
	 */
	public Match(StreetSegment seg, Coordinate point, double score) {
		this.segment = seg;
		this.point = point;
		this.score = score;
	}
	
	/**
	 * Returns the segment of this match.
	 * 
	 * @return Segment of this match.
	 */
	public StreetSegment getSegment() {
		return segment;
	}
	
	/**
	 * Returns the point of this match.
	 * 
	 * @return Point of this match.
	 */
	public Coordinate getPoint() {
		return point;
	}
	
	/**
	 * Returns the score of this match.
	 * 
	 * @return Score of this match.
	 */
	public double getScore() {
		return score;
	}
	
	@Override
	public boolean equals(Object other) {
		// If object references are identical return true.
		if (other == this) {
			return true;
		}
		
		// If objects are of different class return false.
		if (getClass() != other.getClass()){
			return false;
		}
		// Otherwise compare point, segment, and score.
		Match m = (Match) other;
		return score == m.score
				&& point.equals(m.point)
				&& segment.equals(m.segment);
	}

	@Override
	public int hashCode() {
		long scoreHash = Double.doubleToLongBits(score);
		return 37 + 39 * (segment.hashCode() + point.hashCode() + (int) (scoreHash ^ (scoreHash >>> 32)));
	}
	
	@Override
	public int compareTo(Match o) {
		// If the matches consider different points, they cannot be compared.
		if (!point.equals(o.point)) {
			return 0;
		}
		
		if (!segment.equals(o.segment)) {
			return 0;
		}
		
		// Compare scores of matches.
		if (score < o.score) {
			return -1;
			
		} else if (score == o.score) {
			return 0;
			
		} else  {
			return 1;
		}
	}
	
	public String toString() {
		return "[Match " + segment.toString() + ", " + point.toString() + ", " + score + "]";
	}
}
