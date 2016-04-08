package allow.util.tracesfromgtfs;

import java.util.Comparator;

import allow.simulator.util.Pair;

public class PathComparator implements Comparator<Pair<Match, Path>>{

	@Override
	public int compare(Pair<Match, Path> o1, Pair<Match, Path> o2) {
		int score1 = (int) Math.ceil(o1.first.getScore() / 7.0) * 7;
		int score2 = (int) Math.ceil(o2.first.getScore() / 7.0) * 7;

		if (score1 < score2) {
			return -1;
			
		} else if (score1 == score2) {
			return o1.second.compareTo(o2.second);
		} else {
			return 1;
		}
		/*if (o1.first.getScore() < o2.first.getScore()) {
			return -1;
			
		} else if (o1.first.getScore() == o2.first.getScore()) {
			return o1.second.compareTo(o2.second);
		} else {
			return 1;
		}*/
	}
}
