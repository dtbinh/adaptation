    package allow.simulator.entity.knowledge;


public abstract class Experience {

	public enum Type {
		
		/**
		 * Movement experiences e.g. information about travelling segments.
		 */
		TRAVEL,
		
		/**
		 * Stop experiences of public transportation e.g. how many people
		 * were on a bus after it left a certain stop.
		 */
		STOP
	}

	// Type of experience mainly used to produce correct logging output.
	private Type type;
		
	protected Experience(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
}
