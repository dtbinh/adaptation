package allow.simulator.entity.knowledge;

import allow.simulator.entity.Entity;
import allow.simulator.entity.Person;
import allow.simulator.entity.PublicTransportation;

/**
 * 
 * 
 * @author Andi
 *
 */
public class HPersonAndBus extends ExchangeHandler {

	@Override
	public void exchange(Entity entity1, Entity entity2) {
		
		if ((entity1 instanceof Person) && (entity2 instanceof PublicTransportation)) {

		} else if (next != null) {
			next.exchange(entity1, entity2);
		}

	}

}
