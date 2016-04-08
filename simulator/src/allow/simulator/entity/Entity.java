package allow.simulator.entity;

import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import allow.simulator.core.Context;
import allow.simulator.ensemble.IMessage;
import allow.simulator.entity.knowledge.EvoKnowledge;
import allow.simulator.entity.relation.RelationGraph;
import allow.simulator.entity.utility.IUtility;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.flow.activity.Flow;
import allow.simulator.util.Coordinate;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract class representing an entity which can be used in the simulation.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public abstract class Entity extends Observable {
	
	public enum Type {
		
		/**
		 * Person entities are the most general form. They cannot be instantiated
		 * directly. Use one of the following subclasses (child, homemaker,
		 * retired, student, worker) instead.
		 */
		PERSON,
		
		/**
		 * Bus entity executing a given fixed schedule.
		 */
		BUS,
		
		/**
		 * FlexiBus entity executing dynamic trips.
		 */
		FLEXIBUS,
		
		/**
		 * Train entity executing a given fixed schedule.
		 */
		TRAIN,
		
		/**
		 * Transport agency managing a static set of routes.
		 */
		PUBLICTRANSPORTAGENCY,
		
		/**
		 * FlexiBus agency providing dynamic on-request bus scheduling.
		 */
		FLEXIBUSAGENCY,
		
		/**
		 * Car pooling agency providing dynamic on-request ride-sharing.
		 */
		CARPOOLINGAGENCY,
		
		/**
		 * The urban mobility system for smart journey planning.
		 */
		URBANMOBILITYSYSTEM
		
	}
	
	// Id of the entity.
	protected long id;
	
	// Type of entity.
	protected Type type;
	
	// Utility module.
	protected IUtility utility;
		
	// Preferences of the entity, e.g. weights for travel time, costs, etc.
	protected Preferences preferences;
		
	// Simulation context.
	@JsonIgnore
	protected Context context;
	
	// Knowledge of the entity.
	@JsonIgnore
	protected EvoKnowledge knowledge;
	
	// Relations of the entity.
	@JsonIgnore
	protected RelationGraph relations;
	
	// Flow of activities to execute.
	@JsonIgnore
	protected Flow flow;
	
	// Position of an entity.
	@JsonIgnore
	protected Coordinate position;
	
	@JsonIgnore
	protected Queue<IMessage> messageQueue;
	
	/**
	 * Constructor.
	 * Creates a new entity with in a given simulation context. Knowledge and
	 * relations are newly initialized.
	 * 
	 * @param id Id used for identification.
	 * @param type Type of the entity.
	 * @param utility Utility function for decision making.
	 * @param prefs Preferences required for utility function.
	 * @param context Simulation context the entity is used in.
	 */
	protected Entity(long id, Type type, IUtility utility, Preferences prefs, Context context) {
		this.id = id;
		this.type = type;
		position = new Coordinate(-1, -1);
		knowledge = new EvoKnowledge(this);
		relations = new RelationGraph(this);
		this.context = context;
		flow = new Flow();
		this.utility = utility;
		this.preferences = prefs;
		messageQueue = new ConcurrentLinkedQueue<IMessage>();
		setPosition(position);
	}

	/**
	 * Constructor.
	 * Creates a new entity without a given simulation context. To use entity
	 * in simulation, context must be set with setContext(). Knowledge and 
	 * relations are newly initialized.
	 * 
	 * @param id Id used for identification.
	 * @param type Type of the entity.
	 * @param utility Utility function for decision making.
	 * @param prefs Preferences required for utility function.
	 */
	protected Entity(long id, Type type, IUtility utility, Preferences prefs) {
		// Initialize members.
		this.id = id;
		this.type = type;
		position = new Coordinate(-1, -1);
		knowledge = new EvoKnowledge(this);
		relations = new RelationGraph(this);
		flow = new Flow();
		this.utility = utility;
		this.preferences = prefs;
		messageQueue = new ConcurrentLinkedQueue<IMessage>();
		setPosition(position);
	}
	
	/**
	 * Returns Id of the entity.
	 * 
	 * @return Id of entity.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Returns type of the entity.
	 * 
	 * @return Type of the entity.
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Get current position of entity.
	 * 
	 * @return Current position of entity.
	 */
	public Coordinate getPosition() {
		return new Coordinate(position.x, position.y);
	}
		
	/**
	 * Set the current position of the entity.
	 * 
	 * @param newPosition The new position of the entity.
	 */
	public void setPosition(Coordinate newPosition) {
		position.x = newPosition.x;
		position.y = newPosition.y;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Returns flow of activities of the entity.
	 * 
	 * @return Flow of activities.
	 */
	@JsonIgnore
	public Flow getFlow() {
		return flow;
	}
	
	/**
	 * Specifies the flow to use by the entity for activity execution.
	 * 
	 * @param flow Flow to use for activity execution.
	 */
	public void setFlow(Flow flow) {
		this.flow = flow;
	}
	
	/**
	 * Returns knowledge instance of this entity.
	 * 
	 * @return Knowledge instance.
	 */
	@JsonIgnore
	public EvoKnowledge getKnowledge() {
		return knowledge;
	}
	
	/**
	 * Returns the context of this entity. In case entity has not been assigned
	 * to a specific simulation context.
	 *  
	 * @return Context of this entity or null in case entity has not been
	 *         assigned to a specific simulation context.
	 */
	@JsonIgnore
	public Context getContext() {
		return context;
	}
	
	/**
	 * Specifies the context the entity is used in.
	 * 
	 * @param context Context the entity is used in.
	 */
	public void setContext(Context context) {
		this.context = context;
	}
	
	/**
	 * Returns the utility function of this entity.
	 * 
	 * @return Utility function of this entity.
	 */
	public IUtility getUtility() {
		return utility;
	}
	
	/**
	 * Sets the utility function of this entity.
	 * 
	 * @param Utility function to use.
	 */
	public void setUtility(IUtility utility) {
		this.utility = utility;
	}
	
	/**
	 * Returns the preferences of this entity e.g. for utility computation.
	 * 
	 * @return Preferences of this entity.
	 */
	public Preferences getPreferences() {
		return preferences;
	}
	
	/**
	 * Returns the relations of this entity.
	 * 
	 * @return Relations of this entity.
	 */
	@JsonIgnore
	public RelationGraph getRelations() {
		return relations;
	}
	
	/**
	 * Returns the message queue of this entity for ensemble communication.
	 * 
	 * @return Message queue of this entity.
	 */
	@JsonIgnore
	public Queue<IMessage> getMessageQueue() {
		return messageQueue;
	}
	
	/**
	 * Checks, if entity is an actively moving entity.
	 * 
	 * @return True, if entity is an actively moving agent, false otherwise.
	 */
	@JsonIgnore
	public abstract boolean isActive();
	
	/**
	 * Initiate knowledge exchange with other entities.
	 */
	public void exchangeKnowledge() {
		knowledge.exchangeKnowledge();
	}
	
	/**
	 * Returns String representation of this entity.
	 * 
	 * @return String representation.
	 */
	@Override
	public String toString() {
		return "[Entity" + id + "]";
	}
}
