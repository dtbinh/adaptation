package allow.simulator.util;

public class Pair<V, E> {
	
	public V first;
	public E second;
	
	public Pair(V first, E second) {
		this.first = first;
		this.second = second;
	}
	
	public String toString() {
		return "[Pair " + first.toString() + ", " + second.toString() + "]";
	}
}
