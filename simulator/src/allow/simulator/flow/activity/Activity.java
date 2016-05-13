package allow.simulator.flow.activity;

import allow.simulator.entity.Entity;

/**
 * Abstract class representing an Activity to be executed by an entity in a
 * flow.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public abstract class Activity {
	
	public enum Type {
		
		/**
		 * Bus activities.
		 */
		PREPARE_TRIP,
		
		PICKUP_AND_WAIT,
		
		DRIVE_TO_NEXT_STOP,
		
		RETURN_TO_AGENCY,

		/**
		 * Person activities.
		 */
		USE_PUBLIC_TRANSPORT,
		
		PLAN_JOURNEY,
		
		FILTER_ALTERNATIVES,
		
		RANK_ALTERNATIVES,
		
		PREPARE_JOURNEY,
		
		DRIVE,
		
		WALK,
		
		CYCLE,
		
		CORRECT_POSITION,
		
		REGISTER_TO_FLEXIBUS,
		
		USE_FLEXIBUS,
		
		REPLAN,
		
		WAIT,
		
		/**
		 * Taxi activities.
		 */
		PREPARE_TAXI_TRIP,
		
		DRIVE_TO_NEXT_DESTINATION,
		
		PICK_UP_OR_DROP,
		
		RETURN_TO_TAXI_AGENCY,
		
		/**
		 * Transportation agency activities.
		 */
		SCHEDULE_NEXT_TRIPS,

		SCHEDULE_NEXT_FLEXIBUS_TRIPS,
		
		SCHEDULE_NEXT_TAXI_TRIPS,
		
		/**
		 * Smart planner activities.
		 */
		QUERY_JOURNEY_PLANNER,
		
		/**
		 * General activities.
		 */
		LEARN
		
	}
	// Indicates if Activity has finished.
	private boolean finished;
	
	// Type of the Activity.
	protected Type type;
	
	// Entity executing the activity.
	protected Entity entity;
	
	// Starting and ending timestamps.
	protected long tStart;
	protected long tEnd;
	
	/**
	 * Constructor.
	 * 
	 * @param type Type of the Activity.
	 * @param entitiy Entity supposed to execute the Activity.
	 */
	protected Activity(Type type, Entity entity) {
		this.type = type;
		this.entity = entity;
		finished = false;
		tStart = -1;
		tEnd = -1;
	}
	
	/**
	 * Execute the Activity, which may require more than one call of execute().
	 * If the returned time is smaller than deltaT, activity has finished before
	 * deltaT.
	 * 
	 * @param deltaT Time to execute the activity.
	 * @return Time needed to execute the activity.
	 */
	public abstract double execute(double deltaT);
	
	/**
	 * Check, if current Activity is finished.
	 * 
	 * @return True, if Activity is finished, false otherwise.
	 */
	public boolean isFinished() {
		return finished;
	}
	
	/**
	 * Marks this Activity as finished.
	 */
	public void setFinished() {
		finished = true;
	}
	
	/**
	 * Returns the type of the Activity.
	 * 
	 * @return Type of the Activity.
	 */
	public Type getType() {
		return type;
	}
}
