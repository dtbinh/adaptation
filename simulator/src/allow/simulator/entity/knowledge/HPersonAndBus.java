package allow.simulator.entity.knowledge;

import allow.simulator.entity.Bus;
import allow.simulator.entity.Entity;
import allow.simulator.entity.Person;

/**
 * 
 * 
 * @author Andi
 *
 */
public class HPersonAndBus extends ExchangeHandler {

	@Override
	public void exchange(Entity entity1, Entity entity2) {
		
		if ((entity1 instanceof Person) && (entity2 instanceof Bus)) {

		} else if (next != null) {
			next.exchange(entity1, entity2);
		}

	}

}
