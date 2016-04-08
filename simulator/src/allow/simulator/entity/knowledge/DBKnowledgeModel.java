package allow.simulator.entity.knowledge;

import java.util.List;

import allow.simulator.entity.Entity;

public interface DBKnowledgeModel {

	boolean addEntry(Entity agent, List<TravelExperience> proir, List<TravelExperience> posterior, String tablePrefix);
	
	List<TravelExperience> getPredictedItinerary(Entity agent, List<TravelExperience> it, String tablePrefix);
	
	boolean exchangeKnowledge(Entity agent1, Entity agent2, String tablePrefix);
	
	void clean(Entity entity, String tablePrefix);
	
}
