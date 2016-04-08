package allow.simulator.entity.knowledge;

import allow.simulator.entity.Entity;

/**
 * Class exhibiting an abstract interface to exchange knowledge between
 * two entities. Instances of subclasses can be combined to handler chains.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public abstract class ExchangeHandler {
	
	/**
	 * Standard exchange handler chain for a person entity.
	 */
	public static ExchangeHandler StandardPersonChain;
	
	/**
	 * Standard exchange handler chain for a bus entity.
	 */
	public static ExchangeHandler StandardBusChain;
	
	static {
		// Initialize person exchange handler chain.
		StandardPersonChain = new HPersonAndPerson();
		HPersonAndBus handler2 = new HPersonAndBus();
		StandardPersonChain.next = handler2;
		
		// Initialize bus exchange handler chain.
		StandardBusChain = new HBusAndPerson();
		HBusAndBus handler3 = new HBusAndBus();
		StandardBusChain.next = handler3;
	}
	
	// Next handler in chain.
	protected ExchangeHandler next;
	
	/**
	 * Sets the subsequent handler to call if this handler is unable to handle
	 * exchange request between given types of entities.
	 * 
	 * @param next Next handler in chain.
	 */
	public void setNextHandler(ExchangeHandler next) {
		this.next = next;
	}
	
	/**
	 * Executes knowledge exchange between two entities. If handler can not
	 * handle types of entities, the next handler in chain is called.
	 * 
	 * @param entity1 First entity.
	 * @param entity2 Second entity.
	 */
	public void exchange(Entity entity1, Entity entity2) {
		
	}
	
}
